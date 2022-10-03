package com.sun.rowset.internal;

import java.io.ObjectInputStream;
import javax.sql.RowSetInternal;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.sql.ResultSet;
import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.Map;
import java.text.MessageFormat;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.sql.SQLException;
import javax.sql.rowset.WebRowSet;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.util.Stack;
import java.io.Writer;
import java.io.Serializable;
import javax.sql.rowset.spi.XmlWriter;

public class WebRowSetXmlWriter implements XmlWriter, Serializable
{
    private transient Writer writer;
    private Stack<String> stack;
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = 7163134986189677641L;
    
    public WebRowSetXmlWriter() {
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void writeXML(final WebRowSet set, final Writer writer) throws SQLException {
        this.stack = new Stack<String>();
        this.writer = writer;
        this.writeRowSet(set);
    }
    
    public void writeXML(final WebRowSet set, final OutputStream outputStream) throws SQLException {
        this.stack = new Stack<String>();
        this.writer = new OutputStreamWriter(outputStream);
        this.writeRowSet(set);
    }
    
    private void writeRowSet(final WebRowSet set) throws SQLException {
        try {
            this.startHeader();
            this.writeProperties(set);
            this.writeMetaData(set);
            this.writeData(set);
            this.endHeader();
        }
        catch (final IOException ex) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), ex.getMessage()));
        }
    }
    
    private void startHeader() throws IOException {
        this.setTag("webRowSet");
        this.writer.write("<?xml version=\"1.0\"?>\n");
        this.writer.write("<webRowSet xmlns=\"http://java.sun.com/xml/ns/jdbc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        this.writer.write("xsi:schemaLocation=\"http://java.sun.com/xml/ns/jdbc http://java.sun.com/xml/ns/jdbc/webrowset.xsd\">\n");
    }
    
    private void endHeader() throws IOException {
        this.endTag("webRowSet");
    }
    
    private void writeProperties(final WebRowSet set) throws IOException {
        this.beginSection("properties");
        try {
            this.propString("command", this.processSpecialCharacters(set.getCommand()));
            this.propInteger("concurrency", set.getConcurrency());
            this.propString("datasource", set.getDataSourceName());
            this.propBoolean("escape-processing", set.getEscapeProcessing());
            try {
                this.propInteger("fetch-direction", set.getFetchDirection());
            }
            catch (final SQLException ex) {}
            this.propInteger("fetch-size", set.getFetchSize());
            this.propInteger("isolation-level", set.getTransactionIsolation());
            this.beginSection("key-columns");
            final int[] keyColumns = set.getKeyColumns();
            for (int n = 0; keyColumns != null && n < keyColumns.length; ++n) {
                this.propInteger("column", keyColumns[n]);
            }
            this.endSection("key-columns");
            this.beginSection("map");
            final Map<String, Class<?>> typeMap = set.getTypeMap();
            if (typeMap != null) {
                for (final Map.Entry entry : typeMap.entrySet()) {
                    this.propString("type", (String)entry.getKey());
                    this.propString("class", ((Class)entry.getValue()).getName());
                }
            }
            this.endSection("map");
            this.propInteger("max-field-size", set.getMaxFieldSize());
            this.propInteger("max-rows", set.getMaxRows());
            this.propInteger("query-timeout", set.getQueryTimeout());
            this.propBoolean("read-only", set.isReadOnly());
            final int type = set.getType();
            String s = "";
            if (type == 1003) {
                s = "ResultSet.TYPE_FORWARD_ONLY";
            }
            else if (type == 1004) {
                s = "ResultSet.TYPE_SCROLL_INSENSITIVE";
            }
            else if (type == 1005) {
                s = "ResultSet.TYPE_SCROLL_SENSITIVE";
            }
            this.propString("rowset-type", s);
            this.propBoolean("show-deleted", set.getShowDeleted());
            this.propString("table-name", set.getTableName());
            this.propString("url", set.getUrl());
            this.beginSection("sync-provider");
            this.propString("sync-provider-name", set.getSyncProvider().toString().substring(0, set.getSyncProvider().toString().indexOf("@")));
            this.propString("sync-provider-vendor", "Oracle Corporation");
            this.propString("sync-provider-version", "1.0");
            this.propInteger("sync-provider-grade", set.getSyncProvider().getProviderGrade());
            this.propInteger("data-source-lock", set.getSyncProvider().getDataSourceLock());
            this.endSection("sync-provider");
        }
        catch (final SQLException ex2) {
            throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex2.getMessage()));
        }
        this.endSection("properties");
    }
    
    private void writeMetaData(final WebRowSet set) throws IOException {
        this.beginSection("metadata");
        try {
            final ResultSetMetaData metaData = set.getMetaData();
            final int columnCount = metaData.getColumnCount();
            this.propInteger("column-count", columnCount);
            for (int i = 1; i <= columnCount; ++i) {
                this.beginSection("column-definition");
                this.propInteger("column-index", i);
                this.propBoolean("auto-increment", metaData.isAutoIncrement(i));
                this.propBoolean("case-sensitive", metaData.isCaseSensitive(i));
                this.propBoolean("currency", metaData.isCurrency(i));
                this.propInteger("nullable", metaData.isNullable(i));
                this.propBoolean("signed", metaData.isSigned(i));
                this.propBoolean("searchable", metaData.isSearchable(i));
                this.propInteger("column-display-size", metaData.getColumnDisplaySize(i));
                this.propString("column-label", metaData.getColumnLabel(i));
                this.propString("column-name", metaData.getColumnName(i));
                this.propString("schema-name", metaData.getSchemaName(i));
                this.propInteger("column-precision", metaData.getPrecision(i));
                this.propInteger("column-scale", metaData.getScale(i));
                this.propString("table-name", metaData.getTableName(i));
                this.propString("catalog-name", metaData.getCatalogName(i));
                this.propInteger("column-type", metaData.getColumnType(i));
                this.propString("column-type-name", metaData.getColumnTypeName(i));
                this.endSection("column-definition");
            }
        }
        catch (final SQLException ex) {
            throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex.getMessage()));
        }
        this.endSection("metadata");
    }
    
    private void writeData(final WebRowSet set) throws IOException {
        try {
            final int columnCount = set.getMetaData().getColumnCount();
            this.beginSection("data");
            set.beforeFirst();
            set.setShowDeleted(true);
            while (set.next()) {
                if (set.rowDeleted() && set.rowInserted()) {
                    this.beginSection("modifyRow");
                }
                else if (set.rowDeleted()) {
                    this.beginSection("deleteRow");
                }
                else if (set.rowInserted()) {
                    this.beginSection("insertRow");
                }
                else {
                    this.beginSection("currentRow");
                }
                for (int i = 1; i <= columnCount; ++i) {
                    if (set.columnUpdated(i)) {
                        final ResultSet originalRow = set.getOriginalRow();
                        originalRow.next();
                        this.beginTag("columnValue");
                        this.writeValue(i, (RowSet)originalRow);
                        this.endTag("columnValue");
                        this.beginTag("updateRow");
                        this.writeValue(i, set);
                        this.endTag("updateRow");
                    }
                    else {
                        this.beginTag("columnValue");
                        this.writeValue(i, set);
                        this.endTag("columnValue");
                    }
                }
                this.endSection();
            }
            this.endSection("data");
        }
        catch (final SQLException ex) {
            throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex.getMessage()));
        }
    }
    
    private void writeValue(final int n, final RowSet set) throws IOException {
        try {
            switch (set.getMetaData().getColumnType(n)) {
                case -7:
                case 16: {
                    final boolean boolean1 = set.getBoolean(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeBoolean(boolean1);
                    break;
                }
                case -6:
                case 5: {
                    final short short1 = set.getShort(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeShort(short1);
                    break;
                }
                case 4: {
                    final int int1 = set.getInt(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeInteger(int1);
                    break;
                }
                case -5: {
                    final long long1 = set.getLong(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeLong(long1);
                    break;
                }
                case 6:
                case 7: {
                    final float float1 = set.getFloat(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeFloat(float1);
                    break;
                }
                case 8: {
                    final double double1 = set.getDouble(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeDouble(double1);
                    break;
                }
                case 2:
                case 3: {
                    this.writeBigDecimal(set.getBigDecimal(n));
                    break;
                }
                case -4:
                case -3:
                case -2: {
                    break;
                }
                case 91: {
                    final Date date = set.getDate(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeLong(date.getTime());
                    break;
                }
                case 92: {
                    final Time time = set.getTime(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeLong(time.getTime());
                    break;
                }
                case 93: {
                    final Timestamp timestamp = set.getTimestamp(n);
                    if (set.wasNull()) {
                        this.writeNull();
                        break;
                    }
                    this.writeLong(timestamp.getTime());
                    break;
                }
                case -1:
                case 1:
                case 12: {
                    this.writeStringData(set.getString(n));
                    break;
                }
                default: {
                    System.out.println(this.resBundle.handleGetObject("wsrxmlwriter.notproper").toString());
                    break;
                }
            }
        }
        catch (final SQLException ex) {
            throw new IOException(this.resBundle.handleGetObject("wrsxmlwriter.failedwrite").toString() + ex.getMessage());
        }
    }
    
    private void beginSection(final String tag) throws IOException {
        this.setTag(tag);
        this.writeIndent(this.stack.size());
        this.writer.write("<" + tag + ">\n");
    }
    
    private void endSection(final String s) throws IOException {
        this.writeIndent(this.stack.size());
        String tag = this.getTag();
        if (tag.indexOf("webRowSet") != -1) {
            tag = "webRowSet";
        }
        if (s.equals(tag)) {
            this.writer.write("</" + tag + ">\n");
        }
        this.writer.flush();
    }
    
    private void endSection() throws IOException {
        this.writeIndent(this.stack.size());
        this.writer.write("</" + this.getTag() + ">\n");
        this.writer.flush();
    }
    
    private void beginTag(final String tag) throws IOException {
        this.setTag(tag);
        this.writeIndent(this.stack.size());
        this.writer.write("<" + tag + ">");
    }
    
    private void endTag(final String s) throws IOException {
        final String tag = this.getTag();
        if (s.equals(tag)) {
            this.writer.write("</" + tag + ">\n");
        }
        this.writer.flush();
    }
    
    private void emptyTag(final String s) throws IOException {
        this.writer.write("<" + s + "/>");
    }
    
    private void setTag(final String s) {
        this.stack.push(s);
    }
    
    private String getTag() {
        return this.stack.pop();
    }
    
    private void writeNull() throws IOException {
        this.emptyTag("null");
    }
    
    private void writeStringData(String processSpecialCharacters) throws IOException {
        if (processSpecialCharacters == null) {
            this.writeNull();
        }
        else if (processSpecialCharacters.equals("")) {
            this.writeEmptyString();
        }
        else {
            processSpecialCharacters = this.processSpecialCharacters(processSpecialCharacters);
            this.writer.write(processSpecialCharacters);
        }
    }
    
    private void writeString(final String s) throws IOException {
        if (s != null) {
            this.writer.write(s);
        }
        else {
            this.writeNull();
        }
    }
    
    private void writeShort(final short n) throws IOException {
        this.writer.write(Short.toString(n));
    }
    
    private void writeLong(final long n) throws IOException {
        this.writer.write(Long.toString(n));
    }
    
    private void writeInteger(final int n) throws IOException {
        this.writer.write(Integer.toString(n));
    }
    
    private void writeBoolean(final boolean b) throws IOException {
        this.writer.write(Boolean.valueOf(b).toString());
    }
    
    private void writeFloat(final float n) throws IOException {
        this.writer.write(Float.toString(n));
    }
    
    private void writeDouble(final double n) throws IOException {
        this.writer.write(Double.toString(n));
    }
    
    private void writeBigDecimal(final BigDecimal bigDecimal) throws IOException {
        if (bigDecimal != null) {
            this.writer.write(bigDecimal.toString());
        }
        else {
            this.emptyTag("null");
        }
    }
    
    private void writeIndent(final int n) throws IOException {
        for (int i = 1; i < n; ++i) {
            this.writer.write("  ");
        }
    }
    
    private void propString(final String s, final String s2) throws IOException {
        this.beginTag(s);
        this.writeString(s2);
        this.endTag(s);
    }
    
    private void propInteger(final String s, final int n) throws IOException {
        this.beginTag(s);
        this.writeInteger(n);
        this.endTag(s);
    }
    
    private void propBoolean(final String s, final boolean b) throws IOException {
        this.beginTag(s);
        this.writeBoolean(b);
        this.endTag(s);
    }
    
    private void writeEmptyString() throws IOException {
        this.emptyTag("emptyString");
    }
    
    @Override
    public boolean writeData(final RowSetInternal rowSetInternal) {
        return false;
    }
    
    private String processSpecialCharacters(String s) {
        if (s == null) {
            return null;
        }
        final char[] charArray = s.toCharArray();
        String s2 = "";
        for (int i = 0; i < charArray.length; ++i) {
            if (charArray[i] == '&') {
                s2 = s2.concat("&amp;");
            }
            else if (charArray[i] == '<') {
                s2 = s2.concat("&lt;");
            }
            else if (charArray[i] == '>') {
                s2 = s2.concat("&gt;");
            }
            else if (charArray[i] == '\'') {
                s2 = s2.concat("&apos;");
            }
            else if (charArray[i] == '\"') {
                s2 = s2.concat("&quot;");
            }
            else {
                s2 = s2.concat(String.valueOf(charArray[i]));
            }
        }
        s = s2;
        return s;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
