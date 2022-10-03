package com.adventnet.persistence.migration;

import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.sql.ResultSet;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.net.InetAddress;
import java.io.File;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.AlterTableQueryImpl;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.HashMap;
import java.util.logging.Logger;

public class DefaultDDChangeListener implements DDChangeListener
{
    private static Logger logger;
    private MigrationType migrationType;
    private final HashMap<Long, String> tableIdVsTableName;
    
    public DefaultDDChangeListener() {
        this.migrationType = MigrationType.INSTALL;
        this.tableIdVsTableName = new HashMap<Long, String>();
    }
    
    @Override
    public void setMigrationType(final MigrationType migrationType) {
        this.migrationType = migrationType;
    }
    
    @Override
    public MigrationType getMigrationType() {
        return this.migrationType;
    }
    
    @Override
    public void postInvokeForAlterTable(final AlterTableQuery atq) throws Exception {
        final AlterOperation ao = atq.getAlterOperations().get(0);
        if (ao.getOperationType() == 1) {
            final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
            if (atq.getTableName().equals("AaaAccSession") && colDef.getColumnName().equals("USER_HOST_NAME")) {
                Connection con = null;
                Statement statement = null;
                try {
                    con = RelationalAPI.getInstance().getConnection();
                    statement = con.createStatement();
                    final String sql = "UPDATE AaaAccSession SET USER_HOST_NAME = '-'";
                    DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute SQL :: [" + sql + "]");
                    statement.execute(sql);
                    final AlterTableQuery newATQ = new AlterTableQueryImpl("AaaAccSession");
                    colDef.setNullable(false);
                    newATQ.modifyColumn("USER_HOST_NAME", colDef);
                    DataAccess.alterTable(newATQ);
                }
                finally {
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (con != null) {
                            con.close();
                        }
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (atq.getTableName().equals("TierComponent") && colDef.getColumnName().equals("PATH")) {
                final UpdateQuery uq = new UpdateQueryImpl(atq.getTableName());
                final String jbossHomeDir = System.getProperty("jboss.home.dir");
                uq.setUpdateColumn(colDef.getColumnName(), new File(jbossHomeDir).getCanonicalPath());
                uq.setUpdateColumn("HOST", InetAddress.getLocalHost().getHostName());
                DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute :: " + RelationalAPI.getInstance().getUpdateSQL(uq.getTableName(), uq.getUpdateColumns(), uq.getCriteria()));
                DataAccess.update(uq);
            }
            else if (atq.getTableName().equals("UVHValues")) {
                if (colDef.getColumnName().equals("TABLE_ID")) {
                    DefaultDDChangeListener.logger.log(Level.INFO, " Successfully UVHValues table revert changes are done");
                    this.generateValues_UVHValuesForRevert();
                }
                if (colDef.getColumnName().equals("COLUMN_NAME")) {
                    DefaultDDChangeListener.logger.log(Level.INFO, "Successfully UVHValues table install changes are done");
                    this.generateValues_UVHValues();
                }
            }
        }
        else if ((this.migrationType == MigrationType.UNINSTALL || this.migrationType == MigrationType.INSTALL_FAILURE) && ao.getOperationType() == 2 && ao.getTableName().equals("AaaLogin") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("DOMAINNAME")) {
            final ColumnDefinition cd = (ColumnDefinition)ao.getAlterObject();
            if (cd.getDefaultValue() == null) {
                final UpdateQuery uq = new UpdateQueryImpl("AaaLogin");
                uq.setUpdateColumn("DOMAINNAME", null);
                final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), "-", 0);
                uq.setCriteria(criteria);
                DataAccess.update(uq);
            }
        }
    }
    
    @Override
    public void postInvokeForCreateTable(final TableDefinition tabDef) throws Exception {
    }
    
    @Override
    public void postInvokeForDropTable(final TableDefinition tableDefinition) throws Exception {
    }
    
