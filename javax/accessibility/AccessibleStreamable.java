package javax.accessibility;

import java.io.InputStream;
import java.awt.datatransfer.DataFlavor;

public interface AccessibleStreamable
{
    DataFlavor[] getMimeTypes();
    
    InputStream getStream(final DataFlavor p0);
}
