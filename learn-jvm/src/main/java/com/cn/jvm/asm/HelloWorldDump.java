package com.cn.jvm.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @Description: HelloWorldDump
 * @Author: 一方通行
 * @Date: 2024-02-11
 * @Version:v1.0
 */
public class HelloWorldDump implements Opcodes {

    public static byte[] dumpHello() {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V11, ACC_PUBLIC | ACC_SUPER, "com/cn/jvm/asm/HelloWorld", null, "java/lang/Object", null);
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        MethodVisitor m2 = classWriter.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
        m2.visitCode();
        m2.visitLdcInsn("this is hello word");
        m2.visitInsn(ARETURN);
        m2.visitMaxs(1, 1);
        m2.visitEnd();
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
