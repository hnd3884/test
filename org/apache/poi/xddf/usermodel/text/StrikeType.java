package org.apache.poi.xddf.usermodel.text;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;

public enum StrikeType
{
    DOUBLE_STRIKE(STTextStrikeType.DBL_STRIKE), 
    NO_STRIKE(STTextStrikeType.NO_STRIKE), 
    SINGLE_STRIKE(STTextStrikeType.SNG_STRIKE);
    
    final STTextStrikeType.Enum underlying;
    private static final HashMap<STTextStrikeType.Enum, StrikeType> reverse;
    
    private StrikeType(final STTextStrikeType.Enum strike) {
        this.underlying = strike;
    }
    
    static StrikeType valueOf(final STTextStrikeType.Enum strike) {
        return StrikeType.reverse.get(strike);
    }
    
    static {
        reverse = new HashMap<STTextStrikeType.Enum, StrikeType>();
        for (final StrikeType value : values()) {
            StrikeType.reverse.put(value.underlying, value);
        }
    }
}
