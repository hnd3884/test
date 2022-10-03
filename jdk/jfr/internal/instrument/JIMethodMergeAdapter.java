package jdk.jfr.internal.instrument;

import jdk.internal.org.objectweb.asm.Type;
import java.util.Iterator;
import jdk.internal.org.objectweb.asm.commons.Remapper;
import jdk.internal.org.objectweb.asm.commons.RemappingMethodAdapter;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.ClassVisitor;

@Deprecated
final class JIMethodMergeAdapter extends ClassVisitor
{
    private final ClassNode cn;
    private final List<Method> methodFilter;
    private final Map<String, String> typeMap;
    
    public JIMethodMergeAdapter(final ClassVisitor classVisitor, final ClassNode cn, final List<Method> methodFilter, final JITypeMapping[] array) {
        super(327680, classVisitor);
        this.cn = cn;
        this.methodFilter = methodFilter;
        this.typeMap = new HashMap<String, String>();
        for (final JITypeMapping jiTypeMapping : array) {
            this.typeMap.put(jiTypeMapping.from().replace('.', '/'), jiTypeMapping.to().replace('.', '/'));
        }
    }
    
    @Override
    public void visit(final int n, final int n2, final String s, final String s2, final String s3, final String[] array) {
        super.visit(n, n2, s, s2, s3, array);
        this.typeMap.put(this.cn.name, s);
    }
    
    @Override
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        if (this.methodInFilter(s, s2)) {
            Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.DEBUG, "Deleting " + s + s2);
            return null;
        }
        return super.visitMethod(n, s, s2, s3, array);
    }
    
    @Override
    public void visitEnd() {
        final SimpleRemapper simpleRemapper = new SimpleRemapper(this.typeMap);
        for (final MethodNode methodNode : this.cn.methods) {
            if (this.methodInFilter(methodNode.name, methodNode.desc)) {
                Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.DEBUG, "Copying method: " + methodNode.name + methodNode.desc);
                Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.DEBUG, "   with mapper: " + this.typeMap);
                final String[] array = new String[methodNode.exceptions.size()];
                methodNode.exceptions.toArray(array);
                final MethodVisitor visitMethod = this.cv.visitMethod(methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, array);
                methodNode.instructions.resetLabels();
                methodNode.accept(new RemappingMethodAdapter(methodNode.access, methodNode.desc, visitMethod, simpleRemapper));
            }
        }
        super.visitEnd();
    }
    
    private boolean methodInFilter(final String s, final String s2) {
        for (final Method method : this.methodFilter) {
            if (method.getName().equals(s) && Type.getMethodDescriptor(method).equals(s2)) {
                return true;
            }
        }
        return false;
    }
}
