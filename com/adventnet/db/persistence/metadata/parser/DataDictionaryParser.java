package com.adventnet.db.persistence.metadata.parser;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.io.File;
import java.io.InputStream;
import org.xml.sax.EntityResolver;
import com.adventnet.db.persistence.metadata.MetaDataEntityResolver;
import org.xml.sax.ErrorHandler;
import com.adventnet.db.persistence.metadata.MetaDataErrorHandler;
import java.util.Properties;
import java.net.URL;
import com.adventnet.mfw.ConsoleOut;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Collection;
import java.util.Collections;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.logging.Level;
import java.util.Locale;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import com.adventnet.persistence.util.DCManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.zoho.conf.AppResources;
import com.adventnet.db.persistence.metadata.DataDictionary;
import org.w3c.dom.Document;
import java.util.List;
import java.util.logging.Logger;

public class DataDictionaryParser
{
    private static final Logger LOGGER;
    private static boolean onSAS;
    private static List nonUVGTables;
    Document document;
    private String url;
    DataDictionary dd;
    private DDErrorInfo errorInfo;
    private boolean skipException;
    private String exceptionMsg;
    private String warningMsg;
    private String searchPattern;
    private boolean developmentMode;
    
    public DataDictionaryParser(final Document document) {
        this.url = null;
        this.dd = null;
        this.skipException = false;
        this.exceptionMsg = null;
        this.warningMsg = null;
        this.searchPattern = null;
        this.developmentMode = AppResources.getString("development.mode", "false").equalsIgnoreCase("true");
        this.document = document;
    }
    
    public DataDictionaryParser(final Document document, final String url) {
        this(document, url, false);
    }
    
    public DataDictionaryParser(final Document document, final String url, final boolean skipException) {
        this.url = null;
        this.dd = null;
        this.skipException = false;
        this.exceptionMsg = null;
        this.warningMsg = null;
        this.searchPattern = null;
        this.developmentMode = AppResources.getString("development.mode", "false").equalsIgnoreCase("true");
        this.document = document;
        this.url = url;
        this.skipException = skipException;
        this.errorInfo = new DDErrorInfo();
    }
    
    private DataDictionaryParser(final Document document, final boolean skipException) throws MetaDataException {
        this.url = null;
        this.dd = null;
        this.skipException = false;
        this.exceptionMsg = null;
        this.warningMsg = null;
        this.searchPattern = null;
        this.developmentMode = AppResources.getString("development.mode", "false").equalsIgnoreCase("true");
        this.document = document;
        this.skipException = skipException;
        this.errorInfo = new DDErrorInfo();
    }
    
    public static void setValueForOnSAS(final boolean checkBasedOnSAS) {
        DataDictionaryParser.onSAS = checkBasedOnSAS;
    }
    
    public static void setTablesWithoutUVGColsInPK(final List tableNameList) {
        DataDictionaryParser.nonUVGTables = tableNameList;
    }
    
    public static boolean canBeIgnored(final String tableName) {
        return !DataDictionaryParser.onSAS || DataDictionaryParser.nonUVGTables.contains(tableName);
    }
    
    void removeUniquePK(final TableDefinition tabDef) throws MetaDataException {
        final PrimaryKeyDefinition pkDef = tabDef.getPrimaryKey();
        final List colList = pkDef.getColumnList();
        final List uniquKeys = tabDef.getUniqueKeys();
        if (uniquKeys != null) {
        Label_0202:
            for (int index = 0; index < uniquKeys.size(); ++index) {
                final UniqueKeyDefinition unq = uniquKeys.get(index);
                final List columns = unq.getColumns();
                if (columns != null && columns.size() == colList.size()) {
                    final Iterator iterator = colList.iterator();
                    final boolean matched = false;
                    while (iterator.hasNext()) {
                        if (!columns.contains(iterator.next())) {
                            continue Label_0202;
                        }
                    }
                    this.warningMsg = "A column cannot have a unique constraint as true, if it alone participates in the PKDefinition of that tableDefinition. TableName :: [" + pkDef.getTableName() + "], columnNames :: [" + columns + "]";
                    if (this.skipException) {
                        this.errorInfo.addWarning(this.warningMsg, tabDef, 2, null);
                    }
                    else {
                        this.warning(this.warningMsg);
                    }
                    tabDef.removeUniqueKey(unq.getName());
                }
            }
        }
    }
    
    public void parseDocument() throws MetaDataException {
        this.parseDocument(null);
    }
    
    public void parseDocument(final DataDictionary newDD) throws MetaDataException {
        final Element element = this.document.getDocumentElement();
        if (element != null && element.getTagName().equals("data-dictionary")) {
            this.dd = newDD;
            this.getDataDictionary(element);
        }
    }
    
    public DataDictionary getDataDictionary() {
        return this.dd;
    }
    
