package com.adventnet.tree.parser;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class TreeDefinitionEntityResolver implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicID, final String systemID) throws IOException {
        try {
            final File file = new File(systemID);
            final String fileName = file.getName();
            final ClassLoader clsLoader = this.getClass().getClassLoader();
            final URL url = clsLoader.getResource("dtd/tree-definition.dtd");
            final InputStream stream = url.openStream();
            return new InputSource(stream);
        }
        catch (final Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e.getMessage());
        }
    }
}
