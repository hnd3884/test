package org.apache.xml.security.utils.resolver.implementations;

import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.xml.utils.URI;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

public class ResolverLocalFilesystem extends ResourceResolverSpi
{
    static Log log;
    private static int FILE_URI_LENGTH;
    
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    public XMLSignatureInput engineResolve(final Attr attr, final String s) throws ResourceResolverException {
        try {
            final URI uri = new URI(new URI(s), attr.getNodeValue());
            final URI uri2 = new URI(uri);
            uri2.setFragment((String)null);
            final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(new FileInputStream(translateUriToFilename(uri2.toString())));
            xmlSignatureInput.setSourceURI(uri.toString());
            return xmlSignatureInput;
        }
        catch (final Exception ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex, attr, s);
        }
    }
    
    private static String translateUriToFilename(final String s) {
        String s2 = s.substring(ResolverLocalFilesystem.FILE_URI_LENGTH);
        if (s2.indexOf("%20") > -1) {
            int n = 0;
            final StringBuffer sb = new StringBuffer(s2.length());
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
    
    public boolean engineCanResolve(final Attr attr, final String s) {
        if (attr == null) {
            return false;
        }
        final String nodeValue = attr.getNodeValue();
        if (nodeValue.equals("") || nodeValue.charAt(0) == '#') {
            return false;
        }
        try {
            if (ResolverLocalFilesystem.log.isDebugEnabled()) {
                ResolverLocalFilesystem.log.debug((Object)("I was asked whether I can resolve " + nodeValue));
            }
            if (nodeValue.startsWith("file:") || s.startsWith("file:")) {
                if (ResolverLocalFilesystem.log.isDebugEnabled()) {
                    ResolverLocalFilesystem.log.debug((Object)("I state that I can resolve " + nodeValue));
                }
                return true;
            }
        }
        catch (final Exception ex) {}
        ResolverLocalFilesystem.log.debug((Object)"But I can't");
        return false;
    }
    
    static {
        ResolverLocalFilesystem.log = LogFactory.getLog(ResolverLocalFilesystem.class.getName());
        ResolverLocalFilesystem.FILE_URI_LENGTH = "file:/".length();
    }
}
