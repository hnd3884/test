package sun.awt.datatransfer;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.awt.datatransfer.DataFlavor;
import java.util.HashMap;
import java.awt.datatransfer.Transferable;

public class ClipboardTransferable implements Transferable
{
    private final HashMap flavorsToData;
    private DataFlavor[] flavors;
    
    public ClipboardTransferable(final SunClipboard sunClipboard) {
        this.flavorsToData = new HashMap();
        this.flavors = new DataFlavor[0];
        sunClipboard.openClipboard(null);
        try {
            final long[] clipboardFormats = sunClipboard.getClipboardFormats();
            if (clipboardFormats != null && clipboardFormats.length > 0) {
                final HashMap hashMap = new HashMap(clipboardFormats.length, 1.0f);
                final Map flavorsForFormats = DataTransferer.getInstance().getFlavorsForFormats(clipboardFormats, SunClipboard.getDefaultFlavorTable());
                for (final DataFlavor dataFlavor : flavorsForFormats.keySet()) {
                    this.fetchOneFlavor(sunClipboard, dataFlavor, (Long)flavorsForFormats.get(dataFlavor), hashMap);
                }
                DataTransferer.getInstance();
                this.flavors = DataTransferer.setToSortedDataFlavorArray(this.flavorsToData.keySet());
            }
        }
        finally {
            sunClipboard.closeClipboard();
        }
    }
    
    private boolean fetchOneFlavor(final SunClipboard sunClipboard, final DataFlavor dataFlavor, final Long n, final HashMap hashMap) {
        if (!this.flavorsToData.containsKey(dataFlavor)) {
            final long longValue = n;
            byte[] array = null;
            if (!hashMap.containsKey(n)) {
                try {
                    array = sunClipboard.getClipboardData(longValue);
                }
                catch (final IOException ex) {
                    array = (byte[])(Object)ex;
                }
                catch (final Throwable t) {
                    t.printStackTrace();
                }
                hashMap.put(n, array);
            }
            else {
                array = hashMap.get(n);
            }
            if (array instanceof IOException) {
                this.flavorsToData.put(dataFlavor, array);
                return false;
            }
            if (array != null) {
                this.flavorsToData.put(dataFlavor, new DataFactory(longValue, array));
                return true;
            }
        }
        return false;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return this.flavors.clone();
    }
    
    @Override
    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        return this.flavorsToData.containsKey(dataFlavor);
    }
    
    @Override
    public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (!this.isDataFlavorSupported(dataFlavor)) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        Object o = this.flavorsToData.get(dataFlavor);
        if (o instanceof IOException) {
            throw (IOException)o;
        }
        if (o instanceof DataFactory) {
            o = ((DataFactory)o).getTransferData(dataFlavor);
        }
        return o;
    }
    
    private final class DataFactory
    {
        final long format;
        final byte[] data;
        
        DataFactory(final long format, final byte[] data) {
            this.format = format;
            this.data = data;
        }
        
        public Object getTransferData(final DataFlavor dataFlavor) throws IOException {
            return DataTransferer.getInstance().translateBytes(this.data, dataFlavor, this.format, ClipboardTransferable.this);
        }
    }
}
