package com.sun.imageio.plugins.bmp;

public class BMPCompressionTypes
{
    private static final String[] compressionTypeNames;
    
    static int getType(final String s) {
        for (int i = 0; i < BMPCompressionTypes.compressionTypeNames.length; ++i) {
            if (BMPCompressionTypes.compressionTypeNames[i].equals(s)) {
                return i;
            }
        }
        return 0;
    }
    
    static String getName(final int n) {
        return BMPCompressionTypes.compressionTypeNames[n];
    }
    
    public static String[] getCompressionTypes() {
        return BMPCompressionTypes.compressionTypeNames.clone();
    }
    
    static {
        compressionTypeNames = new String[] { "BI_RGB", "BI_RLE8", "BI_RLE4", "BI_BITFIELDS", "BI_JPEG", "BI_PNG" };
    }
}
