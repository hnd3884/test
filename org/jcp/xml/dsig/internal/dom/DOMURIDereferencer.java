package org.jcp.xml.dsig.internal.dom;

import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.URIReferenceException;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Element;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import org.w3c.dom.Attr;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.URIReference;
import org.apache.xml.security.Init;
import javax.xml.crypto.URIDereferencer;

public class DOMURIDereferencer implements URIDereferencer
{
    static final URIDereferencer INSTANCE;
    
    private DOMURIDereferencer() {
        Init.init();
    }
    
    public Data dereference(final URIReference uriReference, final XMLCryptoContext xmlCryptoContext) throws URIReferenceException {
        if (uriReference == null) {
            throw new NullPointerException("uriRef cannot be null");
        }
        if (xmlCryptoContext == null) {
            throw new NullPointerException("context cannot be null");
        }
        final Attr attr = (Attr)((DOMURIReference)uriReference).getHere();
        final String uri = uriReference.getURI();
        final DOMCryptoContext domCryptoContext = (DOMCryptoContext)xmlCryptoContext;
        if (uri != null && uri.length() != 0 && uri.charAt(0) == '#') {
            String s = uri.substring(1);
            if (s.startsWith("xpointer(id(")) {
                final int index = s.indexOf(39);
                s = s.substring(index + 1, s.indexOf(39, index + 1));
            }
            final Element elementById = domCryptoContext.getElementById(s);
            if (elementById != null) {
                IdResolver.registerElementById(elementById, s);
            }
        }
        try {
            final String baseURI = xmlCryptoContext.getBaseURI();
            final XMLSignatureInput resolve = ResourceResolver.getInstance(attr, baseURI).resolve(attr, baseURI);
            if (resolve.isOctetStream()) {
                return new ApacheOctetStreamData(resolve);
            }
            return new ApacheNodeSetData(resolve);
        }
        catch (final Exception ex) {
            throw new URIReferenceException(ex);
        }
    }
    
    static {
        INSTANCE = new DOMURIDereferencer();
    }
}
