package sun.management.counter;

import java.io.Serializable;

public class Units implements Serializable
{
    private static final int NUNITS = 8;
    private static Units[] map;
    private final String name;
    private final int value;
    public static final Units INVALID;
    public static final Units NONE;
    public static final Units BYTES;
    public static final Units TICKS;
    public static final Units EVENTS;
    public static final Units STRING;
    public static final Units HERTZ;
    private static final long serialVersionUID = 6992337162326171013L;
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public int intValue() {
        return this.value;
    }
    
    public static Units toUnits(final int n) {
        if (n < 0 || n >= Units.map.length || Units.map[n] == null) {
            return Units.INVALID;
        }
        return Units.map[n];
    }
    
    private Units(final String name, final int value) {
        this.name = name;
        this.value = value;
        Units.map[value] = this;
    }
    
    static {
        Units.map = new Units[8];
        INVALID = new Units("Invalid", 0);
        NONE = new Units("None", 1);
        BYTES = new Units("Bytes", 2);
        TICKS = new Units("Ticks", 3);
        EVENTS = new Units("Events", 4);
        STRING = new Units("String", 5);
        HERTZ = new Units("Hertz", 6);
    }
}
