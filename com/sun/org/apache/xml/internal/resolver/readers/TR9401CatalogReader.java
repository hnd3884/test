package com.sun.org.apache.xml.internal.resolver.readers;

import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.util.Vector;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import java.io.InputStream;
import com.sun.org.apache.xml.internal.resolver.Catalog;

public class TR9401CatalogReader extends TextCatalogReader
{
    @Override
    public void readCatalog(final Catalog catalog, final InputStream is) throws MalformedURLException, IOException {
        this.catfile = is;
        if (this.catfile == null) {
            return;
        }
        Vector unknownEntry = null;
        try {
            while (true) {
                final String token = this.nextToken();
                if (token == null) {
                    break;
                }
                String entryToken = null;
                if (this.caseSensitive) {
                    entryToken = token;
                }
                else {
                    entryToken = token.toUpperCase();
                }
                if (entryToken.equals("DELEGATE")) {
                    entryToken = "DELEGATE_PUBLIC";
                }
                try {
                    final int type = CatalogEntry.getEntryType(entryToken);
                    final int numArgs = CatalogEntry.getEntryArgCount(type);
                    final Vector args = new Vector();
                    if (unknownEntry != null) {
                        catalog.unknownEntry(unknownEntry);
                        unknownEntry = null;
                    }
                    for (int count = 0; count < numArgs; ++count) {
                        args.addElement(this.nextToken());
                    }
                    catalog.addEntry(new CatalogEntry(entryToken, args));
                }
                catch (final CatalogException cex) {
                    if (cex.getExceptionType() == 3) {
                        if (unknownEntry == null) {
                            unknownEntry = new Vector();
                        }
                        unknownEntry.addElement(token);
                    }
                    else if (cex.getExceptionType() == 2) {
                        catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", token);
                        unknownEntry = null;
                    }
                    else {
                        if (cex.getExceptionType() != 8) {
                            continue;
                        }
                        catalog.getCatalogManager().debug.message(1, cex.getMessage());
                    }
                }
            }
            if (unknownEntry != null) {
                catalog.unknownEntry(unknownEntry);
                unknownEntry = null;
            }
            this.catfile.close();
            this.catfile = null;
        }
        catch (final CatalogException cex2) {
            if (cex2.getExceptionType() == 8) {
                catalog.getCatalogManager().debug.message(1, cex2.getMessage());
            }
        }
    }
}
