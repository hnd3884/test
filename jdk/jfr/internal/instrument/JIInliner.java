package jdk.jfr.internal.instrument;

import java.util.Iterator;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import java.lang.reflect.Modifier;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import java.lang.reflect.Method;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.ClassVisitor;

@Deprecated
final class JIInliner extends ClassVisitor
{
    private final String targetClassName;
    private final String instrumentationClassName;
    private final ClassNode targetClassNode;
    private final List<Method> instrumentationMethods;
    
    JIInliner(final int n, final ClassVisitor classVisitor, final String targetClassName, final String instrumentationClassName, final ClassReader classReader, final List<Method> instrumentationMethods) {
        super(n, classVisitor);
        this.targetClassName = targetClassName;
        this.instrumentationClassName = instrumentationClassName;
        this.instrumentationMethods = instrumentationMethods;
        final ClassNode targetClassNode = new ClassNode(327680);
        classReader.accept(targetClassNode, 8);
        this.targetClassNode = targetClassNode;
    }
    
    @Override
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        final MethodVisitor visitMethod = super.visitMethod(n, s, s2, s3, array);
        if (!this.isInstrumentationMethod(s, s2)) {
            return visitMethod;
        }
        final MethodNode targetMethodNode = this.findTargetMethodNode(s, s2);
        if (targetMethodNode == null) {
            throw new IllegalArgumentException("Could not find the method to instrument in the target class");
        }
        if (Modifier.isNative(targetMethodNode.access)) {
            throw new IllegalArgumentException("Cannot instrument native methods: " + this.targetClassNode.name + "." + targetMethodNode.name + targetMethodNode.desc);
        }
        Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.DEBUG, "Inliner processing method " + s + s2);
        return new JIMethodCallInliner(n, s2, visitMethod, targetMethodNode, this.targetClassName, this.instrumentationClassName);
    }
    
    private boolean isInstrumentationMethod(final String s, final String s2) {
        for (final Method method : this.instrumentationMethods) {
            if (method.getName().equals(s) && Type.getMethodDescriptor(method).equals(s2)) {
                return true;
            }
        }
        return false;
    }
    
    private MethodNode findTargetMethodNode(final String s, final String s2) {
        for (final MethodNode methodNode : this.targetClassNode.methods) {
            if (methodNode.desc.equals(s2) && methodNode.name.equals(s)) {
                return methodNode;
            }
        }
        throw new IllegalArgumentException("could not find MethodNode for " + s + s2);
    }
}
