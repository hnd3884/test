package javax.swing.plaf.nimbus;

import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.awt.image.BufferedImage;

class InnerShadowEffect extends ShadowEffect
{
    @Override
    EffectType getEffectType() {
        return EffectType.OVER;
    }
    
    @Override
    BufferedImage applyEffect(final BufferedImage bufferedImage, BufferedImage bufferedImage2, final int n, final int n2) {
        if (bufferedImage == null || bufferedImage.getType() != 2) {
            throw new IllegalArgumentException("Effect only works with source images of type BufferedImage.TYPE_INT_ARGB.");
        }
        if (bufferedImage2 != null && bufferedImage2.getType() != 2) {
            throw new IllegalArgumentException("Effect only works with destination images of type BufferedImage.TYPE_INT_ARGB.");
        }
        final double radians = Math.toRadians(this.angle - 90);
        final int n3 = (int)(Math.sin(radians) * this.distance);
        final int n4 = (int)(Math.cos(radians) * this.distance);
        final int n5 = n3 + this.size;
        final int n6 = n3 + this.size;
        final int n7 = n + n3 + this.size + this.size;
        final int n8 = n2 + n3 + this.size;
        final int[] tmpIntArray = Effect.getArrayCache().getTmpIntArray(n);
        final byte[] tmpByteArray1 = Effect.getArrayCache().getTmpByteArray1(n7 * n8);
        Arrays.fill(tmpByteArray1, (byte)(-1));
        final byte[] tmpByteArray2 = Effect.getArrayCache().getTmpByteArray2(n7 * n8);
        final byte[] tmpByteArray3 = Effect.getArrayCache().getTmpByteArray3(n7 * n8);
        final WritableRaster raster = bufferedImage.getRaster();
        for (int i = 0; i < n2; ++i) {
            final int n9 = (i + n6) * n7;
            raster.getDataElements(0, i, n, 1, tmpIntArray);
            for (int j = 0; j < n; ++j) {
                tmpByteArray1[n9 + (j + n5)] = (byte)(255 - ((tmpIntArray[j] & 0xFF000000) >>> 24) & 0xFF);
            }
        }
        final float[] gaussianKernel = EffectUtils.createGaussianKernel(this.size * 2);
        EffectUtils.blur(tmpByteArray1, tmpByteArray3, n7, n8, gaussianKernel, this.size * 2);
        EffectUtils.blur(tmpByteArray3, tmpByteArray2, n8, n7, gaussianKernel, this.size * 2);
        final float min = Math.min(1.0f / (1.0f - 0.01f * this.spread), 255.0f);
        for (int k = 0; k < tmpByteArray2.length; ++k) {
            final int n10 = (int)((tmpByteArray2[k] & 0xFF) * min);
            tmpByteArray2[k] = (byte)((n10 > 255) ? -1 : ((byte)n10));
        }
        if (bufferedImage2 == null) {
            bufferedImage2 = new BufferedImage(n, n2, 2);
        }
        final WritableRaster raster2 = bufferedImage2.getRaster();
        final int red = this.color.getRed();
        final int green = this.color.getGreen();
        final int blue = this.color.getBlue();
        for (int l = 0; l < n2; ++l) {
            final int n11 = l + n6;
            final int n12 = n11 * n7;
            final int n13 = (n11 - n4) * n7;
            for (int n14 = 0; n14 < n; ++n14) {
                final int n15 = n14 + n5;
                tmpIntArray[n14] = (((byte)Math.min(255 - (tmpByteArray1[n12 + n15] & 0xFF), tmpByteArray2[n13 + (n15 - n3)] & 0xFF) & 0xFF) << 24 | red << 16 | green << 8 | blue);
            }
            raster2.setDataElements(0, l, n, 1, tmpIntArray);
        }
        return bufferedImage2;
    }
}
