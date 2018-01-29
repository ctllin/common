package com.ctl.utils.entityutil;


/**
 * 鏁版嵁杞崲鎺ュ彛
 * 
 * @author yonsin
 */
public interface Converter {
	/**
	 * 杩涜杞崲鎿嶄綔
	 * 
	 * @param obj
	 *            鍘熷璞�	 * @return 杞崲鍚庡璞�	 */
    public Object convert(Object obj);
}
