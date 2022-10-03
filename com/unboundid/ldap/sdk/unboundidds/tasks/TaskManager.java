package com.unboundid.ldap.sdk.unboundidds.tasks;

import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import java.util.Iterator;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import java.util.List;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TaskManager
{
    private TaskManager() {
    }
    
    private static String getTaskDN(final String taskID) {
        return "ds-task-id=" + taskID + ',' + "cn=Scheduled Tasks,cn=tasks";
    }
    
    public static Task getTask(final String taskID, final LDAPConnection connection) throws LDAPException, TaskException {
        try {
            final Entry taskEntry = connection.getEntry(getTaskDN(taskID));
            if (taskEntry == null) {
                return null;
            }
            return Task.decodeTask(taskEntry);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                return null;
            }
            throw le;
        }
    }
    
    public static List<Task> getTasks(final LDAPConnection connection) throws LDAPException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-task");
        final SearchResult result = connection.search("cn=Scheduled Tasks,cn=tasks", SearchScope.SUB, filter, new String[0]);
        final LinkedList<Task> tasks = new LinkedList<Task>();
        for (final SearchResultEntry e : result.getSearchEntries()) {
            try {
                tasks.add(Task.decodeTask(e));
            }
            catch (final TaskException te) {
                Debug.debugException(te);
            }
        }
        return tasks;
    }
    
    public static Task scheduleTask(final Task task, final LDAPConnection connection) throws LDAPException, TaskException {
        final Entry taskEntry = task.createTaskEntry();
        connection.add(task.createTaskEntry());
        final Entry newTaskEntry = connection.getEntry(taskEntry.getDN());
        if (newTaskEntry == null) {
            throw new LDAPException(ResultCode.NO_SUCH_OBJECT);
        }
        return Task.decodeTask(newTaskEntry);
    }
    
    public static void cancelTask(final String taskID, final LDAPConnection connection) throws LDAPException {
        final Modification mod = new Modification(ModificationType.REPLACE, "ds-task-state", TaskState.CANCELED_BEFORE_STARTING.getName());
        connection.modify(getTaskDN(taskID), mod);
    }
    
    public static void deleteTask(final String taskID, final LDAPConnection connection) throws LDAPException {
        connection.delete(getTaskDN(taskID));
    }
    
    public static Task waitForTask(final String taskID, final LDAPConnection connection, final long pollFrequency, final long maxWaitTime) throws LDAPException, TaskException {
        long stopWaitingTime;
        if (maxWaitTime > 0L) {
            stopWaitingTime = System.currentTimeMillis() + maxWaitTime;
        }
        else {
            stopWaitingTime = Long.MAX_VALUE;
        }
        while (true) {
            final Task t = getTask(taskID, connection);
            if (t == null) {
                throw new TaskException(TaskMessages.ERR_TASK_MANAGER_WAIT_NO_SUCH_TASK.get(taskID));
            }
            if (t.isCompleted()) {
                return t;
            }
            final long timeRemaining = stopWaitingTime - System.currentTimeMillis();
            if (timeRemaining <= 0L) {
                return t;
            }
            try {
                Thread.sleep(Math.min(pollFrequency, timeRemaining));
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                Thread.currentThread().interrupt();
                throw new TaskException(TaskMessages.ERR_TASK_MANAGER_WAIT_INTERRUPTED.get(taskID), ie);
            }
        }
    }
}
