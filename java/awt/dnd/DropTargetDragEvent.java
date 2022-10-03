package java.awt.dnd;

import java.awt.datatransfer.Transferable;
import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.Point;

public class DropTargetDragEvent extends DropTargetEvent
{
    private static final long serialVersionUID = -8422265619058953682L;
    private Point location;
    private int actions;
    private int dropAction;
    
    public DropTargetDragEvent(final DropTargetContext dropTargetContext, final Point location, final int dropAction, final int actions) {
        super(dropTargetContext);
        if (location == null) {
            throw new NullPointerException("cursorLocn");
        }
        if (dropAction != 0 && dropAction != 1 && dropAction != 2 && dropAction != 1073741824) {
            throw new IllegalArgumentException("dropAction" + dropAction);
        }
        if ((actions & 0xBFFFFFFC) != 0x0) {
            throw new IllegalArgumentException("srcActions");
        }
        this.location = location;
        this.actions = actions;
        this.dropAction = dropAction;
    }
    
    public Point getLocation() {
        return this.location;
    }
    
    public DataFlavor[] getCurrentDataFlavors() {
        return this.getDropTargetContext().getCurrentDataFlavors();
    }
    
    public List<DataFlavor> getCurrentDataFlavorsAsList() {
        return this.getDropTargetContext().getCurrentDataFlavorsAsList();
    }
    
    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        return this.getDropTargetContext().isDataFlavorSupported(dataFlavor);
    }
    
    public int getSourceActions() {
        return this.actions;
    }
    
    public int getDropAction() {
        return this.dropAction;
    }
    
    public Transferable getTransferable() {
        return this.getDropTargetContext().getTransferable();
    }
    
    public void acceptDrag(final int n) {
        this.getDropTargetContext().acceptDrag(n);
    }
    
    public void rejectDrag() {
        this.getDropTargetContext().rejectDrag();
    }
}
