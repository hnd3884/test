package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import org.json.JSONObject;
import java.util.ArrayList;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableListener;

public class IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied implements MDMCollectionNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final List resourceList, final Long collnId, final List configId, final long customerId) {
        try {
            if (!configId.contains(177) && !configId.contains(774)) {
                final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
                final Criteria restrictionCriteria = new Criteria(new Column("RestrictionsPolicy", "FORCE_WIFI_WHITELISTING"), (Object)true, 0);
                final List supervisedList = ManagedDeviceHandler.getInstance().getSupervisedIosDevicelist(resourceList);
                final JSONObject restrictionObject = profileAssociateDataHandler.getRestrictionAppliedOnResource(supervisedList, "RestrictionsPolicy", restrictionCriteria);
                final List restrictionAppliedList = (List)restrictionObject.get("RESOURCE_ID");
                if (!restrictionAppliedList.isEmpty()) {
                    IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.INFO, "Wifi Restriction applied resources.{0}", new Object[] { restrictionAppliedList });
                    final List wifiApplicableList = profileAssociateDataHandler.getConfigAppliedForResources(restrictionAppliedList, 177);
                    wifiApplicableList.addAll(profileAssociateDataHandler.getConfigAppliedForResources(restrictionAppliedList, 774));
                    restrictionAppliedList.removeAll(wifiApplicableList);
                    return this.getPrevVerOfWifiAssociatedResource(restrictionAppliedList, collnId);
                }
            }
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in getting restriction applied.", (Throwable)e);
        }
        catch (final JSONException e2) {
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in getting restriction applied.", (Throwable)e2);
        }
        catch (final Exception e3) {
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in getting restriction applied.", e3);
        }
        return new ArrayList<Long>();
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            final String remark = "mdm.profile.ios.wifiRestrictionNotApplicable@@@<l>$(mdmUrl)/how-to/mdm-auto-join-secure-wifi-network.html?$(traceurl)&$(did)&pgSrc=wifiUpdateNotAllowed#note";
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 16, remark);
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    public List<Long> getPrevVerOfWifiAssociatedResource(final List<Long> resourceList, final Long collectionId) {
        final List<Long> associatedList = new ArrayList<Long>();
        try {
            final List<Long> collectionIdList = new ArrayList<Long>();
            collectionIdList.add(collectionId);
            final List profileId = new ProfileHandler().getProfileIDsFromCollectionIDs(collectionIdList);
            final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
            final JSONObject previousVersionObject = profileAssociateDataHandler.getPreVerOfProfileAssociatedForResource(resourceList, collectionIdList, profileId);
            if (previousVersionObject.length() > 0) {
                final SelectQuery selectQuery = new ProfileAssociateDataHandler().getPrevVerOfProfileConfigAssociatedForResourceQuery();
                final Criteria resourceCriteria = this.getCriteriaForWifiFromPrevObject(previousVersionObject);
                selectQuery.setCriteria(resourceCriteria);
                selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "*"));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("ResourceToProfileHistory");
                    while (iterator.hasNext()) {
                        final Row resourceRow = iterator.next();
                        associatedList.add((Long)resourceRow.get("RESOURCE_ID"));
                    }
                }
            }
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.INFO, "Resource that have previous version of wifi. So restricted resource:{0}", new Object[] { resourceList });
        }
        catch (final DataAccessException e) {
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception while performing DB operation", (Throwable)e);
        }
        return associatedList;
    }
    
    public Criteria getCriteriaForWifiFromPrevObject(final JSONObject resourceObject) {
        Criteria finalCriteria = null;
        try {
            if (resourceObject != null) {
                final Iterator iterator = resourceObject.keys();
                while (iterator.hasNext()) {
                    final String key = iterator.next();
                    final JSONObject resourceCollectionObject = resourceObject.getJSONObject(key);
                    final Iterator collectionIterator = resourceCollectionObject.keys();
                    final List<Long> collectionList = new ArrayList<Long>();
                    while (collectionIterator.hasNext()) {
                        final String profileKey = collectionIterator.next();
                        collectionList.add((Long)resourceCollectionObject.get(profileKey));
                    }
                    final Criteria resourceCriteria = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)Long.parseLong(key), 0);
                    final Criteria collectionCriteria = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
                    final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 177, 774 }, 8);
                    if (finalCriteria == null) {
                        finalCriteria = resourceCriteria.and(collectionCriteria).and(configCriteria);
                    }
                    else {
                        finalCriteria = finalCriteria.or(resourceCriteria.and(collectionCriteria).and(configCriteria));
                    }
                }
            }
        }
        catch (final JSONException e) {
            IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied.LOGGER.log(Level.SEVERE, "Exception while parsing the prevObject", (Throwable)e);
        }
        return finalCriteria;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
