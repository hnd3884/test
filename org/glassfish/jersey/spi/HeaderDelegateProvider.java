package org.glassfish.jersey.spi;

import javax.ws.rs.ext.RuntimeDelegate;

@Contract
public interface HeaderDelegateProvider<T> extends RuntimeDelegate.HeaderDelegate<T>
{
    boolean supports(final Class<?> p0);
}
