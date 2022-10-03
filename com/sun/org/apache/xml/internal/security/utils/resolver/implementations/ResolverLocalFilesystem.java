package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;

public class ResolverLocalFilesystem extends ResourceResolverSpi
{
    private static final int FILE_URI_LENGTH;
    private static final Logger LOG;
    
    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    @Override
    public XMLSignatureInput engineResolveURI(final ResourceResolverContext resourceResolverContext) throws ResourceResolverException {
        try {
            final URI newURI = getNewURI(resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
            final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(Files.newInputStream(Paths.get(translateUriToFilename(newURI.toString()), new String[0]), new OpenOption[0]));
            xmlSignatureInput.setSecureValidation(resourceResolverContext.secureValidation);
            xmlSignatureInput.setSourceURI(newURI.toString());
            return xmlSignatureInput;
        }
        catch (final Exception ex) {
            throw new ResourceResolverException(ex, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "generic.EmptyMessage");
        }
    }
    
    private static String translateUriToFilename(final String s) {
        String s2 = s.substring(ResolverLocalFilesystem.FILE_URI_LENGTH);
        if (s2.indexOf("%20") > -1) {
            int n = 0;
            final StringBuilder sb = new StringBuilder(s2.length());
            int i;
            do {
                i = s2.indexOf("%20", n);
                if (i == -1) {
                    sb.append(s2.substring(n));
                }
                else {
                    sb.append(s2.substring(n, i));
                    sb.append(' ');
                    n = i + 3;
                }
            } while (i != -1);
            s2 = sb.toString();
        }
        if (s2.charAt(1) == ':') {
            return s2;
        }
        return "/" + s2;
    }
    
    @Override
    public boolean engineCanResolveURI(final ResourceResolverContext resourceResolverContext) {
        if (resourceResolverContext.uriToResolve == null) {
            return false;
        }
        if (resourceResolverContext.uriToResolve.equals("") || resourceResolverContext.uriToResolve.charAt(0) == '#' || resourceResolverContext.uriToResolve.startsWith("http:")) {
            return false;
        }
        try {
            ResolverLocalFilesystem.LOG.debug("I was asked whether I can resolve {}", resourceResolverContext.uriToResolve);
            if (resourceResolverContext.uriToResolve.startsWith("file:") || resourceResolverContext.baseUri.startsWith("file:")) {
                ResolverLocalFilesystem.LOG.debug("I state that I can resolve {}", resourceResolverContext.uriToResolve);
                return true;
            }
        }
        catch (final Exception ex) {
            ResolverLocalFilesystem.LOG.debug(ex.getMessage(), ex);
        }
        ResolverLocalFilesystem.LOG.debug("But I can't");
        return false;
    }
    
    private static URI getNewURI(final String s, final String s2) throws URISyntaxException {
        URI resolve;
        if (s2 == null || "".equals(s2)) {
            resolve = new URI(s);
        }
        else {
            resolve = new URI(s2).resolve(s);
        }
        if (resolve.getFragment() != null) {
            return new URI(resolve.getScheme(), resolve.getSchemeSpecificPart(), null);
        }
        return resolve;
    }
    
    static {
        FILE_URI_LENGTH = "file:/".length();
        LOG = LoggerFactory.getLogger(ResolverLocalFilesystem.class);
    }
}
