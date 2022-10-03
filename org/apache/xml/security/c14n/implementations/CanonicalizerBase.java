package org.apache.xml.security.c14n.implementations;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ListIterator;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.apache.xml.security.signature.NodeFilter;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Comment;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import java.io.OutputStream;
import org.w3c.dom.Node;
import java.util.Set;
import java.util.List;
import org.w3c.dom.Attr;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.c14n.CanonicalizerSpi;

public abstract class CanonicalizerBase extends CanonicalizerSpi
{
    private static final byte[] _END_PI;
    private static final byte[] _BEGIN_PI;
    private static final byte[] _END_COMM;
    private static final byte[] _BEGIN_COMM;
    private static final byte[] __XA_;
    private static final byte[] __X9_;
    private static final byte[] _QUOT_;
    private static final byte[] __XD_;
    private static final byte[] _GT_;
    private static final byte[] _LT_;
    private static final byte[] _END_TAG;
    private static final byte[] _AMP_;
    static final AttrCompare COMPARE;
    static final String XML = "xml";
    static final String XMLNS = "xmlns";
    static final byte[] equalsStr;
    static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
    static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
    static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
    protected static final Attr nullNode;
    List nodeFilter;
    boolean _includeComments;
    Set _xpathNodeSet;
    Node _excludeNode;
    OutputStream _writer;
    
    public CanonicalizerBase(final boolean includeComments) {
        this._xpathNodeSet = null;
        this._excludeNode = null;
        this._writer = new UnsyncByteArrayOutputStream();
        this._includeComments = includeComments;
    }
    
    public byte[] engineCanonicalizeSubTree(final Node node) throws CanonicalizationException {
        return this.engineCanonicalizeSubTree(node, (Node)null);
    }
    
    public byte[] engineCanonicalizeXPathNodeSet(final Set xpathNodeSet) throws CanonicalizationException {
        this._xpathNodeSet = xpathNodeSet;
        return this.engineCanonicalizeXPathNodeSetInternal(XMLUtils.getOwnerDocument(this._xpathNodeSet));
    }
    
    public byte[] engineCanonicalize(final XMLSignatureInput xmlSignatureInput) throws CanonicalizationException {
        try {
            if (xmlSignatureInput.isExcludeComments()) {
                this._includeComments = false;
            }
            if (xmlSignatureInput.isOctetStream()) {
                return this.engineCanonicalize(xmlSignatureInput.getBytes());
            }
            if (xmlSignatureInput.isElement()) {
                return this.engineCanonicalizeSubTree(xmlSignatureInput.getSubNode(), xmlSignatureInput.getExcludeNode());
            }
            if (xmlSignatureInput.isNodeSet()) {
                this.nodeFilter = xmlSignatureInput.getNodeFilters();
                this.circumventBugIfNeeded(xmlSignatureInput);
                byte[] array;
                if (xmlSignatureInput.getSubNode() != null) {
                    array = this.engineCanonicalizeXPathNodeSetInternal(xmlSignatureInput.getSubNode());
                }
                else {
                    array = this.engineCanonicalizeXPathNodeSet(xmlSignatureInput.getNodeSet());
                }
                return array;
            }
            return null;
        }
        catch (final CanonicalizationException ex) {
            throw new CanonicalizationException("empty", ex);
        }
        catch (final ParserConfigurationException ex2) {
            throw new CanonicalizationException("empty", ex2);
        }
        catch (final IOException ex3) {
            throw new CanonicalizationException("empty", ex3);
        }
        catch (final SAXException ex4) {
            throw new CanonicalizationException("empty", ex4);
        }
    }
    
    public void setWriter(final OutputStream writer) {
        this._writer = writer;
    }
    
    byte[] engineCanonicalizeSubTree(final Node node, final Node excludeNode) throws CanonicalizationException {
        this._excludeNode = excludeNode;
        try {
            final NameSpaceSymbTable nameSpaceSymbTable = new NameSpaceSymbTable();
            int n = -1;
            if (node instanceof Element) {
                this.getParentNameSpaces((Element)node, nameSpaceSymbTable);
                n = 0;
            }
            this.canonicalizeSubTree(node, nameSpaceSymbTable, node, n);
            this._writer.close();
            if (this._writer instanceof ByteArrayOutputStream) {
                final byte[] byteArray = ((ByteArrayOutputStream)this._writer).toByteArray();
                if (super.reset) {
                    ((ByteArrayOutputStream)this._writer).reset();
                }
                return byteArray;
            }
            if (this._writer instanceof UnsyncByteArrayOutputStream) {
                final byte[] byteArray2 = ((UnsyncByteArrayOutputStream)this._writer).toByteArray();
                if (super.reset) {
                    ((UnsyncByteArrayOutputStream)this._writer).reset();
                }
                return byteArray2;
            }
            return null;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new CanonicalizationException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new CanonicalizationException("empty", ex2);
        }
    }
    
