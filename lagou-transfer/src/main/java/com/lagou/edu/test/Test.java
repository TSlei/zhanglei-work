package com.lagou.edu.test;

import com.lagou.edu.factory.BeanFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
        // 加载xml
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析xml
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(resourceAsStream);
        Element rootElement = document.getRootElement();
//        List<Element> beanList = rootElement.selectNodes("//bean");
//        for (int i = 0; i < beanList.size(); i++) {
//            Element element = beanList.get(i);
//            // 处理每个bean元素，获取到该元素的id 和 class 属性
//            String id = element.attributeValue("id");        // accountDao
//            String clazz = element.attributeValue("class");  // com.lagou.edu.dao.impl.JdbcAccountDaoImpl
//            // 通过反射技术实例化对象
//            System.out.println("id = " + id + " | " + "class = " + clazz);
//        }


        System.out.println("-----------");
        // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
        // 有property子元素的bean就有传值需求
        List<Element> propertyList = rootElement.selectNodes("//property");
        // 解析property，获取父元素
        for (int i = 0; i < propertyList.size(); i++) {
            Element element = propertyList.get(i);   //<property name="AccountDao" ref="accountDao"></property>
            String name = element.attributeValue("name");
            String ref = element.attributeValue("ref");

            System.out.println("name = " + name + " | " + "class = " + ref);
            // 找到当前需要被处理依赖关系的bean
            Element parent = element.getParent();

            // 调用父元素对象的反射功能
            String parentId = parent.attributeValue("id");
            System.out.println(parentId);
        }
    }
}
