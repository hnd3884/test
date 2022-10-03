package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceFeature;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;

abstract class AbstractFeaturedObjectImpl extends AbstractExtensibleImpl implements WSDLFeaturedObject
{
    protected WebServiceFeatureList features;
    
    protected AbstractFeaturedObjectImpl(final XMLStreamReader xsr) {
        super(xsr);
    }
    
    protected AbstractFeaturedObjectImpl(final String systemId, final int lineNumber) {
        super(systemId, lineNumber);
    }
    
    @Override
    public final void addFeature(final WebServiceFeature feature) {
        if (this.features == null) {
            this.features = new WebServiceFeatureList();
        }
        this.features.add(feature);
    }
    
    @NotNull
    @Override
    public WebServiceFeatureList getFeatures() {
        if (this.features == null) {
            return new WebServiceFeatureList();
        }
        return this.features;
    }
    
    public final WebServiceFeature getFeature(final String id) {
        if (this.features != null) {
            for (final WebServiceFeature f : this.features) {
                if (f.getID().equals(id)) {
                    return f;
                }
            }
        }
        return null;
    }
    
    @Nullable
    @Override
    public <F extends WebServiceFeature> F getFeature(@NotNull final Class<F> featureType) {
        if (this.features == null) {
            return null;
        }
        return this.features.get(featureType);
    }
}
