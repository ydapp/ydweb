package net.yuan.nova.core.vo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * jquery easyUI 数据表格数据模型
 * 
 * @author zhangshuai
 *
 */
public class DataGridData<T> extends JsonVo<T> {

	// 总记录数
	private long total;
	// 行数据
	private Collection<T> rows;

	public DataGridData() {
		this.setSuccess(true);
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Collection<T> getRows() {
		if (rows == null) {
			rows = new ArrayList<T>();
		}
		return rows;
	}

	public void setRows(Collection<T> rows) {
		this.rows = rows;
	}

}
