package java.beans;

import sun.reflect.misc.ReflectUtil;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.ref.SoftReference;

final class MethodRef
{
    private String signature;
    private SoftReference<Method> methodRef;
    private WeakReference<Class<?>> typeRef;
    
    void set(final Method method) {
        if (method == null) {
            this.signature = null;
            this.methodRef = null;
            this.typeRef = null;
        }
        else {
            this.signature = method.toGenericString();
            this.methodRef = new SoftReference<Method>(method);
            this.typeRef = new WeakReference<Class<?>>(method.getDeclaringClass());
        }
    }
    
    boolean isSet() {
        return this.methodRef != null;
    }
    
    Method get() {
        if (this.methodRef == null) {
            return null;
        }
        Method find = this.methodRef.get();
        if (find == null) {
            find = find(this.typeRef.get(), this.signature);
            if (find == null) {
                this.signature = null;
                this.methodRef = null;
                this.typeRef = null;
                return null;
            }
            this.methodRef = new SoftReference<Method>(find);
        }
        return ReflectUtil.isPackageAccessible(find.getDeclaringClass()) ? find : null;
    }
    
    private static Method find(final Class<?> clazz, final String s) {
        if (clazz != null) {
            for (final Method method : clazz.getMethods()) {
                if (clazz.equals(method.getDeclaringClass()) && method.toGenericString().equals(s)) {
                    return method;
                }
            }
        }
        return null;
    }
}
