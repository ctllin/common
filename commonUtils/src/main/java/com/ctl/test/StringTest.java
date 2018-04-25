package com.ctl.test;

import com.ctl.test.mapper.PersonMapper;
import com.ctl.test.po.PersonExample;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: StringTest</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: www.ctl.com</p>
 *
 * @author ctl
 * @version 1.0
 * @date 2018-04-25 09:15
 */
public class StringTest {
    public static void main(String[] args) {
        ApplicationContext context=  new ClassPathXmlApplicationContext("spring/applicationContext.xml");//名字只可以是applicationContext.xml
        System.out.println(context);
        String[] beanNames = context.getBeanDefinitionNames();
        int allBeansCount = context.getBeanDefinitionCount();
        System.out.println("所有beans的数量是：" + allBeansCount);
        for (String beanName : beanNames) {
            Class<?> beanType = context.getType(beanName);
            Package beanPackage = beanType.getPackage();
            Object bean = context.getBean(beanName);
            System.out.println("BeanName:" + beanName);
            System.out.println("Bean的类型：" + beanType);
            System.out.println("Bean所在的包：" + beanPackage);
            System.out.println("\r\n");
        }

        PersonMapper mapper= (PersonMapper)context.getBean("personMapper");
        Map<String,Object> dataMap=new HashMap<>();
        PersonExample example=new PersonExample();
        System.out.println( mapper.countByExample(example));
        System.out.println(mapper.selectByParams(dataMap,new PageBounds(2,10)));
    }
}
