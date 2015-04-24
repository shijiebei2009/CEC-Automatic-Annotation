package edu.shu.auto.ui;

/**
 * 
 * <p>
 * ClassName Annotation
 * </p>
 * <p>
 * Description 该类用来对文本进行标注，此处我想采用多次过滤的思想，将每一个方法都设置为static，然后一遍遍过滤即可
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-7-19 上午12:08:36
 *         </p>
 * @version V1.0.0
 * 
 */
public class Annotation {
	public static final String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String START_BODY = "<Body>";
	public static final String END_BODY = "</Body>";
	public static final String START_TITLE = "<Title>";
	public static final String END_TITLE = "</Title>";
	public static final String START_TIME = "<Time tid=\"\">";
	public static final String END_TIME = "</Time>";
	public static final String START_ABSREPORTTIME = "<ReportTime  type=\"absTime\">";// 绝对时间开始标签
	public static final String END_REPORTTIME = "</ReportTime>";// 报道时间结束标签
	public static final String START_RELREPORTTIME = "<ReportTime  type=\"relTime\">";// 相对时间开始标签
	public static final String START_PARAGRAPH = "<Paragraph>";
	public static final String END_PARAGRAPH = "</Paragraph>";
	public static final String START_CONTENT = "<Content>";
	public static final String END_CONTENT = "</Content>";
	public static final String START_SENTENCE = "<Sentence>";
	public static final String END_SENTENCE = "</Sentence>";
	public static final String START_DENOTER = "<Denoter did=\"\">";
	public static final String END_DENOTER = "</Denoter>";
	public static final String START_MEANS = "<Means mid=\"\">";
	public static final String END_MEANS = "</Means>";
	public static final String START_LOCATION = "<Location lid=\"\">";
	public static final String END_LOCATION = "</Location>";
	public static final String START_OBJECT = "<Object oid=\"\">";
	public static final String END_OBJECT = "</Object>";
	public static final String START_PARTICIPANT = "<Participant sid=\"\">";
	public static final String END_PARTICIPANT = "</Participant>";
	public static final String START_EVENT = "<Event eid=\"\">";
	public static final String END_EVENT = "</Event>";

	// 使用DOM4j解析的时候，添加Element会自动添加尖角号
	public static final String THOUGHTEVENT_RELATION = "eRelation relType=\"Thoughtcontent\" thoughtevent_eid=\"e\" thoughtcontent_eids=\"e\"";
	public static final String CAUSAL_RELATION = "eRelation relType=\"Causal\" cause_eid=\"e\" effect_eid=\"e\"";
	public static final String ACCOMPANY_RELATION = "eRelation relType=\"Accompany\" accompanya_eid=\"e\" accompanyb_eid=\"e\"";
	public static final String FOLLOW_RELATION = "eRelation relType=\"Follow\" bevent_eid=\"e\" aevent_eid=\"e\"";
	public static final String COMPOSITE_RELATION = "eRelation relType=\"Composite\" fevent_eid=\"e\" sevent_eids=\"e\"";

}
