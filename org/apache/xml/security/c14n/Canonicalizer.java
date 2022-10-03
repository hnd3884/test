package org.apache.xml.security.c14n;

import java.io.OutputStream;
import java.util.Set;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.apache.xml.security.utils.IgnoreAllErrorHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import java.util.HashMap;
import java.util.Map;

public class Canonicalizer
{
    public static final String ENCODING = "UTF8";
    public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String ALGO_ID_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    static boolean _alreadyInitialized;
    static Map _canonicalizerHash;
    protected CanonicalizerSpi canonicalizerSpi;
    
    public static void init() {
        if (!Canonicalizer._alreadyInitialized) {
            Canonicalizer._canonicalizerHash = new HashMap(10);
            Canonicalizer._alreadyInitialized = true;
        }
    }
    
    private Canonicalizer(final String s) throws InvalidCanonicalizerException {
        this.canonicalizerSpi = null;
        try {
            this.canonicalizerSpi = getImplementingClass(s).newInstance();
            this.canonicalizerSpi.reset = true;
        }
        catch (final Exception ex) {
            throw new InvalidCanonicalizerException("signature.Canonicalizer.UnknownCanonicalizer", new Object[] { s });
        }
    }
    
    public static final Canonicalizer getInstance(final String s) throws InvalidCanonicalizerException {
        return new Canonicalizer(s);
    }
    
    public static void register(final String s, final String s2) throws AlgorithmAlreadyRegisteredException {
        final Class implementingClass = getImplementingClass(s);
        if (implementingClass != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, implementingClass });
        }
        try {
            Canonicalizer._canonicalizerHash.put(s, Class.forName(s2));
        }
        catch (final ClassNotFoundException ex) {
            throw new RuntimeException("c14n class not found");
        }
    }
    
    public final String getURI() {
        return this.canonicalizerSpi.engineGetURI();
    }
    
    public boolean getIncludeComments() {
        return this.canonicalizerSpi.engineGetIncludeComments();
    }
    
    public byte[] canonicalize(final byte[] array) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        final InputSource inputSource = new InputSource(new ByteArrayInputStream(array));
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setNamespaceAware(true);
        instance.setValidating(true);
        final DocumentBuilder documentBuilder = instance.newDocumentBuilder();
        documentBuilder.setErrorHandler(new IgnoreAllErrorHandler());
        return this.canonicalizeSubtree(documentBuilder.parse(inputSource));
    }
    
    public byte[] canonicalizeSubtree(final Node node) throws CanonicalizationException {
        return this.canonicalizerSpi.engineCanonicalizeSubTree(node);
    }
    
    public byte[] canonicalizeSubtree(final Node node, final String s) throws CanonicalizationException {
        return this.canonicalizerSpi.engineCanonicalizeSubTree(node, s);
    }
    
    public byte[] canonicalizeXPathNodeSet(final NodeList list) throws CanonicalizationException {
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(list);
    }
    
    public byte[] canonicalizeXPathNodeSet(final NodeList list, final String s) throws CanonicalizationException {
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(list, s);
    }
    
    public byte[] canonicalizeXPathNodeSet(final Set set) throws CanonicalizationException {
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(set);
    }
    
    public byte[] canonicalizeXPathNodeSet(final Set set, final String s) throws CanonicalizationException {
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(set, s);
    }
    
    public void setWriter(final OutputStream writer) {
        this.canonicalizerSpi.setWriter(writer);
    }
    
    public String getImplementingCanonicalizerClass() {
        return this.canonicalizerSpi.getClass().getName();
    }
    
    private static Class getImplementingClass(final String s) {
        return Canonicalizer._canonicalizerHash.get(s);
    }
    
    public void notReset() {
        this.canonicalizerSpi.reset = false;
    }
    
    static {
        Canonicalizer._alreadyInitialized = false;
        Canonicalizer._canonicalizerHash = null;
    }
}
