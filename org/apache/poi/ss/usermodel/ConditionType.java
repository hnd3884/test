package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;

public class ConditionType
{
    private static Map<Integer, ConditionType> lookup;
    public static final ConditionType CELL_VALUE_IS;
    public static final ConditionType FORMULA;
    public static final ConditionType COLOR_SCALE;
    public static final ConditionType DATA_BAR;
    public static final ConditionType FILTER;
    public static final ConditionType ICON_SET;
    public final byte id;
    public final String type;
    
    @Override
    public String toString() {
        return this.id + " - " + this.type;
    }
    
    public static ConditionType forId(final byte id) {
        return forId((int)id);
    }
    
    public static ConditionType forId(final int id) {
        return ConditionType.lookup.get(id);
    }
    
    private ConditionType(final int id, final String type) {
        this.id = (byte)id;
        this.type = type;
        ConditionType.lookup.put(id, this);
    }
    
    static {
        ConditionType.lookup = new HashMap<Integer, ConditionType>();
        CELL_VALUE_IS = new ConditionType(1, "cellIs");
        FORMULA = new ConditionType(2, "expression");
        COLOR_SCALE = new ConditionType(3, "colorScale");
        DATA_BAR = new ConditionType(4, "dataBar");
        FILTER = new ConditionType(5, null);
        ICON_SET = new ConditionType(6, "iconSet");
    }
}
