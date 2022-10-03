package com.adventnet.sym.webclient.mdm.reports;

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
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.reports.ReportUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class ReportsTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public ReportsTRAction() {
        this.logger = Logger.getLogger(ReportsCommonTRAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
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
                if (platform != null && !platform.equalsIgnoreCase("0")) {
                    final Criteria cPlatform = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform);
                    }
                    else {
                        crit = cPlatform;
                    }
                }
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
                if (platform2 != null && !platform2.equalsIgnoreCase("0")) {
                    final Criteria cPlatform2 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform2), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform2);
                    }
                    else {
                        crit = cPlatform2;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("DeviceByEnrolledTime") || unique.equalsIgnoreCase("InactiveDevices")) {
                String period = viewCtx.getRequest().getParameter("period");
                final String startDate = this.getStateValue(viewCtx, "startDate");
                final String endDate = this.getStateValue(viewCtx, "endDate");
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
                if (unique.equalsIgnoreCase("InactiveDevices")) {
                    criteriaTable = "AgentContact";
                    criteriaColumn = "LAST_CONTACT_TIME";
                }
                if (startDate == null && endDate == null && period == null && !hasLastNDaysCri) {
                    period = "30";
                }
                periodCrit = this.getTimePeriodCriteria(criteriaTable, criteriaColumn, period, startDate, endDate);
                if (startDate != null && endDate != null) {
                    viewCtx.getRequest().setAttribute("startDate", (Object)startDate);
                    viewCtx.getRequest().setAttribute("endDate", (Object)endDate);
                }
                if (periodCrit != null && unique.equalsIgnoreCase("InactiveDevices")) {
                    periodCrit = periodCrit.negate();
                }
                if (crit != null) {
                    crit = crit.and(periodCrit);
                }
                else {
                    crit = periodCrit;
                }
                if (platform2 != null && !platform2.equalsIgnoreCase("0")) {
                    final Criteria cPlatform3 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform2), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform3);
                    }
                    else {
                        crit = cPlatform3;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("DevicesByApplication")) {
                final String sAppGroupID = this.getStateValue(viewCtx, "APP_GROUP_ID");
                final String sAppID = this.getStateValue(viewCtx, "APP_ID");
                final String filterType = this.getStateValue(viewCtx, "FilterType");
                if (sAppGroupID != null) {
                    final Criteria criteria3 = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)sAppGroupID, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria3);
                    }
                    else {
                        crit = criteria3;
                    }
                }
                else if (!MDMStringUtils.isEmpty(sAppID)) {
                    final Criteria criteria3 = new Criteria(new Column("MdAppToGroupRel", "APP_ID"), (Object)sAppID, 0, false);
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
                    final Criteria criteria2 = new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)appIDVal, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria2);
                    }
                    else {
                        crit = criteria2;
                    }
                }
                final String installedInval = this.getStateValue(viewCtx, "installedIn");
                if (installedInval != null && !installedInval.equals("-1")) {
                    viewCtx.getRequest().setAttribute("installed", (Object)installedInval);
                    final Integer installedIn = Integer.parseInt(installedInval);
                    final Criteria criteria4 = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)installedIn, 0);
                    if (crit != null) {
                        crit = crit.and(criteria4);
                    }
                    else {
                        crit = criteria4;
                    }
                }
                final Criteria showAppCriteria = AppsUtil.getInstance().showAppCriteria(MSPWebClientUtil.getCustomerID(viewCtx.getRequest()));
                if (showAppCriteria != null) {
                    if (crit != null) {
                        crit = crit.and(showAppCriteria);
                    }
                    else {
                        crit = showAppCriteria;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("BlacklistedDevicesByApplication")) {
                final String sAppID2 = this.getStateValue(viewCtx, "APP_GROUP_ID");
                final String filterType2 = this.getStateValue(viewCtx, "FilterType");
                if (sAppID2 != null) {
                    final Criteria criteria5 = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)sAppID2, 0, false);
                    if (crit != null) {
                        crit = crit.and(criteria5);
                    }
                    else {
                        crit = criteria5;
                    }
                }
                if (filterType2 == null) {
                    if (crit != null) {
                        crit = crit.and(new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)new int[] { 1, 2, 3, 9 }, 8));
                    }
                    else {
                        crit = new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)new int[] { 1, 2, 3, 9 }, 8);
                    }
                }
                if (filterType2 != null && !filterType2.isEmpty() && !filterType2.equalsIgnoreCase("0") && !filterType2.equals("all")) {
                    final BlacklistQueryUtils blackList2 = new BlacklistQueryUtils();
                    Criteria filterCriteria2 = new Criteria();
                    filterCriteria2 = blackList2.getCriteriaforDeviceForApps(Integer.parseInt(filterType2));
                    viewCtx.getRequest().setAttribute("deviceFilter", (Object)filterType2);
                    if (crit != null) {
                        crit = crit.and(filterCriteria2);
                    }
                    else {
                        crit = filterCriteria2;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("RootedDevices")) {
                final Long customerID2 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final ArrayList arrProductNames2 = ReportUtil.getInstance().getProductNames(customerID2, 2);
                viewCtx.getRequest().setAttribute("productnames", (Object)arrProductNames2);
                this.setMDMGroupFilter(viewCtx);
                final Criteria cPlatform4 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
                if (crit != null) {
                    crit = crit.and(cPlatform4);
                }
                else {
                    crit = cPlatform4;
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
                final Long customerID2 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final ArrayList arrProductNames2 = ReportUtil.getInstance().getProductNames(customerID2, 1);
                viewCtx.getRequest().setAttribute("productnames", (Object)arrProductNames2);
                this.setMDMGroupFilter(viewCtx);
                final Criteria cPlatform4 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
                if (crit != null) {
                    crit = crit.and(cPlatform4);
                }
                else {
                    crit = cPlatform4;
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
                    final Criteria cPlatform5 = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform3), 0);
                    if (crit != null) {
                        crit = crit.and(cPlatform5);
                    }
                    else {
                        crit = cPlatform5;
                    }
                }
                final String encryptionStatus = this.getStateValue(viewCtx, "encryptionStatus");
                if (encryptionStatus != null && !encryptionStatus.equalsIgnoreCase("all")) {
                    final Criteria criteria5 = new Criteria(new Column("MdSecurityInfo", "STORAGE_ENCRYPTION"), (Object)Boolean.valueOf(encryptionStatus), 0);
                    if (crit != null) {
                        crit = crit.and(criteria5);
                    }
                    else {
                        crit = criteria5;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("CloudBackUpedDevices")) {
                final String platform3 = this.getStateValue(viewCtx, "platform");
                final String isCloudBackupEnabled = this.getStateValue(viewCtx, "isCloudBackupEnabled");
                final String isCloudBackupRestricted = this.getStateValue(viewCtx, "isCloudBackupRestricted");
                final String period2 = viewCtx.getRequest().getParameter("period");
                final String startDate2 = this.getStateValue(viewCtx, "startDate");
                final String endDate2 = this.getStateValue(viewCtx, "endDate");
                viewCtx.getRequest().setAttribute("period", (Object)viewCtx.getRequest().getParameter("period"));
                query.addJoin(new Join("ManagedDevice", "MdIOSRestriction", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                query.addSelectColumn(Column.getColumn("MdIOSRestriction", "ALLOW_CLOUD_BACKUP", "BACKUP_RESTRICTED_IN_IOS_DEVICE"));
                if (isCloudBackupEnabled != null && !isCloudBackupEnabled.equalsIgnoreCase("All")) {
                    final Criteria criteria4 = new Criteria(new Column("MdDeviceInfo", "IS_CLOUD_BACKUP_ENABLED"), (Object)Boolean.valueOf(isCloudBackupEnabled), 0);
                    if (crit != null) {
                        crit = crit.and(criteria4);
                    }
                    else {
                        crit = criteria4;
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
                    Criteria criteria4 = null;
                    criteria4 = new Criteria(new Column("MdIOSRestriction", "ALLOW_CLOUD_BACKUP"), (Object)Integer.valueOf(isCloudBackupRestricted), 0);
                    if (crit != null) {
                        crit = crit.and(criteria4);
                    }
                    else {
                        crit = criteria4;
                    }
                }
                final Criteria periodCrit2 = this.getTimePeriodCriteria("MdDeviceInfo", "LAST_CLOUD_BACKUP_DATE", period2, startDate2, endDate2);
                if (startDate2 != null && endDate2 != null) {
                    viewCtx.getRequest().setAttribute("startDate", (Object)startDate2);
                    viewCtx.getRequest().setAttribute("endDate", (Object)endDate2);
                }
                if (periodCrit2 != null) {
                    if (crit != null) {
                        crit = crit.and(periodCrit2);
                    }
                    else {
                        crit = periodCrit2;
                    }
                }
            }
            else if (unique.equalsIgnoreCase("DevicesByApplicationFlat")) {
                final String platform3 = viewCtx.getRequest().getParameter("platform");
                final String mdAppFilter = viewCtx.getRequest().getParameter("mdAppFilter");
                if (platform3 != null && !platform3.equalsIgnoreCase("0") && !platform3.equalsIgnoreCase("all")) {
                    viewCtx.getRequest().setAttribute("platform", (Object)platform3);
                    final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform3), 0);
                    if (crit != null) {
                        crit = crit.and(platformCri);
                    }
                    else {
                        crit = platformCri;
                    }
                }
                final Long customerID3 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                try {
                    Criteria settingsCriteria = null;
                    final JSONObject settings = AppSettingsDataHandler.getInstance().getAppViewSettings(customerID3);
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
                if (mdAppFilter != null && !mdAppFilter.equalsIgnoreCase("all")) {
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
            else if (unique.equalsIgnoreCase("DeviceByApplicationList")) {
                final Long customerID2 = MSPWebClientUtil.getCustomerID(viewCtx.getRequest());
                final Criteria customerCriteria = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerID2, 0);
                if (crit != null) {
                    crit = crit.and(customerCriteria);
                }
                else {
                    crit = customerCriteria;
                }
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
            this.logger.log(Level.INFO, "ReportsTRAction for view name {0} \nQuery -- {1}", new Object[] { unique, sQuery });
        }
        catch (final QueryConstructionException ex) {
            this.logger.log(Level.SEVERE, "Exception in ReportsTRAction ", (Throwable)ex);
        }
        super.setCriteria(query, viewCtx);
    }
    
    private String getStateValue(final ViewContext viewCtx, final String key) {
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
    
    private void setReportParameters(final ViewContext viewCtx) {
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
    
    private void setMDMGroupFilter(final ViewContext viewCtx) {
        final List mdmGpList = MDMGroupHandler.getCustomGroups();
        if (mdmGpList != null) {
            viewCtx.getRequest().setAttribute("mdmGroupList", (Object)mdmGpList);
        }
    }
    
    private Criteria getTimePeriodCriteria(final String criteriaTable, final String criteriaColumn, final String period, final String startDate, final String endDate) {
        Criteria periodCrit = null;
        if (period != null) {
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
                periodCrit = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)new Long(filter), 4);
            }
        }
        else if (startDate != null && endDate != null) {
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
            }
            catch (final ParseException exp) {
                this.logger.log(Level.WARNING, "Exception occured while parsing start and end date for recently enrolled device report ", exp);
            }
            final Criteria criteria1 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)start, 4);
            final Criteria criteria2 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)end, 6);
            periodCrit = criteria1.and(criteria2);
        }
        return periodCrit;
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
            final Criteria appInsCrit = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"), 0);
            final Criteria appGrpCrit = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)new Column("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
            final Criteria appInstalledCriteria = new Criteria(new Column("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
            final Join appCatJoin = new Join("MdAppToGroupRel", "MdAppCatalogToResource", appGrpCrit.and(appInsCrit.and(appInstalledCriteria)), 1);
            selectQuery.addJoin(appCatJoin);
            final Column appCatalogColumn = new Column("MdAppCatalogToResource", "RESOURCE_ID", "RESOURCE_ID");
            selectQuery.addSelectColumn(appCatalogColumn);
        }
        if (!selectQuery.getTableList().contains(Table.getTable("MdAppCatalogToResource")) && unique.equalsIgnoreCase("DeviceByApplicationList")) {
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), 0);
            selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppCatalogToResource", appGroupCriteria.and(managedDeviceCriteria), 1));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
        }
        return selectQuery;
    }
}
