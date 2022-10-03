package com.me.mdm.api.command.schedule;

import com.me.mdm.server.inv.actions.InvActionUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collections;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Collection;
import com.me.mdm.api.command.CommandFacade;
import javax.ws.rs.core.Response;
import com.me.mdm.server.inv.api.service.ActionService;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Logger;
import com.me.mdm.api.model.BaseAPIModel;

public class ScheduledActionsService
{
    BaseAPIModel baseAPIModel;
    protected static Logger logger;
    
    public ScheduledActionsService() {
        this.baseAPIModel = new BaseAPIModel();
    }
    
    public void setCustomerDetails(final ContainerRequestContext requestContext) throws Exception {
        this.baseAPIModel.setCustomerUserDetails(requestContext);
    }
    
    private ScheduledGroupActionResponseModel getScheduledGroupActionResponseModelForActionInfo(final Map groupActionDetails, final JSONObject scheduleDetails) throws Exception {
        try {
            final ScheduledGroupActionResponseModel responseModel = new ScheduledGroupActionResponseModel();
            final Integer executionType = scheduleDetails.getInt("executionType");
            final Long scheduleID = (Long)scheduleDetails.get("schedule_id");
            final String timeZone = (String)scheduleDetails.get("time_zone");
            responseModel.setInititedUser(groupActionDetails.get("initiated_by"));
            responseModel.setInitiated_time(groupActionDetails.get("initiated_time"));
            final String updatedBy = ScheduledActionsUtils.getUserNameForUserID(groupActionDetails.get("updated_by"));
            responseModel.setUpdatedBy(updatedBy);
            responseModel.setUpdatedTime(groupActionDetails.get("updated_time"));
            final Long expiry = scheduleDetails.getLong("expiry");
            if (executionType == 1) {
                responseModel.setNextExecutionTime(ScheduledActionsUtils.getNextExecutionTimeForSchedule(scheduleID));
                responseModel.setFrequency(String.valueOf(scheduleDetails.get("schedType")));
                if (scheduleDetails.has("daysOfWeek")) {
                    final List<Object> rawDaysOfWeek = scheduleDetails.getJSONArray("daysOfWeek").toList();
                    final List<Integer> daysOfWeek = new ArrayList<Integer>();
                    for (final Object day : rawDaysOfWeek) {
                        final Integer currentDay = (Integer)day;
                        daysOfWeek.add(currentDay);
                    }
                    responseModel.setDaysOfWeek(daysOfWeek);
                }
            }
            else {
                responseModel.setFrequency("-1");
                responseModel.setNextExecutionTime(scheduleDetails.getLong("scheduleOnceTime"));
            }
            responseModel.setTimeZone(timeZone);
            responseModel.setExpiry(expiry);
            responseModel.setIsScheduled(true);
            responseModel.setSuspened(groupActionDetails.get("is_suspended"));
            responseModel.setAction_purpose(groupActionDetails.get("action_purpose"));
            responseModel.setaction_type(groupActionDetails.get("action_type"));
            return responseModel;
        }
        catch (final Exception e) {
            ScheduledActionsService.logger.log(Level.SEVERE, "Error while populating the pojo class ScheduledGroupActionResponseModel", scheduleDetails);
            ScheduledActionsService.logger.log(Level.SEVERE, "Error in getScheduledGroupActionResponseModelForActionInfo", e);
            throw e;
        }
    }
    
    public ScheduledGroupActionResponseModel getScheduledActionInfo(final String actionID) throws Exception {
        ScheduledActionsService.logger.log(Level.INFO, "Getting action details for the actionID {0}", actionID);
        final ActionService actionService = new ActionService();
        final Long groupActionID = Long.parseLong(actionID);
        final Long collectionID = GroupActionToCollectionHandler.getInstance().getCollectionForGroupAction(groupActionID);
        final Long scheduleID = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDForCollection(collectionID);
        final JSONObject scheduleDetails = ScheduledActionsUtils.getScheduleDetailsAsJSON(collectionID);
        final String timeZone = ScheduledTimeZoneHandler.getInstance().getTimeZoneForCollection(collectionID);
        final Long expiry = ScheduledCommandToCollectionHandler.getInstance().getExpiryForCollection(collectionID);
        final Map groupActionDetails = actionService.getGroupActionDetails(groupActionID, this.baseAPIModel.getCustomerId());
        final Boolean isGrouActionSuspended = GroupActionScheduleUtils.isGroupActionSuspended(groupActionID);
        groupActionDetails.put("is_suspended", isGrouActionSuspended);
        scheduleDetails.put("schedule_id", (Object)scheduleID);
        scheduleDetails.put("time_zone", (Object)timeZone);
        scheduleDetails.put("expiry", (Object)expiry);
        return this.getScheduledGroupActionResponseModelForActionInfo(groupActionDetails, scheduleDetails);
    }
    
