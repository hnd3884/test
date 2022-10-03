package com.me.ems.summaryserver.common.settings.util;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class SettingsStatusDBUtil
{
    public static final Long RDS_SETTINGS;
    public static final Long RDS_SCR_REC_SETTINGS;
    public static final Long RDS_PERFORMANCE_SETTINGS;
    public static final Long RDS_PROMPT_SETTINGS;
    public static final Long RDS_PROMPT_EXCLUDE_COMP_SETTINGS;
    public static final Long NOT_YET_ENABLED;
    public static final Long ENABLED_IN_SS;
    public static final Long ENABLED_IN_PROBE;
    public static final Long ENABLED_IN_BOTH;
    private static Logger logger;
    private static SettingsStatusDBUtil settingsStatusDBUtil;
    
    private SettingsStatusDBUtil() {
    }
    
    public static SettingsStatusDBUtil getInstance() {
        if (SettingsStatusDBUtil.settingsStatusDBUtil == null) {
            SettingsStatusDBUtil.settingsStatusDBUtil = new SettingsStatusDBUtil();
        }
        return SettingsStatusDBUtil.settingsStatusDBUtil;
    }
    
    public Long getSSSettingsStatus(final Long settingsID) throws Exception {
        Long settingsStatus = -1L;
        final String tableName = "SummarySettingsStatus";
        try {
            final Column settingsIDCol = new Column(tableName, "SETTINGS_ID");
            final Criteria criteria = new Criteria(settingsIDCol, (Object)settingsID, 0);
            final DataObject dataObject = DataAccess.get(tableName, criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow(tableName);
                settingsStatus = Long.valueOf(row.get("SETTINGS_STATUS").toString());
            }
        }
        catch (final DataAccessException e) {
            SettingsStatusDBUtil.logger.log(Level.SEVERE, "Exception in getSSSettingsStatus...", (Throwable)e);
            throw e;
        }
        return settingsStatus;
    }
    
    public boolean updateSSSettingsStatus(final Long settingsID, final Long settingsStatus) {
        final String tableName = "SummarySettingsStatus";
        try {
            final Column settingsIDCol = new Column(tableName, "SETTINGS_ID");
            final Criteria criteria = new Criteria(settingsIDCol, (Object)settingsID, 0);
            final UpdateQueryImpl updateQuery = new UpdateQueryImpl(tableName);
            updateQuery.setCriteria(criteria);
            updateQuery.setUpdateColumn("SETTINGS_STATUS", (Object)settingsStatus);
            DataAccess.update((UpdateQuery)updateQuery);
            SettingsStatusDBUtil.logger.log(Level.INFO, "Updated Summary Setting Status for Setting :" + settingsID + " with Status :" + settingsStatus);
        }
        catch (final DataAccessException e) {
            SettingsStatusDBUtil.logger.log(Level.SEVERE, "Exception in updateSSSettingsStatus...", (Throwable)e);
            return false;
        }
        return true;
    }
    
    static {
        RDS_SETTINGS = 1L;
        RDS_SCR_REC_SETTINGS = 2L;
        RDS_PERFORMANCE_SETTINGS = 3L;
        RDS_PROMPT_SETTINGS = 4L;
        RDS_PROMPT_EXCLUDE_COMP_SETTINGS = 5L;
        NOT_YET_ENABLED = 0L;
        ENABLED_IN_SS = 1L;
        ENABLED_IN_PROBE = 2L;
        ENABLED_IN_BOTH = 3L;
        SettingsStatusDBUtil.logger = Logger.getLogger("RDSLogger");
        SettingsStatusDBUtil.settingsStatusDBUtil = null;
    }
}
