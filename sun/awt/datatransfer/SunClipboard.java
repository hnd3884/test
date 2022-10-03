package sun.awt.datatransfer;

import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.FlavorTable;
import java.util.Iterator;
import java.util.Arrays;
import java.awt.datatransfer.FlavorEvent;
import java.util.EventListener;
import sun.awt.EventListenerAggregate;
import java.awt.datatransfer.FlavorListener;
import java.util.Set;
import java.awt.AWTEvent;
import sun.awt.SunToolkit;
import sun.awt.PeerEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.awt.EventQueue;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import sun.awt.AppContext;
import java.beans.PropertyChangeListener;
import java.awt.datatransfer.Clipboard;

public abstract class SunClipboard extends Clipboard implements PropertyChangeListener
{
    private AppContext contentsContext;
    private final Object CLIPBOARD_FLAVOR_LISTENER_KEY;
    private volatile int numberOfFlavorListeners;
    private volatile long[] currentFormats;
    
    public SunClipboard(final String s) {
        super(s);
        this.contentsContext = null;
        this.numberOfFlavorListeners = 0;
        this.CLIPBOARD_FLAVOR_LISTENER_KEY = new StringBuffer(s + "_CLIPBOARD_FLAVOR_LISTENER_KEY");
    }
    
    @Override
    public synchronized void setContents(final Transferable contentsNative, final ClipboardOwner owner) {
        if (contentsNative == null) {
            throw new NullPointerException("contents");
        }
        this.initContext();
        final ClipboardOwner owner2 = this.owner;
        final Transferable contents = this.contents;
        try {
            this.owner = owner;
            this.contents = new TransferableProxy(contentsNative, true);
            this.setContentsNative(contentsNative);
        }
        finally {
            if (owner2 != null && owner2 != owner) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        owner2.lostOwnership(SunClipboard.this, contents);
                    }
                });
            }
        }
    }
    
    private synchronized void initContext() {
        final AppContext appContext = AppContext.getAppContext();
        if (this.contentsContext != appContext) {
            synchronized (appContext) {
                if (appContext.isDisposed()) {
                    throw new IllegalStateException("Can't set contents from disposed AppContext");
                }
                appContext.addPropertyChangeListener("disposed", this);
            }
            if (this.contentsContext != null) {
                this.contentsContext.removePropertyChangeListener("disposed", this);
            }
            this.contentsContext = appContext;
        }
    }
    
    @Override
    public synchronized Transferable getContents(final Object o) {
        if (this.contents != null) {
            return this.contents;
        }
        return new ClipboardTransferable(this);
    }
    
    protected synchronized Transferable getContextContents() {
        return (AppContext.getAppContext() == this.contentsContext) ? this.contents : null;
    }
    
    @Override
    public DataFlavor[] getAvailableDataFlavors() {
        final Transferable contextContents = this.getContextContents();
        if (contextContents != null) {
            return contextContents.getTransferDataFlavors();
        }
        return DataTransferer.getInstance().getFlavorsForFormatsAsArray(this.getClipboardFormatsOpenClose(), getDefaultFlavorTable());
    }
    
    @Override
    public boolean isDataFlavorAvailable(final DataFlavor dataFlavor) {
        if (dataFlavor == null) {
            throw new NullPointerException("flavor");
        }
        final Transferable contextContents = this.getContextContents();
        if (contextContents != null) {
            return contextContents.isDataFlavorSupported(dataFlavor);
        }
        return formatArrayAsDataFlavorSet(this.getClipboardFormatsOpenClose()).contains(dataFlavor);
    }
    
    @Override
    public Object getData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (dataFlavor == null) {
            throw new NullPointerException("flavor");
        }
        final Transferable contextContents = this.getContextContents();
        if (contextContents != null) {
            return contextContents.getTransferData(dataFlavor);
        }
        long longValue = 0L;
        byte[] clipboardData = null;
        Transferable localeTransferable = null;
        try {
            this.openClipboard(null);
            final long[] clipboardFormats = this.getClipboardFormats();
            final Long n = DataTransferer.getInstance().getFlavorsForFormats(clipboardFormats, getDefaultFlavorTable()).get(dataFlavor);
            if (n == null) {
                throw new UnsupportedFlavorException(dataFlavor);
            }
            longValue = n;
            clipboardData = this.getClipboardData(longValue);
            if (DataTransferer.getInstance().isLocaleDependentTextFormat(longValue)) {
                localeTransferable = this.createLocaleTransferable(clipboardFormats);
            }
        }
        finally {
            this.closeClipboard();
        }
        return DataTransferer.getInstance().translateBytes(clipboardData, dataFlavor, longValue, localeTransferable);
    }
    
    protected Transferable createLocaleTransferable(final long[] array) throws IOException {
        return null;
    }
    
    public void openClipboard(final SunClipboard sunClipboard) {
    }
    
    public void closeClipboard() {
    }
    
    public abstract long getID();
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if ("disposed".equals(propertyChangeEvent.getPropertyName()) && Boolean.TRUE.equals(propertyChangeEvent.getNewValue())) {
            this.lostOwnershipLater((AppContext)propertyChangeEvent.getSource());
        }
    }
    
    protected void lostOwnershipImpl() {
        this.lostOwnershipLater(null);
    }
    
    protected void lostOwnershipLater(final AppContext appContext) {
        final AppContext contentsContext = this.contentsContext;
        if (contentsContext == null) {
            return;
        }
        SunToolkit.postEvent(contentsContext, new PeerEvent(this, () -> this.lostOwnershipNow(appContext2), 1L));
    }
    
    protected void lostOwnershipNow(final AppContext appContext) {
        ClipboardOwner owner = null;
        Transferable contents = null;
        synchronized (this) {
            final AppContext contentsContext = this.contentsContext;
            if (contentsContext == null) {
                return;
            }
            if (appContext != null && contentsContext != appContext) {
                return;
            }
            owner = this.owner;
            contents = this.contents;
            this.contentsContext = null;
            this.owner = null;
            this.contents = null;
            this.clearNativeContext();
            contentsContext.removePropertyChangeListener("disposed", this);
        }
        if (owner != null) {
            owner.lostOwnership(this, contents);
        }
    }
    
    protected abstract void clearNativeContext();
    
    protected abstract void setContentsNative(final Transferable p0);
    
    protected long[] getClipboardFormatsOpenClose() {
        try {
            this.openClipboard(null);
            return this.getClipboardFormats();
        }
        finally {
            this.closeClipboard();
        }
    }
    
    protected abstract long[] getClipboardFormats();
    
    protected abstract byte[] getClipboardData(final long p0) throws IOException;
    
    private static Set formatArrayAsDataFlavorSet(final long[] array) {
        return (array == null) ? null : DataTransferer.getInstance().getFlavorsForFormatsAsSet(array, getDefaultFlavorTable());
    }
    
    @Override
    public synchronized void addFlavorListener(final FlavorListener flavorListener) {
        if (flavorListener == null) {
            return;
        }
        final AppContext appContext = AppContext.getAppContext();
        EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)appContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
        if (eventListenerAggregate == null) {
            eventListenerAggregate = new EventListenerAggregate(FlavorListener.class);
            appContext.put(this.CLIPBOARD_FLAVOR_LISTENER_KEY, eventListenerAggregate);
        }
        eventListenerAggregate.add(flavorListener);
        if (this.numberOfFlavorListeners++ == 0) {
            long[] clipboardFormats = null;
            try {
                this.openClipboard(null);
                clipboardFormats = this.getClipboardFormats();
            }
            catch (final IllegalStateException ex) {}
            finally {
                this.closeClipboard();
            }
            this.currentFormats = clipboardFormats;
            this.registerClipboardViewerChecked();
        }
    }
    
    @Override
    public synchronized void removeFlavorListener(final FlavorListener flavorListener) {
        if (flavorListener == null) {
            return;
        }
        final EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)AppContext.getAppContext().get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
        if (eventListenerAggregate == null) {
            return;
        }
        if (eventListenerAggregate.remove(flavorListener) && --this.numberOfFlavorListeners == 0) {
            this.unregisterClipboardViewerChecked();
            this.currentFormats = null;
        }
    }
    
    @Override
    public synchronized FlavorListener[] getFlavorListeners() {
        final EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)AppContext.getAppContext().get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
        return (FlavorListener[])((eventListenerAggregate == null) ? new FlavorListener[0] : eventListenerAggregate.getListenersCopy());
    }
    
    public boolean areFlavorListenersRegistered() {
        return this.numberOfFlavorListeners > 0;
    }
    
    protected abstract void registerClipboardViewerChecked();
    
    protected abstract void unregisterClipboardViewerChecked();
    
    protected final void checkChange(final long[] currentFormats) {
        if (Arrays.equals(currentFormats, this.currentFormats)) {
            return;
        }
        this.currentFormats = currentFormats;
        for (final AppContext appContext : AppContext.getAppContexts()) {
            if (appContext != null) {
                if (appContext.isDisposed()) {
                    continue;
                }
                final EventListenerAggregate eventListenerAggregate = (EventListenerAggregate)appContext.get(this.CLIPBOARD_FLAVOR_LISTENER_KEY);
                if (eventListenerAggregate == null) {
                    continue;
                }
                final FlavorListener[] array = (FlavorListener[])eventListenerAggregate.getListenersInternal();
                for (int i = 0; i < array.length; ++i) {
                    class SunFlavorChangeNotifier implements Runnable
                    {
                        private final FlavorListener flavorListener = array[i];
                        
                        @Override
                        public void run() {
                            if (this.flavorListener != null) {
                                this.flavorListener.flavorsChanged(new FlavorEvent(SunClipboard.this));
                            }
                        }
                    }
                    SunToolkit.postEvent(appContext, new PeerEvent(this, new SunFlavorChangeNotifier(), 1L));
                }
            }
        }
    }
    
    public static FlavorTable getDefaultFlavorTable() {
        return (FlavorTable)SystemFlavorMap.getDefaultFlavorMap();
    }
}
