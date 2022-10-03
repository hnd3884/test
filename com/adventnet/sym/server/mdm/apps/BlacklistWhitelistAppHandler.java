package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class BlacklistWhitelistAppHandler
{
    public static final Logger LOGGER;
    private static BlacklistWhitelistAppHandler blacklistWhitelistAppHandler;
    public static boolean isNotify;
    public static final String BLACKLIST_ACTION = "Action";
    public static final String BLACKLIST_APPS = "BlacklistApps";
    public static final String WHITELIST_APPS = "WhitelistApps";
    public static final String APP_LIST = "AppList";
    public static final String SUCCESS_LIST = "SuccessList";
    public static final String FAILURE_LIST = "FailureList";
    public static final int COMMAND_YET_TO_SEND = -1;
    public static final int COMMAND_SENT = 0;
    public static final int COMMAND_SUCCESS = 1;
    public static final int COMMAND_INVALID = 2;
    public static final int NO_ACTION_ON_BLACKLISTED_APPS = 1;
    public static final int DISABLE_ACTION_ON_BLACKLISTED_APPS = 2;
    public static final int UNINSTALL_ACTION_ON_BLACKLISTED_APPS = 3;
    public static final String LAST_CORP_ACTION = "LastCorpAction";
    public static final String LAST_BYOD_ACTION = "LastBYODAction";
    public static final String CORP_ACTION = "CorpAction";
    public static final String BYOD_ACTION = "BYODAction";
    
    private BlacklistWhitelistAppHandler() {
    }
    
    public static BlacklistWhitelistAppHandler getInstance() {
        return (BlacklistWhitelistAppHandler.blacklistWhitelistAppHandler == null) ? (BlacklistWhitelistAppHandler.blacklistWhitelistAppHandler = new BlacklistWhitelistAppHandler()) : BlacklistWhitelistAppHandler.blacklistWhitelistAppHandler;
    }
    
    public void updateBlacklistWhitelistAppCommandStatus(final Properties props) {
        BlacklistWhitelistAppHandler.LOGGER.log(Level.INFO, "-- Update Blacklist/Whitelist command status in MdBlacklistAppInAgent begins --");
        BlacklistWhitelistAppHandler.LOGGER.log(Level.INFO, "Blacklist/Whitelist command status resource id : {0}", ((Hashtable<K, Object>)props).get("RESOURCE_ID"));
        BlacklistWhitelistAppHandler.LOGGER.log(Level.INFO, "Blacklist/Whitelist command status criteria : {0}", ((Hashtable<K, Object>)props).get("APP_STATUS"));
        BlacklistWhitelistAppHandler.LOGGER.log(Level.INFO, "Blacklist/Whitelist command status value : {0}", ((Hashtable<K, Object>)props).get("COMMAND_STATUS"));
        try {
            final long resourceID = ((Hashtable<K, Long>)props).get("RESOURCE_ID");
            final int commandStatusCriteria = ((Hashtable<K, Integer>)props).get("APP_STATUS");
            final int scope = ((Hashtable<K, Integer>)props).get("SCOPE");
            final Criteria resCriteria = new Criteria(Column.getColumn("MdBlacklistAppInAgent", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria commandSentCriteria = new Criteria(Column.getColumn("MdBlacklistAppInAgent", "COMMAND_STATUS"), (Object)commandStatusCriteria, 0);
            final Criteria scopeCriteria = new Criteria(Column.getColumn("MdBlacklistAppInAgent", "SCOPE"), (Object)scope, 0);
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBlacklistAppInAgent");
            uQuery.setCriteria(resCriteria.and(commandSentCriteria).and(scopeCriteria));
            final Integer commandStatus = ((Hashtable<K, Integer>)props).get("COMMAND_STATUS");
            if (commandStatus != null) {
                uQuery.setUpdateColumn("COMMAND_STATUS", (Object)commandStatus);
            }
            final String remarks = ((Hashtable<K, String>)props).get("REMARKS");
            if (remarks != null) {
                uQuery.setUpdateColumn("REMARKS", (Object)remarks);
            }
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            BlacklistWhitelistAppHandler.LOGGER.log(Level.SEVERE, "Exception occurred during updateInvalidCommandStatus. ", ex);
        }
        BlacklistWhitelistAppHandler.LOGGER.log(Level.INFO, "-- Update Blacklist/Whitelist command status in MdBlacklistAppInAgent ends --");
    }
    
    public int getBlacklistAppCountOnDevice(final Long resourceID) {
        int blackListCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBlackListAppInResource"));
            sQuery.addJoin(new Join("MdBlackListAppInResource", "MdInstalledAppResourceRel", new String[] { "RESOURCE_ID", "APP_ID" }, new String[] { "RESOURCE_ID", "APP_ID" }, 2));
            final Criteria criteria;
            final Criteria resourceCri = criteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
            sQuery.setCriteria(criteria);
            blackListCount = DBUtil.getRecordCount(sQuery, "MdBlackListAppInResource", "APP_ID");
        }
        catch (final Exception ex) {
            BlacklistWhitelistAppHandler.LOGGER.log(Level.SEVERE, "Exception while getBlackListApp count", ex);
        }
        return blackListCount;
    }
    
    public String getBlacklistActionText(final int action) {
        String actionText = "";
        switch (action) {
            case 1: {
                actionText = "No Action";
                break;
            }
            case 2: {
                actionText = "Disable";
                break;
            }
            case 3: {
                actionText = "Uninstall";
                break;
            }
        }
        return actionText;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMAppMgmtLogger");
        BlacklistWhitelistAppHandler.blacklistWhitelistAppHandler = null;
        BlacklistWhitelistAppHandler.isNotify = false;
    }
}
