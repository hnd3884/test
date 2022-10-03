package com.unboundid.ldap.sdk.unboundidds.tasks;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum TaskState
{
    CANCELED_BEFORE_STARTING("canceled_before_starting"), 
    COMPLETED_SUCCESSFULLY("completed_successfully"), 
    COMPLETED_WITH_ERRORS("completed_with_errors"), 
    DISABLED("disabled"), 
    RUNNING("running"), 
    STOPPED_BY_ADMINISTRATOR("stopped_by_administrator"), 
    STOPPED_BY_ERROR("stopped_by_error"), 
    STOPPED_BY_SHUTDOWN("stopped_by_shutdown"), 
    UNSCHEDULED("unscheduled"), 
    WAITING_ON_DEPENDENCY("waiting_on_dependency"), 
    WAITING_ON_START_TIME("waiting_on_start_time");
    
    private final String name;
    
    private TaskState(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static TaskState forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "canceledbeforestarting":
            case "canceled-before-starting":
            case "canceled_before_starting": {
                return TaskState.CANCELED_BEFORE_STARTING;
            }
            case "completedsuccessfully":
            case "completed-successfully":
            case "completed_successfully": {
                return TaskState.COMPLETED_SUCCESSFULLY;
            }
            case "completedwitherrors":
            case "completed-with-errors":
            case "completed_with_errors": {
                return TaskState.COMPLETED_WITH_ERRORS;
            }
            case "disabled": {
                return TaskState.DISABLED;
            }
            case "running": {
                return TaskState.RUNNING;
            }
            case "stoppedbyadministrator":
            case "stopped-by-administrator":
            case "stopped_by_administrator": {
                return TaskState.STOPPED_BY_ADMINISTRATOR;
            }
            case "stoppedbyerror":
            case "stopped-by-error":
            case "stopped_by_error": {
                return TaskState.STOPPED_BY_ERROR;
            }
            case "stoppedbyshutdown":
            case "stopped-by-shutdown":
            case "stopped_by_shutdown": {
                return TaskState.STOPPED_BY_SHUTDOWN;
            }
            case "unscheduled": {
                return TaskState.UNSCHEDULED;
            }
            case "waitingondependency":
            case "waiting-on-dependency":
            case "waiting_on_dependency": {
                return TaskState.WAITING_ON_DEPENDENCY;
            }
            case "waitingonstarttime":
            case "waiting-on-start-time":
            case "waiting_on_start_time": {
                return TaskState.WAITING_ON_START_TIME;
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean isPending() {
        switch (this) {
            case DISABLED:
            case UNSCHEDULED:
            case WAITING_ON_DEPENDENCY:
            case WAITING_ON_START_TIME: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isRunning() {
        return this == TaskState.RUNNING;
    }
    
    public boolean isCompleted() {
        return !this.isPending() && !this.isRunning();
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
