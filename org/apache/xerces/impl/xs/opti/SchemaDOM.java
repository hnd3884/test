package org.apache.xerces.impl.xs.opti;

import java.util.Enumeration;
import org.apache.xerces.util.XMLSymbols;
import java.util.ArrayList;
import org.apache.xerces.xni.NamespaceContext;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.apache.xerces.xni.XMLString;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

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
    
    public ElementImpl startElement(final QName qName, final XMLAttributes xmlAttributes, final int n, final int n2, final int n3) {
        final ElementImpl parent = new ElementImpl(n, n2, n3);
        this.processElement(qName, xmlAttributes, parent);
        return this.parent = parent;
    }
    
    public ElementImpl emptyElement(final QName qName, final XMLAttributes xmlAttributes, final int n, final int n2, final int n3) {
        final ElementImpl elementImpl = new ElementImpl(n, n2, n3);
        this.processElement(qName, xmlAttributes, elementImpl);
        return elementImpl;
    }
    
    public ElementImpl startElement(final QName qName, final XMLAttributes xmlAttributes, final int n, final int n2) {
        return this.startElement(qName, xmlAttributes, n, n2, -1);
    }
    
    public ElementImpl emptyElement(final QName qName, final XMLAttributes xmlAttributes, final int n, final int n2) {
        return this.emptyElement(qName, xmlAttributes, n, n2, -1);
    }
    
    private void processElement(final QName qName, final XMLAttributes xmlAttributes, final ElementImpl elementImpl) {
        elementImpl.prefix = qName.prefix;
        elementImpl.localpart = qName.localpart;
        elementImpl.rawname = qName.rawname;
        elementImpl.uri = qName.uri;
        elementImpl.schemaDOM = this;
        final Attr[] attrs = new Attr[xmlAttributes.getLength()];
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            attrs[i] = new AttrImpl(elementImpl, xmlAttributes.getPrefix(i), xmlAttributes.getLocalName(i), xmlAttributes.getQName(i), xmlAttributes.getURI(i), xmlAttributes.getValue(i));
        }
        elementImpl.attrs = attrs;
        if (this.nextFreeLoc == this.relations.length) {
            this.resizeRelations();
        }
        if (this.relations[this.currLoc][0] != this.parent) {
            this.relations[this.nextFreeLoc][0] = this.parent;
            this.currLoc = this.nextFreeLoc++;
        }
        boolean b = false;
        int j;
        for (j = 1; j < this.relations[this.currLoc].length; ++j) {
            if (this.relations[this.currLoc][j] == null) {
                b = true;
                break;
            }
        }
        if (!b) {
            this.resizeRelations(this.currLoc);
        }
        this.relations[this.currLoc][j] = elementImpl;
        this.parent.parentRow = this.currLoc;
        elementImpl.row = this.currLoc;
        elementImpl.col = j;
    }
    
    public void endElement() {
        this.currLoc = this.parent.row;
        this.parent = (ElementImpl)this.relations[this.currLoc][0];
    }
    
    void comment(final XMLString xmlString) {
        this.fAnnotationBuffer.append("<!--");
        if (xmlString.length > 0) {
            this.fAnnotationBuffer.append(xmlString.ch, xmlString.offset, xmlString.length);
        }
        this.fAnnotationBuffer.append("-->");
    }
    
    void processingInstruction(final String s, final XMLString xmlString) {
        this.fAnnotationBuffer.append("<?").append(s);
        if (xmlString.length > 0) {
            this.fAnnotationBuffer.append(' ').append(xmlString.ch, xmlString.offset, xmlString.length);
        }
        this.fAnnotationBuffer.append("?>");
    }
    
    void characters(final XMLString xmlString) {
        if (!this.inCDATA) {
            final StringBuffer fAnnotationBuffer = this.fAnnotationBuffer;
            for (int i = xmlString.offset; i < xmlString.offset + xmlString.length; ++i) {
                final char c = xmlString.ch[i];
                if (c == '&') {
                    fAnnotationBuffer.append("&amp;");
                }
                else if (c == '<') {
                    fAnnotationBuffer.append("&lt;");
                }
                else if (c == '>') {
                    fAnnotationBuffer.append("&gt;");
                }
                else if (c == '\r') {
                    fAnnotationBuffer.append("&#xD;");
                }
                else {
                    fAnnotationBuffer.append(c);
                }
            }
        }
        else {
            this.fAnnotationBuffer.append(xmlString.ch, xmlString.offset, xmlString.length);
        }
    }
    
    void charactersRaw(final String s) {
        this.fAnnotationBuffer.append(s);
    }
    
    void endAnnotation(final QName qName, final ElementImpl elementImpl) {
        this.fAnnotationBuffer.append("\n</").append(qName.rawname).append(">");
        elementImpl.fAnnotation = this.fAnnotationBuffer.toString();
        this.fAnnotationBuffer = null;
    }
    
    void endAnnotationElement(final QName qName) {
        this.endAnnotationElement(qName.rawname);
    }
    
    void endAnnotationElement(final String s) {
        this.fAnnotationBuffer.append("</").append(s).append(">");
    }
    
    void endSyntheticAnnotationElement(final QName qName, final boolean b) {
        this.endSyntheticAnnotationElement(qName.rawname, b);
    }
    
    void endSyntheticAnnotationElement(final String s, final boolean b) {
        if (b) {
            this.fAnnotationBuffer.append("\n</").append(s).append(">");
            this.parent.fSyntheticAnnotation = this.fAnnotationBuffer.toString();
            this.fAnnotationBuffer = null;
        }
        else {
            this.fAnnotationBuffer.append("</").append(s).append(">");
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
        final NodeImpl[][] relations = new NodeImpl[this.relations.length + 15][];
        System.arraycopy(this.relations, 0, relations, 0, this.relations.length);
        for (int i = this.relations.length; i < relations.length; ++i) {
            relations[i] = new NodeImpl[10];
        }
        this.relations = relations;
    }
    
    private void resizeRelations(final int n) {
        final NodeImpl[] array = new NodeImpl[this.relations[n].length + 10];
        System.arraycopy(this.relations[n], 0, array, 0, this.relations[n].length);
        this.relations[n] = array;
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
        for (int k = 0; k < 15; ++k) {
            this.relations[k] = new NodeImpl[10];
        }
        this.relations[this.currLoc][0] = this.parent;
    }
    
    public void printDOM() {
    }
    
    public static void traverse(final Node node, int n) {
        indent(n);
        System.out.print("<" + node.getNodeName());
        if (node.hasAttributes()) {
            final NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                System.out.print("  " + ((Attr)attributes.item(i)).getName() + "=\"" + ((Attr)attributes.item(i)).getValue() + "\"");
            }
        }
        if (node.hasChildNodes()) {
            System.out.println(">");
            n += 4;
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                traverse(node2, n);
            }
            n -= 4;
            indent(n);
            System.out.println("</" + node.getNodeName() + ">");
        }
        else {
            System.out.println("/>");
        }
    }
    
    public static void indent(final int n) {
        for (int i = 0; i < n; ++i) {
            System.out.print(' ');
        }
    }
    
    public Element getDocumentElement() {
        return (ElementImpl)this.relations[0][1];
    }
    
    public DOMImplementation getImplementation() {
        return SchemaDOMImplementation.getDOMImplementation();
    }
    
    void startAnnotation(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) {
        this.startAnnotation(qName.rawname, xmlAttributes, namespaceContext);
    }
    
    void startAnnotation(final String s, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) {
        if (this.fAnnotationBuffer == null) {
            this.fAnnotationBuffer = new StringBuffer(256);
        }
        this.fAnnotationBuffer.append("<").append(s).append(" ");
        final ArrayList list = new ArrayList();
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            final String value = xmlAttributes.getValue(i);
            final String prefix = xmlAttributes.getPrefix(i);
            final String qName = xmlAttributes.getQName(i);
            if (prefix == XMLSymbols.PREFIX_XMLNS || qName == XMLSymbols.PREFIX_XMLNS) {
                list.add((prefix == XMLSymbols.PREFIX_XMLNS) ? xmlAttributes.getLocalName(i) : XMLSymbols.EMPTY_STRING);
            }
            this.fAnnotationBuffer.append(qName).append("=\"").append(processAttValue(value)).append("\" ");
        }
        final Enumeration allPrefixes = namespaceContext.getAllPrefixes();
        while (allPrefixes.hasMoreElements()) {
            final String s2 = allPrefixes.nextElement();
            String s3 = namespaceContext.getURI(s2);
            if (s3 == null) {
                s3 = XMLSymbols.EMPTY_STRING;
            }
            if (!list.contains(s2)) {
                if (s2 == XMLSymbols.EMPTY_STRING) {
                    this.fAnnotationBuffer.append("xmlns").append("=\"").append(processAttValue(s3)).append("\" ");
                }
                else {
                    this.fAnnotationBuffer.append("xmlns:").append(s2).append("=\"").append(processAttValue(s3)).append("\" ");
                }
            }
        }
        this.fAnnotationBuffer.append(">\n");
    }
    
    void startAnnotationElement(final QName qName, final XMLAttributes xmlAttributes) {
        this.startAnnotationElement(qName.rawname, xmlAttributes);
    }
    
    void startAnnotationElement(final String s, final XMLAttributes xmlAttributes) {
        this.fAnnotationBuffer.append("<").append(s);
        for (int i = 0; i < xmlAttributes.getLength(); ++i) {
            this.fAnnotationBuffer.append(" ").append(xmlAttributes.getQName(i)).append("=\"").append(processAttValue(xmlAttributes.getValue(i))).append("\"");
        }
        this.fAnnotationBuffer.append(">");
    }
    
    private static String processAttValue(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"' || char1 == '<' || char1 == '&' || char1 == '\t' || char1 == '\n' || char1 == '\r') {
                return escapeAttValue(s, i);
            }
        }
        return s;
    }
    
    private static String escapeAttValue(final String s, final int n) {
        final int length = s.length();
        final StringBuffer sb = new StringBuffer(length);
        sb.append(s.substring(0, n));
        for (int i = n; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"') {
                sb.append("&quot;");
            }
            else if (char1 == '<') {
                sb.append("&lt;");
            }
            else if (char1 == '&') {
                sb.append("&amp;");
            }
            else if (char1 == '\t') {
                sb.append("&#x9;");
            }
            else if (char1 == '\n') {
                sb.append("&#xA;");
            }
            else if (char1 == '\r') {
                sb.append("&#xD;");
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
}
