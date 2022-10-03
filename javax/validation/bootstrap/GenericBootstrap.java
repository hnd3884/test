package javax.validation.bootstrap;

import javax.validation.Configuration;
import javax.validation.ValidationProviderResolver;

public interface GenericBootstrap
{
    GenericBootstrap providerResolver(final ValidationProviderResolver p0);
    
    Configuration<?> configure();
}
