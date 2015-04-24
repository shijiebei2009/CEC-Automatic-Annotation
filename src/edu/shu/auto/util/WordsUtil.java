package edu.shu.auto.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>
 * ClassName WordsUtil
 * </p>
 * <p>
 * Description 作为在第一遍过滤时候使用的辅助工具类
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014年11月19日 下午10:29:13
 *         </p>
 * @version V1.0.0
 *
 */
public class WordsUtil {
	// 为了保证一致性，key统一存储的是id，value分别存储的是content/pos/ne/relate
	public static Map<Integer, String> contentHashMap = new HashMap<Integer, String>();
	public static Map<Integer, String> posHashMap = new HashMap<Integer, String>();
	public static Map<Integer, String> neHashMap = new HashMap<Integer, String>();
	public static Map<Integer, String> relateHashMap = new HashMap<Integer, String>();
	// 其中的内容表示LOC和TMP和DIS以及beg和end，存储方式均是type后面跟着beg/end
	public static List<String> argArrayList = new ArrayList<String>();
	// 设立一个ID数组，用来表示还没有进行处理的ID
	public static List<Integer> ids = new ArrayList<Integer>();
	// paragraph用来存储段落的开始和结束
}
