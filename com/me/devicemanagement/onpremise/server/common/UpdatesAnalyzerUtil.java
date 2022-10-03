package com.me.devicemanagement.onpremise.server.common;

import java.util.Date;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class UpdatesAnalyzerUtil
{
    private static Logger logger;
    protected static UpdatesAnalyzerUtil updatesAnalyzerUtil;
    
    public static UpdatesAnalyzerUtil getInstance() {
        if (UpdatesAnalyzerUtil.updatesAnalyzerUtil == null) {
            UpdatesAnalyzerUtil.updatesAnalyzerUtil = new UpdatesAnalyzerUtil();
        }
        return UpdatesAnalyzerUtil.updatesAnalyzerUtil;
    }
    
    public int getInitialInstalledBuild() {
        Connection conn = null;
        int initialInstalledBuild = 0;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
            query.addSelectColumn(new Column("DCServerBuildHistory", "*"));
            query.addSortColumn(new SortColumn("DCServerBuildHistory", "BUILD_NUMBER", true));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            if (ds.next()) {
                initialInstalledBuild = (int)ds.getValue("BUILD_NUMBER");
            }
            ds.close();
            return initialInstalledBuild;
        }
        catch (final Exception e) {
            UpdatesAnalyzerUtil.logger.log(Level.SEVERE, "Exception in getInitialInstalledBuild", e);
            return initialInstalledBuild;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex) {
                UpdatesAnalyzerUtil.logger.log(Level.WARNING, "Exception while closing connection in getInitialInstalledBuild", ex);
            }
        }
    }
    
    public int getInstalledBeforeInDays() {
        int installedBefore = 0;
        final String installTime = SyMUtil.getInstallationProperty("it");
        if (installTime != null) {
            final Long installTimeStamp = Long.valueOf(installTime);
            final Date installedDate = new Date();
            installedDate.setTime(installTimeStamp);
            final Date currentDate = new Date();
            currentDate.setTime(System.currentTimeMillis());
            installedBefore = (int)((currentDate.getTime() - installedDate.getTime()) / 86400000L);
        }
        return installedBefore;
    }
    
    static {
        UpdatesAnalyzerUtil.logger = Logger.getLogger(UpdatesAnalyzerUtil.class.getName());
        UpdatesAnalyzerUtil.updatesAnalyzerUtil = null;
    }
}
