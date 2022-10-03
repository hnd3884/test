package com.adventnet.sym.webclient.mdm.reports;

import org.json.JSONObject;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.webclient.common.MDMWebClientUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class DeviceWithAppViewController extends MDMEmberSqlViewController
{
    public Logger logger;
    
    public DeviceWithAppViewController() {
        this.logger = Logger.getLogger(DeviceWithAppViewController.class.getName());
    }
    
    private String getSubQuery(final String subqueryCriteria, final String comparator) {
        String subquery = " ${OPERATORVAL} (SELECT MdInstalledAppResourceRel.RESOURCE_ID FROM MdAppDetails inner join MdInstalledAppResourceRel ON MdAppDetails.APP_ID = MdInstalledAppResourceRel.APP_ID ${APPVIEWAPPGROUPJOIN} WHERE  ${APPNAME} ${HIDESYSAPP}) ";
        subquery = subquery.replace("${OPERATORVAL}", comparator);
        subquery = subquery.replace("${APPNAME}", subqueryCriteria);
        subquery = subquery.replace("${APPVIEWAPPGROUPJOIN}", AppSettingsDataHandler.getInstance().getAppViewAppGroupJoinString());
        subquery = subquery.replace("${HIDESYSAPP}", AppSettingsDataHandler.getInstance().getGloballAppViewSettingCriteriaString("MdInstalledAppResourceRel"));
        return subquery;
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        final HttpServletRequest request = viewCtx.getRequest();
        String appName = request.getParameter("appName");
        appName = ((appName != null) ? MDMUtil.getInstance().decodeURIComponentEquivalent(appName) : null);
        final String swNameStr = DMIAMEncoder.encodeSQLForNonPatternContext(appName);
        final String appCriteria = request.getParameter("appCriteria");
        final String groupFilter = request.getParameter("mdmGroupId");
        final String platform = request.getParameter("platform");
        if (variableName.equals("SUBQUERY")) {
            final String isScheduleReport = String.valueOf(request.getParameter("isScheduledReport"));
            final String scheduleId = String.valueOf(request.getParameter("scheduleID"));
            final ReportCriteriaUtil reportUtil = ReportCriteriaUtil.getInstance();
            if (isScheduleReport.equalsIgnoreCase("true") && !reportUtil.isCustomScheduleReport(scheduleId)) {
                try {
                    final ArrayList CriteriaWithComparator = reportUtil.getPlaceHolderCriteriaStringWithComparator(isScheduleReport, scheduleId, request.getParameter("toolID"), variableName);
                    if (!CriteriaWithComparator.get(0).toString().equalsIgnoreCase("")) {
                        String subquery = subquery = this.getSubQuery(CriteriaWithComparator.get(0).toString(), CriteriaWithComparator.get(1).toString());
                        return subquery;
                    }
                }
                catch (final DataAccessException ex) {
                    Logger.getLogger(DeviceWithAppViewController.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                }
                return this.getSubQuery("MdAppDetails.APP_NAME = ' '", "IN");
            }
            String comparator = "";
            String appname = "";
            if (appCriteria != null && !appCriteria.isEmpty()) {
                if (appCriteria.equals("equal") || appCriteria.equals("like")) {
                    comparator = "IN";
                }
                else if (appCriteria.equals("notequal") || appCriteria.equals("notlike")) {
                    comparator = "NOT IN";
                }
            }
            else {
                comparator = "IN";
            }
            if (swNameStr != null && !swNameStr.isEmpty()) {
                if (appCriteria.equals("equal") || appCriteria.equals("notequal")) {
                    appname = "MdAppDetails.APP_NAME = '" + swNameStr + "'";
                }
                else if (appCriteria.equals("like") || appCriteria.equals("notlike")) {
                    appname = "MdAppDetails.APP_NAME like '%" + swNameStr + "%'";
                }
            }
            else {
                appname = "MdAppDetails.APP_NAME = ' '";
            }
            return this.getSubQuery(appname, comparator);
        }
        else {
            if (variableName.equals("CUSTOMERCRITERIA")) {
                String customerCrit = CustomerInfoUtil.getInstance().getCustomerCritForACSQLString("CustomerInfo.CUSTOMER_ID");
                if (!customerCrit.equals("")) {
                    customerCrit = " and " + customerCrit;
                }
                return customerCrit;
            }
            if (variableName.equals("GROUPCRI")) {
                String groupCriteria = "";
                if (groupFilter != null && !"all".equals(groupFilter)) {
                    final Long customGRPID = new Long(groupFilter);
                    groupCriteria = groupCriteria + " and CustomGroupMemberRel.GROUP_RESOURCE_ID=" + customGRPID + " ";
                }
                return groupCriteria;
            }
            if (variableName.equalsIgnoreCase("SCRITERIA")) {
                final String[] searchValuesString = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
                final String[] searchColumnsString = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
                final JSONObject responseJSON = MDMWebClientUtil.getInstance().encodeViewSearchParameters(searchValuesString, searchColumnsString);
                final String encodedSearchText = responseJSON.optString("SEARCH_VALUE");
                final String encodedSearchColumn = responseJSON.optString("SEARCH_COLUMN");
                if (MDMUtil.isStringEmpty(encodedSearchText)) {
                    viewCtx.setStateOrURLStateParam("SEARCH_VALUE", (Object)null);
                    viewCtx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)null);
                }
                else {
                    viewCtx.setStateOrURLStateParam("SEARCH_VALUE", (Object)encodedSearchText);
                    viewCtx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)encodedSearchColumn);
                }
                return super.getVariableValue(viewCtx, variableName);
            }
            if (variableName.equals("PLATFORMCRITERIA")) {
                String platformCri = "";
                if (platform != null && !"0".equals(platform) && !platform.equalsIgnoreCase("all")) {
                    platformCri = " AND ManagedDevice.PLATFORM_TYPE = " + platform;
                }
                return platformCri;
            }
            if (variableName.equalsIgnoreCase("GROUPJOIN")) {
                String groupCriteria = "";
                if (groupFilter != null && !"all".equals(groupFilter)) {
                    groupCriteria = "LEFT JOIN CustomGroupMemberRel on customgroupmemberrel.MEMBER_RESOURCE_ID = ManagedDevice.RESOURCE_ID";
                }
                return groupCriteria;
            }
            return super.getVariableValue(viewCtx, variableName);
        }
    }
}
