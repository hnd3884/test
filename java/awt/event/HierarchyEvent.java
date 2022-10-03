package java.awt.event;

import java.awt.Container;
import java.awt.Component;
import java.awt.AWTEvent;

public class HierarchyEvent extends AWTEvent
{
    private static final long serialVersionUID = -5337576970038043990L;
    public static final int HIERARCHY_FIRST = 1400;
    public static final int HIERARCHY_CHANGED = 1400;
    public static final int ANCESTOR_MOVED = 1401;
    public static final int ANCESTOR_RESIZED = 1402;
    public static final int HIERARCHY_LAST = 1402;
    public static final int PARENT_CHANGED = 1;
    public static final int DISPLAYABILITY_CHANGED = 2;
    public static final int SHOWING_CHANGED = 4;
    Component changed;
    Container changedParent;
    long changeFlags;
    
    public HierarchyEvent(final Component component, final int n, final Component changed, final Container changedParent) {
        super(component, n);
        this.changed = changed;
        this.changedParent = changedParent;
    }
    
    public HierarchyEvent(final Component component, final int n, final Component changed, final Container changedParent, final long changeFlags) {
        super(component, n);
        this.changed = changed;
        this.changedParent = changedParent;
        this.changeFlags = changeFlags;
    }
    
    public Component getComponent() {
        return (this.source instanceof Component) ? ((Component)this.source) : null;
    }
    
    public Component getChanged() {
        return this.changed;
    }
    
    public Container getChangedParent() {
        return this.changedParent;
    }
    
    public long getChangeFlags() {
        return this.changeFlags;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 1401: {
                s = "ANCESTOR_MOVED (" + this.changed + "," + this.changedParent + ")";
                break;
            }
            case 1402: {
                s = "ANCESTOR_RESIZED (" + this.changed + "," + this.changedParent + ")";
                break;
            }
            case 1400: {
                String s2 = "HIERARCHY_CHANGED (";
                int n = 1;
                if ((this.changeFlags & 0x1L) != 0x0L) {
                    n = 0;
                    s2 += "PARENT_CHANGED";
                }
                if ((this.changeFlags & 0x2L) != 0x0L) {
                    if (n != 0) {
                        n = 0;
                    }
                    else {
                        s2 += ",";
                    }
                    s2 += "DISPLAYABILITY_CHANGED";
                }
                if ((this.changeFlags & 0x4L) != 0x0L) {
                    if (n != 0) {
                        n = 0;
                    }
                    else {
                        s2 += ",";
                    }
                    s2 += "SHOWING_CHANGED";
                }
                if (n == 0) {
                    s2 += ",";
                }
                s = s2 + this.changed + "," + this.changedParent + ")";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s;
    }
}
