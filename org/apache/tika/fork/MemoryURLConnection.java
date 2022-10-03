package org.apache.tika.fork;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class MemoryURLConnection extends URLConnection
{
    private final byte[] data;
    
    MemoryURLConnection(final URL url, final byte[] data) {
        super(url);
        this.data = data;
    }
    
    @Override
    public void connect() {
    }
    
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.data);
    }
}
