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
public final class GroovyScriptedTask extends Task
{
    static final String GROOVY_SCRIPTED_TASK_CLASS = "com.unboundid.directory.sdk.extensions.GroovyScriptedTask";
    private static final String ATTR_GROOVY_SCRIPTED_TASK_CLASS = "ds-scripted-task-class";
    private static final String ATTR_GROOVY_SCRIPTED_TASK_ARGUMENT = "ds-scripted-task-argument";
    private static final String OC_GROOVY_SCRIPTED_TASK = "ds-groovy-scripted-task";
    static final TaskProperty PROPERTY_TASK_CLASS;
    static final TaskProperty PROPERTY_TASK_ARG;
    private static final long serialVersionUID = -1354970323227263273L;
    private final List<String> taskArguments;
    private final String taskClassName;
    
    public GroovyScriptedTask() {
        this.taskArguments = null;
        this.taskClassName = null;
    }
    
    public GroovyScriptedTask(final String taskID, final String taskClassName, final List<String> taskArguments) {
        this(taskID, taskClassName, taskArguments, null, null, null, null, null);
    }
    
    public GroovyScriptedTask(final String taskID, final String taskClassName, final List<String> taskArguments, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, taskClassName, taskArguments, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public GroovyScriptedTask(final String taskID, final String taskClassName, final List<String> taskArguments, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.sdk.extensions.GroovyScriptedTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(taskClassName);
        this.taskClassName = taskClassName;
        if (taskArguments == null) {
            this.taskArguments = Collections.emptyList();
        }
        else {
            this.taskArguments = Collections.unmodifiableList((List<? extends String>)taskArguments);
        }
    }
    
    public GroovyScriptedTask(final Entry entry) throws TaskException {
        super(entry);
        this.taskClassName = entry.getAttributeValue("ds-scripted-task-class");
        if (this.taskClassName == null) {
            throw new TaskException(TaskMessages.ERR_GROOVY_SCRIPTED_TASK_NO_CLASS.get(this.getTaskEntryDN()));
        }
        final String[] args = entry.getAttributeValues("ds-scripted-task-argument");
        if (args == null || args.length == 0) {
            this.taskArguments = Collections.emptyList();
        }
        else {
            this.taskArguments = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])args));
        }
    }
    
    public GroovyScriptedTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.sdk.extensions.GroovyScriptedTask", properties);
        String className = null;
        String[] args = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-scripted-task-class")) {
                className = Task.parseString(p, values, null);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-scripted-task-argument")) {
                    continue;
                }
                args = Task.parseStrings(p, values, null);
            }
        }
        if (className == null) {
            throw new TaskException(TaskMessages.ERR_GROOVY_SCRIPTED_TASK_NO_CLASS.get(this.getTaskEntryDN()));
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
        return TaskMessages.INFO_TASK_NAME_GROOVY_SCRIPTED_TASK.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_GROOVY_SCRIPTED_TASK.get();
    }
    
    public String getGroovyScriptedTaskClassName() {
        return this.taskClassName;
    }
    
    public List<String> getGroovyScriptedTaskArguments() {
        return this.taskArguments;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-groovy-scripted-task");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(2);
        attrList.add(new Attribute("ds-scripted-task-class", this.taskClassName));
        if (!this.taskArguments.isEmpty()) {
            attrList.add(new Attribute("ds-scripted-task-argument", this.taskArguments));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(GroovyScriptedTask.PROPERTY_TASK_CLASS, GroovyScriptedTask.PROPERTY_TASK_ARG));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(2));
        props.put(GroovyScriptedTask.PROPERTY_TASK_CLASS, (List<Object>)Collections.singletonList(this.taskClassName));
        props.put(GroovyScriptedTask.PROPERTY_TASK_ARG, Collections.unmodifiableList((List<?>)this.taskArguments));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_TASK_CLASS = new TaskProperty("ds-scripted-task-class", TaskMessages.INFO_DISPLAY_NAME_GROOVY_SCRIPTED_TASK_CLASS.get(), TaskMessages.INFO_DESCRIPTION_GROOVY_SCRIPTED_TASK_CLASS.get(), String.class, true, false, false);
        PROPERTY_TASK_ARG = new TaskProperty("ds-scripted-task-argument", TaskMessages.INFO_DISPLAY_NAME_GROOVY_SCRIPTED_TASK_ARG.get(), TaskMessages.INFO_DESCRIPTION_GROOVY_SCRIPTED_TASK_ARG.get(), String.class, false, true, false);
    }
}
