package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import java.util.AbstractMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.logging.Logger;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMAppListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        final String selectedString = "selected";
        final String associatedString = "associated";
        final String availableString = "all";
        final List treeNodeList = new ArrayList();
        try {
            final String searchValue = requestMap.get("search");
            final String filterButtonVal = requestMap.get("filterButtonVal");
            final String filterTreeParams = requestMap.get("filterTreeParams");
            final String selectAllValue = requestMap.get("selectAllValue");
            final String profileJSONStr = requestMap.get("profileAppJSON");
            final String startIndexStr = requestMap.get("start");
            final String noOfObjStr = requestMap.get("noOfObj");
            final String groupIdsStr = requestMap.get("groupId");
            final String deviceIdsStr = requestMap.get("deviceId");
            Long[] groupIds = null;
            Long[] deviceIds = null;
            final Long customerId = requestMap.get("cid");
            Boolean containsDeviceResource = false;
            int platformType = 0;
            int startIndex = 0;
            int noOfObj = 50;
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            SelectQuery appQuery = null;
            int totaliosDevicesInGroups = 0;
            int totalUsersInGroups = 0;
            HashMap licensesAssociatedToResources = new HashMap();
            Boolean showPlatformIcon = true;
            ProfileDistributionListHandler profileDisInstance = null;
            if (groupIdsStr != null && !groupIdsStr.equals("") && !groupIdsStr.equals("[]") && !groupIdsStr.equals("['']")) {
                groupIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(groupIdsStr);
                final List groupResourceIdsList = new ArrayList(Arrays.asList(groupIds));
                totalUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountInGroups(groupResourceIdsList);
                totaliosDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountInGroups(groupResourceIdsList, null, 1);
                profileDisInstance = ProfileDistributionListHandler.getDistributionProfileListHandler(platformType);
                licensesAssociatedToResources = profileDisInstance.getLicensesAssociatedToGroupsMap(groupResourceIdsList, customerId, null);
                appQuery = this.getGroupAppQuery(customerId, groupIds, filterButtonVal, filterTreeParams, selectedString, associatedString, availableString, searchValue, startIndex, noOfObj, selectAllValue);
                final List grpList = CustomGroupingHandler.getCustomGroupsList(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupIds, 8).and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new Integer[] { 4, 3, 5, 6 }, 8)));
                if (!grpList.isEmpty()) {
                    containsDeviceResource = true;
                }
            }
            else {
                showPlatformIcon = false;
                deviceIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(deviceIdsStr);
                platformType = ManagedDeviceHandler.getInstance().getPlatformType(deviceIds[0]);
                profileDisInstance = ProfileDistributionListHandler.getDistributionProfileListHandler(platformType);
                final List resourceList = new ArrayList(Arrays.asList(deviceIds));
                totaliosDevicesInGroups = ManagedDeviceHandler.getInstance().getManagedDeviceCountForResources(resourceList, null);
                totalUsersInGroups = ManagedDeviceHandler.getInstance().getManagedUserCountForResources(resourceList, null);
                licensesAssociatedToResources = profileDisInstance.getLicensesAssociatedToResourcesMap(resourceList, customerId, null);
                appQuery = this.getDeviceAppQuery(customerId, deviceIds, platformType, filterButtonVal, filterTreeParams, selectedString, associatedString, availableString, searchValue, startIndex, noOfObj, selectAllValue);
                final List deviceList = ManagedDeviceHandler.getInstance().getDeviceResourceIDs(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceIds, 8));
                if (!deviceList.isEmpty()) {
                    containsDeviceResource = true;
                }
            }
            final JSONObject profileJSON = (JSONObject)new JSONParser().parse(profileJSONStr);
            final HashMap remainingLicenseCountMap = profileDisInstance.getRemainingLicenseCountMap(customerId, null);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)appQuery);
                final List displayedAppGroupIds = new ArrayList();
                while (ds.next()) {
                    platformType = (int)ds.getValue("PLATFORM_TYPE");
                    final Long profileId = (Long)ds.getValue("PROFILE_ID");
                    String profileName = (String)ds.getValue("PROFILE_NAME");
                    String appImageUrl = (String)ds.getValue("DISPLAY_IMAGE_LOC");
                    final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                    final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
                    String appCategory = (String)ds.getValue("APP_CATEGORY_LABEL");
                    final int packageType = (int)ds.getValue("PACKAGE_TYPE");
                    final Long appGroupId = (Long)ds.getValue("APP_GROUP_ID");
                    final Boolean paidApp = (Boolean)ds.getValue("IS_PAID_APP");
                    final Boolean purchasedApp = (Boolean)ds.getValue("IS_PURCHASED_FROM_PORTAL");
                    Integer appAssignmentType = (Integer)ds.getValue("LICENSE_TYPE");
                    String backgroundColor = (String)ds.getValue("IMG_BG");
                    final String releaseLabelDisplayName = (String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME");
                    final Long releaseLabelId = (Long)ds.getValue("RELEASE_LABEL_ID");
                    if (displayedAppGroupIds.contains(appGroupId)) {
                        continue;
                    }
                    displayedAppGroupIds.add(appGroupId);
                    if ((backgroundColor == null || backgroundColor.equals("transparent")) && platformType == 3) {
                        backgroundColor = "#0078d7";
                    }
                    if (appAssignmentType == null) {
                        appAssignmentType = 1;
                    }
                    boolean hasUnusedLicenses = true;
                    int totalCountInGroup = 0;
                    int remainingLicense = 0;
                    int totalLicense = 0;
                    int usedLicense = 0;
                    if (platformType != 3 && ((platformType != 2 && purchasedApp) || (platformType == 2 && purchasedApp && paidApp))) {
                        if (remainingLicenseCountMap.containsKey(appGroupId)) {
                            final org.json.JSONObject licenseSummaryJSON = remainingLicenseCountMap.get(appGroupId);
                            remainingLicense = licenseSummaryJSON.optInt("AVAILABLE_LICENSE_COUNT");
                            totalLicense = licenseSummaryJSON.optInt("TOTAL_LICENSE");
                            usedLicense = licenseSummaryJSON.optInt("ASSIGNED_LICENSE_COUNT");
                        }
                        if (appAssignmentType == 2) {
                            totalCountInGroup = totaliosDevicesInGroups;
                        }
                        else {
                            totalCountInGroup = totalUsersInGroups;
                        }
                        hasUnusedLicenses = profileDisInstance.getIfAppHasEnoughLicensesForGroup(appGroupId, remainingLicense, totalCountInGroup, licensesAssociatedToResources);
                    }
                    final int appType = MDMAppMgmtHandler.getInstance().getAppType(packageType, platformType);
                    if (appType != 0) {
                        final HashMap hm = new HashMap();
                        if (appImageUrl != null) {
                            appImageUrl = appImageUrl.replace("\\", "/");
                            hm.put("path", appImageUrl);
                            hm.put("IS_SERVER", true);
                            hm.put("IS_AUTHTOKEN", false);
                            appImageUrl = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        }
                    }
                    if (MDMStringUtils.isEmpty(appImageUrl)) {
                        appImageUrl = this.getDefaultAppImage(platformType);
                    }
                    final String profileCollnId = String.valueOf(profileId).concat("_").concat(String.valueOf(collectionId));
                    Properties userDataProperties = null;
                    final TreeNode childNode = new TreeNode();
                    childNode.id = profileCollnId;
                    if (filterButtonVal.equalsIgnoreCase(selectedString) && !profileJSON.containsKey((Object)childNode.id)) {
                        continue;
                    }
                    if (profileName.length() > 18) {
                        String profileNameFull = profileName;
                        profileName = profileName.substring(0, 18).concat("...");
                        profileNameFull = profileNameFull.replaceAll("'", "\\\\'");
                        profileName = "<f onmouseout=\"return nd();\" onmouseover=\"overlib('" + profileNameFull + "' , WIDTH , '10',WRAP, HAUTO,FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66',CSSCLASS,TEXTFONTCLASS,'bodytext',FGCLASS,'bodybg tablebg',BGCLASS,'bgClass')\">" + profileName + "</f>";
                    }
                    appCategory = I18N.getMsg(appCategory, new Object[0]);
                    userDataProperties = new Properties();
                    ((Hashtable<String, Long>)userDataProperties).put("collectionId", collectionId);
                    final String defaultStyle = "min-width: 205px !important;padding: 5px 15px 3px 5px !important;background-image: url('') !important; background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
                    final String selectedStyle = "min-width: 205px !important;padding: 5px 15px 3px 5px !important;background-image: url(''), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;color: rgb(95, 156, 212) !important;";
                    final String disabledStyle = "min-width: 205px !important;padding: 5px 15px 3px 5px !important;background-image: url('') !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:default";
                    final String upgradeStyle = "min-width: 205px !important;padding: 5px 15px 3px 5px !important;background-image: url(''), url(''), url(/images/app_update.png) !important; background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
                    final String upgradeSelectedStyle = "min-width: 205px !important;padding: 5px 15px 3px 5px !important;background-image: url(''), url(/images/select.png), url(/images/app_update.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important;color: rgb(95, 156, 212) !important;";
                    final String licenseStyle = "min-width: 205px !important;padding: 5px 15px 3px 5px !important;background-image: url('') !important; background-color: rgb(249, 249, 249) !important; filter: opacity(80%); border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:not-allowed";
                    ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
                    ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
                    ((Hashtable<String, String>)userDataProperties).put("upgradeStyle", upgradeStyle);
                    ((Hashtable<String, String>)userDataProperties).put("upgradeSelectedStyle", upgradeSelectedStyle);
                    ((Hashtable<String, Integer>)userDataProperties).put("latestVer", latestVer);
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                    ((Hashtable<String, Boolean>)userDataProperties).put("isUpgrade", false);
                    ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", false);
                    ((Hashtable<String, Long>)userDataProperties).put("releaseLabelId", releaseLabelId);
                    ((Hashtable<String, String>)userDataProperties).put("releaseLabelName", releaseLabelDisplayName);
                    ((Hashtable<String, Integer>)userDataProperties).put("availableLicense", remainingLicense);
                    ((Hashtable<String, Integer>)userDataProperties).put("totalLicense", totalLicense);
                    ((Hashtable<String, Integer>)userDataProperties).put("usedLicense", usedLicense);
                    if (profileJSON.containsKey((Object)childNode.id)) {
                        childNode.checked = true;
                        childNode.style = selectedStyle;
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                    }
                    else {
                        childNode.style = defaultStyle;
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                        if (!hasUnusedLicenses) {
                            final String insufficientLicenseMsg = I18N.getMsg("dc.mdm.group.insufficient_license", new Object[] { totalCountInGroup, remainingLicense });
                            if (!filterButtonVal.equalsIgnoreCase(associatedString)) {
                                ((Hashtable<String, String>)userDataProperties).put("insufficientLicenseMsg", insufficientLicenseMsg);
                                ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                            }
                            ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", false);
                            childNode.style = licenseStyle;
                        }
                        if (platformType == 4 && containsDeviceResource) {
                            final String insufficientLicenseMsg = I18N.getMsg("Chrome app cannot be distributed to the devices or device group.", new Object[] { totalCountInGroup, remainingLicense });
                            if (!filterButtonVal.equalsIgnoreCase(associatedString)) {
                                ((Hashtable<String, String>)userDataProperties).put("insufficientLicenseMsg", insufficientLicenseMsg);
                                ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                            }
                            ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", false);
                            childNode.style = licenseStyle;
                        }
                    }
                    if (!filterButtonVal.equalsIgnoreCase(selectedString)) {
                        final Long PROFILE_ID = (Long)ds.getValue("RECENT_PROFILE_ID");
                        final Integer updateCount = (Integer)ds.getValue("UPDATE_ASSOCIATED_COUNT");
                        if (filterButtonVal.equalsIgnoreCase(availableString) && PROFILE_ID != null && updateCount != null && updateCount != 0) {
                            ((Hashtable<String, Boolean>)userDataProperties).put("isUpgrade", true);
                            if (profileJSON.containsKey((Object)childNode.id)) {
                                childNode.style = upgradeSelectedStyle;
                            }
                            else {
                                childNode.style = upgradeStyle;
                            }
                        }
                        if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                            if (PROFILE_ID == null) {
                                continue;
                            }
                            if (PROFILE_ID != null) {
                                final Integer betaResCount = (Integer)ds.getValue("BETA_RES_COUNT");
                                if (betaResCount != null && betaResCount != 0) {
                                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", Boolean.FALSE);
                                    ((Hashtable<String, String>)userDataProperties).put("insufficientLicenseMsg", I18N.getMsg("mdm.appmgmt.app_already_distributed", new Object[0]));
                                    ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                                    childNode.style = disabledStyle;
                                }
                            }
                        }
                    }
                    final String platformLogo = this.getPlatformLogo(platformType);
                    final String defaultImage = this.getDefaultAppImage(platformType);
                    if (showPlatformIcon) {
                        childNode.text = "<img style='background-color:" + backgroundColor + ";width:40px !important;margin-right: 10px;margin-top: 4px;margin-left: 4px;float: left;' src=" + appImageUrl + " onError=\"this.onerror=null;this.src='" + defaultImage + "';\"><div style='float: left;'>".concat(profileName.concat("<br/><div class='infoText' style='background-image: url(" + platformLogo + ") !important;background-repeat:no-repeat;padding-left:20px' id='app_" + profileCollnId + "'>").concat(appCategory).concat("</div>"));
                    }
                    else {
                        childNode.text = "<img style='background-color:" + backgroundColor + ";width:40px !important;margin-right: 10px;margin-top: 4px;margin-left: 4px;float: left;' src=" + appImageUrl + " onError=\"this.onerror=null;this.src='" + defaultImage + "';\"><div style='float: left;'>".concat(profileName.concat("<br/><div class='infoText' id='app_" + profileCollnId + "'>").concat(appCategory).concat("</div>"));
                    }
                    childNode.userData = userDataProperties;
                    treeNodeList.add(childNode);
                }
            }
            catch (final SQLException ex) {
                MDMUtil.logger.log(Level.WARNING, "Exception occurred in getChildTreeNodes: {0}", ex);
            }
            catch (final QueryConstructionException ex2) {
                MDMUtil.logger.log(Level.WARNING, "Exception occurred in getChildTreeNodes: {0}", (Throwable)ex2);
            }
        }
        catch (final Exception ex3) {
            Logger.getLogger(MDMAppListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex3);
        }
        return treeNodeList;
    }
    
    private SelectQuery getGroupAppQuery(final Long customerId, final Long[] groupIds, final String filterButtonVal, final String filterTreeParams, final String selectedString, final String associatedString, final String availableString, final String searchValue, final int startIndex, final int noOfObj, final String selectAllValue) {
        final SelectQuery appQuery = ProfileUtil.getInstance().getQueryforAppCollnGroup(null, customerId);
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
        final Criteria deleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Criteria appReleaseLabelCri = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, 0);
        appQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appQuery.addJoin(new Join("MdPackageToAppData", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        appQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 1));
        appQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 1));
        appQuery.addJoin(new Join("MdPackageToAppData", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        appQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 1));
        appQuery.addJoin(new Join("MdPackageToAppData", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        appQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_LABEL"));
        appQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
        appQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LICENSE_TYPE"));
        appQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "IMG_BG"));
        appQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
        Criteria availableDistributeAppCri = null;
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        final Column countCol = new Column("RecentProfileForGroup", "GROUP_ID").count();
        countCol.setColumnAlias("RECENT_PROFILE_RES_COUNT");
        profileSQ.addSelectColumn(countCol);
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds, 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForGroup", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)groupIds.length, 4));
        profileSQ.setGroupByClause(groupByClause);
        final Table profileTab = Table.getTable("Profile");
        final DerivedTable profileDerievedTab = new DerivedTable("RecentProfileForGroup", (Query)profileSQ);
        final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), 0);
        appQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
        final Column recentProfileId = Column.getColumn("RecentProfileForGroup", "PROFILE_ID", "RECENT_PROFILE_ID");
        final Column recentProfileCountCol = Column.getColumn("RecentProfileForGroup", "RECENT_PROFILE_RES_COUNT");
        appQuery.addSelectColumn(recentProfileId);
        appQuery.addSelectColumn(recentProfileCountCol);
        if (filterButtonVal.equalsIgnoreCase(selectedString)) {
            availableDistributeAppCri = appQuery.getCriteria();
        }
        else {
            if (filterButtonVal.equalsIgnoreCase(availableString)) {
                final Criteria availableGroupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)null, 0);
                final Criteria associatedGroupCri = new Criteria(Column.getColumn("MDAPPCATALOG", "PROFILE_ID"), (Object)null, 1);
                availableDistributeAppCri = appQuery.getCriteria().and(availableGroupCri.or(associatedGroupCri));
            }
            else if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                final Criteria associatedToAllSelectedGroupsCri = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)null, 1);
                availableDistributeAppCri = appQuery.getCriteria().and(associatedToAllSelectedGroupsCri);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForGroup"));
            selectQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            selectQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria updateAvailableJoinCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForGroup", "GROUP_ID"), 0));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToGroup", updateAvailableJoinCriteria, 2));
            final Column groupColumn = new Column("RecentProfileForGroup", "GROUP_ID").count();
            groupColumn.setColumnAlias("UPDATE_ASSOCIATED_COUNT");
            final GroupByColumn groupByCol = new GroupByColumn(new Column("RecentProfileForGroup", "PROFILE_ID"), true);
            final List<GroupByColumn> groupByListCatalog = new ArrayList<GroupByColumn>();
            groupByListCatalog.add(groupByCol);
            final Criteria criteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria groupIdsCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupIds, 8).and(new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds, 8));
            final Criteria appReleaseLabelCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, 0);
            selectQuery.setCriteria(criteria.and(groupIdsCriteria).and(appReleaseLabelCriteria));
            final GroupByClause groupByClauseCatalog = new GroupByClause((List)groupByListCatalog, new Criteria(groupColumn, (Object)0, 5));
            selectQuery.setGroupByClause(groupByClauseCatalog);
            selectQuery.addSelectColumn(groupColumn);
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
            final DerivedTable derivedTable = new DerivedTable("MDAPPCATALOG", (Query)selectQuery);
            appQuery.addJoin(new Join(Table.getTable("Profile"), (Table)derivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            final Column prodUpdateAssociatedCount = Column.getColumn("MDAPPCATALOG", "UPDATE_ASSOCIATED_COUNT");
            appQuery.addSelectColumn(prodUpdateAssociatedCount);
            final SelectQuery betaAssociatedQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
            betaAssociatedQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            betaAssociatedQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            betaAssociatedQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            final Criteria groupIdCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds, 8);
            final Criteria markedForDelFalse = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            final Criteria betaCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_BETA, 0);
            betaAssociatedQuery.setCriteria(groupIdCriteria.and(markedForDelFalse).and(betaCriteria));
            final Column betaResCount = new Column("RecentProfileForGroup", "GROUP_ID").count();
            final Column profileIdCol = Column.getColumn("RecentProfileForGroup", "PROFILE_ID");
            betaResCount.setColumnAlias("BETA_RES_COUNT");
            betaAssociatedQuery.addSelectColumn(betaResCount);
            betaAssociatedQuery.addSelectColumn(profileIdCol);
            final List<Column> gbcColumnList = new ArrayList<Column>();
            gbcColumnList.add(profileIdCol);
            final GroupByClause profileGroupBy = new GroupByClause((List)gbcColumnList, new Criteria(betaResCount, (Object)groupIds.length, 4));
            betaAssociatedQuery.setGroupByClause(profileGroupBy);
            final DerivedTable prodBetaResDerivedTable = new DerivedTable("PRODBETARES", (Query)betaAssociatedQuery);
            appQuery.addJoin(new Join(Table.getTable("Profile"), (Table)prodBetaResDerivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            appQuery.addSelectColumn(new Column("PRODBETARES", "BETA_RES_COUNT"));
            final SortColumn sortProdUpdateAssociatedCount = new SortColumn(prodUpdateAssociatedCount, false);
            appQuery.addSortColumn(sortProdUpdateAssociatedCount);
            final List selectColumnList = appQuery.getSelectColumns();
            appQuery.setGroupByClause(new GroupByClause(selectColumnList));
            final SortColumn sortRecentProfileIdCountCol = new SortColumn(recentProfileCountCol, false, false);
            appQuery.addSortColumn(sortRecentProfileIdCountCol);
        }
        final Criteria commonCri = this.getCommonCriteria(filterTreeParams, searchValue);
        Criteria cri = availableDistributeAppCri.and(profileTypeCri).and(deleteCri).and(appReleaseLabelCri);
        if (commonCri != null) {
            cri = cri.and(commonCri);
        }
        appQuery.setCriteria(cri);
        if (selectAllValue == null && !filterButtonVal.equalsIgnoreCase(selectedString)) {
            final Range profileRange = new Range(startIndex, noOfObj);
            appQuery.setRange(profileRange);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
        appQuery.addSortColumn(sortCol);
        appQuery.setDistinct(true);
        return appQuery;
    }
    
    private SelectQuery getDeviceAppQuery(final Long customerId, final Long[] deviceIds, final int platformType, final String filterButtonVal, final String filterTreeParams, final String selectedString, final String associatedString, final String availableString, final String searchValue, final int startIndex, final int noOfObj, final String selectAllValue) {
        final SelectQuery appQuery = ProfileUtil.getInstance().getQueryforAppCollnDevice(null, customerId);
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
        final Criteria platformTypeCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria deleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Criteria appReleaseLabelCri = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, 0);
        appQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appQuery.addJoin(new Join("MdPackageToAppData", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        appQuery.addJoin(new Join("MdPackageToAppData", "MDAppAssignableDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        appQuery.addJoin(new Join("MdPackageToAppData", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        appQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 1));
        appQuery.addJoin(new Join("MdPackageToAppData", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        appQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        appQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_LABEL"));
        appQuery.addSelectColumn(Column.getColumn("MDAppAssignableDetails", "APP_ASSIGNABLE_TYPE"));
        appQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "IMG_BG"));
        appQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION", "ProfileColln.PROFILE_VERSION"));
        Criteria availableDistributeAppCri = null;
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        final Column countCol = new Column("RecentProfileForResource", "RESOURCE_ID").count();
        countCol.setColumnAlias("RECENT_PROFILE_RES_COUNT");
        profileSQ.addSelectColumn(countCol);
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds, 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForResource", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)deviceIds.length, 4));
        profileSQ.setGroupByClause(groupByClause);
        final Table profileTab = Table.getTable("Profile");
        final DerivedTable profileDerievedTab = new DerivedTable("RecentProfileForResource", (Query)profileSQ);
        final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"), 0);
        appQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
        final Column recentProfileId = Column.getColumn("RecentProfileForResource", "PROFILE_ID", "RECENT_PROFILE_ID");
        final Column recentProfileCountCol = Column.getColumn("RecentProfileForResource", "RECENT_PROFILE_RES_COUNT");
        appQuery.addSelectColumn(recentProfileId);
        appQuery.addSelectColumn(recentProfileCountCol);
        if (filterButtonVal.equalsIgnoreCase(selectedString)) {
            availableDistributeAppCri = appQuery.getCriteria();
        }
        else {
            if (filterButtonVal.equalsIgnoreCase(availableString)) {
                final Criteria availableDeviceCri = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)null, 0);
                final Criteria associatedDeviceCri = new Criteria(Column.getColumn("MDAPPCATALOG", "PROFILE_ID"), (Object)null, 1);
                availableDistributeAppCri = appQuery.getCriteria().and(availableDeviceCri.or(associatedDeviceCri));
            }
            else if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                final Criteria associatedToAllSelectedDevicesCri = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)null, 1);
                availableDistributeAppCri = appQuery.getCriteria().and(associatedToAllSelectedDevicesCri);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            selectQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria updateAvailableJoinCriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), 0).and(new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"), 0));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResourceExtn", updateAvailableJoinCriteria, 2));
            final Column groupColumn = new Column("RecentProfileForResource", "RESOURCE_ID").count();
            groupColumn.setColumnAlias("UPDATE_ASSOCIATED_COUNT");
            final GroupByColumn groupByCol = new GroupByColumn(new Column("RecentProfileForResource", "PROFILE_ID"), true);
            final List<GroupByColumn> groupByListCatalog = new ArrayList<GroupByColumn>();
            groupByListCatalog.add(groupByCol);
            final Criteria criteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria deviceIdsCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds, 8);
            final Criteria appReleaseLabelCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_PRODUCTION, 0);
            selectQuery.setCriteria(criteria.and(deviceIdsCriteria).and(appReleaseLabelCriteria));
            final GroupByClause groupByClauseCatalog = new GroupByClause((List)groupByListCatalog, new Criteria(groupColumn, (Object)0, 4));
            selectQuery.setGroupByClause(groupByClauseCatalog);
            selectQuery.addSelectColumn(groupColumn);
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
            final DerivedTable derivedTable = new DerivedTable("MDAPPCATALOG", (Query)selectQuery);
            appQuery.addJoin(new Join(Table.getTable("Profile"), (Table)derivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            final Column prodUpdateAssociatedCount = Column.getColumn("MDAPPCATALOG", "UPDATE_ASSOCIATED_COUNT");
            appQuery.addSelectColumn(prodUpdateAssociatedCount);
            final SelectQuery betaAssociatedQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            betaAssociatedQuery.addJoin(new Join("RecentProfileForResource", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            betaAssociatedQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            betaAssociatedQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
            final Criteria deviceIdCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds, 8);
            final Criteria markedForDelFalse = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            final Criteria betaCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE"), (Object)AppVersionDBUtil.RELEASE_LABEL_BETA, 0);
            betaAssociatedQuery.setCriteria(deviceIdCriteria.and(markedForDelFalse).and(betaCriteria));
            final Column betaResCount = new Column("RecentProfileForResource", "RESOURCE_ID").count();
            final Column profileIdCol = Column.getColumn("RecentProfileForResource", "PROFILE_ID");
            betaResCount.setColumnAlias("BETA_RES_COUNT");
            betaAssociatedQuery.addSelectColumn(betaResCount);
            betaAssociatedQuery.addSelectColumn(profileIdCol);
            final List<Column> gbcColumnList = new ArrayList<Column>();
            gbcColumnList.add(profileIdCol);
            final GroupByClause profileGroupBy = new GroupByClause((List)gbcColumnList, new Criteria(betaResCount, (Object)deviceIds.length, 4));
            betaAssociatedQuery.setGroupByClause(profileGroupBy);
            final DerivedTable prodBetaResDerivedTable = new DerivedTable("PRODBETARES", (Query)betaAssociatedQuery);
            appQuery.addJoin(new Join(Table.getTable("Profile"), (Table)prodBetaResDerivedTable, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            appQuery.addSelectColumn(new Column("PRODBETARES", "BETA_RES_COUNT"));
            final SortColumn sortProdUpdateAssociatedCount = new SortColumn(prodUpdateAssociatedCount, false);
            appQuery.addSortColumn(sortProdUpdateAssociatedCount);
            final List selectColumnList = appQuery.getSelectColumns();
            appQuery.setGroupByClause(new GroupByClause(selectColumnList));
            final SortColumn sortRecentProfileIdCountCol = new SortColumn(recentProfileCountCol, false, false);
            appQuery.addSortColumn(sortRecentProfileIdCountCol);
        }
        final Criteria commonCri = this.getCommonCriteria(filterTreeParams, searchValue);
        Criteria cri = availableDistributeAppCri.and(profileTypeCri).and(platformTypeCri).and(deleteCri).and(appReleaseLabelCri);
        if (commonCri != null) {
            cri = cri.and(commonCri);
        }
        appQuery.setCriteria(cri);
        if (selectAllValue == null && !filterButtonVal.equalsIgnoreCase(selectedString)) {
            final Range profileRange = new Range(startIndex, noOfObj);
            appQuery.setRange(profileRange);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
        appQuery.addSortColumn(sortCol);
        appQuery.setDistinct(true);
        return appQuery;
    }
    
    private Criteria getCommonCriteria(final String filterTreeParams, final String searchValue) {
        Criteria commonCri = null;
        if (searchValue != null) {
            final Criteria searchProfileCri = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchValue, 12, false);
            commonCri = ((commonCri != null) ? commonCri.and(searchProfileCri) : searchProfileCri);
        }
        if (filterTreeParams != null) {
            try {
                final JSONArray filterTreeJSON = (JSONArray)new JSONParser().parse(filterTreeParams);
                if (filterTreeJSON.size() > 0) {
                    final Criteria filterCri = MDMProfileAppFilterHandler.getInstance().getFilterCriteria(filterTreeJSON);
                    commonCri = ((commonCri != null) ? commonCri.and(filterCri) : filterCri);
                }
            }
            catch (final ParseException ex) {
                Logger.getLogger(MDMAppListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return commonCri;
    }
    
    private String getDefaultAppImage(final int platformType) throws Exception {
        String value = null;
        if (platformType == 1) {
            value = "/images/ios-grey.png";
        }
        else if (platformType == 2) {
            value = "/images/android-grey.png";
        }
        else if (platformType == 3) {
            value = "/images/windows-grey.png";
        }
        else if (platformType == 4) {
            value = "/images/chrome-large.png";
        }
        return value;
    }
    
    public org.json.JSONObject getJSONObject(final NodeSettings nodeSettings) throws Exception {
        final org.json.JSONObject jsonObject = super.getJSONObject(nodeSettings);
        try {
            Boolean isScrollComplete = Boolean.FALSE;
            final org.json.JSONArray items = (org.json.JSONArray)jsonObject.get("item");
            if (items != null) {
                isScrollComplete = (items.length() == 0 || items.length() < 50);
            }
            final Map userdata = nodeSettings.userData;
            final String gid = userdata.get("groupId");
            final String did = userdata.get("deviceId");
            int trashedApp = 0;
            final Long customerID = Long.parseLong(userdata.get("cid").toString());
            if (!MDMStringUtils.isEmpty(gid) && !gid.equalsIgnoreCase("['']")) {
                final Long[] groupIds = this.convertToLongArray(gid);
                trashedApp = this.getTrashMessageEnable(groupIds, true, customerID);
            }
            if (!MDMStringUtils.isEmpty(did) && !did.equalsIgnoreCase("['']")) {
                final Long[] deviceIds = this.convertToLongArray(did);
                trashedApp = this.getTrashMessageEnable(deviceIds, false, customerID);
            }
            jsonObject.put("isAppInTrash", trashedApp > 0);
            jsonObject.put("trashCount", trashedApp);
            jsonObject.put("isScrollComplete", (Object)isScrollComplete);
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "unable to get if apps Present in trash : failed to populate base json ", e);
        }
        return jsonObject;
    }
    
    private int getTrashMessageEnable(final Long[] resourceIds, final boolean isGroup, final Long customerID) {
        int trashedApps = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        if (isGroup) {
            selectQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        else {
            selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        selectQuery.addJoin(new Join("ManagedDevice", "MdAppGroupDetails", new String[] { "PLATFORM_TYPE" }, new String[] { "PLATFORM_TYPE" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        final Criteria resCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIds, 8);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(resCriteria.and(trashCriteria).and(customerCriteria));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("Profile");
            while (iterator.hasNext()) {
                ++trashedApps;
                iterator.next();
            }
        }
        catch (final DataAccessException e) {
            MDMUtil.logger.log(Level.WARNING, "Unbale to get if apps Present in trash : ", (Throwable)e);
        }
        return trashedApps;
    }
    
    private Long[] convertToLongArray(final String ids) {
        Long[] resourceIds = null;
        try {
            final org.json.JSONArray jsonArray = new org.json.JSONArray(ids);
            resourceIds = new Long[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                resourceIds[i] = jsonArray.getLong(i);
            }
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Unbale to get if apps Present in trash : failed to get resource IDS ", e);
        }
        return resourceIds;
    }
    
    private Map<Long, Map<String, Object>> processDSForAppGroupData(final DMDataSetWrapper ds) throws Exception {
        final Map<Long, Map<String, Object>> profileMap = new HashMap<Long, Map<String, Object>>();
        while (ds.next()) {
            final Long profileId = (Long)ds.getValue("PROFILE_ID");
            Map<String, Object> individualProfileMap = profileMap.get(profileId);
            if (individualProfileMap == null) {
                individualProfileMap = new HashMap<String, Object>();
            }
            Map<String, Object> detailsMap = individualProfileMap.get("profile_details");
            if (detailsMap == null) {
                detailsMap = new HashMap<String, Object>();
                detailsMap.put("PLATFORM_TYPE", ds.getValue("PLATFORM_TYPE"));
                detailsMap.put("PROFILE_NAME", ds.getValue("PROFILE_NAME"));
                detailsMap.put("DISPLAY_IMAGE_LOC", ds.getValue("DISPLAY_IMAGE_LOC"));
                detailsMap.put("PROFILE_VERSION", ds.getValue("PROFILE_VERSION"));
                detailsMap.put("COLLECTION_ID", ds.getValue("COLLECTION_ID"));
                detailsMap.put("APP_CATEGORY_LABEL", ds.getValue("APP_CATEGORY_LABEL"));
                detailsMap.put("PACKAGE_TYPE", ds.getValue("PACKAGE_TYPE"));
                detailsMap.put("APP_GROUP_ID", ds.getValue("MdPackageToAppData_APP_GROUP_ID"));
                detailsMap.put("IS_PAID_APP", ds.getValue("IS_PAID_APP"));
                detailsMap.put("IS_PURCHASED_FROM_PORTAL", ds.getValue("IS_PURCHASED_FROM_PORTAL"));
                detailsMap.put("APP_ASSIGNABLE_TYPE", ds.getValue("APP_ASSIGNABLE_TYPE"));
                detailsMap.put("IMG_BG", ds.getValue("IMG_BG"));
                detailsMap.put("RELEASE_LABEL_ID", ds.getValue("RELEASE_LABEL_ID"));
                detailsMap.put("RELEASE_LABEL_DISPLAY_NAME", I18N.getMsg((String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                individualProfileMap.put("profile_details", detailsMap);
            }
            Integer countOfResourcesAssociatedWith = individualProfileMap.get("countOfAssociatedResources");
            if (countOfResourcesAssociatedWith == null) {
                countOfResourcesAssociatedWith = 0;
            }
            Map<Long, List<Map.Entry<Long, Long>>> labelIdMap = individualProfileMap.get("label_id_list");
            if (labelIdMap == null) {
                labelIdMap = new HashMap<Long, List<Map.Entry<Long, Long>>>();
            }
            final Long distributedCollectionId = (Long)ds.getValue("DIST_COLLECTION_ID");
            if (distributedCollectionId != null) {
                ++countOfResourcesAssociatedWith;
                final Long distributedReleaseLabelId = (Long)ds.getValue("DIST_RELEASE_LABEL_ID");
                final Long groupId = (Long)ds.getValue("DIST_RESOURCE_ID");
                List<Map.Entry<Long, Long>> groupCollectionList = labelIdMap.get(distributedReleaseLabelId);
                if (groupCollectionList == null) {
                    groupCollectionList = new ArrayList<Map.Entry<Long, Long>>();
                }
                groupCollectionList.add(new AbstractMap.SimpleEntry<Long, Long>(groupId, distributedCollectionId));
                labelIdMap.put(distributedReleaseLabelId, groupCollectionList);
            }
            individualProfileMap.put("label_id_list", labelIdMap);
            individualProfileMap.put("countOfAssociatedResources", countOfResourcesAssociatedWith);
            profileMap.put(profileId, individualProfileMap);
        }
        return profileMap;
    }
    
    private String getPlatformLogo(final Integer platformType) {
        String platformLogo = "";
        if (platformType == 1) {
            platformLogo = "/images/applelogo.png";
        }
        if (platformType == 2) {
            platformLogo = "/images/androidlogo.png";
        }
        if (platformType == 3) {
            platformLogo = "/images/windowslogo.png";
        }
        if (platformType == 4) {
            platformLogo = "/images/chrome.png";
        }
        if (platformType == 6) {
            platformLogo = "/images/applelogo.png";
        }
        if (platformType == 7) {
            platformLogo = "/images/applelogo.png";
        }
        return platformLogo;
    }
}
