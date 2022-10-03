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
public final class ReloadHTTPConnectionHandlerCertificatesTask extends Task
{
    static final String RELOAD_HTTP_CONNECTION_HANDLER_CERTIFICATES_TASK_CLASS = "com.unboundid.directory.server.tasks.ReloadHTTPConnectionHandlerCertificatesTask";
    private static final String OC_RELOAD_HTTP_CONNECTION_HANDLER_CERTIFICATES_TASK = "ds-task-reload-http-connection-handler-certificates";
    private static final long serialVersionUID = 842594962305532389L;
    
    public ReloadHTTPConnectionHandlerCertificatesTask() {
        this(null, null, null, null, null, null);
    }
    
    public ReloadHTTPConnectionHandlerCertificatesTask(final String taskID) {
        this(taskID, null, null, null, null, null);
    }
    
    public ReloadHTTPConnectionHandlerCertificatesTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ReloadHTTPConnectionHandlerCertificatesTask(final String taskID, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.ReloadHTTPConnectionHandlerCertificatesTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
    }
    
    public ReloadHTTPConnectionHandlerCertificatesTask(final Entry entry) throws TaskException {
        super(entry);
    }
    
    public ReloadHTTPConnectionHandlerCertificatesTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.ReloadHTTPConnectionHandlerCertificatesTask", properties);
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_RELOAD_HTTP_CONNECTION_HANDLER_CERTIFICATES.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_RELOAD_HTTP_CONNECTION_HANDLER_CERTIFICATES.get();
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-reload-http-connection-handler-certificates");
    }
}
