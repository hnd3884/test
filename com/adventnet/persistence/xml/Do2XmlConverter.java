package com.adventnet.persistence.xml;

import com.zoho.conf.AppResources;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Locale;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.List;
import com.adventnet.persistence.PersistenceUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.io.FileOutputStream;
import java.io.File;
import org.w3c.dom.Element;
import java.util.Map;
import java.io.OutputStream;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class Do2XmlConverter
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    
    public static void transform(final DataObject data, final OutputStream os, final String root, final boolean carryOverGeneratedValues, final String xmlVersion) {
        final Do2XmlConverter doXml = new Do2XmlConverter();
        final DoXmlProps props = doXml.new DoXmlProps();
        props.setCarryOverGeneratedValues(carryOverGeneratedValues);
        props.setRoot(root);
        props.setXmlVersion(xmlVersion);
        transform(data, os, props);
    }
    
    public static void transform(final DataObject data, final OutputStream os, final DoXmlProps props) {
        Do2XmlConverter.LOGGER.entering(Do2XmlConverter.CLASS_NAME, "transform", new Object[] { data });
        final ParentChildrenMap pcm = null;
        Element result = null;
        final String encodingString = props.getEncodingString();
        final String xmlVersion = props.getXmlVersion();
        final String root = props.getRoot();
        final boolean carryOverGeneratedValues = props.getCarryOverGeneratedValues();
        try {
            result = getXmlFromDO(data, root, carryOverGeneratedValues, null, true);
            final XmlWriter writer = new XmlWriter();
            writer.setXmlVersion(xmlVersion);
            writer.write(result, os, encodingString);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void transform(final DataObject data, final String absoluteFilePath) throws Do2XmlConverterException {
        transform(data, absoluteFilePath, false, null, true);
    }
    
    public static void transform(final DataObject data, final String absoluteFilePath, final boolean carryOverGeneratedValues) throws Do2XmlConverterException {
        transform(data, absoluteFilePath, carryOverGeneratedValues, null, true);
    }
    
    public static void transform(final DataObject data, final String absoluteFilePath, final Map uvhMap) throws Do2XmlConverterException {
        transform(data, absoluteFilePath, false, uvhMap, true);
    }
    
    public static void transform(final DataObject data, final String absoluteFilePath, final boolean carryOverGeneratedValues, final Map uvhMap, final boolean useDynamicValueHandlers) throws Do2XmlConverterException {
        transform(data, absoluteFilePath, carryOverGeneratedValues, uvhMap, useDynamicValueHandlers, null);
    }
    
    public static void transform(final DataObject data, final String absoluteFilePath, final boolean carryOverGeneratedValues, final Map uvhMap, final boolean useDynamicValueHandlers, final String encodingString) throws Do2XmlConverterException {
        Do2XmlConverter.LOGGER.entering(Do2XmlConverter.CLASS_NAME, "transform", new Object[] { data, absoluteFilePath });
        FileOutputStream fos = null;
        try {
            final File file = new File(absoluteFilePath);
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".xml");
            if (index < 0) {
                index = fileName.lastIndexOf(".");
            }
            index = ((index > 0) ? index : (fileName.length() - 1));
            fileName = fileName.substring(0, index);
            fos = new FileOutputStream(file);
            transform(data, fos, fileName, carryOverGeneratedValues, uvhMap, useDynamicValueHandlers, encodingString);
        }
        catch (final IOException ioe) {
            Do2XmlConverter.LOGGER.log(Level.SEVERE, "Exception occured during XML transformation ", ioe);
            throw new Do2XmlConverterException(ioe);
        }
        finally {
            Do2XmlConverter.LOGGER.exiting(Do2XmlConverter.CLASS_NAME, "transform");
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e) {
                    Do2XmlConverter.LOGGER.log(Level.WARNING, "Exception occured during close the FileOutputStream");
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void transform(final DataObject data, final OutputStream os, final String root) throws Do2XmlConverterException {
        transform(data, os, root, false);
    }
    
    public static void transform(final DataObject data, final OutputStream os, final String root, final boolean carryOverGeneratedValues) throws Do2XmlConverterException {
        transform(data, os, root, carryOverGeneratedValues, null, true, null);
    }
    
    public static void transform(final DataObject data, final OutputStream os, final String root, final boolean carryOverGeneratedValues, final Map uvhMap, final boolean useDynamicValueHandlers, final String encodingString) throws Do2XmlConverterException {
        Do2XmlConverter.LOGGER.entering(Do2XmlConverter.CLASS_NAME, "transform", new Object[] { data });
        Element result = null;
        try {
            result = getXmlFromDO(data, root, carryOverGeneratedValues, uvhMap, useDynamicValueHandlers);
            final XmlWriter writer = new XmlWriter();
            writer.write(result, os, encodingString);
        }
        catch (final Throwable exc) {
            Do2XmlConverter.LOGGER.log(Level.SEVERE, "", exc);
            throw new Do2XmlConverterException(exc);
        }
        finally {
            Do2XmlConverter.LOGGER.exiting(Do2XmlConverter.CLASS_NAME, "transform");
        }
    }
    
    public static Element getXmlFromDO(final DataObject data, final String root, final boolean carryOverGeneratedValues) throws Exception {
        return getXmlFromDO(data, root, carryOverGeneratedValues, null);
    }
    
    public static Element getXmlFromDO(final DataObject data, final String root, final boolean carryOverGeneratedValues, final Map uvhMap) throws Exception {
        return getXmlFromDO(data, root, carryOverGeneratedValues, null, true);
    }
    
    public static Element getXmlFromDO(final DataObject data, final String root, final boolean carryOverGeneratedValues, final Map uvhMap, final boolean useDynamicValueHandlers) throws Exception {
        Do2XmlConverter.LOGGER.entering(Do2XmlConverter.CLASS_NAME, "transform", new Object[] { data });
        ParentChildrenMap pcm = null;
        Element result = null;
        final List tableNames = PersistenceUtil.sortTables(data.getTableNames());
        final boolean swap_parent_child = true;
        final XmlFormatBuilder builder = new XmlFormatBuilder(swap_parent_child);
        pcm = builder.createFormat(tableNames, root);
        Do2XmlConverter.LOGGER.log(Level.FINEST, "XML Format:{0}", pcm);
        final XmlCreator creator = new XmlCreator();
        creator.setCarryOverGeneratedValues(carryOverGeneratedValues);
        creator.setUVHMap(uvhMap);
        creator.setUseDVH(useDynamicValueHandlers);
        result = creator.createElement(data, pcm);
        Do2XmlConverter.LOGGER.log(Level.FINEST, "XML Document:{0}", result);
        return result;
    }
    
    public static void setDOMHander(final DOMHandler newDOMHandler) {
        XmlCreator.domHandler = newDOMHandler;
    }
    
    public static Map getUVHMap(final Long confFileID) throws DataAccessException {
        final Map uvhMap = new HashMap(5);
        final DataObject uvhValuesDO = DataAccess.get("UVHValues", new Criteria(new Column("UVHValues", "FILEID"), confFileID, 0));
        final Iterator iterator = uvhValuesDO.getRows(null);
        if (!iterator.hasNext()) {
            return null;
        }
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String tableName = (String)row.get("TABLE_NAME");
            final String colName = (String)row.get("COLUMN_NAME");
            final Long genValue = (Long)row.get(4);
            final String pattern = (String)row.get(3);
            final String key = tableName + ":" + colName.toLowerCase() + ":" + genValue;
            uvhMap.put(key, pattern);
        }
        return uvhMap;
    }
    
    public static void fillUVHValues(final String xmlFilePath, final DataObject updatedDO) throws DataAccessException {
        final File xmlFile = new File(xmlFilePath);
        final Long confFileId = getFileId(xmlFilePath);
        final Map patternValues = getPatternValues(confFileId);
        DataObject existingXMLDO = null;
        DataObject diffDO = null;
        try {
            existingXMLDO = Xml2DoConverter.transform(xmlFile.toURL(), false, patternValues);
            diffDO = existingXMLDO.diff(updatedDO);
            updateUVHValues(diffDO, updatedDO, confFileId);
        }
        catch (final Exception ex) {
            throw new DataAccessException("fillUVHValues: ", ex);
        }
    }
    
    private static void updateUVHValues(final DataObject diffDO, final DataObject updatedDO, final Long confFileId) throws DataAccessException, MetaDataException {
        final ArrayList<String> tableNames = (ArrayList<String>)diffDO.getTableNames();
        for (final String tableName : tableNames) {
            final Iterator<Row> dataIterator = diffDO.getRows(tableName);
            if (null != dataIterator) {
                if (!dataIterator.hasNext()) {
                    continue;
                }
                final ArrayList<String> uvhColumns = getUVHColumns(tableName);
                while (dataIterator.hasNext()) {
                    final Row curRow = dataIterator.next();
                    for (final String columnName : uvhColumns) {
                        final Long genValue = (Long)curRow.get(columnName);
                        final Row UVHValueRow = new Row("UVHValues");
                        UVHValueRow.set("FILEID", confFileId);
                        UVHValueRow.set("TABLE_NAME", tableName);
                        UVHValueRow.set("COLUMN_NAME", columnName);
                        UVHValueRow.set("PATTERN", tableName + ":" + columnName.toLowerCase(Locale.ENGLISH) + ":" + genValue);
                        UVHValueRow.set("GENVALUES", genValue);
                        updatedDO.addRow(UVHValueRow);
                    }
                }
            }
        }
    }
    
    private static ArrayList<String> getUVHColumns(final String tableName) throws MetaDataException {
        final TableDefinition tDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final ArrayList<ColumnDefinition> colDefs = (ArrayList<ColumnDefinition>)tDef.getColumnList();
        final ArrayList<String> uvhCols = new ArrayList<String>();
        for (final ColumnDefinition colDef : colDefs) {
            if (null != colDef.getUniqueValueGeneration()) {
                if (colDef.getUniqueValueGeneration().getNameColumn() != null) {
                    continue;
                }
                uvhCols.add(colDef.getColumnName());
            }
        }
        return uvhCols;
    }
    
    private static Long getFileId(final String filePath) throws DataAccessException {
        final String startPattern = "/conf/";
        final int sIndex = filePath.indexOf(startPattern) + startPattern.length();
        final String relativePath = filePath.substring(sIndex);
        final SelectQuery query = new SelectQueryImpl(new Table("ConfFile"));
        final Column urlCol = new Column("ConfFile", "URL");
        query.addSelectColumn(new Column("ConfFile", "FILEID"));
        query.addSelectColumn(urlCol);
        final Criteria urlCriteria = new Criteria(urlCol, "*" + relativePath, 2);
        query.setCriteria(urlCriteria);
        final DataObject rDo = DataAccess.get(query);
        final Row fileIDRow = rDo.getFirstRow("ConfFile");
        return (Long)fileIDRow.get("FILEID");
    }
    
    private static HashMap getPatternValues(final Long confFileId) throws DataAccessException {
        final SelectQuery query = new SelectQueryImpl(new Table("UVHValues"));
        HashMap patternVsValue = null;
        query.addSelectColumn(new Column("UVHValues", "*"));
        final Criteria urlCriteria = new Criteria(new Column("UVHValues", "FILEID"), confFileId, 0);
        query.setCriteria(urlCriteria);
        final DataObject sqlDO = DataAccess.get(query);
        if (sqlDO.size("UVHValues") > 0) {
            patternVsValue = new HashMap();
            final Iterator keyiter = sqlDO.get("UVHValues", "PATTERN");
            final Iterator valueiter = sqlDO.get("UVHValues", "GENVALUES");
            while (keyiter.hasNext()) {
                patternVsValue.put(keyiter.next(), valueiter.next());
            }
        }
        return patternVsValue;
    }
    
    static {
        CLASS_NAME = Do2XmlConverter.class.getName();
        LOGGER = Logger.getLogger(Do2XmlConverter.CLASS_NAME);
    }
    
    public class DoXmlProps
    {
        private boolean carryOverGeneratedValues;
        private String root;
        private String xmlVersion;
        private String encodingString;
        
        public DoXmlProps() {
            this.carryOverGeneratedValues = false;
            this.root = "doxml";
            this.xmlVersion = "1.0";
            this.encodingString = AppResources.getString("do2xml.encoding", "iso-8859-1");
        }
        
        public void setCarryOverGeneratedValues(final boolean condition) {
            this.carryOverGeneratedValues = condition;
        }
        
        public void setRoot(final String root) {
            this.root = root;
        }
        
        public void setXmlVersion(final String xmlVersion) {
            this.xmlVersion = xmlVersion;
        }
        
        public void setEncodingString(final String encodingString) {
            this.encodingString = encodingString;
        }
        
        public boolean getCarryOverGeneratedValues() {
            return this.carryOverGeneratedValues;
        }
        
        public String getRoot() {
            return this.root;
        }
        
        public String getXmlVersion() {
            return this.xmlVersion;
        }
        
        public String getEncodingString() {
            return this.encodingString;
        }
    }
}
