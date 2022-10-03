package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;

public interface SAXCatalogParser extends ContentHandler, DocumentHandler
{
    void setCatalog(final Catalog p0);
}
