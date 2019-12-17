package com.itheone.proxy;

/**
 * @author caohaifengx@163.com 2019-12-17 21:48
 */
public class NormalObject implements INormalObject{
    @Override
    public void operation(String desc) {
        System.out.println("NormalObject.operation()=============" + desc);
    }

    public void method(String desc) {
        System.out.println("NormalObject.method()=============" + desc);
    }
}
