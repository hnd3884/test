package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Arrays;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DisconnectClientTask extends Task
{
    static final String DISCONNECT_CLIENT_TASK_CLASS = "com.unboundid.directory.server.tasks.DisconnectClientTask";
    private static final String ATTR_CONNECTION_ID = "ds-task-disconnect-connection-id";
    private static final String ATTR_DISCONNECT_MESSAGE = "ds-task-disconnect-message";
    private static final String ATTR_NOTIFY_CLIENT = "ds-task-disconnect-notify-client";
    private static final String OC_DISCONNECT_CLIENT_TASK = "ds-task-disconnect";
    private static final TaskProperty PROPERTY_CONNECTION_ID;
    private static final TaskProperty PROPERTY_DISCONNECT_MESSAGE;
    private static final TaskProperty PROPERTY_NOTIFY_CLIENT;
    private static final long serialVersionUID = 6870137048384152893L;
    private final boolean notifyClient;
    private final long connectionID;
    private final String disconnectMessage;
    
    public DisconnectClientTask() {
        this.notifyClient = false;
        this.connectionID = -1L;
        this.disconnectMessage = null;
    }
    
    public DisconnectClientTask(final String taskID, final long connectionID, final String disconnectMessage, final boolean notifyClient) {
        this(taskID, connectionID, disconnectMessage, notifyClient, null, null, null, null, null);
    }
    
    public DisconnectClientTask(final String taskID, final long connectionID, final String disconnectMessage, final boolean notifyClient, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, connectionID, disconnectMessage, notifyClient, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public DisconnectClientTask(final String taskID, final long connectionID, final String disconnectMessage, final boolean notifyClient, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.DisconnectClientTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.connectionID = connectionID;
        this.disconnectMessage = disconnectMessage;
        this.notifyClient = notifyClient;
    }
    
    public DisconnectClientTask(final Entry entry) throws TaskException {
        super(entry);
        final String idStr = entry.getAttributeValue("ds-task-disconnect-connection-id");
        if (idStr == null) {
            throw new TaskException(TaskMessages.ERR_DISCONNECT_TASK_NO_CONN_ID.get(this.getTaskEntryDN()));
        }
        try {
            this.connectionID = Long.parseLong(idStr);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new TaskException(TaskMessages.ERR_DISCONNECT_TASK_CONN_ID_NOT_LONG.get(this.getTaskEntryDN(), idStr), e);
        }
        this.disconnectMessage = entry.getAttributeValue("ds-task-disconnect-message");
        this.notifyClient = Task.parseBooleanValue(entry, "ds-task-disconnect-notify-client", false);
    }
    
    public DisconnectClientTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.DisconnectClientTask", properties);
        boolean notify = false;
        Long connID = null;
        String msg = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-disconnect-connection-id")) {
                connID = Task.parseLong(p, values, connID);
            }
            else if (attrName.equalsIgnoreCase("ds-task-disconnect-message")) {
                msg = Task.parseString(p, values, msg);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-disconnect-notify-client")) {
                    continue;
                }
                notify = Task.parseBoolean(p, values, notify);
            }
        }
        if (connID == null) {
            throw new TaskException(TaskMessages.ERR_DISCONNECT_TASK_NO_CONN_ID.get(this.getTaskEntryDN()));
        }
        this.connectionID = connID;
        this.disconnectMessage = msg;
        this.notifyClient = notify;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_DISCONNECT_CLIENT.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_DISCONNECT_CLIENT.get();
    }
    
    public long getConnectionID() {
        return this.connectionID;
    }
    
    public String getDisconnectMessage() {
        return this.disconnectMessage;
    }
    
    public boolean notifyClient() {
        return this.notifyClient;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-disconnect");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(3);
        attrs.add(new Attribute("ds-task-disconnect-connection-id", String.valueOf(this.connectionID)));
        attrs.add(new Attribute("ds-task-disconnect-notify-client", String.valueOf(this.notifyClient)));
        if (this.disconnectMessage != null) {
            attrs.add(new Attribute("ds-task-disconnect-message", this.disconnectMessage));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(DisconnectClientTask.PROPERTY_CONNECTION_ID, DisconnectClientTask.PROPERTY_DISCONNECT_MESSAGE, DisconnectClientTask.PROPERTY_NOTIFY_CLIENT);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        props.put(DisconnectClientTask.PROPERTY_CONNECTION_ID, (List<Object>)Collections.singletonList(this.connectionID));
        if (this.disconnectMessage == null) {
            props.put(DisconnectClientTask.PROPERTY_DISCONNECT_MESSAGE, Collections.emptyList());
        }
        else {
            props.put(DisconnectClientTask.PROPERTY_DISCONNECT_MESSAGE, (List<Object>)Collections.singletonList(this.disconnectMessage));
        }
        props.put(DisconnectClientTask.PROPERTY_NOTIFY_CLIENT, (List<Object>)Collections.singletonList(this.notifyClient));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_CONNECTION_ID = new TaskProperty("ds-task-disconnect-connection-id", TaskMessages.INFO_DISPLAY_NAME_DISCONNECT_CONN_ID.get(), TaskMessages.INFO_DESCRIPTION_DISCONNECT_CONN_ID.get(), Long.class, true, false, false);
        PROPERTY_DISCONNECT_MESSAGE = new TaskProperty("ds-task-disconnect-message", TaskMessages.INFO_DISPLAY_NAME_DISCONNECT_MESSAGE.get(), TaskMessages.INFO_DESCRIPTION_DISCONNECT_MESSAGE.get(), String.class, false, false, false);
        PROPERTY_NOTIFY_CLIENT = new TaskProperty("ds-task-disconnect-notify-client", TaskMessages.INFO_DISPLAY_NAME_DISCONNECT_NOTIFY.get(), TaskMessages.INFO_DESCRIPTION_DISCONNECT_NOTIFY.get(), Boolean.class, false, false, false);
    }
}
