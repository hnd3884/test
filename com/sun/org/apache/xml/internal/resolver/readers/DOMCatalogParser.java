package com.sun.org.apache.xml.internal.resolver.readers;

import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.resolver.Catalog;

public interface DOMCatalogParser
{
    void parseCatalogEntry(final Catalog p0, final Node p1);
}
