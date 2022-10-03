package org.apache.commons.csv;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.net.URL;
import java.io.StringReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.io.Closeable;

public final class CSVParser implements Iterable<CSVRecord>, Closeable
{
    private final CSVFormat format;
    private final Map<String, Integer> headerMap;
    private final Lexer lexer;
    private final List<String> record;
    private long recordNumber;
    private final long characterOffset;
    private final Token reusableToken;
    
    public static CSVParser parse(final File file, final Charset charset, final CSVFormat format) throws IOException {
        Assertions.notNull(file, "file");
        Assertions.notNull(format, "format");
        return new CSVParser(new InputStreamReader(new FileInputStream(file), charset), format);
    }
    
    public static CSVParser parse(final String string, final CSVFormat format) throws IOException {
        Assertions.notNull(string, "string");
        Assertions.notNull(format, "format");
        return new CSVParser(new StringReader(string), format);
    }
    
    public static CSVParser parse(final URL url, final Charset charset, final CSVFormat format) throws IOException {
        Assertions.notNull(url, "url");
        Assertions.notNull(charset, "charset");
        Assertions.notNull(format, "format");
        return new CSVParser(new InputStreamReader(url.openStream(), charset), format);
    }
    
    public CSVParser(final Reader reader, final CSVFormat format) throws IOException {
        this(reader, format, 0L, 1L);
    }
    
    public CSVParser(final Reader reader, final CSVFormat format, final long characterOffset, final long recordNumber) throws IOException {
        this.record = new ArrayList<String>();
        this.reusableToken = new Token();
        Assertions.notNull(reader, "reader");
        Assertions.notNull(format, "format");
        this.format = format;
        this.lexer = new Lexer(format, new ExtendedBufferedReader(reader));
        this.headerMap = this.initializeHeader();
        this.characterOffset = characterOffset;
        this.recordNumber = recordNumber - 1L;
    }
    
    private void addRecordValue(final boolean lastRecord) {
        final String input = this.reusableToken.content.toString();
        final String inputClean = this.format.getTrim() ? input.trim() : input;
        if (lastRecord && inputClean.isEmpty() && this.format.getTrailingDelimiter()) {
            return;
        }
        final String nullString = this.format.getNullString();
        this.record.add(inputClean.equals(nullString) ? null : inputClean);
    }
    
    @Override
    public void close() throws IOException {
        if (this.lexer != null) {
            this.lexer.close();
        }
    }
    
    public long getCurrentLineNumber() {
        return this.lexer.getCurrentLineNumber();
    }
    
    public Map<String, Integer> getHeaderMap() {
        return (this.headerMap == null) ? null : new LinkedHashMap<String, Integer>(this.headerMap);
    }
    
    public long getRecordNumber() {
        return this.recordNumber;
    }
    
    public List<CSVRecord> getRecords() throws IOException {
        final List<CSVRecord> records = new ArrayList<CSVRecord>();
        CSVRecord rec;
        while ((rec = this.nextRecord()) != null) {
            records.add(rec);
        }
        return records;
    }
    
    private Map<String, Integer> initializeHeader() throws IOException {
        Map<String, Integer> hdrMap = null;
        final String[] formatHeader = this.format.getHeader();
        if (formatHeader != null) {
            hdrMap = (Map<String, Integer>)(this.format.getIgnoreHeaderCase() ? new TreeMap<Object, Object>(String.CASE_INSENSITIVE_ORDER) : new LinkedHashMap<Object, Object>());
            String[] headerRecord = null;
            if (formatHeader.length == 0) {
                final CSVRecord nextRecord = this.nextRecord();
                if (nextRecord != null) {
                    headerRecord = nextRecord.values();
                }
            }
            else {
                if (this.format.getSkipHeaderRecord()) {
                    this.nextRecord();
                }
                headerRecord = formatHeader;
            }
            if (headerRecord != null) {
                for (int i = 0; i < headerRecord.length; ++i) {
                    final String header = headerRecord[i];
                    final boolean containsHeader = hdrMap.containsKey(header);
                    final boolean emptyHeader = header == null || header.trim().isEmpty();
                    if (containsHeader && (!emptyHeader || !this.format.getAllowMissingColumnNames())) {
                        throw new IllegalArgumentException("The header contains a duplicate name: \"" + header + "\" in " + Arrays.toString(headerRecord));
                    }
                    hdrMap.put(header, i);
                }
            }
        }
        return hdrMap;
    }
    
    public boolean isClosed() {
        return this.lexer.isClosed();
    }
    
    @Override
    public Iterator<CSVRecord> iterator() {
        return new Iterator<CSVRecord>() {
            private CSVRecord current;
            
            private CSVRecord getNextRecord() {
                try {
                    return CSVParser.this.nextRecord();
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            
            @Override
            public boolean hasNext() {
                if (CSVParser.this.isClosed()) {
                    return false;
                }
                if (this.current == null) {
                    this.current = this.getNextRecord();
                }
                return this.current != null;
            }
            
            @Override
            public CSVRecord next() {
                if (CSVParser.this.isClosed()) {
                    throw new NoSuchElementException("CSVParser has been closed");
                }
                CSVRecord next = this.current;
                this.current = null;
                if (next == null) {
                    next = this.getNextRecord();
                    if (next == null) {
                        throw new NoSuchElementException("No more CSV records available");
                    }
                }
                return next;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    CSVRecord nextRecord() throws IOException {
        CSVRecord result = null;
        this.record.clear();
        StringBuilder sb = null;
        final long startCharPosition = this.lexer.getCharacterPosition() + this.characterOffset;
        do {
            this.reusableToken.reset();
            this.lexer.nextToken(this.reusableToken);
            switch (this.reusableToken.type) {
                case TOKEN: {
                    this.addRecordValue(false);
                    continue;
                }
                case EORECORD: {
                    this.addRecordValue(true);
                    continue;
                }
                case EOF: {
                    if (this.reusableToken.isReady) {
                        this.addRecordValue(true);
                        continue;
                    }
                    continue;
                }
                case INVALID: {
                    throw new IOException("(line " + this.getCurrentLineNumber() + ") invalid parse sequence");
                }
                case COMMENT: {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    else {
                        sb.append('\n');
                    }
                    sb.append((CharSequence)this.reusableToken.content);
                    this.reusableToken.type = Token.Type.TOKEN;
                    continue;
                }
                default: {
                    throw new IllegalStateException("Unexpected Token type: " + this.reusableToken.type);
                }
            }
        } while (this.reusableToken.type == Token.Type.TOKEN);
        if (!this.record.isEmpty()) {
            ++this.recordNumber;
            final String comment = (sb == null) ? null : sb.toString();
            result = new CSVRecord(this.record.toArray(new String[this.record.size()]), this.headerMap, comment, this.recordNumber, startCharPosition);
        }
        return result;
    }
}
