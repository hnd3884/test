package org.xbill.DNS;

import java.io.IOException;

public class UNKRecord extends Record
{
    private byte[] data;
    
    UNKRecord() {
    }
    
    Record getObject() {
        return new UNKRecord();
    }
    
    void rrFromWire(final DNSInput in) throws IOException {
        this.data = in.readByteArray();
    }
    
    void rdataFromString(final Tokenizer st, final Name origin) throws IOException {
        throw st.exception("invalid unknown RR encoding");
    }
    
    String rrToString() {
        return Record.unknownToString(this.data);
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    void rrToWire(final DNSOutput out, final Compression c, final boolean canonical) {
        out.writeByteArray(this.data);
    }
}
