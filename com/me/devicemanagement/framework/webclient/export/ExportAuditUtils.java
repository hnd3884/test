package com.me.devicemanagement.framework.webclient.export;

import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import javax.swing.table.TableModel;
import com.me.devicemanagement.framework.webclient.admin.DBQueryExecutorAPI;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportAttrBean;
import javax.resource.NotSupportedException;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.i18n.I18N;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.client.view.common.ExportAuditModel;
import java.util.logging.Logger;

public class ExportAuditUtils
{
    public static boolean isExportAuditLoggerEnabled;
    public static boolean isExportEventLoggerEnabled;
    public static boolean isExportAlertLoggerEnabled;
    public static Logger log;
    private static String exportAuditLoggerConfig;
    private static String exportEventLoggerConfig;
    private static String exportAlertLoggerConfig;
    
    public static void auditExport(final ExportAuditModel auditModel) {
        final Long user_id = auditModel.getAccountID();
        final Long exported_time = auditModel.getExportedTime();
        final int export_type = auditModel.getExportType();
        final ViewContext viewContext = auditModel.getViewContext();
        final TableViewModel tableViewModel = (TableViewModel)viewContext.getViewModel();
        try {
            final List<Row> list = tableViewModel.getColumnList();
            String selected_pii_columns = "";
            for (final Row row : list) {
                final String isVisible = row.get("VISIBLE").toString();
                if (isVisible.equals("true")) {
                    final String column_alias = row.get("COLUMNALIAS").toString();
                    if (MetaDataUtil.getAttribute(column_alias + ".pii") == null) {
                        continue;
                    }
                    selected_pii_columns = I18N.getMsgFromPropFile(row.get("DISPLAYNAME").toString(), new Object[] { Locale.getDefault() }) + "," + selected_pii_columns;
                }
            }
            if (!selected_pii_columns.isEmpty()) {
                selected_pii_columns = selected_pii_columns.substring(0, selected_pii_columns.length() - 1);
            }
            final String viewName = viewContext.getUniqueId();
            final String userName = DMUserHandler.getUserName(WebClientUtil.getAccountId());
            if (ExportAuditUtils.isExportAuditLoggerEnabled) {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                final Row row2 = new Row("ReportExportAudit");
                row2.set("VIEW_NAME", (Object)viewName);
                row2.set("USER_ID", (Object)user_id);
                row2.set("EXPORT_TYPE", (Object)export_type);
                row2.set("EXPORTED_TIME", (Object)exported_time);
                row2.set("SELECTED_COLUMNS", (Object)selected_pii_columns);
                dataObject.addRow(row2);
                SyMUtil.getPersistence().update(dataObject);
            }
            if (ExportAuditUtils.isExportEventLoggerEnabled) {
                final HashMap<String, Object> properties = ExportAuditUtils.class.newInstance().getEventDetails(selected_pii_columns, export_type, auditModel);
                if (properties != null) {
                    DCEventLogUtil.getInstance().addEvent(properties.get("event_id"), userName, properties.get("resMap"), properties.get("remarks").toString(), properties.get("remarks_arg"), true, properties.get("customer_id"));
                }
            }
            if (ExportAuditUtils.isExportAlertLoggerEnabled) {
                final HashMap<String, Object> properties = ExportAuditUtils.class.newInstance().getAlertDetails(selected_pii_columns, export_type, auditModel);
                if (properties != null) {
                    AlertsUtil.getInstance().addAlert(Long.parseLong(properties.get("alertType").toString()), properties.get("alertRemarks").toString(), properties.get("remarksArgs"));
                }
            }
        }
        catch (final DataAccessException dae) {
            ExportAuditUtils.log.log(Level.INFO, "Exception while saving export audit details" + dae);
        }
        catch (final JSONException jsonExcep) {
            ExportAuditUtils.log.log(Level.INFO, "Exception while saving export audit details" + jsonExcep);
        }
        catch (final Exception ex) {
            ExportAuditUtils.log.log(Level.INFO, "Exception while saving export audit details" + ex);
        }
    }
    
    public static ExportAuditUtils getExportAuditImpl() {
        try {
            final ExportAuditUtils exportAuditUtils = (ExportAuditUtils)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_AUDIT_EVENT_IMPL_CLASS")).newInstance();
            return exportAuditUtils;
        }
        catch (final NotSupportedException nse) {
            ExportAuditUtils.log.log(Level.INFO, "Multiple events for a single event is not supported" + nse);
        }
        catch (final ClassNotFoundException cnfe) {
            ExportAuditUtils.log.log(Level.INFO, "Class not found" + cnfe);
        }
        catch (final Exception e) {
            ExportAuditUtils.log.log(Level.INFO, "Exception while creating class for the implemented class" + e);
        }
        return null;
    }
    
    public HashMap<String, Object> getEventDetails(final String selected_pii_columns, final int export_type, final ExportAuditModel auditModel) {
        final ExportAuditUtils exportAuditUtils = getExportAuditImpl();
        if (exportAuditUtils != null) {
            return exportAuditUtils.getEventDetails(selected_pii_columns, export_type, auditModel);
        }
        return null;
    }
    
