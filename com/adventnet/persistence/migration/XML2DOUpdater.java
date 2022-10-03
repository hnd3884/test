package com.adventnet.persistence.migration;

import com.zoho.conf.Configuration;
import com.adventnet.persistence.PersistenceUtil;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.sql.Timestamp;
import java.util.Date;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.ActionInfo;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.zip.ZipEntry;
import java.net.URL;
import java.util.zip.ZipFile;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.mfw.ConfPopulator;
import com.adventnet.persistence.WritableDataObject;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.zoho.migration.TableLevelConfigurationHandlerUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.xml.ConfUrlInfo;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.logging.Level;
import com.adventnet.persistence.xml.ConfigurationPopulator;
import com.adventnet.persistence.Row;
import java.io.File;
import java.util.logging.Logger;

class XML2DOUpdater
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private static int opPreference;
    private static String fileToBeUpdated;
    private static boolean isJarFile;
    static boolean isIntallProgress;
    private static String server_home;
    
    public XML2DOUpdater() {
    }
    
    private static void init(final String filetobeUpdated) {
        XML2DOUpdater.fileToBeUpdated = filetobeUpdated;
        if (XML2DOUpdater.fileToBeUpdated.indexOf("!/") != -1) {
            XML2DOUpdater.isJarFile = true;
        }
    }
    
    static void addXml(final File newFile, final File backUpFile, final String moduleName, final Row confFileRow, final DOXMLChangeListener doXmlChangeListener, final ConfigurationPopulator populator) throws Exception {
        final String serverHome = new File(XML2DOUpdater.server_home).getCanonicalPath();
        String fileToBeAdded = newFile.getCanonicalPath().substring(serverHome.length() + 1);
        fileToBeAdded = fileToBeAdded.replaceAll("\\\\", "/");
        XML2DOUpdater.OUT.log(Level.INFO, "fileToBeAdded : : {0}", fileToBeAdded);
        final File file = (!XML2DOUpdater.isIntallProgress && backUpFile != null && backUpFile.exists()) ? backUpFile : newFile;
        DataObject data = Xml2DoConverter.transform(file.getAbsolutePath());
        Row cfRow = data.getRow("ConfFile");
        if (cfRow == null) {
            cfRow = new Row("ConfFile");
        }
        cfRow.set("URL", ConfUrlInfo.getRelativePath(file.getCanonicalPath()));
        final String handlerClass = (String)confFileRow.get("HANDLERCLASS");
        cfRow.set("HANDLERCLASS", handlerClass);
        cfRow.set("POPULATORCLASS", confFileRow.get("POPULATORCLASS"));
        if (data.getRow("ConfFile") == null) {
            data.addRow(cfRow);
        }
        else {
            data.updateRow(cfRow);
        }
        if (data.getRow("ConfFileToModule") == null) {
            final Row cf_module = new Row("ConfFileToModule");
            cf_module.set("FILEID", cfRow.get("FILEID"));
            cf_module.set("MODULE_ID", getModuleId(moduleName));
            data.addRow(cf_module);
        }
        DataAccess.fillGeneratedValues(data);
        XML2DOUpdater.OUT.log(Level.INFO, "Data to be added :: {0}", data);
        final XmlChangeNotifyObject notifyObj = new DoXmlChangeNotifyObject();
        notifyObj.setLatestXMLDO(data);
        notifyObj.setXMLDiffDO(data);
        XML2DOUpdater.OUT.log(Level.INFO, "Table Level configuration Handler started for :: {0}", fileToBeAdded);
        TableLevelConfigurationHandlerUtil.invokeHandlers(data);
        if (doXmlChangeListener != null) {
            final boolean proceed = doXmlChangeListener.preInvoke(fileToBeAdded, notifyObj);
            XML2DOUpdater.OUT.log(Level.INFO, "doXmlChangeListener preInvoke returns :: {0}", proceed);
            if (!proceed) {
                return;
            }
        }
        data = notifyObj.getXMLDiffDO();
        DataObject commitedDO;
        if (populator != null) {
            XML2DOUpdater.OUT.log(Level.INFO, "using the populator [{0}]", populator.getClass().getName());
            populator.populate(data);
            commitedDO = data;
            XML2DOUpdater.OUT.log(Level.INFO, "commitedDO :: [{0}]", commitedDO);
        }
        else {
            commitedDO = DataAccess.add(data);
        }
        if (doXmlChangeListener != null) {
            doXmlChangeListener.postInvoke(fileToBeAdded, commitedDO);
        }
    }
    
    private static long getModuleId(final String moduleName) throws Exception {
        long moduleId = 0L;
        final DataObject ModuleDO = DataAccess.get("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), moduleName, 0));
        final Row moduleEntry = ModuleDO.getFirstRow("Module");
        if (moduleEntry != null) {
            moduleId = Long.parseLong(moduleEntry.get("MODULE_ID").toString());
        }
        return moduleId;
    }
    
    static void diffXml(final File existingFile, final File backUpFile, final String moduleName, final DOXMLChangeListener doXmlChangeListener, final ConfigurationPopulator populator, final Row xmlConfFileEntry) throws Exception {
        final String serverHome = new File(XML2DOUpdater.server_home).getCanonicalPath();
        String filePath = existingFile.getCanonicalPath().substring(serverHome.length() + 1);
        filePath = filePath.replaceAll("\\\\", "/");
        init(filePath);
        XML2DOUpdater.OUT.log(Level.INFO, "filetobeupdated : : {0}", XML2DOUpdater.fileToBeUpdated);
        final Row cfRow = getCFRow(moduleName, xmlConfFileEntry, existingFile);
        XML2DOUpdater.OUT.log(Level.INFO, "CFRow :: " + cfRow);
        final Map patternValues = getPatternValues();
        XML2DOUpdater.OUT.log(Level.INFO, "pattern Values :: {0}", patternValues);
        if (doXmlChangeListener != null) {
            XML2DOUpdater.opPreference = doXmlChangeListener.getPreference(XML2DOUpdater.fileToBeUpdated);
        }
        XML2DOUpdater.OUT.log(Level.INFO, "opertionPreference :: {0}", XML2DOUpdater.opPreference);
        DataObject latestXMLDO;
        DataObject existingXMLDO;
        if (XML2DOUpdater.isIntallProgress) {
            latestXMLDO = checkAndParse(existingFile.getCanonicalPath(), cfRow, patternValues);
            existingXMLDO = checkAndParse(backUpFile.getCanonicalPath(), cfRow, patternValues);
        }
        else {
            existingXMLDO = checkAndParse(existingFile.getCanonicalPath(), cfRow, patternValues);
            latestXMLDO = checkAndParse(backUpFile.getCanonicalPath(), cfRow, patternValues);
        }
        XML2DOUpdater.OUT.log(Level.INFO, "existingXMLDO :: {0}", existingXMLDO);
        XML2DOUpdater.OUT.log(Level.INFO, "latestXMLDO   :: {0}", latestXMLDO);
        diffXml(latestXMLDO, existingXMLDO, cfRow, doXmlChangeListener, populator);
    }
    
    private static HashMap getPatternValues() throws Exception {
        final SelectQuery query = new SelectQueryImpl(new Table("UVHValues"));
        HashMap patternVsValue = null;
        try {
            query.addSelectColumn(Column.getColumn(null, "*"));
            final Join j = new Join("UVHValues", "ConfFile", new String[] { "FILEID" }, new String[] { "FILEID" }, 1);
            query.addJoin(j);
            final String urlCriteria = (XML2DOUpdater.isJarFile ? "jar*" : "*") + XML2DOUpdater.fileToBeUpdated;
            final Criteria criteria = new Criteria(Column.getColumn("ConfFile", "URL"), urlCriteria, 2);
            query.setCriteria(criteria);
            XML2DOUpdater.OUT.log(Level.INFO, " Query  to fetch uvh pattern  : {0}", RelationalAPI.getInstance().getSelectSQL(query));
            final DataObject sqlDO = DataAccess.get(query);
            XML2DOUpdater.OUT.log(Level.INFO, " Fetched DO  : {0}", sqlDO);
            if (sqlDO.size("UVHValues") > 0) {
                patternVsValue = new HashMap();
                final Iterator keyiter = sqlDO.get("UVHValues", "PATTERN");
                final Iterator valueiter = sqlDO.get("UVHValues", "GENVALUES");
                while (keyiter.hasNext()) {
                    patternVsValue.put(keyiter.next(), valueiter.next());
                }
            }
        }
        catch (final Exception x) {
            x.printStackTrace();
            throw x;
        }
        return patternVsValue;
    }
    
    private static Row getCFRow(final String moduleName, final Row xmlConfFileEntry, final File doXmlFile) throws DataAccessException {
        final SelectQuery query = new SelectQueryImpl(new Table("ConfFile"));
        query.addSelectColumn(new Column("ConfFile", "*"));
        final String urlCriteria = (XML2DOUpdater.isJarFile ? "jar*" : "*") + XML2DOUpdater.fileToBeUpdated;
        final Criteria criteria = new Criteria(new Column("ConfFile", "URL"), urlCriteria, 2);
        query.setCriteria(criteria);
        DataObject data = DataAccess.get(query);
        Row retRow = data.getRow("ConfFile");
        try {
            if (retRow == null) {
                data = new WritableDataObject();
                retRow = new Row("ConfFile");
                retRow.set("URL", ConfUrlInfo.getRelativePath(doXmlFile.getCanonicalPath()));
                final String handlerClass = (xmlConfFileEntry != null) ? ((String)xmlConfFileEntry.get("HANDLERCLASS")) : null;
                retRow.set("HANDLERCLASS", handlerClass);
                data.addRow(retRow);
                final Row cf_module = new Row("ConfFileToModule");
                cf_module.set("FILEID", retRow.get("FILEID"));
                cf_module.set("MODULE_ID", ConfPopulator.getModuleId(moduleName));
                data.addRow(cf_module);
                XML2DOUpdater.OUT.log(Level.INFO, "Patch for ConfFile before add ::: {0}", data);
                DataAccess.add(data);
                XML2DOUpdater.OUT.log(Level.INFO, "Patch for ConfFile after add  ::: {0}", data);
                retRow = data.getRow("ConfFile");
            }
        }
        catch (final Exception e) {
            throw new DataAccessException(e);
        }
        return retRow;
    }
    
    private static DataObject checkAndParse(final String fileLoc, final Row cfRow, final Map map) throws Exception {
        if (XML2DOUpdater.isJarFile) {
            final int index = fileLoc.indexOf("!/");
            final File f = new File(fileLoc.substring(0, index));
            if (f.exists()) {
                final File absFile = f.getAbsoluteFile();
                final ZipFile jf = new ZipFile(absFile);
                final String jfEntry = fileLoc.substring(index + 2);
                final ZipEntry entry = jf.getEntry(jfEntry);
                if (entry != null) {
                    final URL url = new URL("jar", "", absFile.toURL().toString() + fileLoc.substring(index));
                    return parseXML(url, cfRow, map);
                }
            }
        }
        else {
            final File f2 = new File(fileLoc);
            if (f2.exists()) {
                return parseXML(f2.getAbsoluteFile().toURL(), cfRow, map);
            }
            XML2DOUpdater.OUT.log(Level.WARNING, "ERROR !!!!! The given XMLFile is not found :: {0}", f2);
        }
        return DataAccess.constructDataObject();
    }
    
    private static DataObject parseXML(final URL file, final Row cfRow, final Map map) throws Exception {
        final DataObject data = Xml2DoConverter.transform(file, true, map);
        if (data.containsTable("UVHValues")) {
            data.set("UVHValues", "FILEID", cfRow.get("FILEID"));
        }
        if (data.containsTable("ConfFile")) {
            data.deleteRows("ConfFile", (Row)null);
        }
        return data;
    }
    
    private static void diffXml(final DataObject latestXMLDO, final DataObject existingXMLDO, final Row cfRow, final DOXMLChangeListener doXmlChangeListener, final ConfigurationPopulator populator) throws Exception {
        DataAccess.fillGeneratedValues(existingXMLDO);
        DataAccess.fillGeneratedValues(latestXMLDO);
        ((WritableDataObject)existingXMLDO).clearOperations();
        ((WritableDataObject)latestXMLDO).clearOperations();
        DataObject xmlDiffDO = existingXMLDO.diff(latestXMLDO);
        XML2DOUpdater.OUT.log(Level.INFO, "xmlDiffDO :: {0}", xmlDiffDO);
        final HashMap map = ((WritableDataObject)xmlDiffDO).getActionsFor("delete");
        if (xmlDiffDO.isEmpty() && (map == null || map.isEmpty())) {
            XML2DOUpdater.OUT.log(Level.INFO, "Since No changes exists in xmlDiff, skipping the xml Change update.");
            return;
        }
        final XmlChangeNotifyObject notifyObj = new DoXmlChangeNotifyObject();
        notifyObj.setExistingXMLDO(existingXMLDO);
        notifyObj.setLatestXMLDO(latestXMLDO);
        notifyObj.setXMLDiffDO(xmlDiffDO);
        xmlDiffDO = notifyObj.getXMLDiffDO();
        if (XML2DOUpdater.opPreference == 2) {
            final Map tableNameVsActionInfos = ((WritableDataObject)xmlDiffDO).getActionsFor("update");
            if (tableNameVsActionInfos != null) {
                final Iterator iterator = tableNameVsActionInfos.keySet().iterator();
                final DataObject dbDO = new WritableDataObject();
                while (iterator.hasNext()) {
                    final String tableName = iterator.next();
                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                    final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
                    final Iterator actionInfos = tableNameVsActionInfos.get(tableName).iterator();
                    final List values = new ArrayList();
                    if (pkDef.getColumnList().size() == 1) {
                        final String pkColName = pkDef.getColumnList().get(0);
                        while (actionInfos.hasNext()) {
                            final ActionInfo actionInfo = actionInfos.next();
                            values.add(actionInfo.getValue().get(pkColName));
                        }
                        dbDO.merge(fetchDataFromDBFor(tableName, pkColName, values));
                    }
                    else {
                        while (actionInfos.hasNext()) {
                            final ActionInfo actionInfo2 = actionInfos.next();
                            values.add(actionInfo2.getValue());
                        }
                        dbDO.merge(fetchDataFromDBFor(tableName, values));
                    }
                }
                ((WritableDataObject)dbDO).clearOperations();
                final Iterator dbDORows = dbDO.getRows(null);
                while (dbDORows.hasNext()) {
                    final Row dbDORow = dbDORows.next();
                    final Row xmlDiffDORow = xmlDiffDO.getRow(dbDORow.getTableName(), dbDORow);
                    final Row existingXMLDORow = existingXMLDO.getRow(dbDORow.getTableName(), dbDORow);
                    XML2DOUpdater.OUT.log(Level.INFO, "dbDORow :: " + dbDORow);
                    XML2DOUpdater.OUT.log(Level.INFO, "xmlDiffDORow :: " + xmlDiffDORow);
                    XML2DOUpdater.OUT.log(Level.INFO, "existingXMLDORow (before setting dbrow values):: " + existingXMLDORow);
                    final List columns = existingXMLDORow.getColumns();
                    for (int i = 1; i <= columns.size(); ++i) {
                        existingXMLDORow.set(i, dbDORow.get(i));
                    }
                    XML2DOUpdater.OUT.log(Level.INFO, "existingXMLDORow (after setting dbrow values):: " + existingXMLDORow);
                    final int[] changedColIndices = existingXMLDORow.getChangedColumnIndex();
                    if (changedColIndices != null) {
                        for (int a = 0; a < changedColIndices.length; ++a) {
                            xmlDiffDORow.set(changedColIndices[a], existingXMLDORow.get(changedColIndices[a]));
                        }
                        xmlDiffDO.updateRow(xmlDiffDORow);
                    }
                }
            }
            else {
                XML2DOUpdater.OUT.log(Level.INFO, "There is no update row operation in the xmlDiffDO : : {0}", xmlDiffDO);
            }
        }
        XML2DOUpdater.OUT.log(Level.INFO, "updated xmlDiffDO : {0}", xmlDiffDO);
        deleteUVHValues(xmlDiffDO, (Long)cfRow.get("FILEID"));
        XML2DOUpdater.OUT.log(Level.INFO, "updated xmlDiffDO after adding UVHValues rows for delete : {0}", xmlDiffDO);
        final XmlChangeNotifyObject notifyObject = new DoXmlChangeNotifyObject();
        notifyObject.setExistingXMLDO(existingXMLDO);
        notifyObject.setLatestXMLDO(latestXMLDO);
        notifyObject.setXMLDiffDO(xmlDiffDO);
        XML2DOUpdater.OUT.log(Level.INFO, "Table Level configuration Handler started for :: {0}", XML2DOUpdater.fileToBeUpdated);
        TableLevelConfigurationHandlerUtil.invokeHandlers(notifyObject.getXMLDiffDO());
        doUpdate(notifyObject, doXmlChangeListener, populator);
    }
    
    private static DataObject fetchDataFromDBFor(final String tableName, final List rows) throws Exception {
        final DataObject returnDO = new WritableDataObject();
        final int size = rows.get(0).getPKColumns().size();
        for (int fetchSize = 60 / size, index = 0; index < rows.size(); index += fetchSize) {
            final int to = index + fetchSize;
            final List subList = rows.subList(index, (to < rows.size()) ? to : rows.size());
            returnDO.merge(DataAccess.get(tableName, subList));
        }
        return returnDO;
    }
    
    private static DataObject fetchDataFromDBFor(final String tableName, final String columnName, final List values) throws Exception {
        final ColumnDefinition cd = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName);
        final String dataType = cd.getDataType();
        final int type = QueryUtil.getJavaSQLType(dataType);
        final DataObject returnDO = new WritableDataObject();
        for (int index = 0; index < values.size(); index += 100) {
            final int to = index + 99;
            final List subList = values.subList(index, (to < values.size()) ? to : values.size());
            Object value = null;
            switch (type) {
                case 4: {
                    value = new int[subList.size()];
                    for (int i = 0; i < subList.size(); ++i) {
                        ((int[])value)[i] = subList.get(i);
                    }
                    break;
                }
                case -5: {
                    value = new long[subList.size()];
                    for (int i = 0; i < subList.size(); ++i) {
                        ((long[])value)[i] = subList.get(i);
                    }
                    break;
                }
                case 1:
                case 12: {
                    value = subList.toArray(new String[0]);
                    break;
                }
                case 6: {
                    value = subList.toArray(new Float[0]);
                    break;
                }
                case 8: {
                    value = subList.toArray(new Double[0]);
                    break;
                }
                case 91: {
                    value = subList.toArray(new Date[0]);
                    break;
                }
                case 92:
                case 93: {
                    value = subList.toArray(new Timestamp[0]);
                    break;
                }
                default: {
                    value = subList.toArray();
                    break;
                }
            }
            final Criteria c = new Criteria(new Column(tableName, columnName), value, 8);
            returnDO.merge(DataAccess.get(tableName, c));
        }
        return returnDO;
    }
    
    private static void doUpdate(final XmlChangeNotifyObject notifyObject, final DOXMLChangeListener doXmlChangeListener, final ConfigurationPopulator populator) throws Exception {
        try {
            boolean proceed = true;
            if (doXmlChangeListener != null) {
                try {
                    proceed = doXmlChangeListener.preInvoke(XML2DOUpdater.fileToBeUpdated, notifyObject);
                    XML2DOUpdater.OUT.log(Level.INFO, "doXmlChangeListener preInvoke returns :: " + proceed);
                }
                catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (proceed) {
                XML2DOUpdater.OUT.log(Level.INFO, "Going to update the DiffDO in DB, {0}", notifyObject.getXMLDiffDO());
                if (populator != null) {
                    XML2DOUpdater.OUT.log(Level.INFO, "using the populator :: [{0}]", populator.getClass().getName());
                    populator.update(notifyObject.getXMLDiffDO());
                }
                else {
                    XML2DOUpdater.OUT.log(Level.INFO, "using DataAccess API to update.");
                    DataAccess.update(notifyObject.getXMLDiffDO());
                }
                XML2DOUpdater.OUT.log(Level.INFO, "commitedDO :: {0}", notifyObject.getXMLDiffDO());
            }
            if (doXmlChangeListener != null) {
                try {
                    doXmlChangeListener.postInvoke(XML2DOUpdater.fileToBeUpdated, notifyObject.getXMLDiffDO());
                }
                catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        catch (final DataAccessException e2) {
            XML2DOUpdater.OUT.log(Level.SEVERE, "error  while updating db with data  {0} \n of File {1} ", new Object[] { notifyObject.getXMLDiffDO(), XML2DOUpdater.fileToBeUpdated });
            throw e2;
        }
    }
    
    private static String getUVGColumnName(final String tableName) throws Exception {
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
        for (final String columnName : td.getColumnNames()) {
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            if (cd.getUniqueValueGeneration() != null) {
                return columnName;
            }
        }
        return null;
    }
    
    private static void deleteUVHValues(final DataObject xmlDiffDO, final Long confFileID) throws Exception {
        final HashMap deleteOperations = ((WritableDataObject)xmlDiffDO).getActionsFor("delete");
        XML2DOUpdater.OUT.log(Level.INFO, "deleteUVHValues(xmlDiffDO, confFileID) :: {0}", deleteOperations);
        if (deleteOperations != null) {
            final DataObject uvhDO = DataAccess.constructDataObject();
            for (final String tName : deleteOperations.keySet()) {
                XML2DOUpdater.OUT.log(Level.INFO, "Processing the delete Operations for the table :: {0}", tName);
                for (final ActionInfo aInfo : deleteOperations.get(tName)) {
                    final Row row = aInfo.getValue();
                    final String tableName = row.getTableName();
                    if (!tableName.equals("UVHValues")) {
                        if (tableName.endsWith("_C")) {
                            continue;
                        }
                        final String columnName = getUVGColumnName(tableName);
                        if (columnName == null) {
                            continue;
                        }
                        Criteria criteria = new Criteria(new Column("UVHValues", "FILEID"), confFileID, 0);
                        criteria = criteria.and(new Criteria(Column.getColumn("UVHValues", "GENVALUES"), row.get(columnName), 0));
                        criteria = criteria.and(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), tableName, 0));
                        criteria = criteria.and(new Criteria(Column.getColumn("UVHValues", "COLUMN_NAME"), columnName.toLowerCase(Locale.ENGLISH), 0, false));
                        XML2DOUpdater.OUT.log(Level.INFO, "UVH Delete criteria :: {0}", criteria);
                        final DataObject dataObject = DataAccess.get("UVHValues", criteria);
                        if (dataObject.isEmpty()) {
                            continue;
                        }
                        dataObject.deleteRows("UVHValues", (Criteria)null);
                        uvhDO.merge(dataObject);
                    }
                }
            }
            XML2DOUpdater.OUT.log(Level.INFO, "Merging following UVHValues delete entries in DiffDO, {0}", uvhDO);
            xmlDiffDO.merge(uvhDO);
        }
    }
    
    static void removeXml(final File existingFile, final File backupFile, final DOXMLChangeListener doXmlChangeListener) throws Exception {
        final String serverHome = new File(XML2DOUpdater.server_home).getCanonicalPath();
        String fileToBeRemoved = existingFile.getCanonicalPath().substring(serverHome.length() + 1);
        fileToBeRemoved = fileToBeRemoved.replaceAll("\\\\", "/");
        init(fileToBeRemoved);
        final Map patternValues = getPatternValues();
        final File f = (XML2DOUpdater.isIntallProgress && backupFile != null && backupFile.exists()) ? backupFile : existingFile;
        final DataObject data = Xml2DoConverter.transform(f.toURI().toURL(), true, patternValues);
        DataObject newDO = (DataObject)data.clone();
        ((WritableDataObject)newDO).clearOperations();
        final List tableNames = PersistenceUtil.sortTables(newDO.getTableNames());
        for (int i = tableNames.size() - 1; i >= 0; --i) {
            final String tableName = tableNames.get(i);
            final Iterator iterator = data.getRows(tableName);
            while (iterator.hasNext()) {
                newDO.deleteRow(iterator.next());
            }
        }
        final Criteria confFileCri = new Criteria(new Column("ConfFile", "URL"), "*" + fileToBeRemoved, 2);
        final DataObject confFileDO = DataAccess.get("ConfFile", confFileCri);
        if (!confFileDO.isEmpty()) {
            final Row cfRow = confFileDO.getFirstRow("ConfFile");
            confFileDO.deleteRow(cfRow);
            newDO.merge(confFileDO);
            deleteUVHValues(newDO, (Long)cfRow.get("FILEID"));
        }
        XML2DOUpdater.OUT.log(Level.INFO, "Data to be removed :: {0} ", newDO);
        final XmlChangeNotifyObject notifyObj = new DoXmlChangeNotifyObject();
        notifyObj.setExistingXMLDO(data);
        notifyObj.setLatestXMLDO(null);
        notifyObj.setXMLDiffDO(newDO);
        XML2DOUpdater.OUT.log(Level.INFO, "Table Level configuration Handler started for :: {0}", XML2DOUpdater.fileToBeUpdated);
        TableLevelConfigurationHandlerUtil.invokeHandlers(data);
        if (doXmlChangeListener != null) {
            final boolean proceed = doXmlChangeListener.preInvoke(fileToBeRemoved, notifyObj);
            XML2DOUpdater.OUT.log(Level.INFO, "doXmlChangeListener preInvoke returns :: {0}", proceed);
            if (!proceed) {
                return;
            }
        }
        newDO = notifyObj.getXMLDiffDO();
        XML2DOUpdater.OUT.log(Level.INFO, "Going to update the DiffDO in DB, {0}", newDO);
        final DataObject commitedDO = DataAccess.update(newDO);
        if (doXmlChangeListener != null) {
            doXmlChangeListener.postInvoke(fileToBeRemoved, commitedDO);
        }
    }
    
    static {
        CLASS_NAME = XML2DOUpdater.class.getName();
        OUT = Logger.getLogger(XML2DOUpdater.CLASS_NAME);
        XML2DOUpdater.opPreference = 1;
        XML2DOUpdater.fileToBeUpdated = null;
        XML2DOUpdater.isJarFile = false;
        XML2DOUpdater.isIntallProgress = true;
        XML2DOUpdater.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
