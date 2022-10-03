package com.me.devicemanagement.framework.webclient.export;

import java.util.Enumeration;
import com.adventnet.client.view.common.ExportUtils;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;

public class ExportPiiValueHandler
{
    public static Logger log;
    public static final String FORCED_MASK = "1";
    public static final String FORCED_HIDE = "2";
    public static final String FORCED_NONE = "3";
    public static final String LET_USER_CHOOSE = "4";
    public static final String DEFAULT = "0";
    
    public static String getMaskedValue(final ViewContext viewContext) throws Exception {
        String schedule_id = null;
        String task_id = null;
        final String[] schedule_rep_id = viewContext.getRequest().getParameterValues("scheduleID");
        if (schedule_rep_id != null) {
            schedule_id = schedule_rep_id[0].toString();
        }
        if (schedule_id != null && !schedule_id.isEmpty()) {
            task_id = ScheduleReportUtil.getTaskIdFromScheduleId(schedule_id);
        }
        String forced_type = null;
        if (task_id == null) {
            final JSONObject export_redact_json = (JSONObject)ExportSettingsUtil.getExportConfiguration().get("fw_export_redact_type");
            forced_type = String.valueOf(export_redact_json.get("enable"));
            if (forced_type.equals("0")) {
                forced_type = String.valueOf(export_redact_json.get("default"));
            }
            if (forced_type.equals("4")) {
                final Long user_id = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                forced_type = ExportSettingsUtil.getUserSpecifiedRedactType(user_id);
            }
        }
        else {
            forced_type = ScheduleReportUtil.getRedactTypeFromTaskId(task_id);
        }
        if (forced_type == null) {
            return "NONE";
        }
        final String s = forced_type;
        switch (s) {
            case "2": {
                return "HIDE";
            }
            case "3": {
                return "NONE";
            }
            case "1": {
                return "MASK";
            }
            default: {
                return null;
            }
        }
    }
    
    public static Properties getMaskedValueMap(final ViewContext viewContext) throws Exception {
        final String redact_type = getMaskedValue(viewContext);
        final Properties properties = ExportUtils.getExportMaskingConfig(viewContext.getModel().getViewConfiguration());
        if (redact_type == null || redact_type.equals("MASK")) {
            return properties;
        }
        if (properties != null) {
            final Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                properties.setProperty(enumeration.nextElement().toString(), redact_type);
            }
        }
        return properties;
    }
    
    static {
        ExportPiiValueHandler.log = Logger.getLogger(ExportPiiValueHandler.class.getName());
    }
}
