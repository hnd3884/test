package com.adventnet.sym.server.mdm.security;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacDeviceUserUnlockHandler
{
    private Logger logger;
    public static final String USER_NAME = "user_name";
    
    public MacDeviceUserUnlockHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void addOrUpdateDeviceUnlockOption(final JSONObject jsonObject) {
        try {
            MDMDBUtil.addOrUpdateAndPersist("MdDeviceUserUnlock", new Object[][] { { "RESOURCE_ID", jsonObject.getLong("RESOURCE_ID") }, { "USERNAME", jsonObject.getString("USERNAME") } });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to add Ulock user account details for the device", e);
        }
    }
    
    public String getUserNameForResourceID(final Long resoureID) {
        try {
            return (String)MDMDBUtil.getFirstRow("MdDeviceUserUnlock", new Object[][] { { "RESOURCE_ID", resoureID } }).get("USERNAME");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to fetch unlock userName for the device", e);
            return null;
        }
    }
    
    public void deleteUserNameForResource(final Long resourceID) {
        final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceUserUnlock", "RESOURCE_ID"), (Object)resourceID, 0);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceUserUnlock"));
        query.setCriteria(resCriteria);
        query.addSelectColumn(Column.getColumn("MdDeviceUserUnlock", "*"));
        try {
            final DataObject appDO = MDMUtil.getPersistence().get(query);
            appDO.deleteRows("MdDeviceUserUnlock", (Criteria)null);
            MDMUtil.getPersistence().update(appDO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> " Failed to delete UnlockUserName details of res ID = " + n);
        }
    }
}
