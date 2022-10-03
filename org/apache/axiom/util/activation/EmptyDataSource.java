package org.apache.axiom.util.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

public class EmptyDataSource implements SizeAwareDataSource
{
    public static final EmptyDataSource INSTANCE;
    private static final InputStream emptyInputStream;
    private final String contentType;
    
    public EmptyDataSource(final String contentType) {
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public String getName() {
        return null;
    }
    
    public long getSize() {
        return 0L;
    }
    
    public InputStream getInputStream() throws IOException {
        return EmptyDataSource.emptyInputStream;
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    static {
        INSTANCE = new EmptyDataSource("application/octet-stream");
        emptyInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }
}
