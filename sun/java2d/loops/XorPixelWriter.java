package sun.java2d.loops;

import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;

abstract class XorPixelWriter extends PixelWriter
{
    protected ColorModel dstCM;
    
    @Override
    public void writePixel(final int n, final int n2) {
        final Object dataElements = this.dstRast.getDataElements(n, n2, null);
        this.xorPixel(dataElements);
        this.dstRast.setDataElements(n, n2, dataElements);
    }
    
    protected abstract void xorPixel(final Object p0);
    
    public static class ByteData extends XorPixelWriter
    {
        byte[] xorData;
        
        ByteData(final Object o, final Object o2) {
            this.xorData = (byte[])o;
            this.xorPixel(o2);
            this.xorData = (byte[])o2;
        }
        
        @Override
        protected void xorPixel(final Object o) {
            final byte[] array = (byte[])o;
            for (int i = 0; i < array.length; ++i) {
                final byte[] array2 = array;
                final int n = i;
                array2[n] ^= this.xorData[i];
            }
        }
    }
    
    public static class ShortData extends XorPixelWriter
    {
        short[] xorData;
        
        ShortData(final Object o, final Object o2) {
            this.xorData = (short[])o;
            this.xorPixel(o2);
            this.xorData = (short[])o2;
        }
        
        @Override
        protected void xorPixel(final Object o) {
            final short[] array = (short[])o;
            for (int i = 0; i < array.length; ++i) {
                final short[] array2 = array;
                final int n = i;
                array2[n] ^= this.xorData[i];
            }
        }
    }
    
    public static class IntData extends XorPixelWriter
    {
        int[] xorData;
        
        IntData(final Object o, final Object o2) {
            this.xorData = (int[])o;
            this.xorPixel(o2);
            this.xorData = (int[])o2;
        }
        
        @Override
        protected void xorPixel(final Object o) {
            final int[] array = (int[])o;
            for (int i = 0; i < array.length; ++i) {
                final int[] array2 = array;
                final int n = i;
                array2[n] ^= this.xorData[i];
            }
        }
    }
    
    public static class FloatData extends XorPixelWriter
    {
        int[] xorData;
        
        FloatData(final Object o, final Object o2) {
            final float[] array = (float[])o;
            final float[] array2 = (float[])o2;
            this.xorData = new int[array.length];
            for (int i = 0; i < array.length; ++i) {
                this.xorData[i] = (Float.floatToIntBits(array[i]) ^ Float.floatToIntBits(array2[i]));
            }
        }
        
        @Override
        protected void xorPixel(final Object o) {
            final float[] array = (float[])o;
            for (int i = 0; i < array.length; ++i) {
                array[i] = Float.intBitsToFloat(Float.floatToIntBits(array[i]) ^ this.xorData[i]);
            }
        }
    }
    
    public static class DoubleData extends XorPixelWriter
    {
        long[] xorData;
        
        DoubleData(final Object o, final Object o2) {
            final double[] array = (double[])o;
            final double[] array2 = (double[])o2;
            this.xorData = new long[array.length];
            for (int i = 0; i < array.length; ++i) {
                this.xorData[i] = (Double.doubleToLongBits(array[i]) ^ Double.doubleToLongBits(array2[i]));
            }
        }
        
        @Override
        protected void xorPixel(final Object o) {
            final double[] array = (double[])o;
            for (int i = 0; i < array.length; ++i) {
                array[i] = Double.longBitsToDouble(Double.doubleToLongBits(array[i]) ^ this.xorData[i]);
            }
        }
    }
}
