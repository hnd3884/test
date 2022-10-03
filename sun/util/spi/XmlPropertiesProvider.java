package sun.util.spi;

import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class XmlPropertiesProvider
{
    protected XmlPropertiesProvider() {
    }
    
    public abstract void load(final Properties p0, final InputStream p1) throws IOException, InvalidPropertiesFormatException;
    
    public abstract void store(final Properties p0, final OutputStream p1, final String p2, final String p3) throws IOException;
}
