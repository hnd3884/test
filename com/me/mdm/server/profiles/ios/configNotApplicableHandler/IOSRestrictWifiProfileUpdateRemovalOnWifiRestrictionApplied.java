package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableListener;

public class IOSRestrictWifiProfileUpdateRemovalOnWifiRestrictionApplied implements MDMCollectionNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final List resourceList, final Long collnId, final List configId, final long customerId) {
        try {
            if (!configId.contains(177) && !configId.contains(774)) {
                final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
                final Criteria restrictionCriteria = new Criteria(new Column("RestrictionsPolicy", "FORCE_WIFI_WHITELISTING"), (Object)true, 0);
                final Criteria notFailureCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)7, 1);
                final List supervisedList = ManagedDeviceHandler.getInstance().getSupervisedIosDevicelist(resourceList);
                final List restrictionAppliedList = profileAssociateDataHandler.getRestrictionAssociatedOnResource(supervisedList, "RestrictionsPolicy", restrictionCriteria.and(notFailureCriteria));
                if (!restrictionAppliedList.isEmpty()) {
                    final List wifiApplicableList = profileAssociateDataHandler.getConfigAppliedForResources(restrictionAppliedList, 177);
                    wifiApplicableList.addAll(profileAssociateDataHandler.getConfigAppliedForResources(restrictionAppliedList, 774));
                    restrictionAppliedList.removeAll(wifiApplicableList);
                    return new IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied().getPrevVerOfWifiAssociatedResource(restrictionAppliedList, collnId);
                }
            }
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileUpdateRemovalOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception while performing DB operation in NA handler", (Throwable)e);
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            final String remark = "mdm.profile.ios.wifiProfileRemovalNA@@@<l>$(mdmUrl)/how-to/mdm-auto-join-secure-wifi-network.html?$(traceurl)&$(did)&pgSrc=wifiUpdateRemovalNotAllowed#note";
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 16, remark);
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileUpdateRemovalOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
