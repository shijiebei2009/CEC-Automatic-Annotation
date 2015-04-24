package edu.shu.auto.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import edu.shu.auto.log.MyLogger;

/**
 * 
 * <p>
 * ClassName WriteToXMLUtil
 * </p>
 * <p>
 * Description 提供向xml文件写入的工具类
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014年11月19日 下午10:30:34
 *         </p>
 * @version V1.0.0
 *
 */
public class WriteToXMLUtil {
	public static boolean writeToXML(Document document, String tempPath) {
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
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error("写入xml文件出错！");
			return false;
		}
		return true;
	}

	@Test
	public void writeToXML() throws IOException {
		String content = "\\&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?> &lt;Body> &lt;Title>fsa</Title>   <ReportTime type=\"relTime\">fsd</ReportTime>   <Content> <Paragraph> <Sentence>XML文档结构相似度研究及其 <Location lid=\"\">在文档聚类中</Location>应用1、基于树的编辑距离计算相似度，计算结构化文档相似度的主要方法。 </Sentence>   <Sentence>需要把结构化文档转成有序标记树。</Sentence>   <Sentence>即文档中的元素或属性转换为树中的结点，文档中的结构关系转换为树中的边。</Sentence> </Paragraph> </Content> </Body>";
		String tempPath = "d:\\aa.xml";
		FileUtils.writeStringToFile(new File(tempPath), content);
		//
		// try {
		// // 使用XMLWriter写入，可以控制格式，经过调试，发现这种方式会出现乱码，主要是因为Eclipse中xml文件和JFrame中文件编码不一致造成的
		// OutputFormat format = OutputFormat.createPrettyPrint();
		// format.setEncoding(EncodingUtil.CHARSET_UTF8);
		// // format.setSuppressDeclaration(true);//这句话会压制xml文件的声明，如果为true，就不打印出声明语句
		// format.setIndent(true);// 设置缩进
		// format.setIndent("	");// 空行方式缩进
		// format.setNewlines(true);// 设置换行
		// XMLWriter writer = new XMLWriter(new FileWriterWithEncoding(new File(tempPath), EncodingUtil.CHARSET_UTF8), format);
		// writer.write(readFileToString);
		// writer.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// MyLogger.logger.error("写入xml文件出错！");
		// }
	}
}