package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class ServerRtException extends JAXWSExceptionBase
{
    public ServerRtException(final String key, final Object... args) {
        super(key, args);
    }
    
    public ServerRtException(final Throwable throwable) {
        super(throwable);
    }
    
    public ServerRtException(final Localizable arg) {
        super("server.rt.err", new Object[] { arg });
    }
    
    public String getDefaultResourceBundleName() {
        return "com.sun.xml.internal.ws.resources.server";
    }
}
