package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class RuntimeModelerException extends JAXWSExceptionBase
{
    public RuntimeModelerException(final String key, final Object... args) {
        super(key, args);
    }
    
    public RuntimeModelerException(final Throwable throwable) {
        super(throwable);
    }
    
    public RuntimeModelerException(final Localizable arg) {
        super("nestedModelerError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.modeler";
    }
}
