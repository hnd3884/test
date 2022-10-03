package com.me.mdm.server.msp.sync;

import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.apache.commons.lang3.RandomStringUtils;
import com.me.mdm.server.profiles.config.ProfileConfigurationUtil;
import com.me.mdm.server.config.MDMConfigUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.webclient.mdm.config.formbean.CloneGlobalConfigHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ProfilePublishActionSyncHandler extends ProfilesSyncEngine
{
    public Boolean isProfileMovedToTrash;
    
    ProfilePublishActionSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
        this.isProfileMovedToTrash = Boolean.FALSE;
        this.isProfileMovedToTrash = this.qData.optBoolean("IS_MOVED_TO_TRASH");
    }
    
    @Override
    public void setParentDO() throws Exception {
        super.setParentDO();
    }
    
    @Override
    public JSONObject getChildSpecificUVH(final Long customerID) throws Exception {
        final JSONObject childSpecificUVH = super.getChildSpecificUVH(customerID);
        final Long childProfileId = childSpecificUVH.getLong("PROFILE_ID");
        final JSONObject cloneProfileDetails = SyncConfigurationsUtil.extractRequiredDetailsFromProfileDOToClone(this.parentProfileDO);
        if (childProfileId != -1L) {
            cloneProfileDetails.put("PROFILE_ID", (Object)childProfileId);
        }
        else {
            cloneProfileDetails.put("CREATED_BY", (Object)this.modifiedByUser);
        }
        cloneProfileDetails.put("CUSTOMER_ID", (Object)customerID);
        cloneProfileDetails.put("PROFILE_SHARED_SCOPE", 1);
        ProfileConfigHandler.addProfileCollection(cloneProfileDetails);
        return cloneProfileDetails;
    }
    
    private Long getCollectionIdToBeClonedFromParentDO() throws Exception {
        Long collectionId = -1L;
        final Row collectionRow = this.parentProfileDO.getFirstRow("RecentPubProfileToColln");
        collectionId = (Long)collectionRow.get("COLLECTION_ID");
        return collectionId;
    }
    
    private void moveProfileToTrash(final Long profileId) throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
        updateQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        updateQuery.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)true);
        DataAccess.update(updateQuery);
    }
    
    @Override
    public void sync() {
        try {
            this.setParentDO();
            List applicableCustomers;
            if (this.childCustomerId == -1L) {
                applicableCustomers = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            }
            else {
                final Long[] array;
                applicableCustomers = new ArrayList(Arrays.asList(array));
                array = new Long[] { this.childCustomerId };
            }
            final List customerList = applicableCustomers;
            ProfilePublishActionSyncHandler.logger.log(Level.INFO, "Syncing profile {0} for customers {1}", new Object[] { this.parentProfileDO, customerList });
            for (final Long customerId : customerList) {
                final String sUserName = DMUserHandler.getUserNameFromUserID(this.modifiedByUser);
                final String remarksArgs = CloneGlobalConfigHandler.getInstance().getProfileName(this.parentProfileDO);
                ProfilePublishActionSyncHandler.logger.log(Level.INFO, "Syncing profile for customer {0}", new Object[] { customerId });
                try {
                    final JSONObject childProfileDetails = this.getChildSpecificUVH(customerId);
                    final Long childCollectionId = childProfileDetails.getLong("COLLECTION_ID");
                    final Long parentCollectionId = this.getCollectionIdToBeClonedFromParentDO();
                    final DataObject clonedConfigData = ProfileConfigHandler.cloneConfigurations(parentCollectionId, childCollectionId);
                    final Iterator<Row> clonnedConfigDataItemExtnRows = clonedConfigData.getRows("MdConfigDataItemExtn");
                    final Row profileRow = clonedConfigData.getRow("Profile");
                    final String profileDataIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
                    while (clonnedConfigDataItemExtnRows.hasNext()) {
                        final Row configDataItemExtnRow = clonnedConfigDataItemExtnRows.next();
                        final Long configDataItemId = (Long)configDataItemExtnRow.get("CONFIG_DATA_ITEM_ID");
                        final Row configDataRow = clonedConfigData.getRow("ConfigData", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0), new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
                        final Integer configID = (Integer)configDataRow.get("CONFIG_ID");
                        final String configName = MDMConfigUtil.getConfigLabel(configID);
                        final String payloadName = ProfileConfigurationUtil.getInstance().getPayloadName(configName);
                        final String configDataIdentifier = ProfileConfigurationUtil.getInstance().getConfigDataIdentifier(payloadName);
                        final String payloadIdentifier = profileDataIdentifier + "." + configDataIdentifier + "." + RandomStringUtils.randomAlphanumeric(3);
                        configDataItemExtnRow.set("CONFIG_PAYLOAD_IDENTIFIER", (Object)payloadIdentifier);
                        clonedConfigData.updateRow(configDataItemExtnRow);
                    }
                    MDMUtil.getPersistence().update(clonedConfigData);
                    final String sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.publish_success";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
                    ProfileConfigHandler.publishProfile(childProfileDetails);
                    if (!this.isProfileMovedToTrash) {
                        continue;
                    }
                    ProfilePublishActionSyncHandler.logger.log(Level.INFO, "Trashed profile published {0}", new Object[] { childProfileDetails });
                    this.moveProfileToTrash(childProfileDetails.getLong("PROFILE_ID"));
                }
                catch (final Exception ex) {
                    ProfilePublishActionSyncHandler.logger.log(Level.SEVERE, "Exception while syncing profile {0} for customer {1} {2}", new Object[] { this.parentProfileDO, customerId, ex });
                    final String sEventLogRemarks = "dc.mdm.actionlog.profilemgmt.create_failure";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2021, null, sUserName, sEventLogRemarks, remarksArgs, customerId);
                }
            }
        }
        catch (final Exception ex2) {
            ProfilePublishActionSyncHandler.logger.log(Level.SEVERE, "Exception in sync() in Profile publish {0} {1}", new Object[] { this.parentProfileDO, ex2 });
        }
    }
}
