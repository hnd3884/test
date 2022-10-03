package com.adventnet.sym.webclient.mdm.reports;

import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import java.text.ParseException;
import org.apache.commons.lang3.time.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class DeviceBatteryDetailsViewController extends MDMEmberSqlViewController
{
    public static Logger logger;
    
    @Override
    public String getVariableValue(final ViewContext viewContext, final String variableName) {
        if (variableName.equals("CUSTOMERCRITERIA")) {
            return CustomerInfoUtil.getInstance().getCustomerCritForACSQLString("CustomerInfo.CUSTOMER_ID");
        }
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equalsIgnoreCase("PLATFORMCRITERIA")) {
            if (this.isScheduleReport(viewContext)) {
                final String scheduledCriteria = super.getVariableValue(viewContext, variableName);
                return MDMStringUtils.isEmpty(scheduledCriteria) ? " " : (" and " + scheduledCriteria);
            }
            final String platform = viewContext.getRequest().getParameter("platform");
            if (platform == null || platform.equalsIgnoreCase("0") || platform.equals("all")) {
                return " ";
            }
            final int platformType = Integer.parseInt(platform);
            viewContext.getRequest().setAttribute("platform", (Object)platform);
            return " and ManagedDevice.PLATFORM_TYPE = " + platformType;
        }
        else if (variableName.equals("GROUPCRITERIA")) {
            if (this.isScheduleReport(viewContext)) {
                final String scheduledCriteria = super.getVariableValue(viewContext, variableName);
                return MDMStringUtils.isEmpty(scheduledCriteria) ? " " : (" and " + scheduledCriteria);
            }
            final String groupFilter = viewContext.getRequest().getParameter("mdmGroupId");
            String groupCriteria = " ";
            if (groupFilter != null && !groupFilter.equals("all")) {
                final Long customGRPID = new Long(groupFilter);
                groupCriteria = groupCriteria + " and CustomGroupMemberRel.GROUP_RESOURCE_ID =" + customGRPID + " ";
                viewContext.getRequest().setAttribute("mdmGroupId", (Object)Long.parseLong(groupFilter));
            }
            final List mdmGpList = MDMGroupHandler.getCustomGroups();
            if (mdmGpList != null) {
                viewContext.getRequest().setAttribute("mdmGroupList", (Object)mdmGpList);
            }
            return groupCriteria;
        }
        else if (variableName.equalsIgnoreCase("SCRITERIA")) {
            final String[] searchColumns = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewContext, "SEARCH_COLUMN");
            final String[] searchValues = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewContext, "SEARCH_VALUE");
            if (searchColumns == null || searchValues == null) {
                return " ";
            }
            return this.getSearchCriteria(searchColumns, searchValues);
        }
        else if (variableName.equalsIgnoreCase("BATTERYLEVELCRITERIA")) {
            if (this.isScheduleReport(viewContext)) {
                final String scheduledCriteria = super.getVariableValue(viewContext, variableName);
                return MDMStringUtils.isEmpty(scheduledCriteria) ? " " : (" and " + scheduledCriteria);
            }
            final String batteryLevelComparator = viewContext.getRequest().getParameter("battery_level_comparator");
            final String batteryLevel = viewContext.getRequest().getParameter("battery_level");
            if (batteryLevel == null || batteryLevelComparator == null || batteryLevel.equalsIgnoreCase("all") || batteryLevelComparator.equalsIgnoreCase("all")) {
                return " ";
            }
            viewContext.getRequest().setAttribute("battery_level", (Object)batteryLevel);
            viewContext.getRequest().setAttribute("battery_level_comparator", (Object)batteryLevelComparator);
            return " and " + this.getBatteryLevelCriteria(batteryLevel, batteryLevelComparator);
        }
        else if (variableName.equalsIgnoreCase("PERIODCRITERIA")) {
            if (this.isScheduleReport(viewContext)) {
                final String scheduledCriteria = super.getVariableValue(viewContext, variableName);
                return MDMStringUtils.isEmpty(scheduledCriteria) ? " " : (" and " + scheduledCriteria);
            }
            final String period = viewContext.getRequest().getParameter("period");
            final String startDate = this.getStateValue(viewContext, "startDate");
            final String endDate = this.getStateValue(viewContext, "endDate");
            String periodCriteria = "";
            if (period != null && !period.equalsIgnoreCase("all")) {
                periodCriteria = getTimePeriodCriteria(period, startDate, endDate);
            }
            viewContext.getRequest().setAttribute("period", (Object)period);
            return periodCriteria;
        }
        else {
            if (variableName.equalsIgnoreCase("SJOIN")) {
                boolean isJoinNeeded;
                if (this.isScheduleReport(viewContext)) {
                    final String scheduledCriteria2 = super.getVariableValue(viewContext, "GROUPCRITERIA");
                    isJoinNeeded = !MDMStringUtils.isEmpty(scheduledCriteria2);
                }
                else {
                    final String groupFilter2 = viewContext.getRequest().getParameter("mdmGroupId");
                    isJoinNeeded = (groupFilter2 != null && !groupFilter2.equals("all"));
                }
                return isJoinNeeded ? " LEFT JOIN CustomGroupMemberRel ON ManagedDevice.RESOURCE_ID = CustomGroupMemberRel.MEMBER_RESOURCE_ID  " : " ";
            }
            if (variableName.equalsIgnoreCase("DEVICECRITERIA")) {
                final String deviceFilter = viewContext.getRequest().getParameter("deviceId");
                String deviceCriteria = " ";
                if (deviceFilter != null) {
                    final long deviceId = Long.parseLong(deviceFilter);
                    deviceCriteria = " and MANAGEDDEVICE.RESOURCE_ID = " + deviceId;
                }
                return deviceCriteria;
            }
            return super.getVariableValue(viewContext, variableName);
        }
    }
    
    private boolean isScheduleReport(final ViewContext viewContext) {
        final HttpServletRequest request = viewContext.getRequest();
        final String scheduledReport = request.getParameter("isScheduledReport");
        return scheduledReport != null && scheduledReport.equalsIgnoreCase("true");
    }
    
    private String getBatteryLevelCriteria(final String batteryLevel, final String batteryLevelComparator) {
        String batteryLevelCriteria = " ";
        final double bLevel = Double.parseDouble(batteryLevel);
        if (batteryLevelComparator.equalsIgnoreCase("above") || batteryLevelComparator.equalsIgnoreCase("greater than")) {
            batteryLevelCriteria = " MDDEVICEBATTERYDETAILS.BATTERY_LEVEL > " + bLevel;
        }
        else if (batteryLevelComparator.equalsIgnoreCase("below") || batteryLevelComparator.equalsIgnoreCase("less than")) {
            batteryLevelCriteria = " MDDEVICEBATTERYDETAILS.BATTERY_LEVEL < " + bLevel;
        }
        else if (batteryLevelComparator.equalsIgnoreCase("greater or equal")) {
            batteryLevelCriteria = " MDDEVICEBATTERYDETAILS.BATTERY_LEVEL >= " + bLevel;
        }
        else if (batteryLevelComparator.equalsIgnoreCase("less or equal")) {
            batteryLevelCriteria = " MDDEVICEBATTERYDETAILS.BATTERY_LEVEL <= " + bLevel;
        }
        else if (batteryLevelComparator.equalsIgnoreCase("equal")) {
            batteryLevelCriteria = " MDDEVICEBATTERYDETAILS.BATTERY_LEVEL = " + bLevel;
        }
        else if (batteryLevelComparator.equalsIgnoreCase("not equal")) {
            batteryLevelCriteria = " MDDEVICEBATTERYDETAILS.BATTERY_LEVEL != " + bLevel;
        }
        return batteryLevelCriteria;
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
        return value;
    }
    
    public static String getTimePeriodCriteria(final String period, final String startDate, final String endDate) {
        String periodCriteria = "";
        if (!period.equalsIgnoreCase("custom")) {
            int noOfDays = Integer.parseInt(period);
            if (noOfDays != 0) {
                long today = -1L;
                try {
                    final GregorianCalendar cal = new GregorianCalendar();
                    final GregorianCalendar cal2 = new GregorianCalendar();
                    cal2.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
                    today = cal2.getTime().getTime();
                }
                catch (final Exception e) {
                    DeviceBatteryDetailsViewController.logger.log(Level.WARNING, "Exception while getting today's value ", e);
                }
                if (today == -1L) {
                    today = new Date().getTime();
                }
                final long filter = today - --noOfDays * 24 * 60 * 60 * 1000L;
                periodCriteria = " and MDDEVICEBATTERYDETAILS.DEVICE_UTC_TIME >= " + filter;
            }
        }
        else if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                final Date startCriteriaDate = sdf.parse(startDate);
                final Date endCriteriaDate = sdf.parse(endDate);
                final Date endDateRounded = DateUtils.ceiling(endCriteriaDate, 5);
                long start = startCriteriaDate.getTime();
                long end = endDateRounded.getTime();
                if (start > end) {
                    final long temp = start;
                    start = end;
                    end = temp;
                }
                periodCriteria = " and MDDEVICEBATTERYDETAILS.DEVICE_UTC_TIME >= " + start + " and MDDEVICEBATTERYDETAILS.DEVICE_UTC_TIME <= " + end;
            }
            catch (final ParseException exp) {
                DeviceBatteryDetailsViewController.logger.log(Level.WARNING, "Exception occurred while parsing start and end date for recently enrolled device report ", exp);
            }
        }
        return periodCriteria;
    }
    
    private String getSearchCriteria(String[] searchColumns, String[] searchValues) {
        searchColumns = searchColumns[0].split(",");
        searchValues = searchValues[0].split(",");
        final StringBuilder resultSearchString = new StringBuilder();
        for (int i = 0; i < searchColumns.length; ++i) {
            final String searchColumn = searchColumns[i];
            String searchValue = searchValues[i];
            searchValue = DMIAMEncoder.encodeSQLForNonPatternContext(searchValue);
            final String s = searchColumn;
            switch (s) {
                case "ManagedDeviceExtn.NAME": {
                    resultSearchString.append(" and (ManagedDeviceExtn.NAME like '%").append(searchValue).append("%')");
                    break;
                }
                case "ManagedUser.FIRST_NAME": {
                    resultSearchString.append(" and (ManagedUser.FIRST_NAME like '%").append(searchValue).append("%')");
                    break;
                }
                case "MdDeviceBatteryDetails.BATTERY_LEVEL": {
                    resultSearchString.append(" and (MdDeviceBatteryDetails.BATTERY_LEVEL = ").append(searchValue).append(")");
                    break;
                }
                case "ManagedUser.MIDDLE_NAME": {
                    resultSearchString.append(" and (ManagedUser.MIDDLE_NAME like '%").append(searchValue).append("%')");
                    break;
                }
                case "ManagedUser.LAST_NAME": {
                    resultSearchString.append(" and (ManagedUser.LAST_NAME like '%").append(searchValue).append("%')");
                    break;
                }
                case "ManagedUser.DISPLAY_NAME": {
                    resultSearchString.append(" and (ManagedUser.DISPLAY_NAME like '%").append(searchValue).append("%')");
                    break;
                }
                case "MdDeviceInfo.SERIAL_NUMBER": {
                    resultSearchString.append(" and (MdDeviceInfo.SERIAL_NUMBER like '%").append(searchValue).append("%')");
                    break;
                }
                case "DEVICE_USER_NAME": {
                    resultSearchString.append(" and (DEVICE_USER_NAME like '%").append(searchValue).append("%')");
                    break;
                }
                case "ManagedUser.EMAIL_ADDRESS": {
                    resultSearchString.append(" and (ManagedUser.EMAIL_ADDRESS like '%").append(searchValue).append("%')");
                    break;
                }
                case "MdModelInfo.MODEL": {
                    resultSearchString.append(" and (MdModelInfo.MODEL like '%").append(searchValue).append("%')");
                    break;
                }
                case "ManagedDevice.REGISTERED_TIME": {
                    resultSearchString.append(" and (ManagedDevice.REGISTERED_TIME like '%").append(searchValue).append("%')");
                    break;
                }
            }
        }
        return resultSearchString.toString();
    }
    
    static {
        DeviceBatteryDetailsViewController.logger = Logger.getLogger(DeviceBatteryDetailsViewController.class.getName());
    }
}
