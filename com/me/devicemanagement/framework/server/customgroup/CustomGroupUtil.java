package com.me.devicemanagement.framework.server.customgroup;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Map;
import org.json.JSONObject;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Locale;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

public class CustomGroupUtil
{
    private static CustomGroupUtil cgUtil;
    protected static Logger logger;
    protected static List customGroupListenerList;
    
    public static CustomGroupUtil getInstance() {
        if (CustomGroupUtil.cgUtil == null) {
            CustomGroupUtil.cgUtil = new CustomGroupUtil();
        }
        return CustomGroupUtil.cgUtil;
    }
    
    public void addCustomGroupListener(final CustomGroupListener listener) {
        CustomGroupUtil.customGroupListenerList.add(listener);
    }
    
    public static void removeCustomGroupListener(final CustomGroupListener listener) {
        CustomGroupUtil.customGroupListenerList.remove(listener);
    }
    
    public void addCGActionLog(final HttpServletRequest request, final String groupName, final int eventConstant) {
        String i18n = "";
        String args = null;
        final Locale locale = request.getLocale();
        try {
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            final Long userID = SYMClientUtil.getCurrentlyLoggedInUserID(request);
            switch (eventConstant) {
                case 1201:
                case 2071: {
                    i18n = "desktopcentral.admin.customGroup.created_successfuly";
                    args = groupName;
                    break;
                }
                case 1202:
                case 2072: {
                    i18n = "desktopcentral.admin.customGroup.updated_successfully";
                    args = groupName;
                    break;
                }
                case 1203:
                case 2073: {
                    i18n = "desktopcentral.admin.customGroup.deleted_successfully";
                    args = groupName;
                    break;
                }
            }
            DCEventLogUtil.getInstance().addEvent(eventConstant, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), null, i18n, args, false, customerId);
        }
        catch (final Exception ex) {
            CustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in....", ex);
        }
    }
    
    public HashMap getResourceProperties(final Long resID) {
        final HashMap propertyHash = new HashMap();
        try {
            Connection conn = null;
            DataSet ds = null;
            final SelectQuery resPropQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join cusGroupJoin = new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join cusGroupUserJoin = new Join("CustomGroup", "CustomGroupExtn", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            resPropQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            resPropQuery.addJoin(cusGroupJoin);
            resPropQuery.addJoin(cusGroupUserJoin);
            final Criteria propertyCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resID, 0);
            resPropQuery.setCriteria(propertyCriteria);
            try {
                conn = RelationalAPI.getInstance().getConnection();
                ds = RelationalAPI.getInstance().executeQuery((Query)resPropQuery, conn);
                if (ds.next()) {
                    propertyHash.put("GROUP_ID", ds.getValue("RESOURCE_ID"));
                    propertyHash.put("NAME", ds.getValue("NAME"));
                    propertyHash.put("DOMAIN_NAME", ds.getValue("DOMAIN_NETBIOS_NAME"));
                    propertyHash.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                    propertyHash.put("DB_ADDED_TIME", ds.getValue("DB_ADDED_TIME"));
                    propertyHash.put("DB_UPDATED_TIME", ds.getValue("DB_UPDATED_TIME"));
                    propertyHash.put("CUSTOMER_ID", ds.getValue("CUSTOMER_ID"));
                    propertyHash.put("GROUP_TYPE", ds.getValue("GROUP_TYPE"));
                    propertyHash.put("GROUP_CATEGORY", ds.getValue("GROUP_CATEGORY"));
                    propertyHash.put("IS_EDITABLE", ds.getValue("IS_EDITABLE"));
                    propertyHash.put("DESCRIPTION", ds.getValue("GROUP_DESCRIPTION"));
                    propertyHash.put("CREATED_BY", ds.getValue("CREATED_BY"));
                    propertyHash.put("LAST_MODIFIED_BY", ds.getValue("LAST_MODIFIED_BY"));
                }
            }
            catch (final Exception e) {
                CustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in getGroupMemberCount Query Execution....", e);
            }
            finally {
                this.closeConnection(conn, ds);
            }
            CustomGroupUtil.logger.log(Level.INFO, "Properties obtained : " + propertyHash);
        }
        catch (final Exception e2) {
            CustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in getResourceProperties....", e2);
        }
        return propertyHash;
    }
    
    public void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception ex) {
            CustomGroupUtil.logger.log(Level.WARNING, "Exception occurred while closing dataset....", ex);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            CustomGroupUtil.logger.log(Level.WARNING, "Exception occurred in closing connection....", ex);
        }
    }
    
    public ArrayList getResourcePropertyList(final DataObject resourceDO) {
        ArrayList resourceList = null;
        HashMap resourceDetailMap = null;
        try {
            resourceList = new ArrayList();
            final Iterator resourceItr = resourceDO.getRows("Resource");
            while (resourceItr.hasNext()) {
                final Row resourceRow = resourceItr.next();
                resourceDetailMap = new HashMap();
                resourceDetailMap.put("NAME", resourceRow.get("NAME"));
                resourceDetailMap.put("RESOURCE_ID", resourceRow.get("RESOURCE_ID"));
                resourceDetailMap.put("RESOURCE_TYPE", resourceRow.get("RESOURCE_TYPE"));
                resourceDetailMap.put("CUSTOMER_ID", resourceRow.get("CUSTOMER_ID"));
                resourceList.add(resourceDetailMap);
            }
        }
        catch (final Exception e) {
            CustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in getResourcePropertyList....", e);
        }
        return resourceList;
    }
    
    public void sendTextResponseThroughAjax(final HttpServletRequest request, final HttpServletResponse response, final String resultText) {
        PrintWriter writer = null;
        try {
            response.setContentType("text/plain");
            writer = response.getWriter();
            writer.println(resultText);
            writer.close();
        }
        catch (final Exception e) {
            CustomGroupUtil.logger.log(Level.WARNING, " sendTextResponseThroughAjax() : Exception : ", e);
            writer.println("false");
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception e2) {
                CustomGroupUtil.logger.log(Level.WARNING, " sendTextResponseThroughAjax() : Exception : ", e2);
            }
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception e3) {
                CustomGroupUtil.logger.log(Level.WARNING, " sendTextResponseThroughAjax() : Exception : ", e3);
            }
        }
    }
    
    public SelectQuery getQueryforGroupControllers(final SelectQuery selectQuery, final boolean isMDM) {
        try {
            final Table resourceTable = Table.getTable("Resource");
            final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            subSQ.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            final Column column_config_data = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID").count();
            column_config_data.setColumnAlias("MEMBER_RESOURCE_ID");
            subSQ.addSelectColumn(column_config_data);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
            list.add(groupByCol);
            final GroupByClause memberGroupBy = new GroupByClause(list);
            subSQ.setGroupByClause(memberGroupBy);
            final DerivedTable groupDerievedTab = new DerivedTable("CustomGroupMemberRel", (Query)subSQ);
            selectQuery.addJoin(new Join(resourceTable, (Table)groupDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
        }
        catch (final Exception e) {
            CustomGroupUtil.logger.log(Level.WARNING, "Exception occoured in queryforGroupControllerViews....", e);
        }
        return selectQuery;
    }
    
    public static List getCGListenersList() {
        return CustomGroupUtil.customGroupListenerList;
    }
    
    public void invokeCustomGroupListeners(final CustomGroupEvent cgEvent, final int eventType) {
        if (cgEvent == null) {
            return;
        }
        try {
            final JSONObject qData = new JSONObject();
            qData.put("resource_Id", (Object)cgEvent.customGroupID);
            qData.put("customer_Id", (Object)cgEvent.customerID);
            qData.put("resource_props", (Map)cgEvent.cgProperties);
            final Long postedTime = System.currentTimeMillis();
            final String qFileName = cgEvent.customerID + "-" + cgEvent.customGroupID + "-" + postedTime + ".txt";
            final DCQueue queue = DCQueueHandler.getQueue("cg-listener-data");
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = postedTime;
            queueData.queueData = qData.toString();
            queueData.queueDataType = eventType;
            queueData.fileName = qFileName;
            queue.addToQueue(queueData);
        }
        catch (final Exception ex) {
            CustomGroupUtil.logger.log(Level.SEVERE, "Exception in invokeCustomGroupListeners ", ex);
        }
    }
    
    static {
        CustomGroupUtil.cgUtil = null;
        CustomGroupUtil.logger = Logger.getLogger("CustomGroupLogger");
        CustomGroupUtil.customGroupListenerList = new ArrayList(5);
    }
}
