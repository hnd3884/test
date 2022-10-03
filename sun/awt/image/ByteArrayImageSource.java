package sun.awt.image;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class ByteArrayImageSource extends InputStreamImageSource
{
    byte[] imagedata;
    int imageoffset;
    int imagelength;
    
    public ByteArrayImageSource(final byte[] array) {
        this(array, 0, array.length);
    }
    
    public ByteArrayImageSource(final byte[] imagedata, final int imageoffset, final int imagelength) {
        this.imagedata = imagedata;
        this.imageoffset = imageoffset;
        this.imagelength = imagelength;
    }
    
    @Override
    final boolean checkSecurity(final Object o, final boolean b) {
        return true;
    }
    
    @Override
    protected ImageDecoder getDecoder() {
        return this.getDecoder(new BufferedInputStream(new ByteArrayInputStream(this.imagedata, this.imageoffset, this.imagelength)));
    }
}
