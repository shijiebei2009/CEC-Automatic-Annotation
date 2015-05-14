package edu.shu.auto.ltp.cloud;

/*
 * This example shows how to use Java to build http connection and request
 * the ltp-cloud service for perform full-stack Chinese language analysis
 * and get results in specified formats
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;

import edu.shu.auto.log.MyLogger;
import edu.shu.auto.util.EventFactory;
import edu.shu.auto.util.FileUtil;
import edu.shu.auto.util.ReadConfigFile;

/**
 * 
 * <p>
 * ClassName LTPAction
 * </p>
 * <p>
 * Description getenv 是获取系统的环境变更，对于windows对在系统属性-->高级-->环境变量中设置的 变量将显示在此(对于 linux,通过 export 设置的变量将显示在此) <br/>
 * <br/>
 * getProperties 是获取系统的相关属性,包括文件编码,操作系统名称,区域,用户名等,此属性一 般由 jvm 自动获取,不能设置.<br/>
 * <br/>
 * enter+newline with different platforms:<br/>
 * windows: \r\n <br/>
 * mac: \r <br/>
 * unix/linux: \n
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-16 下午01:11:17
 *         </p>
 * @version V1.0.0
 * 
 */
public class LTPAction {
	private final String API_KEY = ReadConfigFile.getValue("API_KEY");
	private final String PATTERN = "all";// 标注出所有识别出的信息
	private String format;
	private URL url;
	private URLConnection conn;
	private BufferedReader br;
	private BufferedWriter bw;
	private String line;
	private StringBuilder stringBuilder = new StringBuilder();
//	private String ltpCloudURL = "http://api.ltp-cloud.com/analysis/?";// 使用哈工大的LTP调用接口
	 private String ltpVoiceCloudURL = "http://ltpapi.voicecloud.cn/analysis/?";// 使用哈工大与科大讯飞合作的语音云调用接口

	public void getXML(File file, String type) {
		String filePath = file.getAbsolutePath();
		String fileName = file.getName();
		int txtIndex = fileName.indexOf(".txt");// 获取.txt索引
		int xmlIndex = fileName.indexOf(".xml");// 获取.xml索引
		int end = txtIndex > xmlIndex ? txtIndex : xmlIndex;// 取得较大的那一个，因为有的文件名是.txt.xml，所以需要这样处理
		fileName = fileName.substring(0, end);
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getXML(stringBuilder.toString(), fileName, type);
	}

	/**
	 * 
	 * <p>
	 * Title: getXML
	 * </p>
	 * <p>
	 * Description: 调用LTP REST API以便获得LTP的分析结果
	 * </p>
	 * 
	 * @param text
	 * @param fileName
	 * @param type
	 *
	 */
	public void getXML(String text, String fileName, String type) {
		if (type == null || !(type.equals("xml") || type.equals("json") || type.equals("conll"))) {
			MyLogger.logger.error("Usage: java SimpleAPI [xml/json/conll]");
			return;
		}
		format = type;
		try {
			text = URLEncoder.encode(text, "utf-8");// 对待分析文本进行编码
			url = new URL(ltpVoiceCloudURL + "api_key=" + API_KEY + "&" + "text=" + text + "&" + "format=" + format + "&" + "pattern="
					+ PATTERN);
			conn = url.openConnection();// 打开连接
			conn.connect();// 连接
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			fileName = FileUtil.getRootPath("") + ReadConfigFile.getValue("tempXML");
			bw = new BufferedWriter(new PrintWriter(new File(fileName)));
			while ((line = br.readLine()) != null) {
				bw.write(line + "\n");// 注意要换行
			}
			if (br != null)
				br.close();
			bw.flush();
			if (bw != null)
				bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error(e.getMessage());
		}
		
		System.out.println("------------------");
		try {
			System.out.println(FileUtils.readFileToString(new File(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("------------------");
		MyLogger.logger.info("LTP处理完毕");
		// 在写完xml文件之后，调用EventFactory来解析xml文件
		EventFactory.readXML(fileName);
	}
}