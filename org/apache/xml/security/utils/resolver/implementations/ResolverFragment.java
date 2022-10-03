package org.apache.xml.security.utils.resolver.implementations;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

public class ResolverFragment extends ResourceResolverSpi
{
    static Log log;
    
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    public XMLSignatureInput engineResolve(final Attr attr, final String s) throws ResourceResolverException {
        final String nodeValue = attr.getNodeValue();
        final Document ownerDocument = attr.getOwnerElement().getOwnerDocument();
        Object elementById;
        if (nodeValue.equals("")) {
            ResolverFragment.log.debug((Object)"ResolverFragment with empty URI (means complete document)");
            elementById = ownerDocument;
        }
        else {
            final String substring = nodeValue.substring(1);
            elementById = IdResolver.getElementById(ownerDocument, substring);
            if (elementById == null) {
                throw new ResourceResolverException("signature.Verification.MissingID", new Object[] { substring }, attr, s);
            }
            if (ResolverFragment.log.isDebugEnabled()) {
                ResolverFragment.log.debug((Object)("Try to catch an Element with ID " + substring + " and Element was " + elementById));
            }
        }
        final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput((Node)elementById);
        xmlSignatureInput.setExcludeComments(true);
        xmlSignatureInput.setMIMEType("text/xml");
        xmlSignatureInput.setSourceURI((s != null) ? s.concat(attr.getNodeValue()) : attr.getNodeValue());
        return xmlSignatureInput;
    }
    
    public boolean engineCanResolve(final Attr attr, final String s) {
        if (attr == null) {
            ResolverFragment.log.debug((Object)"Quick fail for null uri");
            return false;
        }
        final String nodeValue = attr.getNodeValue();
        if (nodeValue.equals("") || (nodeValue.charAt(0) == '#' && (nodeValue.charAt(1) != 'x' || !nodeValue.startsWith("#xpointer(")))) {
            if (ResolverFragment.log.isDebugEnabled()) {
                ResolverFragment.log.debug((Object)("State I can resolve reference: \"" + nodeValue + "\""));
            }
            return true;
        }
        if (ResolverFragment.log.isDebugEnabled()) {
            ResolverFragment.log.debug((Object)("Do not seem to be able to resolve reference: \"" + nodeValue + "\""));
        }
        return false;
    }
    
    static {
        ResolverFragment.log = LogFactory.getLog(ResolverFragment.class.getName());
    }
}
