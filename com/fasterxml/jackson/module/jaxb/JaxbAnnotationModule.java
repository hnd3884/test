package com.fasterxml.jackson.module.jaxb;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;

public class JaxbAnnotationModule extends Module
{
    protected Priority _priority;
    protected JaxbAnnotationIntrospector _introspector;
    protected JsonInclude.Include _nonNillableInclusion;
    
    public JaxbAnnotationModule() {
        this._priority = Priority.PRIMARY;
    }
    
    public JaxbAnnotationModule(final JaxbAnnotationIntrospector intr) {
        this._priority = Priority.PRIMARY;
        this._introspector = intr;
    }
    
    public String getModuleName() {
        return this.getClass().getSimpleName();
    }
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public void setupModule(final Module.SetupContext context) {
        JaxbAnnotationIntrospector intr = this._introspector;
        if (intr == null) {
            intr = new JaxbAnnotationIntrospector(context.getTypeFactory());
            if (this._nonNillableInclusion != null) {
                intr.setNonNillableInclusion(this._nonNillableInclusion);
            }
        }
        switch (this._priority) {
            case PRIMARY: {
                context.insertAnnotationIntrospector((AnnotationIntrospector)intr);
                break;
            }
            case SECONDARY: {
                context.appendAnnotationIntrospector((AnnotationIntrospector)intr);
                break;
            }
        }
    }
    
    public JaxbAnnotationModule setPriority(final Priority p) {
        this._priority = p;
        return this;
    }
    
    public Priority getPriority() {
        return this._priority;
    }
    
    public JaxbAnnotationModule setNonNillableInclusion(final JsonInclude.Include incl) {
        this._nonNillableInclusion = incl;
        if (this._introspector != null) {
            this._introspector.setNonNillableInclusion(incl);
        }
        return this;
    }
    
    public JsonInclude.Include getNonNillableInclusion() {
        return this._nonNillableInclusion;
    }
    
    public enum Priority
    {
        PRIMARY, 
        SECONDARY;
    }
}
