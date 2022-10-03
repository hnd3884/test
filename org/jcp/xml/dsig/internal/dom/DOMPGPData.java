package org.jcp.xml.dsig.internal.dom;

import org.w3c.dom.Document;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.NodeList;
import org.apache.xml.security.exceptions.Base64DecodingException;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Element;
import javax.xml.crypto.XMLStructure;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.PGPData;

public final class DOMPGPData extends DOMStructure implements PGPData
{
    private final byte[] keyId;
    private final byte[] keyPacket;
    private final List externalElements;
    
    public DOMPGPData(final byte[] array, final List list) {
        if (array == null) {
            throw new NullPointerException("keyPacket cannot be null");
        }
        if (list == null || list.isEmpty()) {
            this.externalElements = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list2 = new ArrayList(list);
            for (int i = 0; i < list2.size(); ++i) {
                if (!(list2.get(i) instanceof XMLStructure)) {
                    throw new ClassCastException("other[" + i + "] is not a valid PGPData type");
                }
            }
            this.externalElements = Collections.unmodifiableList((List<?>)list2);
        }
        this.keyPacket = array.clone();
        this.checkKeyPacket(array);
        this.keyId = null;
    }
    
    public DOMPGPData(final byte[] array, final byte[] array2, final List list) {
        if (array == null) {
            throw new NullPointerException("keyId cannot be null");
        }
        if (array.length != 8) {
            throw new IllegalArgumentException("keyId must be 8 bytes long");
        }
        if (list == null || list.isEmpty()) {
            this.externalElements = Collections.EMPTY_LIST;
        }
        else {
            final ArrayList list2 = new ArrayList(list);
            for (int i = 0; i < list2.size(); ++i) {
                if (!(list2.get(i) instanceof XMLStructure)) {
                    throw new ClassCastException("other[" + i + "] is not a valid PGPData type");
                }
            }
            this.externalElements = Collections.unmodifiableList((List<?>)list2);
        }
        this.keyId = array.clone();
        this.keyPacket = (byte[])((array2 == null) ? null : ((byte[])array2.clone()));
        if (array2 != null) {
            this.checkKeyPacket(array2);
        }
    }
    
    public DOMPGPData(final Element element) throws MarshalException {
        byte[] decode = null;
        byte[] decode2 = null;
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        final ArrayList list = new ArrayList(length);
        for (int i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                final Element element2 = (Element)item;
                final String localName = element2.getLocalName();
                try {
                    if (localName.equals("PGPKeyID")) {
                        decode = Base64.decode(element2);
                    }
                    else if (localName.equals("PGPKeyPacket")) {
                        decode2 = Base64.decode(element2);
                    }
                    else {
                        list.add((Object)new javax.xml.crypto.dom.DOMStructure(element2));
                    }
                }
                catch (final Base64DecodingException ex) {
                    throw new MarshalException(ex);
                }
            }
        }
        this.keyId = decode;
        this.keyPacket = decode2;
        this.externalElements = Collections.unmodifiableList((List<?>)list);
    }
    
    public byte[] getKeyId() {
        return (byte[])((this.keyId == null) ? null : ((byte[])this.keyId.clone()));
    }
    
    public byte[] getKeyPacket() {
        return (byte[])((this.keyPacket == null) ? null : ((byte[])this.keyPacket.clone()));
    }
    
    public List getExternalElements() {
        return this.externalElements;
    }
    
    public void marshal(final Node node, final String s, final DOMCryptoContext domCryptoContext) throws MarshalException {
        final Document ownerDocument = DOMUtils.getOwnerDocument(node);
        final Element element = DOMUtils.createElement(ownerDocument, "PGPData", "http://www.w3.org/2000/09/xmldsig#", s);
        if (this.keyId != null) {
            final Element element2 = DOMUtils.createElement(ownerDocument, "PGPKeyID", "http://www.w3.org/2000/09/xmldsig#", s);
            element2.appendChild(ownerDocument.createTextNode(Base64.encode(this.keyId)));
            element.appendChild(element2);
        }
        if (this.keyPacket != null) {
            final Element element3 = DOMUtils.createElement(ownerDocument, "PGPKeyPacket", "http://www.w3.org/2000/09/xmldsig#", s);
            element3.appendChild(ownerDocument.createTextNode(Base64.encode(this.keyPacket)));
            element.appendChild(element3);
        }
        for (int i = 0; i < this.externalElements.size(); ++i) {
            DOMUtils.appendChild(element, ((javax.xml.crypto.dom.DOMStructure)this.externalElements.get(i)).getNode());
        }
        node.appendChild(element);
    }
    
    private void checkKeyPacket(final byte[] array) {
        if (array.length < 3) {
            throw new IllegalArgumentException("keypacket must be at least 3 bytes long");
        }
        final byte b = array[0];
        if ((b & 0x80) != 0x80) {
            throw new IllegalArgumentException("keypacket tag is invalid: bit 7 is not set");
        }
        if ((b & 0x40) != 0x40) {
            throw new IllegalArgumentException("old keypacket tag format is unsupported");
        }
        if ((b & 0x6) != 0x6 && (b & 0xE) != 0xE && (b & 0x5) != 0x5 && (b & 0x7) != 0x7) {
            throw new IllegalArgumentException("keypacket tag is invalid: must be 6, 14, 5, or 7");
        }
    }
}
