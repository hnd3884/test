package com.sun.org.apache.xerces.internal.jaxp.validation;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.Validator;
import java.util.HashMap;
import javax.xml.validation.Schema;

abstract class AbstractXMLSchema extends Schema implements XSGrammarPoolContainer
{
    private final HashMap fFeatures;
    private final HashMap fProperties;
    
    public AbstractXMLSchema() {
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
    }
    
    @Override
    public final Validator newValidator() {
        return new ValidatorImpl(this);
    }
    
    @Override
    public final ValidatorHandler newValidatorHandler() {
        return new ValidatorHandlerImpl(this);
    }
    
    @Override
    public final Boolean getFeature(final String featureId) {
        return this.fFeatures.get(featureId);
    }
    
    @Override
    public final void setFeature(final String featureId, final boolean state) {
        this.fFeatures.put(featureId, state ? Boolean.TRUE : Boolean.FALSE);
    }
    
    @Override
    public final Object getProperty(final String propertyId) {
        return this.fProperties.get(propertyId);
    }
    
    @Override
    public final void setProperty(final String propertyId, final Object state) {
        this.fProperties.put(propertyId, state);
    }
}
