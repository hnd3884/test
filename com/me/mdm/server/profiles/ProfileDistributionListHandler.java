package com.me.mdm.server.profiles;

import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import java.util.Properties;
import org.json.JSONObject;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Set;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public abstract class ProfileDistributionListHandler
{
    int platformType;
    public Logger logger;
    
    public ProfileDistributionListHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static ProfileDistributionListHandler getDistributionProfileListHandler(final int platformType) {
        ProfileDistributionListHandler distributionProfileList = null;
        switch (platformType) {
            case 1: {
                distributionProfileList = new IOSProfileDistributionListHandler();
                break;
            }
            case 2:
            case 4: {
                distributionProfileList = new AndroidProfileDistributionListHandler();
                break;
            }
            case 3: {
                distributionProfileList = new WindowsPhoneProfileDistributionListHandler();
                break;
            }
            case 0: {
                distributionProfileList = new PlatformIndependentProfileDistributionListHandler();
                break;
            }
            case 6: {
                distributionProfileList = new IOSProfileDistributionListHandler();
                break;
            }
            case 7: {
                distributionProfileList = new IOSProfileDistributionListHandler();
                break;
            }
        }
        return distributionProfileList;
    }
    
    public abstract HashMap getRemainingLicenseCountMap(final Long p0, final List p1) throws Exception;
    
    public abstract HashMap getLicensesAssociatedToGroupsMap(final List p0, final long p1, final List p2) throws Exception;
    
    public abstract HashMap getLicensesAssociatedToResourcesMap(final List p0, final long p1, final List p2) throws Exception;
    
    public boolean getIfAppHasEnoughLicensesForGroup(final Long appGroupId, final int remainingLicenses, final int totalCountInGroup, final HashMap licensesAssociatedToGroupMembers) {
        int alreadyAssignedLicenses = 0;
        if (licensesAssociatedToGroupMembers.containsKey(appGroupId)) {
            alreadyAssignedLicenses = licensesAssociatedToGroupMembers.get(appGroupId);
        }
        return remainingLicenses >= totalCountInGroup - alreadyAssignedLicenses;
    }
    
    public ArrayList getAvailableProfileGroupList(final Long customerId, final Long groupResourceId, final int profileType) {
        ArrayList availableProfileGroupList = null;
        try {
            final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnGroup(groupResourceId, customerId);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria DeleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(DeleteCri);
            final Long loginId = DMUserHandler.getLoginId();
            final Criteria userIdCriteria = RBDAUtil.getInstance().getProfileCreatedOrModifiedByCriteria(loginId);
            if (userIdCriteria != null) {
                cri = cri.and(userIdCriteria);
            }
            profileQuery.setCriteria(cri);
            if (profileType == 2) {
                availableProfileGroupList = this.setAppCollectionDetails(profileQuery, customerId, groupResourceId, null);
            }
            else {
                availableProfileGroupList = this.setProfileCollectionDetails(profileQuery, null);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in getAvailableProfileGroupList: {0}", ex);
        }
        return availableProfileGroupList;
    }
    
    public ArrayList getAvailableProfileDeviceList(final Long customerId, final Long[] deviceIds, final int profileType, final int platformType) {
        ArrayList availableProfileDeviceList = null;
        try {
            final SelectQuery profileQuery = (profileType == 2) ? ProfileUtil.getInstance().getQueryforAppCollnDevice(null, customerId) : ProfileUtil.getInstance().getQueryforProfileCollnDevice(null, customerId);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria platformTypeCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria DeleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(platformTypeCri).and(DeleteCri);
            final Long loginId = DMUserHandler.getLoginId();
            final Criteria userIdCriteria = RBDAUtil.getInstance().getProfileCreatedOrModifiedByCriteria(loginId);
            if (userIdCriteria != null) {
                cri = cri.and(userIdCriteria);
            }
            profileQuery.setCriteria(cri);
            if (profileType == 2) {
                availableProfileDeviceList = this.setAppCollectionDetails(profileQuery, customerId, null, deviceIds);
            }
            else {
                availableProfileDeviceList = this.setProfileCollectionDetails(profileQuery, null);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getAvailableProfileDeviceList: {0}", ex);
        }
        return availableProfileDeviceList;
    }
    
    public ArrayList getAddedProfileDeviceList(final Long deviceResId, final int profileType) {
        ArrayList addedProfileDeviceList = null;
        try {
            final SelectQuery profileQuery = (profileType == 2) ? ProfileUtil.getInstance().getQueryforAppCollnDevice(deviceResId, null) : ProfileUtil.getInstance().getQueryforProfileCollnDevice(deviceResId, null);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria cri = profileQuery.getCriteria().and(profileTypeCri);
            profileQuery.setCriteria(cri);
            addedProfileDeviceList = this.setProfileCollectionDetails(profileQuery, deviceResId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getAddedProfileDeviceList: {0}", ex);
        }
        return addedProfileDeviceList;
    }
    
    public ArrayList getAddedProfileGroupList(final Long groupResId, final int profileType) {
        ArrayList addedProfileGroupList = null;
        try {
            final SelectQuery profileQuery = (profileType == 2) ? ProfileUtil.getInstance().getQueryforAppCollnGroup(groupResId, null) : ProfileUtil.getInstance().getQueryforProfileCollnGroup(groupResId, null);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria markedForDelete = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            Criteria cri = profileTypeCri.and(markedForDelete);
            if (profileQuery.getCriteria() != null) {
                cri = cri.and(profileQuery.getCriteria());
            }
            profileQuery.setCriteria(cri);
            addedProfileGroupList = this.setProfileCollectionDetails(profileQuery, groupResId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getAddedProfileGroupList: {0}", ex);
        }
        return addedProfileGroupList;
    }
    
    public ArrayList getAddedProfileForManagedUser(final Long userId, final int profileType) {
        ArrayList addedProfileGroupList = null;
        try {
            final SelectQuery profileQuery = (profileType == 2) ? ProfileUtil.getInstance().getQueryForAppCollnUser(userId, null) : ProfileUtil.getInstance().getQueryforProfileCollnManagedUser(userId, null, profileType);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria markedForDelete = new Criteria(Column.getColumn("RecentProfileForMDMResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(markedForDelete);
            profileQuery.setCriteria(cri);
            addedProfileGroupList = this.setProfileCollectionDetails(profileQuery, userId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getAddedProfileGroupList: {0}", ex);
        }
        return addedProfileGroupList;
    }
    
    public int getEnrolledDeviceCount(final List resourceIds) {
        final int totalDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountForResources(resourceIds, null);
        return totalDevicesInGroups;
    }
    
    public int getEnrolledUserCount(final List resourceIds) {
        final int totalUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountForResources(resourceIds, null);
        return totalUsersInGroups;
    }
    
    protected int getEnrolledDeviceCountInGroups(final List groupResourceIds) {
        final int totalDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountInGroups(groupResourceIds);
        return totalDevicesInGroups;
    }
    
    protected int getEnrolledUserCountInGroups(final List groupResourceIds) {
        final int totalUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountInGroups(groupResourceIds);
        return totalUsersInGroups;
    }
    
    public ArrayList setAppCollectionDetails(final SelectQuery profileQuery, final long customerId, final Long groupResourceId, final Long[] resId) {
        ArrayList profileList = null;
        DMDataSetWrapper ds = null;
        int totalDevicesInGroups = 0;
        int totalUsersInGroups = 0;
        List resourceList = null;
        this.getAppPackageDataQuery(profileQuery);
        HashMap licensesAssociatedToResources = new HashMap();
        try {
            if (groupResourceId != null) {
                final List groupResourceIdsList = new ArrayList(Arrays.asList(groupResourceId));
                totalUsersInGroups = this.getEnrolledUserCountInGroups(groupResourceIdsList);
                totalDevicesInGroups = this.getEnrolledDeviceCountInGroups(groupResourceIdsList);
                licensesAssociatedToResources = this.getLicensesAssociatedToGroupsMap(groupResourceIdsList, customerId, null);
            }
            if (resId != null) {
                resourceList = new ArrayList(Arrays.asList(resId));
                totalDevicesInGroups = this.getEnrolledDeviceCount(resourceList);
                totalUsersInGroups = this.getEnrolledUserCount(resourceList);
                licensesAssociatedToResources = this.getLicensesAssociatedToResourcesMap(resourceList, customerId, null);
            }
            final HashMap remainingLicenseCountMap = this.getRemainingLicenseCountMap(customerId, null);
            ds = DMDataSetWrapper.executeQuery((Object)profileQuery);
            profileList = new ArrayList();
            while (ds.next()) {
                final Long profileId = (Long)ds.getValue("PROFILE_ID");
                final String profileName = (String)ds.getValue("PROFILE_NAME");
                final int profileType = (int)ds.getValue("PROFILE_TYPE");
                final int platfromType = (int)ds.getValue("PLATFORM_TYPE");
                final Long profileCollnId = (Long)ds.getValue("COLLECTION_ID");
                final int supDev = (int)ds.getValue("SUPPORTED_DEVICES");
                final Long appGroupId = (Long)ds.getValue("APP_GROUP_ID");
                final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                final Boolean paidApp = (Boolean)ds.getValue("IS_PAID_APP");
                final Boolean purchasedApp = (Boolean)ds.getValue("IS_PURCHASED_FROM_PORTAL");
                Integer appAssignmentType = (Integer)ds.getValue("APP_ASSIGNABLE_TYPE");
                final Long associatedByUser = (Long)ds.getValue("associatedByUser");
                final String associatedByUserName = (String)ds.getValue("associatedByUserName");
                if (appAssignmentType == null) {
                    appAssignmentType = 1;
                }
                final HashMap profileMap = new HashMap();
                profileMap.put("profileId", profileId);
                profileMap.put("profileType", profileType);
                profileMap.put("isBusinessStoreApp", purchasedApp);
                profileMap.put("profileName", profileName);
                profileMap.put("paidApp", paidApp);
                String supDevice = "";
                if (supDev == 1) {
                    supDevice = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone_tablet", new Object[0]);
                }
                else if (supDev == 2) {
                    supDevice = I18N.getMsg("dc.mdm.actionlog.appmgmt.smartPhone", new Object[0]);
                }
                else if (supDev == 3) {
                    supDevice = I18N.getMsg("dc.mdm.graphs.tablet", new Object[0]);
                }
                profileMap.put("supportedDevice", supDevice);
                profileMap.put("profileCollnId", profileCollnId);
                boolean hasUnusedLicenses = true;
                int remainingLicense = 0;
                if ((platfromType != 2 && purchasedApp) || (platfromType == 2 && purchasedApp && paidApp)) {
                    if (remainingLicenseCountMap.containsKey(appGroupId)) {
                        remainingLicense = remainingLicenseCountMap.get(appGroupId);
                    }
                    int totalCountInGroup = 0;
                    if (appAssignmentType == 2) {
                        totalCountInGroup = totalDevicesInGroups;
                    }
                    else {
                        totalCountInGroup = totalUsersInGroups;
                    }
                    hasUnusedLicenses = this.getIfAppHasEnoughLicensesForGroup(appGroupId, remainingLicense, totalCountInGroup, licensesAssociatedToResources);
                }
                if (associatedByUser != null) {
                    profileMap.put("associatedByUser", associatedByUser);
                    profileMap.put("associatedByUserName", associatedByUserName);
                }
                profileMap.put("isValidApp", hasUnusedLicenses);
                profileMap.put("remainingLicenseCount", remainingLicense);
                profileList.add(profileMap);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in setAppCollectionDetails: {0}", ex);
        }
        return profileList;
    }
    
    private ArrayList setProfileCollectionDetails(final SelectQuery profileQuery, final Long resId) {
        ArrayList profileList = null;
        HashMap profileMap = null;
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)profileQuery);
            profileList = new ArrayList();
            while (ds.next()) {
                final Long profileId = (Long)ds.getValue("PROFILE_ID");
                final String profileName = (String)ds.getValue("PROFILE_NAME");
                final int profileType = (int)ds.getValue("PROFILE_TYPE");
                final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                final Long profileCollnId = (Long)ds.getValue("COLLECTION_ID");
                final Long associatedByUser = (Long)ds.getValue("associatedByUser");
                final String associatedByUserName = (String)ds.getValue("associatedByUserName");
                profileMap = new HashMap();
                if (resId != null) {
                    final int executionVer = (int)ds.getValue("ProfileColln.PROFILE_VERSION");
                    profileMap.put("executionVer", executionVer);
                    if (executionVer < latestVer) {
                        profileMap.put("isLatestVer", false);
                    }
                    else {
                        profileMap.put("isLatestVer", true);
                    }
                    profileMap.put("profiledistributedColln", ds.getValue("ProfileColln.COLLECTION_ID"));
                }
                profileMap.put("profileId", profileId);
                profileMap.put("profileType", profileType);
                profileMap.put("profileName", MDMUtil.getInstance().decodeURIComponentEquivalent(profileName));
                profileMap.put("latestVer", latestVer);
                profileMap.put("profileCollnId", profileCollnId);
                if (associatedByUser != null) {
                    profileMap.put("associatedByUser", associatedByUser);
                    profileMap.put("associatedByUserName", associatedByUserName);
                }
                profileList.add(profileMap);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured in setProfileCollnDetails: {0}", ex);
        }
        return profileList;
    }
    
    public void getAppPackageDataQuery(final SelectQuery profileQuery) {
        profileQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        profileQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        profileQuery.addJoin(new Join("MdPackageToAppData", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        profileQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        profileQuery.addJoin(new Join("MdPackageToAppData", "MDAppAssignableDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        profileQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        profileQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        profileQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"));
        profileQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        profileQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        profileQuery.addSelectColumn(Column.getColumn("MDAppAssignableDetails", "APP_ASSIGNABLE_TYPE"));
    }
    
    public HashMap getProfileToCollnMapForGroupListener(final List addedProfileGroupList) {
        final HashMap profileToColnMap = new HashMap();
        for (int i = 0; i < addedProfileGroupList.size(); ++i) {
            final HashMap profileMap = addedProfileGroupList.get(i);
            final Long collnId = profileMap.get("profiledistributedColln");
            final Long profileId = profileMap.get("profileId");
            profileToColnMap.put(profileId, collnId);
        }
        return profileToColnMap;
    }
    
    public HashMap getProfileCollnMapForGroupListener(final List addedProfileGroupList) {
        final HashMap prfileCollMap = new HashMap();
        for (int i = 0; i < addedProfileGroupList.size(); ++i) {
            final HashMap profileMap = addedProfileGroupList.get(i);
            final Long collnId = profileMap.get("profiledistributedColln");
            final Long profileId = profileMap.get("profileId");
            prfileCollMap.put(profileId, collnId);
        }
        return prfileCollMap;
    }
    
    public HashMap getProfileToCollnMapForManagedUserListener(final List addedProfileManagedUserList) {
        final HashMap profileToColnMap = new HashMap();
        for (int i = 0; i < addedProfileManagedUserList.size(); ++i) {
            final HashMap profileMap = addedProfileManagedUserList.get(i);
            final Long collnId = profileMap.get("profileCollnId");
            final Long profileId = profileMap.get("profileId");
            profileToColnMap.put(profileId, collnId);
        }
        return profileToColnMap;
    }
    
    public HashMap getProfileCollnMapForManagedUserListener(final List addedProfileManagedUserList) {
        final HashMap prfileCollMap = new HashMap();
        for (int i = 0; i < addedProfileManagedUserList.size(); ++i) {
            final HashMap profileMap = addedProfileManagedUserList.get(i);
            final Long collnId = profileMap.get("profiledistributedColln");
            final Long profileId = profileMap.get("profileId");
            prfileCollMap.put(profileId, collnId);
        }
        return prfileCollMap;
    }
    
    public HashMap<Long, List> getDeviceExcludeProfileMap(final List resourceList, final HashMap profileCollnMap) {
        final HashMap<Long, List> excludeProfileForDevice = new HashMap<Long, List>();
        try {
            final Set profileIdList = profileCollnMap.keySet();
            final Collection collectionIdList = profileCollnMap.values();
            final Criteria cResource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria cProfileId = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileIdList.toArray(), 8);
            final Criteria cCollectionId = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionIdList.toArray(), 9);
            final Criteria cMarkedForDelete = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final DataObject DO = MDMUtil.getPersistence().get("RecentProfileForResource", cResource.and(cProfileId).and(cCollectionId).and(cMarkedForDelete));
            if (!DO.isEmpty()) {
                for (final Long profileId : profileIdList) {
                    final Criteria cProfile = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
                    final List excludeDeviceList = new ArrayList();
                    final Iterator excludeRows = DO.getRows("RecentProfileForResource", cProfile);
                    while (excludeRows.hasNext()) {
                        final Row resRow = excludeRows.next();
                        excludeDeviceList.add(resRow.get("RESOURCE_ID"));
                    }
                    if (!excludeDeviceList.isEmpty()) {
                        excludeProfileForDevice.put(profileId, excludeDeviceList);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceDeletableProfileMap", e);
        }
        return excludeProfileForDevice;
    }
    
    public HashMap getGroupDeviceExcludeProfileMap(final List resourceList, final HashMap profileCollnMap) {
        return this.getGroupDeviceExcludeProfileMap(resourceList, profileCollnMap, null);
    }
    
    public HashMap getGroupDeviceExcludeProfileMap(final List resourceList, final HashMap profileCollnMap, final List excludeGroupId) {
        final HashMap<Long, List> excludeProfileForDevice = new HashMap<Long, List>();
        DMDataSetWrapper ds = null;
        try {
            final Set profileIdList = profileCollnMap.keySet();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            sQuery.addJoin(new Join("CustomGroupMemberRel", "RecentProfileForGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_ID" }, 2));
            final Criteria cResource = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria cProfileId = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileIdList.toArray(), 8);
            final Criteria cMarkedForDelete = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            sQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            Criteria criteria = cResource.and(cProfileId).and(cMarkedForDelete);
            if (excludeGroupId != null) {
                final Criteria cGroup = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)excludeGroupId.toArray(), 9);
                criteria = criteria.and(cGroup);
            }
            sQuery.setCriteria(criteria);
            final List groupList = new ArrayList();
            groupList.add(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            groupList.add(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            final GroupByClause grpByCls = new GroupByClause(groupList);
            sQuery.setGroupByClause(grpByCls);
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Long resourceId = (Long)ds.getValue("MEMBER_RESOURCE_ID");
                final Long profileId = (Long)ds.getValue("PROFILE_ID");
                List excludeList = excludeProfileForDevice.get(profileId);
                if (excludeList == null) {
                    excludeList = new ArrayList();
                }
                excludeList.add(resourceId);
                excludeProfileForDevice.put(profileId, excludeList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceDeletableProfileMap", e);
        }
        return excludeProfileForDevice;
    }
    
    public HashMap getUserGroupDeviceExcludeProfileMap(final List resourceList, final HashMap profileCollnMap, final List excludeGroupId) {
        final HashMap<Long, List> excludeProfileForDevice = new HashMap<Long, List>();
        DMDataSetWrapper ds = null;
        try {
            final Set profileIdList = profileCollnMap.keySet();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            sQuery.addJoin(new Join("CustomGroupMemberRel", "RecentProfileForGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_ID" }, 2));
            sQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedUserToDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            final Criteria cResource = new Criteria(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria cProfileId = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileIdList.toArray(), 8);
            final Criteria cMarkedForDelete = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            sQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            Criteria criteria = cResource.and(cProfileId).and(cMarkedForDelete);
            if (excludeGroupId != null) {
                final Criteria cGroup = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)excludeGroupId.toArray(), 9);
                criteria = criteria.and(cGroup);
            }
            sQuery.setCriteria(criteria);
            final List groupList = new ArrayList();
            groupList.add(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            groupList.add(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            final GroupByClause grpByCls = new GroupByClause(groupList);
            sQuery.setGroupByClause(grpByCls);
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Long resourceId = (Long)ds.getValue("MANAGED_DEVICE_ID");
                final Long profileId = (Long)ds.getValue("PROFILE_ID");
                List excludeList = excludeProfileForDevice.get(profileId);
                if (excludeList == null) {
                    excludeList = new ArrayList();
                }
                excludeList.add(resourceId);
                excludeProfileForDevice.put(profileId, excludeList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceDeletableProfileMap", e);
        }
        return excludeProfileForDevice;
    }
    
    public void getCollectionSpecificUserProperties(final JSONObject collectionProperties, final Properties userProperties, final TreeNode childNode) throws Exception {
    }
    
    public List getAddedProfileIdGroupList(final Long groupResId, final int profileType, final Long customerId) {
        List addedProfileIdGroupList = null;
        try {
            final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnGroup(groupResId, customerId);
            final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
            final Criteria markedForDelete = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(markedForDelete);
            profileQuery.setCriteria(cri);
            final DataObject dataObject = MDMUtil.getPersistence().get(profileQuery);
            if (dataObject != null && dataObject.containsTable("Profile")) {
                final Iterator<Row> profileRows = dataObject.getRows("Profile");
                addedProfileIdGroupList = DBUtil.getColumnValuesAsList((Iterator)profileRows, "PROFILE_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getAddedProfileGroupList: {0}", ex);
        }
        return addedProfileIdGroupList;
    }
    
    public HashMap<Long, List<Long>> getNonAssociatedResources(final List profileList, final List resourceList) {
        final HashMap<Long, List<Long>> nonAssociatedProfileResource = new HashMap<Long, List<Long>>();
        final Iterator<Long> profileIter = profileList.iterator();
        while (profileIter.hasNext()) {
            nonAssociatedProfileResource.put(profileIter.next(), new ArrayList<Long>(resourceList));
        }
        try {
            final SelectQuery recentProfileForResourceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            recentProfileForResourceQuery.addSelectColumn(new Column("RecentProfileForResource", "*"));
            recentProfileForResourceQuery.setCriteria(profileCriteria.and(resourceCriteria));
            final DataObject recentProfileResourceDO = DataAccess.get(recentProfileForResourceQuery);
            if (!recentProfileResourceDO.isEmpty()) {
                for (final Long profileId : profileList) {
                    final Criteria profCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
                    final DataObject profileResourceDO = recentProfileResourceDO.getDataObject("RecentProfileForResource", profCriteria);
                    if (!profileResourceDO.isEmpty()) {
                        final Iterator<Row> resourceIter = profileResourceDO.getRows("RecentProfileForResource");
                        while (resourceIter.hasNext()) {
                            final Row resourceProfileRow = resourceIter.next();
                            final Long resourceId = (Long)resourceProfileRow.get("RESOURCE_ID");
                            nonAssociatedProfileResource.get(profileId).remove(resourceId);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getNonAssociatedResources", e);
        }
        return nonAssociatedProfileResource;
    }
}
