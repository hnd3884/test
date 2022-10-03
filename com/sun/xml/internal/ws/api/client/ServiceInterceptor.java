package com.sun.xml.internal.ws.api.client;

import java.util.Collection;
import java.util.ArrayList;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import java.util.Collections;
import javax.xml.ws.WebServiceFeature;
import java.util.List;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

public abstract class ServiceInterceptor
{
    public List<WebServiceFeature> preCreateBinding(@NotNull final WSPortInfo port, @Nullable final Class<?> serviceEndpointInterface, @NotNull final WSFeatureList defaultFeatures) {
        return Collections.emptyList();
    }
    
    public void postCreateProxy(@NotNull final WSBindingProvider bp, @NotNull final Class<?> serviceEndpointInterface) {
    }
    
    public void postCreateDispatch(@NotNull final WSBindingProvider bp) {
    }
    
    public static ServiceInterceptor aggregate(final ServiceInterceptor... interceptors) {
        if (interceptors.length == 1) {
            return interceptors[0];
        }
        return new ServiceInterceptor() {
            @Override
            public List<WebServiceFeature> preCreateBinding(@NotNull final WSPortInfo port, @Nullable final Class<?> portInterface, @NotNull final WSFeatureList defaultFeatures) {
                final List<WebServiceFeature> r = new ArrayList<WebServiceFeature>();
                for (final ServiceInterceptor si : interceptors) {
                    r.addAll(si.preCreateBinding(port, portInterface, defaultFeatures));
                }
                return r;
            }
            
            @Override
            public void postCreateProxy(@NotNull final WSBindingProvider bp, @NotNull final Class<?> serviceEndpointInterface) {
                for (final ServiceInterceptor si : interceptors) {
                    si.postCreateProxy(bp, serviceEndpointInterface);
                }
            }
            
            @Override
            public void postCreateDispatch(@NotNull final WSBindingProvider bp) {
                for (final ServiceInterceptor si : interceptors) {
                    si.postCreateDispatch(bp);
                }
            }
        };
    }
}
