package com.sun.org.apache.xml.internal.security.c14n;

import java.util.concurrent.ConcurrentHashMap;
import java.nio.charset.StandardCharsets;
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
import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerPhysical;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.utils.ClassLoaderUtils;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.util.Map;

public class Canonicalizer
{
    public static final String ENCODING;
    public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE = "(.//. | .//@* | .//namespace::*)";
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String ALGO_ID_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String ALGO_ID_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String ALGO_ID_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    public static final String ALGO_ID_C14N_PHYSICAL = "http://santuario.apache.org/c14n/physical";
    private static Map<String, Class<? extends CanonicalizerSpi>> canonicalizerHash;
    private final CanonicalizerSpi canonicalizerSpi;
    private boolean secureValidation;
    
    private Canonicalizer(final String s) throws InvalidCanonicalizerException {
        try {
            this.canonicalizerSpi = Canonicalizer.canonicalizerHash.get(s).newInstance();
            this.canonicalizerSpi.reset = true;
        }
        catch (final Exception ex) {
            throw new InvalidCanonicalizerException(ex, "signature.Canonicalizer.UnknownCanonicalizer", new Object[] { s });
        }
    }
    
    public static final Canonicalizer getInstance(final String s) throws InvalidCanonicalizerException {
        return new Canonicalizer(s);
    }
    
    public static void register(final String s, final String s2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        final Class clazz = Canonicalizer.canonicalizerHash.get(s);
        if (clazz != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, clazz });
        }
        Canonicalizer.canonicalizerHash.put(s, (Class<? extends CanonicalizerSpi>)ClassLoaderUtils.loadClass(s2, Canonicalizer.class));
    }
    
    public static void register(final String s, final Class<? extends CanonicalizerSpi> clazz) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        final Class clazz2 = Canonicalizer.canonicalizerHash.get(s);
        if (clazz2 != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, clazz2 });
        }
        Canonicalizer.canonicalizerHash.put(s, clazz);
    }
    
    public static void registerDefaultAlgorithms() {
        Canonicalizer.canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", Canonicalizer20010315OmitComments.class);
        Canonicalizer.canonicalizerHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", Canonicalizer20010315WithComments.class);
        Canonicalizer.canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", Canonicalizer20010315ExclOmitComments.class);
        Canonicalizer.canonicalizerHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", Canonicalizer20010315ExclWithComments.class);
        Canonicalizer.canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11", Canonicalizer11_OmitComments.class);
        Canonicalizer.canonicalizerHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", Canonicalizer11_WithComments.class);
        Canonicalizer.canonicalizerHash.put("http://santuario.apache.org/c14n/physical", CanonicalizerPhysical.class);
    }
    
    public final String getURI() {
        return this.canonicalizerSpi.engineGetURI();
    }
    
    public boolean getIncludeComments() {
        return this.canonicalizerSpi.engineGetIncludeComments();
    }
    
    public byte[] canonicalize(final byte[] array) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException {
        Node read = null;
        try (final ByteArrayInputStream byteStream = new ByteArrayInputStream(array)) {
            read = XMLUtils.read(new InputSource(byteStream), this.secureValidation);
        }
        return this.canonicalizeSubtree(read);
    }
    
    public byte[] canonicalizeSubtree(final Node node) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeSubTree(node);
    }
    
    public byte[] canonicalizeSubtree(final Node node, final String s) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeSubTree(node, s);
    }
    
    public byte[] canonicalizeSubtree(final Node node, final String s, final boolean b) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeSubTree(node, s, b);
    }
    
    public byte[] canonicalizeXPathNodeSet(final NodeList list) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(list);
    }
    
    public byte[] canonicalizeXPathNodeSet(final NodeList list, final String s) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(list, s);
    }
    
    public byte[] canonicalizeXPathNodeSet(final Set<Node> set) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(set);
    }
    
    public byte[] canonicalizeXPathNodeSet(final Set<Node> set, final String s) throws CanonicalizationException {
        this.canonicalizerSpi.secureValidation = this.secureValidation;
        return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(set, s);
    }
    
    public void setWriter(final OutputStream writer) {
        this.canonicalizerSpi.setWriter(writer);
    }
    
    public String getImplementingCanonicalizerClass() {
        return this.canonicalizerSpi.getClass().getName();
    }
    
    public void notReset() {
        this.canonicalizerSpi.reset = false;
    }
    
    public boolean isSecureValidation() {
        return this.secureValidation;
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
    }
    
    static {
        ENCODING = StandardCharsets.UTF_8.name();
        Canonicalizer.canonicalizerHash = new ConcurrentHashMap<String, Class<? extends CanonicalizerSpi>>();
    }
}
