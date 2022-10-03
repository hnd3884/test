package com.google.api.client.http;

import java.io.OutputStream;
import java.io.IOException;

public class EmptyContent implements HttpContent
{
    @Override
    public long getLength() throws IOException {
        return 0L;
    }
    
    @Override
    public String getType() {
        return null;
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        out.flush();
    }
    
    @Override
    public boolean retrySupported() {
        return true;
    }
}
