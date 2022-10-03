package com.me.mdm.server.apps.blocklist.service;

import com.me.mdm.server.apps.blocklist.model.BlocklistedAppDetailsByDevice;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.device.api.model.UserModel;
import com.me.mdm.server.device.api.model.DeviceDetailsModel;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import org.json.JSONObject;
import com.me.mdm.api.paging.model.PagingResponse;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.Hashtable;
import com.me.mdm.api.model.GroupDetails;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.server.device.api.model.MetaDataModel;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import java.util.HashMap;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.blocklist.model.BlocklistResponsePOJO;
import com.me.mdm.server.apps.blocklist.model.BlocklistPOJO;
import java.util.logging.Logger;

public class BlocklistServices
{
    protected static Logger logger;
    
    public BlocklistResponsePOJO getGroupTreeDataForAppGroups(final BlocklistPOJO params, final int type) throws APIHTTPException {
        try {
            final List<Long> apps = params.getAppGroupIds();
            if (apps == null || apps.size() < 1) {
                BlocklistServices.logger.log(Level.SEVERE, "App size is null.Atleast one app must be selected");
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            BlocklistServices.logger.log(Level.INFO, "Entering Group Tree for Blocklist and fetching from pojo");
            final Long customerID = params.getCustomerId();
            final String search = params.getSearchField();
            final HashMap hashMap = new HashMap();
            hashMap.put("cid", customerID);
            if (search != null) {
                hashMap.put("search", search);
            }
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < apps.size(); ++i) {
                sb.append(apps.get(i).toString()).append(",");
            }
            hashMap.put("appGroupIds", sb.deleteCharAt(sb.length() - 1).toString());
            hashMap.put("type", type);
            final String groupType = params.getGroupType();
            hashMap.put("groups", groupType);
            BlocklistServices.logger.log(Level.INFO, "Entering group Tree and fetching for app_group_ids {0}", new Object[] { sb });
            final SelectQuery groupViewQuery = BlacklistQueryUtils.getInstance().getGroupTreeForAppGroups(hashMap);
            final PagingUtil pagingUtil = params.getPagingUtil();
            hashMap.put("count", Boolean.TRUE);
            final SelectQuery groupViewCountQuery = BlacklistQueryUtils.getInstance().getGroupTreeForAppGroups(hashMap);
            final int count = MDMDBUtil.getRecordCount(groupViewCountQuery);
            final BlocklistResponsePOJO blocklistResponsePOJO = new BlocklistResponsePOJO();
            final MetaDataModel meta = new MetaDataModel();
            meta.setTotalCount(count);
            blocklistResponsePOJO.setMetadata(meta);
            final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(params.getRequestUri());
            if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
                blocklistResponsePOJO.setDeltaToken(newDeltaTokenUtil.getDeltaToken());
            }
            if (count != 0) {
                if (!params.isSelectAll()) {
                    final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
                    if (pagingJSON != null) {
                        blocklistResponsePOJO.setPaging(pagingJSON);
                    }
                    groupViewQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                }
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby") && String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("devicename")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    groupViewQuery.addSortColumn(new SortColumn("Resource", "NAME", (boolean)isSortOrderASC));
                }
                else {
                    groupViewQuery.addSortColumn(new SortColumn("Resource", "RESOURCE_ID", false));
                }
            }
            final List customGroupsList = CustomGroupingHandler.getCustomGroupDetailsList(groupViewQuery);
            final Iterator groupItr = customGroupsList.iterator();
            String resourceName = "";
            final List<GroupDetails> groupDetailsList = new ArrayList<GroupDetails>();
            while (groupItr.hasNext()) {
                final Hashtable groupDetails = groupItr.next();
                final GroupDetails blocklistGroupDetails = new GroupDetails();
                final Long groupID = groupDetails.get("CUSTOM_GP_ID");
                blocklistGroupDetails.setGroupID(groupID);
                resourceName = groupDetails.get("CUSTOM_GP_NAME");
                blocklistGroupDetails.setName(resourceName);
                final Integer grpType = groupDetails.get("CUSTOM_GP_TYPE");
                blocklistGroupDetails.setGroupType(grpType);
                final Integer grpMemCnt = groupDetails.get("CUSTOM_GP_MEMBER_COUNT");
                blocklistGroupDetails.setMemberCount(grpMemCnt);
                groupDetailsList.add(blocklistGroupDetails);
            }
            blocklistResponsePOJO.setGroups(groupDetailsList);
            return blocklistResponsePOJO;
        }
        catch (final Exception e) {
            BlocklistServices.logger.log(Level.SEVERE, "exception occurred in getGroupTreeDataForAppGroups", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public BlocklistResponsePOJO getDeviceTreeDataForAppGroups(final BlocklistPOJO params, final int type) throws APIHTTPException {
        try {
            final List<Long> apps = params.getAppGroupIds();
            if (apps == null || apps.size() < 1) {
                BlocklistServices.logger.log(Level.SEVERE, "App size is null.Atleast one app must be selected");
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            BlocklistServices.logger.log(Level.INFO, "Entering device Tree for Blocklist and fetching from pojo");
            final Long customerID = params.getCustomerId();
            final String search = params.getSearchField();
            final HashMap hashMap = new HashMap();
            hashMap.put("cid", customerID);
            if (search != null) {
                hashMap.put("search", search);
            }
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < apps.size(); ++i) {
                sb.append(apps.get(i).toString()).append(",");
            }
            hashMap.put("appGroupIds", sb.deleteCharAt(sb.length() - 1).toString());
            hashMap.put("type", type);
            final String platform = params.getPlatform();
            if (!MDMStringUtils.isEmpty(platform)) {
                final String[] platformTypes = platform.split(",");
                final ArrayList<Integer> values = new ArrayList<Integer>();
                for (int j = 0; j < platformTypes.length; ++j) {
                    final int temp = Integer.parseInt(platformTypes[j]);
                    if (temp == 2 || temp == 1 || temp == 3 || temp == 4) {
                        values.add(temp);
                    }
                }
                hashMap.put("platform", values);
            }
            BlocklistServices.logger.log(Level.INFO, "Entering device Tree and fetching for app_group_ids {0}", new Object[] { sb });
            final SelectQuery deviceViewQuery = BlacklistQueryUtils.getInstance().getResourceTreeForAppGroups(hashMap);
            final PagingUtil pagingUtil = params.getPagingUtil();
            hashMap.put("count", Boolean.TRUE);
            final SelectQuery deviceViewCountQuery = BlacklistQueryUtils.getInstance().getResourceTreeForAppGroups(hashMap);
            final int count = DBUtil.getRecordCount(deviceViewCountQuery);
            final BlocklistResponsePOJO blocklistResponsePOJO = new BlocklistResponsePOJO();
            final MetaDataModel meta = new MetaDataModel();
            meta.setTotalCount(count);
            blocklistResponsePOJO.setMetadata(meta);
            final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(params.getRequestUri());
            if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
                blocklistResponsePOJO.setDeltaToken(newDeltaTokenUtil.getDeltaToken());
            }
            if (count != 0) {
                if (!params.isSelectAll()) {
                    final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
                    if (pagingJSON != null) {
                        blocklistResponsePOJO.setPaging(pagingJSON);
                    }
                    deviceViewQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                }
                final JSONObject orderByJSON = pagingUtil.getOrderByJSON();
                if (orderByJSON != null && orderByJSON.has("orderby") && String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("devicename")) {
                    final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                    deviceViewQuery.addSortColumn(new SortColumn("ManagedDeviceExtn", "NAME", (boolean)isSortOrderASC));
                }
                else {
                    deviceViewQuery.addSortColumn(new SortColumn("ManagedDeviceExtn", "NAME", false));
                }
            }
            final ArrayList deviceDetailsList = ManagedDeviceHandler.getInstance().getManagedDeviceDetailslist(deviceViewQuery);
            final Iterator deviceItr = deviceDetailsList.iterator();
            final List<DeviceDetailsModel> blocklistDeviceDetailsList = new ArrayList<DeviceDetailsModel>();
            String resourceName2 = "";
            String userName = "";
            while (deviceItr.hasNext()) {
                final HashMap managedDeviceDetails = deviceItr.next();
                final DeviceDetailsModel blocklistDeviceDetails = new DeviceDetailsModel();
                blocklistDeviceDetails.setResourceId(managedDeviceDetails.get("RESOURCE_ID"));
                resourceName2 = managedDeviceDetails.get("NAME");
                userName = managedDeviceDetails.get("USER_RESOURCE_NAME");
                blocklistDeviceDetails.setDeviceName(resourceName2);
                final int platformType = managedDeviceDetails.get("PLATFORM_TYPE");
                blocklistDeviceDetails.setPlatformType(platformType);
                final UserModel userModel = new UserModel();
                userModel.setUserName(userName);
                blocklistDeviceDetails.setUser(userModel);
                blocklistDeviceDetailsList.add(blocklistDeviceDetails);
            }
            blocklistResponsePOJO.setDevices(blocklistDeviceDetailsList);
            return blocklistResponsePOJO;
        }
        catch (final Exception e) {
            BlocklistServices.logger.log(Level.SEVERE, "exception occurred in getDeviceTreeDataForAppGroups", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public BlocklistResponsePOJO getAppsBlockedByDevice(final Long customerId, final Long deviceId, final BlocklistPOJO params) throws Exception, APIHTTPException {
        BlocklistServices.logger.log(Level.INFO, "Entering get Apps Blocked By device with deviceID {0} and fetching app view Query", new Object[] { deviceId });
        final SelectQuery appViewQuery = BlacklistQueryUtils.getInstance().blocklistedAppsByDeviceSelectQuery(customerId, deviceId, false);
        BlocklistServices.logger.log(Level.INFO, "Fetching app view count Query");
        final SelectQuery appViewCountQuery = BlacklistQueryUtils.getInstance().blocklistedAppsByDeviceSelectQuery(customerId, deviceId, true);
        final PagingUtil pagingUtil = params.getPagingUtil();
        final int count = DBUtil.getRecordCount(appViewCountQuery);
        final BlocklistResponsePOJO blocklistResponsePOJO = new BlocklistResponsePOJO();
        final MetaDataModel meta = new MetaDataModel();
        meta.setTotalCount(count);
        blocklistResponsePOJO.setMetadata(meta);
        final DeltaTokenUtil newDeltaTokenUtil = new DeltaTokenUtil(params.getRequestUri());
        if (pagingUtil.getNextToken(count) == null || pagingUtil.getPreviousToken() == null) {
            blocklistResponsePOJO.setDeltaToken(newDeltaTokenUtil.getDeltaToken());
        }
        if (count != 0) {
            if (!params.isSelectAll()) {
                final PagingResponse pagingJSON = pagingUtil.getPagingResponse(count);
                if (pagingJSON != null) {
                    blocklistResponsePOJO.setPaging(pagingJSON);
                }
                appViewQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
            }
            JSONObject orderByJSON = null;
            try {
                orderByJSON = pagingUtil.getOrderByJSON();
            }
            catch (final Exception ex) {}
            if (orderByJSON != null && orderByJSON.has("orderby") && String.valueOf(orderByJSON.get("orderby")).equalsIgnoreCase("appname")) {
                final Boolean isSortOrderASC = String.valueOf(orderByJSON.get("sortorder")).equals("asc");
                appViewQuery.addSortColumn(new SortColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME", (boolean)isSortOrderASC));
            }
            else {
                appViewQuery.addSortColumn(new SortColumn("MdAppGroupDetails", "APP_GROUP_ID", false));
            }
        }
        final String search = params.getSearchField();
        if (search != null) {
            final Criteria searchCri = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)search, 12, false);
            appViewQuery.setCriteria(searchCri);
        }
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)appViewQuery);
            if (dmDataSetWrapper != null) {
                final List<BlocklistedAppDetailsByDevice> blocklistedAppDetailsByDeviceList = new ArrayList<BlocklistedAppDetailsByDevice>();
                while (dmDataSetWrapper.next()) {
                    final BlocklistedAppDetailsByDevice blocklistedAppDetailsByDevice = new BlocklistedAppDetailsByDevice();
                    blocklistedAppDetailsByDevice.setIdentifier(dmDataSetWrapper.getValue("IDENTIFIER").toString());
                    blocklistedAppDetailsByDevice.setPlatformType((int)dmDataSetWrapper.getValue("PLATFORM_TYPE"));
                    blocklistedAppDetailsByDevice.setGroupName(dmDataSetWrapper.getValue("GROUP_DISPLAY_NAME").toString());
                    blocklistedAppDetailsByDevice.setStatus((int)dmDataSetWrapper.getValue("STATUS"));
                    blocklistedAppDetailsByDevice.setScope((int)dmDataSetWrapper.getValue("SCOPE"));
                    blocklistedAppDetailsByDevice.setAppGroupID((Long)dmDataSetWrapper.getValue("APP_GROUP_ID"));
                    if (dmDataSetWrapper.getValue("DISPLAY_IMAGE_LOC") != null) {
                        blocklistedAppDetailsByDevice.setImageUrl(dmDataSetWrapper.getValue("DISPLAY_IMAGE_LOC").toString());
                    }
                    blocklistedAppDetailsByDeviceList.add(blocklistedAppDetailsByDevice);
                }
                blocklistResponsePOJO.setApps(blocklistedAppDetailsByDeviceList);
            }
            return blocklistResponsePOJO;
        }
        catch (final Exception var11) {
            BlocklistServices.logger.log(Level.SEVERE, "Issue on getting blacklisted apps on device", var11);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        BlocklistServices.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
