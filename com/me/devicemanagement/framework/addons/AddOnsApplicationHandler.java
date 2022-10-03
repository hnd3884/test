package com.me.devicemanagement.framework.addons;

import java.util.Set;
import org.json.JSONObject;

public interface AddOnsApplicationHandler
{
    boolean addOnStatusUpdated(final int p0, final JSONObject p1);
    
    boolean addOnPreHandling(final int p0, final JSONObject p1);
    
    boolean addOnPostHandling(final int p0, final JSONObject p1);
    
    void addOnPreHandlingRevert(final int p0, final JSONObject p1);
    
    void addOnPostHandlingRevert(final int p0, final JSONObject p1);
    
    void addOrModifyAddonsForBulkResources(final JSONObject p0);
    
    boolean isResourceApplicableForAddon(final Long p0);
    
    boolean isGroupApplicableForAddon(final Long p0);
    
    void addAddonForResource(final Long p0);
    
    void removeAddonForResource(final Long p0);
    
    int getAddonInstalledCount();
    
    int getLicensedResourceCountForAddon();
    
    void addOnResourceLimitExceedHandling();
    
    String getAddonApplicationName();
    
    String[] getListOfApplicationRolesToBeHandled();
    
    void generateAddonMetaFile();
    
    void rolesUpdated();
    
    Set<Long> getViewIdListToBeHandled();
}
