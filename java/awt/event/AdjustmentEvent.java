package java.awt.event;

import java.awt.Adjustable;
import java.awt.AWTEvent;

public class AdjustmentEvent extends AWTEvent
{
    public static final int ADJUSTMENT_FIRST = 601;
    public static final int ADJUSTMENT_LAST = 601;
    public static final int ADJUSTMENT_VALUE_CHANGED = 601;
    public static final int UNIT_INCREMENT = 1;
    public static final int UNIT_DECREMENT = 2;
    public static final int BLOCK_DECREMENT = 3;
    public static final int BLOCK_INCREMENT = 4;
    public static final int TRACK = 5;
    Adjustable adjustable;
    int value;
    int adjustmentType;
    boolean isAdjusting;
    private static final long serialVersionUID = 5700290645205279921L;
    
    public AdjustmentEvent(final Adjustable adjustable, final int n, final int n2, final int n3) {
        this(adjustable, n, n2, n3, false);
    }
    
    public AdjustmentEvent(final Adjustable adjustable, final int n, final int adjustmentType, final int value, final boolean isAdjusting) {
        super(adjustable, n);
        this.adjustable = adjustable;
        this.adjustmentType = adjustmentType;
        this.value = value;
        this.isAdjusting = isAdjusting;
    }
    
    public Adjustable getAdjustable() {
        return this.adjustable;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getAdjustmentType() {
        return this.adjustmentType;
    }
    
    public boolean getValueIsAdjusting() {
        return this.isAdjusting;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 601: {
                s = "ADJUSTMENT_VALUE_CHANGED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        String s2 = null;
        switch (this.adjustmentType) {
            case 1: {
                s2 = "UNIT_INCREMENT";
                break;
            }
            case 2: {
                s2 = "UNIT_DECREMENT";
                break;
            }
            case 4: {
                s2 = "BLOCK_INCREMENT";
                break;
            }
            case 3: {
                s2 = "BLOCK_DECREMENT";
                break;
            }
            case 5: {
                s2 = "TRACK";
                break;
            }
            default: {
                s2 = "unknown type";
                break;
            }
        }
        return s + ",adjType=" + s2 + ",value=" + this.value + ",isAdjusting=" + this.isAdjusting;
    }
}
