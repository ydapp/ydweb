package net.yuan.nova.core.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * jquery easyUI 树节点数据模型
 * 
 * @author zhangshuai
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode<T> {

	// 节点ID，对加载远程数据很重要。
	private String id;
	// 显示节点文本。
	private String text;
	// 节点使用的图标类
	private String iconCls;
	// 节点状态，'open' 或 'closed'，默认：'open'。如果为'closed'的时候，将不自动展开该节点。
	private String state;
	// 表示该节点是否被选中。
	private String checked;
	// 被添加到节点的自定义属性。
	private T attributes;
	// 一个节点数组声明了若干节点。
	private List<TreeNode<T>> children;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public T getAttributes() {
		return attributes;
	}

	public void setAttributes(T attributes) {
		this.attributes = attributes;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode<T>> children) {
		this.children = children;
	}

}
