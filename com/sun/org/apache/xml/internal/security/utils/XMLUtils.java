package com.sun.org.apache.xml.internal.security.utils;

import java.util.Collections;
import java.util.WeakHashMap;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ArrayBlockingQueue;
import java.math.BigInteger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashSet;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import org.w3c.dom.NamedNodeMap;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import java.util.Queue;
import java.util.Map;
import java.util.Base64;
import com.sun.org.slf4j.internal.Logger;

public final class XMLUtils
{
    private static boolean lineFeedOnly;
    private static boolean ignoreLineBreaks;
    private static int parserPoolSize;
    private static volatile String dsPrefix;
    private static volatile String ds11Prefix;
    private static volatile String xencPrefix;
    private static volatile String xenc11Prefix;
    private static final Logger LOG;
    private static final Base64.Encoder LF_ENCODER;
    private static final Map<ClassLoader, Queue<DocumentBuilder>> DOCUMENT_BUILDERS;
    private static final Map<ClassLoader, Queue<DocumentBuilder>> DOCUMENT_BUILDERS_DISALLOW_DOCTYPE;
    
    private XMLUtils() {
    }
    
    public static void setDsPrefix(final String dsPrefix) {
        JavaUtils.checkRegisterPermission();
        XMLUtils.dsPrefix = dsPrefix;
    }
    
    public static void setDs11Prefix(final String ds11Prefix) {
        JavaUtils.checkRegisterPermission();
        XMLUtils.ds11Prefix = ds11Prefix;
    }
    
    public static void setXencPrefix(final String xencPrefix) {
        JavaUtils.checkRegisterPermission();
        XMLUtils.xencPrefix = xencPrefix;
    }
    
    public static void setXenc11Prefix(final String xenc11Prefix) {
        JavaUtils.checkRegisterPermission();
        XMLUtils.xenc11Prefix = xenc11Prefix;
    }
    
    public static Element getNextElement(final Node node) {
        Node nextSibling;
        for (nextSibling = node; nextSibling != null && nextSibling.getNodeType() != 1; nextSibling = nextSibling.getNextSibling()) {}
        return (Element)nextSibling;
    }
    
    public static void getSet(final Node node, final Set<Node> set, final Node node2, final boolean b) {
        if (node2 != null && isDescendantOrSelf(node2, node)) {
            return;
        }
        getSetRec(node, set, node2, b);
    }
    
