package edu.shu.auto.constant;

/**
 * 
 * <p>
 * ClassName EventConstant
 * </p>
 * <p>
 * Description 根据事件的六元组定义，该类映射事件定义中各个要素的标签以及属性
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014年11月22日 下午3:39:02
 *         </p>
 * @version V1.0.0
 *
 */
public class EventConstant {
	// 定义事件要素标签
	public static final String DENOTER = "Denoter";// 触发词要素
	public static final String MEANS = "Means";// 触发词方式要素
	public static final String TIME = "Time";// 时间要素
	public static final String LOCATION = "Location";// 地点要素
	public static final String PARTICIPANT = "Participant";// 施动者要素
	public static final String OBJECT = "Object";// 受动者要素
	public static final String EVENT = "Event";// 事件标签
	// 定义要素的id属性
	public static final String EID = "eid";// Event标签属性，余下类似
	public static final String DID = "did";
	public static final String SID = "sid";
	public static final String OID = "oid";
	public static final String TID = "tid";
	public static final String LID = "lid";
	public static final String MID = "mid";

	// 定义要素的id属性的值的前缀
	public static final String E = "e";
	public static final String D = "d";
	public static final String S = "s";
	public static final String O = "o";
	public static final String T = "t";
	public static final String L = "l";
	public static final String M = "m";
}
