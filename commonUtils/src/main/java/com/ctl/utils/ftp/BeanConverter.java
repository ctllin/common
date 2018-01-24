package com.ctl.utils.ftp;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method; 
import java.util.Date;
import java.util.HashMap; 
import java.util.Iterator;
import java.util.Map; 
import java.util.Set;

import org.apache.log4j.Logger;
/** 
*ת���� 
*1:��JavaBean ת����Map��JSONObject 
*2:��JSONObject ת����Map 
* 
* @author xxx 
*/ 
public class BeanConverter 
{ 
	static Logger logger = Logger.getLogger(BeanConverter.class);  
	static{
		try {
			new DesUtils();
		} catch (Exception e) {
			logger.error("��ʼ��DesUtilsʧ�ܣ�",e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String str=DateUtil.sdf.format(new Date());
		System.out.println(DesUtils.encrypt(str));
		System.out.println(DesUtils.decrypt(DesUtils.encrypt(str)));
	}
    /** 
     * ��javaBeanת����Map(���ܺ�)  
     * 
     * @param javaBean javaBean 
     * @return Map���� 
     */ 
    public static Map<String, String> toEncryptMap(Object javaBean) 
    { 
        Map<String, String> result = new HashMap<String, String>(); 
        Method[] methods = javaBean.getClass().getDeclaredMethods(); 
        for (Method method : methods){ 
            try{ 
                if (method.getName().startsWith("get")){
                    String field = method.getName(); 
                    String simpleName=method.getReturnType().getSimpleName();
                    field = field.substring(field.indexOf("get") + 3); 
                    field = field.toLowerCase().charAt(0) + field.substring(1); 
                    Object value = method.invoke(javaBean, (Object[])null); 
                    if(null!=value&&!"".equals(value)&&"String".equals(simpleName)){
                    	result.put(field, null == value ? "" : DesUtils.encrypt( value.toString()));
                    }else{
                    	result.put(field, null == value ? "" : value.toString());
                    }
                } 
            }catch (Exception e){
            	logger.error(e);
            } 
        } 
        return result; 
    } 
	/**
	 * 
	 * @param ��javaBeanת����Map(���ܺ�)  
	 * @return
	 */
    public static Map<String, String> toDecryptMap(Object javaBean) 
    { 
        Map<String, String> result = new HashMap<String, String>(); 
        Method[] methods = javaBean.getClass().getDeclaredMethods(); 

        for (Method method : methods){
            try{
                if (method.getName().startsWith("get")){
                	 String simpleName=method.getReturnType().getSimpleName();
                	String field = method.getName(); 
                    field = field.substring(field.indexOf("get") + 3); 
                    field = field.toLowerCase().charAt(0) + field.substring(1); 

                    Object value = method.invoke(javaBean, (Object[])null); 
                    if(null!=value&&!"".equals(value)&&"String".equals(simpleName)){
                    	result.put(field, null == value ? "" : DesUtils.decrypt( value.toString()));
                    }else{
                    	result.put(field, null == value ? "" : value.toString());
                    }
                } 
            }catch (Exception e){
            	logger.error(e);
            } 
        } 
        return result; 
    } 
    /**
     * 
     * @param mapData  ������map
     * @return
     */
    public static Map<String, String> toDecryptMap(Map<String,String> mapData) 
    { 
    	 Set<String> key=mapData.keySet();
         Iterator<String> iter=key.iterator();
         while(iter.hasNext()){
         	String keyStr=iter.next();
         	if(!(mapData.get(keyStr) instanceof String)){
         		continue;
         	}
         	String valueStr=mapData.get(keyStr);
         	try {
				valueStr=null==valueStr ? "" : DesUtils.decrypt( valueStr.toString());
			} catch (Exception e) {
				logger.error(keyStr+" ����ʧ��,����ǰ��"+valueStr,e);
			}
         	mapData.put(keyStr, valueStr);
         }
        return mapData; 
    } 
    
    /** 
     * ��json����ת����Map 
     * 
     * @param jsonObject json���� 
     * @return Map���� 
     */ 
//    public static Map<String, String> toMap(JSONObject jsonObject) 
//    { 
//        Map<String, String> result = new HashMap<String, String>(); 
//        Iterator<String> iterator = jsonObject.keys(); 
//        String key = null; 
//        String value = null; 
//        while (iterator.hasNext()) 
//        { 
//            key = iterator.next(); 
//            value = jsonObject.getString(key); 
//            result.put(key, value); 
//        } 
//        return result; 
//    } 

    /** 
     * ��javaBeanת����JSONObject 
     * 
     * @param bean javaBean 
     * @return json���� 
     */ 
//    public static JSONObject toJSON(Object bean) 
//    { 
//        return new JSONObject(toMap(bean)); 
//    } 

    /** 
     * ��mapת����Javabean 
     * @param javabean javaBean 
     * @param data map���� 
     */ 
    public static Object toJavaBean(Object javabean, Map<String, String> data) 
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
            	logger.error(field+"����Ϊ:"+field.getClass(),e);
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
                // ����class����
                if (!key.equals("class")) {
                    // �õ�property��Ӧ��getter����
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
     * ��javaBeanת����JSONObject 
     * 
     * @param bean javaBean 
     * @return json���� 
     * @throws ParseException json�����쳣 
     */ 
//    public static void toJavaBean(Object javabean, String data) throws ParseException 
//    { 
//        JSONObject jsonObject = new JSONObject(data); 
//        Map<String, String> datas = toMap(jsonObject); 
//        toJavaBean(javabean, datas); 
//    } 
}
