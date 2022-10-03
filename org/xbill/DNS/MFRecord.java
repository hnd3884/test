package org.xbill.DNS;

public class MFRecord extends SingleNameBase
{
    MFRecord() {
    }
    
    Record getObject() {
        return new MFRecord();
    }
    
    public MFRecord(final Name name, final int dclass, final long ttl, final Name mailAgent) {
        super(name, 4, dclass, ttl, mailAgent, "mail agent");
    }
    
    public Name getMailAgent() {
        return this.getSingleName();
    }
    
    public Name getAdditionalName() {
        return this.getSingleName();
    }
}
