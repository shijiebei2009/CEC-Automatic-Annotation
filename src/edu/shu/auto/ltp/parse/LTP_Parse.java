package edu.shu.auto.ltp.parse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import edu.shu.auto.log.MyLogger;
import edu.shu.auto.ltp.cloud.LTPAction;
import edu.shu.auto.preprocess.PreProcess;
import edu.shu.auto.ui.Annotation;
import edu.shu.auto.util.FilterUtil;
import edu.shu.auto.util.MySplit;
import edu.shu.auto.util.WordsUtil;

/**
 * 
 * <p>
 * ClassName LTP_Parse
 * </p>
 * <p>
 * Description 使用哈工大的LTP进行分词标注，然后根据分析结果来对生语料进行标注，所使用的识别方法是从CEC语料库中挖掘的规则，挖掘算法采用韩家炜提出的序列模式挖掘算法
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-16 下午02:36:47
 *         </p>
 * @version V1.0.0
 * 
 */
public class LTP_Parse {
	// 在Eclipse中存在两种格式的省略号
	// ……
	// ......
	@Test
	public void test() {
		Pattern pattern = Pattern.compile("(…{2})");// 这个正则表达式可以用来匹配第一种省略号
		String s1 = "……";
		Matcher matcher = pattern.matcher(s1);
		boolean matches = matcher.matches();
		System.out.println(matches);

		pattern = Pattern.compile("(\\.{6})");// 这个正则表达式可以用来匹配第二种省略号
		String s2 = "......";
		matches = pattern.matcher(s2).matches();
		System.out.println(matches);
	}

	private static LinkedList<String> linkedList;// 用来存储生语料和逐步添加的标签的内容
	private final static String TYPE = "xml";// 这个是用来定义ltp-cloud返回来的文件类型

	public static List<String> getLinkedList(String paragraphContent) {
		linkedList = new LinkedList<String>();// 该list集合用来存储分词之后的字符串以及加入的标签
		participle(paragraphContent);// 调用分词函数，形参为一个段落的内容，这是第一遍过滤
		FilterUtil.filter2(linkedList);// 此处进行第二遍过滤
		return linkedList;
	}

