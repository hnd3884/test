package org.jcp.xml.dsig.internal.dom;

import org.w3c.dom.Node;
import javax.xml.crypto.XMLCryptoContext;
import org.w3c.dom.Element;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import java.math.BigInteger;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import java.security.KeyException;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.security.PublicKey;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;

public final class DOMKeyInfoFactory extends KeyInfoFactory
{
    public KeyInfo newKeyInfo(final List list) {
        return this.newKeyInfo(list, null);
    }
    
    public KeyInfo newKeyInfo(final List list, final String s) {
        return new DOMKeyInfo(list, s);
    }
    
    public KeyName newKeyName(final String s) {
        return new DOMKeyName(s);
    }
    
    public KeyValue newKeyValue(final PublicKey publicKey) throws KeyException {
        return new DOMKeyValue(publicKey);
    }
    
    public PGPData newPGPData(final byte[] array) {
        return this.newPGPData(array, null, null);
    }
    
    public PGPData newPGPData(final byte[] array, final byte[] array2, final List list) {
        return new DOMPGPData(array, array2, list);
    }
    
    public PGPData newPGPData(final byte[] array, final List list) {
        return new DOMPGPData(array, list);
    }
    
    public RetrievalMethod newRetrievalMethod(final String s) {
        return this.newRetrievalMethod(s, null, null);
    }
    
    public RetrievalMethod newRetrievalMethod(final String s, final String s2, final List list) {
        if (s == null) {
            throw new NullPointerException("uri must not be null");
        }
        return new DOMRetrievalMethod(s, s2, list);
    }
    
    public X509Data newX509Data(final List list) {
        return new DOMX509Data(list);
    }
    
    public X509IssuerSerial newX509IssuerSerial(final String s, final BigInteger bigInteger) {
        return new DOMX509IssuerSerial(s, bigInteger);
    }
    
    public boolean isFeatureSupported(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return false;
    }
    
    public URIDereferencer getURIDereferencer() {
        return DOMURIDereferencer.INSTANCE;
    }
    
    public KeyInfo unmarshalKeyInfo(final XMLStructure xmlStructure) throws MarshalException {
        if (xmlStructure == null) {
            throw new NullPointerException("xmlStructure cannot be null");
        }
        final Node node = ((DOMStructure)xmlStructure).getNode();
        node.normalize();
        Element documentElement;
        if (node.getNodeType() == 9) {
            documentElement = ((Document)node).getDocumentElement();
        }
        else {
            if (node.getNodeType() != 1) {
                throw new MarshalException("xmlStructure does not contain a proper Node");
            }
            documentElement = (Element)node;
        }
        final String localName = documentElement.getLocalName();
        if (localName == null) {
            throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware");
        }
        if (localName.equals("KeyInfo")) {
            return new DOMKeyInfo(documentElement, null, this.getProvider());
        }
        throw new MarshalException("invalid KeyInfo tag: " + localName);
    }
}
