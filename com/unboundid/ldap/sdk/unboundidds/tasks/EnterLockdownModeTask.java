package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EnterLockdownModeTask extends Task
{
    static final String ENTER_LOCKDOWN_MODE_TASK_CLASS = "com.unboundid.directory.server.tasks.EnterLockdownModeTask";
    private static final String ATTR_ENTER_LOCKDOWN_REASON = "ds-task-enter-lockdown-reason";
    private static final TaskProperty PROPERTY_ENTER_LOCKDOWN_REASON;
    private static final String OC_ENTER_LOCKDOWN_MODE_TASK = "ds-task-enter-lockdown-mode";
    private static final long serialVersionUID = -4104020769734351458L;
    private final String reason;
    
    public EnterLockdownModeTask() {
        this.reason = null;
    }
    
    public EnterLockdownModeTask(final String taskID) {
        this(taskID, (String)null);
    }
    
    public EnterLockdownModeTask(final String taskID, final String reason) {
        this(taskID, reason, null, null, null, null, null);
    }
    
    public EnterLockdownModeTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, null, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError);
    }
    
    public EnterLockdownModeTask(final String taskID, final String reason, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, reason, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public EnterLockdownModeTask(final String taskID, final String reason, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.EnterLockdownModeTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.reason = reason;
    }
    
    public EnterLockdownModeTask(final Entry entry) throws TaskException {
        super(entry);
        this.reason = entry.getAttributeValue("ds-task-enter-lockdown-reason");
    }
    
    public EnterLockdownModeTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.EnterLockdownModeTask", properties);
        String r = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-enter-lockdown-reason")) {
                r = Task.parseString(p, values, null);
                break;
            }
        }
        this.reason = r;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_ENTER_LOCKDOWN_MODE.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_ENTER_LOCKDOWN_MODE.get();
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-enter-lockdown-mode");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(1);
        if (this.reason != null) {
            attrs.add(new Attribute("ds-task-enter-lockdown-reason", this.reason));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Collections.singletonList(EnterLockdownModeTask.PROPERTY_ENTER_LOCKDOWN_REASON);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        if (this.reason != null) {
            props.put(EnterLockdownModeTask.PROPERTY_ENTER_LOCKDOWN_REASON, (List<Object>)Collections.singletonList(this.reason));
        }
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_ENTER_LOCKDOWN_REASON = new TaskProperty("ds-task-enter-lockdown-reason", TaskMessages.INFO_DISPLAY_NAME_ENTER_LOCKDOWN_REASON.get(), TaskMessages.INFO_DESCRIPTION_ENTER_LOCKDOWN_REASON.get(), String.class, false, false, false);
    }
}
