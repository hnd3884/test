package org.apache.commons.collections.buffer;

import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.Buffer;

public class TypedBuffer
{
    public static Buffer decorate(final Buffer buffer, final Class type) {
        return new PredicatedBuffer(buffer, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedBuffer() {
    }
}
