package org.xbill.DNS;

public class MGRecord extends SingleNameBase
{
    MGRecord() {
    }
    
    Record getObject() {
        return new MGRecord();
    }
    
    public MGRecord(final Name name, final int dclass, final long ttl, final Name mailbox) {
        super(name, 8, dclass, ttl, mailbox, "mailbox");
    }
    
    public Name getMailbox() {
        return this.getSingleName();
    }
}
