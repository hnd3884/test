package com.unboundid.asn1;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ASN1StreamReaderSet
{
    private final ASN1StreamReader reader;
    private final byte type;
    private final int length;
    private final long endBytesRead;
    
    ASN1StreamReaderSet(final ASN1StreamReader reader, final byte type, final int length) {
        this.reader = reader;
        this.type = type;
        this.length = length;
        this.endBytesRead = reader.getTotalBytesRead() + length;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public boolean hasMoreElements() throws ASN1Exception {
        final long currentBytesRead = this.reader.getTotalBytesRead();
        if (currentBytesRead == this.endBytesRead) {
            return false;
        }
        if (currentBytesRead < this.endBytesRead) {
            return true;
        }
        throw new ASN1Exception(ASN1Messages.ERR_STREAM_READER_SET_READ_PAST_END.get(this.length, this.endBytesRead, currentBytesRead));
    }
}
