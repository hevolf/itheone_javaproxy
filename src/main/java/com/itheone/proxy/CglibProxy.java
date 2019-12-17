package com.itheone.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author caohaifengx@163.com 2019-12-17 22:43
 */
public class CglibProxy<T> implements MethodInterceptor {
    private T target;

    // 生成代理对象
    public T proxy(T target){
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("CglibProxy.invokeSuper===before=====" );
        methodProxy.invokeSuper(o, objects);
        System.out.println("CglibProxy.invokeSuper===before=====");
        return null;
    }
}
