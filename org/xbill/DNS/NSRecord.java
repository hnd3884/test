package org.xbill.DNS;

public class NSRecord extends SingleCompressedNameBase
{
    NSRecord() {
    }
    
    Record getObject() {
        return new NSRecord();
    }
    
    public NSRecord(final Name name, final int dclass, final long ttl, final Name target) {
        super(name, 2, dclass, ttl, target, "target");
    }
    
    public Name getTarget() {
        return this.getSingleName();
    }
    
    public Name getAdditionalName() {
        return this.getSingleName();
    }
}