    public Response createScheduedAction(final String actionName, final Map reqParams) throws Exception {
        ScheduledActionsService.logger.log(Level.INFO, "creating a scheduled {0} Action with params:{1}");
        final JSONObject requestJSON = new JSONObject(reqParams);
        requestJSON.put("user_name", (Object)this.baseAPIModel.getUserName());
        requestJSON.put("customer_id", (Object)this.baseAPIModel.getCustomerId());
        requestJSON.put("user_id", (Object)this.baseAPIModel.getUserId());
        requestJSON.put("scheduled", (Object)actionName);
        requestJSON.put("Command", (Object)actionName);
        new CommandFacade().executeScheduledBulkDeviceCommand(requestJSON);
        return Response.status(202).build();
    }
    
    public Response modifyScheduledAction(final String actionName, final Map reqParams) throws Exception {
        ScheduledActionsService.logger.log(Level.INFO, "creating a scheduled {0} Action with params:{1}");
        try {
            final JSONObject requestJSON = new JSONObject(reqParams);
            final String userName = this.baseAPIModel.getUserName();
            final Long userID = this.baseAPIModel.getUserId();
            final Long customerID = this.baseAPIModel.getCustomerId();
            List resList = new ArrayList();
            Long collectionID = -1L;
            if (reqParams.containsKey("group_action_id")) {
                final Long groupActionID = Long.parseLong(reqParams.get("group_action_id").toString());
                requestJSON.put("group_action_id", (Object)groupActionID);
                requestJSON.put("is_modify", true);
                resList = GroupActionScheduleUtils.getResourceListForGroupActionID(groupActionID);
                collectionID = GroupActionToCollectionHandler.getInstance().getCollectionForGroupAction(groupActionID);
            }
            else if (reqParams.containsKey("device_action_id")) {
                final Long deviceActionID = Long.parseLong(reqParams.get("device_action_id").toString());
                requestJSON.put("device_action_id", (Object)deviceActionID);
                requestJSON.put("is_modify", true);
                resList = DeviceActionScheduleUtils.getResourceListForDeviceAction(deviceActionID);
                collectionID = DeviceActionToCollectionHandler.getInstance().getCollectionForDeviceAction(deviceActionID);
            }
            requestJSON.put("user_name", (Object)userName);
            requestJSON.put("collection_id", (Object)collectionID);
            requestJSON.put("customer_id", (Object)customerID);
            requestJSON.put("resource_ids", (Collection)resList);
            requestJSON.put("user_id", (Object)userID);
            requestJSON.put("scheduled", (Object)actionName);
            requestJSON.put("Command", (Object)actionName);
            new CommandFacade().executeScheduledBulkDeviceCommand(requestJSON);
            return Response.status(204).build();
        }
        catch (final Exception e) {
            ScheduledActionsService.logger.log(Level.SEVERE, "Modify scheduled action failed due to", e);
            throw e;
        }
    }
    
