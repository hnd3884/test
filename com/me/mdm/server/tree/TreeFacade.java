package com.me.mdm.server.tree;

import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import com.me.mdm.api.paging.PagingUtil;
import java.util.logging.Level;
import com.me.mdm.server.tree.apidatahandler.ApiListViewDataHandler;
import org.json.JSONArray;
import java.util.HashSet;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import java.util.Map;
import java.util.List;
import com.me.mdm.server.customgroup.GroupFacade;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.ArrayList;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class TreeFacade
{
    protected static Logger logger;
    
    public JSONObject getGroupResourceFilteredValue(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject messageBody = message.getJSONObject("msg_body");
            final Integer filterType = Integer.valueOf(APIUtil.getResourceIDString(message, "tree_id"));
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
            final Long customerId = APIUtil.getCustomerID(message);
            final Long loginId = APIUtil.getLoginID(message);
            final Long userId = APIUtil.getUserID(message);
            final Boolean isgroup = message.optBoolean("is_group");
            JSONArray groupIdsArray = null;
            JSONArray deviceIdArray = null;
            final List userGroupIds = new ArrayList();
            if (isgroup) {
                groupIdsArray = messageBody.optJSONArray("group_ids");
                if (groupIdsArray == null) {
                    throw new APIHTTPException("COM0005", new Object[] { "group_ids" });
                }
                final List groupIds = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdsArray);
                final Map<Long, Integer> groupDetailsMap = new GroupFacade().validateAndGetMultiGroupDetails(groupIds, customerId, false);
                for (final Map.Entry<Long, Integer> entry : groupDetailsMap.entrySet()) {
                    if (entry.getValue().equals(7)) {
                        userGroupIds.add(entry.getKey());
                    }
                }
                if (filterType == 102) {
                    throw new APIHTTPException("COM0008", new Object[] { filterType });
                }
            }
            else {
                deviceIdArray = messageBody.optJSONArray("device_ids");
                if (deviceIdArray == null) {
                    throw new APIHTTPException("COM0005", new Object[] { "device_ids" });
                }
                final List deviceIds = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
                final HashMap<Integer, ArrayList> platformDeviceMap = new DeviceFacade().getProfilePLatformDeviceMap(deviceIds, customerId);
                messageBody.put("platform_type", (Object)JSONUtil.getInstance().convertListToJSONArray(new ArrayList(platformDeviceMap.keySet())));
            }
            final String filterButtonVal = messageBody.optString("filter_button_val");
            if (filterButtonVal.equalsIgnoreCase("")) {
                throw new APIHTTPException("COM0005", new Object[] { "filter_button_val" });
            }
            final Set platformSet = new HashSet(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.optJSONArray("platform_type")));
            if (platformSet.contains(6L) || platformSet.contains(7L)) {
                platformSet.add(1);
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("isGroup", (Object)isgroup);
            jsonObject.put("customerId", (Object)customerId);
            jsonObject.put("loginId", (Object)loginId);
            jsonObject.put("userId", (Object)userId);
            jsonObject.put("searchValue", (Object)messageBody.optString("search"));
            jsonObject.put("startIndex", pagingUtil.getStartIndex());
            jsonObject.put("noOfObj", pagingUtil.getLimit());
            jsonObject.put("filterButtonVal", (Object)messageBody.optString("filter_button_val"));
            jsonObject.put("selectAll", messageBody.optBoolean("select_all"));
            jsonObject.put("groupIds", (Object)groupIdsArray);
            jsonObject.put("deviceIds", (Object)deviceIdArray);
            jsonObject.put("platform", (Object)new JSONArray((Collection)platformSet));
            jsonObject.put("tagType", (Object)messageBody.optJSONArray("tag_type"));
            jsonObject.put("businessstore_id", messageBody.optLong("businessstore_id"));
            jsonObject.put("docType", (Object)messageBody.optJSONArray("doc_type"));
            jsonObject.put("appType", (Object)messageBody.optJSONArray("app_type"));
            jsonObject.put("licenseType", (Object)messageBody.optJSONArray("license_type"));
            jsonObject.put("categoryType", (Object)messageBody.optJSONArray("category_type"));
            jsonObject.put("unassignedTag", messageBody.optBoolean("unassigned_tag"));
            if (filterType == 3 && !userGroupIds.isEmpty()) {
                final String remark = "user group ids:" + APIUtil.getCommaSeperatedString(userGroupIds);
                throw new APIHTTPException("COM0015", new Object[] { remark });
            }
            final JSONObject resultObject = ApiListViewDataHandler.getInstance(filterType).getFilterValues(jsonObject, pagingUtil);
            return resultObject;
        }
        catch (final Exception ex) {
            TreeFacade.logger.log(Level.SEVERE, "exception in getting profile for distribution", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        TreeFacade.logger = Logger.getLogger("MDMApiLogger");
    }
}
