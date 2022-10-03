package javax.activation;

import java.io.OutputStream;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

public interface DataContentHandler
{
    Object getContent(final DataSource p0) throws IOException;
    
    Object getTransferData(final DataFlavor p0, final DataSource p1) throws UnsupportedFlavorException, IOException;
    
    DataFlavor[] getTransferDataFlavors();
    
    void writeTo(final Object p0, final String p1, final OutputStream p2) throws IOException;
}
