package com.adventnet.sym.webclient.mdm.action;

import com.adventnet.ds.query.UnionQuery;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.mdm.api.command.schedule.ScheduledTimeZoneHandler;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import com.me.mdm.api.command.schedule.ScheduleRepositoryHandler;
import com.me.mdm.api.command.schedule.ScheduledCommandToCollectionHandler;
import com.me.mdm.api.command.schedule.GroupActionToCollectionHandler;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupActionRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupActionRetrieverAction() {
        this.logger = Logger.getLogger(MDMGroupActionRetrieverAction.class.getName());
    }
    
    public void postModelFetch(final ViewContext viewContext) {
        super.postModelFetch(viewContext);
        try {
            final Long groupID = Long.parseLong(viewContext.getRequest().getParameter("groupId"));
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Criteria groupCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ID"), (Object)groupID, 0);
            final Join groupActionCollectionJoin = new Join("GroupActionHistory", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            sq.addJoin(groupActionCollectionJoin);
            sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
            sq.setCriteria(groupCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final HashMap actionIDNextExecutionTimeMap = new HashMap();
                final HashMap actionIDTimeZoneMap = new HashMap();
                final HashMap actionIDScheduleTypeMap = new HashMap();
                final Iterator<Row> rows = dataObject.getRows("GroupActionHistory");
                while (rows.hasNext()) {
                    final Row currentRow = rows.next();
                    final Long groupActionID = (Long)currentRow.get("GROUP_ACTION_ID");
                    final Long collectionID = GroupActionToCollectionHandler.getInstance().getCollectionForGroupAction(groupActionID);
                    final Long scheduleID = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDForCollection(collectionID);
                    final Integer scheduleType = ScheduleRepositoryHandler.getInstance().getScheduleExecutionTypeForSchedule(scheduleID);
                    Long nextExecutionTime = ScheduledActionsUtils.getNextExecutionTimeForSchedule(scheduleID);
                    final String timeZone = ScheduledTimeZoneHandler.getInstance().getTimeZoneForCollection(collectionID);
                    if (nextExecutionTime == -1L) {
                        nextExecutionTime = ScheduledCommandToCollectionHandler.getInstance().getExecutionTimeForCollection(collectionID);
                    }
                    actionIDTimeZoneMap.put(groupActionID, timeZone);
                    actionIDScheduleTypeMap.put(groupActionID, scheduleType);
                    actionIDNextExecutionTimeMap.put(groupActionID, nextExecutionTime);
                }
                viewContext.getRequest().setAttribute("ACTION_ID_MAP", (Object)actionIDNextExecutionTimeMap);
                viewContext.getRequest().setAttribute("TIME_ZONE_MAP", (Object)actionIDTimeZoneMap);
                viewContext.getRequest().setAttribute("SCHEDULE_TYPE_MAP", (Object)actionIDScheduleTypeMap);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "DataAccessException in postModelFetch ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in postModelFetch", e2);
        }
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewContext) throws Exception {
        return super.fetchAndCacheSelectQuery(viewContext);
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String viewName = viewCtx.getUniqueId();
            final String groupId = request.getParameter("groupId");
            final String actionStatusStr = request.getParameter("actionStatus");
            final String actionType = request.getParameter("actionType");
            Criteria criteria = new Criteria(new Column("GroupActionHistory", "GROUP_ID"), (Object)Long.parseLong(groupId), 0);
            if (viewName.equals("mdmGroupActions")) {
                query.addSelectColumn(new Column("GroupActionHistory", "*"));
                if (actionType != null && !"all".equals(actionType)) {
                    final int actionInd = new Integer(actionType);
                    criteria = criteria.and(new Criteria(new Column("GroupActionHistory", "ACTION_ID"), (Object)actionInd, 0));
                }
                if (actionStatusStr != null && !"all".equals(actionStatusStr)) {
                    final int status = new Integer(actionStatusStr);
                    criteria = criteria.and(new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)status, 0));
                }
                if (groupId != null) {
                    final UnionQuery unionQuery = new GroupFacade().getGroupActionHistoryUnionQuery(Long.parseLong(groupId));
                    final DerivedTable actionDerievedTable1 = new DerivedTable("subQuery", (Query)unionQuery);
                    query.addJoin(new Join(Table.getTable("GroupActionHistory"), (Table)actionDerievedTable1, new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2));
                }
            }
            query.setCriteria(criteria);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in MDMGroupActionRetrieverAction ", ex);
        }
        super.setCriteria(query, viewCtx);
    }
}
