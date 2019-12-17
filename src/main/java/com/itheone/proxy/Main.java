package com.itheone.proxy;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author caohaifengx@163.com 2019-12-17 21:53
 */
public class Main {
    public static void main(String[] args) {
        INormalObject normalStaticProxy = new StaticProxy();
        normalStaticProxy.operation(" test static proxy");

        INormalObject normalDynamicProxy = (INormalObject) Proxy.newProxyInstance(
                INormalObject.class.getClassLoader(),
                new Class[]{INormalObject.class},
                new DynamicProxy(new NormalObject()));
        normalDynamicProxy.operation("test Dynamic proxy");

        // 动态获取所有信息
        // 待被代理对象
        INormalObject normalObject = new NormalObject();
        // 1. 获取对应的ClassLoader
        ClassLoader classLoader = normalObject.getClass().getClassLoader();
        // 2. 获取所实现的所有接口
        Class<?>[] interfaces = normalObject.getClass().getInterfaces();
        // 3. 设置代理对象的方法调用处理器
        InvocationHandler handler = new DynamicProxy(normalObject);
        // 4. 生成代理对象
        Object o = Proxy.newProxyInstance(classLoader, interfaces, handler);
        INormalObject dynamicProxy = (INormalObject) o;
        dynamicProxy.operation("test jdk Dynamic proxy");

        // cglib 中加强器，用来创建动态代理
        Enhancer enhancer = new Enhancer();
        // 设置要创建动态代理的类
        enhancer.setSuperclass(normalObject.getClass());// 注入被代理对象
        // 设置回调，这里相当于是对于代理类上所有方法的调用，都会调用CallBack，
        // 而Callback则需要实行intercept()方法进行拦截
        enhancer.setCallback(new CglibProxy());// 注入处理器
        INormalObject cglibProxyObject = (INormalObject) enhancer.create();
        cglibProxyObject.operation("test cglib Dynamic proxy");

        // cglib 简化
        CglibProxy cglibProxy = new CglibProxy<INormalObject>();
        INormalObject cglibProxyObject2 = (INormalObject)cglibProxy.proxy(normalObject);
        cglibProxyObject.operation("test cglib2 Dynamic proxy");
    }
}