	/**
	 * 
	 * <p>
	 * Title: participle
	 * </p>
	 * <p>
	 * Description 开始对一个段落的内容进行处理
	 * </p>
	 * 
	 * @param paragraphContent
	 *            接受的原文本是一个段落的内容
	 *
	 */
	public static void participle(String paragraphContent) {
		Set<String> set = PreProcess.getTreeSet();// 获取触发词表，使用TreeSet存储触发词
		// 在链表开始时，先加入paragraph和sentence标签
		linkedList.add("\n" + Annotation.START_PARAGRAPH);

		// 调用LTPAction类对ltp-cloud发请求，同时这个方法内部会调用EventFactory来解析这个xml文件
		LTPAction ltpAction = new LTPAction();
		// String sentences[] = paragraphContent.split("。|？|！");//
		// 进行分句处理，开始使用Java提供的split方法，但是该方法不会保留切分符，所以自定义了MySplit方法来切分，同时保留切分符
		// 此处本来我是以？！。进行切分，但是经过测试，ltp会把；认为是对一个句子的分割，并且会将id重新置0
		// 例子：据交警介绍，驾驶员张某，女，25岁，持有“C1”型机动车驾驶证；环卫工人周某，女，54岁，家住银川市兴庆区大新镇新水桥村。

		// 因为LTP有时会按省略号切分，有时不会按省略号切分，为了结果的统一以及程序的健壮性。我们统一将省略号替换为。
		paragraphContent = paragraphContent.replaceAll("(…{2})", "。").replaceAll("(\\.{6})", "。");// 因为eclipse中会有两种形式的省略号，分别是……和......
		String sentences[] = MySplit.split(paragraphContent, "。|？|！|；");// 调用我自己的split函数，可以保留切分符
		MyLogger.logger.info("切分句子处理完毕");
		MyLogger.logger.info("sentences.length = " + sentences.length);
		for (int i = 0; i < sentences.length; i++) {
			linkedList.add("\n" + Annotation.START_SENTENCE + "\n");
			// 对每一个sentence单独处理
			ltpAction.getXML(sentences[i], null, TYPE);// 这句话调用之后，会生成一个以fileName命名的xml文件
			// 开始对IDs进行处理
			int idLength = WordsUtil.ids.size();
			int idTemp = WordsUtil.ids.get(0);// 拿到第一个元素，idTemp可以看做是id的一个游标，不断的向后游走，然后不断的向后处理

			MyLogger.logger.info("idLength=" + idLength);
			MyLogger.logger.info("ids=" + WordsUtil.ids);
			MyLogger.logger.info("content=" + WordsUtil.contentHashMap);
			MyLogger.logger.info("pos=" + WordsUtil.posHashMap);
			MyLogger.logger.info("ne=" + WordsUtil.neHashMap);
			MyLogger.logger.info("arg=" + WordsUtil.argArrayList);
			MyLogger.logger.info("relate=" + WordsUtil.relateHashMap);

			while (idTemp < idLength - 1) {// 这里这样处理会漏掉最后一个元素，所以最后一个元素单独处理
				MyLogger.logger.info("begin ... idTemp = " + idTemp);
				// 那么说明下面就还有，id还没结束
				// 着手拿到它的词性，只要是wp就表明是标点符号，不处理
				boolean flag = WordsUtil.posHashMap.get(idTemp).trim().equalsIgnoreCase("wp");
				// 注意此处要处理这种特殊情况，例如（记者某某报道）
				if (flag) {
					String content = WordsUtil.contentHashMap.get(idTemp).trim();
					if ("（".equals(content) || "(".equals(content)) {
						linkedList.add(content);// 将开始括号加入
						// 那么一直向后找，直到找到了）为止
						String endContent = WordsUtil.contentHashMap.get(++idTemp).trim();
						while (!"）".equals(endContent) && !")".equals(endContent)) {
							// 只要不是括号的结尾
							idTemp++;
							linkedList.add(endContent);
							endContent = WordsUtil.contentHashMap.get(idTemp);
						}
						// 找到了末尾括号，退出循环
						MyLogger.logger.info("after \"()\" idTemp = " + idTemp);
					}
					linkedList.add(WordsUtil.contentHashMap.get(idTemp));// 是标点，直接将标点加入链表
					MyLogger.logger.info("处理完标点符号...idTemp = " + idTemp);
				} else {
					boolean changeFlag = true;// 做一个标志位，如果这个标志位变了，那么说明是符合了下面的条件语句，如果这个标志位没变，那么没符合下面的条件语句，直接将原文本加入linkedlist即可
					// 说明不是标点，开始着手处理
					// 先处理argArrayList的情况
					// 当arg链表大于1并且游标等于了arg中的开始id的时候，开始处理arg链表的内容
					if (WordsUtil.argArrayList.size() > 1 && idTemp == Integer.parseInt(WordsUtil.argArrayList.get(1))) {
						MyLogger.logger.info("HEAD... argArrayList=" + WordsUtil.argArrayList);
						// arg的开始id等于当前的id，那么进入该逻辑
						MyLogger.logger.info("开始argList处理逻辑...");
						// 说明id等于arg参数的开始节点
						int index = -1;// 索引使用
						String argType = WordsUtil.argArrayList.get(++index).trim();
						if ("TMP".equalsIgnoreCase(argType)) {
							// 时间
							int TMPbeg = Integer.parseInt(WordsUtil.argArrayList.get(++index));
							int TMPend = Integer.parseInt(WordsUtil.argArrayList.get(++index));
							// 如果紧跟着的是DIS，要将其归并到TMP中
							if (WordsUtil.argArrayList.size() > (++index)
									&& WordsUtil.argArrayList.get(index).trim().equalsIgnoreCase("DIS")) {
								// 并且还要判断是否是紧跟着的，那么进行大小的比较
								int DISbeg = Integer.parseInt(WordsUtil.argArrayList.get(++index));// 取得DIS的开始ID
								if (DISbeg - TMPend == 1) {
									// 说明是紧跟着的，那么可以将TMPend索引后移
									TMPend = Integer.parseInt(WordsUtil.argArrayList.get(++index));
								} else {
									// 否则说明不是紧跟着的，那么将index归位
									index = index - 2;
								}
							} else {
								// 将index归位，这是处理if逻辑判断里面多加的情况
								index--;
							}
							String timeStr = "";
							for (int temp = TMPbeg; temp <= TMPend; temp++) {
								timeStr += WordsUtil.contentHashMap.get(temp);// 不断拼接
							}
							// 加入时间标签
							linkedList.add(Annotation.START_TIME);
							linkedList.add(timeStr);
							linkedList.add(Annotation.END_TIME);
							idTemp = TMPend;// 继续向下走，重置游标
							while (index >= 0 && WordsUtil.argArrayList.size() > 0) {
								WordsUtil.argArrayList.remove(0);// 移除操作，将已经处理过的arg内容移除，以便下次处理其后的内容
								index--;
							}
							changeFlag = false;// 将标志位置false
						} else if ("LOC".equalsIgnoreCase(argType.trim())) {// 处理是地点的情况
							int LOCbeg = Integer.parseInt(WordsUtil.argArrayList.get(++index));
							int LOCend = Integer.parseInt(WordsUtil.argArrayList.get(++index));
							String locStr = "";
							for (int temp = LOCbeg; temp <= LOCend; temp++) {
								locStr += WordsUtil.contentHashMap.get(temp);
							}
							linkedList.add(Annotation.START_LOCATION);
							linkedList.add(locStr);
							linkedList.add(Annotation.END_LOCATION);
							while (index >= 0 && WordsUtil.argArrayList.size() > 0) {
								// 进行移除操作
								WordsUtil.argArrayList.remove(0);
								index--;
							}
							idTemp = LOCend;// 重置游标
							changeFlag = false;
						} else if ("DIS".equalsIgnoreCase(argType.trim())) {
							// 如果上来就是DIS，直接移除
							WordsUtil.argArrayList.remove(0);
							WordsUtil.argArrayList.remove(0);
							WordsUtil.argArrayList.remove(0);
						}
						MyLogger.logger.info("END... argArrayList=" + WordsUtil.argArrayList);
					} else {
						// 处理ne（命名实体）的情况了
						String neStr = WordsUtil.neHashMap.get(idTemp).trim();
						MyLogger.logger.info("ne处理逻辑中，neStr = " + neStr);
						int start = idTemp;
						int end = idTemp;
						int idIndex = idTemp;
						// 处理地点逻辑
						if ("S-Ns".equalsIgnoreCase(neStr) && WordsUtil.neHashMap.get(++idIndex) != null) {// 说明其后还有
							// 处理S-Ns+S-Nh，三步之内的情况，这样可以识别为Participant
							int count = idIndex;
							String par_temp = WordsUtil.neHashMap.get(count);
							MyLogger.logger.info("进入了S-Ns...participant_temp = " + par_temp);
							boolean partiFlag = false;
							while (par_temp != null) {
								if ("S-Nh".equalsIgnoreCase(par_temp) && (count - idIndex <= 3)) {
									// 找到了满足的，重置游标
									idTemp = count;
									partiFlag = true;
								} else if (count - idIndex > 3) {
									break;
								}
								par_temp = WordsUtil.neHashMap.get(++count);
							}

							if (partiFlag) {// 说明是Participant
								String participantTemp = "";
								end = idTemp;
								for (int temp = start; temp <= end; temp++) {
									participantTemp += WordsUtil.contentHashMap.get(temp);
								}
								if (!"".equalsIgnoreCase(participantTemp)) {
									linkedList.add(Annotation.START_PARTICIPANT);
									linkedList.add(participantTemp);
									linkedList.add(Annotation.END_PARTICIPANT);
								}
								MyLogger.logger.info("after participant,idTemp = " + idTemp);
							} else {// 说明是Location
								// 那么继续判断，如果其后跟的是nl/nd均继续向下走
								String posStr2 = WordsUtil.posHashMap.get(idIndex);
								if ("nl".equalsIgnoreCase(posStr2) || "nd".equalsIgnoreCase(posStr2)) {
									// 将end后移
									end = idIndex;
								}
								String locationTemp = "";
								for (int temp = start; temp <= end; temp++) {
									locationTemp += WordsUtil.contentHashMap.get(temp);
								}
								// 加入链表
								linkedList.add(Annotation.START_LOCATION);
								linkedList.add(locationTemp);
								linkedList.add(Annotation.END_LOCATION);
								// id继续往后走
								idTemp = end;
								MyLogger.logger.info("after location,idTemp = " + idTemp);
							}
							changeFlag = false;
						} else if ("B-Ns".equalsIgnoreCase(neStr)) {
							String neStr2 = WordsUtil.neHashMap.get(++idIndex);
							MyLogger.logger.info("进入了B-Ns...neStr = " + neStr2);
							while (!"E-Ns".equalsIgnoreCase(neStr2)) {// 找到了B-Ns，那么需要找到E-Ns作为结尾
								idIndex++;
								neStr2 = WordsUtil.neHashMap.get(idIndex);
							}

							String posStr3 = WordsUtil.posHashMap.get(++idIndex).trim();// 继续判断后面是不是nl/nd
							String posStr4 = WordsUtil.neHashMap.get(idIndex).trim();// 如果B-Ns+E-Ns并且紧跟S-Ns
							if (posStr3 != null && ("nl".equalsIgnoreCase(posStr3) || "nd".equalsIgnoreCase(posStr3))) {
								end = idIndex;// 后移end坐标
							} else if (posStr4 != null && "S-Ns".equalsIgnoreCase(posStr4)) {
								end = idIndex;// 后移end坐标
							} else {
								end = --idIndex;// 如果上述条件都没有满足，将end重置，还原
							}

							String locationTemp = "";
							for (int temp = start; temp <= end; temp++) {
								locationTemp += WordsUtil.contentHashMap.get(temp);
							}
							// 加入location
							linkedList.add(Annotation.START_LOCATION);
							linkedList.add(locationTemp);
							linkedList.add(Annotation.END_LOCATION);
							// 后移idTemp
							idTemp = end;
							changeFlag = false;
							MyLogger.logger.info("after location,idTemp = " + idTemp);
						} else if ("S-Ni".equalsIgnoreCase(neStr)) {
							MyLogger.logger.info("进入了S-Ni...");
							// 处理participant的识别逻辑
							start = idTemp;
							end = idTemp;
							// 其后紧跟n或者S-Nh均可，限定在3之内出现S-Nh，否则不继续识别
							int indexTemp = idTemp;
							String neStrNO1 = WordsUtil.neHashMap.get(++indexTemp);
							String neStrNO2 = WordsUtil.neHashMap.get(++indexTemp);
							String neStrNO3 = WordsUtil.neHashMap.get(++indexTemp);

							if (neStrNO3 != null && "S-Nh".equalsIgnoreCase(neStrNO3)) {
								end = indexTemp;// 第三个符合
							}
							if (neStrNO2 != null && "S-Nh".equalsIgnoreCase(neStrNO2)) {
								end = indexTemp - 1;// 第二个符合
							}
							if (neStrNO1 != null && "S-Nh".equalsIgnoreCase(neStrNO3)) {
								end = indexTemp - 2;
							}
							idTemp = end;// 重新置id

							String participantStr = "";
							for (int temp = start; temp <= end; temp++) {
								participantStr += WordsUtil.contentHashMap.get(temp);
							}
							linkedList.add(Annotation.START_PARTICIPANT);
							linkedList.add(participantStr);
							linkedList.add(Annotation.END_PARTICIPANT);
							changeFlag = false;
							MyLogger.logger.info("after participant,idTemp = " + idTemp);
						} else if ("B-Ni".equalsIgnoreCase(neStr)) {
							MyLogger.logger.info("进入了B-Ni...");
							MyLogger.logger.info(idTemp + "此时候的id是");
							start = idTemp;
							end = idTemp;
							int indexTemp = idTemp;
							// 一直向后找，直到找到E-Ni
							String neStr2 = WordsUtil.neHashMap.get(++indexTemp).trim();
							while (!"E-Ni".equalsIgnoreCase(neStr2)) {
								indexTemp++;
								neStr2 = WordsUtil.neHashMap.get(indexTemp).trim();
							}

							String neStrNO1 = WordsUtil.neHashMap.get(++indexTemp);
							String neStrNO2 = WordsUtil.neHashMap.get(++indexTemp);
							String neStrNO3 = WordsUtil.neHashMap.get(++indexTemp);
							MyLogger.logger.info("neStrNO1 = " + neStrNO1);
							MyLogger.logger.info("neStrNO2 = " + neStrNO2);
							MyLogger.logger.info("neStrNO3 = " + neStrNO3);
							if (neStrNO3 != null && "S-Nh".equalsIgnoreCase(neStrNO3)) {
								end = indexTemp;// 第三个符合
								MyLogger.logger.info("第三个符合");
							} else if (neStrNO2 != null && "S-Nh".equalsIgnoreCase(neStrNO2)) {
								end = indexTemp - 1;// 第二个符合
								MyLogger.logger.info("第二个符合");
							} else if (neStrNO1 != null && "S-Nh".equalsIgnoreCase(neStrNO3)) {
								end = indexTemp - 2;// 第一个符合
								MyLogger.logger.info("第一个符合");
							} else {
								indexTemp = indexTemp - 3;// 将其归位
								MyLogger.logger.info("没有匹配到");
							}
							// 退出循环的时候，将idIndex归位
							end = indexTemp;
							idTemp = end;
							String participantStr = "";
							for (int temp = start; temp <= end; temp++) {
								participantStr += WordsUtil.contentHashMap.get(temp);
							}
							linkedList.add(Annotation.START_PARTICIPANT);
							linkedList.add(participantStr);
							linkedList.add(Annotation.END_PARTICIPANT);
							changeFlag = false;
							MyLogger.logger.info("after participant,idTemp = " + idTemp);
						} else if ("S-Nh".equalsIgnoreCase(neStr)) {
							int indexTemp = idTemp;
							MyLogger.logger.info("进入S-Nh...");
							String partiTemp = WordsUtil.contentHashMap.get(idTemp);
							neStr = WordsUtil.neHashMap.get(++indexTemp).trim();// 取到下一个的命名实体
							if ("S-Ni".equals(neStr)) {
								// 如果是(S-Nh)+(S-Ni)
								// 那么需要对partiTemp添加内容
								partiTemp += WordsUtil.contentHashMap.get(indexTemp).trim();
							}
							idTemp = indexTemp;// 重新设置遍历的游标索引
							linkedList.add(Annotation.START_PARTICIPANT);
							linkedList.add(partiTemp);// 加入参与者
							linkedList.add(Annotation.END_PARTICIPANT);
							changeFlag = false;
						} else if ("B-Nh".equals(neStr)) {
							// 处理B-Nh一直到E-Nh的情况
							start = idTemp;
							end = idTemp;
							// 需要一直循环找到E-Nh的情况
							int indexTemp = idTemp;
							neStr = WordsUtil.neHashMap.get(++indexTemp).trim();
							while (!"E-Nh".equals(neStr)) {// 只要不等，就继续循环，默认是说出现了B-Nh那么一定会出现E-Nh
								neStr = WordsUtil.neHashMap.get(++indexTemp).trim();
							}
							end = indexTemp;
							// 退出循环的时候，肯定是end索引找到了
							idTemp = end;
							String participantStr = "";
							for (int temp = start; temp <= end; temp++) {
								participantStr += WordsUtil.contentHashMap.get(temp);
							}
							if (!"".equals(participantStr)) {
								linkedList.add(Annotation.START_PARTICIPANT);
								linkedList.add(participantStr);
								linkedList.add(Annotation.END_PARTICIPANT);
								changeFlag = false;
							}
						} else {
							MyLogger.logger.info("进入了处理触发词逻辑...");
							String denoter = WordsUtil.contentHashMap.get(idIndex).trim();// 注意这时取的不是ne，所以不要对idIndex加1
							String denoterPOS = WordsUtil.posHashMap.get(idIndex).trim();// 拿到词性，n，v
							String posStr = WordsUtil.posHashMap.get(idIndex).trim();// 注意这时取的不是ne，所以不要对idIndex加1
							String relativeStr = WordsUtil.relateHashMap.get(idIndex).trim();
							start = idIndex;
							end = idIndex;
							if (set.contains(denoter) && ("v".equalsIgnoreCase(denoterPOS) || "n".equalsIgnoreCase(denoterPOS))) {
								linkedList.add(Annotation.START_DENOTER);
								linkedList.add(denoter);
								linkedList.add(Annotation.END_DENOTER);
								// 注意，如果发现了触发词，那么回溯，进行查找，以便加入Event标签
								Iterator<String> priorIter = linkedList.descendingIterator();// fuck
																								// 终于发现了，因为linkedList里面可能会含有换行之类的，所以在比较的时候，一定要trim一下
								int index = 0;
								// 注意，往前走的时候，要先跳过自己
								if (priorIter.hasNext()) {
									priorIter.next();// 先跳过自己，下一步再进行判断
									index++;
								}
								while (priorIter.hasNext()) {
									String temp = priorIter.next().trim();
									if (Annotation.END_EVENT.equalsIgnoreCase(temp)
											|| Annotation.START_SENTENCE.equalsIgnoreCase(temp)
											|| Annotation.END_DENOTER.equalsIgnoreCase(temp)) {// 这是处理遇到连续两个触发词的情况
										// 现在需要拿到索引
										index = linkedList.size() - index;// 重新布置index
										break;// 跳出循环之后，下面的一步就不会执行了
									}
									index++;
								}
								//这里会导致一个致命的问题就是当没有发现触发词的时候就无法添加Event开始标签，从而在第二遍过滤中也就无法添加Event结束标签了
								linkedList.add(index, Annotation.START_EVENT);
								// 这里虽然加入了Event开始标签，但是结束标签不好判断，无法加入了，所以采用第二遍过滤的时候再加入Event结束标签
								changeFlag = false;
							} else if ("nl".equalsIgnoreCase(posStr)) {
								// 这里一定要注意，如果仅仅出现了nl是不能将其识别为Time的，所以需要在满足紧跟n的情况下，再进行Time标签的添加
								MyLogger.logger.info("进入了nl..." + idTemp);
								// 处理Time逻辑，可能词性是nl+n+nt，一直到紧跟着的nt的最后一个
								String posStr2 = WordsUtil.posHashMap.get(++idIndex).trim();
								if (posStr2 != null && "n".equalsIgnoreCase(posStr2)) {
									String posStr3 = WordsUtil.posHashMap.get(++idIndex).trim();
									if (posStr3 != null && "m".equals(posStr3)) {
										idIndex++;
									}
									while (posStr3 != null && "nt".equalsIgnoreCase(posStr3)) {
										idIndex++;
										posStr3 = WordsUtil.posHashMap.get(idIndex).trim();
									}

									// 退出while循环，将idIndex归位，同时满足如果只进了if语句，没有进while语句，同样需要将idIndex归位
									end = idIndex - 1;
									idTemp = end;
									String timeStr = "";
									for (int temp = start; temp <= end; temp++) {
										timeStr += WordsUtil.contentHashMap.get(temp);
									}
									if (!"".equals(timeStr)) {
										linkedList.add(Annotation.START_TIME);
										linkedList.add(timeStr);
										linkedList.add(Annotation.END_TIME);
										changeFlag = false;
									}
								}
							} else if ("nt".equalsIgnoreCase(posStr)) {
								// 处理Time逻辑，可能词性是nt，但是并没有识别TMP
								MyLogger.logger.info("进入了nt..." + idTemp);
								String posStr2 = WordsUtil.posHashMap.get(++idIndex);
								// 如果后面的还是nt，继续找
								while (posStr2 != null && "nt".equalsIgnoreCase(posStr2)) {
									idIndex++;
									posStr2 = WordsUtil.posHashMap.get(idIndex);
								}
								// 当退出的时候，将idIndex归位，找到end
								end = idIndex - 1;
								// 开始向后遍历
								if (posStr2 != null && "m".equalsIgnoreCase(posStr2)) {
									String posStr3 = WordsUtil.posHashMap.get(++idIndex);
									if (posStr3 != null && "q".equalsIgnoreCase(posStr3)) {
										end = idIndex;
									} else if (posStr3 != null && "wp".equalsIgnoreCase(posStr3)) {
										String posStr4 = WordsUtil.posHashMap.get(++idIndex);
										// 继续判断是否是m
										if (posStr4 != null && "m".equalsIgnoreCase(posStr4)) {
											end = idIndex;
										} else {
											end = --idIndex;// 这是处理pos=nt，一直检索nt，直到m
										}
									}

								}
								idTemp = end;
								String timeStr = "";
								for (int temp = start; temp <= end; temp++) {
									timeStr += WordsUtil.contentHashMap.get(temp);
								}
								if (!"".equals(timeStr)) {
									linkedList.add(Annotation.START_TIME);
									linkedList.add(timeStr);
									linkedList.add(Annotation.END_TIME);
									changeFlag = false;
								}
								MyLogger.logger.info("退出nt..." + idTemp);
							} else if ("m".equalsIgnoreCase(posStr) && "ATT".equalsIgnoreCase(relativeStr)) {
								// 此处处理Participant逻辑，情况可能是pos是m+q+n，relate是ATT+ATT+VOB/SBV/DBL
								// 或者是pos是m+n，relate是ATT+VOB/SBV/DBL
								MyLogger.logger.info("进入了m和ATT逻辑..." + idTemp);
								// idTemp相当于是我的游标
								// idIndex相当于是每一个逻辑中的临时游标
								// 拿到下一个pos和relate，以便进行判断
								posStr = WordsUtil.posHashMap.get(++idIndex).trim();
								relativeStr = WordsUtil.relateHashMap.get(idIndex).trim();
								if (posStr != null && relativeStr != null) {
									String partiTemp = "";
									if ("n".equalsIgnoreCase(posStr)
											&& ("VOB".equalsIgnoreCase(relativeStr) || "SBV".equalsIgnoreCase(relativeStr) || "DBL"
													.equalsIgnoreCase(relativeStr))) {
										// 处理pos是m+n，relate是ATT+VOB/SBV/DBL
										end = idIndex;
										for (int temp = start; temp <= end; temp++) {
											partiTemp += WordsUtil.contentHashMap.get(temp);
										}
										if (!"".equals(partiTemp)) {
											linkedList.add(Annotation.START_PARTICIPANT);
											linkedList.add(partiTemp);
											linkedList.add(Annotation.END_PARTICIPANT);
											changeFlag = false;
										}
									} else if ("q".equalsIgnoreCase(posStr) && "ATT".equalsIgnoreCase(relativeStr)) {
										// 继续向下拿
										posStr = WordsUtil.posHashMap.get(++idIndex);
										relativeStr = WordsUtil.relateHashMap.get(idIndex);
										if (posStr != null && relativeStr != null) {
											if ("n".equalsIgnoreCase(posStr)
													&& ("VOB".equalsIgnoreCase(relativeStr)
															|| "SBV".equalsIgnoreCase(relativeStr) || "DBL"
																.equalsIgnoreCase(relativeStr))) {
												// 处理pos是m+q+n，relate是ATT+ATT+VOB/SBV/DBL
												end = idIndex;
												for (int temp = start; temp <= end; temp++) {
													partiTemp += WordsUtil.contentHashMap.get(temp);
												}
												if (!"".equals(partiTemp)) {
													linkedList.add(Annotation.START_PARTICIPANT);
													linkedList.add(partiTemp);
													linkedList.add(Annotation.END_PARTICIPANT);
													changeFlag = false;
												}
											}
										}
									}
									idTemp = end;
									MyLogger.logger.info("退出了m和ATT逻辑...");
								}
							}
						}
					}
					MyLogger.logger.info("idTemp=" + idTemp);
					if (changeFlag) {
						// 否则，啥也不是，将内容加入即可
						linkedList.add(WordsUtil.contentHashMap.get(idTemp));
					}
				}
				if (idTemp == (idLength - 1)) {
					// 说明到了最后，跳出while循环
					break;
				} else {
					idTemp = WordsUtil.ids.get(idTemp + 1);
				}
				MyLogger.logger.info("下一轮循环开始前：idTemp = " + idTemp);
			}
			MyLogger.logger.info(WordsUtil.contentHashMap.get(idLength - 1));
			linkedList.add(WordsUtil.contentHashMap.get(idLength - 1));
			// 处理完一个sentence只会就加入sentence标签
			linkedList.add("\n" + Annotation.END_SENTENCE + "\n");
			// 处理完一句话，把所有的WordsUtil清空
			WordsUtil.argArrayList.clear();
			WordsUtil.contentHashMap.clear();
			WordsUtil.ids.clear();
			WordsUtil.neHashMap.clear();
			WordsUtil.posHashMap.clear();
		}
		linkedList.add(Annotation.END_PARAGRAPH + "\n");
	}
}