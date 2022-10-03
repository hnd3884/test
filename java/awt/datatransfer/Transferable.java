package java.awt.datatransfer;

import java.io.IOException;

public interface Transferable
{
    DataFlavor[] getTransferDataFlavors();
    
    boolean isDataFlavorSupported(final DataFlavor p0);
    
    Object getTransferData(final DataFlavor p0) throws UnsupportedFlavorException, IOException;
}
