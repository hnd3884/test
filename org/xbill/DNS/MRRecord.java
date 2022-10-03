package org.xbill.DNS;

public class MRRecord extends SingleNameBase
{
    MRRecord() {
    }
    
    Record getObject() {
        return new MRRecord();
    }
    
    public MRRecord(final Name name, final int dclass, final long ttl, final Name newName) {
        super(name, 9, dclass, ttl, newName, "new name");
    }
    
    public Name getNewName() {
        return this.getSingleName();
    }
}