    DataDictionary getDataDictionary(final Element element) throws MetaDataException {
        final NamedNodeMap attrs = element.getAttributes();
        String name = null;
        String templateMetaHandler = null;
        String dcType = null;
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                name = attr.getValue();
            }
            else if (attr.getName().equals("template-meta-handler")) {
                if (this.dd != null && this.dd.getTemplateMetaHandler() != null) {
                    throw new MetaDataException("Already a template-meta-handler is defined for the module:" + this.dd.getName());
                }
                templateMetaHandler = attr.getValue();
            }
            else if (attr.getName().equals("dc-type")) {
                dcType = attr.getValue();
                if (this.dd != null && this.dd.getDynamicColumnType() != null && !this.dd.getDynamicColumnType().equals(dcType)) {
                    throw new MetaDataException("Already a dc-type is defined for the module:" + this.dd.getName());
                }
                if (!DCManager.getDCTypes().contains(dcType)) {
                    throw new MetaDataException("dc-type not defined in dynamic-column-types.props");
                }
            }
        }
        if (this.dd == null) {
            this.dd = new DataDictionary(name, this.url, templateMetaHandler, dcType);
        }
        if (this.url != null) {
            DataDictionaryParser.LOGGER.info("Parsing the File :: " + this.url);
        }
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("description")) {
                        final String description = ParserUtil.getTextNodeVal(nodeElement);
                        this.dd.setDescription(description);
                        break;
                    }
                    if (tagName.equals("table")) {
                        final TableDefinition td = this.getTableDefinition(nodeElement);
                        this.removeUniquePK(td);
                        this.dd.addTableDefinition(td);
                        td.setModuleName(this.dd.getName());
                        break;
                    }
                    break;
                }
            }
        }
        return this.dd;
    }
    
    TableDefinition getTableDefinition(final Element element) throws MetaDataException {
        final NamedNodeMap attrs = element.getAttributes();
        final Attr sys = (Attr)attrs.getNamedItem("system");
        final Attr creatable = (Attr)attrs.getNamedItem("createtable");
        final boolean isSystem = Boolean.getBoolean(sys.getValue());
        final Attr dirtyWriteCheck = (Attr)attrs.getNamedItem("dirty-write-check-columns");
        TableDefinition td = new TableDefinition(isSystem, Boolean.valueOf(creatable.getValue()));
        if (dirtyWriteCheck != null) {
            td = new TableDefinition(isSystem, Boolean.valueOf(creatable.getValue()), dirtyWriteCheck.getValue());
        }
        else {
            td = new TableDefinition(isSystem, Boolean.valueOf(creatable.getValue()));
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                final String name = attr.getValue();
                final int nameLength = name.length();
                if (nameLength > MetaDataUtil.DBOBJECT_NAMELENGTH) {
                    this.exceptionMsg = "The tableName [" + name + "] has [" + nameLength + "] characters but it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".";
                    if (!this.skipException) {
                        throw new MetaDataException(this.exceptionMsg);
                    }
                    this.searchPattern = "^.*<table \\s*name\\s*=\\s*\"" + name + "\".*$";
                    final TableDefinition td2 = new TableDefinition();
                    td2.setTableName(name);
                    this.errorInfo.addException(this.exceptionMsg, td2, 1, this.searchPattern);
                }
                td.setTableName(name);
            }
            else if (attr.getName().equals("display-name")) {
                final String name = attr.getValue();
                td.setDisplayName(name);
            }
            else if (attr.getName().equals("template")) {
                td.setTemplate(Boolean.parseBoolean(attr.getValue()));
            }
            else if (attr.getName().equals("template-instance-pattern")) {
                td.setTemplateInstancePatternName(attr.getValue());
            }
            else if (attr.getName().equals("dc-type")) {
                final String dcType = attr.getValue();
                if (!dcType.equalsIgnoreCase("nodc") && !DCManager.getDCTypes().contains(dcType)) {
                    throw new MetaDataException("dc-type :: " + dcType + " not defined in dynamic-column-types.props");
                }
                td.setDynamicColumnType(dcType);
            }
        }
        if (td.isTemplate() && td.getDynamicColumnType() != null) {
            throw new MetaDataException("Template table cannot have dynamic column type");
        }
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("description")) {
                        final String description = ParserUtil.getTextNodeVal(nodeElement);
                        td.setDescription(description);
                        break;
                    }
                    if (tagName.equals("columns")) {
                        this.parseColumnDefinitions(td, nodeElement);
                        break;
                    }
                    if (tagName.equals("primary-key")) {
                        final PrimaryKeyDefinition pkd = this.getPrimaryKey(td, nodeElement);
                        pkd.setTableName(td.getTableName());
                        td.setPrimaryKey(pkd);
                        break;
                    }
                    if (tagName.equals("foreign-keys")) {
                        this.parseForeignKeys(td, nodeElement);
                        break;
                    }
                    if (tagName.equals("unique-keys")) {
                        this.parseUniqueKeys(td, nodeElement);
                        break;
                    }
                    if (tagName.equals("indexes")) {
                        this.parseIndexes(td, nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
        final List<String> colNames = td.getColumnNames();
        final Iterator colIterator = colNames.iterator();
        while (colIterator.hasNext()) {
            final ColumnDefinition cd = td.getColumnDefinitionByName(colIterator.next());
            final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
            if (uvg != null) {
                final String nameColumn = uvg.getNameColumn();
                if (nameColumn != null) {
                    if (!colNames.contains(nameColumn)) {
                        this.exceptionMsg = "No Such Column [" + nameColumn + "] Specified in this TableDefinition :: [" + td.getTableName() + "] but specified in the UniqueValueGeneration.";
                        if (!this.skipException) {
                            throw new MetaDataException(this.exceptionMsg);
                        }
                        this.searchPattern = "^.*<name-column>" + nameColumn + ".*$";
                        this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern);
                    }
                    boolean valid = false;
                    final List uniqueKeys = td.getUniqueKeys();
                    if (uniqueKeys != null) {
                        for (int k = 0; k < uniqueKeys.size(); ++k) {
                            final UniqueKeyDefinition ukDef = uniqueKeys.get(k);
                            final List ukCols = ukDef.getColumns();
                            if (ukCols.size() == 1 && ukCols.get(0).equals(nameColumn)) {
                                valid = true;
                                break;
                            }
                        }
                    }
                    if (!valid) {
                        this.exceptionMsg = "Column [" + nameColumn + "] of the table [" + td.getTableName() + "], which is specified as NAME_COLUMN should be UNIQUE.";
                        if (!this.skipException) {
                            throw new MetaDataException(this.exceptionMsg);
                        }
                        this.searchPattern = "^.*<name-column>" + nameColumn + ".*$";
                        this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern);
                    }
                    break;
                }
                break;
            }
        }
        return td;
    }
    
    void parseColumnDefinitions(final TableDefinition td, final Element element) throws MetaDataException {
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("column")) {
                        final ColumnDefinition cd = this.getColumnDefinition(td, nodeElement);
                        td.addColumnDefinition(cd);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    ColumnDefinition getColumnDefinition(final TableDefinition td, final Element element) throws MetaDataException {
        final ColumnDefinition cd = new ColumnDefinition();
        cd.setTableName(td.getTableName());
        final NamedNodeMap attrs = element.getAttributes();
        this.searchPattern = "^.*<column\\s*name\\s*=\\s*\"";
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                final String name = attr.getValue();
                final ColumnDefinition oldCd = td.getColumnDefinitionByName(name);
                if (oldCd != null) {
                    this.exceptionMsg = "Column with name " + name + " is already defined in table " + td.getTableName() + " . Please use unique column names within a table definition";
                    if (!this.skipException) {
                        throw new MetaDataException(this.exceptionMsg);
                    }
                    this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + name + "\".*$");
                }
                final int nameLength = name.length();
                if (nameLength > MetaDataUtil.DBOBJECT_NAMELENGTH) {
                    this.exceptionMsg = "The columnName :: [" + name + "] of tableName :: [" + td.getTableName() + "] has [" + nameLength + "] characters but it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".";
                    if (!this.skipException) {
                        this.warning(this.exceptionMsg);
                        throw new MetaDataException("The columnName :: [" + name + "] of tableName :: [" + td.getTableName() + "] has [" + nameLength + "] characters but it should not exceed " + MetaDataUtil.DBOBJECT_NAMELENGTH + ".");
                    }
                    this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + name + "\".*$");
                }
                if (name.equalsIgnoreCase("DYJSONCOL")) {
                    throw new IllegalArgumentException("Column Name cannot be DYJSONCOL. This name is used for internal purpose");
                }
                cd.setColumnName(name);
            }
            else if (attr.getName().equals("display-name")) {
                final String name = attr.getValue();
                cd.setDisplayName(name);
            }
        }
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("description")) {
                        final String description = ParserUtil.getTextNodeVal(nodeElement);
                        cd.setDescription(description);
                        break;
                    }
                    if (tagName.equals("data-type")) {
                        final String dataType = this.getDataType(nodeElement);
                        try {
                            cd.setDataType(dataType);
                            final int sqlType = QueryUtil.getJavaSQLType(dataType);
                            cd.setSQLType(sqlType);
                        }
                        catch (final IllegalArgumentException e) {
                            if (e.getMessage().toLowerCase(Locale.ENGLISH).contains("unknown data type")) {
                                if (AppResources.getString("ddparser.validate.datatype", "true").equals("true")) {
                                    if (!this.skipException) {
                                        throw e;
                                    }
                                    final String pattern = "^.*<data-type>" + dataType + ".*$";
                                    this.errorInfo.addException(e.getMessage(), td, 1, pattern);
                                }
                                else {
                                    DataDictionaryParser.LOGGER.log(Level.SEVERE, e.getMessage());
                                    DataDictionaryParser.LOGGER.log(Level.FINE, "Exception: " + e);
                                }
                            }
                            if (!this.skipException) {
                                throw e;
                            }
                            final String pattern = "^.*<data-type>" + dataType + ".*$";
                            this.errorInfo.addException(e.getMessage(), td, 1, pattern);
                        }
                        break;
                    }
                    if (tagName.equals("precision")) {
                        String dataType = cd.getDataType();
                        if (DataTypeUtil.isEDT(dataType)) {
                            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
                        }
                        if (!dataType.equals("FLOAT") && !dataType.equals("DOUBLE") && !dataType.equals("DECIMAL")) {
                            this.exceptionMsg = "Precision can be set only for FLOAT/DOUBLE/DECIMAL Datatypes - Here the datatype is [" + cd.getDataType() + "] for the column [" + td.getTableName() + "." + cd.getColumnName() + "]";
                            if (!this.skipException) {
                                throw new MetaDataException(this.exceptionMsg);
                            }
                            this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                        }
                        if (!ParserUtil.getTextNodeVal(nodeElement).equalsIgnoreCase("") && nodeElement != null) {
                            final int precision = this.getPrecision(nodeElement);
                            if (precision < 0) {
                                this.exceptionMsg = "precision cannot have negative value. Check [" + td.getTableName() + "." + cd.getColumnName() + "]";
                                if (!this.skipException) {
                                    throw new MetaDataException(this.exceptionMsg);
                                }
                                this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                            }
                            if (cd.getMaxLength() < precision + 2) {
                                this.exceptionMsg = "max-size for the column [" + td.getTableName() + "." + cd.getColumnName() + "] should be more than [" + (precision + 2) + "] when its precision is [" + precision + "].";
                                if (!this.skipException) {
                                    throw new MetaDataException(this.exceptionMsg);
                                }
                                this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                            }
                            cd.setPrecision(precision);
                        }
                        break;
                    }
                    if (tagName.equals("max-size")) {
                        if (!"".equalsIgnoreCase(ParserUtil.getTextNodeVal(nodeElement)) && nodeElement != null) {
                            final int maxLength = this.getMaxLength(nodeElement);
                            cd.setMaxLength(maxLength);
                            break;
                        }
                        break;
                    }
                    else {
                        if (tagName.equals("default-value")) {
                            final String defVal = this.getDefaultValue(nodeElement);
                            cd.setDefaultValue(defVal);
                            break;
                        }
                        if (tagName.equals("nullable")) {
                            final String nullableValue = ParserUtil.getTextNodeVal(nodeElement);
                            if (nullableValue != null && (nullableValue.trim().equalsIgnoreCase("true") || nullableValue.trim().equalsIgnoreCase("false"))) {
                                final boolean nullable = this.isNullable(nodeElement);
                                cd.setNullable(nullable);
                            }
                            else {
                                this.exceptionMsg = "Invalid value \"" + nullableValue + "\" defined for <nullable> attribute for \"" + td.getTableName() + "\".\"" + cd.getColumnName() + "\" column. It has to be either 'true' or 'false'.";
                                if (!this.skipException) {
                                    throw new MetaDataException(this.exceptionMsg);
                                }
                                this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                            }
                            break;
                        }
                        if (tagName.equals("unique")) {
                            final boolean unique = this.isUnique(nodeElement);
                            if (unique) {
                                this.warningMsg = "Don't use unique true tag instead use unique-keys tag for column [" + cd.getColumnName() + "] in table [" + td.getTableName() + "]";
                                if (this.skipException) {
                                    this.errorInfo.addWarning(this.warningMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                                }
                                else {
                                    this.warning(this.warningMsg);
                                }
                                final UniqueKeyDefinition ukd = new UniqueKeyDefinition();
                                final List ukList = td.getUniqueKeys();
                                final int ukNo = (ukList == null) ? 0 : ukList.size();
                                ukd.setName(td.getTableName() + "_UK" + String.valueOf(ukNo));
                                ukd.addColumn(cd.getColumnName());
                                td.addUniqueKey(ukd);
                            }
                            cd.setUnique(unique);
                            break;
                        }
                        if (tagName.equals("allowed-values")) {
                            final AllowedValues av = this.getAllowedValues(nodeElement, cd);
                            cd.setAllowedValues(av);
                            break;
                        }
                        if (tagName.equals("uniquevalue-generation")) {
                            final UniqueValueGeneration generation = this.getUniqueValueGeneration(nodeElement);
                            if (td.isTemplate()) {
                                generation.setGeneratorType(2);
                            }
                            cd.setUniqueValueGeneration(generation);
                            break;
                        }
                        break;
                    }
                    break;
                }
            }
        }
        final String maxSize = MetaDataUtil.getAttribute(td.getTableName() + "." + cd.getColumnName() + ".maxsize");
        if (maxSize != null && !maxSize.isEmpty()) {
            final int extMaxLength = Integer.parseInt(maxSize);
            final int maxLength2 = cd.getMaxLength();
            if (cd.getMaxLength() != 0) {
                if ((extMaxLength != -1 && maxLength2 != -1 && extMaxLength < maxLength2) || (maxLength2 == -1 && extMaxLength > 0)) {
                    throw new MetaDataException("max-size provided for column [" + td.getTableName() + "." + cd.getColumnName() + "] in extended-dd.conf is less than the value provided in data-dictionary.xml");
                }
                if ((extMaxLength != -1 && maxLength2 != -1 && extMaxLength > maxLength2) || extMaxLength == -1) {
                    cd.setMaxLength(extMaxLength);
                }
            }
            else {
                DataDictionaryParser.LOGGER.info("max-lenth of this column " + cd.getTableName() + "." + cd.getColumnName() + " is undefined in data-dictionary ");
                cd.setMaxLength(extMaxLength);
            }
        }
        final String extendedDefaultValue = MetaDataUtil.getAttribute(td.getTableName() + "." + cd.getColumnName() + ".defaultvalue");
        if (extendedDefaultValue != null) {
            cd.setDefaultValue(extendedDefaultValue);
        }
        return cd;
    }
    
    String getDataType(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    int getPrecision(final Element element) {
        return ParserUtil.getTextNodeValAsInt(element);
    }
    
    int getMaxLength(final Element element) {
        return ParserUtil.getTextNodeValAsInt(element);
    }
    
    String getDefaultValue(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    boolean isNullable(final Element element) {
        return ParserUtil.getTextNodeValAsBoolean(element);
    }
    
    boolean isUnique(final Element element) {
        return ParserUtil.getTextNodeValAsBoolean(element);
    }
    
    AllowedValues getAllowedValues(final Element element, final ColumnDefinition cd) throws MetaDataException {
        final String dataType = cd.getDataType();
        final AllowedValues av = new AllowedValues();
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("from")) {
                        final String fromVal = this.getFrom(nodeElement);
                        final Object setVal = MetaDataUtil.convert(fromVal, dataType);
                        av.setFromVal(setVal);
                        break;
                    }
                    if (tagName.equals("to")) {
                        final String toVal = this.getTo(nodeElement);
                        final Object setVal = MetaDataUtil.convert(toVal, dataType);
                        av.setToVal(setVal);
                        break;
                    }
                    if (tagName.equals("value")) {
                        final String value = this.getValue(nodeElement);
                        final Object setVal = MetaDataUtil.convert(value, dataType);
                        av.addValue(setVal);
                        break;
                    }
                    if (tagName.equals("pattern")) {
                        final String pattern = this.getPattern(nodeElement);
                        av.setPattern(pattern);
                        break;
                    }
                    break;
                }
            }
        }
        return av;
    }
    
    String getFrom(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    String getTo(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    String getValue(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    String getPattern(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    UniqueKeyDefinition getUniqueKey(final TableDefinition td, final Element element) throws MetaDataException {
        UniqueKeyDefinition ukd = null;
        final NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                final String name = attr.getValue();
                ukd = new UniqueKeyDefinition();
                ukd.setName(name);
            }
        }
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("unique-key-column")) {
                        final String columnName = ParserUtil.getTextNodeVal(nodeElement);
                        ukd.addColumn(columnName);
                        break;
                    }
                    break;
                }
            }
        }
        try {
            ParserUtil.validateUniqueKey(td, ukd, null);
        }
        catch (final IllegalArgumentException e) {
            this.exceptionMsg = e.getMessage();
            if (!this.skipException) {
                throw e;
            }
            this.searchPattern = "^.*name\\s*=\\s*\"" + ukd.getName() + "\".*$";
            this.errorInfo.addException(this.exceptionMsg, td, 2, this.searchPattern);
        }
        return ukd;
    }
    
    IndexDefinition getIndex(final TableDefinition td, final Element element) throws MetaDataException {
        IndexDefinition ikd = null;
        final NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                final String name = attr.getValue();
                ikd = new IndexDefinition();
                ikd.setName(name);
            }
        }
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("index-column")) {
                        final String columnName = ParserUtil.getTextNodeVal(nodeElement);
                        final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
                        int indexSize = -1;
                        Boolean isNullsFirst = null;
                        Boolean isAscending = true;
                        final NamedNodeMap attributes = nodeElement.getAttributes();
                        for (int k = 0; k < attributes.getLength(); ++k) {
                            final Attr attr2 = (Attr)attributes.item(k);
                            if (attr2.getName().equals("size")) {
                                final String size = attr2.getValue();
                                if (size != null) {
                                    final String dataType = cd.getDataType();
                                    final DataTypeDefinition udt = DataTypeManager.getDataTypeDefinition(dataType);
                                    final boolean isUDT = udt != null && udt.getMeta() != null;
                                    if ((isUDT && !udt.getMeta().isPartialIndexSupported()) || (!isUDT && !dataType.equals("CHAR"))) {
                                        this.exceptionMsg = "Partial Indexing is not supported for this column [" + cd.getTableName() + "." + cd.getColumnName() + "]  type ";
                                        if (!this.skipException) {
                                            throw new MetaDataException(this.exceptionMsg);
                                        }
                                        this.searchPattern = "^.*<index-column>\\s*" + columnName + ".*$";
                                        this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                                    }
                                    final int sizeOfIndex = Integer.parseInt(size);
                                    if (sizeOfIndex < cd.getMaxLength()) {
                                        indexSize = sizeOfIndex;
                                    }
                                    else {
                                        this.exceptionMsg = "Column [" + cd.getTableName() + "." + cd.getColumnName() + "] cannot have index size [" + size + "] greater than actual size [" + cd.getMaxLength() + "] of column ";
                                        if (!this.skipException) {
                                            throw new IllegalArgumentException(this.exceptionMsg);
                                        }
                                        this.searchPattern = "^.*<index-column>\\s*" + columnName + ".*$";
                                        this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                                    }
                                }
                                else {
                                    this.exceptionMsg = "Index size of Column [" + cd.getTableName() + "." + cd.getColumnName() + "] cannot be null";
                                    if (!this.skipException) {
                                        throw new IllegalArgumentException(this.exceptionMsg);
                                    }
                                    this.searchPattern = "^.*<index-column>\\s*" + columnName + ".*$";
                                    this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern + cd.getColumnName() + "\".*$");
                                }
                            }
                            if (attr2.getName().equals("isAscending")) {
                                final String order = attr2.getValue();
                                isAscending = Boolean.valueOf(order);
                            }
                            if (attr2.getName().equals("isNullsFirst")) {
                                final String nullsOrder = attr2.getValue();
                                if (nullsOrder == null) {
                                    isNullsFirst = null;
                                }
                                else {
                                    isNullsFirst = Boolean.valueOf(nullsOrder);
                                }
                            }
                        }
                        final IndexColumnDefinition icd = new IndexColumnDefinition(cd, indexSize, isAscending, isNullsFirst);
                        ikd.addIndexColumnDefinition(icd);
                        break;
                    }
                    break;
                }
            }
        }
        return ikd;
    }
    
    PrimaryKeyDefinition getPrimaryKey(final TableDefinition td, final Element element) throws MetaDataException {
        PrimaryKeyDefinition pkd = null;
        final NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (attr.getName().equals("name")) {
                final String name = attr.getValue();
                pkd = new PrimaryKeyDefinition();
                pkd.setTableName(td.getTableName());
                pkd.setName(name);
            }
        }
        final NodeList nodes = element.getChildNodes();
        final int length = nodes.getLength();
        boolean pkHasNoBIGINT_UVGCol = true;
        for (int j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("primary-key-column")) {
                        final String columnName = this.getPrimaryKeyColumn(nodeElement);
                        final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
                        if (cd == null) {
                            this.exceptionMsg = "UnKnown Column Name specified " + columnName + " in Primary Key definition for " + td.getTableName();
                            if (!this.skipException) {
                                throw new MetaDataException(this.exceptionMsg);
                            }
                            this.searchPattern = "^.*<primary-key-column>\\s*" + columnName + ".*$";
                            this.errorInfo.addException(this.exceptionMsg, td, 1, this.searchPattern);
                        }
                        else if (cd.getDataType().equals("BIGINT")) {
                            pkHasNoBIGINT_UVGCol = false;
                        }
                        pkd.addColumnName(columnName);
                        break;
                    }
                    break;
                }
            }
        }
        if (pkHasNoBIGINT_UVGCol && !canBeIgnored(td.getTableName())) {
            this.warningMsg = "TableName :: [" + td.getTableName() + "] has no BIGINT Column in its PK.";
            if (this.skipException) {
                this.searchPattern = "<primary-key\\s*name\\s*=\\s*\"" + pkd.getName() + "\".*$";
                this.errorInfo.addWarning(this.warningMsg, td, 1, this.searchPattern);
            }
            else {
                this.warning(this.warningMsg);
            }
        }
        try {
            ParserUtil.validatePrimaryKey(td, pkd, null);
        }
        catch (final IllegalArgumentException e) {
            this.exceptionMsg = e.getMessage();
            if (!this.skipException) {
                throw e;
            }
            this.errorInfo.addException(this.exceptionMsg, td, 2, null);
        }
        return pkd;
    }
    
    String getPrimaryKeyColumn(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    void parseForeignKeys(final TableDefinition td, final Element element) throws MetaDataException {
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("foreign-key")) {
                        final ForeignKeyDefinition fkd = this.getForeignKey(td, nodeElement);
                        td.addForeignKey(fkd);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void parseUniqueKeys(final TableDefinition td, final Element element) throws MetaDataException {
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("unique-key")) {
                        final UniqueKeyDefinition ukd = this.getUniqueKey(td, nodeElement);
                        td.addUniqueKey(ukd);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void parseIndexes(final TableDefinition td, final Element element) throws MetaDataException {
        final NodeList nodes = element.getChildNodes();
        final int length = nodes.getLength();
        this.searchPattern = "<index\\s*name\\s*=\\s*\"";
        for (int i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("index")) {
                        final IndexDefinition id = this.getIndex(td, nodeElement);
                        final String tableName = td.getTableName();
                        final String indexName = id.getName();
                        final List columns = id.getColumns();
                        final PrimaryKeyDefinition pkd = td.getPrimaryKey();
                        final List pkColumns = pkd.getColumnList();
                        boolean addThisIndex = true;
                        if (columns.equals(pkColumns)) {
                            this.warningMsg = "Ignoring the index definition [" + indexName + "] for table [" + tableName + "] as it redefines the Primary Keys";
                            if (this.skipException) {
                                this.errorInfo.addWarning(this.warningMsg, td, 2, this.searchPattern + indexName + "\".*$");
                            }
                            else {
                                this.warning(this.warningMsg);
                            }
                            addThisIndex = false;
                        }
                        else {
                            final List uniqueKeys = td.getUniqueKeys();
                            if (uniqueKeys != null) {
                                for (int uqSize = uniqueKeys.size(), u = 0; u < uqSize; ++u) {
                                    final UniqueKeyDefinition ukd = uniqueKeys.get(u);
                                    final List ukCols = ukd.getColumns();
                                    if (columns.equals(ukCols)) {
                                        this.warningMsg = "Ignoring the index definition [" + indexName + "] for table [" + tableName + "] as it redefines the Unique Key [" + ukd.getName() + "]";
                                        if (this.skipException) {
                                            this.errorInfo.addWarning(this.warningMsg, td, 2, this.searchPattern + indexName + "\".*$");
                                        }
                                        else {
                                            this.warning(this.warningMsg);
                                        }
                                        addThisIndex = false;
                                        break;
                                    }
                                }
                            }
                            final List fks = td.getForeignKeyList();
                            if (fks != null) {
                                for (int fkSize = fks.size(), f = 0; f < fkSize; ++f) {
                                    final ForeignKeyDefinition fkd = fks.get(f);
                                    final List fkCols = fkd.getFkColumns();
                                    if (columns.equals(fkCols)) {
                                        this.warningMsg = "Ignoring the index definition [" + indexName + "] for table [" + tableName + "] as it redefines the Foreign Key [" + fkd.getName() + "]";
                                        if (this.skipException) {
                                            this.errorInfo.addWarning(this.warningMsg, td, 2, this.searchPattern + indexName + "\".*$");
                                        }
                                        else {
                                            this.warning(this.warningMsg);
                                        }
                                        addThisIndex = false;
                                        break;
                                    }
                                }
                            }
                            final List ids = td.getIndexes();
                            if (ids != null) {
                                final int idSize = ids.size();
                                int ii = 0;
                                while (ii < idSize) {
                                    final IndexDefinition iDef = ids.get(ii);
                                    final List idCols = iDef.getColumns();
                                    if (columns.equals(idCols)) {
                                        if (iDef.isPartialIndex() && !id.isPartialIndex()) {
                                            DataDictionaryParser.LOGGER.log(Level.WARNING, "Ignoring the partial index [{0}] for table [{1}] as it redefines the Index [{2}]", new Object[] { iDef.getName(), tableName, indexName });
                                            td.removeIndex(iDef.getName());
                                            addThisIndex = true;
                                            break;
                                        }
                                        if (!iDef.isPartialIndex() && id.isPartialIndex()) {
                                            DataDictionaryParser.LOGGER.log(Level.WARNING, "Ignoring the partial index [{0}] for table [{1}] as it redefines the Index [{2}]", new Object[] { indexName, tableName, iDef.getName() });
                                            addThisIndex = false;
                                            break;
                                        }
                                        this.warningMsg = "Ignoring the index definition [" + indexName + "] for table [" + tableName + "] as it redefines the Index [" + iDef.getName() + "]";
                                        if (this.skipException) {
                                            this.errorInfo.addWarning(this.warningMsg, td, 2, this.searchPattern + indexName + "\".*$");
                                        }
                                        else {
                                            this.warning(this.warningMsg);
                                        }
                                        DataDictionaryParser.LOGGER.log(Level.WARNING, "Ignoring the index definition [{0}] for table [{1}] as it redefines the Index [{2}]", new Object[] { indexName, tableName, iDef.getName() });
                                        addThisIndex = false;
                                        break;
                                    }
                                    else {
                                        ++ii;
                                    }
                                }
                            }
                        }
                        if (addThisIndex) {
                            td.addIndex(id);
                        }
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    ForeignKeyDefinition getForeignKey(final TableDefinition td, final Element element) throws MetaDataException {
        ForeignKeyDefinition fk = null;
        TableDefinition rtd = null;
        List pkColsOfMasterTable = null;
        final NamedNodeMap attrs = element.getAttributes();
        final Attr nameAttr = (Attr)attrs.getNamedItem("name");
        if (nameAttr == null) {
            this.exceptionMsg = "Atleast one foreign key definition in the table " + td.getTableName() + " does not have name attribute";
            if (!this.skipException) {
                throw new MetaDataException(this.exceptionMsg);
            }
            this.errorInfo.addException(this.exceptionMsg, td, 2, null);
        }
        final String name = (nameAttr != null) ? nameAttr.getValue() : "";
        fk = new ForeignKeyDefinition();
        fk.setName(name);
        fk.setSlaveTableName(td.getTableName());
        for (int i = 0; i < attrs.getLength(); ++i) {
            final Attr attr = (Attr)attrs.item(i);
            if (!attr.getName().equals("name")) {
                String refTableName = null;
                if (attr.getName().equals("reference-table-name")) {
                    refTableName = attr.getValue();
                    fk.setMasterTableName(refTableName);
                    rtd = this.dd.getTableDefinitionByName(refTableName);
                    if (rtd == null) {
                        if (refTableName.equals(td.getTableName())) {
                            rtd = td;
                        }
                        else {
                            rtd = MetaDataUtil.getTableDefinitionByName(refTableName);
                        }
                    }
                    if (rtd == null) {
                        this.exceptionMsg = "The Foreign-key \"" + fk.getName() + "\" in the table \"" + td.getTableName() + "\" is Referring a non-existing tableName :: \"" + refTableName + "\"";
                        if (!this.skipException) {
                            throw new MetaDataException(this.exceptionMsg);
                        }
                        this.searchPattern = "^.*name\\s*=\\s*\"" + fk.getName() + "\".*$";
                        this.errorInfo.addException(this.exceptionMsg, td, 2, this.searchPattern);
                    }
                    else {
                        pkColsOfMasterTable = rtd.getPrimaryKey().getColumnList();
                    }
                }
                if (attr.getName().equals("isbidirectional")) {
                    final String biDirStr = attr.getValue();
                    final boolean bidirectional = biDirStr.equalsIgnoreCase("true");
                    fk.setBidirectional(bidirectional);
                }
            }
        }
        List fkLocalColumnNameList = null;
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), j = 0; j < length; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("description")) {
                        final String description = ParserUtil.getTextNodeVal(nodeElement);
                        fk.setDescription(description);
                        break;
                    }
                    if (tagName.equals("fk-columns")) {
                        this.parseFKColumns(td, fk, nodeElement);
                        break;
                    }
                    if (tagName.equals("fk-constraints")) {
                        final String constraintStr = this.getFKConstraints(nodeElement);
                        if (constraintStr != null) {
                            try {
                                final int constraints = this.getIntVal(constraintStr);
                                fk.setConstraints(constraints);
                            }
                            catch (final MetaDataException mde) {
                                this.exceptionMsg = mde.getMessage() + " in table " + td.getTableName();
                                if (!this.skipException) {
                                    throw new MetaDataException(this.exceptionMsg);
                                }
                                this.searchPattern = "^.*<fk-constraints>" + constraintStr + ".*$";
                                this.errorInfo.addException(this.exceptionMsg, td, 2, this.searchPattern);
                            }
                        }
                        break;
                    }
                    break;
                }
            }
        }
        fkLocalColumnNameList = fk.getFkRefColumns();
        if (!fkLocalColumnNameList.equals(pkColsOfMasterTable) && DataDictionaryParser.onSAS) {
            this.warningMsg = "TableName :: [" + td.getTableName() + "] FKName :: [" + fk.getName() + "] doesnot refer a PrimaryKey.";
            if (this.skipException) {
                this.errorInfo.addWarning(this.warningMsg, td, 2, null);
            }
            else {
                this.warning(this.warningMsg);
            }
        }
        if (!this.skipException || (this.skipException && rtd != null && this.IsFKColumnNamesAreUnique(td, rtd, fk))) {
            this.validateFKColumnsLength(td, rtd, fk);
        }
        return fk;
    }
    
    private boolean IsFKColumnNamesAreUnique(final TableDefinition td, final TableDefinition rtd, final ForeignKeyDefinition fk) {
        boolean result = true;
        final List<ForeignKeyColumnDefinition> fkcols = fk.getForeignKeyColumns();
        for (final ForeignKeyColumnDefinition fkcd : fkcols) {
            if (Collections.frequency(td.getColumnNames(), fkcd.getLocalColumnDefinition().getColumnName()) > 1 || Collections.frequency(rtd.getColumnNames(), fkcd.getReferencedColumnDefinition().getColumnName()) > 1) {
                result = false;
                break;
            }
        }
        return result;
    }
    
    void validateFKColumnsLength(final TableDefinition td, final TableDefinition rtd, final ForeignKeyDefinition fk) throws MetaDataException {
        try {
            ParserUtil.validateFK(rtd, td, fk, null);
        }
        catch (final IllegalArgumentException e) {
            this.exceptionMsg = e.getMessage();
            if (!this.skipException) {
                throw e;
            }
            this.searchPattern = "^.*name\\s*=\\s*\"" + fk.getName() + "\".*$";
            this.errorInfo.addException(this.exceptionMsg, td, 2, this.searchPattern);
        }
    }
    
    private int getIntVal(final String constraintStr) throws MetaDataException {
        if (constraintStr.equalsIgnoreCase("ON-DELETE-CASCADE")) {
            return 1;
        }
        if (constraintStr.equalsIgnoreCase("ON-DELETE-SET-DEFAULT")) {
            return 3;
        }
        if (constraintStr.equalsIgnoreCase("ON-DELETE-SET-NULL")) {
            return 2;
        }
        if (constraintStr.equalsIgnoreCase("ON-DELETE-RESTRICT")) {
            return 0;
        }
        throw new MetaDataException("Unknown fk-constraint specified \"" + constraintStr + "\"");
    }
    
    void parseFKColumns(final TableDefinition td, final ForeignKeyDefinition fk, final Element element) throws MetaDataException {
        ForeignKeyColumnDefinition fkcd = null;
        final NodeList nodes = element.getChildNodes();
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (!nodeElement.getTagName().equals("fk-column")) {
                        break;
                    }
                    fkcd = this.getFKColumnDefinition(td, fk, nodeElement);
                    if (fkcd.getLocalColumnDefinition() != null && fkcd.getReferencedColumnDefinition() != null) {
                        fk.addForeignKeyColumns(fkcd);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    ForeignKeyColumnDefinition getFKColumnDefinition(final TableDefinition td, final ForeignKeyDefinition fk, final Element element) throws MetaDataException {
        final ForeignKeyColumnDefinition fkcd = new ForeignKeyColumnDefinition();
        final NodeList nodes = element.getChildNodes();
        ColumnDefinition cd = null;
        ColumnDefinition rcd = null;
        for (int length = nodes.getLength(), i = 0; i < length; ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    final String tagName = nodeElement.getTagName();
                    if (tagName.equals("fk-local-column")) {
                        final String fkColumnName = this.getFKLocalColumnName(nodeElement);
                        cd = td.getColumnDefinitionByName(fkColumnName);
                        if (cd == null) {
                            cd = new ColumnDefinition();
                            cd.setColumnName(fkColumnName);
                        }
                        fkcd.setLocalColumnDefinition(cd);
                        break;
                    }
                    if (tagName.equals("fk-reference-column")) {
                        final String refFKColumnName = this.getFKReferenceColumnName(nodeElement);
                        rcd = new ColumnDefinition();
                        rcd.setColumnName(refFKColumnName);
                        fkcd.setReferencedColumnDefinition(rcd);
                        break;
                    }
                    break;
                }
            }
        }
        if (cd == null || rcd == null) {
            this.exceptionMsg = "Error in Foreign Key definition: " + fk.getName() + " Either of local column or reference column not defined";
            if (!this.skipException) {
                throw new MetaDataException(this.exceptionMsg);
            }
            this.searchPattern = "^.*name\\s*=\\s*\"" + fk.getName() + "\".*$";
            this.errorInfo.addException(this.exceptionMsg, td, 2, this.searchPattern);
        }
        return fkcd;
    }
    
    String getFKLocalColumnName(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    String getFKReferenceColumnName(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    String getFKConstraints(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    private TableDefinition getTableDefinitionByName(final String tableName) throws MetaDataException {
        TableDefinition td = null;
        td = this.dd.getTableDefinitionByName(tableName);
        if (td == null) {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        return td;
    }
    
    private ForeignKeyDefinition getForeignKeyDefinitionByName(final String foreignKeyName) throws MetaDataException {
        ForeignKeyDefinition fd = null;
        fd = this.dd.getForeignKeyDefinitionByName(foreignKeyName);
        if (fd == null) {
            fd = MetaDataUtil.getForeignKeyDefinitionByName(foreignKeyName);
        }
        return fd;
    }
    
    String getGeneratorName(final Element element) {
        return ParserUtil.getTextNodeVal(element);
    }
    
    UniqueValueGeneration getUniqueValueGeneration(final Element element) throws MetaDataException {
        final UniqueValueGeneration generation = new UniqueValueGeneration();
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("generator-name")) {
                        final String appName = this.getGeneratorName(nodeElement);
                        generation.setGeneratorName(appName);
                        break;
                    }
                    if (nodeElement.getTagName().equals("name-column")) {
                        final String appName = this.getGeneratorName(nodeElement);
                        generation.setNameColumn(appName);
                        break;
                    }
                    if (nodeElement.getTagName().equals("generator-class")) {
                        final String genClass = this.getGeneratorName(nodeElement);
                        generation.setGeneratorClass(genClass);
                        break;
                    }
                    if (!nodeElement.getTagName().equals("instancespecific-seqgen")) {
                        break;
                    }
                    final String isEnabledValue = ParserUtil.getTextNodeVal(nodeElement);
                    if (isEnabledValue != null && (isEnabledValue.trim().equalsIgnoreCase("true") || isEnabledValue.trim().equalsIgnoreCase("false"))) {
                        final boolean isEnabled = Boolean.parseBoolean(isEnabledValue);
                        generation.setInstanceSpecificSequenceGenerator(isEnabled);
                        break;
                    }
                    this.exceptionMsg = "Invalid value \"" + isEnabledValue + "\" defined for <global-sequence-generator> attribute in UVG \"" + generation.getGeneratorName() + "\". It has to be either 'true' or 'false'.";
                    throw new MetaDataException(this.exceptionMsg);
                }
            }
        }
        return generation;
    }
    
    public static void main(final String[] args) throws Exception {
        final String fileName = (args.length > 0) ? args[0] : "data-dictionary.xml";
        final DocumentBuilder builder = SecurityUtil.getDocumentBuilder();
        final Document document = builder.parse(new InputSource(fileName));
        final DataDictionaryParser scanner = new DataDictionaryParser(document);
        scanner.parseDocument();
    }
    
    private void warning(final String message) {
        if (this.developmentMode) {
            ConsoleOut.println(message);
        }
        DataDictionaryParser.LOGGER.warning(message);
    }
    
    private static Document getDocument(final URL url) throws Exception {
        final InputStream ddStream = url.openStream();
        final Properties dbfFeatures = new Properties();
        ((Hashtable<String, String>)dbfFeatures).put("http://xml.org/sax/features/validation", "true");
        final DocumentBuilder documentbuilder = SecurityUtil.createDocumentBuilder(true, true, dbfFeatures);
        documentbuilder.setErrorHandler(new MetaDataErrorHandler());
        documentbuilder.setEntityResolver(new MetaDataEntityResolver());
        final Document document = documentbuilder.parse(new InputSource(ddStream));
        ddStream.close();
        return document;
    }
    
    private static DataDictionary getDataDictionary(final URL url, DataDictionary dataDictionary) throws Exception {
        if (url.getFile().endsWith(".xml")) {
            final Document document = getDocument(url);
            final DataDictionaryParser datadictionaryparser = new DataDictionaryParser(document, url.getFile());
            datadictionaryparser.parseDocument(dataDictionary);
            dataDictionary = datadictionaryparser.getDataDictionary();
        }
        return dataDictionary;
    }
    
    private static DataDictionary getDataDictionary(final URL url, final DataDictionary dataDictionary, final String ddXMLDir) throws Exception {
        return getDataDictionary(url, dataDictionary, ddXMLDir, false);
    }
    
    private static DataDictionary getDataDictionary(final URL url, DataDictionary dataDictionary, final String ddXMLDir, final Boolean skipException) throws Exception {
        if (url.getFile().endsWith(".xml")) {
            final InputStream ddStream = url.openStream();
            final Properties dbfFeatures = new Properties();
            ((Hashtable<String, String>)dbfFeatures).put("http://xml.org/sax/features/validation", "true");
            final DocumentBuilder documentbuilder = SecurityUtil.createDocumentBuilder(true, true, dbfFeatures);
            documentbuilder.setErrorHandler(new MetaDataErrorHandler());
            final MetaDataEntityResolver mder = new MetaDataEntityResolver();
            mder.setDDXMLDir(ddXMLDir);
            documentbuilder.setEntityResolver(mder);
            final Document document = documentbuilder.parse(new InputSource(ddStream));
            final DataDictionaryParser datadictionaryparser = new DataDictionaryParser(document, url.getFile(), skipException);
            datadictionaryparser.parseDocument(dataDictionary);
            dataDictionary = datadictionaryparser.getDataDictionary();
        }
        return dataDictionary;
    }
    
    public static DataDictionary getDataDictionary(final URL url) throws Exception {
        return getDataDictionary(url, false);
    }
    
    public static DataDictionary getDataDictionary(final URL url, final boolean skipException) throws Exception {
        DataDictionary dataDictionary = null;
        if (!url.getFile().endsWith("dd-files.xml")) {
            dataDictionary = getDataDictionary(url, null, new File(url.getPath()).getParentFile().toString(), skipException);
        }
        else {
            final String ddFilePath = url.getPath();
            final DataObject ddFilesDO = Xml2DoConverter.transform(url);
            final String moduleDir = ddFilePath.substring(0, ddFilePath.lastIndexOf("/"));
            final Iterator iterator = ddFilesDO.getRows("ConfFile");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                String urlStr = (String)row.get("URL");
                urlStr = moduleDir + "/" + urlStr;
                DataDictionaryParser.LOGGER.log(Level.INFO, " Going to parse DD in ::" + urlStr);
                final URL tempUrl = new File(urlStr).toURI().toURL();
                dataDictionary = getDataDictionary(tempUrl, dataDictionary, new File(tempUrl.getPath()).getParentFile().toString(), skipException);
            }
        }
        return dataDictionary;
    }
    
    private DDErrorInfo getErrorInfo() {
        return this.errorInfo;
    }
    
    public static DDErrorInfo getErrorInfo(final File fileName, final DataDictionary dd, final boolean enableDTDValidation) throws Exception {
        DDErrorInfo info = null;
        final Document document = getDocument(fileName.toURL());
        final DataDictionaryParser scanner = new DataDictionaryParser(document, true);
        scanner.parseDocument(dd);
        info = scanner.getErrorInfo();
        info.dd = scanner.getDataDictionary();
        return info;
    }
    
    static {
        LOGGER = Logger.getLogger(DataDictionaryParser.class.getName());
        DataDictionaryParser.onSAS = false;
        DataDictionaryParser.nonUVGTables = null;
    }
}
