package com.ctl.web.common.mapper;

import java.util.List;
import java.util.Map;

public interface CommonMapper {
	/**
	 * 
	 * @param sql
	 * @return 返回多条多个属性 [{buyerTelephone=13984112233, orderNumber=0A00C3BB2CD77B1B}, {buyerTelephone=13622119295, orderNumber=FEA76DD18299E292}]
	 */
	List<Map<String,Object>> selectBySql(String sql);
	/**
	 * @param sql 查询条件  select id from tablename where id='1'
	 * @return 返回一条一个属性 1140
	 */
	Object selectBySql1(String sql);
	/**
	 * @param sql 查询条件  select id from tablename
	 * @return 返回多条一个属性 [1140, 3220]
	 */
	List<Object> selectBySql2(String sql);
	/**
	 * 
	 * @param sql select * from tablename where id='1'
	 * @return  返回一条多个属性  {buyerTelephone=13984112233, orderNumber=0A00C3BB2CD77B1B20170307111522}
	 */
	Map<String,Object> selectBySql3(String sql);
	
	int deleteBySql(String sql);
	
	int updateBySql(String sql);
	
	int countBySql(String sql);
	/**
	 * 订单接口
	 * @param paramMap
	 * @return
	 */
	List<Map<String,Object>> apiOrdersList(String sql);
}
