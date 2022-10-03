package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.text.ParseException;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.UUID;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.ldap.sdk.Entry;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class Task implements Serializable
{
    private static final String ATTR_ACTUAL_START_TIME = "ds-task-actual-start-time";
    private static final String ATTR_ALERT_ON_ERROR = "ds-task-alert-on-error";
    private static final String ATTR_ALERT_ON_START = "ds-task-alert-on-start";
    private static final String ATTR_ALERT_ON_SUCCESS = "ds-task-alert-on-success";
    private static final String ATTR_COMPLETION_TIME = "ds-task-completion-time";
    private static final String ATTR_DEPENDENCY_ID = "ds-task-dependency-id";
    private static final String ATTR_FAILED_DEPENDENCY_ACTION = "ds-task-failed-dependency-action";
    private static final String ATTR_LOG_MESSAGE = "ds-task-log-message";
    private static final String ATTR_NOTIFY_ON_COMPLETION = "ds-task-notify-on-completion";
    private static final String ATTR_NOTIFY_ON_ERROR = "ds-task-notify-on-error";
    private static final String ATTR_NOTIFY_ON_START = "ds-task-notify-on-start";
    private static final String ATTR_NOTIFY_ON_SUCCESS = "ds-task-notify-on-success";
    private static final String ATTR_SCHEDULED_START_TIME = "ds-task-scheduled-start-time";
    private static final String ATTR_TASK_CLASS = "ds-task-class-name";
    static final String ATTR_TASK_ID = "ds-task-id";
    static final String ATTR_TASK_STATE = "ds-task-state";
    static final String OC_TASK = "ds-task";
    static final String SCHEDULED_TASKS_BASE_DN = "cn=Scheduled Tasks,cn=tasks";
    private static final TaskProperty PROPERTY_TASK_ID;
    private static final TaskProperty PROPERTY_SCHEDULED_START_TIME;
    private static final TaskProperty PROPERTY_DEPENDENCY_ID;
    private static final TaskProperty PROPERTY_FAILED_DEPENDENCY_ACTION;
    private static final TaskProperty PROPERTY_NOTIFY_ON_COMPLETION;
    private static final TaskProperty PROPERTY_NOTIFY_ON_ERROR;
    private static final TaskProperty PROPERTY_NOTIFY_ON_SUCCESS;
    private static final TaskProperty PROPERTY_NOTIFY_ON_START;
    private static final TaskProperty PROPERTY_ALERT_ON_ERROR;
    private static final TaskProperty PROPERTY_ALERT_ON_START;
    private static final TaskProperty PROPERTY_ALERT_ON_SUCCESS;
    private static final long serialVersionUID = -4082350090081577623L;
    private final Boolean alertOnError;
    private final Boolean alertOnStart;
    private final Boolean alertOnSuccess;
    private final Date actualStartTime;
    private final Date completionTime;
    private final Date scheduledStartTime;
    private final Entry taskEntry;
    private final FailedDependencyAction failedDependencyAction;
    private final List<String> dependencyIDs;
    private final List<String> logMessages;
    private final List<String> notifyOnCompletion;
    private final List<String> notifyOnError;
    private final List<String> notifyOnStart;
    private final List<String> notifyOnSuccess;
    private final String taskClassName;
    private final String taskEntryDN;
    private final String taskID;
    private final TaskState taskState;
    
    protected Task() {
        this.alertOnError = null;
        this.alertOnStart = null;
        this.alertOnSuccess = null;
        this.actualStartTime = null;
        this.completionTime = null;
        this.scheduledStartTime = null;
        this.taskEntry = null;
        this.failedDependencyAction = null;
        this.dependencyIDs = null;
        this.logMessages = null;
        this.notifyOnCompletion = null;
        this.notifyOnError = null;
        this.notifyOnStart = null;
        this.notifyOnSuccess = null;
        this.taskClassName = null;
        this.taskEntryDN = null;
        this.taskID = null;
        this.taskState = null;
    }
    
    public Task(final String taskID, final String taskClassName) {
        this(taskID, taskClassName, null, null, null, null, null);
    }
    
    public Task(final String taskID, final String taskClassName, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, taskClassName, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public Task(final String taskID, final String taskClassName, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        Validator.ensureNotNull(taskClassName);
        this.taskClassName = taskClassName;
        this.scheduledStartTime = scheduledStartTime;
        this.failedDependencyAction = failedDependencyAction;
        this.alertOnStart = alertOnStart;
        this.alertOnSuccess = alertOnSuccess;
        this.alertOnError = alertOnError;
        if (taskID == null) {
            this.taskID = UUID.randomUUID().toString();
        }
        else {
            this.taskID = taskID;
        }
        if (dependencyIDs == null) {
            this.dependencyIDs = Collections.emptyList();
        }
        else {
            this.dependencyIDs = Collections.unmodifiableList((List<? extends String>)dependencyIDs);
        }
        if (notifyOnStart == null) {
            this.notifyOnStart = Collections.emptyList();
        }
        else {
            this.notifyOnStart = Collections.unmodifiableList((List<? extends String>)notifyOnStart);
        }
        if (notifyOnCompletion == null) {
            this.notifyOnCompletion = Collections.emptyList();
        }
        else {
            this.notifyOnCompletion = Collections.unmodifiableList((List<? extends String>)notifyOnCompletion);
        }
        if (notifyOnSuccess == null) {
            this.notifyOnSuccess = Collections.emptyList();
        }
        else {
            this.notifyOnSuccess = Collections.unmodifiableList((List<? extends String>)notifyOnSuccess);
        }
        if (notifyOnError == null) {
            this.notifyOnError = Collections.emptyList();
        }
        else {
            this.notifyOnError = Collections.unmodifiableList((List<? extends String>)notifyOnError);
        }
        this.taskEntry = null;
        this.taskEntryDN = "ds-task-id=" + this.taskID + ',' + "cn=Scheduled Tasks,cn=tasks";
        this.actualStartTime = null;
        this.completionTime = null;
        this.logMessages = Collections.emptyList();
        this.taskState = TaskState.UNSCHEDULED;
    }
    
    public Task(final Entry entry) throws TaskException {
        this.taskEntry = entry;
        this.taskEntryDN = entry.getDN();
        if (!entry.hasObjectClass("ds-task")) {
            throw new TaskException(TaskMessages.ERR_TASK_MISSING_OC.get(this.taskEntryDN));
        }
        this.taskID = entry.getAttributeValue("ds-task-id");
        if (this.taskID == null) {
            throw new TaskException(TaskMessages.ERR_TASK_NO_ID.get(this.taskEntryDN));
        }
        this.taskClassName = entry.getAttributeValue("ds-task-class-name");
        if (this.taskClassName == null) {
            throw new TaskException(TaskMessages.ERR_TASK_NO_CLASS.get(this.taskEntryDN));
        }
        final String stateStr = entry.getAttributeValue("ds-task-state");
        if (stateStr == null) {
            this.taskState = TaskState.UNSCHEDULED;
        }
        else {
            this.taskState = TaskState.forName(stateStr);
            if (this.taskState == null) {
                throw new TaskException(TaskMessages.ERR_TASK_INVALID_STATE.get(this.taskEntryDN, stateStr));
            }
        }
        String timestamp = entry.getAttributeValue("ds-task-scheduled-start-time");
        if (timestamp == null) {
            this.scheduledStartTime = null;
        }
        else {
            try {
                this.scheduledStartTime = StaticUtils.decodeGeneralizedTime(timestamp);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new TaskException(TaskMessages.ERR_TASK_CANNOT_PARSE_SCHEDULED_START_TIME.get(this.taskEntryDN, timestamp, pe.getMessage()), pe);
            }
        }
        timestamp = entry.getAttributeValue("ds-task-actual-start-time");
        if (timestamp == null) {
            this.actualStartTime = null;
        }
        else {
            try {
                this.actualStartTime = StaticUtils.decodeGeneralizedTime(timestamp);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new TaskException(TaskMessages.ERR_TASK_CANNOT_PARSE_ACTUAL_START_TIME.get(this.taskEntryDN, timestamp, pe.getMessage()), pe);
            }
        }
        timestamp = entry.getAttributeValue("ds-task-completion-time");
        if (timestamp == null) {
            this.completionTime = null;
        }
        else {
            try {
                this.completionTime = StaticUtils.decodeGeneralizedTime(timestamp);
            }
            catch (final ParseException pe) {
                Debug.debugException(pe);
                throw new TaskException(TaskMessages.ERR_TASK_CANNOT_PARSE_COMPLETION_TIME.get(this.taskEntryDN, timestamp, pe.getMessage()), pe);
            }
        }
        final String name = entry.getAttributeValue("ds-task-failed-dependency-action");
        if (name == null) {
            this.failedDependencyAction = null;
        }
        else {
            this.failedDependencyAction = FailedDependencyAction.forName(name);
        }
        this.dependencyIDs = parseStringList(entry, "ds-task-dependency-id");
        this.logMessages = parseStringList(entry, "ds-task-log-message");
        this.notifyOnStart = parseStringList(entry, "ds-task-notify-on-start");
        this.notifyOnCompletion = parseStringList(entry, "ds-task-notify-on-completion");
        this.notifyOnSuccess = parseStringList(entry, "ds-task-notify-on-success");
        this.notifyOnError = parseStringList(entry, "ds-task-notify-on-error");
        this.alertOnStart = entry.getAttributeValueAsBoolean("ds-task-alert-on-start");
        this.alertOnSuccess = entry.getAttributeValueAsBoolean("ds-task-alert-on-success");
        this.alertOnError = entry.getAttributeValueAsBoolean("ds-task-alert-on-error");
    }
    
    public Task(final String taskClassName, final Map<TaskProperty, List<Object>> properties) throws TaskException {
        Validator.ensureNotNull(taskClassName, properties);
        this.taskClassName = taskClassName;
        String idStr = UUID.randomUUID().toString();
        Date sst = null;
        String[] depIDs = StaticUtils.NO_STRINGS;
        FailedDependencyAction fda = FailedDependencyAction.CANCEL;
        String[] nob = StaticUtils.NO_STRINGS;
        String[] noc = StaticUtils.NO_STRINGS;
        String[] noe = StaticUtils.NO_STRINGS;
        String[] nos = StaticUtils.NO_STRINGS;
        Boolean aob = null;
        Boolean aoe = null;
        Boolean aos = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-id")) {
                idStr = parseString(p, values, idStr);
            }
            else if (attrName.equalsIgnoreCase("ds-task-scheduled-start-time")) {
                sst = parseDate(p, values, sst);
            }
            else if (attrName.equalsIgnoreCase("ds-task-dependency-id")) {
                depIDs = parseStrings(p, values, depIDs);
            }
            else if (attrName.equalsIgnoreCase("ds-task-failed-dependency-action")) {
                fda = FailedDependencyAction.forName(parseString(p, values, fda.getName()));
            }
            else if (attrName.equalsIgnoreCase("ds-task-notify-on-start")) {
                nob = parseStrings(p, values, nob);
            }
            else if (attrName.equalsIgnoreCase("ds-task-notify-on-completion")) {
                noc = parseStrings(p, values, noc);
            }
            else if (attrName.equalsIgnoreCase("ds-task-notify-on-success")) {
                nos = parseStrings(p, values, nos);
            }
            else if (attrName.equalsIgnoreCase("ds-task-notify-on-error")) {
                noe = parseStrings(p, values, noe);
            }
            else if (attrName.equalsIgnoreCase("ds-task-alert-on-start")) {
                aob = parseBoolean(p, values, aob);
            }
            else if (attrName.equalsIgnoreCase("ds-task-alert-on-success")) {
                aos = parseBoolean(p, values, aos);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-alert-on-error")) {
                    continue;
                }
                aoe = parseBoolean(p, values, aoe);
            }
        }
        this.taskID = idStr;
        this.scheduledStartTime = sst;
        this.dependencyIDs = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])depIDs));
        this.failedDependencyAction = fda;
        this.notifyOnStart = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])nob));
        this.notifyOnCompletion = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])noc));
        this.notifyOnSuccess = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])nos));
        this.notifyOnError = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])noe));
        this.alertOnStart = aob;
        this.alertOnSuccess = aos;
        this.alertOnError = aoe;
        this.taskEntry = null;
        this.taskEntryDN = "ds-task-id=" + this.taskID + ',' + "cn=Scheduled Tasks,cn=tasks";
        this.actualStartTime = null;
        this.completionTime = null;
        this.logMessages = Collections.emptyList();
        this.taskState = TaskState.UNSCHEDULED;
    }
    
    public static List<Task> getAvailableTaskTypes() {
        final List<Task> taskList = Arrays.asList(new AddSchemaFileTask(), new AlertTask(), new AuditDataSecurityTask(), new BackupTask(), new DelayTask(), new DisconnectClientTask(), new DumpDBDetailsTask(), new EnterLockdownModeTask(), new ExecTask(), new ExportTask(), new FileRetentionTask(), new GroovyScriptedTask(), new ImportTask(), new LeaveLockdownModeTask(), new RebuildTask(), new ReEncodeEntriesTask(), new RefreshEncryptionSettingsTask(), new ReloadGlobalIndexTask(), new ReloadHTTPConnectionHandlerCertificatesTask(), new RestoreTask(), new RotateLogTask(), new SearchTask(), new ShutdownTask(), new SynchronizeEncryptionSettingsTask(), new ThirdPartyTask());
        return Collections.unmodifiableList((List<? extends Task>)taskList);
    }
    
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_GENERIC.get();
    }
    
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_GENERIC.get();
    }
    
    protected final Entry getTaskEntry() {
        return this.taskEntry;
    }
    
    public final String getTaskEntryDN() {
        return this.taskEntryDN;
    }
    
    public final String getTaskID() {
        return this.taskID;
    }
    
    public final String getTaskClassName() {
        return this.taskClassName;
    }
    
    public final TaskState getState() {
        return this.taskState;
    }
    
    public final boolean isPending() {
        return this.taskState.isPending();
    }
    
    public final boolean isRunning() {
        return this.taskState.isRunning();
    }
    
    public final boolean isCompleted() {
        return this.taskState.isCompleted();
    }
    
    public final Date getScheduledStartTime() {
        return this.scheduledStartTime;
    }
    
    public final Date getActualStartTime() {
        return this.actualStartTime;
    }
    
    public final Date getCompletionTime() {
        return this.completionTime;
    }
    
    public final List<String> getDependencyIDs() {
        return this.dependencyIDs;
    }
    
    public final FailedDependencyAction getFailedDependencyAction() {
        return this.failedDependencyAction;
    }
    
    public final List<String> getLogMessages() {
        return this.logMessages;
    }
    
    public final List<String> getNotifyOnStartAddresses() {
        return this.notifyOnStart;
    }
    
    public final List<String> getNotifyOnCompletionAddresses() {
        return this.notifyOnCompletion;
    }
    
    public final List<String> getNotifyOnSuccessAddresses() {
        return this.notifyOnSuccess;
    }
    
    public final List<String> getNotifyOnErrorAddresses() {
        return this.notifyOnError;
    }
    
    public final Boolean getAlertOnStart() {
        return this.alertOnStart;
    }
    
    public final Boolean getAlertOnSuccess() {
        return this.alertOnSuccess;
    }
    
    public final Boolean getAlertOnError() {
        return this.alertOnError;
    }
    
    public final Entry createTaskEntry() {
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>(20);
        final ArrayList<String> ocValues = new ArrayList<String>(5);
        ocValues.add("top");
        ocValues.add("ds-task");
        ocValues.addAll(this.getAdditionalObjectClasses());
        attributes.add(new Attribute("objectClass", ocValues));
        attributes.add(new Attribute("ds-task-id", this.taskID));
        attributes.add(new Attribute("ds-task-class-name", this.taskClassName));
        if (this.scheduledStartTime != null) {
            attributes.add(new Attribute("ds-task-scheduled-start-time", StaticUtils.encodeGeneralizedTime(this.scheduledStartTime)));
        }
        if (!this.dependencyIDs.isEmpty()) {
            attributes.add(new Attribute("ds-task-dependency-id", this.dependencyIDs));
        }
        if (this.failedDependencyAction != null) {
            attributes.add(new Attribute("ds-task-failed-dependency-action", this.failedDependencyAction.getName()));
        }
        if (!this.notifyOnStart.isEmpty()) {
            attributes.add(new Attribute("ds-task-notify-on-start", this.notifyOnStart));
        }
        if (!this.notifyOnCompletion.isEmpty()) {
            attributes.add(new Attribute("ds-task-notify-on-completion", this.notifyOnCompletion));
        }
        if (!this.notifyOnSuccess.isEmpty()) {
            attributes.add(new Attribute("ds-task-notify-on-success", this.notifyOnSuccess));
        }
        if (!this.notifyOnError.isEmpty()) {
            attributes.add(new Attribute("ds-task-notify-on-error", this.notifyOnError));
        }
        if (this.alertOnStart != null) {
            attributes.add(new Attribute("ds-task-alert-on-start", String.valueOf(this.alertOnStart)));
        }
        if (this.alertOnSuccess != null) {
            attributes.add(new Attribute("ds-task-alert-on-success", String.valueOf(this.alertOnSuccess)));
        }
        if (this.alertOnError != null) {
            attributes.add(new Attribute("ds-task-alert-on-error", String.valueOf(this.alertOnError)));
        }
        attributes.addAll(this.getAdditionalAttributes());
        return new Entry(this.taskEntryDN, attributes);
    }
    
    protected static boolean parseBooleanValue(final Entry taskEntry, final String attributeName, final boolean defaultValue) throws TaskException {
        final String valueString = taskEntry.getAttributeValue(attributeName);
        if (valueString == null) {
            return defaultValue;
        }
        if (valueString.equalsIgnoreCase("true")) {
            return true;
        }
        if (valueString.equalsIgnoreCase("false")) {
            return false;
        }
        throw new TaskException(TaskMessages.ERR_TASK_CANNOT_PARSE_BOOLEAN.get(taskEntry.getDN(), valueString, attributeName));
    }
    
    protected static List<String> parseStringList(final Entry taskEntry, final String attributeName) {
        final String[] valueStrings = taskEntry.getAttributeValues(attributeName);
        if (valueStrings == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])valueStrings));
    }
    
    protected static Boolean parseBoolean(final TaskProperty p, final List<Object> values, final Boolean defaultValue) throws TaskException {
        if (values.isEmpty()) {
            if (p.isRequired()) {
                throw new TaskException(TaskMessages.ERR_TASK_REQUIRED_PROPERTY_WITHOUT_VALUES.get(p.getDisplayName()));
            }
            return defaultValue;
        }
        else {
            if (values.size() > 1) {
                throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_NOT_MULTIVALUED.get(p.getDisplayName()));
            }
            final Object o = values.get(0);
            Boolean booleanValue;
            if (o instanceof Boolean) {
                booleanValue = (Boolean)o;
            }
            else {
                if (!(o instanceof String)) {
                    throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_BOOLEAN.get(p.getDisplayName()));
                }
                final String valueStr = (String)o;
                if (valueStr.equalsIgnoreCase("true")) {
                    booleanValue = Boolean.TRUE;
                }
                else {
                    if (!valueStr.equalsIgnoreCase("false")) {
                        throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_BOOLEAN.get(p.getDisplayName()));
                    }
                    booleanValue = Boolean.FALSE;
                }
            }
            return booleanValue;
        }
    }
    
    protected static Date parseDate(final TaskProperty p, final List<Object> values, final Date defaultValue) throws TaskException {
        if (values.isEmpty()) {
            if (p.isRequired()) {
                throw new TaskException(TaskMessages.ERR_TASK_REQUIRED_PROPERTY_WITHOUT_VALUES.get(p.getDisplayName()));
            }
            return defaultValue;
        }
        else {
            if (values.size() > 1) {
                throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_NOT_MULTIVALUED.get(p.getDisplayName()));
            }
            final Object o = values.get(0);
            Date dateValue = null;
            Label_0178: {
                if (!(o instanceof Date)) {
                    if (o instanceof String) {
                        try {
                            dateValue = StaticUtils.decodeGeneralizedTime((String)o);
                            break Label_0178;
                        }
                        catch (final ParseException pe) {
                            throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_DATE.get(p.getDisplayName()), pe);
                        }
                    }
                    throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_DATE.get(p.getDisplayName()));
                }
                dateValue = (Date)o;
            }
            final Object[] allowedValues = p.getAllowedValues();
            if (allowedValues != null) {
                boolean found = false;
                for (final Object allowedValue : allowedValues) {
                    if (dateValue.equals(allowedValue)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_ALLOWED.get(p.getDisplayName(), dateValue.toString()));
                }
            }
            return dateValue;
        }
    }
    
    protected static Long parseLong(final TaskProperty p, final List<Object> values, final Long defaultValue) throws TaskException {
        if (values.isEmpty()) {
            if (p.isRequired()) {
                throw new TaskException(TaskMessages.ERR_TASK_REQUIRED_PROPERTY_WITHOUT_VALUES.get(p.getDisplayName()));
            }
            return defaultValue;
        }
        else {
            if (values.size() > 1) {
                throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_NOT_MULTIVALUED.get(p.getDisplayName()));
            }
            final Object o = values.get(0);
            Long longValue = null;
            Label_0204: {
                if (o instanceof Long) {
                    longValue = (Long)o;
                }
                else {
                    if (!(o instanceof Number)) {
                        if (o instanceof String) {
                            try {
                                longValue = Long.parseLong((String)o);
                                break Label_0204;
                            }
                            catch (final Exception e) {
                                throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_LONG.get(p.getDisplayName()), e);
                            }
                        }
                        throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_LONG.get(p.getDisplayName()));
                    }
                    longValue = ((Number)o).longValue();
                }
            }
            final Object[] allowedValues = p.getAllowedValues();
            if (allowedValues != null) {
                boolean found = false;
                for (final Object allowedValue : allowedValues) {
                    if (longValue.equals(allowedValue)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_ALLOWED.get(p.getDisplayName(), longValue.toString()));
                }
            }
            return longValue;
        }
    }
    
    protected static String parseString(final TaskProperty p, final List<Object> values, final String defaultValue) throws TaskException {
        if (values.isEmpty()) {
            if (p.isRequired()) {
                throw new TaskException(TaskMessages.ERR_TASK_REQUIRED_PROPERTY_WITHOUT_VALUES.get(p.getDisplayName()));
            }
            return defaultValue;
        }
        else {
            if (values.size() > 1) {
                throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_NOT_MULTIVALUED.get(p.getDisplayName()));
            }
            final Object o = values.get(0);
            String valueStr;
            if (o instanceof String) {
                valueStr = (String)o;
            }
            else {
                if (!(values.get(0) instanceof CharSequence)) {
                    throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_STRING.get(p.getDisplayName()));
                }
                valueStr = o.toString();
            }
            final Object[] allowedValues = p.getAllowedValues();
            if (allowedValues != null) {
                boolean found = false;
                for (final Object allowedValue : allowedValues) {
                    final String s = (String)allowedValue;
                    if (valueStr.equalsIgnoreCase(s)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_ALLOWED.get(p.getDisplayName(), valueStr));
                }
            }
            return valueStr;
        }
    }
    
    protected static String[] parseStrings(final TaskProperty p, final List<Object> values, final String[] defaultValues) throws TaskException {
        if (!values.isEmpty()) {
            final String[] stringValues = new String[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                final Object o = values.get(i);
                String valueStr;
                if (o instanceof String) {
                    valueStr = (String)o;
                }
                else {
                    if (!(o instanceof CharSequence)) {
                        throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_STRING.get(p.getDisplayName()));
                    }
                    valueStr = o.toString();
                }
                final Object[] allowedValues = p.getAllowedValues();
                if (allowedValues != null) {
                    boolean found = false;
                    for (final Object allowedValue : allowedValues) {
                        final String s = (String)allowedValue;
                        if (valueStr.equalsIgnoreCase(s)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new TaskException(TaskMessages.ERR_TASK_PROPERTY_VALUE_NOT_ALLOWED.get(p.getDisplayName(), valueStr));
                    }
                }
                stringValues[i] = valueStr;
            }
            return stringValues;
        }
        if (p.isRequired()) {
            throw new TaskException(TaskMessages.ERR_TASK_REQUIRED_PROPERTY_WITHOUT_VALUES.get(p.getDisplayName()));
        }
        return defaultValues;
    }
    
    protected List<String> getAdditionalObjectClasses() {
        return Collections.emptyList();
    }
    
    protected List<Attribute> getAdditionalAttributes() {
        return Collections.emptyList();
    }
    
    public static Task decodeTask(final Entry entry) throws TaskException {
        final String taskClass = entry.getAttributeValue("ds-task-class-name");
        if (taskClass == null) {
            throw new TaskException(TaskMessages.ERR_TASK_NO_CLASS.get(entry.getDN()));
        }
        try {
            if (taskClass.equals("com.unboundid.directory.server.tasks.AddSchemaFileTask")) {
                return new AddSchemaFileTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.AlertTask")) {
                return new AlertTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.AuditDataSecurityTask")) {
                return new AuditDataSecurityTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.BackupTask")) {
                return new BackupTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.DelayTask")) {
                return new DelayTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.DisconnectClientTask")) {
                return new DisconnectClientTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.DumpDBDetailsTask")) {
                return new DumpDBDetailsTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.EnterLockdownModeTask")) {
                return new EnterLockdownModeTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.ExecTask")) {
                return new ExecTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.ExportTask")) {
                return new ExportTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.FileRetentionTask")) {
                return new FileRetentionTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.sdk.extensions.GroovyScriptedTask")) {
                return new GroovyScriptedTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.ImportTask")) {
                return new ImportTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.LeaveLockdownModeTask")) {
                return new LeaveLockdownModeTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.RebuildTask")) {
                return new RebuildTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.ReEncodeEntriesTask")) {
                return new ReEncodeEntriesTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.RefreshEncryptionSettingsTask")) {
                return new RefreshEncryptionSettingsTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.proxy.tasks.ReloadTask")) {
                return new ReloadGlobalIndexTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.ReloadHTTPConnectionHandlerCertificatesTask")) {
                return new ReloadHTTPConnectionHandlerCertificatesTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.RestoreTask")) {
                return new RestoreTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.RotateLogTask")) {
                return new RotateLogTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.SearchTask")) {
                return new SearchTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.tasks.ShutdownTask")) {
                return new ShutdownTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.server.crypto.SynchronizeEncryptionSettingsTask")) {
                return new SynchronizeEncryptionSettingsTask(entry);
            }
            if (taskClass.equals("com.unboundid.directory.sdk.extensions.ThirdPartyTask")) {
                return new ThirdPartyTask(entry);
            }
        }
        catch (final TaskException te) {
            Debug.debugException(te);
        }
        return new Task(entry);
    }
    
    public static List<TaskProperty> getCommonTaskProperties() {
        final List<TaskProperty> taskList = Arrays.asList(Task.PROPERTY_TASK_ID, Task.PROPERTY_SCHEDULED_START_TIME, Task.PROPERTY_DEPENDENCY_ID, Task.PROPERTY_FAILED_DEPENDENCY_ACTION, Task.PROPERTY_NOTIFY_ON_START, Task.PROPERTY_NOTIFY_ON_COMPLETION, Task.PROPERTY_NOTIFY_ON_SUCCESS, Task.PROPERTY_NOTIFY_ON_ERROR, Task.PROPERTY_ALERT_ON_START, Task.PROPERTY_ALERT_ON_SUCCESS, Task.PROPERTY_ALERT_ON_ERROR);
        return Collections.unmodifiableList((List<? extends TaskProperty>)taskList);
    }
    
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.emptyList();
    }
    
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(20));
        props.put(Task.PROPERTY_TASK_ID, (List<Object>)Collections.singletonList(this.taskID));
        if (this.scheduledStartTime == null) {
            props.put(Task.PROPERTY_SCHEDULED_START_TIME, Collections.emptyList());
        }
        else {
            props.put(Task.PROPERTY_SCHEDULED_START_TIME, (List<Object>)Collections.singletonList(this.scheduledStartTime));
        }
        props.put(Task.PROPERTY_DEPENDENCY_ID, Collections.unmodifiableList((List<?>)this.dependencyIDs));
        if (this.failedDependencyAction == null) {
            props.put(Task.PROPERTY_FAILED_DEPENDENCY_ACTION, Collections.emptyList());
        }
        else {
            props.put(Task.PROPERTY_FAILED_DEPENDENCY_ACTION, (List<Object>)Collections.singletonList(this.failedDependencyAction.getName()));
        }
        props.put(Task.PROPERTY_NOTIFY_ON_START, Collections.unmodifiableList((List<?>)this.notifyOnStart));
        props.put(Task.PROPERTY_NOTIFY_ON_COMPLETION, Collections.unmodifiableList((List<?>)this.notifyOnCompletion));
        props.put(Task.PROPERTY_NOTIFY_ON_SUCCESS, Collections.unmodifiableList((List<?>)this.notifyOnSuccess));
        props.put(Task.PROPERTY_NOTIFY_ON_ERROR, Collections.unmodifiableList((List<?>)this.notifyOnError));
        if (this.alertOnStart != null) {
            props.put(Task.PROPERTY_ALERT_ON_START, (List<Object>)Collections.singletonList(this.alertOnStart));
        }
        if (this.alertOnSuccess != null) {
            props.put(Task.PROPERTY_ALERT_ON_SUCCESS, (List<Object>)Collections.singletonList(this.alertOnSuccess));
        }
        if (this.alertOnError != null) {
            props.put(Task.PROPERTY_ALERT_ON_ERROR, (List<Object>)Collections.singletonList(this.alertOnError));
        }
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public final void toString(final StringBuilder buffer) {
        buffer.append("Task(name='");
        buffer.append(this.getTaskName());
        buffer.append("', className='");
        buffer.append(this.taskClassName);
        buffer.append(", properties={");
        boolean added = false;
        for (final Map.Entry<TaskProperty, List<Object>> e : this.getTaskPropertyValues().entrySet()) {
            if (added) {
                buffer.append(", ");
            }
            else {
                added = true;
            }
            buffer.append(e.getKey().getAttributeName());
            buffer.append("={");
            final Iterator<Object> iterator = e.getValue().iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(String.valueOf(iterator.next()));
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        buffer.append("})");
    }
    
    static {
        PROPERTY_TASK_ID = new TaskProperty("ds-task-id", TaskMessages.INFO_DISPLAY_NAME_TASK_ID.get(), TaskMessages.INFO_DESCRIPTION_TASK_ID.get(), String.class, false, false, true);
        PROPERTY_SCHEDULED_START_TIME = new TaskProperty("ds-task-scheduled-start-time", TaskMessages.INFO_DISPLAY_NAME_SCHEDULED_START_TIME.get(), TaskMessages.INFO_DESCRIPTION_SCHEDULED_START_TIME.get(), Date.class, false, false, true);
        PROPERTY_DEPENDENCY_ID = new TaskProperty("ds-task-dependency-id", TaskMessages.INFO_DISPLAY_NAME_DEPENDENCY_ID.get(), TaskMessages.INFO_DESCRIPTION_DEPENDENCY_ID.get(), String.class, false, true, true);
        PROPERTY_FAILED_DEPENDENCY_ACTION = new TaskProperty("ds-task-failed-dependency-action", TaskMessages.INFO_DISPLAY_NAME_FAILED_DEPENDENCY_ACTION.get(), TaskMessages.INFO_DESCRIPTION_FAILED_DEPENDENCY_ACTION.get(), String.class, false, false, true, new String[] { FailedDependencyAction.CANCEL.getName(), FailedDependencyAction.DISABLE.getName(), FailedDependencyAction.PROCESS.getName() });
        PROPERTY_NOTIFY_ON_COMPLETION = new TaskProperty("ds-task-notify-on-completion", TaskMessages.INFO_DISPLAY_NAME_NOTIFY_ON_COMPLETION.get(), TaskMessages.INFO_DESCRIPTION_NOTIFY_ON_COMPLETION.get(), String.class, false, true, true);
        PROPERTY_NOTIFY_ON_ERROR = new TaskProperty("ds-task-notify-on-error", TaskMessages.INFO_DISPLAY_NAME_NOTIFY_ON_ERROR.get(), TaskMessages.INFO_DESCRIPTION_NOTIFY_ON_ERROR.get(), String.class, false, true, true);
        PROPERTY_NOTIFY_ON_SUCCESS = new TaskProperty("ds-task-notify-on-success", TaskMessages.INFO_DISPLAY_NAME_NOTIFY_ON_SUCCESS.get(), TaskMessages.INFO_DESCRIPTION_NOTIFY_ON_SUCCESS.get(), String.class, false, true, true);
        PROPERTY_NOTIFY_ON_START = new TaskProperty("ds-task-notify-on-start", TaskMessages.INFO_DISPLAY_NAME_NOTIFY_ON_START.get(), TaskMessages.INFO_DESCRIPTION_NOTIFY_ON_START.get(), String.class, false, true, true);
        PROPERTY_ALERT_ON_ERROR = new TaskProperty("ds-task-alert-on-error", TaskMessages.INFO_DISPLAY_NAME_ALERT_ON_ERROR.get(), TaskMessages.INFO_DESCRIPTION_ALERT_ON_ERROR.get(), Boolean.class, false, false, true);
        PROPERTY_ALERT_ON_START = new TaskProperty("ds-task-alert-on-start", TaskMessages.INFO_DISPLAY_NAME_ALERT_ON_START.get(), TaskMessages.INFO_DESCRIPTION_ALERT_ON_START.get(), Boolean.class, false, false, true);
        PROPERTY_ALERT_ON_SUCCESS = new TaskProperty("ds-task-alert-on-success", TaskMessages.INFO_DISPLAY_NAME_ALERT_ON_SUCCESS.get(), TaskMessages.INFO_DESCRIPTION_ALERT_ON_SUCCESS.get(), Boolean.class, false, false, true);
    }
}
