package java.awt.dnd;

import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

public abstract class MouseDragGestureRecognizer extends DragGestureRecognizer implements MouseListener, MouseMotionListener
{
    private static final long serialVersionUID = 6220099344182281120L;
    
    protected MouseDragGestureRecognizer(final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        super(dragSource, component, n, dragGestureListener);
    }
    
    protected MouseDragGestureRecognizer(final DragSource dragSource, final Component component, final int n) {
        this(dragSource, component, n, null);
    }
    
    protected MouseDragGestureRecognizer(final DragSource dragSource, final Component component) {
        this(dragSource, component, 0);
    }
    
    protected MouseDragGestureRecognizer(final DragSource dragSource) {
        this(dragSource, null);
    }
    
    @Override
    protected void registerListeners() {
        this.component.addMouseListener(this);
        this.component.addMouseMotionListener(this);
    }
    
    @Override
    protected void unregisterListeners() {
        this.component.removeMouseListener(this);
        this.component.removeMouseMotionListener(this);
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
    }
}
