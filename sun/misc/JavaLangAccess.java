package sun.misc;

import java.security.AccessControlContext;
import sun.nio.ch.Interruptible;
import java.lang.reflect.Executable;
import java.lang.annotation.Annotation;
import java.util.Map;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.ConstantPool;

public interface JavaLangAccess
{
    ConstantPool getConstantPool(final Class<?> p0);
    
    boolean casAnnotationType(final Class<?> p0, final AnnotationType p1, final AnnotationType p2);
    
    AnnotationType getAnnotationType(final Class<?> p0);
    
    Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap(final Class<?> p0);
    
    byte[] getRawClassAnnotations(final Class<?> p0);
    
    byte[] getRawClassTypeAnnotations(final Class<?> p0);
    
    byte[] getRawExecutableTypeAnnotations(final Executable p0);
    
     <E extends Enum<E>> E[] getEnumConstantsShared(final Class<E> p0);
    
    void blockedOn(final Thread p0, final Interruptible p1);
    
    void registerShutdownHook(final int p0, final boolean p1, final Runnable p2);
    
    int getStackTraceDepth(final Throwable p0);
    
    StackTraceElement getStackTraceElement(final Throwable p0, final int p1);
    
    String newStringUnsafe(final char[] p0);
    
    Thread newThreadWithAcc(final Runnable p0, final AccessControlContext p1);
    
    void invokeFinalize(final Object p0) throws Throwable;
}
