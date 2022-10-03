package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import org.w3c.dom.DOMException;
import java.io.OutputStream;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import org.w3c.dom.Attr;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Map;
import org.w3c.dom.Element;
import java.util.Set;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Node;
import java.util.SortedSet;

public abstract class Canonicalizer20010315Excl extends CanonicalizerBase
{
    private SortedSet<String> inclusiveNSSet;
    private boolean propagateDefaultNamespace;
    
    public Canonicalizer20010315Excl(final boolean b) {
        super(b);
        this.propagateDefaultNamespace = false;
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node) throws CanonicalizationException {
        return this.engineCanonicalizeSubTree(node, "", null);
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s) throws CanonicalizationException {
        return this.engineCanonicalizeSubTree(node, s, null);
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s, final boolean propagateDefaultNamespace) throws CanonicalizationException {
        this.propagateDefaultNamespace = propagateDefaultNamespace;
        return this.engineCanonicalizeSubTree(node, s, null);
    }
    
    public byte[] engineCanonicalizeSubTree(final Node node, final String s, final Node node2) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(s);
        return super.engineCanonicalizeSubTree(node, node2);
    }
    
    public byte[] engineCanonicalize(final XMLSignatureInput xmlSignatureInput, final String s) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(s);
        return super.engineCanonicalize(xmlSignatureInput);
    }
    
    @Override
    public byte[] engineCanonicalizeXPathNodeSet(final Set<Node> set, final String s) throws CanonicalizationException {
        this.inclusiveNSSet = InclusiveNamespaces.prefixStr2Set(s);
        return super.engineCanonicalizeXPathNodeSet(set);
    }
    
    protected void outputAttributesSubtree(final Element element, final NameSpaceSymbTable nameSpaceSymbTable, final Map<String, byte[]> map) throws CanonicalizationException, DOMException, IOException {
        final TreeSet set = new TreeSet((Comparator<? super E>)Canonicalizer20010315Excl.COMPARE);
        final TreeSet set2 = new TreeSet();
        if (this.inclusiveNSSet != null && !this.inclusiveNSSet.isEmpty()) {
            set2.addAll(this.inclusiveNSSet);
        }
        if (element.hasAttributes()) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String localName = attr.getLocalName();
                final String nodeValue = attr.getNodeValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
                    final String prefix = attr.getPrefix();
                    if (prefix != null && !prefix.equals("xml") && !prefix.equals("xmlns")) {
                        set2.add(prefix);
                    }
                    set.add(attr);
                }
                else if ((!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(nodeValue)) && nameSpaceSymbTable.addMapping(localName, nodeValue, attr) && C14nHelper.namespaceIsRelative(nodeValue)) {
                    throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { element.getTagName(), localName, attr.getNodeValue() });
                }
            }
        }
        if (this.propagateDefaultNamespace && nameSpaceSymbTable.getLevel() == 1 && this.inclusiveNSSet.contains("xmlns") && nameSpaceSymbTable.getMappingWithoutRendered("xmlns") == null) {
            nameSpaceSymbTable.removeMapping("xmlns");
            nameSpaceSymbTable.addMapping("xmlns", "", this.getNullNode(element.getOwnerDocument()));
        }
        String prefix2;
        if (element.getNamespaceURI() != null && element.getPrefix() != null && element.getPrefix().length() != 0) {
            prefix2 = element.getPrefix();
        }
        else {
            prefix2 = "xmlns";
        }
        set2.add(prefix2);
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            final Attr mapping = nameSpaceSymbTable.getMapping((String)iterator.next());
            if (mapping != null) {
                set.add(mapping);
            }
        }
        final OutputStream writer = this.getWriter();
        for (final Attr attr2 : set) {
            CanonicalizerBase.outputAttrToWriter(attr2.getNodeName(), attr2.getNodeValue(), writer, map);
        }
    }
    
    protected void outputAttributes(final Element element, final NameSpaceSymbTable nameSpaceSymbTable, final Map<String, byte[]> map) throws CanonicalizationException, DOMException, IOException {
        final TreeSet set = new TreeSet((Comparator<? super E>)Canonicalizer20010315Excl.COMPARE);
        Set set2 = null;
        final boolean b = this.isVisibleDO(element, nameSpaceSymbTable.getLevel()) == 1;
        if (b) {
            set2 = new TreeSet();
            if (this.inclusiveNSSet != null && !this.inclusiveNSSet.isEmpty()) {
                set2.addAll(this.inclusiveNSSet);
            }
        }
        if (element.hasAttributes()) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String localName = attr.getLocalName();
                final String nodeValue = attr.getNodeValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
                    if (this.isVisible(attr) && b) {
                        final String prefix = attr.getPrefix();
                        if (prefix != null && !prefix.equals("xml") && !prefix.equals("xmlns")) {
                            set2.add(prefix);
                        }
                        set.add(attr);
                    }
                }
                else if (b && !this.isVisible(attr) && !"xmlns".equals(localName)) {
                    nameSpaceSymbTable.removeMappingIfNotRender(localName);
                }
                else {
                    if (!b && this.isVisible(attr) && this.inclusiveNSSet.contains(localName) && !nameSpaceSymbTable.removeMappingIfRender(localName)) {
                        final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, nodeValue, attr);
                        if (addMappingAndRender != null) {
                            set.add(addMappingAndRender);
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
                nameSpaceSymbTable.addMapping("xmlns", "", this.getNullNode(attributeNodeNS.getOwnerDocument()));
            }
            String prefix2;
            if (element.getNamespaceURI() != null && element.getPrefix() != null && element.getPrefix().length() != 0) {
                prefix2 = element.getPrefix();
            }
            else {
                prefix2 = "xmlns";
            }
            set2.add(prefix2);
            final Iterator iterator = set2.iterator();
            while (iterator.hasNext()) {
                final Attr mapping = nameSpaceSymbTable.getMapping((String)iterator.next());
                if (mapping != null) {
                    set.add(mapping);
                }
            }
        }
        final OutputStream writer = this.getWriter();
        for (final Attr attr2 : set) {
            CanonicalizerBase.outputAttrToWriter(attr2.getNodeName(), attr2.getNodeValue(), writer, map);
        }
    }
    
    protected void circumventBugIfNeeded(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
        if (!xmlSignatureInput.isNeedsToBeExpanded() || this.inclusiveNSSet.isEmpty()) {
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
