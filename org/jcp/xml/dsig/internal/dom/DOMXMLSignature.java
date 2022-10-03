package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.SignatureMethod;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.Init;
import javax.xml.crypto.dsig.Transform;
import java.security.Key;
import java.security.InvalidKeyException;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.XMLStructure;
import java.util.logging.Level;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import java.security.Provider;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLObject;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.crypto.KeySelectorResult;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.crypto.dsig.SignedInfo;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.XMLSignature;

public final class DOMXMLSignature extends DOMStructure implements XMLSignature
{
    private static Logger log;
    private String id;
    private SignatureValue sv;
    private KeyInfo ki;
    private List objects;
    private SignedInfo si;
    private Document ownerDoc;
    private Element localSigElem;
    private Element sigElem;
    private boolean validationStatus;
    private boolean validated;
    private KeySelectorResult ksr;
    private HashMap signatureIdMap;
    
    public DOMXMLSignature(final SignedInfo si, final KeyInfo ki, final List list, final String id, final String s) {
        this.ownerDoc = null;
        this.localSigElem = null;
        this.sigElem = null;
        this.validated = false;
        if (si == null) {
            throw new NullPointerException("signedInfo cannot be null");
        }
        this.si = si;
        this.id = id;
        this.sv = new DOMSignatureValue(s);
        if (list == null) {
            this.objects = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list2 = new ArrayList(list);
            for (int i = 0; i < list2.size(); ++i) {
                if (!(list2.get(i) instanceof XMLObject)) {
                    throw new ClassCastException("objs[" + i + "] is not an XMLObject");
                }
            }
            this.objects = Collections.unmodifiableList((List<?>)list2);
        }
        this.ki = ki;
    }
    
    public DOMXMLSignature(final Element localSigElem, final XMLCryptoContext xmlCryptoContext, final Provider provider) throws MarshalException {
        this.ownerDoc = null;
        this.localSigElem = null;
        this.sigElem = null;
        this.validated = false;
        this.localSigElem = localSigElem;
        this.ownerDoc = this.localSigElem.getOwnerDocument();
        this.id = DOMUtils.getAttributeValue(this.localSigElem, "Id");
        final Element firstChildElement = DOMUtils.getFirstChildElement(this.localSigElem);
        this.si = new DOMSignedInfo(firstChildElement, xmlCryptoContext, provider);
        final Element nextSiblingElement = DOMUtils.getNextSiblingElement(firstChildElement);
        this.sv = new DOMSignatureValue(nextSiblingElement);
        Element element = DOMUtils.getNextSiblingElement(nextSiblingElement);
        if (element != null && element.getLocalName().equals("KeyInfo")) {
            this.ki = new DOMKeyInfo(element, xmlCryptoContext, provider);
            element = DOMUtils.getNextSiblingElement(element);
        }
        if (element == null) {
            this.objects = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list = new ArrayList();
            while (element != null) {
                list.add(new DOMXMLObject(element, xmlCryptoContext, provider));
                element = DOMUtils.getNextSiblingElement(element);
            }
            this.objects = Collections.unmodifiableList((List<?>)list);
        }
    }
    
    public String getId() {
        return this.id;
    }
    
    public KeyInfo getKeyInfo() {
        return this.ki;
    }
    
    public SignedInfo getSignedInfo() {
        return this.si;
    }
    
    public List getObjects() {
        return this.objects;
    }
    
    public SignatureValue getSignatureValue() {
        return this.sv;
    }
    
    public KeySelectorResult getKeySelectorResult() {
        return this.ksr;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        this.marshal(node, null, s, domCryptoContext);
    }
    
