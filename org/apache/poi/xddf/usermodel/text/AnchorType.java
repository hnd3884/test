package org.apache.poi.xddf.usermodel.text;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;

public enum AnchorType
{
    BOTTOM(STTextAnchoringType.B), 
    CENTER(STTextAnchoringType.CTR), 
    DISTRIBUTED(STTextAnchoringType.DIST), 
    JUSTIFIED(STTextAnchoringType.JUST), 
    TOP(STTextAnchoringType.T);
    
    final STTextAnchoringType.Enum underlying;
    private static final HashMap<STTextAnchoringType.Enum, AnchorType> reverse;
    
    private AnchorType(final STTextAnchoringType.Enum caps) {
        this.underlying = caps;
    }
    
    static AnchorType valueOf(final STTextAnchoringType.Enum caps) {
        return AnchorType.reverse.get(caps);
    }
    
    static {
        reverse = new HashMap<STTextAnchoringType.Enum, AnchorType>();
        for (final AnchorType value : values()) {
            AnchorType.reverse.put(value.underlying, value);
        }
    }
}
