package com.me.mdm.server.command.kiosk;

import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class KioskPauseResumeHandler
{
    public Logger logger;
    
    public KioskPauseResumeHandler() {
        this.logger = Logger.getLogger(KioskPauseResumeManager.class.getName());
    }
    
    public void addPauseKioskCommandInfo(final JSONObject data) throws JSONException, DataAccessException {
        final Long commandHistoryId = data.getLong("COMMAND_HISTORY_ID");
        final Long delay = data.getLong("RESUME_KIOSK_INTERVAL");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("PauseKioskCommandHistory"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        final Row row = new Row("PauseKioskCommandHistory");
        row.set("COMMAND_HISTORY_ID", (Object)commandHistoryId);
        row.set("RESUME_KIOSK_INTERVAL", (Object)delay);
        dO.addRow(row);
        DataAccess.update(dO);
    }
    
    public Long getPauseDelayForDevice(final Long commandHistoryId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("PauseKioskCommandHistory"));
        final Criteria cmdHistCriteria = new Criteria(new Column("PauseKioskCommandHistory", "COMMAND_HISTORY_ID"), (Object)commandHistoryId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return null;
        }
        return (Long)dO.getRow("PauseKioskCommandHistory").get("RESUME_KIOSK_INTERVAL");
    }
    
    public Integer getDeviceKioskState(final Long resourceID) throws DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceKioskStateInfo"));
        final Criteria resourceCriteria = new Criteria(new Column("DeviceKioskStateInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        sQuery.setCriteria(resourceCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return 3;
        }
        return (Integer)dO.getRow("DeviceKioskStateInfo").get("CURRENT_KIOSK_STATE");
    }
    
    public void addOrUpdateKioskStateForDevice(final JSONObject agentUpdate) throws DataAccessException {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("DeviceKioskStateInfo"));
            final Criteria resCriteria = new Criteria(new Column("DeviceKioskStateInfo", "RESOURCE_ID"), agentUpdate.get("RESOURCE_ID"), 0);
            query.setCriteria(resCriteria);
            query.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(query);
            if (dO.isEmpty()) {
                final Row row = new Row("DeviceKioskStateInfo");
                row.set("RESOURCE_ID", agentUpdate.get("RESOURCE_ID"));
                row.set("CURRENT_KIOSK_STATE", agentUpdate.get("CURRENT_KIOSK_STATE"));
                row.set("REMARKS", agentUpdate.get("REMARKS"));
                row.set("LAST_CHANGED_TIME", agentUpdate.get("LAST_CHANGED_TIME"));
                dO.addRow(row);
                DataAccess.add(dO);
            }
            else {
                final Row row = dO.getRow("DeviceKioskStateInfo");
                row.set("CURRENT_KIOSK_STATE", agentUpdate.get("CURRENT_KIOSK_STATE"));
                row.set("REMARKS", agentUpdate.get("REMARKS"));
                row.set("LAST_CHANGED_TIME", agentUpdate.get("LAST_CHANGED_TIME"));
                dO.updateRow(row);
                DataAccess.update(dO);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Kiosk Command: Exception occured while getting device's kiosk state ", (Throwable)e);
        }
    }
    
    public void logUpdateFromAgent(final Long resourceID, final String remarks) throws Exception {
        String commandType = null;
        switch (remarks) {
            case "resume_kiosk_timer_exhausted": {
                commandType = "mdm.common.resumed.resume_kiosk_timer_exhausted";
                break;
            }
            case "pause_kiosk_passcode": {
                commandType = "mdm.common.paused.pause_kiosk_passcode";
                break;
            }
            case "resume_kiosk_notification": {
                commandType = "mdm.common.resumed.resume_kiosk_notification";
                break;
            }
            case "pause_kiosk_chat_command": {
                commandType = "mdm.common.paused.pause_kiosk_chat_command";
                break;
            }
            case "resume_kiosk_chat_command": {
                commandType = "mdm.common.resumed.resume_kiosk_chat_command";
                break;
            }
            case "pause_kiosk_command": {
                commandType = "mdm.common.paused.pause_kiosk_command";
                break;
            }
            case "pause_kiosk_lostmode": {
                commandType = "mdm.common.paused.pause_kiosk_lostmode";
                break;
            }
            case "resume_kiosk_reboot": {
                commandType = "mdm.common.resumed.resume_kiosk_reboot";
                break;
            }
            case "resume_kiosk_lostmode": {
                commandType = "mdm.common.resumed.resume_kiosk_lostmode";
                break;
            }
            case "resume_kiosk_post_upgrade": {
                commandType = "mdm.common.resumed.resume_kiosk_post_upgrade";
                break;
            }
            case "pause_kiosk_simlock": {
                commandType = "mdm.common.paused.pause_kiosk_simlock";
                break;
            }
            case "resume_kiosk_simlock": {
                commandType = "mdm.common.resumed.resume_kiosk_simlock";
                break;
            }
        }
        final String sEventLogRemarks = commandType;
        final String remarksArgs = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, DMUserHandler.getDCUser(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()), sEventLogRemarks, remarksArgs, CustomerInfoUtil.getInstance().getCustomerId());
    }
    
    public Boolean isProfilePublisedForPlatform(final Integer platform) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        query.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        query.addJoin(new Join("RecentProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("ProfileToCollection", "CollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        query.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        query.addSelectColumn(new Column("CollectionStatus", "PROFILE_COLLECTION_STATUS"));
        query.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
        query.setCriteria(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platform, 0));
        if (platform == 1) {
            query.setCriteria(new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)183, 0).and(new Criteria(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0)));
        }
        else if (platform == 3) {
            query.setCriteria(new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)608, 0).and(new Criteria(Column.getColumn("CollectionStatus", "PROFILE_COLLECTION_STATUS"), (Object)110, 0)));
        }
        final DataObject dO = DataAccess.get(query);
        if (dO.isEmpty()) {
            return false;
        }
        final Iterator iterator = dO.getRows("Profile");
        if (iterator.hasNext()) {
            return true;
        }
        return false;
    }
}
