package org.apache.poi.xddf.usermodel.text;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTabAlignType;

public enum TabAlignment
{
    CENTER(STTextTabAlignType.CTR), 
    DECIMAL(STTextTabAlignType.DEC), 
    LEFT(STTextTabAlignType.L), 
    RIGHT(STTextTabAlignType.R);
    
    final STTextTabAlignType.Enum underlying;
    private static final HashMap<STTextTabAlignType.Enum, TabAlignment> reverse;
    
    private TabAlignment(final STTextTabAlignType.Enum align) {
        this.underlying = align;
    }
    
    static TabAlignment valueOf(final STTextTabAlignType.Enum align) {
        return TabAlignment.reverse.get(align);
    }
    
    static {
        reverse = new HashMap<STTextTabAlignType.Enum, TabAlignment>();
        for (final TabAlignment value : values()) {
            TabAlignment.reverse.put(value.underlying, value);
        }
    }
}
