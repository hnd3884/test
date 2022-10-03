package org.xbill.DNS;

public class DNAMERecord extends SingleNameBase
{
    DNAMERecord() {
    }
    
    Record getObject() {
        return new DNAMERecord();
    }
    
    public DNAMERecord(final Name name, final int dclass, final long ttl, final Name alias) {
        super(name, 39, dclass, ttl, alias, "alias");
    }
    
    public Name getTarget() {
        return this.getSingleName();
    }
    
    public Name getAlias() {
        return this.getSingleName();
    }
}
