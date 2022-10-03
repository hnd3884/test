package org.xbill.DNS;

public class NSAP_PTRRecord extends SingleNameBase
{
    NSAP_PTRRecord() {
    }
    
    Record getObject() {
        return new NSAP_PTRRecord();
    }
    
    public NSAP_PTRRecord(final Name name, final int dclass, final long ttl, final Name target) {
        super(name, 23, dclass, ttl, target, "target");
    }
    
    public Name getTarget() {
        return this.getSingleName();
    }
}
