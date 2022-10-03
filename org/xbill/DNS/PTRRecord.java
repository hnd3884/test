package org.xbill.DNS;

public class PTRRecord extends SingleCompressedNameBase
{
    PTRRecord() {
    }
    
    Record getObject() {
        return new PTRRecord();
    }
    
    public PTRRecord(final Name name, final int dclass, final long ttl, final Name target) {
        super(name, 12, dclass, ttl, target, "target");
    }
    
    public Name getTarget() {
        return this.getSingleName();
    }
}
