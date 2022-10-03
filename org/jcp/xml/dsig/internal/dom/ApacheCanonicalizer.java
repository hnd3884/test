package org.jcp.xml.dsig.internal.dom;

import java.util.Set;
import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.NodeSetData;
import java.io.InputStream;
import javax.xml.crypto.OctetStreamData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
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
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.c14n.Canonicalizer;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.TransformService;

public abstract class ApacheCanonicalizer extends TransformService
{
    private static Logger log;
    protected Canonicalizer apacheCanonicalizer;
    private org.apache.xml.security.transforms.Transform apacheTransform;
    protected String inclusiveNamespaces;
    protected C14NMethodParameterSpec params;
    protected Document ownerDoc;
    protected Element transformElem;
    
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
    
    public Data canonicalize(final Data data, final XMLCryptoContext xmlCryptoContext) throws TransformException {
        return this.canonicalize(data, xmlCryptoContext, null);
    }
    
    public Data canonicalize(final Data data, final XMLCryptoContext xmlCryptoContext, final OutputStream writer) throws TransformException {
        if (this.apacheCanonicalizer == null) {
            try {
                this.apacheCanonicalizer = Canonicalizer.getInstance(this.getAlgorithm());
                if (ApacheCanonicalizer.log.isLoggable(Level.FINE)) {
                    ApacheCanonicalizer.log.log(Level.FINE, "Created canonicalizer for algorithm: " + this.getAlgorithm());
                }
            }
            catch (final InvalidCanonicalizerException ex) {
                throw new TransformException("Couldn't find Canonicalizer for: " + this.getAlgorithm() + ": " + ex.getMessage(), ex);
            }
        }
        if (writer != null) {
            this.apacheCanonicalizer.setWriter(writer);
        }
        else {
            this.apacheCanonicalizer.setWriter(new ByteArrayOutputStream());
        }
        try {
            Set set;
            if (data instanceof ApacheData) {
                final XMLSignatureInput xmlSignatureInput = ((ApacheData)data).getXMLSignatureInput();
                if (xmlSignatureInput.isElement()) {
                    if (this.inclusiveNamespaces != null) {
                        return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(xmlSignatureInput.getSubNode(), this.inclusiveNamespaces)));
                    }
                    return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(xmlSignatureInput.getSubNode())));
                }
                else {
                    if (!xmlSignatureInput.isNodeSet()) {
                        return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(xmlSignatureInput.getOctetStream()))));
                    }
                    set = xmlSignatureInput.getNodeSet();
                }
            }
            else if (data instanceof DOMSubTreeData) {
                final DOMSubTreeData domSubTreeData = (DOMSubTreeData)data;
                if (this.inclusiveNamespaces != null) {
                    return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(domSubTreeData.getRoot(), this.inclusiveNamespaces)));
                }
                return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(domSubTreeData.getRoot())));
            }
            else {
                if (!(data instanceof NodeSetData)) {
                    return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(((OctetStreamData)data).getOctetStream()))));
                }
                set = Utils.toNodeSet(((NodeSetData)data).iterator());
                if (ApacheCanonicalizer.log.isLoggable(Level.FINE)) {
                    ApacheCanonicalizer.log.log(Level.FINE, "Canonicalizing " + set.size() + " nodes");
                }
            }
            if (this.inclusiveNamespaces != null) {
                return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeXPathNodeSet(set, this.inclusiveNamespaces)));
            }
            return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeXPathNodeSet(set)));
        }
        catch (final Exception ex2) {
            throw new TransformException(ex2);
        }
    }
    
    public Data transform(final Data data, final XMLCryptoContext xmlCryptoContext, final OutputStream outputStream) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        if (outputStream == null) {
            throw new NullPointerException("output stream must not be null");
        }
        if (this.ownerDoc == null) {
            throw new TransformException("transform must be marshalled");
        }
        if (this.apacheTransform == null) {
            try {
                (this.apacheTransform = org.apache.xml.security.transforms.Transform.getInstance(this.ownerDoc, this.getAlgorithm(), this.transformElem.getChildNodes())).setElement(this.transformElem, xmlCryptoContext.getBaseURI());
                if (ApacheCanonicalizer.log.isLoggable(Level.FINE)) {
                    ApacheCanonicalizer.log.log(Level.FINE, "Created transform for algorithm: " + this.getAlgorithm());
                }
            }
            catch (final Exception ex) {
                throw new TransformException("Couldn't find Transform for: " + this.getAlgorithm(), ex);
            }
        }
        XMLSignatureInput xmlSignatureInput;
        if (data instanceof ApacheData) {
            if (ApacheCanonicalizer.log.isLoggable(Level.FINE)) {
                ApacheCanonicalizer.log.log(Level.FINE, "ApacheData = true");
            }
            xmlSignatureInput = ((ApacheData)data).getXMLSignatureInput();
        }
        else if (data instanceof NodeSetData) {
            if (ApacheCanonicalizer.log.isLoggable(Level.FINE)) {
                ApacheCanonicalizer.log.log(Level.FINE, "isNodeSet() = true");
            }
            if (data instanceof DOMSubTreeData) {
                final DOMSubTreeData domSubTreeData = (DOMSubTreeData)data;
                xmlSignatureInput = new XMLSignatureInput(domSubTreeData.getRoot());
                xmlSignatureInput.setExcludeComments(domSubTreeData.excludeComments());
            }
            else {
                xmlSignatureInput = new XMLSignatureInput(Utils.toNodeSet(((NodeSetData)data).iterator()));
            }
        }
        else {
            if (ApacheCanonicalizer.log.isLoggable(Level.FINE)) {
                ApacheCanonicalizer.log.log(Level.FINE, "isNodeSet() = false");
            }
            try {
                xmlSignatureInput = new XMLSignatureInput(((OctetStreamData)data).getOctetStream());
            }
            catch (final Exception ex2) {
                throw new TransformException(ex2);
            }
        }
        try {
            final XMLSignatureInput performTransform = this.apacheTransform.performTransform(xmlSignatureInput, outputStream);
            if (!performTransform.isNodeSet() && !performTransform.isElement()) {
                return null;
            }
            if (performTransform.isOctetStream()) {
                return new ApacheOctetStreamData(performTransform);
            }
            return new ApacheNodeSetData(performTransform);
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
        ApacheCanonicalizer.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
