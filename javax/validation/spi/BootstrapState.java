package javax.validation.spi;

import javax.validation.ValidationProviderResolver;

public interface BootstrapState
{
    ValidationProviderResolver getValidationProviderResolver();
    
    ValidationProviderResolver getDefaultValidationProviderResolver();
}
