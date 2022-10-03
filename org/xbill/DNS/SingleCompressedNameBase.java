package org.xbill.DNS;

abstract class SingleCompressedNameBase extends SingleNameBase
{
    protected SingleCompressedNameBase() {
    }
    
    protected SingleCompressedNameBase(final Name name, final int type, final int dclass, final long ttl, final Name singleName, final String description) {
        super(name, type, dclass, ttl, singleName, description);
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        this.singleName.toWire(out, c, canonical);
    }
}
