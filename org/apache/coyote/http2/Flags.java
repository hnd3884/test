package org.apache.coyote.http2;

class Flags
{
    private Flags() {
    }
    
    static boolean isEndOfStream(final int flags) {
        return (flags & 0x1) != 0x0;
    }
    
    static boolean isAck(final int flags) {
        return (flags & 0x1) != 0x0;
    }
    
    static boolean isEndOfHeaders(final int flags) {
        return (flags & 0x4) != 0x0;
    }
    
    static boolean hasPadding(final int flags) {
        return (flags & 0x8) != 0x0;
    }
    
    static boolean hasPriority(final int flags) {
        return (flags & 0x20) != 0x0;
    }
}
