package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;

public class ElementImpl extends DefaultElement
{
    SchemaDOM schemaDOM;
    Attr[] attrs;
    int row;
    int col;
    int parentRow;
    int line;
    int column;
    int charOffset;
    String fAnnotation;
    String fSyntheticAnnotation;
    
    public ElementImpl(final int line, final int column, final int offset) {
        this.row = -1;
        this.col = -1;
        this.parentRow = -1;
        this.nodeType = 1;
        this.line = line;
        this.column = column;
        this.charOffset = offset;
    }
    
    public ElementImpl(final int line, final int column) {
        this(line, column, -1);
    }
    
    public ElementImpl(final String prefix, final String localpart, final String rawname, final String uri, final int line, final int column, final int offset) {
        super(prefix, localpart, rawname, uri, (short)1);
        this.row = -1;
        this.col = -1;
        this.parentRow = -1;
        this.line = line;
        this.column = column;
        this.charOffset = offset;
    }
    
    public ElementImpl(final String prefix, final String localpart, final String rawname, final String uri, final int line, final int column) {
        this(prefix, localpart, rawname, uri, line, column, -1);
    }
    
    @Override
    public Document getOwnerDocument() {
        return this.schemaDOM;
    }
    
    @Override
    public Node getParentNode() {
        return this.schemaDOM.relations[this.row][0];
    }
    
    @Override
    public boolean hasChildNodes() {
        return this.parentRow != -1;
    }
    
    @Override
    public Node getFirstChild() {
        if (this.parentRow == -1) {
            return null;
        }
        return this.schemaDOM.relations[this.parentRow][1];
    }
    
    @Override
    public Node getLastChild() {
        if (this.parentRow == -1) {
            return null;
        }
        int i;
        for (i = 1; i < this.schemaDOM.relations[this.parentRow].length; ++i) {
            if (this.schemaDOM.relations[this.parentRow][i] == null) {
                return this.schemaDOM.relations[this.parentRow][i - 1];
            }
        }
        if (i == 1) {
            ++i;
        }
        return this.schemaDOM.relations[this.parentRow][i - 1];
    }
    
    @Override
    public Node getPreviousSibling() {
        if (this.col == 1) {
            return null;
        }
        return this.schemaDOM.relations[this.row][this.col - 1];
    }
    
    @Override
    public Node getNextSibling() {
        if (this.col == this.schemaDOM.relations[this.row].length - 1) {
            return null;
        }
        return this.schemaDOM.relations[this.row][this.col + 1];
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return new NamedNodeMapImpl(this.attrs);
    }
    
    @Override
    public boolean hasAttributes() {
        return this.attrs.length != 0;
    }
    
    @Override
    public String getTagName() {
        return this.rawname;
    }
    
    @Override
    public String getAttribute(final String name) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(name)) {
                return this.attrs[i].getValue();
            }
        }
        return "";
    }
    
    @Override
    public Attr getAttributeNode(final String name) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(name)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    @Override
    public String getAttributeNS(final String namespaceURI, final String localName) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getLocalName().equals(localName) && nsEquals(this.attrs[i].getNamespaceURI(), namespaceURI)) {
                return this.attrs[i].getValue();
            }
        }
        return "";
    }
    
    @Override
    public Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(localName) && nsEquals(this.attrs[i].getNamespaceURI(), namespaceURI)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    @Override
    public boolean hasAttribute(final String name) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean hasAttributeNS(final String namespaceURI, final String localName) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(localName) && nsEquals(this.attrs[i].getNamespaceURI(), namespaceURI)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setAttribute(final String name, final String value) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(name)) {
                this.attrs[i].setValue(value);
                return;
            }
        }
    }
    
    public int getLineNumber() {
        return this.line;
    }
    
    public int getColumnNumber() {
        return this.column;
    }
    
    public int getCharacterOffset() {
        return this.charOffset;
    }
    
    public String getAnnotation() {
        return this.fAnnotation;
    }
    
    public String getSyntheticAnnotation() {
        return this.fSyntheticAnnotation;
    }
    
    private static boolean nsEquals(final String nsURI_1, final String nsURI_2) {
        if (nsURI_1 == null) {
            return nsURI_2 == null;
        }
        return nsURI_1.equals(nsURI_2);
    }
}
