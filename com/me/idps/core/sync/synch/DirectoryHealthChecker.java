package com.me.idps.core.sync.synch;

import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Array;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DeleteQuery;
import java.sql.Connection;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.me.idps.core.util.DirectoryQueryutil;

class DirectoryHealthChecker extends DirectoryQueryutil
{
    private static DirectoryHealthChecker directoryHealthChecker;
    
    public static DirectoryHealthChecker getInstance() {
        if (DirectoryHealthChecker.directoryHealthChecker == null) {
            DirectoryHealthChecker.directoryHealthChecker = new DirectoryHealthChecker();
        }
        return DirectoryHealthChecker.directoryHealthChecker;
    }
    
    private boolean checkIfADtypeRowsExist(final Iterator iterator, final String rowType) {
        boolean rowsPresent = false;
        if (iterator != null && iterator.hasNext()) {
            while (iterator.hasNext() && !rowsPresent) {
                final Row row = iterator.next();
                if (row != null) {
                    rowsPresent |= true;
                }
                IDPSlogger.ASYNCH.log(Level.INFO, "{0} row : {1}", new Object[] { rowType, String.valueOf(row) });
            }
        }
        else {
            IDPSlogger.ASYNCH.log(Level.INFO, "no {0} rows found", new Object[] { rowType });
        }
        return rowsPresent;
    }
    
