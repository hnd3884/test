package com.me.ems.framework.common.api.v1.service;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.export.ExportSettingsUtil;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.ExportSettingsService;

public class ExportSettingsServiceImpl implements ExportSettingsService
{
    protected static Logger logger;
    public static final int EXPORTREPORT = 307;
    
    @Override
    public Map getExportSettings(final User user) throws APIException {
        final Map exportSettingsDetails = new HashMap();
        try {
            exportSettingsDetails.put("exportRedactType", ExportSettingsUtil.getRedactTypeOfExportSettings());
            final int scheduleReportRedactType = ExportSettingsUtil.getScheduleExportRedactType();
            exportSettingsDetails.put("scheduleReportRedactType", scheduleReportRedactType);
            if (scheduleReportRedactType == 0) {
                exportSettingsDetails.put("isAdmin", user.isAdminUser());
            }
            exportSettingsDetails.put("isPasswordMandatory", ExportSettingsUtil.isPasswordMandatory());
            exportSettingsDetails.put("exportAvailableOptions", ExportSettingsUtil.getAdminSelectedExportRedactTypeForUser());
            exportSettingsDetails.put("exportScheduleAvailableOptions", ExportSettingsUtil.getAdminSelectedScheduleReportRedactTypeForUser());
        }
        catch (final Exception ex) {
            ExportSettingsServiceImpl.logger.log(Level.SEVERE, "Error while retrieving Export Settings", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.gdpr.export.failed");
        }
        return exportSettingsDetails;
    }
    
    @Override
    public Map currentExportRedactType(final User user) throws APIException {
        final Map currentExportRedactTypedetails = new HashMap();
        try {
            final int redactType = ExportSettingsUtil.getRedactTypeOfExportSettings();
            currentExportRedactTypedetails.put("redactType", redactType);
            if (redactType == 0) {
                currentExportRedactTypedetails.put("isAdmin", user.isAdminUser());
            }
            else if (redactType == 4) {
                final String adminApprovedTypes = ExportSettingsUtil.getAdminSelectedExportRedactTypeForUser();
                currentExportRedactTypedetails.put("adminApprovedOptions", adminApprovedTypes);
                final String lastRedactType = ExportSettingsUtil.getUserSpecifiedRedactType(user.getUserID());
                if (lastRedactType != null && adminApprovedTypes.contains(lastRedactType)) {
                    currentExportRedactTypedetails.put("lastSetRedactTypeByUser", lastRedactType);
                }
                else {
                    currentExportRedactTypedetails.put("lastSetRedactTypeByUser", "0");
                }
            }
        }
        catch (final Exception ex) {
            ExportSettingsServiceImpl.logger.log(Level.SEVERE, "Error while currentExportRedactType", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.gdpr.export.failed");
        }
        return currentExportRedactTypedetails;
    }
    
    @Override
    public void userChosenRedactLevel(final User user, final Map redactLevelDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (ExportSettingsUtil.getRedactTypeOfExportSettings() != Integer.parseInt("4")) {
                throw new APIException("USER0002");
            }
            if (!redactLevelDetails.containsKey("redactLevel")) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "GENERIC0003", null, new String[] { "ExportSettings" });
            }
            final int redactLevel = redactLevelDetails.get("redactLevel");
            if (ExportSettingsUtil.getAdminSelectedExportRedactTypeForUser().contains(redactLevel + "")) {
                ExportSettingsUtil.setRedactTypeForUser(redactLevel, user.getUserID());
                redactLevelDetails.put("isAvailableForUser", true);
            }
            else {
                redactLevelDetails.put("isAvailableForUser", false);
            }
        }
        catch (final Exception ex) {
            ExportSettingsServiceImpl.logger.log(Level.SEVERE, "Error while userChosenRedactLevel", ex);
            if (ex instanceof APIException) {
                throw (APIException)ex;
            }
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.gdpr.export.failed");
        }
    }
    
    @Override
    public void validateExportSettings(final Map exportSettingsDetails, final User user) throws APIException {
        try {
            final Set paramKeys = exportSettingsDetails.keySet();
            final List requiredFields = new LinkedList();
            requiredFields.add("exportRedactType");
            requiredFields.add("scheduleReportRedactType");
            if (!paramKeys.containsAll(requiredFields)) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "GENERIC0003", null, new String[] { "ExportSettings" });
            }
            final int exportViewSettings = exportSettingsDetails.get("exportRedactType");
            final int schReportSettings = exportSettingsDetails.get("scheduleReportRedactType");
            if (schReportSettings == 0 && exportViewSettings == 0) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "GENERIC0003", null, new String[] { "ExportSettings" });
            }
            exportSettingsDetails.put("userName", user.getName());
        }
        catch (final Exception ex) {
            if (ex instanceof APIException) {
                throw ex;
            }
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.gdpr.export.failed");
        }
    }
    
    @Override
    public boolean saveExportSettings(final Map exportSettingsDetails, final Long customerID, final HttpServletRequest httpServletRequest) throws APIException {
        boolean exportSaveStatus = false;
        try {
            final int exportViewSettings = exportSettingsDetails.get("exportRedactType");
            final int schReportSettings = exportSettingsDetails.get("scheduleReportRedactType");
            ExportSettingsServiceImpl.logger.info("going to update Export settings value.");
            if (exportViewSettings != 0 && ExportSettingsUtil.setRedactTypeOfExportSettings(exportViewSettings) == 3001) {
                final Object remarkArgs = exportSettingsDetails.get("userName") + "@@@" + this.textForType(exportViewSettings);
                DCEventLogUtil.getInstance().addEvent(307, exportSettingsDetails.get("userName"), null, "dc.gdpr.audit.admin.export.remarks", remarkArgs, true, customerID);
                exportSaveStatus = true;
            }
            if (schReportSettings != 0 && ExportSettingsUtil.setScheduleReportRedactType(schReportSettings) == 3001) {
                final Object remarkArgs = exportSettingsDetails.get("userName") + "@@@" + this.textForType(schReportSettings);
                DCEventLogUtil.getInstance().addEvent(307, exportSettingsDetails.get("userName"), null, "dc.gdpr.audit.admin.sch.remarks", remarkArgs, true, customerID);
                if (ExportSettingsUtil.getScheduleExportRedactType() == 0 && schReportSettings == 4) {
                    ExportSettingsServiceImpl.logger.info("result of setting redact type of Schedule Tasks = " + ScheduleReportUtil.setRedactTypeForAllScheduleTasks(exportSettingsDetails.get("exportPIOldScheduleReportType")));
                }
                else if (schReportSettings != 4) {
                    ExportSettingsServiceImpl.logger.info("result of setting redact type of Schedule Tasks = " + ScheduleReportUtil.setRedactTypeForAllScheduleTasks(schReportSettings));
                }
                exportSaveStatus = true;
            }
            final int exportSuccessCode = ExportSettingsUtil.setAdminSelectedExportRedactTypeForUser(exportSettingsDetails.get("exportAvailableOptions"));
            final int scheduleReportExportSuccessCode = ExportSettingsUtil.setAdminSelectedScheduleRedactTypeForUser(exportSettingsDetails.get("exportScheduleAvailableOptions"));
            if (exportSuccessCode == 3001 || scheduleReportExportSuccessCode == 3001) {
                exportSaveStatus = true;
            }
        }
        catch (final Exception ex) {
            ExportSettingsServiceImpl.logger.log(Level.SEVERE, "Error while submitting export settings", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.gdpr.export.failed");
        }
        finally {
            SecurityOneLineLogger.log("Security_Management", "Export_Settings_Modify", exportSettingsDetails.toString(), Level.INFO);
        }
        return exportSaveStatus;
    }
    
    private String textForType(final int type) throws Exception {
        switch (type) {
            case 1: {
                return I18N.getMsg("dc.gdpr.export.dropdown.2", new Object[0]);
            }
            case 2: {
                return I18N.getMsg("dc.gdpr.export.dropdown.3", new Object[0]);
            }
            case 3: {
                return I18N.getMsg("dc.gdpr.export.dropdown.4", new Object[0]);
            }
            case 4: {
                return I18N.getMsg("dc.gdpr.export.dropdown.5", new Object[0]);
            }
            default: {
                return "";
            }
        }
    }
    
    @Override
    public Long validateCustomer(final String customerIdStr) throws APIException {
        Long customerID = null;
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
        }
        Label_0072: {
            if (customerIdStr != null && !customerIdStr.isEmpty()) {
                if (customerIdStr.equalsIgnoreCase("all")) {
                    try {
                        customerID = CustomerInfoUtil.getInstance().getCustomerIDForLoginUser();
                        break Label_0072;
                    }
                    catch (final Exception ex) {
                        throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.gdpr.export.failed");
                    }
                }
                customerID = Long.parseLong(customerIdStr);
            }
        }
        if (customerID == null) {
            throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0004", null, new String[] { "X-Customer" });
        }
        return customerID;
    }
    
    static {
        ExportSettingsServiceImpl.logger = Logger.getLogger("SecurityLogger");
    }
}
