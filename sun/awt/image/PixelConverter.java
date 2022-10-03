package sun.awt.image;

import java.awt.image.ColorModel;

public class PixelConverter
{
    public static final PixelConverter instance;
    protected int alphaMask;
    
    protected PixelConverter() {
        this.alphaMask = 0;
    }
    
    public int rgbToPixel(final int n, final ColorModel colorModel) {
        final Object dataElements = colorModel.getDataElements(n, null);
        switch (colorModel.getTransferType()) {
            case 0: {
                final byte[] array = (byte[])dataElements;
                int n2 = 0;
                switch (array.length) {
                    default: {
                        n2 = array[3] << 24;
                    }
                    case 3: {
                        n2 |= (array[2] & 0xFF) << 16;
                    }
                    case 2: {
                        n2 |= (array[1] & 0xFF) << 8;
                    }
                    case 1: {
                        return n2 | (array[0] & 0xFF);
                    }
                }
                break;
            }
            case 1:
            case 2: {
                final short[] array2 = (short[])dataElements;
                return ((array2.length > 1) ? (array2[1] << 16) : 0) | (array2[0] & 0xFFFF);
            }
            case 3: {
                return ((int[])dataElements)[0];
            }
            default: {
                return n;
            }
        }
    }
    
    public int pixelToRgb(final int n, final ColorModel colorModel) {
        return n;
    }
    
    public final int getAlphaMask() {
        return this.alphaMask;
    }
    
    static {
        instance = new PixelConverter();
    }
    
