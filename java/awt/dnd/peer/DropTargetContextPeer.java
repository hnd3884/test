package java.awt.dnd.peer;

import java.awt.dnd.InvalidDnDOperationException;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;

public interface DropTargetContextPeer
{
    void setTargetActions(final int p0);
    
    int getTargetActions();
    
    DropTarget getDropTarget();
    
    DataFlavor[] getTransferDataFlavors();
    
    Transferable getTransferable() throws InvalidDnDOperationException;
    
    boolean isTransferableJVMLocal();
    
    void acceptDrag(final int p0);
    
    void rejectDrag();
    
    void acceptDrop(final int p0);
    
    void rejectDrop();
    
    void dropComplete(final boolean p0);
}
