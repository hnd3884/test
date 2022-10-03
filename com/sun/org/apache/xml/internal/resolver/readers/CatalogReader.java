package com.sun.org.apache.xml.internal.resolver.readers;

import java.io.InputStream;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.org.apache.xml.internal.resolver.Catalog;

public interface CatalogReader
{
    void readCatalog(final Catalog p0, final String p1) throws MalformedURLException, IOException, CatalogException;
    
    void readCatalog(final Catalog p0, final InputStream p1) throws IOException, CatalogException;
}
