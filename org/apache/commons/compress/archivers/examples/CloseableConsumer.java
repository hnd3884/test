package org.apache.commons.compress.archivers.examples;

import java.io.IOException;
import java.io.Closeable;

public interface CloseableConsumer
{
    public static final CloseableConsumer CLOSING_CONSUMER = Closeable::close;
    public static final CloseableConsumer NULL_CONSUMER = c -> {};
    
    void accept(final Closeable p0) throws IOException;
}
