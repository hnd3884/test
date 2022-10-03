package com.me.mdm.server.profiles;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.mdm.files.MDMFileUtil;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.WritableDataObject;
import java.util.List;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public class AppleCustomProfileHandler extends CustomProfileHandler
{
    public static final Integer CUSTOM_CONFIGURATION;
    public static final Integer CUSTOM_COMMAND;
    
    @Override
    public Object addCustomProfile(final JSONObject customProfileJSON, final DataObject dataObject, final List<String> payloadTypeList) throws Exception {
        final DataObject customProfileDataObject = (DataObject)new WritableDataObject();
        super.addCustomProfile(customProfileJSON, customProfileDataObject, payloadTypeList);
        MDMUtil.getPersistenceLite().update(customProfileDataObject);
        final String filePath = customProfileJSON.optString("CUSTOM_PROFILE_PATH");
        final Integer customProfileType = customProfileJSON.optInt("CUSTOM_PROFILE_TYPE", (int)AppleCustomProfileHandler.CUSTOM_CONFIGURATION);
        final Long customerId = customProfileJSON.optLong("CUSTOMER_ID");
        final Row customProfileRow = customProfileDataObject.getRow("CustomProfileDetails");
        final Long customProfileId = (Long)customProfileRow.get("CUSTOM_PROFILE_ID");
        String customProfileDBCompletePath = "";
        if (!MDMStringUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            final String fileName = file.getName();
            final String customProfileDBpath = ProfileUtil.getCustomProfileDBPath(customProfileId, customerId);
            final String customProfileFolderPath = ProfileUtil.getCustomProfileFolderPath(customProfileId, customerId);
            MDMFileUtil.uploadFileToDirectory(filePath, customProfileFolderPath, fileName);
            customProfileDBCompletePath = customProfileDBpath + File.separator + fileName;
            customProfileDBCompletePath = customProfileDBCompletePath.replaceAll("\\\\", "/");
        }
        customProfileRow.set("CUSTOM_PROFILE_PATH", (Object)customProfileDBCompletePath);
        dataObject.updateBlindly(customProfileRow);
        Row appleCustomProfileExtnRow = null;
        if (dataObject.containsTable("AppleCustomProfilesDataExtn")) {
            appleCustomProfileExtnRow = dataObject.getRow("AppleCustomProfilesDataExtn");
            appleCustomProfileExtnRow.set("CUSTOM_PROFILE_ID", (Object)customProfileId);
            appleCustomProfileExtnRow.set("CUSTOM_PROFILE_TYPE", (Object)customProfileType);
            dataObject.updateRow(appleCustomProfileExtnRow);
        }
        else {
            appleCustomProfileExtnRow = new Row("AppleCustomProfilesDataExtn");
            appleCustomProfileExtnRow.set("CUSTOM_PROFILE_ID", (Object)customProfileId);
            appleCustomProfileExtnRow.set("CUSTOM_PROFILE_TYPE", (Object)customProfileType);
            dataObject.addRow(appleCustomProfileExtnRow);
        }
        return customProfileId;
    }
    
    public boolean isCustomCommandConfiguredForCollection(final Long collectionId) {
        try {
            final SelectQuery configQuery = this.getCustomConfigurationQuery();
            configQuery.addSelectColumn(new Column("AppleCustomProfilesDataExtn", "*"));
            final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            configQuery.setCriteria(collectionCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(configQuery);
            if (!dataObject.isEmpty()) {
                final Row appleCustomProfileExtnRow = dataObject.getRow("AppleCustomProfilesDataExtn");
                if (appleCustomProfileExtnRow.get("CUSTOM_PROFILE_TYPE").equals(AppleCustomProfileHandler.CUSTOM_COMMAND)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isCustomCommand", e);
        }
        return false;
    }
    
    public SelectQuery getCustomConfigurationQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "CustomProfileToCfgDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileToCfgDataItem", "CustomProfileDetails", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("CustomProfileDetails", "AppleCustomProfilesDataExtn", new String[] { "CUSTOM_PROFILE_ID" }, new String[] { "CUSTOM_PROFILE_ID" }, 2));
        return selectQuery;
    }
    
    public DataObject getCustomCommandDO(final Long collectionId) {
        try {
            final SelectQuery selectQuery = this.getCustomConfigurationQuery();
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            selectQuery.setCriteria(collectionCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            return dataObject;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in isCustomCommand", (Throwable)e);
            return null;
        }
    }
    
    static {
        CUSTOM_CONFIGURATION = 1;
        CUSTOM_COMMAND = 2;
    }
}
