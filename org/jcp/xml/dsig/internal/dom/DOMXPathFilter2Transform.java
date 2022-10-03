package org.jcp.xml.dsig.internal.dom;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import java.util.HashMap;
import javax.xml.crypto.dsig.spec.XPathType;
import java.util.ArrayList;
import org.w3c.dom.Element;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Node;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMXPathFilter2Transform extends ApacheTransform
{
    public void init(final TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("params are required");
        }
        if (!(params instanceof XPathFilter2ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type XPathFilter2ParameterSpec");
        }
        super.params = params;
    }
    
    public void init(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws InvalidAlgorithmParameterException {
        super.init(xmlStructure, xmlCryptoContext);
        try {
            this.unmarshalParams(DOMUtils.getFirstChildElement(super.transformElem));
        }
        catch (final MarshalException ex) {
            throw (InvalidAlgorithmParameterException)new InvalidAlgorithmParameterException().initCause(ex);
        }
    }
    
    private void unmarshalParams(Element nextSiblingElement) throws MarshalException {
        final ArrayList list = new ArrayList();
        while (nextSiblingElement != null) {
            final String nodeValue = nextSiblingElement.getFirstChild().getNodeValue();
            final String attributeValue = DOMUtils.getAttributeValue(nextSiblingElement, "Filter");
            if (attributeValue == null) {
                throw new MarshalException("filter cannot be null");
            }
            XPathType.Filter filter;
            if (attributeValue.equals("intersect")) {
                filter = XPathType.Filter.INTERSECT;
            }
            else if (attributeValue.equals("subtract")) {
                filter = XPathType.Filter.SUBTRACT;
            }
            else {
                if (!attributeValue.equals("union")) {
                    throw new MarshalException("Unknown XPathType filter type" + attributeValue);
                }
                filter = XPathType.Filter.UNION;
            }
            final NamedNodeMap attributes = nextSiblingElement.getAttributes();
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
                list.add(new XPathType(nodeValue, filter, hashMap));
            }
            else {
                list.add(new XPathType(nodeValue, filter));
            }
            nextSiblingElement = DOMUtils.getNextSiblingElement(nextSiblingElement);
        }
        super.params = new XPathFilter2ParameterSpec(list);
    }
    
    public void marshalParams(final XMLStructure xmlStructure, final XMLCryptoContext xmlCryptoContext) throws MarshalException {
        super.marshalParams(xmlStructure, xmlCryptoContext);
        final XPathFilter2ParameterSpec xPathFilter2ParameterSpec = (XPathFilter2ParameterSpec)this.getParameterSpec();
        final String nsPrefix = DOMUtils.getNSPrefix(xmlCryptoContext, "http://www.w3.org/2002/06/xmldsig-filter2");
        final String s = (nsPrefix == null) ? "xmlns" : ("xmlns:" + nsPrefix);
        final List xPathList = xPathFilter2ParameterSpec.getXPathList();
        for (int i = 0; i < xPathList.size(); ++i) {
            final XPathType xPathType = xPathList.get(i);
            final Element element = DOMUtils.createElement(super.ownerDoc, "XPath", "http://www.w3.org/2002/06/xmldsig-filter2", nsPrefix);
            element.appendChild(super.ownerDoc.createTextNode(xPathType.getExpression()));
            DOMUtils.setAttribute(element, "Filter", xPathType.getFilter().toString());
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", s, "http://www.w3.org/2002/06/xmldsig-filter2");
            final Iterator iterator = xPathType.getNamespaceMap().entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + (String)entry.getKey(), (String)entry.getValue());
            }
            super.transformElem.appendChild(element);
        }
    }
}
