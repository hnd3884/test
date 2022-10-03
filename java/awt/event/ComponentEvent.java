package java.awt.event;

import java.awt.Rectangle;
import java.awt.Component;
import java.awt.AWTEvent;

public class ComponentEvent extends AWTEvent
{
    public static final int COMPONENT_FIRST = 100;
    public static final int COMPONENT_LAST = 103;
    public static final int COMPONENT_MOVED = 100;
    public static final int COMPONENT_RESIZED = 101;
    public static final int COMPONENT_SHOWN = 102;
    public static final int COMPONENT_HIDDEN = 103;
    private static final long serialVersionUID = 8101406823902992965L;
    
    public ComponentEvent(final Component component, final int n) {
        super(component, n);
    }
    
    public Component getComponent() {
        return (this.source instanceof Component) ? ((Component)this.source) : null;
    }
    
    @Override
    public String paramString() {
        final Rectangle rectangle = (this.source != null) ? ((Component)this.source).getBounds() : null;
        String s = null;
        switch (this.id) {
            case 102: {
                s = "COMPONENT_SHOWN";
                break;
            }
            case 103: {
                s = "COMPONENT_HIDDEN";
                break;
            }
            case 100: {
                s = "COMPONENT_MOVED (" + rectangle.x + "," + rectangle.y + " " + rectangle.width + "x" + rectangle.height + ")";
                break;
            }
            case 101: {
                s = "COMPONENT_RESIZED (" + rectangle.x + "," + rectangle.y + " " + rectangle.width + "x" + rectangle.height + ")";
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
