package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.Data;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.DigestMethod;
import java.util.List;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignatureFactory;

public final class DOMXMLSignatureFactory extends XMLSignatureFactory
{
    public XMLSignature newXMLSignature(final SignedInfo signedInfo, final KeyInfo keyInfo) {
        return new DOMXMLSignature(signedInfo, keyInfo, null, null, null);
    }
    
    public XMLSignature newXMLSignature(final SignedInfo signedInfo, final KeyInfo keyInfo, final List list, final String s, final String s2) {
        return new DOMXMLSignature(signedInfo, keyInfo, list, s, s2);
    }
    
    public Reference newReference(final String s, final DigestMethod digestMethod) {
        return this.newReference(s, digestMethod, null, null, null);
    }
    
    public Reference newReference(final String s, final DigestMethod digestMethod, final List list, final String s2, final String s3) {
        return new DOMReference(s, s2, digestMethod, list, s3, this.getProvider());
    }
    
    public Reference newReference(final String s, final DigestMethod digestMethod, final List list, final Data data, final List list2, final String s2, final String s3) {
        if (list == null) {
            throw new NullPointerException("appliedTransforms cannot be null");
        }
        if (list.isEmpty()) {
            throw new NullPointerException("appliedTransforms cannot be empty");
        }
        if (data == null) {
            throw new NullPointerException("result cannot be null");
        }
        return new DOMReference(s, s2, digestMethod, list, data, list2, s3, this.getProvider());
    }
    
    public Reference newReference(final String s, final DigestMethod digestMethod, final List list, final String s2, final String s3, final byte[] array) {
        if (array == null) {
            throw new NullPointerException("digestValue cannot be null");
        }
        return new DOMReference(s, s2, digestMethod, null, null, list, s3, array, this.getProvider());
    }
    
    public SignedInfo newSignedInfo(final CanonicalizationMethod canonicalizationMethod, final SignatureMethod signatureMethod, final List list) {
        return this.newSignedInfo(canonicalizationMethod, signatureMethod, list, null);
    }
    
    public SignedInfo newSignedInfo(final CanonicalizationMethod canonicalizationMethod, final SignatureMethod signatureMethod, final List list, final String s) {
        return new DOMSignedInfo(canonicalizationMethod, signatureMethod, list, s);
    }
    
    public XMLObject newXMLObject(final List list, final String s, final String s2, final String s3) {
        return new DOMXMLObject(list, s, s2, s3);
    }
    
    public Manifest newManifest(final List list) {
        return this.newManifest(list, null);
    }
    
    public Manifest newManifest(final List list, final String s) {
        return new DOMManifest(list, s);
    }
    
    public SignatureProperties newSignatureProperties(final List list, final String s) {
        return new DOMSignatureProperties(list, s);
    }
    
    public SignatureProperty newSignatureProperty(final List list, final String s, final String s2) {
        return new DOMSignatureProperty(list, s, s2);
    }
    
    public XMLSignature unmarshalXMLSignature(final XMLValidateContext xmlValidateContext) throws MarshalException {
        if (xmlValidateContext == null) {
            throw new NullPointerException("context cannot be null");
        }
        return this.unmarshal(((DOMValidateContext)xmlValidateContext).getNode(), xmlValidateContext);
    }
    
    public XMLSignature unmarshalXMLSignature(final XMLStructure xmlStructure) throws MarshalException {
        if (xmlStructure == null) {
            throw new NullPointerException("xmlStructure cannot be null");
        }
        return this.unmarshal(((DOMStructure)xmlStructure).getNode(), null);
    }
    
    private XMLSignature unmarshal(final Node node, final XMLValidateContext xmlValidateContext) throws MarshalException {
        node.normalize();
        Element documentElement;
        if (node.getNodeType() == 9) {
            documentElement = ((Document)node).getDocumentElement();
        }
        else {
            if (node.getNodeType() != 1) {
                throw new MarshalException("Signature element is not a proper Node");
            }
            documentElement = (Element)node;
        }
        final String localName = documentElement.getLocalName();
        if (localName == null) {
            throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware");
        }
        if (localName.equals("Signature")) {
            return new DOMXMLSignature(documentElement, xmlValidateContext, this.getProvider());
        }
        throw new MarshalException("invalid Signature tag: " + localName);
    }
    
    public boolean isFeatureSupported(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return false;
    }
    
    public DigestMethod newDigestMethod(final String s, final DigestMethodParameterSpec digestMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
            return DOMSHADigestMethod.SHA1(digestMethodParameterSpec);
        }
        if (s.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
            return DOMSHADigestMethod.SHA256(digestMethodParameterSpec);
        }
        if (s.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
            return DOMSHADigestMethod.SHA512(digestMethodParameterSpec);
        }
        throw new NoSuchAlgorithmException("unsupported algorithm");
    }
    
    public SignatureMethod newSignatureMethod(final String s, final SignatureMethodParameterSpec signatureMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
            return new DOMHMACSignatureMethod(signatureMethodParameterSpec);
        }
        if (s.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
            return new DOMRSASignatureMethod(signatureMethodParameterSpec);
        }
        if (s.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
            return new DOMDSASignatureMethod(signatureMethodParameterSpec);
        }
        throw new NoSuchAlgorithmException("unsupported algorithm");
    }
    
    public Transform newTransform(final String s, final TransformParameterSpec transformParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService transformService;
        try {
            transformService = TransformService.getInstance(s, "DOM");
        }
        catch (final NoSuchAlgorithmException ex) {
            transformService = TransformService.getInstance(s, "DOM", this.getProvider());
        }
        transformService.init(transformParameterSpec);
        return new DOMTransform(transformService);
    }
    
    public Transform newTransform(final String s, final XMLStructure xmlStructure) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService transformService;
        try {
            transformService = TransformService.getInstance(s, "DOM");
        }
        catch (final NoSuchAlgorithmException ex) {
            transformService = TransformService.getInstance(s, "DOM", this.getProvider());
        }
        if (xmlStructure == null) {
            transformService.init(null);
        }
        else {
            transformService.init(xmlStructure, null);
        }
        return new DOMTransform(transformService);
    }
    
    public CanonicalizationMethod newCanonicalizationMethod(final String s, final C14NMethodParameterSpec c14NMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService transformService;
        try {
            transformService = TransformService.getInstance(s, "DOM");
        }
        catch (final NoSuchAlgorithmException ex) {
            transformService = TransformService.getInstance(s, "DOM", this.getProvider());
        }
        transformService.init(c14NMethodParameterSpec);
        return new DOMCanonicalizationMethod(transformService);
    }
    
    public CanonicalizationMethod newCanonicalizationMethod(final String s, final XMLStructure xmlStructure) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        TransformService transformService;
        try {
            transformService = TransformService.getInstance(s, "DOM");
        }
        catch (final NoSuchAlgorithmException ex) {
            transformService = TransformService.getInstance(s, "DOM", this.getProvider());
        }
        if (xmlStructure == null) {
            transformService.init(null);
        }
        else {
            transformService.init(xmlStructure, null);
        }
        return new DOMCanonicalizationMethod(transformService);
    }
    
    public URIDereferencer getURIDereferencer() {
        return DOMURIDereferencer.INSTANCE;
    }
}
