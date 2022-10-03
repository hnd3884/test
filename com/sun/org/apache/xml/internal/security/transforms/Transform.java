package com.sun.org.apache.xml.internal.security.transforms;

import java.util.concurrent.ConcurrentHashMap;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath2Filter;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXSLT;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformEnvelopedSignature;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusiveWithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusive;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11_WithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NWithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformBase64Decode;
import com.sun.org.apache.xml.internal.security.utils.ClassLoaderUtils;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.util.Map;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public final class Transform extends SignatureElementProxy
{
    private static final Logger LOG;
    private static Map<String, Class<? extends TransformSpi>> transformSpiHash;
    private final TransformSpi transformSpi;
    private boolean secureValidation;
    
    public Transform(final Document document, final String s) throws InvalidTransformException {
        this(document, s, (NodeList)null);
    }
    
    public Transform(final Document document, final String s, final Element element) throws InvalidTransformException {
        super(document);
        HelperNodeList list = null;
        if (element != null) {
            list = new HelperNodeList();
            XMLUtils.addReturnToElement(document, list);
            list.appendChild(element);
            XMLUtils.addReturnToElement(document, list);
        }
        this.transformSpi = this.initializeTransform(s, list);
    }
    
    public Transform(final Document document, final String s, final NodeList list) throws InvalidTransformException {
        super(document);
        this.transformSpi = this.initializeTransform(s, list);
    }
    
    public Transform(final Element element, final String s) throws InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, s);
        final String attributeNS = element.getAttributeNS(null, "Algorithm");
        if (attributeNS == null || attributeNS.length() == 0) {
            throw new TransformationException("xml.WrongContent", new Object[] { "Algorithm", "Transform" });
        }
        final Class clazz = Transform.transformSpiHash.get(attributeNS);
        if (clazz == null) {
            throw new InvalidTransformException("signature.Transform.UnknownTransform", new Object[] { attributeNS });
        }
        try {
            this.transformSpi = (TransformSpi)clazz.newInstance();
        }
        catch (final InstantiationException ex) {
            throw new InvalidTransformException(ex, "signature.Transform.UnknownTransform", new Object[] { attributeNS });
        }
        catch (final IllegalAccessException ex2) {
            throw new InvalidTransformException(ex2, "signature.Transform.UnknownTransform", new Object[] { attributeNS });
        }
    }
    
    public static void register(final String s, final String s2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, InvalidTransformException {
        JavaUtils.checkRegisterPermission();
        final Class clazz = Transform.transformSpiHash.get(s);
        if (clazz != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, clazz });
        }
        Transform.transformSpiHash.put(s, (Class<? extends TransformSpi>)ClassLoaderUtils.loadClass(s2, Transform.class));
    }
    
    public static void register(final String s, final Class<? extends TransformSpi> clazz) throws AlgorithmAlreadyRegisteredException {
        JavaUtils.checkRegisterPermission();
        final Class clazz2 = Transform.transformSpiHash.get(s);
        if (clazz2 != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, clazz2 });
        }
        Transform.transformSpiHash.put(s, clazz);
    }
    
    public static void registerDefaultAlgorithms() {
        Transform.transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#base64", TransformBase64Decode.class);
        Transform.transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", TransformC14N.class);
        Transform.transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", TransformC14NWithComments.class);
        Transform.transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11", TransformC14N11.class);
        Transform.transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", TransformC14N11_WithComments.class);
        Transform.transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", TransformC14NExclusive.class);
        Transform.transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", TransformC14NExclusiveWithComments.class);
        Transform.transformSpiHash.put("http://www.w3.org/TR/1999/REC-xpath-19991116", TransformXPath.class);
        Transform.transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#enveloped-signature", TransformEnvelopedSignature.class);
        Transform.transformSpiHash.put("http://www.w3.org/TR/1999/REC-xslt-19991116", TransformXSLT.class);
        Transform.transformSpiHash.put("http://www.w3.org/2002/06/xmldsig-filter2", TransformXPath2Filter.class);
    }
    
    public String getURI() {
        return this.getLocalAttribute("Algorithm");
    }
    
    public XMLSignatureInput performTransform(final XMLSignatureInput xmlSignatureInput) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
        return this.performTransform(xmlSignatureInput, null);
    }
    
    public XMLSignatureInput performTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
        XMLSignatureInput enginePerformTransform;
        try {
            this.transformSpi.secureValidation = this.secureValidation;
            enginePerformTransform = this.transformSpi.enginePerformTransform(xmlSignatureInput, outputStream, this);
        }
        catch (final ParserConfigurationException ex) {
            throw new CanonicalizationException(ex, "signature.Transform.ErrorDuringTransform", new Object[] { this.getURI(), "ParserConfigurationException" });
        }
        catch (final SAXException ex2) {
            throw new CanonicalizationException(ex2, "signature.Transform.ErrorDuringTransform", new Object[] { this.getURI(), "SAXException" });
        }
        return enginePerformTransform;
    }
    
    @Override
    public String getBaseLocalName() {
        return "Transform";
    }
    
    private TransformSpi initializeTransform(final String s, final NodeList list) throws InvalidTransformException {
        this.setLocalAttribute("Algorithm", s);
        final Class clazz = Transform.transformSpiHash.get(s);
        if (clazz == null) {
            throw new InvalidTransformException("signature.Transform.UnknownTransform", new Object[] { s });
        }
        TransformSpi transformSpi;
        try {
            transformSpi = (TransformSpi)clazz.newInstance();
        }
        catch (final InstantiationException ex) {
            throw new InvalidTransformException(ex, "signature.Transform.UnknownTransform", new Object[] { s });
        }
        catch (final IllegalAccessException ex2) {
            throw new InvalidTransformException(ex2, "signature.Transform.UnknownTransform", new Object[] { s });
        }
        Transform.LOG.debug("Create URI \"{}\" class \"{}\"", s, transformSpi.getClass());
        Transform.LOG.debug("The NodeList is {}", list);
        if (list != null) {
            for (int length = list.getLength(), i = 0; i < length; ++i) {
                this.appendSelf(list.item(i).cloneNode(true));
            }
        }
        return transformSpi;
    }
    
    public boolean isSecureValidation() {
        return this.secureValidation;
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
    }
    
    static {
        LOG = LoggerFactory.getLogger(Transform.class);
        Transform.transformSpiHash = new ConcurrentHashMap<String, Class<? extends TransformSpi>>();
    }
}
