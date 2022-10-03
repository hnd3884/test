package sun.management.counter;

import java.io.Serializable;

public class Variability implements Serializable
{
    private static final int NATTRIBUTES = 4;
    private static Variability[] map;
    private String name;
    private int value;
    public static final Variability INVALID;
    public static final Variability CONSTANT;
    public static final Variability MONOTONIC;
    public static final Variability VARIABLE;
    private static final long serialVersionUID = 6992337162326171013L;
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public int intValue() {
        return this.value;
    }
    
    public static Variability toVariability(final int n) {
        if (n < 0 || n >= Variability.map.length || Variability.map[n] == null) {
            return Variability.INVALID;
        }
        return Variability.map[n];
    }
    
    private Variability(final String name, final int value) {
        this.name = name;
        this.value = value;
        Variability.map[value] = this;
    }
    
    static {
        Variability.map = new Variability[4];
        INVALID = new Variability("Invalid", 0);
        CONSTANT = new Variability("Constant", 1);
        MONOTONIC = new Variability("Monotonic", 2);
        VARIABLE = new Variability("Variable", 3);
    }
}
