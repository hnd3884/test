package com.me.idps.core.util;

import java.util.Hashtable;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import java.util.List;
import com.me.idps.core.sync.events.IdpEventConstants;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import java.util.Map;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.DBUtil;

public class DMDomainSyncDetailsDataHandler
{
    private static DMDomainSyncDetailsDataHandler dmDomainSyncDetailsDataHandler;
    
    public static DMDomainSyncDetailsDataHandler getInstance() {
        if (DMDomainSyncDetailsDataHandler.dmDomainSyncDetailsDataHandler == null) {
            DMDomainSyncDetailsDataHandler.dmDomainSyncDetailsDataHandler = new DMDomainSyncDetailsDataHandler();
        }
        return DMDomainSyncDetailsDataHandler.dmDomainSyncDetailsDataHandler;
    }
    
    public Long getSyncIntiatedByUserID(final Long dmDomainID) throws Exception {
        Long syncInitiatedBy = (Long)getInstance().getDMdomainSyncDetail(dmDomainID, "LAST_MODIFIED_BY");
        if (syncInitiatedBy == null) {
            syncInitiatedBy = IdpsUtil.getAdminUserId();
        }
        return syncInitiatedBy;
    }
    
    public String getSyncIntiatedByUsername(final Long dmDomainID) throws Exception {
        Long syncInitiatedBy = (Long)this.getDMdomainSyncDetail(dmDomainID, "LAST_MODIFIED_BY");
        if (syncInitiatedBy == null) {
            syncInitiatedBy = IdpsUtil.getAdminUserId();
        }
        return (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)syncInitiatedBy, "FIRST_NAME");
    }
    
    public Object getDMdomainSyncDetail(final Long dmDomainID, final String columnName) throws Exception {
        return DBUtil.getValueFromDB("DMDomainSyncDetails", "DM_DOMAIN_ID", (Object)dmDomainID, columnName);
    }
    
    public void addOrUpdateADDomainSyncDetails(final Long domainID, final String key, final Object value) throws Exception {
        this.addOrUpdateADDomainSyncDetails(domainID, new HashMap<String, Object>() {
            {
                this.put(key, value);
            }
        });
    }
    
    public void addOrUpdateADDomainSyncDetails(final Long domainID, final HashMap<String, Object> map) throws Exception {
        Long userID = null;
        try {
            userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.WARNING, "could not get userID", e);
        }
        if (userID == null) {
            userID = this.getSyncIntiatedByUserID(domainID);
        }
        DataObject dObj = SyMUtil.getPersistenceLite().get("DMDomainSyncDetails", new Criteria(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), (Object)domainID, 0, false));
        if (dObj != null) {
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                if (dObj.isEmpty() && !dObj.containsTable("DMDomainSyncDetails")) {
                    final Row row = new Row("DMDomainSyncDetails");
                    row.set("DM_DOMAIN_ID", (Object)domainID);
                    row.set(key, value);
                    if (userID != null) {
                        row.set("LAST_MODIFIED_BY", (Object)userID);
                    }
                    if (key.equalsIgnoreCase("FETCH_STATUS")) {
                        row.set("FETCH_STATUS", value);
                    }
                    else {
                        row.set("FETCH_STATUS", (Object)0);
                    }
                    dObj = (DataObject)new WritableDataObject();
                    dObj.addRow(row);
                }
                else {
                    final Row row = dObj.getRow("DMDomainSyncDetails");
                    row.set(key, value);
                    if (userID != null) {
                        row.set("LAST_MODIFIED_BY", (Object)userID);
                    }
                    dObj.updateRow(row);
                }
                if (!SyMUtil.isStringEmpty(key) && key.equalsIgnoreCase("SYNC_STATUS")) {
                    IDPSlogger.DBO.log(Level.INFO, String.valueOf(value));
                }
            }
            SyMUtil.getPersistenceLite().update(dObj);
        }
    }
    
    public Integer getSyncStatus(final String dmDomainName, final Long customerID) throws DataAccessException {
        Integer syncStatus = -1;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
        query.addJoin(new Join("DMDomain", "DMDomainSyncDetails", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 2));
        final Criteria customerCri = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0);
        if (!SyMUtil.isStringEmpty(dmDomainName)) {
            query.setCriteria(customerCri.and(new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)dmDomainName, 0, false)));
        }
        else {
            query.setCriteria(customerCri.and(new Criteria(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), (Object)951, 0, false).or(new Criteria(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), (Object)941, 0, false))));
        }
        query.addSelectColumn(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"));
        query.addSelectColumn(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"));
        final DataObject DO = SyMUtil.getPersistenceLite().get(query);
        if (!DO.isEmpty()) {
            syncStatus = IdpsUtil.getInstance().getIntVal(String.valueOf(DO.getRow("DMDomainSyncDetails").get("FETCH_STATUS")));
        }
        return syncStatus;
    }
    
    public void addOrUpdateDirectorySyncSettings(final Properties dmDomainProps, final int resType, final Boolean enable) throws Exception {
        this.addOrUpdateDirectorySyncSettings(dmDomainProps, new HashSet<Integer>(Arrays.asList(resType)), enable);
    }
    
    public void addOrUpdateDirectorySyncSettings(final Properties dmDomainProps, final Set<Integer> objectTypesToBeSynced, final Boolean enable) throws Exception {
        this.addOrUpdateDirectorySyncSettings(dmDomainProps, objectTypesToBeSynced, enable, true);
    }
    
    public void addOrUpdateDirectorySyncSettings(final Properties dmDomainProps, final Set<Integer> objectTypesToBeSynced, final Boolean enable, final boolean initateSync) throws Exception {
        if (!objectTypesToBeSynced.isEmpty()) {
            boolean changed = false;
            final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            final Criteria domainCriteria = new Criteria(Column.getColumn("DirectorySyncObject", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
            DataObject dObj = IdpsUtil.getPersistenceLite().get("DirectorySyncObject", domainCriteria);
            if (dObj == null) {
                dObj = IdpsUtil.getPersistenceLite().constructDataObject();
            }
            final JSONArray resTypes = new JSONArray();
            for (final int curResType : objectTypesToBeSynced) {
                resTypes.add((Object)String.valueOf(curResType));
                Row row = dObj.getRow("DirectorySyncObject", new Criteria(Column.getColumn("DirectorySyncObject", "RESOURCE_TYPE"), (Object)curResType, 0));
                if (row != null) {
                    final boolean curEnableVal = (boolean)row.get("SYNC_ENABLED");
                    row.set("SYNC_ENABLED", (Object)enable);
                    dObj.updateRow(row);
                    changed = (curEnableVal != enable);
                }
                else {
                    row = new Row("DirectorySyncObject");
                    row.set("SYNC_ENABLED", (Object)enable);
                    row.set("DM_DOMAIN_ID", (Object)dmDomainID);
                    row.set("RESOURCE_TYPE", (Object)curResType);
                    dObj.addRow(row);
                    changed = true;
                }
            }
            IdpsUtil.getPersistenceLite().update(dObj);
            if (resTypes.contains((Object)1000)) {
                resTypes.remove(1000);
            }
            final String dmDomainName = dmDomainProps.getProperty("NAME");
            final Long customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
            if (!enable) {
                if (!resTypes.isEmpty()) {
                    final JSONObject qData = new JSONObject();
                    qData.put((Object)"RESOURCE_TYPE", (Object)resTypes);
                    qData.put((Object)IdpEventConstants.STATUS_CHANGE_EVENT, (Object)false);
                    qData.put((Object)"TASK_TYPE", (Object)"DISABLE_RES_TYPE_SYNC");
                    DirectoryUtil.getInstance().addTaskToQueue("adCoreDB-task", dmDomainProps, qData);
                }
                else {
                    DirectoryUtil.getInstance().syncDomain(dmDomainProps, true);
                }
            }
            else if (changed && initateSync) {
                DirectoryUtil.getInstance().syncDomain(dmDomainProps, true);
            }
        }
    }
    
    public List<Integer> getObjectTypesToBeSynced(final Long dmDomainID) throws DataAccessException {
        final List<Integer> syncObjTypes = new ArrayList<Integer>();
        final DataObject dObj = IdpsUtil.getPersistenceLite().get(IdpsUtil.formSelectQuery("DirectorySyncObject", new Criteria(Column.getColumn("DirectorySyncObject", "DM_DOMAIN_ID"), (Object)dmDomainID, 0), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DirectorySyncObject", "*"))), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new SortColumn(Column.getColumn("DirectorySyncObject", "RESOURCE_TYPE"), true, false))), (ArrayList)null, (Criteria)null));
        if (!dObj.isEmpty()) {
            final Iterator itr = dObj.getRows("DirectorySyncObject");
            while (itr != null && itr.hasNext()) {
                final Row row = itr.next();
                final Boolean syncEnabled = (Boolean)row.get("SYNC_ENABLED");
                if (syncEnabled != null && syncEnabled.equals(Boolean.TRUE)) {
                    syncObjTypes.add(Integer.valueOf(String.valueOf(row.get("RESOURCE_TYPE"))));
                }
            }
        }
        return syncObjTypes;
    }
    
    public org.json.JSONObject getDMdomainSyncDetails(final Long dmDomainId) throws Exception {
        org.json.JSONObject dmSyncDetails = null;
        final DataObject dObj = SyMUtil.getPersistenceLite().get("DMDomainSyncDetails", new Criteria(Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainId, 0, false));
        if (!dObj.isEmpty()) {
            dmSyncDetails = new org.json.JSONObject();
            final Row r = dObj.getFirstRow("DMDomainSyncDetails");
            dmSyncDetails.put("SYNC_STATUS", r.get("SYNC_STATUS"));
            dmSyncDetails.put("FETCH_STATUS", r.get("FETCH_STATUS"));
            dmSyncDetails.put("LAST_MODIFIED_BY", r.get("LAST_MODIFIED_BY"));
            dmSyncDetails.put("LAST_SUCCESSFUL_SYNC", r.get("LAST_SUCCESSFUL_SYNC"));
        }
        return dmSyncDetails;
    }
    
    static {
        DMDomainSyncDetailsDataHandler.dmDomainSyncDetailsDataHandler = null;
    }
}
