package com.me.mdm.api.message;

import java.util.Hashtable;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Set;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.WritableDataObject;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.webclient.common.OnlineUrlLoader;
import java.util.StringTokenizer;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;
import java.util.Properties;
import java.util.Collection;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SortColumn;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.List;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMMessageProvider
{
    public static final String MSG_ID = "msg_id";
    public static final String PAGE_ID = "page_id";
    public static final String PAGE_NAME = "page_name";
    public static final String MSG_TITLE = "msg_title";
    public static final String MSG_GROUP_ID = "msg_group_id";
    public static final String MSG_CONTENT = "msg_content";
    public static final String MSG_NAME = "msg_name";
    public static final String ENABLE_USER_CLOSE = "enable_user_close";
    public static final String MESSAGE_LIST = "msg_list";
    public static final String MSG_TYPE = "msg_type";
    public static final String MSG = "msg";
    private static MDMMessageProvider msgProBase;
    String className;
    private Logger out;
    private static final int CUSTOMER_USER_SCOPE = 2;
    
    public MDMMessageProvider() {
        this.className = MDMMessageProvider.class.getName();
        this.out = Logger.getLogger(this.className);
    }
    
    public static synchronized MDMMessageProvider getInstance() {
        if (MDMMessageProvider.msgProBase == null) {
            MDMMessageProvider.msgProBase = new MDMMessageProvider();
        }
        return MDMMessageProvider.msgProBase;
    }
    
    public JSONObject getMessageJson(final String msgPageViewName, final Long customerID, final Long loginID, final Long userID, final JSONObject apiRequestJSON) {
        final JSONObject messageJson = new JSONObject();
        try {
            final List roleIdList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            this.out.log(Level.FINE, "loginID : {0}", loginID);
            this.out.log(Level.FINE, "roleIdList : {0}", roleIdList);
            this.out.log(Level.FINE, "customerID : {0}", customerID);
            final List priorityRoleIds = this.getMsgPrecedenceData(msgPageViewName, roleIdList, loginID);
            if (priorityRoleIds == null || priorityRoleIds.isEmpty()) {
                this.out.log(Level.FINE, "No messages found for msgPageViewName : {0} and roleIdList :{1}", new Object[] { msgPageViewName, roleIdList });
                return messageJson;
            }
            final Criteria pageCriteria = new Criteria(Column.getColumn("MsgPage", "MSG_PAGE_NAME"), (Object)msgPageViewName, 0);
            final DataObject priorityLevelDO = SyMUtil.getPersistence().get("MsgPage", pageCriteria);
            final Boolean considerCount = (Boolean)priorityLevelDO.getFirstValue("MsgPage", "DISPLAY_TYPE");
            final Integer msgCount = (Integer)priorityLevelDO.getFirstValue("MsgPage", "MSG_COUNT");
            final Long pageId = (Long)priorityLevelDO.getFirstValue("MsgPage", "PAGE_ID");
            this.out.log(Level.FINE, "loginID : {0} ; customerID : {1} ; considerCount : {2} ; msgCount : {3}", new Object[] { loginID, customerID, considerCount, msgCount });
            final DataObject msgDO = this.getMsgData(msgPageViewName, customerID, userID, priorityRoleIds);
            this.out.log(Level.FINE, "Message DO : {0}", msgDO);
            if (msgDO != null && !msgDO.isEmpty()) {
                messageJson.put("page_id", (Object)pageId);
                messageJson.put("page_name", (Object)msgPageViewName);
                final JSONArray multiMsgList = this.getMsgProperties(msgDO, customerID, apiRequestJSON);
                if (multiMsgList.length() != 0) {
                    for (int i = 0; i != multiMsgList.length(); ++i) {
                        final JSONArray singleMsgList = (JSONArray)multiMsgList.get(i);
                        if (singleMsgList.length() != 0) {
                            for (int j = 0; j != singleMsgList.length(); ++j) {
                                final JSONObject msgArrayList2 = (JSONObject)singleMsgList.get(j);
                                final int msgType = (int)msgArrayList2.get("msg_type");
                                if (msgType == 2) {
                                    final Long msgId = (Long)msgArrayList2.get("msg_id");
                                    this.updateMsgStatusForInfoMessage(userID, msgId);
                                }
                            }
                        }
                    }
                }
                messageJson.put("msg", (Object)multiMsgList);
            }
            else {
                this.out.log(Level.FINE, "No messages to be shown : {0} and roleIdList :{1}", new Object[] { msgPageViewName, roleIdList });
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in getting msg property..............", e);
        }
        return messageJson;
    }
    
    private DataObject getActionToStatusData(final String actionName) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgGroupToAction"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(new Join("MsgGroupToAction", "MsgContent", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
        query.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_CONTENT_ID" }, new String[] { "MSG_CONTENT_ID" }, 1));
        final Criteria nameCriteria = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0, false);
        query.setCriteria(nameCriteria);
        final DataObject contentDO = SyMUtil.getPersistence().get(query);
        return contentDO;
    }
    
    private DataObject getCustomerCloseObject(final ArrayList customerCloseList, final Long customerID) throws Exception {
        final Object[] closedList = customerCloseList.toArray();
        final Criteria contentCriteria = new Criteria(Column.getColumn("MsgToCustomerStatus", "MSG_CONTENT_ID"), (Object)closedList, 8, false);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MsgToCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
        final DataObject dataObj = SyMUtil.getPersistence().get("MsgToCustomerStatus", contentCriteria.and(customerCriteria));
        for (final Long contentId : customerCloseList) {
            final Row row = new Row("MsgToCustomerStatus");
            row.set("MSG_CONTENT_ID", (Object)contentId);
            row.set("CUSTOMER_ID", (Object)customerID);
            final Row tempRow = dataObj.getRow("MsgToCustomerStatus", row);
            if (tempRow == null) {
                row.set("MSG_STATUS", (Object)Boolean.FALSE);
                dataObj.addRow(row);
            }
            else {
                tempRow.set("MSG_STATUS", (Object)Boolean.FALSE);
                dataObj.updateRow(tempRow);
            }
        }
        return dataObj;
    }
    
    private String[] listToArray(final List itemList) {
        final int arrSize = itemList.size();
        int count = 0;
        final String[] itemArray = new String[arrSize];
        final Iterator iter = itemList.iterator();
        while (iter.hasNext()) {
            itemArray[count] = iter.next();
            ++count;
        }
        return itemArray;
    }
    
    private List getMsgPrecedenceData(final String msgPageViewName, final List roleIdList, final Long loginID) {
        DataObject priorityLevelDO = null;
        ArrayList returnList = null;
        try {
            this.out.log(Level.FINE, "Getting role priority for  msgPageViewName : {0}", msgPageViewName);
            final String[] roles = this.listToArray(roleIdList);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCUserModuleExtn"));
            query.addJoin(new Join("DCUserModuleExtn", "UMModule", new String[] { "MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2));
            query.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            Criteria roleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roles, 8);
            query.setCriteria(roleCriteria);
            final SortColumn prioritySort = new SortColumn(Column.getColumn("UMModule", "PRECEDENCE_LEVEL"), false);
            query.addSortColumn(prioritySort);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            priorityLevelDO = SyMUtil.getPersistence().get(query);
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (!priorityLevelDO.isEmpty()) {
                returnList = new ArrayList();
                final ArrayList checkList = new ArrayList();
                final Iterator iter = priorityLevelDO.getRows("UMModule");
                final boolean userInAdminRole = DMUserHandler.isUserInAdminRole(loginID);
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    if (isMSP && !userInAdminRole) {
                        roleCriteria = new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), row.get("ROLE_ID"), 0);
                        final Row aaaRoleRow = priorityLevelDO.getRow("AaaRole", roleCriteria);
                        String roleName = (String)aaaRoleRow.get("NAME");
                        roleName = roleName.toLowerCase();
                        if (!roleName.contains("ca_write") && !roleName.startsWith("mdm_")) {
                            continue;
                        }
                    }
                    final Long roleId = (Long)row.get("ROLE_ID");
                    final Long moduleId = (Long)row.get("DC_MODULE_ID");
                    if (!checkList.contains(moduleId)) {
                        checkList.add(moduleId);
                        returnList.add(roleId);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in getting precedence in the page dataobject : " + priorityLevelDO + "| roleIdList :" + roleIdList, e);
        }
        return returnList;
    }
    
    private ArrayList getUserCloseList(final String msgPageViewName, final Long userId) {
        DataObject data = null;
        final ArrayList arrList = new ArrayList();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgPage"));
            query.addSelectColumn(Column.getColumn("MsgPage", "MSG_PAGE_ID"));
            query.addSelectColumn(Column.getColumn("MsgContentUserStatus", "MSG_PAGE_ID"));
            query.addSelectColumn(Column.getColumn("MsgContentUserStatus", "USER_ID"));
            query.addSelectColumn(Column.getColumn("MsgPage", "MSG_PAGE_NAME"));
            query.addSelectColumn(Column.getColumn("MsgContentUserStatus", "MSG_CONTENT_ID"));
            query.addSelectColumn(Column.getColumn("MsgContentUserStatus", "MSG_DISPLAY_COUNT"));
            query.addJoin(new Join("MsgPage", "MsgContentUserStatus", new String[] { "MSG_PAGE_ID" }, new String[] { "MSG_PAGE_ID" }, 2));
            final Criteria userCriteria = new Criteria(Column.getColumn("MsgContentUserStatus", "USER_ID"), (Object)userId, 0, false);
            final Criteria viewCriteria = new Criteria(Column.getColumn("MsgPage", "MSG_PAGE_NAME"), (Object)msgPageViewName, 0, false);
            query.setCriteria(userCriteria.and(viewCriteria));
            data = SyMUtil.getPersistence().get(query);
            final Iterator userClosedIter = data.getRows("MsgContentUserStatus");
            while (userClosedIter.hasNext()) {
                final Row summaryROW = userClosedIter.next();
                final Long msgcontentid = (Long)summaryROW.get("MSG_CONTENT_ID");
                if (msgcontentid != null) {
                    final Long displaycount = (Long)summaryROW.get("MSG_DISPLAY_COUNT");
                    if (displaycount == null || displaycount != 0L) {
                        continue;
                    }
                    arrList.add(msgcontentid);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting list of messages closed by user : " + userId + " data : " + data, e);
        }
        return arrList;
    }
    
    private ArrayList getCustomerCloseList(final Long customerID) {
        final ArrayList arrList = new ArrayList();
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("MsgToCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject data = SyMUtil.getPersistence().get("MsgToCustomerStatus", customerCriteria);
            final Iterator customerClosedIter = data.get("MsgToCustomerStatus", "MSG_CONTENT_ID");
            while (customerClosedIter.hasNext()) {
                arrList.add(customerClosedIter.next());
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, e, () -> "Exception while getting list of messages closed by customer : " + n);
        }
        return arrList;
    }
    
    private ArrayList getUserClosedCustomerMsgList(final String msgPageViewName, final Long customerID, final Long userId) {
        DataObject data = null;
        final ArrayList arrList = new ArrayList();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgPage"));
            query.addSelectColumn(Column.getColumn("MsgPage", "MSG_PAGE_ID"));
            query.addSelectColumn(Column.getColumn("MsgContentCustomerUserStatus", "MSG_PAGE_ID"));
            query.addSelectColumn(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"));
            query.addSelectColumn(Column.getColumn("MsgPage", "MSG_PAGE_NAME"));
            query.addSelectColumn(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"));
            query.addSelectColumn(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"));
            query.addJoin(new Join("MsgPage", "MsgContentCustomerUserStatus", new String[] { "MSG_PAGE_ID" }, new String[] { "MSG_PAGE_ID" }, 2));
            final Criteria customerCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
            final Criteria userCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"), (Object)userId, 0, false);
            final Criteria viewCriteria = new Criteria(Column.getColumn("MsgPage", "MSG_PAGE_NAME"), (Object)msgPageViewName, 0, false);
            query.setCriteria(userCriteria.and(viewCriteria).and(customerCriteria));
            data = SyMUtil.getPersistence().get(query);
            final Iterator userClosedIter = data.get("MsgContentCustomerUserStatus", "MSG_CONTENT_ID");
            while (userClosedIter.hasNext()) {
                final Long closeId = userClosedIter.next();
                if (closeId != null) {
                    arrList.add(closeId);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting list of messages closed by user : " + userId + " data : " + data, e);
        }
        return arrList;
    }
    
    private ArrayList getClosedArray(final String msgPageViewName, final Long customerID, final Long userId) {
        this.out.log(Level.FINE, "Getting closed list...");
        final ArrayList returnList = new ArrayList();
        final ArrayList userClose = this.getUserCloseList(msgPageViewName, userId);
        final ArrayList customerClose = this.getCustomerCloseList(customerID);
        final ArrayList userClosedCustMsgList = this.getUserClosedCustomerMsgList(msgPageViewName, customerID, userId);
        if (userClose != null) {
            returnList.addAll(userClose);
        }
        if (customerClose != null) {
            returnList.addAll(customerClose);
        }
        if (userClosedCustMsgList != null) {
            returnList.addAll(userClosedCustMsgList);
        }
        this.out.log(Level.FINE, "complete close list : {0}", returnList);
        return returnList;
    }
    
    private DataObject getMsgData(final String msgPageViewName, final Long customerID, final Long userId, final List msgRoleIds) {
        DataObject completeData = null;
        try {
            this.out.log(Level.FINE, "Getting messages for - msgPageViewName : {0}", msgPageViewName);
            final ArrayList closedList = this.getClosedArray(msgPageViewName, customerID, userId);
            final Object[] hideArray = closedList.toArray();
            final Object[] roleArray = msgRoleIds.toArray();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgPage"));
            query.addJoin(new Join("MsgPage", "MsgGroupToPage", new String[] { "MSG_PAGE_ID" }, new String[] { "MSG_PAGE_ID" }, 2));
            query.addJoin(new Join("MsgGroupToPage", "MsgContent", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
            query.addJoin(new Join("MsgGroupToPage", "UMModule", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            query.addJoin(new Join("MsgGroupToPage", "MsgGroupToAction", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
            query.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_CONTENT_ID" }, new String[] { "MSG_CONTENT_ID" }, 1));
            query.addJoin(new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            Criteria viewCriteria = new Criteria(Column.getColumn("MsgPage", "MSG_PAGE_NAME"), (Object)msgPageViewName, 0);
            final Criteria roleCriteria = new Criteria(Column.getColumn("MsgGroupToPage", "ROLE_ID"), (Object)roleArray, 8);
            Criteria msgCloseCriteria = new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_STATUS"), (Object)Boolean.TRUE, 0);
            msgCloseCriteria = msgCloseCriteria.or(new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_STATUS"), (Object)null, 2));
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            Criteria prductSelectCriteria = new Criteria(Column.getColumn("MsgContent", "PRODUCT_SELECT"), (Object)new Integer(2), 1);
            if (isMSP) {
                prductSelectCriteria = new Criteria(Column.getColumn("MsgContent", "PRODUCT_SELECT"), (Object)new Integer(1), 1);
            }
            viewCriteria = viewCriteria.and(prductSelectCriteria);
            if (hideArray != null) {
                msgCloseCriteria = msgCloseCriteria.and(new Criteria(Column.getColumn("MsgContent", "MSG_CONTENT_ID"), (Object)hideArray, 9));
            }
            final Criteria msgCriteria = viewCriteria.and(roleCriteria).and(msgCloseCriteria);
            query.setCriteria(msgCriteria);
            final SortColumn prioritySort1 = new SortColumn(Column.getColumn("MsgGroupToPage", "ORDER_OF_DISPLAY"), false);
            query.addSortColumn(prioritySort1);
            final SortColumn prioritySort2 = new SortColumn(Column.getColumn("MsgContent", "MSG_ORDER"), false);
            query.addSortColumn(prioritySort2);
            final SortColumn prioritySort3 = new SortColumn(Column.getColumn("UMModule", "PRECEDENCE_LEVEL"), false);
            query.addSortColumn(prioritySort3);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            completeData = SyMUtil.getPersistence().get(query);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in getting msg property from db..............", e);
        }
        return completeData;
    }
    
    private JSONArray getMsgProperties(final DataObject completeData, final Long customerId, final JSONObject apiRequestJSON) {
        final JSONArray multiMsgList = new JSONArray();
        try {
            final Boolean displayType = (Boolean)completeData.getFirstValue("MsgPage", "DISPLAY_TYPE");
            final boolean considerCount = displayType;
            final Integer noOfMsgToDisplay = (Integer)completeData.getFirstValue("MsgPage", "MSG_COUNT");
            final int noOfMsgs = noOfMsgToDisplay;
            final Iterator countIter = completeData.get("MsgGroupToAction", "ACTION_NAME");
            final List<String> actionList = new ArrayList<String>();
            for (int noOfMsgsToBeDisplayed = 0; countIter.hasNext() && (!considerCount || noOfMsgsToBeDisplayed < noOfMsgs); ++noOfMsgsToBeDisplayed) {
                final String action_name = countIter.next();
                if (!actionList.contains(action_name)) {
                    actionList.add(action_name);
                }
            }
            final List tablesRequired = completeData.getTableNames();
            final Iterator actionIter = actionList.iterator();
            while (actionIter.hasNext()) {
                final String actionName = actionIter.next().toString();
                final Criteria selectActions = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0);
                final Iterator selectgroupforAction = completeData.getRows("MsgGroupToAction", selectActions);
                final ArrayList groupIdList = new ArrayList();
                while (selectgroupforAction.hasNext()) {
                    final Row msgGrpOnActionRow = selectgroupforAction.next();
                    final Long grpId = (Long)msgGrpOnActionRow.get(1);
                    groupIdList.add(grpId);
                }
                final Object[] groupIds = groupIdList.toArray();
                final Criteria selectGps = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), (Object)groupIds, 8);
                final Iterator selectedGroups = completeData.getRows("MsgGroupToPage", selectGps);
                if (selectedGroups.hasNext()) {
                    final Row msgGroupRow = selectedGroups.next();
                    final DataObject msgGrpData = completeData.getDataObject(tablesRequired, msgGroupRow);
                    final JSONArray singleMsgList = this.generateSingleMsgList(msgGrpData, customerId, apiRequestJSON);
                    if (singleMsgList.length() == 0) {
                        continue;
                    }
                    multiMsgList.put((Object)singleMsgList);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, e, () -> "Exception in creating property from Exception in creating property from : " + dataObject);
        }
        return multiMsgList;
    }
    
    private JSONArray generateSingleMsgList(final DataObject msgGrpData, final Long customerID, final JSONObject apiRequestJSON) {
        final JSONArray singleMsgList = new JSONArray();
        try {
            final Iterator contents = msgGrpData.getRows("MsgContent");
            while (contents.hasNext()) {
                final Row row = contents.next();
                final Properties singleMsgProp = this.generateProperty(row, customerID, apiRequestJSON);
                if (!singleMsgProp.isEmpty()) {
                    final JSONObject singleMsg = new JSONObject();
                    singleMsg.put("msg_id", ((Hashtable<K, Object>)singleMsgProp).get("MSG_CONTENT_ID"));
                    singleMsg.put("msg_name", ((Hashtable<K, Object>)singleMsgProp).get("MSG_NAME"));
                    singleMsg.put("msg_title", ((Hashtable<K, Object>)singleMsgProp).get("MSG_TITLE"));
                    singleMsg.put("msg_content", ((Hashtable<K, Object>)singleMsgProp).get("MSG_CONTENT"));
                    singleMsg.put("enable_user_close", ((Hashtable<K, Object>)singleMsgProp).get("ENABLE_USER_CLOSE"));
                    singleMsg.put("msg_type", ((Hashtable<K, Object>)singleMsgProp).get("MSG_TYPE"));
                    singleMsgList.put((Object)singleMsg);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, e, () -> "Exception while creating single msg list from do : " + dataObject);
        }
        return singleMsgList;
    }
    
    private Properties generateProperty(final Row messageRow, final Long customerId, final JSONObject apiRequestJSON) {
        Properties singleMsgProp = new Properties();
        try {
            singleMsgProp = this.rowToProperty(messageRow);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, e, () -> "Exception on generating property for row : " + row);
        }
        if (singleMsgProp.containsKey("MSG_HANDLER_CLASS")) {
            try {
                final Properties userDefined = new Properties();
                ((Hashtable<String, Long>)userDefined).put("CUSTOMER_ID", customerId);
                ((Hashtable<String, String>)userDefined).put("remote_address", apiRequestJSON.getString("remote_address"));
                final String msgHandlerClass = ((Hashtable<K, String>)singleMsgProp).get("MSG_HANDLER_CLASS");
                final MsgHandler handler = (MsgHandler)Class.forName(msgHandlerClass).newInstance();
                handler.modifyMsgProperty(singleMsgProp, userDefined, (HttpServletRequest)null);
            }
            catch (final Exception e) {
                this.out.log(Level.WARNING, "Exception in processing handler section, cannot modify existing message", e);
            }
        }
        return singleMsgProp;
    }
    
    private Properties rowToProperty(final Row row) throws Exception {
        final Properties prop = new Properties();
        final List columnList = row.getColumns();
        final Iterator columnIter = columnList.iterator();
        final Long msgContentID = (Long)row.get("MSG_CONTENT_ID");
        while (columnIter.hasNext()) {
            final String colnName = columnIter.next();
            Object colData = row.get(colnName);
            if (colnName != null && colnName.equalsIgnoreCase("MSG_CONTENT")) {
                colData = this.getI18NMsg(colData, msgContentID);
            }
            if (colData != null) {
                if (colnName.equalsIgnoreCase("MSG_TITLE")) {
                    colData = I18N.getMsg(colData.toString(), new Object[0]);
                }
                ((Hashtable<String, Object>)prop).put(colnName, colData);
            }
        }
        return prop;
    }
    
    private String getI18NMsg(final Object msgKey, final Long msgContentID) {
        String returnString = "";
        try {
            if (msgKey != null) {
                final StringTokenizer st = new StringTokenizer(msgKey.toString(), "+");
                while (st.hasMoreTokens()) {
                    String compString = "";
                    compString = st.nextToken();
                    final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgContentUrl"));
                    query.addSelectColumn(Column.getColumn((String)null, "*"));
                    final SortColumn sortCol = new SortColumn(Column.getColumn("MsgContentUrl", "URL_ORDER"), true);
                    query.addSortColumn(sortCol);
                    final Criteria keyCriteria = new Criteria(Column.getColumn("MsgContentUrl", "MSG_KEY"), (Object)compString, 0, false);
                    final Criteria msgContentCriteria = new Criteria(Column.getColumn("MsgContentUrl", "MSG_CONTENT_ID"), (Object)msgContentID, 0);
                    query.setCriteria(keyCriteria.and(msgContentCriteria));
                    final DataObject urlDO = SyMUtil.getPersistence().get(query);
                    final Iterator urlItr = urlDO.getRows("MsgContentUrl");
                    compString = I18N.getMsg(compString, new Object[0]);
                    int i = 0;
                    while (urlItr.hasNext()) {
                        final Row urlRow = urlItr.next();
                        String urlValue = urlRow.get("MSG_URL") + "";
                        final String urlTarget = urlRow.get("URL_TARGET") + "";
                        final String traceType = urlRow.get("TRACE_TYPE") + "";
                        if (!traceType.equalsIgnoreCase("local")) {
                            if (traceType.equalsIgnoreCase("site_url_as_key")) {
                                if (urlValue != null && urlValue.length() != 0) {
                                    urlValue = OnlineUrlLoader.getInstance().getValue(urlValue, true);
                                }
                            }
                            else {
                                urlValue = OnlineUrlLoader.getInstance().getValue(urlValue, false);
                            }
                        }
                        String replaceValue = null;
                        if (urlValue.contains("javascript")) {
                            replaceValue = "<a href='" + urlValue + "' target='" + urlTarget + "' >";
                        }
                        else {
                            urlValue = urlValue.replaceAll("'", "\\\\'");
                            urlValue = urlValue.replaceAll("\"", "\\\\\"");
                            urlValue = urlValue.replaceAll("/", "\\\\/");
                            replaceValue = "<a href='" + urlValue + "' target='" + urlTarget + "' >";
                        }
                        replaceValue = replaceValue.replace("?dci", "?dcimb");
                        compString = compString.replaceFirst("<a>", replaceValue);
                        ++i;
                    }
                    returnString += compString;
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Error in getI18NMsg method ..............", e);
        }
        return returnString;
    }
    
    public void closeMsgForUser(final Long userId, final Long msgId, final Long pageId) {
        try {
            this.out.log(Level.FINE, "closeMsgForUser : msg with id : {0} , user id : {1} , pageId : {2}", new Object[] { msgId, userId, pageId });
            final String actionName = (String)this.getActionName(msgId);
            final DataObject contentDO = this.getActionToStatusData(actionName);
            if (!contentDO.isEmpty()) {
                final int msgScope = (int)contentDO.getFirstValue("MsgContent", "MSG_SCOPE");
                if (msgScope == 2) {
                    this.closeMsgForCustomerUser(userId, actionName, null);
                }
                else {
                    this.closeMsg(userId, actionName);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
        }
    }
    
    public void replaceContent(final Properties msgProperties, final Object... args) {
        String content = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
        content = MessageFormat.format(content, args);
        ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", content);
    }
    
    private void closeMsg(final Long userId, final String actionName) {
        try {
            final DataObject userDO = (DataObject)new WritableDataObject();
            Long contentId = null;
            final Long pageID = null;
            final DataObject actionContentDO = this.getActionContentDO(actionName);
            if (!actionContentDO.isEmpty()) {
                final Iterator closedItr = actionContentDO.getRows("MsgContent");
                while (closedItr.hasNext()) {
                    final Row row = closedItr.next();
                    contentId = (Long)row.get("MSG_CONTENT_ID");
                    final Long msgGroupID = (Long)row.get("MSG_GROUP_ID");
                    final Criteria criteria = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), (Object)msgGroupID, 0, false);
                    final Iterator pageIdIter = actionContentDO.getRows("MsgGroupToPage", criteria);
                    if (pageIdIter != null) {
                        List<Long> pageList = null;
                        try {
                            pageList = DBUtil.getColumnValuesAsList(pageIdIter, "MSG_PAGE_ID");
                            final Set<Long> pageIds = new HashSet<Long>(pageList);
                            this.closeMsgContentUserStatusForUser(userId, contentId, pageIds);
                        }
                        catch (final Exception ex) {
                            this.out.log(Level.SEVERE, "Exception in getting the pageIds and updating the msg status for user ");
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
        }
    }
    
    private void closeMsgContentUserStatusForUser(final Long userID, final Long contentId, final Set pageIds) {
        try {
            final Iterator iterator = pageIds.iterator();
            final DataObject msgContentUserStatusDO = this.getMsgContentUserStatusDO(userID, contentId);
            while (iterator.hasNext()) {
                final Long pageId = iterator.next();
                final Criteria isRowPresent = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_PAGE_ID"), (Object)pageId, 0, false);
                final Row msgContentUserStatusRow = msgContentUserStatusDO.getRow("MsgContentUserStatus", isRowPresent);
                if (msgContentUserStatusRow != null) {
                    msgContentUserStatusRow.set("MSG_DISPLAY_COUNT", (Object)0);
                    msgContentUserStatusDO.updateRow(msgContentUserStatusRow);
                }
                else {
                    final Row newMsgContentUserStatusRow = new Row("MsgContentUserStatus");
                    newMsgContentUserStatusRow.set("MSG_CONTENT_ID", (Object)contentId);
                    newMsgContentUserStatusRow.set("USER_ID", (Object)userID);
                    newMsgContentUserStatusRow.set("MSG_PAGE_ID", (Object)pageId);
                    newMsgContentUserStatusRow.set("MSG_DISPLAY_COUNT", (Object)0);
                    msgContentUserStatusDO.addRow(newMsgContentUserStatusRow);
                }
            }
            SyMUtil.getPersistence().update(msgContentUserStatusDO);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While updating message status for user...............", e);
        }
    }
    
    private DataObject getMsgContentUserStatusDO(final Long userId, final Long msgContentId) throws Exception {
        final SelectQueryImpl msgContentUserStatusDOQuery = new SelectQueryImpl(Table.getTable("MsgContentUserStatus"));
        msgContentUserStatusDOQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        Criteria userCriteria = new Criteria(Column.getColumn("MsgContentUserStatus", "USER_ID"), (Object)userId, 0, false);
        final Criteria msgCriteria = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_CONTENT_ID"), (Object)msgContentId, 0, false);
        userCriteria = userCriteria.and(msgCriteria);
        msgContentUserStatusDOQuery.setCriteria(userCriteria);
        final DataObject msgContentUserStatusDO = SyMUtil.getPersistence().get((SelectQuery)msgContentUserStatusDOQuery);
        return msgContentUserStatusDO;
    }
    
    public void closeMsgForCustomerUser(final Long userId, final String actionName, Long customerID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgContentCustomerUserStatus"));
            final Criteria custCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
            final Criteria userCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"), (Object)userId, 0, false);
            sq.addSelectColumn(Column.getColumn("MsgContentCustomerUserStatus", "*"));
            DataObject userDO = null;
            Long contentId = null;
            Long pageID = null;
            if (customerID == null) {
                customerID = CustomerInfoUtil.getInstance().getCustomerId();
            }
            final DataObject actionContentDO = this.getActionContentDO(actionName);
            if (!actionContentDO.isEmpty()) {
                final Iterator closedItr = actionContentDO.getRows("MsgContent");
                while (closedItr.hasNext()) {
                    final Row row = closedItr.next();
                    contentId = (Long)row.get("MSG_CONTENT_ID");
                    final Criteria contentCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"), (Object)contentId, 0, false);
                    sq.setCriteria(custCriteria.and(userCriteria).and(contentCriteria));
                    userDO = SyMUtil.getPersistence().get(sq);
                    final Long msgGroupID = (Long)row.get("MSG_GROUP_ID");
                    final Criteria criteria = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), (Object)msgGroupID, 0, false);
                    final Iterator pageIdIter = actionContentDO.getRows("MsgGroupToPage", criteria);
                    while (pageIdIter.hasNext()) {
                        final Row pageRow = pageIdIter.next();
                        pageID = (Long)pageRow.get("MSG_PAGE_ID");
                        Criteria criteria2 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_PAGE_ID"), (Object)pageID, 0, false);
                        final Criteria criteria3 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"), (Object)contentId, 0, false);
                        criteria2 = criteria2.and(criteria3);
                        Row hideUserMsg = userDO.getRow("MsgContentCustomerUserStatus", criteria2);
                        if (hideUserMsg == null) {
                            hideUserMsg = new Row("MsgContentCustomerUserStatus");
                            hideUserMsg.set("MSG_CONTENT_ID", (Object)contentId);
                            hideUserMsg.set("USER_ID", (Object)userId);
                            hideUserMsg.set("CUSTOMER_ID", (Object)customerID);
                            hideUserMsg.set("MSG_PAGE_ID", (Object)pageID);
                            hideUserMsg.set("MSG_STATUS", (Object)false);
                            userDO.addRow(hideUserMsg);
                        }
                        else {
                            hideUserMsg.set("MSG_STATUS", (Object)false);
                            userDO.updateRow(hideUserMsg);
                        }
                    }
                    SyMUtil.getPersistence().update(userDO);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
        }
    }
    
    public void openMsgForCustomerUser(final Long loginId, final String actionName, Long customerID) {
        try {
            Long contentId = null;
            Long pageID = null;
            if (customerID == null) {
                customerID = CustomerInfoUtil.getInstance().getCustomerId();
            }
            final DataObject actionContentDO = this.getActionContentDO(actionName);
            if (!actionContentDO.isEmpty()) {
                final DeleteQuery dQ = (DeleteQuery)new DeleteQueryImpl("MsgContentCustomerUserStatus");
                Criteria overallCriteria = null;
                final Iterator closedItr = actionContentDO.getRows("MsgContent");
                while (closedItr.hasNext()) {
                    final Row row = closedItr.next();
                    contentId = (Long)row.get("MSG_CONTENT_ID");
                    final Long msgGroupID = (Long)row.get("MSG_GROUP_ID");
                    final Criteria criteria = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), (Object)msgGroupID, 0, false);
                    final Iterator pageIdIter = actionContentDO.getRows("MsgGroupToPage", criteria);
                    while (pageIdIter.hasNext()) {
                        final Row pageRow = pageIdIter.next();
                        pageID = (Long)pageRow.get("MSG_PAGE_ID");
                        Criteria criteria2 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_PAGE_ID"), (Object)pageID, 0, false);
                        final Criteria criteria3 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"), (Object)contentId, 0, false);
                        final Criteria criteria4 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
                        final Criteria criteria5 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"), (Object)loginId, 0, false);
                        criteria2 = criteria2.and(criteria3).and(criteria4).and(criteria5);
                        overallCriteria = ((overallCriteria == null) ? criteria2 : overallCriteria.or(criteria2));
                    }
                }
                dQ.setCriteria(overallCriteria);
                SyMUtil.getPersistence().delete(dQ);
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While opening message for user...............", e);
        }
    }
    
    private Object getActionName(final Long msgId) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgContent"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(new Join("MsgContent", "MsgGroup", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
        query.addJoin(new Join("MsgGroup", "MsgGroupToAction", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
        final Criteria nameCriteria = new Criteria(Column.getColumn("MsgContent", "MSG_CONTENT_ID"), (Object)msgId, 0, false);
        query.setCriteria(nameCriteria);
        final DataObject contentDO = SyMUtil.getPersistence().get(query);
        if (!contentDO.isEmpty()) {
            final Row actionRow = contentDO.getRow("MsgGroupToAction");
            if (actionRow != null) {
                return actionRow.get("ACTION_NAME");
            }
        }
        return "";
    }
    
    private DataObject getActionContentDO(final String actionName) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgContent"));
        query.addSelectColumn(Column.getColumn("MsgContent", "MSG_CONTENT_ID"));
        query.addSelectColumn(Column.getColumn("MsgContent", "MSG_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("MsgGroup", "MSG_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("MsgGroupToAction", "MSG_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("MsgGroupToPage", "MSG_PAGE_ID"));
        query.addSelectColumn(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("MsgGroupToPage", "ROLE_ID"));
        query.addJoin(new Join("MsgContent", "MsgGroup", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
        query.addJoin(new Join("MsgGroup", "MsgGroupToPage", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
        query.addJoin(new Join("MsgGroup", "MsgGroupToAction", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
        final Criteria nameCriteria = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0, false);
        query.setCriteria(nameCriteria);
        final DataObject contentDO = SyMUtil.getPersistence().get(query);
        return contentDO;
    }
    
    public void updateMsgStatusForInfoMessage(final Long userId, final Long msgId) {
        try {
            final String actionName = (String)this.getActionName(msgId);
            Long contentId = null;
            final DataObject actionContentDO = this.getActionContentDO(actionName);
            if (!actionContentDO.isEmpty()) {
                final Iterator closedItr = actionContentDO.getRows("MsgContent");
                while (closedItr.hasNext()) {
                    final Row row = closedItr.next();
                    contentId = (Long)row.get("MSG_CONTENT_ID");
                    final Long msgGroupID = (Long)row.get("MSG_GROUP_ID");
                    final Criteria criteria = new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), (Object)msgGroupID, 0, false);
                    final Iterator pageIdIter = actionContentDO.getRows("MsgGroupToPage", criteria);
                    if (pageIdIter != null) {
                        List<Long> pageList = null;
                        try {
                            pageList = DBUtil.getColumnValuesAsList(pageIdIter, "MSG_PAGE_ID");
                            final Set<Long> pageIds = new HashSet<Long>(pageList);
                            this.updateInfoMsgContentUserStatus(userId, contentId, pageIds);
                        }
                        catch (final Exception ex) {
                            this.out.log(Level.SEVERE, "Exception in getting the pageIds and updating the msg status for user ");
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While displaying message for user.", e);
        }
    }
    
    private void updateInfoMsgContentUserStatus(final Long userId, final Long contentId, final Set msgPageIds) {
        try {
            final Iterator iterator = msgPageIds.iterator();
            final DataObject msgContentUserStatusDO = this.getMsgContentUserStatusDO(userId, contentId);
            while (iterator.hasNext()) {
                final Long msgPageId = iterator.next();
                final Criteria isRowPresent = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_PAGE_ID"), (Object)msgPageId, 0, false);
                final Row msgContentUserStatusRow = msgContentUserStatusDO.getRow("MsgContentUserStatus", isRowPresent);
                if (msgContentUserStatusRow != null) {
                    Long count = (Long)msgContentUserStatusRow.get("MSG_DISPLAY_COUNT");
                    --count;
                    msgContentUserStatusRow.set("MSG_DISPLAY_COUNT", (Object)count);
                    msgContentUserStatusDO.updateRow(msgContentUserStatusRow);
                }
                else {
                    final Row newMsgContentUserStatusRow = new Row("MsgContentUserStatus");
                    newMsgContentUserStatusRow.set("MSG_CONTENT_ID", (Object)contentId);
                    newMsgContentUserStatusRow.set("USER_ID", (Object)userId);
                    newMsgContentUserStatusRow.set("MSG_PAGE_ID", (Object)msgPageId);
                    final Long displayCount = this.getInfoMsgDisplayLimit();
                    newMsgContentUserStatusRow.set("MSG_DISPLAY_COUNT", (Object)displayCount);
                    msgContentUserStatusDO.addRow(newMsgContentUserStatusRow);
                }
            }
            SyMUtil.getPersistence().update(msgContentUserStatusDO);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While updating message status for user.", e);
        }
    }
    
    private Long getInfoMsgDisplayLimit() {
        Long displayCount = null;
        try {
            final Criteria criteria = null;
            final DataObject msgSettingsDO = SyMUtil.getPersistence().get("MessageSettings", criteria);
            if (!msgSettingsDO.isEmpty()) {
                final Row msgSettingsRow = msgSettingsDO.getFirstRow("MessageSettings");
                if (msgSettingsRow != null) {
                    displayCount = (Long)msgSettingsRow.get("INFO_MSG_DISPLAY_COUNT");
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, "Error occured while getting info message display count");
        }
        return displayCount;
    }
    
    static {
        MDMMessageProvider.msgProBase = null;
    }
}
