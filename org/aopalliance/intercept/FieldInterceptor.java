package org.aopalliance.intercept;

public interface FieldInterceptor extends Interceptor
{
    Object get(final FieldAccess p0) throws Throwable;
    
    Object set(final FieldAccess p0) throws Throwable;
}
