package org.glassfish.jersey.server.spi;

import javax.validation.ValidationException;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ValidationInterceptor
{
    void onValidate(final ValidationInterceptorContext p0) throws ValidationException;
}
