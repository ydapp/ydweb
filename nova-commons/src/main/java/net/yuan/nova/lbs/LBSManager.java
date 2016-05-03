package net.yuan.nova.lbs;

import org.apache.http.entity.mime.content.FileBody;

public interface LBSManager {

	/**
	 * 创建表（create geotable）接口
	 */
	public String createGeotable(String name, String ak);

	/**
	 * 创建列
	 * 
	 * @param name
	 * @param key
	 * @param type
	 * @param isSortfilterField
	 * @param isSearchField
	 * @param isTndexField
	 * @param ak
	 */
	public void createColumn(String name, String key, String type, boolean isSortfilterField, boolean isSearchField,
			boolean isTndexField, String ak);

	/**
	 * 获取列列表
	 * 
	 * @param geotableId
	 * @param ak
	 */
	public String getColumnList(String geotableId, String ak);

	/**
	 * 查询指定条件的数据（poi）列表接口
	 * 
	 * @param geotableId
	 * @param ak
	 * @return
	 */
	public String getDataList(String geotableId, String ak, String page_index, String page_size, String title,
			String tags);

	/**
	 * 按条件删掉poi数据
	 * 
	 * @param geotableId
	 * @param ak
	 * @param isTotalDel
	 * @param id
	 * @return
	 */
	public String deleteData(String geotableId, String ak, String isTotalDel, String id);

	/**
	 * 上传数据
	 * 
	 * @param geotableId
	 * @param ak
	 * @param poiList
	 * @param timestamp
	 * @return
	 */
	public String uploadData(String geotableId, String ak, FileBody poiList);

}
