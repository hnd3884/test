package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskState extends Payload
{
    private State state;
    private Long submitTime;
    private Long startTime;
    private Long lastHeardTime;
    private Map<String, Object> summary;
    private List<String> errors;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean cancelRequested;
    
    public State getState() {
        return this.state;
    }
    
    public Long getSubmitTime() {
        return this.submitTime;
    }
    
    public Long getStartTime() {
        return this.startTime;
    }
    
    public Long getLastHeardTime() {
        return this.lastHeardTime;
    }
    
    public Map<String, Object> getSummary() {
        return this.summary;
    }
    
    public List<String> getErrors() {
        return this.errors;
    }
    
    public boolean isCancelRequested() {
        return this.cancelRequested;
    }
    
    public void setState(final State state) {
        this.state = state;
    }
    
    public void setSubmitTime(final Long submitTime) {
        this.submitTime = submitTime;
    }
    
    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }
    
    public void setLastHeardTime(final Long lastHeardTime) {
        this.lastHeardTime = lastHeardTime;
    }
    
    public void setSummary(final Map<String, Object> summary) {
        this.summary = summary;
    }
    
    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }
    
    public void setCancelRequested(final boolean cancelRequested) {
        this.cancelRequested = cancelRequested;
    }
    
    public TaskState state(final State state) {
        this.setState(state);
        return this;
    }
    
    public TaskState state(final String state) {
        this.setState(State.valueOf(state));
        return this;
    }
    
    public TaskState submitTime(final Long createTime) {
        this.setSubmitTime(createTime);
        return this;
    }
    
    public TaskState startTime(final Long startTime) {
        this.setStartTime(startTime);
        return this;
    }
    
    public TaskState lastHeardTime(final Long lastHeardTime) {
        this.setLastHeardTime(lastHeardTime);
        return this;
    }
    
    public TaskState summary(final Map<String, Object> summary) {
        this.setSummary(summary);
        return this;
    }
    
    public TaskState errors(final List<String> errors) {
        this.setErrors(errors);
        return this;
    }
    
    public TaskState cancelRequested(final boolean cancelRequested) {
        this.setCancelRequested(cancelRequested);
        return this;
    }
    
    public TaskState error(final String error) {
        if (this.errors == null) {
            this.errors = new LinkedList<String>();
        }
        this.errors.add(error);
        return this;
    }
    
    public TaskState error(final Throwable th) {
        return this.error((th.getMessage() != null) ? th.getMessage() : th.getClass().getName());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TaskState taskState = (TaskState)o;
        return this.state == taskState.state && Objects.equals(this.submitTime, taskState.submitTime) && Objects.equals(this.startTime, taskState.startTime) && Objects.equals(this.lastHeardTime, taskState.lastHeardTime) && Objects.equals(this.summary, taskState.summary) && Objects.equals(this.errors, taskState.errors);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.state, this.submitTime, this.startTime, this.lastHeardTime, this.summary, this.errors);
    }
    
    public enum State
    {
        SUBMITTED, 
        RUNNING, 
        WAITING, 
        SUCCEEDED, 
        ABORTED, 
        FAILED;
        
        public boolean isFinal() {
            return this == State.SUCCEEDED || this == State.ABORTED || this == State.FAILED;
        }
    }
}
