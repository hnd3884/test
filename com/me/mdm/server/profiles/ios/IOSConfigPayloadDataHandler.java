package com.me.mdm.server.profiles.ios;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class IOSConfigPayloadDataHandler
{
    public static final Integer MANAGED_CONFIG_PROFILE;
    public static final Integer UNMANAGED_CONFIG_PROFILE;
    
    public Long addOrUpdateProfilePayloads(final JSONObject json) throws Exception {
        Long payloadID;
        try {
            Logger.getLogger("MDMLogger").log(Level.INFO, "addOrUpdateProfilePayloads BEGINS...");
            Logger.getLogger("MDMLogger").log(Level.FINE, "FULL DATA... {0}", json.toString());
            MDMUtil.getUserTransaction().begin();
            payloadID = this.getExistingPayloadID(json);
            Logger.getLogger("MDMLogger").log(Level.INFO, "existing payloadID = {0}", payloadID.toString());
            if (payloadID == -1L) {
                payloadID = this.addNewProfilePayloads(json);
            }
            else {
                this.updateProfilePayloads(json);
            }
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while addOrUpdateProfilePayloads() ", e);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception re) {
                Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while ROLLBACK addOrUpdateProfilePayloads() ", re);
            }
            throw e;
        }
        Logger.getLogger("MDMLogger").log(Level.INFO, "addOrUpdateProfilePayloads ENDS...");
        return payloadID;
    }
    
    protected Long addNewProfilePayloads(final JSONObject json) throws Exception {
        final Long outerPayloadID = this.addOrUpdatePayloadRow(json);
        final JSONObject extnJson = new JSONObject(json.toString());
        extnJson.put("PAYLOAD_ID", (Object)outerPayloadID);
        this.addProfileExtnRow(extnJson);
        final JSONArray payloadsArray = json.optJSONArray("PayloadContent");
        if (payloadsArray != null) {
            this.addProfilePayloadsRelations(outerPayloadID, payloadsArray);
        }
        return outerPayloadID;
    }
    
    protected Long updateProfilePayloads(final JSONObject json) throws Exception {
        final Long outerPayloadID = this.addOrUpdatePayloadRow(json);
        final JSONObject extnJson = new JSONObject(json.toString());
        extnJson.put("PAYLOAD_ID", (Object)outerPayloadID);
        this.modifyProfileExtnRow(extnJson);
        this.clearExistingProfilePayloadsRelations(outerPayloadID);
        final JSONArray payloadsArray = json.optJSONArray("PayloadContent");
        if (payloadsArray != null) {
            this.addProfilePayloadsRelations(outerPayloadID, payloadsArray);
        }
        return outerPayloadID;
    }
    
    protected Long addOrUpdatePayloadRow(final JSONObject json) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("IOSConfigPayload"));
        sq.setCriteria(getUniquePayloadCriteria(json));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sq);
        if (!dO.isEmpty()) {
            final Row updateRow = dO.getFirstRow("IOSConfigPayload");
            final Long payloadID = (Long)updateRow.get("PAYLOAD_ID");
            updateRow.set("PAYLOAD_ORGANIZATION", json.get("PAYLOAD_ORGANIZATION"));
            updateRow.set("PAYLOAD_DESCRIPTION", json.get("PAYLOAD_DESCRIPTION"));
            updateRow.set("PAYLOAD_DISPLAY_NAME", json.get("PAYLOAD_DISPLAY_NAME"));
            dO.updateRow(updateRow);
            MDMUtil.getPersistence().update(dO);
            return payloadID;
        }
        final Row newRow = new Row("IOSConfigPayload");
        newRow.set("PAYLOAD_UUID", json.get("PAYLOAD_UUID"));
        newRow.set("PAYLOAD_VERSION", json.get("PAYLOAD_VERSION"));
        newRow.set("PAYLOAD_TYPE", json.get("PAYLOAD_TYPE"));
        newRow.set("PAYLOAD_IDENTIFIER", json.get("PAYLOAD_IDENTIFIER"));
        newRow.set("PAYLOAD_DISPLAY_NAME", json.get("PAYLOAD_DISPLAY_NAME"));
        newRow.set("PAYLOAD_DESCRIPTION", json.get("PAYLOAD_DESCRIPTION"));
        newRow.set("PAYLOAD_ORGANIZATION", json.get("PAYLOAD_ORGANIZATION"));
        dO.addRow(newRow);
        MDMUtil.getPersistence().update(dO);
        final Long payloadID = (Long)dO.getFirstRow("IOSConfigPayload").get("PAYLOAD_ID");
        return payloadID;
    }
    
    protected void addProfilePayloadsRelations(final Long outerPayloadID, final JSONArray payloadsArray) throws Exception {
        Logger.getLogger("MDMLogger").log(Level.INFO, "addProfilePayloadsRelations for outerProfileID = {0}", outerPayloadID);
        final DataObject dO = MDMUtil.getPersistence().constructDataObject();
        if (payloadsArray != null) {
            final ArrayList<Long> addedPayloadIDs = new ArrayList<Long>();
            for (int i = 0; i < payloadsArray.length(); ++i) {
                final Long innerPayloadID = this.addOrUpdatePayloadRow(payloadsArray.getJSONObject(i));
                if (!addedPayloadIDs.contains(innerPayloadID)) {
                    addedPayloadIDs.add(innerPayloadID);
                    Logger.getLogger("MDMLogger").log(Level.FINE, "addOrUpdatePayloadRow()... {0}", payloadsArray.getJSONObject(i).toString());
                    Logger.getLogger("MDMLogger").log(Level.INFO, "payloadID added/updated = {0}", innerPayloadID);
                    final Row relationRow = new Row("IOSConfigProfilePayloads");
                    relationRow.set("PROFILE_PAYLOAD_ID", (Object)outerPayloadID);
                    relationRow.set("PAYLOAD_PAYLOAD_ID", (Object)innerPayloadID);
                    dO.addRow(relationRow);
                }
            }
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    protected void clearExistingProfilePayloadsRelations(final Long outerPayloadID) throws Exception {
        final Criteria idC = new Criteria(Column.getColumn("IOSConfigProfilePayloads", "PROFILE_PAYLOAD_ID"), (Object)outerPayloadID, 0);
        DataAccess.delete("IOSConfigProfilePayloads", idC);
    }
    
    private Long getExistingPayloadID(final JSONObject json) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("IOSConfigPayload"));
        sq.setCriteria(getUniquePayloadCriteria(json));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sq);
        if (dO != null && !dO.isEmpty()) {
            final Long payloadID = (Long)dO.getFirstRow("IOSConfigPayload").get("PAYLOAD_ID");
            return payloadID;
        }
        return -1L;
    }
    
    private void addProfileExtnRow(final JSONObject json) throws Exception {
        final DataObject dO = MDMUtil.getPersistence().constructDataObject();
        final Row profileExtnRow = new Row("IOSConfigProfile");
        profileExtnRow.set("PAYLOAD_ID", json.get("PAYLOAD_ID"));
        profileExtnRow.set("PAYLOAD_HAS_REM_PASSWORD", json.get("PAYLOAD_HAS_REM_PASSWORD"));
        profileExtnRow.set("PAYLOAD_IS_ENCRYPTED", json.get("PAYLOAD_IS_ENCRYPTED"));
        profileExtnRow.set("PAYLOAD_UNREMOVABLE", json.get("PAYLOAD_UNREMOVABLE"));
        dO.addRow(profileExtnRow);
        MDMUtil.getPersistence().update(dO);
    }
    
    private void modifyProfileExtnRow(final JSONObject json) throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("IOSConfigProfile"));
        final Criteria idC = new Criteria(Column.getColumn("IOSConfigProfile", "PAYLOAD_ID"), json.get("PAYLOAD_ID"), 0);
        sq.setCriteria(idC);
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(sq);
        if (!dO.isEmpty()) {
            final Row profileExtnRow = dO.getFirstRow("IOSConfigProfile");
            profileExtnRow.set("PAYLOAD_HAS_REM_PASSWORD", json.get("PAYLOAD_HAS_REM_PASSWORD"));
            profileExtnRow.set("PAYLOAD_IS_ENCRYPTED", json.get("PAYLOAD_IS_ENCRYPTED"));
            profileExtnRow.set("PAYLOAD_UNREMOVABLE", json.get("PAYLOAD_UNREMOVABLE"));
            dO.updateRow(profileExtnRow);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public static Criteria getUniquePayloadCriteria(final JSONObject json) throws Exception {
        final Criteria idC = new Criteria(Column.getColumn("IOSConfigPayload", "PAYLOAD_IDENTIFIER"), json.get("PAYLOAD_IDENTIFIER"), 0);
        final Criteria typeC = new Criteria(Column.getColumn("IOSConfigPayload", "PAYLOAD_TYPE"), json.get("PAYLOAD_TYPE"), 0);
        final Criteria uuidC = new Criteria(Column.getColumn("IOSConfigPayload", "PAYLOAD_UUID"), json.get("PAYLOAD_UUID"), 0);
        final Criteria verC = new Criteria(Column.getColumn("IOSConfigPayload", "PAYLOAD_VERSION"), json.get("PAYLOAD_VERSION"), 0);
        return idC.and(typeC).and(uuidC).and(verC);
    }
    
    public static String getPayloadIdentifierFromPayloadId(final Long payloadId) {
        String payloadIdentifier = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("IOSConfigPayload"));
            final Criteria payloadIdCriteria = new Criteria(new Column("IOSConfigPayload", "PAYLOAD_ID"), (Object)payloadId, 0);
            selectQuery.setCriteria(payloadIdCriteria);
            selectQuery.addSelectColumn(new Column("IOSConfigPayload", "PAYLOAD_ID"));
            selectQuery.addSelectColumn(new Column("IOSConfigPayload", "PAYLOAD_IDENTIFIER"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row configRow = dataObject.getRow("IOSConfigPayload");
                payloadIdentifier = (String)configRow.get("PAYLOAD_IDENTIFIER");
            }
        }
        catch (final DataAccessException e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in getting payload Identifier", (Throwable)e);
        }
        return payloadIdentifier;
    }
    
    static {
        MANAGED_CONFIG_PROFILE = 1;
        UNMANAGED_CONFIG_PROFILE = 2;
    }
}
