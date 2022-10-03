package org.jcp.xml.dsig.internal.dom;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.NoSuchAlgorithmException;
import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.security.interfaces.DSAParams;
import java.security.KeyException;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

public final class DOMKeyValue extends DOMStructure implements KeyValue
{
    private KeyFactory rsakf;
    private KeyFactory dsakf;
    private PublicKey publicKey;
    private javax.xml.crypto.dom.DOMStructure externalPublicKey;
    private DOMCryptoBinary p;
    private DOMCryptoBinary q;
    private DOMCryptoBinary g;
    private DOMCryptoBinary y;
    private DOMCryptoBinary j;
    private DOMCryptoBinary seed;
    private DOMCryptoBinary pgen;
    private DOMCryptoBinary modulus;
    private DOMCryptoBinary exponent;
    
    public DOMKeyValue(final PublicKey publicKey) throws KeyException {
        if (publicKey == null) {
            throw new NullPointerException("key cannot be null");
        }
        this.publicKey = publicKey;
        if (publicKey instanceof DSAPublicKey) {
            final DSAPublicKey dsaPublicKey = (DSAPublicKey)publicKey;
            final DSAParams params = dsaPublicKey.getParams();
            this.p = new DOMCryptoBinary(params.getP());
            this.q = new DOMCryptoBinary(params.getQ());
            this.g = new DOMCryptoBinary(params.getG());
            this.y = new DOMCryptoBinary(dsaPublicKey.getY());
        }
        else {
            if (!(publicKey instanceof RSAPublicKey)) {
                throw new KeyException("unsupported key algorithm: " + publicKey.getAlgorithm());
            }
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
            this.exponent = new DOMCryptoBinary(rsaPublicKey.getPublicExponent());
            this.modulus = new DOMCryptoBinary(rsaPublicKey.getModulus());
        }
    }
    
    public DOMKeyValue(final Element element) throws MarshalException {
        final Element firstChildElement = DOMUtils.getFirstChildElement(element);
        if (firstChildElement.getLocalName().equals("DSAKeyValue")) {
            this.publicKey = this.unmarshalDSAKeyValue(firstChildElement);
        }
        else if (firstChildElement.getLocalName().equals("RSAKeyValue")) {
            this.publicKey = this.unmarshalRSAKeyValue(firstChildElement);
        }
        else {
            this.publicKey = null;
            this.externalPublicKey = new javax.xml.crypto.dom.DOMStructure(firstChildElement);
        }
    }
    
    public PublicKey getPublicKey() throws KeyException {
        if (this.publicKey == null) {
            throw new KeyException("can't convert KeyValue to PublicKey");
        }
        return this.publicKey;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(ownerDocument, "KeyValue", "http://www.w3.org/2000/09/xmldsig#", s);
        this.marshalPublicKey(element, ownerDocument, s, domCryptoContext);
        node.appendChild(element);
    }
    
    private void marshalPublicKey(final Node node, final Document document, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        if (this.publicKey != null) {
            if (this.publicKey instanceof DSAPublicKey) {
                this.marshalDSAPublicKey(node, document, s, domCryptoContext);
            }
            else {
                if (!(this.publicKey instanceof RSAPublicKey)) {
                    throw new MarshalException(this.publicKey.getAlgorithm() + " public key algorithm not supported");
                }
                this.marshalRSAPublicKey(node, document, s, domCryptoContext);
            }
        }
        else {
            node.appendChild(this.externalPublicKey.getNode());
        }
    }
    
