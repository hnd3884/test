package sun.swing;

import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureListener;
import java.awt.Component;
import java.awt.dnd.DragSource;
import java.awt.Cursor;
import javax.swing.JComponent;

public interface LightweightContent
{
    JComponent getComponent();
    
    void paintLock();
    
    void paintUnlock();
    
    default void imageBufferReset(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.imageBufferReset(array, n, n2, n3, n4, n5);
    }
    
    default void imageBufferReset(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.imageBufferReset(array, n, n2, n3, n4, n5, 1);
    }
    
    void imageReshaped(final int p0, final int p1, final int p2, final int p3);
    
    void imageUpdated(final int p0, final int p1, final int p2, final int p3);
    
    void focusGrabbed();
    
    void focusUngrabbed();
    
    void preferredSizeChanged(final int p0, final int p1);
    
    void maximumSizeChanged(final int p0, final int p1);
    
    void minimumSizeChanged(final int p0, final int p1);
    
    default void setCursor(final Cursor cursor) {
    }
    
    default <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> clazz, final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        return null;
    }
    
    default DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent dragGestureEvent) throws InvalidDnDOperationException {
        return null;
    }
    
    default void addDropTarget(final DropTarget dropTarget) {
    }
    
    default void removeDropTarget(final DropTarget dropTarget) {
    }
}
