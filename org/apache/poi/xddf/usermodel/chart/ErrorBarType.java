package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STErrBarType;

public enum ErrorBarType
{
    BOTH(STErrBarType.BOTH), 
    MINUS(STErrBarType.MINUS), 
    PLUS(STErrBarType.PLUS);
    
    final STErrBarType.Enum underlying;
    private static final HashMap<STErrBarType.Enum, ErrorBarType> reverse;
    
    private ErrorBarType(final STErrBarType.Enum barType) {
        this.underlying = barType;
    }
    
    static ErrorBarType valueOf(final STErrBarType.Enum barType) {
        return ErrorBarType.reverse.get(barType);
    }
    
    static {
        reverse = new HashMap<STErrBarType.Enum, ErrorBarType>();
        for (final ErrorBarType value : values()) {
            ErrorBarType.reverse.put(value.underlying, value);
        }
    }
}
