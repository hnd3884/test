package sun.tracing.dtrace;

import java.lang.reflect.InvocationHandler;
import com.sun.tracing.dtrace.DependencyClass;
import com.sun.tracing.dtrace.Attributes;
import com.sun.tracing.dtrace.StabilityLevel;
import com.sun.tracing.dtrace.FunctionName;
import com.sun.tracing.ProbeName;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import com.sun.tracing.dtrace.ModuleName;
import sun.misc.ProxyGenerator;
import java.lang.reflect.Modifier;
import com.sun.tracing.Provider;
import sun.tracing.ProbeSkeleton;
import java.lang.reflect.Method;
import sun.tracing.ProviderSkeleton;

class DTraceProvider extends ProviderSkeleton
{
    private Activation activation;
    private Object proxy;
    private static final Class[] constructorParams;
    private final String proxyClassNamePrefix = "$DTraceTracingProxy";
    static final String DEFAULT_MODULE = "java_tracing";
    static final String DEFAULT_FUNCTION = "unspecified";
    private static long nextUniqueNumber;
    
    private static synchronized long getUniqueNumber() {
        return DTraceProvider.nextUniqueNumber++;
    }
    
    @Override
    protected ProbeSkeleton createProbe(final Method method) {
        return new DTraceProbe(this.proxy, method);
    }
    
    DTraceProvider(final Class<? extends Provider> clazz) {
        super(clazz);
    }
    
    void setProxy(final Object proxy) {
        this.proxy = proxy;
    }
    
    void setActivation(final Activation activation) {
        this.activation = activation;
    }
    
    @Override
    public void dispose() {
        if (this.activation != null) {
            this.activation.disposeProvider(this);
            this.activation = null;
        }
        super.dispose();
    }
    
    @Override
    public <T extends Provider> T newProxyInstance() {
        final long uniqueNumber = getUniqueNumber();
        String s = "";
        if (!Modifier.isPublic(this.providerType.getModifiers())) {
            final String name = this.providerType.getName();
            final int lastIndex = name.lastIndexOf(46);
            s = ((lastIndex == -1) ? "" : name.substring(0, lastIndex + 1));
        }
        final String string = s + "$DTraceTracingProxy" + uniqueNumber;
        final byte[] generateProxyClass = ProxyGenerator.generateProxyClass(string, new Class[] { this.providerType });
        Class<?> defineClass;
        try {
            defineClass = JVM.defineClass(this.providerType.getClassLoader(), string, generateProxyClass, 0, generateProxyClass.length);
        }
        catch (final ClassFormatError classFormatError) {
            throw new IllegalArgumentException(classFormatError.toString());
        }
        try {
            return (T)defineClass.getConstructor((Class<?>[])DTraceProvider.constructorParams).newInstance(this);
        }
        catch (final ReflectiveOperationException ex) {
            throw new InternalError(ex.toString(), ex);
        }
    }
    
    @Override
    protected void triggerProbe(final Method method, final Object[] array) {
        assert false : "This method should have been overridden by the JVM";
    }
    
    public String getProviderName() {
        return super.getProviderName();
    }
    
    String getModuleName() {
        return ProviderSkeleton.getAnnotationString(this.providerType, ModuleName.class, "java_tracing");
    }
    
    static String getProbeName(final Method method) {
        return ProviderSkeleton.getAnnotationString(method, ProbeName.class, method.getName());
    }
    
    static String getFunctionName(final Method method) {
        return ProviderSkeleton.getAnnotationString(method, FunctionName.class, "unspecified");
    }
    
    DTraceProbe[] getProbes() {
        return this.probes.values().toArray(new DTraceProbe[0]);
    }
    
    StabilityLevel getNameStabilityFor(final Class<? extends Annotation> clazz) {
        final Attributes attributes = (Attributes)ProviderSkeleton.getAnnotationValue(this.providerType, clazz, "value", null);
        if (attributes == null) {
            return StabilityLevel.PRIVATE;
        }
        return attributes.name();
    }
    
    StabilityLevel getDataStabilityFor(final Class<? extends Annotation> clazz) {
        final Attributes attributes = (Attributes)ProviderSkeleton.getAnnotationValue(this.providerType, clazz, "value", null);
        if (attributes == null) {
            return StabilityLevel.PRIVATE;
        }
        return attributes.data();
    }
    
    DependencyClass getDependencyClassFor(final Class<? extends Annotation> clazz) {
        final Attributes attributes = (Attributes)ProviderSkeleton.getAnnotationValue(this.providerType, clazz, "value", null);
        if (attributes == null) {
            return DependencyClass.UNKNOWN;
        }
        return attributes.dependency();
    }
    
    static {
        constructorParams = new Class[] { InvocationHandler.class };
        DTraceProvider.nextUniqueNumber = 0L;
    }
}
