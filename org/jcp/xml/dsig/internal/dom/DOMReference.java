package org.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.NodeSetData;
import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.dsig.TransformException;
import java.io.OutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.jcp.xml.dsig.internal.DigesterOutputStream;
import java.security.NoSuchAlgorithmException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.URIReference;
import java.util.Arrays;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignContext;
import org.w3c.dom.Document;
import java.util.logging.Level;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.apache.xml.security.exceptions.Base64DecodingException;
import javax.xml.crypto.MarshalException;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Node;
import javax.xml.crypto.XMLCryptoContext;
import java.net.URISyntaxException;
import java.net.URI;
import javax.xml.crypto.dsig.Transform;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.security.Provider;
import java.security.MessageDigest;
import java.io.InputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import javax.xml.crypto.Data;
import java.util.List;
import javax.xml.crypto.dsig.DigestMethod;
import java.util.logging.Logger;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.Reference;

public final class DOMReference extends DOMStructure implements Reference, DOMURIReference
{
    private static Logger log;
    private final DigestMethod digestMethod;
    private final String id;
    private final List appliedTransforms;
    private final List transforms;
    private final List allTransforms;
    private final Data appliedTransformData;
    private Attr here;
    private final String uri;
    private final String type;
    private byte[] digestValue;
    private byte[] calcDigestValue;
    private Element refElem;
    private boolean digested;
    private boolean validated;
    private boolean validationStatus;
    private Data derefData;
    private InputStream dis;
    private MessageDigest md;
    private Provider provider;
    
    public DOMReference(final String s, final String s2, final DigestMethod digestMethod, final List list, final String s3, final Provider provider) {
        this(s, s2, digestMethod, null, null, list, s3, null, provider);
    }
    
    public DOMReference(final String s, final String s2, final DigestMethod digestMethod, final List list, final Data data, final List list2, final String s3, final Provider provider) {
        this(s, s2, digestMethod, list, data, list2, s3, null, provider);
    }
    
