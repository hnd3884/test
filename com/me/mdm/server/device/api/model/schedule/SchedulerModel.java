package com.me.mdm.server.device.api.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulerModel extends BaseAPIModel
{
    @JsonProperty("schedule_type")
    private String scheduleType;
    @JsonProperty("scheduler_disabled")
    private Boolean scheduleDisabled;
    @JsonProperty("daily_interval_type")
    private String dailyIntervalType;
    @JsonProperty("daily_time")
    private String dailyTime;
    @JsonProperty("days_of_week")
    private String dayOfWeek;
    @JsonProperty("weekly_time")
    private String weeklyTime;
    @JsonIgnore
    private String weekOfMonth;
    
    public SchedulerModel() {
        this.scheduleDisabled = Boolean.FALSE;
        this.dailyIntervalType = "everyDay";
        this.dayOfWeek = "1,2,3,4,5,6,7";
        this.weekOfMonth = "1,2,3,4,5";
    }
    
    public void setWeekOfMonth(final String weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }
    
    public String getWeekOfMonth() {
        return this.weekOfMonth;
    }
    
    public void setScheduleType(final String scheduleType) {
        this.scheduleType = scheduleType;
    }
    
    public String getScheduleType() {
        return this.scheduleType;
    }
    
    public void setScheduleDisabled(final Boolean scheduleDisabled) {
        this.scheduleDisabled = scheduleDisabled;
    }
    
    public Boolean getScheduleDisabled() {
        return this.scheduleDisabled;
    }
    
    public void setDailyIntervalType(final String dailyIntervalType) {
        this.dailyIntervalType = dailyIntervalType;
    }
    
    public String getDailyIntervalType() {
        return this.dailyIntervalType;
    }
    
    public void setDailyTime(final String dailyTime) {
        this.dailyTime = dailyTime;
    }
    
    public String getDailyTime() {
        return this.dailyTime;
    }
    
    public void setDayOfWeek(final String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getDayOfWeek() {
        return this.dayOfWeek;
    }
    
    public void setWeeklyTime(final String weeklyTime) {
        this.weeklyTime = weeklyTime;
    }
    
    public String getWeeklyTime() {
        return this.weeklyTime;
    }
}
