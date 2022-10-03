package org.xbill.DNS;

public class CNAMERecord extends SingleCompressedNameBase
{
    CNAMERecord() {
    }
    
    Record getObject() {
        return new CNAMERecord();
    }
    
    public CNAMERecord(final Name name, final int dclass, final long ttl, final Name alias) {
        super(name, 5, dclass, ttl, alias, "alias");
    }
    
    public Name getTarget() {
        return this.getSingleName();
    }
    
    public Name getAlias() {
        return this.getSingleName();
    }
}
