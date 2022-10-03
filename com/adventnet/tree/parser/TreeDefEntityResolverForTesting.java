package com.adventnet.tree.parser;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class TreeDefEntityResolverForTesting implements EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicID, final String systemID) throws IOException {
        try {
            final File file = new File(systemID);
            final String fileName = file.getName();
            final ClassLoader clsLoader = this.getClass().getClassLoader();
            final URL url = new File("./conf/tree-definition.dtd").toURL();
            System.out.println("URL ::: " + url);
            final InputStream stream = url.openStream();
            return new InputSource(stream);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }
}
