package javax.swing;

import java.io.Serializable;

public class SizeRequirements implements Serializable
{
    public int minimum;
    public int preferred;
    public int maximum;
    public float alignment;
    
    public SizeRequirements() {
        this.minimum = 0;
        this.preferred = 0;
        this.maximum = 0;
        this.alignment = 0.5f;
    }
    
    public SizeRequirements(final int minimum, final int preferred, final int maximum, final float n) {
        this.minimum = minimum;
        this.preferred = preferred;
        this.maximum = maximum;
        this.alignment = ((n > 1.0f) ? 1.0f : ((n < 0.0f) ? 0.0f : n));
    }
    
    @Override
    public String toString() {
        return "[" + this.minimum + "," + this.preferred + "," + this.maximum + "]@" + this.alignment;
    }
    
    public static SizeRequirements getTiledSizeRequirements(final SizeRequirements[] array) {
        final SizeRequirements sizeRequirements = new SizeRequirements();
        for (int i = 0; i < array.length; ++i) {
            final SizeRequirements sizeRequirements2 = array[i];
            sizeRequirements.minimum = (int)Math.min(sizeRequirements.minimum + (long)sizeRequirements2.minimum, 2147483647L);
            sizeRequirements.preferred = (int)Math.min(sizeRequirements.preferred + (long)sizeRequirements2.preferred, 2147483647L);
            sizeRequirements.maximum = (int)Math.min(sizeRequirements.maximum + (long)sizeRequirements2.maximum, 2147483647L);
        }
        return sizeRequirements;
    }
    
    public static SizeRequirements getAlignedSizeRequirements(final SizeRequirements[] array) {
        final SizeRequirements sizeRequirements = new SizeRequirements();
        final SizeRequirements sizeRequirements2 = new SizeRequirements();
        for (int i = 0; i < array.length; ++i) {
            final SizeRequirements sizeRequirements3 = array[i];
            final int n = (int)(sizeRequirements3.alignment * sizeRequirements3.minimum);
            final int n2 = sizeRequirements3.minimum - n;
            sizeRequirements.minimum = Math.max(n, sizeRequirements.minimum);
            sizeRequirements2.minimum = Math.max(n2, sizeRequirements2.minimum);
            final int n3 = (int)(sizeRequirements3.alignment * sizeRequirements3.preferred);
            final int n4 = sizeRequirements3.preferred - n3;
            sizeRequirements.preferred = Math.max(n3, sizeRequirements.preferred);
            sizeRequirements2.preferred = Math.max(n4, sizeRequirements2.preferred);
            final int n5 = (int)(sizeRequirements3.alignment * sizeRequirements3.maximum);
            final int n6 = sizeRequirements3.maximum - n5;
            sizeRequirements.maximum = Math.max(n5, sizeRequirements.maximum);
            sizeRequirements2.maximum = Math.max(n6, sizeRequirements2.maximum);
        }
        final int n7 = (int)Math.min(sizeRequirements.minimum + (long)sizeRequirements2.minimum, 2147483647L);
        final int n8 = (int)Math.min(sizeRequirements.preferred + (long)sizeRequirements2.preferred, 2147483647L);
        final int n9 = (int)Math.min(sizeRequirements.maximum + (long)sizeRequirements2.maximum, 2147483647L);
        float n10 = 0.0f;
        if (n7 > 0) {
            final float n11 = sizeRequirements.minimum / (float)n7;
            n10 = ((n11 > 1.0f) ? 1.0f : ((n11 < 0.0f) ? 0.0f : n11));
        }
        return new SizeRequirements(n7, n8, n9, n10);
    }
    
    public static void calculateTiledPositions(final int n, final SizeRequirements sizeRequirements, final SizeRequirements[] array, final int[] array2, final int[] array3) {
        calculateTiledPositions(n, sizeRequirements, array, array2, array3, true);
    }
    
