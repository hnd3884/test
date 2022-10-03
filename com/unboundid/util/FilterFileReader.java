package com.unboundid.util;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Filter;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FilterFileReader implements Closeable
{
    private final AtomicLong lineNumberCounter;
    private final BufferedReader reader;
    private final File filterFile;
    
    public FilterFileReader(final String path) throws IOException {
        this(new File(path));
    }
    
    public FilterFileReader(final File filterFile) throws IOException {
        this.filterFile = filterFile;
        this.reader = new BufferedReader(new FileReader(filterFile));
        this.lineNumberCounter = new AtomicLong(0L);
    }
    
    public Filter readFilter() throws IOException, LDAPException {
        while (true) {
            final String line;
            final long lineNumber;
            synchronized (this) {
                line = this.reader.readLine();
                lineNumber = this.lineNumberCounter.incrementAndGet();
            }
            if (line == null) {
                return null;
            }
            final String filterString = line.trim();
            if (filterString.isEmpty()) {
                continue;
            }
            if (filterString.startsWith("#")) {
                continue;
            }
            try {
                return Filter.create(filterString);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPException(ResultCode.FILTER_ERROR, UtilityMessages.ERR_FILTER_FILE_READER_CANNOT_PARSE_FILTER.get(filterString, lineNumber, this.filterFile.getAbsolutePath(), le.getMessage()), le);
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
