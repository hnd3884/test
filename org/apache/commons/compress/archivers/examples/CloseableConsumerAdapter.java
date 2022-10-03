package org.apache.commons.compress.archivers.examples;

import java.io.IOException;
import java.util.Objects;
import java.io.Closeable;

final class CloseableConsumerAdapter implements Closeable
{
    private final CloseableConsumer consumer;
    private Closeable closeable;
    
    CloseableConsumerAdapter(final CloseableConsumer consumer) {
        this.consumer = Objects.requireNonNull(consumer, "consumer");
    }
    
     <C extends Closeable> C track(final C closeable) {
        return (C)(this.closeable = closeable);
    }
    
    @Override
    public void close() throws IOException {
        if (this.closeable != null) {
            this.consumer.accept(this.closeable);
        }
    }
}
