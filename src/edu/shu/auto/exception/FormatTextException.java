package edu.shu.auto.exception;

import java.io.FileNotFoundException;
import java.io.IOException;

import edu.shu.auto.log.MyLogger;

/**
 * 
 * <p>
 * ClassName FormatTextException
 * </p>
 * <p>
 * Description 自定义文本格式化异常类
 * </p>
 * 
 * @author TKPad wangx89@126.com
 *         <p>
 *         Date 2015年4月16日 下午4:18:53
 *         </p>
 * @version V1.0.0
 *
 */
public class FormatTextException extends Exception {
	private static final long serialVersionUID = 1L;

	public FormatTextException() {
	}

	public FormatTextException(String msg) {
		super(msg);
		printStackTrace();
	}

	public FormatTextException(Exception e, String file) {
		super(e);
		if (e instanceof FileNotFoundException) {
			MyLogger.logger.error("文件不存在：" + file);
		} else if (e instanceof ClassNotFoundException) {
			MyLogger.logger.error("未发现类：" + e.getMessage());
		} else if (e instanceof IOException) {
			MyLogger.logger.error("文件读取错误：" + file);
		}
		e.printStackTrace();
	}
}
