package com.me.mdm.server.profiles.ios;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;

public class DeviceConfigPayloadsDataHandler
{
    public void clearAndUpdateInstalledProfiles(final Long resID, final JSONArray resProfilesJsonArray) throws Exception {
        try {
            MDMUtil.getUserTransaction().begin();
            this.clearInstalledProfiles(resID);
            this.setInstalledProfiles(resID, resProfilesJsonArray);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while clearAndUpdateInstalledProfiles() ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception re) {
                Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while ROLLBACK clearAndUpdateInstalledProfiles() ", re);
            }
            throw e;
        }
    }
    
    protected void clearInstalledProfiles(final Long resource) throws Exception {
        final Criteria c = new Criteria(Column.getColumn("ResourceToConfigProfiles", "RESOURCE_ID"), (Object)resource, 0);
        DataAccess.delete("ResourceToConfigProfiles", c);
    }
    
    protected void setInstalledProfiles(final Long resource, final JSONArray resProfilesJsonArray) throws Exception {
        final DataObject dO = MDMUtil.getPersistence().constructDataObject();
        for (int i = 0; i < resProfilesJsonArray.length(); ++i) {
            final JSONObject json = resProfilesJsonArray.getJSONObject(i);
            final Row row = new Row("ResourceToConfigProfiles");
            row.set("RESOURCE_ID", (Object)resource);
            row.set("PROFILE_PAYLOAD_ID", json.get("PROFILE_PAYLOAD_ID"));
            row.set("INSTALLED_SOURCE", json.get("INSTALLED_SOURCE"));
            dO.addRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public JSONArray getInstalledProfilesDetails(final Long resourceID, final JSONObject filterObject) throws Exception {
        final List<Long> resourceList = new ArrayList<Long>();
        resourceList.add(resourceID);
        final JSONObject resourceObject = this.getInstalledProfilesDetails(resourceList, filterObject);
        final JSONArray payloadArray = resourceObject.optJSONArray(resourceID.toString());
        return payloadArray;
    }
    
    public JSONObject getInstalledProfilesDetails(final List<Long> resourceIDs, final JSONObject filterObject) throws Exception {
        final JSONObject resourceObject = new JSONObject();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ResourceToConfigProfiles"));
            final Join outerProfileJoin = new Join("ResourceToConfigProfiles", "IOSConfigPayload", new String[] { "PROFILE_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2);
            final Join outerProfileExtnJoin = new Join("IOSConfigPayload", "IOSConfigProfile", new String[] { "PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2);
            sq.addJoin(outerProfileJoin);
            sq.addJoin(outerProfileExtnJoin);
            Criteria resC = new Criteria(Column.getColumn("ResourceToConfigProfiles", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Integer installedSource = filterObject.optInt("INSTALLED_SOURCE", -1);
            if (installedSource != -1) {
                resC = resC.and(new Criteria(new Column("ResourceToConfigProfiles", "INSTALLED_SOURCE"), (Object)installedSource, 0));
            }
            final String payloadIdentifier = filterObject.optString("PAYLOAD_IDENTIFIER");
            if (!MDMStringUtils.isEmpty(payloadIdentifier)) {
                resC = resC.and(new Criteria(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), (Object)payloadIdentifier, 2));
            }
            sq.setCriteria(resC);
            sq.addSelectColumn(Column.getColumn("ResourceToConfigProfiles", "RESOURCE_ID"));
            sq.addSelectColumn(Column.getColumn("ResourceToConfigProfiles", "PROFILE_PAYLOAD_ID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_ID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_UUID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_DISPLAY_NAME"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_DESCRIPTION"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_ORGANIZATION"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_IDENTIFIER"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_TYPE"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_VERSION"));
            sq.addSelectColumn(Column.getColumn("IOSConfigProfile", "PAYLOAD_ID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigProfile", "PAYLOAD_IS_ENCRYPTED"));
            sq.addSelectColumn(Column.getColumn("IOSConfigProfile", "PAYLOAD_UNREMOVABLE"));
            sq.addSelectColumn(Column.getColumn("IOSConfigProfile", "PAYLOAD_HAS_REM_PASSWORD"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(sq);
            if (!dataObject.isEmpty()) {
                for (final Long resourceId : resourceIDs) {
                    final JSONArray jsonArray = new JSONArray();
                    final Criteria resourceCriteria = new Criteria(new Column("ResourceToConfigProfiles", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Iterator payloadIterator = dataObject.getRows("IOSConfigPayload", resourceCriteria);
                    while (payloadIterator.hasNext()) {
                        final JSONObject outerJson = new JSONObject();
                        final Row configPayloadRow = payloadIterator.next();
                        final Long payloadId = (Long)configPayloadRow.get("PAYLOAD_ID");
                        outerJson.put("PAYLOAD_UUID", configPayloadRow.get("PAYLOAD_UUID"));
                        outerJson.put("PAYLOAD_DISPLAY_NAME", configPayloadRow.get("PAYLOAD_DISPLAY_NAME"));
                        outerJson.put("PAYLOAD_DESCRIPTION", configPayloadRow.get("PAYLOAD_DESCRIPTION"));
                        outerJson.put("PAYLOAD_ORGANIZATION", configPayloadRow.get("PAYLOAD_ORGANIZATION"));
                        outerJson.put("PAYLOAD_IDENTIFIER", configPayloadRow.get("PAYLOAD_IDENTIFIER"));
                        outerJson.put("PAYLOAD_TYPE", configPayloadRow.get("PAYLOAD_TYPE"));
                        outerJson.put("PAYLOAD_VERSION", configPayloadRow.get("PAYLOAD_VERSION"));
                        final Row configProfileRow = dataObject.getRow("IOSConfigProfile", new Criteria(new Column("IOSConfigProfile", "PAYLOAD_ID"), (Object)payloadId, 0));
                        outerJson.put("PAYLOAD_UNREMOVABLE", configProfileRow.get("PAYLOAD_UNREMOVABLE"));
                        outerJson.put("PAYLOAD_HAS_REM_PASSWORD", configProfileRow.get("PAYLOAD_HAS_REM_PASSWORD"));
                        outerJson.put("PAYLOAD_IS_ENCRYPTED", configProfileRow.get("PAYLOAD_IS_ENCRYPTED"));
                        final JSONArray innerPayloads = this.getInnerPayloadDetails(payloadId);
                        if (innerPayloads.length() > 0) {
                            outerJson.put("PayloadContent", (Object)innerPayloads);
                        }
                        jsonArray.put((Object)outerJson);
                    }
                    resourceObject.put(resourceId.toString(), (Object)jsonArray);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while getInstalledProvisioningProfiles() ", e);
            throw new Exception("Error while getInstalledProfilesDetails(). See trace. ", e);
        }
        return resourceObject;
    }
    
    public JSONArray getInnerPayloadDetails(final Long outerProfileID) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("IOSConfigProfilePayloads"));
            final Join innerPayloadsJoin = new Join("IOSConfigProfilePayloads", "IOSConfigPayload", new String[] { "PAYLOAD_PAYLOAD_ID" }, new String[] { "PAYLOAD_ID" }, 2);
            sq.addJoin(innerPayloadsJoin);
            final Criteria profC = new Criteria(Column.getColumn("IOSConfigProfilePayloads", "PROFILE_PAYLOAD_ID"), (Object)outerProfileID, 0);
            sq.setCriteria(profC);
            sq.addSelectColumn(Column.getColumn("IOSConfigProfilePayloads", "PROFILE_PAYLOAD_ID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigProfilePayloads", "PAYLOAD_PAYLOAD_ID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_ID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_UUID"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_DISPLAY_NAME"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_DESCRIPTION"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_ORGANIZATION"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_IDENTIFIER"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_TYPE"));
            sq.addSelectColumn(Column.getColumn("IOSConfigPayload", "PAYLOAD_VERSION"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("IOSConfigPayload");
                if (iterator.hasNext()) {
                    final Row configPayloadRow = iterator.next();
                    final JSONObject json = new JSONObject();
                    json.put("PAYLOAD_UUID", configPayloadRow.get("PAYLOAD_UUID"));
                    json.put("PAYLOAD_DISPLAY_NAME", configPayloadRow.get("PAYLOAD_DISPLAY_NAME"));
                    json.put("PAYLOAD_DESCRIPTION", configPayloadRow.get("PAYLOAD_DESCRIPTION"));
                    json.put("PAYLOAD_ORGANIZATION", configPayloadRow.get("PAYLOAD_ORGANIZATION"));
                    json.put("PAYLOAD_IDENTIFIER", configPayloadRow.get("PAYLOAD_IDENTIFIER"));
                    json.put("PAYLOAD_TYPE", configPayloadRow.get("PAYLOAD_TYPE"));
                    json.put("PAYLOAD_VERSION", configPayloadRow.get("PAYLOAD_VERSION"));
                    jsonArray.put((Object)json);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while getInnerPayloadDetails() ", e);
        }
        return jsonArray;
    }
    
    private interface INNERCONFIGPAYLOADS
    {
        public static final String PAYLOAD_ID = "INNER_ID";
        public static final String PAYLOAD_DISPLAY_NAME = "INNER_NAME";
        public static final String PAYLOAD_DESCRIPTION = "INNER_DESCRIPTION";
        public static final String PAYLOAD_IDENTIFIER = "INNER_IDENTIFIER";
        public static final String PAYLOAD_ORG = "INNER_ORG";
        public static final String PAYLOAD_TYPE = "INNER_TYPE";
        public static final String PAYLOAD_UUID = "INNER_UUID";
        public static final String PAYLOAD_VERSION = "INNER_VERSION";
        public static final String TABLE = "INNERCONFIGPAYLOADS";
    }
    
    private interface OUTERCONFIGPROFILES
    {
        public static final String TABLE = "OUTERCONFIGPROFILES";
    }
}