    public DOMReference(final String uri, final String type, final DigestMethod digestMethod, final List list, final Data appliedTransformData, final List list2, final String id, final byte[] array, final Provider provider) {
        this.digested = false;
        this.validated = false;
        if (digestMethod == null) {
            throw new NullPointerException("DigestMethod must be non-null");
        }
        if (list == null || list.isEmpty()) {
            this.appliedTransforms = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list3 = new ArrayList(list);
            for (int i = 0; i < list3.size(); ++i) {
                if (!(list3.get(i) instanceof Transform)) {
                    throw new ClassCastException("appliedTransforms[" + i + "] is not a valid type");
                }
            }
            this.appliedTransforms = Collections.unmodifiableList((List<?>)list3);
        }
        if (list2 == null || list2.isEmpty()) {
            this.transforms = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list4 = new ArrayList(list2);
            for (int j = 0; j < list4.size(); ++j) {
                if (!(list4.get(j) instanceof Transform)) {
                    throw new ClassCastException("transforms[" + j + "] is not a valid type");
                }
            }
            this.transforms = Collections.unmodifiableList((List<?>)list4);
        }
        final ArrayList list5 = new ArrayList(this.appliedTransforms);
        list5.addAll(this.transforms);
        this.allTransforms = Collections.unmodifiableList((List<?>)list5);
        this.digestMethod = digestMethod;
        this.uri = uri;
        if (uri != null && !uri.equals("")) {
            try {
                new URI(uri);
            }
            catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
        this.type = type;
        this.id = id;
        if (array != null) {
            this.digestValue = array.clone();
            this.digested = true;
        }
        this.appliedTransformData = appliedTransformData;
        this.provider = provider;
    }
    
    public DOMReference(final Element refElem, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.digested = false;
        this.validated = false;
        Element element = DOMUtils.getFirstChildElement(refElem);
        final ArrayList allTransforms = new ArrayList(5);
        if (element.getLocalName().equals("Transforms")) {
            for (Element element2 = DOMUtils.getFirstChildElement(element); element2 != null; element2 = DOMUtils.getNextSiblingElement(element2)) {
                allTransforms.add(new DOMTransform(element2, xmlCryptoContext, provider));
            }
            element = DOMUtils.getNextSiblingElement(element);
        }
        final Element element3 = element;
        this.digestMethod = DOMDigestMethod.unmarshal(element3);
        try {
            this.digestValue = Base64.decode(DOMUtils.getNextSiblingElement(element3));
        }
        catch (final Base64DecodingException ex) {
            throw new MarshalException(ex);
        }
        this.uri = DOMUtils.getAttributeValue(refElem, "URI");
        this.id = DOMUtils.getAttributeValue(refElem, "Id");
        this.type = DOMUtils.getAttributeValue(refElem, "Type");
        this.here = refElem.getAttributeNodeNS(null, "URI");
        this.refElem = refElem;
        if (allTransforms.isEmpty()) {
            this.transforms = Collections.EMPTY_LIST;
        }
        else {
            this.transforms = Collections.unmodifiableList((List<?>)allTransforms);
        }
        this.appliedTransforms = Collections.EMPTY_LIST;
        this.allTransforms = allTransforms;
        this.appliedTransformData = null;
        this.provider = provider;
    }
    
    public DigestMethod getDigestMethod() {
        return this.digestMethod;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public String getType() {
        return this.type;
    }
    
    public List getTransforms() {
        return this.allTransforms;
    }
    
    public byte[] getDigestValue() {
        return (byte[])((this.digestValue == null) ? null : ((byte[])this.digestValue.clone()));
    }
    
    public byte[] getCalculatedDigestValue() {
        return (byte[])((this.calcDigestValue == null) ? null : ((byte[])this.calcDigestValue.clone()));
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        if (DOMReference.log.isLoggable(Level.FINE)) {
            DOMReference.log.log(Level.FINE, "Marshalling Reference");
        }
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        DOMUtils.setAttributeID(this.refElem = DOMUtils.createElement(ownerDocument, "Reference", "http://www.w3.org/2000/09/xmldsig#", s), "Id", this.id);
        DOMUtils.setAttribute(this.refElem, "URI", this.uri);
        DOMUtils.setAttribute(this.refElem, "Type", this.type);
        if (!this.transforms.isEmpty() || !this.appliedTransforms.isEmpty()) {
            final Element element = DOMUtils.createElement(ownerDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", s);
            this.refElem.appendChild(element);
            for (int i = 0; i < this.appliedTransforms.size(); ++i) {
                ((DOMStructure)this.appliedTransforms.get(i)).marshal(element, s, domCryptoContext);
            }
            for (int j = 0; j < this.transforms.size(); ++j) {
                ((DOMStructure)this.transforms.get(j)).marshal(element, s, domCryptoContext);
            }
        }
        ((DOMDigestMethod)this.digestMethod).marshal(this.refElem, s, domCryptoContext);
        if (DOMReference.log.isLoggable(Level.FINE)) {
            DOMReference.log.log(Level.FINE, "Adding digestValueElem");
        }
        final Element element2 = DOMUtils.createElement(ownerDocument, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", s);
        if (this.digestValue != null) {
            element2.appendChild(ownerDocument.createTextNode(Base64.encode(this.digestValue)));
        }
        this.refElem.appendChild(element2);
        node.appendChild(this.refElem);
        this.here = this.refElem.getAttributeNodeNS(null, "URI");
    }
    
    public void digest(final XMLSignContext xmlSignContext) throws XMLSignatureException {
        Data data;
        if (this.appliedTransformData == null) {
            data = this.dereference(xmlSignContext);
        }
        else {
            data = this.appliedTransformData;
        }
        this.digestValue = this.transform(data, xmlSignContext);
        final String encode = Base64.encode(this.digestValue);
        if (DOMReference.log.isLoggable(Level.FINE)) {
            DOMReference.log.log(Level.FINE, "Reference object uri = " + this.uri);
        }
        final Element lastChildElement = DOMUtils.getLastChildElement(this.refElem);
        if (lastChildElement == null) {
            throw new XMLSignatureException("DigestValue element expected");
        }
        DOMUtils.removeAllChildren(lastChildElement);
        lastChildElement.appendChild(this.refElem.getOwnerDocument().createTextNode(encode));
        this.digested = true;
        if (DOMReference.log.isLoggable(Level.FINE)) {
            DOMReference.log.log(Level.FINE, "Reference digesting completed");
        }
    }
    
    public boolean validate(final XMLValidateContext xmlValidateContext) throws XMLSignatureException {
        if (xmlValidateContext == null) {
            throw new NullPointerException("validateContext cannot be null");
        }
        if (this.validated) {
            return this.validationStatus;
        }
        this.calcDigestValue = this.transform(this.dereference(xmlValidateContext), xmlValidateContext);
        if (DOMReference.log.isLoggable(Level.FINE)) {
            DOMReference.log.log(Level.FINE, "Expected digest: " + Base64.encode(this.digestValue));
            DOMReference.log.log(Level.FINE, "Actual digest: " + Base64.encode(this.calcDigestValue));
        }
        this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
        this.validated = true;
        return this.validationStatus;
    }
    
    public Data getDereferencedData() {
        return this.derefData;
    }
    
    public InputStream getDigestInputStream() {
        return this.dis;
    }
    
    private Data dereference(final XMLCryptoContext xmlCryptoContext) throws XMLSignatureException {
        URIDereferencer uriDereferencer = xmlCryptoContext.getURIDereferencer();
        if (uriDereferencer == null) {
            uriDereferencer = DOMURIDereferencer.INSTANCE;
        }
        Data dereference;
        try {
            dereference = uriDereferencer.dereference(this, xmlCryptoContext);
            if (DOMReference.log.isLoggable(Level.FINE)) {
                DOMReference.log.log(Level.FINE, "URIDereferencer class name: " + uriDereferencer.getClass().getName());
                DOMReference.log.log(Level.FINE, "Data class name: " + dereference.getClass().getName());
            }
        }
        catch (final URIReferenceException ex) {
            throw new XMLSignatureException(ex);
        }
        return dereference;
    }
    
    private byte[] transform(final Data data, final XMLCryptoContext xmlCryptoContext) throws XMLSignatureException {
        if (this.md == null) {
            try {
                this.md = MessageDigest.getInstance(((DOMDigestMethod)this.digestMethod).getMessageDigestAlgorithm());
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new XMLSignatureException(ex);
            }
        }
        this.md.reset();
        final Boolean b = (Boolean)xmlCryptoContext.getProperty("javax.xml.crypto.dsig.cacheReference");
        DigesterOutputStream digesterOutputStream;
        if (b != null && b) {
            this.derefData = copyDerefData(data);
            digesterOutputStream = new DigesterOutputStream(this.md, true);
        }
        else {
            digesterOutputStream = new DigesterOutputStream(this.md);
        }
        final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(digesterOutputStream);
        Data data2 = data;
        for (int i = 0, size = this.transforms.size(); i < size; ++i) {
            final DOMTransform domTransform = this.transforms.get(i);
            try {
                if (i < size - 1) {
                    data2 = domTransform.transform(data2, xmlCryptoContext);
                }
                else {
                    data2 = domTransform.transform(data2, xmlCryptoContext, unsyncBufferedOutputStream);
                }
            }
            catch (final TransformException ex2) {
                throw new XMLSignatureException(ex2);
            }
        }
        try {
            if (data2 != null) {
                XMLSignatureInput xmlSignatureInput;
                if (data2 instanceof ApacheData) {
                    xmlSignatureInput = ((ApacheData)data2).getXMLSignatureInput();
                }
                else if (data2 instanceof OctetStreamData) {
                    xmlSignatureInput = new XMLSignatureInput(((OctetStreamData)data2).getOctetStream());
                }
                else {
                    if (!(data2 instanceof NodeSetData)) {
                        throw new XMLSignatureException("unrecognized Data type");
                    }
                    TransformService transformService;
                    try {
                        transformService = TransformService.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", "DOM");
                    }
                    catch (final NoSuchAlgorithmException ex3) {
                        transformService = TransformService.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", "DOM", this.provider);
                    }
                    xmlSignatureInput = new XMLSignatureInput(((OctetStreamData)transformService.transform(data2, xmlCryptoContext)).getOctetStream());
                }
                xmlSignatureInput.updateOutputStream(unsyncBufferedOutputStream);
            }
            unsyncBufferedOutputStream.flush();
            if (b != null && b) {
                this.dis = digesterOutputStream.getInputStream();
            }
            return digesterOutputStream.getDigestValue();
        }
        catch (final Exception ex4) {
            throw new XMLSignatureException(ex4);
        }
    }
    
    public Node getHere() {
        return this.here;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        final Reference reference = (Reference)o;
        final boolean b = (this.id == null) ? (reference.getId() == null) : this.id.equals(reference.getId());
        final boolean b2 = (this.uri == null) ? (reference.getURI() == null) : this.uri.equals(reference.getURI());
        final boolean b3 = (this.type == null) ? (reference.getType() == null) : this.type.equals(reference.getType());
        Arrays.equals(this.digestValue, reference.getDigestValue());
        return this.digestMethod.equals(reference.getDigestMethod()) && b && b2 && b3 && this.transforms.equals(reference.getTransforms());
    }
    
    public int hashCode() {
        return 47;
    }
    
    boolean isDigested() {
        return this.digested;
    }
    
    private static Data copyDerefData(final Data data) {
        if (data instanceof ApacheData) {
            final XMLSignatureInput xmlSignatureInput = ((ApacheData)data).getXMLSignatureInput();
            if (xmlSignatureInput.isNodeSet()) {
                try {
                    return new NodeSetData() {
                        private final /* synthetic */ Set val$s = xmlSignatureInput.getNodeSet();
                        
                        public Iterator iterator() {
                            return this.val$s.iterator();
                        }
                    };
                }
                catch (final Exception ex) {
                    DOMReference.log.log(Level.WARNING, "cannot cache dereferenced data: " + ex);
                    return null;
                }
            }
            if (xmlSignatureInput.isElement()) {
                return new DOMSubTreeData(xmlSignatureInput.getSubNode(), xmlSignatureInput.isExcludeComments());
            }
            if (!xmlSignatureInput.isOctetStream()) {
                if (!xmlSignatureInput.isByteArray()) {
                    return data;
                }
            }
            try {
                return new OctetStreamData(xmlSignatureInput.getOctetStream(), xmlSignatureInput.getSourceURI(), xmlSignatureInput.getMIMEType());
            }
            catch (final IOException ex2) {
                DOMReference.log.log(Level.WARNING, "cannot cache dereferenced data: " + ex2);
                return null;
            }
        }
        return data;
    }
    
    static {
        DOMReference.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
    }
}
