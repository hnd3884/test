package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface InjectionResolver<T>
{
    public static final String SYSTEM_RESOLVER_NAME = "SystemInjectResolver";
    
    Object resolve(final Injectee p0, final ServiceHandle<?> p1);
    
    boolean isConstructorParameterIndicator();
    
    boolean isMethodParameterIndicator();
}
