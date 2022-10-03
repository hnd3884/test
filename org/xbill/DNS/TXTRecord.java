package org.xbill.DNS;

import java.util.List;

public class TXTRecord extends TXTBase
{
    TXTRecord() {
    }
    
    Record getObject() {
        return new TXTRecord();
    }
    
    public TXTRecord(final Name name, final int dclass, final long ttl, final List strings) {
        super(name, 16, dclass, ttl, strings);
    }
    
    public TXTRecord(final Name name, final int dclass, final long ttl, final String string) {
        super(name, 16, dclass, ttl, string);
    }
}
