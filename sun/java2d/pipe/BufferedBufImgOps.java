package sun.java2d.pipe;

import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.awt.image.ConvolveOp;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;

public class BufferedBufImgOps
{
    public static void enableBufImgOp(final RenderQueue renderQueue, final SurfaceData surfaceData, final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp) {
        if (bufferedImageOp instanceof ConvolveOp) {
            enableConvolveOp(renderQueue, surfaceData, (ConvolveOp)bufferedImageOp);
        }
        else if (bufferedImageOp instanceof RescaleOp) {
            enableRescaleOp(renderQueue, surfaceData, bufferedImage, (RescaleOp)bufferedImageOp);
        }
        else {
            if (!(bufferedImageOp instanceof LookupOp)) {
                throw new InternalError("Unknown BufferedImageOp");
            }
            enableLookupOp(renderQueue, surfaceData, bufferedImage, (LookupOp)bufferedImageOp);
        }
    }
    
    public static void disableBufImgOp(final RenderQueue renderQueue, final BufferedImageOp bufferedImageOp) {
        if (bufferedImageOp instanceof ConvolveOp) {
            disableConvolveOp(renderQueue);
        }
        else if (bufferedImageOp instanceof RescaleOp) {
            disableRescaleOp(renderQueue);
        }
        else {
            if (!(bufferedImageOp instanceof LookupOp)) {
                throw new InternalError("Unknown BufferedImageOp");
            }
            disableLookupOp(renderQueue);
        }
    }
    
    public static boolean isConvolveOpValid(final ConvolveOp convolveOp) {
        final Kernel kernel = convolveOp.getKernel();
        final int width = kernel.getWidth();
        final int height = kernel.getHeight();
        return (width == 3 && height == 3) || (width == 5 && height == 5);
    }
    
    private static void enableConvolveOp(final RenderQueue renderQueue, final SurfaceData surfaceData, final ConvolveOp convolveOp) {
        final int n = (convolveOp.getEdgeCondition() == 0) ? 1 : 0;
        final Kernel kernel = convolveOp.getKernel();
        final int width = kernel.getWidth();
        final int height = kernel.getHeight();
        final int n2 = 24 + width * height * 4;
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacityAndAlignment(n2, 4);
        buffer.putInt(120);
        buffer.putLong(surfaceData.getNativeOps());
        buffer.putInt(n);
        buffer.putInt(width);
        buffer.putInt(height);
        buffer.put(kernel.getKernelData(null));
    }
    
    private static void disableConvolveOp(final RenderQueue renderQueue) {
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacity(4);
        buffer.putInt(121);
    }
    
