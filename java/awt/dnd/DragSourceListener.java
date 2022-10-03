package java.awt.dnd;

import java.util.EventListener;

public interface DragSourceListener extends EventListener
{
    void dragEnter(final DragSourceDragEvent p0);
    
    void dragOver(final DragSourceDragEvent p0);
    
    void dropActionChanged(final DragSourceDragEvent p0);
    
    void dragExit(final DragSourceEvent p0);
    
    void dragDropEnd(final DragSourceDropEvent p0);
}