    public static class Rgbx extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Rgbx() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return n << 8;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return 0xFF000000 | n >> 8;
        }
        
        static {
            instance = new Rgbx();
        }
    }
    
    public static class Xrgb extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Xrgb() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return n;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return 0xFF000000 | n;
        }
        
        static {
            instance = new Xrgb();
        }
    }
    
    public static class Argb extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Argb() {
            this.alphaMask = -16777216;
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return n;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return n;
        }
        
        static {
            instance = new Argb();
        }
    }
    
    public static class Ushort565Rgb extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Ushort565Rgb() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (n >> 8 & 0xF800) | (n >> 5 & 0x7E0) | (n >> 3 & 0x1F);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            final int n2 = n >> 11 & 0x1F;
            final int n3 = n2 << 3 | n2 >> 2;
            final int n4 = n >> 5 & 0x3F;
            final int n5 = n4 << 2 | n4 >> 4;
            final int n6 = n & 0x1F;
            return 0xFF000000 | n3 << 16 | n5 << 8 | (n6 << 3 | n6 >> 2);
        }
        
        static {
            instance = new Ushort565Rgb();
        }
    }
    
    public static class Ushort555Rgbx extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Ushort555Rgbx() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (n >> 8 & 0xF800) | (n >> 5 & 0x7C0) | (n >> 2 & 0x3E);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            final int n2 = n >> 11 & 0x1F;
            final int n3 = n2 << 3 | n2 >> 2;
            final int n4 = n >> 6 & 0x1F;
            final int n5 = n4 << 3 | n4 >> 2;
            final int n6 = n >> 1 & 0x1F;
            return 0xFF000000 | n3 << 16 | n5 << 8 | (n6 << 3 | n6 >> 2);
        }
        
        static {
            instance = new Ushort555Rgbx();
        }
    }
    
    public static class Ushort555Rgb extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Ushort555Rgb() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (n >> 9 & 0x7C00) | (n >> 6 & 0x3E0) | (n >> 3 & 0x1F);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            final int n2 = n >> 10 & 0x1F;
            final int n3 = n2 << 3 | n2 >> 2;
            final int n4 = n >> 5 & 0x1F;
            final int n5 = n4 << 3 | n4 >> 2;
            final int n6 = n & 0x1F;
            return 0xFF000000 | n3 << 16 | n5 << 8 | (n6 << 3 | n6 >> 2);
        }
        
        static {
            instance = new Ushort555Rgb();
        }
    }
    
    public static class Ushort4444Argb extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Ushort4444Argb() {
            this.alphaMask = 61440;
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (n >> 16 & 0xF000) | (n >> 12 & 0xF00) | (n >> 8 & 0xF0) | (n >> 4 & 0xF);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return ((n << 16 | n << 12) & 0xFF000000) | ((n << 12 | n << 8) & 0xFF0000) | ((n << 8 | n << 4) & 0xFF00) | ((n << 4 | n << 0) & 0xFF);
        }
        
        static {
            instance = new Ushort4444Argb();
        }
    }
    
    public static class Xbgr extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Xbgr() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (n & 0xFF) << 16 | (n & 0xFF00) | (n >> 16 & 0xFF);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return 0xFF000000 | (n & 0xFF) << 16 | (n & 0xFF00) | (n >> 16 & 0xFF);
        }
        
        static {
            instance = new Xbgr();
        }
    }
    
    public static class Bgrx extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Bgrx() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return n << 24 | (n & 0xFF00) << 8 | (n >> 8 & 0xFF00);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return 0xFF000000 | (n & 0xFF00) << 8 | (n >> 8 & 0xFF00) | n >>> 24;
        }
        
        static {
            instance = new Bgrx();
        }
    }
    
    public static class Rgba extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private Rgba() {
            this.alphaMask = 255;
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return n << 8 | n >>> 24;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return n << 24 | n >>> 8;
        }
        
        static {
            instance = new Rgba();
        }
    }
    
    public static class RgbaPre extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private RgbaPre() {
            this.alphaMask = 255;
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            if (n >> 24 == -1) {
                return n << 8 | n >>> 24;
            }
            final int n2 = n >>> 24;
            final int n3 = n >> 16 & 0xFF;
            final int n4 = n >> 8 & 0xFF;
            final int n5 = n & 0xFF;
            final int n6 = n2 + (n2 >> 7);
            return n3 * n6 >> 8 << 24 | n4 * n6 >> 8 << 16 | n5 * n6 >> 8 << 8 | n2;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            final int n2 = n & 0xFF;
            if (n2 == 255 || n2 == 0) {
                return n >>> 8 | n << 24;
            }
            final int n3 = n >>> 24;
            final int n4 = n >> 16 & 0xFF;
            final int n5 = n >> 8 & 0xFF;
            return ((n3 << 8) - n3) / n2 << 24 | ((n4 << 8) - n4) / n2 << 16 | ((n5 << 8) - n5) / n2 << 8 | n2;
        }
        
        static {
            instance = new RgbaPre();
        }
    }
    
    public static class ArgbPre extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private ArgbPre() {
            this.alphaMask = -16777216;
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            if (n >> 24 == -1) {
                return n;
            }
            final int n2 = n >>> 24;
            final int n3 = n >> 16 & 0xFF;
            final int n4 = n >> 8 & 0xFF;
            final int n5 = n & 0xFF;
            final int n6 = n2 + (n2 >> 7);
            return n2 << 24 | n3 * n6 >> 8 << 16 | n4 * n6 >> 8 << 8 | n5 * n6 >> 8;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            final int n2 = n >>> 24;
            if (n2 == 255 || n2 == 0) {
                return n;
            }
            final int n3 = n >> 16 & 0xFF;
            final int n4 = n >> 8 & 0xFF;
            final int n5 = n & 0xFF;
            return n2 << 24 | ((n3 << 8) - n3) / n2 << 16 | ((n4 << 8) - n4) / n2 << 8 | ((n5 << 8) - n5) / n2;
        }
        
        static {
            instance = new ArgbPre();
        }
    }
    
    public static class ArgbBm extends PixelConverter
    {
        public static final PixelConverter instance;
        
        private ArgbBm() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return n | n >> 31 << 24;
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return n << 7 >> 7;
        }
        
        static {
            instance = new ArgbBm();
        }
    }
    
    public static class ByteGray extends PixelConverter
    {
        static final double RED_MULT = 0.299;
        static final double GRN_MULT = 0.587;
        static final double BLU_MULT = 0.114;
        public static final PixelConverter instance;
        
        private ByteGray() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (int)((n >> 16 & 0xFF) * 0.299 + (n >> 8 & 0xFF) * 0.587 + (n & 0xFF) * 0.114 + 0.5);
        }
        
        @Override
        public int pixelToRgb(final int n, final ColorModel colorModel) {
            return ((0xFF00 | n) << 8 | n) << 8 | n;
        }
        
        static {
            instance = new ByteGray();
        }
    }
    
    public static class UshortGray extends ByteGray
    {
        static final double SHORT_MULT = 257.0;
        static final double USHORT_RED_MULT = 76.843;
        static final double USHORT_GRN_MULT = 150.85899999999998;
        static final double USHORT_BLU_MULT = 29.298000000000002;
        public static final PixelConverter instance;
        
        private UshortGray() {
        }
        
        @Override
        public int rgbToPixel(final int n, final ColorModel colorModel) {
            return (int)((n >> 16 & 0xFF) * 76.843 + (n >> 8 & 0xFF) * 150.85899999999998 + (n & 0xFF) * 29.298000000000002 + 0.5);
        }
        
        @Override
        public int pixelToRgb(int n, final ColorModel colorModel) {
            n >>= 8;
            return ((0xFF00 | n) << 8 | n) << 8 | n;
        }
        
        static {
            instance = new UshortGray();
        }
    }
}
