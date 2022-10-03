package com.me.mdm.server.security.passcode;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMManagedPasswordDeviceHandler
{
    private static final Logger LOGGER;
    
    public static void addOrUpdateMDMDeviceManagedPasswordDetails(final JSONObject requestJSON) throws Exception {
        final Long resourceID = JSONUtil.optLongForUVH(requestJSON, "RESOURCE_ID", (Long)null);
        final int passwordType = requestJSON.getInt("MANAGED_PASSWORD_TYPE");
        final Long passwordID = JSONUtil.optLongForUVH(requestJSON, "MANAGED_PASSWORD_ID", (Long)null);
        final Long associatedUser = JSONUtil.optLongForUVH(requestJSON, "ADDED_BY", Long.valueOf(-1L));
        final String remarksCode = requestJSON.optString("REMARKS_CODE", (String)null);
        final JSONObject remarksJSON = requestJSON.optJSONObject("REMARKS_PARAMS");
        final int passwordStatus = requestJSON.optInt("MANAGED_PASSWORD_STATUS", -1);
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMDeviceManagedPasswordDetails"));
        sql.addSelectColumn(new Column("MDMDeviceManagedPasswordDetails", "*"));
        final Criteria resourceIDCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria passwordTypeCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_TYPE"), (Object)passwordType, 0);
        final Criteria passwordIDCri = new Criteria(new Column("MDMDeviceManagedPasswordDetails", "MANAGED_PASSWORD_ID"), (Object)passwordID, 0);
        sql.setCriteria(resourceIDCri.and(passwordTypeCri).and(passwordIDCri));
        final DataObject dao = MDMUtil.getPersistence().get(sql);
        Row devicePasswordRow = null;
        boolean isAdd = true;
        if (dao != null && !dao.isEmpty()) {
            devicePasswordRow = dao.getRow("MDMDeviceManagedPasswordDetails");
            isAdd = false;
        }
        else {
            devicePasswordRow = new Row("MDMDeviceManagedPasswordDetails");
            devicePasswordRow.set("MANAGED_PASSWORD_ID", (Object)passwordID);
            devicePasswordRow.set("RESOURCE_ID", (Object)resourceID);
            devicePasswordRow.set("MANAGED_PASSWORD_TYPE", (Object)passwordType);
        }
        devicePasswordRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
        if (associatedUser != -1L) {
            devicePasswordRow.set("ADDED_BY", (Object)associatedUser);
        }
        if (remarksCode != null) {
            devicePasswordRow.set("REMARKS_CODE", (Object)remarksCode);
        }
        if (remarksJSON != null) {
            devicePasswordRow.set("REMARKS_PARAMS", (Object)remarksJSON.toString());
        }
        if (passwordStatus != -1) {
            devicePasswordRow.set("MANAGED_PASSWORD_STATUS", (Object)passwordStatus);
        }
        if (isAdd) {
            dao.addRow(devicePasswordRow);
            MDMManagedPasswordDeviceHandler.LOGGER.log(Level.INFO, "NewPasswordAssociatedToDevice passwordType:{0} deviceID:{1} passwordID:{2} passwordStatus:{3}", new Object[] { passwordType, resourceID, passwordID, passwordStatus });
        }
        else {
            dao.updateRow(devicePasswordRow);
            MDMManagedPasswordDeviceHandler.LOGGER.log(Level.INFO, "UpdatePasswordAssociatedToDevice passwordType:{0} deviceID:{1} passwordID:{2} passwordStatus:{3}", new Object[] { passwordType, resourceID, passwordID, passwordStatus });
        }
        MDMUtil.getPersistence().update(dao);
    }
    
    public static JSONArray getDeviceManagedPasswordDetails(final Criteria criteria) {
        final JSONArray array = new JSONArray();
        try {
            final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMDeviceManagedPasswordDetails"));
            sql.addSelectColumn(new Column("MDMDeviceManagedPasswordDetails", "*"));
            if (criteria != null) {
                sql.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(sql);
            if (dataObject != null) {
                final Iterator rowItr = dataObject.getRows("MDMDeviceManagedPasswordDetails");
                while (rowItr.hasNext()) {
                    final Row row = rowItr.next();
                    array.put((Object)MDMDBUtil.rowToJSON(row));
                }
            }
        }
        catch (final Exception ex) {
            MDMManagedPasswordDeviceHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception while addOrUpdateMDMDeviceManagedPasswordDetails", ex);
        }
        return array;
    }
    
    public static boolean deleteManagedPasswordForResource(final Criteria criteria) {
        if (criteria == null) {
            return false;
        }
        final JSONArray array = new JSONArray();
        try {
            final DeleteQuery dsql = (DeleteQuery)new DeleteQueryImpl("MDMDeviceManagedPasswordDetails");
            dsql.setCriteria(criteria);
            MDMUtil.getPersistence().delete(dsql);
            return true;
        }
        catch (final Exception ex) {
            MDMManagedPasswordDeviceHandler.LOGGER.log(Level.SEVERE, "FirmwareLog: Exception while deleteManagedPasswordForResource", ex);
            return false;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMDeviceSecurityLogger");
    }
}
