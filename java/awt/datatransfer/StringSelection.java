package java.awt.datatransfer;

import java.io.IOException;
import java.io.StringReader;

public class StringSelection implements Transferable, ClipboardOwner
{
    private static final int STRING = 0;
    private static final int PLAIN_TEXT = 1;
    private static final DataFlavor[] flavors;
    private String data;
    
    public StringSelection(final String data) {
        this.data = data;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return StringSelection.flavors.clone();
    }
    
    @Override
    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        for (int i = 0; i < StringSelection.flavors.length; ++i) {
            if (dataFlavor.equals(StringSelection.flavors[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (dataFlavor.equals(StringSelection.flavors[0])) {
            return this.data;
        }
        if (dataFlavor.equals(StringSelection.flavors[1])) {
            return new StringReader((this.data == null) ? "" : this.data);
        }
        throw new UnsupportedFlavorException(dataFlavor);
    }
    
    @Override
    public void lostOwnership(final Clipboard clipboard, final Transferable transferable) {
    }
    
    static {
        flavors = new DataFlavor[] { DataFlavor.stringFlavor, DataFlavor.plainTextFlavor };
    }
}
