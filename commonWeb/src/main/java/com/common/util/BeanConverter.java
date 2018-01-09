package com.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method; 
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap; 
import java.util.Iterator;
import java.util.Map; 
import java.util.TreeMap;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.log4j.Logger;

/** 
*转换器 
*1:将JavaBean 转换成Map、JSONObject 
*2:将JSONObject 转换成Map 
* 
* @author xxx 
*/ 

public class BeanConverter 
{ 
	static Logger logger = Logger.getLogger(BeanConverter.class);  
	public static void main(String[] args) throws Exception {
		
		JsonConfig jsonConfig=new JsonConfig();
		Map<String,Object> classMap=new HashMap<String,Object>();
		jsonConfig.setClassMap(classMap);
		JsonDateValueProcessor jsonValueProcessor = new JsonDateValueProcessor();  
		jsonConfig.registerJsonValueProcessor(Date.class, jsonValueProcessor);  
		jsonConfig.registerJsonValueProcessor(java.sql.Date.class, jsonValueProcessor);  
		jsonConfig.registerJsonValueProcessor(Timestamp.class, jsonValueProcessor);  
		jsonConfig.registerJsonValueProcessor(Float.class, new JsonFloatValueProcessor()); 
		jsonConfig.registerJsonValueProcessor(float.class, new JsonFloatValueProcessor()); 
        jsonConfig.setJsonPropertyFilter(new JsonNullValuePropertyFilter());
		Person person=new Person();
		person.setId(1);
		person.setJine(999.346734d);
		person.setJineMax(9.12354d);
		person.setMax(10);
		person.setName("ctl");
		person.setPrice(1000.99647f);
		person.setPriceMax(10.0134646f);
		person.setResult(true);
		person.setSavetime(new Date());
		person.setSuccess(false);
		person.setContent(null);
		Map<String, Object> data =new TreeMap<String, Object>();
	    transBean2Map(person, data);
	    Person personData=new Person();
	    System.out.println(data);
	    transMap2Bean(personData, data);
	    System.out.println(JSONObject.fromObject(personData,jsonConfig));
	    
	    
	}
     
    
    /** 
     * 将json对象转换成Map 
     * 
     * @param jsonObject json对象 
     * @return Map对象 
     */ 
    @SuppressWarnings("unchecked")
	public static Map<Object, Object> toMap(JSONObject jsonObject) 
    { 
        Map<Object, Object> result = new HashMap<Object, Object>(); 
        Iterator<Object> iterator = jsonObject.keys(); 
        Object key = null; 
        Object value = null; 
        while (iterator.hasNext()) 
        { 
            key = iterator.next(); 
            value = jsonObject.get(key); 
            result.put(key, value); 
        } 
        return result; 
    } 

    /** 
     * 将javaBean转换成JSONObject 
     * 
     * @param bean javaBean 
     * @return json对象 
     */ 
//    public static JSONObject toJSON(Object bean) 
//    { 
//        return new JSONObject(toMap(bean)); 
//    } 

    /** 
     * 将map转换成Javabean 
     * @param javabean javaBean 
     * @param data map数据 
     */ 
    public static Object toJavaBean(Object javabean, Map<Object, Object> data) 
    { 
        Method[] methods = javabean.getClass().getDeclaredMethods(); 
        for (Method method : methods){ 
        	String field =null;
            try { 
                if (method.getName().startsWith("set")){
                    field = method.getName(); 
                    field = field.substring(field.indexOf("set") + 3); 
                    field = field.toLowerCase().charAt(0) + field.substring(1); 
                    Object value=data.get(field);
                    if(value!=null){
	                    try {
	                    	method.invoke(javabean, new Object[]{(Object)value}); 
						} catch (Exception e) {
							method.invoke(javabean, new Object[]{Float.parseFloat(value.toString())}); 
						}
                    }
                } 
            }catch (Exception e){ 
            	logger.error(field+"类型为:"+field.getClass(),e);
            } 
        } 
        return javabean; 
    } 
    public static Map<String, Object> transBean2Map(Object obj,Map<String,Object> map) {
        if(obj == null){
            return null;
        }        
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if(null!=value&&!"".equals(value))
                    map.put(key, value);
                }

            }
        } catch (Exception e) {
        	logger.error("transBean2Map Error " , e);
        }
        return map;
    }
    
    
    public static Object transMap2Bean(Object javabean, Map<String, Object> data) 
    { 
        Method[] methods = javabean.getClass().getDeclaredMethods(); 
        for (Method method : methods){
            try{ 
                if (method.getName().startsWith("set")){
                    String field = method.getName(); 
                    field = field.substring(field.indexOf("set") + 3); 
                    field = field.toLowerCase().charAt(0) + field.substring(1); 
                    method.invoke(javabean, new Object[]{data.get(field)}); 
                } 
            }catch (Exception e){ 
            	logger.error(e);
            } 
        } 
        return javabean; 
    } 
    
    /** 
     * 将javaBean转换成JSONObject 
     * 
     * @param bean javaBean 
     * @return json对象 
     * @throws ParseException json解析异常 
     */ 
//    public static void toJavaBean(Object javabean, String data) throws ParseException 
//    { 
//        JSONObject jsonObject = new JSONObject(data); 
//        Map<String, String> datas = toMap(jsonObject); 
//        toJavaBean(javabean, datas); 
//    } 
}
