package com.zoho.mickey.tools.crypto;

import java.util.HashMap;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import java.sql.SQLException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.RowIterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.persistence.DataAccess;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import com.zoho.mickey.dt.DefaultDTKeyModifier;
import com.zoho.conf.tree.ConfTreeBuilder;
import java.io.File;
import com.zoho.mickey.exception.KeyModificationException;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.mickey.dt.DTKeyModifier;
import com.zoho.conf.tree.ConfTree;
import java.util.Map;
import java.util.logging.Logger;

public class ECTagModifierUtil
{
    static Logger out;
    private static ECTagPrePostHandler handler;
    private static String dbType;
    private static Map<String, String> diffMap;
    private static ConfTree configTree;
    static String confFilePath;
    private static Map<String, DTKeyModifier> dtVsModifierClass;
    private static String newKey;
    private static ECTagModifierStatus statusObj;
    private static String topicName;
    
    public static void modifyECTag(final String newTag) throws KeyModificationException {
        try {
            ECTagModifierUtil.newKey = newTag;
            ECTagModifierUtil.dbType = PersistenceInitializer.getConfigurationValue("DBName");
            if (!ECTagModifierUtil.dbType.equalsIgnoreCase("postgres") && !ECTagModifierUtil.dbType.equalsIgnoreCase("mysql")) {
                throw new KeyModificationException("ECTag migration has been supported only for Postgres and Mysql!!");
            }
            initializeConfiguration();
            initializePrePostHandler();
            if (isHandlerExists()) {
                ECTagModifierUtil.handler.preHandle();
                ECTagModifierUtil.out.info("pre handler is called");
            }
            startECTagMigration();
            if (isHandlerExists()) {
                ECTagModifierUtil.handler.postHandle(true);
                ECTagModifierUtil.out.info("post handler is called");
            }
        }
        catch (final Exception e) {
            throw new KeyModificationException("Exception while modifying the ECTag " + e.getMessage(), (Throwable)e);
        }
        finally {
            try {
                PersistenceInitializer.stopDB();
                if (isHandlerExists()) {
                    ECTagModifierUtil.handler.postHandle(false);
                    ECTagModifierUtil.out.info("post handler is called");
                }
            }
            catch (final Exception e2) {
                throw new KeyModificationException("Exception while stopping the database " + e2.getMessage(), (Throwable)e2);
            }
        }
    }
    
    private static boolean isHandlerExists() {
        return ECTagModifierUtil.handler != null;
    }
    
    public static void initializeConfiguration() throws Exception {
        final File keyModifierConf = new File(System.getProperty("key.modifier.config.file.path", ECTagModifierUtil.confFilePath));
        if (!keyModifierConf.exists()) {
            ECTagModifierUtil.out.info("key_modifier.conf file not found...");
        }
        else {
            ECTagModifierUtil.configTree = ((ConfTreeBuilder)ConfTreeBuilder.confTree().fromConfFile(keyModifierConf.getCanonicalPath())).build();
            ECTagModifierUtil.out.info("key_modifier.conf file is initialized");
            ECTagModifierUtil.out.info("configurations:: " + ECTagModifierUtil.configTree);
        }
        initializeDTModifiers(ECTagModifierUtil.configTree);
        validateDTModifiers(ECTagModifierUtil.dtVsModifierClass);
    }
    
    public static String getNewKey() {
        return ECTagModifierUtil.newKey;
    }
    
    protected static void validateDTModifiers(final Map<String, DTKeyModifier> dtVsModifierClass) throws Exception {
        if (getDTKeyModifier("SCHAR") == null || getDTKeyModifier("SBLOB") == null) {
            throw new Exception(" DTKeyModifier has not been implemented for basic database encryption datatype (ie) schar, sblob");
        }
    }
    
    protected static void initializeDTModifiers(final ConfTree confTree) throws Exception {
        final Properties dbProps = PersistenceInitializer.getConfigurationProps(ECTagModifierUtil.dbType);
        dbProps.setProperty("ECTag", ECTagModifierUtil.newKey);
        if (confTree == null) {
            final DTKeyModifier keyModifier = (DTKeyModifier)Class.forName(DefaultDTKeyModifier.class.getName()).newInstance();
            keyModifier.initialize(dbProps);
            ECTagModifierUtil.dtVsModifierClass.put("schar", keyModifier);
            ECTagModifierUtil.dtVsModifierClass.put("sblob", keyModifier);
        }
        else {
            final List<String> dataTypes = Arrays.asList(confTree.get("migrate.dt").split(","));
            String className = null;
            for (String dataType : dataTypes) {
                dataType = dataType.trim();
                className = confTree.get(ECTagModifierUtil.dbType + "." + dataType + ".dtkeymodifier");
                if (className == null) {
                    className = confTree.get(dataType + ".dtkeymodifier");
                }
                final DTKeyModifier modifier = (DTKeyModifier)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                modifier.initialize(dbProps);
                ECTagModifierUtil.dtVsModifierClass.put(dataType, modifier);
            }
        }
        ECTagModifierUtil.out.info("dtVsModifierClass :: " + ECTagModifierUtil.dtVsModifierClass);
    }
    