    private void marshalDSAPublicKey(final Node node, final Document document, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(document, "DSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element2 = DOMUtils.createElement(document, "P", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element3 = DOMUtils.createElement(document, "Q", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element4 = DOMUtils.createElement(document, "G", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element5 = DOMUtils.createElement(document, "Y", "http://www.w3.org/2000/09/xmldsig#", s);
        this.p.marshal(element2, s, domCryptoContext);
        this.q.marshal(element3, s, domCryptoContext);
        this.g.marshal(element4, s, domCryptoContext);
        this.y.marshal(element5, s, domCryptoContext);
        element.appendChild(element2);
        element.appendChild(element3);
        element.appendChild(element4);
        element.appendChild(element5);
        node.appendChild(element);
    }
    
    private void marshalRSAPublicKey(final Node node, final Document document, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Element element = DOMUtils.createElement(document, "RSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element2 = DOMUtils.createElement(document, "Modulus", "http://www.w3.org/2000/09/xmldsig#", s);
        final Element element3 = DOMUtils.createElement(document, "Exponent", "http://www.w3.org/2000/09/xmldsig#", s);
        this.modulus.marshal(element2, s, domCryptoContext);
        this.exponent.marshal(element3, s, domCryptoContext);
        element.appendChild(element2);
        element.appendChild(element3);
        node.appendChild(element);
    }
    
    private DSAPublicKey unmarshalDSAKeyValue(final Element element) throws MarshalException {
        if (this.dsakf == null) {
            try {
                this.dsakf = KeyFactory.getInstance("DSA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new RuntimeException("unable to create DSA KeyFactory: " + ex.getMessage());
            }
        }
        Element element2 = DOMUtils.getFirstChildElement(element);
        if (element2.getLocalName().equals("P")) {
            this.p = new DOMCryptoBinary(element2.getFirstChild());
            final Element nextSiblingElement = DOMUtils.getNextSiblingElement(element2);
            this.q = new DOMCryptoBinary(nextSiblingElement.getFirstChild());
            element2 = DOMUtils.getNextSiblingElement(nextSiblingElement);
        }
        if (element2.getLocalName().equals("G")) {
            this.g = new DOMCryptoBinary(element2.getFirstChild());
            element2 = DOMUtils.getNextSiblingElement(element2);
        }
        this.y = new DOMCryptoBinary(element2.getFirstChild());
        Element element3 = DOMUtils.getNextSiblingElement(element2);
        if (element3 != null && element3.getLocalName().equals("J")) {
            this.j = new DOMCryptoBinary(element3.getFirstChild());
            element3 = DOMUtils.getNextSiblingElement(element3);
        }
        if (element3 != null) {
            this.seed = new DOMCryptoBinary(element3.getFirstChild());
            this.pgen = new DOMCryptoBinary(DOMUtils.getNextSiblingElement(element3).getFirstChild());
        }
        return (DSAPublicKey)this.generatePublicKey(this.dsakf, new DSAPublicKeySpec(this.y.getBigNum(), this.p.getBigNum(), this.q.getBigNum(), this.g.getBigNum()));
    }
    
    private RSAPublicKey unmarshalRSAKeyValue(final Element element) throws MarshalException {
        if (this.rsakf == null) {
            try {
                this.rsakf = KeyFactory.getInstance("RSA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new RuntimeException("unable to create RSA KeyFactory: " + ex.getMessage());
            }
        }
        final Element firstChildElement = DOMUtils.getFirstChildElement(element);
        this.modulus = new DOMCryptoBinary(firstChildElement.getFirstChild());
        this.exponent = new DOMCryptoBinary(DOMUtils.getNextSiblingElement(firstChildElement).getFirstChild());
        return (RSAPublicKey)this.generatePublicKey(this.rsakf, new RSAPublicKeySpec(this.modulus.getBigNum(), this.exponent.getBigNum()));
    }
    
    private PublicKey generatePublicKey(final KeyFactory keyFactory, final KeySpec keySpec) {
        try {
            return keyFactory.generatePublic(keySpec);
        }
        catch (final InvalidKeySpecException ex) {
            return null;
        }
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValue)) {
            return false;
        }
        try {
            final KeyValue keyValue = (KeyValue)o;
            if (this.publicKey == null) {
                if (keyValue.getPublicKey() != null) {
                    return false;
                }
            }
            else if (!this.publicKey.equals(keyValue.getPublicKey())) {
                return false;
            }
        }
        catch (final KeyException ex) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        return 45;
    }
}
