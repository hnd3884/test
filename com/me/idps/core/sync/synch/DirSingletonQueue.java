package com.me.idps.core.sync.synch;

import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataObject;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.HashMap;
import org.json.simple.JSONObject;
import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.sync.db.DirectoryTempDataHandler;
import com.me.idps.core.sync.db.DirCoreDBhandler;
import java.util.logging.Logger;
import com.me.idps.core.util.DirQueue;

public abstract class DirSingletonQueue extends DirQueue
{
    private TABLE tablePkGen;
    private Logger logger;
    private static final long START_ID = 9000000L;
    
    protected TABLE getTablePKGen() {
        return null;
    }
    
    protected abstract Logger getLogger();
    
    public DirSingletonQueue() {
        final TABLE genPKforTable = this.getTablePKGen();
        if (this.authenticatePKallocationRequest(genPKforTable.id, 1)) {
            this.tablePkGen = genPKforTable;
            this.logger = this.getLogger();
        }
    }
    
    private boolean authenticatePKallocationRequest(final int genPKforTable, final int numOfPKsrequired) {
        return ((TABLE.DIRRESRELTBL.id == genPKforTable && this instanceof DirCoreDBhandler) || (TABLE.DIROBJTMPTBL.id == genPKforTable && this instanceof DirectoryTempDataHandler)) && numOfPKsrequired > 0;
    }
    
