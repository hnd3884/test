package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.NotNull;
import org.w3c.dom.NodeList;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.OutputStream;
import com.sun.istack.internal.XMLStreamException2;
import org.w3c.dom.Text;
import org.w3c.dom.ProcessingInstruction;
import java.util.Collections;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Attr;
import com.sun.istack.internal.FinalArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

public class DOMStreamReader implements XMLStreamReader, NamespaceContext
{
    protected Node _current;
    private Node _start;
    private NamedNodeMap _namedNodeMap;
    protected String wholeText;
    private final FinalArrayList<Attr> _currentAttributes;
    protected Scope[] scopes;
    protected int depth;
    protected int _state;
    
    public DOMStreamReader() {
        this._currentAttributes = new FinalArrayList<Attr>();
        this.scopes = new Scope[8];
        this.depth = 0;
    }
    
    public DOMStreamReader(final Node node) {
        this._currentAttributes = new FinalArrayList<Attr>();
        this.scopes = new Scope[8];
        this.depth = 0;
        this.setCurrentNode(node);
    }
    
    public void setCurrentNode(final Node node) {
        this.scopes[0] = new Scope(null);
        this.depth = 0;
        this._current = node;
        this._start = node;
        this._state = 7;
    }
    
    @Override
    public void close() throws XMLStreamException {
    }
    
    protected void splitAttributes() {
        this._currentAttributes.clear();
        final Scope scope = this.allocateScope();
        this._namedNodeMap = this._current.getAttributes();
        if (this._namedNodeMap != null) {
            for (int n = this._namedNodeMap.getLength(), i = 0; i < n; ++i) {
                final Attr attr = (Attr)this._namedNodeMap.item(i);
                final String attrName = attr.getNodeName();
                if (attrName.startsWith("xmlns:") || attrName.equals("xmlns")) {
                    scope.currentNamespaces.add(attr);
                }
                else {
                    this._currentAttributes.add(attr);
                }
            }
        }
        this.ensureNs(this._current);
        for (int j = this._currentAttributes.size() - 1; j >= 0; --j) {
            final Attr a = this._currentAttributes.get(j);
            if (fixNull(a.getNamespaceURI()).length() > 0) {
                this.ensureNs(a);
            }
        }
    }
    
    private void ensureNs(final Node n) {
        final String prefix = fixNull(n.getPrefix());
        final String uri = fixNull(n.getNamespaceURI());
        final Scope scope = this.scopes[this.depth];
        String currentUri = scope.getNamespaceURI(prefix);
        if (prefix.length() == 0) {
            currentUri = fixNull(currentUri);
            if (currentUri.equals(uri)) {
                return;
            }
        }
        else if (currentUri != null && currentUri.equals(uri)) {
            return;
        }
        if (prefix.equals("xml") || prefix.equals("xmlns")) {
            return;
        }
        scope.additionalNamespaces.add(prefix);
        scope.additionalNamespaces.add(uri);
    }
    
    private Scope allocateScope() {
        if (this.scopes.length == ++this.depth) {
            final Scope[] newBuf = new Scope[this.scopes.length * 2];
            System.arraycopy(this.scopes, 0, newBuf, 0, this.scopes.length);
            this.scopes = newBuf;
        }
        Scope scope = this.scopes[this.depth];
        if (scope == null) {
            final Scope[] scopes = this.scopes;
            final int depth = this.depth;
            final Scope scope2 = new Scope(this.scopes[this.depth - 1]);
            scopes[depth] = scope2;
            scope = scope2;
        }
        else {
            scope.reset();
        }
        return scope;
    }
    
