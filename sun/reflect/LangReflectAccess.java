package sun.reflect;

import java.lang.reflect.Executable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public interface LangReflectAccess
{
    Field newField(final Class<?> p0, final String p1, final Class<?> p2, final int p3, final int p4, final String p5, final byte[] p6);
    
    Method newMethod(final Class<?> p0, final String p1, final Class<?>[] p2, final Class<?> p3, final Class<?>[] p4, final int p5, final int p6, final String p7, final byte[] p8, final byte[] p9, final byte[] p10);
    
     <T> Constructor<T> newConstructor(final Class<T> p0, final Class<?>[] p1, final Class<?>[] p2, final int p3, final int p4, final String p5, final byte[] p6, final byte[] p7);
    
    MethodAccessor getMethodAccessor(final Method p0);
    
    void setMethodAccessor(final Method p0, final MethodAccessor p1);
    
    ConstructorAccessor getConstructorAccessor(final Constructor<?> p0);
    
    void setConstructorAccessor(final Constructor<?> p0, final ConstructorAccessor p1);
    
    byte[] getExecutableTypeAnnotationBytes(final Executable p0);
    
    int getConstructorSlot(final Constructor<?> p0);
    
    String getConstructorSignature(final Constructor<?> p0);
    
    byte[] getConstructorAnnotations(final Constructor<?> p0);
    
    byte[] getConstructorParameterAnnotations(final Constructor<?> p0);
    
    Method copyMethod(final Method p0);
    
    Field copyField(final Field p0);
    
     <T> Constructor<T> copyConstructor(final Constructor<T> p0);
}
