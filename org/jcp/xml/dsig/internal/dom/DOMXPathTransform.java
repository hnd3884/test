package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import java.util.Map;
import org.w3c.dom.Attr;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMXPathTransform extends ApacheTransform
{
    public void init(final TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("params are required");
        }
        if (!(params instanceof XPathFilterParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type XPathFilterParameterSpec");
        }
        super.params = params;
    }
    
    public void init(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws InvalidAlgorithmParameterException {
        super.init(xmlStructure, xmlCryptoContext);
        this.unmarshalParams(DOMUtils.getFirstChildElement(super.transformElem));
    }
    
    private void unmarshalParams(final Element element) {
        final String nodeValue = element.getFirstChild().getNodeValue();
        final NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            final int length = attributes.getLength();
            final HashMap hashMap = new HashMap(length);
            for (int i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String prefix = attr.getPrefix();
                if (prefix != null && prefix.equals("xmlns")) {
                    hashMap.put((Object)attr.getLocalName(), (Object)attr.getValue());
                }
            }
            super.params = new XPathFilterParameterSpec(nodeValue, hashMap);
        }
        else {
            super.params = new XPathFilterParameterSpec(nodeValue);
        }
    }
    
    public void marshalParams(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws MarshalException {
        super.marshalParams(xmlStructure, xmlCryptoContext);
        final XPathFilterParameterSpec xPathFilterParameterSpec = (XPathFilterParameterSpec)this.getParameterSpec();
        final Element element = DOMUtils.createElement(super.ownerDoc, "XPath", "http://www.w3.org/2000/09/xmldsig#", DOMUtils.getSignaturePrefix(xmlCryptoContext));
        element.appendChild(super.ownerDoc.createTextNode(xPathFilterParameterSpec.getXPath()));
        final Iterator iterator = xPathFilterParameterSpec.getNamespaceMap().entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + (String)entry.getKey(), (String)entry.getValue());
        }
        super.transformElem.appendChild(element);
    }
}
