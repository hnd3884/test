package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.net.URISyntaxException;
import java.net.URI;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import org.w3c.dom.NamedNodeMap;
import java.util.Collection;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import java.util.Iterator;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Node;
import java.util.Set;
import java.util.Comparator;
import java.util.TreeSet;
import org.w3c.dom.Attr;
import java.util.SortedSet;
import java.util.logging.Logger;

public abstract class Canonicalizer11 extends CanonicalizerBase
{
    private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    private static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
    private static Logger log;
    private final SortedSet<Attr> result;
    private boolean firstCall;
    private XmlAttrStack xmlattrStack;
    
    public Canonicalizer11(final boolean b) {
        super(b);
        this.result = new TreeSet<Attr>(Canonicalizer11.COMPARE);
        this.firstCall = true;
        this.xmlattrStack = new XmlAttrStack();
    }
    
    @Override
    public byte[] engineCanonicalizeXPathNodeSet(final Set<Node> set, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node, final String s) throws CanonicalizationException {
        throw new CanonicalizationException("c14n.Canonicalizer.UnsupportedOperation");
    }
    
    protected Iterator<Attr> handleAttributesSubtree(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) throws CanonicalizationException {
        if (!element.hasAttributes() && !this.firstCall) {
            return null;
        }
        final SortedSet<Attr> result = this.result;
        result.clear();
        if (element.hasAttributes()) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String namespaceURI = attr.getNamespaceURI();
                final String localName = attr.getLocalName();
                final String value = attr.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
                    result.add(attr);
                }
                else if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(value)) {
                    final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, value, attr);
                    if (addMappingAndRender != null) {
                        result.add((Attr)addMappingAndRender);
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
        return (Iterator<Attr>)result.iterator();
    }
    
    protected Iterator<Attr> handleAttributes(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) throws CanonicalizationException {
        this.xmlattrStack.push(nameSpaceSymbTable.getLevel());
        final boolean b = this.isVisibleDO(element, nameSpaceSymbTable.getLevel()) == 1;
        final SortedSet<Attr> result = this.result;
        result.clear();
        if (element.hasAttributes()) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Attr attr = (Attr)attributes.item(i);
                final String namespaceURI = attr.getNamespaceURI();
                final String localName = attr.getLocalName();
                final String value = attr.getValue();
                if (!"http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
                    if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
                        if (localName.equals("id")) {
                            if (b) {
                                result.add(attr);
                            }
                        }
                        else {
                            this.xmlattrStack.addXmlnsAttr(attr);
                        }
                    }
                    else if (b) {
                        result.add(attr);
                    }
                }
                else if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(value)) {
                    if (this.isVisible(attr)) {
                        if (b || !nameSpaceSymbTable.removeMappingIfRender(localName)) {
                            final Node addMappingAndRender = nameSpaceSymbTable.addMappingAndRender(localName, value, attr);
                            if (addMappingAndRender != null) {
                                result.add((Attr)addMappingAndRender);
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
                result.add((Attr)node);
            }
            this.xmlattrStack.getXmlnsAttr(result);
            nameSpaceSymbTable.getUnrenderedNodes(result);
        }
        return (Iterator<Attr>)result.iterator();
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
            else if (!"id".equals(localName) && "http://www.w3.org/XML/1998/namespace".equals(attr.getNamespaceURI())) {
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
    
    private static String joinURI(String string, final String s) throws URISyntaxException {
        String scheme = null;
        String authority = null;
        String path = "";
        String query = null;
        if (string != null) {
            if (string.endsWith("..")) {
                string += "/";
            }
            final URI uri = new URI(string);
            scheme = uri.getScheme();
            authority = uri.getAuthority();
            path = uri.getPath();
            query = uri.getQuery();
        }
        final URI uri2 = new URI(s);
        String scheme2 = uri2.getScheme();
        final String authority2 = uri2.getAuthority();
        final String path2 = uri2.getPath();
        final String query2 = uri2.getQuery();
        if (scheme2 != null && scheme2.equals(scheme)) {
            scheme2 = null;
        }
        String s2;
        String s3;
        String s4;
        String s5;
        if (scheme2 != null) {
            s2 = scheme2;
            s3 = authority2;
            s4 = removeDotSegments(path2);
            s5 = query2;
        }
        else {
            if (authority2 != null) {
                s3 = authority2;
                s4 = removeDotSegments(path2);
                s5 = query2;
            }
            else {
                if (path2.length() == 0) {
                    s4 = path;
                    if (query2 != null) {
                        s5 = query2;
                    }
                    else {
                        s5 = query;
                    }
                }
                else {
                    if (path2.startsWith("/")) {
                        s4 = removeDotSegments(path2);
                    }
                    else {
                        String s6;
                        if (authority != null && path.length() == 0) {
                            s6 = "/" + path2;
                        }
                        else {
                            final int lastIndex = path.lastIndexOf(47);
                            if (lastIndex == -1) {
                                s6 = path2;
                            }
                            else {
                                s6 = path.substring(0, lastIndex + 1) + path2;
                            }
                        }
                        s4 = removeDotSegments(s6);
                    }
                    s5 = query2;
                }
                s3 = authority;
            }
            s2 = scheme;
        }
        return new URI(s2, s3, s4, s5, null).toString();
    }
    
    private static String removeDotSegments(final String s) {
        if (Canonicalizer11.log.isLoggable(Level.FINE)) {
            Canonicalizer11.log.log(Level.FINE, "STEP   OUTPUT BUFFER\t\tINPUT BUFFER");
        }
        String s2;
        for (s2 = s; s2.indexOf("//") > -1; s2 = s2.replaceAll("//", "/")) {}
        StringBuilder sb = new StringBuilder();
        if (s2.charAt(0) == '/') {
            sb.append("/");
            s2 = s2.substring(1);
        }
        printStep("1 ", sb.toString(), s2);
        while (s2.length() != 0) {
            if (s2.startsWith("./")) {
                s2 = s2.substring(2);
                printStep("2A", sb.toString(), s2);
            }
            else if (s2.startsWith("../")) {
                s2 = s2.substring(3);
                if (!sb.toString().equals("/")) {
                    sb.append("../");
                }
                printStep("2A", sb.toString(), s2);
            }
            else if (s2.startsWith("/./")) {
                s2 = s2.substring(2);
                printStep("2B", sb.toString(), s2);
            }
            else if (s2.equals("/.")) {
                s2 = s2.replaceFirst("/.", "/");
                printStep("2B", sb.toString(), s2);
            }
            else if (s2.startsWith("/../")) {
                s2 = s2.substring(3);
                if (sb.length() == 0) {
                    sb.append("/");
                }
                else if (sb.toString().endsWith("../")) {
                    sb.append("..");
                }
                else if (sb.toString().endsWith("..")) {
                    sb.append("/..");
                }
                else {
                    final int lastIndex = sb.lastIndexOf("/");
                    if (lastIndex == -1) {
                        sb = new StringBuilder();
                        if (s2.charAt(0) == '/') {
                            s2 = s2.substring(1);
                        }
                    }
                    else {
                        sb = sb.delete(lastIndex, sb.length());
                    }
                }
                printStep("2C", sb.toString(), s2);
            }
            else if (s2.equals("/..")) {
                s2 = s2.replaceFirst("/..", "/");
                if (sb.length() == 0) {
                    sb.append("/");
                }
                else if (sb.toString().endsWith("../")) {
                    sb.append("..");
                }
                else if (sb.toString().endsWith("..")) {
                    sb.append("/..");
                }
                else {
                    final int lastIndex2 = sb.lastIndexOf("/");
                    if (lastIndex2 == -1) {
                        sb = new StringBuilder();
                        if (s2.charAt(0) == '/') {
                            s2 = s2.substring(1);
                        }
                    }
                    else {
                        sb = sb.delete(lastIndex2, sb.length());
                    }
                }
                printStep("2C", sb.toString(), s2);
            }
            else if (s2.equals(".")) {
                s2 = "";
                printStep("2D", sb.toString(), s2);
            }
            else if (s2.equals("..")) {
                if (!sb.toString().equals("/")) {
                    sb.append("..");
                }
                s2 = "";
                printStep("2D", sb.toString(), s2);
            }
            else {
                int index = s2.indexOf(47);
                int index2;
                if (index == 0) {
                    index2 = s2.indexOf(47, 1);
                }
                else {
                    index2 = index;
                    index = 0;
                }
                String s3;
                if (index2 == -1) {
                    s3 = s2.substring(index);
                    s2 = "";
                }
                else {
                    s3 = s2.substring(index, index2);
                    s2 = s2.substring(index2);
                }
                sb.append(s3);
                printStep("2E", sb.toString(), s2);
            }
        }
        if (sb.toString().endsWith("..")) {
            sb.append("/");
            printStep("3 ", sb.toString(), s2);
        }
        return sb.toString();
    }
    
    private static void printStep(final String s, final String s2, final String s3) {
        if (Canonicalizer11.log.isLoggable(Level.FINE)) {
            Canonicalizer11.log.log(Level.FINE, " " + s + ":   " + s2);
            if (s2.length() == 0) {
                Canonicalizer11.log.log(Level.FINE, "\t\t\t\t" + s3);
            }
            else {
                Canonicalizer11.log.log(Level.FINE, "\t\t\t" + s3);
            }
        }
    }
    
    static {
        Canonicalizer11.log = Logger.getLogger(Canonicalizer11.class.getName());
    }
    
    private static class XmlAttrStack
    {
        int currentLevel;
        int lastlevel;
        XmlsStackElement cur;
        List<XmlsStackElement> levels;
        
        private XmlAttrStack() {
            this.currentLevel = 0;
            this.lastlevel = 0;
            this.levels = new ArrayList<XmlsStackElement>();
        }
        
        void push(final int currentLevel) {
            this.currentLevel = currentLevel;
            if (this.currentLevel == -1) {
                return;
            }
            this.cur = null;
            while (this.lastlevel >= this.currentLevel) {
                this.levels.remove(this.levels.size() - 1);
                final int size = this.levels.size();
                if (size == 0) {
                    this.lastlevel = 0;
                    return;
                }
                this.lastlevel = this.levels.get(size - 1).level;
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
        
        void getXmlnsAttr(final Collection<Attr> collection) {
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
            final ArrayList list = new ArrayList();
            boolean b2 = true;
            while (i >= 0) {
                final XmlsStackElement xmlsStackElement2 = this.levels.get(i);
                if (xmlsStackElement2.rendered) {
                    b2 = false;
                }
                final Iterator<Attr> iterator = xmlsStackElement2.nodes.iterator();
                while (iterator.hasNext() && b2) {
                    final Attr attr = iterator.next();
                    if (attr.getLocalName().equals("base") && !xmlsStackElement2.rendered) {
                        list.add(attr);
                    }
                    else {
                        if (hashMap.containsKey(attr.getName())) {
                            continue;
                        }
                        hashMap.put(attr.getName(), attr);
                    }
                }
                --i;
            }
            if (!list.isEmpty()) {
                final Iterator<Attr> iterator2 = collection.iterator();
                String value = null;
                Attr attr2 = null;
                while (iterator2.hasNext()) {
                    final Attr attr3 = iterator2.next();
                    if (attr3.getLocalName().equals("base")) {
                        value = attr3.getValue();
                        attr2 = attr3;
                        break;
                    }
                }
                for (final Attr attr4 : list) {
                    if (value == null) {
                        value = attr4.getValue();
                        attr2 = attr4;
                    }
                    else {
                        try {
                            value = joinURI(attr4.getValue(), value);
                        }
                        catch (final URISyntaxException ex) {
                            if (!Canonicalizer11.log.isLoggable(Level.FINE)) {
                                continue;
                            }
                            Canonicalizer11.log.log(Level.FINE, ex.getMessage(), ex);
                        }
                    }
                }
                if (value != null && value.length() != 0) {
                    attr2.setValue(value);
                    collection.add(attr2);
                }
            }
            this.cur.rendered = true;
            collection.addAll(hashMap.values());
        }
        
        static class XmlsStackElement
        {
            int level;
            boolean rendered;
            List<Attr> nodes;
            
            XmlsStackElement() {
                this.rendered = false;
                this.nodes = new ArrayList<Attr>();
            }
        }
    }
}
