package org.apache.poi.xddf.usermodel.text;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextCapsType;

public enum CapsType
{
    ALL(STTextCapsType.ALL), 
    NONE(STTextCapsType.NONE), 
    SMALL(STTextCapsType.SMALL);
    
    final STTextCapsType.Enum underlying;
    private static final HashMap<STTextCapsType.Enum, CapsType> reverse;
    
    private CapsType(final STTextCapsType.Enum caps) {
        this.underlying = caps;
    }
    
    static CapsType valueOf(final STTextCapsType.Enum caps) {
        return CapsType.reverse.get(caps);
    }
    
    static {
        reverse = new HashMap<STTextCapsType.Enum, CapsType>();
        for (final CapsType value : values()) {
            CapsType.reverse.put(value.underlying, value);
        }
    }
}
