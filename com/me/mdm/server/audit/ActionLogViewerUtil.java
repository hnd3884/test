package com.me.mdm.server.audit;

import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Join;
import com.me.mdm.api.paging.PagingUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.webclient.audit.EventLogUtil;
import java.util.ArrayList;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ActionLogViewerUtil
{
    Logger logger;
    public static final String MDM_MODULE_NAME = "MDM";
    public static final String CEA_MODULE_NAME = "Conditional Exchange Access";
    private static final String IS_GENERAL_MODULES_NEEDED = "is_general_modules_needed";
    
    public ActionLogViewerUtil() {
        this.logger = Logger.getLogger(ActionLogViewerUtil.class.getName());
    }
    
    public JSONObject getModuleList(final JSONObject requestJSON) throws DataAccessException, JSONException {
        Boolean isGeneralModulesNeeded = false;
        if (requestJSON.has("msg_header") && "true".equalsIgnoreCase(APIUtil.getStringFilter(requestJSON, "is_general_modules_needed"))) {
            isGeneralModulesNeeded = true;
        }
        return this.getModuleList(isGeneralModulesNeeded);
    }
    
    public JSONObject getModuleList(final Boolean isGeneralModulesNeeded) throws DataAccessException, JSONException {
        JSONArray moduleList = this.getSubModuleList("MDM");
        if (isGeneralModulesNeeded) {
            final JSONArray generalModuleList = this.getEventModuleList();
            moduleList = JSONUtil.mergeJSONArray(moduleList, generalModuleList);
        }
        for (int i = 0; i < moduleList.length(); ++i) {
            final JSONObject jsonObject = moduleList.getJSONObject(i);
            if ("MDM".equalsIgnoreCase(jsonObject.getString("module_name")) || (CustomerInfoUtil.isSAS && "Conditional Exchange Access".equalsIgnoreCase(jsonObject.getString("module_name")))) {
                moduleList.remove(i);
                --i;
            }
        }
        final JSONObject moduleJSON = new JSONObject();
        moduleJSON.put("modules", (Object)moduleList);
        return moduleJSON;
    }
    
    private JSONArray getEventModuleList() {
        final List moduleName = new ArrayList();
        final JSONArray moduleList = new JSONArray();
        final Criteria licenseCriteria = EventLogUtil.getInstance().getLicenseCriteriaForEventCode();
        final Criteria desktopModuleCriteria = EventLogUtil.getInstance().getDesktopModuleCriteriaForEventCode();
        final Criteria osdModuleCriteria = EventLogUtil.getInstance().getOSDModuleCriteriaForEventCode();
        final DataObject resultDO = this.getEventCodeDOForModuleCriteria(desktopModuleCriteria, licenseCriteria, osdModuleCriteria);
        try {
            final Iterator resRows = resultDO.getRows("EventCode");
            while (resRows.hasNext()) {
                final Row resRow = resRows.next();
                final String domainName = (String)resRow.get("EVENT_MODULE");
                final String i18nName = I18N.getMsg((String)resRow.get("EVENT_MODULE_LABEL"), new Object[0]);
                if (!moduleName.contains(domainName)) {
                    moduleName.add(domainName);
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("module_name", (Object)domainName);
                    jsonObject.put("module_display_name", (Object)i18nName);
                    moduleList.put((Object)jsonObject);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while exception while getting event module list", ex);
        }
        return moduleList;
    }
    
    private DataObject getEventCodeDOForModuleCriteria(final Criteria desktopModuleCriteria, final Criteria licenseCriteria, final Criteria osdModuleCriteria) {
        Criteria criteria = desktopModuleCriteria;
        if (licenseCriteria != null) {
            if (criteria != null) {
                criteria = criteria.and(licenseCriteria);
            }
            else {
                criteria = licenseCriteria;
            }
        }
        if (osdModuleCriteria != null) {
            if (criteria != null) {
                criteria = criteria.and(osdModuleCriteria);
            }
            else {
                criteria = osdModuleCriteria;
            }
        }
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EventCode"));
        query.setCriteria(criteria);
        final Column eventIDColumn = Column.getColumn("EventCode", "EVENT_ID");
        final Column eventModuleColumn = Column.getColumn("EventCode", "EVENT_MODULE");
        final Column eventModuleLabel = Column.getColumn("EventCode", "EVENT_MODULE_LABEL");
        query.addSelectColumn(eventIDColumn);
        query.addSelectColumn(eventModuleColumn);
        query.addSelectColumn(eventModuleLabel);
        final SortColumn sortCol = new SortColumn(eventModuleColumn, true);
        query.addSortColumn(sortCol);
        DataObject resultDO = null;
        try {
            resultDO = SyMUtil.getPersistence().get(query);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while getting event module list ", ex);
        }
        return resultDO;
    }
    
    protected JSONArray getSubModuleList(final String moduleName) throws DataAccessException {
        final List subModuleList = new ArrayList();
        final JSONArray moduleSummary = new JSONArray();
        final Criteria modCriteria = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)moduleName, 0);
        final Criteria subModCriteria = new Criteria(Column.getColumn("EventCode", "CONTAIN_SUB_MODULE"), (Object)true, 0);
        final Criteria cri = modCriteria.and(subModCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventCode"));
        selectQuery.setCriteria(cri);
        final Column subModuleColumn = Column.getColumn("EventCode", "SUB_MODULE");
        final Column subModuleLabelColumn = Column.getColumn("EventCode", "SUB_MODULE_LABEL");
        selectQuery.addSelectColumn(subModuleColumn);
        selectQuery.addSelectColumn(subModuleLabelColumn);
        selectQuery.addSelectColumn(Column.getColumn("EventCode", "EVENT_ID"));
        final DataObject resultDO = SyMUtil.getPersistence().get(selectQuery);
        if (resultDO.isEmpty()) {
            return moduleSummary;
        }
        try {
            final Iterator resRows = resultDO.getRows("EventCode");
            while (resRows.hasNext()) {
                final Row resRow = resRows.next();
                final String subModule = (String)resRow.get("SUB_MODULE");
                final String subLabel = I18N.getMsg((String)resRow.get("SUB_MODULE_LABEL"), new Object[0]);
                if (!subModuleList.contains(subModule)) {
                    subModuleList.add(subModule);
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("module_name", (Object)subModule);
                    jsonObject.put("module_display_name", (Object)subLabel);
                    moduleSummary.put((Object)jsonObject);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Caught exception while getting event sub module list", ex);
        }
        return moduleSummary;
    }
    
    public JSONObject getActionLogEntries(final JSONObject message) {
        try {
            message.getJSONObject("msg_body");
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        try {
            this.validateActionLogFilterData(message);
            return this.getActionLogForFilter(message);
        }
        catch (final APIHTTPException e2) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[] { e3.getMessage() });
        }
    }
    
    private void validateActionLogFilterData(final JSONObject message) throws Exception {
        final JSONObject requestJSON = message.getJSONObject("msg_body");
        try {
            if (!requestJSON.has("from_time") || !requestJSON.has("filter_type")) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            final Long fromDate = requestJSON.getLong("from_time");
            if (fromDate > System.currentTimeMillis()) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            final int filterType = requestJSON.getInt("filter_type");
            if (filterType == 1) {
                if (!requestJSON.has("to_time")) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
                final Long toDate = requestJSON.getLong("to_time");
                if (fromDate > toDate) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
            }
            else if (filterType == 2) {
                if (!requestJSON.has("no_of_days")) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
                final int days = requestJSON.getInt("no_of_days");
                if (days > 90 || days < 1) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
            }
            if (requestJSON.has("modules")) {
                final JSONArray modules = requestJSON.getJSONArray("modules");
                if (!this.validateModules(modules)) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
            }
            if (requestJSON.has("user")) {
                final String userName = String.valueOf(requestJSON.get("user"));
                if (!this.validateUser(userName)) {
                    throw new APIHTTPException("COM0005", new Object[0]);
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Exception in validating audit log", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in validating audit log", ex);
            throw ex;
        }
    }
    
    private boolean validateModules(final JSONArray filterModules) throws DataAccessException, JSONException {
        final JSONArray modulesAvailable = (JSONArray)this.getModuleList(true).get("modules");
        for (int length = filterModules.length(), i = 0; i < length; ++i) {
            final String filterModuleName = filterModules.getString(i);
            Boolean isValidModule = false;
            for (int j = 0; j < modulesAvailable.length(); ++j) {
                final JSONObject availableModule = modulesAvailable.getJSONObject(j);
                final String availableModuleName = availableModule.getString("module_name");
                if (availableModuleName.equals(filterModuleName)) {
                    isValidModule = true;
                    break;
                }
            }
            if (!isValidModule) {
                return false;
            }
        }
        return true;
    }
    
    private boolean validateUser(final String user) {
        final List dcUserList = DMUserHandler.getDCUsers();
        for (final Object dcUser : dcUserList) {
            final Hashtable dcUserHash = (Hashtable)dcUser;
            final String name = dcUserHash.get("NAME");
            if (name.equals(user)) {
                return true;
            }
        }
        return false;
    }
    
    private JSONObject getActionLogForFilter(final JSONObject message) throws Exception {
        final JSONObject requestJSON = message.getJSONObject("msg_body");
        requestJSON.put("customer_id", (Object)APIUtil.getCustomerID(message));
        final JSONObject response = new JSONObject();
        final JSONArray eventLogs = new JSONArray();
        SelectQuery sQuery = this.getActionLogQuery(requestJSON);
        SelectQuery countQuery = this.getActionLogQuery(requestJSON);
        sQuery = this.addSelectColumnsForData(sQuery, requestJSON);
        countQuery = this.addSelectColumnsForCount(countQuery);
        final int count = DBUtil.getRecordCount(countQuery);
        final JSONObject metaData = new JSONObject();
        metaData.put("count", count);
        response.put("metadata", (Object)metaData);
        if (count != 0) {
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(message);
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
            if (pagingJSON != null) {
                response.put("paging", (Object)pagingJSON);
            }
            sQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (!dO.isEmpty()) {
                final Iterator eventLogItr = dO.getRows("EventLog");
                while (eventLogItr.hasNext()) {
                    final JSONObject eventLog = new JSONObject();
                    final Row eventLogRow = eventLogItr.next();
                    eventLog.put("EVENT_LOG_ID", eventLogRow.get("EVENT_LOG_ID"));
                    final String eventRemarks = (String)eventLogRow.get("EVENT_REMARKS");
                    eventLog.put("EVENT_REMARKS", (Object)eventRemarks);
                    final String eventRemarkArgs = (String)eventLogRow.get("EVENT_REMARKS_ARGS");
                    eventLog.put("EVENT_REMARKS_ARGS", (Object)eventRemarkArgs);
                    eventLog.put("event_remarks_text", (Object)I18NUtil.transformRemarks(eventRemarks, eventRemarkArgs));
                    eventLog.put("LOGON_USER_NAME", eventLogRow.get("LOGON_USER_NAME"));
                    eventLog.put("EVENT_TIMESTAMP", eventLogRow.get("EVENT_TIMESTAMP"));
                    final Criteria eventCriteria = new Criteria(Column.getColumn("EventCode", "EVENT_ID"), eventLogRow.get("EVENT_ID"), 0);
                    final Row eventRow = dO.getRow("EventCode", eventCriteria);
                    eventLog.put("EVENT_MODULE_LABEL", eventRow.get("EVENT_MODULE_LABEL"));
                    eventLog.put("SUB_MODULE_LABEL", eventRow.get("SUB_MODULE_LABEL"));
                    eventLog.put("SUB_MODULE", eventRow.get("SUB_MODULE"));
                    eventLog.put("EVENT_TYPE", eventRow.get("EVENT_TYPE"));
                    eventLog.put("EVENT_ID", eventRow.get("EVENT_ID"));
                    eventLogs.put((Object)eventLog);
                }
            }
        }
        response.put("events", (Object)eventLogs);
        return response;
    }
    
    private SelectQuery getActionLogQuery(final JSONObject requestJSON) throws JSONException {
        final Long fromTime = requestJSON.getLong("from_time");
        final int filterType = requestJSON.getInt("filter_type");
        final JSONArray modules = requestJSON.optJSONArray("modules");
        SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventLog"));
        final Join eventLogJoin = new Join("EventLog", "EventTimeDuration", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1);
        final Join ResourceEventLogJoin = new Join("EventLog", "ResourceEventLogRel", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1);
        final Join eventCodeJoin = new Join("EventLog", "EventCode", new String[] { "EVENT_ID" }, new String[] { "EVENT_ID" }, 2);
        final Join customerEventJoin = new Join("EventLog", "CustomerEventLog", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1);
        final Join resourceJoin = new Join("ResourceEventLogRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        sQuery.addJoin(eventLogJoin);
        sQuery.addJoin(ResourceEventLogJoin);
        sQuery.addJoin(eventCodeJoin);
        sQuery.addJoin(customerEventJoin);
        sQuery.addJoin(resourceJoin);
        Criteria criteria;
        final Criteria mdmCriteria = criteria = new Criteria(Column.getColumn("EventCode", "EVENT_MODULE"), (Object)"MDM", 0);
        final Criteria desktopModuleCri = EventLogUtil.getInstance().getDesktopModuleCriteriaForEventCode();
        if (desktopModuleCri != null) {
            criteria = criteria.or(desktopModuleCri);
        }
        if (modules != null && modules.length() > 0) {
            final List moduleList = JSONUtil.getInstance().convertJSONArrayTOList(modules);
            final Criteria moduleCriteria = new Criteria(Column.getColumn("EventCode", "SUB_MODULE"), (Object)moduleList.toArray(), 8);
            criteria = criteria.and(moduleCriteria);
        }
        if (requestJSON.has("user")) {
            final String user = String.valueOf(requestJSON.get("user"));
            final Criteria userCriteria = new Criteria(Column.getColumn("EventLog", "LOGON_USER_NAME"), (Object)user, 0);
            criteria = criteria.and(userCriteria);
        }
        if (filterType == 1) {
            final Long toTime = requestJSON.getLong("to_time");
            final Criteria fromTimecriteria = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)fromTime, 4);
            final Criteria endTimeCriteria = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)toTime, 6);
            criteria = criteria.and(fromTimecriteria).and(endTimeCriteria);
        }
        else {
            final int days = requestJSON.getInt("no_of_days");
            final Long toTime2 = fromTime - days * 24L * 60L * 60L * 1000L;
            final Criteria fromTimecriteria2 = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)toTime2, 4);
            final Criteria endTimeCriteria2 = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)fromTime, 6);
            criteria = criteria.and(fromTimecriteria2).and(endTimeCriteria2);
        }
        final Long customerId = requestJSON.optLong("customer_id");
        criteria = criteria.and(Column.getColumn("CustomerEventLog", "CUSTOMER_ID"), (Object)customerId, 0);
        sQuery.setCriteria(criteria);
        sQuery = RBDAUtil.getInstance().getRBDAQuery(sQuery);
        return sQuery;
    }
    
    private SelectQuery addSelectColumnsForData(final SelectQuery sQuery, final JSONObject requestJSON) {
        sQuery.addSelectColumn(Column.getColumn("EventCode", "EVENT_MODULE_LABEL"));
        sQuery.addSelectColumn(Column.getColumn("EventCode", "SUB_MODULE_LABEL"));
        sQuery.addSelectColumn(Column.getColumn("EventCode", "SUB_MODULE"));
        sQuery.addSelectColumn(Column.getColumn("EventCode", "EVENT_TYPE"));
        sQuery.addSelectColumn(Column.getColumn("EventCode", "EVENT_ID"));
        sQuery.addSelectColumn(Column.getColumn("EventLog", "EVENT_LOG_ID"));
        sQuery.addSelectColumn(Column.getColumn("EventLog", "EVENT_REMARKS"));
        sQuery.addSelectColumn(Column.getColumn("EventLog", "EVENT_REMARKS_ARGS"));
        sQuery.addSelectColumn(Column.getColumn("EventLog", "LOGON_USER_NAME"));
        sQuery.addSelectColumn(Column.getColumn("EventLog", "EVENT_TIMESTAMP"));
        sQuery.addSelectColumn(Column.getColumn("EventLog", "EVENT_ID"));
        final Boolean ascending = requestJSON.optBoolean("sort_newest_first", true);
        final SortColumn sortColumn = new SortColumn("EventLog", "EVENT_TIMESTAMP", !ascending);
        sQuery.addSortColumn(sortColumn);
        return sQuery;
    }
    
    private SelectQuery addSelectColumnsForCount(final SelectQuery sQuery) {
        sQuery.addSelectColumn(Column.getColumn("EventLog", "EVENT_LOG_ID").count());
        return sQuery;
    }
    
    public boolean addDynamicEventLog(final JSONObject jsonObject) throws APIHTTPException {
        try {
            final String sUserName = APIUtil.getUserName(jsonObject);
            final JSONObject messageBody = jsonObject.optJSONObject("msg_body");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            if (messageBody != null && messageBody.has("message")) {
                String message = messageBody.optString("message");
                message = DMIAMEncoder.encodeSQLForNonPatternContext(message);
                message = DMIAMEncoder.encodeJavaScript(message);
                message = DMIAMEncoder.encodeHTML(message);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sUserName, message, null, customerId);
                return true;
            }
            return false;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while adding dynamic Event Log", ex);
            throw new APIHTTPException("COM0015", new Object[] { "The given message is corrupted" });
        }
    }
}
