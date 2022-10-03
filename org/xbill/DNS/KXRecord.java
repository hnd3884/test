package org.xbill.DNS;

public class KXRecord extends U16NameBase
{
    KXRecord() {
    }
    
    Record getObject() {
        return new KXRecord();
    }
    
    public KXRecord(final Name name, final int dclass, final long ttl, final int preference, final Name target) {
        super(name, 36, dclass, ttl, preference, "preference", target, "target");
    }
    
    public Name getTarget() {
        return this.getNameField();
    }
    
    public int getPreference() {
        return this.getU16Field();
    }
    
    public Name getAdditionalName() {
        return this.getNameField();
    }
}
