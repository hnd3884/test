package com.me.mdm.server.apps.multiversion;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Map;
import java.util.Properties;
import java.util.List;

public class IOSAppVersionHandler extends BaseAppVersionHandler
{
    @Override
    public Map<String, List<Long>> removeDevicesContainingLatestVersionOfApp(final List<Long> deviceList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isDowngrade = ((Hashtable<K, Boolean>)collnProps).get("isAppDowngrade");
        if ((isDowngrade != null && isDowngrade) || !AppsUtil.getInstance().isEnterpriseApp(collectionId)) {
            final List<Long> clonedDeviceList = new ArrayList<Long>(deviceList);
            final List<Long> removedListOfResources = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedDeviceList", clonedDeviceList);
            retMap.put("removedDeviceList", removedListOfResources);
            return retMap;
        }
        return super.removeDevicesContainingLatestVersionOfApp(deviceList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Map<String, List<Long>> removeGroupsContainingLatestVersionOfApp(final List<Long> groupList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isDowngrade = ((Hashtable<K, Boolean>)collnProps).get("isAppDowngrade");
        if ((isDowngrade != null && isDowngrade) || !AppsUtil.getInstance().isEnterpriseApp(collectionId)) {
            final List<Long> clonedGroupList = new ArrayList<Long>(groupList);
            final List<Long> removedListOfGroups = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedGroupList", clonedGroupList);
            retMap.put("removedGroupList", removedListOfGroups);
            return retMap;
        }
        return super.removeGroupsContainingLatestVersionOfApp(groupList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Map<String, List<Long>> removeUsersContainingLatestVersionOfApp(final List<Long> userList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isDowngrade = ((Hashtable<K, Boolean>)collnProps).get("isAppDowngrade");
        if ((isDowngrade != null && isDowngrade) || !AppsUtil.getInstance().isEnterpriseApp(collectionId)) {
            final List<Long> clonedUserList = new ArrayList<Long>(userList);
            final List<Long> removedListOfResources = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedUserList", clonedUserList);
            retMap.put("removedUserList", removedListOfResources);
            return retMap;
        }
        return super.removeUsersContainingLatestVersionOfApp(userList, appProfileId, collectionId, collnProps);
    }
    
    @Deprecated
    @Override
    public JSONObject getPossibleChannelsToMergeApp(final JSONObject requestJSON) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final Long packageID = APIUtil.getResourceID(requestJSON, "app_id");
        final Long releaseLabelID = APIUtil.getResourceID(requestJSON, "label_id");
        final PagingUtil pagingUtil = apiUtil.getPagingParams(requestJSON);
        final boolean selectAll = APIUtil.getBooleanFilter(requestJSON, "select_all", false);
        final String search = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("search", (String)null);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        final SelectQuery appAllLiveVersionQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria packageIDCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 1);
        if (search != null) {
            final Criteria searchCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"), (Object)search, 12);
            appAllLiveVersionQuery.setCriteria(appAllLiveVersionQuery.getCriteria().and(searchCriteria));
        }
        appAllLiveVersionQuery.setCriteria(packageIDCriteria.and(releaseLabelCriteria));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_VERSION"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "APP_NAME"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdAppDetails", "IDENTIFIER"));
        appAllLiveVersionQuery.addSelectColumn(new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appAllLiveVersionQuery.addSelectColumn(new Column("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final int count = MDMDBUtil.getCount(appAllLiveVersionQuery, "AppReleaseLabel", "RELEASE_LABEL_ID");
        responseJSON.put("paging", (Object)pagingUtil.getPagingJSON(count));
        if (count != 0) {
            if (!selectAll) {
                appAllLiveVersionQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    if (String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("channel_name")) {
                        appAllLiveVersionQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", (boolean)isSortOrderASC));
                    }
                }
                else {
                    appAllLiveVersionQuery.addSortColumn(new SortColumn("AppReleaseLabel", "RELEASE_LABEL_TYPE", true));
                }
            }
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)appAllLiveVersionQuery);
            while (ds.next()) {
                final JSONObject channelDetails = new JSONObject();
                channelDetails.put("bundle_identifier", ds.getValue("IDENTIFIER"));
                channelDetails.put("app_name", ds.getValue("APP_NAME"));
                channelDetails.put("app_version_code", ds.getValue("APP_NAME_SHORT_VERSION"));
                channelDetails.put("version", ds.getValue("APP_VERSION"));
                if (!MDMStringUtils.isEmpty((String)ds.getValue("DISPLAY_IMAGE_LOC"))) {
                    final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                    if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                        if (!displayImageLoc.startsWith("http")) {
                            channelDetails.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                        }
                        else {
                            channelDetails.put("icon", (Object)displayImageLoc);
                        }
                    }
                }
                channelDetails.put("release_label_id", ds.getValue("RELEASE_LABEL_ID"));
                channelDetails.put("release_label_name", (Object)I18N.getMsg((String)ds.getValue("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                jsonArray.put((Object)channelDetails);
            }
        }
        responseJSON.put("possible_channels_merge", (Object)jsonArray);
        return responseJSON;
    }
    
    @Override
    public Criteria getDistributedDeviceListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        return super.getDistributedDeviceListForAppCriteria(collectionID, profileID);
    }
    
    @Override
    public Criteria getDistributedGroupListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        return super.getDistributedGroupListForAppCriteria(collectionID, profileID);
    }
    
    @Override
    protected Criteria getPlatformCriteria() {
        return new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
    }
}
