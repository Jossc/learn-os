package com.cn.agent;

import com.cn.agent.interceptor.Interceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

/**
 * @Description: TestAgent
 * @Author: 一方通行
 * @Date: 2021-08-07
 * @Version:v1.0
 */
public class TestAgent {
    public static void premain(String agentArgs, Instrumentation instrument) {
        //定义了一个transformer类型，通过访问者模式切入start方法
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, module)
                -> builder.visit(Advice.to(Interceptor.class).on(ElementMatchers.named("start")));
        new AgentBuilder.Default()
                .disableClassFormatChanges()
                .ignore(ElementMatchers.noneOf(Thread.class))
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .type(ElementMatchers.is(Thread.class))
                .transform(transformer)
                .installOn(instrument);

    }
    /*public static void premain(String agentArgs, Instrumentation instrument) {
        System.out.println("this is a java agent with ");
        System.out.println("参数: " + agentArgs + "\n");
        // instrument.addTransformer(new DefineTransformer());

        AgentBuilder.Transformer transformer =
                (builder, typeDescription, classLoader, module) -> {
                    // method()指定哪些方法需要被拦截，ElementMatchers.any()表
                    // 示拦截所有方法
                    return builder.method(
                                    ElementMatchers.<MethodDescription>any())
                            // intercept()指明拦截上述方法的拦截器
                            .intercept(MethodDelegation.to(TimeInterceptor.class));
                };
        // Byte Buddy专门有个AgentBuilder来处理Java Agent的场景
        new AgentBuilder
                .Default()
                // 根据包名前缀拦截类 所有com的都处理
                .type(ElementMatchers.nameStartsWith("com."))
                // 拦截到的类由transformer处理
                .transform(transformer)
                // 安装到 Instrumentation
                .installOn(instrument);


    }
*/

/*    public static void premain(String agentArgs) {
        System.out.println("this is a java agent with only one args");
        System.out.println("参数: " + agentArgs + "\n");
    }*/


}
