package javax.validation.spi;

import javax.validation.ValidatorFactory;
import javax.validation.Configuration;

public interface ValidationProvider<T extends Configuration<T>>
{
    T createSpecializedConfiguration(final BootstrapState p0);
    
    Configuration<?> createGenericConfiguration(final BootstrapState p0);
    
    ValidatorFactory buildValidatorFactory(final ConfigurationState p0);
}
