package com.me.devicemanagement.framework.server.scheduler;

import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.logging.Logger;

public class SchedulerInfo
{
    private static SchedulerInfo schedule;
    private Logger logger;
    
    public SchedulerInfo() {
        this.logger = Logger.getLogger(SchedulerInfo.class.getName());
    }
    
    public static SchedulerInfo getInstance() {
        if (SchedulerInfo.schedule == null) {
            SchedulerInfo.schedule = new SchedulerInfo();
        }
        return SchedulerInfo.schedule;
    }
    
    public static String getI18nValforRepeatFreq(final String repeatFreq) {
        String repeatFreq_i18n = "";
        try {
            if (repeatFreq.equalsIgnoreCase("Once")) {
                repeatFreq_i18n = I18N.getMsg("dc.common.ONCE", new Object[0]);
            }
            else if (repeatFreq.equalsIgnoreCase("Daily")) {
                repeatFreq_i18n = I18N.getMsg("dc.common.DAILY", new Object[0]);
            }
            else if (repeatFreq.equalsIgnoreCase("Weekly")) {
                repeatFreq_i18n = I18N.getMsg("dc.common.WEEKLY", new Object[0]);
            }
            else if (repeatFreq.equalsIgnoreCase("Monthly")) {
                repeatFreq_i18n = I18N.getMsg("dc.common.MONTHLY", new Object[0]);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return repeatFreq_i18n;
    }
    
    public static HashMap convertSchedulerDetails(final HashMap scheduleDetailHash) {
        String weekDays = "";
        final HashMap convSchedulerDetails = new HashMap();
        if (scheduleDetailHash.isEmpty()) {
            convSchedulerDetails.put("scheduleType", "Once");
            convSchedulerDetails.put("onceStartTime", "");
            convSchedulerDetails.put("onceStartDate", "");
        }
        else {
            final String schType = scheduleDetailHash.get("schedType");
            final int hours = scheduleDetailHash.get("exeHours");
            final int minutes = scheduleDetailHash.get("exeMinutes");
            final int seconds = scheduleDetailHash.get("exeSeconds");
            String updateTime = null;
            if (minutes < 10) {
                updateTime = hours + ":0" + minutes;
            }
            else {
                updateTime = hours + ":" + minutes;
            }
            if (seconds < 10) {
                updateTime = updateTime + ":0" + seconds;
            }
            else {
                updateTime = updateTime + ":" + seconds;
            }
            String dateOfExec = "";
            if (schType.equals("Once") || schType.equalsIgnoreCase("Daily")) {
                final int day = scheduleDetailHash.get("startDate");
                final int month = scheduleDetailHash.get("startMonth");
                final int year = scheduleDetailHash.get("startYear");
                dateOfExec = ((month < 9) ? dateOfExec.concat("0" + (month + 1) + "/") : dateOfExec.concat("" + (month + 1) + "/"));
                dateOfExec = ((day < 10) ? dateOfExec.concat("0" + day + "/") : dateOfExec.concat("" + day + "/"));
                dateOfExec = dateOfExec.concat("" + year);
                if (schType.equals("Once")) {
                    convSchedulerDetails.put("scheduleType", "Once");
                    convSchedulerDetails.put("onceStartTime", updateTime);
                    convSchedulerDetails.put("onceStartDate", dateOfExec);
                }
                else if (schType.equalsIgnoreCase("Daily")) {
                    convSchedulerDetails.put("scheduleType", "Daily");
                    convSchedulerDetails.put("dailyStartTime", updateTime);
                    convSchedulerDetails.put("dailyStartDate", dateOfExec);
                    final String intervalType = scheduleDetailHash.get("dailyIntervalType");
                    convSchedulerDetails.put("dailyIntervalType", intervalType);
                }
            }
            else if (schType.equalsIgnoreCase("Weekly")) {
                int[] days = new int[7];
                convSchedulerDetails.put("scheduleType", "Weekly");
                convSchedulerDetails.put("weeklyStartTime", updateTime);
                days = scheduleDetailHash.get("daysOfWeek");
                for (int j = 0; j < days.length; ++j) {
                    weekDays = weekDays + days[j] + ",";
                    weekDays = weekDays.trim();
                }
                convSchedulerDetails.put("daysOfWeek", weekDays);
            }
            else if (schType.equalsIgnoreCase("Monthly")) {
                convSchedulerDetails.put("scheduleType", "Monthly");
                convSchedulerDetails.put("monthlyStartTime", updateTime);
                final int[] months = scheduleDetailHash.get("months");
                String monthList = "";
                for (int i = 0; i < months.length; ++i) {
                    monthList = monthList + months[i] + ",";
                    monthList = monthList.trim();
                }
                final String monthlyPerform = scheduleDetailHash.get("monthlyPerform");
                convSchedulerDetails.put("monthlyPerform", monthlyPerform);
                if (monthlyPerform.equals("Day")) {
                    String updateDate = "";
                    final Integer dateOfMonth = scheduleDetailHash.get("dates");
                    if (dateOfMonth < 10) {
                        updateDate = "0" + dateOfMonth;
                    }
                    else {
                        updateDate = dateOfMonth + "";
                    }
                    convSchedulerDetails.put("monthlyDay", updateDate);
                }
                else if (monthlyPerform.equals("WeekDay")) {
                    convSchedulerDetails.put("monthlyWeekDay", scheduleDetailHash.get("monthlyWeekDay") + "");
                    final int[] monthlyWeek = scheduleDetailHash.get("monthlyWeekNum");
                    String monthlyWeekNum = "";
                    for (int k = 0; k < monthlyWeek.length; ++k) {
                        monthlyWeekNum = monthlyWeekNum + monthlyWeek[k] + ",";
                        monthlyWeekNum = monthlyWeekNum.trim();
                    }
                    if (monthlyWeekNum.charAt(monthlyWeekNum.length() - 1) == ',') {
                        monthlyWeekNum = monthlyWeekNum.substring(0, monthlyWeekNum.length() - 1);
                    }
                    convSchedulerDetails.put("monthlyWeekNum", monthlyWeekNum);
                }
                convSchedulerDetails.put("monthsList", monthList);
            }
        }
        return convSchedulerDetails;
    }
    
    public String getWeekDaysString(final String windowDays) {
        String windowDaysString = "";
        try {
            final String[] days = windowDays.split(",");
            final int daysLength = days.length;
            if (daysLength != 7) {
                for (int i = 0; i < daysLength; ++i) {
                    final int day = Integer.parseInt(days[i]);
                    String dayText = "";
                    if (day == 1) {
                        dayText = I18N.getMsg("dc.common.scheduler.sun", new Object[0]);
                    }
                    else if (day == 2) {
                        dayText = I18N.getMsg("dc.common.scheduler.mon", new Object[0]);
                    }
                    else if (day == 3) {
                        dayText = I18N.getMsg("dc.common.scheduler.tue", new Object[0]);
                    }
                    else if (day == 4) {
                        dayText = I18N.getMsg("dc.common.scheduler.wed", new Object[0]);
                    }
                    else if (day == 5) {
                        dayText = I18N.getMsg("dc.common.scheduler.thu", new Object[0]);
                    }
                    else if (day == 6) {
                        dayText = I18N.getMsg("dc.common.scheduler.fri", new Object[0]);
                    }
                    else if (day == 7) {
                        dayText = I18N.getMsg("dc.common.scheduler.sat", new Object[0]);
                    }
                    if (i == 0) {
                        windowDaysString = dayText;
                    }
                    else {
                        windowDaysString = windowDaysString + ", " + dayText;
                    }
                }
            }
            else {
                windowDaysString = I18N.getMsg("dc.common.scheduler.all_days", new Object[0]);
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return windowDaysString;
    }
    
    public String getDateString(String date) {
        try {
            final int intDate = new Integer(date);
            if (intDate == 1 || intDate == 21 || intDate == 31) {
                date += "st";
            }
            else if (intDate == 2 || intDate == 22) {
                date += "nd";
            }
            else if (intDate == 3 || intDate == 23) {
                date += "rd";
            }
            else if ((intDate >= 4 && intDate <= 20) || intDate > 23) {
                date += "th";
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return date;
    }
    
    public String getMonthString(final String monthList) {
        String monthDaysString = "";
        try {
            final String[] months = monthList.split(",");
            final int monthsLength = months.length;
            if (monthsLength != 12) {
                for (int i = 0; i < monthsLength; ++i) {
                    final int month = Integer.parseInt(months[i]);
                    String monthText = "";
                    if (month == 0) {
                        monthText = I18N.getMsg("dc.common.scheduler.jan", new Object[0]);
                    }
                    else if (month == 1) {
                        monthText = I18N.getMsg("dc.common.scheduler.feb", new Object[0]);
                    }
                    else if (month == 2) {
                        monthText = I18N.getMsg("dc.common.scheduler.mar", new Object[0]);
                    }
                    else if (month == 3) {
                        monthText = I18N.getMsg("dc.common.scheduler.apr", new Object[0]);
                    }
                    else if (month == 4) {
                        monthText = I18N.getMsg("dc.common.MAY", new Object[0]);
                    }
                    else if (month == 5) {
                        monthText = I18N.getMsg("dc.common.scheduler.jun", new Object[0]);
                    }
                    else if (month == 6) {
                        monthText = I18N.getMsg("dc.common.scheduler.jul", new Object[0]);
                    }
                    else if (month == 7) {
                        monthText = I18N.getMsg("dc.common.scheduler.aug", new Object[0]);
                    }
                    else if (month == 8) {
                        monthText = I18N.getMsg("dc.common.scheduler.sep", new Object[0]);
                    }
                    else if (month == 9) {
                        monthText = I18N.getMsg("dc.common.scheduler.oct", new Object[0]);
                    }
                    else if (month == 10) {
                        monthText = I18N.getMsg("dc.common.scheduler.nov", new Object[0]);
                    }
                    else if (month == 11) {
                        monthText = I18N.getMsg("dc.common.scheduler.dec", new Object[0]);
                    }
                    if (i == 0) {
                        monthDaysString = monthText;
                    }
                    else {
                        monthDaysString = monthDaysString + ", " + monthText;
                    }
                }
            }
            else {
                monthDaysString = I18N.getMsg("dc.common.scheduler.all_months", new Object[0]);
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return monthDaysString;
    }
    
    public String getWeekNumString(final String selectedweeks) {
        String weekNumString = "";
        try {
            final String[] weekNums = selectedweeks.split(",");
            for (int weekNumLen = weekNums.length, i = 0; i < weekNumLen; ++i) {
                final int weekNum = Integer.parseInt(weekNums[i]);
                String week = "";
                if (weekNum == 1) {
                    week = I18N.getMsg("dc.common.FIRST", new Object[0]);
                }
                else if (weekNum == 2) {
                    week = I18N.getMsg("dc.common.SECOND", new Object[0]);
                }
                else if (weekNum == 3) {
                    week = I18N.getMsg("dc.common.THIRD", new Object[0]);
                }
                else if (weekNum == 4) {
                    week = I18N.getMsg("dc.common.FOURTH", new Object[0]);
                }
                else if (weekNum == 5) {
                    week = I18N.getMsg("dc.conf.schdule.last", new Object[0]);
                }
                if (weekNumLen == 1) {
                    weekNumString = week;
                }
                else if (i != weekNumLen - 1) {
                    if (weekNumString.equalsIgnoreCase("")) {
                        weekNumString = week;
                    }
                    else {
                        weekNumString = weekNumString + ", " + week;
                    }
                }
                else {
                    weekNumString = weekNumString + " " + I18N.getMsg("dc.common.AND", new Object[0]) + " " + week;
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return weekNumString;
    }
    
    public String getDaysString(final String weekDays) {
        String weekDaysString = "";
        try {
            final String[] days = weekDays.split(",");
            for (int daysLength = days.length, i = 0; i < daysLength; ++i) {
                final int day = Integer.parseInt(days[i]);
                String dayText = "";
                if (day == 1) {
                    dayText = I18N.getMsg("dc.common.SUNDAY", new Object[0]);
                }
                else if (day == 2) {
                    dayText = I18N.getMsg("dc.common.MONDAY", new Object[0]);
                }
                else if (day == 3) {
                    dayText = I18N.getMsg("dc.common.TUESDAY", new Object[0]);
                }
                else if (day == 4) {
                    dayText = I18N.getMsg("dc.common.WEDNESDAY", new Object[0]);
                }
                else if (day == 5) {
                    dayText = I18N.getMsg("dc.common.THURSDAY", new Object[0]);
                }
                else if (day == 6) {
                    dayText = I18N.getMsg("dc.common.FRIDAY", new Object[0]);
                }
                else if (day == 7) {
                    dayText = I18N.getMsg("dc.common.SATURDAY", new Object[0]);
                }
                if (i == 0) {
                    weekDaysString = dayText;
                }
                else {
                    weekDaysString = weekDaysString + ", " + dayText;
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return weekDaysString;
    }
    
    public boolean removeScheduleForTask(final Long taskId) {
        boolean returnType = true;
        try {
            this.logger.log(Level.INFO, "Entered into removeScheduleForTask for taskid" + taskId);
            final Long schedulerClassID = ApiFactoryProvider.getSchedulerAPI().getSchedulerClassIDForTask(taskId);
            this.logger.log(Level.INFO, "removing TaskId : " + taskId + " from TaskDetails");
            final Criteria removeTaskcriteria = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskId, 0);
            final DataObject removeTaskDO = DataAccess.get("TaskDetails", removeTaskcriteria);
            final Row taskRow = removeTaskDO.getFirstRow("TaskDetails");
            final String taskName = (String)taskRow.get("TASKNAME");
            removeTaskDO.deleteRows("TaskDetails", removeTaskcriteria);
            DataAccess.update(removeTaskDO);
            if (schedulerClassID != null) {
                this.logger.log(Level.INFO, "remove  scheduler  with sheduleclassid : " + schedulerClassID + " and TaskId : " + taskId);
                ApiFactoryProvider.getSchedulerAPI().removeScheduler(schedulerClassID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in removing  scheduled reports...", e);
            returnType = false;
        }
        return returnType;
    }
    
    public boolean removeScheduleOnlyForTask(final Long taskId) {
        boolean returnType = true;
        try {
            this.logger.log(Level.INFO, "Entered into removeScheduleForTask for taskid" + taskId);
            final Long schedulerClassID = ApiFactoryProvider.getSchedulerAPI().getSchedulerClassIDForTask(taskId);
            if (schedulerClassID != null) {
                this.logger.log(Level.INFO, "remove  scheduler  with shedulename : " + schedulerClassID + " and TaskId : " + taskId);
                ApiFactoryProvider.getSchedulerAPI().removeScheduler(schedulerClassID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in removing  scheduled reports...", e);
            returnType = false;
        }
        return returnType;
    }
    
    public static String getScheduleType(final Long taskID) throws DataAccessException {
        String repeatFrequency = "Once";
        try {
            repeatFrequency = (String)DBUtil.getValueFromDB("ScheduledTaskDetails", "TASK_ID", taskID, "REPEAT_FREQUENCY");
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return repeatFrequency;
    }
    
    static {
        SchedulerInfo.schedule = null;
    }
}
