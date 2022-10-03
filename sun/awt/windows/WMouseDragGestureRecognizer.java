package sun.awt.windows;

import java.awt.Point;
import java.awt.event.InputEvent;
import sun.awt.dnd.SunDragSourceContextPeer;
import java.awt.event.MouseEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.Component;
import java.awt.dnd.DragSource;
import java.awt.dnd.MouseDragGestureRecognizer;

final class WMouseDragGestureRecognizer extends MouseDragGestureRecognizer
{
    private static final long serialVersionUID = -3527844310018033570L;
    protected static int motionThreshold;
    protected static final int ButtonMask = 7168;
    
    protected WMouseDragGestureRecognizer(final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        super(dragSource, component, n, dragGestureListener);
    }
    
    protected WMouseDragGestureRecognizer(final DragSource dragSource, final Component component, final int n) {
        this(dragSource, component, n, null);
    }
    
    protected WMouseDragGestureRecognizer(final DragSource dragSource, final Component component) {
        this(dragSource, component, 0);
    }
    
    protected WMouseDragGestureRecognizer(final DragSource dragSource) {
        this(dragSource, null);
    }
    
    protected int mapDragOperationFromModifiers(final MouseEvent mouseEvent) {
        final int modifiersEx = mouseEvent.getModifiersEx();
        final int n = modifiersEx & 0x1C00;
        if (n != 1024 && n != 2048 && n != 4096) {
            return 0;
        }
        return SunDragSourceContextPeer.convertModifiersToDropAction(modifiersEx, this.getSourceActions());
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        this.events.clear();
        if (this.mapDragOperationFromModifiers(mouseEvent) != 0) {
            try {
                WMouseDragGestureRecognizer.motionThreshold = DragSource.getDragThreshold();
            }
            catch (final Exception ex) {
                WMouseDragGestureRecognizer.motionThreshold = 5;
            }
            this.appendEvent(mouseEvent);
        }
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
        this.events.clear();
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
        this.events.clear();
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
        if (!this.events.isEmpty() && this.mapDragOperationFromModifiers(mouseEvent) == 0) {
            this.events.clear();
        }
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
        if (!this.events.isEmpty()) {
            final int mapDragOperationFromModifiers = this.mapDragOperationFromModifiers(mouseEvent);
            if (mapDragOperationFromModifiers == 0) {
                return;
            }
            final Point point = this.events.get(0).getPoint();
            final Point point2 = mouseEvent.getPoint();
            final int abs = Math.abs(point.x - point2.x);
            final int abs2 = Math.abs(point.y - point2.y);
            if (abs > WMouseDragGestureRecognizer.motionThreshold || abs2 > WMouseDragGestureRecognizer.motionThreshold) {
                this.fireDragGestureRecognized(mapDragOperationFromModifiers, ((MouseEvent)this.getTriggerEvent()).getPoint());
            }
            else {
                this.appendEvent(mouseEvent);
            }
        }
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
    }
}
