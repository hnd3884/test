package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MacFileVaultNotApplicableOnNonUserApprovedMDMHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        List resourceList = configNotApplicable.resourceList;
        resourceList = new MacFileVaultProfileOSVersionNotApplicableHandler().getResourcesWithLessthanGeraterThanGivenVersion(resourceList, 10.15f, true);
        if (!resourceList.isEmpty()) {
            resourceList = getFilevaultNonUserApprovedDevices(resourceList);
        }
        return resourceList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.profile.filevault_non_user_approved_mdm@@@<l>$(mdmUrl)/how-to/mac_user_approved_mdm.html?$(traceurl)&$(did)&pgSrc=filevault_not_applicable");
        }
        catch (final Exception ex) {
            MacFileVaultNotApplicableOnNonUserApprovedMDMHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in  MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler setNotApplicableStatus():", ex);
        }
    }
    
    public static List<Long> getFilevaultNonUserApprovedDevices(final List<Long> resourceIDList) {
        final Criteria resIDCri = new Criteria(new Column("MDDeviceManagementInfo", "RESOURCE_ID"), (Object)resourceIDList.toArray(), 8);
        final Criteria enabledCri = new Criteria(new Column("MDDeviceManagementInfo", "MANAGEMENT_TYPE"), (Object)new int[] { 1, 2 }, 9);
        final List<Long> resListLong = new ArrayList<Long>();
        try {
            final List resList = DBUtil.getDistinctColumnValue("MDDeviceManagementInfo", "RESOURCE_ID", resIDCri.and(enabledCri));
            for (int i = 0; i < resList.size(); ++i) {
                resListLong.add(Long.parseLong(resList.get(i)));
            }
            return resListLong;
        }
        catch (final Exception e) {
            MacFileVaultNotApplicableOnNonUserApprovedMDMHandler.LOGGER.log(Level.SEVERE, "FileVaultLog: Exception in  MacFileVaultAlreadyEnabledOnDeviceNotApplicableHandler getFileVaultEnabledDevices():", e);
            return new ArrayList<Long>();
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
