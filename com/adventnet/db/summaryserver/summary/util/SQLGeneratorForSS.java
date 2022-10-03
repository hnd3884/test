package com.adventnet.db.summaryserver.summary.util;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.sql.SQLProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class SQLGeneratorForSS
{
    Logger logger;
    private static SQLGeneratorForSS objSQLGeneratorForSS;
    private static String sourceClass;
    
    public SQLGeneratorForSS() {
        this.logger = Logger.getLogger("SummarySyncLogger");
    }
    
    public static SQLGeneratorForSS getInstance() {
        if (SQLGeneratorForSS.objSQLGeneratorForSS == null) {
            SQLGeneratorForSS.objSQLGeneratorForSS = new SQLGeneratorForSS();
        }
        return SQLGeneratorForSS.objSQLGeneratorForSS;
    }
    
    void executeNativeQuery(final int sqlIdentifier, final Properties variableProps, final String sourceMethod) throws Exception {
        try {
            final String sql = SQLProvider.getInstance().getSQLString(sqlIdentifier, variableProps);
            this.executeNativeQuery(sql, sourceMethod);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception occurred while data synchronization..", e);
            throw e;
        }
    }
    
    void executeNativeQuery(final String sql, final String sourceMethod) throws Exception {
        Connection connection = null;
        try {
            final RelationalAPI relationalAPI = RelationalAPI.getInstance();
            connection = relationalAPI.getConnection();
            this.logger.log(Level.INFO, "Going to execute the query : " + sql);
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            this.logger.log(Level.INFO, "Executed " + sourceMethod + " query successfully..");
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception occurred while data synchronization..", e);
            throw e;
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public void createMssqlStagingTable(final String table) throws Exception {
        try {
            final String sourceMethod = "createMssqlStagingTable";
            final int sqlIdentifier = 33002;
            final Properties variableProps = new Properties();
            variableProps.setProperty("%TABLE_NAME%", table);
            this.executeNativeQuery(sqlIdentifier, variableProps, sourceMethod);
            this.logger.log(Level.INFO, "Successfully created staging table");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in createMssqlStagingTable ", e);
            throw e;
        }
    }
    
    public void createPostgresStagingTable(final String table) throws Exception {
        try {
            final String sourceMethod = "createPostgresStagingTable";
            final int sqlIdentifier = 33002;
            final Properties variableProps = new Properties();
            variableProps.setProperty("%TABLE_NAME%", table);
            this.executeNativeQuery(sqlIdentifier, variableProps, sourceMethod);
            this.logger.log(Level.INFO, "Successfully created staging table");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in createPostgresStagingTable ", e);
            throw e;
        }
    }
    
    public void copyMssqlStagingTable(final String table, final String filePath) throws Exception {
        try {
            final String sourceMethod = "copyMssqlStagingTable";
            final int sqlIdentifier = 33000;
            final Properties variableProps = new Properties();
            variableProps.setProperty("%TABLE_NAME%", table);
            variableProps.setProperty("%FILE_PATH%", filePath);
            this.executeNativeQuery(sqlIdentifier, variableProps, sourceMethod);
            this.logger.log(Level.INFO, "Successfully copied to staging table");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in copyMssqlStagingTable ", e);
            throw e;
        }
    }
    
    public void copyPostgresStagingTable(final String table, final String filePath) throws Exception {
        try {
            final String sourceMethod = "copyPostgresStagingTable";
            final int sqlIdentifier = 33000;
            final Properties variableProps = new Properties();
            variableProps.setProperty("%COL_LIST%", "");
            variableProps.setProperty("%TABLE_NAME%", table);
            variableProps.setProperty("%FILE_PATH%", filePath);
            this.executeNativeQuery(sqlIdentifier, variableProps, sourceMethod);
            this.logger.log(Level.INFO, "Successfully copied to staging table");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in copyPostgresStagingTable ", e);
            throw e;
        }
    }
    
    public void upsertPostgresOriginalTable(final String table, final String tempTable) throws Exception {
        try {
            final String sourceMethod = "upsertPostgresOriginalTable";
            final TableDefinition tDef = MetaDataUtil.getTableDefinitionByName(table);
            final List primaryKeysList = tDef.getPrimaryKey().getColumnList();
            String placeHolderValue = "";
            final int cntOfPrimaryKeys = primaryKeysList.size();
            if (cntOfPrimaryKeys > 1) {
                for (int index = 0; index < cntOfPrimaryKeys; ++index) {
                    final String primaryKey = tDef.getPrimaryKey().getColumnList().get(index);
                    placeHolderValue += primaryKey;
                    if (index + 1 != cntOfPrimaryKeys) {
                        placeHolderValue += ",";
                    }
                }
            }
            else {
                final String primaryKey = placeHolderValue = tDef.getPrimaryKey().getColumnList().get(0);
            }
            final List columnList = tDef.getColumnNames();
            final Properties variableProps = new Properties();
            variableProps.setProperty("%TABLE_NAME%", table);
            variableProps.setProperty("%TEMP_TABLE_NAME%", tempTable);
            variableProps.setProperty("%PRIMARY_KEY%", placeHolderValue);
            String cols = " ";
            String sql = SQLProvider.getInstance().getSQLString(33001, variableProps);
            for (int index2 = 0; index2 < columnList.size(); ++index2) {
                final String currCol = columnList.get(index2).toString();
                this.logger.log(Level.FINE, "Current column name: " + columnList.get(index2));
                if (!currCol.equalsIgnoreCase("PASSWORD")) {
                    cols = cols + currCol + " = EXCLUDED." + currCol;
                }
                else {
                    cols = cols + "\"" + currCol + "\"" + " = EXCLUDED." + "\"" + currCol + "\"";
                }
                if (index2 + 1 != columnList.size()) {
                    cols += ",";
                }
                else {
                    cols += ";";
                }
            }
            sql += cols;
            this.executeNativeQuery(sql, sourceMethod);
            this.logger.log(Level.INFO, "Successfully merged to target table");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in upsertPostgresOriginalTable ", e);
            throw e;
        }
    }
    
    public void upsertMssqlOriginalTable(final String table, final String tempTable) throws Exception {
        try {
            final String sourceMethod = "upsertMssqlOriginalTable";
            final TableDefinition tDef = MetaDataUtil.getTableDefinitionByName(table);
            final List primaryKeysList = tDef.getPrimaryKey().getColumnList();
            final int cntOfPrimaryKeys = primaryKeysList.size();
            String placeHolderValue = "";
            if (cntOfPrimaryKeys > 1) {
                for (int index = 0; index < cntOfPrimaryKeys; ++index) {
                    final String primaryKey = tDef.getPrimaryKey().getColumnList().get(index);
                    placeHolderValue = placeHolderValue + "s." + primaryKey + " = t." + primaryKey;
                    if (index + 1 != cntOfPrimaryKeys) {
                        placeHolderValue += " and ";
                    }
                }
            }
            else {
                final String primaryKey = tDef.getPrimaryKey().getColumnList().get(0);
                placeHolderValue = placeHolderValue + "s." + primaryKey + " = t." + primaryKey;
            }
            final List columnList = tDef.getColumnNames();
            final Properties variableProps = new Properties();
            variableProps.setProperty("%TABLE_NAME%", table);
            variableProps.setProperty("%TEMP_TABLE_NAME%", tempTable);
            variableProps.setProperty("%PRIMARY_KEY%", placeHolderValue);
            String cols = " ";
            String COL_HEADERS = "";
            String SOURCE_COL_HEADERS = "";
            for (int index2 = 0; index2 < columnList.size(); ++index2) {
                final String currCol = columnList.get(index2).toString();
                this.logger.log(Level.FINE, "Current column name: " + columnList.get(index2));
                cols = cols + "t." + currCol + " = s." + currCol;
                COL_HEADERS += currCol;
                SOURCE_COL_HEADERS = SOURCE_COL_HEADERS + "s." + currCol;
                if (index2 + 1 != columnList.size()) {
                    cols += ",";
                    COL_HEADERS += ",";
                    SOURCE_COL_HEADERS += ",";
                }
                else {
                    cols += " ";
                }
            }
            variableProps.setProperty("%COL_MAPPING%", cols);
            variableProps.setProperty("%COL_HEADERS%", COL_HEADERS);
            variableProps.setProperty("%SOURCE_COL_HEADERS%", SOURCE_COL_HEADERS);
            final String sql = SQLProvider.getInstance().getSQLString(33001, variableProps);
            this.executeNativeQuery(sql, sourceMethod);
            this.logger.log(Level.INFO, "Successfully merged to target table");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in upsertMssqlOriginalTable ", e);
            throw e;
        }
    }
    
    private List getTableColumnList(String table) {
        table += "_Summary";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("CustomViewConfiguration"));
            query.addSelectColumn(new Column("CustomViewConfiguration", "*"));
            final Criteria criteria = new Criteria(new Column("CustomViewConfiguration", "CVNAME"), (Object)table, 0, false);
            query.setCriteria(criteria);
            this.logger.log(Level.INFO, "Query to fetch sql id: " + query);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            Long queryId = 0L;
            this.logger.log(Level.FINE, "DO Fetched: " + dobj);
            final Iterator itr = dobj.getRows("CustomViewConfiguration");
            while (itr.hasNext()) {
                final Row row = itr.next();
                queryId = Long.parseLong(row.get("QUERYID").toString());
            }
            final SelectQuery selectQuery = QueryUtil.getSelectQuery((long)queryId);
            final int tablesInvolved = selectQuery.getTableList().size();
            if (tablesInvolved == 1) {
                return selectQuery.getSelectColumns();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in fetching sql id for table: " + table, e);
        }
        return null;
    }
    
    private void createPostgresStagingTableOnCommitDrop(final String table) throws Exception {
        try {
            final String sourceMethod = "createPostgresStagingTableOnCommitDrop";
            final int sqlIdentifier = 33003;
            final Properties variableProps = new Properties();
            variableProps.setProperty("%TABLE_NAME%", table);
            this.executeNativeQuery(sqlIdentifier, variableProps, sourceMethod);
            this.logger.log(Level.INFO, "Successfully created staging table with on commit drop ");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in createPostgresStagingTableOnCommitDrop ", e);
            throw e;
        }
    }
    
    static {
        SQLGeneratorForSS.objSQLGeneratorForSS = null;
        SQLGeneratorForSS.sourceClass = "SQLGeneratorForSS";
    }
}
