package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import java.util.ListIterator;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.Attr;
import java.io.OutputStream;
import org.w3c.dom.Node;
import java.util.Set;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import java.util.List;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizerSpi;

public abstract class CanonicalizerBase extends CanonicalizerSpi
{
    public static final String XML = "xml";
    public static final String XMLNS = "xmlns";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    public static final String XML_LANG_URI = "http://www.w3.org/XML/1998/namespace";
    protected static final AttrCompare COMPARE;
    private static final byte[] END_PI;
    private static final byte[] BEGIN_PI;
    private static final byte[] END_COMM;
    private static final byte[] BEGIN_COMM;
    private static final byte[] XA;
    private static final byte[] X9;
    private static final byte[] QUOT;
    private static final byte[] XD;
    private static final byte[] GT;
    private static final byte[] LT;
    private static final byte[] END_TAG;
    private static final byte[] AMP;
    private static final byte[] EQUALS_STR;
    protected static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
    protected static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
    protected static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
    private List<NodeFilter> nodeFilter;
    private boolean includeComments;
    private Set<Node> xpathNodeSet;
    private Node excludeNode;
    private OutputStream writer;
    private Attr nullNode;
    
    public CanonicalizerBase(final boolean includeComments) {
        this.writer = new ByteArrayOutputStream();
        this.includeComments = includeComments;
    }
    
    @Override
    public byte[] engineCanonicalizeSubTree(final Node node) throws CanonicalizationException {
        return this.engineCanonicalizeSubTree(node, (Node)null);
    }
    
