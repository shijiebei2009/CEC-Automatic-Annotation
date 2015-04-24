package edu.shu.auto.domain;

/**
 * 
 * <p>
 * ClassName FilterEntity
 * </p>
 * <p>
 * Description 对LTP返回的语义角色标注识别进行过滤的时候用到的domain对象，<br/>
 * &lt;arg id="0" type="A0" beg="10" end="11"/&gt; <br/>
 * &lt;arg id="1" type="TMP" beg="12" end="12"/&gt;<br/>
 * 在ltp返回的分析结果中，有时出现多个arg标签一样的情况，所以在一个句子中要对重复的arg标签进行过滤
 * </p>
 * 
 * @author wangxu wangx89@126.com
 *         <p>
 *         Date 2014-10-31 下午03:44:15
 *         </p>
 * @version V1.0.0
 * 
 */
public class FilterEntity {
	private String name;// type属性的值
	private String startId;// 开始ID的标号
	private String endId;// 结束ID的标号

	public FilterEntity() {
	}

	public FilterEntity(String name, String startId, String endId) {
		this.name = name;
		this.startId = startId;
		this.endId = endId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartId() {
		return startId;
	}

	public void setStartId(String startId) {
		this.startId = startId;
	}

	public String getEndId() {
		return endId;
	}

	public void setEndId(String endId) {
		this.endId = endId;
	}

	/**
	 * 覆写hashCode方法
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endId == null) ? 0 : endId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((startId == null) ? 0 : startId.hashCode());
		return result;
	}

	/**
	 * 覆写equals方法
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterEntity other = (FilterEntity) obj;
		if (endId == null) {
			if (other.endId != null)
				return false;
		} else if (!endId.equals(other.endId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startId == null) {
			if (other.startId != null)
				return false;
		} else if (!startId.equals(other.startId))
			return false;
		return true;
	}

	/**
	 * 覆写toString方法
	 */
	@Override
	public String toString() {
		return "FilterEntity [name=" + name + ", startId=" + startId + ", endId=" + endId + "]";
	}
}