    private HashMap<String, Object> getAlertDetails(final String selected_pii_columns, final int export_type, final ExportAuditModel auditModel) {
        final ExportAuditUtils exportAuditUtils = getExportAuditImpl();
        if (exportAuditUtils != null) {
            return exportAuditUtils.getAlertDetails(selected_pii_columns, export_type, auditModel);
        }
        return null;
    }
    
    public static HashMap<String, Object> getScheduleEventDetails(final String selected_columns, final int export_type, final QueryReportAttrBean queryReportAttrBean) {
        final ExportAuditUtils exportAuditUtils = getExportAuditImpl();
        if (exportAuditUtils != null) {
            return getScheduleEventDetails(selected_columns, export_type, queryReportAttrBean);
        }
        return null;
    }
    
    public static HashMap<String, Object> getScheduleQueryAlertDetails(final String selected_columns, final int export_type, final QueryReportAttrBean queryReportAttrBean) {
        final ExportAuditUtils exportAuditUtils = getExportAuditImpl();
        if (exportAuditUtils != null) {
            return getScheduleQueryAlertDetails(selected_columns, export_type, queryReportAttrBean);
        }
        return null;
    }
    
    public static void queryReportLogger(final QueryReportAttrBean queryRepBean, final int export_type) {
        try {
            final String query = queryRepBean.getQueryVal();
            final DBQueryExecutorAPI dbQueryExecutorAPI = (DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance();
            TableModel tableModel = null;
            if (dbQueryExecutorAPI != null) {
                tableModel = (TableModel)dbQueryExecutorAPI.getTableModel(query, null, false);
            }
            final Long user_id = WebClientUtil.getAccountId();
            final String userName = DMUserHandler.getUserName(user_id);
            final int columnCount = tableModel.getColumnCount();
            String selected_columns = "";
            for (int count = 0; count < columnCount; ++count) {
                selected_columns = tableModel.getColumnName(count) + "," + selected_columns;
            }
            if (ExportAuditUtils.isExportAuditLoggerEnabled) {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                final Row row = new Row("ReportExportAudit");
                row.set("VIEW_NAME", (Object)queryRepBean.getQueryNameVal());
                row.set("USER_ID", (Object)user_id);
                row.set("EXPORT_TYPE", (Object)export_type);
                row.set("EXPORTED_TIME", (Object)System.currentTimeMillis());
                row.set("SELECTED_COLUMNS", (Object)selected_columns);
                dataObject.addRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
            if (ExportAuditUtils.isExportEventLoggerEnabled) {
                final HashMap<String, Object> properties = getScheduleEventDetails(selected_columns, export_type, queryRepBean);
                DCEventLogUtil.getInstance().addEvent(properties.get("event_id"), userName, properties.get("resMap"), properties.get("remarks").toString(), properties.get("remarks_arg"), true, properties.get("customer_id"));
            }
            if (ExportAuditUtils.isExportAlertLoggerEnabled) {
                final HashMap<String, Object> properties = getScheduleQueryAlertDetails(selected_columns, export_type, queryRepBean);
                AlertsUtil.getInstance().addAlert(Long.parseLong(properties.get("alertType").toString()), properties.get("alertRemarks").toString(), properties.get("remarksArgs"));
            }
        }
        catch (final DataAccessException dae) {
            ExportAuditUtils.log.log(Level.INFO, "Exception while saving schedule export audit details" + dae);
        }
        catch (final Exception e) {
            ExportAuditUtils.log.log(Level.INFO, "Exception while saving schedule export audit details" + e);
        }
    }
    
    static {
        ExportAuditUtils.log = Logger.getLogger(ExportAuditUtils.class.getName());
        ExportAuditUtils.exportAuditLoggerConfig = "export_audit_logger";
        ExportAuditUtils.exportEventLoggerConfig = "export_event_logger";
        ExportAuditUtils.exportAlertLoggerConfig = "export_alert_logger";
        ExportAuditUtils.log.log(Level.INFO, "Going to load export logging settings");
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            ExportAuditUtils.isExportAuditLoggerEnabled = Boolean.parseBoolean(((JSONObject)frameworkConfigurations.get(ExportAuditUtils.exportAuditLoggerConfig)).get("enable").toString());
            ExportAuditUtils.isExportEventLoggerEnabled = Boolean.parseBoolean(((JSONObject)frameworkConfigurations.get(ExportAuditUtils.exportEventLoggerConfig)).get("enable").toString());
            ExportAuditUtils.isExportAlertLoggerEnabled = Boolean.parseBoolean(((JSONObject)frameworkConfigurations.get(ExportAuditUtils.exportAlertLoggerConfig)).get("enable").toString());
        }
        catch (final JSONException jsonExcep) {
            ExportAuditUtils.log.log(Level.WARNING, "Exception while handling framework configuration json ", (Throwable)jsonExcep);
        }
    }
}
