package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.InputStreamAdapter;
import com.sun.imageio.plugins.common.SubImageInputStream;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import java.io.InputStream;
import java.util.Enumeration;

class PNGImageDataEnumeration implements Enumeration<InputStream>
{
    boolean firstTime;
    ImageInputStream stream;
    int length;
    
    public PNGImageDataEnumeration(final ImageInputStream stream) throws IOException {
        this.firstTime = true;
        this.stream = stream;
        this.length = stream.readInt();
        stream.readInt();
    }
    
    @Override
    public InputStream nextElement() {
        try {
            this.firstTime = false;
            return new InputStreamAdapter(new SubImageInputStream(this.stream, this.length));
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    @Override
    public boolean hasMoreElements() {
        if (this.firstTime) {
            return true;
        }
        try {
            this.stream.readInt();
            this.length = this.stream.readInt();
            return this.stream.readInt() == 1229209940;
        }
        catch (final IOException ex) {
            return false;
        }
    }
}
