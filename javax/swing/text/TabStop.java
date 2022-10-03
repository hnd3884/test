package javax.swing.text;

import java.io.Serializable;

public class TabStop implements Serializable
{
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_DECIMAL = 4;
    public static final int ALIGN_BAR = 5;
    public static final int LEAD_NONE = 0;
    public static final int LEAD_DOTS = 1;
    public static final int LEAD_HYPHENS = 2;
    public static final int LEAD_UNDERLINE = 3;
    public static final int LEAD_THICKLINE = 4;
    public static final int LEAD_EQUALS = 5;
    private int alignment;
    private float position;
    private int leader;
    
    public TabStop(final float n) {
        this(n, 0, 0);
    }
    
    public TabStop(final float position, final int alignment, final int leader) {
        this.alignment = alignment;
        this.leader = leader;
        this.position = position;
    }
    
    public float getPosition() {
        return this.position;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public int getLeader() {
        return this.leader;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TabStop) {
            final TabStop tabStop = (TabStop)o;
            return this.alignment == tabStop.alignment && this.leader == tabStop.leader && this.position == tabStop.position;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.alignment ^ this.leader ^ Math.round(this.position);
    }
    
    @Override
    public String toString() {
        String s = null;
        switch (this.alignment) {
            default: {
                s = "";
                break;
            }
            case 1: {
                s = "right ";
                break;
            }
            case 2: {
                s = "center ";
                break;
            }
            case 4: {
                s = "decimal ";
                break;
            }
            case 5: {
                s = "bar ";
                break;
            }
        }
        String s2 = s + "tab @" + String.valueOf(this.position);
        if (this.leader != 0) {
            s2 += " (w/leaders)";
        }
        return s2;
    }
}
