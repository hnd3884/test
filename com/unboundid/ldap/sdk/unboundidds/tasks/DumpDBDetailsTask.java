package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DumpDBDetailsTask extends Task
{
    static final String DUMP_DB_DETAILS_TASK_CLASS = "com.unboundid.directory.server.tasks.DumpDBDetailsTask";
    private static final String ATTR_BACKEND_ID = "ds-task-dump-db-backend-id";
    private static final String OC_DUMP_DB_DETAILS_TASK = "ds-task-dump-db";
    private static final TaskProperty PROPERTY_BACKEND_ID;
    private static final long serialVersionUID = 7267871080385864231L;
    private final String backendID;
    
    public DumpDBDetailsTask() {
        this.backendID = null;
    }
    
    public DumpDBDetailsTask(final String taskID, final String backendID) {
        this(taskID, backendID, null, null, null, null, null);
    }
    
    public DumpDBDetailsTask(final String taskID, final String backendID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backendID, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public DumpDBDetailsTask(final String taskID, final String backendID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.DumpDBDetailsTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(backendID);
        this.backendID = backendID;
    }
    
    public DumpDBDetailsTask(final Entry entry) throws TaskException {
        super(entry);
        this.backendID = entry.getAttributeValue("ds-task-dump-db-backend-id");
        if (this.backendID == null) {
            throw new TaskException(TaskMessages.ERR_DUMP_DB_ENTRY_MISSING_BACKEND_ID.get(this.getTaskEntryDN(), "ds-task-dump-db-backend-id"));
        }
    }
    
    public DumpDBDetailsTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.DumpDBDetailsTask", properties);
        String id = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-dump-db-backend-id")) {
                id = Task.parseString(p, values, id);
            }
        }
        if (id == null) {
            throw new TaskException(TaskMessages.ERR_DUMP_DB_ENTRY_MISSING_BACKEND_ID.get(this.getTaskEntryDN(), "ds-task-dump-db-backend-id"));
        }
        this.backendID = id;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_DUMP_DB.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_DUMP_DB.get();
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-dump-db");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        return Collections.singletonList(new Attribute("ds-task-dump-db-backend-id", this.backendID));
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.singletonList(DumpDBDetailsTask.PROPERTY_BACKEND_ID);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(1));
        props.put(DumpDBDetailsTask.PROPERTY_BACKEND_ID, (List<Object>)Collections.singletonList(this.backendID));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKEND_ID = new TaskProperty("ds-task-dump-db-backend-id", TaskMessages.INFO_DUMP_DB_DISPLAY_NAME_BACKEND_ID.get(), TaskMessages.INFO_DUMP_DB_DESCRIPTION_BACKEND_ID.get(), String.class, true, false, false);
    }
}
