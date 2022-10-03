package com.sun.xml.internal.ws.addressing;

import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.util.logging.Logger;

public class WsaActionUtil
{
    private static final Logger LOGGER;
    
    public static final String getDefaultFaultAction(final JavaMethod method, final CheckedException ce) {
        String tns = method.getOwner().getTargetNamespace();
        final String delim = getDelimiter(tns);
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + method.getOperationName() + delim + "Fault" + delim + ce.getExceptionClass().getSimpleName();
    }
    
    private static String getDelimiter(final String tns) {
        String delim = "/";
        try {
            final URI uri = new URI(tns);
            if (uri.getScheme() != null && uri.getScheme().equalsIgnoreCase("urn")) {
                delim = ":";
            }
        }
        catch (final URISyntaxException e) {
            WsaActionUtil.LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        return delim;
    }
    
    static {
        LOGGER = Logger.getLogger(WsaActionUtil.class.getName());
    }
}
