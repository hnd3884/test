package com.me.mdm.server.profiles;

import java.util.Hashtable;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.persistence.DataObject;
import java.util.List;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import java.util.HashSet;

public abstract class ProfileDistributionHandler
{
    public void associateProfile(final HashSet<Long> profileSet, final Integer version, final HashSet<Long> resourceSet) throws SyMException {
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, String>)properties).put("commandName", "InstallProfile");
            ((Hashtable<String, Long>)properties).put("customerId", CustomerInfoUtil.getInstance().getCustomerId());
            ((Hashtable<String, String>)properties).put("loggedOnUserName", ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName());
            ((Hashtable<String, ArrayList>)properties).put("resourceList", new ArrayList(resourceSet));
            try {
                final HashMap profileCollectionMap = new HashMap();
                if (version != null) {
                    final Long profileId = profileSet.iterator().next();
                    final DataObject DO = MDMUtil.getPersistence().get("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"), (Object)(long)version, 0).and(new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0)));
                    if (DO.isEmpty()) {
                        throw new SyMException(404, "invalid version", (Throwable)null);
                    }
                    final Long collectionID = (Long)DO.getValue("ProfileToCollection", "COLLECTION_ID", (Criteria)null);
                    final int collectionStatus = (int)DBUtil.getValueFromDB("CollectionStatus", "COLLECTION_ID", (Object)collectionID, "PROFILE_COLLECTION_STATUS");
                    if (collectionStatus != 110) {
                        throw new SyMException(409, "Profile is not published.", (Throwable)null);
                    }
                    profileCollectionMap.put(profileId, collectionID);
                }
                ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            }
            catch (final JSONException ex) {
                throw new SyMException(400, (Throwable)null);
            }
            if (resourceSet.isEmpty()) {
                throw new SyMException(400, "No valid resoource Ids provided", (Throwable)null);
            }
            this.validateResourceList(new ArrayList<Long>(resourceSet), -1);
            this.associateCollectionForResource(properties);
        }
        catch (final SyMException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new SyMException(500, (Throwable)null);
        }
    }
    
    public void disassociateProfile(final HashSet<Long> profileSet, final Integer version, final HashSet<Long> resourceSet) throws SyMException {
        final Properties properties = new Properties();
        final List resourceList = new ArrayList(resourceSet);
        ((Hashtable<String, ArrayList>)properties).put("resourceList", new ArrayList(resourceSet));
        ((Hashtable<String, Long>)properties).put("customerId", CustomerInfoUtil.getInstance().getCustomerId());
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
        ((Hashtable<String, String>)properties).put("commandName", "RemoveProfile");
        final Set<Long> associatedResourceList = new HashSet<Long>();
        final Map<Long, Long> profileCollnMap = new HashMap<Long, Long>();
        try {
            final List<Properties> profileCollectionList = new ArrayList<Properties>();
            for (final Long profileID : profileSet) {
                final Map resourceMap = this.getManagedResourcesAssignedForProfile(profileID);
                resourceMap.keySet().retainAll(resourceList);
                associatedResourceList.addAll(resourceMap.keySet());
                for (final Long collectionID : resourceMap.values()) {
                    profileCollnMap.put(profileID, collectionID);
                    final Properties profileColProp = new Properties();
                    ((Hashtable<String, Long>)profileColProp).put("PROFILE_ID", profileID);
                    ((Hashtable<String, Long>)profileColProp).put("COLLECTION_ID", collectionID);
                    profileCollectionList.add(profileColProp);
                }
            }
            final Iterator<Long> iterator = profileSet.iterator();
            if (iterator.hasNext()) {
                ((Hashtable<String, Map<Long, Long>>)properties).put("profileCollnMap", profileCollnMap);
                ((Hashtable<String, List<Properties>>)properties).put("profileCollectionList", profileCollectionList);
                ((Hashtable<String, Integer>)properties).put("platformtype", new ProfileUtil().getPlatformType(profileSet.iterator().next()));
            }
            final List invalidResources = new ArrayList(resourceList);
            invalidResources.removeAll(associatedResourceList);
            if (!invalidResources.isEmpty()) {
                throw new SyMException(409, "Some of the reources provided are not associated to the profile", (Throwable)null);
            }
            if (associatedResourceList.isEmpty()) {
                throw new SyMException(400, "No valid reources IDs provided", (Throwable)null);
            }
            ((Hashtable<String, ArrayList>)properties).put("resourceList", new ArrayList(associatedResourceList));
            this.disassociateCollectionForResource(properties);
        }
        catch (final SyMException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new SyMException(500, (Throwable)null);
        }
    }
    
    protected abstract void validateResourceList(final List<Long> p0, final int p1) throws Exception;
    
    protected abstract void associateCollectionForResource(final Properties p0);
    
    protected abstract void disassociateCollectionForResource(final Properties p0);
    
    protected abstract Map getManagedResourcesAssignedForProfile(final Long p0) throws DataAccessException, QueryConstructionException;
}
