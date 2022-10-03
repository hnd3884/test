package com.me.mdm.api.command.schedule;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Collections;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import java.util.HashMap;
import com.me.mdm.server.constants.ScheduleRepoConstants;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import com.me.mdm.api.model.BaseAPIModel;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduleCommandService
{
    private static Logger logger;
    public static final Long TIME_DIFFERENCE;
    
    public ScheduleActionsResponseModel getScheduledCommandDetails(final String collectionID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Getting Scheduled command details for collectionID:{0}", new Object[] { collectionID });
        final ScheduleActionsResponseModel response = new ScheduleActionsResponseModel();
        final Long collection = Long.parseLong(collectionID);
        final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collection);
        final String commandName = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
        final JSONObject responseJSON = ScheduledActionsUtils.getScheduleDetailsAsJSON(collection);
        final int scheduleType = responseJSON.optInt("schedType", -1);
        response.setCommand(commandName);
        response.setExecutionTime(responseJSON.optString("execTime", (String)null));
        response.setScheduleType(scheduleType);
        if (scheduleType == 1) {
            response.setDailyIntervalType(responseJSON.optInt("dailyIntervalType", -1));
        }
        else if (scheduleType == 2) {
            response.setDaysOfWeek((List<Integer>)responseJSON.get("daysOfWeek"));
        }
        else if (scheduleType == 3) {
            final int monthlyType = responseJSON.optInt("monthlyPerform");
            response.setMonths((List<Integer>)responseJSON.get("months"));
            response.setMonthlyType(monthlyType);
            if (monthlyType == 1) {
                response.setDates(responseJSON.getInt("dates"));
            }
            else if (monthlyType == 2) {
                response.setMonthlyWeekDay(responseJSON.getInt("monthlyWeekDay"));
                response.setMonthlyWeekNum((List<Integer>)responseJSON.get("monthlyWeekNum"));
            }
        }
        return response;
    }
    
    public ScheduleActionsResponseModel scheduleCommandsToResourcesHandler(final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Scheduling a command to the resources with params:{0}", new Object[] { reqParams });
        final ScheduleActionsResponseModel response = new ScheduleActionsResponseModel();
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        final String userName = baseAPIModel.getUserName();
        final Long userID = baseAPIModel.getUserId();
        final Long customerID = baseAPIModel.getCustomerId();
        final Integer commandRepositoryType = reqParams.get("agent_type");
        reqParams.put("command_repository_type", commandRepositoryType);
        final JSONObject result = this.scheduleCommandsToResources(reqParams, customerID, userName, userID);
        response.setSchedule_command_ids((List<Long>)result.get("scheduled_command_ids"));
        return response;
    }
    
    public ScheduleActionsResponseModel modifyScheduledCommandToResourceHandler(final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Modifying a scheduledCommand with the params:{0}", new Object[] { reqParams });
        final ScheduleActionsResponseModel response = new ScheduleActionsResponseModel();
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        final String userName = baseAPIModel.getUserName();
        final Long userID = baseAPIModel.getUserId();
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final int commandRepositoryType = reqParams.get("agent_type");
        reqParams.put("command_repository_type", commandRepositoryType);
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        final JSONObject responseJSON = this.updateScheduledCommandsToDevice(reqParams, customerID, userName, userID);
        response.setSchedule_command_ids((List<Long>)responseJSON.get("scheduled_command_ids"));
        return response;
    }
    
    public Response removeScheduledCommandToResource(@Context final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Removing a scheduledcommand for resources with params:{0}", new Object[] { reqParams });
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        final Long userID = baseAPIModel.getUserId();
        final String customerIDStr = (String)requestContext.getProperty("X-Customer");
        final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
        final JSONObject params = new JSONObject(reqParams);
        final Long collectionID = params.getLong("scheduled_command_id");
        final JSONArray resourceList = params.getJSONArray("resource_ids");
        this.deleteScheduledCommand(collectionID, resourceList.toList(), customerID, userID);
        return Response.status(202).build();
    }
    
    public ScheduleActionsResponseModel getScheduleOnceCommandToResourceDetails(final ContainerRequestContext requestContext, final String collectionID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "getting schedule once command details for the collectionID", new Object[] { collectionID });
        final ScheduleActionsResponseModel response = new ScheduleActionsResponseModel();
        final Long collection = Long.parseLong(collectionID);
        final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collection);
        final Long executionTime = ScheduledCommandToCollectionHandler.getInstance().getExecutionTimeForCollection(collection);
        final String commandName = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
        response.setCommand(commandName);
        final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        final Date resultdate = new Date(executionTime);
        response.setExecutionTime(sdf.format(resultdate));
        return response;
    }
    
    public ScheduleActionsResponseModel scheduleOnceCommandWrapper(final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Creating a once type scheduled command with params:{0}", new Object[] { reqParams });
        final ScheduleActionsResponseModel response = new ScheduleActionsResponseModel();
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        final Long userID = baseAPIModel.getUserId();
        final Long customerID = baseAPIModel.getCustomerId();
        final Integer commandRepositoryType = reqParams.getOrDefault("agent_type", 1);
        reqParams.put("command_repository_type", commandRepositoryType);
        final JSONObject responseObject = this.scheduleCommandOnce(reqParams, userID, customerID);
        response.setSchedule_command_ids((List<Long>)responseObject.get("scheduled_command_ids"));
        return response;
    }
    
    public ScheduleActionsResponseModel modifyScheduleOnceCommand(final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Modifying Scheduled once command for resources with params:{0}", new Object[] { reqParams });
        final ScheduleActionsResponseModel response = new ScheduleActionsResponseModel();
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        final Long userID = baseAPIModel.getUserId();
        final Long customerID = baseAPIModel.getCustomerId();
        final Integer commandRepositoryType = reqParams.get("agent_type");
        reqParams.put("command_repository_type", commandRepositoryType);
        final JSONObject responseObject = this.updateScheduledOnceCommand(reqParams, userID, customerID);
        response.setSchedule_command_ids((List<Long>)responseObject.get("scheduled_command_ids"));
        return response;
    }
    
    public Response removeScheduleOnceCommand(final ContainerRequestContext requestContext, final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "removing scheduled once command for the following params:{0}", new Object[] { reqParams });
        final BaseAPIModel baseAPIModel = new BaseAPIModel();
        baseAPIModel.setCustomerUserDetails(requestContext);
        final Long userID = baseAPIModel.getUserId();
        final Long customerID = baseAPIModel.getCustomerId();
        final JSONObject requestObject = new JSONObject(reqParams);
        final Long collectionID = requestObject.getLong("scheduled_command_id");
        final JSONArray resourceIDs = requestObject.getJSONArray("resource_ids");
        this.deleteScheduledCommandOnce(collectionID, resourceIDs.toList(), customerID, userID);
        return Response.status(202).build();
    }
    
    protected static List getUniqueListItems(final ArrayList list) {
        final Set set = new LinkedHashSet();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }
    
    private static Long convertStringTimeToLong(final String time, final String delimiter) {
        final String[] hoursAndMins = time.split(delimiter);
        final Long totalMinutes = Long.parseLong(hoursAndMins[0]) * 60L + Long.parseLong(hoursAndMins[1]);
        return totalMinutes;
    }
    
    private static String subtractTime(final String time, final Long minutes) {
        final Long currentTime = convertStringTimeToLong(time, ":");
        final Long endTotalTime = currentTime - minutes;
        return endTotalTime / 60L + ":" + endTotalTime % 60L;
    }
    
    public JSONObject updateScheduledCommandsToDevice(final Map reqParams, final Long customerId, final String userName, final Long userID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Updating Scheduled Action with parameters {0}", reqParams);
        try {
            final JSONObject reqparams = new JSONObject(reqParams);
            final JSONArray resourceIds = reqparams.getJSONArray("resource_ids");
            List<Long> resourceListForValidation = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            resourceListForValidation = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceListForValidation);
            new DeviceFacade().validateIfDevicesExists(resourceListForValidation, customerId);
            final Long collectionID = reqparams.getLong("collection_id");
            this.deleteScheduledCommand(collectionID, resourceIds.toList(), customerId, userID);
            return this.scheduleCommandsToResources(reqParams, customerId, userName, userID);
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in updateScheduledCommands", e);
            throw e;
        }
    }
    
    private Long createSchedule(final JSONObject scheduler, final String scheduleTaskClassName, final Long customerID, final String userName, final String type) {
        ScheduleCommandService.logger.log(Level.INFO, "Creating a new Schedule of type:{0}", type);
        ScheduleCommandService.logger.log(Level.INFO, "schedule properties{0}", scheduler.toMap());
        ScheduleCommandService.logger.log(Level.INFO, "userName:{0}", userName);
        ScheduleCommandService.logger.log(Level.INFO, "customerID:{0}", customerID);
        ScheduleCommandService.logger.log(Level.INFO, "className:{0}", scheduleTaskClassName);
        JSONObject schedJson = scheduler;
        final String operationType = String.valueOf(6000);
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        final String scheduleName = "ScheduledAction_" + timeStamp + "_" + type;
        final String description = "Schedule for Scheduled Actions";
        schedJson = MDMRestAPIFactoryProvider.getAPIUtil().convertAPIJSONtoServerJSON(schedJson);
        ScheduleCommandService.logger.log(Level.INFO, "creating a schedule with the following params{0} with the name{1}", new Object[] { schedJson, scheduleName });
        final Long taskId = ApiFactoryProvider.getSchedulerAPI().createScheduleFromJson(schedJson, operationType, scheduleTaskClassName, scheduleName, scheduleTaskClassName, scheduleTaskClassName, description, (String)null, userName, customerID, (Long)null, Boolean.valueOf(true));
        ScheduleCommandService.logger.log(Level.INFO, "Schedule Creation is success with scheduleName:{1} and taskID:{0}", new Object[] { taskId, scheduleName });
        return ScheduleRepositoryHandler.getInstance().addSchedule(scheduleName, null, ScheduleRepoConstants.SCHEDULED_ACTION_MODULE, 1);
    }
    
    private void scheduleAsyncTask(final Long slotTime, final List resourceIds, final String scheduleName) {
        ScheduleCommandService.logger.log(Level.INFO, "Creating an asynchronous task for resources:{0} with time{1}", new Object[] { resourceIds, slotTime });
        try {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "ScheduleCommandOnce");
            taskInfoMap.put("schedulerTime", slotTime);
            taskInfoMap.put("poolName", "asynchThreadPool");
            final Properties taskProps = new Properties();
            ((Hashtable<String, String>)taskProps).put("scheduleName", scheduleName);
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.api.command.schedule.ScheduledCommandsLoadCommandsToCacheTask", taskInfoMap, taskProps);
            ScheduleCommandService.logger.log(Level.INFO, "Asynchronous Schedule Creation is success for time{1} and resources{0}", new Object[] { resourceIds, slotTime });
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in scheduleAsyncTask", e);
            throw e;
        }
    }
    
    public void disableSchedule(final Long scheduleID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Disabling a repeat scheduled action with scheduleID:{0}", scheduleID);
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCollectionToResource"));
            final Join join = new Join("ScheduledCollectionToResource", "ScheduledCommandToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Criteria c = new Criteria(new Column("ScheduledCommandToCollection", "SCHEDULE_ID"), (Object)scheduleID, 0);
            selectQuery.addJoin(join);
            selectQuery.addSelectColumn(new Column("ScheduledCollectionToResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("ScheduledCommandToCollection", "COLLECTION_ID"));
            selectQuery.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                final Long preScheduleId = ScheduleMapperHandler.getInstance().getPreScheduleId(scheduleID);
                final String scheduleName = ScheduleRepositoryHandler.getInstance().getScheduleName(scheduleID);
                final String preScheduleName = ScheduleRepositoryHandler.getInstance().getScheduleName(preScheduleId);
                final Long scheduleIDRef = ApiFactoryProvider.getSchedulerAPI().getSchedulerClassID(scheduleName);
                final Long preScheduleIDRef = ApiFactoryProvider.getSchedulerAPI().getSchedulerClassID(preScheduleName);
                ApiFactoryProvider.getSchedulerAPI().setSchedulerState(false, preScheduleIDRef);
                ApiFactoryProvider.getSchedulerAPI().setSchedulerState(false, scheduleIDRef);
            }
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in disableschedule", e);
            throw e;
        }
    }
    
    public void deleteScheduledCommand(final Long collectionID, final List resourceIds, final Long customerID, final Long userID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Deleting a repeat Scheduled Action for resources{0} with collectionID{1}", new Object[] { resourceIds, collectionID });
        try {
            final List<Long> resourceListForValidation = resourceIds;
            new DeviceFacade().validateIfDevicesExists(resourceListForValidation, customerID);
            new ProfileAssociateHandler().disassociateCollectionToResources(collectionID, resourceIds, customerID, userID);
            ScheduledActionsUtils.deleteSchedule(Collections.singletonList(collectionID), resourceIds);
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in deleteScheduledCommand", e);
            throw e;
        }
    }
    
    public void deleteScheduledCommandOnce(final Long collectionID, final List resourceIDs, final Long customerID, final Long userID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Deleting a Non-repeating(Once) Scheduled Action for resources{0} with collectionID{1}", new Object[] { resourceIDs, collectionID });
        try {
            new ProfileAssociateHandler().disassociateCollectionToResources(collectionID, resourceIDs, customerID, userID);
            DeviceCommandRepository.getInstance().removeMdCommandsForScheduledCommands(collectionID, resourceIDs);
            ScheduledCollectionToResourceHandler.getInstance().deleteScheduledCollectionToResource(Collections.singletonList(collectionID), resourceIDs);
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in deleteScheduledCommandOnce", e);
            throw e;
        }
    }
    
    public JSONObject updateScheduledOnceCommand(final Map reqParams, final Long userID, final Long customerID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Updating Non repeating Scheduled Action with parameters {0}", reqParams);
        ScheduleCommandService.logger.log(Level.INFO, "userID:{0},customerID:{1}", new Object[] { userID, customerID });
        final JSONObject requestObject = new JSONObject(reqParams);
        final JSONArray resourceIDS = requestObject.getJSONArray("resource_ids");
        List<Long> resourceListForValidation = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIDS);
        resourceListForValidation = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceListForValidation);
        new DeviceFacade().validateIfDevicesExists(resourceListForValidation, customerID);
        final Long collectionID = requestObject.getLong("collection_id");
        this.deleteScheduledCommandOnce(collectionID, resourceIDS.toList(), customerID, userID);
        return this.scheduleCommandOnce(reqParams, userID, customerID);
    }
    
    public JSONObject scheduleCommandOnce(final Map reqParams, final Long userID, final Long customerId) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Starting to call workflow for creating Non Repeating scheduled commands for device with params:{0}", reqParams);
        ScheduleCommandService.logger.log(Level.INFO, "userID:{0},customerID:{1}", new Object[] { userID, customerId });
        final JSONObject responseObject = new JSONObject();
        try {
            final JSONObject requestObject = new JSONObject(reqParams);
            final JSONArray commandIds = requestObject.getJSONArray("command_ids");
            final JSONArray resourceIds = requestObject.getJSONArray("resource_ids");
            List<Long> resourceListForValidation = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            resourceListForValidation = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceListForValidation);
            new DeviceFacade().validateIfDevicesExists(resourceListForValidation, customerId);
            final Long slotTime = requestObject.getLong("schedule_once_time");
            final String timeZone = requestObject.getString("time_zone");
            final Long delay = requestObject.getLong("expiry");
            final int commandRepositoryType = requestObject.getInt("command_repository_type");
            final Long slotEndTime = slotTime + delay;
            Long scheduleId = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDForProperties(delay, slotTime, commandIds.toList());
            String scheduleName;
            if (scheduleId == -1L) {
                scheduleName = "ScheduleOnce_" + System.currentTimeMillis();
                scheduleId = ScheduleRepositoryHandler.getInstance().addSchedule(scheduleName, null, ScheduleRepoConstants.SCHEDULED_ACTION_MODULE, 2);
            }
            else {
                scheduleName = ScheduleRepositoryHandler.getInstance().getScheduleName(scheduleId);
            }
            final List collectionIDList = ScheduledActionsUtils.ScheduleCollectionsToResource(commandIds, resourceIds, scheduleId, delay, commandRepositoryType, customerId, userID, 2, slotTime, timeZone);
            for (final Long collectionID : collectionIDList) {
                ScheduledTimeZoneHandler.getInstance().addCollectionForTimeZone(collectionID, timeZone);
            }
            responseObject.put("scheduled_command_ids", (Collection)collectionIDList);
            List resourceIDlist = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceIds.toList());
            resourceIDlist = ScheduledActionsUtils.excludeAndroidDevicesForResourceList(resourceIDlist);
            this.scheduleAsyncTask(slotTime, resourceIDlist, scheduleName);
            final List tempCommandList = new ArrayList();
            for (final Long collectionID2 : collectionIDList) {
                final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collectionID2);
                final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
                final String commandName = commandUUID + "Scheduled;collection=" + collectionID2;
                final Long newCommandID = DeviceCommandRepository.getInstance().addCommand(commandName);
                tempCommandList.add(newCommandID);
            }
            DeviceCommandRepository.getInstance().assignCommandToDevicesWithSlot(tempCommandList, resourceIDlist, commandRepositoryType, slotTime, slotEndTime);
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in scheduleCommandOnce", e);
            throw e;
        }
        catch (final Throwable t) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Throwable in scheduleCommandOnce", t);
        }
        return responseObject;
    }
    
    private JSONObject convertParamsTimeZone(final Map reqParams) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "converting timezone for the params", new Object[] { reqParams });
        JSONObject scheduleParams = new JSONObject();
        try {
            final JSONObject requestObject = new JSONObject(reqParams);
            final String timeZone = requestObject.getString("time_zone");
            scheduleParams = requestObject.getJSONObject("schedule_params");
            final String string;
            final String scheduleType = string = scheduleParams.getString("schedule_type");
            switch (string) {
                case "Daily": {
                    final String dailyTime = scheduleParams.getString("daily_time");
                    String slotTime = dailyTime.substring(dailyTime.length() - 5);
                    slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, timeZone);
                    final String dailySubsString = dailyTime.substring(0, dailyTime.length() - 5);
                    final String slotDailyTime = dailySubsString + slotTime;
                    scheduleParams.put("daily_time", (Object)slotDailyTime);
                    break;
                }
                case "Monthly": {
                    String slotTime = scheduleParams.getString("monthly_time");
                    slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, timeZone);
                    scheduleParams.put("monthly_time", (Object)slotTime);
                    break;
                }
                case "Weekly": {
                    String slotTime = scheduleParams.getString("weekly_time");
                    slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, timeZone);
                    scheduleParams.put("weekly_time", (Object)slotTime);
                    break;
                }
                default: {
                    ScheduleCommandService.logger.log(Level.SEVERE, "Schedule Type{0} is not found", scheduleType);
                    break;
                }
            }
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception while converting the timeZoneForSchedule", e);
            throw e;
        }
        return scheduleParams;
    }
    
    public JSONObject scheduleCommandsToResources(final Map reqParams, final Long customerId, final String userName, final Long userID) throws Exception {
        final JSONObject responseObject = new JSONObject();
        try {
            ScheduleCommandService.logger.log(Level.INFO, "Starting to call workflow for checking existing schedule and creating Repeating scheduled commands for device with params:{0}", reqParams);
            ScheduleCommandService.logger.log(Level.INFO, "userID:{0},customerID:{1},userName{2}", new Object[] { userID, customerId, userName });
            final JSONObject requestObject = new JSONObject(reqParams);
            final JSONArray resourceIds = requestObject.getJSONArray("resource_ids");
            List<Long> resourceListForValidation = JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIds);
            final String timeZone = requestObject.getString("time_zone");
            resourceListForValidation = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceListForValidation);
            new DeviceFacade().validateIfDevicesExists(resourceListForValidation, customerId);
            final JSONArray commandIds = requestObject.getJSONArray("command_ids");
            final int commandRepositoryType = requestObject.getInt("command_repository_type");
            final Long expiry = requestObject.getLong("expiry");
            final JSONObject schedulePrams = this.convertParamsTimeZone(reqParams);
            final Long scheduleID = ScheduledActionsUtils.getExistingScheduleID(schedulePrams);
            if (scheduleID == -1L) {
                return this.createScheduledCommandsToResources(reqParams, customerId, userName, userID);
            }
            responseObject.put("schedule_id", (Object)scheduleID);
            final List collectionIDList = ScheduledActionsUtils.ScheduleCollectionsToResource(commandIds, resourceIds, scheduleID, expiry, commandRepositoryType, customerId, userID, 1, 10L, timeZone);
            final Long preScheduleID = ScheduleMapperHandler.getInstance().getPreScheduleId(scheduleID);
            final Long scheduleTimeInMillis = ScheduledActionsUtils.getNextExecutionTimeForSchedule(scheduleID);
            final Long preScheduleTimeinMillis = ScheduledActionsUtils.getNextExecutionTimeForSchedule(preScheduleID);
            if (preScheduleTimeinMillis > scheduleTimeInMillis + expiry) {
                ScheduleCommandService.logger.log(Level.INFO, "Schedule Time:{0} PreScheduleTime:{1}", new Object[] { scheduleTimeInMillis, preScheduleTimeinMillis });
                ScheduledActionsUtils.addCommmandsToDeviceForSchedule(scheduleID);
            }
            for (final Long collectionID : collectionIDList) {
                ScheduledTimeZoneHandler.getInstance().addCollectionForTimeZone(collectionID, timeZone);
            }
            responseObject.put("scheduled_command_ids", (Collection)collectionIDList);
            return responseObject;
        }
        catch (final Exception e) {
            ScheduleCommandService.logger.log(Level.SEVERE, "Exception in scheduleCommandsToResources", e);
            throw e;
        }
    }
    
    private JSONObject createScheduledCommandsToResources(final Map reqParams, final Long customerId, final String userName, final Long userID) throws Exception {
        ScheduleCommandService.logger.log(Level.INFO, "Starting to call workflow creating Repeating scheduled commands for device with params:{0}", reqParams);
        final JSONObject responseObject = new JSONObject();
        final JSONObject requestObject = new JSONObject(reqParams);
        final String timeZone = requestObject.getString("time_zone");
        final JSONArray commandIds = requestObject.getJSONArray("command_ids");
        final JSONArray resourceIds = requestObject.getJSONArray("resource_ids");
        final JSONObject scheduleParams = requestObject.getJSONObject("schedule_params");
        final int commandRepositoryType = requestObject.getInt("command_repository_type");
        final JSONObject scheduleParamsWithDelay = new JSONObject(scheduleParams, JSONObject.getNames(scheduleParams));
        final String scheduleType = scheduleParams.getString("schedule_type");
        final Long delay = requestObject.getLong("expiry");
        final String s = scheduleType;
        switch (s) {
            case "Daily": {
                final String dailyTime = scheduleParams.getString("daily_time");
                String slotTime = dailyTime.substring(dailyTime.length() - 5);
                slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, timeZone);
                final String delayedScheduleTime = subtractTime(slotTime, ScheduleCommandService.TIME_DIFFERENCE);
                final String dailySubsString = dailyTime.substring(0, dailyTime.length() - 5);
                final String slotDailyTime = dailySubsString + slotTime;
                final String delayedDailyTime = dailySubsString + delayedScheduleTime;
                scheduleParams.put("daily_time", (Object)slotDailyTime);
                scheduleParamsWithDelay.put("daily_time", (Object)delayedDailyTime);
                break;
            }
            case "Monthly": {
                String slotTime = scheduleParams.getString("monthly_time");
                slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, timeZone);
                scheduleParams.put("monthly_time", (Object)slotTime);
                scheduleParamsWithDelay.put("monthly_time", (Object)subtractTime(slotTime, ScheduleCommandService.TIME_DIFFERENCE));
                break;
            }
            case "Weekly": {
                String slotTime = scheduleParams.getString("weekly_time");
                slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, timeZone);
                scheduleParams.put("weekly_time", (Object)slotTime);
                scheduleParamsWithDelay.put("weekly_time", (Object)subtractTime(slotTime, ScheduleCommandService.TIME_DIFFERENCE));
                break;
            }
            default: {
                ScheduleCommandService.logger.log(Level.SEVERE, "SlotTime not initalized in scheduleCommandsToResources");
                break;
            }
        }
        final Long scheduleId = this.createSchedule(scheduleParams, "com.me.mdm.api.command.schedule.ScheduledCommandsLoadCommandsToCacheTask", customerId, userName, "cache");
        final Long preScheduleId = this.createSchedule(scheduleParamsWithDelay, "com.me.mdm.api.command.schedule.ScheduleCommandLoadCommandsToResourceTask", customerId, userName, "load");
        responseObject.put("schedule_id", (Object)scheduleId);
        ScheduleMapperHandler.getInstance().addOrUpdateScheduleMapping(scheduleId, preScheduleId);
        final List collectionIDList = ScheduledActionsUtils.ScheduleCollectionsToResource(commandIds, resourceIds, scheduleId, delay, commandRepositoryType, customerId, userID, 1, 10L, timeZone);
        responseObject.put("scheduled_command_ids", (Collection)collectionIDList);
        final List androidExcludedDevices = ScheduledActionsUtils.excludeAndroidDevicesForResourceList(resourceIds.toList());
        final Long scheduleTimeInMillis = ScheduledActionsUtils.getNextExecutionTimeForSchedule(scheduleId);
        final Long preScheduleTimeinMillis = ScheduledActionsUtils.getNextExecutionTimeForSchedule(preScheduleId);
        if (preScheduleTimeinMillis > scheduleTimeInMillis + delay) {
            if (scheduleTimeInMillis < System.currentTimeMillis() && scheduleTimeInMillis < scheduleTimeInMillis + delay) {
                GroupActionScheduleUtils.updateCommandHistoryStatus(collectionIDList, androidExcludedDevices, Collections.singletonList(7), 1, "dc.mdm.general.command.initiated");
            }
            ScheduledActionsUtils.addCommmandsToDeviceForSchedule(scheduleId);
        }
        return responseObject;
    }
    
    static {
        ScheduleCommandService.logger = Logger.getLogger("ActionsLogger");
        TIME_DIFFERENCE = 10L;
    }
}
