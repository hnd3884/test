package org.w3c.tidy;

public class Attribute
{
    private String name;
    private boolean nowrap;
    private boolean literal;
    private short versions;
    private AttrCheck attrchk;
    
    public Attribute(final String name, final short versions, final AttrCheck attrchk) {
        this.name = name;
        this.versions = versions;
        this.attrchk = attrchk;
    }
    
    public void setLiteral(final boolean literal) {
        this.literal = literal;
    }
    
    public void setNowrap(final boolean nowrap) {
        this.nowrap = nowrap;
    }
    
    public AttrCheck getAttrchk() {
        return this.attrchk;
    }
    
    public boolean isLiteral() {
        return this.literal;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isNowrap() {
        return this.nowrap;
    }
    
    public short getVersions() {
        return this.versions;
    }
}