    public Map getScheduledGroupDetails(final List groupIds, final Integer actionID) {
        try {
            ScheduledActionsService.logger.log(Level.INFO, "Getting groupAction details for the groupIds :{0} and actionID: {1}", new Object[] { groupIds, actionID });
            final Map response = new HashMap();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Join groupActionToCollectionJoin = new Join("GroupActionHistory", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            final Column collectionIDCol = new Column("GroupActionToCollection", "COLLECTION_ID");
            final Column groupActionIDCol = new Column("GroupActionHistory", "GROUP_ACTION_ID");
            final Column actionCol = new Column("GroupActionHistory", "ACTION_ID");
            final Column groupIDCol = new Column("GroupActionHistory", "GROUP_ID");
            final Column groupActionStatusCol = new Column("GroupActionHistory", "ACTION_STATUS");
            sq.addJoin(groupActionToCollectionJoin);
            sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
            sq.addSelectColumn(collectionIDCol);
            sq.addSelectColumn(groupActionIDCol);
            sq.addSelectColumn(groupIDCol);
            final Criteria actionStatusCriteria = new Criteria(groupActionStatusCol, (Object)6, 1);
            final Criteria groupCriteria = new Criteria(groupIDCol, (Object)groupIds.toArray(), 8);
            final Criteria actionCriteria = new Criteria(actionCol, (Object)actionID, 0);
            sq.setCriteria(groupCriteria.and(actionCriteria).and(actionStatusCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> totalRows = dataObject.getRows("GroupActionHistory");
            final List values = DBUtil.getColumnValuesAsList((Iterator)totalRows, "GROUP_ID");
            final List scheduledGroups = ScheduleCommandService.getUniqueListItems((ArrayList)values);
            final List userGroups = MDMGroupHandler.getInstance().filterGroupsByGroupType(groupIds, 7);
            response.put("scheduled_groups", scheduledGroups);
            response.put("user_groups", userGroups);
            return response;
        }
        catch (final Exception ex) {
            ScheduledActionsService.logger.log(Level.SEVERE, "Exception in ScheduledActionService::getScheduledGroupActionDetails :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private String getStatusCountName(final Integer cmdStatus) {
        switch (cmdStatus) {
            case 1: {
                return "inprogress_count";
            }
            case 2: {
                return "success_count";
            }
            case 0: {
                return "failure_count";
            }
            case 7: {
                return "scheduled_count";
            }
            case 6: {
                return "suspend_count";
            }
            default: {
                return cmdStatus.toString();
            }
        }
    }
    
    public Map getScheduledGroupActionStatusDetails(final Long groupActionID) throws Exception {
        try {
            ScheduledActionsService.logger.log(Level.INFO, "Getting groupAction details for the groupActionID :{0}", groupActionID);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("CommandHistory"));
            final Column commandStatus = new Column("CommandHistory", "COMMAND_STATUS");
            final Column commandStatusCount = new Column("CommandHistory", "COMMAND_STATUS").count();
            commandStatusCount.setColumnAlias("STATUS_COUNT");
            final Join groupActionHistoryJoin = new Join("CommandHistory", "GroupActionToCommand", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
            final Criteria groupActionCriteria = new Criteria(new Column("GroupActionToCommand", "GROUP_ACTION_ID"), (Object)groupActionID, 0);
            sq.addJoin(groupActionHistoryJoin);
            sq.setCriteria(groupActionCriteria);
            sq.addSelectColumn(commandStatusCount);
            sq.addSelectColumn(commandStatus);
            final GroupByClause clause = new GroupByClause((List)Collections.singletonList(commandStatus));
            sq.setGroupByClause(clause);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)sq);
            final Map resultMap = new HashMap();
            Integer total_count = 0;
            resultMap.put("inprogress_count", 0);
            resultMap.put("success_count", 0);
            resultMap.put("failure_count", 0);
            resultMap.put("scheduled_count", 0);
            resultMap.put("suspend_count", 0);
            while (ds.next()) {
                final Integer status = (Integer)ds.getValue("COMMAND_STATUS");
                final Integer statusCount = (Integer)ds.getValue("STATUS_COUNT");
                total_count += statusCount;
                resultMap.put(this.getStatusCountName(status), statusCount);
            }
            resultMap.put("total_count", total_count);
            return resultMap;
        }
        catch (final Exception ex) {
            ScheduledActionsService.logger.log(Level.SEVERE, "Exception in ScheduledActionService::getScheduledGroupActionDetails :{0}", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Response suspendScheduledAction(final Map reqParams, final String action_type) {
        ScheduledActionsService.logger.log(Level.INFO, "Suspend actions for the given actionID{0}", reqParams);
        final Long userID = this.baseAPIModel.getUserId();
        final Long customerID = this.baseAPIModel.getCustomerId();
        reqParams.put("customer_id", customerID);
        reqParams.put("user_id", userID);
        reqParams.put("user_name", this.baseAPIModel.getUserName());
        final ActionService actionService = new ActionService();
        actionService.suspendActions(reqParams, action_type, customerID);
        return Response.status(204).build();
    }
    
    public Map getScheduledGroupWithActionList(final List groupIds, final List actionList) {
        ScheduledActionsService.logger.log(Level.INFO, "Getting details for Groups {0} with actions:{1}", new Object[] { groupIds, actionList });
        final Map actionsMap = new HashMap();
        for (final String actionName : actionList) {
            final int actionID = InvActionUtil.getEquivalentActionType(actionName);
            final Map actionMap = this.getScheduledGroupDetails(groupIds, actionID);
            actionsMap.put(actionName, actionMap.get("scheduled_groups"));
            actionsMap.put("user_groups", actionMap.get("user_groups"));
        }
        return actionsMap;
    }
    
    static {
        ScheduledActionsService.logger = Logger.getLogger("ActionsLogger");
    }
}
