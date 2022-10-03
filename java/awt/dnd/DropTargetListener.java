package java.awt.dnd;

import java.util.EventListener;

public interface DropTargetListener extends EventListener
{
    void dragEnter(final DropTargetDragEvent p0);
    
    void dragOver(final DropTargetDragEvent p0);
    
    void dropActionChanged(final DropTargetDragEvent p0);
    
    void dragExit(final DropTargetEvent p0);
    
    void drop(final DropTargetDropEvent p0);
}
