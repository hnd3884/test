package com.sun.xml.internal.ws.api;

import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ComponentsFeature extends WebServiceFeature implements ServiceSharedFeatureMarker
{
    private final List<ComponentFeature> componentFeatures;
    
    public ComponentsFeature(final List<ComponentFeature> componentFeatures) {
        this.enabled = true;
        this.componentFeatures = componentFeatures;
    }
    
    @Override
    public String getID() {
        return ComponentsFeature.class.getName();
    }
    
    public List<ComponentFeature> getComponentFeatures() {
        return this.componentFeatures;
    }
}
