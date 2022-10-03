package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.util.logging.Logger;

public class ACSQLStringUtility
{
    private static final Logger OUT;
    
    public void updateACSQLStringBasedOnConfigDB() throws Exception {
        final List allSelectQueryConfFileURLs = this.getAllSelectQueryConfFileURLs();
        final ACSQLStringHandler objACSQLStringHandler = new ACSQLStringHandler();
        final Iterator confFileURLs = allSelectQueryConfFileURLs.iterator();
        while (confFileURLs.hasNext()) {
            String confFileURL = String.valueOf(confFileURLs.next());
            confFileURL = this.getExactPath(confFileURL);
            final File file = new File(confFileURL);
            final Map queryId_SQL_Map = objACSQLStringHandler.parse(file.toURL());
            this.updateACSQLString(queryId_SQL_Map);
        }
    }
    
    private void updateACSQLString(final Map queryId_SQL_Map) throws Exception {
        final Iterator queryIDPatternKeySet = queryId_SQL_Map.keySet().iterator();
        while (queryIDPatternKeySet.hasNext()) {
            final String queryIDPatternKey = String.valueOf(queryIDPatternKeySet.next());
            final String sql = String.valueOf(queryId_SQL_Map.get(queryIDPatternKey));
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ACSQLString"));
            query.addJoin(new Join("ACSQLString", "UVHValues", new String[] { "QUERYID" }, new String[] { "GENVALUES" }, 2));
            final Column patternColumn = Column.getColumn("UVHValues", "PATTERN");
            final Criteria criteria = new Criteria(patternColumn, (Object)queryIDPatternKey, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(new Column((String)null, "*"));
            final DataObject resultDO = this.getPersistence().get(query);
            if (!resultDO.isEmpty()) {
                final Row row = resultDO.getRow("ACSQLString");
                row.set("SQL", (Object)sql);
                resultDO.updateRow(row);
                this.getPersistence().update(resultDO);
            }
        }
    }
    
    private Persistence getPersistence() {
        Persistence pers = null;
        try {
            pers = (Persistence)BeanUtil.lookup("Persistence");
            return pers;
        }
        catch (final Exception ex) {
            ACSQLStringUtility.OUT.log(Level.SEVERE, "Exception while lookup Persistence. ", ex);
            return pers;
        }
    }
    
    private ArrayList getAllSelectQueryConfFileURLs() {
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;
        final ArrayList arrURLS = new ArrayList();
        try {
            final String sql = "SELECT URL from conffile where fileid in (select distinct fileid from uvhvalues where pattern like '%SelectQuery%')";
            final RelationalAPI relApi = RelationalAPI.getInstance();
            conn = relApi.getConnection();
            statement = conn.createStatement();
            rs = relApi.executeQueryForSQL(sql, (Map)null, statement);
            while (rs.next()) {
                final String url = rs.getString("URL");
                arrURLS.add(url);
            }
        }
        catch (final Exception e) {
            ACSQLStringUtility.OUT.log(Level.SEVERE, null, e);
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final SQLException e2) {
                ACSQLStringUtility.OUT.log(Level.SEVERE, null, e2);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final SQLException e3) {
                ACSQLStringUtility.OUT.log(Level.SEVERE, null, e3);
            }
        }
        return arrURLS;
    }
    
    private String getExactPath(final String relativePathString) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).toURL().getPath();
            final String populationURL = relativePathString.replaceAll(".*.conf/", serverHome + "conf/");
            return populationURL;
        }
        catch (final Exception e) {
            ACSQLStringUtility.OUT.log(Level.SEVERE, null, e);
            return null;
        }
    }
    
    static {
        OUT = Logger.getLogger(ACSQLStringUtility.class.getName());
    }
}
