package edu.shu.auto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import edu.shu.auto.constant.EventConstant;
import edu.shu.auto.domain.FilterEntity;
import edu.shu.auto.log.MyLogger;
import edu.shu.auto.ui.Annotation;

/**
 * 
 * <p>
 * ClassName FilterUtil
 * </p>
 * <p>
 * Description 过滤操作工具类，在多遍过滤自动标注中，主要是调用该类的filter方法
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-31 下午03:47:44
 *         </p>
 * @version V1.0.0
 * 
 */
public class FilterUtil {
	private static Set<String> actionTreeSet = new TreeSet<String>();
	private static Set<String> emergencyTreeSet = new TreeSet<String>();
	private static Set<String> movementTreeSet = new TreeSet<String>();
	private static Set<String> operationTreeSet = new TreeSet<String>();
	private static Set<String> perceptionTreeSet = new TreeSet<String>();
	private static Set<String> stateChangeTreeSet = new TreeSet<String>();
	private static Set<String> statementTreeSet = new TreeSet<String>();
	private static Set<String> thoughtEventTreeSet = new TreeSet<String>();
	static String paths[] = new String[8];
	static List<Set<String>> treeSetsList = new ArrayList<Set<String>>();
	static Map<Integer, String> type_value = new HashMap<Integer, String>();

	/**
	 * 
	 * <p>
	 * Title: filter2
	 * </p>
	 * <p>
	 * Description: 第二遍过滤，主要是确定事件的边界，根据&lt;Event&gt;标签，添加对应的&lt;/Event&gt;
	 * </p>
	 * 
	 * @param linkedList
	 *
	 */
	public static void filter2(LinkedList<String> linkedList) {
		ListIterator<String> linkListIter = linkedList.listIterator();// 使用ListIterator迭代器，可以向前向后进行迭代
		Map<Integer, String> map = new TreeMap<Integer, String>();// 用来存储索引和值，其中索引表示应该插入Event结束标签的位置，值就是Event结束标签
		int currentIndex = -1;
		while (linkListIter.hasNext()) {
			String tempValue = linkListIter.next().trim();
			currentIndex++;
			if (Annotation.START_EVENT.equalsIgnoreCase(tempValue)) {
				// 发现了Event的开始标签
				while (linkListIter.hasNext()) {
					String tempValue2 = linkListIter.next().trim();
					currentIndex++;
					if (Annotation.START_EVENT.equalsIgnoreCase(tempValue2)
							|| Annotation.END_SENTENCE.equalsIgnoreCase(tempValue2)) {
						// 这里要往前回溯一下，防止略过了Event开始标签，这是我自认为比较漂亮的一句代码
						linkListIter.previous();// 这一句话非常重要，不仅是要处理两个紧跟在一起的Event标签，同时还可以表示我又加入了一个标签，所以索引值对应加1，那么将其向前移动的同时达到了索引增加的目的
						map.put(currentIndex, Annotation.END_EVENT);
						break;
					}
				}
			}
		}
		MyLogger.logger.info("map集合中：" + map);
		Set<Integer> s = map.keySet();
		Iterator<Integer> iter = s.iterator();
		while (iter.hasNext()) {
			int index = iter.next();
			String value = map.get(index).toString();
			linkedList.add(index, value);// 由于这个时候迭代器没变，所以可以加入
		}
	}

	// 测试List与Set相互转化
	@Test
	public void testListAndSet() {
		List<String> list = new ArrayList<String>();
		Set<String> set = new TreeSet<String>();
		Collections.addAll(list, "wo", "woai", "woaini", "wo");
		set.addAll(list);// 将List转成Set
		list.clear();
		list.addAll(set);// 将Set转成List
		int index = 0;
		System.out.println(list.get(index++));
		System.out.println(list);
		System.out.println(set);
	}

