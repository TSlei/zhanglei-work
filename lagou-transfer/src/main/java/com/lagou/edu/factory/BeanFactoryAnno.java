package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.utils.ScanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bean工厂解析xml和注解，通过注解的方式创建对象，注入属性
 */
public class BeanFactoryAnno {

    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */
    private static Map<String,Object> map = new HashMap<>();  // 存储对象

    /**
     * 存储添加了事务的方法和处理事务的类
     */
    private static Map<String,Object> transactionMap = new HashMap<>();

    static {
        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
        // 加载xml
        // 扫描所有类，获取类上的自定义注解，通过反射技术实例化对象并且存储待用（map集合）
        InputStream resourceAsStream = BeanFactoryAnno.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> beanList = rootElement.selectNodes("//component-scan");
            for (int i = 0; i < beanList.size(); i++) {
                Element element =  beanList.get(i);
                // 处理base-package元素，获取到该元素的base-package属性
                String pack = element.attributeValue("base-package");  // com.lagou.edu
                // 扫描工具类扫描指定包下的所有类
                Set<Class<?>> classes = ScanUtils.getClasses(pack);

                for (Class<?> aClass : classes) {
                    // 如果有注解则实例化对象
                    Service service = aClass.getAnnotation(Service.class);
                    if(service != null){
                        // 破坏单例模式，使对象可以被实例化
                        Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
                        declaredConstructor.setAccessible(true);
                        String value = service.value();
                        Object o = declaredConstructor.newInstance();
                        // 注解默认value值作为key，否则截取类名作为key
                        if(value.equals("proxyFactory")){
                            System.out.println("proxyFactory");
                        }
                        if(!"".equals(value)){
                            // 存储到map中待用
                            map.put(value.toLowerCase(),o);
                        }else {
                            String className = aClass.getName();
                            int startIndex = className.lastIndexOf(".");
                            // 截取类名
                            className = className.substring(startIndex+1, className.length());
                            // 存储到map中待用
                            map.put(className.toLowerCase(),o);
                        }
                    }
                }
            }

            // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
            // 有property子元素的bean就有传值需求
            for (String parent : map.keySet()) {
                Field[] fields = map.get(parent.toLowerCase()).getClass().getDeclaredFields();
                Method[] methods = map.get(parent.toLowerCase()).getClass().getDeclaredMethods();
                // 从map中获取父类对象
                Object parentObject = map.get(parent.toLowerCase());
                for (Field field : fields) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if(autowired != null){
                        field.setAccessible(true);
                        String fieldTypeName = field.getName();
                        // 如果字段上有autowired注解则给该对象的字段赋值
                        field.set(parentObject, map.get(fieldTypeName.toLowerCase()));
                    }
                }
                for (Method method : methods) {
                    Transactional annotation = method.getAnnotation(Transactional.class);
                    if (annotation != null){
                        transactionMap.put(method.getName().toLowerCase(), map.get(annotation.value().toLowerCase()));
                    }
                }
                // 把处理之后的parentObject重新放到map中
                map.put(parent.toLowerCase(), parentObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static  Object getBean(String id) {
        return map.get(id.toLowerCase());
    }

    public static Object getTransactionBean(String id) {
        return transactionMap.get(id.toLowerCase());
    }
}
