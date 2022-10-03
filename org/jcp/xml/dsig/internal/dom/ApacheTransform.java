package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.OctetStreamData;
import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.NodeSetData;
import java.util.logging.Level;
import javax.xml.crypto.dsig.TransformException;
import java.io.OutputStream;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import java.security.InvalidAlgorithmParameterException;
import org.w3c.dom.Node;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.Transform;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.TransformService;

public abstract class ApacheTransform extends TransformService
{
    private static Logger log;
    private org.apache.xml.security.transforms.Transform apacheTransform;
    protected Document ownerDoc;
    protected Element transformElem;
    protected TransformParameterSpec params;
    
    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }
    
    public void init(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws InvalidAlgorithmParameterException {
        if (xmlCryptoContext != null && !(xmlCryptoContext instanceof DOMCryptoContext)) {
            throw new ClassCastException("context must be of type DOMCryptoContext");
        }
        this.transformElem = (Element)((DOMStructure)xmlStructure).getNode();
        this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
    }
    
    public void marshalParams(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws MarshalException {
        if (xmlCryptoContext != null && !(xmlCryptoContext instanceof DOMCryptoContext)) {
            throw new ClassCastException("context must be of type DOMCryptoContext");
        }
        this.transformElem = (Element)((DOMStructure)xmlStructure).getNode();
        this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        return this.transformIt(data, xmlCryptoContext, null);
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext, final OutputStream outputStream) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        if (outputStream == null) {
            throw new NullPointerException("output stream must not be null");
        }
        return this.transformIt(data, xmlCryptoContext, outputStream);
    }
    
    private Data transformIt(final Data data, final XMLCryptoContext xmlCryptoContext, final OutputStream outputStream) throws TransformException {
        if (this.ownerDoc == null) {
            throw new TransformException("transform must be marshalled");
        }
        if (this.apacheTransform == null) {
            try {
                (this.apacheTransform = org.apache.xml.security.transforms.Transform.getInstance(this.ownerDoc, this.getAlgorithm(), this.transformElem.getChildNodes())).setElement(this.transformElem, xmlCryptoContext.getBaseURI());
                if (ApacheTransform.log.isLoggable(Level.FINE)) {
                    ApacheTransform.log.log(Level.FINE, "Created transform for algorithm: " + this.getAlgorithm());
                }
            }
            catch (final Exception ex) {
                throw new TransformException("Couldn't find Transform for: " + this.getAlgorithm(), ex);
            }
        }
        XMLSignatureInput xmlSignatureInput;
        if (data instanceof ApacheData) {
            if (ApacheTransform.log.isLoggable(Level.FINE)) {
                ApacheTransform.log.log(Level.FINE, "ApacheData = true");
            }
            xmlSignatureInput = ((ApacheData)data).getXMLSignatureInput();
        }
        else if (data instanceof NodeSetData) {
            if (ApacheTransform.log.isLoggable(Level.FINE)) {
                ApacheTransform.log.log(Level.FINE, "isNodeSet() = true");
            }
            if (data instanceof DOMSubTreeData) {
                if (ApacheTransform.log.isLoggable(Level.FINE)) {
                    ApacheTransform.log.log(Level.FINE, "DOMSubTreeData = true");
                }
                final DOMSubTreeData domSubTreeData = (DOMSubTreeData)data;
                xmlSignatureInput = new XMLSignatureInput(domSubTreeData.getRoot());
                xmlSignatureInput.setExcludeComments(domSubTreeData.excludeComments());
            }
            else {
                xmlSignatureInput = new XMLSignatureInput(Utils.toNodeSet(((NodeSetData)data).iterator()));
            }
        }
        else {
            if (ApacheTransform.log.isLoggable(Level.FINE)) {
                ApacheTransform.log.log(Level.FINE, "isNodeSet() = false");
            }
            try {
                xmlSignatureInput = new XMLSignatureInput(((OctetStreamData)data).getOctetStream());
            }
            catch (final Exception ex2) {
                throw new TransformException(ex2);
            }
        }
        try {
            XMLSignatureInput xmlSignatureInput2;
            if (outputStream != null) {
                xmlSignatureInput2 = this.apacheTransform.performTransform(xmlSignatureInput, outputStream);
                if (!xmlSignatureInput2.isNodeSet() && !xmlSignatureInput2.isElement()) {
                    return null;
                }
            }
            else {
                xmlSignatureInput2 = this.apacheTransform.performTransform(xmlSignatureInput);
            }
            if (xmlSignatureInput2.isOctetStream()) {
                return new ApacheOctetStreamData(xmlSignatureInput2);
            }
            return new ApacheNodeSetData(xmlSignatureInput2);
        }
        catch (final Exception ex3) {
            throw new TransformException(ex3);
        }
    }
    
    public final boolean isFeatureSupported(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return false;
    }
    
    static {
        ApacheTransform.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
