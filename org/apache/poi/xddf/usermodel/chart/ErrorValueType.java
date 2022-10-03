package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STErrValType;

public enum ErrorValueType
{
    CUSTOM(STErrValType.CUST), 
    FIXED_VALUE(STErrValType.FIXED_VAL), 
    PERCENTAGE(STErrValType.PERCENTAGE), 
    STANDARD_DEVIATION(STErrValType.STD_DEV), 
    STANDARD_ERROR(STErrValType.STD_ERR);
    
    final STErrValType.Enum underlying;
    private static final HashMap<STErrValType.Enum, ErrorValueType> reverse;
    
    private ErrorValueType(final STErrValType.Enum valueType) {
        this.underlying = valueType;
    }
    
    static ErrorValueType valueOf(final STErrValType.Enum valueType) {
        return ErrorValueType.reverse.get(valueType);
    }
    
    static {
        reverse = new HashMap<STErrValType.Enum, ErrorValueType>();
        for (final ErrorValueType value : values()) {
            ErrorValueType.reverse.put(value.underlying, value);
        }
    }
}
