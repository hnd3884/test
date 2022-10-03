package jdk.internal.util.xml;

import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import sun.util.spi.XmlPropertiesProvider;

public class BasicXmlPropertiesProvider extends XmlPropertiesProvider
{
    @Override
    public void load(final Properties properties, final InputStream inputStream) throws IOException, InvalidPropertiesFormatException {
        new PropertiesDefaultHandler().load(properties, inputStream);
    }
    
    @Override
    public void store(final Properties properties, final OutputStream outputStream, final String s, final String s2) throws IOException {
        new PropertiesDefaultHandler().store(properties, outputStream, s, s2);
    }
}
