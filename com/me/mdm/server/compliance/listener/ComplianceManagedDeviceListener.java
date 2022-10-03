package com.me.mdm.server.compliance.listener;

import java.util.List;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.server.compliance.ComplianceDistributionHandler;
import java.util.HashMap;
import java.util.Collection;
import org.json.JSONObject;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class ComplianceManagedDeviceListener extends ManagedDeviceListener
{
    private static Logger logger;
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
        try {
            final Long resourceId = userEvent.resourceID;
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("ComplianceToResource");
            deleteQuery.setCriteria(resourceCriteria);
            MDMUtil.getPersistence().delete(deleteQuery);
            ComplianceManagedDeviceListener.logger.log(Level.INFO, "Deleted compliance status for device {0}", resourceId);
        }
        catch (final DataAccessException e) {
            ComplianceManagedDeviceListener.logger.log(Level.SEVERE, " -- deviceUnmanaged()  >   Error   ", (Throwable)e);
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
        try {
            final Long resourceId = userEvent.resourceID;
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("ComplianceToResource");
            deleteQuery.setCriteria(resourceCriteria);
            MDMUtil.getPersistence().delete(deleteQuery);
            ComplianceManagedDeviceListener.logger.log(Level.INFO, "Deleted compliance status for device {0}", resourceId);
        }
        catch (final DataAccessException e) {
            ComplianceManagedDeviceListener.logger.log(Level.SEVERE, " -- deviceUnmanaged()  >   Error   ", (Throwable)e);
        }
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        try {
            final Long oldUserId = JSONUtil.optLongForUVH(deviceEvent.resourceJSON, "oldUserId", Long.valueOf(-1L));
            final Long userId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            String userName = null;
            if (userId != null) {
                userName = DMUserHandler.getUserNameFromUserID(userId);
            }
            final List resourceList = Arrays.asList(deviceEvent.resourceID);
            final Long customerId = deviceEvent.customerID;
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            List addedComplianceForManagedUser = handler.getAddedProfileForManagedUser(oldUserId, 5);
            HashMap profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedComplianceForManagedUser);
            if (!profileCollectionMap.isEmpty()) {
                final JSONObject complianceJSON = new JSONObject();
                complianceJSON.put("user_id", (Object)userId);
                complianceJSON.put("customer_id", (Object)customerId);
                complianceJSON.put("user_name", (Object)userName);
                for (int i = 0; i < addedComplianceForManagedUser.toArray().length; ++i) {
                    complianceJSON.put("resource_list", (Collection)resourceList);
                    final HashMap policyMap = addedComplianceForManagedUser.get(i);
                    final Long complianceId = policyMap.get("profileId");
                    complianceJSON.put("compliance_id", (Object)complianceId);
                    final Long collectionId = profileCollectionMap.get(complianceId);
                    complianceJSON.put("collection_id", (Object)collectionId);
                    complianceJSON.put("profile_id", (Object)complianceId);
                    final Boolean isRemoveSafe = ComplianceDistributionHandler.getInstance().checkComplianceRemoveSafeOnUserChange(deviceEvent.resourceID, complianceId, collectionId);
                    if (isRemoveSafe) {
                        ComplianceDistributionHandler.getInstance().disassociateComplianceToDevices(complianceJSON);
                    }
                }
            }
            ComplianceManagedDeviceListener.logger.log(Level.INFO, "Removed policy for device with old user {0}", oldUserId);
            final HashMap userMap = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceEvent.resourceID);
            final Long managedUserId = userMap.get("MANAGED_USER_ID");
            addedComplianceForManagedUser = handler.getAddedProfileForManagedUser(managedUserId, 5);
            profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedComplianceForManagedUser);
            if (!profileCollectionMap.isEmpty()) {
                final JSONObject complianceJSON2 = new JSONObject();
                complianceJSON2.put("user_id", (Object)userId);
                complianceJSON2.put("customer_id", (Object)customerId);
                complianceJSON2.put("user_name", (Object)userName);
                for (int j = 0; j < addedComplianceForManagedUser.toArray().length; ++j) {
                    complianceJSON2.put("resource_list", (Collection)resourceList);
                    final HashMap policyMap2 = addedComplianceForManagedUser.get(j);
                    final Long complianceId2 = policyMap2.get("profileId");
                    complianceJSON2.put("compliance_id", (Object)complianceId2);
                    final Long collectionId2 = profileCollectionMap.get(complianceId2);
                    complianceJSON2.put("collection_id", (Object)collectionId2);
                    complianceJSON2.put("profile_id", (Object)complianceId2);
                    ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON2);
                }
            }
            ComplianceManagedDeviceListener.logger.log(Level.INFO, "Added policy for device with new user {0}", oldUserId);
        }
        catch (final Exception e) {
            ComplianceManagedDeviceListener.logger.log(Level.SEVERE, " -- userAssigned()  >   Error   ", e);
        }
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        try {
            final List resourceList = Arrays.asList(deviceEvent.resourceID);
            final Long managedUserId = JSONUtil.optLongForUVH(deviceEvent.resourceJSON, "MANAGED_USER_ID", Long.valueOf(-1L));
            final Long customerId = deviceEvent.customerID;
            final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
            final List addedComplianceForManagedUser = handler.getAddedProfileForManagedUser(managedUserId, 5);
            final HashMap profileCollectionMap = handler.getProfileToCollnMapForManagedUserListener(addedComplianceForManagedUser);
            if (!profileCollectionMap.isEmpty()) {
                final JSONObject complianceJSON = new JSONObject();
                complianceJSON.put("customer_id", (Object)customerId);
                for (int i = 0; i < addedComplianceForManagedUser.toArray().length; ++i) {
                    complianceJSON.put("resource_list", (Collection)resourceList);
                    final HashMap policyMap = addedComplianceForManagedUser.get(i);
                    final Long complianceId = policyMap.get("profileId");
                    final Long userId = policyMap.get("associatedByUser");
                    final String userName = DMUserHandler.getUserNameFromUserID(userId);
                    complianceJSON.put("user_id", (Object)userId);
                    complianceJSON.put("user_name", (Object)userName);
                    complianceJSON.put("compliance_id", (Object)complianceId);
                    final Long collectionId = profileCollectionMap.get(complianceId);
                    complianceJSON.put("collection_id", (Object)collectionId);
                    complianceJSON.put("profile_id", (Object)complianceId);
                    ComplianceDistributionHandler.getInstance().associateComplianceToDevices(complianceJSON);
                }
            }
            ComplianceManagedDeviceListener.logger.log(Level.INFO, "Added policy for device with new user {0}", managedUserId);
        }
        catch (final Exception e) {
            ComplianceManagedDeviceListener.logger.log(Level.SEVERE, " -- deviceManaged()  >   Error   ", e);
        }
    }
    
    static {
        ComplianceManagedDeviceListener.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
}
