package com.me.tools.dbmigration.handler;

import java.sql.PreparedStatement;
import com.adventnet.db.adapter.DBAdapter;
import java.util.Iterator;
import java.sql.Connection;
import com.adventnet.ds.query.Criteria;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Column;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.DBMigrationPrePostHandler;

public class DCNativeSQLTableHandler implements DBMigrationPrePostHandler
{
    Logger logger;
    
    public DCNativeSQLTableHandler() {
        this.logger = Logger.getLogger(DCNativeSQLTableHandler.class.getName());
    }
    
    public void postHandle() throws Exception {
        this.logger.log(Level.INFO, "Updating the DCNativeSQL Table with respect to the database");
        this.updateDCNativeSQLBasedOnConfigDB();
        this.logger.log(Level.INFO, "Completed updating the DCNativeSQL Table with respect to the database");
    }
    
    public void updateDCNativeSQLBasedOnConfigDB() throws Exception {
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        final String sDCNativeSQLConfFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DesktopCentral" + File.separator + "sql" + File.separator + "NativeSQLStringDC.xml";
        final File fileDC = new File(sDCNativeSQLConfFilePath);
        final String sMDMNativeSQLConfFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDM" + File.separator + "sql" + File.separator + "NativeSQLStringMDM.xml";
        final File fileMDM = new File(sMDMNativeSQLConfFilePath);
        final DCNativeSQLXmlHandler objDCNativeSQLXmlHandler = new DCNativeSQLXmlHandler();
        Map queryId_SQL_Map = objDCNativeSQLXmlHandler.parse(fileDC.toURL());
        this.updateDCNativeSQL(queryId_SQL_Map);
        queryId_SQL_Map = objDCNativeSQLXmlHandler.parse(fileMDM.toURL());
        this.updateDCNativeSQL(queryId_SQL_Map);
    }
    
    private void updateDCNativeSQL(final Map queryId_SQL_Map) throws Exception {
        Connection dstConnection = null;
        try {
            final Iterator sqlIDKeySet = queryId_SQL_Map.keySet().iterator();
            final Map dcNativeSqlObjMap = new LinkedHashMap();
            dcNativeSqlObjMap.put(Column.getColumn("DCNativeSQLString", "SQL_COMMAND"), QueryConstants.PREPARED_STMT_CONST);
            dcNativeSqlObjMap.put(Column.getColumn("DCNativeSQLString", "SQL_REMARKS"), QueryConstants.PREPARED_STMT_CONST);
            dstConnection = DBMigrationUtil.getDestConnection();
            final DBAdapter destDBAdapter = DBMigrationUtil.getDestDBAdapter();
            while (sqlIDKeySet.hasNext()) {
                final String sqlMap = String.valueOf(sqlIDKeySet.next());
                final LinkedHashMap detailsMap = queryId_SQL_Map.get(sqlMap);
                final int sqlID = Integer.parseInt(detailsMap.get("sql_id"));
                final Criteria criteria = new Criteria(Column.getColumn("DCNativeSQLString", "SQL_ID"), (Object)sqlID, 0);
                final String updateSQL = destDBAdapter.getSQLGenerator().getSQLForUpdate("DCNativeSQLString", dcNativeSqlObjMap, criteria);
                this.logger.log(Level.INFO, "Update query : {0}", updateSQL);
                final PreparedStatement prepStmt = dstConnection.prepareStatement(updateSQL);
                prepStmt.setString(1, detailsMap.get("sql_command"));
                prepStmt.setString(2, detailsMap.get("sql_remarks"));
                this.logger.log(Level.INFO, "Sql Id : {0} Command : {1}", new Object[] { sqlID, detailsMap.get("sql_command") });
                prepStmt.executeUpdate();
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
            throw new Exception(t);
        }
        finally {
            if (dstConnection != null) {
                dstConnection.close();
            }
        }
    }
    
    public void preHandle() throws Exception {
        this.logger.log(Level.INFO, "Not supported yet");
    }
}
