package com.unboundid.asn1;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ASN1BufferSequence implements Serializable
{
    private static final long serialVersionUID = 7219098399193345629L;
    private final ASN1Buffer buffer;
    private final int valueStartPos;
    
    ASN1BufferSequence(final ASN1Buffer buffer) {
        this.buffer = buffer;
        this.valueStartPos = buffer.length();
    }
    
    public void end() {
        this.buffer.endSequenceOrSet(this.valueStartPos);
    }
}