    public static String getConfigurationValue(final String configName) {
        return (ECTagModifierUtil.configTree != null) ? ECTagModifierUtil.configTree.get(configName) : null;
    }
    
    private static void initializePrePostHandler() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String className = getConfigurationValue("key.modifier.prepost.handler");
        if (className == null) {
            className = DefaultECTagPrePostHandler.class.getName();
        }
        if (className != null) {
            ECTagModifierUtil.out.info("key.modifier.prepost.handler ::: " + className);
            ECTagModifierUtil.handler = (ECTagPrePostHandler)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        }
    }
    
    public static DTKeyModifier getDTKeyModifier(String dataType) {
        if (DataTypeUtil.isEDT(dataType)) {
            final DataTypeDefinition dt = DataTypeManager.getDataTypeDefinition(dataType);
            dataType = dt.getBaseType();
        }
        return ECTagModifierUtil.dtVsModifierClass.get(dataType.toLowerCase(Locale.ENGLISH));
    }
    
    private static void startECTagMigration() throws Exception {
        final TableDefinition auditTable = getAuditTableDefinition();
        if (MetaDataUtil.getTableDefinitionByName(auditTable.getTableName()) != null) {
            DataAccess.dropTable(auditTable.getTableName());
            ECTagModifierUtil.out.info("Audit table dropped");
        }
        DataAccess.createTable("Persistence", auditTable);
        ECTagModifierUtil.out.info("Audit table created");
        final List<String> tableNames = MetaDataUtil.getTableNamesInDefinedOrder();
        (ECTagModifierUtil.statusObj = new ECTagModifierStatus()).initialize(tableNames);
        ECTagModifierUtil.statusObj.setCurrentStatus(8);
        Messenger.publish(ECTagModifierUtil.topicName, (Object)ECTagModifierUtil.statusObj);
        try {
            DTKeyModifier modifier = null;
            List<String> colNames = null;
            ColumnDefinition colDef = null;
            String dataType = null;
            TableDefinition tabDef = null;
            int processedTableCount = 0;
            for (final String tableName : tableNames) {
                ECTagModifierUtil.statusObj.setProcessingTableName(tableName);
                tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                colNames = new ArrayList<String>(tabDef.getColumnNames());
                for (final String colName : colNames) {
                    colDef = tabDef.getColumnDefinitionByName(colName);
                    dataType = colDef.getDataType();
                    if (colDef.isEncryptedColumn()) {
                        modifier = getDTKeyModifier(dataType);
                        if (modifier == null) {
                            continue;
                        }
                        ECTagModifierUtil.statusObj.setProcessingColumnName(colName);
                        ECTagModifierUtil.statusObj.setCurrentStatus(1);
                        Messenger.publish(ECTagModifierUtil.topicName, (Object)ECTagModifierUtil.statusObj);
                        modifier.changeKey(tableName, colName);
                        if (getConfigurationValue("run.sanity") != null && getConfigurationValue("run.sanity").equals("true")) {
                            modifier.sanitize(tableName, colName, (Map)ECTagModifierUtil.diffMap);
                        }
                        ECTagModifierUtil.statusObj.setCurrentStatus(7);
                        Messenger.publish(ECTagModifierUtil.topicName, (Object)ECTagModifierUtil.statusObj);
                    }
                }
                ++processedTableCount;
                ECTagModifierUtil.statusObj.setCompletedTableCount(processedTableCount);
                Messenger.publish(ECTagModifierUtil.topicName, (Object)ECTagModifierUtil.statusObj);
            }
            if (!ECTagModifierUtil.diffMap.isEmpty()) {
                ECTagModifierUtil.out.severe("Diff identified:: " + ECTagModifierUtil.diffMap);
                throw new Exception("There is difference in the encrypted data!!. Please check logs for diff map!!");
            }
            dropTempColumn();
            ECTagModifierUtil.statusObj.setCurrentStatus(9);
            Messenger.publish(ECTagModifierUtil.topicName, (Object)ECTagModifierUtil.statusObj);
        }
        catch (final Exception e) {
            ECTagModifierUtil.out.severe("Error while reencrypting with new ectag:: " + e.getMessage());
            e.printStackTrace();
            try {
                revertData();
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
            throw e;
        }
    }
    
    private static void dropTempColumn() throws SQLException {
        try {
            final DataObject dobj = DataAccess.get("ECMStatus", new Criteria(new Column("ECMStatus", "STATUS"), (Object)6, 0));
            if (!dobj.isEmpty()) {
                final RowIterator itr = (RowIterator)dobj.getRows("ECMStatus");
                Row row = null;
                String tableName = null;
                String columnName = null;
                TableDefinition td = null;
                ColumnDefinition cd = null;
                DTKeyModifier modifier = null;
                while (itr.hasNext()) {
                    row = (Row)itr.next();
                    tableName = (String)row.get("TABLENAME");
                    columnName = (String)row.get("COLUMNNAME");
                    td = MetaDataUtil.getTableDefinitionByName(tableName);
                    cd = td.getColumnDefinitionByName(columnName);
                    modifier = getDTKeyModifier(cd.getDataType());
                    modifier.cleanUp(tableName, columnName, true);
                }
            }
        }
        catch (final Exception e) {
            throw new SQLException("Exception while dropping the temp column ", e);
        }
    }
    
    private static void revertData() throws Exception {
        try {
            final DataObject dobj = DataAccess.get("ECMStatus", (Criteria)null);
            if (!dobj.isEmpty()) {
                final RowIterator itr = (RowIterator)dobj.getRows("ECMStatus");
                Row row = null;
                String tableName = null;
                String columnName = null;
                TableDefinition td = null;
                ColumnDefinition cd = null;
                DTKeyModifier modifier = null;
                while (itr.hasNext()) {
                    row = (Row)itr.next();
                    tableName = (String)row.get("TABLENAME");
                    columnName = (String)row.get("COLUMNNAME");
                    td = MetaDataUtil.getTableDefinitionByName(tableName);
                    cd = td.getColumnDefinitionByName(columnName);
                    modifier = getDTKeyModifier(cd.getDataType());
                    modifier.cleanUp(tableName, columnName, false);
                }
            }
        }
        catch (final Exception e) {
            throw new SQLException("Exception while dropping the temp column ", e);
        }
    }
    
    public static void addStatusRow(final String tableName, final String columnName) throws DataAccessException {
        final DataObject dob = (DataObject)new WritableDataObject();
        final Row row = new Row("ECMStatus");
        row.set("TABLENAME", (Object)tableName);
        row.set("COLUMNNAME", (Object)columnName);
        row.set("STATUS", (Object)1);
        dob.addRow(row);
        DataAccess.add(dob);
        ECTagModifierUtil.out.info("stating migration for the table:: " + tableName + " col name:: " + columnName);
    }
    
    public static void updateStatus(final String tableName, final String columnName, final int status) throws DataAccessException {
        final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("ECMStatus");
        uq.setUpdateColumn("STATUS", (Object)status);
        Criteria cri = new Criteria(new Column("ECMStatus", "TABLENAME"), (Object)tableName, 0);
        cri = cri.and(new Criteria(new Column("ECMStatus", "COLUMNNAME"), (Object)columnName, 0));
        uq.setCriteria(cri);
        DataAccess.update(uq);
        ECTagModifierUtil.out.info("status " + status + " update for the table " + tableName + " of the column : " + columnName);
    }
    
    private static TableDefinition getAuditTableDefinition() throws MetaDataException {
        final TableDefinition td = new TableDefinition();
        final PrimaryKeyDefinition pk = new PrimaryKeyDefinition();
        td.setTableName("ECMStatus");
        final ColumnDefinition colDef = new ColumnDefinition();
        colDef.setColumnName("TABLENAME");
        colDef.setDataType("CHAR");
        colDef.setMaxLength(255);
        td.addColumnDefinition(colDef);
        final ColumnDefinition colDef2 = new ColumnDefinition();
        colDef2.setColumnName("COLUMNNAME");
        colDef2.setDataType("CHAR");
        colDef2.setMaxLength(255);
        td.addColumnDefinition(colDef2);
        pk.setTableName(td.getTableName());
        pk.setName(td.getTableName() + "_PK");
        pk.addColumnName(colDef.getColumnName());
        pk.addColumnName(colDef2.getColumnName());
        td.setPrimaryKey(pk);
        final ColumnDefinition c1 = new ColumnDefinition();
        c1.setColumnName("STATUS");
        c1.setDataType("INTEGER");
        c1.setDefaultValue((Object)1);
        td.addColumnDefinition(c1);
        td.setModuleName("Persitence");
        return td;
    }
    
    static {
        ECTagModifierUtil.out = Logger.getLogger(ECTagModifierUtil.class.getName());
        ECTagModifierUtil.handler = null;
        ECTagModifierUtil.dbType = null;
        ECTagModifierUtil.diffMap = new HashMap<String, String>();
        ECTagModifierUtil.configTree = null;
        ECTagModifierUtil.confFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "key_modifier.conf";
        ECTagModifierUtil.dtVsModifierClass = new HashMap<String, DTKeyModifier>();
        ECTagModifierUtil.newKey = null;
        ECTagModifierUtil.statusObj = null;
        ECTagModifierUtil.topicName = "ECTagModifier";
    }
}