    public static void calculateTiledPositions(final int n, final SizeRequirements sizeRequirements, final SizeRequirements[] array, final int[] array2, final int[] array3, final boolean b) {
        long n2 = 0L;
        long n3 = 0L;
        long n4 = 0L;
        for (int i = 0; i < array.length; ++i) {
            n2 += array[i].minimum;
            n3 += array[i].preferred;
            n4 += array[i].maximum;
        }
        if (n >= n3) {
            expandedTile(n, n2, n3, n4, array, array2, array3, b);
        }
        else {
            compressedTile(n, n2, n3, n4, array, array2, array3, b);
        }
    }
    
    private static void compressedTile(final int n, final long n2, final long n3, final long n4, final SizeRequirements[] array, final int[] array2, final int[] array3, final boolean b) {
        final float n5 = (float)Math.min(n3 - n, n3 - n2);
        final float n6 = (n3 - n2 == 0L) ? 0.0f : (n5 / (n3 - n2));
        if (b) {
            int n7 = 0;
            for (int i = 0; i < array3.length; ++i) {
                array2[i] = n7;
                final SizeRequirements sizeRequirements = array[i];
                array3[i] = (int)(sizeRequirements.preferred - n6 * (sizeRequirements.preferred - sizeRequirements.minimum));
                n7 = (int)Math.min(n7 + (long)array3[i], 2147483647L);
            }
        }
        else {
            int n8 = n;
            for (int j = 0; j < array3.length; ++j) {
                final SizeRequirements sizeRequirements2 = array[j];
                array3[j] = (int)(sizeRequirements2.preferred - n6 * (sizeRequirements2.preferred - sizeRequirements2.minimum));
                array2[j] = n8 - array3[j];
                n8 = (int)Math.max(n8 - (long)array3[j], 0L);
            }
        }
    }
    
    private static void expandedTile(final int n, final long n2, final long n3, final long n4, final SizeRequirements[] array, final int[] array2, final int[] array3, final boolean b) {
        final float n5 = (float)Math.min(n - n3, n4 - n3);
        final float n6 = (n4 - n3 == 0L) ? 0.0f : (n5 / (n4 - n3));
        if (b) {
            int n7 = 0;
            for (int i = 0; i < array3.length; ++i) {
                array2[i] = n7;
                final SizeRequirements sizeRequirements = array[i];
                array3[i] = (int)Math.min(sizeRequirements.preferred + (long)(int)(n6 * (sizeRequirements.maximum - sizeRequirements.preferred)), 2147483647L);
                n7 = (int)Math.min(n7 + (long)array3[i], 2147483647L);
            }
        }
        else {
            int n8 = n;
            for (int j = 0; j < array3.length; ++j) {
                final SizeRequirements sizeRequirements2 = array[j];
                array3[j] = (int)Math.min(sizeRequirements2.preferred + (long)(int)(n6 * (sizeRequirements2.maximum - sizeRequirements2.preferred)), 2147483647L);
                array2[j] = n8 - array3[j];
                n8 = (int)Math.max(n8 - (long)array3[j], 0L);
            }
        }
    }
    
    public static void calculateAlignedPositions(final int n, final SizeRequirements sizeRequirements, final SizeRequirements[] array, final int[] array2, final int[] array3) {
        calculateAlignedPositions(n, sizeRequirements, array, array2, array3, true);
    }
    
    public static void calculateAlignedPositions(final int n, final SizeRequirements sizeRequirements, final SizeRequirements[] array, final int[] array2, final int[] array3, final boolean b) {
        final int n2 = (int)(n * (b ? sizeRequirements.alignment : (1.0f - sizeRequirements.alignment)));
        final int n3 = n - n2;
        for (int i = 0; i < array.length; ++i) {
            final SizeRequirements sizeRequirements2 = array[i];
            final int n4 = (int)(sizeRequirements2.maximum * (b ? sizeRequirements2.alignment : (1.0f - sizeRequirements2.alignment)));
            final int n5 = sizeRequirements2.maximum - n4;
            final int min = Math.min(n2, n4);
            final int min2 = Math.min(n3, n5);
            array2[i] = n2 - min;
            array3[i] = (int)Math.min(min + (long)min2, 2147483647L);
        }
    }
    
    public static int[] adjustSizes(final int n, final SizeRequirements[] array) {
        return new int[0];
    }
}
