package edu.shu.auto.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import edu.shu.auto.log.MyLogger;
import edu.shu.auto.ltp.parse.LTP_Parse;
import edu.shu.auto.ui.Annotation;
import edu.shu.auto.ui.OnClickListener;
import edu.shu.auto.util.EncodingUtil;
import edu.shu.auto.util.FileUtil;
import edu.shu.auto.util.FilterUtil;
import edu.shu.auto.util.ReadConfigFile;

/**
 * 
 * <p>
 * ClassName PreProcess
 * </p>
 * <p>
 * Description 自动标注语料的预处理类，提供对外暴露的接口，可以调用
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-7-19 上午12:04:04
 *         </p>
 * @version V1.0.0<br/>
 * 
 *          Java正则匹配方式典型的调用顺序是<br/>
 *          Pattern p = Pattern.compile("a*b");<br/>
 *          Matcher m = p.matcher("aaaaab");<br/>
 *          boolean b = m.matches();<br/>
 *          或者 boolean b = Pattern.matches ("a*b","aaaaab");<br/>
 *          等效于上面的三个语句，尽管对于重复的匹配而言它效率不高，因为它不允许重用已编译的模式。
 */
public class PreProcess {
	private static String reportTime;// 取得用来判断报道的时间是绝对时间还是相对时间

