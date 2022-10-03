package org.apache.commons.csv;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Set;
import java.util.HashSet;
import java.io.Reader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.io.Serializable;

public final class CSVFormat implements Serializable
{
    public static final CSVFormat DEFAULT;
    public static final CSVFormat EXCEL;
    public static final CSVFormat INFORMIX_UNLOAD;
    public static final CSVFormat INFORMIX_UNLOAD_CSV;
    public static final CSVFormat MYSQL;
    public static final CSVFormat RFC4180;
    private static final long serialVersionUID = 1L;
    public static final CSVFormat TDF;
    private final boolean allowMissingColumnNames;
    private final Character commentMarker;
    private final char delimiter;
    private final Character escapeCharacter;
    private final String[] header;
    private final String[] headerComments;
    private final boolean ignoreEmptyLines;
    private final boolean ignoreHeaderCase;
    private final boolean ignoreSurroundingSpaces;
    private final String nullString;
    private final Character quoteCharacter;
    private final QuoteMode quoteMode;
    private final String recordSeparator;
    private final boolean skipHeaderRecord;
    private final boolean trailingDelimiter;
    private final boolean trim;
    
    private static boolean isLineBreak(final char c) {
        return c == '\n' || c == '\r';
    }
    
    private static boolean isLineBreak(final Character c) {
        return c != null && isLineBreak((char)c);
    }
    
    public static CSVFormat newFormat(final char delimiter) {
        return new CSVFormat(delimiter, null, null, null, null, false, false, null, null, null, null, false, false, false, false, false);
    }
    
    public static CSVFormat valueOf(final String format) {
        return Predefined.valueOf(format).getFormat();
    }
    
    private CSVFormat(final char delimiter, final Character quoteChar, final QuoteMode quoteMode, final Character commentStart, final Character escape, final boolean ignoreSurroundingSpaces, final boolean ignoreEmptyLines, final String recordSeparator, final String nullString, final Object[] headerComments, final String[] header, final boolean skipHeaderRecord, final boolean allowMissingColumnNames, final boolean ignoreHeaderCase, final boolean trim, final boolean trailingDelimiter) {
        this.delimiter = delimiter;
        this.quoteCharacter = quoteChar;
        this.quoteMode = quoteMode;
        this.commentMarker = commentStart;
        this.escapeCharacter = escape;
        this.ignoreSurroundingSpaces = ignoreSurroundingSpaces;
        this.allowMissingColumnNames = allowMissingColumnNames;
        this.ignoreEmptyLines = ignoreEmptyLines;
        this.recordSeparator = recordSeparator;
        this.nullString = nullString;
        this.headerComments = this.toStringArray(headerComments);
        this.header = (String[])((header == null) ? null : ((String[])header.clone()));
        this.skipHeaderRecord = skipHeaderRecord;
        this.ignoreHeaderCase = ignoreHeaderCase;
        this.trailingDelimiter = trailingDelimiter;
        this.trim = trim;
        this.validate();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CSVFormat other = (CSVFormat)obj;
        if (this.delimiter != other.delimiter) {
            return false;
        }
        if (this.quoteMode != other.quoteMode) {
            return false;
        }
        if (this.quoteCharacter == null) {
            if (other.quoteCharacter != null) {
                return false;
            }
        }
        else if (!this.quoteCharacter.equals(other.quoteCharacter)) {
            return false;
        }
        if (this.commentMarker == null) {
            if (other.commentMarker != null) {
                return false;
            }
        }
        else if (!this.commentMarker.equals(other.commentMarker)) {
            return false;
        }
        if (this.escapeCharacter == null) {
            if (other.escapeCharacter != null) {
                return false;
            }
        }
        else if (!this.escapeCharacter.equals(other.escapeCharacter)) {
            return false;
        }
        if (this.nullString == null) {
            if (other.nullString != null) {
                return false;
            }
        }
        else if (!this.nullString.equals(other.nullString)) {
            return false;
        }
        if (!Arrays.equals(this.header, other.header)) {
            return false;
        }
        if (this.ignoreSurroundingSpaces != other.ignoreSurroundingSpaces) {
            return false;
        }
        if (this.ignoreEmptyLines != other.ignoreEmptyLines) {
            return false;
        }
        if (this.skipHeaderRecord != other.skipHeaderRecord) {
            return false;
        }
        if (this.recordSeparator == null) {
            if (other.recordSeparator != null) {
                return false;
            }
        }
        else if (!this.recordSeparator.equals(other.recordSeparator)) {
            return false;
        }
        return true;
    }
    
