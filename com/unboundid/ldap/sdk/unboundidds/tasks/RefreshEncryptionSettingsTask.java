package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.Collections;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RefreshEncryptionSettingsTask extends Task
{
    static final String REFRESH_ENCRYPTION_SETTINGS_TASK_CLASS = "com.unboundid.directory.server.tasks.RefreshEncryptionSettingsTask";
    private static final String OC_REFRESH_ENCRYPTION_SETTINGS_TASK = "ds-task-refresh-encryption-settings";
    private static final long serialVersionUID = -2469450547006114721L;
    
    public RefreshEncryptionSettingsTask() {
        this(null, null, null, null, null, null);
    }
    
    public RefreshEncryptionSettingsTask(final String taskID) {
        this(taskID, null, null, null, null, null);
    }
    
    public RefreshEncryptionSettingsTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public RefreshEncryptionSettingsTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.RefreshEncryptionSettingsTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
    }
    
    public RefreshEncryptionSettingsTask(final Entry entry) throws TaskException {
        super(entry);
    }
    
    public RefreshEncryptionSettingsTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.RefreshEncryptionSettingsTask", properties);
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_REFRESH_ENCRYPTION_SETTINGS.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_REFRESH_ENCRYPTION_SETTINGS.get();
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-refresh-encryption-settings");
    }
}
