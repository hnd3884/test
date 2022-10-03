package sun.awt.image;

import java.awt.image.IndexColorModel;
import java.awt.image.DirectColorModel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.awt.image.ColorModel;

public class JPEGImageDecoder extends ImageDecoder
{
    private static ColorModel RGBcolormodel;
    private static ColorModel ARGBcolormodel;
    private static ColorModel Graycolormodel;
    private static final Class InputStreamClass;
    private ColorModel colormodel;
    Hashtable props;
    private static final int hintflags = 22;
    
    private static native void initIDs(final Class p0);
    
    private native void readImage(final InputStream p0, final byte[] p1) throws ImageFormatException, IOException;
    
    public JPEGImageDecoder(final InputStreamImageSource inputStreamImageSource, final InputStream inputStream) {
        super(inputStreamImageSource, inputStream);
        this.props = new Hashtable();
    }
    
    private static void error(final String s) throws ImageFormatException {
        throw new ImageFormatException(s);
    }
    
    public boolean sendHeaderInfo(final int n, final int n2, final boolean b, final boolean b2, final boolean b3) {
        this.setDimensions(n, n2);
        this.setProperties(this.props);
        if (b) {
            this.colormodel = JPEGImageDecoder.Graycolormodel;
        }
        else if (b2) {
            this.colormodel = JPEGImageDecoder.ARGBcolormodel;
        }
        else {
            this.colormodel = JPEGImageDecoder.RGBcolormodel;
        }
        this.setColorModel(this.colormodel);
        if (!b3) {}
        this.setHints(22);
        this.headerComplete();
        return true;
    }
    
    public boolean sendPixels(final int[] array, final int n) {
        if (this.setPixels(0, n, array.length, 1, this.colormodel, array, 0, array.length) <= 0) {
            this.aborted = true;
        }
        return !this.aborted;
    }
    
    public boolean sendPixels(final byte[] array, final int n) {
        if (this.setPixels(0, n, array.length, 1, this.colormodel, array, 0, array.length) <= 0) {
            this.aborted = true;
        }
        return !this.aborted;
    }
    
    @Override
    public void produceImage() throws IOException, ImageFormatException {
        try {
            this.readImage(this.input, new byte[1024]);
            if (!this.aborted) {
                this.imageComplete(3, true);
            }
        }
        catch (final IOException ex) {
            if (!this.aborted) {
                throw ex;
            }
        }
        finally {
            this.close();
        }
    }
    
    static {
        InputStreamClass = InputStream.class;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("jpeg");
                return null;
            }
        });
        initIDs(JPEGImageDecoder.InputStreamClass);
        JPEGImageDecoder.RGBcolormodel = new DirectColorModel(24, 16711680, 65280, 255);
        JPEGImageDecoder.ARGBcolormodel = ColorModel.getRGBdefault();
        final byte[] array = new byte[256];
        for (int i = 0; i < 256; ++i) {
            array[i] = (byte)i;
        }
        JPEGImageDecoder.Graycolormodel = new IndexColorModel(8, 256, array, array, array);
    }
}
