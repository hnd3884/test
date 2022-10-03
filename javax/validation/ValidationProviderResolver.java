package javax.validation;

import javax.validation.spi.ValidationProvider;
import java.util.List;

public interface ValidationProviderResolver
{
    List<ValidationProvider<?>> getValidationProviders();
}
