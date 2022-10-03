package org.apache.poi.xddf.usermodel.chart;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.STErrDir;

public enum ErrorDirection
{
    X(STErrDir.X), 
    Y(STErrDir.Y);
    
    final STErrDir.Enum underlying;
    private static final HashMap<STErrDir.Enum, ErrorDirection> reverse;
    
    private ErrorDirection(final STErrDir.Enum direction) {
        this.underlying = direction;
    }
    
    static ErrorDirection valueOf(final STErrDir.Enum direction) {
        return ErrorDirection.reverse.get(direction);
    }
    
    static {
        reverse = new HashMap<STErrDir.Enum, ErrorDirection>();
        for (final ErrorDirection value : values()) {
            ErrorDirection.reverse.put(value.underlying, value);
        }
    }
}
