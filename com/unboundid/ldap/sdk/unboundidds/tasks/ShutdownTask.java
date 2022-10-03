package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Arrays;
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
public final class ShutdownTask extends Task
{
    static final String SHUTDOWN_TASK_CLASS = "com.unboundid.directory.server.tasks.ShutdownTask";
    private static final String ATTR_SHUTDOWN_MESSAGE = "ds-task-shutdown-message";
    private static final String ATTR_RESTART_SERVER = "ds-task-restart-server";
    private static final String OC_SHUTDOWN_TASK = "ds-task-shutdown";
    private static final TaskProperty PROPERTY_SHUTDOWN_MESSAGE;
    private static final TaskProperty PROPERTY_RESTART_SERVER;
    private static final long serialVersionUID = -5332685779844073667L;
    private final boolean restartServer;
    private final String shutdownMessage;
    
    public ShutdownTask() {
        this.shutdownMessage = null;
        this.restartServer = false;
    }
    
    public ShutdownTask(final String taskID, final String shutdownMessage, final boolean restartServer) {
        this(taskID, shutdownMessage, restartServer, null, null, null, null, null);
    }
    
    public ShutdownTask(final String taskID, final String shutdownMessage, final boolean restartServer, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, shutdownMessage, restartServer, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ShutdownTask(final String taskID, final String shutdownMessage, final boolean restartServer, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.ShutdownTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.shutdownMessage = shutdownMessage;
        this.restartServer = restartServer;
    }
    
    public ShutdownTask(final Entry entry) throws TaskException {
        super(entry);
        this.shutdownMessage = entry.getAttributeValue("ds-task-shutdown-message");
        this.restartServer = Task.parseBooleanValue(entry, "ds-task-restart-server", false);
    }
    
    public ShutdownTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.ShutdownTask", properties);
        boolean r = false;
        String m = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-shutdown-message")) {
                m = Task.parseString(p, values, m);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-restart-server")) {
                    continue;
                }
                r = Task.parseBoolean(p, values, r);
            }
        }
        this.shutdownMessage = m;
        this.restartServer = r;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_SHUTDOWN.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_SHUTDOWN.get();
    }
    
    public String getShutdownMessage() {
        return this.shutdownMessage;
    }
    
    public boolean restartServer() {
        return this.restartServer;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-shutdown");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(2);
        if (this.shutdownMessage != null) {
            attrs.add(new Attribute("ds-task-shutdown-message", this.shutdownMessage));
        }
        attrs.add(new Attribute("ds-task-restart-server", String.valueOf(this.restartServer)));
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(ShutdownTask.PROPERTY_SHUTDOWN_MESSAGE, ShutdownTask.PROPERTY_RESTART_SERVER);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        if (this.shutdownMessage == null) {
            props.put(ShutdownTask.PROPERTY_SHUTDOWN_MESSAGE, Collections.emptyList());
        }
        else {
            props.put(ShutdownTask.PROPERTY_SHUTDOWN_MESSAGE, (List<Object>)Collections.singletonList(this.shutdownMessage));
        }
        props.put(ShutdownTask.PROPERTY_RESTART_SERVER, (List<Object>)Collections.singletonList(this.restartServer));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_SHUTDOWN_MESSAGE = new TaskProperty("ds-task-shutdown-message", TaskMessages.INFO_DISPLAY_NAME_SHUTDOWN_MESSAGE.get(), TaskMessages.INFO_DESCRIPTION_SHUTDOWN_MESSAGE.get(), String.class, false, false, false);
        PROPERTY_RESTART_SERVER = new TaskProperty("ds-task-restart-server", TaskMessages.INFO_DISPLAY_NAME_RESTART_SERVER.get(), TaskMessages.INFO_DESCRIPTION_RESTART_SERVER.get(), Boolean.class, false, false, false);
    }
}
