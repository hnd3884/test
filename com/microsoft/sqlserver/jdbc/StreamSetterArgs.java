package com.microsoft.sqlserver.jdbc;

final class StreamSetterArgs
{
    private long length;
    final StreamType streamType;
    
    final long getLength() {
        return this.length;
    }
    
    final void setLength(final long newLength) {
        assert -1L == this.length;
        assert newLength >= 0L;
        this.length = newLength;
    }
    
    StreamSetterArgs(final StreamType streamType, final long length) {
        this.streamType = streamType;
        this.length = length;
    }
}
