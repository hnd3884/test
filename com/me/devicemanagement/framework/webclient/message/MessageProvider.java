package com.me.devicemanagement.framework.webclient.message;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.text.MessageFormat;
import java.util.Set;
import java.util.HashSet;
import com.me.devicemanagement.framework.webclient.common.OnlineUrlLoader;
import com.me.devicemanagement.framework.utils.EMSSuiteConfigurations;
import org.json.JSONObject;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SortColumn;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.webclient.quicklink.QuickLinkException;
import com.me.devicemanagement.framework.webclient.quicklink.QuickLinkControllerUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageProvider
{
    private static final Integer MESSAGE_TYPE_VIDEO;
    private static final Integer MESSAGE_TYPE_ALERT;
    private static final Integer MESSAGE_TYPE_INFO;
    private static final Integer MESSAGE_TYPE_NEW;
    protected static MessageProvider msgProBase;
    String className;
    protected Logger out;
    protected static final int CUSTOMER_SCOPE = 1;
    protected static final int GLOBAL_SCOPE = 0;
    protected static final int CUSTOMER_USER_SCOPE = 2;
    public static int productCode;
    public static List msgtypesAvailable;
    public static ArrayList productList;
    public boolean isAutoCloseCountFromBackEnd;
    public boolean isCommonTemplateUsed;
    DMMessageAuditAPI dmMsgAuditAPI;
    public static boolean collectMsgShownToPageList;
    private HttpServletRequest request;
    private Properties userDefined;
    
    protected MessageProvider() {
        this.className = MessageProvider.class.getName();
        this.out = Logger.getLogger(this.className);
        this.isAutoCloseCountFromBackEnd = true;
        this.isCommonTemplateUsed = true;
        this.dmMsgAuditAPI = null;
        this.request = null;
        this.userDefined = null;
        this.getAuditInstance();
    }
    
    public static synchronized MessageProvider getInstance() {
        if (MessageProvider.msgProBase == null) {
            MessageProvider.msgProBase = new MessageProvider();
        }
        return MessageProvider.msgProBase;
    }
    
    public boolean hideMessage(final String actionName) {
        this.out.log(Level.FINE, "The messages related to action : " + actionName + " and having no customer depedency will be closed");
        return this.hideMessage(actionName, null);
    }
    
    public boolean hideMessage(final String actionName, final Long customerID) {
        boolean hideDone = false;
        final ArrayList customerCloseList = new ArrayList();
        final ArrayList globalCloseList = new ArrayList();
        try {
            final DataObject contentDO = this.getActionToStatusData(actionName);
            if (contentDO == null || contentDO.isEmpty()) {
                this.out.log(Level.WARNING, "Action : " + actionName + " not mapped to any message...");
                return false;
            }
            final Iterator closedItr = contentDO.getRows("MsgContent");
            while (closedItr.hasNext()) {
                final Row row = closedItr.next();
                final Long contentId = (Long)row.get("MSG_CONTENT_ID");
                final int scope = (int)row.get("MSG_SCOPE");
                if (scope == 1 || scope == 2) {
                    if (customerID != null) {
                        customerCloseList.add(contentId);
                    }
                    else {
                        this.out.log(Level.WARNING, "The message row : " + row + " - Requires customer id to close.");
                    }
                }
                else {
                    if (scope != 0) {
                        continue;
                    }
                    final Criteria isRowPresent = new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_CONTENT_ID"), (Object)contentId, 0, false);
                    final Row globalStatRow = contentDO.getRow("MsgToGlobalStatus", isRowPresent);
                    if (globalStatRow != null) {
                        globalStatRow.set("MSG_STATUS", (Object)Boolean.FALSE);
                        contentDO.updateRow(globalStatRow);
                    }
                    else {
                        final Row msgStatus = new Row("MsgToGlobalStatus");
                        msgStatus.set("MSG_CONTENT_ID", (Object)contentId);
                        msgStatus.set("MSG_STATUS", (Object)Boolean.FALSE);
                        contentDO.addRow(msgStatus);
                    }
                }
            }
            if (!customerCloseList.isEmpty()) {
                final DataObject customerCloseDo = this.getCustomerCloseObject(customerCloseList, customerID);
                if (customerCloseDo != null) {
                    SyMUtil.getPersistence().update(customerCloseDo);
                }
            }
            SyMUtil.getPersistence().update(contentDO);
            hideDone = true;
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message...", e);
        }
        try {
            QuickLinkControllerUtil.getInstance().setQuickLinkstatusForAction(actionName);
        }
        catch (final QuickLinkException e2) {
            e2.printStackTrace();
        }
        return hideDone;
    }
    
    public boolean unhideMessage(final String actionName) {
        this.out.log(Level.INFO, "The messages related to action : " + actionName + " and having no customer depedency will be oppened");
        return this.unhideMessage(actionName, null);
    }
    
    public boolean unhideMessage(final String actionName, final Long customerID) {
        boolean returnStatus = false;
        final ArrayList customerCloseList = new ArrayList();
        final ArrayList globalCloseList = new ArrayList();
        final ArrayList customerUserCloseList = new ArrayList();
        try {
            final DataObject contentDO = this.getActionToStatusData(actionName);
            if (contentDO == null || contentDO.isEmpty()) {
                this.out.log(Level.FINE, "Action : " + actionName + " not mapped to any message...");
                return false;
            }
            final Iterator closedItr = contentDO.getRows("MsgContent");
            while (closedItr.hasNext()) {
                final Row row = closedItr.next();
                final Long contentId = (Long)row.get("MSG_CONTENT_ID");
                final int scope = (int)row.get("MSG_SCOPE");
                if (scope == 1) {
                    if (customerID != null) {
                        customerCloseList.add(contentId);
                    }
                    else {
                        this.out.log(Level.FINE, "Requires customer id to open this message : " + row);
                    }
                }
                else if (scope == 0) {
                    globalCloseList.add(contentId);
                }
                else {
                    if (scope != 2) {
                        continue;
                    }
                    if (customerID != null) {
                        customerUserCloseList.add(contentId);
                    }
                    else {
                        this.out.log(Level.FINE, "Requires customer id to open this message : " + row);
                    }
                }
            }
            if (!globalCloseList.isEmpty()) {
                final Object[] closeArray = globalCloseList.toArray();
                final Criteria criteria1 = new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_CONTENT_ID"), (Object)closeArray, 8);
                SyMUtil.getPersistence().delete(criteria1);
                final Criteria criteria2 = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_CONTENT_ID"), (Object)closeArray, 8);
                SyMUtil.getPersistence().delete(criteria2);
            }
            if (customerID != null && !customerCloseList.isEmpty()) {
                final Object customerCloseArray = customerCloseList.toArray();
                final Criteria criteria1 = new Criteria(Column.getColumn("MsgToCustomerStatus", "MSG_CONTENT_ID"), customerCloseArray, 8);
                final Criteria customerCrt = new Criteria(Column.getColumn("MsgToCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0);
                SyMUtil.getPersistence().delete(criteria1.and(customerCrt));
                final Criteria customerUserCrt = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 0);
                final List userIds = new ArrayList();
                final DataObject userIdsData = SyMUtil.getPersistence().get("LoginUserCustomerMapping", customerUserCrt);
                if (userIdsData != null) {
                    final Iterator iter = userIdsData.get("LoginUserCustomerMapping", "DC_USER_ID");
                    while (iter.hasNext()) {
                        userIds.add(iter.next());
                    }
                }
                Criteria contentCr = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_CONTENT_ID"), customerCloseArray, 8);
                if (!userIds.isEmpty()) {
                    final Object[] userIdArray = userIds.toArray();
                    contentCr = contentCr.and(new Criteria(Column.getColumn("MsgContentUserStatus", "USER_ID"), (Object)userIdArray, 8));
                }
                SyMUtil.getPersistence().delete(contentCr);
            }
            if (customerID != null && !customerUserCloseList.isEmpty()) {
                final Object customerUserCloseArray = customerUserCloseList.toArray();
                final Criteria criteria1 = new Criteria(Column.getColumn("MsgToCustomerStatus", "MSG_CONTENT_ID"), customerUserCloseArray, 8);
                final Criteria customerCrt = new Criteria(Column.getColumn("MsgToCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0);
                SyMUtil.getPersistence().delete(criteria1.and(customerCrt));
                final Criteria contentCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"), customerUserCloseArray, 8);
                final Criteria customerCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria userCriteria = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"), (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), 0);
                SyMUtil.getPersistence().delete(contentCriteria.and(customerCriteria).and(userCriteria));
            }
            returnStatus = true;
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while unhiding messages for action : " + actionName + " customer id : " + customerID, e);
        }
        try {
            QuickLinkControllerUtil.getInstance().resetQuickLinkstatusForAction(actionName);
        }
        catch (final QuickLinkException e2) {
            e2.printStackTrace();
        }
        return returnStatus;
    }
    
    protected DataObject getActionToStatusData(final String actionName) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgGroupToAction"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(new Join("MsgGroupToAction", "MsgContent", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
        query.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_CONTENT_ID" }, new String[] { "MSG_CONTENT_ID" }, 1));
        final Criteria nameCriteria = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0, false);
        query.setCriteria(nameCriteria);
        final DataObject contentDO = SyMUtil.getPersistence().get(query);
        return contentDO;
    }
    
    protected DataObject getCustomerCloseObject(final ArrayList customerCloseList, final Long customerID) throws Exception {
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
    
    protected String[] listToArray(final List itemList) {
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
    
    public DataObject getMessageDataObject(final Long pageNumber, final Long userID, final boolean isAdmin, final ArrayList<Long> customerIDList, final List userRoles) {
        DataObject messageDO = null;
        try {
            final Row msgPageRow = DBUtil.getRowFromDB("MsgPage", "PAGE_ID", pageNumber);
            if (msgPageRow != null) {
                final String msgPageViewName = (String)msgPageRow.get("MSG_PAGE_NAME");
                messageDO = this.getMessageDataObject(msgPageViewName, userID, isAdmin, customerIDList, userRoles);
            }
        }
        catch (final Exception e) {
            this.out.log(Level.INFO, "Exception while getting Message dataObject for {0}", e);
        }
        return messageDO;
    }
    
    public DataObject getMessageDataObject(final String msgPageViewName, final Long userID, final boolean isAdmin, final ArrayList<Long> customerIDList, final List userRoles) {
        DataObject messageDO = null;
        try {
            final List priorityUserRoleIDS = this.getMsgPrecedenceRoles(userRoles, isAdmin);
            if (priorityUserRoleIDS == null || priorityUserRoleIDS.isEmpty()) {
                return null;
            }
            messageDO = this.getMsgData(msgPageViewName, customerIDList, userID, priorityUserRoleIDS);
            messageDO = this.getRestrictedMsgData(messageDO);
        }
        catch (final Exception e) {
            this.out.log(Level.INFO, "Exception while getting Message dataObject for {0}", e);
        }
        return messageDO;
    }
    
    private List getMsgPrecedenceRoles(final List userRoles, final Boolean isAdmin) {
        final ArrayList returnRoleList = new ArrayList();
        try {
            final String[] roles = Arrays.copyOf(userRoles.toArray(), userRoles.size(), (Class<? extends String[]>)String[].class);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCUserModuleExtn"));
            Criteria roleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roles, 8);
            final SortColumn prioritySort = new SortColumn(Column.getColumn("UMModule", "PRECEDENCE_LEVEL"), false);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addJoin(new Join("DCUserModuleExtn", "UMModule", new String[] { "MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2));
            query.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            query.addSortColumn(prioritySort);
            query.setCriteria(roleCriteria);
            final DataObject priorityLevelDO = SyMUtil.getPersistence().get(query);
            final Boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (!priorityLevelDO.isEmpty()) {
                final ArrayList checkList = new ArrayList();
                final Iterator iter = priorityLevelDO.getRows("UMModule");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    if (isMSP && !isAdmin) {
                        roleCriteria = new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), row.get("ROLE_ID"), 0);
                        final Row aaaRoleRow = priorityLevelDO.getRow("AaaRole", roleCriteria);
                        String roleName = (String)aaaRoleRow.get("NAME");
                        roleName = roleName.toLowerCase();
                        if (!roleName.startsWith("mdm_") && roleName.contains("_write") && !roleName.contains("ca_write")) {
                            continue;
                        }
                    }
                    final Long roleId = (Long)row.get("ROLE_ID");
                    final Long moduleId = (Long)row.get("DC_MODULE_ID");
                    if (!checkList.contains(moduleId)) {
                        checkList.add(moduleId);
                        returnRoleList.add(roleId);
                    }
                }
            }
            this.out.log(Level.FINE, "Priority roles : " + returnRoleList);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in getting role Precedence for roles {0}", e);
        }
        return returnRoleList;
    }
    
    private DataObject getRestrictedMsgData(final DataObject completeMessageDO) {
        final DataObject restrictedDO = (DataObject)new WritableDataObject();
        try {
            final Iterator actionIterator = completeMessageDO.get("MsgGroupToAction", "ACTION_NAME");
            final List tablesRequired = completeMessageDO.getTableNames();
            while (actionIterator.hasNext()) {
                final String actionName = actionIterator.next();
                final Criteria actionNameCriteria = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0);
                final Row messageGroupRow = completeMessageDO.getRow("MsgGroupToAction", actionNameCriteria);
                final DataObject priorityMessageForAction = completeMessageDO.getDataObject(tablesRequired, messageGroupRow);
                final Row msgContentRow = priorityMessageForAction.getFirstRow("MsgContent");
                if (restrictedDO.findRow(msgContentRow) == null) {
                    restrictedDO.addRow(msgContentRow);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting Restricted DataObject of messages ...!");
        }
        return restrictedDO;
    }
    
    private List getMsgPrecedenceData(final boolean userInAdminRole, final String msgPageViewName, final List roleIdList) {
        DataObject priorityLevelDO = null;
        ArrayList returnList = null;
        try {
            this.out.log(Level.FINE, "Getting role priority for  msgPageViewName : " + msgPageViewName);
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
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    if (isMSP && !userInAdminRole) {
                        roleCriteria = new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), row.get("ROLE_ID"), 0);
                        final Row aaaRoleRow = priorityLevelDO.getRow("AaaRole", roleCriteria);
                        String roleName = (String)aaaRoleRow.get("NAME");
                        roleName = roleName.toLowerCase();
                        if (!roleName.startsWith("mdm_") && roleName.contains("_write") && !roleName.contains("ca_write")) {
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
            this.out.log(Level.FINE, "Priority roles : " + returnList);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in getting precedence in the page dataobject : " + priorityLevelDO + "| roleIdList :" + roleIdList, e);
        }
        return returnList;
    }
    
    public Long getLoginID() throws Exception {
        return ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
    }
    
    public List getRoleID() throws Exception {
        return ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
    }
    
    public Properties getPropertiesList(final String msgPageViewName, final Long customerID, final boolean userInAdminRole) {
        Properties returnProperty = new Properties();
        try {
            final Long loginID = this.getLoginID();
            final List roleID = this.getRoleID();
            returnProperty = this.getPropertiesList(msgPageViewName, customerID, SYMClientUtil.isUserInAdminRole(this.request), loginID, roleID);
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "Exception in getting msg property..............", ex);
        }
        return returnProperty;
    }
    
    @Deprecated
    public Properties getPropertiesList(final String msgPageViewName, final Properties userDefined, final Long customerID, final HttpServletRequest request) {
        Properties returnProperty = new Properties();
        try {
            this.userDefined = userDefined;
            this.request = request;
            final Long loginID = this.getLoginID();
            final List roleID = this.getRoleID();
            returnProperty = this.getPropertiesList(msgPageViewName, customerID, SYMClientUtil.isUserInAdminRole(request), loginID, roleID);
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "Exception in getting msg property..............", ex);
        }
        return returnProperty;
    }
    
    @Deprecated
    public Properties getPropertiesList(final String msgPageViewName, final Long customerID, final boolean userInAdminRole, final Long loginID, final List roleIdList) {
        MessageProvider.productCode = Math.toIntExact(EMSProductUtil.getBitwiseValueForCurrentProduct());
        final Properties returnProperty = new Properties();
        this.out.log(Level.FINE, "loginID : " + loginID);
        this.out.log(Level.FINE, "roleIdList : " + roleIdList);
        this.out.log(Level.FINE, "customerID : " + customerID);
        try {
            final List priorityRoleIds = this.getMsgPrecedenceData(userInAdminRole, msgPageViewName, roleIdList);
            if (priorityRoleIds == null || priorityRoleIds.isEmpty()) {
                this.out.log(Level.FINE, "No messages found for msgPageViewName : " + msgPageViewName + " and roleIdList :" + roleIdList);
                return returnProperty;
            }
            final Criteria pageCriteria = new Criteria(Column.getColumn("MsgPage", "MSG_PAGE_NAME"), (Object)msgPageViewName, 0);
            final DataObject priorityLevelDO = SyMUtil.getPersistence().get("MsgPage", pageCriteria);
            final Boolean considerCount = (Boolean)priorityLevelDO.getFirstValue("MsgPage", "DISPLAY_TYPE");
            final Integer msgCount = (Integer)priorityLevelDO.getFirstValue("MsgPage", "MSG_COUNT");
            final Long pageId = (Long)priorityLevelDO.getFirstValue("MsgPage", "PAGE_ID");
            this.out.log(Level.FINE, "loginID : " + loginID + " ; customerID : " + customerID + " ; considerCount : " + considerCount + " ; msgCount : " + msgCount);
            final ArrayList<Long> customerIDList = new ArrayList<Long>();
            customerIDList.add(customerID);
            final DataObject msgDO = this.getMsgData(msgPageViewName, customerIDList, loginID, priorityRoleIds);
            this.out.log(Level.FINE, "Message DO : " + msgDO);
            if (msgDO != null && !msgDO.isEmpty()) {
                final Properties msgProperties = this.getMsgProperties(msgDO, loginID);
                final Properties commonProps = new Properties();
                ((Hashtable<String, Long>)commonProps).put("userId", loginID);
                ((Hashtable<String, Long>)commonProps).put("pageId", pageId);
                ((Hashtable<String, String>)commonProps).put("msgPageViewName", msgPageViewName);
                ((Hashtable<String, Properties>)commonProps).put("MsgCountProps", this.getMsgCountList(msgProperties));
                ((Hashtable<String, Boolean>)commonProps).put("is_common_template_ui_used", this.isCommonTemplateUsed);
                if (this.isPageVisitedByUser(loginID, customerID, pageId)) {
                    ((Hashtable<String, String>)commonProps).put("isMsgPageAlreadyVisited", "true");
                }
                else {
                    ((Hashtable<String, String>)commonProps).put("isMsgPageAlreadyVisited", "false");
                }
                this.updateMsgShownOnPageList(loginID, customerID, pageId);
                ((Hashtable<String, Properties>)msgProperties).put("commonProps", commonProps);
                final ArrayList multiMsgList = ((Hashtable<K, ArrayList>)msgProperties).get("MULTI_MESSAGE_LIST");
                if (multiMsgList.size() != 0) {
                    final Iterator multiMsgIterator = multiMsgList.listIterator();
                    while (multiMsgIterator.hasNext()) {
                        final ArrayList singleMsgList = multiMsgIterator.next();
                        if (singleMsgList.size() != 0) {
                            final Iterator singleMsgIterator = singleMsgList.listIterator();
                            while (singleMsgIterator.hasNext()) {
                                final Properties msgArrayList2 = singleMsgIterator.next();
                                final int msgType = ((Hashtable<K, Integer>)msgArrayList2).get("MSG_TYPE");
                                if (msgType == 2 && this.isAutoCloseCountFromBackEnd) {
                                    final Long msgId = ((Hashtable<K, Long>)msgArrayList2).get("MSG_CONTENT_ID");
                                    this.updateMsgStatusForInfoMessage(loginID, msgId, pageId);
                                }
                            }
                        }
                    }
                }
                ((Hashtable<String, Properties>)returnProperty).put(msgPageViewName, msgProperties);
            }
            else {
                this.out.log(Level.FINE, "No messages to be shown : " + msgPageViewName + " and roleIdList :" + roleIdList);
            }
            this.out.log(Level.FINE, "Message Property changed : " + returnProperty);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in getting msg property..............", e);
        }
        return returnProperty;
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
    
    private List<Long> getCustomerCloseList(final Long customerID) {
        final List<Long> arrList = new ArrayList<Long>();
        try {
            Criteria customerCriteria = (customerID == null) ? null : new Criteria(Column.getColumn("MsgToCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria productCriteria = EMSProductUtil.constructProductCodeCriteria("MsgToCustomerStatus", "PRODUCT_CODE", MessageProvider.productCode);
            customerCriteria = customerCriteria.and(productCriteria);
            final DataObject data = SyMUtil.getPersistence().get("MsgToCustomerStatus", customerCriteria);
            final Iterator customerClosedIter = data.get("MsgToCustomerStatus", "MSG_CONTENT_ID");
            while (customerClosedIter.hasNext()) {
                arrList.add(customerClosedIter.next());
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting list of messages closed by customer : " + customerID, e);
        }
        return arrList;
    }
    
    private ArrayList getGroupIDWithAllRoles() {
        final ArrayList arrList = new ArrayList();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgGroupForAllRoles"));
            query.addSelectColumn(Column.getColumn("MsgGroupForAllRoles", "MSG_GROUP_FOR_ALL_ROLES_ID"));
            query.addSelectColumn(Column.getColumn("MsgGroupForAllRoles", "MSG_GROUP_ID"));
            DataObject data = null;
            data = SyMUtil.getPersistence().get(query);
            final Iterator userClosedIter = data.getRows("MsgGroupForAllRoles");
            while (userClosedIter.hasNext()) {
                final Row row = userClosedIter.next();
                final Long msgallrolesid = (Long)row.get("MSG_GROUP_FOR_ALL_ROLES_ID");
                if (msgallrolesid != null) {
                    final Long msg_group_id = (Long)row.get("MSG_GROUP_ID");
                    if (msg_group_id == null) {
                        continue;
                    }
                    arrList.add(msg_group_id);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting list of GroupID's which are accessed by all roles", e);
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
    
    private List<Long> getClosedArray(final String msgPageViewName, final ArrayList<Long> customerIDList, final Long userID) {
        final List<Long> userCloseList = this.getUserCloseList(msgPageViewName, userID);
        List<Long> customerCloseList = new ArrayList<Long>();
        List<Long> userClosedCustomerList = new ArrayList<Long>();
        if (!customerIDList.isEmpty() && customerIDList.size() == 1) {
            final Long customerID = customerIDList.get(0);
            customerCloseList = this.getCustomerCloseList(customerID);
            userClosedCustomerList = this.getUserClosedCustomerMsgList(msgPageViewName, customerID, userID);
        }
        final List<Long> closedMessageList = new ArrayList<Long>(userCloseList);
        closedMessageList.addAll(customerCloseList);
        closedMessageList.addAll(userClosedCustomerList);
        return closedMessageList;
    }
    
    public DataObject getMsgData(final String msgPageViewName, final ArrayList<Long> customerID, final Long userId, final List msgRoleIds) {
        DataObject completeData = null;
        try {
            this.out.log(Level.FINE, "Getting messages for - msgPageViewName : " + msgPageViewName);
            final List<Long> closedList = this.getClosedArray(msgPageViewName, customerID, userId);
            final List<Long> groupIDList = this.getGroupIDWithAllRoles();
            final Object[] hideArray = closedList.toArray();
            final Object[] roleArray = msgRoleIds.toArray();
            final Object[] arrList = groupIDList.toArray();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgPage"));
            query.addJoin(new Join("MsgPage", "TypeSpecificMsgCount", new String[] { "MSG_PAGE_ID" }, new String[] { "MSG_PAGE_ID" }, 1));
            query.addJoin(new Join("MsgPage", "MsgGroupToPage", new String[] { "MSG_PAGE_ID" }, new String[] { "MSG_PAGE_ID" }, 2));
            query.addJoin(new Join("MsgGroupToPage", "MsgContent", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
            query.addJoin(new Join("MsgGroupToPage", "UMModule", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            query.addJoin(new Join("MsgGroupToPage", "MsgGroupToAction", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
            query.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_CONTENT_ID" }, new String[] { "MSG_CONTENT_ID" }, 1));
            query.addJoin(new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            Criteria viewCriteria = new Criteria(Column.getColumn("MsgPage", "MSG_PAGE_NAME"), (Object)msgPageViewName, 0);
            Criteria roleCriteria = new Criteria(Column.getColumn("MsgGroupToPage", "ROLE_ID"), (Object)roleArray, 8);
            final Criteria msgGrpToPageproCodeCri = EMSProductUtil.constructProductCodeCriteria("MsgGroupToPage", "PRODUCT_CODE", MessageProvider.productCode);
            roleCriteria = roleCriteria.or(new Criteria(Column.getColumn("MsgGroupToPage", "MSG_GROUP_ID"), (Object)arrList, 8));
            final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("MsgToGlobalStatus", "PRODUCT_CODE", MessageProvider.productCode);
            Criteria msgCloseCriteria = new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_STATUS"), (Object)Boolean.TRUE, 0).and(productCodeCriteria);
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
            final Criteria msgCriteria = viewCriteria.and(roleCriteria).and(msgCloseCriteria).and(msgGrpToPageproCodeCri);
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
    
    private void getPropertiesUsingMsgType(final DataObject completeData, final Criteria criteria, final Row row, final ArrayList multiMsgList, final ArrayList messages) throws DataAccessException {
        final Boolean displayType = (Boolean)completeData.getFirstValue("MsgPage", "DISPLAY_TYPE");
        final boolean considerCount = displayType;
        final List tablesRequired = completeData.getTableNames();
        if (row != null) {
            final Integer count = (Integer)row.get("MSG_COUNT");
            final Iterator iter = completeData.getRows("MsgContent", criteria);
            int l = 0;
            final ArrayList actionList = new ArrayList();
            while (iter.hasNext() && (l < count || !considerCount)) {
                final Row row2 = iter.next();
                final Long msgGroupID = (Long)row2.get("MSG_GROUP_ID");
                final Row actionRow = completeData.getRow("MsgGroupToAction", new Criteria(Column.getColumn("MsgGroupToAction", "MSG_GROUP_ID"), (Object)msgGroupID, 0));
                final String actionName = (String)actionRow.get("ACTION_NAME");
                if (!actionList.contains(actionName)) {
                    actionList.add(actionName);
                    final DataObject msgGrpData = completeData.getDataObject(tablesRequired, row2);
                    final ArrayList singleMsgList = this.generateSingleMsgList(msgGrpData);
                    if (!singleMsgList.isEmpty()) {
                        multiMsgList.add(singleMsgList);
                        messages.add(singleMsgList);
                    }
                    ++l;
                }
            }
        }
    }
    
    private Properties getMsgProperties(final DataObject completeData, final Long loginID) {
        final Properties returnProp = new Properties();
        try {
            final ArrayList multiMsgList = new ArrayList();
            final List tablesRequired = completeData.getTableNames();
            final Boolean displayType = (Boolean)completeData.getFirstValue("MsgPage", "DISPLAY_TYPE");
            final boolean considerCount = displayType;
            if (completeData.getRow("TypeSpecificMsgCount") != null) {
                for (final String type : MessageProvider.msgtypesAvailable) {
                    final ArrayList messages = new ArrayList();
                    if (type.equals("VideoMessages")) {
                        final Row row = completeData.getRow("TypeSpecificMsgCount", new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_VIDEO, 0));
                        this.getPropertiesUsingMsgType(completeData, new Criteria(Column.getColumn("MsgContent", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_VIDEO, 0), row, multiMsgList, messages);
                        ((Hashtable<String, ArrayList>)returnProp).put("VideoMessages", messages);
                    }
                    else if (type.equals("AlertMessages")) {
                        final Row row = completeData.getRow("TypeSpecificMsgCount", new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_ALERT, 0));
                        this.getPropertiesUsingMsgType(completeData, new Criteria(Column.getColumn("MsgContent", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_ALERT, 0), row, multiMsgList, messages);
                        ((Hashtable<String, ArrayList>)returnProp).put("AlertMessages", messages);
                    }
                    else if (type.equals("InfoMessages")) {
                        final Row row = completeData.getRow("TypeSpecificMsgCount", new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_INFO, 0));
                        this.getPropertiesUsingMsgType(completeData, new Criteria(Column.getColumn("MsgContent", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_INFO, 0), row, multiMsgList, messages);
                        ((Hashtable<String, ArrayList>)returnProp).put("InfoMessages", messages);
                    }
                    else if (type.equals("newMessages")) {
                        final Row row = completeData.getRow("TypeSpecificMsgCount", new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_NEW, 0));
                        this.getPropertiesUsingMsgType(completeData, new Criteria(Column.getColumn("MsgContent", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_NEW, 0), row, multiMsgList, messages);
                        ((Hashtable<String, ArrayList>)returnProp).put("newMessages", messages);
                    }
                    else {
                        if (!type.equals("OtherTypeMessage")) {
                            continue;
                        }
                        final Row row = completeData.getRow("TypeSpecificMsgCount", new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_NEW, 1).and(new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_ALERT, 1)).and(new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_VIDEO, 1).and(new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_INFO, 1))));
                        this.getPropertiesUsingMsgType(completeData, new Criteria(Column.getColumn("MsgContent", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_NEW, 1).and(new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_ALERT, 1)).and(new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_VIDEO, 1).and(new Criteria(Column.getColumn("TypeSpecificMsgCount", "MSG_TYPE"), (Object)MessageProvider.MESSAGE_TYPE_INFO, 1))), row, multiMsgList, messages);
                        ((Hashtable<String, ArrayList>)returnProp).put("OtherTypeMessage", messages);
                    }
                }
            }
            else {
                final Long pageId = (Long)completeData.getFirstValue("MsgPage", "PAGE_ID");
                final Integer noOfMsgToDisplay = (Integer)completeData.getFirstValue("MsgPage", "MSG_COUNT");
                final int noOfMsgs = noOfMsgToDisplay;
                final Iterator countIter = completeData.get("MsgGroupToAction", "ACTION_NAME");
                final List<String> actionList = new ArrayList<String>();
                int noOfMsgsToBeDisplayed;
                for (noOfMsgsToBeDisplayed = 0; countIter.hasNext() && (!considerCount || noOfMsgsToBeDisplayed < noOfMsgs); ++noOfMsgsToBeDisplayed) {
                    final String action_name = countIter.next();
                    if (!actionList.contains(action_name)) {
                        actionList.add(action_name);
                    }
                }
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
                        final ArrayList singleMsgList = this.generateSingleMsgList(msgGrpData);
                        if (singleMsgList.isEmpty()) {
                            continue;
                        }
                        multiMsgList.add(singleMsgList);
                    }
                }
                if (this.dmMsgAuditAPI != null) {
                    this.dmMsgAuditAPI.addOrUpdateMsgCountAudit(pageId, loginID, noOfMsgsToBeDisplayed, true);
                }
            }
            ((Hashtable<String, ArrayList>)returnProp).put("MULTI_MESSAGE_LIST", multiMsgList);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception in creating property from Exception in creating property from : " + completeData, e);
        }
        return returnProp;
    }
    
    private ArrayList generateSingleMsgList(final DataObject msgGrpData) {
        final ArrayList singleMsgList = new ArrayList();
        try {
            final Iterator contents = msgGrpData.getRows("MsgContent");
            while (contents.hasNext()) {
                final Row row = contents.next();
                final Properties singleMsgProp = this.generateProperty(row);
                if (!singleMsgProp.isEmpty()) {
                    singleMsgList.add(singleMsgProp);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while creating single msg list from do : " + msgGrpData, e);
        }
        return singleMsgList;
    }
    
    private Properties generateProperty(final Row messageRow) {
        Properties singleMsgProp = new Properties();
        try {
            singleMsgProp = this.rowToProperty(messageRow);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception on generating property for row : " + messageRow, e);
        }
        if (singleMsgProp.containsKey("MSG_HANDLER_CLASS")) {
            try {
                final String msgHandlerClass = ((Hashtable<K, String>)singleMsgProp).get("MSG_HANDLER_CLASS");
                final MsgHandler handler = (MsgHandler)Class.forName(msgHandlerClass).newInstance();
                handler.modifyMsgProperty(singleMsgProp, this.userDefined, this.request);
                this.userDefined = null;
                this.request = null;
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
        final String msgname = (String)row.get("MSG_NAME");
        while (columnIter.hasNext()) {
            final String colnName = columnIter.next();
            Object colData = row.get(colnName);
            if (colnName != null && colnName.equalsIgnoreCase("MSG_CONTENT")) {
                final ArrayList msgObject = new ArrayList();
                colData = this.getI18NMsg(colData, msgContentID, msgObject, msgname);
                ((Hashtable<String, ArrayList>)prop).put("MSGOBJECT", msgObject);
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
    
    public Map getI18NMessageMap(final Row msgContentRow) {
        final Map messageContentMap = new HashMap();
        final Map messageAttributes = new HashMap();
        try {
            final Long msgContentID = (Long)msgContentRow.get("MSG_CONTENT_ID");
            final String messageContent = (String)msgContentRow.get("MSG_CONTENT");
            final String messageName = (String)msgContentRow.get("MSG_NAME");
            final String messageTitle = (String)msgContentRow.get("MSG_TITLE");
            final String msgName = (String)msgContentRow.get("MSG_NAME");
            final StringBuilder messageContentBuilder = new StringBuilder();
            if (messageContent != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(messageContent, "+");
                while (stringTokenizer.hasMoreTokens()) {
                    String compString = stringTokenizer.nextToken();
                    final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgContentUrl"));
                    final SortColumn sortCol = new SortColumn(Column.getColumn("MsgContentUrl", "URL_ORDER"), true);
                    final Criteria keyCriteria = new Criteria(Column.getColumn("MsgContentUrl", "MSG_KEY"), (Object)compString, 0, false);
                    final Criteria msgContentCriteria = new Criteria(Column.getColumn("MsgContentUrl", "MSG_CONTENT_ID"), (Object)msgContentID, 0);
                    query.addSortColumn(sortCol);
                    query.addSelectColumn(Column.getColumn((String)null, "*"));
                    query.setCriteria(keyCriteria.and(msgContentCriteria));
                    compString = I18N.getMsg(compString, new Object[0]);
                    final DataObject messageUrlDataObject = SyMUtil.getPersistence().get(query);
                    if (messageUrlDataObject != null && !messageUrlDataObject.isEmpty()) {
                        final Iterator messageURLIterator = messageUrlDataObject.getRows("MsgContentUrl");
                        while (messageURLIterator.hasNext()) {
                            final Map messageAttributeDetails = new HashMap();
                            final String linkTagID = "LINK_" + messageAttributes.size();
                            final String linkOpenTag = "#{" + linkTagID + "}";
                            final Row urlRow = messageURLIterator.next();
                            String urlValue = (String)urlRow.get("MSG_URL");
                            final String urlTarget = (String)urlRow.get("URL_TARGET");
                            final String traceType = (String)urlRow.get("TRACE_TYPE");
                            final String key = (String)urlRow.get("MSG_KEY");
                            final Integer indexOfOpenAnchorTag = compString.indexOf("<a>");
                            final Integer indexOfCloseAnchorTag = compString.indexOf("</a>");
                            if (indexOfOpenAnchorTag != -1 && indexOfCloseAnchorTag != -1) {
                                final String replaceString;
                                String linkSubString = replaceString = compString.substring(indexOfOpenAnchorTag, indexOfCloseAnchorTag + 4);
                                linkSubString = linkSubString.replaceAll("<a>", "");
                                linkSubString = linkSubString.replaceAll("</a>", "");
                                compString = compString.replace(replaceString, linkOpenTag);
                                JSONObject conf = new JSONObject();
                                if (EMSSuiteConfigurations.getEmsSuiteConfigurations().has(msgName)) {
                                    conf = (JSONObject)EMSSuiteConfigurations.getEmsSuiteConfigurations().get(msgName);
                                }
                                JSONObject title = new JSONObject();
                                if (conf.has(key)) {
                                    title = (JSONObject)conf.get(key);
                                }
                                JSONObject productSpecificUrl = new JSONObject();
                                if (title.has(String.valueOf(EMSProductUtil.getEMSProductCode().get(0)))) {
                                    productSpecificUrl = (JSONObject)title.get(String.valueOf(EMSProductUtil.getEMSProductCode().get(0)));
                                }
                                if (productSpecificUrl != null && productSpecificUrl.has("msgurl") && productSpecificUrl.get("msgurl") != null) {
                                    urlValue = productSpecificUrl.get("msgurl").toString();
                                }
                                else {
                                    urlValue = (String)urlRow.get("MSG_URL");
                                }
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
                                messageAttributeDetails.put("type", "link");
                                messageAttributeDetails.put("url", urlValue);
                                messageAttributeDetails.put("target", urlTarget);
                                messageAttributeDetails.put("linkText", linkSubString);
                                messageAttributeDetails.put("placeHolder", linkOpenTag);
                                messageAttributes.put(linkTagID, messageAttributeDetails);
                            }
                        }
                        messageContentBuilder.append(compString);
                    }
                    else {
                        messageContentBuilder.append(compString);
                    }
                }
                messageContentMap.put("title", I18N.getMsg(messageTitle, new Object[0]));
                messageContentMap.put("content", messageContentBuilder.toString());
                messageContentMap.put("name", messageName);
                messageContentMap.put("messageAttributes", messageAttributes);
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting I18NMessage Map from msgContentRow {0}", e);
        }
        return messageContentMap;
    }
    
    private String getI18NMsg(final Object msgKey, final Long msgContentID, final ArrayList msgObject, final String msgname) {
        String returnString = "";
        try {
            if (msgKey != null) {
                final StringTokenizer st = new StringTokenizer(msgKey.toString(), "+");
                while (st.hasMoreTokens()) {
                    String urlValue = "";
                    final DetailedMessage detailedMessage = new DetailedMessage();
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
                    detailedMessage.content = compString;
                    int i = 0;
                    while (urlItr.hasNext()) {
                        final Row urlRow = urlItr.next();
                        final String key = (String)urlRow.get("MSG_KEY");
                        JSONObject conf = new JSONObject();
                        if (EMSSuiteConfigurations.getEmsSuiteConfigurations().has(msgname)) {
                            conf = (JSONObject)EMSSuiteConfigurations.getEmsSuiteConfigurations().get(msgname);
                        }
                        JSONObject title = new JSONObject();
                        if (conf.has(key)) {
                            title = (JSONObject)conf.get(key);
                        }
                        JSONObject productSpecificUrl = new JSONObject();
                        if (title.has(String.valueOf(EMSProductUtil.getEMSProductCode().get(0)))) {
                            productSpecificUrl = (JSONObject)title.get(String.valueOf(EMSProductUtil.getEMSProductCode().get(0)));
                        }
                        if (productSpecificUrl != null && productSpecificUrl.has("msgurl") && productSpecificUrl.get("msgurl") != null) {
                            urlValue = productSpecificUrl.get("msgurl").toString();
                        }
                        else {
                            urlValue = urlRow.get("MSG_URL") + "";
                        }
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
                        detailedMessage.url.add(urlValue);
                        detailedMessage.traceType.add(traceType);
                        detailedMessage.urlTarget.add(urlTarget);
                        String replaceValue = null;
                        if (urlValue.contains("javascript")) {
                            replaceValue = "<a href='" + urlValue + "' target='" + urlTarget + "' >";
                        }
                        else {
                            urlValue = urlValue.replaceAll("'", "\\\\'");
                            urlValue = urlValue.replaceAll("\"", "\\\\\"");
                            urlValue = urlValue.replaceAll("/", "\\\\/");
                            replaceValue = "<a href=\"#\" onclick=\"javascript:redirectToLink(" + msgContentID + ",'".concat(urlValue) + "','" + urlTarget + "');\">";
                        }
                        replaceValue = replaceValue.replace("?dci", "?dcimb");
                        compString = compString.replaceFirst("<a>", replaceValue);
                        ++i;
                    }
                    returnString += compString;
                    msgObject.add(detailedMessage);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Error in getI18NMsg method ..............", e);
        }
        return returnString;
    }
    
    public Boolean closeMsgForUser(final Long userID, final Long msgID, final Long pageID, final Long customerID) {
        try {
            this.out.log(Level.FINE, "closeMsgForUser : msg with id : " + msgID + " , user id : " + userID + " , pageId : " + pageID);
            final String actionName = (String)this.getActionName(msgID);
            final DataObject contentDO = this.getActionToStatusData(actionName);
            if (!contentDO.isEmpty()) {
                final int msgScope = (int)contentDO.getFirstValue("MsgContent", "MSG_SCOPE");
                if (msgScope == 2) {
                    this.closeMsgForCustomerUser(userID, actionName, customerID);
                }
                else {
                    this.closeMsg(userID, actionName);
                }
            }
            return true;
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while closing the message ....", e);
            return false;
        }
    }
    
    public void closeMsgForUserList(final List<Long> loginIds, final Long msgId) {
        try {
            final String actionName = (String)this.getActionName(msgId);
            final DataObject contentDO = this.getActionToStatusData(actionName);
            if (!contentDO.isEmpty()) {
                final int msgScope = (int)contentDO.getFirstValue("MsgContent", "MSG_SCOPE");
                if (msgScope == 2) {
                    this.closeMsgForCustomerUserList(loginIds, actionName);
                }
                else {
                    this.closeMsgs(loginIds, actionName);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
        }
    }
    
    private void closeMsgForCustomerUserList(final List<Long> loginIds, final String actionName) {
        try {
            DataObject userDO = (DataObject)new WritableDataObject();
            Long contentId = null;
            Long pageID = null;
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final DataObject actionContentDO = this.getActionContentDO(actionName);
            if (!actionContentDO.isEmpty()) {
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
                        for (final Long loginId : loginIds) {
                            final Row hideUserMsg = new Row("MsgContentCustomerUserStatus");
                            hideUserMsg.set("MSG_CONTENT_ID", (Object)contentId);
                            hideUserMsg.set("USER_ID", (Object)loginId);
                            hideUserMsg.set("CUSTOMER_ID", (Object)customerID);
                            hideUserMsg.set("MSG_PAGE_ID", (Object)pageID);
                            Criteria criteria2 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_PAGE_ID"), (Object)pageID, 0, false);
                            final Criteria criteria3 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"), (Object)contentId, 0, false);
                            final Criteria criteria4 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
                            final Criteria criteria5 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"), (Object)loginId, 0, false);
                            criteria2 = criteria2.and(criteria3).and(criteria4).and(criteria5);
                            if (userDO.getRow("MsgContentCustomerUserStatus", criteria2) == null) {
                                userDO.addRow(hideUserMsg);
                            }
                        }
                    }
                }
            }
            userDO = ((userDO.size("MsgContentCustomerUserStatus") > 0) ? SyMUtil.getPersistence().update(userDO) : userDO);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
        }
    }
    
    private void closeMsgs(final List<Long> loginIDs, final String actionName) {
        try {
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
                            this.closeMsgContentUserStatusForUsers(loginIDs, contentId, pageIds);
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
    
    private void closeMsgContentUserStatusForUsers(final List<Long> loginIds, final Long contentId, final Set<Long> pageIds) {
        try {
            final DataObject msgContentUsersStatusDO = this.getMsgContentUsersStatusDO(loginIds, contentId);
            for (final Long loginId : loginIds) {
                for (final Long pageId : pageIds) {
                    Criteria isRowPresent = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_PAGE_ID"), (Object)pageId, 0, false);
                    final Criteria isUserPresent = new Criteria(Column.getColumn("MsgContentUserStatus", "USER_ID"), (Object)loginId, 0, false);
                    isRowPresent = isRowPresent.and(isUserPresent);
                    final Row msgContentUsersStatusRow = msgContentUsersStatusDO.getRow("MsgContentUserStatus", isRowPresent);
                    if (msgContentUsersStatusRow != null) {
                        msgContentUsersStatusRow.set("MSG_DISPLAY_COUNT", (Object)0);
                        msgContentUsersStatusDO.updateRow(msgContentUsersStatusRow);
                    }
                    else {
                        final Row newMsgContentUsersStatusRow = new Row("MsgContentUserStatus");
                        newMsgContentUsersStatusRow.set("MSG_CONTENT_ID", (Object)contentId);
                        newMsgContentUsersStatusRow.set("USER_ID", (Object)loginId);
                        newMsgContentUsersStatusRow.set("MSG_PAGE_ID", (Object)pageId);
                        newMsgContentUsersStatusRow.set("MSG_DISPLAY_COUNT", (Object)0);
                        msgContentUsersStatusDO.addRow(newMsgContentUsersStatusRow);
                    }
                }
            }
            SyMUtil.getPersistence().update(msgContentUsersStatusDO);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While updating message status for user...............", e);
        }
    }
    
    private DataObject getMsgContentUsersStatusDO(final List<Long> loginIds, final Long msgContentId) throws Exception {
        final Object[] loginIdArray = loginIds.toArray();
        final SelectQueryImpl msgContentUserStatusDOQuery = new SelectQueryImpl(Table.getTable("MsgContentUserStatus"));
        msgContentUserStatusDOQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        Criteria userCriteria = new Criteria(Column.getColumn("MsgContentUserStatus", "USER_ID"), (Object)loginIdArray, 8, false);
        final Criteria msgCriteria = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_CONTENT_ID"), (Object)msgContentId, 0, false);
        userCriteria = userCriteria.and(msgCriteria);
        msgContentUserStatusDOQuery.setCriteria(userCriteria);
        final DataObject msgContentUserStatusDO = SyMUtil.getPersistence().get((SelectQuery)msgContentUserStatusDOQuery);
        return msgContentUserStatusDO;
    }
    
    public void closeMsgForUser(final List<Long> loginIds, final Long msgId, final Long pageId) {
        try {
            final String actionName = (String)this.getActionName(msgId);
            final DataObject contentDO = this.getActionToStatusData(actionName);
            if (!contentDO.isEmpty()) {
                final int msgScope = (int)contentDO.getFirstValue("MsgContent", "MSG_SCOPE");
                if (msgScope == 2) {
                    this.closeMsgForCustomerUserList(loginIds, actionName);
                }
                else {
                    this.closeMsgs(loginIds, actionName);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
        }
    }
    
    public Boolean closeMsgForUser(final Long loginId, final Long msgId, final Long pageId) {
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        return this.closeMsgForUser(loginId, msgId, pageId, customerID);
    }
    
    public void replaceContent(final Properties msgProperties, final Object... args) {
        String content = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
        content = MessageFormat.format(content, args);
        ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", content);
    }
    
    private void closeMsg(final Long loginId, final String actionName) {
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
                            this.closeMsgContentUserStatusForUser(loginId, contentId, pageIds);
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
    
    private void closeMsgContentUserStatusForUser(final Long loginId, final Long contentId, final Set pageIds) {
        try {
            final Iterator iterator = pageIds.iterator();
            final DataObject msgContentUserStatusDO = this.getMsgContentUserStatusDO(loginId, contentId);
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
                    newMsgContentUserStatusRow.set("USER_ID", (Object)loginId);
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
    
    private void closeMsgForCustomerUser(final Long loginId, final String actionName, final Long customerID) {
        try {
            DataObject userDO = (DataObject)new WritableDataObject();
            Long contentId = null;
            Long pageID = null;
            final DataObject actionContentDO = this.getActionContentDO(actionName);
            if (!actionContentDO.isEmpty()) {
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
                        final Row hideUserMsg = new Row("MsgContentCustomerUserStatus");
                        hideUserMsg.set("MSG_CONTENT_ID", (Object)contentId);
                        hideUserMsg.set("USER_ID", (Object)loginId);
                        hideUserMsg.set("CUSTOMER_ID", (Object)customerID);
                        hideUserMsg.set("MSG_PAGE_ID", (Object)pageID);
                        Criteria criteria2 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_PAGE_ID"), (Object)pageID, 0, false);
                        final Criteria criteria3 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "MSG_CONTENT_ID"), (Object)contentId, 0, false);
                        final Criteria criteria4 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
                        final Criteria criteria5 = new Criteria(Column.getColumn("MsgContentCustomerUserStatus", "USER_ID"), (Object)loginId, 0, false);
                        criteria2 = criteria2.and(criteria3).and(criteria4).and(criteria5);
                        if (userDO.getRow("MsgContentCustomerUserStatus", criteria2) == null) {
                            userDO.addRow(hideUserMsg);
                        }
                    }
                }
            }
            userDO = ((userDO.size("MsgContentCustomerUserStatus") > 0) ? SyMUtil.getPersistence().update(userDO) : userDO);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "While hiding message for user...............", e);
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
        query.addSelectColumn(Column.getColumn("MsgGroupToPage", "PRODUCT_CODE"));
        query.addJoin(new Join("MsgContent", "MsgGroup", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
        query.addJoin(new Join("MsgGroup", "MsgGroupToPage", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
        query.addJoin(new Join("MsgGroup", "MsgGroupToAction", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
        Criteria nameCriteria = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0, false);
        final Criteria productCriteria = EMSProductUtil.constructProductCodeCriteria("MsgGroupToPage", "PRODUCT_CODE", MessageProvider.productCode);
        nameCriteria = nameCriteria.and(productCriteria);
        query.setCriteria(nameCriteria);
        final DataObject contentDO = SyMUtil.getPersistence().get(query);
        return contentDO;
    }
    
    public void updateMsgStatusForInfoMessage(final Long loginId, final Long msgId) {
        try {
            final String actionName = (String)this.getActionName(msgId);
            final DataObject userDO = (DataObject)new WritableDataObject();
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
                            this.updateInfoMsgContentUserStatus(loginId, contentId, pageIds);
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
    
    public void updateMsgStatusForInfoMessage(final Long userID, final Long msgId, final Long pageId) {
        this.updateMsgStatusForInfoMessage(userID, msgId);
    }
    
    private void updateInfoMsgContentUserStatus(final Long loginId, final Long contentId, final Set msgPageIds) {
        try {
            final Iterator iterator = msgPageIds.iterator();
            final DataObject msgContentUserStatusDO = this.getMsgContentUserStatusDO(loginId, contentId);
            while (iterator.hasNext()) {
                final Long msgPageId = iterator.next();
                final Criteria isRowPresent = new Criteria(Column.getColumn("MsgContentUserStatus", "MSG_PAGE_ID"), (Object)msgPageId, 0, false);
                final Row msgContentUserStatusRow = msgContentUserStatusDO.getRow("MsgContentUserStatus", isRowPresent);
                if (msgContentUserStatusRow != null) {
                    Long count = (Long)msgContentUserStatusRow.get("MSG_DISPLAY_COUNT");
                    --count;
                    if (count == 0L && this.dmMsgAuditAPI != null) {
                        this.dmMsgAuditAPI.updateClickCountAudit(this.getPageId(msgPageId), loginId, false, false, true);
                    }
                    msgContentUserStatusRow.set("MSG_DISPLAY_COUNT", (Object)count);
                    msgContentUserStatusDO.updateRow(msgContentUserStatusRow);
                }
                else {
                    final Row newMsgContentUserStatusRow = new Row("MsgContentUserStatus");
                    newMsgContentUserStatusRow.set("MSG_CONTENT_ID", (Object)contentId);
                    newMsgContentUserStatusRow.set("USER_ID", (Object)loginId);
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
    
    private Properties getMsgCountList(final Properties msgProperties) {
        final Properties msgCountProps = new Properties();
        try {
            final ArrayList msgTypeList = new ArrayList();
            final SelectQueryImpl selectq = new SelectQueryImpl(Table.getTable("MsgContent"));
            final Column msgTypeCol = Column.getColumn("MsgContent", "MSG_TYPE");
            selectq.addSelectColumn(msgTypeCol);
            selectq.setDistinct(true);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            Connection conn = null;
            try {
                conn = relapi.getConnection();
                final DataSet msgTypesItr = relapi.executeQuery((Query)selectq, conn);
                while (msgTypesItr.next()) {
                    final Object msgType = msgTypesItr.getValue(1);
                    if (msgType != null) {
                        msgTypeList.add(msgType);
                    }
                }
                msgTypesItr.close();
            }
            catch (final Exception ex) {
                throw ex;
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception exc) {
                    throw exc;
                }
            }
            for (final int msgType2 : msgTypeList) {
                msgCountProps.setProperty("type" + Integer.toString(msgType2), Integer.toString(0));
            }
            final ArrayList multiMsgList = ((Hashtable<K, ArrayList>)msgProperties).get("MULTI_MESSAGE_LIST");
            if (multiMsgList.size() != 0) {
                final Iterator multiMsgIterator = multiMsgList.listIterator();
                while (multiMsgIterator.hasNext()) {
                    final ArrayList singleMsgList = multiMsgIterator.next();
                    if (singleMsgList.size() != 0) {
                        final Iterator singleMsgIterator = singleMsgList.listIterator();
                        while (singleMsgIterator.hasNext()) {
                            final Properties msgArrayList2 = singleMsgIterator.next();
                            final int msgType3 = ((Hashtable<K, Integer>)msgArrayList2).get("MSG_TYPE");
                            final String msgCountFromProps = msgCountProps.getProperty("type" + Integer.toString(msgType3));
                            if (msgCountFromProps != null) {
                                int msgTypeCount = Integer.parseInt(msgCountFromProps);
                                msgCountProps.setProperty("type" + Integer.toString(msgType3), Integer.toString(++msgTypeCount));
                            }
                        }
                    }
                }
            }
            this.out.log(Level.INFO, "message count props " + msgCountProps.toString());
        }
        catch (final Exception var20) {
            this.out.log(Level.INFO, "Exception message count " + var20.toString());
        }
        return msgCountProps;
    }
    
    private void updateMsgShownOnPageList(final Long loginId, final Long customerId, final Long pageId) {
        if (MessageProvider.collectMsgShownToPageList) {
            String userStr = loginId.toString();
            if (customerId != null) {
                userStr = userStr.concat("." + customerId);
            }
            Map<String, List<Long>> msgShownToPageList = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("msgShownToPageList", 2);
            if (msgShownToPageList == null) {
                this.out.log(Level.INFO, " msgShownOnPageList null");
                msgShownToPageList = new HashMap<String, List<Long>>();
            }
            if (pageId != null && loginId != null) {
                if (msgShownToPageList.containsKey(userStr)) {
                    final ArrayList<Long> pageList = msgShownToPageList.get(userStr);
                    if (!pageList.contains(pageId)) {
                        pageList.add(pageId);
                        msgShownToPageList.put(userStr, pageList);
                    }
                }
                else {
                    final List<Long> pageList2 = new ArrayList<Long>();
                    pageList2.add(pageId);
                    msgShownToPageList.put(userStr, pageList2);
                }
                this.out.log(Level.INFO, "msgshowntoPageList " + msgShownToPageList.toString());
                ApiFactoryProvider.getCacheAccessAPI().putCache("msgShownToPageList", msgShownToPageList, 2);
            }
        }
    }
    
    public void removeUserDataFromMsgShownList(final Long loginId, final Long customerId) {
        String userStr = loginId.toString();
        if (customerId != null) {
            userStr = userStr.concat("." + customerId);
        }
        if (loginId != null) {
            final Map<Long, List<Long>> msgShownToPageList = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("msgShownToPageList", 2);
            if (msgShownToPageList != null && msgShownToPageList.containsKey(userStr)) {
                msgShownToPageList.remove(userStr);
                ApiFactoryProvider.getCacheAccessAPI().putCache("msgShownToPageList", msgShownToPageList, 2);
                this.out.log(Level.INFO, "removed user from msgShownList");
            }
        }
    }
    
    private boolean isPageVisitedByUser(final Long loginId, final Long customerId, final Long pageId) {
        String userStr = loginId.toString();
        if (customerId != null) {
            userStr = userStr.concat("." + customerId);
        }
        if (loginId != null && pageId != null) {
            final Map<String, List<Long>> msgShownToPageList = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("msgShownToPageList", 2);
            if (msgShownToPageList != null && msgShownToPageList.containsKey(userStr)) {
                final ArrayList<Long> pageList = msgShownToPageList.get(userStr);
                if (pageList.contains(pageId)) {
                    this.out.log(Level.INFO, "user visited the page");
                    return true;
                }
            }
        }
        return false;
    }
    
    private Long getPageId(final Long msgPageId) throws DataAccessException {
        final Column col = Column.getColumn("MsgPage", "MSG_PAGE_ID");
        final Criteria crit = new Criteria(col, (Object)msgPageId, 0, false);
        final DataObject msgAuditDObj = SyMUtil.getPersistence().get("MsgPage", crit);
        final Row pageRow = msgAuditDObj.getFirstRow("MsgPage");
        return (Long)pageRow.get("PAGE_ID");
    }
    
    private void getAuditInstance() {
        try {
            if (this.dmMsgAuditAPI == null) {
                final String[] msgAuditClasses = ProductClassLoader.getMultiImplProductClass("DM_MSGAUDIT_CLASS");
                if (msgAuditClasses.length > 0) {
                    this.dmMsgAuditAPI = (DMMessageAuditAPI)Class.forName(msgAuditClasses[0]).newInstance();
                }
            }
        }
        catch (final Exception ex) {
            this.dmMsgAuditAPI = null;
        }
    }
    
    public Map getMessageAttributeMap(Map oldAttributesMap, final String linkText, final String type, final String url, final String target) {
        final Map messageAttributeDetails = new HashMap();
        try {
            if (oldAttributesMap == null) {
                oldAttributesMap = new HashMap();
            }
            final String linkTagID = "LINK_" + oldAttributesMap.size();
            final String linkOpenTag = "#{" + linkTagID + "}";
            messageAttributeDetails.put("type", type);
            messageAttributeDetails.put("linkText", linkText);
            messageAttributeDetails.put("url", url);
            messageAttributeDetails.put("target", target);
            messageAttributeDetails.put("placeHolder", linkOpenTag);
            oldAttributesMap.put(linkTagID, messageAttributeDetails);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting Attribute Map for the Messages", e);
        }
        return oldAttributesMap;
    }
    
    public Map getMessageComponentAttributeMap(Map oldAttributesMap, final String linkText, final String componentName, final Map componentPayload) {
        final Map messageAttributeDetails = new HashMap();
        try {
            if (oldAttributesMap == null) {
                oldAttributesMap = new HashMap();
            }
            final String linkTagID = "LINK_" + oldAttributesMap.size();
            final String linkOpenTag = "#{" + linkTagID + "}";
            messageAttributeDetails.put("type", "action");
            messageAttributeDetails.put("linkText", linkText);
            messageAttributeDetails.put("componentName", componentName);
            messageAttributeDetails.put("placeHolder", linkOpenTag);
            messageAttributeDetails.put("componentPayload", componentPayload);
            oldAttributesMap.put(linkTagID, messageAttributeDetails);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting Component Attribute Map for the Messages", e);
        }
        return oldAttributesMap;
    }
    
    public Map getMessageContentMap(final Map messageAttributes, final String title, final String content) {
        final Map messageContentMap = new HashMap();
        try {
            messageContentMap.put("messageAttributes", messageAttributes);
            messageContentMap.put("title", title);
            messageContentMap.put("content", content);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting Message Map", e);
        }
        return messageContentMap;
    }
    
    static {
        MESSAGE_TYPE_VIDEO = 5;
        MESSAGE_TYPE_ALERT = 1;
        MESSAGE_TYPE_INFO = 2;
        MESSAGE_TYPE_NEW = 3;
        MessageProvider.msgProBase = null;
        MessageProvider.msgtypesAvailable = new ArrayList();
        MessageProvider.productList = new ArrayList();
        MessageProvider.collectMsgShownToPageList = false;
        MessageProvider.productCode = (int)(long)EMSProductUtil.getBitwiseValueForCurrentProduct();
        MessageProvider.msgtypesAvailable.add("VideoMessages");
        MessageProvider.msgtypesAvailable.add("AlertMessages");
        MessageProvider.msgtypesAvailable.add("InfoMessages");
        MessageProvider.msgtypesAvailable.add("NewMessages");
        MessageProvider.msgtypesAvailable.add("OtherMessages");
    }
    
    class DetailedMessage
    {
        public String content;
        public ArrayList url;
        public ArrayList urlTarget;
        public ArrayList traceType;
        
        DetailedMessage() {
            this.url = new ArrayList();
            this.urlTarget = new ArrayList();
            this.traceType = new ArrayList();
        }
    }
}
