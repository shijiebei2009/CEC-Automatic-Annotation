package edu.shu.auto.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * <p>
 * ClassName MyLogger
 * </p>
 * <p>
 * Description 自定义的日志对象工具类，直接调用该类的静态属性即可获取Logger的实例对象
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-31 下午03:44:41
 *         </p>
 * @version V1.0.0
 * 
 */
public class MyLogger {
	public static Logger logger = LogManager.getLogger();
}
