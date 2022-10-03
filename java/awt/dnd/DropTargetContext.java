package java.awt.dnd;

import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import sun.awt.datatransfer.TransferableProxy;
import java.util.Arrays;
import java.util.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.Serializable;

public class DropTargetContext implements Serializable
{
    private static final long serialVersionUID = -634158968993743371L;
    private DropTarget dropTarget;
    private transient DropTargetContextPeer dropTargetContextPeer;
    private transient Transferable transferable;
    
    DropTargetContext(final DropTarget dropTarget) {
        this.dropTarget = dropTarget;
    }
    
    public DropTarget getDropTarget() {
        return this.dropTarget;
    }
    
    public Component getComponent() {
        return this.dropTarget.getComponent();
    }
    
    public void addNotify(final DropTargetContextPeer dropTargetContextPeer) {
        this.dropTargetContextPeer = dropTargetContextPeer;
    }
    
    public void removeNotify() {
        this.dropTargetContextPeer = null;
        this.transferable = null;
    }
    
    protected void setTargetActions(final int targetActions) {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer != null) {
            synchronized (dropTargetContextPeer) {
                dropTargetContextPeer.setTargetActions(targetActions);
                this.getDropTarget().doSetDefaultActions(targetActions);
            }
        }
        else {
            this.getDropTarget().doSetDefaultActions(targetActions);
        }
    }
    
    protected int getTargetActions() {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        return (dropTargetContextPeer != null) ? dropTargetContextPeer.getTargetActions() : this.dropTarget.getDefaultActions();
    }
    
    public void dropComplete(final boolean b) throws InvalidDnDOperationException {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer != null) {
            dropTargetContextPeer.dropComplete(b);
        }
    }
    
    protected void acceptDrag(final int n) {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer != null) {
            dropTargetContextPeer.acceptDrag(n);
        }
    }
    
    protected void rejectDrag() {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer != null) {
            dropTargetContextPeer.rejectDrag();
        }
    }
    
    protected void acceptDrop(final int n) {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer != null) {
            dropTargetContextPeer.acceptDrop(n);
        }
    }
    
    protected void rejectDrop() {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer != null) {
            dropTargetContextPeer.rejectDrop();
        }
    }
    
    protected DataFlavor[] getCurrentDataFlavors() {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        return (dropTargetContextPeer != null) ? dropTargetContextPeer.getTransferDataFlavors() : new DataFlavor[0];
    }
    
    protected List<DataFlavor> getCurrentDataFlavorsAsList() {
        return Arrays.asList(this.getCurrentDataFlavors());
    }
    
    protected boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        return this.getCurrentDataFlavorsAsList().contains(dataFlavor);
    }
    
    protected Transferable getTransferable() throws InvalidDnDOperationException {
        final DropTargetContextPeer dropTargetContextPeer = this.getDropTargetContextPeer();
        if (dropTargetContextPeer == null) {
            throw new InvalidDnDOperationException();
        }
        if (this.transferable == null) {
            final Transferable transferable = dropTargetContextPeer.getTransferable();
            final boolean transferableJVMLocal = dropTargetContextPeer.isTransferableJVMLocal();
            synchronized (this) {
                if (this.transferable == null) {
                    this.transferable = this.createTransferableProxy(transferable, transferableJVMLocal);
                }
            }
        }
        return this.transferable;
    }
    
    DropTargetContextPeer getDropTargetContextPeer() {
        return this.dropTargetContextPeer;
    }
    
    protected Transferable createTransferableProxy(final Transferable transferable, final boolean b) {
        return new TransferableProxy(transferable, b);
    }
    
    protected class TransferableProxy implements Transferable
    {
        protected Transferable transferable;
        protected boolean isLocal;
        private sun.awt.datatransfer.TransferableProxy proxy;
        
        TransferableProxy(final Transferable transferable, final boolean isLocal) {
            this.proxy = new sun.awt.datatransfer.TransferableProxy(transferable, isLocal);
            this.transferable = transferable;
            this.isLocal = isLocal;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return this.proxy.getTransferDataFlavors();
        }
        
        @Override
        public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
            return this.proxy.isDataFlavorSupported(dataFlavor);
        }
        
        @Override
        public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
            return this.proxy.getTransferData(dataFlavor);
        }
    }
}
