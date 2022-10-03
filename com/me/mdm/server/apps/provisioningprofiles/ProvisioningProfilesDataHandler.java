package com.me.mdm.server.apps.provisioningprofiles;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import java.util.logging.Logger;

public class ProvisioningProfilesDataHandler
{
    private Logger logger;
    
    public ProvisioningProfilesDataHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONArray addOrUpdateProvisioningProfiles(final JSONArray jsonArray) throws Exception {
        final ArrayList<String> uuids = this.getUUIDList(jsonArray);
        final JSONArray returnArray = new JSONArray();
        if (!uuids.isEmpty()) {
            final Criteria uc = new Criteria(Column.getColumn("AppleProvisioningProfiles", "PROV_UUID"), (Object)uuids.toArray(), 8);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AppleProvisioningProfiles"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.setCriteria(uc);
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject pJson = jsonArray.getJSONObject(i);
                final Criteria rc = new Criteria(Column.getColumn("AppleProvisioningProfiles", "PROV_UUID"), pJson.get("PROV_UUID"), 0);
                final Row row = dO.getRow("AppleProvisioningProfiles", rc);
                if (row == null) {
                    final Row newRow = new Row("AppleProvisioningProfiles");
                    newRow.set("PROV_UUID", pJson.get("PROV_UUID"));
                    newRow.set("PROV_NAME", pJson.get("PROV_NAME"));
                    newRow.set("PROV_EXPIRY_DATE", pJson.get("PROV_EXPIRY_DATE"));
                    dO.addRow(newRow);
                }
            }
            MDMUtil.getPersistence().update(dO);
            for (int j = 0; j < jsonArray.length(); ++j) {
                final JSONObject json = new JSONObject(jsonArray.getJSONObject(j).toString());
                final Criteria rc = new Criteria(Column.getColumn("AppleProvisioningProfiles", "PROV_UUID"), json.get("PROV_UUID"), 0);
                final Row row = dO.getRow("AppleProvisioningProfiles", rc);
                final Long provId = (Long)row.get("PROV_ID");
                json.put("PROV_ID", (Object)provId);
                returnArray.put((Object)json);
            }
        }
        return returnArray;
    }
    
    public JSONObject addOrUpdateAppleProvProfiles(final JSONObject jsonObject) throws APIHTTPException {
        try {
            Object provID = JSONUtil.optLongForUVH(jsonObject, "PROV_ID", Long.valueOf(-1L));
            provID = (((long)provID == -1L) ? null : provID);
            final String teamID = String.valueOf(jsonObject.get("TEAM_ID"));
            final Long customerID = jsonObject.getLong("CUSTOMER_ID");
            String provAppID = jsonObject.optString("PROV_APP_ID", (String)null);
            final String provUUID = jsonObject.optString("PROV_UUID", "--");
            provAppID = (MDMStringUtils.isEmpty(provAppID) ? "--" : (teamID + "." + provAppID));
            final DataObject dataObject = this.getAppleProvProfileDO(provID, customerID, null, teamID, provAppID);
            final Boolean isModifyOperation = !dataObject.isEmpty();
            if (!isModifyOperation && provID != null) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            provID = MDMDBUtil.updateRow(dataObject, "AppleProvProfilesDetails", new Object[][] { { "PROV_NAME", String.valueOf(jsonObject.get("PROV_NAME")) }, { "CUSTOMER_ID", jsonObject.getLong("CUSTOMER_ID") }, { "PROV_UUID", provUUID }, { "PROV_EXPIRY_DATE", JSONUtil.optLongForUVH(jsonObject, "PROV_EXPIRY_DATE", Long.valueOf(0L)) }, { "PROV_PROFILE_TYPE", jsonObject.optInt("PROV_PROFILE_TYPE", 1) }, { "PROV_PROFILE_PLATFORM", jsonObject.optString("PROV_PROFILE_PLATFORM") }, { "PROV_ID", provID } }).get("PROV_ID");
            MDMDBUtil.updateRow(dataObject, "AppleProvProfilesExtn", new Object[][] { { "PROV_ID", provID }, { "PROV_PROFILE_PATH", jsonObject.optString("PROV_PROFILE_PATH", "--") }, { "CREATED_TIME", MDMUtil.getCurrentTimeInMillis() }, { "PROV_APP_ID", provAppID }, { "TEAM_ID", teamID }, { "PROV_PROV_SIGNED_TYPE", jsonObject.optInt("PROV_PROV_SIGNED_TYPE", 1) } });
            if (isModifyOperation) {
                MDMUtil.getPersistence().update(dataObject);
            }
            else {
                MDMUtil.getPersistence().add(dataObject);
            }
            return this.getAppleProvProfilesDetails(dataObject, null);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to perform add/update provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to perform add/update provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static Boolean isProvAppIDEmpty(final Long provID) throws Exception {
        final String provAppID = (String)DBUtil.getValueFromDB("AppleProvProfilesExtn", "PROV_ID", (Object)provID, "PROV_APP_ID");
        return provAppID == "--" || MDMStringUtils.isEmpty(provAppID);
    }
    
    public JSONObject getAppleProvProfilesDetails(final Long provID, final Long customerID) {
        try {
            final DataObject dataObject = this.getAppleProvProfileDO(provID, customerID, null, null, null);
            return this.getAppleProvProfilesDetails(dataObject, null);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to get provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to get provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAppleProvProfilesDetails(final DataObject dataObject, final Long provID) {
        try {
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            Row provRow;
            Row extnRow;
            if (provID != null) {
                final Criteria criteria = new Criteria(new Column("AppleProvProfilesDetails", "PROV_ID"), (Object)provID, 0);
                final Criteria criteriaExtn = new Criteria(new Column("AppleProvProfilesExtn", "PROV_ID"), (Object)provID, 0);
                provRow = dataObject.getRow("AppleProvProfilesDetails", criteria);
                extnRow = dataObject.getRow("AppleProvProfilesExtn", criteriaExtn);
            }
            else {
                provRow = dataObject.getFirstRow("AppleProvProfilesDetails");
                extnRow = dataObject.getFirstRow("AppleProvProfilesExtn");
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("PROV_NAME", (Object)provRow.get("PROV_NAME"));
            jsonObject.put("PROV_UUID", (Object)provRow.get("PROV_UUID"));
            jsonObject.put("CUSTOMER_ID", provRow.get("PROV_ID"));
            jsonObject.put("PROV_EXPIRY_DATE", provRow.get("PROV_EXPIRY_DATE"));
            jsonObject.put("PROV_PROFILE_PLATFORM", provRow.get("PROV_PROFILE_PLATFORM"));
            jsonObject.put("PROV_PROFILE_TYPE", provRow.get("PROV_PROFILE_TYPE"));
            jsonObject.put("PROV_ID", provRow.get("PROV_ID"));
            jsonObject.put("PROV_PROV_SIGNED_TYPE", extnRow.get("PROV_PROV_SIGNED_TYPE"));
            jsonObject.put("CREATED_TIME", extnRow.get("CREATED_TIME"));
            jsonObject.put("PROV_APP_ID", extnRow.get("PROV_APP_ID"));
            jsonObject.put("TEAM_ID", extnRow.get("TEAM_ID"));
            return jsonObject;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to get provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to get provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteAppleProvisioningProfile(final Long provID, final Long customerID) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AppleProvProfilesDetails");
            Criteria criteria = new Criteria(Column.getColumn("AppleProvProfilesDetails", "PROV_ID"), (Object)provID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("AppleProvProfilesDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            deleteQuery.setCriteria(criteria);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public DataObject getAppleProvProfileDO(final Object provID, final Long customerID, final String provUUID, final String teamID, final String provAppID) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleProvProfilesDetails"));
        query.addJoin(new Join("AppleProvProfilesDetails", "AppleProvProfilesExtn", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
        query.addSelectColumn(Column.getColumn("AppleProvProfilesDetails", "*"));
        query.addSelectColumn(Column.getColumn("AppleProvProfilesExtn", "*"));
        Criteria criteria;
        if (provID != null && (long)provID != -1L) {
            criteria = new Criteria(Column.getColumn("AppleProvProfilesExtn", "PROV_ID"), provID, 0);
        }
        else if (provUUID != null) {
            criteria = new Criteria(Column.getColumn("AppleProvProfilesDetails", "PROV_UUID"), (Object)provUUID, 0);
        }
        else {
            criteria = new Criteria(Column.getColumn("AppleProvProfilesExtn", "PROV_APP_ID"), (Object)provAppID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("AppleProvProfilesExtn", "TEAM_ID"), (Object)teamID, 0));
        }
        criteria = criteria.and(new Criteria(Column.getColumn("AppleProvProfilesDetails", "CUSTOMER_ID"), (Object)customerID, 0));
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    private ArrayList<String> getUUIDList(final JSONArray jsonArray) throws Exception {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final String uuid = jsonArray.getJSONObject(i).get("PROV_UUID").toString();
            list.add(uuid);
        }
        return list;
    }
}
