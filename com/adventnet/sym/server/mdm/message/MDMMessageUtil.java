package com.adventnet.sym.server.mdm.message;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;

public class MDMMessageUtil extends MessageProvider
{
    public static Logger logger;
    private static MDMMessageUtil mdmMessageUtil;
    
    public static MDMMessageUtil getInstance() {
        if (MDMMessageUtil.mdmMessageUtil == null) {
            MDMMessageUtil.mdmMessageUtil = new MDMMessageUtil();
        }
        return MDMMessageUtil.mdmMessageUtil;
    }
    
    public boolean isNATNotConfiguredMsgOpen() {
        try {
            final String actionName = "NAT_NOT_CONFIGURED";
            return this.IsMsgOpen(actionName);
        }
        catch (final Exception e) {
            MDMMessageUtil.logger.log(Level.WARNING, "MDMMessageUtil:isNATNotConfiguredMsgOpen : Exception while Checking isNATNotConfiguredMsgOpen  ", e);
            return false;
        }
    }
    
    private SelectQuery getActionToStatusQuery(final String actionName) throws Exception {
        SelectQuery query = null;
        try {
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("MsgGroupToAction"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addJoin(new Join("MsgGroupToAction", "MsgContent", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 2));
            query.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_CONTENT_ID" }, new String[] { "MSG_CONTENT_ID" }, 1));
            final Criteria nameCriteria = new Criteria(Column.getColumn("MsgGroupToAction", "ACTION_NAME"), (Object)actionName, 0, false);
            query.setCriteria(nameCriteria);
        }
        catch (final Exception ex) {
            MDMMessageUtil.logger.log(Level.WARNING, "MDMEnrollAction:getActionToStatusData : Exception while Checking message open status in  ", ex);
            return query;
        }
        return query;
    }
    
    public boolean IsMsgOpen(final String actionName) throws Exception {
        DataObject actionNameDetailsDO = null;
        DataObject messageStatusDO = null;
        try {
            final SelectQuery actionNameQuery = this.getActionToStatusQuery(actionName);
            if (actionNameQuery == null) {
                return false;
            }
            actionNameDetailsDO = SyMUtil.getPersistence().get(actionNameQuery);
            if (actionNameDetailsDO == null || actionNameDetailsDO.isEmpty()) {
                MDMMessageUtil.logger.log(Level.WARNING, "NO Such a Message called : {0}So reurns as Messages Closed", actionName);
                return false;
            }
            final Iterator closedItr = actionNameDetailsDO.getRows("MsgContent");
            if (closedItr.hasNext()) {
                final Row row = closedItr.next();
                final Long contentId = (Long)row.get("MSG_CONTENT_ID");
                final int scope = (int)row.get("MSG_SCOPE");
                if (scope == 0) {
                    final Criteria statusCritera = new Criteria(Column.getColumn("MsgToGlobalStatus", "MSG_STATUS"), (Object)false, 0);
                    final SelectQuery globalScopeMesageStatusQuery = actionNameQuery;
                    globalScopeMesageStatusQuery.setCriteria(actionNameQuery.getCriteria().and(statusCritera));
                    messageStatusDO = SyMUtil.getPersistence().get(globalScopeMesageStatusQuery);
                }
            }
            if (messageStatusDO.isEmpty()) {
                MDMMessageUtil.logger.log(Level.INFO, "{0} : Message is open", actionName);
                return true;
            }
            MDMMessageUtil.logger.log(Level.INFO, "{0} : Message is closed", actionName);
            return false;
        }
        catch (final Exception ex) {
            MDMMessageUtil.logger.log(Level.WARNING, "MDMEnrollAction:getActionToStatusData : Exception while Checking message open status in  ", ex);
            return false;
        }
    }
    
    public boolean closeMsgForUser(final String actionName, final Long userId, final Long customerId) {
        boolean result = false;
        try {
            final SelectQuery sq = this.getActionToStatusQuery(actionName);
            sq.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            if (!dO.isEmpty() && dO.containsTable("MsgContent") && dO.containsTable("MsgGroupToPage")) {
                final Long msgId = (Long)dO.getFirstValue("MsgContent", "MSG_CONTENT_ID");
                final Long pageId = (Long)dO.getFirstValue("MsgGroupToPage", "MSG_PAGE_ID");
                if (customerId != null) {
                    result = this.closeMsgForUser(userId, msgId, pageId, customerId);
                }
                else {
                    result = this.closeMsgForUser(userId, msgId, pageId);
                }
            }
        }
        catch (final Exception ex) {
            MDMMessageUtil.logger.log(Level.SEVERE, "Exception inside closeMsgForUser().. ", ex);
        }
        return result;
    }
    
    public boolean openMsgForUser(final String actionName, final Long userId, final Long customerId) {
        boolean result = false;
        try {
            final SelectQuery sq = this.getActionToStatusQuery(actionName);
            sq.addJoin(new Join("MsgContent", "MsgToGlobalStatus", new String[] { "MSG_GROUP_ID" }, new String[] { "MSG_GROUP_ID" }, 1));
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            if (!dO.isEmpty() && dO.containsTable("MsgContent") && dO.containsTable("MsgGroupToPage")) {
                final Long msgId = (Long)dO.getFirstValue("MsgContent", "MSG_CONTENT_ID");
                final Long pageId = (Long)dO.getFirstValue("MsgGroupToPage", "MSG_PAGE_ID");
                if (customerId != null) {
                    result = this.closeMsgForUser(userId, msgId, pageId, customerId);
                }
                else {
                    result = this.closeMsgForUser(userId, msgId, pageId);
                }
            }
        }
        catch (final Exception ex) {
            MDMMessageUtil.logger.log(Level.SEVERE, "Exception inside closeMsgForUser().. ", ex);
        }
        return result;
    }
    
    static {
        MDMMessageUtil.logger = Logger.getLogger("MDMLogger");
        MDMMessageUtil.mdmMessageUtil = null;
    }
}
