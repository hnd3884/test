package com.adventnet.db.archive;

import com.zoho.conf.Configuration;
import java.util.Set;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.HashSet;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import java.sql.Timestamp;
import java.util.Collection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TableArchiverUtil
{
    private static final Logger LOGGER;
    private static Map<Long, ArchivePolicyInfo> policyIdVsInfo;
    private static String policiesinProps;
    private static boolean isArchiveBackupEnabled;
    private static boolean isArchiveRotationEnabled;
    private static boolean isArchiveEnabled;
    private static String server_home;
    
    public static void addArchivePolicy(final List<Row> rowsList) throws Exception {
        addArchivePolicy(rowsList, true);
    }
    
    public static boolean isArchiveEnabled() {
        return TableArchiverUtil.isArchiveEnabled;
    }
    
    private static void addArchivePolicy(final List<Row> rowsList, final boolean reinitialize) throws Exception {
        final DataObject dobj = new WritableDataObject();
        for (final Row row : rowsList) {
            validateArchiveEnabledTables((String)row.get("TABLENAME"));
            dobj.addRow(row);
        }
        DataAccess.add(dobj);
        if (reinitialize) {
            reinitialiseArchive();
        }
    }
    
    public static int startArchive(final Long archivePolicyID) throws Exception {
        final ArchivePolicyInfo policyRow = getPolicy(archivePolicyID);
        return RelationalAPI.getInstance().getArchiveAdapter().startArchive(policyRow);
    }
    
    public static void reinitialiseArchive() throws Exception {
        initialiseArchive();
    }
    
    private static void initialiseArchive() throws Exception {
        if (TableArchiverUtil.policyIdVsInfo != null) {
            TableArchiverUtil.LOGGER.log(Level.INFO, "Re-initialising Archive Policy Details....");
        }
        TableArchiverUtil.policyIdVsInfo = new HashMap<Long, ArchivePolicyInfo>();
        Properties prop = null;
        InputStream input = null;
        final ArchivePolicyInfo policyInfo = null;
        try {
            if (new File(TableArchiverUtil.server_home + File.separator + "conf" + File.separator + "archivepolicy.properties").exists()) {
                input = new FileInputStream(new File(TableArchiverUtil.server_home + File.separator + "conf" + File.separator + "archivepolicy.properties"));
                prop = new Properties();
                prop.load(input);
            }
            loadFromTable(policyInfo, prop);
            loadFromProps(policyInfo, prop);
            if (TableArchiverUtil.policyIdVsInfo != null && !TableArchiverUtil.policyIdVsInfo.isEmpty()) {
                TableArchiverUtil.isArchiveEnabled = true;
            }
        }
        catch (final Exception ex) {
            TableArchiverUtil.LOGGER.log(Level.INFO, "Error in initialising Archive Policy Details....");
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
    }
    
    private static void loadFromTable(ArchivePolicyInfo policyInfo, final Properties prop) throws Exception {
        try {
            final DataObject archPolicy = DataAccess.get("ArchivePolicy", (Criteria)null);
            final Iterator itr = archPolicy.getRows("ArchivePolicy");
            while (itr.hasNext()) {
                final Row r = itr.next();
                policyInfo = new ArchivePolicyInfo();
                policyInfo.setArchivePolicyID((Long)r.get(1));
                policyInfo.setArchivePolicyName((String)r.get(2));
                validateArchiveEnabledTables((String)r.get(3));
                policyInfo.setTableName((String)r.get(3));
                policyInfo.setCriteria((String)r.get(4));
                policyInfo.setThreshold((Long)r.get(5));
                policyInfo.setArchiveMode((String)r.get(6));
                policyInfo.setDataSourceID((Long)r.get(7));
                setCommonArchiveProps(policyInfo, prop);
                TableArchiverUtil.policyIdVsInfo.put(policyInfo.getArchivePolicyID(), policyInfo);
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    private static void loadFromProps(ArchivePolicyInfo policyInfo, final Properties prop) throws Exception {
        try {
            if (prop != null) {
                TableArchiverUtil.isArchiveBackupEnabled = Boolean.valueOf(prop.getProperty("archive_backup", "false"));
                TableArchiverUtil.isArchiveRotationEnabled = Boolean.valueOf(prop.getProperty("archive_rotation", "false"));
                if ((TableArchiverUtil.policiesinProps = prop.getProperty("policies")) != null) {
                    final String[] policies_names = TableArchiverUtil.policiesinProps.split(",");
                    final List<Row> rowList = new ArrayList<Row>();
                    for (final String name : policies_names) {
                        policyInfo = new ArchivePolicyInfo();
                        final String policyid = prop.getProperty(name + ".archive_policy_id");
                        if (policyid == null || policyid.length() < 1) {
                            throw new Exception("Policy ID cannot be null");
                        }
                        policyInfo.setArchivePolicyID(Long.parseLong(policyid));
                        policyInfo.setArchivePolicyName(name);
                        final String tableName = prop.getProperty(name + ".tablename");
                        if (tableName == null || tableName.length() < 1) {
                            throw new Exception("Table Name in policy definition cannot be null");
                        }
                        validateArchiveEnabledTables(tableName);
                        policyInfo.setTableName(tableName);
                        final String mode = prop.getProperty(name + ".mode", "PULL");
                        policyInfo.setArchiveMode(mode);
                        final String criteria = prop.getProperty(name + ".criteria_string", "");
                        if ((criteria == null || criteria.length() < 0) && mode.equals("PUSH")) {
                            throw new Exception("Criteria in policy definition cannot be null for PUSH OPERATION");
                        }
                        policyInfo.setCriteria(criteria);
                        final String handler = prop.getProperty(name + ".notification_handler");
                        if (handler != null) {
                            final Class c = TableArchiverUtil.class.getClassLoader().loadClass(handler);
                            policyInfo.setNotificationHandler(c.newInstance());
                        }
                        final String threshold = prop.getProperty(name + ".threshold");
                        if (threshold != null && threshold.length() > 0) {
                            policyInfo.setThreshold(Long.parseLong(threshold));
                        }
                        final String dataSrcID = prop.getProperty(name + ".datasource_id", null);
                        policyInfo.setDataSourceID((dataSrcID != null && dataSrcID.length() > 0) ? Long.valueOf(Long.parseLong(dataSrcID)) : null);
                        setCommonArchiveProps(policyInfo, prop);
                        if (TableArchiverUtil.policyIdVsInfo.get(policyInfo.getArchivePolicyID()) == null) {
                            final Row nRow = new Row("ArchivePolicy");
                            nRow.set("ARCHIVE_POLICY_ID", policyInfo.getArchivePolicyID());
                            nRow.set("ARCHIVE_POLICY_NAME", policyInfo.getArchivePolicyName());
                            nRow.set("TABLENAME", policyInfo.getTableName());
                            nRow.set("CRITERIA_STRING", policyInfo.getCriteriaString());
                            nRow.set("THRESHOLD", policyInfo.getThreshold());
                            nRow.set("MODE", policyInfo.getArchiveMode());
                            nRow.set("DATASOURCE_ID", policyInfo.getDataSourceID());
                            rowList.add(nRow);
                        }
                        TableArchiverUtil.policyIdVsInfo.put(policyInfo.getArchivePolicyID(), policyInfo);
                    }
                    if (!rowList.isEmpty()) {
                        addArchivePolicy(rowList, false);
                    }
                }
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public static ArchivePolicyInfo getPolicy(final Long archivePolicyID) throws DataAccessException {
        try {
            final ArchivePolicyInfo archivePolicy = TableArchiverUtil.policyIdVsInfo.get(archivePolicyID);
            if (archivePolicy == null) {
                throw new DataAccessException("Archive Not Started...policy id {" + archivePolicyID + "} specified is invalid");
            }
            return archivePolicy;
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
            throw dae;
        }
    }
    
    private static void setCommonArchiveProps(final ArchivePolicyInfo archivePolicy, final Properties prop) throws Exception {
        try {
            if (prop != null) {
                archivePolicy.setBackupEnabled(Boolean.parseBoolean(prop.getProperty(archivePolicy.getArchivePolicyName() + ".backup", "true")));
                if (prop.getProperty(archivePolicy.getArchivePolicyName() + ".archive_pattern") != null) {
                    archivePolicy.setArchivePattern(prop.getProperty(archivePolicy.getArchivePolicyName() + ".archive_pattern"));
                }
                if (prop.getProperty(archivePolicy.getArchivePolicyName() + ".notification_handler") != null) {
                    final Class c = TableArchiverUtil.class.getClassLoader().loadClass(prop.getProperty(archivePolicy.getArchivePolicyName() + ".notification_handler"));
                    archivePolicy.setNotificationHandler(c.newInstance());
                }
                archivePolicy.setRotationCount(Integer.parseInt(prop.getProperty(archivePolicy.getArchivePolicyName() + ".count", "-1")));
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static boolean isParticipatedInArchiveProcess(final String tableName) {
        try {
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            sQuery.addSelectColumn(Column.getColumn(null, "*"));
            sQuery.setCriteria(new Criteria(Column.getColumn("ArchiveTableDetails", "ACTUAL_TABLENAME"), tableName, 0, false));
            sQuery.setRange(new Range(0, 1));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("ArchiveTableDetails", "ACTUAL_TABLENAME"), true));
            final DataObject tableDO = DataAccess.get(sQuery);
            final Row row = tableDO.getRow("ArchiveTableDetails");
            return row != null;
        }
        catch (final Exception ex) {
            TableArchiverUtil.LOGGER.log(Level.FINE, "Exception occured in isParticipatedInArchiveProcess() method . Hence returning false", ex);
            return false;
        }
    }
    
    public static List<ArchivePolicyInfo> getAllPolicies() {
        try {
            return new ArrayList<ArchivePolicyInfo>(TableArchiverUtil.policyIdVsInfo.values());
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Iterator getArchivedTableDetails() throws DataAccessException {
        try {
            final DataObject dobj = DataAccess.get("ArchiveTableDetails", (Criteria)null);
            return dobj.getRows("ArchiveTableDetails");
        }
        catch (final DataAccessException ex) {
            TableArchiverUtil.LOGGER.log(Level.FINE, "Exception occured in getting archive table details...", ex);
            return null;
        }
    }
    
    public static Iterator getArchivedTableDetails(final Long policyID) throws DataAccessException {
        try {
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            sQuery.addSelectColumn(Column.getColumn(null, "*"));
            sQuery.setCriteria(new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVE_POLICY_ID"), policyID, 0));
            final DataObject dobj = DataAccess.get(sQuery);
            return dobj.getRows("ArchiveTableDetails");
        }
        catch (final DataAccessException ex) {
            TableArchiverUtil.LOGGER.log(Level.FINE, "Exception occured in getting archive table details...", ex);
            return null;
        }
    }
    
    public static String getActualTable(final String archiveTable) {
        try {
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            sQuery.addSelectColumn(Column.getColumn(null, "*"));
            sQuery.setCriteria(new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVED_TABLENAME"), archiveTable, 0, false));
            final DataObject tableDO = DataAccess.get(sQuery);
            final Row row = tableDO.getRow("ArchiveTableDetails");
            if (row != null) {
                return (String)row.get(3);
            }
            return null;
        }
        catch (final Exception ex) {
            TableArchiverUtil.LOGGER.log(Level.FINE, "Exception occured in getArchiveTableDefinition(String archiveTable) returning null", ex);
            return null;
        }
    }
    
    public static int getArchivedTableCount(final Long policyID, final Timestamp archivedDate) throws DataAccessException {
        int count = 0;
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            final Column idCol = Column.getColumn("ArchiveTableDetails", "ARCHIVE_POLICY_ID");
            final Column col = Column.createFunction("COUNT", idCol);
            col.setDataType("BIGINT");
            col.setColumnAlias("ROW_COUNT");
            final Criteria cri = new Criteria(idCol, policyID, 0);
            if (archivedDate != null) {
                sQuery.setCriteria(cri.and(new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVED_DATE"), archivedDate, 4)));
            }
            else {
                sQuery.setCriteria(cri);
            }
            sQuery.addSelectColumn(col);
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery(sQuery, conn);
            while (ds.next()) {
                count = ds.getAsLong("ROW_COUNT").intValue();
            }
            return count;
        }
        catch (final Exception ex) {
            TableArchiverUtil.LOGGER.log(Level.INFO, "Error in getting count for the archived table returning zero.");
            ex.printStackTrace();
            return 0;
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static int getArchivedTableCount(final Long policyID) throws DataAccessException {
        return getArchivedTableCount(policyID, null);
    }
    
    public static boolean isArchiveBackupEnabled() {
        return TableArchiverUtil.isArchiveBackupEnabled;
    }
    
    public static boolean isArchiveRotationEnabled() {
        return TableArchiverUtil.isArchiveRotationEnabled;
    }
    
    public static List<String> getArchivedTables(final Long policyID) {
        final List<String> archivedList = new ArrayList<String>();
        try {
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            sQuery.addSelectColumn(Column.getColumn(null, "*"));
            if (policyID != null) {
                sQuery.setCriteria(new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVE_POLICY_ID"), policyID, 0));
            }
            final DataObject tableDO = DataAccess.get(sQuery);
            final Iterator itr = tableDO.getRows("ArchiveTableDetails");
            while (itr.hasNext()) {
                final Row r = itr.next();
                archivedList.add((String)r.get(4));
            }
            return archivedList;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static List<String> getAllArchivedTables() {
        return getArchivedTables(null);
    }
    
    public static boolean isArchiveTableExists(final String archiveTable) {
        try {
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            sQuery.addSelectColumn(Column.getColumn(null, "*"));
            sQuery.setCriteria(new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVED_TABLENAME"), archiveTable, 0, false));
            final DataObject tableDO = DataAccess.get(sQuery);
            final Iterator itr = tableDO.getRows("ArchiveTableDetails");
            return itr.hasNext();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static void addArchivePolicyToProps(final ArchivePolicyInfo policy) {
        final Properties prop = new Properties();
        ArchivePolicyInfo policyinfo = null;
        OutputStream output = null;
        try {
            if (!new File(TableArchiverUtil.server_home + File.separator + "conf" + File.separator + "archivepolicy.properties").exists()) {
                throw new FileNotFoundException("archivepolicy.properties file does not exist in conf folder.");
            }
            output = new FileOutputStream(new File(TableArchiverUtil.server_home + File.separator + "conf" + File.separator + "archivepolicy.properties"), true);
            prop.setProperty(policy.getArchivePolicyName() + ".backup", policy.isBackupEnabled().toString());
            prop.setProperty(policy.getArchivePolicyName() + ".archive_pattern", (policy.getArchivePattern() != null) ? policy.getArchivePattern() : "");
            prop.setProperty(policy.getArchivePolicyName() + ".count", String.valueOf(policy.getRotationCount()));
            prop.store(output, null);
            if ((policyinfo = TableArchiverUtil.policyIdVsInfo.get(policy.getArchivePolicyID())) != null) {
                policyinfo.setBackupEnabled(policy.isBackupEnabled());
                policyinfo.setArchivePattern(policy.getArchivePattern());
                policyinfo.setRotationCount(policy.getRotationCount());
                TableArchiverUtil.policyIdVsInfo.put(policy.getArchivePolicyID(), policyinfo);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            if (output != null) {
                try {
                    output.close();
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (output != null) {
                try {
                    output.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public static List<String> getTableNamesForBackup() {
        final List<String> tablesForBackup = new ArrayList<String>();
        if (isArchiveBackupEnabled()) {
            final List<ArchivePolicyInfo> policylist = getAllPolicies();
            for (final ArchivePolicyInfo policy : policylist) {
                if (policy.isBackupEnabled()) {
                    tablesForBackup.addAll(getArchivedTables(policy.getArchivePolicyID()));
                }
            }
        }
        return tablesForBackup;
    }
    
    public DataSet getArchiveData(final String archiveTable, final Criteria cri, final Connection conn) throws Exception {
        return RelationalAPI.getInstance().getArchiveAdapter().getArchiveData(archiveTable, cri, conn);
    }
    
    public DataSet getArchiveData(final SelectQuery query, final Connection conn) throws Exception {
        return RelationalAPI.getInstance().getArchiveAdapter().getArchiveData(query, conn);
    }
    
    public static void restoreFromArchive(final String archiveTable, final Criteria criteriaString) throws Exception {
        RelationalAPI.getInstance().getArchiveAdapter().restoreFromArchive(archiveTable, criteriaString);
    }
    
    private static void validateArchiveEnabledTables(final String tableNameToBeValidated) throws MetaDataException {
        final Set<TableDefinition> definitionsList = new HashSet<TableDefinition>();
        final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableNameToBeValidated);
        if (tableDef == null) {
            throw new MetaDataException("Table Definition not found for {" + tableNameToBeValidated + "} in Meta-Data");
        }
        definitionsList.add(tableDef);
        final List<TableDefinition> relatedDefinitions = MetaDataUtil.getAllRelatedTableDefinitions(tableNameToBeValidated);
        if (relatedDefinitions != null) {
            definitionsList.addAll(relatedDefinitions);
        }
        for (final TableDefinition td : definitionsList) {
            if (!td.creatable()) {
                throw new MetaDataException("Table {" + td.getTableName() + "} is not creatable, archiving is not supported for tables which are not creatable");
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(TableArchiverUtil.class.getName());
        TableArchiverUtil.policyIdVsInfo = null;
        TableArchiverUtil.policiesinProps = new String();
        TableArchiverUtil.isArchiveBackupEnabled = false;
        TableArchiverUtil.isArchiveRotationEnabled = false;
        TableArchiverUtil.isArchiveEnabled = false;
        TableArchiverUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        try {
            initialiseArchive();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
