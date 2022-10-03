package com.adventnet.persistence.xml;

import com.adventnet.mfw.ConsoleOut;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import com.adventnet.iam.security.SecurityUtil;
import org.xml.sax.InputSource;
import java.net.URLDecoder;
import java.io.IOException;
import java.io.File;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.StringTokenizer;
import java.util.Arrays;
import com.adventnet.persistence.Row;
import java.util.Locale;
import com.adventnet.persistence.ConcurrentStartupUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import org.xml.sax.Attributes;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.zoho.conf.AppResources;
import java.util.Properties;
import java.util.Stack;
import java.util.List;
import java.util.Map;
import java.net.URL;
import com.adventnet.persistence.DataObject;
import org.xml.sax.XMLReader;
import java.util.logging.Logger;
import org.xml.sax.ContentHandler;

public class XmlParser implements ContentHandler
{
    private static final Logger LOGGER;
    private XMLReader parser;
    private boolean dumpUVH;
    private DataObject dataObject;
    private URL url;
    private Map userSpecifiedPatternVsValue;
    private Map patternVsValue;
    private List patternList;
    private Stack parents;
    private Map patternVsTableName;
    private boolean useDVH;
    private boolean isRootElement;
    private List dvhRows;
    private Map tableNameVsRowList;
    private List rowList;
    private boolean developmentMode;
    private Properties securityProperties;
    private String moduleName;
    
    protected XmlParser() {
        this.dumpUVH = true;
        this.url = null;
        this.parents = null;
        this.patternVsTableName = null;
        this.useDVH = true;
        this.isRootElement = true;
        this.dvhRows = null;
        this.tableNameVsRowList = null;
        this.rowList = null;
        this.developmentMode = AppResources.getString("development.mode", "false").equalsIgnoreCase("true");
        this.securityProperties = null;
        this.moduleName = null;
    }
    
    protected XmlParser(final boolean useDynamicValueHandlers) {
        this.dumpUVH = true;
        this.url = null;
        this.parents = null;
        this.patternVsTableName = null;
        this.useDVH = true;
        this.isRootElement = true;
        this.dvhRows = null;
        this.tableNameVsRowList = null;
        this.rowList = null;
        this.developmentMode = AppResources.getString("development.mode", "false").equalsIgnoreCase("true");
        this.securityProperties = null;
        this.moduleName = null;
        this.useDVH = useDynamicValueHandlers;
    }
    
    private void addToPatternTableMap(final String pattern, final String tName, final String cName) {
        this.patternVsTableName.put(pattern, tName + ":" + cName.toLowerCase());
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int end) throws SAXException {
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (this.url != null) {
            XmlParser.LOGGER.log(Level.INFO, "Going to parse the XML :: [{0}]", this.url);
        }
        this.parents = new Stack();
        this.rowList = new ArrayList();
        this.patternVsTableName = new HashMap();
        this.patternVsValue = new HashMap(4);
        this.patternList = new ArrayList();
        this.dvhRows = new ArrayList();
        if (this.userSpecifiedPatternVsValue != null) {
            this.patternVsValue = new HashMap(this.userSpecifiedPatternVsValue);
        }
        else {
            this.userSpecifiedPatternVsValue = new HashMap(1);
        }
        this.tableNameVsRowList = new HashMap(2);
    }
    
