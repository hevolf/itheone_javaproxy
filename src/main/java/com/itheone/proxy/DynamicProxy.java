package com.itheone.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author caohaifengx@163.com 2019-12-17 21:55
 */
public class DynamicProxy implements InvocationHandler {

    private INormalObject normalObject;

    public DynamicProxy(INormalObject normalObject){
        this.normalObject = normalObject;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("DynamicProxy.invoke===before=====" + args.toString());
        Object result = method.invoke(normalObject, args);
        System.out.println("DynamicProxy.invoke===after=====" + args.toString());
        return result;
    }
}
