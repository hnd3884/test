package com.sun.rowset.internal;

import org.xml.sax.SAXParseException;
import javax.sql.rowset.RowSetMetaDataImpl;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.text.MessageFormat;
import sun.reflect.misc.ReflectUtil;
import java.sql.SQLException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.sql.RowSet;
import com.sun.rowset.JdbcRowSetResourceBundle;
import javax.sql.RowSetMetaData;
import com.sun.rowset.WebRowSetImpl;
import java.util.Vector;
import java.util.HashMap;
import org.xml.sax.helpers.DefaultHandler;

public class XmlReaderContentHandler extends DefaultHandler
{
    private HashMap<String, Integer> propMap;
    private HashMap<String, Integer> colDefMap;
    private HashMap<String, Integer> dataMap;
    private HashMap<String, Class<?>> typeMap;
    private Vector<Object[]> updates;
    private Vector<String> keyCols;
    private String columnValue;
    private String propertyValue;
    private String metaDataValue;
    private int tag;
    private int state;
    private WebRowSetImpl rs;
    private boolean nullVal;
    private boolean emptyStringVal;
    private RowSetMetaData md;
    private int idx;
    private String lastval;
    private String Key_map;
    private String Value_map;
    private String tempStr;
    private String tempUpdate;
    private String tempCommand;
    private Object[] upd;
    private String[] properties;
    private static final int CommandTag = 0;
    private static final int ConcurrencyTag = 1;
    private static final int DatasourceTag = 2;
    private static final int EscapeProcessingTag = 3;
    private static final int FetchDirectionTag = 4;
    private static final int FetchSizeTag = 5;
    private static final int IsolationLevelTag = 6;
    private static final int KeycolsTag = 7;
    private static final int MapTag = 8;
    private static final int MaxFieldSizeTag = 9;
    private static final int MaxRowsTag = 10;
    private static final int QueryTimeoutTag = 11;
    private static final int ReadOnlyTag = 12;
    private static final int RowsetTypeTag = 13;
    private static final int ShowDeletedTag = 14;
    private static final int TableNameTag = 15;
    private static final int UrlTag = 16;
    private static final int PropNullTag = 17;
    private static final int PropColumnTag = 18;
    private static final int PropTypeTag = 19;
    private static final int PropClassTag = 20;
    private static final int SyncProviderTag = 21;
    private static final int SyncProviderNameTag = 22;
    private static final int SyncProviderVendorTag = 23;
    private static final int SyncProviderVersionTag = 24;
    private static final int SyncProviderGradeTag = 25;
    private static final int DataSourceLock = 26;
    private String[] colDef;
    private static final int ColumnCountTag = 0;
    private static final int ColumnDefinitionTag = 1;
    private static final int ColumnIndexTag = 2;
    private static final int AutoIncrementTag = 3;
    private static final int CaseSensitiveTag = 4;
    private static final int CurrencyTag = 5;
    private static final int NullableTag = 6;
    private static final int SignedTag = 7;
    private static final int SearchableTag = 8;
    private static final int ColumnDisplaySizeTag = 9;
    private static final int ColumnLabelTag = 10;
    private static final int ColumnNameTag = 11;
    private static final int SchemaNameTag = 12;
    private static final int ColumnPrecisionTag = 13;
    private static final int ColumnScaleTag = 14;
    private static final int MetaTableNameTag = 15;
    private static final int CatalogNameTag = 16;
    private static final int ColumnTypeTag = 17;
    private static final int ColumnTypeNameTag = 18;
    private static final int MetaNullTag = 19;
    private String[] data;
    private static final int RowTag = 0;
    private static final int ColTag = 1;
    private static final int InsTag = 2;
    private static final int DelTag = 3;
    private static final int InsDelTag = 4;
    private static final int UpdTag = 5;
    private static final int NullTag = 6;
    private static final int EmptyStringTag = 7;
    private static final int INITIAL = 0;
    private static final int PROPERTIES = 1;
    private static final int METADATA = 2;
    private static final int DATA = 3;
    private JdbcRowSetResourceBundle resBundle;
    
