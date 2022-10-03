package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.Collection;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.config.ConfigUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.profiles.RestrictionProfileHandler;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class IOSCheckAndAddWifiRestriction implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    HashMap<String, String> wifiRestrictionMap;
    
    public IOSCheckAndAddWifiRestriction() {
        this.wifiRestrictionMap = new HashMap<String, String>() {
            {
                this.put("FORCE_WIFI_WHITELISTING", "true");
            }
        };
    }
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final Long collnId = configNotApplicable.collectionId;
        final List resourceList = configNotApplicable.resourceList;
        final RestrictionProfileHandler restrictionProfileHandler = new RestrictionProfileHandler();
        if (!this.checkWifiConfiguredInCollection(collnId) && restrictionProfileHandler.isRestrictionConfigured(collnId, this.wifiRestrictionMap, "RestrictionsPolicy")) {
            final List supervisedList = ManagedDeviceHandler.getInstance().getSupervisedIosDevicelist(resourceList);
            return this.getResourceNotConfiguredWithWifi(supervisedList);
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            final String remark = "mdm.profile.ios.wifiRestrictionNotApplicable@@@<l>$(mdmUrl)/how-to/mdm-auto-join-secure-wifi-network.html?$(traceurl)&$(did)&pgSrc=wifiRestrictionNotAllowed#note";
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 16, remark);
        }
        catch (final DataAccessException e) {
            IOSCheckAndAddWifiRestriction.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    private boolean checkWifiConfiguredInCollection(final Long collectionId) {
        try {
            final List configIds = ConfigUtil.getConfigIds(collectionId);
            return configIds.size() > 0 && configIds.contains(177);
        }
        catch (final SyMException e) {
            IOSCheckAndAddWifiRestriction.LOGGER.log(Level.SEVERE, "Exception while getting config", (Throwable)e);
            return false;
        }
    }
    
    private List getResourceNotConfiguredWithWifi(final List resourceList) {
        final List notApplicableList = new ArrayList(resourceList);
        try {
            final List applicableList = new ProfileAssociateDataHandler().getConfigAppliedForResources(resourceList, 177);
            notApplicableList.removeAll(applicableList);
            IOSCheckAndAddWifiRestriction.LOGGER.log(Level.INFO, "Resource not configured with Wifi so this will not be applied.{0}", new Object[] { resourceList });
        }
        catch (final DataAccessException e) {
            IOSCheckAndAddWifiRestriction.LOGGER.log(Level.SEVERE, "Exception in checking wifiRestriction", (Throwable)e);
        }
        IOSCheckAndAddWifiRestriction.LOGGER.log(Level.INFO, "Not Applicable Resources for Wifi Configuration.ResourceList:{0}", notApplicableList);
        return notApplicableList;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
