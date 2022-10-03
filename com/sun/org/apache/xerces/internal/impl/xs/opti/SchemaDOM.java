package com.sun.org.apache.xerces.internal.impl.xs.opti;

import java.util.Enumeration;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;

public class SchemaDOM extends DefaultDocument
{
    static final int relationsRowResizeFactor = 15;
    static final int relationsColResizeFactor = 10;
    NodeImpl[][] relations;
    ElementImpl parent;
    int currLoc;
    int nextFreeLoc;
    boolean hidden;
    boolean inCDATA;
    private StringBuffer fAnnotationBuffer;
    
    public SchemaDOM() {
        this.fAnnotationBuffer = null;
        this.reset();
    }
    
    public ElementImpl startElement(final QName element, final XMLAttributes attributes, final int line, final int column, final int offset) {
        final ElementImpl node = new ElementImpl(line, column, offset);
        this.processElement(element, attributes, node);
        return this.parent = node;
    }
    
    public ElementImpl emptyElement(final QName element, final XMLAttributes attributes, final int line, final int column, final int offset) {
        final ElementImpl node = new ElementImpl(line, column, offset);
        this.processElement(element, attributes, node);
        return node;
    }
    
    public ElementImpl startElement(final QName element, final XMLAttributes attributes, final int line, final int column) {
        return this.startElement(element, attributes, line, column, -1);
    }
    
    public ElementImpl emptyElement(final QName element, final XMLAttributes attributes, final int line, final int column) {
        return this.emptyElement(element, attributes, line, column, -1);
    }
    
