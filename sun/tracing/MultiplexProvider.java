package sun.tracing;

import java.util.Iterator;
import java.lang.reflect.Method;
import com.sun.tracing.Provider;
import java.util.Set;

class MultiplexProvider extends ProviderSkeleton
{
    private Set<Provider> providers;
    
    @Override
    protected ProbeSkeleton createProbe(final Method method) {
        return new MultiplexProbe(method, this.providers);
    }
    
    MultiplexProvider(final Class<? extends Provider> clazz, final Set<Provider> providers) {
        super(clazz);
        this.providers = providers;
    }
    
    @Override
    public void dispose() {
        final Iterator<Provider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            iterator.next().dispose();
        }
        super.dispose();
    }
}
