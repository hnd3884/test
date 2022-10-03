package org.apache.xml.security.c14n.implementations;

import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.XMLUtils;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.w3c.dom.Attr;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.w3c.dom.Node;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class Canonicalizer20010315Excl extends CanonicalizerBase
{
    TreeSet _inclusiveNSSet;
    static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    final SortedSet result;
    
    public Canonicalizer20010315Excl(final boolean b) {
        super(b);
        this._inclusiveNSSet = new TreeSet();
        this.result = new TreeSet(CanonicalizerBase.COMPARE);
    }
    
    public byte[] engineCanonicalizeSubTree(final Node node) throws CanonicalizationException {
        return this.engineCanonicalizeSubTree(node, "", null);
    }
    
    public byte[] engineCanonicalizeSubTree(final Node node, final String s) throws CanonicalizationException {
        return this.engineCanonicalizeSubTree(node, s, null);
    }
    
    public byte[] engineCanonicalizeSubTree(final Node node, final String s, final Node node2) throws CanonicalizationException {
        this._inclusiveNSSet = (TreeSet)InclusiveNamespaces.prefixStr2Set(s);
        return super.engineCanonicalizeSubTree(node, node2);
    }
    
    public byte[] engineCanonicalize(final XMLSignatureInput xmlSignatureInput, final String s) throws CanonicalizationException {
        this._inclusiveNSSet = (TreeSet)InclusiveNamespaces.prefixStr2Set(s);
        return super.engineCanonicalize(xmlSignatureInput);
    }
    
    Iterator handleAttributesSubtree(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) throws CanonicalizationException {
        final SortedSet result = this.result;
        result.clear();
        NamedNodeMap attributes = null;
        int length = 0;
        if (element.hasAttributes()) {
            attributes = element.getAttributes();
            length = attributes.getLength();
        }
        final SortedSet set = (SortedSet)this._inclusiveNSSet.clone();
        for (int i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            if ("http://www.w3.org/2000/xmlns/" != attr.getNamespaceURI()) {
                final String prefix = attr.getPrefix();
                if (prefix != null && !prefix.equals("xml") && !prefix.equals("xmlns")) {
                    set.add(prefix);
                }
                result.add(attr);
            }
            else {
                final String localName = attr.getLocalName();
                final String nodeValue = attr.getNodeValue();
                if (nameSpaceSymbTable.addMapping(localName, nodeValue, attr) && C14nHelper.namespaceIsRelative(nodeValue)) {
                    throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                }
            }
        }
        String prefix2;
        if (element.getNamespaceURI() != null) {
            prefix2 = element.getPrefix();
            if (prefix2 == null || prefix2.length() == 0) {
                prefix2 = "xmlns";
            }
        }
        else {
            prefix2 = "xmlns";
        }
        set.add(prefix2);
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final Attr mapping = nameSpaceSymbTable.getMapping((String)iterator.next());
            if (mapping == null) {
                continue;
            }
            result.add(mapping);
        }
        return result.iterator();
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final Set set, final String s) throws CanonicalizationException {
        this._inclusiveNSSet = (TreeSet)InclusiveNamespaces.prefixStr2Set(s);
        return super.engineCanonicalizeXPathNodeSet(set);
    }
    
    final Iterator handleAttributes(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) throws CanonicalizationException {
        final SortedSet result = this.result;
        result.clear();
        NamedNodeMap attributes = null;
        int length = 0;
        if (element.hasAttributes()) {
            attributes = element.getAttributes();
            length = attributes.getLength();
        }
        Set<String> set = null;
        final boolean b = this.isVisibleDO(element, nameSpaceSymbTable.getLevel()) == 1;
        if (b) {
            set = (Set)this._inclusiveNSSet.clone();
        }
        for (int i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            if ("http://www.w3.org/2000/xmlns/" != attr.getNamespaceURI()) {
                if (this.isVisible(attr)) {
                    if (b) {
                        final String prefix = attr.getPrefix();
                        if (prefix != null && !prefix.equals("xml") && !prefix.equals("xmlns")) {
                            set.add(prefix);
                        }
                        result.add(attr);
                    }
                }
            }
            else {
                final String localName = attr.getLocalName();
                if (b && !this.isVisible(attr) && localName != "xmlns") {
                    nameSpaceSymbTable.removeMappingIfNotRender(localName);
                }
                else {
                    final String nodeValue = attr.getNodeValue();
                    if (!b && this.isVisible(attr) && this._inclusiveNSSet.contains(localName) && !nameSpaceSymbTable.removeMappingIfRender(localName)) {
                        final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, nodeValue, attr);
                        if (addMappingAndRender != null) {
                            result.add(addMappingAndRender);
                            if (C14nHelper.namespaceIsRelative(attr)) {
                                throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                            }
                        }
                    }
                    if (nameSpaceSymbTable.addMapping(localName, nodeValue, attr) && C14nHelper.namespaceIsRelative(nodeValue)) {
                        throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                    }
                }
            }
        }
        if (b) {
            final Attr attributeNodeNS = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
            if (attributeNodeNS != null && !this.isVisible(attributeNodeNS)) {
                nameSpaceSymbTable.addMapping("xmlns", "", CanonicalizerBase.nullNode);
            }
            if (element.getNamespaceURI() != null) {
                final String prefix2 = element.getPrefix();
                if (prefix2 == null || prefix2.length() == 0) {
                    set.add("xmlns");
                }
                else {
                    set.add(prefix2);
                }
            }
            else {
                set.add("xmlns");
            }
            final Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                final Attr mapping = nameSpaceSymbTable.getMapping(iterator.next());
                if (mapping == null) {
                    continue;
                }
                result.add(mapping);
            }
        }
        return result.iterator();
    }
    
    void circumventBugIfNeeded(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        if (!xmlSignatureInput.isNeedsToBeExpanded() || this._inclusiveNSSet.isEmpty()) {
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
}