    public long[] allocatePK(final int numOfPKsrequired, final boolean obtainDBlock) throws Exception {
        Connection connection = null;
        try {
            final int tcTableId = getTableID();
            if (this.authenticatePKallocationRequest(tcTableId, numOfPKsrequired)) {
                Long curPKid = null;
                final long[] pkRange = new long[2];
                final Criteria criteria = new Criteria(Column.getColumn("DirPKGenerator", "TABLE_ID"), (Object)tcTableId, 0);
                connection = RelationalAPI.getInstance().getConnection();
                connection.setAutoCommit(false);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirPKGenerator"));
                selectQuery.addSelectColumn(Column.getColumn("DirPKGenerator", "PK_ID"));
                selectQuery.setCriteria(criteria);
                String selectQueryStr = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
                if (obtainDBlock && DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
                    selectQueryStr += " FOR UPDATE";
                }
                final JSONArray jsonArray = IdpsUtil.executeSelectQuery(connection, selectQueryStr, selectQuery.getSelectColumns());
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    final JSONObject jsonObject = (JSONObject)jsonArray.get(0);
                    curPKid = Long.valueOf(String.valueOf(jsonObject.get((Object)"PK_ID")));
                }
                else {
                    curPKid = 9000000L;
                    final HashMap<String, TableDefinition> tdMap = new HashMap<String, TableDefinition>();
                    tdMap.put("TABLE_ID", MetaDataUtil.getTableDefinitionByName("DirPKGenerator"));
                    final DataObject dataObject = (DataObject)new WritableDataObject();
                    final Row row = new Row("DirPKGenerator");
                    row.set("TABLE_ID", (Object)tcTableId);
                    row.set("PK_ID", (Object)9000000L);
                    dataObject.addRow(row);
                    SyMUtil.getPersistenceLite().add(dataObject);
                }
                final long startPKval = curPKid + 1L;
                final long endPKval = curPKid + 1L + numOfPKsrequired - 1L;
                this.logger.log(Level.INFO, "allocating pks from {0} to {1} (both inclusive) for {2}", new String[] { String.valueOf(startPKval), String.valueOf(endPKval), (TABLE.DIROBJTMPTBL.id == tcTableId) ? "DirObjTmp" : "DirResRel" });
                pkRange[0] = startPKval;
                pkRange[1] = endPKval;
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirPKGenerator");
                updateQuery.setCriteria(criteria);
                updateQuery.setUpdateColumn("PK_ID", (Object)endPKval);
                DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
                connection.commit();
                return pkRange;
            }
            throw new Exception("PK allocation request denied");
        }
        catch (final Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in rolling back connection", ex2);
            }
            throw ex;
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex3) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection while allocating", ex3);
            }
        }
    }
    
    protected long[] allocatePKs(final int numOfPKsrequired) throws Exception {
        long[] pkRange = new long[2];
        if (this.authenticatePKallocationRequest(this.tablePkGen.id, numOfPKsrequired)) {
            setTableID(this.tablePkGen.id);
            pkRange = IdpsFactoryProvider.getIdpsProdEnvAPI().allocatePKs(this, numOfPKsrequired);
            clearTableID();
        }
        else {
            pkRange[1] = (pkRange[0] = -1L);
        }
        return pkRange;
    }
    
    private Long getmax(final Connection connection, final SelectQuery query) throws Exception {
        Long maxVal = 0L;
        final String sqlQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
        final DMDataSetWrapper ddsw = DMDataSetWrapper.executeQuery(connection, sqlQuery);
        while (ddsw.next()) {
            final String curValue = String.valueOf(ddsw.getValue("max"));
            if (!SyMUtil.isStringEmpty(curValue)) {
                maxVal += Long.valueOf(curValue);
            }
        }
        return maxVal;
    }
    
    private long getmaxID(final Connection connection, final String tableName, final String maxColName) throws Exception {
        final SelectQuery maxTempIDQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        maxTempIDQuery.addSelectColumn(IdpsUtil.getMaxOfColumn(tableName, maxColName, "max", -5));
        long maxTempIDval = this.getmax(connection, maxTempIDQuery);
        if (maxTempIDval == 0L) {
            maxTempIDval = 9000000L;
        }
        return maxTempIDval;
    }
    
    private long getCurOffset(final Connection connection, final TABLE table_id) throws Exception {
        long curOffSet = 0L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirPKGenerator"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DirPKGenerator", "TABLE_ID"), (Object)table_id.id, 0));
        selectQuery.addSelectColumn(Column.getColumn("DirPKGenerator", "PK_ID"));
        final JSONArray jsArray = IdpsUtil.executeSelectQuery(connection, selectQuery);
        if (jsArray != null && !jsArray.isEmpty()) {
            final JSONObject jsonObject = (JSONObject)jsArray.get(0);
            final String curVal = String.valueOf(jsonObject.get((Object)"PK_ID"));
            if (!SyMUtil.isStringEmpty(curVal)) {
                curOffSet = Long.valueOf(curVal);
            }
        }
        if (curOffSet == 0L) {
            curOffSet = 9000000L;
        }
        return curOffSet;
    }
    
    private void reEvaluate(final Connection connection, final TABLE tableID) throws Exception {
        if (tableID != null) {
            String tableName = null;
            String maxColName = null;
            if (TABLE.DIROBJTMPTBL.equals(tableID)) {
                tableName = "DirObjTmp";
                maxColName = "TEMP_ID";
            }
            else if (TABLE.DIRRESRELTBL.equals(tableID)) {
                tableName = "DirResRel";
                maxColName = "OBJ_ID";
            }
            if (!SyMUtil.isStringEmpty(tableName) && !SyMUtil.isStringEmpty(maxColName)) {
                final long maxID = this.getmaxID(connection, tableName, maxColName);
                this.logger.log(Level.INFO, "max {0} ID {1}", new Object[] { tableName, String.valueOf(maxID) });
                final long curIDoffSet = this.getCurOffset(connection, tableID);
                this.logger.log(Level.INFO, "{0} cur offset {1}", new Object[] { tableName, String.valueOf(curIDoffSet) });
                if (curIDoffSet > maxID && TABLE.DIROBJTMPTBL.equals(tableID)) {
                    final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirPKGenerator");
                    updateQuery.setCriteria(new Criteria(Column.getColumn("DirPKGenerator", "TABLE_ID"), (Object)tableID.id, 0));
                    updateQuery.setUpdateColumn("PK_ID", (Object)maxID);
                    final int affectedRows = DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, true);
                    this.logger.log(Level.INFO, "updated {0} row in {1}", new Object[] { String.valueOf(affectedRows), "DirPKGenerator" });
                }
            }
        }
    }
    
    private int getCount(final Connection connection, final SelectQuery query, final String append) throws Exception {
        String sqlQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
        if (sqlQuery.contains("WHERE")) {
            sqlQuery = sqlQuery.substring(0, sqlQuery.indexOf("WHERE"));
        }
        else if (sqlQuery.contains("where")) {
            sqlQuery = sqlQuery.substring(0, sqlQuery.indexOf("where"));
        }
        if (!SyMUtil.isStringEmpty(append)) {
            sqlQuery += append;
        }
        int count = 0;
        final DMDataSetWrapper ddsw = DMDataSetWrapper.executeQuery(connection, sqlQuery);
        while (ddsw.next()) {
            final String curValue = String.valueOf(ddsw.getValue("count"));
            if (!SyMUtil.isStringEmpty(curValue)) {
                count += Integer.valueOf(curValue);
            }
        }
        return count;
    }
    
    private int getInProgressSyncCount(final Connection connection) throws Exception {
        final SelectQuery syncInProgressQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomainSyncDetails"));
        syncInProgressQuery.addSelectColumn(IdpsUtil.getCountOfColumn("DMDomainSyncDetails", "DM_DOMAIN_ID", "count"));
        final int syncInProgressCount = this.getCount(connection, syncInProgressQuery, " WHERE FETCH_STATUS not in(901,921)");
        return syncInProgressCount;
    }
    
    private int getLiveSyncTokenCount(final Connection connection) throws Exception {
        final SelectQuery syncTokenQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        syncTokenQuery.addSelectColumn(IdpsUtil.getCountOfColumn("DirectorySyncDetails", "SYNC_TOKEN_ID", "count"));
        final int syncInProgressCount = this.getCount(connection, syncTokenQuery, null);
        return syncInProgressCount;
    }
    
    protected boolean reEvaluateOffset() {
        boolean response = false;
        if (this instanceof DirCoreDBhandler) {
            Connection conn = null;
            final RelationalAPI relapi = RelationalAPI.getInstance();
            try {
                conn = relapi.getConnection();
                final int liveSyncTokenCount = this.getLiveSyncTokenCount(conn);
                final int syncInProgressCount = this.getInProgressSyncCount(conn);
                this.logger.log(Level.INFO, "schema validation. sync in progress count : {0}, liveSyncTokenCount : {1}", new Object[] { String.valueOf(syncInProgressCount), String.valueOf(liveSyncTokenCount) });
                if (syncInProgressCount == 0 && liveSyncTokenCount == 0) {
                    final String schemaName = IdpsFactoryProvider.getIdpsProdEnvAPI().getSchemaName();
                    this.logger.log(Level.INFO, "no data can be inserted in temp tables under a sync tokens or have sync running, at the moment for {0} schema(search_path)", new Object[] { schemaName });
                    this.logger.log(Level.INFO, "Resetting pk offset");
                    this.reEvaluate(conn, TABLE.DIROBJTMPTBL);
                    this.reEvaluate(conn, TABLE.DIRRESRELTBL);
                    response = true;
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in re-evaluating offset", ex);
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex2);
                }
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception ex3) {
                    IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex3);
                }
            }
        }
        return response;
    }
    
    private static class TableThreadLocal
    {
        private static ThreadLocal<Integer> tableID;
        
        private static void clearTableID() {
            TableThreadLocal.tableID.remove();
        }
        
        private static Integer getTableID() {
            return TableThreadLocal.tableID.get();
        }
        
        private static void setTableID(final Integer tableId) {
            TableThreadLocal.tableID.set(tableId);
        }
        
        static {
            TableThreadLocal.tableID = new ThreadLocal<Integer>();
        }
    }
    
    protected enum TABLE
    {
        DIROBJTMPTBL(1), 
        DIRRESRELTBL(2);
        
        public int id;
        
        private TABLE(final int id) {
            this.id = id;
        }
    }
}