    @Override
    public byte[] engineCanonicalizeXPathNodeSet(final Set<Node> xpathNodeSet) throws CanonicalizationException {
        this.xpathNodeSet = xpathNodeSet;
        return this.engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this.xpathNodeSet));
    }
    
    public byte[] engineCanonicalize(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException {
        try {
            if (xmlSignatureInput.isExcludeComments()) {
                this.includeComments = false;
            }
            if (xmlSignatureInput.isOctetStream()) {
                return this.engineCanonicalize(xmlSignatureInput.getBytes());
            }
            if (xmlSignatureInput.isElement()) {
                return this.engineCanonicalizeSubTree(xmlSignatureInput.getSubNode(), xmlSignatureInput.getExcludeNode());
            }
            if (!xmlSignatureInput.isNodeSet()) {
                return null;
            }
            this.nodeFilter = xmlSignatureInput.getNodeFilters();
            this.circumventBugIfNeeded(xmlSignatureInput);
            if (xmlSignatureInput.getSubNode() != null) {
                return this.engineCanonicalizeXPathNodeSetInternal(xmlSignatureInput.getSubNode());
            }
            return this.engineCanonicalizeXPathNodeSet(xmlSignatureInput.getNodeSet());
        }
        catch (final ParserConfigurationException ex) {
            throw new CanonicalizationException(ex);
        }
        catch (final IOException ex2) {
            throw new CanonicalizationException(ex2);
        }
        catch (final SAXException ex3) {
            throw new CanonicalizationException(ex3);
        }
    }
    
    @Override
    public void setWriter(final OutputStream writer) {
        this.writer = writer;
    }
    
    protected OutputStream getWriter() {
        return this.writer;
    }
    
    protected byte[] engineCanonicalizeSubTree(final Node node, final Node excludeNode) throws CanonicalizationException {
        this.excludeNode = excludeNode;
        try {
            final NameSpaceSymbTable nameSpaceSymbTable = new NameSpaceSymbTable();
            int n = -1;
            if (node != null && 1 == node.getNodeType()) {
                this.getParentNameSpaces((Element)node, nameSpaceSymbTable);
                n = 0;
            }
            this.canonicalizeSubTree(node, nameSpaceSymbTable, node, n);
            this.writer.flush();
            if (this.writer instanceof ByteArrayOutputStream) {
                final byte[] byteArray = ((ByteArrayOutputStream)this.writer).toByteArray();
                if (this.reset) {
                    ((ByteArrayOutputStream)this.writer).reset();
                }
                else {
                    this.writer.close();
                }
                return byteArray;
            }
            if (this.writer instanceof UnsyncByteArrayOutputStream) {
                final byte[] byteArray2 = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
                if (this.reset) {
                    ((UnsyncByteArrayOutputStream)this.writer).reset();
                }
                else {
                    this.writer.close();
                }
                return byteArray2;
            }
            this.writer.close();
            return null;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new CanonicalizationException(ex);
        }
        catch (final IOException ex2) {
            throw new CanonicalizationException(ex2);
        }
    }
    
    protected final void canonicalizeSubTree(Node node, final NameSpaceSymbTable nameSpaceSymbTable, final Node node2, int n) throws CanonicalizationException, IOException {
        if (node == null || this.isVisibleInt(node) == -1) {
            return;
        }
        Node node3 = null;
        Node parentNode = null;
        final OutputStream writer = this.writer;
        final Node excludeNode = this.excludeNode;
        final boolean includeComments = this.includeComments;
        final HashMap hashMap = new HashMap();
        while (true) {
            switch (node.getNodeType()) {
                case 2:
                case 6:
                case 12: {
                    throw new CanonicalizationException("empty", new Object[] { "illegal node type during traversal" });
                }
                case 9:
                case 11: {
                    nameSpaceSymbTable.outputNodePush();
                    node3 = node.getFirstChild();
                    break;
                }
                case 8: {
                    if (includeComments) {
                        this.outputCommentToWriter((Comment)node, writer, n);
                        break;
                    }
                    break;
                }
                case 7: {
                    this.outputPItoWriter((ProcessingInstruction)node, writer, n);
                    break;
                }
                case 3:
                case 4: {
                    outputTextToWriter(node.getNodeValue(), writer);
                    break;
                }
                case 1: {
                    n = 0;
                    if (node == excludeNode) {
                        break;
                    }
                    final Element element = (Element)node;
                    nameSpaceSymbTable.outputNodePush();
                    writer.write(60);
                    final String tagName = element.getTagName();
                    UtfHelpper.writeByte(tagName, writer, hashMap);
                    this.outputAttributesSubtree(element, nameSpaceSymbTable, hashMap);
                    writer.write(62);
                    node3 = node.getFirstChild();
                    if (node3 != null) {
                        parentNode = element;
                        break;
                    }
                    writer.write(CanonicalizerBase.END_TAG.clone());
                    UtfHelpper.writeStringToUtf8(tagName, writer);
                    writer.write(62);
                    nameSpaceSymbTable.outputNodePop();
                    if (parentNode != null) {
                        node3 = node.getNextSibling();
                        break;
                    }
                    break;
                }
            }
            while (node3 == null && parentNode != null) {
                writer.write(CanonicalizerBase.END_TAG.clone());
                UtfHelpper.writeByte(((Element)parentNode).getTagName(), writer, hashMap);
                writer.write(62);
                nameSpaceSymbTable.outputNodePop();
                if (parentNode == node2) {
                    return;
                }
                node3 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
                if (parentNode != null && 1 == parentNode.getNodeType()) {
                    continue;
                }
                n = 1;
                parentNode = null;
            }
            if (node3 == null) {
                return;
            }
            node = node3;
            node3 = node.getNextSibling();
        }
    }
    
    private byte[] engineCanonicalizeXPathNodeSetInternal(final Node node) throws CanonicalizationException {
        try {
            this.canonicalizeXPathNodeSet(node, node);
            this.writer.flush();
            if (this.writer instanceof ByteArrayOutputStream) {
                final byte[] byteArray = ((ByteArrayOutputStream)this.writer).toByteArray();
                if (this.reset) {
                    ((ByteArrayOutputStream)this.writer).reset();
                }
                else {
                    this.writer.close();
                }
                return byteArray;
            }
            if (this.writer instanceof UnsyncByteArrayOutputStream) {
                final byte[] byteArray2 = ((UnsyncByteArrayOutputStream)this.writer).toByteArray();
                if (this.reset) {
                    ((UnsyncByteArrayOutputStream)this.writer).reset();
                }
                else {
                    this.writer.close();
                }
                return byteArray2;
            }
            this.writer.close();
            return null;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new CanonicalizationException(ex);
        }
        catch (final IOException ex2) {
            throw new CanonicalizationException(ex2);
        }
    }
    
    protected final void canonicalizeXPathNodeSet(Node node, final Node node2) throws CanonicalizationException, IOException {
        if (this.isVisibleInt(node) == -1) {
            return;
        }
        final NameSpaceSymbTable nameSpaceSymbTable = new NameSpaceSymbTable();
        if (node != null && 1 == node.getNodeType()) {
            this.getParentNameSpaces((Element)node, nameSpaceSymbTable);
        }
        if (node == null) {
            return;
        }
        Node node3 = null;
        Node parentNode = null;
        int n = -1;
        final HashMap hashMap = new HashMap();
        while (true) {
            switch (node.getNodeType()) {
                case 2:
                case 6:
                case 12: {
                    throw new CanonicalizationException("empty", new Object[] { "illegal node type during traversal" });
                }
                case 9:
                case 11: {
                    nameSpaceSymbTable.outputNodePush();
                    node3 = node.getFirstChild();
                    break;
                }
                case 8: {
                    if (this.includeComments && this.isVisibleDO(node, nameSpaceSymbTable.getLevel()) == 1) {
                        this.outputCommentToWriter((Comment)node, this.writer, n);
                        break;
                    }
                    break;
                }
                case 7: {
                    if (this.isVisible(node)) {
                        this.outputPItoWriter((ProcessingInstruction)node, this.writer, n);
                        break;
                    }
                    break;
                }
                case 3:
                case 4: {
                    if (this.isVisible(node)) {
                        outputTextToWriter(node.getNodeValue(), this.writer);
                        for (Node node4 = node.getNextSibling(); node4 != null && (node4.getNodeType() == 3 || node4.getNodeType() == 4); node4 = node4.getNextSibling()) {
                            outputTextToWriter(node4.getNodeValue(), this.writer);
                            node = node4;
                            node3 = node.getNextSibling();
                        }
                        break;
                    }
                    break;
                }
                case 1: {
                    n = 0;
                    final Element element = (Element)node;
                    String tagName = null;
                    final int visibleDO = this.isVisibleDO(node, nameSpaceSymbTable.getLevel());
                    if (visibleDO == -1) {
                        node3 = node.getNextSibling();
                        break;
                    }
                    final boolean b = visibleDO == 1;
                    if (b) {
                        nameSpaceSymbTable.outputNodePush();
                        this.writer.write(60);
                        tagName = element.getTagName();
                        UtfHelpper.writeByte(tagName, this.writer, hashMap);
                    }
                    else {
                        nameSpaceSymbTable.push();
                    }
                    this.outputAttributes(element, nameSpaceSymbTable, hashMap);
                    if (b) {
                        this.writer.write(62);
                    }
                    node3 = node.getFirstChild();
                    if (node3 != null) {
                        parentNode = element;
                        break;
                    }
                    if (b) {
                        this.writer.write(CanonicalizerBase.END_TAG.clone());
                        UtfHelpper.writeByte(tagName, this.writer, hashMap);
                        this.writer.write(62);
                        nameSpaceSymbTable.outputNodePop();
                    }
                    else {
                        nameSpaceSymbTable.pop();
                    }
                    if (parentNode != null) {
                        node3 = node.getNextSibling();
                        break;
                    }
                    break;
                }
            }
            while (node3 == null && parentNode != null) {
                if (this.isVisible(parentNode)) {
                    this.writer.write(CanonicalizerBase.END_TAG.clone());
                    UtfHelpper.writeByte(((Element)parentNode).getTagName(), this.writer, hashMap);
                    this.writer.write(62);
                    nameSpaceSymbTable.outputNodePop();
                }
                else {
                    nameSpaceSymbTable.pop();
                }
                if (parentNode == node2) {
                    return;
                }
                node3 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
                if (parentNode != null && 1 == parentNode.getNodeType()) {
                    continue;
                }
                parentNode = null;
                n = 1;
            }
            if (node3 == null) {
                return;
            }
            node = node3;
            node3 = node.getNextSibling();
        }
    }
    
    protected int isVisibleDO(final Node node, final int n) {
        if (this.nodeFilter != null) {
            final Iterator<NodeFilter> iterator = this.nodeFilter.iterator();
            while (iterator.hasNext()) {
                final int nodeIncludeDO = iterator.next().isNodeIncludeDO(node, n);
                if (nodeIncludeDO != 1) {
                    return nodeIncludeDO;
                }
            }
        }
        if (this.xpathNodeSet != null && !this.xpathNodeSet.contains(node)) {
            return 0;
        }
        return 1;
    }
    
    protected int isVisibleInt(final Node node) {
        if (this.nodeFilter != null) {
            final Iterator<NodeFilter> iterator = this.nodeFilter.iterator();
            while (iterator.hasNext()) {
                final int nodeInclude = iterator.next().isNodeInclude(node);
                if (nodeInclude != 1) {
                    return nodeInclude;
                }
            }
        }
        if (this.xpathNodeSet != null && !this.xpathNodeSet.contains(node)) {
            return 0;
        }
        return 1;
    }
    
    protected boolean isVisible(final Node node) {
        if (this.nodeFilter != null) {
            final Iterator<NodeFilter> iterator = this.nodeFilter.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isNodeInclude(node) != 1) {
                    return false;
                }
            }
        }
        return this.xpathNodeSet == null || this.xpathNodeSet.contains(node);
    }
    
    protected void handleParent(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
        if (!element.hasAttributes() && element.getNamespaceURI() == null) {
            return;
        }
        final NamedNodeMap attributes = element.getAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            final String localName = attr.getLocalName();
            final String nodeValue = attr.getNodeValue();
            if ("http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI()) && (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(nodeValue))) {
                nameSpaceSymbTable.addMapping(localName, nodeValue, attr);
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
    
    protected final void getParentNameSpaces(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
        final Node parentNode = element.getParentNode();
        if (parentNode == null || 1 != parentNode.getNodeType()) {
            return;
        }
        final ArrayList list = new ArrayList();
        for (Node parentNode2 = parentNode; parentNode2 != null && 1 == parentNode2.getNodeType(); parentNode2 = parentNode2.getParentNode()) {
            list.add(parentNode2);
        }
        final ListIterator listIterator = list.listIterator(list.size());
        while (listIterator.hasPrevious()) {
            this.handleParent((Element)listIterator.previous(), nameSpaceSymbTable);
        }
        list.clear();
        final Attr mappingWithoutRendered = nameSpaceSymbTable.getMappingWithoutRendered("xmlns");
        if (mappingWithoutRendered != null && "".equals(mappingWithoutRendered.getValue())) {
            nameSpaceSymbTable.addMappingAndRender("xmlns", "", this.getNullNode(mappingWithoutRendered.getOwnerDocument()));
        }
    }
    
    abstract void outputAttributes(final Element p0, final NameSpaceSymbTable p1, final Map<String, byte[]> p2) throws CanonicalizationException, DOMException, IOException;
    
    abstract void outputAttributesSubtree(final Element p0, final NameSpaceSymbTable p1, final Map<String, byte[]> p2) throws CanonicalizationException, DOMException, IOException;
    
    abstract void circumventBugIfNeeded(final XMLSignatureInput p0) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException;
    
    protected static final void outputAttrToWriter(final String s, final String s2, final OutputStream outputStream, final Map<String, byte[]> map) throws IOException {
        outputStream.write(32);
        UtfHelpper.writeByte(s, outputStream, map);
        outputStream.write(CanonicalizerBase.EQUALS_STR.clone());
        final int length = s2.length();
        int i = 0;
        while (i < length) {
            final int codePoint = s2.codePointAt(i);
            i += Character.charCount(codePoint);
            byte[] array = null;
            switch (codePoint) {
                case 38: {
                    array = CanonicalizerBase.AMP.clone();
                    break;
                }
                case 60: {
                    array = CanonicalizerBase.LT.clone();
                    break;
                }
                case 34: {
                    array = CanonicalizerBase.QUOT.clone();
                    break;
                }
                case 9: {
                    array = CanonicalizerBase.X9.clone();
                    break;
                }
                case 10: {
                    array = CanonicalizerBase.XA.clone();
                    break;
                }
                case 13: {
                    array = CanonicalizerBase.XD.clone();
                    break;
                }
                default: {
                    if (codePoint < 128) {
                        outputStream.write(codePoint);
                        continue;
                    }
                    UtfHelpper.writeCodePointToUtf8(codePoint, outputStream);
                    continue;
                }
            }
            outputStream.write(array);
        }
        outputStream.write(34);
    }
    
    protected void outputPItoWriter(final ProcessingInstruction processingInstruction, final OutputStream outputStream, final int n) throws IOException {
        if (n == 1) {
            outputStream.write(10);
        }
        outputStream.write(CanonicalizerBase.BEGIN_PI.clone());
        final String target = processingInstruction.getTarget();
        final int length = target.length();
        int i = 0;
        while (i < length) {
            final int codePoint = target.codePointAt(i);
            i += Character.charCount(codePoint);
            if (codePoint == 13) {
                outputStream.write(CanonicalizerBase.XD.clone());
            }
            else if (codePoint < 128) {
                outputStream.write(codePoint);
            }
            else {
                UtfHelpper.writeCodePointToUtf8(codePoint, outputStream);
            }
        }
        final String data = processingInstruction.getData();
        final int length2 = data.length();
        if (length2 > 0) {
            outputStream.write(32);
            int j = 0;
            while (j < length2) {
                final int codePoint2 = data.codePointAt(j);
                j += Character.charCount(codePoint2);
                if (codePoint2 == 13) {
                    outputStream.write(CanonicalizerBase.XD.clone());
                }
                else {
                    UtfHelpper.writeCodePointToUtf8(codePoint2, outputStream);
                }
            }
        }
        outputStream.write(CanonicalizerBase.END_PI.clone());
        if (n == -1) {
            outputStream.write(10);
        }
    }
    
    protected void outputCommentToWriter(final Comment comment, final OutputStream outputStream, final int n) throws IOException {
        if (n == 1) {
            outputStream.write(10);
        }
        outputStream.write(CanonicalizerBase.BEGIN_COMM.clone());
        final String data = comment.getData();
        final int length = data.length();
        int i = 0;
        while (i < length) {
            final int codePoint = data.codePointAt(i);
            i += Character.charCount(codePoint);
            if (codePoint == 13) {
                outputStream.write(CanonicalizerBase.XD.clone());
            }
            else if (codePoint < 128) {
                outputStream.write(codePoint);
            }
            else {
                UtfHelpper.writeCodePointToUtf8(codePoint, outputStream);
            }
        }
        outputStream.write(CanonicalizerBase.END_COMM.clone());
        if (n == -1) {
            outputStream.write(10);
        }
    }
    
    protected static final void outputTextToWriter(final String s, final OutputStream outputStream) throws IOException {
        final int length = s.length();
        int i = 0;
        while (i < length) {
            final int codePoint = s.codePointAt(i);
            i += Character.charCount(codePoint);
            byte[] array = null;
            switch (codePoint) {
                case 38: {
                    array = CanonicalizerBase.AMP.clone();
                    break;
                }
                case 60: {
                    array = CanonicalizerBase.LT.clone();
                    break;
                }
                case 62: {
                    array = CanonicalizerBase.GT.clone();
                    break;
                }
                case 13: {
                    array = CanonicalizerBase.XD.clone();
                    break;
                }
                default: {
                    if (codePoint < 128) {
                        outputStream.write(codePoint);
                        continue;
                    }
                    UtfHelpper.writeCodePointToUtf8(codePoint, outputStream);
                    continue;
                }
            }
            outputStream.write(array);
        }
    }
    
    protected Attr getNullNode(final Document document) {
        if (this.nullNode == null) {
            try {
                (this.nullNode = document.createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns")).setValue("");
            }
            catch (final Exception ex) {
                throw new RuntimeException("Unable to create nullNode: " + ex);
            }
        }
        return this.nullNode;
    }
    
    static {
        COMPARE = new AttrCompare();
        END_PI = new byte[] { 63, 62 };
        BEGIN_PI = new byte[] { 60, 63 };
        END_COMM = new byte[] { 45, 45, 62 };
        BEGIN_COMM = new byte[] { 60, 33, 45, 45 };
        XA = new byte[] { 38, 35, 120, 65, 59 };
        X9 = new byte[] { 38, 35, 120, 57, 59 };
        QUOT = new byte[] { 38, 113, 117, 111, 116, 59 };
        XD = new byte[] { 38, 35, 120, 68, 59 };
        GT = new byte[] { 38, 103, 116, 59 };
        LT = new byte[] { 38, 108, 116, 59 };
        END_TAG = new byte[] { 60, 47 };
        AMP = new byte[] { 38, 97, 109, 112, 59 };
        EQUALS_STR = new byte[] { 61, 34 };
    }
}
