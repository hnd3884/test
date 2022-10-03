package org.apache.axiom.util.stax.dialect;

import org.apache.commons.logging.LogFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.logging.Log;
import javax.xml.stream.XMLResolver;

final class SecureXMLResolver implements XMLResolver
{
    private static final Log log;
    
    public Object resolveEntity(final String publicID, final String systemID, final String baseURI, final String namespace) throws XMLStreamException {
        if (SecureXMLResolver.log.isDebugEnabled()) {
            SecureXMLResolver.log.debug((Object)("resolveEntity is disabled because this is a secure XMLStreamReader(" + publicID + ") (" + systemID + ") (" + baseURI + ") (" + namespace + ")"));
        }
        throw new XMLStreamException("Reading external entities is disabled");
    }
    
    static {
        log = LogFactory.getLog((Class)SecureXMLResolver.class);
    }
}
