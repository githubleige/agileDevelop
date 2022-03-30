package JDKAop;

import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.*;


public class ProxyTest {
    public static void main(String[] args) {
        String save=System.getProperty("sun.misc.ProxyGenerator.saveGeneratedFiles");
        System.out.println(save);
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
        InvocationHandler handler=new MyInvokationHandler();
        Person p=(Person)Proxy.newProxyInstance(Person.class.getClassLoader(),new Class[]{Person.class},handler);
        p.walk();
//        Person p1=(Person)Proxy.newProxyInstance(Person.class.getClassLoader(),new Class[]{Person.class},handler);
//        p1.walk();
        System.out.println(p.getClass());
        System.out.println("-------->");
        Class clazz=AopProxyUtils.ultimateTargetClass(p);
        System.out.println(clazz);
//       for(Type aa : clazz.getGenericInterfaces()){
//           System.out.println(aa);
//        }
//
//        for(Class<?> aa : clazz.getInterfaces()){
//            System.out.println(aa);
//        }
//        System.out.println(p.getClass());
    }
}

interface Person{
    void walk();
    void sayHello(String name);
}

class MyInvokationHandler implements InvocationHandler{

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(proxy instanceof Person);
        //方法不可以被代理
        System.out.println(proxy.getClass());
//        System.out.println(Person.class);
//        System.out.println(proxy.toString());
//        System.out.println(proxy.equals(method));
//        System.out.println(method);
//        System.out.println(method.getDeclaringClass());
//        System.out.println(method.getReturnType());
//        System.out.println(args==null);
        return null;
    }
}