    public void marshal(final Node node, final Node node2, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        this.ownerDoc = DOMUtils.getOwnerDocument(node);
        this.sigElem = DOMUtils.createElement(this.ownerDoc, "Signature", "http://www.w3.org/2000/09/xmldsig#", s);
        if (s == null) {
            this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
        }
        else {
            this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + s, "http://www.w3.org/2000/09/xmldsig#");
        }
        ((DOMSignedInfo)this.si).marshal(this.sigElem, s, domCryptoContext);
        ((DOMSignatureValue)this.sv).marshal(this.sigElem, s, domCryptoContext);
        if (this.ki != null) {
            ((DOMKeyInfo)this.ki).marshal(this.sigElem, null, s, domCryptoContext);
        }
        for (int i = 0; i < this.objects.size(); ++i) {
            ((DOMXMLObject)this.objects.get(i)).marshal(this.sigElem, s, domCryptoContext);
        }
        DOMUtils.setAttributeID(this.sigElem, "Id", this.id);
        node.insertBefore(this.sigElem, node2);
    }
    
    public boolean validate(final XMLValidateContext xmlValidateContext) throws XMLSignatureException {
        if (xmlValidateContext == null) {
            throw new NullPointerException("validateContext is null");
        }
        if (this.validated) {
            return this.validationStatus;
        }
        final List references = this.si.getReferences();
        int n = 1;
        boolean validate;
        for (int n2 = 0, size = references.size(); n != 0 && n2 < size; n &= (validate ? 1 : 0), ++n2) {
            final Reference reference = references.get(n2);
            validate = reference.validate(xmlValidateContext);
            if (DOMXMLSignature.log.isLoggable(Level.FINE)) {
                DOMXMLSignature.log.log(Level.FINE, "Reference[" + reference.getURI() + "] is valid: " + validate);
            }
        }
        if (n == 0) {
            if (DOMXMLSignature.log.isLoggable(Level.FINE)) {
                DOMXMLSignature.log.log(Level.FINE, "Couldn't validate the References");
            }
            this.validationStatus = false;
            this.validated = true;
            return this.validationStatus;
        }
        if (!this.sv.validate(xmlValidateContext)) {
            this.validationStatus = false;
            this.validated = true;
            return this.validationStatus;
        }
        boolean validationStatus = true;
        if (Boolean.TRUE.equals(xmlValidateContext.getProperty("org.jcp.xml.dsig.validateManifests"))) {
            for (int n3 = 0, size2 = this.objects.size(); validationStatus && n3 < size2; ++n3) {
                final List content = this.objects.get(n3).getContent();
                for (int size3 = content.size(), n4 = 0; validationStatus && n4 < size3; ++n4) {
                    final XMLStructure xmlStructure = content.get(n4);
                    if (xmlStructure instanceof Manifest) {
                        if (DOMXMLSignature.log.isLoggable(Level.FINE)) {
                            DOMXMLSignature.log.log(Level.FINE, "validating manifest");
                        }
                        final List references2 = ((Manifest)xmlStructure).getReferences();
                        boolean validate2;
                        for (int size4 = references2.size(), n5 = 0; validationStatus && n5 < size4; validationStatus &= validate2, ++n5) {
                            final Reference reference2 = references2.get(n5);
                            validate2 = reference2.validate(xmlValidateContext);
                            if (DOMXMLSignature.log.isLoggable(Level.FINE)) {
                                DOMXMLSignature.log.log(Level.FINE, "Manifest ref[" + reference2.getURI() + "] is valid: " + validate2);
                            }
                        }
                    }
                }
            }
        }
        this.validationStatus = validationStatus;
        this.validated = true;
        return this.validationStatus;
    }
    
    public void sign(final XMLSignContext xmlSignContext) throws MarshalException, XMLSignatureException {
        if (xmlSignContext == null) {
            throw new NullPointerException("signContext cannot be null");
        }
        final DOMSignContext domSignContext = (DOMSignContext)xmlSignContext;
        if (domSignContext != null) {
            this.marshal(domSignContext.getParent(), domSignContext.getNextSibling(), DOMUtils.getSignaturePrefix(domSignContext), domSignContext);
        }
        final ArrayList list = new ArrayList(this.si.getReferences());
        (this.signatureIdMap = new HashMap()).put(this.id, this);
        this.signatureIdMap.put(this.si.getId(), this.si);
        final List references = this.si.getReferences();
        for (int i = 0; i < references.size(); ++i) {
            final Reference reference = references.get(i);
            this.signatureIdMap.put(reference.getId(), reference);
        }
        for (int j = 0; j < this.objects.size(); ++j) {
            final XMLObject xmlObject = this.objects.get(j);
            this.signatureIdMap.put(xmlObject.getId(), xmlObject);
            final List content = xmlObject.getContent();
            for (int k = 0; k < content.size(); ++k) {
                final XMLStructure xmlStructure = content.get(k);
                if (xmlStructure instanceof Manifest) {
                    final Manifest manifest = (Manifest)xmlStructure;
                    this.signatureIdMap.put(manifest.getId(), manifest);
                    final List references2 = manifest.getReferences();
                    for (int l = 0; l < references2.size(); ++l) {
                        final Reference reference2 = references2.get(l);
                        list.add(reference2);
                        this.signatureIdMap.put(reference2.getId(), reference2);
                    }
                }
            }
        }
        for (int n = 0; n < list.size(); ++n) {
            this.digestReference((DOMReference)list.get(n), xmlSignContext);
        }
        for (int n2 = 0; n2 < list.size(); ++n2) {
            final DOMReference domReference = (DOMReference)list.get(n2);
            if (!domReference.isDigested()) {
                domReference.digest(xmlSignContext);
            }
        }
        KeySelectorResult select;
        Key key;
        try {
            select = xmlSignContext.getKeySelector().select(this.ki, KeySelector.Purpose.SIGN, this.si.getSignatureMethod(), xmlSignContext);
            key = select.getKey();
            if (key == null) {
                throw new XMLSignatureException("the keySelector did not find a signing key");
            }
        }
        catch (final KeySelectorException ex) {
            throw new XMLSignatureException("cannot find signing key", ex);
        }
        byte[] sign;
        try {
            sign = ((DOMSignatureMethod)this.si.getSignatureMethod()).sign(key, (DOMSignedInfo)this.si, xmlSignContext);
        }
        catch (final InvalidKeyException ex2) {
            throw new XMLSignatureException(ex2);
        }
        if (DOMXMLSignature.log.isLoggable(Level.FINE)) {
            DOMXMLSignature.log.log(Level.FINE, "SignatureValue = " + sign);
        }
        ((DOMSignatureValue)this.sv).setValue(sign);
        this.localSigElem = this.sigElem;
        this.ksr = select;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XMLSignature)) {
            return false;
        }
        final XMLSignature xmlSignature = (XMLSignature)o;
        final boolean b = (this.id == null) ? (xmlSignature.getId() == null) : this.id.equals(xmlSignature.getId());
        final boolean b2 = (this.ki == null) ? (xmlSignature.getKeyInfo() == null) : this.ki.equals(xmlSignature.getKeyInfo());
        return b && b2 && this.sv.equals(xmlSignature.getSignatureValue()) && this.si.equals(xmlSignature.getSignedInfo()) && this.objects.equals(xmlSignature.getObjects());
    }
    
    public int hashCode() {
        return 54;
    }
    
    private void digestReference(final DOMReference domReference, final XMLSignContext xmlSignContext) throws XMLSignatureException {
        if (domReference.isDigested()) {
            return;
        }
        final String uri = domReference.getURI();
        if (Utils.sameDocumentURI(uri)) {
            final String idFromSameDocumentURI = Utils.parseIdFromSameDocumentURI(uri);
            if (idFromSameDocumentURI != null && this.signatureIdMap.containsKey(idFromSameDocumentURI)) {
                final Object value = this.signatureIdMap.get(idFromSameDocumentURI);
                if (value instanceof DOMReference) {
                    this.digestReference((DOMReference)value, xmlSignContext);
                }
                else if (value instanceof Manifest) {
                    final List references = ((Manifest)value).getReferences();
                    for (int i = 0; i < references.size(); ++i) {
                        this.digestReference((DOMReference)references.get(i), xmlSignContext);
                    }
                }
            }
            if (uri.length() == 0) {
                final List transforms = domReference.getTransforms();
                for (int j = 0; j < transforms.size(); ++j) {
                    final String algorithm = transforms.get(j).getAlgorithm();
                    if (algorithm.equals("http://www.w3.org/TR/1999/REC-xpath-19991116") || algorithm.equals("http://www.w3.org/2002/06/xmldsig-filter2")) {
                        return;
                    }
                }
            }
        }
        domReference.digest(xmlSignContext);
    }
    
    static {
        DOMXMLSignature.log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
        Init.init();
    }
    
    public class DOMSignatureValue extends DOMStructure implements SignatureValue
    {
        private String id;
        private byte[] value;
        private String valueBase64;
        private Element sigValueElem;
        private boolean validated;
        private boolean validationStatus;
        
        DOMSignatureValue(final String id) {
            this.validated = false;
            this.id = id;
        }
        
        DOMSignatureValue(final Element sigValueElem) throws MarshalException {
            this.validated = false;
            try {
                this.value = Base64.decode(sigValueElem);
            }
            catch (final Base64DecodingException ex) {
                throw new MarshalException(ex);
            }
            this.id = DOMUtils.getAttributeValue(sigValueElem, "Id");
            this.sigValueElem = sigValueElem;
        }
        
        public String getId() {
            return this.id;
        }
        
        public byte[] getValue() {
            return (byte[])((this.value == null) ? null : ((byte[])this.value.clone()));
        }
        
        public boolean validate(final XMLValidateContext xmlValidateContext) throws XMLSignatureException {
            if (xmlValidateContext == null) {
                throw new NullPointerException("context cannot be null");
            }
            if (this.validated) {
                return this.validationStatus;
            }
            final SignatureMethod signatureMethod = DOMXMLSignature.this.si.getSignatureMethod();
            KeySelectorResult select;
            Key key;
            try {
                select = xmlValidateContext.getKeySelector().select(DOMXMLSignature.this.ki, KeySelector.Purpose.VERIFY, signatureMethod, xmlValidateContext);
                key = select.getKey();
                if (key == null) {
                    throw new XMLSignatureException("the keyselector did not find a validation key");
                }
            }
            catch (final KeySelectorException ex) {
                throw new XMLSignatureException("cannot find validation key", ex);
            }
            try {
                this.validationStatus = ((DOMSignatureMethod)signatureMethod).verify(key, (DOMSignedInfo)DOMXMLSignature.this.si, this.value, xmlValidateContext);
            }
            catch (final Exception ex2) {
                throw new XMLSignatureException(ex2);
            }
            this.validated = true;
            DOMXMLSignature.this.ksr = select;
            return this.validationStatus;
        }
        
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SignatureValue)) {
                return false;
            }
            final SignatureValue signatureValue = (SignatureValue)o;
            return (this.id == null) ? (signatureValue.getId() == null) : this.id.equals(signatureValue.getId());
        }
        
        public int hashCode() {
            return 55;
        }
        
        public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
            this.sigValueElem = DOMUtils.createElement(DOMXMLSignature.this.ownerDoc, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#", s);
            if (this.valueBase64 != null) {
                this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
            }
            DOMUtils.setAttributeID(this.sigValueElem, "Id", this.id);
            node.appendChild(this.sigValueElem);
        }
        
        void setValue(final byte[] value) {
            this.value = value;
            this.valueBase64 = Base64.encode(value);
            this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
        }
    }
}
