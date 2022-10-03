package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.logging.Logger;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import java.util.Properties;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.me.mdm.server.config.MDMConfigUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.tree.NodeSettings;
import com.me.devicemanagement.framework.server.tree.datahandler.DefaultListViewNodeDataObject;

public class MDMProfileListViewDataHandler extends DefaultListViewNodeDataObject
{
    public List getChildTreeNodes(final NodeSettings nodeSettings) throws Exception {
        final Map requestMap = nodeSettings.userData;
        final String selectedString = "selected";
        final String associatedString = "associated";
        final String availableString = "all";
        final List treeNodeList = new ArrayList();
        boolean isGroup = false;
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
            Long customerId = null;
            int platformType = -1;
            int startIndex = 0;
            int noOfObj = 50;
            Boolean containsDeviceResource = false;
            if (startIndexStr != null) {
                startIndex = Integer.parseInt(startIndexStr);
                noOfObj = Integer.parseInt(noOfObjStr);
            }
            Boolean showPlatformIcon = true;
            SelectQuery profileQuery = null;
            if (groupIdsStr != null && !groupIdsStr.equals("") && !groupIdsStr.equals("[]") && !groupIdsStr.equals("['']")) {
                isGroup = true;
                groupIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(groupIdsStr);
                customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(groupIds[0]);
                final List grpList = CustomGroupingHandler.getCustomGroupsList(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupIds, 8).and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new Integer[] { 4, 3, 5, 6 }, 8)));
                if (!grpList.isEmpty()) {
                    containsDeviceResource = true;
                }
                profileQuery = this.getGroupProfileQuery(customerId, groupIds, searchValue, startIndex, noOfObj, filterTreeParams, selectAllValue);
            }
            else {
                showPlatformIcon = false;
                deviceIds = MDMGroupHandler.getInstance().decodeGroupMemberIds(deviceIdsStr);
                customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(deviceIds[0]);
                platformType = ManagedDeviceHandler.getInstance().getPlatformType(deviceIds[0]);
                platformType = (MDMUtil.getInstance().isMacDevice(deviceIds[0]) ? 6 : platformType);
                final List deviceList = ManagedDeviceHandler.getInstance().getDeviceResourceIDs(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceIds, 8));
                if (!deviceList.isEmpty()) {
                    containsDeviceResource = true;
                }
                profileQuery = this.getDeviceProfileQuery(customerId, deviceIds, platformType, searchValue, startIndex, noOfObj, selectAllValue);
            }
            final JSONObject profileJSON = (JSONObject)new JSONParser().parse(profileJSONStr);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)profileQuery);
                while (ds.next()) {
                    final Long profileId = (Long)ds.getValue("PROFILE_ID");
                    String profileName = (String)ds.getValue("PROFILE_NAME");
                    final int latestVer = (int)ds.getValue("PROFILE_VERSION");
                    final int platform = (int)ds.getValue("PLATFORM_TYPE");
                    final int scope = (int)ds.getValue("SCOPE");
                    final Long collectionId = (Long)ds.getValue("COLLECTION_ID");
                    final org.json.JSONObject configIDs = MDMConfigUtil.getConfiuguredPolicyInfo(collectionId);
                    final Boolean hasWinEmailOrExchangeConfig = configIDs.has("602") || configIDs.has("603") || configIDs.has("606");
                    final Boolean hasKioskConfig = ProfileUtil.getInstance().hasKioskConfigWithAutoDistributeAppsConfigured(platform, collectionId);
                    final String profileCollnId = String.valueOf(profileId).concat("_").concat(String.valueOf(collectionId));
                    Properties userDataProperties = null;
                    final TreeNode childNode = new TreeNode();
                    childNode.id = profileCollnId;
                    if (filterButtonVal.equalsIgnoreCase(selectedString) && !profileJSON.containsKey((Object)childNode.id)) {
                        continue;
                    }
                    userDataProperties = new Properties();
                    ((Hashtable<String, Integer>)userDataProperties).put("latestVer", latestVer);
                    ((Hashtable<String, Long>)userDataProperties).put("collectionId", collectionId);
                    if (profileName.length() > 18) {
                        String profileNameFull = profileName;
                        profileName = profileName.substring(0, 18).concat("...");
                        profileNameFull = profileNameFull.replaceAll("'", "\\\\'");
                        profileName = "<f onmouseout=\"return nd();\" onmouseover=\"overlib('" + profileNameFull + "' , WIDTH , '10',WRAP, HAUTO,FGCOLOR, '#faf8de' , BGCOLOR, '#d9ca66',CSSCLASS,TEXTFONTCLASS,'bodytext',FGCLASS,'bodybg tablebg',BGCLASS,'bgClass')\">" + profileName + "</f>";
                    }
                    String resourceImage = "";
                    if (platform == 1) {
                        resourceImage = "/images/applelogo.png";
                    }
                    if (platform == 2) {
                        resourceImage = "/images/androidlogo.png";
                    }
                    if (platform == 3) {
                        resourceImage = "/images/windowslogo.png";
                    }
                    if (platform == 4) {
                        resourceImage = "/images/chrome.png";
                    }
                    if (platform == 6) {
                        resourceImage = "/images/applelogo.png";
                    }
                    if (platform == 7) {
                        resourceImage = "/images/applelogo.png";
                    }
                    int profileVersion = latestVer;
                    ((Hashtable<String, Integer>)userDataProperties).put("latestVer", latestVer);
                    final String defaultStyle = "background-image: url(/images/profile.png) !important; background-color: rgb(249, 249, 249) !important;  border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
                    final String selectedStyle = "background-image: url(/images/profile-select.png), url(/images/select.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important; color: rgb(95, 156, 212) !important;";
                    final String disabledStyle = "background-image: url(/images/profile.png) !important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:default";
                    final String upgradeStyle = "background-image: url(/images/profile.png), url(''), url(/images/app_update.png)!important; background-color: rgb(249, 249, 249) !important; border: 1px solid rgb(226, 226, 226) !important; color: black !important;";
                    final String upgradeSelectedStyle = "background-image: url(/images/profile-select.png), url(/images/select.png), url(/images/app_update.png) !important; background-color: rgb(244, 249, 254) !important; border: 1px solid rgb(205, 228, 245) !important; color: rgb(95, 156, 212) !important;";
                    final String licenseStyle = "background-image: url(/images/profile.png) !important; background-color: rgb(249, 249, 249) !important; filter: opacity(80%); border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:not-allowed";
                    ((Hashtable<String, String>)userDataProperties).put("defaultStyle", defaultStyle);
                    ((Hashtable<String, String>)userDataProperties).put("selectedStyle", selectedStyle);
                    ((Hashtable<String, String>)userDataProperties).put("upgradeStyle", upgradeStyle);
                    ((Hashtable<String, String>)userDataProperties).put("upgradeSelectedStyle", upgradeSelectedStyle);
                    ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", true);
                    ((Hashtable<String, Boolean>)userDataProperties).put("isUpgrade", false);
                    ((Hashtable<String, Boolean>)userDataProperties).put("hasWinEmailOrExchangeConfig", hasWinEmailOrExchangeConfig);
                    ((Hashtable<String, Boolean>)userDataProperties).put("hasKioskConfig", hasKioskConfig);
                    if (profileJSON.containsKey((Object)childNode.id)) {
                        childNode.checked = true;
                        childNode.style = selectedStyle;
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 1);
                    }
                    else {
                        childNode.style = defaultStyle;
                        ((Hashtable<String, Integer>)userDataProperties).put("checked", 0);
                    }
                    if (scope == 2 && containsDeviceResource) {
                        ((Hashtable<String, Boolean>)userDataProperties).put("isEnabled", false);
                        ((Hashtable<String, String>)userDataProperties).put("insufficientLicenseMsg", "Can not apply this user profile to the device or device group");
                        ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                        childNode.style = "background-image: url(/images/profile.png) !important; background-color: rgb(249, 249, 249) !important; filter: opacity(80%); border: 1px solid rgb(226, 226, 226) !important; color: black !important; cursor:not-allowed";
                    }
                    if (filterButtonVal.equalsIgnoreCase(availableString) && ds.getValue("executed_profile_version") != null) {
                        final Integer execVersion = (Integer)ds.getValue("executed_profile_version");
                        if (latestVer <= execVersion) {
                            continue;
                        }
                        ((Hashtable<String, Boolean>)userDataProperties).put("isUpgrade", true);
                        if (profileJSON.containsKey((Object)childNode.id)) {
                            childNode.style = upgradeSelectedStyle;
                        }
                        else {
                            childNode.style = upgradeStyle;
                        }
                    }
                    if (filterButtonVal.equalsIgnoreCase(associatedString)) {
                        if (ds.getValue("executed_profile_version") == null) {
                            continue;
                        }
                        final Integer execVersion = (Integer)ds.getValue("executed_profile_version");
                        profileVersion = execVersion;
                    }
                    final org.json.JSONObject collectionInfo = new org.json.JSONObject();
                    collectionInfo.put("collectionID", (Object)collectionId);
                    collectionInfo.put("groupIDs", (Object)groupIds);
                    collectionInfo.put("deviceIDs", (Object)deviceIds);
                    collectionInfo.put("isGroup", isGroup);
                    collectionInfo.put("customerID", (Object)customerId);
                    final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(platform);
                    handler.getCollectionSpecificUserProperties(collectionInfo, userDataProperties, childNode);
                    final String profileVersionStr = I18N.getMsg("dc.mdm.group.profile_version", new Object[] { profileVersion });
                    if (showPlatformIcon) {
                        childNode.text = profileName.concat("<br/> <div class='infoText' style='background-image: url(" + resourceImage + ") !important;background-repeat:no-repeat;padding-left:20px' id='app_" + profileCollnId + "'>").concat(profileVersionStr).concat("</div>");
                    }
                    else {
                        childNode.text = profileName.concat("<br/> <div class='infoText' id='app_" + profileCollnId + "'>").concat(profileVersionStr).concat("</div>");
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
            Logger.getLogger(MDMProfileListViewDataHandler.class.getName()).log(Level.SEVERE, null, ex3);
        }
        return treeNodeList;
    }
    
    private SelectQuery getGroupProfileQuery(final Long customerId, final Long[] groupIds, final String searchValue, final int startIndex, final int noOfObj, final String filterTreeParams, final String selectAllValue) {
        final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnGroup(null, customerId);
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0);
        final Criteria deleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Join recentProfileTocllnJoin = new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        profileQuery.addJoin(recentProfileTocllnJoin);
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID", "derived_profile_id"));
        final Join profileforGroupJoin = new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profiletoCollectionJoin = new Join("RecentProfileForGroup", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        profileSQ.addJoin(profileforGroupJoin);
        profileSQ.addJoin(profiletoCollectionJoin);
        final Column versionCol = new Column("ProfileToCollection", "PROFILE_VERSION").minimum();
        versionCol.setColumnAlias("executed_profile_version");
        profileSQ.addSelectColumn(versionCol);
        final Column countCol = new Column("RecentProfileForGroup", "GROUP_ID").count();
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds, 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForGroup", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)groupIds.length, 4));
        profileSQ.setGroupByClause(groupByClause);
        final Table profileTab = Table.getTable("Profile");
        final DerivedTable profileDerievedTab = new DerivedTable("derivedProfileTable", (Query)profileSQ);
        final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("derivedProfileTable", "derived_profile_id"), 0);
        profileQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
        profileQuery.addSelectColumn(Column.getColumn("derivedProfileTable", "derived_profile_id"));
        profileQuery.addSelectColumn(Column.getColumn("derivedProfileTable", "executed_profile_version"));
        final Criteria commonCri = this.getCommonCriteria(filterTreeParams, searchValue);
        final Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(deleteCri).and(commonCri);
        profileQuery.setCriteria(cri);
        if (selectAllValue == null) {
            final Range profileRange = new Range(startIndex, noOfObj);
            profileQuery.setRange(profileRange);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
        profileQuery.addSortColumn(sortCol);
        return profileQuery;
    }
    
    private SelectQuery getDeviceProfileQuery(final Long customerId, final Long[] deviceIds, final int platformType, final String searchValue, final int startIndex, final int noOfObj, final String selectAllValue) {
        final SelectQuery profileQuery = ProfileUtil.getInstance().getQueryforProfileCollnDevice(null, customerId);
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0);
        Criteria platformTypeCri = null;
        if (platformType != -1) {
            platformTypeCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
        }
        final Criteria deleteCri = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Join recentProfileTocllnJoin = new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        profileQuery.addJoin(recentProfileTocllnJoin);
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID", "derived_profile_id"));
        final Join profileforGroupJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profiletoCollectionJoin = new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        profileSQ.addJoin(profileforGroupJoin);
        profileSQ.addJoin(profiletoCollectionJoin);
        final Column versionCol = new Column("ProfileToCollection", "PROFILE_VERSION").minimum();
        versionCol.setColumnAlias("executed_profile_version");
        profileSQ.addSelectColumn(versionCol);
        final Column collectionCol = new Column("RecentProfileForResource", "COLLECTION_ID").minimum();
        collectionCol.setColumnAlias("derived_collection_id");
        profileSQ.addSelectColumn(collectionCol);
        final Column countCol = new Column("RecentProfileForResource", "RESOURCE_ID").count();
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds, 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForResource", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)deviceIds.length, 4));
        profileSQ.setGroupByClause(groupByClause);
        final Table profileTab = Table.getTable("Profile");
        final DerivedTable profileDerievedTab = new DerivedTable("derivedProfileTable", (Query)profileSQ);
        final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("derivedProfileTable", "derived_profile_id"), 0);
        profileQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
        profileQuery.addSelectColumn(Column.getColumn("derivedProfileTable", "derived_profile_id"));
        profileQuery.addSelectColumn(Column.getColumn("derivedProfileTable", "executed_profile_version"));
        final Criteria commonCri = this.getCommonCriteria(null, searchValue);
        final Criteria cri = profileQuery.getCriteria().and(profileTypeCri).and(platformTypeCri).and(deleteCri).and(commonCri);
        profileQuery.setCriteria(cri);
        if (selectAllValue == null) {
            final Range profileRange = new Range(startIndex, noOfObj);
            profileQuery.setRange(profileRange);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
        profileQuery.addSortColumn(sortCol);
        return profileQuery;
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
                    final Criteria filterCri = MDMProfileAppFilterHandler.getInstance().getProfileFilterCriteria(filterTreeJSON);
                    commonCri = ((commonCri != null) ? commonCri.and(filterCri) : filterCri);
                }
            }
            catch (final ParseException ex) {
                Logger.getLogger(MDMProfileListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return commonCri;
    }
}
