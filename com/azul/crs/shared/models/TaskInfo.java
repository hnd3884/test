package com.azul.crs.shared.models;

import java.util.Objects;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfo extends Payload
{
    private String taskId;
    private String taskType;
    private String parentTaskId;
    private TaskState taskState;
    @JsonDeserialize(as = GenericPayload.class)
    private Payload taskPayload;
    @JsonDeserialize(as = GenericPayload.class)
    private Payload taskResult;
    
    public String getTaskId() {
        return this.taskId;
    }
    
    public String getTaskType() {
        return this.taskType;
    }
    
    public String getParentTaskId() {
        return this.parentTaskId;
    }
    
    public Payload getTaskPayload() {
        return this.taskPayload;
    }
    
    public TaskState getTaskState() {
        return this.taskState;
    }
    
    public Payload getTaskResult() {
        return this.taskResult;
    }
    
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }
    
    public void setTaskType(final String taskType) {
        this.taskType = taskType;
    }
    
    public void setTaskPayload(final Payload taskPayload) {
        this.taskPayload = taskPayload;
    }
    
    public void setTaskState(final TaskState taskState) {
        this.taskState = taskState;
    }
    
    public void setTaskResult(final Payload taskResult) {
        this.taskResult = taskResult;
    }
    
    public void setParentTaskId(final String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
    
    public TaskInfo taskId(final String taskId) {
        this.setTaskId(taskId);
        return this;
    }
    
    public TaskInfo taskType(final String type) {
        this.setTaskType(type);
        return this;
    }
    
    public TaskInfo parentTaskId(final String parentTaskId) {
        this.setParentTaskId(parentTaskId);
        return this;
    }
    
    public TaskInfo taskPayload(final Payload payload) {
        this.setTaskPayload(payload);
        return this;
    }
    
    public TaskInfo taskPayload(final String json) {
        this.setTaskPayload(Payload.genericPayload(json));
        return this;
    }
    
    public TaskInfo taskState(final TaskState state) {
        this.setTaskState(state);
        return this;
    }
    
    public TaskInfo taskState(final Map state) {
        this.setTaskState(Payload.fromMap(state, TaskState.class));
        return this;
    }
    
    public TaskInfo taskState(final String json) {
        this.setTaskState(Payload.fromJsonUnchecked(json, TaskState.class));
        return this;
    }
    
    public TaskInfo taskResult(final Payload result) {
        this.setTaskResult(result);
        return this;
    }
    
    public TaskInfo taskResult(final String json) {
        this.setTaskResult(Payload.genericPayload(json));
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TaskInfo taskInfo = (TaskInfo)o;
        return Objects.equals(this.taskId, taskInfo.taskId) && Objects.equals(this.taskType, taskInfo.taskType) && Objects.equals(this.parentTaskId, taskInfo.parentTaskId) && Objects.equals(this.taskPayload, taskInfo.taskPayload) && Objects.equals(this.taskState, taskInfo.taskState) && Objects.equals(this.taskResult, taskInfo.taskResult);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.taskId, this.taskType, this.parentTaskId, this.taskPayload, this.taskState, this.taskResult);
    }
}
