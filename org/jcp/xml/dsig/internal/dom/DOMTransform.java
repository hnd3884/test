package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.dom.DOMSignContext;
import java.io.OutputStream;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.Data;
import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMCryptoContext;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.XMLStructure;
import org.w3c.dom.Node;
import java.security.NoSuchAlgorithmException;
import javax.xml.crypto.MarshalException;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.Transform;

public class DOMTransform extends DOMStructure implements Transform
{
    protected TransformService spi;
    
    public DOMTransform(final TransformService spi) {
        this.spi = spi;
    }
    
    public DOMTransform(final Element element, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        final String attributeValue = DOMUtils.getAttributeValue(element, "Algorithm");
        try {
            this.spi = TransformService.getInstance(attributeValue, "DOM");
        }
        catch (final NoSuchAlgorithmException ex) {
            try {
                this.spi = TransformService.getInstance(attributeValue, "DOM", provider);
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new MarshalException(ex2);
            }
        }
        try {
            this.spi.init(new javax.xml.crypto.dom.DOMStructure(element), xmlCryptoContext);
        }
        catch (final InvalidAlgorithmParameterException ex3) {
            throw new MarshalException(ex3);
        }
    }
    
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.spi.getParameterSpec();
    }
    
    public final String getAlgorithm() {
        return this.spi.getAlgorithm();
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        Element element;
        if (node.getLocalName().equals("Transforms")) {
            element = DOMUtils.createElement(ownerDocument, "Transform", "http://www.w3.org/2000/09/xmldsig#", s);
        }
        else {
            element = DOMUtils.createElement(ownerDocument, "CanonicalizationMethod", "http://www.w3.org/2000/09/xmldsig#", s);
        }
        DOMUtils.setAttribute(element, "Algorithm", this.getAlgorithm());
        this.spi.marshalParams(new javax.xml.crypto.dom.DOMStructure(element), domCryptoContext);
        node.appendChild(element);
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        return this.spi.transform(data, xmlCryptoContext);
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext, final OutputStream outputStream) throws TransformException {
        return this.spi.transform(data, xmlCryptoContext, outputStream);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transform)) {
            return false;
        }
        final Transform transform = (Transform)o;
        return this.getAlgorithm().equals(transform.getAlgorithm()) && DOMUtils.paramsEqual(this.getParameterSpec(), transform.getParameterSpec());
    }
    
    public int hashCode() {
        return 58;
    }
    
    Data transform(final Data data, final XMLCryptoContext xmlCryptoContext, final DOMSignContext domSignContext) throws MarshalException, TransformException {
        this.marshal(domSignContext.getParent(), DOMUtils.getSignaturePrefix(domSignContext), domSignContext);
        return this.transform(data, xmlCryptoContext);
    }
}
