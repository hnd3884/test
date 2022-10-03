package com.me.mdm.server.settings.location;

import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.persistence.DataObject;
import java.util.Date;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Range;
import org.json.JSONArray;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class GeoLocationFacade
{
    private Logger logger;
    private long weekInMilli;
    
    public GeoLocationFacade() {
        this.logger = Logger.getLogger(GeoLocationFacade.class.getName());
        this.weekInMilli = 604800000L;
    }
    
    public JSONObject getLocations(final JSONObject jsonObject) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        Connection conn = null;
        DataSet ds = null;
        try {
            final int locationTrackingStatus = LocationSettingsDataHandler.getInstance().getLocationTrackingStatus(APIUtil.getCustomerID(jsonObject));
            final boolean isLostModeOnly = locationTrackingStatus == 2;
            final boolean selectAll = APIUtil.getBooleanFilter(jsonObject, "select_all");
            if (locationTrackingStatus == 3) {
                throw new APIHTTPException("LOC0001", new Object[] { "Location tracking is disabled in geolocation settings." });
            }
            final String device_name = APIUtil.getStringFilter(jsonObject, "device_name");
            Long groupID = APIUtil.getLongFilter(jsonObject, "group_id");
            final Long userID = APIUtil.getLongFilter(jsonObject, "managed_user_id");
            final Boolean isLostModeFilter = APIUtil.getBooleanFilter(jsonObject, "is_lost");
            if (groupID == -1L) {
                groupID = null;
            }
            final SelectQuery locationQuery = MDMGeoLocationHandler.getInstance().getDeviceLocationsDetailsQuery(APIUtil.getCustomerID(jsonObject), groupID, device_name, null);
            Criteria cri = locationQuery.getCriteria();
            final Criteria locStatusCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)true, 0);
            final Criteria lostModeCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8);
            if (cri != null) {
                locationQuery.setCriteria(cri.and(locStatusCri.or(lostModeCriteria)));
            }
            else {
                locationQuery.setCriteria(locStatusCri.or(lostModeCriteria));
            }
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(jsonObject);
            final SelectQuery locationCountQuery = MDMGeoLocationHandler.getInstance().getDeviceLocationsDetailsQuery(APIUtil.getCustomerID(jsonObject), groupID, device_name, null);
            cri = locationCountQuery.getCriteria();
            if (cri != null) {
                locationQuery.setCriteria(cri.and(locStatusCri.or(lostModeCriteria)));
            }
            else {
                locationQuery.setCriteria(locStatusCri.or(lostModeCriteria));
            }
            final ArrayList groupByColList = new ArrayList();
            ArrayList selectColList = (ArrayList)locationCountQuery.getSelectColumns();
            for (final Object col : selectColList) {
                locationCountQuery.removeSelectColumn((Column)col);
            }
            final ArrayList sortColList = (ArrayList)locationCountQuery.getSortColumns();
            for (final Object col2 : sortColList) {
                locationCountQuery.removeSortColumn((SortColumn)col2);
            }
            if (isLostModeFilter) {
                Criteria locationQueryCriteria = locationQuery.getCriteria();
                if (locationQueryCriteria != null) {
                    locationQueryCriteria = locationQueryCriteria.and(lostModeCriteria);
                    locationQuery.setCriteria(locationQueryCriteria);
                }
                else {
                    locationQuery.setCriteria(lostModeCriteria);
                }
            }
            Column countColumn = new Column("ManagedDevice", "RESOURCE_ID");
            countColumn = countColumn.distinct();
            countColumn = countColumn.count();
            countColumn.setColumnAlias("COUNT");
            locationCountQuery.addSelectColumn(countColumn);
            groupByColList.add(new Column("Resource", "CUSTOMER_ID"));
            final GroupByClause groupByClause = new GroupByClause((List)groupByColList);
            locationCountQuery.setGroupByClause(groupByClause);
            locationCountQuery.addSelectColumns((List)groupByColList);
            final int count = DBUtil.getRecordCount(locationCountQuery);
            selectColList = (ArrayList)locationQuery.getSelectColumns();
            for (final Object col3 : selectColList) {
                locationQuery.removeSelectColumn((Column)col3);
            }
            locationQuery.addJoin(new Join("ManagedDevice", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            locationQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME", "located_time"));
            locationQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME", "added_time"));
            locationQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE", "longitude"));
            locationQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE", "latitude"));
            locationQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID", "device_id"));
            locationQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE", "platform_type"));
            final Column deviceNameCol = Column.getColumn("ManagedDeviceExtn", "NAME", "device_name");
            locationQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE", "model_type"));
            locationQuery.addSelectColumn(deviceNameCol);
            locationQuery.addSelectColumn(Column.getColumn("UserResource", "NAME", "user_name"));
            locationQuery.addSelectColumn(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS", "tracking_status"));
            final JSONArray locations = new JSONArray();
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                if (!selectAll) {
                    locationQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                    final SortColumn sortColumn = new SortColumn("MdDeviceLocationDetails", "LOCATED_TIME", false);
                    locationQuery.addSortColumn(sortColumn);
                }
                locationQuery.removeSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                if (!selectAll) {
                    ds = relapi.executeQuery((Query)locationQuery, conn);
                    while (ds.next()) {
                        final JSONObject location = APIUtil.getNewInstance().getJSONObjectFromDS(ds, locationQuery);
                        locations.put((Object)location);
                    }
                }
                else {
                    for (int i = 0; i <= count / 500; ++i) {
                        locationQuery.setRange(new Range((i * 500 == 0) ? 0 : (i * 500 + 1), 500));
                        locationQuery.addSortColumn(new SortColumn("MdDeviceLocationDetails", "LOCATED_TIME", false));
                        ds = relapi.executeQuery((Query)locationQuery, conn);
                        while (ds.next()) {
                            final JSONObject location2 = APIUtil.getNewInstance().getJSONObjectFromDS(ds, locationQuery);
                            locations.put((Object)location2);
                        }
                    }
                }
            }
            response.put("device_locations", (Object)locations);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in GeoLocationFacade", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            APIUtil.closeConnection(conn, ds);
        }
    }
    
    public SelectQuery getLocationHistoryBaseQuery(final Long deviceID, final List groupIDs, final String sStartDate, final String sEndDate, final Long customerId) throws Exception {
        try {
            Long startDate = null;
            Long endDate = null;
            if (sStartDate != null) {
                startDate = MDMUtil.getInstance().convertDateToMillis(sStartDate);
            }
            if (sEndDate != null) {
                endDate = MDMUtil.getInstance().convertDateToMillis(sEndDate) + 86400000L;
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceLocationDetails", new String[] { "RESOURCE_ID" }, new String[] { "DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("MdDeviceLocationDetails", "Resource", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria crit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            crit = crit.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            selectQuery.setCriteria(crit);
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), true));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), true));
            if (deviceID != null) {
                Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceID, 0);
                cri = ((selectQuery.getCriteria() == null) ? cri : selectQuery.getCriteria().and(cri));
                selectQuery.setCriteria(cri);
            }
            if (groupIDs != null) {
                selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                final Criteria deviceJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
                final Criteria userJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), 0);
                selectQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", deviceJoinCri.or(userJoinCri), 2));
                Criteria cri2 = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupIDs, 8);
                cri2 = ((selectQuery.getCriteria() == null) ? cri2 : selectQuery.getCriteria().and(cri2));
                selectQuery.setCriteria(cri2);
            }
            if (startDate != null) {
                Criteria cri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)startDate, 4);
                cri = ((selectQuery.getCriteria() == null) ? cri : selectQuery.getCriteria().and(cri));
                selectQuery.setCriteria(cri);
            }
            if (endDate != null) {
                Criteria cri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)endDate, 7);
                cri = ((selectQuery.getCriteria() == null) ? cri : selectQuery.getCriteria().and(cri));
                selectQuery.setCriteria(cri);
            }
            return selectQuery;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error while getting query in getLocationHistoryBaseQuery()----", e);
            throw e;
        }
    }
    
    public ArrayList getLocationHistoryIds(final Long deviceID, final List groupIDs, final String sStartDate, final String sEndDate, final String sStartTime, final String sEndTime, final String intervalInMin, final Long customerId) {
        final ArrayList locationIds = new ArrayList();
        try {
            final SelectQuery selectQuery = this.getLocationHistoryBaseQuery(deviceID, groupIDs, sStartDate, sEndDate, customerId);
            final Calendar cal = Calendar.getInstance(ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone());
            int startTime = 0;
            int endTime = 24;
            if (sStartTime != null && sEndTime != null) {
                startTime = Integer.valueOf(sStartTime);
                endTime = Integer.valueOf(sEndTime);
            }
            long lastLocatedTime = 0L;
            long lastLocatedDevice = 0L;
            long interval = 0L;
            if (intervalInMin != null) {
                interval = Integer.valueOf(intervalInMin) * 60000;
            }
            int startIndex = 0;
            DataObject locationDetailsDO;
            do {
                selectQuery.setRange(new Range(startIndex, 5000));
                startIndex += 5000;
                locationDetailsDO = DataAccess.get(selectQuery);
                final Iterator rows = locationDetailsDO.getRows("MdDeviceLocationDetails");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final long locatedTimeInMilli = (long)row.get("LOCATED_TIME");
                    final long locatedDeviceId = (long)row.get("DEVICE_ID");
                    if (locatedDeviceId != lastLocatedDevice) {
                        lastLocatedDevice = locatedDeviceId;
                        lastLocatedTime = 0L;
                    }
                    final Date locatedTime = new Date(locatedTimeInMilli);
                    cal.setTime(locatedTime);
                    final int hour = cal.get(11);
                    if (hour >= startTime) {
                        if (hour >= endTime) {
                            continue;
                        }
                        if (lastLocatedTime != 0L && locatedTimeInMilli - lastLocatedTime <= interval) {
                            continue;
                        }
                        locationIds.add((long)row.get("LOCATION_DETAIL_ID"));
                        lastLocatedTime = locatedTimeInMilli;
                    }
                }
            } while (!locationDetailsDO.isEmpty());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error while getting query in getLocationHistoryIds()----", e);
        }
        return locationIds;
    }
    
    public SelectQuery getLocationHistoryDetailsQuery(final Long customerId) {
        SelectQuery selectQuery = null;
        try {
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            final Criteria deviceJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
            final Criteria userJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), 0);
            selectQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", deviceJoinCri.or(userJoinCri), 1));
            final Criteria commonGrpCrit = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new int[] { 9, 8 }, 9);
            final Criteria groupJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)Column.getColumn("CustomGroup", "RESOURCE_ID"), 0);
            selectQuery.addJoin(new Join("CustomGroupMemberRel", "CustomGroup", commonGrpCrit.and(groupJoinCri), 1));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceLocationToErrCode", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "LocationDeviceStatus", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "DeviceRecentLocation", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceRecentLocation", "MdDeviceLocationDetails", new String[] { "LOCATION_DETAIL_ID" }, new String[] { "LOCATION_DETAIL_ID" }, 1));
            selectQuery.addJoin(new Join("ManagedDevice", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            final Criteria appJoinCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)Column.getColumn("MdAppDetails", "APP_ID"), 0);
            final Criteria identifierCri = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppDetails", appJoinCri.and(identifierCri), 1));
            selectQuery.addJoin(new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, "CustomGroup", "GROUP_RESOURCE", 1));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "USER_RESOURCE", 1));
            selectQuery.addJoin(new Join("Resource", "LostModeTrackInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            Criteria crit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            crit = crit.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            if (selectQuery.getCriteria() != null) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(crit));
            }
            else {
                selectQuery.setCriteria(crit);
            }
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GROUP_RESOURCE", "NAME", "GROUP_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("GROUP_RESOURCE", "RESOURCE_ID", "GROUP_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"));
            selectQuery.addSelectColumn(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME", "USER_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "RESOURCE_ID", "USER_RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"), true));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error while getting query in getLocationHistoryDetailsQuery()----", e);
        }
        return selectQuery;
    }
    
    public String getGeoStatus(final String errorCode, final boolean locationEnabled, final long deviceId, final int platformTypeVal, final long lastLocatedTimeInMilli) {
        String status = null;
        try {
            if (!locationEnabled) {
                status = I18N.getMsg("mdm.agent.efrp.not_applicable", new Object[0]);
            }
            else if (errorCode != null) {
                if (errorCode.equalsIgnoreCase("12142")) {
                    status = I18N.getMsg("mdm.location.geostatus.limited", new Object[0]);
                }
                else if (errorCode.equalsIgnoreCase("12141")) {
                    status = I18N.getMsg("mdm.location.geostatus.permission_denied", new Object[0]);
                }
                else if (errorCode.equalsIgnoreCase("12135") || errorCode.equalsIgnoreCase("12136") || errorCode.equalsIgnoreCase("12137")) {
                    status = I18N.getMsg("mdm.location.geostatus.location_disabled", new Object[0]);
                }
                else {
                    status = I18N.getMsg("dir.status.failed", new Object[0]);
                }
            }
            else {
                if (platformTypeVal == 1) {
                    Integer appInstallStatus = null;
                    final boolean isNativeAppInstalled = new IOSAppUtils().isNativeAppInstalledInDevice(deviceId, MDMUtil.getInstance().isMacDevice(deviceId));
                    if (isNativeAppInstalled) {
                        appInstallStatus = 2;
                    }
                    if (appInstallStatus == null || appInstallStatus != 2) {
                        status = I18N.getMsg("mdm.location.geostatus.missing_app", new Object[0]);
                        return status;
                    }
                    if (!IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(deviceId)) {
                        status = I18N.getMsg("mdm.location.geostatus.app_not_activated", new Object[0]);
                        return status;
                    }
                }
                if (lastLocatedTimeInMilli > DateTimeUtil.determine_From_To_Times("today").get("date2") - this.weekInMilli) {
                    status = I18N.getMsg("mdm.location.geostatus.active", new Object[0]);
                }
                else {
                    status = I18N.getMsg("dc.mdm.knox.container.status.inactive", new Object[0]);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getGeoStatus()-----", e);
        }
        return status;
    }
    
    public String getAssociatedGroupNames(final DataObject locationDetailsDO, final long userId, final long deviceId) {
        String groupNames = "";
        final Criteria memberCrit = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceId, 0).or(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)userId, 0));
        try {
            final Iterator groupRows = locationDetailsDO.getRows("CustomGroupMemberRel", memberCrit);
            while (groupRows.hasNext()) {
                final Row row = groupRows.next();
                final long groupId = (long)row.get("GROUP_RESOURCE_ID");
                final String nextGroup = (String)locationDetailsDO.getRow("GROUP_RESOURCE", new Criteria(Column.getColumn("GROUP_RESOURCE", "RESOURCE_ID"), (Object)groupId, 0)).get("NAME");
                if (!groupNames.equalsIgnoreCase("") && !nextGroup.equalsIgnoreCase("")) {
                    groupNames += ",";
                }
                groupNames += nextGroup;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in getAssociatedGroupNames()-----", e);
        }
        if (groupNames.equalsIgnoreCase("")) {
            groupNames = "--";
        }
        return groupNames;
    }
    
    public Criteria getGeoStatusCrit(final int geoStatus, final boolean hasRecentLocationDetails) {
        String locationDetailsTable = "MdDeviceLocationDetails";
        if (hasRecentLocationDetails) {
            locationDetailsTable = "RecentLocationDetails";
        }
        Criteria geoStatusCri = null;
        try {
            Criteria geoTrackingEnabledCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)true, 0);
            final Criteria lostModeCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8);
            geoTrackingEnabledCri = geoTrackingEnabledCri.or(lostModeCriteria);
            final Criteria appStatusID = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)null, 0).or(new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)2, 1));
            final Criteria iosCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria missingAppCri = iosCri.and(appStatusID);
            final Criteria appNotRegisteredCri = iosCri.and(new Criteria(Column.getColumn("IOSNativeAppStatus", "INSTALLATION_STATUS"), (Object)0, 0));
            switch (geoStatus) {
                case 1: {
                    geoStatusCri = new Criteria(Column.getColumn("LocationDeviceStatus", "IS_ENABLED"), (Object)false, 0).and(lostModeCriteria.negate().or(new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)null, 0)));
                    break;
                }
                case 2: {
                    geoStatusCri = geoTrackingEnabledCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)12142, 0);
                    break;
                }
                case 3: {
                    geoStatusCri = geoTrackingEnabledCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)12141, 0);
                    break;
                }
                case 4: {
                    geoStatusCri = geoTrackingEnabledCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)new int[] { 12135, 12136, 12137 }, 8);
                    break;
                }
                case 5: {
                    geoStatusCri = geoTrackingEnabledCri.and(missingAppCri);
                    break;
                }
                case 6: {
                    geoStatusCri = geoTrackingEnabledCri.and(appNotRegisteredCri);
                    break;
                }
                case 7: {
                    geoStatusCri = geoTrackingEnabledCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)null, 0);
                    geoStatusCri = geoStatusCri.and(new Criteria(Column.getColumn(locationDetailsTable, "LOCATED_TIME"), (Object)(DateTimeUtil.determine_From_To_Times("today").get("date2") - this.weekInMilli), 4));
                    geoStatusCri = geoStatusCri.and(iosCri.negate().or(new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)2, 0).and(new Criteria(Column.getColumn("IOSNativeAppStatus", "INSTALLATION_STATUS"), (Object)0, 1))));
                    break;
                }
                case 8: {
                    geoStatusCri = geoTrackingEnabledCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)null, 0);
                    geoStatusCri = geoStatusCri.and(new Criteria(Column.getColumn(locationDetailsTable, "LOCATED_TIME"), (Object)(DateTimeUtil.determine_From_To_Times("today").get("date2") - this.weekInMilli), 7).or(new Criteria(Column.getColumn(locationDetailsTable, "LOCATED_TIME"), (Object)null, 0)));
                    geoStatusCri = geoStatusCri.and(iosCri.negate().or(new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)2, 0).and(new Criteria(Column.getColumn("IOSNativeAppStatus", "INSTALLATION_STATUS"), (Object)0, 1))));
                    break;
                }
                case 9: {
                    geoStatusCri = geoTrackingEnabledCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)new int[] { 12135, 12136, 12137, 12142, 12141 }, 9);
                    geoStatusCri = geoStatusCri.and(Column.getColumn("MdDeviceLocationToErrCode", "ERROR_CODE"), (Object)null, 1);
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error in geoStatusCrit()-----", e);
        }
        return geoStatusCri;
    }
}
