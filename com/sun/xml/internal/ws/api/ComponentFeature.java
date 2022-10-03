package com.sun.xml.internal.ws.api;

import javax.xml.ws.WebServiceFeature;

public class ComponentFeature extends WebServiceFeature implements ServiceSharedFeatureMarker
{
    private final Component component;
    private final Target target;
    
    public ComponentFeature(final Component component) {
        this(component, Target.CONTAINER);
    }
    
    public ComponentFeature(final Component component, final Target target) {
        this.enabled = true;
        this.component = component;
        this.target = target;
    }
    
    @Override
    public String getID() {
        return ComponentFeature.class.getName();
    }
    
    public Component getComponent() {
        return this.component;
    }
    
    public Target getTarget() {
        return this.target;
    }
    
    public enum Target
    {
        CONTAINER, 
        ENDPOINT, 
        SERVICE, 
        STUB;
    }
}
