# 静态代理和动态代理

## 原始对象及接口

```java
public class NormalObject implements INormalObject{
    @Override
    public void operation(String desc) {
        System.out.println("NormalObject.operation()=============" + desc);
    }
}
```



## 静态代理

> 需要实现原始对象的接口

```java
// StaticProxy代理NormalObject
public class StaticProxy implements INormalObject{

    private INormalObject normalObject = new NormalObject();
    
    @Override
    public void operation(String desc) {
        System.out.println("StaticProxy.operation===before=====" + desc);
        normalObject.operation(desc);
        System.out.println("StaticProxy.operation===after=====" + desc);

    }
}
```



## 动态代理

> 需要实现InvocationHandler接口
>
> 基于JDK实现动态代理, 被代理的类**必须有要实现的接口 Class<?>[] interfaces**

```java
// DynamicProxy 动态代理NormalObject，需要实现InvocationHandler接口
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
```

## 验证

```java
public class Main {
    public static void main(String[] args) {
        // 生成静态代理对象
        INormalObject normalStaticProxy = new StaticProxy();
        normalStaticProxy.operation(" test static proxy");

        // 生成动态代理对象
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
```

参考：[Java动态代理机制详解（JDK 和CGLIB，Javassist，ASM）](https://blog.csdn.net/luanlouis/article/details/24589193)

## cglib 生成动态代理类的机制----通过类继承

> ​		JDK中提供的生成动态代理类的机制有个鲜明的特点是： 某个类必须有实现的接口，而生成的代理类也**只能代理**某个类**接口定义的方法**，比如：InterfaceA有MethodA和MethodB两个方法，ClassA 实现了接口InterfaceA，另外ClassA中新增了方法MethodC， ,则在产生的动态代理类中不会有MethodC这个方法了！更极端的情况是：如果某个类没有实现接口，那么这个类就不能通过JDK产生动态代理了

 cglib 创建某个类A的动态代理类的模式是： 

1.    查找A上的所有非final 的public类型的方法定义；
2.    将这些方法的定义转换成字节码；
3.    将组成的字节码转换成相应的代理的class对象；
4.    实现 MethodInterceptor接口，用来处理 对代理类上所有方法的请求（这个接口和JDK动态代理InvocationHandler的功能和角色是一样的）

```java
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

```

