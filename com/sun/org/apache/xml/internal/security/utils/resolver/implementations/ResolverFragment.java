package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;

public class ResolverFragment extends ResourceResolverSpi
{
    private static final Logger LOG;
    
    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    @Override
    public XMLSignatureInput engineResolveURI(final ResourceResolverContext resourceResolverContext) throws ResourceResolverException {
        final Document ownerDocument = resourceResolverContext.attr.getOwnerElement().getOwnerDocument();
        Object elementById;
        if (resourceResolverContext.uriToResolve.equals("")) {
            ResolverFragment.LOG.debug("ResolverFragment with empty URI (means complete document)");
            elementById = ownerDocument;
        }
        else {
            final String substring = resourceResolverContext.uriToResolve.substring(1);
            elementById = ownerDocument.getElementById(substring);
            if (elementById == null) {
                throw new ResourceResolverException("signature.Verification.MissingID", new Object[] { substring }, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
            }
            if (resourceResolverContext.secureValidation && !XMLUtils.protectAgainstWrappingAttack(resourceResolverContext.attr.getOwnerDocument().getDocumentElement(), substring)) {
                throw new ResourceResolverException("signature.Verification.MultipleIDs", new Object[] { substring }, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
            }
            ResolverFragment.LOG.debug("Try to catch an Element with ID {} and Element was {}", substring, elementById);
        }
        final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput((Node)elementById);
        xmlSignatureInput.setSecureValidation(resourceResolverContext.secureValidation);
        xmlSignatureInput.setExcludeComments(true);
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
        if (resourceResolverContext.uriToResolve == null) {
            ResolverFragment.LOG.debug("Quick fail for null uri");
            return false;
        }
        if (resourceResolverContext.uriToResolve.equals("") || (resourceResolverContext.uriToResolve.charAt(0) == '#' && !resourceResolverContext.uriToResolve.startsWith("#xpointer("))) {
            ResolverFragment.LOG.debug("State I can resolve reference: \"{}\"", resourceResolverContext.uriToResolve);
            return true;
        }
        ResolverFragment.LOG.debug("Do not seem to be able to resolve reference: \"{}\"", resourceResolverContext.uriToResolve);
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ResolverFragment.class);
    }
}
