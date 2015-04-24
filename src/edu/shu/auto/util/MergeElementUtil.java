package edu.shu.auto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 
 * <p>
 * ClassName MergeElementUtil
 * </p>
 * <p>
 * Description 用来实现对紧邻的相同的标签元素的内容和两对标签之间的非标签内容进行合并
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014年11月26日 下午6:43:44
 *         </p>
 * @version V1.0.0
 *
 */
public class MergeElementUtil {
	private static SAXReader saxReader;
	private static Document document;
	static String path = "test/mergeTest.xml";

	public static void main(String[] args) {
		mergeByPath(path);
	}

	public static void mergeByPath(String filePath) {
		saxReader = new SAXReader();
		try {
			document = saxReader.read(new FileInputStream(new File(filePath)));
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		List<Element> eventElements = document.selectNodes("//Sentence/Event");// 拿到所有的Event节点
		mergeElement(eventElements);// 合并紧邻的相同的要素或者是中间间隔内容长度不超过2的情况
		boolean writeToXML = WriteToXMLUtil.writeToXML(document, filePath);
		if (writeToXML)
			System.out.println("merge elements SUCCESS!");
		else
			System.out.println("merge elements Failed");
	}

	/**
	 * 
	 * <p>
	 * Title: mergeElement
	 * </p>
	 * <p>
	 * Description: 该函数可以实现将两个紧邻的标签内容进行合并，也就是减少标签个数，比如<Time>a</Time><Time>b</Time>合并之后为<Time>ab</Time>
	 * </p>
	 * 
	 * @param selectNodes
	 *            父节点列表集合
	 * @param nodeName
	 *            需要合并的要素名称
	 *
	 */
	private static void mergeElement(List<Element> selectNodes) {
		// selectNodes是传递进来的所有Event元素
		for (int outIndex = 0; outIndex < selectNodes.size(); outIndex++) {// 开始遍历所有的Event节点
			Element event_node = selectNodes.get(outIndex);
			@SuppressWarnings("unchecked")
			List<Element> elements = event_node.elements();// 获取Event节点下所有元素名称
			int size = elements.size();
			// 只有当size大于1的时候才进一步处理，size大于1，说明有多个同一要素的节点，才有继续的必要
			if (size > 1) {
				for (int i = 0; i < size; i++) {
					int nextIndex = i + 1;
					Element element = elements.get(i);
					String priorNodeName = element.getName().trim();
					System.out.println("priorNodeName = " + priorNodeName);
					String priorTimeValue = element.getTextTrim();// 拿到前一个节点的值
					System.out.println("priorTimeValue = " + priorTimeValue);
					// 开始判断，如果该节点之后紧跟着相同的标签，并且中间没有值的话，开始进行合并
					@SuppressWarnings("unchecked")
					List<Node> textNodes = event_node.selectNodes("child::text()");// child::text()选取当前节点的所有文本子节点
					Node node = textNodes.get(nextIndex);// 注意，这个要取要素内容的下一个节点，才有判断的必要，如果此处用i的话，只能取到要素内容上面的不在标签之内的内容
					// System.out.println(node.getName());// 拿到的永远是null
					String nodeValue = node.getText().trim();// 拿到不属于标签之内的值，此处有一个注意点，如果该值前面有空行，那么拿到的永远都是空，否则可以取得该值
					System.out.println("nodeValue = " + nodeValue);
					int length = nodeValue != null ? nodeValue.length() : 0;
					String nextNodeName = null;
					String nextTimeValue = null;
					if ((nextIndex) < size) {
						Element tempEle = elements.get(nextIndex);
						nextNodeName = tempEle.getName().trim();
						System.out.println("nextNodeName = " + nextNodeName);
						nextTimeValue = tempEle.getTextTrim();
						System.out.println("nextTimeValue = " + nextTimeValue);
					}
					System.out.println("nodeValue = " + nodeValue);
					// 只要length小于等于2，并且两个标签名称相同，才可以认为两个标签的内容可以合并
					if (length <= 2 && priorNodeName.equals(nextNodeName)) {
						System.out.println("匹配成功!");
						// 那么需要将三部分的值内容拼接到一起，并且删除后面一个标签
						String totalValue = priorTimeValue + nodeValue + nextTimeValue;
						System.out.println("拼接之后的结果：" + totalValue);
						// 重新设置前一个标签的内容
						element.setText(totalValue);
						Element remove = elements.remove(nextIndex);// 移除后一个，注意重新置size，重新循环，这句话是移除标签及标签之内的内容
						System.out.println("remove掉的值是 = " + remove.getStringValue());
						boolean removeFlag = elements.remove(node);// 这句话是移除标签之外的内容，即便移除成功，返回值依然是false，因为该值不在标签之内，所以不认为是一个元素，那么只有移除一个元素的时候，返回值才为true
						System.out.println("removeFlag = " + removeFlag);
						size = elements.size();
						i = -1;// 重新从头开始遍历
					}
				}
			}
		}
	}

}
