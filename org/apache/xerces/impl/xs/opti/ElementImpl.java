package org.apache.xerces.impl.xs.opti;

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
    
    public ElementImpl(final int line, final int column, final int charOffset) {
        this.row = -1;
        this.col = -1;
        this.parentRow = -1;
        this.nodeType = 1;
        this.line = line;
        this.column = column;
        this.charOffset = charOffset;
    }
    
    public ElementImpl(final int n, final int n2) {
        this(n, n2, -1);
    }
    
    public ElementImpl(final String s, final String s2, final String s3, final String s4, final int line, final int column, final int charOffset) {
        super(s, s2, s3, s4, (short)1);
        this.row = -1;
        this.col = -1;
        this.parentRow = -1;
        this.line = line;
        this.column = column;
        this.charOffset = charOffset;
    }
    
    public ElementImpl(final String s, final String s2, final String s3, final String s4, final int n, final int n2) {
        this(s, s2, s3, s4, n, n2, -1);
    }
    
    public Document getOwnerDocument() {
        return this.schemaDOM;
    }
    
    public Node getParentNode() {
        return this.schemaDOM.relations[this.row][0];
    }
    
    public boolean hasChildNodes() {
        return this.parentRow != -1;
    }
    
    public Node getFirstChild() {
        if (this.parentRow == -1) {
            return null;
        }
        return this.schemaDOM.relations[this.parentRow][1];
    }
    
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
    
    public Node getPreviousSibling() {
        if (this.col == 1) {
            return null;
        }
        return this.schemaDOM.relations[this.row][this.col - 1];
    }
    
    public Node getNextSibling() {
        if (this.col == this.schemaDOM.relations[this.row].length - 1) {
            return null;
        }
        return this.schemaDOM.relations[this.row][this.col + 1];
    }
    
    public NamedNodeMap getAttributes() {
        return new NamedNodeMapImpl(this.attrs);
    }
    
    public boolean hasAttributes() {
        return this.attrs.length != 0;
    }
    
    public String getTagName() {
        return this.rawname;
    }
    
    public String getAttribute(final String s) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s)) {
                return this.attrs[i].getValue();
            }
        }
        return "";
    }
    
    public Attr getAttributeNode(final String s) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    public String getAttributeNS(final String s, final String s2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getLocalName().equals(s2) && nsEquals(this.attrs[i].getNamespaceURI(), s)) {
                return this.attrs[i].getValue();
            }
        }
        return "";
    }
    
    public Attr getAttributeNodeNS(final String s, final String s2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s2) && nsEquals(this.attrs[i].getNamespaceURI(), s)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    public boolean hasAttribute(final String s) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttributeNS(final String s, final String s2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s2) && nsEquals(this.attrs[i].getNamespaceURI(), s)) {
                return true;
            }
        }
        return false;
    }
    
    public void setAttribute(final String s, final String value) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s)) {
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
    
    private static boolean nsEquals(final String s, final String s2) {
        if (s == null) {
            return s2 == null;
        }
        return s.equals(s2);
    }
}
