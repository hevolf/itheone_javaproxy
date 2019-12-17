package com.itheone.proxy;

/**
 * @author caohaifengx@163.com 2019-12-17 21:51
 */
public class StaticProxy implements INormalObject{

    private INormalObject normalObject = new NormalObject();

    @Override
    public void operation(String desc) {
        System.out.println("StaticProxy.operation===before=====" + desc);
        normalObject.operation(desc);
        System.out.println("StaticProxy.operation===after=====" + desc);

    }
}
