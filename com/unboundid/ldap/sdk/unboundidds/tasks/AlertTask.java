package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import com.unboundid.util.StaticUtils;
import java.util.LinkedList;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.Date;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AlertTask extends Task
{
    static final String ALERT_TASK_CLASS = "com.unboundid.directory.server.tasks.AlertTask";
    private static final String ATTR_ALERT_TYPE = "ds-task-alert-type";
    private static final String ATTR_ALERT_MESSAGE = "ds-task-alert-message";
    private static final String ATTR_ADD_DEGRADED_TYPE = "ds-task-alert-add-degraded-type";
    private static final String ATTR_REMOVE_DEGRADED_TYPE = "ds-task-alert-remove-degraded-type";
    private static final String ATTR_ADD_UNAVAILABLE_TYPE = "ds-task-alert-add-unavailable-type";
    private static final String ATTR_REMOVE_UNAVAILABLE_TYPE = "ds-task-alert-remove-unavailable-type";
    private static final String OC_ALERT_TASK = "ds-task-alert";
    private static final TaskProperty PROPERTY_ALERT_TYPE;
    private static final TaskProperty PROPERTY_ALERT_MESSAGE;
    private static final TaskProperty PROPERTY_ADD_DEGRADED_TYPE;
    private static final TaskProperty PROPERTY_REMOVE_DEGRADED_TYPE;
    private static final TaskProperty PROPERTY_ADD_UNAVAILABLE_TYPE;
    private static final TaskProperty PROPERTY_REMOVE_UNAVAILABLE_TYPE;
    private static final long serialVersionUID = 8253375533166941221L;
    private final List<String> addDegradedTypes;
    private final List<String> addUnavailableTypes;
    private final List<String> removeDegradedTypes;
    private final List<String> removeUnavailableTypes;
    private final String alertMessage;
    private final String alertType;
    
    public AlertTask() {
        this.alertType = null;
        this.alertMessage = null;
        this.addDegradedTypes = null;
        this.addUnavailableTypes = null;
        this.removeDegradedTypes = null;
        this.removeUnavailableTypes = null;
    }
    
    public AlertTask(final String alertType, final String alertMessage) {
        this(null, alertType, alertMessage, null, null, null, null, null, null, null, null, null);
    }
    
    public AlertTask(final String alertType, final String alertMessage, final List<String> addDegradedTypes, final List<String> removeDegradedTypes, final List<String> addUnavailableTypes, final List<String> removeUnavailableTypes) {
        this(null, alertType, alertMessage, addDegradedTypes, removeDegradedTypes, addUnavailableTypes, removeUnavailableTypes, null, null, null, null, null);
    }
    
    public AlertTask(final String taskID, final String alertType, final String alertMessage, final List<String> addDegradedTypes, final List<String> removeDegradedTypes, final List<String> addUnavailableTypes, final List<String> removeUnavailableTypes, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, alertType, alertMessage, addDegradedTypes, removeDegradedTypes, addUnavailableTypes, removeUnavailableTypes, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public AlertTask(final String taskID, final String alertType, final String alertMessage, final List<String> addDegradedTypes, final List<String> removeDegradedTypes, final List<String> addUnavailableTypes, final List<String> removeUnavailableTypes, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.AlertTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.alertType = alertType;
        this.alertMessage = alertMessage;
        Validator.ensureTrue(alertType == null == (alertMessage == null));
        this.addDegradedTypes = getStringList(addDegradedTypes);
        this.removeDegradedTypes = getStringList(removeDegradedTypes);
        this.addUnavailableTypes = getStringList(addUnavailableTypes);
        this.removeUnavailableTypes = getStringList(removeUnavailableTypes);
        if (alertType == null) {
            Validator.ensureFalse(this.addDegradedTypes.isEmpty() && this.removeDegradedTypes.isEmpty() && this.addUnavailableTypes.isEmpty() && this.removeUnavailableTypes.isEmpty());
        }
    }
    
    public AlertTask(final Entry entry) throws TaskException {
        super(entry);
        this.alertType = entry.getAttributeValue("ds-task-alert-type");
        this.alertMessage = entry.getAttributeValue("ds-task-alert-message");
        if (this.alertType == null != (this.alertMessage == null)) {
            throw new TaskException(TaskMessages.ERR_ALERT_TYPE_AND_MESSAGE_INTERDEPENDENT.get());
        }
        this.addDegradedTypes = Task.parseStringList(entry, "ds-task-alert-add-degraded-type");
        this.removeDegradedTypes = Task.parseStringList(entry, "ds-task-alert-remove-degraded-type");
        this.addUnavailableTypes = Task.parseStringList(entry, "ds-task-alert-add-unavailable-type");
        this.removeUnavailableTypes = Task.parseStringList(entry, "ds-task-alert-remove-unavailable-type");
        if (this.alertType == null && this.addDegradedTypes.isEmpty() && this.removeDegradedTypes.isEmpty() && this.addUnavailableTypes.isEmpty() && this.removeUnavailableTypes.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_ALERT_ENTRY_NO_ELEMENTS.get());
        }
    }
    
    public AlertTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.AlertTask", properties);
        String type = null;
        String message = null;
        final LinkedList<String> addDegraded = new LinkedList<String>();
        final LinkedList<String> removeDegraded = new LinkedList<String>();
        final LinkedList<String> addUnavailable = new LinkedList<String>();
        final LinkedList<String> removeUnavailable = new LinkedList<String>();
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = StaticUtils.toLowerCase(p.getAttributeName());
            final List<Object> values = entry.getValue();
            if (attrName.equals("ds-task-alert-type")) {
                type = Task.parseString(p, values, type);
            }
            else if (attrName.equals("ds-task-alert-message")) {
                message = Task.parseString(p, values, message);
            }
            else if (attrName.equals("ds-task-alert-add-degraded-type")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                addDegraded.addAll(Arrays.asList(s));
            }
            else if (attrName.equals("ds-task-alert-remove-degraded-type")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                removeDegraded.addAll(Arrays.asList(s));
            }
            else if (attrName.equals("ds-task-alert-add-unavailable-type")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                addUnavailable.addAll(Arrays.asList(s));
            }
            else {
                if (!attrName.equals("ds-task-alert-remove-unavailable-type")) {
                    continue;
                }
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                removeUnavailable.addAll(Arrays.asList(s));
            }
        }
        this.alertType = type;
        this.alertMessage = message;
        this.addDegradedTypes = Collections.unmodifiableList((List<? extends String>)addDegraded);
        this.removeDegradedTypes = Collections.unmodifiableList((List<? extends String>)removeDegraded);
        this.addUnavailableTypes = Collections.unmodifiableList((List<? extends String>)addUnavailable);
        this.removeUnavailableTypes = Collections.unmodifiableList((List<? extends String>)removeUnavailable);
        if (this.alertType == null != (this.alertMessage == null)) {
            throw new TaskException(TaskMessages.ERR_ALERT_TYPE_AND_MESSAGE_INTERDEPENDENT.get());
        }
        if (this.alertType == null && this.addDegradedTypes.isEmpty() && this.removeDegradedTypes.isEmpty() && this.addUnavailableTypes.isEmpty() && this.removeUnavailableTypes.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_ALERT_PROPERTIES_NO_ELEMENTS.get());
        }
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_ALERT.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_ALERT.get();
    }
    
    public String getAlertType() {
        return this.alertType;
    }
    
    public String getAlertMessage() {
        return this.alertMessage;
    }
    
    public List<String> getAddDegradedAlertTypes() {
        return this.addDegradedTypes;
    }
    
    public List<String> getRemoveDegradedAlertTypes() {
        return this.removeDegradedTypes;
    }
    
    public List<String> getAddUnavailableAlertTypes() {
        return this.addUnavailableTypes;
    }
    
    public List<String> getRemoveUnavailableAlertTypes() {
        return this.removeUnavailableTypes;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-alert");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final LinkedList<Attribute> attrList = new LinkedList<Attribute>();
        if (this.alertType != null) {
            attrList.add(new Attribute("ds-task-alert-type", this.alertType));
            attrList.add(new Attribute("ds-task-alert-message", this.alertMessage));
        }
        if (!this.addDegradedTypes.isEmpty()) {
            attrList.add(new Attribute("ds-task-alert-add-degraded-type", this.addDegradedTypes));
        }
        if (!this.removeDegradedTypes.isEmpty()) {
            attrList.add(new Attribute("ds-task-alert-remove-degraded-type", this.removeDegradedTypes));
        }
        if (!this.addUnavailableTypes.isEmpty()) {
            attrList.add(new Attribute("ds-task-alert-add-unavailable-type", this.addUnavailableTypes));
        }
        if (!this.removeUnavailableTypes.isEmpty()) {
            attrList.add(new Attribute("ds-task-alert-remove-unavailable-type", this.removeUnavailableTypes));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(AlertTask.PROPERTY_ALERT_TYPE, AlertTask.PROPERTY_ALERT_MESSAGE, AlertTask.PROPERTY_ADD_DEGRADED_TYPE, AlertTask.PROPERTY_REMOVE_DEGRADED_TYPE, AlertTask.PROPERTY_ADD_UNAVAILABLE_TYPE, AlertTask.PROPERTY_REMOVE_UNAVAILABLE_TYPE));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(6));
        if (this.alertType != null) {
            props.put(AlertTask.PROPERTY_ALERT_TYPE, (List<Object>)Collections.singletonList(this.alertType));
            props.put(AlertTask.PROPERTY_ALERT_MESSAGE, (List<Object>)Collections.singletonList(this.alertMessage));
        }
        if (!this.addDegradedTypes.isEmpty()) {
            props.put(AlertTask.PROPERTY_ADD_DEGRADED_TYPE, Collections.unmodifiableList((List<?>)this.addDegradedTypes));
        }
        if (!this.removeDegradedTypes.isEmpty()) {
            props.put(AlertTask.PROPERTY_REMOVE_DEGRADED_TYPE, Collections.unmodifiableList((List<?>)this.removeDegradedTypes));
        }
        if (!this.addUnavailableTypes.isEmpty()) {
            props.put(AlertTask.PROPERTY_ADD_UNAVAILABLE_TYPE, Collections.unmodifiableList((List<?>)this.addUnavailableTypes));
        }
        if (!this.removeUnavailableTypes.isEmpty()) {
            props.put(AlertTask.PROPERTY_REMOVE_UNAVAILABLE_TYPE, Collections.unmodifiableList((List<?>)this.removeUnavailableTypes));
        }
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    private static List<String> getStringList(final List<String> l) {
        if (l == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)l);
    }
    
    static {
        PROPERTY_ALERT_TYPE = new TaskProperty("ds-task-alert-type", TaskMessages.INFO_ALERT_DISPLAY_NAME_TYPE.get(), TaskMessages.INFO_ALERT_DESCRIPTION_TYPE.get(), String.class, false, false, false);
        PROPERTY_ALERT_MESSAGE = new TaskProperty("ds-task-alert-message", TaskMessages.INFO_ALERT_DISPLAY_NAME_MESSAGE.get(), TaskMessages.INFO_ALERT_DESCRIPTION_MESSAGE.get(), String.class, false, false, false);
        PROPERTY_ADD_DEGRADED_TYPE = new TaskProperty("ds-task-alert-add-degraded-type", TaskMessages.INFO_ALERT_DISPLAY_NAME_ADD_DEGRADED.get(), TaskMessages.INFO_ALERT_DESCRIPTION_ADD_DEGRADED.get(), String.class, false, true, false);
        PROPERTY_REMOVE_DEGRADED_TYPE = new TaskProperty("ds-task-alert-remove-degraded-type", TaskMessages.INFO_ALERT_DISPLAY_NAME_REMOVE_DEGRADED.get(), TaskMessages.INFO_ALERT_DESCRIPTION_REMOVE_DEGRADED.get(), String.class, false, true, false);
        PROPERTY_ADD_UNAVAILABLE_TYPE = new TaskProperty("ds-task-alert-add-unavailable-type", TaskMessages.INFO_ALERT_DISPLAY_NAME_ADD_UNAVAILABLE.get(), TaskMessages.INFO_ALERT_DESCRIPTION_ADD_UNAVAILABLE.get(), String.class, false, true, false);
        PROPERTY_REMOVE_UNAVAILABLE_TYPE = new TaskProperty("ds-task-alert-remove-unavailable-type", TaskMessages.INFO_ALERT_DISPLAY_NAME_REMOVE_UNAVAILABLE.get(), TaskMessages.INFO_ALERT_DESCRIPTION_REMOVE_UNAVAILABLE.get(), String.class, false, true, false);
    }
}
