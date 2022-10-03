package com.adventnet.sym.server.mdm.command;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LockScreenMessageUtil
{
    private static LockScreenMessageUtil lockScreenMessageUtil;
    public static final Logger LOGGER;
    public static final String DEFAULT_LOCKSCREEN_MESSAGE = "This phone is lost";
    
    public static LockScreenMessageUtil getInstance() {
        if (LockScreenMessageUtil.lockScreenMessageUtil == null) {
            LockScreenMessageUtil.lockScreenMessageUtil = new LockScreenMessageUtil();
        }
        return LockScreenMessageUtil.lockScreenMessageUtil;
    }
    
    public boolean addorUpdateLockScreenMessage(final JSONObject messageObject) {
        Boolean addedSuccess = Boolean.TRUE;
        try {
            final Long resourceID = (Long)messageObject.get("resourceId");
            final String phoneNumber = messageObject.optString("phoneNumber");
            final String lockMessage = messageObject.optString("lockMessage");
            final String unlockpin = messageObject.optString("unlockPin");
            final Boolean sendToUser = messageObject.optBoolean("sendEmailToUser", false);
            final Criteria resourceIDCriteria = new Criteria(new Column("MdDeviceLockMessage", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject DO = MDMUtil.getPersistence().get("MdDeviceLockMessage", resourceIDCriteria);
            if (DO.isEmpty()) {
                final Row row = new Row("MdDeviceLockMessage");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("PHONE_NUMBER", (Object)phoneNumber);
                row.set("LOCK_MESSAGE", (Object)lockMessage);
                if (unlockpin != null) {
                    row.set("UNLOCK_PIN", (Object)unlockpin);
                }
                row.set("SEND_MAIL_TO_USER", (Object)sendToUser);
                DO.addRow(row);
                MDMUtil.getPersistence().add(DO);
            }
            else {
                final Row row = DO.getFirstRow("MdDeviceLockMessage");
                row.set("PHONE_NUMBER", (Object)phoneNumber);
                row.set("LOCK_MESSAGE", (Object)lockMessage);
                if (unlockpin != null) {
                    row.set("UNLOCK_PIN", (Object)unlockpin);
                }
                row.set("SEND_MAIL_TO_USER", (Object)sendToUser);
                DO.updateRow(row);
                MDMUtil.getPersistence().update(DO);
            }
        }
        catch (final JSONException ex) {
            LockScreenMessageUtil.LOGGER.log(Level.SEVERE, "Exception while parsing the json", (Throwable)ex);
        }
        catch (final Exception ex2) {
            LockScreenMessageUtil.LOGGER.log(Level.SEVERE, "Exception in addorUpdateLockScreenMessage -- {0}", ex2);
            addedSuccess = Boolean.FALSE;
        }
        return addedSuccess;
    }
    
    public boolean addorUpdateBulkLockScreenMessage(final JSONObject messageObject, final List<Long> resourceIds) {
        Boolean addedSuccess = Boolean.TRUE;
        try {
            final List<Long> resourceList = new ArrayList<Long>(resourceIds);
            final String phoneNumber = messageObject.optString("phoneNumber");
            final String lockMessage = messageObject.optString("lockMessage");
            final String unlockpin = messageObject.optString("unlockPin");
            final Boolean sendToUser = messageObject.optBoolean("sendEmailToUser", false);
            final Criteria resourceIDCriteria = new Criteria(new Column("MdDeviceLockMessage", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final DataObject updateMsgDO = MDMUtil.getPersistence().get("MdDeviceLockMessage", resourceIDCriteria);
            if (updateMsgDO != null) {
                final Iterator<Row> iterator = updateMsgDO.getRows("MdDeviceLockMessage");
                while (iterator.hasNext()) {
                    final Row msgRow = iterator.next();
                    if (msgRow != null) {
                        msgRow.set("PHONE_NUMBER", (Object)phoneNumber);
                        msgRow.set("LOCK_MESSAGE", (Object)lockMessage);
                        if (unlockpin != null) {
                            msgRow.set("UNLOCK_PIN", (Object)unlockpin);
                        }
                        msgRow.set("SEND_MAIL_TO_USER", (Object)sendToUser);
                        updateMsgDO.updateRow(msgRow);
                        resourceList.remove(new Long(msgRow.get("RESOURCE_ID").toString()));
                    }
                }
            }
            MDMUtil.getPersistence().update(updateMsgDO);
            final DataObject addMsgDO = MDMUtil.getPersistence().constructDataObject();
            for (final Long resourceID : resourceList) {
                final Row row = new Row("MdDeviceLockMessage");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("PHONE_NUMBER", (Object)phoneNumber);
                row.set("LOCK_MESSAGE", (Object)lockMessage);
                if (unlockpin != null) {
                    row.set("UNLOCK_PIN", (Object)unlockpin);
                }
                row.set("SEND_MAIL_TO_USER", (Object)sendToUser);
                addMsgDO.addRow(row);
            }
            MDMUtil.getPersistence().add(addMsgDO);
        }
        catch (final JSONException ex) {
            LockScreenMessageUtil.LOGGER.log(Level.SEVERE, "Exception while parsing the json", (Throwable)ex);
        }
        catch (final Exception ex2) {
            LockScreenMessageUtil.LOGGER.log(Level.SEVERE, "Exception in addorUpdateLockScreenMessage -- {0}", ex2);
            addedSuccess = Boolean.FALSE;
        }
        return addedSuccess;
    }
    
    public HashMap getLockScreenMessage(final Long resourceID) {
        HashMap hsLockScreenMessage = null;
        final Criteria resourceIDCriteria = new Criteria(new Column("MdDeviceLockMessage", "RESOURCE_ID"), (Object)resourceID, 0);
        try {
            final DataObject DO = MDMUtil.getPersistence().get("MdDeviceLockMessage", resourceIDCriteria);
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("MdDeviceLockMessage");
                hsLockScreenMessage = new HashMap();
                hsLockScreenMessage.put("PHONE_NUMBER", row.get("PHONE_NUMBER"));
                hsLockScreenMessage.put("LOCK_MESSAGE", row.get("LOCK_MESSAGE"));
                final String unlockPin = (row.get("UNLOCK_PIN") == null) ? null : row.get("UNLOCK_PIN").toString();
                if (unlockPin != null) {
                    hsLockScreenMessage.put("UNLOCK_PIN", unlockPin);
                }
            }
        }
        catch (final Exception ex) {
            LockScreenMessageUtil.LOGGER.log(Level.SEVERE, "Exception in getLockScreenMessage -- {0}", ex);
        }
        return hsLockScreenMessage;
    }
    
    public JSONObject getLockScreenRecentMessage(final Long customerId) {
        final JSONObject msgJson = new JSONObject();
        try {
            final SelectQuery deviceLockMsgQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceLockMessage"));
            deviceLockMsgQuery.addJoin(new Join("MdDeviceLockMessage", "CommandHistory", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deviceLockMsgQuery.addJoin(new Join("CommandHistory", "CommandAuditLog", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
            deviceLockMsgQuery.addJoin(new Join("MdDeviceLockMessage", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            deviceLockMsgQuery.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            final Criteria commandStatusCri = new Criteria(new Column("CommandAuditLog", "COMMAND_STATUS"), (Object)1, 0);
            final Criteria customerIdCri = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria lostModeCmdCri = new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)"EnableLostMode", 0);
            deviceLockMsgQuery.setCriteria(commandStatusCri.and(customerIdCri).and(lostModeCmdCri));
            deviceLockMsgQuery.setRange(new Range(0, 1));
            deviceLockMsgQuery.addSelectColumn(Column.getColumn("MdDeviceLockMessage", "LOCK_MESSAGE"));
            deviceLockMsgQuery.addSelectColumn(Column.getColumn("MdDeviceLockMessage", "PHONE_NUMBER"));
            deviceLockMsgQuery.addSelectColumn(Column.getColumn("MdDeviceLockMessage", "DEVICE_LOCK_MESSAGE_ID"));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("CommandAuditLog", "UPDATED_TIME"), false);
            deviceLockMsgQuery.addSortColumn(sortColumn);
            final DataObject msgDO = MDMUtil.getPersistence().get(deviceLockMsgQuery);
            if (!msgDO.isEmpty()) {
                final Row msgRow = msgDO.getFirstRow("MdDeviceLockMessage");
                if (msgRow != null) {
                    msgJson.put("LOCK_MESSAGE", msgRow.get("LOCK_MESSAGE"));
                    msgJson.put("PHONE_NUMBER", msgRow.get("PHONE_NUMBER"));
                }
            }
        }
        catch (final Exception ex) {
            LockScreenMessageUtil.LOGGER.log(Level.SEVERE, "Exception in getLockScreenRecentMessage", ex);
        }
        return msgJson;
    }
    
    static {
        LockScreenMessageUtil.lockScreenMessageUtil = null;
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
