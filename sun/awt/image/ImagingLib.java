package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.AffineTransformOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.WritableRaster;
import java.awt.image.RasterOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;

public class ImagingLib
{
    static boolean useLib;
    static boolean verbose;
    private static final int NUM_NATIVE_OPS = 3;
    private static final int LOOKUP_OP = 0;
    private static final int AFFINE_OP = 1;
    private static final int CONVOLVE_OP = 2;
    private static Class[] nativeOpClass;
    
    private static native boolean init();
    
    public static native int transformBI(final BufferedImage p0, final BufferedImage p1, final double[] p2, final int p3);
    
    public static native int transformRaster(final Raster p0, final Raster p1, final double[] p2, final int p3);
    
    public static native int convolveBI(final BufferedImage p0, final BufferedImage p1, final Kernel p2, final int p3);
    
    public static native int convolveRaster(final Raster p0, final Raster p1, final Kernel p2, final int p3);
    
    public static native int lookupByteBI(final BufferedImage p0, final BufferedImage p1, final byte[][] p2);
    
    public static native int lookupByteRaster(final Raster p0, final Raster p1, final byte[][] p2);
    
    private static int getNativeOpIndex(final Class clazz) {
        int n = -1;
        for (int i = 0; i < 3; ++i) {
            if (clazz == ImagingLib.nativeOpClass[i]) {
                n = i;
                break;
            }
        }
        return n;
    }
    
    public static WritableRaster filter(final RasterOp rasterOp, final Raster raster, WritableRaster compatibleDestRaster) {
        if (!ImagingLib.useLib) {
            return null;
        }
        if (compatibleDestRaster == null) {
            compatibleDestRaster = rasterOp.createCompatibleDestRaster(raster);
        }
        WritableRaster writableRaster = null;
        switch (getNativeOpIndex(rasterOp.getClass())) {
            case 0: {
                final LookupTable table = ((LookupOp)rasterOp).getTable();
                if (table.getOffset() != 0) {
                    return null;
                }
                if (table instanceof ByteLookupTable) {
                    if (lookupByteRaster(raster, compatibleDestRaster, ((ByteLookupTable)table).getTable()) > 0) {
                        writableRaster = compatibleDestRaster;
                    }
                    break;
                }
                break;
            }
            case 1: {
                final AffineTransformOp affineTransformOp = (AffineTransformOp)rasterOp;
                final double[] array = new double[6];
                affineTransformOp.getTransform().getMatrix(array);
                if (transformRaster(raster, compatibleDestRaster, array, affineTransformOp.getInterpolationType()) > 0) {
                    writableRaster = compatibleDestRaster;
                    break;
                }
                break;
            }
            case 2: {
                final ConvolveOp convolveOp = (ConvolveOp)rasterOp;
                if (convolveRaster(raster, compatibleDestRaster, convolveOp.getKernel(), convolveOp.getEdgeCondition()) > 0) {
                    writableRaster = compatibleDestRaster;
                    break;
                }
                break;
            }
        }
        if (writableRaster != null) {
            SunWritableRaster.markDirty(writableRaster);
        }
        return writableRaster;
    }
    
    public static BufferedImage filter(final BufferedImageOp bufferedImageOp, final BufferedImage bufferedImage, BufferedImage compatibleDestImage) {
        if (ImagingLib.verbose) {
            System.out.println("in filter and op is " + bufferedImageOp + "bufimage is " + bufferedImage + " and " + compatibleDestImage);
        }
        if (!ImagingLib.useLib) {
            return null;
        }
        if (compatibleDestImage == null) {
            compatibleDestImage = bufferedImageOp.createCompatibleDestImage(bufferedImage, null);
        }
        Image image = null;
        switch (getNativeOpIndex(bufferedImageOp.getClass())) {
            case 0: {
                final LookupTable table = ((LookupOp)bufferedImageOp).getTable();
                if (table.getOffset() != 0) {
                    return null;
                }
                if (table instanceof ByteLookupTable) {
                    if (lookupByteBI(bufferedImage, compatibleDestImage, ((ByteLookupTable)table).getTable()) > 0) {
                        image = compatibleDestImage;
                    }
                    break;
                }
                break;
            }
            case 1: {
                final AffineTransformOp affineTransformOp = (AffineTransformOp)bufferedImageOp;
                final double[] array = new double[6];
                affineTransformOp.getTransform();
                affineTransformOp.getTransform().getMatrix(array);
                if (transformBI(bufferedImage, compatibleDestImage, array, affineTransformOp.getInterpolationType()) > 0) {
                    image = compatibleDestImage;
                    break;
                }
                break;
            }
            case 2: {
                final ConvolveOp convolveOp = (ConvolveOp)bufferedImageOp;
                if (convolveBI(bufferedImage, compatibleDestImage, convolveOp.getKernel(), convolveOp.getEdgeCondition()) > 0) {
                    image = compatibleDestImage;
                    break;
                }
                break;
            }
        }
        if (image != null) {
            SunWritableRaster.markDirty(image);
        }
        return (BufferedImage)image;
    }
    
    static {
        ImagingLib.useLib = true;
        ImagingLib.verbose = false;
        ImagingLib.nativeOpClass = new Class[3];
        ImagingLib.useLib = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("os.arch");
                if (property != null) {
                    if (property.startsWith("sparc")) {
                        return init();
                    }
                }
                try {
                    System.loadLibrary("mlib_image");
                }
                catch (final UnsatisfiedLinkError unsatisfiedLinkError) {
                    return Boolean.FALSE;
                }
                return init();
            }
        });
        try {
            ImagingLib.nativeOpClass[0] = Class.forName("java.awt.image.LookupOp");
        }
        catch (final ClassNotFoundException ex) {
            System.err.println("Could not find class: " + ex);
        }
        try {
            ImagingLib.nativeOpClass[1] = Class.forName("java.awt.image.AffineTransformOp");
        }
        catch (final ClassNotFoundException ex2) {
            System.err.println("Could not find class: " + ex2);
        }
        try {
            ImagingLib.nativeOpClass[2] = Class.forName("java.awt.image.ConvolveOp");
        }
        catch (final ClassNotFoundException ex3) {
            System.err.println("Could not find class: " + ex3);
        }
    }
}
