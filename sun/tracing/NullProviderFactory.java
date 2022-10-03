package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;

public class NullProviderFactory extends ProviderFactory
{
    @Override
    public <T extends Provider> T createProvider(final Class<T> clazz) {
        final NullProvider nullProvider = new NullProvider(clazz);
        nullProvider.init();
        return (T)nullProvider.newProxyInstance();
    }
}
