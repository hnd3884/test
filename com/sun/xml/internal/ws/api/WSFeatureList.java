package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceFeature;

public interface WSFeatureList extends Iterable<WebServiceFeature>
{
    boolean isEnabled(@NotNull final Class<? extends WebServiceFeature> p0);
    
    @Nullable
     <F extends WebServiceFeature> F get(@NotNull final Class<F> p0);
    
    @NotNull
    WebServiceFeature[] toArray();
    
    void mergeFeatures(@NotNull final WebServiceFeature[] p0, final boolean p1);
    
    void mergeFeatures(@NotNull final Iterable<WebServiceFeature> p0, final boolean p1);
}
