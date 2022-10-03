package sun.awt.windows;

import java.awt.image.WritableRaster;
import java.awt.Graphics;
import sun.awt.image.IntegerComponentRaster;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import sun.awt.image.ToolkitImage;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Image;
import sun.awt.CustomCursor;

final class WCustomCursor extends CustomCursor
{
    WCustomCursor(final Image image, final Point point, final String s) throws IndexOutOfBoundsException {
        super(image, point, s);
    }
    
    @Override
    protected void createNativeCursor(final Image image, final int[] array, final int n, final int n2, final int n3, final int n4) {
        final BufferedImage bufferedImage = new BufferedImage(n, n2, 1);
        final Graphics graphics = bufferedImage.getGraphics();
        try {
            if (image instanceof ToolkitImage) {
                ((ToolkitImage)image).getImageRep().reconstruct(32);
            }
            graphics.drawImage(image, 0, 0, n, n2, null);
        }
        finally {
            graphics.dispose();
        }
        final WritableRaster raster = bufferedImage.getRaster();
        ((DataBufferInt)raster.getDataBuffer()).getData();
        final byte[] array2 = new byte[n * n2 / 8];
        for (int length = array.length, i = 0; i < length; ++i) {
            final int n5 = i / 8;
            final int n6 = 1 << 7 - i % 8;
            if ((array[i] & 0xFF000000) == 0x0) {
                final byte[] array3 = array2;
                final int n7 = n5;
                array3[n7] |= (byte)n6;
            }
        }
        int n8 = raster.getWidth();
        if (raster instanceof IntegerComponentRaster) {
            n8 = ((IntegerComponentRaster)raster).getScanlineStride();
        }
        this.createCursorIndirect(((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData(), array2, n8, raster.getWidth(), raster.getHeight(), n3, n4);
    }
    
    private native void createCursorIndirect(final int[] p0, final byte[] p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    static native int getCursorWidth();
    
    static native int getCursorHeight();
}
