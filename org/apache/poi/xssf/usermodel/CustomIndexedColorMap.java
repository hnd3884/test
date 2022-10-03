package org.apache.poi.xssf.usermodel;

import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColors;

public class CustomIndexedColorMap implements IndexedColorMap
{
    private final byte[][] colorIndex;
    
    private CustomIndexedColorMap(final byte[][] colors) {
        this.colorIndex = colors;
    }
    
    @Override
    public byte[] getRGB(final int index) {
        if (this.colorIndex == null || index < 0 || index >= this.colorIndex.length) {
            return null;
        }
        return this.colorIndex[index];
    }
    
    public static CustomIndexedColorMap fromColors(final CTColors colors) {
        if (colors == null || !colors.isSetIndexedColors()) {
            return null;
        }
        final List<CTRgbColor> rgbColorList = colors.getIndexedColors().getRgbColorList();
        final byte[][] customColorIndex = new byte[rgbColorList.size()][3];
        for (int i = 0; i < rgbColorList.size(); ++i) {
            customColorIndex[i] = rgbColorList.get(i).getRgb();
        }
        return new CustomIndexedColorMap(customColorIndex);
    }
}