    private void processElement(final QName element, final XMLAttributes attributes, final ElementImpl node) {
        node.prefix = element.prefix;
        node.localpart = element.localpart;
        node.rawname = element.rawname;
        node.uri = element.uri;
        node.schemaDOM = this;
        final Attr[] attrs = new Attr[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); ++i) {
            attrs[i] = new AttrImpl(node, attributes.getPrefix(i), attributes.getLocalName(i), attributes.getQName(i), attributes.getURI(i), attributes.getValue(i));
        }
        node.attrs = attrs;
        if (this.nextFreeLoc == this.relations.length) {
            this.resizeRelations();
        }
        if (this.relations[this.currLoc][0] != this.parent) {
            this.relations[this.nextFreeLoc][0] = this.parent;
            this.currLoc = this.nextFreeLoc++;
        }
        boolean foundPlace = false;
        int j;
        for (j = 1, j = 1; j < this.relations[this.currLoc].length; ++j) {
            if (this.relations[this.currLoc][j] == null) {
                foundPlace = true;
                break;
            }
        }
        if (!foundPlace) {
            this.resizeRelations(this.currLoc);
        }
        this.relations[this.currLoc][j] = node;
        this.parent.parentRow = this.currLoc;
        node.row = this.currLoc;
        node.col = j;
    }
    
    public void endElement() {
        this.currLoc = this.parent.row;
        this.parent = (ElementImpl)this.relations[this.currLoc][0];
    }
    
    void comment(final XMLString text) {
        this.fAnnotationBuffer.append("<!--");
        if (text.length > 0) {
            this.fAnnotationBuffer.append(text.ch, text.offset, text.length);
        }
        this.fAnnotationBuffer.append("-->");
    }
    
    void processingInstruction(final String target, final XMLString data) {
        this.fAnnotationBuffer.append("<?").append(target);
        if (data.length > 0) {
            this.fAnnotationBuffer.append(' ').append(data.ch, data.offset, data.length);
        }
        this.fAnnotationBuffer.append("?>");
    }
    
    void characters(final XMLString text) {
        if (!this.inCDATA) {
            final StringBuffer annotationBuffer = this.fAnnotationBuffer;
            for (int i = text.offset; i < text.offset + text.length; ++i) {
                final char ch = text.ch[i];
                if (ch == '&') {
                    annotationBuffer.append("&amp;");
                }
                else if (ch == '<') {
                    annotationBuffer.append("&lt;");
                }
                else if (ch == '>') {
                    annotationBuffer.append("&gt;");
                }
                else if (ch == '\r') {
                    annotationBuffer.append("&#xD;");
                }
                else {
                    annotationBuffer.append(ch);
                }
            }
        }
        else {
            this.fAnnotationBuffer.append(text.ch, text.offset, text.length);
        }
    }
    
    void charactersRaw(final String text) {
        this.fAnnotationBuffer.append(text);
    }
    
    void endAnnotation(final QName elemName, final ElementImpl annotation) {
        this.fAnnotationBuffer.append("\n</").append(elemName.rawname).append(">");
        annotation.fAnnotation = this.fAnnotationBuffer.toString();
        this.fAnnotationBuffer = null;
    }
    
    void endAnnotationElement(final QName elemName) {
        this.endAnnotationElement(elemName.rawname);
    }
    
    void endAnnotationElement(final String elemRawName) {
        this.fAnnotationBuffer.append("</").append(elemRawName).append(">");
    }
    
    void endSyntheticAnnotationElement(final QName elemName, final boolean complete) {
        this.endSyntheticAnnotationElement(elemName.rawname, complete);
    }
    
    void endSyntheticAnnotationElement(final String elemRawName, final boolean complete) {
        if (complete) {
            this.fAnnotationBuffer.append("\n</").append(elemRawName).append(">");
            this.parent.fSyntheticAnnotation = this.fAnnotationBuffer.toString();
            this.fAnnotationBuffer = null;
        }
        else {
            this.fAnnotationBuffer.append("</").append(elemRawName).append(">");
        }
    }
    
    void startAnnotationCDATA() {
        this.inCDATA = true;
        this.fAnnotationBuffer.append("<![CDATA[");
    }
    
    void endAnnotationCDATA() {
        this.fAnnotationBuffer.append("]]>");
        this.inCDATA = false;
    }
    
    private void resizeRelations() {
        final NodeImpl[][] temp = new NodeImpl[this.relations.length + 15][];
        System.arraycopy(this.relations, 0, temp, 0, this.relations.length);
        for (int i = this.relations.length; i < temp.length; ++i) {
            temp[i] = new NodeImpl[10];
        }
        this.relations = temp;
    }
    
    private void resizeRelations(final int i) {
        final NodeImpl[] temp = new NodeImpl[this.relations[i].length + 10];
        System.arraycopy(this.relations[i], 0, temp, 0, this.relations[i].length);
        this.relations[i] = temp;
    }
    
    public void reset() {
        if (this.relations != null) {
            for (int i = 0; i < this.relations.length; ++i) {
                for (int j = 0; j < this.relations[i].length; ++j) {
                    this.relations[i][j] = null;
                }
            }
        }
        this.relations = new NodeImpl[15][];
        this.parent = new ElementImpl(0, 0, 0);
        this.parent.rawname = "DOCUMENT_NODE";
        this.currLoc = 0;
        this.nextFreeLoc = 1;
        this.inCDATA = false;
        for (int i = 0; i < 15; ++i) {
            this.relations[i] = new NodeImpl[10];
        }
        this.relations[this.currLoc][0] = this.parent;
    }
    
    public void printDOM() {
    }
    
    public static void traverse(final Node node, int depth) {
        indent(depth);
        System.out.print("<" + node.getNodeName());
        if (node.hasAttributes()) {
            final NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); ++i) {
                System.out.print("  " + ((Attr)attrs.item(i)).getName() + "=\"" + ((Attr)attrs.item(i)).getValue() + "\"");
            }
        }
        if (node.hasChildNodes()) {
            System.out.println(">");
            depth += 4;
            for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                traverse(child, depth);
            }
            depth -= 4;
            indent(depth);
            System.out.println("</" + node.getNodeName() + ">");
        }
        else {
            System.out.println("/>");
        }
    }
    
    public static void indent(final int amount) {
        for (int i = 0; i < amount; ++i) {
            System.out.print(' ');
        }
    }
    
    @Override
    public Element getDocumentElement() {
        return (ElementImpl)this.relations[0][1];
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return SchemaDOMImplementation.getDOMImplementation();
    }
    
    void startAnnotation(final QName elemName, final XMLAttributes attributes, final NamespaceContext namespaceContext) {
        this.startAnnotation(elemName.rawname, attributes, namespaceContext);
    }
    
    void startAnnotation(final String elemRawName, final XMLAttributes attributes, final NamespaceContext namespaceContext) {
        if (this.fAnnotationBuffer == null) {
            this.fAnnotationBuffer = new StringBuffer(256);
        }
        this.fAnnotationBuffer.append("<").append(elemRawName).append(" ");
        final ArrayList namespaces = new ArrayList();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String aValue = attributes.getValue(i);
            final String aPrefix = attributes.getPrefix(i);
            final String aQName = attributes.getQName(i);
            if (aPrefix == XMLSymbols.PREFIX_XMLNS || aQName == XMLSymbols.PREFIX_XMLNS) {
                namespaces.add((aPrefix == XMLSymbols.PREFIX_XMLNS) ? attributes.getLocalName(i) : XMLSymbols.EMPTY_STRING);
            }
            this.fAnnotationBuffer.append(aQName).append("=\"").append(processAttValue(aValue)).append("\" ");
        }
        final Enumeration currPrefixes = namespaceContext.getAllPrefixes();
        while (currPrefixes.hasMoreElements()) {
            final String prefix = currPrefixes.nextElement();
            String uri = namespaceContext.getURI(prefix);
            if (uri == null) {
                uri = XMLSymbols.EMPTY_STRING;
            }
            if (!namespaces.contains(prefix)) {
                if (prefix == XMLSymbols.EMPTY_STRING) {
                    this.fAnnotationBuffer.append("xmlns").append("=\"").append(processAttValue(uri)).append("\" ");
                }
                else {
                    this.fAnnotationBuffer.append("xmlns:").append(prefix).append("=\"").append(processAttValue(uri)).append("\" ");
                }
            }
        }
        this.fAnnotationBuffer.append(">\n");
    }
    
    void startAnnotationElement(final QName elemName, final XMLAttributes attributes) {
        this.startAnnotationElement(elemName.rawname, attributes);
    }
    
    void startAnnotationElement(final String elemRawName, final XMLAttributes attributes) {
        this.fAnnotationBuffer.append("<").append(elemRawName);
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String aValue = attributes.getValue(i);
            this.fAnnotationBuffer.append(" ").append(attributes.getQName(i)).append("=\"").append(processAttValue(aValue)).append("\"");
        }
        this.fAnnotationBuffer.append(">");
    }
    
    private static String processAttValue(final String original) {
        for (int length = original.length(), i = 0; i < length; ++i) {
            final char currChar = original.charAt(i);
            if (currChar == '\"' || currChar == '<' || currChar == '&' || currChar == '\t' || currChar == '\n' || currChar == '\r') {
                return escapeAttValue(original, i);
            }
        }
        return original;
    }
    
    private static String escapeAttValue(final String original, final int from) {
        final int length = original.length();
        final StringBuffer newVal = new StringBuffer(length);
        newVal.append(original.substring(0, from));
        for (int i = from; i < length; ++i) {
            final char currChar = original.charAt(i);
            if (currChar == '\"') {
                newVal.append("&quot;");
            }
            else if (currChar == '<') {
                newVal.append("&lt;");
            }
            else if (currChar == '&') {
                newVal.append("&amp;");
            }
            else if (currChar == '\t') {
                newVal.append("&#x9;");
            }
            else if (currChar == '\n') {
                newVal.append("&#xA;");
            }
            else if (currChar == '\r') {
                newVal.append("&#xD;");
            }
            else {
                newVal.append(currChar);
            }
        }
        return newVal.toString();
    }
}