	public static void filter3(String filePath) {

		// String dir = FileUtil.getRootPath() + ReadConfigFile.getValue("denoterDirectory");
		String dir = FileUtil.getRootPath("")+ReadConfigFile.getValue("denoterDirectory");
		String actionPath = dir + "action_denoter.txt";
		String emergencyPath = dir + "emergency_denoter.txt";
		String movementPath = dir + "movement_denoter.txt";
		String operationPath = dir + "operation_denoter.txt";
		String perceptionPath = dir + "perception_denoter.txt";
		String stateChangePath = dir + "stateChange_denoter.txt";
		String statementPath = dir + "statement_denoter.txt";
		String thoughteventPath = dir + "thoughtevent_denoter.txt";
		// 为treeSetsList赋值
		treeSetsList.add(thoughtEventTreeSet);// 第一个用来存放意念事件
		treeSetsList.add(actionTreeSet);
		treeSetsList.add(emergencyTreeSet);
		treeSetsList.add(movementTreeSet);
		treeSetsList.add(operationTreeSet);
		treeSetsList.add(perceptionTreeSet);
		treeSetsList.add(stateChangeTreeSet);
		treeSetsList.add(statementTreeSet);

		// 为paths数组赋值
		paths[0] = thoughteventPath;
		paths[1] = actionPath;
		paths[2] = emergencyPath;
		paths[3] = movementPath;
		paths[4] = operationPath;
		paths[5] = perceptionPath;
		paths[6] = stateChangePath;
		paths[7] = statementPath;

		// 为type_value进行赋值
		type_value.put(0, "thoughtevent");
		type_value.put(1, "action");
		type_value.put(2, "emergency");
		type_value.put(3, "movement");
		type_value.put(4, "operation");
		type_value.put(5, "perception");
		type_value.put(6, "stateChange");
		type_value.put(7, "statement");

		List<String> readLines = null;
		for (int i = 0; i < paths.length; i++) {
			try {
				// 一行一行进行读取
				readLines = FileUtils.readLines(new File(paths[i]));
			} catch (IOException e) {
				e.printStackTrace();
				MyLogger.logger.error(e.getMessage());
			}
			treeSetsList.get(i).addAll(readLines);
			MyLogger.logger.info(paths[i] + "=>" + treeSetsList.get(i).size());
		}
		parseXML(filePath);
	}

	/**
	 * 
	 * <p>
	 * Title: filter5
	 * </p>
	 * <p>
	 * Description: 对自动标注结果进行第五遍过滤，主要是合并一个事件<Event>标签之内，紧邻的或者仅仅有两个字符距离之内的相同标签
	 * </p>
	 * 
	 * @param filePath
	 *
	 */
	public static void filter5(String filePath) {
		// 调用具体的实现类和方法
		MergeElementUtil.mergeByPath(filePath);
	}