    private static void getSetRec(final Node node, final Set<Node> set, final Node node2, final boolean b) {
        if (node == node2) {
            return;
        }
        switch (node.getNodeType()) {
            case 1: {
                set.add(node);
                final Element element = (Element)node;
                if (element.hasAttributes()) {
                    final NamedNodeMap attributes = element.getAttributes();
                    for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                        set.add(attributes.item(i));
                    }
                }
            }
            case 9: {
                for (Node node3 = node.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    if (node3.getNodeType() == 3) {
                        set.add(node3);
                        while (node3 != null && node3.getNodeType() == 3) {
                            node3 = node3.getNextSibling();
                        }
                        if (node3 == null) {
                            return;
                        }
                    }
                    getSetRec(node3, set, node2, b);
                }
                break;
            }
            case 8: {
                if (b) {
                    set.add(node);
                    break;
                }
                break;
            }
            case 10: {
                break;
            }
            default: {
                set.add(node);
                break;
            }
        }
    }
    
    public static void outputDOM(final Node node, final OutputStream outputStream) {
        outputDOM(node, outputStream, false);
    }
    
    public static void outputDOM(final Node node, final OutputStream outputStream, final boolean b) {
        try {
            if (b) {
                outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
            }
            outputStream.write(Canonicalizer.getInstance("http://santuario.apache.org/c14n/physical").canonicalizeSubtree(node));
        }
        catch (final IOException ex) {
            XMLUtils.LOG.debug(ex.getMessage(), ex);
        }
        catch (final InvalidCanonicalizerException ex2) {
            XMLUtils.LOG.debug(ex2.getMessage(), ex2);
        }
        catch (final CanonicalizationException ex3) {
            XMLUtils.LOG.debug(ex3.getMessage(), ex3);
        }
    }
    
    public static void outputDOMc14nWithComments(final Node node, final OutputStream outputStream) {
        try {
            outputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(node));
        }
        catch (final IOException ex) {
            XMLUtils.LOG.debug(ex.getMessage(), ex);
        }
        catch (final InvalidCanonicalizerException ex2) {
            XMLUtils.LOG.debug(ex2.getMessage(), ex2);
        }
        catch (final CanonicalizationException ex3) {
            XMLUtils.LOG.debug(ex3.getMessage(), ex3);
        }
    }
    
    @Deprecated
    public static String getFullTextChildrenFromElement(final Element element) {
        return getFullTextChildrenFromNode(element);
    }
    
    public static String getFullTextChildrenFromNode(final Node node) {
        final StringBuilder sb = new StringBuilder();
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 3) {
                sb.append(((Text)node2).getData());
            }
        }
        return sb.toString();
    }
    
    public static Element createElementInSignatureSpace(final Document document, final String s) {
        if (document == null) {
            throw new RuntimeException("Document is null");
        }
        if (XMLUtils.dsPrefix == null || XMLUtils.dsPrefix.length() == 0) {
            return document.createElementNS("http://www.w3.org/2000/09/xmldsig#", s);
        }
        return document.createElementNS("http://www.w3.org/2000/09/xmldsig#", XMLUtils.dsPrefix + ":" + s);
    }
    
    public static Element createElementInSignature11Space(final Document document, final String s) {
        if (document == null) {
            throw new RuntimeException("Document is null");
        }
        if (XMLUtils.ds11Prefix == null || XMLUtils.ds11Prefix.length() == 0) {
            return document.createElementNS("http://www.w3.org/2009/xmldsig11#", s);
        }
        return document.createElementNS("http://www.w3.org/2009/xmldsig11#", XMLUtils.ds11Prefix + ":" + s);
    }
    
    public static boolean elementIsInSignatureSpace(final Element element, final String s) {
        return element != null && "http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI()) && element.getLocalName().equals(s);
    }
    
    public static boolean elementIsInSignature11Space(final Element element, final String s) {
        return element != null && "http://www.w3.org/2009/xmldsig11#".equals(element.getNamespaceURI()) && element.getLocalName().equals(s);
    }
    
    public static Document getOwnerDocument(final Node node) {
        if (node.getNodeType() == 9) {
            return (Document)node;
        }
        try {
            return node.getOwnerDocument();
        }
        catch (final NullPointerException ex) {
            throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + ex.getMessage() + "\"");
        }
    }
    
    public static Document getOwnerDocument(final Set<Node> set) {
        Throwable t = null;
        for (final Node node : set) {
            final short nodeType = node.getNodeType();
            if (nodeType == 9) {
                return (Document)node;
            }
            try {
                if (nodeType == 2) {
                    return ((Attr)node).getOwnerElement().getOwnerDocument();
                }
                return node.getOwnerDocument();
            }
            catch (final NullPointerException ex) {
                t = ex;
                continue;
            }
            break;
        }
        throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + ((t == null) ? "" : t.getMessage()) + "\"");
    }
    
    public static Element createDSctx(final Document document, final String s, final String s2) {
        if (s == null || s.trim().length() == 0) {
            throw new IllegalArgumentException("You must supply a prefix");
        }
        final Element elementNS = document.createElementNS(null, "namespaceContext");
        elementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + s.trim(), s2);
        return elementNS;
    }
    
    public static void addReturnToElement(final Element element) {
        if (!XMLUtils.ignoreLineBreaks) {
            element.appendChild(element.getOwnerDocument().createTextNode("\n"));
        }
    }
    
    public static void addReturnToElement(final Document document, final HelperNodeList list) {
        if (!XMLUtils.ignoreLineBreaks) {
            list.appendChild(document.createTextNode("\n"));
        }
    }
    
    public static void addReturnBeforeChild(final Element element, final Node node) {
        if (!XMLUtils.ignoreLineBreaks) {
            element.insertBefore(element.getOwnerDocument().createTextNode("\n"), node);
        }
    }
    
    public static String encodeToString(final byte[] array) {
        if (XMLUtils.ignoreLineBreaks) {
            return Base64.getEncoder().encodeToString(array);
        }
        if (XMLUtils.lineFeedOnly) {
            return XMLUtils.LF_ENCODER.encodeToString(array);
        }
        return Base64.getMimeEncoder().encodeToString(array);
    }
    
    public static byte[] decode(final String s) {
        return Base64.getMimeDecoder().decode(s);
    }
    
    public static byte[] decode(final byte[] array) {
        return Base64.getMimeDecoder().decode(array);
    }
    
    public static boolean isIgnoreLineBreaks() {
        return XMLUtils.ignoreLineBreaks;
    }
    
    public static Set<Node> convertNodelistToSet(final NodeList list) {
        if (list == null) {
            return new HashSet<Node>();
        }
        final int length = list.getLength();
        final HashSet set = new HashSet(length);
        for (int i = 0; i < length; ++i) {
            set.add((Object)list.item(i));
        }
        return (Set<Node>)set;
    }
    
    public static void circumventBug2650(final Document document) {
        final Element documentElement = document.getDocumentElement();
        if (documentElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns") == null) {
            documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
        }
        circumventBug2650internal(document);
    }
    
    private static void circumventBug2650internal(Node node) {
        Node parentNode = null;
        Node node2 = null;
        while (true) {
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (!element.hasChildNodes()) {
                        break;
                    }
                    if (element.hasAttributes()) {
                        final NamedNodeMap attributes = element.getAttributes();
                        final int length = attributes.getLength();
                        for (Node node3 = element.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            if (node3.getNodeType() == 1) {
                                final Element element2 = (Element)node3;
                                for (int i = 0; i < length; ++i) {
                                    final Attr attr = (Attr)attributes.item(i);
                                    if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI())) {
                                        if (!element2.hasAttributeNS("http://www.w3.org/2000/xmlns/", attr.getLocalName())) {
                                            element2.setAttributeNS("http://www.w3.org/2000/xmlns/", attr.getName(), attr.getNodeValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                case 5:
                case 9: {
                    parentNode = node;
                    node2 = node.getFirstChild();
                    break;
                }
            }
            while (node2 == null && parentNode != null) {
                node2 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
            }
            if (node2 == null) {
                break;
            }
            node = node2;
            node2 = node.getNextSibling();
        }
    }
    
    public static Element selectDsNode(Node nextSibling, final String s, int n) {
        while (nextSibling != null) {
            if ("http://www.w3.org/2000/09/xmldsig#".equals(nextSibling.getNamespaceURI()) && nextSibling.getLocalName().equals(s)) {
                if (n == 0) {
                    return (Element)nextSibling;
                }
                --n;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return null;
    }
    
    public static Element selectDs11Node(Node nextSibling, final String s, int n) {
        while (nextSibling != null) {
            if ("http://www.w3.org/2009/xmldsig11#".equals(nextSibling.getNamespaceURI()) && nextSibling.getLocalName().equals(s)) {
                if (n == 0) {
                    return (Element)nextSibling;
                }
                --n;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return null;
    }
    
    public static Text selectDsNodeText(final Node node, final String s, final int n) {
        final Element selectDsNode = selectDsNode(node, s, n);
        if (selectDsNode == null) {
            return null;
        }
        Node node2;
        for (node2 = selectDsNode.getFirstChild(); node2 != null && node2.getNodeType() != 3; node2 = node2.getNextSibling()) {}
        return (Text)node2;
    }
    
    public static Text selectDs11NodeText(final Node node, final String s, final int n) {
        final Element selectDs11Node = selectDs11Node(node, s, n);
        if (selectDs11Node == null) {
            return null;
        }
        Node node2;
        for (node2 = selectDs11Node.getFirstChild(); node2 != null && node2.getNodeType() != 3; node2 = node2.getNextSibling()) {}
        return (Text)node2;
    }
    
    public static Text selectNodeText(final Node node, final String s, final String s2, final int n) {
        final Element selectNode = selectNode(node, s, s2, n);
        if (selectNode == null) {
            return null;
        }
        Node node2;
        for (node2 = selectNode.getFirstChild(); node2 != null && node2.getNodeType() != 3; node2 = node2.getNextSibling()) {}
        return (Text)node2;
    }
    
    public static Element selectNode(Node nextSibling, final String s, final String s2, int n) {
        while (nextSibling != null) {
            if (nextSibling.getNamespaceURI() != null && nextSibling.getNamespaceURI().equals(s) && nextSibling.getLocalName().equals(s2)) {
                if (n == 0) {
                    return (Element)nextSibling;
                }
                --n;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return null;
    }
    
    public static Element[] selectDsNodes(final Node node, final String s) {
        return selectNodes(node, "http://www.w3.org/2000/09/xmldsig#", s);
    }
    
    public static Element[] selectDs11Nodes(final Node node, final String s) {
        return selectNodes(node, "http://www.w3.org/2009/xmldsig11#", s);
    }
    
    public static Element[] selectNodes(Node nextSibling, final String s, final String s2) {
        final ArrayList list = new ArrayList();
        while (nextSibling != null) {
            if (nextSibling.getNamespaceURI() != null && nextSibling.getNamespaceURI().equals(s) && nextSibling.getLocalName().equals(s2)) {
                list.add(nextSibling);
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return (Element[])list.toArray(new Element[list.size()]);
    }
    
    public static Set<Node> excludeNodeFromSet(final Node node, final Set<Node> set) {
        final HashSet set2 = new HashSet();
        for (final Node node2 : set) {
            if (!isDescendantOrSelf(node, node2)) {
                set2.add(node2);
            }
        }
        return set2;
    }
    
    public static String getStrFromNode(final Node node) {
        if (node.getNodeType() == 3) {
            final StringBuilder sb = new StringBuilder();
            for (Node node2 = node.getParentNode().getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2.getNodeType() == 3) {
                    sb.append(((Text)node2).getData());
                }
            }
            return sb.toString();
        }
        if (node.getNodeType() == 2) {
            return node.getNodeValue();
        }
        if (node.getNodeType() == 7) {
            return node.getNodeValue();
        }
        return null;
    }
    
    public static boolean isDescendantOrSelf(final Node node, final Node node2) {
        if (node == node2) {
            return true;
        }
        Node node3 = node2;
        while (node3 != null) {
            if (node3 == node) {
                return true;
            }
            if (node3.getNodeType() == 2) {
                node3 = ((Attr)node3).getOwnerElement();
            }
            else {
                node3 = node3.getParentNode();
            }
        }
        return false;
    }
    
    public static boolean ignoreLineBreaks() {
        return XMLUtils.ignoreLineBreaks;
    }
    
    public static String getAttributeValue(final Element element, final String s) {
        final Attr attributeNodeNS = element.getAttributeNodeNS(null, s);
        return (attributeNodeNS == null) ? null : attributeNodeNS.getValue();
    }
    
    public static boolean protectAgainstWrappingAttack(Node node, final String s) {
        String s2 = s.trim();
        if (!s2.isEmpty() && s2.charAt(0) == '#') {
            s2 = s2.substring(1);
        }
        Node parentNode = null;
        Element ownerElement = null;
        if (node != null) {
            parentNode = node.getParentNode();
        }
        while (node != null) {
            if (node.getNodeType() == 1) {
                final NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) {
                    for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                        final Attr attr = (Attr)attributes.item(i);
                        if (attr.isId() && s2.equals(attr.getValue())) {
                            if (ownerElement != null) {
                                XMLUtils.LOG.debug("Multiple elements with the same 'Id' attribute value!");
                                return false;
                            }
                            ownerElement = attr.getOwnerElement();
                        }
                    }
                }
            }
            Node parentNode2 = node;
            node = node.getFirstChild();
            if (node == null) {
                node = parentNode2.getNextSibling();
            }
            while (node == null) {
                parentNode2 = parentNode2.getParentNode();
                if (parentNode2 == parentNode) {
                    return true;
                }
                node = parentNode2.getNextSibling();
            }
        }
        return true;
    }
    
    public static boolean protectAgainstWrappingAttack(Node node, final Element element, final String s) {
        String s2 = s.trim();
        if (!s2.isEmpty() && s2.charAt(0) == '#') {
            s2 = s2.substring(1);
        }
        Node parentNode = null;
        if (node != null) {
            parentNode = node.getParentNode();
        }
        while (node != null) {
            if (node.getNodeType() == 1) {
                final Element element2 = (Element)node;
                final NamedNodeMap attributes = element2.getAttributes();
                if (attributes != null) {
                    for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                        final Attr attr = (Attr)attributes.item(i);
                        if (attr.isId() && s2.equals(attr.getValue()) && element2 != element) {
                            XMLUtils.LOG.debug("Multiple elements with the same 'Id' attribute value!");
                            return false;
                        }
                    }
                }
            }
            Node parentNode2 = node;
            node = node.getFirstChild();
            if (node == null) {
                node = parentNode2.getNextSibling();
            }
            while (node == null) {
                parentNode2 = parentNode2.getParentNode();
                if (parentNode2 == parentNode) {
                    return true;
                }
                node = parentNode2.getNextSibling();
            }
        }
        return true;
    }
    
    public static Document newDocument() throws ParserConfigurationException {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClassLoader(XMLUtils.class);
        }
        if (classLoader == null) {
            return buildDocumentBuilder(true).newDocument();
        }
        final Queue<DocumentBuilder> documentBuilderQueue = getDocumentBuilderQueue(true, classLoader);
        final DocumentBuilder documentBuilder = getDocumentBuilder(true, documentBuilderQueue);
        final Document document = documentBuilder.newDocument();
        repoolDocumentBuilder(documentBuilder, documentBuilderQueue);
        return document;
    }
    
    public static Document read(final InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        return read(inputStream, true);
    }
    
    public static Document read(final InputStream inputStream, final boolean b) throws ParserConfigurationException, SAXException, IOException {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClassLoader(XMLUtils.class);
        }
        if (classLoader == null) {
            return buildDocumentBuilder(b).parse(inputStream);
        }
        final Queue<DocumentBuilder> documentBuilderQueue = getDocumentBuilderQueue(b, classLoader);
        final DocumentBuilder documentBuilder = getDocumentBuilder(b, documentBuilderQueue);
        final Document parse = documentBuilder.parse(inputStream);
        repoolDocumentBuilder(documentBuilder, documentBuilderQueue);
        return parse;
    }
    
    public static Document read(final String s, final boolean b) throws ParserConfigurationException, SAXException, IOException {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClassLoader(XMLUtils.class);
        }
        if (classLoader == null) {
            return buildDocumentBuilder(b).parse(s);
        }
        final Queue<DocumentBuilder> documentBuilderQueue = getDocumentBuilderQueue(b, classLoader);
        final DocumentBuilder documentBuilder = getDocumentBuilder(b, documentBuilderQueue);
        final Document parse = documentBuilder.parse(s);
        repoolDocumentBuilder(documentBuilder, documentBuilderQueue);
        return parse;
    }
    
    public static Document read(final InputSource inputSource) throws ParserConfigurationException, SAXException, IOException {
        return read(inputSource, true);
    }
    
    public static Document read(final InputSource inputSource, final boolean b) throws ParserConfigurationException, SAXException, IOException {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClassLoader(XMLUtils.class);
        }
        if (classLoader == null) {
            return buildDocumentBuilder(b).parse(inputSource);
        }
        final Queue<DocumentBuilder> documentBuilderQueue = getDocumentBuilderQueue(b, classLoader);
        final DocumentBuilder documentBuilder = getDocumentBuilder(b, documentBuilderQueue);
        final Document parse = documentBuilder.parse(inputSource);
        repoolDocumentBuilder(documentBuilder, documentBuilderQueue);
        return parse;
    }
    
    @Deprecated
    public static DocumentBuilder createDocumentBuilder(final boolean b) throws ParserConfigurationException {
        return createDocumentBuilder(b, true);
    }
    
    @Deprecated
    public static DocumentBuilder createDocumentBuilder(final boolean validating, final boolean b) throws ParserConfigurationException {
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
        if (b) {
            instance.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        }
        instance.setValidating(validating);
        instance.setNamespaceAware(true);
        return instance.newDocumentBuilder();
    }
    
    @Deprecated
    public static boolean repoolDocumentBuilder(final DocumentBuilder documentBuilder) {
        return true;
    }
    
    public static byte[] getBytes(final BigInteger bigInteger, int n) {
        n = n + 7 >> 3 << 3;
        if (n < bigInteger.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        final byte[] byteArray = bigInteger.toByteArray();
        if (bigInteger.bitLength() % 8 != 0 && bigInteger.bitLength() / 8 + 1 == n / 8) {
            return byteArray;
        }
        int n2 = 0;
        int length = byteArray.length;
        if (bigInteger.bitLength() % 8 == 0) {
            n2 = 1;
            --length;
        }
        final int n3 = n / 8 - length;
        final byte[] array = new byte[n / 8];
        System.arraycopy(byteArray, n2, array, n3, length);
        return array;
    }
    
    private static Queue<DocumentBuilder> getDocumentBuilderQueue(final boolean b, final ClassLoader classLoader) throws ParserConfigurationException {
        final Map<ClassLoader, Queue<DocumentBuilder>> map = b ? XMLUtils.DOCUMENT_BUILDERS_DISALLOW_DOCTYPE : XMLUtils.DOCUMENT_BUILDERS;
        Queue queue = map.get(classLoader);
        if (queue == null) {
            queue = new ArrayBlockingQueue(XMLUtils.parserPoolSize);
            map.put(classLoader, queue);
        }
        return queue;
    }
    
    private static DocumentBuilder getDocumentBuilder(final boolean b, final Queue<DocumentBuilder> queue) throws ParserConfigurationException {
        DocumentBuilder buildDocumentBuilder = queue.poll();
        if (buildDocumentBuilder == null) {
            buildDocumentBuilder = buildDocumentBuilder(b);
        }
        return buildDocumentBuilder;
    }
    
    private static DocumentBuilder buildDocumentBuilder(final boolean b) throws ParserConfigurationException {
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setNamespaceAware(true);
        instance.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        instance.setFeature("http://apache.org/xml/features/disallow-doctype-decl", b);
        return instance.newDocumentBuilder();
    }
    
    private static void repoolDocumentBuilder(final DocumentBuilder documentBuilder, final Queue<DocumentBuilder> queue) {
        if (queue != null) {
            documentBuilder.reset();
            queue.offer(documentBuilder);
        }
    }
    
    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
        return Thread.currentThread().getContextClassLoader();
    }
    
    private static ClassLoader getClassLoader(final Class<?> clazz) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return clazz.getClassLoader();
                }
            });
        }
        return clazz.getClassLoader();
    }
    
    static {
        XMLUtils.lineFeedOnly = AccessController.doPrivileged(() -> Boolean.getBoolean("com.sun.org.apache.xml.internal.security.lineFeedOnly"));
        XMLUtils.ignoreLineBreaks = AccessController.doPrivileged(() -> Boolean.getBoolean("com.sun.org.apache.xml.internal.security.ignoreLineBreaks"));
        XMLUtils.parserPoolSize = AccessController.doPrivileged(() -> Integer.getInteger("com.sun.org.apache.xml.internal.security.parser.pool-size", 20));
        XMLUtils.dsPrefix = "ds";
        XMLUtils.ds11Prefix = "dsig11";
        XMLUtils.xencPrefix = "xenc";
        XMLUtils.xenc11Prefix = "xenc11";
        LOG = LoggerFactory.getLogger(XMLUtils.class);
        LF_ENCODER = Base64.getMimeEncoder(76, new byte[] { 10 });
        DOCUMENT_BUILDERS = Collections.synchronizedMap(new WeakHashMap<ClassLoader, Queue<DocumentBuilder>>());
        DOCUMENT_BUILDERS_DISALLOW_DOCTYPE = Collections.synchronizedMap(new WeakHashMap<ClassLoader, Queue<DocumentBuilder>>());
    }
}
