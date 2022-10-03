package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import java.io.OutputStream;
import java.io.InputStream;

class CMSProcessableInputStream implements CMSProcessable, CMSReadable
{
    private InputStream input;
    private boolean used;
    
    public CMSProcessableInputStream(final InputStream input) {
        this.used = false;
        this.input = input;
    }
    
    public InputStream getInputStream() {
        this.checkSingleUsage();
        return this.input;
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
        this.checkSingleUsage();
        Streams.pipeAll(this.input, outputStream);
        this.input.close();
    }
    
    public Object getContent() {
        return this.getInputStream();
    }
    
    private synchronized void checkSingleUsage() {
        if (this.used) {
            throw new IllegalStateException("CMSProcessableInputStream can only be used once");
        }
        this.used = true;
    }
}
