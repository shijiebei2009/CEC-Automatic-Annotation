package edu.shu.auto.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.shu.auto.log.MyLogger;

/**
 * 
 * <p>
 * ClassName ReadConfigFile
 * </p>
 * <p>
 * Description 对外提供静态方法，接收配置文件中的KEY作为参数，返回对应的VALUE值
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年4月16日 下午4:28:09
 *         </p>
 * @version V1.0.0
 *
 */
public class ReadConfigFile {
	private static InputStream is;
	private static Properties props;
	static {
		is = ReadConfigFile.class.getClassLoader().getResourceAsStream("config.properties");
		props = new Properties();
	}

	public static String getValue(String key) {
		try {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			MyLogger.logger.error("未加载到资源文件！");
		}
		// 注意键值是区分大小写的"API_KEY"
		String value = (String) props.get(key);
		return value;
	}
}
