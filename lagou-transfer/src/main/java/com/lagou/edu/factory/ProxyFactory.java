package com.lagou.edu.factory;

import com.lagou.edu.annotation.Service;
import com.lagou.edu.transaction.ITransaction;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 应癫
 *
 *
 * 代理对象工厂：生成代理对象的
 */
@Service("proxyFactory")
public class ProxyFactory {

    /**
     * Jdk动态代理
     * @param obj  委托对象
     * @return   代理对象
     */
    public Object getJdkProxy(Object obj) {

        // 获取代理对象
        return  Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        ITransaction transaction = null;
                        try{
                            // 开启事务(关闭事务的自动提交)
                            transaction = (ITransaction) BeanFactoryAnno.getTransactionBean(method.getName());
                            if(transaction != null){
                                transaction.beginTransaction();
                            }
                            result = method.invoke(obj,args);

                            // 提交事务
                            if(transaction != null){
                                transaction.commit();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            // 回滚事务
                            if(transaction != null){
                                transaction.rollback();
                            }
                            // 抛出异常便于上层servlet捕获
                            throw e;
                        }
                        return result;
                    }
                });

    }


    /**
     * 使用cglib动态代理生成代理对象
     * @param obj 委托对象
     * @return
     */
    public Object getCglibProxy(Object obj) {
        return  Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                ITransaction transaction = null;
                try{
                    // 开启事务(关闭事务的自动提交)
                    transaction = (ITransaction)BeanFactoryAnno.getTransactionBean(method.getName());
                    if(transaction != null){
                        transaction.beginTransaction();
                    }
                    result = method.invoke(obj,objects);
                    // 提交事务
                    if(transaction != null){
                        transaction.commit();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    if(transaction != null){
                        transaction.rollback();
                    }
                    // 抛出异常便于上层servlet捕获
                    throw e;

                }
                return result;
            }
        });
    }

    /**
     * 是否实现接口，选择jdk还是cglib动态代理
     * @param obj
     * @return
     */
    public Object getProxy(Object obj){
        Class<?>[] classes = obj.getClass().getInterfaces();
        if(classes == null || classes.length == 0){
            return getCglibProxy(obj);
        }else{
            return getJdkProxy(obj);
        }
    }

}
