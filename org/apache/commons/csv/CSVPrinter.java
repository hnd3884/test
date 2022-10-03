package org.apache.commons.csv;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.io.IOException;
import java.io.Closeable;
import java.io.Flushable;

public final class CSVPrinter implements Flushable, Closeable
{
    private final Appendable out;
    private final CSVFormat format;
    private boolean newRecord;
    
    public CSVPrinter(final Appendable out, final CSVFormat format) throws IOException {
        this.newRecord = true;
        Assertions.notNull(out, "out");
        Assertions.notNull(format, "format");
        this.out = out;
        this.format = format;
        if (format.getHeaderComments() != null) {
            for (final String line : format.getHeaderComments()) {
                if (line != null) {
                    this.printComment(line);
                }
            }
        }
        if (format.getHeader() != null && !format.getSkipHeaderRecord()) {
            this.printRecord((Object[])format.getHeader());
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.out instanceof Closeable) {
            ((Closeable)this.out).close();
        }
    }
    
    @Override
    public void flush() throws IOException {
        if (this.out instanceof Flushable) {
            ((Flushable)this.out).flush();
        }
    }
    
    public Appendable getOut() {
        return this.out;
    }
    
    public void print(final Object value) throws IOException {
        String strValue;
        if (value == null) {
            final String nullString = this.format.getNullString();
            strValue = ((nullString == null) ? "" : nullString);
        }
        else {
            strValue = value.toString();
        }
        strValue = (this.format.getTrim() ? strValue.trim() : strValue);
        this.print(value, strValue, 0, strValue.length());
    }
    
    private void print(final Object object, final CharSequence value, final int offset, final int len) throws IOException {
        if (!this.newRecord) {
            this.out.append(this.format.getDelimiter());
        }
        if (object == null) {
            this.out.append(value);
        }
        else if (this.format.isQuoteCharacterSet()) {
            this.printAndQuote(object, value, offset, len);
        }
        else if (this.format.isEscapeCharacterSet()) {
            this.printAndEscape(value, offset, len);
        }
        else {
            this.out.append(value, offset, offset + len);
        }
        this.newRecord = false;
    }
    
    private void printAndEscape(final CharSequence value, final int offset, final int len) throws IOException {
        int start = offset;
        int pos = offset;
        final int end = offset + len;
        final char delim = this.format.getDelimiter();
        final char escape = this.format.getEscapeCharacter();
        while (pos < end) {
            char c = value.charAt(pos);
            if (c == '\r' || c == '\n' || c == delim || c == escape) {
                if (pos > start) {
                    this.out.append(value, start, pos);
                }
                if (c == '\n') {
                    c = 'n';
                }
                else if (c == '\r') {
                    c = 'r';
                }
                this.out.append(escape);
                this.out.append(c);
                start = pos + 1;
            }
            ++pos;
        }
        if (pos > start) {
            this.out.append(value, start, pos);
        }
    }
    
    private void printAndQuote(final Object object, final CharSequence value, final int offset, final int len) throws IOException {
        boolean quote = false;
        int start = offset;
        int pos = offset;
        final int end = offset + len;
        final char delimChar = this.format.getDelimiter();
        final char quoteChar = this.format.getQuoteCharacter();
        QuoteMode quoteModePolicy = this.format.getQuoteMode();
        if (quoteModePolicy == null) {
            quoteModePolicy = QuoteMode.MINIMAL;
        }
        switch (quoteModePolicy) {
            case ALL: {
                quote = true;
                break;
            }
            case NON_NUMERIC: {
                quote = !(object instanceof Number);
                break;
            }
            case NONE: {
                this.printAndEscape(value, offset, len);
                return;
            }
            case MINIMAL: {
                if (len <= 0) {
                    if (this.newRecord) {
                        quote = true;
                    }
                }
                else {
                    char c = value.charAt(pos);
                    if (this.newRecord && (c < '0' || (c > '9' && c < 'A') || (c > 'Z' && c < 'a') || c > 'z')) {
                        quote = true;
                    }
                    else if (c <= '#') {
                        quote = true;
                    }
                    else {
                        while (pos < end) {
                            c = value.charAt(pos);
                            if (c == '\n' || c == '\r' || c == quoteChar || c == delimChar) {
                                quote = true;
                                break;
                            }
                            ++pos;
                        }
                        if (!quote) {
                            pos = end - 1;
                            c = value.charAt(pos);
                            if (c <= ' ') {
                                quote = true;
                            }
                        }
                    }
                }
                if (!quote) {
                    this.out.append(value, start, end);
                    return;
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected Quote value: " + quoteModePolicy);
            }
        }
        if (!quote) {
            this.out.append(value, start, end);
            return;
        }
        this.out.append(quoteChar);
        while (pos < end) {
            final char c = value.charAt(pos);
            if (c == quoteChar) {
                this.out.append(value, start, pos + 1);
                start = pos;
            }
            ++pos;
        }
        this.out.append(value, start, pos);
        this.out.append(quoteChar);
    }
    
    public void printComment(final String comment) throws IOException {
        if (!this.format.isCommentMarkerSet()) {
            return;
        }
        if (!this.newRecord) {
            this.println();
        }
        this.out.append(this.format.getCommentMarker());
        this.out.append(' ');
        for (int i = 0; i < comment.length(); ++i) {
            final char c = comment.charAt(i);
            switch (c) {
                case '\r': {
                    if (i + 1 < comment.length() && comment.charAt(i + 1) == '\n') {
                        ++i;
                    }
                }
                case '\n': {
                    this.println();
                    this.out.append(this.format.getCommentMarker());
                    this.out.append(' ');
                    break;
                }
                default: {
                    this.out.append(c);
                    break;
                }
            }
        }
        this.println();
    }
    
    public void println() throws IOException {
        if (this.format.getTrailingDelimiter()) {
            this.out.append(this.format.getDelimiter());
        }
        final String recordSeparator = this.format.getRecordSeparator();
        if (recordSeparator != null) {
            this.out.append(recordSeparator);
        }
        this.newRecord = true;
    }
    
    public void printRecord(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            this.print(value);
        }
        this.println();
    }
    
    public void printRecord(final Object... values) throws IOException {
        for (final Object value : values) {
            this.print(value);
        }
        this.println();
    }
    
    public void printRecords(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[])value);
            }
            else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>)value);
            }
            else {
                this.printRecord(value);
            }
        }
    }
    
    public void printRecords(final Object... values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[])value);
            }
            else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>)value);
            }
            else {
                this.printRecord(value);
            }
        }
    }
    
    public void printRecords(final ResultSet resultSet) throws SQLException, IOException {
        final int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; ++i) {
                this.print(resultSet.getObject(i));
            }
            this.println();
        }
    }
}
