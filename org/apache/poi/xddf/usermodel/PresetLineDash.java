package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;

public enum PresetLineDash
{
    DASH(STPresetLineDashVal.DASH), 
    DASH_DOT(STPresetLineDashVal.DASH_DOT), 
    DOT(STPresetLineDashVal.DOT), 
    LARGE_DASH(STPresetLineDashVal.LG_DASH), 
    LARGE_DASH_DOT(STPresetLineDashVal.LG_DASH_DOT), 
    LARGE_DASH_DOT_DOT(STPresetLineDashVal.LG_DASH_DOT_DOT), 
    SOLID(STPresetLineDashVal.SOLID), 
    SYSTEM_DASH(STPresetLineDashVal.SYS_DASH), 
    SYSTEM_DASH_DOT(STPresetLineDashVal.SYS_DASH_DOT), 
    SYSTEM_DASH_DOT_DOT(STPresetLineDashVal.SYS_DASH_DOT_DOT), 
    SYSTEM_DOT(STPresetLineDashVal.SYS_DOT);
    
    final STPresetLineDashVal.Enum underlying;
    private static final HashMap<STPresetLineDashVal.Enum, PresetLineDash> reverse;
    
    private PresetLineDash(final STPresetLineDashVal.Enum dash) {
        this.underlying = dash;
    }
    
    static PresetLineDash valueOf(final STPresetLineDashVal.Enum dash) {
        return PresetLineDash.reverse.get(dash);
    }
    
    static {
        reverse = new HashMap<STPresetLineDashVal.Enum, PresetLineDash>();
        for (final PresetLineDash value : values()) {
            PresetLineDash.reverse.put(value.underlying, value);
        }
    }
}
