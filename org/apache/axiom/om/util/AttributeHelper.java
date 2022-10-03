package org.apache.axiom.om.util;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAttribute;

public class AttributeHelper
{
    public static void importOMAttribute(final OMAttribute omAttribute, final OMElement omElement) {
        if (omAttribute.getOMFactory().getMetaFactory() == omElement.getOMFactory().getMetaFactory()) {
            omElement.addAttribute(omAttribute);
        }
        else {
            final OMNamespace ns = omAttribute.getNamespace();
            omElement.addAttribute(omAttribute.getLocalName(), omAttribute.getAttributeValue(), omElement.getOMFactory().createOMNamespace(ns.getNamespaceURI(), ns.getPrefix()));
        }
    }
}