	public static void parseXML(String filePath) {
		// InputStream is = null;
		// try {
		// is = new InputStreamReader(new FileInputStream(new File(filePath)));
		// } catch (FileNotFoundException e1) {
		// e1.printStackTrace();
		// MyLogger.logger.error(e1.getMessage());
		// }
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)),
					EncodingUtil.CHARSET_UTF8)));
		} catch (DocumentException | UnsupportedEncodingException | FileNotFoundException e1) {
			e1.printStackTrace();
			MyLogger.logger.error(e1.getMessage());
		}
		// Element rootElement = document.getRootElement();
		// System.out.println("根节点名称：" + rootElement.getName());
		// System.out.println("根节点的属性个数：" + rootElement.attributeCount());
		// System.out.println("根节点id属性的值：" + rootElement.attributeValue("id"));
		// System.out.println("根节点内文本：" + rootElement.getText());
		// System.out.println("根节点内去掉换行tab键等字符：" + rootElement.getTextTrim());
		// System.out.println("根节点子节点文本内容：" + rootElement.getStringValue());

		// Element content = rootElement.element("Content");
		// Element paragraph = content.element("Paragraph");
		// Element sentence = paragraph.element("Sentence");

		// Element event = sentence.element("Event");
		// Element event = paragraph.element("Event");
		@SuppressWarnings("unchecked")
		List<Element> event_list = document.selectNodes("//Sentence/Event");
		@SuppressWarnings("unchecked")
		List<Element> denoter_list = document.selectNodes("//Sentence/Event/Denoter");
		Iterator<Element> denoterIter = denoter_list.iterator();
		Iterator<Element> eventIter = event_list.iterator();
		// Element para = doc.element("para");
		// Element sent = para.element("sent");
		while (denoterIter.hasNext()) {
			Element denoter = denoterIter.next();
			Element event = eventIter.next();
			String denoterValue = denoter.getTextTrim();
			for (int i = 0; i < treeSetsList.size(); i++) {
				if (treeSetsList.get(i).contains(denoterValue)) {
					String typeValue = type_value.get(i);// 获取denoter的type值
					if (0 == i) {
						// 说明是意念事件，那么这时候拿到的typeValue是Event的属性值
						event.addAttribute("type", typeValue);
						denoter.addAttribute("type", "statement");// 默认意念事件触发词的类型都是statement
						// 注意如果是意念事件的话，event的type是thoughtEvent，denoter的属性是statement
						// 只要发现了一个意念事件，那个根据原则，就应该将意念事件的关系加到文档末尾
						document.getRootElement().addElement(Annotation.THOUGHTEVENT_RELATION);
					} else {
						// 为event添加属性和值
						denoter.addAttribute("type", typeValue);
					}
				}
			}
		}
		// 这部分用来实现判断Time是不是绝对时间
		@SuppressWarnings("unchecked")
		List<Element> time_list = document.selectNodes("//Sentence/Event/Time");
		Iterator<Element> timeIter = time_list.iterator();
		while (timeIter.hasNext()) {
			Element time = timeIter.next();
			String timeValue = time.getTextTrim();
			if (isAbsTime(timeValue)) {
				time.addAttribute("type", "absTime");
			}
		}

		try {
			// 使用XMLWriter写入，可以控制格式，经过调试，发现这种方式会出现乱码，主要是因为Eclipse中xml文件和JFrame中文件编码不一致造成的
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(EncodingUtil.CHARSET_UTF8);
			// format.setSuppressDeclaration(true);//这句话会压制xml文件的声明，如果为true，就不打印出声明语句
			format.setIndent(true);// 设置缩进
			format.setIndent("	");// 空行方式缩进
			format.setNewlines(true);// 设置换行

			XMLWriter writer = new XMLWriter(new FileWriterWithEncoding(new File(filePath), EncodingUtil.CHARSET_UTF8), format);
			writer.write(document);
			writer.close();
			// 使用common的Util包写入
			// FileWriterWithEncoding out = new FileWriterWithEncoding(new File(filePath), "UTF-8");
			// document.write(out);
			// out.flush();
			// out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 该函数用来判断时间是绝对时间还是非绝对时间
	public static boolean isAbsTime(String timeStr) {
		// String str1 = "2121年的1月1日";
		// String str2 = "2012/12/12";
		// String str3 = "2012----12-12";
		// String str4 = "15年6月6日";
		// String str5 = "15/12/12";
		// String str6 = "2015年3月";
		// String str7 = "2014-05-14 20:59:03";
		// +表示一次或多次
		// *表示零次或多次
		// ?表示一次或没有
		// {n,}表示至少n次
		String regular_str = "^.*(\\d{4}(.)+\\d{1,}(.)*\\d{1,}(.)*)|(\\d{2}年\\d{1,}(.)*\\d{1,}(.)*)$";// 匹配以四位数字打头的字串
		Pattern p = Pattern.compile(regular_str);
		Matcher m = p.matcher(timeStr);// 判断报道时间
		return m.matches();// 如果是绝对时间，返回true，否则，返回false
	}

	@Test
	public void testFilterList() {
		List<String> list = new ArrayList<String>();
		list.add("TMP");
		list.add("1");
		list.add("1");
		list.add("LOC");
		list.add("2");
		list.add("4");
		list.add("TMP");
		list.add("3");
		list.add("9");
		list.add("LOC");
		list.add("2");
		list.add("4");
		list.add("TMP");
		list.add("1");
		list.add("1");
		filterList(list);
	}

	/**
	 * 
	 * <p>
	 * Title: filterList
	 * </p>
	 * <p>
	 * Description: 处理&lt;arg&gt;标签重复标注情况
	 * </p>
	 * 
	 * @param list
	 * @return 过滤完成之后的链表
	 *
	 */
	public static List<String> filterList(List<String> list) {
		int size = list.size();
		int index = 0;
		MyLogger.logger.info("size = " + size);
		MyLogger.logger.info("过滤之前：" + list);
		List<FilterEntity> listTemp = new ArrayList<FilterEntity>();
		while (index < size - 1) {
			String name = list.get(index++);
			String startId = list.get(index++);
			String endId = list.get(index++);
			FilterEntity filterEntity = new FilterEntity(name, startId, endId);
			if (!listTemp.contains(filterEntity)) {// 这里注意，使用的contains方法需要，domain对象重写equal和hashCode方法
				listTemp.add(filterEntity);// 如果不包含，就将其加进来
			}
		}
		MyLogger.logger.info("listTemp = " + listTemp);
		list.clear();// 将链表清空
		Iterator<FilterEntity> iterator = listTemp.iterator();
		while (iterator.hasNext()) {
			FilterEntity f = iterator.next();
			String name = f.getName();
			String startId = f.getStartId();
			String endId = f.getEndId();
			list.add(name);
			list.add(startId);
			list.add(endId);
		}
		MyLogger.logger.info("过滤之后：" + list);
		return list;
	}

	public static void filter4(String tempPath) {
		int idIndex = 1;// 定义id的开始数字
		InputStream is = null;
		try {
			is = new FileInputStream(new File(tempPath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			MyLogger.logger.error(e1.getMessage());
		}
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(is);
		} catch (DocumentException e1) {
			e1.printStackTrace();
			MyLogger.logger.error(e1.getMessage());
		}
		@SuppressWarnings("unchecked")
		List<Element> event_list = document.selectNodes("//Sentence/Event");
		Iterator<Element> eventIter = event_list.iterator();
		Attribute attribute;
		while (eventIter.hasNext()) {
			// 先拿到event的id
			Element eventElem = eventIter.next();
			// 拿到事件的属性
			attribute = eventElem.attribute(EventConstant.EID);
			attribute.setData(EventConstant.E + idIndex);// 为事件的id赋值
			@SuppressWarnings("unchecked")
			Iterator<Element> elementsIter = eventElem.elementIterator();// 拿到下属的所有元素
			while (elementsIter.hasNext()) {
				Element elem = elementsIter.next();
				String elementName = elem.getName();
				switch (elementName) {
				case EventConstant.DENOTER:
					attribute = elem.attribute(EventConstant.DID);
					attribute.setData(EventConstant.D + idIndex);
					break;
				case EventConstant.LOCATION:
					attribute = elem.attribute(EventConstant.LID);
					attribute.setData(EventConstant.L + idIndex);
					break;
				case EventConstant.MEANS:
					attribute = elem.attribute(EventConstant.MID);
					attribute.setData(EventConstant.M + idIndex);
					break;
				case EventConstant.OBJECT:
					attribute = elem.attribute(EventConstant.OID);
					attribute.setData(EventConstant.O + idIndex);
					break;
				case EventConstant.PARTICIPANT:
					attribute = elem.attribute(EventConstant.SID);
					attribute.setData(EventConstant.S + idIndex);
					break;
				case EventConstant.TIME:
					attribute = elem.attribute(EventConstant.TID);
					attribute.setData(EventConstant.T + idIndex);
					break;
				default:
					MyLogger.logger.info("在为ID赋值中，没有匹配到~！");
					break;
				}
			}
			idIndex++;
		}
		try {
			// 使用XMLWriter写入，可以控制格式，经过调试，发现这种方式会出现乱码，主要是因为Eclipse中xml文件和JFrame中文件编码不一致造成的
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(EncodingUtil.CHARSET_UTF8);
			// format.setSuppressDeclaration(true);//这句话会压制xml文件的声明，如果为true，就不打印出声明语句
			format.setIndent(true);// 设置缩进
			format.setIndent("	");// 空行方式缩进
			format.setNewlines(true);// 设置换行
			XMLWriter writer = new XMLWriter(new FileWriterWithEncoding(new File(tempPath), EncodingUtil.CHARSET_UTF8), format);
			writer.write(document);
			writer.close();
			// 使用common的Util包写入
			// FileWriterWithEncoding out = new FileWriterWithEncoding(new File(filePath), "UTF-8");
			// document.write(out);
			// out.flush();
			// out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MyLogger.logger.info("event_list的size = " + event_list.size());
	}

	/**
	 * 
	 * <p>
	 * Title filterWordId
	 * </p>
	 * <p>
	 * Description 该函数用来对词性规则抽取过程中的word的id进行过滤，保留连续递增的子序列，比如过滤前：31,45,46,49,50,53,54,56，过滤后：45,46,49,50,53,54
	 * </p>
	 * 
	 * @param ids_str
	 *            id字符序列
	 * @return 过滤之后的set集合
	 *
	 */
	public static Set<Integer> filterWordId(String ids_str) {
		Set<Integer> result = new TreeSet<Integer>();
		String[] strs = ids_str.split(",");
		for (int i = 0; i < strs.length - 1; i++) {
			int num2 = Integer.parseInt(strs[i + 1].trim());
			int num1 = Integer.parseInt(strs[i].trim());
			if (num2 - num1 == 1) {// 只要后面比前面大1，就将它们全都加入set集合
				result.add(num1);
				result.add(num2);
			}
		}
		return result;
	}
}