    private void validateForWG(final String dmDomainName, final Long customerID) throws Exception {
        final Criteria dmDomainCri = new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)dmDomainName, 0, false).and(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
        selectQuery.setCriteria(dmDomainCri);
        selectQuery.addSelectColumn(Column.getColumn("DMDomain", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DMDomain", "CLIENT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DMDomain", "CUSTOMER_ID"));
        final DataObject dobj = IdpsUtil.getPersistenceLite().get(selectQuery);
        if (dobj != null && !dobj.isEmpty() && dobj.containsTable("DMDomain")) {
            final Criteria wgCri = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)1, 0);
            final Criteria opCri = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)2, 0);
            final boolean wgRowsPresent = this.checkIfADtypeRowsExist(dobj.getRows("DMDomain", wgCri), "wg");
            final boolean opRowsPresent = this.checkIfADtypeRowsExist(dobj.getRows("DMDomain", opCri), "op");
            if (opRowsPresent && wgRowsPresent) {
                IDPSlogger.ASYNCH.log(Level.INFO, dobj.toString());
                IDPSlogger.ASYNCH.log(Level.INFO, "wgRowsPresent : {0}; opRowsPresent : {1}", new Object[] { String.valueOf(wgRowsPresent), String.valueOf(opRowsPresent) });
                dobj.deleteRows("DMDomain", wgCri);
                IdpsUtil.getPersistenceLite().update(dobj);
                throw new Exception("domain duplication error;" + dmDomainName + ";" + customerID);
            }
        }
    }
    
    private void clearSyncedDirData(final Long dmDomainID) {
        IDPSlogger.AUDIT.log(Level.WARNING, "Oh no!.. having to clear daya and do a full sync all over again");
        final Criteria dmDomainCri = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjRegStrVal");
            deleteQuery.addJoin(new Join("DirObjRegStrVal", "DirResRel", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
            deleteQuery.setCriteria(dmDomainCri);
            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjRegIntVal");
            deleteQuery.addJoin(new Join("DirObjRegIntVal", "DirResRel", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
            deleteQuery.setCriteria(dmDomainCri);
            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjArrLngVal");
            deleteQuery.addJoin(new Join("DirObjArrLngVal", "DirResRel", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
            deleteQuery.setCriteria(dmDomainCri);
            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirResRel");
            deleteQuery.setCriteria(dmDomainCri);
            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                }
            }
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                }
            }
        }
    }
    
    private void reEvaluateAutoVAForDirTables() {
        if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
            IDPSlogger.DBO.log(Level.INFO, "pg db");
            ResultSet rs = null;
            Statement stmt = null;
            Connection connection = null;
            final StringBuilder selectQuery = new StringBuilder();
            try {
                boolean isValidSchemaName = false;
                final String schemaName = IdpsFactoryProvider.getIdpsProdEnvAPI().getSchemaName();
                if (!SyMUtil.isStringEmpty(schemaName) && !schemaName.equalsIgnoreCase("---")) {
                    isValidSchemaName = true;
                    IDPSlogger.DBO.log(Level.INFO, "schema {0}", new Object[] { schemaName });
                }
                else {
                    isValidSchemaName = false;
                }
                final boolean manualVAdisabled = DirectoryUtil.getInstance().isManualVAdisabled(true);
                IDPSlogger.DBO.log(Level.INFO, "isManualVAdisabled {0}", new Object[] { manualVAdisabled });
                final String template = "ALTER TABLE {0} SET (autovacuum_enabled = {1})";
                selectQuery.append("SELECT\n");
                selectQuery.append("    RELNAME,\n");
                selectQuery.append("    RELOPTIONS\n");
                selectQuery.append("FROM\n");
                selectQuery.append("    PG_CLASS\n");
                if (isValidSchemaName) {
                    selectQuery.append("INNER JOIN PG_NAMESPACE ON PG_CLASS.RELNAMESPACE = PG_NAMESPACE.OID\n");
                }
                selectQuery.append("WHERE\n");
                selectQuery.append("    (\n");
                selectQuery.append("        UPPER(RELNAME) IN(\n");
                selectQuery.append("            'DIROBJTMP',\n");
                selectQuery.append("            'DIROBJTMPDUPL',\n");
                selectQuery.append("            'DIROBJTMPREGINTVAL',\n");
                selectQuery.append("            'RESOURCETOPROFILESUMMARY',\n");
                selectQuery.append("            'DIROBJTMPREGSTRVAL',\n");
                selectQuery.append("            'DIROBJTMPARRSTRVAL',\n");
                selectQuery.append("            'DIROBJTMPDUPLATTR',\n");
                selectQuery.append("            'DIROBJTMPDUPLVAL',\n");
                selectQuery.append("            'DIRTMPAVAILABLERES',\n");
                selectQuery.append("            'DIRRESREL',\n");
                selectQuery.append("            'DIROBJREGINTVAL',\n");
                selectQuery.append("            'DIROBJREGSTRVAL',\n");
                selectQuery.append("            'DIROBJARRLNGVAL',\n");
                selectQuery.append("            'DIRECTORYEVENTTOKEN',\n");
                selectQuery.append("            'DIRECTORYEVENTDETAILS'\n");
                selectQuery.append("        )\n");
                selectQuery.append("    )\n");
                if (isValidSchemaName) {
                    selectQuery.append("AND (NSPNAME = '" + schemaName + "')");
                }
                final String finalQuery = selectQuery.toString();
                final JSONArray jsonArray = new JSONArray();
                connection = RelationalAPI.getInstance().getConnection();
                stmt = connection.createStatement();
                rs = stmt.executeQuery(finalQuery);
                while (rs != null & rs.next()) {
                    final String tableName = rs.getString("RELNAME".toLowerCase());
                    final JSONArray optionsAr = new JSONArray();
                    final Array relOptions = rs.getArray("RELOPTIONS".toLowerCase());
                    if (relOptions != null) {
                        try {
                            final String[] options = (String[])relOptions.getArray();
                            for (int i = 0; i < options.length; ++i) {
                                optionsAr.add((Object)options[i]);
                            }
                        }
                        catch (final Exception ex) {
                            IDPSlogger.ERR.log(Level.SEVERE, "exception in getting reloptions for " + tableName, ex);
                        }
                    }
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put((Object)"RELNAME", (Object)tableName);
                    jsonObject.put((Object)"RELOPTIONS", (Object)optionsAr);
                    jsonArray.add((Object)jsonObject);
                }
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    for (int j = 0; j < jsonArray.size(); ++j) {
                        final JSONObject jsonObject2 = (JSONObject)jsonArray.get(j);
                        final String tableName2 = String.valueOf(jsonObject2.get((Object)"RELNAME"));
                        final String relOptions2 = String.valueOf(jsonObject2.get((Object)"RELOPTIONS"));
                        boolean autoVAdisabled = false;
                        if (!SyMUtil.isStringEmpty(relOptions2) && relOptions2.toLowerCase().contains("autovacuum_enabled=false")) {
                            autoVAdisabled = true;
                        }
                        String finalTableName = tableName2;
                        if (isValidSchemaName) {
                            finalTableName = schemaName + "." + tableName2;
                        }
                        if (manualVAdisabled == autoVAdisabled) {
                            IDPSlogger.DBO.log(Level.INFO, "for {0} table, autoVA {1}abled, and manualVA {2}abled", new Object[] { finalTableName, autoVAdisabled ? "dis" : "en", manualVAdisabled ? "dis" : "en" });
                            IDPSlogger.DBO.log(Level.INFO, "hence {0}abling autoVA for {1} table", new Object[] { manualVAdisabled ? "en" : "dis", finalTableName });
                            final String query = MessageFormat.format(template, finalTableName, String.valueOf(manualVAdisabled));
                            this.executePGSpecificQueries(connection, query);
                        }
                    }
                }
                else {
                    IDPSlogger.SYNC.log(Level.INFO, "could not get autovacuum enabled Or disabled status of dir tables");
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                }
            }
            finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (final Exception ex3) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex3);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (final Exception ex3) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex3);
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex3) {
                    IDPSlogger.ERR.log(Level.SEVERE, null, ex3);
                }
            }
        }
    }
    
    boolean checkHealthAndQualifyIfFullSyncRequired(final Long dmDomainID, final String dmDomainName, final Long customerID, final Integer dmDomainClientID, boolean doFullSync) throws Exception {
        this.validateForWG(dmDomainName, customerID);
        this.reEvaluateAutoVAForDirTables();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomainSyncDetails"));
        selectQuery.addJoin(new Join("DMDomainSyncDetails", "DirResRel", new String[] { "DM_DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0));
        selectQuery.addSelectColumns((List)new ArrayList(Arrays.asList(Column.getColumn("DirResRel", "OBJ_ID"), Column.getColumn("DMDomainSyncDetails", "SYNC_STATUS"), Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"))));
        selectQuery.addSortColumns((List)new ArrayList(Arrays.asList(new SortColumn(Column.getColumn("DirResRel", "OBJ_ID"), true), new SortColumn(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), true))));
        selectQuery.setRange(new Range(0, 1));
        String errStatus = null;
        Long dirObjID = null;
        int fetchStatus = 901;
        final DataObject syncStatusObj = SyMUtil.getPersistenceLite().get(selectQuery);
        if (syncStatusObj != null && syncStatusObj.containsTable("DirResRel")) {
            dirObjID = (Long)syncStatusObj.getFirstValue("DirResRel", "OBJ_ID");
        }
        if (syncStatusObj != null && syncStatusObj.containsTable("DMDomainSyncDetails")) {
            errStatus = (String)syncStatusObj.getFirstValue("DMDomainSyncDetails", "SYNC_STATUS");
            try {
                fetchStatus = Integer.valueOf(String.valueOf(syncStatusObj.getFirstValue("DMDomainSyncDetails", "FETCH_STATUS")));
            }
            catch (final Exception ex) {}
        }
        if (fetchStatus != 951 || (!SyMUtil.isStringEmpty(errStatus) && errStatus.equalsIgnoreCase("INTERNAL_ERROR")) || dirObjID == null) {
            IDPSlogger.AUDIT.log(Level.INFO, "checkHealthAndQualifyIfFullSyncRequired fetchStatus {0} errStatus {1} dirObjID {2}", new Object[] { String.valueOf(fetchStatus), String.valueOf(errStatus), String.valueOf(dirObjID) });
            doFullSync = true;
            if (!SyMUtil.isStringEmpty(errStatus) && errStatus.equalsIgnoreCase("INTERNAL_ERROR")) {
                this.clearSyncedDirData(dmDomainID);
            }
        }
        if (!doFullSync && dmDomainClientID == 2) {
            final List<Integer> objectTypesToBeSyncedForOPdomain = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(dmDomainID);
            if (objectTypesToBeSyncedForOPdomain != null && objectTypesToBeSyncedForOPdomain.contains(7)) {
                doFullSync = true;
            }
        }
        if (!doFullSync) {
            if (!IdpsUtil.isFeatureAvailable("PERPETUAL_DIFF_SYNC")) {
                if (IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).alwaysDoFullSync()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    static {
        DirectoryHealthChecker.directoryHealthChecker = null;
    }
    
    private class PG_CLASS
    {
        private static final String TABLE = "PG_CLASS";
        private static final String RELNAME = "RELNAME";
        private static final String RELOPTIONS = "RELOPTIONS";
        private static final String RELNAMESPACE = "RELNAMESPACE";
    }
    
    private class PG_NAMESPACE
    {
        private static final String OID = "OID";
        private static final String NSPNAME = "NSPNAME";
        private static final String TABLE = "PG_NAMESPACE";
    }
}
