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

public class ResolverXPointer extends ResourceResolverSpi
{
    static Log log;
    private static final String XP = "#xpointer(id(";
    private static final int XP_LENGTH;
    
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    public XMLSignatureInput engineResolve(final Attr attr, final String s) throws ResourceResolverException {
        Node elementById = null;
        final Document ownerDocument = attr.getOwnerElement().getOwnerDocument();
        final String nodeValue = attr.getNodeValue();
        if (isXPointerSlash(nodeValue)) {
            elementById = ownerDocument;
        }
        else if (isXPointerId(nodeValue)) {
            final String xPointerId = getXPointerId(nodeValue);
            elementById = IdResolver.getElementById(ownerDocument, xPointerId);
            if (elementById == null) {
                throw new ResourceResolverException("signature.Verification.MissingID", new Object[] { xPointerId }, attr, s);
            }
        }
        final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(elementById);
        xmlSignatureInput.setMIMEType("text/xml");
        if (s != null && s.length() > 0) {
            xmlSignatureInput.setSourceURI(s.concat(attr.getNodeValue()));
        }
        else {
            xmlSignatureInput.setSourceURI(attr.getNodeValue());
        }
        return xmlSignatureInput;
    }
    
    public boolean engineCanResolve(final Attr attr, final String s) {
        if (attr == null) {
            return false;
        }
        final String nodeValue = attr.getNodeValue();
        return isXPointerSlash(nodeValue) || isXPointerId(nodeValue);
    }
    
    private static boolean isXPointerSlash(final String s) {
        return s.equals("#xpointer(/)");
    }
    
    private static boolean isXPointerId(final String s) {
        if (s.startsWith("#xpointer(id(") && s.endsWith("))")) {
            final String substring = s.substring(ResolverXPointer.XP_LENGTH, s.length() - 2);
            final int n = substring.length() - 1;
            if ((substring.charAt(0) == '\"' && substring.charAt(n) == '\"') || (substring.charAt(0) == '\'' && substring.charAt(n) == '\'')) {
                if (ResolverXPointer.log.isDebugEnabled()) {
                    ResolverXPointer.log.debug((Object)("Id=" + substring.substring(1, n)));
                }
                return true;
            }
        }
        return false;
    }
    
    private static String getXPointerId(final String s) {
        if (s.startsWith("#xpointer(id(") && s.endsWith("))")) {
            final String substring = s.substring(ResolverXPointer.XP_LENGTH, s.length() - 2);
            final int n = substring.length() - 1;
            if ((substring.charAt(0) == '\"' && substring.charAt(n) == '\"') || (substring.charAt(0) == '\'' && substring.charAt(n) == '\'')) {
                return substring.substring(1, n);
            }
        }
        return null;
    }
    
    static {
        ResolverXPointer.log = LogFactory.getLog(ResolverXPointer.class.getName());
        XP_LENGTH = "#xpointer(id(".length();
    }
}
