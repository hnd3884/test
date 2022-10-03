package org.apache.xml.security.c14n.implementations;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.signature.XMLSignatureInput;
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.util.Collection;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.w3c.dom.Attr;
import java.util.Iterator;
import org.w3c.dom.Element;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.SortedSet;

public abstract class Canonicalizer20010315 extends CanonicalizerBase
{
    boolean firstCall;
    final SortedSet result;
    static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
    XmlAttrStack xmlattrStack;
    
    public Canonicalizer20010315(final boolean b) {
        super(b);
        this.firstCall = true;
        this.result = new TreeSet(CanonicalizerBase.COMPARE);
        this.xmlattrStack = new XmlAttrStack();
    }
    
    Iterator handleAttributesSubtree(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) throws CanonicalizationException {
        if (!element.hasAttributes() && !this.firstCall) {
            return null;
        }
        final SortedSet result = this.result;
        result.clear();
        final NamedNodeMap attributes = element.getAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            if ("http://www.w3.org/2000/xmlns/" != attr.getNamespaceURI()) {
                result.add(attr);
            }
            else {
                final String localName = attr.getLocalName();
                final String value = attr.getValue();
                if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(value)) {
                    final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, value, attr);
                    if (addMappingAndRender != null) {
                        result.add(addMappingAndRender);
                        if (C14nHelper.namespaceIsRelative(attr)) {
                            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                        }
                    }
                }
            }
        }
        if (this.firstCall) {
            nameSpaceSymbTable.getUnrenderedNodes(result);
            this.xmlattrStack.getXmlnsAttr(result);
            this.firstCall = false;
        }
        return result.iterator();
    }
    
    Iterator handleAttributes(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) throws CanonicalizationException {
        this.xmlattrStack.push(nameSpaceSymbTable.getLevel());
        final boolean b = this.isVisibleDO(element, nameSpaceSymbTable.getLevel()) == 1;
        NamedNodeMap attributes = null;
        int length = 0;
        if (element.hasAttributes()) {
            attributes = element.getAttributes();
            length = attributes.getLength();
        }
        final SortedSet result = this.result;
        result.clear();
        for (int i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            final String namespaceURI = attr.getNamespaceURI();
            if ("http://www.w3.org/2000/xmlns/" != namespaceURI) {
                if ("http://www.w3.org/XML/1998/namespace" == namespaceURI) {
                    this.xmlattrStack.addXmlnsAttr(attr);
                }
                else if (b) {
                    result.add(attr);
                }
            }
            else {
                final String localName = attr.getLocalName();
                final String value = attr.getValue();
                if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(value)) {
                    if (this.isVisible(attr)) {
                        if (b || !nameSpaceSymbTable.removeMappingIfRender(localName)) {
                            final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, value, attr);
                            if (addMappingAndRender != null) {
                                result.add(addMappingAndRender);
                                if (C14nHelper.namespaceIsRelative(attr)) {
                                    throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                                }
                            }
                        }
                    }
                    else if (b && localName != "xmlns") {
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
            Object o = null;
            if (attributeNodeNS == null) {
                o = nameSpaceSymbTable.getMapping("xmlns");
            }
            else if (!this.isVisible(attributeNodeNS)) {
                o = nameSpaceSymbTable.addMappingAndRender("xmlns", "", CanonicalizerBase.nullNode);
            }
            if (o != null) {
                result.add(o);
            }
            this.xmlattrStack.getXmlnsAttr(result);
            nameSpaceSymbTable.getUnrenderedNodes(result);
        }
        return result.iterator();
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final Set set, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    public byte[] engineCanonicalizeSubTree(final Node node, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    void circumventBugIfNeeded(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
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
    
    void handleParent(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
        if (!element.hasAttributes()) {
            return;
        }
        this.xmlattrStack.push(-1);
        final NamedNodeMap attributes = element.getAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            if ("http://www.w3.org/2000/xmlns/" != attr.getNamespaceURI()) {
                if ("http://www.w3.org/XML/1998/namespace" == attr.getNamespaceURI()) {
                    this.xmlattrStack.addXmlnsAttr(attr);
                }
            }
            else {
                final String localName = attr.getLocalName();
                final String nodeValue = attr.getNodeValue();
                if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(nodeValue)) {
                    nameSpaceSymbTable.addMapping(localName, nodeValue, attr);
                }
            }
        }
    }
    
    static class XmlAttrStack
    {
        int currentLevel;
        int lastlevel;
        XmlsStackElement cur;
        List levels;
        
        XmlAttrStack() {
            this.currentLevel = 0;
            this.lastlevel = 0;
            this.levels = new ArrayList();
        }
        
        void push(final int currentLevel) {
            this.currentLevel = currentLevel;
            if (this.currentLevel == -1) {
                return;
            }
            this.cur = null;
            while (this.lastlevel > this.currentLevel) {
                this.levels.remove(this.levels.size() - 1);
                if (this.levels.size() == 0) {
                    this.lastlevel = 0;
                    return;
                }
                this.lastlevel = this.levels.get(this.levels.size() - 1).level;
            }
        }
        
        void addXmlnsAttr(final Attr attr) {
            if (this.cur == null) {
                this.cur = new XmlsStackElement();
                this.cur.level = this.currentLevel;
                this.levels.add(this.cur);
                this.lastlevel = this.currentLevel;
            }
            this.cur.nodes.add(attr);
        }
        
        void getXmlnsAttr(final Collection collection) {
            int i = this.levels.size() - 1;
            if (this.cur == null) {
                this.cur = new XmlsStackElement();
                this.cur.level = this.currentLevel;
                this.lastlevel = this.currentLevel;
                this.levels.add(this.cur);
            }
            boolean b = false;
            if (i == -1) {
                b = true;
            }
            else {
                final XmlsStackElement xmlsStackElement = this.levels.get(i);
                if (xmlsStackElement.rendered && xmlsStackElement.level + 1 == this.currentLevel) {
                    b = true;
                }
            }
            if (b) {
                collection.addAll(this.cur.nodes);
                this.cur.rendered = true;
                return;
            }
            final HashMap hashMap = new HashMap();
            while (i >= 0) {
                final Iterator iterator = this.levels.get(i).nodes.iterator();
                while (iterator.hasNext()) {
                    final Attr attr = (Attr)iterator.next();
                    if (!hashMap.containsKey(attr.getName())) {
                        hashMap.put(attr.getName(), attr);
                    }
                }
                --i;
            }
            this.cur.rendered = true;
            collection.addAll(hashMap.values());
        }
        
        static class XmlsStackElement
        {
            int level;
            boolean rendered;
            List nodes;
            
            XmlsStackElement() {
                this.rendered = false;
                this.nodes = new ArrayList();
            }
        }
    }
}
