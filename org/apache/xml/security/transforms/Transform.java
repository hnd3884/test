package org.apache.xml.security.transforms;

import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public final class Transform extends SignatureElementProxy
{
    static Log log;
    static boolean _alreadyInitialized;
    static Map _transformHash;
    static HashMap classesHash;
    protected TransformSpi transformSpi;
    
    public Transform(final Document document, final String s, final NodeList list) throws InvalidTransformException {
        super(document);
        this.transformSpi = null;
        super._constructionElement.setAttributeNS(null, "Algorithm", s);
        this.transformSpi = getImplementingClass(s);
        if (this.transformSpi == null) {
            throw new InvalidTransformException("signature.Transform.UnknownTransform", new Object[] { s });
        }
        if (Transform.log.isDebugEnabled()) {
            Transform.log.debug((Object)("Create URI \"" + s + "\" class \"" + this.transformSpi.getClass() + "\""));
            Transform.log.debug((Object)("The NodeList is " + list));
        }
        if (list != null) {
            for (int i = 0; i < list.getLength(); ++i) {
                super._constructionElement.appendChild(list.item(i).cloneNode(true));
            }
        }
    }
    
    public Transform(final Element element, final String s) throws InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, s);
        this.transformSpi = null;
        final String attributeNS = element.getAttributeNS(null, "Algorithm");
        if (attributeNS == null || attributeNS.length() == 0) {
            throw new TransformationException("xml.WrongContent", new Object[] { "Algorithm", "Transform" });
        }
        this.transformSpi = getImplementingClass(attributeNS);
        if (this.transformSpi == null) {
            throw new InvalidTransformException("signature.Transform.UnknownTransform", new Object[] { attributeNS });
        }
    }
    
    public static final Transform getInstance(final Document document, final String s) throws InvalidTransformException {
        return getInstance(document, s, (NodeList)null);
    }
    
    public static final Transform getInstance(final Document document, final String s, final Element element) throws InvalidTransformException {
        final HelperNodeList list = new HelperNodeList();
        list.appendChild(document.createTextNode("\n"));
        list.appendChild(element);
        list.appendChild(document.createTextNode("\n"));
        return getInstance(document, s, list);
    }
    
    public static final Transform getInstance(final Document document, final String s, final NodeList list) throws InvalidTransformException {
        return new Transform(document, s, list);
    }
    
    public static void init() {
        if (!Transform._alreadyInitialized) {
            Transform._transformHash = new HashMap(10);
            Transform._alreadyInitialized = true;
        }
    }
    
    public static void register(final String s, final String s2) throws AlgorithmAlreadyRegisteredException {
        Object implementingClass = null;
        try {
            implementingClass = getImplementingClass(s);
        }
        catch (final InvalidTransformException ex) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, implementingClass });
        }
        if (implementingClass != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, implementingClass });
        }
        try {
            Transform._transformHash.put(s, Class.forName(s2));
        }
        catch (final ClassNotFoundException ex2) {
            ex2.printStackTrace();
        }
    }
    
    public final String getURI() {
        return super._constructionElement.getAttributeNS(null, "Algorithm");
    }
    
    public XMLSignatureInput performTransform(final XMLSignatureInput xmlSignatureInput) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
        XMLSignatureInput enginePerformTransform;
        try {
            enginePerformTransform = this.transformSpi.enginePerformTransform(xmlSignatureInput, this);
        }
        catch (final ParserConfigurationException ex) {
            throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", new Object[] { this.getURI(), "ParserConfigurationException" }, ex);
        }
        catch (final SAXException ex2) {
            throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", new Object[] { this.getURI(), "SAXException" }, ex2);
        }
        return enginePerformTransform;
    }
    
    public XMLSignatureInput performTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException {
        XMLSignatureInput enginePerformTransform;
        try {
            enginePerformTransform = this.transformSpi.enginePerformTransform(xmlSignatureInput, outputStream, this);
        }
        catch (final ParserConfigurationException ex) {
            throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", new Object[] { this.getURI(), "ParserConfigurationException" }, ex);
        }
        catch (final SAXException ex2) {
            throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", new Object[] { this.getURI(), "SAXException" }, ex2);
        }
        return enginePerformTransform;
    }
    
    private static TransformSpi getImplementingClass(final String s) throws InvalidTransformException {
        try {
            final TransformSpi value = Transform.classesHash.get(s);
            if (value != null) {
                return value;
            }
            final Class clazz = Transform._transformHash.get(s);
            if (clazz != null) {
                final TransformSpi transformSpi = (TransformSpi)clazz.newInstance();
                Transform.classesHash.put(s, transformSpi);
                return transformSpi;
            }
        }
        catch (final InstantiationException ex) {
            throw new InvalidTransformException("signature.Transform.UnknownTransform", new Object[] { s }, ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new InvalidTransformException("signature.Transform.UnknownTransform", new Object[] { s }, ex2);
        }
        return null;
    }
    
    public String getBaseLocalName() {
        return "Transform";
    }
    
    static {
        Transform.log = LogFactory.getLog(Transform.class.getName());
        Transform._alreadyInitialized = false;
        Transform._transformHash = null;
        Transform.classesHash = new HashMap();
    }
}