    public String format(final Object... values) {
        final StringWriter out = new StringWriter();
        try {
            new CSVPrinter(out, this).printRecord(values);
            return out.toString().trim();
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public boolean getAllowMissingColumnNames() {
        return this.allowMissingColumnNames;
    }
    
    public Character getCommentMarker() {
        return this.commentMarker;
    }
    
    public char getDelimiter() {
        return this.delimiter;
    }
    
    public Character getEscapeCharacter() {
        return this.escapeCharacter;
    }
    
    public String[] getHeader() {
        return (String[])((this.header != null) ? ((String[])this.header.clone()) : null);
    }
    
    public String[] getHeaderComments() {
        return (String[])((this.headerComments != null) ? ((String[])this.headerComments.clone()) : null);
    }
    
    public boolean getIgnoreEmptyLines() {
        return this.ignoreEmptyLines;
    }
    
    public boolean getIgnoreHeaderCase() {
        return this.ignoreHeaderCase;
    }
    
    public boolean getIgnoreSurroundingSpaces() {
        return this.ignoreSurroundingSpaces;
    }
    
    public String getNullString() {
        return this.nullString;
    }
    
    public Character getQuoteCharacter() {
        return this.quoteCharacter;
    }
    
    public QuoteMode getQuoteMode() {
        return this.quoteMode;
    }
    
    public String getRecordSeparator() {
        return this.recordSeparator;
    }
    
    public boolean getSkipHeaderRecord() {
        return this.skipHeaderRecord;
    }
    
    public boolean getTrailingDelimiter() {
        return this.trailingDelimiter;
    }
    
    public boolean getTrim() {
        return this.trim;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.delimiter;
        result = 31 * result + ((this.quoteMode == null) ? 0 : this.quoteMode.hashCode());
        result = 31 * result + ((this.quoteCharacter == null) ? 0 : this.quoteCharacter.hashCode());
        result = 31 * result + ((this.commentMarker == null) ? 0 : this.commentMarker.hashCode());
        result = 31 * result + ((this.escapeCharacter == null) ? 0 : this.escapeCharacter.hashCode());
        result = 31 * result + ((this.nullString == null) ? 0 : this.nullString.hashCode());
        result = 31 * result + (this.ignoreSurroundingSpaces ? 1231 : 1237);
        result = 31 * result + (this.ignoreHeaderCase ? 1231 : 1237);
        result = 31 * result + (this.ignoreEmptyLines ? 1231 : 1237);
        result = 31 * result + (this.skipHeaderRecord ? 1231 : 1237);
        result = 31 * result + ((this.recordSeparator == null) ? 0 : this.recordSeparator.hashCode());
        result = 31 * result + Arrays.hashCode(this.header);
        return result;
    }
    
    public boolean isCommentMarkerSet() {
        return this.commentMarker != null;
    }
    
    public boolean isEscapeCharacterSet() {
        return this.escapeCharacter != null;
    }
    
    public boolean isNullStringSet() {
        return this.nullString != null;
    }
    
    public boolean isQuoteCharacterSet() {
        return this.quoteCharacter != null;
    }
    
    public CSVParser parse(final Reader in) throws IOException {
        return new CSVParser(in, this);
    }
    
    public CSVPrinter print(final Appendable out) throws IOException {
        return new CSVPrinter(out, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Delimiter=<").append(this.delimiter).append('>');
        if (this.isEscapeCharacterSet()) {
            sb.append(' ');
            sb.append("Escape=<").append(this.escapeCharacter).append('>');
        }
        if (this.isQuoteCharacterSet()) {
            sb.append(' ');
            sb.append("QuoteChar=<").append(this.quoteCharacter).append('>');
        }
        if (this.isCommentMarkerSet()) {
            sb.append(' ');
            sb.append("CommentStart=<").append(this.commentMarker).append('>');
        }
        if (this.isNullStringSet()) {
            sb.append(' ');
            sb.append("NullString=<").append(this.nullString).append('>');
        }
        if (this.recordSeparator != null) {
            sb.append(' ');
            sb.append("RecordSeparator=<").append(this.recordSeparator).append('>');
        }
        if (this.getIgnoreEmptyLines()) {
            sb.append(" EmptyLines:ignored");
        }
        if (this.getIgnoreSurroundingSpaces()) {
            sb.append(" SurroundingSpaces:ignored");
        }
        if (this.getIgnoreHeaderCase()) {
            sb.append(" IgnoreHeaderCase:ignored");
        }
        sb.append(" SkipHeaderRecord:").append(this.skipHeaderRecord);
        if (this.headerComments != null) {
            sb.append(' ');
            sb.append("HeaderComments:").append(Arrays.toString(this.headerComments));
        }
        if (this.header != null) {
            sb.append(' ');
            sb.append("Header:").append(Arrays.toString(this.header));
        }
        return sb.toString();
    }
    
    private String[] toStringArray(final Object[] values) {
        if (values == null) {
            return null;
        }
        final String[] strings = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            final Object value = values[i];
            strings[i] = ((value == null) ? null : value.toString());
        }
        return strings;
    }
    
    private void validate() throws IllegalArgumentException {
        if (isLineBreak(this.delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }
        if (this.quoteCharacter != null && this.delimiter == this.quoteCharacter) {
            throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + this.quoteCharacter + "')");
        }
        if (this.escapeCharacter != null && this.delimiter == this.escapeCharacter) {
            throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + this.escapeCharacter + "')");
        }
        if (this.commentMarker != null && this.delimiter == this.commentMarker) {
            throw new IllegalArgumentException("The comment start character and the delimiter cannot be the same ('" + this.commentMarker + "')");
        }
        if (this.quoteCharacter != null && this.quoteCharacter.equals(this.commentMarker)) {
            throw new IllegalArgumentException("The comment start character and the quoteChar cannot be the same ('" + this.commentMarker + "')");
        }
        if (this.escapeCharacter != null && this.escapeCharacter.equals(this.commentMarker)) {
            throw new IllegalArgumentException("The comment start and the escape character cannot be the same ('" + this.commentMarker + "')");
        }
        if (this.escapeCharacter == null && this.quoteMode == QuoteMode.NONE) {
            throw new IllegalArgumentException("No quotes mode set but no escape character is set");
        }
        if (this.header != null) {
            final Set<String> dupCheck = new HashSet<String>();
            for (final String hdr : this.header) {
                if (!dupCheck.add(hdr)) {
                    throw new IllegalArgumentException("The header contains a duplicate entry: '" + hdr + "' in " + Arrays.toString(this.header));
                }
            }
        }
    }
    
    public CSVFormat withAllowMissingColumnNames() {
        return this.withAllowMissingColumnNames(true);
    }
    
    public CSVFormat withAllowMissingColumnNames(final boolean allowMissingColumnNames) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withCommentMarker(final char commentMarker) {
        return this.withCommentMarker(Character.valueOf(commentMarker));
    }
    
    public CSVFormat withCommentMarker(final Character commentMarker) {
        if (isLineBreak(commentMarker)) {
            throw new IllegalArgumentException("The comment start marker character cannot be a line break");
        }
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withDelimiter(final char delimiter) {
        if (isLineBreak(delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }
        return new CSVFormat(delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withEscape(final char escape) {
        return this.withEscape(Character.valueOf(escape));
    }
    
    public CSVFormat withEscape(final Character escape) {
        if (isLineBreak(escape)) {
            throw new IllegalArgumentException("The escape character cannot be a line break");
        }
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, escape, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withFirstRecordAsHeader() {
        return this.withHeader(new String[0]).withSkipHeaderRecord();
    }
    
    public CSVFormat withHeader(final ResultSet resultSet) throws SQLException {
        return this.withHeader((resultSet != null) ? resultSet.getMetaData() : null);
    }
    
    public CSVFormat withHeader(final ResultSetMetaData metaData) throws SQLException {
        String[] labels = null;
        if (metaData != null) {
            final int columnCount = metaData.getColumnCount();
            labels = new String[columnCount];
            for (int i = 0; i < columnCount; ++i) {
                labels[i] = metaData.getColumnLabel(i + 1);
            }
        }
        return this.withHeader(labels);
    }
    
    public CSVFormat withHeader(final String... header) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withHeader(final Class<? extends Enum<?>> headerEnum) {
        String[] header = null;
        if (headerEnum != null) {
            final Enum<?>[] enumValues = (Enum<?>[])headerEnum.getEnumConstants();
            header = new String[enumValues.length];
            for (int i = 0; i < enumValues.length; ++i) {
                header[i] = enumValues[i].name();
            }
        }
        return this.withHeader(header);
    }
    
    public CSVFormat withHeaderComments(final Object... headerComments) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withIgnoreEmptyLines() {
        return this.withIgnoreEmptyLines(true);
    }
    
    public CSVFormat withIgnoreEmptyLines(final boolean ignoreEmptyLines) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withIgnoreHeaderCase() {
        return this.withIgnoreHeaderCase(true);
    }
    
    public CSVFormat withIgnoreHeaderCase(final boolean ignoreHeaderCase) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withIgnoreSurroundingSpaces() {
        return this.withIgnoreSurroundingSpaces(true);
    }
    
    public CSVFormat withIgnoreSurroundingSpaces(final boolean ignoreSurroundingSpaces) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withNullString(final String nullString) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withQuote(final char quoteChar) {
        return this.withQuote(Character.valueOf(quoteChar));
    }
    
    public CSVFormat withQuote(final Character quoteChar) {
        if (isLineBreak(quoteChar)) {
            throw new IllegalArgumentException("The quoteChar cannot be a line break");
        }
        return new CSVFormat(this.delimiter, quoteChar, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withQuoteMode(final QuoteMode quoteModePolicy) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, quoteModePolicy, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withRecordSeparator(final char recordSeparator) {
        return this.withRecordSeparator(String.valueOf(recordSeparator));
    }
    
    public CSVFormat withRecordSeparator(final String recordSeparator) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withSkipHeaderRecord() {
        return this.withSkipHeaderRecord(true);
    }
    
    public CSVFormat withSkipHeaderRecord(final boolean skipHeaderRecord) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }
    
    public CSVFormat withTrailingDelimiter() {
        return this.withTrailingDelimiter(true);
    }
    
    public CSVFormat withTrailingDelimiter(final boolean trailingDelimiter) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, trailingDelimiter);
    }
    
    public CSVFormat withTrim() {
        return this.withTrim(true);
    }
    
    public CSVFormat withTrim(final boolean trim) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, trim, this.trailingDelimiter);
    }
    
    static {
        DEFAULT = new CSVFormat(',', Constants.DOUBLE_QUOTE_CHAR, null, null, null, false, true, "\r\n", null, null, null, false, false, false, false, false);
        EXCEL = CSVFormat.DEFAULT.withIgnoreEmptyLines(false).withAllowMissingColumnNames();
        INFORMIX_UNLOAD = CSVFormat.DEFAULT.withDelimiter('|').withEscape('\\').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
        INFORMIX_UNLOAD_CSV = CSVFormat.DEFAULT.withDelimiter(',').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
        MYSQL = CSVFormat.DEFAULT.withDelimiter('\t').withEscape('\\').withIgnoreEmptyLines(false).withQuote(null).withRecordSeparator('\n').withNullString("\\N");
        RFC4180 = CSVFormat.DEFAULT.withIgnoreEmptyLines(false);
        TDF = CSVFormat.DEFAULT.withDelimiter('\t').withIgnoreSurroundingSpaces();
    }
    
    public enum Predefined
    {
        Default(CSVFormat.DEFAULT), 
        Excel(CSVFormat.EXCEL), 
        InformixUnload(CSVFormat.INFORMIX_UNLOAD), 
        InformixUnloadCsv(CSVFormat.INFORMIX_UNLOAD_CSV), 
        MySQL(CSVFormat.MYSQL), 
        RFC4180(CSVFormat.RFC4180), 
        TDF(CSVFormat.TDF);
        
        private final CSVFormat format;
        
        private Predefined(final CSVFormat format) {
            this.format = format;
        }
        
        public CSVFormat getFormat() {
            return this.format;
        }
    }
}