    final void canonicalizeSubTree(Node node, final NameSpaceSymbTable nameSpaceSymbTable, final Node node2, int n) throws CanonicalizationException, IOException {
        if (this.isVisibleInt(node) == -1) {
            return;
        }
        Node node3 = null;
        Node parentNode = null;
        final OutputStream writer = this._writer;
        final Node excludeNode = this._excludeNode;
        final boolean includeComments = this._includeComments;
        final HashMap hashMap = new HashMap();
        while (true) {
            switch (node.getNodeType()) {
                case 2:
                case 6:
                case 12: {
                    throw new CanonicalizationException("empty");
                }
                case 9:
                case 11: {
                    nameSpaceSymbTable.outputNodePush();
                    node3 = node.getFirstChild();
                    break;
                }
                case 8: {
                    if (includeComments) {
                        outputCommentToWriter((Comment)node, writer, n);
                        break;
                    }
                    break;
                }
                case 7: {
                    outputPItoWriter((ProcessingInstruction)node, writer, n);
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
                    final Iterator handleAttributesSubtree = this.handleAttributesSubtree(element, nameSpaceSymbTable);
                    if (handleAttributesSubtree != null) {
                        while (handleAttributesSubtree.hasNext()) {
                            final Attr attr = handleAttributesSubtree.next();
                            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, hashMap);
                        }
                    }
                    writer.write(62);
                    node3 = node.getFirstChild();
                    if (node3 != null) {
                        parentNode = element;
                        break;
                    }
                    writer.write(CanonicalizerBase._END_TAG);
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
                writer.write(CanonicalizerBase._END_TAG);
                UtfHelpper.writeByte(((Element)parentNode).getTagName(), writer, hashMap);
                writer.write(62);
                nameSpaceSymbTable.outputNodePop();
                if (parentNode == node2) {
                    return;
                }
                node3 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
                if (parentNode instanceof Element) {
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
            this._writer.close();
            if (this._writer instanceof ByteArrayOutputStream) {
                final byte[] byteArray = ((ByteArrayOutputStream)this._writer).toByteArray();
                if (super.reset) {
                    ((ByteArrayOutputStream)this._writer).reset();
                }
                return byteArray;
            }
            if (this._writer instanceof UnsyncByteArrayOutputStream) {
                final byte[] byteArray2 = ((UnsyncByteArrayOutputStream)this._writer).toByteArray();
                if (super.reset) {
                    ((UnsyncByteArrayOutputStream)this._writer).reset();
                }
                return byteArray2;
            }
            return null;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new CanonicalizationException("empty", ex);
        }
        catch (final IOException ex2) {
            throw new CanonicalizationException("empty", ex2);
        }
    }
    
    final void canonicalizeXPathNodeSet(Node node, final Node node2) throws CanonicalizationException, IOException {
        if (this.isVisibleInt(node) == -1) {
            return;
        }
        final NameSpaceSymbTable nameSpaceSymbTable = new NameSpaceSymbTable();
        if (node instanceof Element) {
            this.getParentNameSpaces((Element)node, nameSpaceSymbTable);
        }
        Node node3 = null;
        Node parentNode = null;
        final OutputStream writer = this._writer;
        int n = -1;
        final HashMap hashMap = new HashMap();
        while (true) {
            switch (node.getNodeType()) {
                case 2:
                case 6:
                case 12: {
                    throw new CanonicalizationException("empty");
                }
                case 9:
                case 11: {
                    nameSpaceSymbTable.outputNodePush();
                    node3 = node.getFirstChild();
                    break;
                }
                case 8: {
                    if (this._includeComments && this.isVisibleDO(node, nameSpaceSymbTable.getLevel()) == 1) {
                        outputCommentToWriter((Comment)node, writer, n);
                        break;
                    }
                    break;
                }
                case 7: {
                    if (this.isVisible(node)) {
                        outputPItoWriter((ProcessingInstruction)node, writer, n);
                        break;
                    }
                    break;
                }
                case 3:
                case 4: {
                    if (this.isVisible(node)) {
                        outputTextToWriter(node.getNodeValue(), writer);
                        for (Node node4 = node.getNextSibling(); node4 != null && (node4.getNodeType() == 3 || node4.getNodeType() == 4); node4 = node4.getNextSibling()) {
                            outputTextToWriter(node4.getNodeValue(), writer);
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
                        writer.write(60);
                        tagName = element.getTagName();
                        UtfHelpper.writeByte(tagName, writer, hashMap);
                    }
                    else {
                        nameSpaceSymbTable.push();
                    }
                    final Iterator handleAttributes = this.handleAttributes(element, nameSpaceSymbTable);
                    if (handleAttributes != null) {
                        while (handleAttributes.hasNext()) {
                            final Attr attr = handleAttributes.next();
                            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue(), writer, hashMap);
                        }
                    }
                    if (b) {
                        writer.write(62);
                    }
                    node3 = node.getFirstChild();
                    if (node3 != null) {
                        parentNode = element;
                        break;
                    }
                    if (b) {
                        writer.write(CanonicalizerBase._END_TAG);
                        UtfHelpper.writeByte(tagName, writer, hashMap);
                        writer.write(62);
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
                    writer.write(CanonicalizerBase._END_TAG);
                    UtfHelpper.writeByte(((Element)parentNode).getTagName(), writer, hashMap);
                    writer.write(62);
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
                if (parentNode instanceof Element) {
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
    
    int isVisibleDO(final Node node, final int n) {
        if (this.nodeFilter != null) {
            final Iterator iterator = this.nodeFilter.iterator();
            while (iterator.hasNext()) {
                final int nodeIncludeDO = ((NodeFilter)iterator.next()).isNodeIncludeDO(node, n);
                if (nodeIncludeDO != 1) {
                    return nodeIncludeDO;
                }
            }
        }
        if (this._xpathNodeSet != null && !this._xpathNodeSet.contains(node)) {
            return 0;
        }
        return 1;
    }
    
    int isVisibleInt(final Node node) {
        if (this.nodeFilter != null) {
            final Iterator iterator = this.nodeFilter.iterator();
            while (iterator.hasNext()) {
                final int nodeInclude = ((NodeFilter)iterator.next()).isNodeInclude(node);
                if (nodeInclude != 1) {
                    return nodeInclude;
                }
            }
        }
        if (this._xpathNodeSet != null && !this._xpathNodeSet.contains(node)) {
            return 0;
        }
        return 1;
    }
    
    boolean isVisible(final Node node) {
        if (this.nodeFilter != null) {
            final Iterator iterator = this.nodeFilter.iterator();
            while (iterator.hasNext()) {
                if (((NodeFilter)iterator.next()).isNodeInclude(node) != 1) {
                    return false;
                }
            }
        }
        return this._xpathNodeSet == null || this._xpathNodeSet.contains(node);
    }
    
    void handleParent(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
        if (!element.hasAttributes()) {
            return;
        }
        final NamedNodeMap attributes = element.getAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            if ("http://www.w3.org/2000/xmlns/" == attr.getNamespaceURI()) {
                final String localName = attr.getLocalName();
                final String nodeValue = attr.getNodeValue();
                if (!"xml".equals(localName) || !"http://www.w3.org/XML/1998/namespace".equals(nodeValue)) {
                    nameSpaceSymbTable.addMapping(localName, nodeValue, attr);
                }
            }
        }
    }
    
    final void getParentNameSpaces(final Element element, final NameSpaceSymbTable nameSpaceSymbTable) {
        final ArrayList list = new ArrayList(10);
        final Node parentNode = element.getParentNode();
        if (!(parentNode instanceof Element)) {
            return;
        }
        Node parentNode2;
        for (Element element2 = (Element)parentNode; element2 != null; element2 = (Element)parentNode2) {
            list.add(element2);
            parentNode2 = element2.getParentNode();
            if (!(parentNode2 instanceof Element)) {
                break;
            }
        }
        final ListIterator listIterator = list.listIterator(list.size());
        while (listIterator.hasPrevious()) {
            this.handleParent((Element)listIterator.previous(), nameSpaceSymbTable);
        }
        final Attr mappingWithoutRendered;
        if ((mappingWithoutRendered = nameSpaceSymbTable.getMappingWithoutRendered("xmlns")) != null && "".equals(mappingWithoutRendered.getValue())) {
            nameSpaceSymbTable.addMappingAndRender("xmlns", "", CanonicalizerBase.nullNode);
        }
    }
    
    abstract Iterator handleAttributes(final Element p0, final NameSpaceSymbTable p1) throws CanonicalizationException;
    
    abstract Iterator handleAttributesSubtree(final Element p0, final NameSpaceSymbTable p1) throws CanonicalizationException;
    
    abstract void circumventBugIfNeeded(final XMLSignatureInput p0) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException;
    
    static final void outputAttrToWriter(final String s, final String s2, final OutputStream outputStream, final Map map) throws IOException {
        outputStream.write(32);
        UtfHelpper.writeByte(s, outputStream, map);
        outputStream.write(CanonicalizerBase.equalsStr);
        final int length = s2.length();
        int i = 0;
        while (i < length) {
            final char char1 = s2.charAt(i++);
            byte[] array = null;
            switch (char1) {
                case 38: {
                    array = CanonicalizerBase._AMP_;
                    break;
                }
                case 60: {
                    array = CanonicalizerBase._LT_;
                    break;
                }
                case 34: {
                    array = CanonicalizerBase._QUOT_;
                    break;
                }
                case 9: {
                    array = CanonicalizerBase.__X9_;
                    break;
                }
                case 10: {
                    array = CanonicalizerBase.__XA_;
                    break;
                }
                case 13: {
                    array = CanonicalizerBase.__XD_;
                    break;
                }
                default: {
                    if (char1 < '\u0080') {
                        outputStream.write(char1);
                        continue;
                    }
                    UtfHelpper.writeCharToUtf8(char1, outputStream);
                    continue;
                }
            }
            outputStream.write(array);
        }
        outputStream.write(34);
    }
    
    static final void outputPItoWriter(final ProcessingInstruction processingInstruction, final OutputStream outputStream, final int n) throws IOException {
        if (n == 1) {
            outputStream.write(10);
        }
        outputStream.write(CanonicalizerBase._BEGIN_PI);
        final String target = processingInstruction.getTarget();
        for (int length = target.length(), i = 0; i < length; ++i) {
            final char char1 = target.charAt(i);
            if (char1 == '\r') {
                outputStream.write(CanonicalizerBase.__XD_);
            }
            else if (char1 < '\u0080') {
                outputStream.write(char1);
            }
            else {
                UtfHelpper.writeCharToUtf8(char1, outputStream);
            }
        }
        final String data = processingInstruction.getData();
        final int length2 = data.length();
        if (length2 > 0) {
            outputStream.write(32);
            for (int j = 0; j < length2; ++j) {
                final char char2 = data.charAt(j);
                if (char2 == '\r') {
                    outputStream.write(CanonicalizerBase.__XD_);
                }
                else {
                    UtfHelpper.writeCharToUtf8(char2, outputStream);
                }
            }
        }
        outputStream.write(CanonicalizerBase._END_PI);
        if (n == -1) {
            outputStream.write(10);
        }
    }
    
    static final void outputCommentToWriter(final Comment comment, final OutputStream outputStream, final int n) throws IOException {
        if (n == 1) {
            outputStream.write(10);
        }
        outputStream.write(CanonicalizerBase._BEGIN_COMM);
        final String data = comment.getData();
        for (int length = data.length(), i = 0; i < length; ++i) {
            final char char1 = data.charAt(i);
            if (char1 == '\r') {
                outputStream.write(CanonicalizerBase.__XD_);
            }
            else if (char1 < '\u0080') {
                outputStream.write(char1);
            }
            else {
                UtfHelpper.writeCharToUtf8(char1, outputStream);
            }
        }
        outputStream.write(CanonicalizerBase._END_COMM);
        if (n == -1) {
            outputStream.write(10);
        }
    }
    
    static final void outputTextToWriter(final String s, final OutputStream outputStream) throws IOException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            byte[] array = null;
            switch (char1) {
                case 38: {
                    array = CanonicalizerBase._AMP_;
                    break;
                }
                case 60: {
                    array = CanonicalizerBase._LT_;
                    break;
                }
                case 62: {
                    array = CanonicalizerBase._GT_;
                    break;
                }
                case 13: {
                    array = CanonicalizerBase.__XD_;
                    break;
                }
                default: {
                    if (char1 < '\u0080') {
                        outputStream.write(char1);
                        continue;
                    }
                    UtfHelpper.writeCharToUtf8(char1, outputStream);
                    continue;
                }
            }
            outputStream.write(array);
        }
    }
    
    static {
        _END_PI = new byte[] { 63, 62 };
        _BEGIN_PI = new byte[] { 60, 63 };
        _END_COMM = new byte[] { 45, 45, 62 };
        _BEGIN_COMM = new byte[] { 60, 33, 45, 45 };
        __XA_ = new byte[] { 38, 35, 120, 65, 59 };
        __X9_ = new byte[] { 38, 35, 120, 57, 59 };
        _QUOT_ = new byte[] { 38, 113, 117, 111, 116, 59 };
        __XD_ = new byte[] { 38, 35, 120, 68, 59 };
        _GT_ = new byte[] { 38, 103, 116, 59 };
        _LT_ = new byte[] { 38, 108, 116, 59 };
        _END_TAG = new byte[] { 60, 47 };
        _AMP_ = new byte[] { 38, 97, 109, 112, 59 };
        COMPARE = new AttrCompare();
        equalsStr = new byte[] { 61, 34 };
        try {
            (nullNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns")).setValue("");
        }
        catch (final Exception ex) {
            throw new RuntimeException("Unable to create nullNode" + ex);
        }
    }
}