    @Override
    public boolean preInvokeForAlterTable(final AlterTableQuery atq) throws Exception {
        if (this.migrationType == MigrationType.INSTALL || this.migrationType == MigrationType.UNINSTALL_FAILURE) {
            final AlterOperation ao = atq.getAlterOperations().get(0);
            if (ao.getOperationType() == 1) {
                if (ao.getTableName().equals("Scheduled_Task") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("OFFSET_MS")) {
                    final AlterTableQuery newATQ = new AlterTableQueryImpl("Scheduled_Task");
                    newATQ.renameColumn("OFFSET", "OFFSET_MS");
                    final String query = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getSQLForAlterTable(newATQ);
                    DefaultDDChangeListener.logger.log(Level.INFO, "Skipping add column OFFSET_MS, drop column OFFSET queries and going to execute rename query :: " + query);
                    DataAccess.alterTable(newATQ);
                    return false;
                }
                if (ao.getTableName().equals("Calendar") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("WEEK")) {
                    this.installWeekColumn();
                    return false;
                }
            }
            if (ao.getOperationType() == 3 && ((ao.getTableName().equals("Scheduled_Task") && ((String)ao.getAlterObject()).equals("OFFSET")) || (ao.getTableName().equals("Calendar") && ((String)ao.getAlterObject()).equals("WEEK_OF_MONTH")))) {
                return false;
            }
            if (ao.getOperationType() == 2 && ao.getTableName().equals("AaaLogin") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("DOMAINNAME")) {
                final ColumnDefinition cd = (ColumnDefinition)ao.getAlterObject();
                if (cd.getDefaultValue() != null) {
                    final UpdateQuery uq = new UpdateQueryImpl("AaaLogin");
                    uq.setUpdateColumn("DOMAINNAME", "-");
                    final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), null, 0);
                    uq.setCriteria(criteria);
                    DataAccess.update(uq);
                }
                return true;
            }
        }
        else if (this.migrationType == MigrationType.UNINSTALL || this.migrationType == MigrationType.INSTALL_FAILURE) {
            final AlterOperation ao = atq.getAlterOperations().get(0);
            if (ao.getOperationType() == 1 && ((ao.getTableName().equals("Scheduled_Task") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("OFFSET")) || (ao.getTableName().equals("Calendar") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("WEEK_OF_MONTH")))) {
                return false;
            }
            if (ao.getOperationType() == 3) {
                if (ao.getTableName().equals("Scheduled_Task") && ((String)ao.getAlterObject()).equals("OFFSET_MS")) {
                    final AlterTableQuery newATQ = new AlterTableQueryImpl("Scheduled_Task");
                    newATQ.renameColumn("OFFSET_MS", "OFFSET");
                    final String query = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getSQLForAlterTable(newATQ);
                    DefaultDDChangeListener.logger.log(Level.INFO, "Skipping add column {0}, drop column {1} queries and going to execute rename query :: {2}", new String[] { "OFFSET", "OFFSET_MS", query });
                    DataAccess.alterTable(newATQ);
                    return false;
                }
                if (ao.getTableName().equals("Calendar") && ((String)ao.getAlterObject()).equals("WEEK")) {
                    this.revertWeekColumn();
                    return false;
                }
            }
            if (ao.getOperationType() == 2 && ((ao.getTableName().equals("ACSQLString") && atq.getColumnName().equals("SQL")) || (ao.getTableName().equals("AaaTablePermission") && atq.getColumnName().equals("CRITERIA")))) {
                DefaultDDChangeListener.logger.log(Level.INFO, "MaxLength Change from -1 to " + atq.getMaxLength() + " is skipped to execute in DB for column " + atq.getTableName() + "." + atq.getColumnName() + "(i.e. isExecutable set as false in AlterTableQuery).");
                atq.setIsExecutable(false);
            }
        }
        final AlterOperation ao = atq.getAlterOperations().get(0);
        if (ao.getOperationType() == 1 && ao.getTableName().equals("AaaAccSession") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("USER_HOST_NAME")) {
            ((ColumnDefinition)ao.getAlterObject()).setNullable(true);
            return true;
        }
        if (ao.getOperationType() == 2) {
            if (ao.getTableName().equals("OpenViewInContentArea") && ((ColumnDefinition)ao.getAlterObject()).getColumnName().equals("CONTENTAREANAME")) {
                final UpdateQuery uq2 = new UpdateQueryImpl("OpenViewInContentArea");
                uq2.setUpdateColumn("CONTENTAREANAME", "DEFAULTCONTENTAREA");
                final Criteria criteria2 = new Criteria(Column.getColumn("OpenViewInContentArea", "CONTENTAREANAME"), null, 0);
                uq2.setCriteria(criteria2);
                DataAccess.update(uq2);
                return true;
            }
            if (ao.getTableName().equals("AaaAccSession")) {
                final ColumnDefinition modifiedColumn = (ColumnDefinition)ao.getAlterObject();
                if (modifiedColumn.getColumnName().equals("STATUS") && modifiedColumn.getDefaultValue().equals("OPEN")) {
                    ((ColumnDefinition)ao.getAlterObject()).setDefaultValue("ACTIVE");
                }
                return true;
            }
        }
        return true;
    }
    
    @Override
    public boolean preInvokeForCreateTable(final TableDefinition tabDef) throws Exception {
        if (this.migrationType == MigrationType.INSTALL || this.migrationType == MigrationType.UNINSTALL_FAILURE) {
            if (tabDef.getTableName().equals("MsSQLErrorCode")) {
                final String oldTableName = "MssqlErrorCode";
                if (MetaDataUtil.getTableDefinitionByName(oldTableName) != null) {
                    DataAccess.dropTable(oldTableName);
                }
            }
        }
        else if (tabDef.getTableName().equals("MssqlErrorCode")) {
            final String oldTableName = "MsSQLErrorCode";
            if (MetaDataUtil.getTableDefinitionByName(oldTableName) != null) {
                DataAccess.dropTable(oldTableName);
            }
        }
        return true;
    }
    
    @Override
    public boolean preInvokeForDropTable(final TableDefinition tableDefinition) throws Exception {
        if (this.migrationType == MigrationType.INSTALL || this.migrationType == MigrationType.UNINSTALL_FAILURE) {
            if (tableDefinition.getTableName().equals("MssqlErrorCode")) {
                return false;
            }
        }
        else if (tableDefinition.getTableName().equals("MsSQLErrorCode")) {
            return false;
        }
        return true;
    }
    
    @Override
    public void handleExceptionForAlterTable(final AlterTableQuery atq, final Exception e) throws Exception {
        throw e;
    }
    
    @Override
    public void handleExceptionForCreateTable(final TableDefinition tabDef, final Exception e) throws Exception {
        throw e;
    }
    
    @Override
    public void handleExceptionForDropTable(final TableDefinition tabDef, final Exception e) throws Exception {
        throw e;
    }
    
    private void generateValues_UVHValuesForRevert() throws Exception {
        final DataObject dataObject = DataAccess.get("UVHValues", (Criteria)null);
        final Iterator iterator = dataObject.getRows("UVHValues");
        DefaultDDChangeListener.logger.log(Level.INFO, "generateValuesForNewUVGColumn_REVERT :: dataObject :: {0}", dataObject);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String tableName = (String)row.get("TABLE_NAME");
            if (null == tableName) {
                DefaultDDChangeListener.logger.log(Level.SEVERE, "UVHValues Revert: TableName is  null for {0}", row);
            }
            else {
                final Long tableId = this.getTableID(tableName);
                if (null == tableId) {
                    DefaultDDChangeListener.logger.log(Level.INFO, "UVHValues Revert: Unable to find TableID for {0}", row);
                }
                else {
                    row.set("TABLE_ID", tableId);
                    dataObject.updateRow(row);
                }
            }
        }
        DefaultDDChangeListener.logger.log(Level.INFO, "UVHValues Revert: generateValuesFor TableID :: dataObject :: [{0}]", dataObject);
        DataAccess.update(dataObject);
    }
    
    private void generateValues_UVHValues() throws Exception {
        final DataObject dataObject = DataAccess.get("UVHValues", (Criteria)null);
        final Iterator iterator = dataObject.getRows("UVHValues");
        DefaultDDChangeListener.logger.log(Level.INFO, "generateValuesForNewUVGColumn :: dataObject :: {0}", dataObject);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            String tableName = null;
            String columnName = null;
            if (row.getTableDefinition().hasColumn("TABLE_ID")) {
                final Long tableId = (Long)row.get("TABLE_ID");
                if (tableId != null) {
                    tableName = this.tableIdVsTableName.get(tableId);
                    if (tableName == null) {
                        tableName = this.getTableName(tableId);
                    }
                }
            }
            final String uvhPattern = (String)row.get("PATTERN");
            final String[] patternVals = uvhPattern.split(":");
            if (tableName == null) {
                tableName = patternVals[0];
            }
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td == null) {
                continue;
            }
            ColumnDefinition cd = null;
            if (patternVals.length > 1) {
                columnName = patternVals[1];
                cd = td.getColumnDefinitionByName(columnName);
            }
            if (cd == null) {
                cd = this.getSingleUVHColumn(td);
                columnName = ((cd != null) ? cd.getColumnName() : null);
            }
            row.set("TABLE_NAME", tableName);
            row.set("COLUMN_NAME", columnName);
            dataObject.updateRow(row);
        }
        DefaultDDChangeListener.logger.log(Level.INFO, "generateValuesFor Table, Column columns::\tdataObject :: [{0}]", dataObject);
        DataAccess.update(dataObject);
    }
    
    private long getTableID(final String tableName) throws SQLException, QueryConstructionException {
        Connection c = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            c = RelationalAPI.getInstance().getConnection();
            s = c.createStatement();
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("TableDetails"));
            sq.addSelectColumn(Column.getColumn("TableDetails", "TABLE_ID"));
            sq.setCriteria(new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0));
            final String sql = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getSQLForSelect(sq);
            DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute the SQL :: [{0}]", sql);
            rs = s.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if (s != null) {
                    s.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if (c != null) {
                    c.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return -1L;
    }
    
    private String getTableName(final Long tableId) throws Exception {
        final SelectQuery sq = new SelectQueryImpl(new Table("TableDetails"));
        sq.addSelectColumn(Column.getColumn("TableDetails", "TABLE_ID"));
        sq.addSelectColumn(Column.getColumn("TableDetails", "TABLE_NAME"));
        final Criteria criteria = new Criteria(Column.getColumn("TableDetails", "TABLE_ID"), tableId, 0);
        sq.setCriteria(criteria);
        DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute this SelectQuery --> {0}", new Object[] { RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getSQLForSelect(sq) });
        final DataObject data = DataAccess.get(sq);
        final String tableName = (String)data.getFirstRow("TableDetails").get("TABLE_NAME");
        this.tableIdVsTableName.put(tableId, tableName);
        return tableName;
    }
    
    private ColumnDefinition getSingleUVHColumn(final TableDefinition tDef) throws Exception {
        final ArrayList<ColumnDefinition> colDefs = (ArrayList<ColumnDefinition>)tDef.getColumnList();
        for (final ColumnDefinition colDef : colDefs) {
            final String uvgName = colDef.getUniqueValueGeneration().getGeneratorName();
            if (uvgName != null) {
                DefaultDDChangeListener.logger.info("Returning ColumnDefinition from getSingleUVHColumn method");
                return colDef;
            }
        }
        DefaultDDChangeListener.logger.info("Returning null from getSingleUVHColumn method");
        return null;
    }
    
    private void installWeekColumn() throws Exception {
        final AlterTableQuery aq1 = new AlterTableQueryImpl("Calendar");
        aq1.renameColumn("WEEK_OF_MONTH", "WEEK");
        DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute the AlterTableQuery :: [{0}]", aq1);
        DataAccess.alterTable(aq1);
        DefaultDDChangeListener.logger.log(Level.INFO, "Successfully executed the AlterTableQuery :: [{0}]", aq1);
        final ColumnDefinition cd = MetaDataUtil.getTableDefinitionByName("Calendar").getColumnDefinitionByName("WEEK");
        final AlterTableQuery aq2 = new AlterTableQueryImpl("Calendar");
        cd.setDataType("BIGINT");
        cd.setDefaultValue(new Integer(-1));
        aq2.modifyColumn(cd.getColumnName(), cd);
        DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute the AlterTableQuery :: [{0}]", aq2);
        DataAccess.alterTable(aq2, false);
        DefaultDDChangeListener.logger.log(Level.INFO, "Successfully executed the AlterTableQuery :: [{0}]", aq2);
    }
    
    private void revertWeekColumn() throws Exception {
        final AlterTableQuery aq1 = new AlterTableQueryImpl("Calendar");
        aq1.renameColumn("WEEK", "WEEK_OF_MONTH");
        DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute the AlterTableQuery :: [{0}]", aq1);
        DataAccess.alterTable(aq1);
        DefaultDDChangeListener.logger.log(Level.INFO, "Successfully executed the AlterTableQuery :: [{0}]", aq1);
        final ColumnDefinition cd = MetaDataUtil.getTableDefinitionByName("Calendar").getColumnDefinitionByName("WEEK_OF_MONTH");
        final AlterTableQuery aq2 = new AlterTableQueryImpl("Calendar");
        cd.setDataType("INTEGER");
        cd.setDefaultValue(new Integer(-1));
        aq2.modifyColumn(cd.getColumnName(), cd);
        DefaultDDChangeListener.logger.log(Level.INFO, "Going to execute the AlterTableQuery :: [{0}]", aq2);
        DataAccess.alterTable(aq2, false);
        DefaultDDChangeListener.logger.log(Level.INFO, "Successfully executed the AlterTableQuery :: [{0}]", aq2);
    }
    
    static {
        DefaultDDChangeListener.logger = Logger.getLogger(DefaultDDChangeListener.class.getName());
    }
}
