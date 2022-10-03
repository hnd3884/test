package org.apache.xml.security.c14n;

import java.io.OutputStream;
import java.util.Set;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;

public abstract class CanonicalizerSpi
{
    protected boolean reset;
    
    public CanonicalizerSpi() {
        this.reset = false;
    }
    
    public byte[] engineCanonicalize(final byte[] array) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        final InputSource inputSource = new InputSource(new ByteArrayInputStream(array));
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setNamespaceAware(true);
        return this.engineCanonicalizeSubTree(instance.newDocumentBuilder().parse(inputSource));
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final NodeList list) throws CanonicalizationException {
        return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(list));
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final NodeList list, final String s) throws CanonicalizationException {
        return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(list), s);
    }
    
    public abstract String engineGetURI();
    
    public abstract boolean engineGetIncludeComments();
    
    public abstract byte[] engineCanonicalizeXPathNodeSet(final Set p0) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeXPathNodeSet(final Set p0, final String p1) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeSubTree(final Node p0) throws CanonicalizationException;
    
    public abstract byte[] engineCanonicalizeSubTree(final Node p0, final String p1) throws CanonicalizationException;
    
    public abstract void setWriter(final OutputStream p0);
}
