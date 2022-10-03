package java.awt.event;

import java.awt.Component;
import java.awt.Rectangle;

public class PaintEvent extends ComponentEvent
{
    public static final int PAINT_FIRST = 800;
    public static final int PAINT_LAST = 801;
    public static final int PAINT = 800;
    public static final int UPDATE = 801;
    Rectangle updateRect;
    private static final long serialVersionUID = 1267492026433337593L;
    
    public PaintEvent(final Component component, final int n, final Rectangle updateRect) {
        super(component, n);
        this.updateRect = updateRect;
    }
    
    public Rectangle getUpdateRect() {
        return this.updateRect;
    }
    
    public void setUpdateRect(final Rectangle updateRect) {
        this.updateRect = updateRect;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 800: {
                s = "PAINT";
                break;
            }
            case 801: {
                s = "UPDATE";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s + ",updateRect=" + ((this.updateRect != null) ? this.updateRect.toString() : "null");
    }
}
