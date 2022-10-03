package org.xbill.DNS;

import java.io.IOException;

class EmptyRecord extends Record
{
    Record getObject() {
        return new EmptyRecord();
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
    }
    
    String rrToString() {
        return "";
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
    }
}
