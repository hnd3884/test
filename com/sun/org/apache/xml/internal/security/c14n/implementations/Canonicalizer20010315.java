package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import org.w3c.dom.DOMException;
import java.util.Iterator;
import java.io.OutputStream;
import org.w3c.dom.NamedNodeMap;
import java.util.Collection;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import org.w3c.dom.Attr;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Map;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Node;
import java.util.Set;

public abstract class Canonicalizer20010315 extends CanonicalizerBase
{
    private boolean firstCall;
    private final XmlAttrStack xmlattrStack;
    private final boolean c14n11;
    
    public Canonicalizer20010315(final boolean b) {
        this(b, false);
    }
    
    public Canonicalizer20010315(final boolean b, final boolean c14n11) {
        super(b);
        this.firstCall = true;
        this.xmlattrStack = new XmlAttrStack(c14n11);
        this.c14n11 = c14n11;
    }
    
    @Override
    public byte[] engineCanonicalizeXPathNodeSet(final Set<Node> set, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s, final boolean b) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    protected void outputAttributesSubtree(final Element element, final NameSpaceSymbTable nameSpaceSymbTable, final Map<String, byte[]> map) throws CanonicalizationException, DOMException, IOException {
        if (!element.hasAttributes() && !this.firstCall) {
            return;
        }
        final TreeSet set = new TreeSet((Comparator<? super E>)Canonicalizer20010315.COMPARE);
        if (element.hasAttributes()) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String namespaceURI = attr.getNamespaceURI();
                final String localName = attr.getLocalName();
                final String value = attr.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
                    set.add(attr);
                }
                else if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(value)) {
                    final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, value, attr);
                    if (addMappingAndRender != null) {
                        set.add(addMappingAndRender);
                        if (C14nHelper.namespaceIsRelative(attr)) {
                            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                        }
                    }
                }
            }
        }
        if (this.firstCall) {
            nameSpaceSymbTable.getUnrenderedNodes(set);
            this.xmlattrStack.getXmlnsAttr(set);
            this.firstCall = false;
        }
        final OutputStream writer = this.getWriter();
        for (final Attr attr2 : set) {
            CanonicalizerBase.outputAttrToWriter(attr2.getNodeName(), attr2.getNodeValue(), writer, map);
        }
    }
    
    protected void outputAttributes(final Element element, final NameSpaceSymbTable nameSpaceSymbTable, final Map<String, byte[]> map) throws CanonicalizationException, DOMException, IOException {
        this.xmlattrStack.push(nameSpaceSymbTable.getLevel());
        final boolean b = this.isVisibleDO(element, nameSpaceSymbTable.getLevel()) == 1;
        final TreeSet set = new TreeSet((Comparator<? super E>)Canonicalizer20010315.COMPARE);
        if (element.hasAttributes()) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String namespaceURI = attr.getNamespaceURI();
                final String localName = attr.getLocalName();
                final String value = attr.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
                    if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
                        if (this.c14n11 && "id".equals(localName)) {
                            if (b) {
                                set.add(attr);
                            }
                        }
                        else {
                            this.xmlattrStack.addXmlnsAttr(attr);
                        }
                    }
                    else if (b) {
                        set.add(attr);
                    }
                }
                else if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(value)) {
                    if (this.isVisible(attr)) {
                        if (b || !nameSpaceSymbTable.removeMappingIfRender(localName)) {
                            final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, value, attr);
                            if (addMappingAndRender != null) {
                                set.add(addMappingAndRender);
                                if (C14nHelper.namespaceIsRelative(attr)) {
                                    throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                                }
                            }
                        }
                    }
                    else if (b && !"xmlns".equals(localName)) {
                        nameSpaceSymbTable.removeMapping(localName);
                    }
                    else {
                        nameSpaceSymbTable.addMapping(localName, value, attr);
                    }
                }
            }
        }
        if (b) {
            final Attr attributeNodeNS = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
            Node node = null;
            if (attributeNodeNS == null) {
                node = nameSpaceSymbTable.getMapping("xmlns");
            }
            else if (!this.isVisible(attributeNodeNS)) {
                node = nameSpaceSymbTable.addMappingAndRender("xmlns", "", this.getNullNode(attributeNodeNS.getOwnerDocument()));
            }
            if (node != null) {
                set.add(node);
            }
            this.xmlattrStack.getXmlnsAttr(set);
            nameSpaceSymbTable.getUnrenderedNodes(set);
        }
        final OutputStream writer = this.getWriter();
        for (final Attr attr2 : set) {
            CanonicalizerBase.outputAttrToWriter(attr2.getNodeName(), attr2.getNodeValue(), writer, map);
        }
    }
    
    protected void circumventBugIfNeeded(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        if (!xmlSignatureInput.isNeedsToBeExpanded()) {
            return;
        }
        Document document;
        if (xmlSignatureInput.getSubNode() != null) {
            document = XMLUtils.getOwnerDocument(xmlSignatureInput.getSubNode());
        }
        else {
            document = XMLUtils.getOwnerDocument(xmlSignatureInput.getNodeSet());
        }
        XMLUtils.circumventBug2650(document);
    }
    
    @Override
    protected void handleParent(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
        if (!element.hasAttributes() && element.getNamespaceURI() == null) {
            return;
        }
        this.xmlattrStack.push(-1);
        final NamedNodeMap attributes = element.getAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            final String localName = attr.getLocalName();
            final String nodeValue = attr.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
                if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(nodeValue)) {
                    nameSpaceSymbTable.addMapping(localName, nodeValue, attr);
                }
            }
            else if ("http://www.w3.org/XML/1998/namespace".equals(attr.getNamespaceURI()) && (!this.c14n11 || !"id".equals(localName))) {
                this.xmlattrStack.addXmlnsAttr(attr);
            }
        }
        if (element.getNamespaceURI() != null) {
            String prefix = element.getPrefix();
            final String namespaceURI = element.getNamespaceURI();
            String string;
            if (prefix == null || prefix.equals("")) {
                prefix = "xmlns";
                string = "xmlns";
            }
            else {
                string = "xmlns:" + prefix;
            }
            final Attr attributeNS = element.getOwnerDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", string);
            attributeNS.setValue(namespaceURI);
            nameSpaceSymbTable.addMapping(prefix, namespaceURI, attributeNS);
        }
    }
}