	/**
	 * 
	 * <p>
	 * Title: getTreeSet
	 * </p>
	 * <p>
	 * Description: 读取所有的触发词，构造TreeSet集合
	 * </p>
	 * 
	 * @return 树集合
	 *
	 */
	public static Set<String> getTreeSet() {
		String path = FileUtil.getRootPath("") + ReadConfigFile.getValue("denoter");// 获取存放所有触发词的文件路径
		MyLogger.logger.info(path);
		Set<String> treeSet = new TreeSet<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(path)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!treeSet.contains(line.trim())) {
					treeSet.add(line.trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return treeSet;
	}

	/**
	 * 
	 * <p>
	 * Title: getText
	 * </p>
	 * <p>
	 * Description: 该函数是用来获取还未进行标注的语料，并将其按段落存储在ArrayList链表中。
	 * </p>
	 * 
	 * @param filePath
	 *            文件路径
	 * @param Charset
	 *            文件编码，如果原文本是ANSI编码，那么其使用的是操作系统编码，大陆地区建议传递参数为EncodingUtil.CHARSET_GBK，如果原文本是UTF-8编码，建议传递参数为EncodingUtil.
	 *            CHARSET_UTF8
	 * @return 添加标签之后的内容
	 *
	 */
	public static StringBuilder getText(String filePath, String charset) {
		StringBuilder returnSb = new StringBuilder();// 用来存储返回的文本
		List<String> paragraphs = new ArrayList<String>();// 这个用来按段落存储内容，主要是便于在段落之间插入<Paragraph>,这时候，list里面存的是一个一个段落的内容
		try {
			// 这一句获取的文本可能会显示乱码，所以改成下面一句
			// BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			// InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), Charset);
			// BufferedReader br = new BufferedReader(isr);
			paragraphs = getParagraph(filePath, charset);
			// 用来单单处理第一行，因为第一行是报道时间
			reportTime = paragraphs.remove(0).trim();
			MyLogger.logger.info("段落个数是：" + paragraphs.size());
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		// 从此处开始拼接字符串
		returnSb.append(Annotation.HEAD + "\n");
		returnSb.append(Annotation.START_BODY + "\n");
		returnSb.append(Annotation.START_TITLE);
		returnSb.append(OnClickListener.fileName);// 添加Title标签的内容,fileName从OnClickListener获得
		returnSb.append(Annotation.END_TITLE + "\n");
		if (FilterUtil.isAbsTime(reportTime)) {// 添加绝对属性的报道时间
			returnSb.append(Annotation.START_ABSREPORTTIME);
		} else {// 添加相对属性的报道时间
			returnSb.append(Annotation.START_RELREPORTTIME);
		}
		returnSb.append(reportTime);
		MyLogger.logger.info("reportTime：" + reportTime);
		returnSb.append(Annotation.END_REPORTTIME + "\n");
		returnSb.append(Annotation.START_CONTENT);// 加入content标签
		// 此处开始对段落一个一个进行处理
		Iterator<String> ite = paragraphs.iterator();
		MyLogger.logger.info("在切分段落之后的输出内容是：" + paragraphs);
		while (ite.hasNext()) {
			// 获取分词处理之后标出的触发词
			// 这句话使用的中科院分词工具
			// List<String> paragraphContentList = ICTCLAS_Parse.getLinkedList((String) ite.next());
			// 这句话使用的是哈工大分词工具
			List<String> paragraphContentList = LTP_Parse.getLinkedList(ite.next());
			MyLogger.logger.info("LTP分词工具处理完毕");
			// 将获取的内容进行组拼，主要是为了防止直接输出list有"[]"
			Iterator<String> paragraph_iter = paragraphContentList.iterator();
			StringBuilder paragraph = new StringBuilder();
			while (paragraph_iter.hasNext()) {
				paragraph.append(paragraph_iter.next());
			}
			returnSb.append(paragraph);
		}
		returnSb.append(Annotation.END_CONTENT + "\n");// 加入content结束标签
		returnSb.append(Annotation.END_BODY);
		MyLogger.logger.info("第二遍过滤结果：\n" + returnSb);
		// String tempPath = FileUtil.getRootPath() + ReadConfigFile.getValue("tempXML");
		String tempPath = FileUtil.getRootPath("") + ReadConfigFile.getValue("tempXML");
		MyLogger.logger.info("tempPath = " + tempPath);
		File tempFile = new File(tempPath);
		// 在此处将内容写入XML文件
		charset = EncodingUtil.CHARSET_UTF8;
		MyLogger.logger.info(charset);
		try {
			FileUtils.writeStringToFile(tempFile, returnSb.toString(), charset);
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		// 在此处进行第三遍过滤，调用filter方法
		FilterUtil.filter3(tempPath);
		MyLogger.logger.info("第三遍过滤结果：");
		try {
			returnSb = new StringBuilder(FileUtils.readFileToString(tempFile, charset));
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		MyLogger.logger.info(returnSb);

		// 在此处进行第四遍过滤，调用filter4方法
		FilterUtil.filter4(tempPath);
		try {
			returnSb = new StringBuilder(FileUtils.readFileToString(tempFile, charset));
		} catch (IOException e1) {
			e1.printStackTrace();
			MyLogger.logger.error(e1.getMessage());
		}
		MyLogger.logger.info("第四遍过滤结果：\n" + returnSb);
		// 在此处进行第五遍过滤，调用filter5方法
		FilterUtil.filter5(tempPath);
		// 然后重新将内容读出来
		try {
			returnSb = new StringBuilder(FileUtils.readFileToString(tempFile, charset));
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		MyLogger.logger.info("第五遍过滤结果：\n" + returnSb);
		return returnSb;
	}

	// 该函数是用来获取还未进行标注的语料，并将其按段落存储在ArrayList链表中
	/**
	 * 
	 * <p>
	 * Title: getText
	 * </p>
	 * <p>
	 * Description: 该函数是B/S架构的程序调用的，按理说应该和C/S架构调用的函数进行整合，会在后续版本中合并
	 * </p>
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 带标签的内容
	 *
	 */
	public static StringBuilder getText(String filePath) {
		File file = new File(filePath);
		StringBuilder returnSb = new StringBuilder();// 用来存储返回的文本
		List<String> paragraphs = new ArrayList<String>();// 这个用来按段落存储内容，主要是便于在段落之间插入<Paragraph>,这时候，list里面存的是一个一个段落的内容
		try {
			// 这一句获取的文本可能会显示乱码，所以改成下面一句
			// BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			// InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), Charset);
			// BufferedReader br = new BufferedReader(isr);
			paragraphs = getParagraph(filePath, EncodingUtil.CHARSET_UTF8);
			// 用来单单处理第一行，因为第一行是报道时间
			reportTime = paragraphs.remove(0).trim();
			MyLogger.logger.info("段落个数是：" + paragraphs.size());
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		// 从此处开始拼接字符串
		returnSb.append(Annotation.HEAD + "\n");
		returnSb.append(Annotation.START_BODY + "\n");
		returnSb.append(Annotation.START_TITLE);
		String nameTemp = file.getName();
		int endIndex = nameTemp.lastIndexOf(".txt");
		nameTemp = nameTemp.substring(0, endIndex);
		returnSb.append(nameTemp);// 添加Title标签的内容,fileName从OnClickListener获得
		returnSb.append(Annotation.END_TITLE + "\n");
		if (FilterUtil.isAbsTime(reportTime)) {// 添加绝对属性的报道时间
			returnSb.append(Annotation.START_ABSREPORTTIME);
		} else {// 添加相对属性的报道时间
			returnSb.append(Annotation.START_RELREPORTTIME);
		}
		returnSb.append(reportTime);
		MyLogger.logger.info("reportTime：" + reportTime);
		returnSb.append(Annotation.END_REPORTTIME + "\n");
		returnSb.append(Annotation.START_CONTENT);// 加入content标签

		// 此处开始对段落一个一个进行处理
		Iterator<String> ite = paragraphs.iterator();
		MyLogger.logger.info("在切分段落之后的输出内容是：" + paragraphs);
		while (ite.hasNext()) {
			// 获取分词处理之后标出的触发词
			// 这句话使用的中科院分词工具
			// List<String> paragraphContentList = ICTCLAS_Parse.getLinkedList((String) ite.next());
			// 这句话使用的是哈工大分词工具
			List<String> paragraphContentList = LTP_Parse.getLinkedList(ite.next());
			MyLogger.logger.info("LTP分词工具处理完毕");
			// 将获取的内容进行组拼，主要是为了防止直接输出list有"[]"
			Iterator<String> paragraph_ite = paragraphContentList.iterator();
			StringBuilder paragraph = new StringBuilder();
			while (paragraph_ite.hasNext()) {
				paragraph.append(paragraph_ite.next());
			}
			returnSb.append(paragraph);
		}

		returnSb.append(Annotation.END_CONTENT + "\n");// 加入content结束标签
		returnSb.append(Annotation.END_BODY);
		returnSb.toString().trim();
		MyLogger.logger.info("第二遍过滤结果：\n");
		System.out.println(returnSb);

		// String tempPath = FileUtil.getRootPath() + ReadConfigFile.getValue("tempXML");
		String tempPath = FileUtil.getRootPath("") + ReadConfigFile.getValue("tempXML");
		File tempFile = new File(tempPath);
		// 在此处将内容写入xml文件
		try {
			FileUtils.writeStringToFile(tempFile, returnSb.toString(), EncodingUtil.CHARSET_UTF8);
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		// 在此处进行第三遍过滤，调用Filter方法
		FilterUtil.filter3(tempPath);
		MyLogger.logger.info("第三遍过滤结果：\n");
		try {
			returnSb = new StringBuilder(FileUtils.readFileToString(tempFile, EncodingUtil.CHARSET_UTF8));
		} catch (IOException e1) {
			e1.printStackTrace();
			MyLogger.logger.error(e1.getMessage());
		}
		System.out.println(returnSb);

		// 在第四遍过滤之前，进行经纬度的过滤，主要是将将维度识别为Location
		FilterUtil.filterLongitudeLatitude(tempPath);

		// 在此处进行第四遍过滤，调用filter方法
		FilterUtil.filter4(tempPath);
		// 在此处进行第五遍过滤
		FilterUtil.filter5(tempPath);

		// 然后重新将内容读出来
		try {
			returnSb = new StringBuilder(FileUtils.readFileToString(tempFile, EncodingUtil.CHARSET_UTF8));
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		MyLogger.logger.info("第四遍过滤结果：\n");
		System.out.println(returnSb);
		return returnSb;
	}

	/**
	 * 
	 * <p>
	 * Title: getParagraph
	 * </p>
	 * <p>
	 * Description: 根据给出的文本路径，读取文本内容，并按照段落切分，以段落为单位，存储在List集合中
	 * </p>
	 * 
	 * @param filePath
	 * @return List&lt;String&gt; 段落集合
	 * @throws IOException
	 *
	 */
	private static List<String> getParagraph(String filePath, String charset) throws IOException {
		List<String> res = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();// 拼接读取的内容
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), charset);
		BufferedReader br = new BufferedReader(isr);
		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp + "\n");
		}
		// \s A whitespace character: [ \t\n\x0B\f\r]
		String p[] = sb.toString().split("\\s*\n");
		for (String string : p) {
			res.add(string.replaceAll("\\s*", ""));
		}
		if (br != null)
			br.close();
		if (isr != null)
			isr.close();
		return res;
	}
}
