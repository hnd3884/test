package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.Date;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AddSchemaFileTask extends Task
{
    static final String ADD_SCHEMA_FILE_TASK_CLASS = "com.unboundid.directory.server.tasks.AddSchemaFileTask";
    private static final String ATTR_SCHEMA_FILE = "ds-task-schema-file-name";
    private static final String OC_ADD_SCHEMA_FILE_TASK = "ds-task-add-schema-file";
    private static final TaskProperty PROPERTY_SCHEMA_FILE;
    private static final long serialVersionUID = -5430392768265418966L;
    private final List<String> schemaFileNames;
    
    public AddSchemaFileTask() {
        this.schemaFileNames = null;
    }
    
    public AddSchemaFileTask(final String taskID, final String schemaFileName) {
        this(taskID, Collections.singletonList(schemaFileName), null, null, null, null, null);
        Validator.ensureNotNull(schemaFileName);
    }
    
    public AddSchemaFileTask(final String taskID, final List<String> schemaFileNames) {
        this(taskID, schemaFileNames, null, null, null, null, null);
    }
    
    public AddSchemaFileTask(final String taskID, final List<String> schemaFileNames, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, schemaFileNames, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public AddSchemaFileTask(final String taskID, final List<String> schemaFileNames, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.AddSchemaFileTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(schemaFileNames);
        Validator.ensureFalse(schemaFileNames.isEmpty(), "AddSchemaFileTask.schemaFileNames must not be empty.");
        this.schemaFileNames = Collections.unmodifiableList((List<? extends String>)schemaFileNames);
    }
    
    public AddSchemaFileTask(final Entry entry) throws TaskException {
        super(entry);
        final String[] fileNames = entry.getAttributeValues("ds-task-schema-file-name");
        if (fileNames == null || fileNames.length == 0) {
            throw new TaskException(TaskMessages.ERR_ADD_SCHEMA_FILE_TASK_NO_FILES.get(this.getTaskEntryDN()));
        }
        this.schemaFileNames = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])fileNames));
    }
    
    public AddSchemaFileTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.AddSchemaFileTask", properties);
        String[] names = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-schema-file-name")) {
                names = Task.parseStrings(p, values, names);
            }
        }
        if (names == null) {
            throw new TaskException(TaskMessages.ERR_ADD_SCHEMA_FILE_TASK_NO_FILES.get(this.getTaskEntryDN()));
        }
        this.schemaFileNames = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])names));
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_ADD_SCHEMA_FILE.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_ADD_SCHEMA_FILE.get();
    }
    
    public List<String> getSchemaFileNames() {
        return this.schemaFileNames;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-add-schema-file");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        return Collections.singletonList(new Attribute("ds-task-schema-file-name", this.schemaFileNames));
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.singletonList(AddSchemaFileTask.PROPERTY_SCHEMA_FILE);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        props.put(AddSchemaFileTask.PROPERTY_SCHEMA_FILE, Collections.unmodifiableList((List<?>)this.schemaFileNames));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_SCHEMA_FILE = new TaskProperty("ds-task-schema-file-name", TaskMessages.INFO_DISPLAY_NAME_SCHEMA_FILE.get(), TaskMessages.INFO_DESCRIPTION_SCHEMA_FILE.get(), String.class, true, true, false);
    }
}
