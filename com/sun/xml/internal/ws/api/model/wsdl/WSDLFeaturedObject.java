package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceFeature;
import com.sun.istack.internal.NotNull;

public interface WSDLFeaturedObject extends WSDLObject
{
    @Nullable
     <F extends WebServiceFeature> F getFeature(@NotNull final Class<F> p0);
    
    @NotNull
    WSFeatureList getFeatures();
    
    void addFeature(@NotNull final WebServiceFeature p0);
}
