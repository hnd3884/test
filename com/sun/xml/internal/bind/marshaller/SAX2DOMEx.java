package com.sun.xml.internal.bind.marshaller;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.w3c.dom.Text;
import com.sun.xml.internal.bind.util.Which;
import org.xml.sax.Attributes;
import org.w3c.dom.Element;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import com.sun.istack.internal.FinalArrayList;
import java.util.Stack;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class SAX2DOMEx implements ContentHandler
{
    private Node node;
    private boolean isConsolidate;
    protected final Stack<Node> nodeStack;
    private final FinalArrayList<String> unprocessedNamespaces;
    protected final Document document;
    
    public SAX2DOMEx(final Node node) {
        this(node, false);
    }
    
    public SAX2DOMEx(final Node node, final boolean isConsolidate) {
        this.node = null;
        this.nodeStack = new Stack<Node>();
        this.unprocessedNamespaces = new FinalArrayList<String>();
        this.node = node;
        this.isConsolidate = isConsolidate;
        this.nodeStack.push(this.node);
        if (node instanceof Document) {
            this.document = (Document)node;
        }
        else {
            this.document = node.getOwnerDocument();
        }
    }
    
    public SAX2DOMEx(final DocumentBuilderFactory f) throws ParserConfigurationException {
        this.node = null;
        this.nodeStack = new Stack<Node>();
        this.unprocessedNamespaces = new FinalArrayList<String>();
        f.setValidating(false);
        this.document = f.newDocumentBuilder().newDocument();
        this.node = this.document;
        this.nodeStack.push(this.document);
    }
    
    @Deprecated
    public SAX2DOMEx() throws ParserConfigurationException {
        this.node = null;
        this.nodeStack = new Stack<Node>();
        this.unprocessedNamespaces = new FinalArrayList<String>();
        final DocumentBuilderFactory factory = XmlFactory.createDocumentBuilderFactory(false);
        factory.setValidating(false);
        this.document = factory.newDocumentBuilder().newDocument();
        this.node = this.document;
        this.nodeStack.push(this.document);
    }
    
    public final Element getCurrentElement() {
        return this.nodeStack.peek();
    }
    
    public Node getDOM() {
        return this.node;
    }
    
    @Override
    public void startDocument() {
    }
    
    @Override
    public void endDocument() {
    }
    
    protected void namespace(final Element element, final String prefix, final String uri) {
        String qname;
        if ("".equals(prefix) || prefix == null) {
            qname = "xmlns";
        }
        else {
            qname = "xmlns:" + prefix;
        }
        if (element.hasAttributeNS("http://www.w3.org/2000/xmlns/", qname)) {
            element.removeAttributeNS("http://www.w3.org/2000/xmlns/", qname);
        }
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", qname, uri);
    }
    
    @Override
    public void startElement(final String namespace, final String localName, final String qName, final Attributes attrs) {
        final Node parent = this.nodeStack.peek();
        final Element element = this.document.createElementNS(namespace, qName);
        if (element == null) {
            throw new AssertionError((Object)Messages.format("SAX2DOMEx.DomImplDoesntSupportCreateElementNs", this.document.getClass().getName(), Which.which(this.document.getClass())));
        }
        for (int i = 0; i < this.unprocessedNamespaces.size(); i += 2) {
            final String prefix = this.unprocessedNamespaces.get(i);
            final String uri = this.unprocessedNamespaces.get(i + 1);
            this.namespace(element, prefix, uri);
        }
        this.unprocessedNamespaces.clear();
        if (attrs != null) {
            for (int length = attrs.getLength(), j = 0; j < length; ++j) {
                final String namespaceuri = attrs.getURI(j);
                final String value = attrs.getValue(j);
                final String qname = attrs.getQName(j);
                element.setAttributeNS(namespaceuri, qname, value);
            }
        }
        parent.appendChild(element);
        this.nodeStack.push(element);
    }
    
    @Override
    public void endElement(final String namespace, final String localName, final String qName) {
        this.nodeStack.pop();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.characters(new String(ch, start, length));
    }
    
    protected Text characters(final String s) {
        final Node parent = this.nodeStack.peek();
        final Node lastChild = parent.getLastChild();
        Text text;
        if (this.isConsolidate && lastChild != null && lastChild.getNodeType() == 3) {
            text = (Text)lastChild;
            text.appendData(s);
        }
        else {
            text = this.document.createTextNode(s);
            parent.appendChild(text);
        }
        return text;
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        final Node parent = this.nodeStack.peek();
        final Node n = this.document.createProcessingInstruction(target, data);
        parent.appendChild(n);
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void skippedEntity(final String name) {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        this.unprocessedNamespaces.add(prefix);
        this.unprocessedNamespaces.add(uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) {
    }
}
