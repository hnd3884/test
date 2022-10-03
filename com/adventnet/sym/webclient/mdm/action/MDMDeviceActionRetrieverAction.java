package com.adventnet.sym.webclient.mdm.action;

import com.adventnet.ds.query.UnionQuery;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.command.CommandConstants;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDeviceActionRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDeviceActionRetrieverAction() {
        this.logger = Logger.getLogger(MDMDeviceActionRetrieverAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String viewName = viewCtx.getUniqueId();
            final String deviceId = request.getParameter("deviceId");
            final String groupId = request.getParameter("groupId");
            final String groupActionId = request.getParameter("groupActionId");
            final String actionStatus = request.getParameter("actionStatus");
            final String actionType = request.getParameter("actionType");
            String platform = request.getParameter("platformType");
            if (platform == null) {
                platform = "";
            }
            Criteria criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0, false);
            if (viewName.equals("mdmGroupActionDevices")) {
                Criteria subQueryCri = null;
                final SelectQuery subquery = (SelectQuery)new SelectQueryImpl(new Table("CommandHistory"));
                subquery.addJoin(new Join("CommandHistory", "GroupActionToCommand", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
                final Criteria groupActionCriteria = new Criteria(new Column("GroupActionToCommand", "GROUP_ACTION_ID"), (Object)groupActionId, 0);
                subQueryCri = ((subQueryCri != null) ? subQueryCri.and(groupActionCriteria) : groupActionCriteria);
                subquery.setCriteria(subQueryCri);
                final Column resource_id = new Column("CommandHistory", "RESOURCE_ID");
                resource_id.setColumnAlias("DEVICE_RESOURCE_ID");
                subquery.addSelectColumn(resource_id);
                final Column cmdStatusSubQueryColumn = new Column("CommandHistory", "COMMAND_STATUS");
                cmdStatusSubQueryColumn.setColumnAlias("COMMAND_STATUS");
                subquery.addSelectColumn(cmdStatusSubQueryColumn);
                final Column remarksSubQueryColumn = new Column("CommandHistory", "REMARKS");
                remarksSubQueryColumn.setColumnAlias("REMARKS");
                subquery.addSelectColumn(remarksSubQueryColumn);
                final Column addedTimeSubQueryColumn = new Column("CommandHistory", "ADDED_TIME");
                addedTimeSubQueryColumn.setColumnAlias("ADDED_TIME");
                subquery.addSelectColumn(addedTimeSubQueryColumn);
                final Column updatedTimeSubQueryColumn = new Column("CommandHistory", "UPDATED_TIME");
                updatedTimeSubQueryColumn.setColumnAlias("UPDATED_TIME");
                subquery.addSelectColumn(updatedTimeSubQueryColumn);
                final Column addedBySubQueryColumn = new Column("CommandHistory", "ADDED_BY");
                addedBySubQueryColumn.setColumnAlias("ADDED_BY");
                subquery.addSelectColumn(addedBySubQueryColumn);
                final DerivedTable groupActionDerievedTable = new DerivedTable("CmdHistory", (Query)subquery);
                query.addJoin(new Join(Table.getTable("CustomGroupMemberRel"), (Table)groupActionDerievedTable, new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "DEVICE_RESOURCE_ID" }, 1));
                final Criteria groupCriteria = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)Long.parseLong(groupId), 0);
                criteria = criteria.and(groupCriteria);
                query.addSelectColumn(Column.getColumn("CmdHistory", "COMMAND_STATUS", "CommandHistory.COMMAND_STATUS"));
                query.addSelectColumn(Column.getColumn("CmdHistory", "REMARKS", "CommandHistory.REMARKS"));
                query.addSelectColumn(Column.getColumn("CmdHistory", "ADDED_TIME", "CommandHistory.ADDED_TIME"));
                query.addSelectColumn(Column.getColumn("CmdHistory", "UPDATED_TIME", "CommandHistory.UPDATED_TIME"));
                query.addSelectColumn(Column.getColumn("CmdHistory", "ADDED_BY", "CommandHistory.ADDED_BY"));
                if (actionStatus != null && !"all".equals(actionStatus)) {
                    final ArrayList actionStatusArray = new ArrayList<Integer>() {
                        {
                            this.add(Integer.parseInt(actionStatus));
                        }
                    };
                    if (1 == Integer.parseInt(actionStatus)) {
                        actionStatusArray.add(new Integer(5));
                    }
                    if (actionStatus.equals("1000")) {
                        criteria = criteria.and(new Criteria(new Column("CmdHistory", "COMMAND_STATUS"), (Object)null, 0));
                    }
                    else {
                        criteria = criteria.and(new Criteria(new Column("CmdHistory", "COMMAND_STATUS"), (Object)actionStatusArray.toArray(), 8));
                    }
                }
                if (!MDMStringUtils.isEmpty(platform) && !"all".equals(platform)) {
                    request.setAttribute("platform", (Object)platform);
                    final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer(platform), 0);
                    criteria = criteria.and(cPlatform);
                }
                query.setCriteria(criteria);
            }
            if (viewName.equals("mdmDeviceActions")) {
                if (actionStatus != null && !"all".equals(actionStatus)) {
                    final ArrayList actionStatusArray2 = new ArrayList();
                    actionStatusArray2.add(Long.parseLong(actionStatus));
                    if (actionStatus.equalsIgnoreCase(new Integer(1).toString())) {
                        actionStatusArray2.add(Long.parseLong(new Integer(5).toString()));
                    }
                    criteria = criteria.and(new Criteria(new Column("CommandHistory", "COMMAND_STATUS"), (Object)actionStatusArray2.toArray(), 8));
                }
                if (actionType != null && !"all".equals(actionType)) {
                    final int actionInd = new Integer(actionType);
                    criteria = criteria.and(new Criteria(new Column("DeviceActionHistory", "ACTION_ID"), (Object)actionInd, 0));
                }
                if (deviceId != null) {
                    criteria = criteria.and(new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)CommandConstants.BULK_COMMAND_LIST, 8));
                    final UnionQuery unionQuery = new DeviceFacade().getDeviceActionHistoryUnionQuery(Long.parseLong(deviceId));
                    final DerivedTable actionDerievedTable = new DerivedTable("subQuery", (Query)unionQuery);
                    query.addJoin(new Join(Table.getTable("DeviceActionHistory"), (Table)actionDerievedTable, new String[] { "DEVICE_ACTION_ID" }, new String[] { "DEVICE_ACTION_ID" }, 2));
                }
            }
            query.setCriteria(criteria);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in MDMDeviceActionRetrieverAction ", ex);
        }
        super.setCriteria(query, viewCtx);
    }
}
