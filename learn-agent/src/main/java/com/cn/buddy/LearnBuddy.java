package com.cn.buddy;

import com.cn.buddy.test.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

/**
 * @Description: LearnBuddy
 * @Author: 一方通行
 * @Date: 2021-08-12
 * @Version:v1.0
 */
public class LearnBuddy {

    public static void main(String[] args) throws Exception {
        //oneDemo();
        /*  demoTwo();*/

        //demoThree();
        /* loadClass();*/
        /* reLoadClassMethod();*/
        /* reLoadMethod();*/
        /*   reLoadClassMethod();*/
        /* reLoadClassMethod();*/
     /*   testMethodDelegation();
        testSuperMoo();*/
        testCreateFieldGetAnsSetValue();
    }


    public static void oneDemo() {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .make();
        System.out.println();
    }

    public static void demoTwo() throws IOException {
        Map<TypeDescription, File> typeDescriptionFileMap = new ByteBuddy()
                .subclass(Object.class)
                .name("com.cn.buddy.HelloWorld")
                .make()
                .saveIn(new File("/Users/chenzhuo/Desktop/learnCode/"));
        Set<Map.Entry<TypeDescription, File>> entries = typeDescriptionFileMap.entrySet();
      /*  //放入当前线程的类加载器
        DynamicType.Loaded<?> load = dynamicType.load(Thread.currentThread().getContextClassLoader());
        //获取加载的类
        Class<?> loaded = load.getLoaded();
        System.out.println("load class name" + loaded.getName());*/

    }

    public static void demoThree() throws IOException {
        ByteBuddy bytebuddy = new ByteBuddy();
        bytebuddy.with(new AuxiliaryType.NamingStrategy.SuffixingRandom("suffix"));
        Map<TypeDescription, File> typeDescriptionFileMap = bytebuddy.subclass(Object.class).make()
                .saveIn(new File("/Users/chenzhuo/Desktop/learnCode/"));
    }


    public static void loadClass() {
        Class<?> load = new ByteBuddy()
                .subclass(Object.class)
                .name("com.cn.buddy.HelloWorld")
                .make()
                .load(LearnBuddy.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println("loaded :" + load.getName());
        Constructor<?> enclosingConstructor = load.getEnclosingConstructor();
        System.out.println("enclosingConstructor :" + enclosingConstructor.getName());
    }

    /**
     * 重新加载类
     */
    public static void reLoadClass() throws IOException {
        new ByteBuddy().rebase(Foo.class)
                .make()
                .saveIn(new File("/Users/chenzhuo/Desktop/learnCode/"))

        ;
       /* Method[] methods = load.getMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }*/
    }

    /**
     * 重新加载已存在的类
     *
     * @throws Exception
     */
    public static void reLoadClassMethod() throws Exception {
        new ByteBuddy()
                .subclass(Foo.class)
                /*.method(ElementMatchers.named("bar"))*/
                // 这里是重新定义了一个bar 的方法 然后指定了可兼职和返回值
                .defineMethod("bar", String.class)
                .intercept(FixedValue.value("hello word"))
                .make()
                .saveIn(new File("/Users/chenzhuo/Desktop/learnCode/Foo"));
               /* .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded().newInstance()*/
        ;
      /*  Foo foo = loaded.newInstance();
        System.out.println(foo.bar() + "");*/
      /*  Method method = loaded.getMethod("bar");
        System.out.println(method.invoke(loaded));*/

        /*        System.out.println(foo.bar());*/

    }

    public static void reLoadMethod() throws Exception {
        Class<?> loaded = new ByteBuddy().subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("chen zhuo test"))
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded();
        Object o = loaded.newInstance();
        System.out.println(o.toString());
    }

    public static void testMethodDelegation() throws Exception {
        Moo moo = new ByteBuddy()
                .subclass(Moo.class)
                .method(ElementMatchers.named("Moo").or(ElementMatchers.named("Moo1")))
                .intercept(MethodDelegation.to(DelegateMoo.class))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded()
                .newInstance();
        System.out.println("moo:" + moo.Moo("哈哈哈测试", 26));
        System.out.println("moo1:" + moo.Moo1("哈哈哈哈 不知道啊"));
    }

    public static void testSuperMoo() throws Exception {
        Moo moo = new ByteBuddy()
                .subclass(Moo.class)
                .method(ElementMatchers.named("Moo").or(ElementMatchers.named("Moo1")))
                .intercept(MethodDelegation.to(DelegateMooWithSuper.class))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded()
                .newInstance();

        System.out.println("moo1:" + moo.Moo1("冲冲冲"));
    }


    /**
     * 创建对象属性
     *
     * @throws Exception
     */
    public static void testCreateField() throws Exception {
        Moo moo = new ByteBuddy()
                .subclass(Moo.class)
                .defineField("name", String.class)
                .value("哈哈哈")
                .defineField("age", int.class)
                .value(26)
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded().newInstance();
        System.out.println("name" + moo.getClass().getDeclaredField("name"));
        System.out.println("age" + moo.getClass().getDeclaredField("age"));

      /*
       new ByteBuddy()
                .subclass(Moo.class)
                .defineField("name", String.class)
                .value("哈哈哈")
                .defineField("age", int.class)
                .value(26)
                .make()
                .saveIn(new File("/Users/chenzhuo/Desktop/learnCode/Moo"));*/
    }

    /**
     * 设置 get set属性
     */
    public static void testCreateFieldGetAnsSet() throws Exception {
        new ByteBuddy()
                .subclass(Moo.class)
                .defineField("name", String.class)
                // 这里代理接口
                .implement(NameInterceptor.class)
                .intercept(FieldAccessor.ofBeanProperty())
                .make()
                .saveIn(new File("/Users/chenzhuo/Desktop/learnCode/"));
    }


    public static void testCreateFieldGetAnsSetValue() throws Exception {
        Moo moo = new ByteBuddy()
                .subclass(Moo.class)
                .defineField("name", String.class, Modifier.PUBLIC)
                .implement(NameInterceptor.class).intercept(FieldAccessor.ofBeanProperty())
                .method(ElementMatchers.named("Moo1"))
                .intercept(MethodDelegation.to(DelegateWithField.class))
                .make().load(ClassLoader.getSystemClassLoader())
                .getLoaded().newInstance();
        moo.getClass().getMethod("setName", String.class).invoke(moo, "皮皮陈测试");
        System.out.println(moo.getClass().getMethod("Moo1", String.class).invoke(moo, "param1"));
    }

}
