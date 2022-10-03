package com.me.mdm.api.command.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleActionsResponseModel
{
    @JsonAlias({ "collections" })
    private List<Long> collections;
    @JsonAlias({ "scheduled_command_ids" })
    private List<Long> schedule_command_ids;
    @JsonAlias({ "schedule_id" })
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("schedule_id")
    private Long scheduleID;
    @JsonAlias({ "status" })
    @JsonProperty("status")
    private Integer status;
    @JsonAlias({ "command" })
    private String command;
    @JsonAlias({ "execution_time" })
    private String executionTime;
    @JsonAlias({ "schedule_type" })
    private Integer scheduleType;
    @JsonAlias({ "daily_interval_type" })
    private Integer dailyIntervalType;
    @JsonAlias({ "days_of_week" })
    private List<Integer> daysOfWeek;
    @JsonAlias({ "months" })
    private List<Integer> months;
    @JsonAlias({ "monthly_type" })
    private Integer monthlyType;
    @JsonAlias({ "dates" })
    private Integer dates;
    @JsonAlias({ "monthly_week_day" })
    private Integer monthlyWeekDay;
    @JsonAlias({ "monthly_week_num" })
    private List<Integer> monthlyWeekNum;
    
    public List<Long> getCollections() {
        return this.collections;
    }
    
    public void setCollections(final List<Long> collections) {
        this.collections = collections;
    }
    
    public Long getScheduleID() {
        return this.scheduleID;
    }
    
    public void setScheduleID(final Long scheduleID) {
        this.scheduleID = scheduleID;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public void setDaysOfWeek(final List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public void setDailyIntervalType(final Integer dailyIntervalType) {
        this.dailyIntervalType = dailyIntervalType;
    }
    
    public void setDates(final Integer dates) {
        this.dates = dates;
    }
    
    public void setExecutionTime(final String executionTime) {
        this.executionTime = executionTime;
    }
    
    public void setMonthlyType(final Integer monthlyType) {
        this.monthlyType = monthlyType;
    }
    
    public void setMonthlyWeekDay(final Integer monthlyWeekDay) {
        this.monthlyWeekDay = monthlyWeekDay;
    }
    
    public void setScheduleType(final Integer scheduleType) {
        this.scheduleType = scheduleType;
    }
    
    public void setMonthlyWeekNum(final List<Integer> monthlyWeekNum) {
        this.monthlyWeekNum = monthlyWeekNum;
    }
    
    public void setMonths(final List<Integer> months) {
        this.months = months;
    }
    
    public Integer getDailyIntervalType() {
        return this.dailyIntervalType;
    }
    
    public Integer getMonthlyType() {
        return this.monthlyType;
    }
    
    public Integer getScheduleType() {
        return this.scheduleType;
    }
    
    public Integer getMonthlyWeekDay() {
        return this.monthlyWeekDay;
    }
    
    public List<Integer> getDaysOfWeek() {
        return this.daysOfWeek;
    }
    
    public List<Integer> getMonthlyWeekNum() {
        return this.monthlyWeekNum;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getExecutionTime() {
        return this.executionTime;
    }
    
    public Integer getDates() {
        return this.dates;
    }
    
    public List<Long> getSchedule_command_ids() {
        return this.schedule_command_ids;
    }
    
    public void setSchedule_command_ids(final List<Long> schedule_command_ids) {
        this.schedule_command_ids = schedule_command_ids;
    }
    
    public List<Integer> getMonths() {
        return this.months;
    }
}
