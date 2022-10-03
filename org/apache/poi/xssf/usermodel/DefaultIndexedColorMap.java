package org.apache.poi.xssf.usermodel;

import org.apache.poi.hssf.util.HSSFColor;

public class DefaultIndexedColorMap implements IndexedColorMap
{
    @Override
    public byte[] getRGB(final int index) {
        return getDefaultRGB(index);
    }
    
    public static byte[] getDefaultRGB(final int index) {
        final HSSFColor hssfColor = HSSFColor.getIndexHash().get(index);
        if (hssfColor == null) {
            return null;
        }
        final short[] rgbShort = hssfColor.getTriplet();
        return new byte[] { (byte)rgbShort[0], (byte)rgbShort[1], (byte)rgbShort[2] };
    }
}
