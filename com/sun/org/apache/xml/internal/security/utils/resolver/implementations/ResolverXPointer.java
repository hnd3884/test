package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;

public class ResolverXPointer extends ResourceResolverSpi
{
    private static final Logger LOG;
    private static final String XP = "#xpointer(id(";
    private static final int XP_LENGTH;
    
    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    @Override
    public XMLSignatureInput engineResolveURI(final ResourceResolverContext resourceResolverContext) throws ResourceResolverException {
        Node elementById = null;
        final Document ownerDocument = resourceResolverContext.attr.getOwnerElement().getOwnerDocument();
        if (isXPointerSlash(resourceResolverContext.uriToResolve)) {
            elementById = ownerDocument;
        }
        else if (isXPointerId(resourceResolverContext.uriToResolve)) {
            final String xPointerId = getXPointerId(resourceResolverContext.uriToResolve);
            elementById = ownerDocument.getElementById(xPointerId);
            if (resourceResolverContext.secureValidation && !XMLUtils.protectAgainstWrappingAttack(resourceResolverContext.attr.getOwnerDocument().getDocumentElement(), xPointerId)) {
                throw new ResourceResolverException("signature.Verification.MultipleIDs", new Object[] { xPointerId }, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
            }
            if (elementById == null) {
                throw new ResourceResolverException("signature.Verification.MissingID", new Object[] { xPointerId }, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
            }
        }
        final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(elementById);
        xmlSignatureInput.setSecureValidation(resourceResolverContext.secureValidation);
        xmlSignatureInput.setMIMEType("text/xml");
        if (resourceResolverContext.baseUri != null && resourceResolverContext.baseUri.length() > 0) {
            xmlSignatureInput.setSourceURI(resourceResolverContext.baseUri.concat(resourceResolverContext.uriToResolve));
        }
        else {
            xmlSignatureInput.setSourceURI(resourceResolverContext.uriToResolve);
        }
        return xmlSignatureInput;
    }
    
    @Override
    public boolean engineCanResolveURI(final ResourceResolverContext resourceResolverContext) {
        return resourceResolverContext.uriToResolve != null && (isXPointerSlash(resourceResolverContext.uriToResolve) || isXPointerId(resourceResolverContext.uriToResolve));
    }
    
    private static boolean isXPointerSlash(final String s) {
        return s.equals("#xpointer(/)");
    }
    
    private static boolean isXPointerId(final String s) {
        if (s.startsWith("#xpointer(id(") && s.endsWith("))")) {
            final String substring = s.substring(ResolverXPointer.XP_LENGTH, s.length() - 2);
            final int n = substring.length() - 1;
            if ((substring.charAt(0) == '\"' && substring.charAt(n) == '\"') || (substring.charAt(0) == '\'' && substring.charAt(n) == '\'')) {
                ResolverXPointer.LOG.debug("Id = {}", substring.substring(1, n));
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
        LOG = LoggerFactory.getLogger(ResolverXPointer.class);
        XP_LENGTH = "#xpointer(id(".length();
    }
}
