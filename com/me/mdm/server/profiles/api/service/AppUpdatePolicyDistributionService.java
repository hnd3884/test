package com.me.mdm.server.profiles.api.service;

import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyPostDisAssociationListener;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePostPolicyAssociationListener;
import java.util.ArrayList;
import com.me.mdm.server.profiles.api.model.ProfileAssociationToGroupModel;

public class AppUpdatePolicyDistributionService extends ProfileService
{
    @Override
    protected Integer getProfileType() {
        return 12;
    }
    
    @Override
    public void associateProfilesToGroups(final ProfileAssociationToGroupModel profileAssociationToGroupModel) throws APIHTTPException {
        try {
            super.associateProfilesToGroups(profileAssociationToGroupModel);
            List groupIds = profileAssociationToGroupModel.getGroupIds();
            if (groupIds == null || groupIds.isEmpty()) {
                final Long groupId = profileAssociationToGroupModel.getGroupId();
                groupIds = new ArrayList();
                groupIds.add(groupId);
            }
            List profileIds = profileAssociationToGroupModel.getProfileIds();
            if (profileIds == null || profileIds.isEmpty()) {
                final Long profileId = profileAssociationToGroupModel.getProfileId();
                profileIds = new ArrayList();
                profileIds.add(profileId);
            }
            AppUpdatePostPolicyAssociationListener.getInstance().invokePostPolicyAssociationListener(groupIds, profileIds);
        }
        catch (final Exception ex) {
            AppUpdatePolicyDistributionService.logger.log(Level.SEVERE, "Exception in associateProfilesToGroups in AppUpdatePolicyDistributionService", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void disassociateProfilesToGroups(final ProfileAssociationToGroupModel profileAssociationToGroupModel) throws APIHTTPException {
        try {
            super.disassociateProfilesToGroups(profileAssociationToGroupModel);
            List groupIds = profileAssociationToGroupModel.getGroupIds();
            if (groupIds == null || groupIds.isEmpty()) {
                final Long groupId = profileAssociationToGroupModel.getGroupId();
                groupIds = new ArrayList();
                groupIds.add(groupId);
            }
            List profileIds = profileAssociationToGroupModel.getProfileIds();
            if (profileIds == null || profileIds.isEmpty()) {
                final Long profileId = profileAssociationToGroupModel.getProfileId();
                profileIds = new ArrayList();
                profileIds.add(profileId);
            }
            AppUpdatePolicyPostDisAssociationListener.getInstance().invokePostPolicyDisAssociationListener(groupIds, profileIds);
        }
        catch (final Exception ex) {
            AppUpdatePolicyDistributionService.logger.log(Level.SEVERE, "Exception in associateProfilesToGroups in disassociateProfilesToGroups", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
