package edu.shu.auto.util;

/**
 * 
 * <p>
 * ClassName MySplit
 * </p>
 * <p>
 * Description :我需要以指定的符号对字符串进行切分，同时保留作为切分条件的分割符<br/>
 * 比如：如果原始字符串是“我爱你美国。我？你！她”，那么用“！|？|。”来进行切分的话<br/>
 * 使用Java自带的split方法切分如下：<br/>
 * 我爱你美国<br/>
 * 我<br/>
 * 你<br/>
 * 她 <br/>
 * 使用我的split切分方法如下：<br/>
 * 我爱你美国。<br/>
 * 我？<br/>
 * 你！<br/>
 * 她<br/>
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-19 下午09:40:45
 *         </p>
 * @version V1.0.0
 * 
 */
public class MySplit {
	public static void main(String[] args) {
		String strs[];
		// String str = "我爱你美国。我？你！她。";//测试字串1
		String str = "我爱你美国。But我更爱？中！国。";// 测试字串2
		String regex = "？|！|。";
		strs = MySplit.split(str, regex);// 调用自己封装的split方法
		for (String s : strs) {
			System.out.println(s);// 打印切分结果
		}
	}

	/**
	 * 
	 * <p>
	 * Title: split
	 * </p>
	 * <p>
	 * Description: 根据regex对字符串进行切分，同时保留切分符
	 * </p>
	 * 
	 * @param str
	 *            源字符串
	 * @param regex
	 *            切分的正则表达式
	 * @return 保留切分符的字符串数组
	 *
	 */
	public static String[] split(String str, String regex) {
		String[] strs = str.split(regex);
		int length = str.length();
		int start = 0;
		int lengthTemp = 0;
		int end = 0;
		for (int i = 0; i < strs.length; i++) {
			lengthTemp = strs[i].length();
			end = start + lengthTemp + 1;
			// 此处做一下判断，如果end大于了length，那么将end归位length，这是为了处理不是以切分符结尾的情况
			if (end > length) {
				end = length;
			}
			strs[i] = str.substring(start, end);// 用新的内容来更新字符串数组
			start = end;
		}
		return strs;
	}
}
