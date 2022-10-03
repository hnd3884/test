package com.adventnet.sym.server.mdm.security;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RemoteWipeHandler
{
    private Logger logger;
    public static final String ALLOW_WIPE_SD_CARD = "WipeSDCard";
    public static final String RETAIN_MDM = "RetainMDM";
    
    public RemoteWipeHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public boolean addOrUpdateDeviceWipeOptions(final JSONObject optionObj) {
        boolean success = true;
        try {
            final Long resourceID = (Long)optionObj.get("RESOURCE_ID");
            final Boolean allowWipeSDCard = optionObj.optBoolean("WIPE_SD_CARD", (boolean)Boolean.FALSE);
            final Boolean retainMDM = optionObj.optBoolean("WIPE_BUT_RETAIN_MDM", (boolean)Boolean.FALSE);
            final String wipeLockPin = optionObj.optString("WIPE_LOCK_PIN", "");
            final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceWipeOptions", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject wipeOptionDO = MDMUtil.getPersistence().get("MdDeviceWipeOptions", resCriteria);
            if (wipeOptionDO.isEmpty()) {
                final Row row = new Row("MdDeviceWipeOptions");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("WIPE_SD_CARD", (Object)(boolean)allowWipeSDCard);
                row.set("WIPE_BUT_RETAIN_MDM", (Object)(boolean)retainMDM);
                row.set("WIPE_LOCK_PIN", (Object)wipeLockPin);
                wipeOptionDO.addRow(row);
                MDMUtil.getPersistence().add(wipeOptionDO);
            }
            else {
                final Row row = wipeOptionDO.getFirstRow("MdDeviceWipeOptions");
                row.set("WIPE_SD_CARD", (Object)(boolean)allowWipeSDCard);
                row.set("WIPE_BUT_RETAIN_MDM", (Object)(boolean)retainMDM);
                row.set("WIPE_LOCK_PIN", (Object)wipeLockPin);
                wipeOptionDO.updateRow(row);
                MDMUtil.getPersistence().update(wipeOptionDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while addOrUpdateDeviceWipeOptions. data : " + jsonObject);
            success = false;
        }
        return success;
    }
    
    public JSONObject getWipeOptionData(final Long resourceID) {
        final JSONObject wipeOptionData = new JSONObject();
        try {
            Boolean allowWipeSDCard = null;
            Boolean retainMDM = null;
            String wipeLockPin = null;
            final Row wipeOptionRow = DBUtil.getRowFromDB("MdDeviceWipeOptions", "RESOURCE_ID", (Object)resourceID);
            if (wipeOptionRow != null) {
                allowWipeSDCard = (Boolean)wipeOptionRow.get("WIPE_SD_CARD");
                retainMDM = (Boolean)wipeOptionRow.get("WIPE_BUT_RETAIN_MDM");
                wipeLockPin = (String)wipeOptionRow.get("WIPE_LOCK_PIN");
            }
            if (allowWipeSDCard != null) {
                wipeOptionData.put("WipeSDCard", (Object)allowWipeSDCard);
            }
            if (retainMDM != null) {
                wipeOptionData.put("RetainMDM", (Object)retainMDM);
            }
            if (!MDMStringUtils.isEmpty(wipeLockPin)) {
                wipeOptionData.put("WIPE_LOCK_PIN", (Object)wipeLockPin);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exceptin occurred while getWipeOptionData. resourceID : " + n);
        }
        return wipeOptionData;
    }
    
    public void deleteWipeOptionForResource(final Long resourceID) {
        final Criteria resCriteria = new Criteria(Column.getColumn("MdDeviceWipeOptions", "RESOURCE_ID"), (Object)resourceID, 0);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceWipeOptions"));
        query.setCriteria(resCriteria);
        query.addSelectColumn(Column.getColumn("MdDeviceWipeOptions", "*"));
        try {
            final DataObject appDO = MDMUtil.getPersistence().get(query);
            appDO.deleteRows("MdDeviceWipeOptions", (Criteria)null);
            MDMUtil.getPersistence().update(appDO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> " Exception occurred while deleteWipeOptionForResource res ID = " + n);
        }
    }
}
