package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.util.InjectionPlan;
import javax.xml.ws.WebServiceContext;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.api.server.ResourceInjector;

public final class DefaultResourceInjector extends ResourceInjector
{
    @Override
    public void inject(@NotNull final WSWebServiceContext context, @NotNull final Object instance) {
        InjectionPlan.buildInjectionPlan(instance.getClass(), (Class<WSWebServiceContext>)WebServiceContext.class, false).inject(instance, context);
    }
}
