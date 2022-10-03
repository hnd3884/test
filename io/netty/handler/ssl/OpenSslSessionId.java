package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import java.util.Arrays;

final class OpenSslSessionId
{
    private final byte[] id;
    private final int hashCode;
    static final OpenSslSessionId NULL_ID;
    
    OpenSslSessionId(final byte[] id) {
        this.id = id;
        this.hashCode = Arrays.hashCode(id);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof OpenSslSessionId && Arrays.equals(this.id, ((OpenSslSessionId)o).id));
    }
    
    @Override
    public String toString() {
        return "OpenSslSessionId{id=" + Arrays.toString(this.id) + '}';
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    byte[] cloneBytes() {
        return this.id.clone();
    }
    
    static {
        NULL_ID = new OpenSslSessionId(EmptyArrays.EMPTY_BYTES);
    }
}
