package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.DurationArgument;
import java.util.concurrent.TimeUnit;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Collection;
import com.unboundid.ldap.sdk.LDAPURL;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DelayTask extends Task
{
    static final String DELAY_TASK_CLASS = "com.unboundid.directory.server.tasks.DelayTask";
    private static final String ATTR_SLEEP_DURATION = "ds-task-delay-sleep-duration";
    private static final String ATTR_WAIT_FOR_WORK_QUEUE_IDLE = "ds-task-delay-duration-to-wait-for-work-queue-idle";
    private static final String ATTR_SEARCH_URL = "ds-task-delay-ldap-url-for-search-expected-to-return-entries";
    private static final String ATTR_SEARCH_INTERVAL = "ds-task-delay-search-interval";
    private static final String ATTR_SEARCH_TIME_LIMIT = "ds-task-delay-search-time-limit";
    private static final String ATTR_SEARCH_DURATION = "ds-task-delay-duration-to-wait-for-search-to-return-entries";
    private static final String ATTR_TIMEOUT_RETURN_STATE = "ds-task-delay-task-return-state-if-timeout-is-encountered";
    private static final String OC_DELAY_TASK = "ds-task-delay";
    private static final TaskProperty PROPERTY_SLEEP_DURATION_MILLIS;
    private static final TaskProperty PROPERTY_WAIT_FOR_WORK_QUEUE_IDLE_MILLIS;
    private static final TaskProperty PROPERTY_SEARCH_URL;
    private static final TaskProperty PROPERTY_SEARCH_INTERVAL_MILLIS;
    private static final TaskProperty PROPERTY_SEARCH_TIME_LIMIT_MILLIS;
    private static final TaskProperty PROPERTY_SEARCH_DURATION_MILLIS;
    private static final TaskProperty PROPERTY_TIMEOUT_RETURN_STATE;
    private static final long serialVersionUID = -639870096358259180L;
    private final List<LDAPURL> ldapURLsForSearchesExpectedToReturnEntries;
    private final Long millisBetweenSearches;
    private final Long millisToWaitForWorkQueueToBecomeIdle;
    private final Long searchTimeLimitMillis;
    private final Long sleepDurationMillis;
    private final Long totalDurationMillisForEachLDAPURL;
    private final String taskStateIfTimeoutIsEncountered;
    
    public DelayTask() {
        this.ldapURLsForSearchesExpectedToReturnEntries = null;
        this.millisBetweenSearches = null;
        this.millisToWaitForWorkQueueToBecomeIdle = null;
        this.searchTimeLimitMillis = null;
        this.sleepDurationMillis = null;
        this.totalDurationMillisForEachLDAPURL = null;
        this.taskStateIfTimeoutIsEncountered = null;
    }
    
    public DelayTask(final Long sleepDurationMillis, final Long millisToWaitForWorkQueueToBecomeIdle, final Collection<LDAPURL> ldapURLsForSearchesExpectedToReturnEntries, final Long millisBetweenSearches, final Long searchTimeLimitMillis, final Long totalDurationMillisForEachLDAPURL, final TaskState taskStateIfTimeoutIsEncountered) throws TaskException {
        this(null, sleepDurationMillis, millisToWaitForWorkQueueToBecomeIdle, ldapURLsForSearchesExpectedToReturnEntries, millisBetweenSearches, searchTimeLimitMillis, totalDurationMillisForEachLDAPURL, taskStateIfTimeoutIsEncountered, null, null, null, null, null, null, null, null, null, null);
    }
    
    public DelayTask(final String taskID, final Long sleepDurationMillis, final Long millisToWaitForWorkQueueToBecomeIdle, final Collection<LDAPURL> ldapURLsForSearchesExpectedToReturnEntries, final Long millisBetweenSearches, final Long searchTimeLimitMillis, final Long totalDurationMillisForEachLDAPURL, final TaskState taskStateIfTimeoutIsEncountered, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) throws TaskException {
        super(taskID, "com.unboundid.directory.server.tasks.DelayTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.sleepDurationMillis = sleepDurationMillis;
        this.millisToWaitForWorkQueueToBecomeIdle = millisToWaitForWorkQueueToBecomeIdle;
        this.millisBetweenSearches = millisBetweenSearches;
        this.searchTimeLimitMillis = searchTimeLimitMillis;
        this.totalDurationMillisForEachLDAPURL = totalDurationMillisForEachLDAPURL;
        if (ldapURLsForSearchesExpectedToReturnEntries == null) {
            this.ldapURLsForSearchesExpectedToReturnEntries = Collections.emptyList();
        }
        else {
            this.ldapURLsForSearchesExpectedToReturnEntries = Collections.unmodifiableList((List<? extends LDAPURL>)new ArrayList<LDAPURL>(ldapURLsForSearchesExpectedToReturnEntries));
        }
        if (taskStateIfTimeoutIsEncountered == null) {
            this.taskStateIfTimeoutIsEncountered = null;
        }
        else {
            switch (taskStateIfTimeoutIsEncountered) {
                case STOPPED_BY_ERROR:
                case COMPLETED_WITH_ERRORS:
                case COMPLETED_SUCCESSFULLY: {
                    this.taskStateIfTimeoutIsEncountered = taskStateIfTimeoutIsEncountered.name();
                    break;
                }
                default: {
                    throw new TaskException(TaskMessages.ERR_DELAY_INVALID_TIMEOUT_STATE.get(TaskState.STOPPED_BY_ERROR.name(), TaskState.COMPLETED_WITH_ERRORS.name(), TaskState.COMPLETED_SUCCESSFULLY.name()));
                }
            }
        }
        if (sleepDurationMillis != null && sleepDurationMillis <= 0L) {
            throw new TaskException(TaskMessages.ERR_DELAY_INVALID_SLEEP_DURATION.get());
        }
        if (millisToWaitForWorkQueueToBecomeIdle != null && millisToWaitForWorkQueueToBecomeIdle <= 0L) {
            throw new TaskException(TaskMessages.ERR_DELAY_INVALID_WAIT_FOR_QUEUE_IDLE.get());
        }
        if (millisBetweenSearches != null && millisBetweenSearches <= 0L) {
            throw new TaskException(TaskMessages.ERR_DELAY_INVALID_SEARCH_INTERVAL.get());
        }
        if (searchTimeLimitMillis != null && searchTimeLimitMillis <= 0L) {
            throw new TaskException(TaskMessages.ERR_DELAY_INVALID_SEARCH_TIME_LIMIT.get());
        }
        if (totalDurationMillisForEachLDAPURL != null && totalDurationMillisForEachLDAPURL <= 0L) {
            throw new TaskException(TaskMessages.ERR_DELAY_INVALID_SEARCH_DURATION.get());
        }
        if (!this.ldapURLsForSearchesExpectedToReturnEntries.isEmpty()) {
            if (millisBetweenSearches == null || searchTimeLimitMillis == null || totalDurationMillisForEachLDAPURL == null) {
                throw new TaskException(TaskMessages.ERR_DELAY_URL_WITHOUT_REQUIRED_PARAM.get());
            }
            if (millisBetweenSearches >= totalDurationMillisForEachLDAPURL) {
                throw new TaskException(TaskMessages.ERR_DELAY_INVALID_SEARCH_INTERVAL.get());
            }
            if (searchTimeLimitMillis >= totalDurationMillisForEachLDAPURL) {
                throw new TaskException(TaskMessages.ERR_DELAY_INVALID_SEARCH_TIME_LIMIT.get());
            }
        }
    }
    
    public DelayTask(final Entry entry) throws TaskException {
        super(entry);
        this.taskStateIfTimeoutIsEncountered = entry.getAttributeValue("ds-task-delay-task-return-state-if-timeout-is-encountered");
        this.sleepDurationMillis = parseDuration(entry, "ds-task-delay-sleep-duration");
        this.millisToWaitForWorkQueueToBecomeIdle = parseDuration(entry, "ds-task-delay-duration-to-wait-for-work-queue-idle");
        this.millisBetweenSearches = parseDuration(entry, "ds-task-delay-search-interval");
        this.searchTimeLimitMillis = parseDuration(entry, "ds-task-delay-search-time-limit");
        this.totalDurationMillisForEachLDAPURL = parseDuration(entry, "ds-task-delay-duration-to-wait-for-search-to-return-entries");
        final String[] urlStrings = entry.getAttributeValues("ds-task-delay-ldap-url-for-search-expected-to-return-entries");
        if (urlStrings == null) {
            this.ldapURLsForSearchesExpectedToReturnEntries = Collections.emptyList();
        }
        else {
            final ArrayList<LDAPURL> urls = new ArrayList<LDAPURL>(urlStrings.length);
            for (final String s : urlStrings) {
                try {
                    urls.add(new LDAPURL(s));
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    throw new TaskException(TaskMessages.ERR_DELAY_ENTRY_MALFORMED_URL.get("ds-task-delay-ldap-url-for-search-expected-to-return-entries", s, e.getMessage()), e);
                }
            }
            this.ldapURLsForSearchesExpectedToReturnEntries = Collections.unmodifiableList((List<? extends LDAPURL>)urls);
        }
    }
    
    private static Long parseDuration(final Entry entry, final String attributeName) throws TaskException {
        final String value = entry.getAttributeValue(attributeName);
        if (value == null) {
            return null;
        }
        try {
            return DurationArgument.parseDuration(value, TimeUnit.MILLISECONDS);
        }
        catch (final ArgumentException e) {
            throw new TaskException(TaskMessages.ERR_DELAY_CANNOT_PARSE_ATTR_VALUE_AS_DURATION.get(attributeName, e.getMessage()), e);
        }
    }
    
    public DelayTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.DelayTask", properties);
        Long searchDuration = null;
        Long searchInterval = null;
        Long searchTimeLimit = null;
        Long sleepDuration = null;
        Long workQueueWaitTime = null;
        String timeoutReturnState = null;
        final List<LDAPURL> urls = new ArrayList<LDAPURL>(10);
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = StaticUtils.toLowerCase(p.getAttributeName());
            final List<Object> values = entry.getValue();
            final String s = attrName;
            switch (s) {
                case "ds-task-delay-sleep-duration": {
                    sleepDuration = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-delay-duration-to-wait-for-work-queue-idle": {
                    workQueueWaitTime = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-delay-ldap-url-for-search-expected-to-return-entries": {
                    for (final String urlString : Task.parseStrings(p, values, StaticUtils.NO_STRINGS)) {
                        try {
                            urls.add(new LDAPURL(urlString));
                        }
                        catch (final LDAPException e) {
                            Debug.debugException(e);
                            throw new TaskException(TaskMessages.ERR_DELAY_ENTRY_MALFORMED_URL.get("ds-task-delay-ldap-url-for-search-expected-to-return-entries", urlString, e.getMessage()), e);
                        }
                    }
                    continue;
                }
                case "ds-task-delay-search-interval": {
                    searchInterval = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-delay-search-time-limit": {
                    searchTimeLimit = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-delay-duration-to-wait-for-search-to-return-entries": {
                    searchDuration = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-delay-task-return-state-if-timeout-is-encountered": {
                    timeoutReturnState = Task.parseString(p, values, null);
                    continue;
                }
            }
        }
        this.sleepDurationMillis = sleepDuration;
        this.millisToWaitForWorkQueueToBecomeIdle = workQueueWaitTime;
        this.ldapURLsForSearchesExpectedToReturnEntries = Collections.unmodifiableList((List<? extends LDAPURL>)urls);
        this.millisBetweenSearches = searchInterval;
        this.searchTimeLimitMillis = searchTimeLimit;
        this.totalDurationMillisForEachLDAPURL = searchDuration;
        this.taskStateIfTimeoutIsEncountered = timeoutReturnState;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_DELAY.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_DELAY.get();
    }
    
    public Long getSleepDurationMillis() {
        return this.sleepDurationMillis;
    }
    
    public Long getMillisToWaitForWorkQueueToBecomeIdle() {
        return this.millisToWaitForWorkQueueToBecomeIdle;
    }
    
    public List<LDAPURL> getLDAPURLsForSearchesExpectedToReturnEntries() {
        return this.ldapURLsForSearchesExpectedToReturnEntries;
    }
    
    public Long getMillisBetweenSearches() {
        return this.millisBetweenSearches;
    }
    
    public Long getSearchTimeLimitMillis() {
        return this.searchTimeLimitMillis;
    }
    
    public Long getTotalDurationMillisForEachLDAPURL() {
        return this.totalDurationMillisForEachLDAPURL;
    }
    
    public String getTaskStateIfTimeoutIsEncountered() {
        return this.taskStateIfTimeoutIsEncountered;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-delay");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final LinkedList<Attribute> attrList = new LinkedList<Attribute>();
        if (this.sleepDurationMillis != null) {
            final long sleepDurationNanos = this.sleepDurationMillis * 1000000L;
            attrList.add(new Attribute("ds-task-delay-sleep-duration", DurationArgument.nanosToDuration(sleepDurationNanos)));
        }
        if (this.millisToWaitForWorkQueueToBecomeIdle != null) {
            final long waitTimeNanos = this.millisToWaitForWorkQueueToBecomeIdle * 1000000L;
            attrList.add(new Attribute("ds-task-delay-duration-to-wait-for-work-queue-idle", DurationArgument.nanosToDuration(waitTimeNanos)));
        }
        if (!this.ldapURLsForSearchesExpectedToReturnEntries.isEmpty()) {
            final ArrayList<String> urlStrings = new ArrayList<String>(this.ldapURLsForSearchesExpectedToReturnEntries.size());
            for (final LDAPURL url : this.ldapURLsForSearchesExpectedToReturnEntries) {
                urlStrings.add(url.toString());
            }
            attrList.add(new Attribute("ds-task-delay-ldap-url-for-search-expected-to-return-entries", urlStrings));
        }
        if (this.millisBetweenSearches != null) {
            final long intervalNanos = this.millisBetweenSearches * 1000000L;
            attrList.add(new Attribute("ds-task-delay-search-interval", DurationArgument.nanosToDuration(intervalNanos)));
        }
        if (this.searchTimeLimitMillis != null) {
            final long timeLimitNanos = this.searchTimeLimitMillis * 1000000L;
            attrList.add(new Attribute("ds-task-delay-search-time-limit", DurationArgument.nanosToDuration(timeLimitNanos)));
        }
        if (this.totalDurationMillisForEachLDAPURL != null) {
            final long durationNanos = this.totalDurationMillisForEachLDAPURL * 1000000L;
            attrList.add(new Attribute("ds-task-delay-duration-to-wait-for-search-to-return-entries", DurationArgument.nanosToDuration(durationNanos)));
        }
        if (this.taskStateIfTimeoutIsEncountered != null) {
            attrList.add(new Attribute("ds-task-delay-task-return-state-if-timeout-is-encountered", this.taskStateIfTimeoutIsEncountered));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(DelayTask.PROPERTY_SLEEP_DURATION_MILLIS, DelayTask.PROPERTY_WAIT_FOR_WORK_QUEUE_IDLE_MILLIS, DelayTask.PROPERTY_SEARCH_URL, DelayTask.PROPERTY_SEARCH_INTERVAL_MILLIS, DelayTask.PROPERTY_SEARCH_TIME_LIMIT_MILLIS, DelayTask.PROPERTY_SEARCH_DURATION_MILLIS, DelayTask.PROPERTY_TIMEOUT_RETURN_STATE));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(7));
        if (this.sleepDurationMillis != null) {
            props.put(DelayTask.PROPERTY_SLEEP_DURATION_MILLIS, (List<Object>)Collections.singletonList(this.sleepDurationMillis));
        }
        if (this.millisToWaitForWorkQueueToBecomeIdle != null) {
            props.put(DelayTask.PROPERTY_WAIT_FOR_WORK_QUEUE_IDLE_MILLIS, (List<Object>)Collections.singletonList(this.millisToWaitForWorkQueueToBecomeIdle));
        }
        if (!this.ldapURLsForSearchesExpectedToReturnEntries.isEmpty()) {
            final List<String> urlStrings = new ArrayList<String>(this.ldapURLsForSearchesExpectedToReturnEntries.size());
            for (final LDAPURL url : this.ldapURLsForSearchesExpectedToReturnEntries) {
                urlStrings.add(url.toString());
            }
            props.put(DelayTask.PROPERTY_SEARCH_URL, Collections.unmodifiableList((List<?>)urlStrings));
        }
        if (this.millisBetweenSearches != null) {
            props.put(DelayTask.PROPERTY_SEARCH_INTERVAL_MILLIS, (List<Object>)Collections.singletonList(this.millisBetweenSearches));
        }
        if (this.searchTimeLimitMillis != null) {
            props.put(DelayTask.PROPERTY_SEARCH_TIME_LIMIT_MILLIS, (List<Object>)Collections.singletonList(this.searchTimeLimitMillis));
        }
        if (this.totalDurationMillisForEachLDAPURL != null) {
            props.put(DelayTask.PROPERTY_SEARCH_DURATION_MILLIS, (List<Object>)Collections.singletonList(this.totalDurationMillisForEachLDAPURL));
        }
        if (this.taskStateIfTimeoutIsEncountered != null) {
            props.put(DelayTask.PROPERTY_TIMEOUT_RETURN_STATE, (List<Object>)Collections.singletonList(this.taskStateIfTimeoutIsEncountered));
        }
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_SLEEP_DURATION_MILLIS = new TaskProperty("ds-task-delay-sleep-duration", TaskMessages.INFO_DELAY_DISPLAY_NAME_SLEEP_DURATION.get(), TaskMessages.INFO_DELAY_DESCRIPTION_SLEEP_DURATION.get(), Long.class, false, false, false);
        PROPERTY_WAIT_FOR_WORK_QUEUE_IDLE_MILLIS = new TaskProperty("ds-task-delay-duration-to-wait-for-work-queue-idle", TaskMessages.INFO_DELAY_DISPLAY_NAME_WAIT_FOR_WORK_QUEUE_IDLE.get(), TaskMessages.INFO_DELAY_DESCRIPTION_WAIT_FOR_WORK_QUEUE_IDLE.get(), Long.class, false, false, false);
        PROPERTY_SEARCH_URL = new TaskProperty("ds-task-delay-ldap-url-for-search-expected-to-return-entries", TaskMessages.INFO_DELAY_DISPLAY_NAME_SEARCH_URL.get(), TaskMessages.INFO_DELAY_DESCRIPTION_SEARCH_URL.get(), String.class, false, true, false);
        PROPERTY_SEARCH_INTERVAL_MILLIS = new TaskProperty("ds-task-delay-search-interval", TaskMessages.INFO_DELAY_DISPLAY_NAME_SEARCH_INTERVAL.get(), TaskMessages.INFO_DELAY_DESCRIPTION_SEARCH_INTERVAL.get(), Long.class, false, false, false);
        PROPERTY_SEARCH_TIME_LIMIT_MILLIS = new TaskProperty("ds-task-delay-search-time-limit", TaskMessages.INFO_DELAY_DISPLAY_NAME_SEARCH_TIME_LIMIT.get(), TaskMessages.INFO_DELAY_DESCRIPTION_SEARCH_TIME_LIMIT.get(), Long.class, false, false, false);
        PROPERTY_SEARCH_DURATION_MILLIS = new TaskProperty("ds-task-delay-duration-to-wait-for-search-to-return-entries", TaskMessages.INFO_DELAY_DISPLAY_NAME_SEARCH_DURATION.get(), TaskMessages.INFO_DELAY_DESCRIPTION_SEARCH_DURATION.get(), Long.class, false, false, false);
        PROPERTY_TIMEOUT_RETURN_STATE = new TaskProperty("ds-task-delay-task-return-state-if-timeout-is-encountered", TaskMessages.INFO_DELAY_DISPLAY_NAME_TIMEOUT_RETURN_STATE.get(), TaskMessages.INFO_DELAY_DESCRIPTION_TIMEOUT_RETURN_STATE.get(), String.class, false, false, false, new String[] { "STOPPED_BY_ERROR", "STOPPED-BY-ERROR", "COMPLETED_WITH_ERRORS", "COMPLETED-WITH-ERRORS", "COMPLETED_SUCCESSFULLY", "COMPLETED-SUCCESSFULLY" });
    }
}
