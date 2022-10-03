package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import com.sun.tracing.ProbeName;
import java.lang.reflect.Method;
import java.io.PrintStream;

class PrintStreamProvider extends ProviderSkeleton
{
    private PrintStream stream;
    private String providerName;
    
    @Override
    protected ProbeSkeleton createProbe(final Method method) {
        return new PrintStreamProbe(this, ProviderSkeleton.getAnnotationString(method, ProbeName.class, method.getName()), method.getParameterTypes());
    }
    
    PrintStreamProvider(final Class<? extends Provider> clazz, final PrintStream stream) {
        super(clazz);
        this.stream = stream;
        this.providerName = this.getProviderName();
    }
    
    PrintStream getStream() {
        return this.stream;
    }
    
    String getName() {
        return this.providerName;
    }
}
