package com.unboundid.asn1;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.io.IOException;
import com.unboundid.util.Debug;
import java.io.OutputStream;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Writer
{
    private static final ThreadLocal<ByteStringBuffer> buffers;
    private static final int MAX_BUFFER_LENGTH = 524288;
    
    private ASN1Writer() {
    }
    
    public static void writeElement(final ASN1Element element, final OutputStream outputStream) throws IOException {
        Debug.debugASN1Write(element);
        ByteStringBuffer buffer = ASN1Writer.buffers.get();
        if (buffer == null) {
            buffer = new ByteStringBuffer();
            ASN1Writer.buffers.set(buffer);
        }
        element.encodeTo(buffer);
        try {
            buffer.write(outputStream);
        }
        finally {
            if (buffer.capacity() > 524288) {
                buffer.setCapacity(524288);
            }
            buffer.clear();
        }
    }
    
    public static void writeElement(final ASN1Element element, final ByteBuffer buffer) throws BufferOverflowException {
        Debug.debugASN1Write(element);
        ByteStringBuffer b = ASN1Writer.buffers.get();
        if (b == null) {
            b = new ByteStringBuffer();
            ASN1Writer.buffers.set(b);
        }
        element.encodeTo(b);
        try {
            if (buffer.remaining() < b.length()) {
                throw new BufferOverflowException();
            }
            final int pos = buffer.position();
            buffer.put(b.getBackingArray(), 0, b.length());
            buffer.limit(buffer.position());
            buffer.position(pos);
        }
        finally {
            if (b.capacity() > 524288) {
                b.setCapacity(524288);
            }
            b.clear();
        }
    }
    
    static {
        buffers = new ThreadLocal<ByteStringBuffer>();
    }
}
