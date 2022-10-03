package org.xbill.DNS;

public class AFSDBRecord extends U16NameBase
{
    AFSDBRecord() {
    }
    
    Record getObject() {
        return new AFSDBRecord();
    }
    
    public AFSDBRecord(final Name name, final int dclass, final long ttl, final int subtype, final Name host) {
        super(name, 18, dclass, ttl, subtype, "subtype", host, "host");
    }
    
    public int getSubtype() {
        return this.getU16Field();
    }
    
    public Name getHost() {
        return this.getNameField();
    }
}
