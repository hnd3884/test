package org.w3c.tidy;

import org.w3c.dom.Attr;

public class AttVal implements Cloneable
{
    protected AttVal next;
    protected Attribute dict;
    protected Node asp;
    protected Node php;
    protected int delim;
    protected String attribute;
    protected String value;
    protected Attr adapter;
    
    public AttVal() {
    }
    
    public AttVal(final AttVal next, final Attribute dict, final int delim, final String attribute, final String value) {
        this.next = next;
        this.dict = dict;
        this.delim = delim;
        this.attribute = attribute;
        this.value = value;
    }
    
    public AttVal(final AttVal next, final Attribute dict, final Node asp, final Node php, final int delim, final String attribute, final String value) {
        this.next = next;
        this.dict = dict;
        this.asp = asp;
        this.php = php;
        this.delim = delim;
        this.attribute = attribute;
        this.value = value;
    }
    
    protected Object clone() {
        AttVal attVal = null;
        try {
            attVal = (AttVal)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        if (this.next != null) {
            attVal.next = (AttVal)this.next.clone();
        }
        if (this.asp != null) {
            attVal.asp = this.asp.cloneNode(false);
        }
        if (this.php != null) {
            attVal.php = this.php.cloneNode(false);
        }
        return attVal;
    }
    
    public boolean isBoolAttribute() {
        final Attribute dict = this.dict;
        return dict != null && dict.getAttrchk() == AttrCheckImpl.BOOL;
    }
    
    void checkLowerCaseAttrValue(final Lexer lexer, final Node node) {
        if (this.value == null) {
            return;
        }
        final String lowerCase = this.value.toLowerCase();
        if (!this.value.equals(lowerCase)) {
            if (lexer.isvoyager) {
                lexer.report.attrError(lexer, node, this, (short)70);
            }
            if (lexer.isvoyager || lexer.configuration.lowerLiterals) {
                this.value = lowerCase;
            }
        }
    }
    
    public Attribute checkAttribute(final Lexer lexer, final Node node) {
        final TagTable tt = lexer.configuration.tt;
        final Attribute dict = this.dict;
        if (dict != null) {
            if (TidyUtils.toBoolean(dict.getVersions() & 0x20)) {
                if (!lexer.configuration.xmlTags && !lexer.configuration.xmlOut) {
                    lexer.report.attrError(lexer, node, this, (short)57);
                }
            }
            else if (dict != AttributeTable.attrTitle || (node.tag != tt.tagA && node.tag != tt.tagLink)) {
                lexer.constrainVersion(dict.getVersions());
            }
            if (dict.getAttrchk() != null) {
                dict.getAttrchk().check(lexer, node, this);
            }
            else if (TidyUtils.toBoolean(this.dict.getVersions() & 0x1C0)) {
                lexer.report.attrError(lexer, node, this, (short)53);
            }
        }
        else if (!lexer.configuration.xmlTags && node.tag != null && this.asp == null && (node.tag == null || !TidyUtils.toBoolean(node.tag.versions & 0x1C0))) {
            lexer.report.attrError(lexer, node, this, (short)48);
        }
        return dict;
    }
    
    protected Attr getAdapter() {
        if (this.adapter == null) {
            this.adapter = new DOMAttrImpl(this);
        }
        return this.adapter;
    }
    
    public Node getAsp() {
        return this.asp;
    }
    
    public void setAsp(final Node asp) {
        this.asp = asp;
    }
    
    public String getAttribute() {
        return this.attribute;
    }
    
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }
    
    public int getDelim() {
        return this.delim;
    }
    
    public void setDelim(final int delim) {
        this.delim = delim;
    }
    
    public Attribute getDict() {
        return this.dict;
    }
    
    public void setDict(final Attribute dict) {
        this.dict = dict;
    }
    
    public AttVal getNext() {
        return this.next;
    }
    
    public void setNext(final AttVal next) {
        this.next = next;
    }
    
    public Node getPhp() {
        return this.php;
    }
    
    public void setPhp(final Node php) {
        this.php = php;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
