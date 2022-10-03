package sun.tracing;

import java.lang.reflect.Method;
import com.sun.tracing.Provider;

class NullProvider extends ProviderSkeleton
{
    NullProvider(final Class<? extends Provider> clazz) {
        super(clazz);
    }
    
    @Override
    protected ProbeSkeleton createProbe(final Method method) {
        return new NullProbe(method.getParameterTypes());
    }
}
