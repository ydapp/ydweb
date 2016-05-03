package net.yuan.nova.pis.service;

import java.util.List;
import java.util.Map;

import net.yuan.nova.commons.SystemConstant;
import net.yuan.nova.commons.SystemParam;
import net.yuan.nova.commons.SystemParamHandle;
import net.yuan.nova.pis.dao.PisSystemParamMapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 获得系统参数和一些静态数据
 * 
 * @author Administrator
 *
 */
@Service
public class SystemParamService extends SystemParamHandle implements InitializingBean {

	protected final Logger log = LoggerFactory.getLogger(SystemParamService.class);

	@Autowired
	private PisSystemParamMapper systemParamDao;

	/**
	 * 字典属性名称查询属性列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public List<Map<String, String>> getDataList(String dictName) {
		if (StringUtils.isBlank(dictName)) {
			// 字典名称为空，返回null
			return null;
		}
		if (StringUtils.contains(dictName, " ")) {
			// 字典名称中含有空值，返回null
			return null;
		}
		// 判断表是否存在

		String sql = new StringBuilder("select * from ").append(dictName).toString();
		return systemParamDao.getDataList(sql);
	}

	/**
	 * 字典属性名称查询属性列表
	 * 
	 * @param dictName
	 *            字典名称
	 * @param flag
	 *            是否统一格式
	 * @return
	 */
//	public List<ComboBoxItem> getDictValues(String dictName) {
//		List<Map<String, String>> list = getDataList(dictName);
//		List<ComboBoxItem> dictValues = new ArrayList<ComboBoxItem>();
//		// 通过映射文件返回统一的格式
//		for (Map<String, String> map : list) {
//			ComboBoxItem dictValue = beanMapper.map(map, ComboBoxItem.class, dictName.toLowerCase());
//			dictValues.add(dictValue);
//		}
//		return dictValues;
//	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("开始加载系统参数信息");
		SystemConstant.setSystemParams(this);
		try {
			refresh();
		} catch (Exception e) {
			log.error("配置完成，加载系统参数出错。", e);
		}
	}

	/**
	 * 刷新系统参数数据
	 */
	public void refresh() {
		getAllSystemParam();
	}

	/**
	 * 获得所有参数
	 * 
	 * @return
	 */
	public List<SystemParam> getAllSystemParam() {
		List<SystemParam> systemParams = systemParamDao.findAll();
		if (systemParams != null) {
			for (SystemParam systemParam : systemParams) {
				this.put(systemParam.getMask(), systemParam);
			}
		}
		log.info("系统参数加载完成");
		return systemParams;
	}

	@Override
	public SystemParam getSystemParam(String mask) {
		if (StringUtils.isBlank(mask)) {
			return null;
		}
		mask = mask.toUpperCase();
		SystemParam param = (SystemParam) this.getCacheValue(mask);
		if (param == null) {
			// 没有相应的数据，尝试从数据库中获得
			param = systemParamDao.findSystemParam(mask);
			if (param != null) {
				this.put(mask, param);
			}
		}
		return param;
	}

	@Override
	protected void put(String mask, SystemParam systemParam) {
		this.setCacheValue(mask, systemParam);
	}

	public int updataSystemParams(String currentValue, String comments, String mask) {
		int flag = systemParamDao.updataSystemParams(currentValue, comments, mask);
		if (flag == 1) {
			SystemParam systemParam = new SystemParam();
			systemParam.setComments(comments);
			systemParam.setMask(mask);
			systemParam.setCurrentValue(currentValue);
			put(mask, systemParam);
		}
		return flag;
	}

}
