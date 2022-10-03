package javax.swing.plaf.nimbus;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

class EffectUtils
{
    static void clearImage(final BufferedImage bufferedImage) {
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.dispose();
    }
    
    static BufferedImage gaussianBlur(final BufferedImage bufferedImage, BufferedImage colorModelCompatibleImage, final int n) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        if (colorModelCompatibleImage == null || colorModelCompatibleImage.getWidth() != width || colorModelCompatibleImage.getHeight() != height || bufferedImage.getType() != colorModelCompatibleImage.getType()) {
            colorModelCompatibleImage = createColorModelCompatibleImage(bufferedImage);
        }
        final float[] gaussianKernel = createGaussianKernel(n);
        if (bufferedImage.getType() == 2) {
            final int[] array = new int[width * height];
            final int[] array2 = new int[width * height];
            getPixels(bufferedImage, 0, 0, width, height, array);
            blur(array, array2, width, height, gaussianKernel, n);
            blur(array2, array, height, width, gaussianKernel, n);
            setPixels(colorModelCompatibleImage, 0, 0, width, height, array);
        }
        else {
            if (bufferedImage.getType() != 10) {
                throw new IllegalArgumentException("EffectUtils.gaussianBlur() src image is not a supported type, type=[" + bufferedImage.getType() + "]");
            }
            final byte[] array3 = new byte[width * height];
            final byte[] array4 = new byte[width * height];
            getPixels(bufferedImage, 0, 0, width, height, array3);
            blur(array3, array4, width, height, gaussianKernel, n);
            blur(array4, array3, height, width, gaussianKernel, n);
            setPixels(colorModelCompatibleImage, 0, 0, width, height, array3);
        }
        return colorModelCompatibleImage;
    }
    
    private static void blur(final int[] array, final int[] array2, final int n, final int n2, final float[] array3, final int n3) {
        for (int i = 0; i < n2; ++i) {
            int n4 = i;
            final int n5 = i * n;
            for (int j = 0; j < n; ++j) {
                float n9;
                float n8;
                float n7;
                float n6 = n7 = (n8 = (n9 = 0.0f));
                for (int k = -n3; k <= n3; ++k) {
                    int n10 = j + k;
                    if (n10 < 0 || n10 >= n) {
                        n10 = (j + n) % n;
                    }
                    final int n11 = array[n5 + n10];
                    final float n12 = array3[n3 + k];
                    n7 += n12 * (n11 >> 24 & 0xFF);
                    n6 += n12 * (n11 >> 16 & 0xFF);
                    n8 += n12 * (n11 >> 8 & 0xFF);
                    n9 += n12 * (n11 & 0xFF);
                }
                final int n13 = (int)(n7 + 0.5f);
                final int n14 = (int)(n6 + 0.5f);
                final int n15 = (int)(n8 + 0.5f);
                final int n16 = (int)(n9 + 0.5f);
                array2[n4] = (((n13 > 255) ? 255 : n13) << 24 | ((n14 > 255) ? 255 : n14) << 16 | ((n15 > 255) ? 255 : n15) << 8 | ((n16 > 255) ? 255 : n16));
                n4 += n2;
            }
        }
    }
    
    static void blur(final byte[] array, final byte[] array2, final int n, final int n2, final float[] array3, final int n3) {
        for (int i = 0; i < n2; ++i) {
            int n4 = i;
            final int n5 = i * n;
            for (int j = 0; j < n; ++j) {
                float n6 = 0.0f;
                for (int k = -n3; k <= n3; ++k) {
                    int n7 = j + k;
                    if (n7 < 0 || n7 >= n) {
                        n7 = (j + n) % n;
                    }
                    n6 += array3[n3 + k] * (array[n5 + n7] & 0xFF);
                }
                final int n8 = (int)(n6 + 0.5f);
                array2[n4] = (byte)((n8 > 255) ? 255 : n8);
                n4 += n2;
            }
        }
    }
    
    static float[] createGaussianKernel(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }
        final float[] array = new float[n * 2 + 1];
        final float n2 = n / 3.0f;
        final float n3 = 2.0f * n2 * n2;
        final float n4 = (float)Math.sqrt(n3 * 3.141592653589793);
        float n5 = 0.0f;
        for (int i = -n; i <= n; ++i) {
            final float n6 = (float)(i * i);
            final int n7 = i + n;
            array[n7] = (float)Math.exp(-n6 / n3) / n4;
            n5 += array[n7];
        }
        for (int j = 0; j < array.length; ++j) {
            final float[] array2 = array;
            final int n8 = j;
            array2[n8] /= n5;
        }
        return array;
    }
    
    static byte[] getPixels(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, byte[] array) {
        if (n3 == 0 || n4 == 0) {
            return new byte[0];
        }
        if (array == null) {
            array = new byte[n3 * n4];
        }
        else if (array.length < n3 * n4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }
        if (bufferedImage.getType() == 10) {
            return (byte[])bufferedImage.getRaster().getDataElements(n, n2, n3, n4, array);
        }
        throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
    }
    
    static void setPixels(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final byte[] array) {
        if (array == null || n3 == 0 || n4 == 0) {
            return;
        }
        if (array.length < n3 * n4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }
        if (bufferedImage.getType() == 10) {
            bufferedImage.getRaster().setDataElements(n, n2, n3, n4, array);
            return;
        }
        throw new IllegalArgumentException("Only type BYTE_GRAY is supported");
    }
    
    public static int[] getPixels(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, int[] array) {
        if (n3 == 0 || n4 == 0) {
            return new int[0];
        }
        if (array == null) {
            array = new int[n3 * n4];
        }
        else if (array.length < n3 * n4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }
        final int type = bufferedImage.getType();
        if (type == 2 || type == 1) {
            return (int[])bufferedImage.getRaster().getDataElements(n, n2, n3, n4, array);
        }
        return bufferedImage.getRGB(n, n2, n3, n4, array, 0, n3);
    }
    
    public static void setPixels(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int[] array) {
        if (array == null || n3 == 0 || n4 == 0) {
            return;
        }
        if (array.length < n3 * n4) {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }
        final int type = bufferedImage.getType();
        if (type == 2 || type == 1) {
            bufferedImage.getRaster().setDataElements(n, n2, n3, n4, array);
        }
        else {
            bufferedImage.setRGB(n, n2, n3, n4, array, 0, n3);
        }
    }
    
    public static BufferedImage createColorModelCompatibleImage(final BufferedImage bufferedImage) {
        final ColorModel colorModel = bufferedImage.getColorModel();
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), null);
    }
    
    public static BufferedImage createCompatibleTranslucentImage(final int n, final int n2) {
        return isHeadless() ? new BufferedImage(n, n2, 2) : getGraphicsConfiguration().createCompatibleImage(n, n2, 3);
    }
    
    private static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }
    
    private static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
}
