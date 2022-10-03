package org.glassfish.jersey.server.internal.inject;

import javax.validation.ConstraintViolationException;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.spi.Contract;
import javax.validation.Validator;

@Contract
public interface ConfiguredValidator extends Validator
{
    void validateResourceAndInputParams(final Object p0, final Invocable p1, final Object[] p2) throws ConstraintViolationException;
    
    void validateResult(final Object p0, final Invocable p1, final Object p2) throws ConstraintViolationException;
}
