package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;

public enum BlackWhiteMode
{
    AUTO(STBlackWhiteMode.AUTO), 
    BLACK(STBlackWhiteMode.BLACK), 
    BLACK_GRAY(STBlackWhiteMode.BLACK_GRAY), 
    BLACK_WHITE(STBlackWhiteMode.BLACK_WHITE);
    
    final STBlackWhiteMode.Enum underlying;
    private static final HashMap<STBlackWhiteMode.Enum, BlackWhiteMode> reverse;
    
    private BlackWhiteMode(final STBlackWhiteMode.Enum mode) {
        this.underlying = mode;
    }
    
    static BlackWhiteMode valueOf(final STBlackWhiteMode.Enum mode) {
        return BlackWhiteMode.reverse.get(mode);
    }
    
    static {
        reverse = new HashMap<STBlackWhiteMode.Enum, BlackWhiteMode>();
        for (final BlackWhiteMode value : values()) {
            BlackWhiteMode.reverse.put(value.underlying, value);
        }
    }
}
