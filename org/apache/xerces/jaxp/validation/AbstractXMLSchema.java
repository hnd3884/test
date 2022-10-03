package org.apache.xerces.jaxp.validation;

import javax.xml.validation.ValidatorHandler;
import javax.xml.validation.Validator;
import java.util.HashMap;
import javax.xml.validation.Schema;

abstract class AbstractXMLSchema extends Schema implements XSGrammarPoolContainer
{
    private final HashMap fFeatures;
    private final String fXSDVersion;
    
    public AbstractXMLSchema(final String fxsdVersion) {
        this.fFeatures = new HashMap();
        this.fXSDVersion = fxsdVersion;
    }
    
    public final Validator newValidator() {
        return new ValidatorImpl(this);
    }
    
    public final ValidatorHandler newValidatorHandler() {
        return new ValidatorHandlerImpl(this);
    }
    
    public final Boolean getFeature(final String s) {
        return this.fFeatures.get(s);
    }
    
    public final String getXMLSchemaVersion() {
        return this.fXSDVersion;
    }
    
    final void setFeature(final String s, final boolean b) {
        this.fFeatures.put(s, b ? Boolean.TRUE : Boolean.FALSE);
    }
}
