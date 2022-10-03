package java.awt.event;

import sun.awt.AppContext;
import sun.awt.SunToolkit;
import java.awt.Component;

public class FocusEvent extends ComponentEvent
{
    public static final int FOCUS_FIRST = 1004;
    public static final int FOCUS_LAST = 1005;
    public static final int FOCUS_GAINED = 1004;
    public static final int FOCUS_LOST = 1005;
    boolean temporary;
    transient Component opposite;
    private static final long serialVersionUID = 523753786457416396L;
    
    public FocusEvent(final Component component, final int n, final boolean temporary, final Component opposite) {
        super(component, n);
        this.temporary = temporary;
        this.opposite = opposite;
    }
    
    public FocusEvent(final Component component, final int n, final boolean b) {
        this(component, n, b, null);
    }
    
    public FocusEvent(final Component component, final int n) {
        this(component, n, false);
    }
    
    public boolean isTemporary() {
        return this.temporary;
    }
    
    public Component getOppositeComponent() {
        if (this.opposite == null) {
            return null;
        }
        return (SunToolkit.targetToAppContext(this.opposite) == AppContext.getAppContext()) ? this.opposite : null;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 1004: {
                s = "FOCUS_GAINED";
                break;
            }
            case 1005: {
                s = "FOCUS_LOST";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s + (this.temporary ? ",temporary" : ",permanent") + ",opposite=" + this.getOppositeComponent();
    }
}
