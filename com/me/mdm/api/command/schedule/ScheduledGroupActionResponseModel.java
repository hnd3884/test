package com.me.mdm.api.command.schedule;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

public class ScheduledGroupActionResponseModel
{
    @JsonAlias({ "initiated_by" })
    @JsonProperty("initiated_by")
    private String inititedUser;
    @JsonAlias({ "initiated_time" })
    private Long initiated_time;
    @JsonAlias({ "expiry" })
    private Long expiry;
    @JsonAlias({ "updated_by" })
    private String updatedBy;
    @JsonAlias({ "updated_time" })
    private Long updatedTime;
    @JsonAlias({ "frequency" })
    private String frequency;
    @JsonAlias({ "action_type" })
    private Integer action_type;
    @JsonAlias({ "is_scheduled" })
    private Boolean isScheduled;
    @JsonAlias({ "is_suspended" })
    private Boolean isSuspened;
    @JsonAlias({ "action_purpose" })
    private String action_purpose;
    @JsonAlias({ "next_execution_time" })
    private Long nextExecutionTime;
    @JsonAlias({ "time_zone" })
    private String timeZone;
    @JsonAlias({ "days_of_week" })
    private List<Integer> daysOfWeek;
    
    public String getInititedUser() {
        return this.inititedUser;
    }
    
    public Long getInitiated_time() {
        return this.initiated_time;
    }
    
    public String getUpdatedBy() {
        return this.updatedBy;
    }
    
    public Long getUpdatedTime() {
        return this.updatedTime;
    }
    
    public String getFrequency() {
        return this.frequency;
    }
    
    public Integer getaction_type() {
        return this.action_type;
    }
    
    public Boolean getSuspened() {
        return this.isSuspened;
    }
    
    public String getAction_purpose() {
        return this.action_purpose;
    }
    
    public Long getNextExecutionTime() {
        return this.nextExecutionTime;
    }
    
    public String getTimeZone() {
        return this.timeZone;
    }
    
    public Boolean getIsScheduled() {
        return this.isScheduled;
    }
    
    public Long getExpiry() {
        return this.expiry;
    }
    
    public List<Integer> getDaysOfWeek() {
        return this.daysOfWeek;
    }
    
    public void setaction_type(final Integer action_type) {
        this.action_type = action_type;
    }
    
    public void setDaysOfWeek(final List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }
    
    public void setFrequency(final String frequency) {
        this.frequency = frequency;
    }
    
    public void setInitiated_time(final Long initiated_time) {
        this.initiated_time = initiated_time;
    }
    
    public void setInititedUser(final String inititedUser) {
        this.inititedUser = inititedUser;
    }
    
    public void setNextExecutionTime(final Long nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }
    
    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }
    
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public void setUpdatedTime(final Long updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    public void setAction_purpose(final String action_purpose) {
        this.action_purpose = action_purpose;
    }
    
    public void setIsScheduled(final Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }
    
    public void setSuspened(final Boolean suspened) {
        this.isSuspened = suspened;
    }
    
    public void setExpiry(final Long expiry) {
        this.expiry = expiry;
    }
}