    @Override
    public void startElement(final String name, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.isRootElement) {
            XmlParser.LOGGER.fine("Root Element:" + qName + " is skipped");
            this.isRootElement = false;
            return;
        }
        if (this.parents.size() > 0 && this.parents.peek() == null) {
            this.parents.push(null);
            return;
        }
        try {
            final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(qName);
            if (tableDef == null) {
                if (atts.getLength() > 0) {
                    this.warning("Invalid TableName [" + qName + "] specified");
                }
                return;
            }
            if (ConcurrentStartupUtil.isConcurrentModulePopulation()) {
                final String tableModule = tableDef.getModuleName();
                if (this.moduleName == null) {
                    XmlParser.LOGGER.log(Level.FINER, "Module Name cannot be null when parallel module population is enabled. Acut {0} cur {1} table {2}", new Object[] { this.moduleName, tableModule, tableDef.getTableName() });
                }
                else if (!ConcurrentStartupUtil.isModuleCompleted(tableModule) && !tableModule.equalsIgnoreCase(this.moduleName)) {
                    throw new SAXException("Error while populating table [ " + tableDef.getTableName() + " ] entry of module [ " + tableModule + " ] via Concurrent Module Population. Since the module does not belong to " + "current or previously completed module. Current module [ " + this.moduleName + " ]");
                }
            }
            final HashMap colvsvalue = new HashMap();
            for (int i = 0; i < atts.getLength(); ++i) {
                final String cname = atts.getQName(i);
                final String value = atts.getValue(cname);
                colvsvalue.put(cname.toLowerCase(Locale.ENGLISH), value);
            }
            final XmlRowTransformer rowProcessInstance = DynamicValueHandlerRepositry.getRowTransformer(qName);
            Row row = null;
            if (null == rowProcessInstance) {
                row = new Row(qName);
            }
            else {
                row = rowProcessInstance.createRow(qName, atts);
                if (null == row) {
                    this.parents.push(null);
                    return;
                }
                rowProcessInstance.setColumnNames(qName, colvsvalue);
                final String rowTableName = row.getTableName();
                if (!qName.equals(rowTableName)) {
                    throw new SAXException("The row object of the table [" + rowTableName + "] is returned. It should be constructed for the table [" + qName + "]. ");
                }
            }
            boolean isDVHValueNull = false;
            final List<String> columnNames = tableDef.getColumnNames();
            final Iterator iterator = columnNames.iterator();
            final int[] modifiedColIndices = row.getChangedColumnIndex();
            if (null != modifiedColIndices) {
                Arrays.sort(modifiedColIndices);
            }
            int indexVal = 0;
            while (iterator.hasNext()) {
                ++indexVal;
                final String columnName = iterator.next();
                String columnValue = null;
                final String colNameinLC = columnName.toLowerCase(Locale.ENGLISH);
                if (colvsvalue.containsKey(colNameinLC)) {
                    columnValue = colvsvalue.get(colNameinLC);
                }
                final ColumnDefinition colDef = tableDef.getColumnDefinitionByName(columnName);
                if (columnValue != null && columnValue != "" && colDef.isEncryptedColumn()) {
                    XmlParser.LOGGER.log(Level.WARNING, "Static sensitive data found while parsing. Data is from " + tableDef.getTableName() + "." + columnName);
                }
                final String dataType = colDef.getDataType();
                final UniqueValueGeneration uvg = colDef.getUniqueValueGeneration();
                if (null != modifiedColIndices && Arrays.binarySearch(modifiedColIndices, indexVal) >= 0) {
                    continue;
                }
                if (uvg != null) {
                    String unassignedUVHPattern = null;
                    if (columnValue != null) {
                        final StringTokenizer tempValTok = new StringTokenizer(columnValue, ":");
                        if (tempValTok.countTokens() >= 2) {
                            if (this.patternVsValue.get(columnValue) == null) {
                                this.patternVsValue.put(columnValue, row.get(columnName));
                                this.patternList.add(columnValue);
                            }
                            this.addToPatternTableMap(columnValue, qName, columnName);
                            unassignedUVHPattern = columnValue;
                        }
                        else if (columnValue.startsWith("::")) {
                            String genPattern = qName + ":" + colNameinLC;
                            genPattern += columnValue.substring(1);
                            if (this.patternVsValue.get(genPattern) == null) {
                                this.patternVsValue.put(genPattern, row.get(columnName));
                                this.patternList.add(genPattern);
                            }
                            this.addToPatternTableMap(genPattern, qName, columnName);
                            unassignedUVHPattern = genPattern;
                        }
                        else {
                            if (columnValue.toString().startsWith("UVH@")) {
                                throw new SAXException("UVH is not given in specified pattern [" + columnValue + "] for column [" + columnName + "] for table [" + qName + "] in the url [" + this.url + "]");
                            }
                            try {
                                if (dataType.equals("BIGINT")) {
                                    row.set(columnName, new Long(columnValue));
                                    continue;
                                }
                                row.set(columnName, new Integer(columnValue));
                                continue;
                            }
                            catch (final Exception e) {
                                unassignedUVHPattern = columnValue;
                                this.patternVsValue.put(columnValue, row.get(columnName));
                                this.patternList.add(columnValue);
                                this.addToPatternTableMap(columnName, qName, columnName);
                                if (this.url != null && !this.url.toString().endsWith("/conf-files.xml") && !this.url.toString().endsWith("/dd-files.xml") && !this.url.toString().endsWith("/module.xml")) {
                                    this.warning("UVH is not given in specified pattern [" + columnValue + "] for column [" + columnName + "] for table [" + qName + "] in the url [" + this.url + "]");
                                }
                            }
                        }
                    }
                    else if (uvg.getNameColumn() != null && colvsvalue.containsKey(uvg.getNameColumn().toLowerCase(Locale.ENGLISH))) {
                        final String uvgcolumnValue = colvsvalue.get(uvg.getNameColumn().toLowerCase(Locale.ENGLISH));
                        final String uvhString = qName + ":" + colNameinLC + ":" + uvgcolumnValue;
                        this.addToPatternTableMap(uvhString, qName, columnName);
                        if (!this.setValue(row, columnName, uvhString)) {
                            final Object uvhValue = row.get(columnName);
                            this.patternVsValue.put(uvhString, uvhValue);
                            this.patternList.add(uvhString);
                        }
                        unassignedUVHPattern = uvhString;
                    }
                    if (unassignedUVHPattern != null) {
                        this.setValue(row, columnName, unassignedUVHPattern);
                    }
                    else {
                        if (this.url == null || this.url.toString().endsWith("/conf-files.xml") || this.url.toString().endsWith("/dd-files.xml") || this.url.toString().endsWith("/module.xml")) {
                            continue;
                        }
                        this.warning("UVH Pattern is not specified for column [" + columnName + "] for table [" + qName + "] in the url [" + this.url + "]");
                    }
                }
                else {
                    Object value2 = (columnValue == null) ? null : (this.useDVH ? this.getDynamicValue(qName, columnName, columnValue, this.tableNameVsRowList) : null);
                    Label_1580: {
                        if (value2 != null) {
                            try {
                                row.set(columnName, (value2 instanceof UniqueValueHolder) ? value2 : XmlDoUtil.convert(value2.toString(), dataType));
                                continue;
                            }
                            catch (final Exception ex) {
                                break Label_1580;
                            }
                        }
                        if (columnValue != null && this.useDVH && XmlDoUtil.checkIfDynamicValueGeneratorExists(qName + ":" + columnName) && columnValue.trim().length() > 0) {
                            row.set(columnName, columnValue);
                            isDVHValueNull = true;
                            continue;
                        }
                    }
                    if (columnValue == null) {
                        continue;
                    }
                    if (columnValue.matches(".+:.+:.+")) {
                        value2 = this.patternVsValue.get(columnValue);
                        if (value2 != null) {
                            if (dataType.equals("INTEGER") && value2 instanceof Long) {
                                value2 = new Integer(value2.toString());
                            }
                            row.set(columnName, value2);
                            continue;
                        }
                    }
                    try {
                        row.set(columnName, XmlDoUtil.convert(columnValue, dataType));
                        final int index = tableDef.getColumnIndex(columnName);
                        row.markAsDirty(index);
                    }
                    catch (final Exception e2) {
                        row.set(columnName, columnValue);
                    }
                }
            }
            if (isDVHValueNull) {
                this.dvhRows.add(row);
            }
            else {
                this.rowList.add(row);
            }
            this.parents.push(row);
            this.addFKRefColumns(row, true);
            if (isDVHValueNull) {
                this.updateFKRefColumns(row);
            }
            List tempList = new ArrayList(1);
            if (this.tableNameVsRowList.get(qName) != null) {
                tempList = this.tableNameVsRowList.get(qName);
            }
            tempList.add(row);
            this.tableNameVsRowList.put(qName, tempList);
        }
        catch (final DataAccessException exp) {
            exp.printStackTrace();
            final SAXException saxExp = new SAXException(exp.getMessage());
            saxExp.initCause(exp);
            throw saxExp;
        }
        catch (final MetaDataException exp2) {
            exp2.printStackTrace();
            final SAXException saxExp = new SAXException(exp2.getMessage());
            saxExp.initCause(exp2);
            throw saxExp;
        }
        catch (final DynamicValueHandlingException exp3) {
            exp3.printStackTrace();
            final SAXException saxExp = new SAXException(exp3.getMessage());
            saxExp.initCause(exp3);
            throw saxExp;
        }
    }
    
    protected boolean setValue(final Row row, final String columnName, final String pattern) throws DataAccessException, MetaDataException {
        final ColumnDefinition cd = MetaDataUtil.getTableDefinitionByName(row.getTableName()).getColumnDefinitionByName(columnName);
        final String dataType = cd.getDataType();
        if (this.patternVsValue.get(pattern) != null) {
            Object value = this.patternVsValue.get(pattern);
            if (value instanceof Long && dataType.equals("INTEGER")) {
                value = new Integer(value.toString());
            }
            row.set(columnName, value);
            return true;
        }
        final DataObject uvhData = DataAccess.get("UVHValues", new Criteria(Column.getColumn("UVHValues", "PATTERN"), pattern, 0));
        final int patternEntryCount = uvhData.size("UVHValues");
        if (patternEntryCount > 1) {
            XmlParser.LOGGER.log(Level.WARNING, "There are Duplicate patterns for same table ::" + row.getTableName() + ":::" + columnName + ":::" + pattern);
        }
        if (!uvhData.isEmpty()) {
            final Row uvhRow = uvhData.getRow("UVHValues");
            Object value2 = uvhRow.get("GENVALUES");
            if (dataType.equals("INTEGER")) {
                value2 = new Integer(value2.toString());
            }
            row.set(columnName, value2);
            this.patternVsValue.put(pattern, value2);
            this.patternList.add(pattern);
            this.userSpecifiedPatternVsValue.put(pattern, value2);
            return true;
        }
        return false;
    }
    
    @Override
    public void endElement(final String uriName, final String localName, final String qName) throws SAXException {
        if (this.parents.size() > 0 && this.parents.peek() == null) {
            this.parents.pop();
            return;
        }
        if (!this.parents.empty()) {
            try {
                final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(qName);
                if (tableDef == null) {
                    return;
                }
                Row row = null;
                row = this.parents.pop();
                this.addFKRefColumns(row, false);
            }
            catch (final Exception exp) {
                exp.printStackTrace();
                final SAXException sax = new SAXException(exp.getMessage());
                sax.initCause(exp);
                throw sax;
            }
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            for (int i = 0; i < this.rowList.size(); ++i) {
                final Row row = this.rowList.get(i);
                this.updateFKRefColumns(row);
                this.dataObject.addRow(row);
            }
            for (int i = 0; i < this.dvhRows.size(); ++i) {
                final Row row = this.dvhRows.get(i);
                this.updateFKRefColumns(row);
                this.updateDVHColumns(row);
            }
            this.addUVHToDataObject();
            XmlParser.LOGGER.log(Level.FINEST, " Final dataObject :: {0}", this.dataObject);
        }
        catch (final DataAccessException exp) {
            exp.printStackTrace();
            final SAXException saxExp = new SAXException(exp.getMessage());
            saxExp.initCause(exp);
            throw saxExp;
        }
        catch (final MetaDataException exp2) {
            exp2.printStackTrace();
            final SAXException saxExp = new SAXException(exp2.getMessage());
            saxExp.initCause(exp2);
            throw saxExp;
        }
    }
    
    private void updateDVHColumns(final Row curRow) throws MetaDataException, DataAccessException {
        final String tableName = curRow.getTableName();
        final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final List<String> columnNames = tabDef.getColumnNames();
        for (final String columnName : columnNames) {
            if (this.useDVH && XmlDoUtil.checkIfDynamicValueGeneratorExists(tableName + ":" + columnName)) {
                Object columnValue = curRow.get(columnName);
                try {
                    if (columnValue != null) {
                        columnValue = this.getDynamicValue(tableName, columnName, columnValue.toString(), this.tableNameVsRowList);
                    }
                }
                catch (final DynamicValueHandlingException exp) {
                    exp.printStackTrace();
                    final MetaDataException me = new MetaDataException(exp.getMessage());
                    me.initCause(exp.getCause());
                    throw me;
                }
                if (columnValue != null) {
                    try {
                        curRow.set(columnName, XmlDoUtil.convert(columnValue.toString(), curRow.getColumnType(columnName)));
                    }
                    catch (final Exception e) {
                        curRow.set(columnName, columnValue);
                    }
                }
                else {
                    try {
                        curRow.set(columnName, XmlDoUtil.convert((String)curRow.get(columnName), curRow.getColumnType(columnName)));
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        this.dataObject.addRow(curRow);
    }
    
    private void updateFKRefColumns(final Row curRow) throws MetaDataException, DataAccessException {
        final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(curRow.getTableName());
        final List fkList = tabDef.getForeignKeyList();
        if (fkList == null) {
            return;
        }
        for (int i = 0; i < fkList.size(); ++i) {
            final ForeignKeyDefinition fkDef = fkList.get(i);
            final List localColNames = fkDef.getFkColumns();
            final List parColNames = fkDef.getFkRefColumns();
            for (int k = 0; k < localColNames.size(); ++k) {
                String parTableName = fkDef.getMasterTableName();
                String parColName = parColNames.get(k);
                ColumnDefinition colDef = MetaDataUtil.getTableDefinitionByName(parTableName).getColumnDefinitionByName(parColName);
                if (colDef != null) {
                    if (colDef.getRootColumn() != null) {
                        colDef = colDef.getRootColumn();
                    }
                    parTableName = colDef.getTableName();
                    parColName = colDef.getColumnName();
                    final String parColNameInLC = parColName.toLowerCase(Locale.ENGLISH);
                    final UniqueValueGeneration uvg = colDef.getUniqueValueGeneration();
                    final String columnName = localColNames.get(k);
                    Object columnValue = curRow.get(columnName);
                    if (columnValue instanceof String) {
                        if (columnValue.toString().matches(".*:.*:.+")) {
                            if (columnValue.toString().startsWith("::")) {
                                columnValue = parTableName + ":" + parColNameInLC + columnValue.toString().substring(1);
                            }
                            this.setValue(curRow, columnName, columnValue.toString());
                        }
                        else if (uvg != null && uvg.getNameColumn() != null) {
                            columnValue = parTableName + ":" + parColNameInLC + ":" + columnValue;
                            this.setValue(curRow, columnName, columnValue.toString());
                        }
                        else if (uvg != null && this.patternVsValue.get(columnValue) != null) {
                            curRow.set(columnName, this.patternVsValue.get(columnValue.toString()));
                        }
                    }
                }
            }
        }
    }
    
    private void addFKRefColumns(final Row curRow, final boolean isStart) throws MetaDataException {
        final String tableName = curRow.getTableName();
        if (isStart) {
            if (this.parents.size() == 1) {
                return;
            }
            final List masterTableNames = MetaDataUtil.getMasterTableNames(tableName);
            final List processedFK_list = new ArrayList();
            for (int j = 0; j < this.parents.size() - 1; ++j) {
                final Row rowFromStack = (Row)this.parents.get(j);
                final String parTableName = rowFromStack.getTableName();
                if (masterTableNames.contains(parTableName)) {
                    final List fkList = MetaDataUtil.getForeignKeys(parTableName, tableName);
                    for (int l = 0; fkList != null && l < fkList.size(); ++l) {
                        final List parentTableColList = new ArrayList();
                        parentTableColList.add(parTableName);
                        final ForeignKeyDefinition fkDef = fkList.get(l);
                        final List parColNames = fkDef.getFkRefColumns();
                        parentTableColList.addAll(parColNames);
                        final List localColNames = fkDef.getFkColumns();
                        for (int k = 0; k < parColNames.size(); ++k) {
                            final Object value = curRow.get(localColNames.get(k));
                            if ("$NULL$".equals(value)) {
                                curRow.set(localColNames.get(k), null);
                            }
                            else if (value != null && value.toString().startsWith("UVH@")) {
                                if (this.patternVsValue.get(value) != null) {
                                    curRow.set(localColNames.get(k), rowFromStack.get(parColNames.get(k)));
                                }
                            }
                            else if (value == null || (value instanceof String && value.toString().length() == 0)) {
                                if (!processedFK_list.contains(parentTableColList)) {
                                    curRow.set(localColNames.get(k), rowFromStack.get(parColNames.get(k)));
                                }
                            }
                            else {
                                String pattern = value.toString();
                                if (value instanceof String && pattern.matches(".+:.+:.+")) {
                                    if (this.patternVsValue.get(value) != null) {
                                        curRow.set(localColNames.get(k), this.patternVsValue.get(value));
                                    }
                                }
                                else if (value instanceof String && pattern.startsWith("::")) {
                                    pattern = parTableName + ":" + parColNames.get(k).toString().toLowerCase(Locale.ENGLISH) + pattern.substring(1);
                                    if (this.patternVsValue.get(pattern) != null) {
                                        curRow.set(localColNames.get(k), this.patternVsValue.get(pattern));
                                    }
                                    else {
                                        curRow.set(localColNames.get(k), pattern);
                                    }
                                }
                                else if (value.toString().trim().length() > 0) {
                                    final ColumnDefinition colDef = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(localColNames.get(k));
                                    if (colDef.getRootColumn() != null) {
                                        final ColumnDefinition parColDef = colDef.getRootColumn();
                                        final UniqueValueGeneration uvg = parColDef.getUniqueValueGeneration();
                                        if (uvg != null && uvg.getNameColumn() != null) {
                                            continue;
                                        }
                                    }
                                    if (this.useDVH) {
                                        if (XmlDoUtil.checkIfDynamicValueGeneratorExists(tableName + ":" + localColNames.get(k))) {
                                            continue;
                                        }
                                    }
                                    try {
                                        XmlDoUtil.convert(value.toString(), colDef.getDataType());
                                    }
                                    catch (final Exception ex) {
                                        curRow.set(localColNames.get(k), rowFromStack.get(parColNames.get(k)));
                                    }
                                }
                            }
                        }
                        processedFK_list.add(parentTableColList);
                    }
                }
                else if (MetaDataUtil.getTableDefinitionByName(parTableName).hasBDFK()) {
                    final List fkList = MetaDataUtil.getForeignKeys(parTableName, tableName);
                    for (int l = 0; fkList != null && l < fkList.size(); ++l) {
                        final List parentTableColList = new ArrayList();
                        parentTableColList.add(parTableName);
                        final ForeignKeyDefinition fkDef = fkList.get(l);
                        final List parColNames = fkDef.getFkColumns();
                        parentTableColList.addAll(parColNames);
                        final List localColNames = fkDef.getFkRefColumns();
                        for (int k = 0; k < parColNames.size(); ++k) {
                            final Object value = curRow.get(localColNames.get(k));
                            Object parentValue = rowFromStack.get(parColNames.get(k));
                            if (parentValue != null) {
                                if (parentValue instanceof String && parentValue.toString().matches(".+:.+:.+")) {
                                    if (this.patternVsValue.get(value) == null) {
                                        continue;
                                    }
                                    rowFromStack.set(parColNames.get(k), this.patternVsValue.get(parentValue));
                                    parentValue = this.patternVsValue.get(parentValue);
                                }
                                if (value != null && value instanceof UniqueValueHolder) {
                                    curRow.set(localColNames.get(k), parentValue);
                                }
                                else if ((value == null || (value instanceof String && value.toString().length() == 0)) && !processedFK_list.contains(parentTableColList)) {
                                    curRow.set(localColNames.get(k), parentValue);
                                }
                            }
                        }
                        processedFK_list.add(parentTableColList);
                    }
                }
            }
        }
        else {
            final List slaveTableNames = MetaDataUtil.getSlaveTableNames(tableName);
            for (int i = 0; i < this.parents.size(); ++i) {
                final Row rowFromStack2 = (Row)this.parents.get(i);
                final String childTableName = rowFromStack2.getTableName();
                if (slaveTableNames.contains(childTableName)) {
                    final List slaveTableFKList = MetaDataUtil.getForeignKeys(childTableName, tableName);
                    for (int m = 0; slaveTableFKList != null && m < slaveTableFKList.size(); ++m) {
                        final ForeignKeyDefinition slaveTableFKDef = slaveTableFKList.get(m);
                        final List parColNames2 = slaveTableFKDef.getFkRefColumns();
                        final List localColNames2 = slaveTableFKDef.getFkColumns();
                        for (int k2 = 0; k2 < parColNames2.size(); ++k2) {
                            final Object value2 = rowFromStack2.get(localColNames2.get(k2));
                            if (value2 != null && value2.toString().startsWith("UVH@")) {
                                if (this.patternVsValue.get(value2) != null) {
                                    rowFromStack2.set(localColNames2.get(k2), curRow.get(parColNames2.get(k2)));
                                }
                            }
                            else if (value2 == null || (value2 instanceof String && value2.toString().length() == 0)) {
                                rowFromStack2.set(localColNames2.get(k2), curRow.get(parColNames2.get(k2)));
                            }
                            else {
                                String pattern2 = value2.toString();
                                if (value2 instanceof String && pattern2.matches(".+:.+:.+")) {
                                    if (this.patternVsValue.get(value2) != null) {
                                        rowFromStack2.set(localColNames2.get(k2), this.patternVsValue.get(value2));
                                    }
                                }
                                else if (value2 instanceof String && pattern2.startsWith("::")) {
                                    pattern2 = childTableName + ":" + localColNames2.get(k2).toString().toLowerCase(Locale.ENGLISH) + pattern2.substring(1);
                                    if (this.patternVsValue.get(pattern2) != null) {
                                        rowFromStack2.set(localColNames2.get(k2), this.patternVsValue.get(pattern2));
                                    }
                                    else {
                                        rowFromStack2.set(localColNames2.get(k2), pattern2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void addUVHToDataObject() throws DataAccessException {
        if (this.patternVsValue != null && this.patternVsValue.size() > 0) {
            final Iterator iter = this.patternList.iterator();
            final Object fileID = this.getFileID();
            if (fileID != null) {
                while (iter.hasNext()) {
                    final String pattern = iter.next();
                    final Object value = this.patternVsValue.get(pattern);
                    if (value != null && this.userSpecifiedPatternVsValue.get(pattern) == null) {
                        final String[] tableColumn = this.patternVsTableName.get(pattern).split(":");
                        final Row uvhRow = new Row("UVHValues");
                        uvhRow.set(2, fileID);
                        uvhRow.set(3, pattern);
                        uvhRow.set(4, value);
                        uvhRow.set(5, tableColumn[0]);
                        uvhRow.set(6, tableColumn[1]);
                        this.dataObject.addRow(uvhRow);
                    }
                }
            }
        }
    }
    
    private Object getFileID() throws DataAccessException {
        Object id = null;
        try {
            final Table confFile = Table.getTable("ConfFile");
            final SelectQuery sq = new SelectQueryImpl(confFile);
            final Column fileID = Column.getColumn("ConfFile", "FILEID");
            final Column urlCol = Column.getColumn("ConfFile", "URL");
            String urlStr = this.url.toString();
            urlStr = "*" + urlStr.substring(urlStr.lastIndexOf("/conf/"));
            final Criteria urlCr = new Criteria(urlCol, urlStr, 2);
            sq.addSelectColumn(fileID);
            sq.setCriteria(urlCr);
            final DataObject dao = DataAccess.get(sq);
            id = dao.getFirstValue("ConfFile", "FILEID");
        }
        catch (final Exception x) {
            id = null;
        }
        if (id == null && this.dumpUVH && this.url != null) {
            final Row confFileRow = new Row("ConfFile");
            confFileRow.set("URL", this.url.toString());
            this.dataObject.addRow(confFileRow);
            id = confFileRow.get("FILEID");
        }
        return id;
    }
    
    private Object getDynamicValue(final String tableName, final String columnName, final String columnValue, final Map tbNameVsRowList) throws DynamicValueHandlingException, MetaDataException, DataAccessException {
        Object returnValue = null;
        final Object temp = DynamicValueHandlerRepositry.dynamicHandlers.get(tableName + ":" + columnName);
        if (temp != null) {
            final DVHandlerTemplate dvTemplate = (DVHandlerTemplate)temp;
            final DynamicValueHandler handler = dvTemplate.getDynamicValueHandler();
            if (handler != null) {
                returnValue = handler.getColumnValue(dvTemplate.getTableName(), dvTemplate.getColumnName(), dvTemplate.getConfiguredAttributes(), columnValue);
                if (returnValue == null && tbNameVsRowList != null && dvTemplate.getConfiguredAttributes() != null) {
                    final String refTbName = dvTemplate.getConfiguredAttributes().getProperty("referred-table");
                    final List tempList = tbNameVsRowList.get(refTbName);
                    if (tempList == null) {
                        return null;
                    }
                    final DataObject tempDO = new WritableDataObject();
                    for (int i = 0; i < tempList.size(); ++i) {
                        tempDO.addRow(tempList.get(i));
                    }
                    try {
                        handler.set(tempDO);
                        returnValue = handler.getColumnValue(dvTemplate.getTableName(), dvTemplate.getColumnName(), dvTemplate.getConfiguredAttributes(), columnValue);
                    }
                    finally {
                        handler.set(null);
                    }
                }
            }
        }
        XmlParser.LOGGER.log(Level.FINEST, " dynamic column value :: {0}", returnValue);
        return returnValue;
    }
    
    DataObject transform(final String fileName) throws SAXException, IOException, DataAccessException {
        this.url = new File(fileName).toURL();
        return this.transform(this.url);
    }
    
    DataObject transform(final String fileName, final Properties featureProperties) throws SAXException, IOException, DataAccessException {
        this.url = new URL(URLDecoder.decode(new File(fileName).toURI().toURL().toExternalForm(), "UTF-8"));
        return this.transform(this.url, featureProperties);
    }
    
    DataObject transform(final URL url, final Map userSpecifiedPatternVsValue, final boolean dumpUVH) throws SAXException, IOException, DataAccessException {
        this.userSpecifiedPatternVsValue = userSpecifiedPatternVsValue;
        this.dumpUVH = dumpUVH;
        return this.transform(url);
    }
    
    DataObject transform(final URL url, final Map userSpecifiedPatternVsValue, final boolean dumpUVH, final String moduleName) throws SAXException, IOException, DataAccessException {
        this.userSpecifiedPatternVsValue = userSpecifiedPatternVsValue;
        this.dumpUVH = dumpUVH;
        this.moduleName = moduleName;
        return this.transform(url);
    }
    
    DataObject transform(final InputSource input) throws SAXException, IOException, DataAccessException {
        return this.transform(input, null);
    }
    
    DataObject transform(final InputSource input, final URL url, final Map userSpecifiedPatternVsValue, final boolean dumpUVH) throws SAXException, IOException, DataAccessException {
        this.dumpUVH = dumpUVH;
        this.userSpecifiedPatternVsValue = userSpecifiedPatternVsValue;
        this.url = url;
        return this.parse(input, this, this.securityProperties);
    }
    
    DataObject transform(final InputSource input, final Properties securityProperties) throws SAXException, IOException, DataAccessException {
        final String systemId = input.getSystemId();
        this.securityProperties = securityProperties;
        URL inputURL = null;
        if (systemId != null) {
            inputURL = new URL(URLDecoder.decode(new File(systemId).toURI().toURL().toExternalForm(), "UTF-8"));
        }
        return this.transform(input, inputURL, null, false);
    }
    
    DataObject transform(final URL url) throws SAXException, IOException, DataAccessException {
        this.url = url;
        return this.parse(new InputSource(url.toExternalForm()), this, null);
    }
    
    DataObject transform(final URL url, final Properties securityProperties) throws SAXException, IOException, DataAccessException {
        this.url = url;
        return this.parse(new InputSource(url.toExternalForm()), this, securityProperties);
    }
    
    private DataObject parse(final InputSource input, final XmlParser recognizer, final Properties securityProperties) throws SAXException, IOException, DataAccessException {
        this.dataObject = DataAccess.constructDataObject();
        (this.parser = SecurityUtil.getSAXXMLReader(true, true, securityProperties)).setContentHandler(recognizer);
        this.parser.setEntityResolver(recognizer.getDefaultEntityResolver());
        this.parser.setErrorHandler(recognizer.getDefaultErrorHandler());
        this.parser.parse(input);
        return this.dataObject;
    }
    
    protected ErrorHandler getDefaultErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void error(final SAXParseException ex) throws SAXException {
                throw ex;
            }
            
            @Override
            public void fatalError(final SAXParseException ex) throws SAXException {
                throw ex;
            }
            
            @Override
            public void warning(final SAXParseException ex) throws SAXException {
            }
        };
    }
    
    protected EntityResolver getDefaultEntityResolver() {
        return new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
                XmlParser.LOGGER.log(Level.FINE, "xml parser,Entered resolveEntity :: publicId :: [{0}], systemId :: [{1}]", new Object[] { publicId, systemId });
                if (publicId != null) {
                    throw new SAXException("Trying to parse a PUBLIC entity");
                }
                if (systemId == null) {
                    throw new SAXException("SystemID cannot be [null]");
                }
                if (systemId.indexOf("http:") >= 0) {
                    throw new SAXException("Invalid Entity ::[" + systemId + "]");
                }
                if (!systemId.endsWith(".xml")) {
                    throw new SAXException(" URL :: [" + systemId + "] is not an xml file");
                }
                return null;
            }
        };
    }
    
    public static DataObject check(final int option, URL url, Map patterns) {
        try {
            if (patterns == null) {
                patterns = new HashMap();
                patterns.put("StudentDetails:id:saran", "1");
                patterns.put("StudentDetails:id:babu", "2");
            }
            final XmlParser xml2do = new XmlParser();
            if (url == null) {
                url = new File("/home/saranbabu/do-xml.xml").toURL();
            }
            if (option == 1) {
                return xml2do.transform(url, patterns, true);
            }
            if (option == 2) {
                return xml2do.transform(url, patterns, false);
            }
            return xml2do.transform(url);
        }
        catch (final Exception sax) {
            sax.printStackTrace();
            return null;
        }
    }
    
    private void warning(final String message) {
        if (this.developmentMode) {
            ConsoleOut.println(message);
        }
        XmlParser.LOGGER.warning(message);
    }
    
    static {
        LOGGER = Logger.getLogger(XmlParser.class.getName());
    }
}
