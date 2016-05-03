package net.yuan.nova.commons;

import net.yuan.nova.cache.CacheHandle;

public abstract class SystemParamHandle extends CacheHandle {

	protected abstract SystemParam getSystemParam(String mask);
	
	protected abstract void put(String mask, SystemParam systemParam);

}