    @Override
    public int getAttributeCount() {
        if (this._state == 1) {
            return this._currentAttributes.size();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeCount() called in illegal state");
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        if (this._state == 1) {
            final String localName = this._currentAttributes.get(index).getLocalName();
            return (localName != null) ? localName : QName.valueOf(this._currentAttributes.get(index).getNodeName()).getLocalPart();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeLocalName() called in illegal state");
    }
    
    @Override
    public QName getAttributeName(final int index) {
        if (this._state != 1) {
            throw new IllegalStateException("DOMStreamReader: getAttributeName() called in illegal state");
        }
        final Node attr = this._currentAttributes.get(index);
        final String localName = attr.getLocalName();
        if (localName != null) {
            final String prefix = attr.getPrefix();
            final String uri = attr.getNamespaceURI();
            return new QName(fixNull(uri), localName, fixNull(prefix));
        }
        return QName.valueOf(attr.getNodeName());
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        if (this._state == 1) {
            final String uri = this._currentAttributes.get(index).getNamespaceURI();
            return fixNull(uri);
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeNamespace() called in illegal state");
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        if (this._state == 1) {
            final String prefix = this._currentAttributes.get(index).getPrefix();
            return fixNull(prefix);
        }
        throw new IllegalStateException("DOMStreamReader: getAttributePrefix() called in illegal state");
    }
    
    @Override
    public String getAttributeType(final int index) {
        if (this._state == 1) {
            return "CDATA";
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeType() called in illegal state");
    }
    
    @Override
    public String getAttributeValue(final int index) {
        if (this._state == 1) {
            return this._currentAttributes.get(index).getNodeValue();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
    }
    
    @Override
    public String getAttributeValue(final String namespaceURI, final String localName) {
        if (this._state != 1) {
            throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
        }
        if (this._namedNodeMap != null) {
            final Node attr = this._namedNodeMap.getNamedItemNS(namespaceURI, localName);
            return (attr != null) ? attr.getNodeValue() : null;
        }
        return null;
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return null;
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        throw new RuntimeException("DOMStreamReader: getElementText() not implemented");
    }
    
    @Override
    public String getEncoding() {
        return null;
    }
    
    @Override
    public int getEventType() {
        return this._state;
    }
    
    @Override
    public String getLocalName() {
        if (this._state == 1 || this._state == 2) {
            final String localName = this._current.getLocalName();
            return (localName != null) ? localName : QName.valueOf(this._current.getNodeName()).getLocalPart();
        }
        if (this._state == 9) {
            return this._current.getNodeName();
        }
        throw new IllegalStateException("DOMStreamReader: getAttributeValue() called in illegal state");
    }
    
    @Override
    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }
    
    @Override
    public QName getName() {
        if (this._state != 1 && this._state != 2) {
            throw new IllegalStateException("DOMStreamReader: getName() called in illegal state");
        }
        final String localName = this._current.getLocalName();
        if (localName != null) {
            final String prefix = this._current.getPrefix();
            final String uri = this._current.getNamespaceURI();
            return new QName(fixNull(uri), localName, fixNull(prefix));
        }
        return QName.valueOf(this._current.getNodeName());
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this;
    }
    
    private Scope getCheckedScope() {
        if (this._state == 1 || this._state == 2) {
            return this.scopes[this.depth];
        }
        throw new IllegalStateException("DOMStreamReader: neither on START_ELEMENT nor END_ELEMENT");
    }
    
    @Override
    public int getNamespaceCount() {
        return this.getCheckedScope().getNamespaceCount();
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        return this.getCheckedScope().getNamespacePrefix(index);
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        return this.getCheckedScope().getNamespaceURI(index);
    }
    
    @Override
    public String getNamespaceURI() {
        if (this._state == 1 || this._state == 2) {
            final String uri = this._current.getNamespaceURI();
            return fixNull(uri);
        }
        return null;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("DOMStreamReader: getNamespaceURI(String) call with a null prefix");
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        final String nsUri = this.scopes[this.depth].getNamespaceURI(prefix);
        if (nsUri != null) {
            return nsUri;
        }
        Node node = this.findRootElement();
        final String nsDeclName = (prefix.length() == 0) ? "xmlns" : ("xmlns:" + prefix);
        while (node.getNodeType() != 9) {
            final NamedNodeMap namedNodeMap = node.getAttributes();
            final Attr attr = (Attr)namedNodeMap.getNamedItem(nsDeclName);
            if (attr != null) {
                return attr.getValue();
            }
            node = node.getParentNode();
        }
        return null;
    }
    
    @Override
    public String getPrefix(final String nsUri) {
        if (nsUri == null) {
            throw new IllegalArgumentException("DOMStreamReader: getPrefix(String) call with a null namespace URI");
        }
        if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        String prefix = this.scopes[this.depth].getPrefix(nsUri);
        if (prefix != null) {
            return prefix;
        }
        for (Node node = this.findRootElement(); node.getNodeType() != 9; node = node.getParentNode()) {
            final NamedNodeMap namedNodeMap = node.getAttributes();
            for (int i = namedNodeMap.getLength() - 1; i >= 0; --i) {
                final Attr attr = (Attr)namedNodeMap.item(i);
                prefix = getPrefixForAttr(attr, nsUri);
                if (prefix != null) {
                    return prefix;
                }
            }
        }
        return null;
    }
    
    private Node findRootElement() {
        Node node;
        int type;
        for (node = this._start; (type = node.getNodeType()) != 9 && type != 1; node = node.getParentNode()) {}
        return node;
    }
    
    private static String getPrefixForAttr(final Attr attr, final String nsUri) {
        final String attrName = attr.getNodeName();
        if (!attrName.startsWith("xmlns:") && !attrName.equals("xmlns")) {
            return null;
        }
        if (!attr.getValue().equals(nsUri)) {
            return null;
        }
        if (attrName.equals("xmlns")) {
            return "";
        }
        final String localName = attr.getLocalName();
        return (localName != null) ? localName : QName.valueOf(attrName).getLocalPart();
    }
    
    @Override
    public Iterator getPrefixes(final String nsUri) {
        final String prefix = this.getPrefix(nsUri);
        if (prefix == null) {
            return Collections.emptyList().iterator();
        }
        return Collections.singletonList(prefix).iterator();
    }
    
    @Override
    public String getPIData() {
        if (this._state == 3) {
            return ((ProcessingInstruction)this._current).getData();
        }
        return null;
    }
    
    @Override
    public String getPITarget() {
        if (this._state == 3) {
            return ((ProcessingInstruction)this._current).getTarget();
        }
        return null;
    }
    
    @Override
    public String getPrefix() {
        if (this._state == 1 || this._state == 2) {
            final String prefix = this._current.getPrefix();
            return fixNull(prefix);
        }
        return null;
    }
    
    @Override
    public Object getProperty(final String str) throws IllegalArgumentException {
        return null;
    }
    
    @Override
    public String getText() {
        if (this._state == 4) {
            return this.wholeText;
        }
        if (this._state == 12 || this._state == 5 || this._state == 9) {
            return this._current.getNodeValue();
        }
        throw new IllegalStateException("DOMStreamReader: getTextLength() called in illegal state");
    }
    
    @Override
    public char[] getTextCharacters() {
        return this.getText().toCharArray();
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int targetLength) throws XMLStreamException {
        final String text = this.getText();
        final int copiedSize = Math.min(targetLength, text.length() - sourceStart);
        text.getChars(sourceStart, sourceStart + copiedSize, target, targetStart);
        return copiedSize;
    }
    
    @Override
    public int getTextLength() {
        return this.getText().length();
    }
    
    @Override
    public int getTextStart() {
        if (this._state == 4 || this._state == 12 || this._state == 5 || this._state == 9) {
            return 0;
        }
        throw new IllegalStateException("DOMStreamReader: getTextStart() called in illegal state");
    }
    
    @Override
    public String getVersion() {
        return null;
    }
    
    @Override
    public boolean hasName() {
        return this._state == 1 || this._state == 2;
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        return this._state != 8;
    }
    
    @Override
    public boolean hasText() {
        return (this._state == 4 || this._state == 12 || this._state == 5 || this._state == 9) && this.getText().trim().length() > 0;
    }
    
    @Override
    public boolean isAttributeSpecified(final int param) {
        return false;
    }
    
    @Override
    public boolean isCharacters() {
        return this._state == 4;
    }
    
    @Override
    public boolean isEndElement() {
        return this._state == 2;
    }
    
    @Override
    public boolean isStandalone() {
        return true;
    }
    
    @Override
    public boolean isStartElement() {
        return this._state == 1;
    }
    
    @Override
    public boolean isWhiteSpace() {
        return (this._state == 4 || this._state == 12) && this.getText().trim().length() == 0;
    }
    
    private static int mapNodeTypeToState(final int nodetype) {
        switch (nodetype) {
            case 4: {
                return 12;
            }
            case 8: {
                return 5;
            }
            case 1: {
                return 1;
            }
            case 6: {
                return 15;
            }
            case 5: {
                return 9;
            }
            case 12: {
                return 14;
            }
            case 7: {
                return 3;
            }
            case 3: {
                return 4;
            }
            default: {
                throw new RuntimeException("DOMStreamReader: Unexpected node type");
            }
        }
    }
    
    @Override
    public int next() throws XMLStreamException {
        while (true) {
            final int r = this._next();
            switch (r) {
                case 4: {
                    final Node prev = this._current.getPreviousSibling();
                    if (prev != null && prev.getNodeType() == 3) {
                        continue;
                    }
                    final Text t = (Text)this._current;
                    this.wholeText = t.getWholeText();
                    if (this.wholeText.length() == 0) {
                        continue;
                    }
                    return 4;
                }
                case 1: {
                    this.splitAttributes();
                    return 1;
                }
                default: {
                    return r;
                }
            }
        }
    }
    
    protected int _next() throws XMLStreamException {
        switch (this._state) {
            case 8: {
                throw new IllegalStateException("DOMStreamReader: Calling next() at END_DOCUMENT");
            }
            case 7: {
                if (this._current.getNodeType() == 1) {
                    return this._state = 1;
                }
                final Node child = this._current.getFirstChild();
                if (child == null) {
                    return this._state = 8;
                }
                this._current = child;
                return this._state = mapNodeTypeToState(this._current.getNodeType());
            }
            case 1: {
                final Node child = this._current.getFirstChild();
                if (child == null) {
                    return this._state = 2;
                }
                this._current = child;
                return this._state = mapNodeTypeToState(this._current.getNodeType());
            }
            case 2: {
                --this.depth;
            }
            case 3:
            case 4:
            case 5:
            case 9:
            case 12: {
                if (this._current == this._start) {
                    return this._state = 8;
                }
                final Node sibling = this._current.getNextSibling();
                if (sibling == null) {
                    this._current = this._current.getParentNode();
                    return this._state = ((this._current == null || this._current.getNodeType() == 9) ? 8 : 2);
                }
                this._current = sibling;
                return this._state = mapNodeTypeToState(this._current.getNodeType());
            }
            default: {
                throw new RuntimeException("DOMStreamReader: Unexpected internal state");
            }
        }
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        int eventType;
        for (eventType = this.next(); (eventType == 4 && this.isWhiteSpace()) || (eventType == 12 && this.isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5; eventType = this.next()) {}
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException2("DOMStreamReader: Expected start or end tag");
        }
        return eventType;
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (type != this._state) {
            throw new XMLStreamException2("DOMStreamReader: Required event type not found");
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException2("DOMStreamReader: Required namespaceURI not found");
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException2("DOMStreamReader: Required localName not found");
        }
    }
    
    @Override
    public boolean standaloneSet() {
        return true;
    }
    
    private static void displayDOM(final Node node, final OutputStream ostream) {
        try {
            System.out.println("\n====\n");
            XmlUtil.newTransformer().transform(new DOMSource(node), new StreamResult(ostream));
            System.out.println("\n====\n");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void verifyDOMIntegrity(final Node node) {
        switch (node.getNodeType()) {
            case 1:
            case 2: {
                if (node.getLocalName() == null) {
                    System.out.println("WARNING: DOM level 1 node found");
                    System.out.println(" -> node.getNodeName() = " + node.getNodeName());
                    System.out.println(" -> node.getNamespaceURI() = " + node.getNamespaceURI());
                    System.out.println(" -> node.getLocalName() = " + node.getLocalName());
                    System.out.println(" -> node.getPrefix() = " + node.getPrefix());
                }
                if (node.getNodeType() == 2) {
                    return;
                }
                final NamedNodeMap attrs = node.getAttributes();
                for (int i = 0; i < attrs.getLength(); ++i) {
                    verifyDOMIntegrity(attrs.item(i));
                }
            }
            case 9: {
                final NodeList children = node.getChildNodes();
                for (int j = 0; j < children.getLength(); ++j) {
                    verifyDOMIntegrity(children.item(j));
                }
                break;
            }
        }
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    protected static final class Scope
    {
        final Scope parent;
        final FinalArrayList<Attr> currentNamespaces;
        final FinalArrayList<String> additionalNamespaces;
        
        Scope(final Scope parent) {
            this.currentNamespaces = new FinalArrayList<Attr>();
            this.additionalNamespaces = new FinalArrayList<String>();
            this.parent = parent;
        }
        
        void reset() {
            this.currentNamespaces.clear();
            this.additionalNamespaces.clear();
        }
        
        int getNamespaceCount() {
            return this.currentNamespaces.size() + this.additionalNamespaces.size() / 2;
        }
        
        String getNamespacePrefix(final int index) {
            final int sz = this.currentNamespaces.size();
            if (index < sz) {
                final Attr attr = this.currentNamespaces.get(index);
                String result = attr.getLocalName();
                if (result == null) {
                    result = QName.valueOf(attr.getNodeName()).getLocalPart();
                }
                return result.equals("xmlns") ? null : result;
            }
            return this.additionalNamespaces.get((index - sz) * 2);
        }
        
        String getNamespaceURI(final int index) {
            final int sz = this.currentNamespaces.size();
            if (index < sz) {
                return this.currentNamespaces.get(index).getValue();
            }
            return this.additionalNamespaces.get((index - sz) * 2 + 1);
        }
        
        String getPrefix(final String nsUri) {
            for (Scope sp = this; sp != null; sp = sp.parent) {
                for (int i = sp.currentNamespaces.size() - 1; i >= 0; --i) {
                    final String result = getPrefixForAttr(sp.currentNamespaces.get(i), nsUri);
                    if (result != null) {
                        return result;
                    }
                }
                for (int i = sp.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
                    if (sp.additionalNamespaces.get(i + 1).equals(nsUri)) {
                        return sp.additionalNamespaces.get(i);
                    }
                }
            }
            return null;
        }
        
        String getNamespaceURI(@NotNull final String prefix) {
            final String nsDeclName = (prefix.length() == 0) ? "xmlns" : ("xmlns:" + prefix);
            for (Scope sp = this; sp != null; sp = sp.parent) {
                for (int i = sp.currentNamespaces.size() - 1; i >= 0; --i) {
                    final Attr a = sp.currentNamespaces.get(i);
                    if (a.getNodeName().equals(nsDeclName)) {
                        return a.getValue();
                    }
                }
                for (int i = sp.additionalNamespaces.size() - 2; i >= 0; i -= 2) {
                    if (sp.additionalNamespaces.get(i).equals(prefix)) {
                        return sp.additionalNamespaces.get(i + 1);
                    }
                }
            }
            return null;
        }
    }
}
