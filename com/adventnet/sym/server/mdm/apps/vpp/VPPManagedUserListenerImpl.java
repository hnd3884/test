package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.UserEvent;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;

public class VPPManagedUserListenerImpl implements ManagedUserListener
{
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
        final Long userId = userEvent.resourceID;
        final Long customerId = userEvent.customerID;
        final Properties vppUserIDToBusinessStoreMap = VPPManagedUserHandler.getInstance().getVppUserIDToBusinessStoreMap(userId, customerId);
        if (vppUserIDToBusinessStoreMap != null && !vppUserIDToBusinessStoreMap.isEmpty()) {
            final List vppUserIdList = new ArrayList();
            vppUserIdList.addAll(vppUserIDToBusinessStoreMap.keySet());
            for (int i = 0; i < vppUserIdList.size(); ++i) {
                final Long vppUserID = vppUserIdList.get(i);
                final Long businessStoreID = ((Hashtable<K, Long>)vppUserIDToBusinessStoreMap).get(vppUserID);
                final Boolean isSuccess = VPPManagedUserHandler.getInstance().retierUserToVPP(vppUserID, businessStoreID, customerId);
                if (isSuccess) {
                    new VPPManagedUserHandler().clearAppLicenseForUser(businessStoreID, userId);
                }
            }
        }
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
        final Long userId = userEvent.resourceID;
        final Long customerId = userEvent.customerID;
        final Long vppUserId = VPPManagedUserHandler.getInstance().getVPPUserId(userId);
        final Properties vppUserIDToBusinessStoreMap = VPPManagedUserHandler.getInstance().getVppUserIDToBusinessStoreMap(userId, customerId);
        if (vppUserIDToBusinessStoreMap != null && !vppUserIDToBusinessStoreMap.isEmpty()) {
            final List vppUserIdList = new ArrayList();
            vppUserIdList.addAll(vppUserIDToBusinessStoreMap.keySet());
            for (int i = 0; i < vppUserIdList.size(); ++i) {
                final Long vppUserID = vppUserIdList.get(i);
                final Long businessStoreID = ((Hashtable<K, Long>)vppUserIDToBusinessStoreMap).get(vppUserID);
                final Boolean isSuccess = VPPManagedUserHandler.getInstance().retierUserToVPP(vppUserID, businessStoreID, customerId);
                if (isSuccess) {
                    new VPPManagedUserHandler().clearAppLicenseForUser(businessStoreID, userId);
                }
            }
        }
    }
}