    public static boolean isRescaleOpValid(final RescaleOp rescaleOp, final BufferedImage bufferedImage) {
        final int numFactors = rescaleOp.getNumFactors();
        final ColorModel colorModel = bufferedImage.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
        }
        if (numFactors != 1 && numFactors != colorModel.getNumColorComponents() && numFactors != colorModel.getNumComponents()) {
            throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components");
        }
        final int type = colorModel.getColorSpace().getType();
        return (type == 5 || type == 6) && numFactors != 2 && numFactors <= 4;
    }
    
    private static void enableRescaleOp(final RenderQueue renderQueue, final SurfaceData surfaceData, final BufferedImage bufferedImage, final RescaleOp rescaleOp) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        final int n = (colorModel.hasAlpha() && colorModel.isAlphaPremultiplied()) ? 1 : 0;
        final int numFactors = rescaleOp.getNumFactors();
        final float[] scaleFactors = rescaleOp.getScaleFactors(null);
        final float[] offsets = rescaleOp.getOffsets(null);
        float[] array;
        float[] array2;
        if (numFactors == 1) {
            array = new float[4];
            array2 = new float[4];
            for (int i = 0; i < 3; ++i) {
                array[i] = scaleFactors[0];
                array2[i] = offsets[0];
            }
            array[3] = 1.0f;
            array2[3] = 0.0f;
        }
        else if (numFactors == 3) {
            array = new float[4];
            array2 = new float[4];
            for (int j = 0; j < 3; ++j) {
                array[j] = scaleFactors[j];
                array2[j] = offsets[j];
            }
            array[3] = 1.0f;
            array2[3] = 0.0f;
        }
        else {
            array = scaleFactors;
            array2 = offsets;
        }
        if (colorModel.getNumComponents() == 1) {
            final int n2 = (1 << colorModel.getComponentSize(0)) - 1;
            for (int k = 0; k < 3; ++k) {
                final float[] array3 = array2;
                final int n3 = k;
                array3[n3] /= n2;
            }
        }
        else {
            for (int l = 0; l < colorModel.getNumComponents(); ++l) {
                final int n4 = (1 << colorModel.getComponentSize(l)) - 1;
                final float[] array4 = array2;
                final int n5 = l;
                array4[n5] /= n4;
            }
        }
        final int n6 = 16 + 4 * 4 * 2;
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacityAndAlignment(n6, 4);
        buffer.putInt(122);
        buffer.putLong(surfaceData.getNativeOps());
        buffer.putInt(n);
        buffer.put(array);
        buffer.put(array2);
    }
    
    private static void disableRescaleOp(final RenderQueue renderQueue) {
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacity(4);
        buffer.putInt(123);
    }
    
    public static boolean isLookupOpValid(final LookupOp lookupOp, final BufferedImage bufferedImage) {
        final LookupTable table = lookupOp.getTable();
        final int numComponents = table.getNumComponents();
        final ColorModel colorModel = bufferedImage.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
        }
        if (numComponents != 1 && numComponents != colorModel.getNumComponents() && numComponents != colorModel.getNumColorComponents()) {
            throw new IllegalArgumentException("Number of arrays in the  lookup table (" + numComponents + ") is not compatible with the src image: " + bufferedImage);
        }
        final int type = colorModel.getColorSpace().getType();
        if (type != 5 && type != 6) {
            return false;
        }
        if (numComponents == 2 || numComponents > 4) {
            return false;
        }
        if (table instanceof ByteLookupTable) {
            final byte[][] table2 = ((ByteLookupTable)table).getTable();
            for (int i = 1; i < table2.length; ++i) {
                if (table2[i].length > 256 || table2[i].length != table2[i - 1].length) {
                    return false;
                }
            }
        }
        else {
            if (!(table instanceof ShortLookupTable)) {
                return false;
            }
            final short[][] table3 = ((ShortLookupTable)table).getTable();
            for (int j = 1; j < table3.length; ++j) {
                if (table3[j].length > 256 || table3[j].length != table3[j - 1].length) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static void enableLookupOp(final RenderQueue renderQueue, final SurfaceData surfaceData, final BufferedImage bufferedImage, final LookupOp lookupOp) {
        final int n = (bufferedImage.getColorModel().hasAlpha() && bufferedImage.isAlphaPremultiplied()) ? 1 : 0;
        final LookupTable table = lookupOp.getTable();
        final int numComponents = table.getNumComponents();
        final int offset = table.getOffset();
        int n2;
        int n3;
        int n4;
        if (table instanceof ShortLookupTable) {
            n2 = ((ShortLookupTable)table).getTable()[0].length;
            n3 = 2;
            n4 = 1;
        }
        else {
            n2 = ((ByteLookupTable)table).getTable()[0].length;
            n3 = 1;
            n4 = 0;
        }
        final int n5 = numComponents * n2 * n3;
        final int n6 = n5 + 3 & 0xFFFFFFFC;
        final int n7 = n6 - n5;
        final int n8 = 32 + n6;
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacityAndAlignment(n8, 4);
        buffer.putInt(124);
        buffer.putLong(surfaceData.getNativeOps());
        buffer.putInt(n);
        buffer.putInt(n4);
        buffer.putInt(numComponents);
        buffer.putInt(n2);
        buffer.putInt(offset);
        if (n4 != 0) {
            final short[][] table2 = ((ShortLookupTable)table).getTable();
            for (int i = 0; i < numComponents; ++i) {
                buffer.put(table2[i]);
            }
        }
        else {
            final byte[][] table3 = ((ByteLookupTable)table).getTable();
            for (int j = 0; j < numComponents; ++j) {
                buffer.put(table3[j]);
            }
        }
        if (n7 != 0) {
            buffer.position(buffer.position() + n7);
        }
    }
    
    private static void disableLookupOp(final RenderQueue renderQueue) {
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacity(4);
        buffer.putInt(125);
    }
}
