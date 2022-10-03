package com.sun.xml.internal.stream.writers;

import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import java.lang.reflect.Method;
import org.xml.sax.helpers.NamespaceSupport;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.stream.XMLStreamWriter;

public class XMLDOMWriterImpl implements XMLStreamWriter
{
    private Document ownerDoc;
    private Node currentNode;
    private Node node;
    private NamespaceSupport namespaceContext;
    private Method mXmlVersion;
    private boolean[] needContextPop;
    private StringBuffer stringBuffer;
    private int resizeValue;
    private int depth;
    
    public XMLDOMWriterImpl(final DOMResult result) {
        this.ownerDoc = null;
        this.currentNode = null;
        this.node = null;
        this.namespaceContext = null;
        this.mXmlVersion = null;
        this.needContextPop = null;
        this.stringBuffer = null;
        this.resizeValue = 20;
        this.depth = 0;
        this.node = result.getNode();
        if (this.node.getNodeType() == 9) {
            this.ownerDoc = (Document)this.node;
            this.currentNode = this.ownerDoc;
        }
        else {
            this.ownerDoc = this.node.getOwnerDocument();
            this.currentNode = this.node;
        }
        this.getDLThreeMethods();
        this.stringBuffer = new StringBuffer();
        this.needContextPop = new boolean[this.resizeValue];
        this.namespaceContext = new NamespaceSupport();
    }
    
    private void getDLThreeMethods() {
        try {
            this.mXmlVersion = this.ownerDoc.getClass().getMethod("setXmlVersion", String.class);
        }
        catch (final NoSuchMethodException mex) {
            this.mXmlVersion = null;
        }
        catch (final SecurityException se) {
            this.mXmlVersion = null;
        }
    }
    
    @Override
    public void close() throws XMLStreamException {
    }
    
    @Override
    public void flush() throws XMLStreamException {
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return null;
    }
    
    @Override
    public String getPrefix(final String namespaceURI) throws XMLStreamException {
        String prefix = null;
        if (this.namespaceContext != null) {
            prefix = this.namespaceContext.getPrefix(namespaceURI);
        }
        return prefix;
    }
    
