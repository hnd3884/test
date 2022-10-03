package java.awt.dnd.peer;

import java.awt.dnd.InvalidDnDOperationException;
import java.awt.Point;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.dnd.DragSourceContext;

public interface DragSourceContextPeer
{
    void startDrag(final DragSourceContext p0, final Cursor p1, final Image p2, final Point p3) throws InvalidDnDOperationException;
    
    Cursor getCursor();
    
    void setCursor(final Cursor p0) throws InvalidDnDOperationException;
    
    void transferablesFlavorsChanged();
}
