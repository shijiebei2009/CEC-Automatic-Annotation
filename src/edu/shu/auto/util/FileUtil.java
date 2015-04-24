package edu.shu.auto.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * <p>
 * ClassName FileUtil
 * </p>
 * <p>
 * Description 工具类，对外提供一个方法，可以获取一个文件夹下的所有文件的路径并存储到Set对象实例中
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014年12月1日 下午1:15:46
 *         </p>
 * @version V1.0.0
 *
 */
public class FileUtil {
	public static Set<String> paths = new HashSet<String>();

	public static void listFiles(File file) {
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				listFiles(files[i]);// 递归调用
			} else {
				String path = files[i].getAbsolutePath();// 拿到绝对路径，加入path集合
				paths.add(path);
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Title: getRootPath
	 * </p>
	 * <p>
	 * Description: 使用类加载器获取项目的根目录
	 * </p>
	 * 
	 * @param clazz
	 * @return
	 *
	 */
	public static String getRootPath(String filePath) {
		return FileUtil.class.getClassLoader().getResource(filePath).getPath();
		// InputStream resourceAsStream = FileUtil.class.getClass().getResourceAsStream(filePath);
		// URL resource = Thread.currentThread().getContextClassLoader().getResource(filePath);
		// 注意文件路径一定要以/开头
		// MyLogger.logger.info(filePath);
		// URL resource = FileUtil.class.getClass().getResource(filePath);// 返回null
		// URL resource = clazz.getClass().getResource("/" + filePath);
		// URL resource = FileUtil.class.getClass().getClassLoader().getResource(filePath);// 空指针异常
		// URL resource = FileUtil.class.getClass().getClassLoader().getResource("/" + filePath);// 空指针异常
		// MyLogger.logger.info("resource = " + resource);
		// System.out.println(resource.getFile());
		// Class<? extends Class> currentClass = FileUtil.class.getClass();
		// String getFile()
		// Gets the file name of this URL.
		// String getHost()
		// Gets the host name of this URL, if applicable.
		// String getPath()
		// Gets the path part of this URL.
		// System.out.println(currentClass);
		// String path = currentClass.getResource(filePath).getPath();
		// System.out.println(resource.getFile());
		// MyLogger.logger.info("resource.getPath() = " + resource.getPath());
		// MyLogger.logger.info("resource.getFile() = " + resource.getFile());
		// return resource.getFile();
	}

	@Test
	public void test() {
		String rootPath = getRootPath("");
		System.out.println(rootPath);

	}
}