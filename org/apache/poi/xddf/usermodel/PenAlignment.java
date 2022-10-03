package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STPenAlignment;

public enum PenAlignment
{
    CENTER(STPenAlignment.CTR), 
    IN(STPenAlignment.IN);
    
    final STPenAlignment.Enum underlying;
    private static final HashMap<STPenAlignment.Enum, PenAlignment> reverse;
    
    private PenAlignment(final STPenAlignment.Enum alignment) {
        this.underlying = alignment;
    }
    
    static PenAlignment valueOf(final STPenAlignment.Enum LineEndWidth) {
        return PenAlignment.reverse.get(LineEndWidth);
    }
    
    static {
        reverse = new HashMap<STPenAlignment.Enum, PenAlignment>();
        for (final PenAlignment value : values()) {
            PenAlignment.reverse.put(value.underlying, value);
        }
    }
}
