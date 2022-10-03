package org.apache.poi.xwpf.model;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

public final class WMLHelper
{
    public static boolean convertSTOnOffToBoolean(final STOnOff.Enum value) {
        return value == STOnOff.TRUE || value == STOnOff.ON || value == STOnOff.X_1;
    }
    
    public static STOnOff.Enum convertBooleanToSTOnOff(final boolean value) {
        return value ? STOnOff.TRUE : STOnOff.FALSE;
    }
}
