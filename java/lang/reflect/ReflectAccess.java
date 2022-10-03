package java.lang.reflect;

import sun.reflect.ConstructorAccessor;
import sun.reflect.MethodAccessor;
import sun.reflect.LangReflectAccess;

class ReflectAccess implements LangReflectAccess
{
    @Override
    public Field newField(final Class<?> clazz, final String s, final Class<?> clazz2, final int n, final int n2, final String s2, final byte[] array) {
        return new Field(clazz, s, clazz2, n, n2, s2, array);
    }
    
    @Override
    public Method newMethod(final Class<?> clazz, final String s, final Class<?>[] array, final Class<?> clazz2, final Class<?>[] array2, final int n, final int n2, final String s2, final byte[] array3, final byte[] array4, final byte[] array5) {
        return new Method(clazz, s, array, clazz2, array2, n, n2, s2, array3, array4, array5);
    }
    
    @Override
    public <T> Constructor<T> newConstructor(final Class<T> clazz, final Class<?>[] array, final Class<?>[] array2, final int n, final int n2, final String s, final byte[] array3, final byte[] array4) {
        return new Constructor<T>(clazz, array, array2, n, n2, s, array3, array4);
    }
    
    @Override
    public MethodAccessor getMethodAccessor(final Method method) {
        return method.getMethodAccessor();
    }
    
    @Override
    public void setMethodAccessor(final Method method, final MethodAccessor methodAccessor) {
        method.setMethodAccessor(methodAccessor);
    }
    
    @Override
    public ConstructorAccessor getConstructorAccessor(final Constructor<?> constructor) {
        return constructor.getConstructorAccessor();
    }
    
    @Override
    public void setConstructorAccessor(final Constructor<?> constructor, final ConstructorAccessor constructorAccessor) {
        constructor.setConstructorAccessor(constructorAccessor);
    }
    
    @Override
    public int getConstructorSlot(final Constructor<?> constructor) {
        return constructor.getSlot();
    }
    
    @Override
    public String getConstructorSignature(final Constructor<?> constructor) {
        return constructor.getSignature();
    }
    
    @Override
    public byte[] getConstructorAnnotations(final Constructor<?> constructor) {
        return constructor.getRawAnnotations();
    }
    
    @Override
    public byte[] getConstructorParameterAnnotations(final Constructor<?> constructor) {
        return constructor.getRawParameterAnnotations();
    }
    
    @Override
    public byte[] getExecutableTypeAnnotationBytes(final Executable executable) {
        return executable.getTypeAnnotationBytes();
    }
    
    @Override
    public Method copyMethod(final Method method) {
        return method.copy();
    }
    
    @Override
    public Field copyField(final Field field) {
        return field.copy();
    }
    
    @Override
    public <T> Constructor<T> copyConstructor(final Constructor<T> constructor) {
        return constructor.copy();
    }
}
