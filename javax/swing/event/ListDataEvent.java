package javax.swing.event;

import java.util.EventObject;

public class ListDataEvent extends EventObject
{
    public static final int CONTENTS_CHANGED = 0;
    public static final int INTERVAL_ADDED = 1;
    public static final int INTERVAL_REMOVED = 2;
    private int type;
    private int index0;
    private int index1;
    
    public int getType() {
        return this.type;
    }
    
    public int getIndex0() {
        return this.index0;
    }
    
    public int getIndex1() {
        return this.index1;
    }
    
    public ListDataEvent(final Object o, final int type, final int n, final int n2) {
        super(o);
        this.type = type;
        this.index0 = Math.min(n, n2);
        this.index1 = Math.max(n, n2);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[type=" + this.type + ",index0=" + this.index0 + ",index1=" + this.index1 + "]";
    }
}
