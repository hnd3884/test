package org.xbill.DNS;

public class RTRecord extends U16NameBase
{
    RTRecord() {
    }
    
    Record getObject() {
        return new RTRecord();
    }
    
    public RTRecord(final Name name, final int dclass, final long ttl, final int preference, final Name intermediateHost) {
        super(name, 21, dclass, ttl, preference, "preference", intermediateHost, "intermediateHost");
    }
    
    public int getPreference() {
        return this.getU16Field();
    }
    
    public Name getIntermediateHost() {
        return this.getNameField();
    }
}
