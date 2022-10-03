package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.util.Vector;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import java.io.IOException;
import java.net.URLConnection;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import java.util.Stack;
import java.io.InputStream;

public class TextCatalogReader implements CatalogReader
{
    protected InputStream catfile;
    protected int[] stack;
    protected Stack tokenStack;
    protected int top;
    protected boolean caseSensitive;
    
    public TextCatalogReader() {
        this.catfile = null;
        this.stack = new int[3];
        this.tokenStack = new Stack();
        this.top = -1;
        this.caseSensitive = false;
    }
    
    public void setCaseSensitive(final boolean isCaseSensitive) {
        this.caseSensitive = isCaseSensitive;
    }
    
    public boolean getCaseSensitive() {
        return this.caseSensitive;
    }
    
    @Override
    public void readCatalog(final Catalog catalog, final String fileUrl) throws MalformedURLException, IOException {
        URL catURL = null;
        try {
            catURL = new URL(fileUrl);
        }
        catch (final MalformedURLException e) {
            catURL = new URL("file:///" + fileUrl);
        }
        final URLConnection urlCon = catURL.openConnection();
        try {
            this.readCatalog(catalog, urlCon.getInputStream());
        }
        catch (final FileNotFoundException e2) {
            catalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found", catURL.toString());
        }
    }
    
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
    
    @Override
    protected void finalize() {
        if (this.catfile != null) {
            try {
                this.catfile.close();
            }
            catch (final IOException ex) {}
        }
        this.catfile = null;
    }
    
    protected String nextToken() throws IOException, CatalogException {
        String token = "";
        if (!this.tokenStack.empty()) {
            return this.tokenStack.pop();
        }
        int nextch;
        do {
            int ch = this.catfile.read();
            while (ch <= 32) {
                ch = this.catfile.read();
                if (ch < 0) {
                    return null;
                }
            }
            nextch = this.catfile.read();
            if (nextch < 0) {
                return null;
            }
            if (ch == 45 && nextch == 45) {
                for (ch = 32, nextch = this.nextChar(); (ch != 45 || nextch != 45) && nextch > 0; ch = nextch, nextch = this.nextChar()) {}
            }
            else {
                this.stack[++this.top] = nextch;
                this.stack[++this.top] = ch;
                ch = this.nextChar();
                if (ch == 34 || ch == 39) {
                    final int quote = ch;
                    while ((ch = this.nextChar()) != quote) {
                        final char[] chararr = { (char)ch };
                        final String s = new String(chararr);
                        token = token.concat(s);
                    }
                    return token;
                }
                while (ch > 32) {
                    nextch = this.nextChar();
                    if (ch == 45 && nextch == 45) {
                        this.stack[++this.top] = ch;
                        this.stack[++this.top] = nextch;
                        return token;
                    }
                    final char[] chararr2 = { (char)ch };
                    final String s2 = new String(chararr2);
                    token = token.concat(s2);
                    ch = nextch;
                }
                return token;
            }
        } while (nextch >= 0);
        throw new CatalogException(8, "Unterminated comment in catalog file; EOF treated as end-of-comment.");
    }
    
    protected int nextChar() throws IOException {
        if (this.top < 0) {
            return this.catfile.read();
        }
        return this.stack[this.top--];
    }
}
