package org.aopalliance.intercept;

public interface MethodInterceptor extends Interceptor
{
    Object invoke(final MethodInvocation p0) throws Throwable;
}
