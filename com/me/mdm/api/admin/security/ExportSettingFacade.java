package com.me.mdm.api.admin.security;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import com.me.devicemanagement.framework.webclient.export.ExportSettingsUtil;
import org.json.JSONObject;

public class ExportSettingFacade
{
    public static final String EXPORT_REDACT = "export_redact";
    private static final String SCHEDULED_EXPORT_REDACT = "scheduled_report_redact";
    public static final String EXPORT_TYPE = "export_type";
    private static final String TECHNICIAN_OPTION = "technician_option";
    public static final String SELECTED_OPTION = "selected_option";
    public static final int LEAVE_TO_TECH = 4;
    
    public JSONObject getExportSettings() throws Exception {
        final JSONObject exportInfo = new JSONObject();
        final JSONObject exportSettings = new JSONObject();
        final ExportSettingsUtil settings = new ExportSettingsUtil();
        final int exportType = ExportSettingsUtil.getRedactTypeOfExportSettings();
        exportSettings.put("export_type", exportType);
        if (exportType == 4) {
            final String exportOptions = ExportSettingsUtil.getAdminSelectedExportRedactTypeForUser();
            final String[] options = exportOptions.split(",");
            final JSONArray option = new JSONArray();
            for (final String val : options) {
                if (!val.equals("")) {
                    option.put((Object)val);
                }
            }
            exportSettings.put("technician_option", (Object)option);
        }
        exportInfo.put("export_redact", (Object)exportSettings);
        final JSONObject scheduledReportSettings = new JSONObject();
        final int schduleExportType = ExportSettingsUtil.getScheduleExportRedactType();
        scheduledReportSettings.put("export_type", schduleExportType);
        if (schduleExportType == 4) {
            final String exportOptions2 = ExportSettingsUtil.getAdminSelectedScheduleReportRedactTypeForUser();
            final String[] options2 = exportOptions2.split(",");
            final JSONArray option2 = new JSONArray();
            for (final String val2 : options2) {
                if (!val2.equals("")) {
                    option2.put((Object)val2);
                }
            }
            scheduledReportSettings.put("technician_option", (Object)option2);
        }
        exportInfo.put("scheduled_report_redact", (Object)scheduledReportSettings);
        return exportInfo;
    }
    
    public void saveExportSettings(final JSONObject message) throws Exception {
        JSONObject requestJSON;
        try {
            requestJSON = message.getJSONObject("msg_body");
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        final JSONObject exportSettings = (JSONObject)requestJSON.get("export_redact");
        final int exportType = Integer.parseInt(String.valueOf(exportSettings.get("export_type")));
        final ExportSettingsUtil settings = new ExportSettingsUtil();
        ExportSettingsUtil.setRedactTypeOfExportSettings(Integer.valueOf(exportType));
        if (exportType == 4) {
            final JSONArray exportArray = exportSettings.getJSONArray("technician_option");
            String option = "";
            for (int i = 0; i < exportArray.length(); ++i) {
                if (i == 0) {
                    option = "" + exportArray.get(i);
                }
                else {
                    option = option + "," + exportArray.get(i);
                }
            }
            ExportSettingsUtil.setAdminSelectedExportRedactTypeForUser(option);
        }
        final JSONObject scheExportSettings = (JSONObject)requestJSON.get("scheduled_report_redact");
        final int schExportType = Integer.parseInt(String.valueOf(scheExportSettings.get("export_type")));
        ExportSettingsUtil.setScheduleReportRedactType(Integer.valueOf(schExportType));
        if (schExportType == 4) {
            final JSONArray exportArray2 = scheExportSettings.getJSONArray("technician_option");
            String option2 = "";
            for (int j = 0; j < exportArray2.length(); ++j) {
                if (j == 0) {
                    option2 = "" + exportArray2.get(j);
                }
                else {
                    option2 = option2 + "," + exportArray2.get(j);
                }
            }
            ExportSettingsUtil.setAdminSelectedScheduleRedactTypeForUser(option2);
        }
    }
    
    public JSONObject getUserExportRedactType(final Long userId) throws Exception {
        final JSONObject exportInfo = new JSONObject();
        final ExportSettingsUtil settings = new ExportSettingsUtil();
        final int redactType = ExportSettingsUtil.getRedactTypeOfExportSettings();
        exportInfo.put("export_type", redactType);
        if (redactType == 4) {
            final String exportOptions = ExportSettingsUtil.getAdminSelectedExportRedactTypeForUser();
            final String[] options = exportOptions.split(",");
            final JSONArray option = new JSONArray();
            for (final String val : options) {
                if (!val.equals("")) {
                    option.put((Object)val);
                }
            }
            exportInfo.put("technician_option", (Object)option);
            final String lastRedactType = ExportSettingsUtil.getUserSpecifiedRedactType(userId);
            if (lastRedactType != null && exportOptions.contains(lastRedactType)) {
                exportInfo.put("selected_option", (Object)lastRedactType);
            }
        }
        return exportInfo;
    }
    
    public void setUserExportRedactType(final JSONObject message, final Long userId) throws Exception {
        JSONObject requestJSON;
        try {
            requestJSON = message.getJSONObject("msg_body");
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        final int redactLevel = requestJSON.optInt("export_type");
        ExportSettingsUtil.setRedactTypeForUser(redactLevel, userId);
    }
}
