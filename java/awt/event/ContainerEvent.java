package java.awt.event;

import java.awt.Container;
import java.awt.Component;

public class ContainerEvent extends ComponentEvent
{
    public static final int CONTAINER_FIRST = 300;
    public static final int CONTAINER_LAST = 301;
    public static final int COMPONENT_ADDED = 300;
    public static final int COMPONENT_REMOVED = 301;
    Component child;
    private static final long serialVersionUID = -4114942250539772041L;
    
    public ContainerEvent(final Component component, final int n, final Component child) {
        super(component, n);
        this.child = child;
    }
    
    public Container getContainer() {
        return (this.source instanceof Container) ? ((Container)this.source) : null;
    }
    
    public Component getChild() {
        return this.child;
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 300: {
                s = "COMPONENT_ADDED";
                break;
            }
            case 301: {
                s = "COMPONENT_REMOVED";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s + ",child=" + this.child.getName();
    }
}
