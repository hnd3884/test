package java.awt.dnd;

import java.awt.Point;
import java.util.EventObject;

public class DragSourceEvent extends EventObject
{
    private static final long serialVersionUID = -763287114604032641L;
    private final boolean locationSpecified;
    private final int x;
    private final int y;
    
    public DragSourceEvent(final DragSourceContext dragSourceContext) {
        super(dragSourceContext);
        this.locationSpecified = false;
        this.x = 0;
        this.y = 0;
    }
    
    public DragSourceEvent(final DragSourceContext dragSourceContext, final int x, final int y) {
        super(dragSourceContext);
        this.locationSpecified = true;
        this.x = x;
        this.y = y;
    }
    
    public DragSourceContext getDragSourceContext() {
        return (DragSourceContext)this.getSource();
    }
    
    public Point getLocation() {
        if (this.locationSpecified) {
            return new Point(this.x, this.y);
        }
        return null;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
}
