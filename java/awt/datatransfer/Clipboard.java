package java.awt.datatransfer;

import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.EventListener;
import java.io.IOException;
import java.awt.EventQueue;
import java.util.Set;
import sun.awt.EventListenerAggregate;

public class Clipboard
{
    String name;
    protected ClipboardOwner owner;
    protected Transferable contents;
    private EventListenerAggregate flavorListeners;
    private Set<DataFlavor> currentDataFlavors;
    
    public Clipboard(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public synchronized void setContents(final Transferable contents, final ClipboardOwner owner) {
        final ClipboardOwner owner2 = this.owner;
        final Transferable contents2 = this.contents;
        this.owner = owner;
        this.contents = contents;
        if (owner2 != null && owner2 != owner) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    owner2.lostOwnership(Clipboard.this, contents2);
                }
            });
        }
        this.fireFlavorsChanged();
    }
    
    public synchronized Transferable getContents(final Object o) {
        return this.contents;
    }
    
    public DataFlavor[] getAvailableDataFlavors() {
        final Transferable contents = this.getContents(null);
        if (contents == null) {
            return new DataFlavor[0];
        }
        return contents.getTransferDataFlavors();
    }
    
    public boolean isDataFlavorAvailable(final DataFlavor dataFlavor) {
        if (dataFlavor == null) {
            throw new NullPointerException("flavor");
        }
        final Transferable contents = this.getContents(null);
        return contents != null && contents.isDataFlavorSupported(dataFlavor);
    }
    
    public Object getData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (dataFlavor == null) {
            throw new NullPointerException("flavor");
        }
        final Transferable contents = this.getContents(null);
        if (contents == null) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        return contents.getTransferData(dataFlavor);
    }
    
    public synchronized void addFlavorListener(final FlavorListener flavorListener) {
        if (flavorListener == null) {
            return;
        }
        if (this.flavorListeners == null) {
            this.currentDataFlavors = this.getAvailableDataFlavorSet();
            this.flavorListeners = new EventListenerAggregate(FlavorListener.class);
        }
        this.flavorListeners.add(flavorListener);
    }
    
    public synchronized void removeFlavorListener(final FlavorListener flavorListener) {
        if (flavorListener == null || this.flavorListeners == null) {
            return;
        }
        this.flavorListeners.remove(flavorListener);
    }
    
    public synchronized FlavorListener[] getFlavorListeners() {
        return (FlavorListener[])((this.flavorListeners == null) ? new FlavorListener[0] : this.flavorListeners.getListenersCopy());
    }
    
    private void fireFlavorsChanged() {
        if (this.flavorListeners == null) {
            return;
        }
        final Set<DataFlavor> currentDataFlavors = this.currentDataFlavors;
        this.currentDataFlavors = this.getAvailableDataFlavorSet();
        if (currentDataFlavors.equals(this.currentDataFlavors)) {
            return;
        }
        final FlavorListener[] array = (FlavorListener[])this.flavorListeners.getListenersInternal();
        for (int i = 0; i < array.length; ++i) {
            EventQueue.invokeLater(new Runnable() {
                final /* synthetic */ FlavorListener val$listener = array[i];
                
                @Override
                public void run() {
                    this.val$listener.flavorsChanged(new FlavorEvent(Clipboard.this));
                }
            });
        }
    }
    
    private Set<DataFlavor> getAvailableDataFlavorSet() {
        final HashSet set = new HashSet();
        final Transferable contents = this.getContents(null);
        if (contents != null) {
            final DataFlavor[] transferDataFlavors = contents.getTransferDataFlavors();
            if (transferDataFlavors != null) {
                set.addAll(Arrays.asList(transferDataFlavors));
            }
        }
        return set;
    }
}