    public XmlReaderContentHandler(final RowSet set) {
        this.properties = new String[] { "command", "concurrency", "datasource", "escape-processing", "fetch-direction", "fetch-size", "isolation-level", "key-columns", "map", "max-field-size", "max-rows", "query-timeout", "read-only", "rowset-type", "show-deleted", "table-name", "url", "null", "column", "type", "class", "sync-provider", "sync-provider-name", "sync-provider-vendor", "sync-provider-version", "sync-provider-grade", "data-source-lock" };
        this.colDef = new String[] { "column-count", "column-definition", "column-index", "auto-increment", "case-sensitive", "currency", "nullable", "signed", "searchable", "column-display-size", "column-label", "column-name", "schema-name", "column-precision", "column-scale", "table-name", "catalog-name", "column-type", "column-type-name", "null" };
        this.data = new String[] { "currentRow", "columnValue", "insertRow", "deleteRow", "insdel", "updateRow", "null", "emptyString" };
        this.rs = (WebRowSetImpl)set;
        this.initMaps();
        this.updates = new Vector<Object[]>();
        this.columnValue = "";
        this.propertyValue = "";
        this.metaDataValue = "";
        this.nullVal = false;
        this.idx = 0;
        this.tempStr = "";
        this.tempUpdate = "";
        this.tempCommand = "";
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void initMaps() {
        this.propMap = new HashMap<String, Integer>();
        for (int length = this.properties.length, i = 0; i < length; ++i) {
            this.propMap.put(this.properties[i], i);
        }
        this.colDefMap = new HashMap<String, Integer>();
        for (int length2 = this.colDef.length, j = 0; j < length2; ++j) {
            this.colDefMap.put(this.colDef[j], j);
        }
        this.dataMap = new HashMap<String, Integer>();
        for (int length3 = this.data.length, k = 0; k < length3; ++k) {
            this.dataMap.put(this.data[k], k);
        }
        this.typeMap = new HashMap<String, Class<?>>();
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startElement(final String s, final String state, final String s2, final Attributes attributes) throws SAXException {
        switch (this.getState()) {
            case 1: {
                this.tempCommand = "";
                final int intValue = this.propMap.get(state);
                if (intValue == 17) {
                    this.setNullValue(true);
                    break;
                }
                this.setTag(intValue);
                break;
            }
            case 2: {
                final int intValue2 = this.colDefMap.get(state);
                if (intValue2 == 19) {
                    this.setNullValue(true);
                    break;
                }
                this.setTag(intValue2);
                break;
            }
            case 3: {
                this.tempStr = "";
                this.tempUpdate = "";
                int intValue3;
                if (this.dataMap.get(state) == null) {
                    intValue3 = 6;
                }
                else if (this.dataMap.get(state) == 7) {
                    intValue3 = 7;
                }
                else {
                    intValue3 = this.dataMap.get(state);
                }
                if (intValue3 == 6) {
                    this.setNullValue(true);
                    break;
                }
                if (intValue3 == 7) {
                    this.setEmptyStringValue(true);
                    break;
                }
                this.setTag(intValue3);
                if (intValue3 == 0 || intValue3 == 3 || intValue3 == 2) {
                    this.idx = 0;
                    try {
                        this.rs.moveToInsertRow();
                    }
                    catch (final SQLException ex) {}
                    break;
                }
                break;
            }
            default: {
                this.setState(state);
                break;
            }
        }
    }
    
    @Override
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        Label_1017: {
            switch (this.getState()) {
                case 1: {
                    if (s2.equals("properties")) {
                        this.state = 0;
                        break;
                    }
                    try {
                        switch (this.propMap.get(s2)) {
                            case 7: {
                                if (this.keyCols != null) {
                                    final int[] keyColumns = new int[this.keyCols.size()];
                                    for (int i = 0; i < keyColumns.length; ++i) {
                                        keyColumns[i] = Integer.parseInt(this.keyCols.elementAt(i));
                                    }
                                    this.rs.setKeyColumns(keyColumns);
                                    break;
                                }
                                break;
                            }
                            case 20: {
                                try {
                                    this.typeMap.put(this.Key_map, ReflectUtil.forName(this.Value_map));
                                    break;
                                }
                                catch (final ClassNotFoundException ex) {
                                    throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmap").toString(), ex.getMessage()));
                                }
                            }
                            case 8: {
                                this.rs.setTypeMap(this.typeMap);
                                break;
                            }
                        }
                        if (this.getNullValue()) {
                            this.setPropertyValue(null);
                            this.setNullValue(false);
                        }
                        else {
                            this.setPropertyValue(this.propertyValue);
                        }
                    }
                    catch (final SQLException ex2) {
                        throw new SAXException(ex2.getMessage());
                    }
                    this.propertyValue = "";
                    this.setTag(-1);
                    break;
                }
                case 2: {
                    Label_0448: {
                        if (s2.equals("metadata")) {
                            try {
                                this.rs.setMetaData(this.md);
                                this.state = 0;
                                break Label_0448;
                            }
                            catch (final SQLException ex3) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmetadata").toString(), ex3.getMessage()));
                            }
                        }
                        try {
                            if (this.getNullValue()) {
                                this.setMetaDataValue(null);
                                this.setNullValue(false);
                            }
                            else {
                                this.setMetaDataValue(this.metaDataValue);
                            }
                        }
                        catch (final SQLException ex4) {
                            throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmetadata").toString(), ex4.getMessage()));
                        }
                        this.metaDataValue = "";
                    }
                    this.setTag(-1);
                    break;
                }
                case 3: {
                    if (s2.equals("data")) {
                        this.state = 0;
                        return;
                    }
                    int intValue;
                    if (this.dataMap.get(s2) == null) {
                        intValue = 6;
                    }
                    else {
                        intValue = this.dataMap.get(s2);
                    }
                    switch (intValue) {
                        case 1: {
                            try {
                                ++this.idx;
                                if (this.getNullValue()) {
                                    this.insertValue(null);
                                    this.setNullValue(false);
                                }
                                else {
                                    this.insertValue(this.tempStr);
                                }
                                this.columnValue = "";
                                break Label_1017;
                            }
                            catch (final SQLException ex5) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsertval").toString(), ex5.getMessage()));
                            }
                        }
                        case 0: {
                            try {
                                this.rs.insertRow();
                                this.rs.moveToCurrentRow();
                                this.rs.next();
                                this.rs.setOriginalRow();
                                this.applyUpdates();
                                break Label_1017;
                            }
                            catch (final SQLException ex6) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errconstr").toString(), ex6.getMessage()));
                            }
                        }
                        case 3: {
                            try {
                                this.rs.insertRow();
                                this.rs.moveToCurrentRow();
                                this.rs.next();
                                this.rs.setOriginalRow();
                                this.applyUpdates();
                                this.rs.deleteRow();
                                break Label_1017;
                            }
                            catch (final SQLException ex7) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errdel").toString(), ex7.getMessage()));
                            }
                        }
                        case 2: {
                            try {
                                this.rs.insertRow();
                                this.rs.moveToCurrentRow();
                                this.rs.next();
                                this.applyUpdates();
                                break Label_1017;
                            }
                            catch (final SQLException ex8) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsert").toString(), ex8.getMessage()));
                            }
                        }
                        case 4: {
                            try {
                                this.rs.insertRow();
                                this.rs.moveToCurrentRow();
                                this.rs.next();
                                this.rs.setOriginalRow();
                                this.applyUpdates();
                                break Label_1017;
                            }
                            catch (final SQLException ex9) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsdel").toString(), ex9.getMessage()));
                            }
                        }
                        case 5: {
                            try {
                                if (this.getNullValue()) {
                                    this.insertValue(null);
                                    this.setNullValue(false);
                                }
                                else if (this.getEmptyStringValue()) {
                                    this.insertValue("");
                                    this.setEmptyStringValue(false);
                                }
                                else {
                                    this.updates.add(this.upd);
                                }
                            }
                            catch (final SQLException ex10) {
                                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errupdate").toString(), ex10.getMessage()));
                            }
                            break Label_1017;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    private void applyUpdates() throws SAXException {
        if (this.updates.size() > 0) {
            try {
                for (final Object[] array : this.updates) {
                    this.idx = (int)array[0];
                    if (!this.lastval.equals(array[1])) {
                        this.insertValue((String)array[1]);
                    }
                }
                this.rs.updateRow();
            }
            catch (final SQLException ex) {
                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errupdrow").toString(), ex.getMessage()));
            }
            this.updates.removeAllElements();
        }
    }
    
    @Override
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        try {
            switch (this.getState()) {
                case 1: {
                    this.propertyValue = new String(array, n, n2);
                    this.tempCommand = this.tempCommand.concat(this.propertyValue);
                    this.propertyValue = this.tempCommand;
                    if (this.tag == 19) {
                        this.Key_map = this.propertyValue;
                        break;
                    }
                    if (this.tag == 20) {
                        this.Value_map = this.propertyValue;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (this.tag == -1) {
                        break;
                    }
                    this.metaDataValue = new String(array, n, n2);
                    break;
                }
                case 3: {
                    this.setDataValue(array, n, n2);
                    break;
                }
            }
        }
        catch (final SQLException ex) {
            throw new SAXException(this.resBundle.handleGetObject("xmlrch.chars").toString() + ex.getMessage());
        }
    }
    
    private void setState(final String s) throws SAXException {
        if (s.equals("webRowSet")) {
            this.state = 0;
        }
        else if (s.equals("properties")) {
            if (this.state != 1) {
                this.state = 1;
            }
            else {
                this.state = 0;
            }
        }
        else if (s.equals("metadata")) {
            if (this.state != 2) {
                this.state = 2;
            }
            else {
                this.state = 0;
            }
        }
        else if (s.equals("data")) {
            if (this.state != 3) {
                this.state = 3;
            }
            else {
                this.state = 0;
            }
        }
    }
    
    private int getState() {
        return this.state;
    }
    
    private void setTag(final int tag) {
        this.tag = tag;
    }
    
    private int getTag() {
        return this.tag;
    }
    
    private void setNullValue(final boolean nullVal) {
        this.nullVal = nullVal;
    }
    
    private boolean getNullValue() {
        return this.nullVal;
    }
    
    private void setEmptyStringValue(final boolean emptyStringVal) {
        this.emptyStringVal = emptyStringVal;
    }
    
    private boolean getEmptyStringValue() {
        return this.emptyStringVal;
    }
    
    private String getStringValue(final String s) {
        return s;
    }
    
    private int getIntegerValue(final String s) {
        return Integer.parseInt(s);
    }
    
    private boolean getBooleanValue(final String s) {
        return Boolean.valueOf(s);
    }
    
    private BigDecimal getBigDecimalValue(final String s) {
        return new BigDecimal(s);
    }
    
    private byte getByteValue(final String s) {
        return Byte.parseByte(s);
    }
    
    private short getShortValue(final String s) {
        return Short.parseShort(s);
    }
    
    private long getLongValue(final String s) {
        return Long.parseLong(s);
    }
    
    private float getFloatValue(final String s) {
        return Float.parseFloat(s);
    }
    
    private double getDoubleValue(final String s) {
        return Double.parseDouble(s);
    }
    
    private byte[] getBinaryValue(final String s) {
        return s.getBytes();
    }
    
    private Date getDateValue(final String s) {
        return new Date(this.getLongValue(s));
    }
    
    private Time getTimeValue(final String s) {
        return new Time(this.getLongValue(s));
    }
    
    private Timestamp getTimestampValue(final String s) {
        return new Timestamp(this.getLongValue(s));
    }
    
    private void setPropertyValue(final String s) throws SQLException {
        final boolean nullValue = this.getNullValue();
        switch (this.getTag()) {
            case 0: {
                if (nullValue) {
                    break;
                }
                this.rs.setCommand(s);
                break;
            }
            case 1: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setConcurrency(this.getIntegerValue(s));
                break;
            }
            case 2: {
                if (nullValue) {
                    this.rs.setDataSourceName(null);
                    break;
                }
                this.rs.setDataSourceName(s);
                break;
            }
            case 3: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setEscapeProcessing(this.getBooleanValue(s));
                break;
            }
            case 4: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setFetchDirection(this.getIntegerValue(s));
                break;
            }
            case 5: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setFetchSize(this.getIntegerValue(s));
                break;
            }
            case 6: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setTransactionIsolation(this.getIntegerValue(s));
                break;
            }
            case 18: {
                if (this.keyCols == null) {
                    this.keyCols = new Vector<String>();
                }
                this.keyCols.add(s);
            }
            case 9: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setMaxFieldSize(this.getIntegerValue(s));
                break;
            }
            case 10: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setMaxRows(this.getIntegerValue(s));
                break;
            }
            case 11: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setQueryTimeout(this.getIntegerValue(s));
                break;
            }
            case 12: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setReadOnly(this.getBooleanValue(s));
                break;
            }
            case 13: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                final String stringValue = this.getStringValue(s);
                int type = 0;
                if (stringValue.trim().equals("ResultSet.TYPE_SCROLL_INSENSITIVE")) {
                    type = 1004;
                }
                else if (stringValue.trim().equals("ResultSet.TYPE_SCROLL_SENSITIVE")) {
                    type = 1005;
                }
                else if (stringValue.trim().equals("ResultSet.TYPE_FORWARD_ONLY")) {
                    type = 1003;
                }
                this.rs.setType(type);
                break;
            }
            case 14: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString());
                }
                this.rs.setShowDeleted(this.getBooleanValue(s));
                break;
            }
            case 15: {
                if (nullValue) {
                    break;
                }
                this.rs.setTableName(s);
                break;
            }
            case 16: {
                if (nullValue) {
                    this.rs.setUrl(null);
                    break;
                }
                this.rs.setUrl(s);
                break;
            }
            case 22: {
                if (nullValue) {
                    this.rs.setSyncProvider(null);
                    break;
                }
                this.rs.setSyncProvider(s.substring(0, s.indexOf("@") + 1));
                break;
            }
            case 23: {}
            case 24: {}
            case 25: {}
        }
    }
    
    private void setMetaDataValue(final String s) throws SQLException {
        final boolean nullValue = this.getNullValue();
        switch (this.getTag()) {
            case 0: {
                this.md = new RowSetMetaDataImpl();
                this.idx = 0;
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setColumnCount(this.getIntegerValue(s));
                break;
            }
            case 2: {
                ++this.idx;
                break;
            }
            case 3: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setAutoIncrement(this.idx, this.getBooleanValue(s));
                break;
            }
            case 4: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setCaseSensitive(this.idx, this.getBooleanValue(s));
                break;
            }
            case 5: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setCurrency(this.idx, this.getBooleanValue(s));
                break;
            }
            case 6: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setNullable(this.idx, this.getIntegerValue(s));
                break;
            }
            case 7: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setSigned(this.idx, this.getBooleanValue(s));
                break;
            }
            case 8: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setSearchable(this.idx, this.getBooleanValue(s));
                break;
            }
            case 9: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setColumnDisplaySize(this.idx, this.getIntegerValue(s));
                break;
            }
            case 10: {
                if (nullValue) {
                    this.md.setColumnLabel(this.idx, null);
                    break;
                }
                this.md.setColumnLabel(this.idx, s);
                break;
            }
            case 11: {
                if (nullValue) {
                    this.md.setColumnName(this.idx, null);
                    break;
                }
                this.md.setColumnName(this.idx, s);
                break;
            }
            case 12: {
                if (nullValue) {
                    this.md.setSchemaName(this.idx, null);
                    break;
                }
                this.md.setSchemaName(this.idx, s);
                break;
            }
            case 13: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setPrecision(this.idx, this.getIntegerValue(s));
                break;
            }
            case 14: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setScale(this.idx, this.getIntegerValue(s));
                break;
            }
            case 15: {
                if (nullValue) {
                    this.md.setTableName(this.idx, null);
                    break;
                }
                this.md.setTableName(this.idx, s);
                break;
            }
            case 16: {
                if (nullValue) {
                    this.md.setCatalogName(this.idx, null);
                    break;
                }
                this.md.setCatalogName(this.idx, s);
                break;
            }
            case 17: {
                if (nullValue) {
                    throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString());
                }
                this.md.setColumnType(this.idx, this.getIntegerValue(s));
                break;
            }
            case 18: {
                if (nullValue) {
                    this.md.setColumnTypeName(this.idx, null);
                    break;
                }
                this.md.setColumnTypeName(this.idx, s);
                break;
            }
        }
    }
    
    private void setDataValue(final char[] array, final int n, final int n2) throws SQLException {
        switch (this.getTag()) {
            case 1: {
                this.columnValue = new String(array, n, n2);
                this.tempStr = this.tempStr.concat(this.columnValue);
                break;
            }
            case 5: {
                this.upd = new Object[2];
                this.tempUpdate = this.tempUpdate.concat(new String(array, n, n2));
                this.upd[0] = this.idx;
                this.upd[1] = this.tempUpdate;
                this.lastval = (String)this.upd[1];
                break;
            }
        }
    }
    
    private void insertValue(final String s) throws SQLException {
        if (this.getNullValue()) {
            this.rs.updateNull(this.idx);
            return;
        }
        switch (this.rs.getMetaData().getColumnType(this.idx)) {
            case -7: {
                this.rs.updateBoolean(this.idx, this.getBooleanValue(s));
                break;
            }
            case 16: {
                this.rs.updateBoolean(this.idx, this.getBooleanValue(s));
                break;
            }
            case -6:
            case 5: {
                this.rs.updateShort(this.idx, this.getShortValue(s));
                break;
            }
            case 4: {
                this.rs.updateInt(this.idx, this.getIntegerValue(s));
                break;
            }
            case -5: {
                this.rs.updateLong(this.idx, this.getLongValue(s));
                break;
            }
            case 6:
            case 7: {
                this.rs.updateFloat(this.idx, this.getFloatValue(s));
                break;
            }
            case 8: {
                this.rs.updateDouble(this.idx, this.getDoubleValue(s));
                break;
            }
            case 2:
            case 3: {
                this.rs.updateObject(this.idx, this.getBigDecimalValue(s));
                break;
            }
            case -4:
            case -3:
            case -2: {
                this.rs.updateBytes(this.idx, this.getBinaryValue(s));
                break;
            }
            case 91: {
                this.rs.updateDate(this.idx, this.getDateValue(s));
                break;
            }
            case 92: {
                this.rs.updateTime(this.idx, this.getTimeValue(s));
                break;
            }
            case 93: {
                this.rs.updateTimestamp(this.idx, this.getTimestampValue(s));
                break;
            }
            case -1:
            case 1:
            case 12: {
                this.rs.updateString(this.idx, this.getStringValue(s));
                break;
            }
        }
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXParseException {
        throw ex;
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXParseException {
        System.out.println(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.warning").toString(), ex.getMessage(), ex.getLineNumber(), ex.getSystemId()));
    }
    
    @Override
    public void notationDecl(final String s, final String s2, final String s3) {
    }
    
    @Override
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) {
    }
    
    private Row getPresentRow(final WebRowSetImpl webRowSetImpl) throws SQLException {
        return null;
    }
}
