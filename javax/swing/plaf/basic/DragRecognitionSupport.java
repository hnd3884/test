package javax.swing.plaf.basic;

import java.awt.event.InputEvent;
import java.awt.dnd.DragSource;
import sun.awt.dnd.SunDragSourceContextPeer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import sun.awt.AppContext;
import javax.swing.JComponent;
import java.awt.event.MouseEvent;

class DragRecognitionSupport
{
    private int motionThreshold;
    private MouseEvent dndArmedEvent;
    private JComponent component;
    
    private static DragRecognitionSupport getDragRecognitionSupport() {
        DragRecognitionSupport dragRecognitionSupport = (DragRecognitionSupport)AppContext.getAppContext().get(DragRecognitionSupport.class);
        if (dragRecognitionSupport == null) {
            dragRecognitionSupport = new DragRecognitionSupport();
            AppContext.getAppContext().put(DragRecognitionSupport.class, dragRecognitionSupport);
        }
        return dragRecognitionSupport;
    }
    
    public static boolean mousePressed(final MouseEvent mouseEvent) {
        return getDragRecognitionSupport().mousePressedImpl(mouseEvent);
    }
    
    public static MouseEvent mouseReleased(final MouseEvent mouseEvent) {
        return getDragRecognitionSupport().mouseReleasedImpl(mouseEvent);
    }
    
    public static boolean mouseDragged(final MouseEvent mouseEvent, final BeforeDrag beforeDrag) {
        return getDragRecognitionSupport().mouseDraggedImpl(mouseEvent, beforeDrag);
    }
    
    private void clearState() {
        this.dndArmedEvent = null;
        this.component = null;
    }
    
    private int mapDragOperationFromModifiers(final MouseEvent mouseEvent, final TransferHandler transferHandler) {
        if (transferHandler == null || !SwingUtilities.isLeftMouseButton(mouseEvent)) {
            return 0;
        }
        return SunDragSourceContextPeer.convertModifiersToDropAction(mouseEvent.getModifiersEx(), transferHandler.getSourceActions(this.component));
    }
    
    private boolean mousePressedImpl(final MouseEvent dndArmedEvent) {
        this.component = (JComponent)dndArmedEvent.getSource();
        if (this.mapDragOperationFromModifiers(dndArmedEvent, this.component.getTransferHandler()) != 0) {
            this.motionThreshold = DragSource.getDragThreshold();
            this.dndArmedEvent = dndArmedEvent;
            return true;
        }
        this.clearState();
        return false;
    }
    
    private MouseEvent mouseReleasedImpl(final MouseEvent mouseEvent) {
        if (this.dndArmedEvent == null) {
            return null;
        }
        MouseEvent dndArmedEvent = null;
        if (mouseEvent.getSource() == this.component) {
            dndArmedEvent = this.dndArmedEvent;
        }
        this.clearState();
        return dndArmedEvent;
    }
    
    private boolean mouseDraggedImpl(final MouseEvent mouseEvent, final BeforeDrag beforeDrag) {
        if (this.dndArmedEvent == null) {
            return false;
        }
        if (mouseEvent.getSource() != this.component) {
            this.clearState();
            return false;
        }
        final int abs = Math.abs(mouseEvent.getX() - this.dndArmedEvent.getX());
        final int abs2 = Math.abs(mouseEvent.getY() - this.dndArmedEvent.getY());
        if (abs > this.motionThreshold || abs2 > this.motionThreshold) {
            final TransferHandler transferHandler = this.component.getTransferHandler();
            final int mapDragOperationFromModifiers = this.mapDragOperationFromModifiers(mouseEvent, transferHandler);
            if (mapDragOperationFromModifiers != 0) {
                if (beforeDrag != null) {
                    beforeDrag.dragStarting(this.dndArmedEvent);
                }
                transferHandler.exportAsDrag(this.component, this.dndArmedEvent, mapDragOperationFromModifiers);
                this.clearState();
            }
        }
        return true;
    }
    
    public interface BeforeDrag
    {
        void dragStarting(final MouseEvent p0);
    }
}
