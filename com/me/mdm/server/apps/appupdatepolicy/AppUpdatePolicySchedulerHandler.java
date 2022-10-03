package com.me.mdm.server.apps.appupdatepolicy;

import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import com.me.mdm.server.device.api.model.schedule.SchedulerModel;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.constants.ScheduleRepoConstants;
import com.me.mdm.api.command.schedule.ScheduleRepositoryHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppUpdatePolicySchedulerHandler
{
    private static AppUpdatePolicySchedulerHandler appUpdatePolicySchedulerHandler;
    private Logger logger;
    
    public AppUpdatePolicySchedulerHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AppUpdatePolicySchedulerHandler getInstance() {
        if (AppUpdatePolicySchedulerHandler.appUpdatePolicySchedulerHandler == null) {
            AppUpdatePolicySchedulerHandler.appUpdatePolicySchedulerHandler = new AppUpdatePolicySchedulerHandler();
        }
        return AppUpdatePolicySchedulerHandler.appUpdatePolicySchedulerHandler;
    }
    
    private void createSchedule(JSONObject scheduleJSON, final AppUpdatePolicyModel appUpdatePolicyModel) {
        this.logger.log(Level.INFO, "Creating scheduler for app update policy API JSON {0}", scheduleJSON);
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        final String scheduleName = appUpdatePolicyModel.getPolicyName() + "_" + timeStamp + "_app_update";
        final String description = "App update policy scheduler";
        scheduleJSON = MDMRestAPIFactoryProvider.getAPIUtil().convertAPIJSONtoServerJSON(scheduleJSON);
        this.logger.log(Level.INFO, "Creating scheduler for app update policy  SERVER JSON {0}", scheduleJSON);
        final Long taskId = ApiFactoryProvider.getSchedulerAPI().createScheduleFromJson(scheduleJSON, String.valueOf(8000), AppUpdatePolicyConstants.SchedulerConstants.schedulerClass, scheduleName, AppUpdatePolicyConstants.SchedulerConstants.schedulerClass, AppUpdatePolicyConstants.SchedulerConstants.schedulerClass, description, (String)null, appUpdatePolicyModel.getUserName(), appUpdatePolicyModel.getCustomerId(), (Long)null, Boolean.valueOf(true));
        final Long scheduleId = ScheduleRepositoryHandler.getInstance().addSchedule(scheduleName, ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(scheduleName), ScheduleRepoConstants.APP_UPDATE_MODULE, 1);
        this.logger.log(Level.INFO, "Scheduler created with task Id {0}  schedule Id {1} scheduleName {2}", new Object[] { taskId, scheduleId, scheduleName });
        appUpdatePolicyModel.setScheduleId(scheduleId);
    }
    
    private Long getExistingScheduleId(final JSONObject scheduleParams) throws Exception {
        final Long scheduleId = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepository"));
        final Criteria moduleCriteria = new Criteria(Column.getColumn("ScheduleRepository", "SCHEDULE_MODULE"), (Object)ScheduleRepoConstants.APP_UPDATE_MODULE, 0);
        selectQuery.setCriteria(moduleCriteria);
        selectQuery.addSelectColumn(Column.getColumn("ScheduleRepository", "SCHEDULE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("ScheduleRepository");
            while (iterator.hasNext()) {
                final Row scheduleRepoRow = iterator.next();
                final Long existingScheduleId = (Long)scheduleRepoRow.get("SCHEDULE_ID");
                if (ScheduledActionsUtils.checkScheduleParamsAreEqual(scheduleParams, existingScheduleId)) {
                    return existingScheduleId;
                }
            }
        }
        return scheduleId;
    }
    
    private String getSlotTimeFromWindowStartTime(final String windowStartTime) throws Exception {
        final Integer windowStartTimeInt = Integer.parseInt(windowStartTime);
        final int hours = windowStartTimeInt / 60;
        final int minutes = windowStartTimeInt % 60;
        final String slotTime = hours + ":" + minutes;
        final DateFormat sdf = new SimpleDateFormat("HH:mm");
        final Date date = sdf.parse(slotTime);
        return sdf.format(date);
    }
    
    private String getDailyTime() {
        final Calendar cal = Calendar.getInstance();
        cal.add(2, -1);
        final Date result = cal.getTime();
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(result);
    }
    
    public void addAppUpdateScheduler(final AppUpdatePolicyModel appUpdatePolicyModel) throws Exception {
        final SchedulerModel schedulerModel = appUpdatePolicyModel.getSchedulerModel();
        if (schedulerModel != null) {
            final String schedulerType = schedulerModel.getScheduleType();
            final JSONObject scheduleParams = new JSONObject();
            final String s = schedulerType;
            switch (s) {
                case "Daily": {
                    String slotTime = this.getSlotTimeFromWindowStartTime(appUpdatePolicyModel.getWindowStartTime());
                    slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, appUpdatePolicyModel.getTimeZone());
                    final String dailySubsString = this.getDailyTime();
                    final String slotDailyTime = dailySubsString + ", " + slotTime;
                    scheduleParams.put("daily_time", (Object)slotDailyTime);
                    scheduleParams.put("daily_interval_type", (Object)schedulerModel.getDailyIntervalType());
                    scheduleParams.put("schedule_type", (Object)schedulerModel.getScheduleType());
                    scheduleParams.put("scheduler_disabled", (Object)schedulerModel.getScheduleDisabled());
                    scheduleParams.put("task_time_zone", (Object)TimeZone.getDefault().getID());
                    break;
                }
                case "Weekly": {
                    String slotTime = this.getSlotTimeFromWindowStartTime(appUpdatePolicyModel.getWindowStartTime());
                    slotTime = ScheduledActionsUtils.convertTimeZone(slotTime, appUpdatePolicyModel.getTimeZone());
                    scheduleParams.put("schedule_type", (Object)schedulerModel.getScheduleType());
                    scheduleParams.put("scheduler_disabled", (Object)schedulerModel.getScheduleDisabled());
                    scheduleParams.put("weekly_time", (Object)slotTime);
                    scheduleParams.put("days_of_week", (Object)schedulerModel.getDayOfWeek());
                    scheduleParams.put("task_time_zone", (Object)TimeZone.getDefault().getID());
                    break;
                }
            }
            final Long scheduleId = this.getExistingScheduleId(scheduleParams);
            if (scheduleId == -1L) {
                this.createSchedule(scheduleParams, appUpdatePolicyModel);
            }
            else {
                this.logger.log(Level.INFO, "Scheduler with same time configuration present picking schedule Id {0}", scheduleId);
                appUpdatePolicyModel.setScheduleId(scheduleId);
            }
        }
    }
    
    public void getAppUpdateScheduler(final AppUpdatePolicyModel appUpdatePolicyModel, final DataObject appUpdatePolicyDO) throws DataAccessException {
        final Row row = appUpdatePolicyDO.getRow("ScheduleRepository");
        if (row != null) {
            final String scheduleName = (String)row.get("SCHEDULE_NAME");
            final HashMap map = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(scheduleName);
            this.logger.log(Level.INFO, "Existing schedule values for policy Id {0} is {1}", new Object[] { appUpdatePolicyModel.getProfileId(), map });
            SchedulerModel schedulerModel = appUpdatePolicyModel.getSchedulerModel();
            if (schedulerModel == null) {
                schedulerModel = new SchedulerModel();
            }
            final String s;
            final String scheduleType = s = map.get("schedType");
            switch (s) {
                case "Daily": {
                    schedulerModel.setScheduleType(scheduleType);
                    schedulerModel.setDailyIntervalType(map.get("dailyIntervalType"));
                    final int minutes = map.get("exeMinutes");
                    final int hours = map.get("exeHours");
                    final int day = map.get("startDate");
                    final int month = map.get("startMonth");
                    final int year = map.get("startYear");
                    final String dailyTime = day + "/" + month + "/" + year + ", " + hours + ":" + minutes;
                    schedulerModel.setDailyTime(dailyTime);
                    break;
                }
                case "Weekly": {
                    schedulerModel.setScheduleType(scheduleType);
                    final int minutes = map.get("exeMinutes");
                    final int hours = map.get("exeHours");
                    final String weeklyTime = hours + ":" + minutes;
                    schedulerModel.setWeeklyTime(weeklyTime);
                    break;
                }
            }
        }
    }
    
    static {
        AppUpdatePolicySchedulerHandler.appUpdatePolicySchedulerHandler = null;
    }
}
