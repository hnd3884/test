package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class TextImpl extends DefaultText
{
    String fData;
    SchemaDOM fSchemaDOM;
    int fRow;
    int fCol;
    
    public TextImpl(final StringBuffer str, final SchemaDOM sDOM, final int row, final int col) {
        this.fData = null;
        this.fSchemaDOM = null;
        this.fData = str.toString();
        this.fSchemaDOM = sDOM;
        this.fRow = row;
        this.fCol = col;
        final String s = null;
        this.uri = s;
        this.localpart = s;
        this.prefix = s;
        this.rawname = s;
        this.nodeType = 3;
    }
    
    @Override
    public Node getParentNode() {
        return this.fSchemaDOM.relations[this.fRow][0];
    }
    
    @Override
    public Node getPreviousSibling() {
        if (this.fCol == 1) {
            return null;
        }
        return this.fSchemaDOM.relations[this.fRow][this.fCol - 1];
    }
    
    @Override
    public Node getNextSibling() {
        if (this.fCol == this.fSchemaDOM.relations[this.fRow].length - 1) {
            return null;
        }
        return this.fSchemaDOM.relations[this.fRow][this.fCol + 1];
    }
    
    @Override
    public String getData() throws DOMException {
        return this.fData;
    }
    
    @Override
    public int getLength() {
        if (this.fData == null) {
            return 0;
        }
        return this.fData.length();
    }
    
    @Override
    public String substringData(final int offset, final int count) throws DOMException {
        if (this.fData == null) {
            return null;
        }
        if (count < 0 || offset < 0 || offset > this.fData.length()) {
            throw new DOMException((short)1, "parameter error");
        }
        if (offset + count >= this.fData.length()) {
            return this.fData.substring(offset);
        }
        return this.fData.substring(offset, offset + count);
    }
}
