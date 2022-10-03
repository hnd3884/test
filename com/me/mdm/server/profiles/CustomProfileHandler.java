package com.me.mdm.server.profiles;

import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.HashSet;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import org.json.JSONObject;

public class CustomProfileHandler
{
    protected JSONObject existingPayloadDetails;
    protected Logger logger;
    
    public CustomProfileHandler() {
        this.existingPayloadDetails = new JSONObject();
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public Object addCustomProfile(final JSONObject customProfileJSON, final DataObject dataObject, final List<String> payloadTypeList) throws Exception {
        try {
            final String customProfileDBCompletePath = "";
            final Object customProfileId = this.addCustomProfileDetails(customProfileDBCompletePath, dataObject);
            this.addPayloadTypeDetails(payloadTypeList, dataObject);
            this.mapCustomProfileWithPayload(dataObject, payloadTypeList, customProfileId);
            return customProfileId;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in adding custom profile", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Exception in adding custom profile", (Throwable)e2);
            throw e2;
        }
    }
    
    protected Object addCustomProfileDetails(final String filePath, final DataObject dataObject) throws DataAccessException {
        final Row customProfileRow = new Row("CustomProfileDetails");
        customProfileRow.set("CUSTOM_PROFILE_PATH", (Object)filePath);
        dataObject.addRow(customProfileRow);
        return customProfileRow.get("CUSTOM_PROFILE_ID");
    }
    
    protected void addPayloadTypeDetails(final List<String> payloadTypeList, final DataObject dataObject) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("PayloadTypeDetails"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(new Criteria(new Column("PayloadTypeDetails", "PAYLOAD_TYPE"), (Object)payloadTypeList.toArray(), 8));
        final DataObject payloadTypeObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final List<String> notAddedList = new ArrayList<String>(payloadTypeList);
        if (!payloadTypeObject.isEmpty()) {
            final Iterator payloadTypeRows = payloadTypeObject.getRows("PayloadTypeDetails");
            while (payloadTypeRows.hasNext()) {
                final Row payloadTypeRow = payloadTypeRows.next();
                final String payloadType = (String)payloadTypeRow.get("PAYLOAD_TYPE");
                final Long payloadTypeId = (Long)payloadTypeRow.get("PAYLOAD_TYPE_ID");
                this.existingPayloadDetails.put(payloadType, (Object)payloadTypeId);
                notAddedList.remove(payloadType);
            }
        }
        for (final String payloadType2 : notAddedList) {
            final Row payloadTypeRow2 = new Row("PayloadTypeDetails");
            payloadTypeRow2.set("PAYLOAD_TYPE", (Object)payloadType2);
            dataObject.addRow(payloadTypeRow2);
        }
    }
    
    protected void mapCustomProfileWithPayload(final DataObject dataObject, final List<String> payloadTypeList, final Object customProfileId) throws DataAccessException {
        final HashSet<String> payloadTypehash = new HashSet<String>(payloadTypeList);
        for (final String payloadType : payloadTypehash) {
            Object payloadTypeId = this.existingPayloadDetails.opt(payloadType);
            if (payloadTypeId == null) {
                final Row payloadTypeRow = dataObject.getRow("PayloadTypeDetails", new Criteria(new Column("PayloadTypeDetails", "PAYLOAD_TYPE"), (Object)payloadType, 0));
                payloadTypeId = payloadTypeRow.get("PAYLOAD_TYPE_ID");
            }
            final Row profilePayloadRel = new Row("CustomProfileToPayloadDetails");
            profilePayloadRel.set("CUSTOM_PROFILE_ID", customProfileId);
            profilePayloadRel.set("PAYLOAD_TYPE_ID", payloadTypeId);
            dataObject.addRow(profilePayloadRel);
        }
    }
    
    public DataObject getCustomProfileDO(final Criteria criteria) throws Exception {
        final SelectQuery selectQuery = ProfileUtil.getProfileToConfigIdQuery();
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileDetails", "CustomProfileToPayloadDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileToPayloadDetails", "PayloadTypeDetails", new String[] { "PAYLOAD_TYPE_ID" }, new String[] { "PAYLOAD_TYPE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(new Column("CustomProfileDetails", "*"));
        selectQuery.addSelectColumn(new Column("CustomProfileToCfgDataItem", "*"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("CustomProfileToPayloadDetails", "*"));
        selectQuery.addSelectColumn(new Column("PayloadTypeDetails", "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
}
