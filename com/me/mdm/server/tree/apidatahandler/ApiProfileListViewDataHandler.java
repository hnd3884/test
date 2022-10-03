package com.me.mdm.server.tree.apidatahandler;

import java.util.Hashtable;
import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.server.tree.TreeNode;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.mdm.server.config.MDMConfigUtil;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import java.util.Arrays;
import java.util.List;

public class ApiProfileListViewDataHandler extends ApiListViewDataHandler
{
    public static final String UPDATE_AVAILABLE = "update_available";
    
    private List getProfileTypes() {
        return Arrays.asList(1, 10);
    }
    
    @Override
    protected SelectQuery getSelectQuery() {
        (this.selectQuery = super.getSelectQuery()).addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        this.selectQuery.addJoin(new Join("RecentPubProfileToColln", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        this.selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        this.selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        return this.selectQuery;
    }
    
    @Override
    protected SelectQuery setCriteria() throws APIHTTPException {
        this.selectQuery = super.setCriteria();
        final String filterButtonVal = this.requestJson.optString("filterButtonVal");
        Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)this.getProfileTypes().toArray(), 8);
        profileTypeCriteria = profileTypeCriteria.and(this.selectQuery.getCriteria());
        final Criteria profileFilterButCriteria = this.getProfileFilterButtonCriteria(filterButtonVal);
        if (profileFilterButCriteria != null) {
            profileTypeCriteria = profileTypeCriteria.and(profileFilterButCriteria);
        }
        this.selectQuery.setCriteria(profileTypeCriteria);
        return this.selectQuery;
    }
    
    @Override
    protected JSONObject fetchResultObject() throws APIHTTPException {
        try {
            final Boolean isGroup = this.requestJson.optBoolean("isGroup");
            final Long customerId = this.requestJson.optLong("customerId");
            final String filterButtonVal = this.requestJson.optString("filterButtonVal");
            Boolean containsDeviceResource = false;
            Long[] groupIds = null;
            Long[] deviceIds = null;
            final JSONObject resultJson = new JSONObject();
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)this.selectQuery);
            ApiProfileListViewDataHandler.logger.log(Level.FINE, "Query formation for profile filtered values completed with result");
            final JSONArray yetToApplyArray = new JSONArray();
            final JSONArray successfullyAppliedArray = new JSONArray();
            final JSONArray updateAvailableArray = new JSONArray();
            while (dmDataSetWrapper.next()) {
                final int latestVer = (int)dmDataSetWrapper.getValue("PROFILE_VERSION");
                final int platform = (int)dmDataSetWrapper.getValue("PLATFORM_TYPE");
                final int scope = (int)dmDataSetWrapper.getValue("SCOPE");
                final Long collectionId = (Long)dmDataSetWrapper.getValue("COLLECTION_ID");
                final JSONObject configIDs = MDMConfigUtil.getConfiuguredPolicyInfo(collectionId);
                final Boolean hasWinEmailOrExchangeConfig = configIDs.has("602") || configIDs.has("603") || configIDs.has("606");
                final Boolean hasKioskConfig = ProfileUtil.getInstance().hasKioskConfigWithAutoDistributeAppsConfigured(platform, collectionId);
                final JSONObject profileObject = this.setBasicProfileValues(dmDataSetWrapper);
                profileObject.put("version", latestVer);
                final Properties userDataProperties = new Properties();
                ((Hashtable<String, Boolean>)userDataProperties).put("hasWinEmailOrExchangeConfig", hasWinEmailOrExchangeConfig);
                ((Hashtable<String, Boolean>)userDataProperties).put("hasKioskConfig", hasKioskConfig);
                if (isGroup) {
                    final JSONArray groupIdArray = this.requestJson.getJSONArray("groupIds");
                    final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
                    groupIds = groupIdList.toArray(new Long[groupIdList.size()]);
                    final List grpList = CustomGroupingHandler.getCustomGroupsList(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupIds, 8).and(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new Integer[] { 4, 3, 5, 6 }, 8)));
                    if (!grpList.isEmpty()) {
                        containsDeviceResource = true;
                    }
                }
                else {
                    final JSONArray deviceIdArray = this.requestJson.getJSONArray("deviceIds");
                    final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
                    deviceIds = deviceIdList.toArray(new Long[deviceIdList.size()]);
                    final List deviceList = ManagedDeviceHandler.getInstance().getDeviceResourceIDs(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)deviceIds, 8));
                    if (!deviceList.isEmpty()) {
                        containsDeviceResource = true;
                    }
                }
                if (scope == 2 && containsDeviceResource) {
                    profileObject.put("isEnabled", false);
                    ((Hashtable<String, String>)userDataProperties).put("remarks", I18N.getMsg("mdm.group.scope.user_profile", new Object[0]));
                    ((Hashtable<String, Boolean>)userDataProperties).put("insufficientLicense", true);
                }
                final JSONObject collectionInfo = new JSONObject();
                collectionInfo.put("collectionID", (Object)collectionId);
                collectionInfo.put("groupIDs", (Object)groupIds);
                collectionInfo.put("deviceIDs", (Object)deviceIds);
                collectionInfo.put("isGroup", (Object)isGroup);
                collectionInfo.put("customerID", (Object)customerId);
                final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(platform);
                handler.getCollectionSpecificUserProperties(collectionInfo, userDataProperties, new TreeNode());
                JSONObject userDataJsonObject = IdpsUtil.convertPropertiesToJSONObject(userDataProperties);
                profileObject.put("user_data", (Object)userDataJsonObject);
                if (userDataJsonObject.has("isEnabled")) {
                    profileObject.put("isEnabled", userDataJsonObject.getBoolean("isEnabled"));
                }
                if (dmDataSetWrapper.getValue("executed_profile_version") != null) {
                    final Integer execVersion = (Integer)dmDataSetWrapper.getValue("executed_profile_version");
                    ((Hashtable<String, Integer>)userDataProperties).put("executed_version", execVersion);
                    userDataJsonObject = IdpsUtil.convertPropertiesToJSONObject(userDataProperties);
                    profileObject.put("user_data", (Object)userDataJsonObject);
                    if (latestVer > execVersion) {
                        profileObject.put("isUpgrade", true);
                        updateAvailableArray.put((Object)profileObject);
                        yetToApplyArray.put((Object)profileObject);
                    }
                    successfullyAppliedArray.put((Object)profileObject);
                }
                else {
                    yetToApplyArray.put((Object)profileObject);
                }
            }
            if (filterButtonVal.equalsIgnoreCase("all") || filterButtonVal == "") {
                resultJson.put("yet_to_apply", (Object)yetToApplyArray);
            }
            if (filterButtonVal.equalsIgnoreCase("associated") || filterButtonVal == "") {
                resultJson.put("successfull_applied", (Object)successfullyAppliedArray);
            }
            resultJson.put("update_available", (Object)updateAvailableArray);
            return resultJson;
        }
        catch (final Exception ex) {
            ApiProfileListViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching filter data for profiles", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
