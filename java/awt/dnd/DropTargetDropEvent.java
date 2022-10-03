package java.awt.dnd;

import java.awt.datatransfer.Transferable;
import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.Point;

public class DropTargetDropEvent extends DropTargetEvent
{
    private static final long serialVersionUID = -1721911170440459322L;
    private static final Point zero;
    private Point location;
    private int actions;
    private int dropAction;
    private boolean isLocalTx;
    
    public DropTargetDropEvent(final DropTargetContext dropTargetContext, final Point location, final int dropAction, final int actions) {
        super(dropTargetContext);
        this.location = DropTargetDropEvent.zero;
        this.actions = 0;
        this.dropAction = 0;
        this.isLocalTx = false;
        if (location == null) {
            throw new NullPointerException("cursorLocn");
        }
        if (dropAction != 0 && dropAction != 1 && dropAction != 2 && dropAction != 1073741824) {
            throw new IllegalArgumentException("dropAction = " + dropAction);
        }
        if ((actions & 0xBFFFFFFC) != 0x0) {
            throw new IllegalArgumentException("srcActions");
        }
        this.location = location;
        this.actions = actions;
        this.dropAction = dropAction;
    }
    
    public DropTargetDropEvent(final DropTargetContext dropTargetContext, final Point point, final int n, final int n2, final boolean isLocalTx) {
        this(dropTargetContext, point, n, n2);
        this.isLocalTx = isLocalTx;
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
    
    public void acceptDrop(final int n) {
        this.getDropTargetContext().acceptDrop(n);
    }
    
    public void rejectDrop() {
        this.getDropTargetContext().rejectDrop();
    }
    
    public void dropComplete(final boolean b) {
        this.getDropTargetContext().dropComplete(b);
    }
    
    public boolean isLocalTransfer() {
        return this.isLocalTx;
    }
    
    static {
        zero = new Point(0, 0);
    }
}
