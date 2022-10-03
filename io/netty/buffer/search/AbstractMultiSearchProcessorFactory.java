package io.netty.buffer.search;

public abstract class AbstractMultiSearchProcessorFactory implements MultiSearchProcessorFactory
{
    public static AhoCorasicSearchProcessorFactory newAhoCorasicSearchProcessorFactory(final byte[]... needles) {
        return new AhoCorasicSearchProcessorFactory(needles);
    }
}