    @Override
    public Object getProperty(final String str) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.namespaceContext.declarePrefix("", uri);
        if (!this.needContextPop[this.depth]) {
            this.needContextPop[this.depth] = true;
        }
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new XMLStreamException("Prefix cannot be null");
        }
        this.namespaceContext.declarePrefix(prefix, uri);
        if (!this.needContextPop[this.depth]) {
            this.needContextPop[this.depth] = true;
        }
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        if (this.currentNode.getNodeType() == 1) {
            final Attr attr = this.ownerDoc.createAttribute(localName);
            attr.setValue(value);
            ((Element)this.currentNode).setAttributeNode(attr);
            return;
        }
        throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        if (this.currentNode.getNodeType() != 1) {
            throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
        }
        String prefix = null;
        if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
        }
        if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
        }
        if (this.namespaceContext != null) {
            prefix = this.namespaceContext.getPrefix(namespaceURI);
        }
        if (prefix == null) {
            throw new XMLStreamException("Namespace URI " + namespaceURI + "is not bound to any prefix");
        }
        String qualifiedName = null;
        if (prefix.equals("")) {
            qualifiedName = localName;
        }
        else {
            qualifiedName = this.getQName(prefix, localName);
        }
        final Attr attr = this.ownerDoc.createAttributeNS(namespaceURI, qualifiedName);
        attr.setValue(value);
        ((Element)this.currentNode).setAttributeNode(attr);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        if (this.currentNode.getNodeType() != 1) {
            throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
        }
        if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
        }
        if (localName == null) {
            throw new XMLStreamException("Local name cannot be null");
        }
        if (prefix == null) {
            throw new XMLStreamException("prefix cannot be null");
        }
        String qualifiedName = null;
        if (prefix.equals("")) {
            qualifiedName = localName;
        }
        else {
            qualifiedName = this.getQName(prefix, localName);
        }
        final Attr attr = this.ownerDoc.createAttributeNS(namespaceURI, qualifiedName);
        attr.setValue(value);
        ((Element)this.currentNode).setAttributeNodeNS(attr);
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        if (data == null) {
            throw new XMLStreamException("CDATA cannot be null");
        }
        final CDATASection cdata = this.ownerDoc.createCDATASection(data);
        this.getNode().appendChild(cdata);
    }
    
    @Override
    public void writeCharacters(final String charData) throws XMLStreamException {
        final Text text = this.ownerDoc.createTextNode(charData);
        this.currentNode.appendChild(text);
    }
    
    @Override
    public void writeCharacters(final char[] values, final int param, final int param2) throws XMLStreamException {
        final Text text = this.ownerDoc.createTextNode(new String(values, param, param2));
        this.currentNode.appendChild(text);
    }
    
    @Override
    public void writeComment(final String str) throws XMLStreamException {
        final Comment comment = this.ownerDoc.createComment(str);
        this.getNode().appendChild(comment);
    }
    
    @Override
    public void writeDTD(final String str) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        if (this.currentNode.getNodeType() == 1) {
            final String qname = "xmlns";
            ((Element)this.currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", qname, namespaceURI);
            return;
        }
        throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        if (this.ownerDoc != null) {
            final Element element = this.ownerDoc.createElement(localName);
            if (this.currentNode != null) {
                this.currentNode.appendChild(element);
            }
            else {
                this.ownerDoc.appendChild(element);
            }
        }
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        if (this.ownerDoc != null) {
            String qualifiedName = null;
            String prefix = null;
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (localName == null) {
                throw new XMLStreamException("Local name cannot be null");
            }
            if (this.namespaceContext != null) {
                prefix = this.namespaceContext.getPrefix(namespaceURI);
            }
            if (prefix == null) {
                throw new XMLStreamException("Namespace URI " + namespaceURI + "is not bound to any prefix");
            }
            if ("".equals(prefix)) {
                qualifiedName = localName;
            }
            else {
                qualifiedName = this.getQName(prefix, localName);
            }
            final Element element = this.ownerDoc.createElementNS(namespaceURI, qualifiedName);
            if (this.currentNode != null) {
                this.currentNode.appendChild(element);
            }
            else {
                this.ownerDoc.appendChild(element);
            }
        }
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        if (this.ownerDoc != null) {
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (localName == null) {
                throw new XMLStreamException("Local name cannot be null");
            }
            if (prefix == null) {
                throw new XMLStreamException("Prefix cannot be null");
            }
            String qualifiedName = null;
            if ("".equals(prefix)) {
                qualifiedName = localName;
            }
            else {
                qualifiedName = this.getQName(prefix, localName);
            }
            final Element el = this.ownerDoc.createElementNS(namespaceURI, qualifiedName);
            if (this.currentNode != null) {
                this.currentNode.appendChild(el);
            }
            else {
                this.ownerDoc.appendChild(el);
            }
        }
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.currentNode = null;
        for (int i = 0; i < this.depth; ++i) {
            if (this.needContextPop[this.depth]) {
                this.needContextPop[this.depth] = false;
                this.namespaceContext.popContext();
            }
            --this.depth;
        }
        this.depth = 0;
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        final Node node = this.currentNode.getParentNode();
        if (this.currentNode.getNodeType() == 9) {
            this.currentNode = null;
        }
        else {
            this.currentNode = node;
        }
        if (this.needContextPop[this.depth]) {
            this.needContextPop[this.depth] = false;
            this.namespaceContext.popContext();
        }
        --this.depth;
    }
    
    @Override
    public void writeEntityRef(final String name) throws XMLStreamException {
        final EntityReference er = this.ownerDoc.createEntityReference(name);
        this.currentNode.appendChild(er);
    }
    
    @Override
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        if (prefix == null) {
            throw new XMLStreamException("prefix cannot be null");
        }
        if (namespaceURI == null) {
            throw new XMLStreamException("NamespaceURI cannot be null");
        }
        String qname = null;
        if (prefix.equals("")) {
            qname = "xmlns";
        }
        else {
            qname = this.getQName("xmlns", prefix);
        }
        ((Element)this.currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", qname, namespaceURI);
    }
    
    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        if (target == null) {
            throw new XMLStreamException("Target cannot be null");
        }
        final ProcessingInstruction pi = this.ownerDoc.createProcessingInstruction(target, "");
        this.currentNode.appendChild(pi);
    }
    
    @Override
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        if (target == null) {
            throw new XMLStreamException("Target cannot be null");
        }
        final ProcessingInstruction pi = this.ownerDoc.createProcessingInstruction(target, data);
        this.currentNode.appendChild(pi);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        try {
            if (this.mXmlVersion != null) {
                this.mXmlVersion.invoke(this.ownerDoc, "1.0");
            }
        }
        catch (final IllegalAccessException iae) {
            throw new XMLStreamException(iae);
        }
        catch (final InvocationTargetException ite) {
            throw new XMLStreamException(ite);
        }
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        try {
            if (this.mXmlVersion != null) {
                this.mXmlVersion.invoke(this.ownerDoc, version);
            }
        }
        catch (final IllegalAccessException iae) {
            throw new XMLStreamException(iae);
        }
        catch (final InvocationTargetException ite) {
            throw new XMLStreamException(ite);
        }
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        try {
            if (this.mXmlVersion != null) {
                this.mXmlVersion.invoke(this.ownerDoc, version);
            }
        }
        catch (final IllegalAccessException iae) {
            throw new XMLStreamException(iae);
        }
        catch (final InvocationTargetException ite) {
            throw new XMLStreamException(ite);
        }
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        if (this.ownerDoc != null) {
            final Element element = this.ownerDoc.createElement(localName);
            if (this.currentNode != null) {
                this.currentNode.appendChild(element);
            }
            else {
                this.ownerDoc.appendChild(element);
            }
            this.currentNode = element;
        }
        if (this.needContextPop[this.depth]) {
            this.namespaceContext.pushContext();
        }
        this.incDepth();
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        if (this.ownerDoc != null) {
            String qualifiedName = null;
            String prefix = null;
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (localName == null) {
                throw new XMLStreamException("Local name cannot be null");
            }
            if (this.namespaceContext != null) {
                prefix = this.namespaceContext.getPrefix(namespaceURI);
            }
            if (prefix == null) {
                throw new XMLStreamException("Namespace URI " + namespaceURI + "is not bound to any prefix");
            }
            if ("".equals(prefix)) {
                qualifiedName = localName;
            }
            else {
                qualifiedName = this.getQName(prefix, localName);
            }
            final Element element = this.ownerDoc.createElementNS(namespaceURI, qualifiedName);
            if (this.currentNode != null) {
                this.currentNode.appendChild(element);
            }
            else {
                this.ownerDoc.appendChild(element);
            }
            this.currentNode = element;
        }
        if (this.needContextPop[this.depth]) {
            this.namespaceContext.pushContext();
        }
        this.incDepth();
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        if (this.ownerDoc != null) {
            String qname = null;
            if (namespaceURI == null) {
                throw new XMLStreamException("NamespaceURI cannot be null");
            }
            if (localName == null) {
                throw new XMLStreamException("Local name cannot be null");
            }
            if (prefix == null) {
                throw new XMLStreamException("Prefix cannot be null");
            }
            if (prefix.equals("")) {
                qname = localName;
            }
            else {
                qname = this.getQName(prefix, localName);
            }
            final Element el = this.ownerDoc.createElementNS(namespaceURI, qname);
            if (this.currentNode != null) {
                this.currentNode.appendChild(el);
            }
            else {
                this.ownerDoc.appendChild(el);
            }
            this.currentNode = el;
            if (this.needContextPop[this.depth]) {
                this.namespaceContext.pushContext();
            }
            this.incDepth();
        }
    }
    
    private String getQName(final String prefix, final String localName) {
        this.stringBuffer.setLength(0);
        this.stringBuffer.append(prefix);
        this.stringBuffer.append(":");
        this.stringBuffer.append(localName);
        return this.stringBuffer.toString();
    }
    
    private Node getNode() {
        if (this.currentNode == null) {
            return this.ownerDoc;
        }
        return this.currentNode;
    }
    
    private void incDepth() {
        ++this.depth;
        if (this.depth == this.needContextPop.length) {
            final boolean[] array = new boolean[this.depth + this.resizeValue];
            System.arraycopy(this.needContextPop, 0, array, 0, this.depth);
            this.needContextPop = array;
        }
    }
}
