package com.adventnet.sym.webclient.mdm.reports;

import com.adventnet.sym.webclient.mdm.encryption.ios.MDMDeviceRecentUserViewHandler;
import com.adventnet.ds.query.Table;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.settings.location.GeoLocationFacade;
import org.json.JSONArray;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.reports.ReportUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class ReportsCommonTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public ReportsCommonTRAction() {
        this.logger = Logger.getLogger(ReportsCommonTRAction.class.getName());
    }
    
    @Override
    public void setCriteria(SelectQuery query, final ViewContext viewCtx) {
        try {
            final String unique = viewCtx.getUniqueId();
            Criteria crit = query.getCriteria();
            this.setReportParameters(viewCtx);
            if (unique.equalsIgnoreCase("DeviceByModel")) {
                final String sProduct = this.getStateValue(viewCtx, "product");
                final String sModel = this.getStateValue(viewCtx, "model");
                final String platform = this.getStateValue(viewCtx, "platform");
                final Long customerID = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final ArrayList arrProductNames = ReportUtil.getInstance().getAllProductNames(customerID);
                viewCtx.getRequest().setAttribute("productnames", (Object)arrProductNames);
                this.setMDMGroupFilter(viewCtx);
                if (sProduct != null && !sProduct.equalsIgnoreCase("all")) {
                    viewCtx.getRequest().setAttribute("product", (Object)sProduct);
                    final Criteria criteria = new Criteria(new Column("MdModelInfo", "MODEL_NAME"), (Object)sProduct, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria);
                    }
                    else {
                        crit = criteria;
                    }
                }
                if (sModel != null && !sModel.equalsIgnoreCase("all")) {
                    viewCtx.getRequest().setAttribute("model", (Object)sModel);
                    final Criteria criteria = new Criteria(new Column("MdModelInfo", "MODEL"), (Object)sModel, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria);
                    }
                    else {
                        crit = criteria;
                    }
                }
                if (platform != null && !platform.equalsIgnoreCase("0") && !platform.equalsIgnoreCase("all")) {
                    final Criteria cPlatform = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform);
                    }
                    else {
                        crit = cPlatform;
                    }
                }
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
            }
            else if (unique.equalsIgnoreCase("DeviceByPasscode")) {
                final String sPasscodePresent = this.getStateValue(viewCtx, "present");
                final String sPasscodeCompliant = this.getStateValue(viewCtx, "compliant");
                final String sPasscodeCompliantProfile = this.getStateValue(viewCtx, "profile");
                final String platform2 = this.getStateValue(viewCtx, "platform");
                if (sPasscodePresent != null && sPasscodePresent.equalsIgnoreCase("true")) {
                    final Criteria criteria2 = new Criteria(new Column("MdSecurityInfo", "PASSCODE_PRESENT"), (Object)sPasscodePresent, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria2);
                    }
                    else {
                        crit = criteria2;
                    }
                }
                if (sPasscodeCompliant != null && sPasscodeCompliant.equalsIgnoreCase("true")) {
                    final Criteria criteria2 = new Criteria(new Column("MdSecurityInfo", "PASSCODE_COMPLAINT"), (Object)sPasscodeCompliant, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria2);
                    }
                    else {
                        crit = criteria2;
                    }
                }
                if (sPasscodeCompliantProfile != null && sPasscodeCompliantProfile.equalsIgnoreCase("true")) {
                    final Criteria criteria2 = new Criteria(new Column("MdSecurityInfo", "PASSCODE_COMPLAINT_PROFILES"), (Object)sPasscodeCompliantProfile, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria2);
                    }
                    else {
                        crit = criteria2;
                    }
                }
                if (platform2 != null && !platform2.equalsIgnoreCase("0") && !platform2.equalsIgnoreCase("all")) {
                    final Criteria cPlatform2 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform2), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform2);
                    }
                    else {
                        crit = cPlatform2;
                    }
                }
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
            }
            else if (unique.equalsIgnoreCase("DeviceLocationList") || unique.equalsIgnoreCase("DeviceGeoStatusList") || unique.equalsIgnoreCase("DeviceLocationHistoryList")) {
                final String platform3 = this.getStateValue(viewCtx, "platformType");
                if (platform3 != null && !platform3.equalsIgnoreCase("0") && !platform3.equalsIgnoreCase("all")) {
                    final Criteria cPlatform3 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform3), 0);
                    crit = ((crit == null) ? cPlatform3 : crit.and(cPlatform3));
                }
                final String groupIdString = viewCtx.getRequest().getParameter("groupIds");
                JSONArray groupIds = null;
                if (groupIdString != null && !groupIdString.isEmpty()) {
                    groupIds = new JSONArray(groupIdString);
                }
                final Criteria deviceJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
                final Criteria userJoinCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)Column.getColumn("ManagedUser", "MANAGED_USER_ID"), 0);
                query.addJoin(new Join("Resource", "CustomGroupMemberRel", deviceJoinCri.or(userJoinCri), 1));
                query.setDistinct(true);
                List groupIdsList = null;
                if (groupIds != null && groupIds.length() > 0) {
                    groupIdsList = groupIds.toList();
                    final Criteria groupcri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupIdsList.toArray(), 8);
                    crit = ((crit == null) ? groupcri : crit.and(groupcri));
                }
                final String status = this.getStateValue(viewCtx, "geoStatus");
                if (status != null && !status.equalsIgnoreCase("0") && !status.equalsIgnoreCase("all")) {
                    final int geoStatus = Integer.valueOf(status);
                    final Criteria geoStatusCri = new GeoLocationFacade().getGeoStatusCrit(geoStatus, false);
                    crit = ((crit == null) ? geoStatusCri : crit.and(geoStatusCri));
                }
                Boolean lostModeStatus = null;
                final String tempString = this.getStateValue(viewCtx, "lostStatus");
                if (!MDMUtil.isStringEmpty(tempString) && !tempString.equalsIgnoreCase("0") && !tempString.equalsIgnoreCase("all")) {
                    lostModeStatus = Boolean.parseBoolean(tempString);
                }
                if (lostModeStatus != null && lostModeStatus) {
                    final Criteria lostModeCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Integer[] { 2, 1, 4, 6 }, 8);
                    crit = ((crit == null) ? lostModeCriteria : crit.and(lostModeCriteria));
                }
                final String beforeHours = this.getStateValue(viewCtx, "locatedTimeBeforeHours");
                final String beforeDays = this.getStateValue(viewCtx, "locatedTimeBeforeDays");
                final String startDateLastLoc = this.getStateValue(viewCtx, "startDate");
                final String endDateLastLoc = this.getStateValue(viewCtx, "endDate");
                Long before = 0L;
                Long start = 0L;
                Long end = 0L;
                if (beforeHours != null && !beforeHours.isEmpty()) {
                    before = 3600000L * Long.valueOf(beforeHours);
                }
                if (beforeDays != null && !beforeDays.isEmpty()) {
                    before = 86400000L * Long.valueOf(beforeDays);
                }
                if (startDateLastLoc != null && !startDateLastLoc.isEmpty() && unique.equalsIgnoreCase("DeviceLocationList")) {
                    start = MDMUtil.getInstance().convertDateToMillis(startDateLastLoc);
                }
                if (endDateLastLoc != null && !endDateLastLoc.isEmpty() && unique.equalsIgnoreCase("DeviceLocationList")) {
                    end = MDMUtil.getInstance().convertDateToMillis(endDateLastLoc);
                }
                if (before != 0L) {
                    final Criteria locatedTimeCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)(System.currentTimeMillis() - before), 7);
                    crit = ((crit == null) ? locatedTimeCri : crit.and(locatedTimeCri));
                }
                if (start != 0L && end != 0L) {
                    final Criteria startCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)start, 4);
                    final Criteria endCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)end, 6);
                    final Criteria locatedTimeCri2 = startCri.and(endCri);
                    crit = ((crit == null) ? locatedTimeCri2 : crit.and(locatedTimeCri2));
                }
                query = this.customGroupNameJoin(query);
                query.addJoin(new Join("ManagedDevice", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                final Criteria appJoinCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)Column.getColumn("MdAppDetails", "APP_ID"), 0);
                final Criteria identifierCri = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
                query.addJoin(new Join("MdAppCatalogToResource", "MdAppDetails", appJoinCri.and(identifierCri), 1));
                query.addJoin(new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
                if (unique.equalsIgnoreCase("DeviceLocationHistoryList")) {
                    final String sDeviceID = this.getStateValue(viewCtx, "deviceId");
                    Long deviceID = null;
                    final Long customerID2 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                    if (sDeviceID != null) {
                        deviceID = Long.valueOf(sDeviceID);
                        final Criteria deviceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceID, 0);
                        crit = ((crit == null) ? deviceCri : crit.and(deviceCri));
                    }
                    final String startDateString = this.getStateValue(viewCtx, "startDate");
                    final String startTimeString = this.getStateValue(viewCtx, "startTime");
                    final String endDateString = this.getStateValue(viewCtx, "endDate");
                    final String endTimeString = this.getStateValue(viewCtx, "endTime");
                    final String intervalInMinsString = this.getStateValue(viewCtx, "interval");
                    Long startDate = null;
                    Long endDate = null;
                    if (startDateString != null) {
                        startDate = MDMUtil.getInstance().convertDateToMillis(startDateString);
                    }
                    if (endDateString != null) {
                        endDate = MDMUtil.getInstance().convertDateToMillis(endDateString) + 86400000L;
                    }
                    if (startDate != null) {
                        final Criteria cri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)startDate, 4);
                        crit = ((crit == null) ? cri : crit.and(cri));
                    }
                    if (endDate != null) {
                        final Criteria cri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), (Object)endDate, 7);
                        crit = ((crit == null) ? cri : crit.and(cri));
                    }
                    if (startTimeString != null || endDateString != null || (intervalInMinsString != null && !intervalInMinsString.equalsIgnoreCase("all"))) {
                        final ArrayList locationIds = new GeoLocationFacade().getLocationHistoryIds(deviceID, groupIdsList, startDateString, endDateString, startTimeString, endTimeString, intervalInMinsString, customerID2);
                        final Criteria locationIdCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)locationIds.toArray(), 8);
                        crit = ((crit == null) ? locationIdCri : crit.and(locationIdCri));
                    }
                }
            }
            else if (unique.equalsIgnoreCase("DeviceByEnrolledTime") || unique.equalsIgnoreCase("InactiveDevices")) {
                String period = viewCtx.getRequest().getParameter("period");
                final String startDate2 = this.getStateValue(viewCtx, "startDate");
                final String endDate2 = this.getStateValue(viewCtx, "endDate");
                final String platform2 = this.getStateValue(viewCtx, "platform");
                Criteria periodCrit = null;
                String criteriaTable = "ManagedDevice";
                String criteriaColumn = "REGISTERED_TIME";
                viewCtx.getRequest().setAttribute("period", (Object)viewCtx.getRequest().getParameter("period"));
                final DataObject criteriaDO = ReportCriteriaUtil.getInstance().getCriteriaDetailsFromDb((Object)viewCtx.getRequest().getParameter("scheduleID"));
                final Criteria before_n_daysCrit = new Criteria(Column.getColumn("CriteriaColumnDetails", "COMPARATOR"), (Object)"Before n Days", 0);
                final Criteria next_n_daysCrit = new Criteria(Column.getColumn("CriteriaColumnDetails", "COMPARATOR"), (Object)"Next n Days", 0);
                final Criteria dateCri = new Criteria(Column.getColumn("CRColumns", "DATA_TYPE"), (Object)"DATE", 0);
                boolean hasLastNDaysCri = false;
                try {
                    final Row comparatorRow = criteriaDO.getRow("CriteriaColumnDetails", before_n_daysCrit.or(next_n_daysCrit));
                    if (comparatorRow != null) {
                        final Row dateRow = criteriaDO.getRow("CRColumns", dateCri);
                        if (dateRow != null) {
                            hasLastNDaysCri = true;
                        }
                    }
                }
                catch (final DataAccessException e) {
                    this.logger.log(Level.SEVERE, "Exception in retriving CRColumn", (Throwable)e);
                }
                final List mdmGpList = MDMGroupHandler.getCustomGroups();
                if (mdmGpList != null) {
                    viewCtx.getRequest().setAttribute("mdmGroupList", (Object)mdmGpList);
                }
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
                if (unique.equalsIgnoreCase("InactiveDevices")) {
                    criteriaTable = "AgentContact";
                    criteriaColumn = "LAST_CONTACT_TIME";
                }
                if (unique.equalsIgnoreCase("InactiveDevices")) {
                    final String criteriaJSON = viewCtx.getRequest().getParameter("criteriaJSON");
                    if (period == null && startDate2 == null && endDate2 == null && (criteriaJSON == null || (criteriaJSON != null && !criteriaJSON.contains("last_n_days") && !criteriaJSON.contains("is")))) {
                        final Long customerID3 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                        final Long inactivePeriod = (long)new InactiveDevicePolicyTask().getInactiveDevicePolicyThresholdValues(customerID3).get("InactiveThreshold") / 86400000L;
                        period = String.valueOf(inactivePeriod);
                    }
                    periodCrit = this.getTimePeriodCriteria(criteriaTable, criteriaColumn, period, startDate2, endDate2, true);
                }
                else {
                    periodCrit = this.getTimePeriodCriteria(criteriaTable, criteriaColumn, period, startDate2, endDate2, false);
                }
                if (startDate2 != null && endDate2 != null) {
                    viewCtx.getRequest().setAttribute("startDate", (Object)startDate2);
                    viewCtx.getRequest().setAttribute("endDate", (Object)endDate2);
                }
                if (crit != null) {
                    crit = crit.and(periodCrit);
                }
                else {
                    crit = periodCrit;
                }
                if (platform2 != null && !platform2.equalsIgnoreCase("0") && !platform2.equalsIgnoreCase("all")) {
                    final Criteria cPlatform4 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform2), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform4);
                    }
                    else {
                        crit = cPlatform4;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("DevicesByApplication")) {
                final String sAppID = this.getStateValue(viewCtx, "APP_GROUP_ID");
                final String filterType = this.getStateValue(viewCtx, "FilterType");
                if (sAppID != null) {
                    final Criteria criteria3 = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)sAppID, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria3);
                    }
                    else {
                        crit = criteria3;
                    }
                }
                if (filterType != null && !filterType.isEmpty() && !filterType.equalsIgnoreCase("0") && !filterType.equals("all")) {
                    final BlacklistQueryUtils blackList = new BlacklistQueryUtils();
                    Criteria filterCriteria = new Criteria();
                    filterCriteria = blackList.getCriteriaforDeviceForApps(Integer.parseInt(filterType));
                    viewCtx.getRequest().setAttribute("deviceFilter", (Object)filterType);
                    if (crit != null) {
                        crit = crit.and(filterCriteria);
                    }
                    else {
                        crit = filterCriteria;
                    }
                }
                final String appIDVal = this.getStateValue(viewCtx, "APP_ID");
                if (appIDVal != null) {
                    final Criteria criteria4 = new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)appIDVal, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria4);
                    }
                    else {
                        crit = criteria4;
                    }
                }
                final String installedInval = this.getStateValue(viewCtx, "installedIn");
                if (installedInval != null && !installedInval.equals("-1")) {
                    viewCtx.getRequest().setAttribute("installed", (Object)installedInval);
                    final Integer installedIn = Integer.parseInt(installedInval);
                    final Criteria criteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)installedIn, 0);
                    if (crit != null) {
                        crit = crit.and(criteria);
                    }
                    else {
                        crit = criteria;
                    }
                }
                query = AppSettingsDataHandler.getInstance().setOnViewFilterCriteria(query, viewCtx.getRequest(), unique);
            }
            else if (unique.equalsIgnoreCase("BlacklistedDevicesByApplication")) {
                final String sAppID = this.getStateValue(viewCtx, "APP_GROUP_ID");
                final String filterType = this.getStateValue(viewCtx, "FilterType");
                if (sAppID != null) {
                    final Criteria criteria3 = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)sAppID, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria3);
                    }
                    else {
                        crit = criteria3;
                    }
                }
                if (filterType != null && !filterType.isEmpty() && !filterType.equalsIgnoreCase("0") && !filterType.equals("all")) {
                    final BlacklistQueryUtils blackList = new BlacklistQueryUtils();
                    Criteria filterCriteria = new Criteria();
                    filterCriteria = blackList.getCriteriaforDeviceForApps(Integer.parseInt(filterType));
                    viewCtx.getRequest().setAttribute("deviceFilter", (Object)filterType);
                    if (crit != null) {
                        crit = crit.and(filterCriteria);
                    }
                    else {
                        crit = filterCriteria;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("RootedDevices")) {
                final Long customerID4 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final ArrayList arrProductNames2 = ReportUtil.getInstance().getProductNames(customerID4, 2);
                viewCtx.getRequest().setAttribute("productnames", (Object)arrProductNames2);
                this.setMDMGroupFilter(viewCtx);
                final Criteria cPlatform5 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
                if (crit != null) {
                    crit = crit.and(cPlatform5);
                }
                else {
                    crit = cPlatform5;
                }
                final Criteria rootedDevicesCriteria = new Criteria(Column.getColumn("MdSecurityInfo", "DEVICE_ROOTED"), (Object)Boolean.TRUE, 0);
                crit = crit.and(rootedDevicesCriteria);
                final String sProduct2 = this.getStateValue(viewCtx, "product");
                if (sProduct2 != null && !sProduct2.equalsIgnoreCase("all")) {
                    viewCtx.getRequest().setAttribute("sProduct", (Object)sProduct2);
                    final Criteria criteria = new Criteria(new Column("MdModelInfo", "PRODUCT_NAME"), (Object)sProduct2, 0, false);
                    crit = crit.and(criteria);
                }
            }
            else if (unique.equalsIgnoreCase("JailBrokenDevices")) {
                final Long customerID4 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final ArrayList arrProductNames2 = ReportUtil.getInstance().getProductNames(customerID4, 1);
                viewCtx.getRequest().setAttribute("productnames", (Object)arrProductNames2);
                this.setMDMGroupFilter(viewCtx);
                final Criteria cPlatform5 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
                if (crit != null) {
                    crit = crit.and(cPlatform5);
                }
                else {
                    crit = cPlatform5;
                }
                final Criteria rootedDevicesCriteria = new Criteria(Column.getColumn("MdSecurityInfo", "DEVICE_ROOTED"), (Object)Boolean.TRUE, 0);
                crit = crit.and(rootedDevicesCriteria);
                final String sProduct2 = this.getStateValue(viewCtx, "product");
                if (sProduct2 != null && !sProduct2.equalsIgnoreCase("all")) {
                    viewCtx.getRequest().setAttribute("sProduct", (Object)sProduct2);
                    final Criteria criteria = new Criteria(new Column("MdModelInfo", "PRODUCT_NAME"), (Object)sProduct2, 0, false);
                    crit = crit.and(criteria);
                }
            }
            else if (unique.equalsIgnoreCase("DevicesByEncryption")) {
                this.setMDMGroupFilter(viewCtx);
                final String platform3 = this.getStateValue(viewCtx, "platform");
                if (platform3 != null && !platform3.equalsIgnoreCase("0")) {
                    final Criteria cPlatform3 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform3), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform3);
                    }
                    else {
                        crit = cPlatform3;
                    }
                }
                final String encryptionStatus = this.getStateValue(viewCtx, "encryptionStatus");
                if (encryptionStatus != null && !encryptionStatus.equalsIgnoreCase("all")) {
                    final Criteria criteria3 = new Criteria(new Column("MdSecurityInfo", "STORAGE_ENCRYPTION"), (Object)Boolean.valueOf(encryptionStatus), 0);
                    if (crit != null) {
                        crit = crit.and(criteria3);
                    }
                    else {
                        crit = criteria3;
                    }
                }
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
            }
            else if (unique.equalsIgnoreCase("CloudBackUpedDevices")) {
                final String platform3 = this.getStateValue(viewCtx, "platform");
                final String isCloudBackupEnabled = this.getStateValue(viewCtx, "isCloudBackupEnabled");
                final String isCloudBackupRestricted = this.getStateValue(viewCtx, "isCloudBackupRestricted");
                final String period2 = viewCtx.getRequest().getParameter("period");
                final String startDate3 = this.getStateValue(viewCtx, "startDate");
                final String endDate3 = this.getStateValue(viewCtx, "endDate");
                viewCtx.getRequest().setAttribute("period", (Object)viewCtx.getRequest().getParameter("period"));
                query.addJoin(new Join("ManagedDevice", "MdIOSRestriction", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                query.addSelectColumn(Column.getColumn("MdIOSRestriction", "ALLOW_CLOUD_BACKUP", "BACKUP_RESTRICTED_IN_IOS_DEVICE"));
                if (isCloudBackupEnabled != null && !isCloudBackupEnabled.equalsIgnoreCase("All")) {
                    final Criteria criteria5 = new Criteria(new Column("MdDeviceInfo", "IS_CLOUD_BACKUP_ENABLED"), (Object)Boolean.valueOf(isCloudBackupEnabled), 0);
                    if (crit != null) {
                        crit = crit.and(criteria5);
                    }
                    else {
                        crit = criteria5;
                    }
                }
                if (platform3 != null && !platform3.equalsIgnoreCase("0")) {
                    final Criteria cPlatform6 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform3), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform6);
                    }
                    else {
                        crit = cPlatform6;
                    }
                }
                if (isCloudBackupRestricted != null && !isCloudBackupRestricted.equalsIgnoreCase("all")) {
                    Criteria criteria5 = null;
                    criteria5 = new Criteria(new Column("MdIOSRestriction", "ALLOW_CLOUD_BACKUP"), (Object)Integer.valueOf(isCloudBackupRestricted), 0);
                    if (crit != null) {
                        crit = crit.and(criteria5);
                    }
                    else {
                        crit = criteria5;
                    }
                }
                final Criteria periodCrit2 = this.getTimePeriodCriteria("MdDeviceInfo", "LAST_CLOUD_BACKUP_DATE", period2, startDate3, endDate3, false);
                if (startDate3 != null && endDate3 != null && !startDate3.equalsIgnoreCase("") && !endDate3.equalsIgnoreCase("")) {
                    viewCtx.getRequest().setAttribute("startDate", (Object)startDate3);
                    viewCtx.getRequest().setAttribute("endDate", (Object)endDate3);
                }
                if (periodCrit2 != null) {
                    if (crit != null) {
                        crit = crit.and(periodCrit2);
                    }
                    else {
                        crit = periodCrit2;
                    }
                }
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
            }
            else if (unique.equalsIgnoreCase("DevicesByApplicationFlat")) {
                final String platform3 = viewCtx.getRequest().getParameter("platform");
                final String mdAppFilter = viewCtx.getRequest().getParameter("mdAppFilter");
                if (platform3 != null && !platform3.equalsIgnoreCase("0")) {
                    viewCtx.getRequest().setAttribute("platform", (Object)platform3);
                    final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform3), 0);
                    if (crit != null) {
                        crit = crit.and(platformCri);
                    }
                    else {
                        crit = platformCri;
                    }
                }
                final Long customerID5 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                try {
                    Criteria settingsCriteria = null;
                    final JSONObject settings = AppSettingsDataHandler.getInstance().getAppViewSettings(customerID5);
                    if (!settings.getBoolean("SHOW_SYSTEM_APPS")) {
                        settingsCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 1);
                    }
                    if (!settings.getBoolean("SHOW_USER_INSTALLED_APPS")) {
                        if (settingsCriteria == null) {
                            settingsCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 1);
                        }
                        else {
                            settingsCriteria = settingsCriteria.and(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 1));
                        }
                    }
                    if (!settings.getBoolean("SHOW_MANAGED_APPS")) {
                        if (settingsCriteria == null) {
                            settingsCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)3, 1);
                        }
                        else {
                            settingsCriteria = settingsCriteria.and(new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)3, 1));
                        }
                    }
                    if (settingsCriteria != null) {
                        if (crit != null) {
                            crit = crit.and(settingsCriteria);
                        }
                        else {
                            crit = settingsCriteria;
                        }
                    }
                }
                catch (final JSONException e2) {
                    this.logger.log(Level.SEVERE, "Exception in ReportsCommonTRAction ", (Throwable)e2);
                }
                if (mdAppFilter != null) {
                    Criteria installCri = null;
                    if (mdAppFilter.equalsIgnoreCase("1")) {
                        installCri = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)3, 0);
                    }
                    else if (mdAppFilter.equalsIgnoreCase("2")) {
                        installCri = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
                    }
                    else if (mdAppFilter.equalsIgnoreCase("3")) {
                        installCri = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
                    }
                    if (installCri != null) {
                        if (crit != null) {
                            crit = crit.and(installCri);
                        }
                        else {
                            crit = installCri;
                        }
                    }
                }
            }
            else if (unique.equalsIgnoreCase("LostDevices")) {
                this.setMDMGroupFilter(viewCtx);
                Criteria criteria6 = query.getCriteria();
                final String tempString2 = viewCtx.getRequest().getParameter("status");
                Integer lostModeStatus2 = null;
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                if (!MDMUtil.isStringEmpty(tempString2)) {
                    lostModeStatus2 = Integer.parseInt(tempString2);
                }
                Criteria lostModeCriteria2;
                if (lostModeStatus2 != null && lostModeStatus2 != -1) {
                    lostModeCriteria2 = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)lostModeStatus2, 0);
                    viewCtx.getRequest().setAttribute("status", (Object)lostModeStatus2);
                }
                else {
                    lostModeCriteria2 = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 4, 6, 3, 1, 2 }, 8);
                }
                if (criteria6 != null) {
                    criteria6 = criteria6.and(lostModeCriteria2);
                }
                else {
                    criteria6 = lostModeCriteria2;
                }
                final String platform4 = viewCtx.getRequest().getParameter("platform");
                if (platform4 != null && !platform4.equalsIgnoreCase("0")) {
                    final Criteria cPlatform = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform4), 0);
                    criteria6 = criteria6.and(cPlatform);
                    viewCtx.getRequest().setAttribute("platform", (Object)platform4);
                }
                crit = criteria6;
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
            }
            else if (unique.equalsIgnoreCase("SafetyNetDevices")) {
                final String basicInteg = this.getStateValue(viewCtx, "basicIntegrity");
                final String ctsInteg = this.getStateValue(viewCtx, "cts");
                final String platformInteg = this.getStateValue(viewCtx, "platform");
                final String modelTypeInteg = this.getStateValue(viewCtx, "modelType");
                final String isMultiUserInteg = this.getStateValue(viewCtx, "isMultiUser");
                Criteria basicCriteria = null;
                Criteria ctsCriteria = null;
                Criteria platformCriteria = null;
                Criteria modelTypeCriteria = null;
                Criteria isMultiUserCriteria = null;
                if (basicInteg != null && ctsInteg != null && platformInteg != null && modelTypeInteg != null && isMultiUserInteg != null) {
                    if (!basicInteg.equalsIgnoreCase("all")) {
                        if (!basicInteg.equalsIgnoreCase("notSupported")) {
                            final boolean basicIntegState = !basicInteg.equalsIgnoreCase("false");
                            basicCriteria = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_BASIC_INTEGRITY"), (Object)basicIntegState, 0);
                        }
                        else {
                            basicCriteria = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_BASIC_INTEGRITY"), (Object)null, 0);
                        }
                    }
                    if (!ctsInteg.equalsIgnoreCase("all")) {
                        if (!ctsInteg.equalsIgnoreCase("notSupported")) {
                            final boolean ctsIntegState = !ctsInteg.equalsIgnoreCase("false");
                            ctsCriteria = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_CTS"), (Object)ctsIntegState, 0);
                        }
                        else {
                            ctsCriteria = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_CTS"), (Object)null, 0);
                        }
                    }
                    if (!modelTypeInteg.equalsIgnoreCase("all")) {
                        if (modelTypeInteg.equalsIgnoreCase(String.valueOf(1))) {
                            modelTypeCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)1, 0);
                        }
                        else if (modelTypeInteg.equalsIgnoreCase(String.valueOf(2))) {
                            modelTypeCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)2, 0);
                        }
                        else if (modelTypeInteg.equalsIgnoreCase(String.valueOf(5))) {
                            modelTypeCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)5, 0);
                        }
                    }
                    else {
                        final Criteria smartphoneCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)1, 0);
                        final Criteria tabletCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)2, 0);
                        final Criteria tvCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)5, 0);
                        modelTypeCriteria = smartphoneCriteria.or(tabletCriteria).or(tvCriteria);
                    }
                    if (!isMultiUserInteg.equalsIgnoreCase("all") && modelTypeInteg.equalsIgnoreCase(String.valueOf(2))) {
                        isMultiUserCriteria = new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)isMultiUserInteg, 0);
                    }
                    if (!platformInteg.equalsIgnoreCase("all")) {
                        if (platformInteg.equalsIgnoreCase(String.valueOf(1))) {
                            platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
                        }
                        else {
                            platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
                        }
                    }
                    else {
                        final Criteria IOSCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
                        final Criteria androidCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
                        platformCriteria = IOSCriteria.or(androidCriteria);
                    }
                    crit = platformCriteria;
                    if (basicCriteria != null) {
                        crit = crit.and(basicCriteria);
                    }
                    if (ctsCriteria != null) {
                        crit = crit.and(ctsCriteria);
                    }
                    if (modelTypeCriteria != null) {
                        crit = crit.and(modelTypeCriteria);
                    }
                    if (isMultiUserCriteria != null) {
                        crit = crit.and(isMultiUserCriteria);
                    }
                }
                if (viewCtx.getRequest().getParameter("mdmGroupId") == null) {
                    query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    query.setDistinct(true);
                }
                query = this.customGroupNameJoin(query);
                viewCtx.getRequest().setAttribute("CUSTOM_GROUPS", (Object)MDMCustomGroupUtil.getInstance().getGroupNamesWithResourceID());
            }
            final HttpServletRequest request = viewCtx.getRequest();
            final String mdmGroupIdStr = request.getParameter("mdmGroupId");
            if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                viewCtx.getRequest().setAttribute("mdmGroupId", (Object)mdmGroupIdStr);
                final Long mdmGroupId = new Long(mdmGroupIdStr);
                query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                final Criteria cgCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)mdmGroupId, 0);
                if (crit == null) {
                    crit = cgCriteria;
                }
                else {
                    crit = crit.and(cgCriteria);
                }
            }
            final Criteria enrolledCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0, false);
            final Criteria trCrit = query.getCriteria();
            if (trCrit != null && crit != null) {
                query.setCriteria(trCrit.and(crit).and(enrolledCriteria));
            }
            else if (crit != null) {
                query.setCriteria(crit.and(enrolledCriteria));
            }
            else {
                query.setCriteria(enrolledCriteria);
            }
            final String sQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
            this.logger.log(Level.INFO, "ReportsCommonTRAction for view name {0} \nQuery -- {1}", new Object[] { unique, sQuery });
        }
        catch (final QueryConstructionException ex) {
            this.logger.log(Level.SEVERE, "Exception in ReportsCommonTRAction ", (Throwable)ex);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception in ReportsCommonTRAction ", e3);
        }
        super.setCriteria(query, viewCtx);
    }
    
    protected String getStateValue(final ViewContext viewCtx, final String key) {
        String value = viewCtx.getRequest().getParameter(key);
        if (value == null) {
            value = (String)viewCtx.getRequest().getAttribute(key);
        }
        if (value == null) {
            value = (String)viewCtx.getStateParameter(key);
        }
        if (value != null) {
            viewCtx.setStateParameter(key, (Object)value);
            viewCtx.getRequest().setAttribute(key, (Object)value);
        }
        this.logger.log(Level.INFO, "Value for key : {0} is {1}", new Object[] { key, value });
        return value;
    }
    
    protected void setReportParameters(final ViewContext viewCtx) {
        final String toolID = (String)SYMClientUtil.getStateValue(viewCtx, "toolID");
        if (toolID != null) {
            final int reportConstant = Integer.parseInt(toolID);
            try {
                final Hashtable viewProps = SYMReportUtil.getViewParams(Integer.valueOf(reportConstant));
                viewCtx.getRequest().setAttribute("viewProps", (Object)viewProps);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void setMDMGroupFilter(final ViewContext viewCtx) {
        final List mdmGpList = MDMGroupHandler.getCustomGroups();
        if (mdmGpList != null) {
            viewCtx.getRequest().setAttribute("mdmGroupList", (Object)mdmGpList);
        }
    }
    
    protected Criteria getTimePeriodCriteria(final String criteriaTable, final String criteriaColumn, final String period, final String startDate, final String endDate, final boolean negate) {
        Criteria periodCrit = null;
        if (period != null && !period.equalsIgnoreCase("all") && !"custom".equals(period)) {
            final Calendar cal = Calendar.getInstance();
            int noOfDays = Integer.parseInt(period);
            if (noOfDays != 0) {
                noOfDays *= -1;
                final Date today = new Date();
                cal.setTime(today);
                cal.add(5, noOfDays);
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                final long filter = cal.getTime().getTime();
                if (negate) {
                    periodCrit = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)new Long(filter), 7);
                }
                else {
                    periodCrit = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)new Long(filter), 4);
                }
            }
        }
        else if (startDate != null && endDate != null && !startDate.equalsIgnoreCase("") && !endDate.equalsIgnoreCase("")) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long start = 0L;
            long end = 0L;
            try {
                start = sdf.parse(startDate).getTime();
                end = sdf.parse(endDate).getTime();
                if (start > end) {
                    final long temp = start;
                    start = end;
                    end = temp;
                }
                if (negate) {
                    final Criteria criteria1 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)start, 7);
                    final Criteria criteria2 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)(end + 86400000L), 5);
                    periodCrit = criteria1.or(criteria2);
                }
                else {
                    final Criteria criteria1 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)start, 4);
                    final Criteria criteria2 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)(end + 86400000L), 6);
                    periodCrit = criteria1.and(criteria2);
                }
            }
            catch (final ParseException exp) {
                this.logger.log(Level.WARNING, "Exception occured while parsing start and end date for recently enrolled device report ", exp);
            }
        }
        else if (startDate != null && !startDate.equalsIgnoreCase("") && (endDate == null || endDate.equalsIgnoreCase(""))) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            long start = 0L;
            try {
                start = sdf.parse(startDate).getTime();
                if (negate) {
                    periodCrit = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)start, 6);
                }
                else {
                    periodCrit = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)start, 4);
                }
            }
            catch (final ParseException exp2) {
                this.logger.log(Level.WARNING, "Exception occured while parsing start and end date for recently enrolled device report ", exp2);
            }
        }
        return periodCrit;
    }
    
    protected SelectQuery customGroupNameJoin(final SelectQuery query) {
        query.addJoin(new Join("ManagedDevice", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        return query;
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final String unique = viewCtx.getUniqueId();
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        if (!selectQuery.getTableList().contains(Table.getTable("BlacklistAppCollectionStatus")) && unique.equalsIgnoreCase("DevicesByApplication")) {
            final Criteria collnCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), 0);
            final Criteria resCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), 0);
            final Criteria scopeCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"), 0);
            selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", collnCriteria.and(resCriteria).and(scopeCriteria), 1));
            selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"));
        }
        else if (!selectQuery.getTableList().contains(Table.getTable("MdDeviceRecentUsersInfo")) && unique.equalsIgnoreCase("DevicesByEncryption")) {
            new MDMDeviceRecentUserViewHandler().addRecentUsersTableJoin(selectQuery, unique);
        }
        if (unique.equalsIgnoreCase("DevicesByEncryption")) {
            final Criteria resourceCrit = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)Column.getColumn("MacBootstrapToken", "RESOURCE_ID"), 0);
            selectQuery.addJoin(new Join("ManagedDevice", "MacBootstrapToken", resourceCrit, 1));
            selectQuery.addSelectColumn(Column.getColumn("MacBootstrapToken", "RESOURCE_ID", "MacBootstrapToken.RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MacBootstrapToken", "TOKEN", "MacBootstrapToken.TOKEN"));
        }
        return selectQuery;
    }
}
