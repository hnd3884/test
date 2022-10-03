package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.Date;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ThirdPartyTask extends Task
{
    static final String THIRD_PARTY_TASK_CLASS = "com.unboundid.directory.sdk.extensions.ThirdPartyTask";
    private static final String ATTR_THIRD_PARTY_TASK_CLASS = "ds-third-party-task-java-class";
    private static final String ATTR_THIRD_PARTY_TASK_ARGUMENT = "ds-third-party-task-argument";
    private static final String OC_THIRD_PARTY_TASK = "ds-third-party-task";
    static final TaskProperty PROPERTY_TASK_CLASS;
    static final TaskProperty PROPERTY_TASK_ARG;
    private static final long serialVersionUID = 8448474409066265724L;
    private final List<String> taskArguments;
    private final String taskClassName;
    
    public ThirdPartyTask() {
        this.taskArguments = null;
        this.taskClassName = null;
    }
    
    public ThirdPartyTask(final String taskID, final String taskClassName, final List<String> taskArguments) {
        this(taskID, taskClassName, taskArguments, null, null, null, null, null);
    }
    
    public ThirdPartyTask(final String taskID, final String taskClassName, final List<String> taskArguments, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, taskClassName, taskArguments, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ThirdPartyTask(final String taskID, final String taskClassName, final List<String> taskArguments, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.sdk.extensions.ThirdPartyTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(taskClassName);
        this.taskClassName = taskClassName;
        if (taskArguments == null) {
            this.taskArguments = Collections.emptyList();
        }
        else {
            this.taskArguments = Collections.unmodifiableList((List<? extends String>)taskArguments);
        }
    }
    
    public ThirdPartyTask(final Entry entry) throws TaskException {
        super(entry);
        this.taskClassName = entry.getAttributeValue("ds-third-party-task-java-class");
        if (this.taskClassName == null) {
            throw new TaskException(TaskMessages.ERR_THIRD_PARTY_TASK_NO_CLASS.get(this.getTaskEntryDN()));
        }
        final String[] args = entry.getAttributeValues("ds-third-party-task-argument");
        if (args == null || args.length == 0) {
            this.taskArguments = Collections.emptyList();
        }
        else {
            this.taskArguments = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])args));
        }
    }
    
    public ThirdPartyTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.sdk.extensions.ThirdPartyTask", properties);
        String className = null;
        String[] args = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-third-party-task-java-class")) {
                className = Task.parseString(p, values, null);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-third-party-task-argument")) {
                    continue;
                }
                args = Task.parseStrings(p, values, null);
            }
        }
        if (className == null) {
            throw new TaskException(TaskMessages.ERR_THIRD_PARTY_TASK_NO_CLASS.get(this.getTaskEntryDN()));
        }
        this.taskClassName = className;
        if (args == null) {
            this.taskArguments = Collections.emptyList();
        }
        else {
            this.taskArguments = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])args));
        }
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_THIRD_PARTY_TASK.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_THIRD_PARTY_TASK.get();
    }
    
    public String getThirdPartyTaskClassName() {
        return this.taskClassName;
    }
    
    public List<String> getThirdPartyTaskArguments() {
        return this.taskArguments;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-third-party-task");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(2);
        attrList.add(new Attribute("ds-third-party-task-java-class", this.taskClassName));
        if (!this.taskArguments.isEmpty()) {
            attrList.add(new Attribute("ds-third-party-task-argument", this.taskArguments));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(ThirdPartyTask.PROPERTY_TASK_CLASS, ThirdPartyTask.PROPERTY_TASK_ARG));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(2));
        props.put(ThirdPartyTask.PROPERTY_TASK_CLASS, (List<Object>)Collections.singletonList(this.taskClassName));
        props.put(ThirdPartyTask.PROPERTY_TASK_ARG, Collections.unmodifiableList((List<?>)this.taskArguments));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_TASK_CLASS = new TaskProperty("ds-third-party-task-java-class", TaskMessages.INFO_DISPLAY_NAME_THIRD_PARTY_TASK_CLASS.get(), TaskMessages.INFO_DESCRIPTION_THIRD_PARTY_TASK_CLASS.get(), String.class, true, false, false);
        PROPERTY_TASK_ARG = new TaskProperty("ds-third-party-task-argument", TaskMessages.INFO_DISPLAY_NAME_THIRD_PARTY_TASK_ARG.get(), TaskMessages.INFO_DESCRIPTION_THIRD_PARTY_TASK_ARG.get(), String.class, false, true, false);
    }
}
