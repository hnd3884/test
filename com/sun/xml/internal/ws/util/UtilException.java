package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class UtilException extends JAXWSExceptionBase
{
    public UtilException(final String key, final Object... args) {
        super(key, args);
    }
    
    public UtilException(final Throwable throwable) {
        super(throwable);
    }
    
    public UtilException(final Localizable arg) {
        super("nestedUtilError", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.util";
    }
}
