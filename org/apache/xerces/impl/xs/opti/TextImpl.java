package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class TextImpl extends DefaultText
{
    String fData;
    SchemaDOM fSchemaDOM;
    int fRow;
    int fCol;
    
    public TextImpl(final StringBuffer sb, final SchemaDOM fSchemaDOM, final int fRow, final int fCol) {
        this.fData = null;
        this.fSchemaDOM = null;
        this.fData = sb.toString();
        this.fSchemaDOM = fSchemaDOM;
        this.fRow = fRow;
        this.fCol = fCol;
        final String s = null;
        this.uri = s;
        this.localpart = s;
        this.prefix = s;
        this.rawname = s;
        this.nodeType = 3;
    }
    
    public String getNodeName() {
        return "#text";
    }
    
    public Node getParentNode() {
        return this.fSchemaDOM.relations[this.fRow][0];
    }
    
    public Node getPreviousSibling() {
        if (this.fCol == 1) {
            return null;
        }
        return this.fSchemaDOM.relations[this.fRow][this.fCol - 1];
    }
    
    public Node getNextSibling() {
        if (this.fCol == this.fSchemaDOM.relations[this.fRow].length - 1) {
            return null;
        }
        return this.fSchemaDOM.relations[this.fRow][this.fCol + 1];
    }
    
    public String getData() throws DOMException {
        return this.fData;
    }
    
    public int getLength() {
        if (this.fData == null) {
            return 0;
        }
        return this.fData.length();
    }
    
    public String substringData(final int n, final int n2) throws DOMException {
        if (this.fData == null) {
            return null;
        }
        if (n2 < 0 || n < 0 || n > this.fData.length()) {
            throw new DOMException((short)1, "parameter error");
        }
        if (n + n2 >= this.fData.length()) {
            return this.fData.substring(n);
        }
        return this.fData.substring(n, n + n2);
    }
}
