package org.apache.commons.net.ftp;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.net.util.Charsets;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;

public class FTPListParseEngine
{
    private List<String> entries;
    private ListIterator<String> _internalIterator;
    private final FTPFileEntryParser parser;
    private final boolean saveUnparseableEntries;
    
    public FTPListParseEngine(final FTPFileEntryParser parser) {
        this(parser, null);
    }
    
    FTPListParseEngine(final FTPFileEntryParser parser, final FTPClientConfig configuration) {
        this.entries = new LinkedList<String>();
        this._internalIterator = this.entries.listIterator();
        this.parser = parser;
        if (configuration != null) {
            this.saveUnparseableEntries = configuration.getUnparseableEntries();
        }
        else {
            this.saveUnparseableEntries = false;
        }
    }
    
    public void readServerList(final InputStream stream, final String encoding) throws IOException {
        this.entries = new LinkedList<String>();
        this.readStream(stream, encoding);
        this.parser.preParse(this.entries);
        this.resetIterator();
    }
    
    private void readStream(final InputStream stream, final String encoding) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.toCharset(encoding)));
        for (String line = this.parser.readNextEntry(reader); line != null; line = this.parser.readNextEntry(reader)) {
            this.entries.add(line);
        }
        reader.close();
    }
    
    public FTPFile[] getNext(final int quantityRequested) {
        final List<FTPFile> tmpResults = new LinkedList<FTPFile>();
        for (int count = quantityRequested; count > 0 && this._internalIterator.hasNext(); --count) {
            final String entry = this._internalIterator.next();
            FTPFile temp = this.parser.parseFTPEntry(entry);
            if (temp == null && this.saveUnparseableEntries) {
                temp = new FTPFile(entry);
            }
            tmpResults.add(temp);
        }
        return tmpResults.toArray(new FTPFile[tmpResults.size()]);
    }
    
    public FTPFile[] getPrevious(final int quantityRequested) {
        final List<FTPFile> tmpResults = new LinkedList<FTPFile>();
        for (int count = quantityRequested; count > 0 && this._internalIterator.hasPrevious(); --count) {
            final String entry = this._internalIterator.previous();
            FTPFile temp = this.parser.parseFTPEntry(entry);
            if (temp == null && this.saveUnparseableEntries) {
                temp = new FTPFile(entry);
            }
            tmpResults.add(0, temp);
        }
        return tmpResults.toArray(new FTPFile[tmpResults.size()]);
    }
    
    public FTPFile[] getFiles() throws IOException {
        return this.getFiles(FTPFileFilters.NON_NULL);
    }
    
    public FTPFile[] getFiles(final FTPFileFilter filter) throws IOException {
        final List<FTPFile> tmpResults = new ArrayList<FTPFile>();
        for (final String entry : this.entries) {
            FTPFile temp = this.parser.parseFTPEntry(entry);
            if (temp == null && this.saveUnparseableEntries) {
                temp = new FTPFile(entry);
            }
            if (filter.accept(temp)) {
                tmpResults.add(temp);
            }
        }
        return tmpResults.toArray(new FTPFile[tmpResults.size()]);
    }
    
    public boolean hasNext() {
        return this._internalIterator.hasNext();
    }
    
    public boolean hasPrevious() {
        return this._internalIterator.hasPrevious();
    }
    
    public void resetIterator() {
        this._internalIterator = this.entries.listIterator();
    }
    
    @Deprecated
    public void readServerList(final InputStream stream) throws IOException {
        this.readServerList(stream, null);
    }
}
