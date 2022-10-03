package org.xbill.DNS;

public class MBRecord extends SingleNameBase
{
    MBRecord() {
    }
    
    Record getObject() {
        return new MBRecord();
    }
    
    public MBRecord(final Name name, final int dclass, final long ttl, final Name mailbox) {
        super(name, 7, dclass, ttl, mailbox, "mailbox");
    }
    
    public Name getMailbox() {
        return this.getSingleName();
    }
    
    public Name getAdditionalName() {
        return this.getSingleName();
    }
}
