package org.xbill.DNS;

import java.util.Date;

public class RRSIGRecord extends SIGBase
{
    RRSIGRecord() {
    }
    
    Record getObject() {
        return new RRSIGRecord();
    }
    
    public RRSIGRecord(final Name name, final int dclass, final long ttl, final int covered, final int alg, final long origttl, final Date expire, final Date timeSigned, final int footprint, final Name signer, final byte[] signature) {
        super(name, 46, dclass, ttl, covered, alg, origttl, expire, timeSigned, footprint, signer, signature);
    }
}
