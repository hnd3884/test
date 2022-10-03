package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import com.unboundid.ldap.sdk.Entry;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RotateLogTask extends Task
{
    static final String ROTATE_LOG_TASK_CLASS = "com.unboundid.directory.server.tasks.RotateLogTask";
    private static final String ATTR_PATH = "ds-task-rotate-log-path";
    private static final String OC_ROTATE_LOG_TASK = "ds-task-rotate-log";
    private static final TaskProperty PROPERTY_PATH;
    private static final long serialVersionUID = -7737121245254808139L;
    private final List<String> paths;
    
    public RotateLogTask() {
        this.paths = null;
    }
    
    public RotateLogTask(final String taskID, final String... paths) {
        this(taskID, null, null, null, null, (List<String>)null, paths);
    }
    
    public RotateLogTask(final String taskID, final Collection<String> paths) {
        this(taskID, null, null, null, null, null, paths);
    }
    
    public RotateLogTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError, final String... paths) {
        this(taskID, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError, StaticUtils.toList(paths));
    }
    
    public RotateLogTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError, final Collection<String> paths) {
        this(taskID, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null, paths);
    }
    
    public RotateLogTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError, final Collection<String> paths) {
        super(taskID, "com.unboundid.directory.server.tasks.RotateLogTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        if (paths == null) {
            this.paths = Collections.emptyList();
        }
        else {
            this.paths = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(paths));
        }
    }
    
    public RotateLogTask(final Entry entry) throws TaskException {
        super(entry);
        final String[] pathValues = entry.getAttributeValues("ds-task-rotate-log-path");
        if (pathValues == null) {
            this.paths = Collections.emptyList();
        }
        else {
            this.paths = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(Arrays.asList(pathValues)));
        }
    }
    
    public RotateLogTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.RotateLogTask", properties);
        String[] pathArray = StaticUtils.NO_STRINGS;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-rotate-log-path")) {
                pathArray = Task.parseStrings(p, values, pathArray);
            }
        }
        this.paths = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])pathArray));
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_ROTATE_LOG.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_ROTATE_LOG.get();
    }
    
    public List<String> getPaths() {
        return this.paths;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-rotate-log");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        if (this.paths.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new Attribute("ds-task-rotate-log-path", this.paths));
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.singletonList(RotateLogTask.PROPERTY_PATH);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        if (!this.paths.isEmpty()) {
            props.put(RotateLogTask.PROPERTY_PATH, Collections.unmodifiableList((List<?>)this.paths));
        }
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_PATH = new TaskProperty("ds-task-rotate-log-path", TaskMessages.INFO_ROTATE_LOG_DISPLAY_NAME_PATH.get(), TaskMessages.INFO_ROTATE_LOG_DESCRIPTION_PATH.get(), String.class, false, true, false);
    }
}
