package java.lang.invoke;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import java.util.Objects;
import java.io.Serializable;

public final class SerializedLambda implements Serializable
{
    private static final long serialVersionUID = 8025925345765570181L;
    private final Class<?> capturingClass;
    private final String functionalInterfaceClass;
    private final String functionalInterfaceMethodName;
    private final String functionalInterfaceMethodSignature;
    private final String implClass;
    private final String implMethodName;
    private final String implMethodSignature;
    private final int implMethodKind;
    private final String instantiatedMethodType;
    private final Object[] capturedArgs;
    
    public SerializedLambda(final Class<?> capturingClass, final String functionalInterfaceClass, final String functionalInterfaceMethodName, final String functionalInterfaceMethodSignature, final int implMethodKind, final String implClass, final String implMethodName, final String implMethodSignature, final String instantiatedMethodType, final Object[] array) {
        this.capturingClass = capturingClass;
        this.functionalInterfaceClass = functionalInterfaceClass;
        this.functionalInterfaceMethodName = functionalInterfaceMethodName;
        this.functionalInterfaceMethodSignature = functionalInterfaceMethodSignature;
        this.implMethodKind = implMethodKind;
        this.implClass = implClass;
        this.implMethodName = implMethodName;
        this.implMethodSignature = implMethodSignature;
        this.instantiatedMethodType = instantiatedMethodType;
        this.capturedArgs = Objects.requireNonNull(array).clone();
    }
    
    public String getCapturingClass() {
        return this.capturingClass.getName().replace('.', '/');
    }
    
    public String getFunctionalInterfaceClass() {
        return this.functionalInterfaceClass;
    }
    
    public String getFunctionalInterfaceMethodName() {
        return this.functionalInterfaceMethodName;
    }
    
    public String getFunctionalInterfaceMethodSignature() {
        return this.functionalInterfaceMethodSignature;
    }
    
    public String getImplClass() {
        return this.implClass;
    }
    
    public String getImplMethodName() {
        return this.implMethodName;
    }
    
    public String getImplMethodSignature() {
        return this.implMethodSignature;
    }
    
    public int getImplMethodKind() {
        return this.implMethodKind;
    }
    
    public final String getInstantiatedMethodType() {
        return this.instantiatedMethodType;
    }
    
    public int getCapturedArgCount() {
        return this.capturedArgs.length;
    }
    
    public Object getCapturedArg(final int n) {
        return this.capturedArgs[n];
    }
    
    private Object readResolve() throws ReflectiveOperationException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    final Method declaredMethod = SerializedLambda.this.capturingClass.getDeclaredMethod("$deserializeLambda$", SerializedLambda.class);
                    declaredMethod.setAccessible(true);
                    return declaredMethod;
                }
            }).invoke(null, this);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof ReflectiveOperationException) {
                throw (ReflectiveOperationException)exception;
            }
            if (exception instanceof RuntimeException) {
                throw (RuntimeException)exception;
            }
            throw new RuntimeException("Exception in SerializedLambda.readResolve", ex);
        }
    }
    
    @Override
    public String toString() {
        return String.format("SerializedLambda[%s=%s, %s=%s.%s:%s, %s=%s %s.%s:%s, %s=%s, %s=%d]", "capturingClass", this.capturingClass, "functionalInterfaceMethod", this.functionalInterfaceClass, this.functionalInterfaceMethodName, this.functionalInterfaceMethodSignature, "implementation", MethodHandleInfo.referenceKindToString(this.implMethodKind), this.implClass, this.implMethodName, this.implMethodSignature, "instantiatedMethodType", this.instantiatedMethodType, "numCaptured", this.capturedArgs.length);
    }
}
