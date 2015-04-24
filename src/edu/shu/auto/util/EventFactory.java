package edu.shu.auto.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import edu.shu.auto.log.MyLogger;
import edu.shu.auto.ltp.domain.LTPXMLTag;

/**
 * 
 * <p>
 * ClassName EventFactory
 * </p>
 * <p>
 * Description 这是一个使用SAX解析XML文档的工具类，主要是将ltp分析之后的xml文件的内容存入到WordsUtil类中
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-17 下午04:33:14
 *         </p>
 * @version V1.0.0
 * 
 */
public class EventFactory extends DefaultHandler {
	private static XMLReader xmlReader;

	public static void readXML(String xmlPath) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = spf.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(new EventFactory());
			xmlReader.parse(new InputSource(xmlPath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}

		MyLogger.logger.info("XML文件解析完毕");
		// 解析完毕之后，对argArrayList链表过滤，过滤掉重复的内容，因为ltp返回的xml文件中arg会有重复的内容
		if (WordsUtil.argArrayList.size() > 0) {
			WordsUtil.argArrayList = FilterUtil.filterList(WordsUtil.argArrayList);
		}
	}

	/**
	 * 
	 * <p>
	 * Title: readXML
	 * </p>
	 * <p>
	 * Description 使用SAX读取XML，并对arg标签进行过滤
	 * </p>
	 * 
	 * @param xmlPath
	 *            ：xml文件路径
	 * @param delete
	 *            ：true则删除xml文件，false则不删除xml文件
	 * @param filter
	 *            ：true则对xml中的Arg进行过滤，false则不过滤
	 *
	 */
	public static void readXML(String xmlPath, boolean delete, boolean filter) {
		File file = new File(xmlPath);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = spf.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(new EventFactory());
			xmlReader.parse(new InputSource(xmlPath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		MyLogger.logger.info("XML文件解析完毕");
		if (filter) {
			// 解析完毕之后，对argArrayList链表过滤，过滤掉重复的内容，因为ltp返回的xml文件中arg会有重复的内容
			if (WordsUtil.argArrayList.size() > 0) {
				WordsUtil.argArrayList = FilterUtil.filterList(WordsUtil.argArrayList);
			}
		}
		if (delete) {
			if (file.delete()) {
				MyLogger.logger.info("XML文件删除成功");
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(LTPXMLTag.WORD)) {
			// 获取元素中的属性值，如<a key="key" value="value"/>，获取key和value
			Integer id = Integer.parseInt(attributes.getValue(LTPXMLTag.ID));
			String cont = attributes.getValue(LTPXMLTag.CONT);
			String pos = attributes.getValue(LTPXMLTag.POS);
			String ne = attributes.getValue(LTPXMLTag.NE);
			String relate = attributes.getValue(LTPXMLTag.RELATE);
			WordsUtil.contentHashMap.put(id, cont);
			WordsUtil.posHashMap.put(id, pos);
			WordsUtil.neHashMap.put(id, ne);
			WordsUtil.relateHashMap.put(id, relate);
			WordsUtil.ids.add(id);// 将id依次加入
		} else if (qName.equals(LTPXMLTag.ARG)) {
			String type = attributes.getValue(LTPXMLTag.TYPE);
			String beg = attributes.getValue(LTPXMLTag.BEG);
			String end = attributes.getValue(LTPXMLTag.END);
			if ("TMP".equalsIgnoreCase(type)) {
				WordsUtil.argArrayList.add(type);
				WordsUtil.argArrayList.add(beg);
				WordsUtil.argArrayList.add(end);
			} else if ("DIS".equalsIgnoreCase(type)) {
				WordsUtil.argArrayList.add(type);
				WordsUtil.argArrayList.add(beg);
				WordsUtil.argArrayList.add(end);
			} else if ("LOC".equalsIgnoreCase(type)) {
				WordsUtil.argArrayList.add(type);
				WordsUtil.argArrayList.add(beg);
				WordsUtil.argArrayList.add(end);
			}
		}
	}

	@Override
	public void startDocument() throws SAXException {
		MyLogger.logger.info("start document...");
	}

	@Override
	public void endDocument() throws SAXException {
		MyLogger.logger.info("end document...");
	}
}
