package com.sun.org.apache.xml.internal.security.c14n;

import java.io.OutputStream;
import java.util.Set;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;

public abstract class CanonicalizerSpi
{
    protected boolean reset;
    protected boolean secureValidation;
    
    public CanonicalizerSpi() {
        this.reset = false;
    }
    
    public byte[] engineCanonicalize(final byte[] array) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        Node read = null;
        try (final ByteArrayInputStream byteStream = new ByteArrayInputStream(array)) {
            read = XMLUtils.read(new InputSource(byteStream), this.secureValidation);
        }
        return this.engineCanonicalizeSubTree(read);
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final NodeList list) throws CanonicalizationException {
        return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(list));
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final NodeList list, final String s) throws CanonicalizationException {
        return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(list), s);
    }
    
    public abstract String engineGetURI();
    
    public abstract boolean engineGetIncludeComments();
    
    public abstract byte[] engineCanonicalizeXPathNodeSet(final Set<Node> p0) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeXPathNodeSet(final Set<Node> p0, final String p1) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeSubTree(final Node p0) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeSubTree(final Node p0, final String p1) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeSubTree(final Node p0, final String p1, final boolean p2) throws CanonicalizationException;
    
    public abstract void setWriter(final OutputStream p0);
    
    public boolean isSecureValidation() {
        return this.secureValidation;
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
    }
}
