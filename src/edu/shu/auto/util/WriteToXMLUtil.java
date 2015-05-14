package edu.shu.auto.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

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
}