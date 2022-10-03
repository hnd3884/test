package io.netty.buffer.search;

public abstract class AbstractSearchProcessorFactory implements SearchProcessorFactory
{
    public static KmpSearchProcessorFactory newKmpSearchProcessorFactory(final byte[] needle) {
        return new KmpSearchProcessorFactory(needle);
    }
    
    public static BitapSearchProcessorFactory newBitapSearchProcessorFactory(final byte[] needle) {
        return new BitapSearchProcessorFactory(needle);
    }
}
