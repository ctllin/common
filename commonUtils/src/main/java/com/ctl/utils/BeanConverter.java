package com.ctl.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ctl.utils.bean.Person;
import com.ctl.utils.encryp.des.DesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *ת����
 *1:��JavaBean ת����Map��JSONObject
 *2:��JSONObject ת����Map
 *
 * @author xxx
 */
public class BeanConverter
{
    static Logger logger = LoggerFactory.getLogger(BeanConverter.class);
    static{
        try {
            new DesUtils();
        } catch (Exception e) {
            logger.error("��ʼ��DesUtilsʧ�ܣ�",e);
        }
    }

    public static void main(String[] args) throws Exception {
        String str= DateUtil.sdf.format(new Date());
        System.out.println("����ǰ��"+str);
        System.out.println("���ܺ�"+ DesUtils.encrypt(str));
        System.out.println("���ܺ�"+DesUtils.decrypt(DesUtils.encrypt(str)));
        Person person=new Person();
        person.setId(1);
        person.setAge(27);
        person.setAddress("XX����");
        person.setFloatv(1.01f);
        person.setFloatVV(1.02f);
        person.setDoublev(2.01d);
        person.setDoubleVV(2.03d);
        person.setDatenow(new Date());
        Map<String,Object> map=new HashMap<String,Object>();
        transBean2Map(person,map);
        logger.info(map.toString());
        person=new Person();
        transMap2Bean(person,map);
        logger.info(person.toString());
    }
    /**
     * ��javaBeanת����Map(���ܺ�)
     *
     * @param javaBean javaBean
     * @return Map����
     */
    public static Map<String, String> toEncryptMap(Object javaBean)
    {
        Map<String, String> result = new HashMap<>();
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
                logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            }
        }
        return result;
    }
    /**
     * @desc  ��javaBeanת����Map(���ܺ�)
     * @param
     * @return
     */
    public static Map<String, String> toDecryptMap(Object javaBean)
    {
        Map<String, String> result = new HashMap<>();
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
                logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(),e);
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
                logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(),e);
            }
        }
        return javabean;
    }
}
