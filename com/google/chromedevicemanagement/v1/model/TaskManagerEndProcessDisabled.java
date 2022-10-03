package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class TaskManagerEndProcessDisabled extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private Boolean taskManagerEndProcessDisabled;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public TaskManagerEndProcessDisabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public Boolean getTaskManagerEndProcessDisabled() {
        return this.taskManagerEndProcessDisabled;
    }
    
    public TaskManagerEndProcessDisabled setTaskManagerEndProcessDisabled(final Boolean taskManagerEndProcessDisabled) {
        this.taskManagerEndProcessDisabled = taskManagerEndProcessDisabled;
        return this;
    }
    
    public TaskManagerEndProcessDisabled set(final String s, final Object o) {
        return (TaskManagerEndProcessDisabled)super.set(s, o);
    }
    
    public TaskManagerEndProcessDisabled clone() {
        return (TaskManagerEndProcessDisabled)super.clone();
    }
}
