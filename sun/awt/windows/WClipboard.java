package sun.awt.windows;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Iterator;
import java.util.SortedMap;
import java.io.IOException;
import java.io.NotSerializableException;
import java.awt.datatransfer.DataFlavor;
import sun.awt.datatransfer.DataTransferer;
import java.awt.datatransfer.Transferable;
import sun.awt.datatransfer.SunClipboard;

final class WClipboard extends SunClipboard
{
    private boolean isClipboardViewerRegistered;
    
    WClipboard() {
        super("System");
    }
    
    @Override
    public long getID() {
        return 0L;
    }
    
    @Override
    protected void setContentsNative(final Transferable transferable) {
        final SortedMap<Long, DataFlavor> formatsForTransferable = DataTransferer.getInstance().getFormatsForTransferable(transferable, SunClipboard.getDefaultFlavorTable());
        this.openClipboard(this);
        try {
            for (final Long n : formatsForTransferable.keySet()) {
                final DataFlavor dataFlavor = formatsForTransferable.get(n);
                try {
                    this.publishClipboardData(n, DataTransferer.getInstance().translateTransferable(transferable, dataFlavor, n));
                }
                catch (final IOException ex) {
                    if (dataFlavor.isMimeTypeEqual("application/x-java-jvm-local-objectref") && ex instanceof NotSerializableException) {
                        continue;
                    }
                    ex.printStackTrace();
                }
            }
        }
        finally {
            this.closeClipboard();
        }
    }
    
    private void lostSelectionOwnershipImpl() {
        this.lostOwnershipImpl();
    }
    
    @Override
    protected void clearNativeContext() {
    }
    
    @Override
    public native void openClipboard(final SunClipboard p0) throws IllegalStateException;
    
    @Override
    public native void closeClipboard();
    
    private native void publishClipboardData(final long p0, final byte[] p1);
    
    private static native void init();
    
    @Override
    protected native long[] getClipboardFormats();
    
    @Override
    protected native byte[] getClipboardData(final long p0) throws IOException;
    
    @Override
    protected void registerClipboardViewerChecked() {
        if (!this.isClipboardViewerRegistered) {
            this.registerClipboardViewer();
            this.isClipboardViewerRegistered = true;
        }
    }
    
    private native void registerClipboardViewer();
    
    @Override
    protected void unregisterClipboardViewerChecked() {
    }
    
    private void handleContentsChanged() {
        if (!this.areFlavorListenersRegistered()) {
            return;
        }
        long[] clipboardFormats = null;
        try {
            this.openClipboard(null);
            clipboardFormats = this.getClipboardFormats();
        }
        catch (final IllegalStateException ex) {}
        finally {
            this.closeClipboard();
        }
        this.checkChange(clipboardFormats);
    }
    
    @Override
    protected Transferable createLocaleTransferable(final long[] array) throws IOException {
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == 16L) {
                b = true;
                break;
            }
        }
        if (!b) {
            return null;
        }
        byte[] clipboardData;
        try {
            clipboardData = this.getClipboardData(16L);
        }
        catch (final IOException ex) {
            return null;
        }
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { DataTransferer.javaTextEncodingFlavor };
            }
            
            @Override
            public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
                return dataFlavor.equals(DataTransferer.javaTextEncodingFlavor);
            }
            
            @Override
            public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException {
                if (this.isDataFlavorSupported(dataFlavor)) {
                    return clipboardData;
                }
                throw new UnsupportedFlavorException(dataFlavor);
            }
        };
    }
    
    static {
        init();
    }
}
