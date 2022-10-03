package com.google.api.client.http;

import java.io.IOException;
import com.google.common.io.ByteStreams;
import java.io.InputStream;
import java.io.FilterInputStream;

final class ConsumingInputStream extends FilterInputStream
{
    private boolean closed;
    
    ConsumingInputStream(final InputStream inputStream) {
        super(inputStream);
        this.closed = false;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed && this.in != null) {
            try {
                ByteStreams.exhaust((InputStream)this);
                super.in.close();
            }
            finally {
                this.closed = true;
            }
        }
    }
}
