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
public final class SynchronizeEncryptionSettingsTask extends Task
{
    static final String SYNCHRONIZE_ENCRYPTION_SETTINGS_TASK_CLASS = "com.unboundid.directory.server.crypto.SynchronizeEncryptionSettingsTask";
    private static final String OC_SYNCHRONIZE_ENCRYPTION_SETTINGS_TASK = "ds-task-synchronize-encryption-settings";
    private static final long serialVersionUID = 5176601759135180183L;
    
    public SynchronizeEncryptionSettingsTask() {
        this(null, null, null, null, null, null);
    }
    
    public SynchronizeEncryptionSettingsTask(final String taskID) {
        this(taskID, null, null, null, null, null);
    }
    
    public SynchronizeEncryptionSettingsTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public SynchronizeEncryptionSettingsTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.crypto.SynchronizeEncryptionSettingsTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
    }
    
    public SynchronizeEncryptionSettingsTask(final Entry entry) throws TaskException {
        super(entry);
    }
    
    public SynchronizeEncryptionSettingsTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.crypto.SynchronizeEncryptionSettingsTask", properties);
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_SYNCHRONIZE_ENCRYPTION_SETTINGS.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_SYNCHRONIZE_ENCRYPTION_SETTINGS.get();
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-synchronize-encryption-settings");
    }
}
