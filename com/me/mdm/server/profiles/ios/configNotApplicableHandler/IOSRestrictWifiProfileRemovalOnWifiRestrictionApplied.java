package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import java.util.ArrayList;
import java.util.Collection;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class IOSRestrictWifiProfileRemovalOnWifiRestrictionApplied implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        return this.getResourceNotApplicableForWifiRemoval(configNotApplicable.resourceList, configNotApplicable.collectionId);
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            final String remark = "mdm.profile.ios.wifiProfileRemovalNA@@@<l>$(mdmUrl)/how-to/mdm-auto-join-secure-wifi-network.html?$(traceurl)&$(did)&pgSrc=wifiRemovalNotAllowed#note";
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 16, remark);
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileRemovalOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    private List getResourceNotApplicableForWifiRemoval(final List resourceList, final Long collectionId) {
        try {
            final Criteria wifiCriteria = new Criteria(new Column("RestrictionsPolicy", "FORCE_WIFI_WHITELISTING"), (Object)true, 0);
            final Criteria notFailureCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)7, 1);
            final Criteria notCollectionCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 1);
            final List supervisedList = ManagedDeviceHandler.getInstance().getSupervisedIosDevicelist(resourceList);
            final List restrictionAppliedList = new ProfileAssociateDataHandler().getRestrictionAssociatedOnResource(supervisedList, "RestrictionsPolicy", wifiCriteria.and(notFailureCriteria).and(notCollectionCriteria));
            final List appliedResourceList = new ProfileAssociateDataHandler().getConfigAppliedForResources(restrictionAppliedList, 177);
            appliedResourceList.addAll(new ProfileAssociateDataHandler().getConfigAppliedForResources(restrictionAppliedList, 774));
            restrictionAppliedList.removeAll(appliedResourceList);
            IOSRestrictWifiProfileRemovalOnWifiRestrictionApplied.LOGGER.log(Level.INFO, "Resources not applicable for wifi removal. ResourceList:{0}", new Object[] { restrictionAppliedList });
            return restrictionAppliedList;
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileRemovalOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in getting not applicable fpr wifi removal", (Throwable)e);
            return new ArrayList();
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
