package io.opencensus.tags.propagation;

import io.opencensus.tags.TagContext;

public abstract class TagContextBinarySerializer
{
    public abstract byte[] toByteArray(final TagContext p0) throws TagContextSerializationException;
    
    public abstract TagContext fromByteArray(final byte[] p0) throws TagContextDeserializationException;
}
