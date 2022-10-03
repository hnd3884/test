package javax.validation.bootstrap;

import javax.validation.ValidationProviderResolver;
import javax.validation.Configuration;

public interface ProviderSpecificBootstrap<T extends Configuration<T>>
{
    ProviderSpecificBootstrap<T> providerResolver(final ValidationProviderResolver p0);
    
    T configure();
}
