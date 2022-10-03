package sun.tracing;

import java.util.Iterator;
import java.util.HashSet;
import com.sun.tracing.Provider;
import java.util.Set;
import com.sun.tracing.ProviderFactory;

public class MultiplexProviderFactory extends ProviderFactory
{
    private Set<ProviderFactory> factories;
    
    public MultiplexProviderFactory(final Set<ProviderFactory> factories) {
        this.factories = factories;
    }
    
    @Override
    public <T extends Provider> T createProvider(final Class<T> clazz) {
        final HashSet set = new HashSet();
        final Iterator<ProviderFactory> iterator = this.factories.iterator();
        while (iterator.hasNext()) {
            set.add(iterator.next().createProvider(clazz));
        }
        final MultiplexProvider multiplexProvider = new MultiplexProvider(clazz, set);
        multiplexProvider.init();
        return (T)multiplexProvider.newProxyInstance();
    }
}
