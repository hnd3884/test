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
public final class ReEncodeEntriesTask extends Task
{
    static final String RE_ENCODE_ENTRIES_TASK_CLASS = "com.unboundid.directory.server.tasks.ReEncodeEntriesTask";
    private static final String ATTR_BACKEND_ID = "ds-task-reencode-backend-id";
    private static final String ATTR_INCLUDE_BRANCH = "ds-task-reencode-include-branch";
    private static final String ATTR_EXCLUDE_BRANCH = "ds-task-reencode-exclude-branch";
    private static final String ATTR_INCLUDE_FILTER = "ds-task-reencode-include-filter";
    private static final String ATTR_EXCLUDE_FILTER = "ds-task-reencode-exclude-filter";
    private static final String ATTR_MAX_ENTRIES_PER_SECOND = "ds-task-reencode-max-entries-per-second";
    private static final String ATTR_SKIP_FULLY_UNCACHED = "ds-task-reencode-skip-fully-uncached-entries";
    private static final String ATTR_SKIP_PARTIALLY_UNCACHED = "ds-task-reencode-skip-partially-uncached-entries";
    private static final String OC_REENCODE_ENTRIES_TASK = "ds-task-reencode";
    static final TaskProperty PROPERTY_BACKEND_ID;
    private static final TaskProperty PROPERTY_INCLUDE_BRANCH;
    private static final TaskProperty PROPERTY_EXCLUDE_BRANCH;
    private static final TaskProperty PROPERTY_INCLUDE_FILTER;
    private static final TaskProperty PROPERTY_EXCLUDE_FILTER;
    private static final TaskProperty PROPERTY_MAX_ENTRIES_PER_SECOND;
    private static final TaskProperty PROPERTY_SKIP_FULLY_UNCACHED;
    private static final TaskProperty PROPERTY_SKIP_PARTIALLY_UNCACHED;
    private static final long serialVersionUID = 1804218099237094046L;
    private final boolean skipFullyUncachedEntries;
    private final boolean skipPartiallyUncachedEntries;
    private final Long maxEntriesPerSecond;
    private final List<String> excludeBranches;
    private final List<String> excludeFilters;
    private final List<String> includeBranches;
    private final List<String> includeFilters;
    private final String backendID;
    
    public ReEncodeEntriesTask() {
        this.skipFullyUncachedEntries = false;
        this.skipPartiallyUncachedEntries = false;
        this.maxEntriesPerSecond = null;
        this.excludeBranches = null;
        this.excludeFilters = null;
        this.includeBranches = null;
        this.includeFilters = null;
        this.backendID = null;
    }
    
    public ReEncodeEntriesTask(final String taskID, final String backendID, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final Long maxEntriesPerSecond, final boolean skipFullyUncachedEntries, final boolean skipPartiallyUncachedEntries) {
        this(taskID, backendID, includeBranches, excludeBranches, includeFilters, excludeFilters, maxEntriesPerSecond, skipFullyUncachedEntries, skipPartiallyUncachedEntries, null, null, null, null, null);
    }
    
    public ReEncodeEntriesTask(final String taskID, final String backendID, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final Long maxEntriesPerSecond, final boolean skipFullyUncachedEntries, final boolean skipPartiallyUncachedEntries, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backendID, includeBranches, excludeBranches, includeFilters, excludeFilters, maxEntriesPerSecond, skipFullyUncachedEntries, skipPartiallyUncachedEntries, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ReEncodeEntriesTask(final String taskID, final String backendID, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final Long maxEntriesPerSecond, final boolean skipFullyUncachedEntries, final boolean skipPartiallyUncachedEntries, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.ReEncodeEntriesTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(backendID);
        this.backendID = backendID;
        this.maxEntriesPerSecond = maxEntriesPerSecond;
        this.skipFullyUncachedEntries = skipFullyUncachedEntries;
        this.skipPartiallyUncachedEntries = skipPartiallyUncachedEntries;
        if (includeBranches == null || includeBranches.isEmpty()) {
            this.includeBranches = Collections.emptyList();
        }
        else {
            this.includeBranches = Collections.unmodifiableList((List<? extends String>)includeBranches);
        }
        if (excludeBranches == null || excludeBranches.isEmpty()) {
            this.excludeBranches = Collections.emptyList();
        }
        else {
            this.excludeBranches = Collections.unmodifiableList((List<? extends String>)excludeBranches);
        }
        if (includeFilters == null || includeFilters.isEmpty()) {
            this.includeFilters = Collections.emptyList();
        }
        else {
            this.includeFilters = Collections.unmodifiableList((List<? extends String>)includeFilters);
        }
        if (excludeFilters == null || excludeFilters.isEmpty()) {
            this.excludeFilters = Collections.emptyList();
        }
        else {
            this.excludeFilters = Collections.unmodifiableList((List<? extends String>)excludeFilters);
        }
    }
    
    public ReEncodeEntriesTask(final Entry entry) throws TaskException {
        super(entry);
        this.backendID = entry.getAttributeValue("ds-task-reencode-backend-id");
        if (this.backendID == null) {
            throw new TaskException(TaskMessages.ERR_REENCODE_TASK_MISSING_REQUIRED_ATTR.get(entry.getDN(), "ds-task-reencode-backend-id"));
        }
        final String[] iBranches = entry.getAttributeValues("ds-task-reencode-include-branch");
        if (iBranches == null) {
            this.includeBranches = Collections.emptyList();
        }
        else {
            this.includeBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iBranches));
        }
        final String[] eBranches = entry.getAttributeValues("ds-task-reencode-exclude-branch");
        if (eBranches == null) {
            this.excludeBranches = Collections.emptyList();
        }
        else {
            this.excludeBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eBranches));
        }
        final String[] iFilters = entry.getAttributeValues("ds-task-reencode-include-filter");
        if (iFilters == null) {
            this.includeFilters = Collections.emptyList();
        }
        else {
            this.includeFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iFilters));
        }
        final String[] eFilters = entry.getAttributeValues("ds-task-reencode-exclude-filter");
        if (eFilters == null) {
            this.excludeFilters = Collections.emptyList();
        }
        else {
            this.excludeFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eFilters));
        }
        this.maxEntriesPerSecond = entry.getAttributeValueAsLong("ds-task-reencode-max-entries-per-second");
        final Boolean skipFullyUncached = entry.getAttributeValueAsBoolean("ds-task-reencode-skip-fully-uncached-entries");
        if (skipFullyUncached == null) {
            this.skipFullyUncachedEntries = false;
        }
        else {
            this.skipFullyUncachedEntries = skipFullyUncached;
        }
        final Boolean skipPartiallyUncached = entry.getAttributeValueAsBoolean("ds-task-reencode-skip-partially-uncached-entries");
        if (skipPartiallyUncached == null) {
            this.skipPartiallyUncachedEntries = false;
        }
        else {
            this.skipPartiallyUncachedEntries = skipPartiallyUncached;
        }
    }
    
    public ReEncodeEntriesTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.ReEncodeEntriesTask", properties);
        boolean skipFullyUncached = false;
        boolean skipPartiallyUncached = false;
        Long maxRate = null;
        List<String> eBranches = Collections.emptyList();
        List<String> eFilters = Collections.emptyList();
        List<String> iBranches = Collections.emptyList();
        List<String> iFilters = Collections.emptyList();
        String id = null;
        for (final Map.Entry<TaskProperty, List<Object>> e : properties.entrySet()) {
            final TaskProperty p = e.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = e.getValue();
            if (attrName.equalsIgnoreCase("ds-task-reencode-backend-id")) {
                id = Task.parseString(p, values, null);
            }
            else if (attrName.equalsIgnoreCase("ds-task-reencode-include-branch")) {
                final String[] branches = Task.parseStrings(p, values, null);
                if (branches == null) {
                    continue;
                }
                iBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])branches));
            }
            else if (attrName.equalsIgnoreCase("ds-task-reencode-exclude-branch")) {
                final String[] branches = Task.parseStrings(p, values, null);
                if (branches == null) {
                    continue;
                }
                eBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])branches));
            }
            else if (attrName.equalsIgnoreCase("ds-task-reencode-include-filter")) {
                final String[] filters = Task.parseStrings(p, values, null);
                if (filters == null) {
                    continue;
                }
                iFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])filters));
            }
            else if (attrName.equalsIgnoreCase("ds-task-reencode-exclude-filter")) {
                final String[] filters = Task.parseStrings(p, values, null);
                if (filters == null) {
                    continue;
                }
                eFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])filters));
            }
            else if (attrName.equalsIgnoreCase("ds-task-reencode-max-entries-per-second")) {
                maxRate = Task.parseLong(p, values, null);
            }
            else if (attrName.equalsIgnoreCase("ds-task-reencode-skip-fully-uncached-entries")) {
                skipFullyUncached = Task.parseBoolean(p, values, false);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-reencode-skip-partially-uncached-entries")) {
                    continue;
                }
                skipPartiallyUncached = Task.parseBoolean(p, values, false);
            }
        }
        if (id == null) {
            throw new TaskException(TaskMessages.ERR_REENCODE_TASK_MISSING_REQUIRED_PROPERTY.get("ds-task-reencode-backend-id"));
        }
        this.backendID = id;
        this.includeBranches = iBranches;
        this.excludeBranches = eBranches;
        this.includeFilters = iFilters;
        this.excludeFilters = eFilters;
        this.maxEntriesPerSecond = maxRate;
        this.skipFullyUncachedEntries = skipFullyUncached;
        this.skipPartiallyUncachedEntries = skipPartiallyUncached;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_REENCODE_ENTRIES.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_REENCODE_ENTRIES.get();
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    public List<String> getIncludeBranches() {
        return this.includeBranches;
    }
    
    public List<String> getExcludeBranches() {
        return this.excludeBranches;
    }
    
    public List<String> getIncludeFilters() {
        return this.includeFilters;
    }
    
    public List<String> getExcludeFilters() {
        return this.excludeFilters;
    }
    
    public Long getMaxEntriesPerSecond() {
        return this.maxEntriesPerSecond;
    }
    
    public boolean skipFullyUncachedEntries() {
        return this.skipFullyUncachedEntries;
    }
    
    public boolean skipPartiallyUncachedEntries() {
        return this.skipPartiallyUncachedEntries;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-reencode");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(7);
        attrList.add(new Attribute("ds-task-reencode-backend-id", this.backendID));
        attrList.add(new Attribute("ds-task-reencode-skip-fully-uncached-entries", String.valueOf(this.skipFullyUncachedEntries)));
        attrList.add(new Attribute("ds-task-reencode-skip-partially-uncached-entries", String.valueOf(this.skipPartiallyUncachedEntries)));
        if (!this.includeBranches.isEmpty()) {
            attrList.add(new Attribute("ds-task-reencode-include-branch", this.includeBranches));
        }
        if (!this.excludeBranches.isEmpty()) {
            attrList.add(new Attribute("ds-task-reencode-exclude-branch", this.excludeBranches));
        }
        if (!this.includeFilters.isEmpty()) {
            attrList.add(new Attribute("ds-task-reencode-include-filter", this.includeFilters));
        }
        if (!this.excludeFilters.isEmpty()) {
            attrList.add(new Attribute("ds-task-reencode-exclude-filter", this.excludeFilters));
        }
        if (this.maxEntriesPerSecond != null) {
            attrList.add(new Attribute("ds-task-reencode-max-entries-per-second", String.valueOf(this.maxEntriesPerSecond)));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(ReEncodeEntriesTask.PROPERTY_BACKEND_ID, ReEncodeEntriesTask.PROPERTY_INCLUDE_BRANCH, ReEncodeEntriesTask.PROPERTY_EXCLUDE_BRANCH, ReEncodeEntriesTask.PROPERTY_INCLUDE_FILTER, ReEncodeEntriesTask.PROPERTY_EXCLUDE_FILTER, ReEncodeEntriesTask.PROPERTY_MAX_ENTRIES_PER_SECOND, ReEncodeEntriesTask.PROPERTY_SKIP_FULLY_UNCACHED, ReEncodeEntriesTask.PROPERTY_SKIP_PARTIALLY_UNCACHED));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(15));
        props.put(ReEncodeEntriesTask.PROPERTY_BACKEND_ID, (List<Object>)Collections.singletonList(this.backendID));
        props.put(ReEncodeEntriesTask.PROPERTY_INCLUDE_BRANCH, Collections.unmodifiableList((List<?>)this.includeBranches));
        props.put(ReEncodeEntriesTask.PROPERTY_EXCLUDE_BRANCH, Collections.unmodifiableList((List<?>)this.excludeBranches));
        props.put(ReEncodeEntriesTask.PROPERTY_INCLUDE_FILTER, Collections.unmodifiableList((List<?>)this.includeFilters));
        props.put(ReEncodeEntriesTask.PROPERTY_EXCLUDE_FILTER, Collections.unmodifiableList((List<?>)this.excludeFilters));
        if (this.maxEntriesPerSecond == null) {
            props.put(ReEncodeEntriesTask.PROPERTY_MAX_ENTRIES_PER_SECOND, Collections.emptyList());
        }
        else {
            props.put(ReEncodeEntriesTask.PROPERTY_MAX_ENTRIES_PER_SECOND, (List<Object>)Collections.singletonList(this.maxEntriesPerSecond));
        }
        props.put(ReEncodeEntriesTask.PROPERTY_SKIP_FULLY_UNCACHED, (List<Object>)Collections.singletonList(this.skipFullyUncachedEntries));
        props.put(ReEncodeEntriesTask.PROPERTY_SKIP_PARTIALLY_UNCACHED, (List<Object>)Collections.singletonList(this.skipPartiallyUncachedEntries));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKEND_ID = new TaskProperty("ds-task-reencode-backend-id", TaskMessages.INFO_DISPLAY_NAME_REENCODE_BACKEND_ID.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_BACKEND_ID.get(), String.class, true, false, false);
        PROPERTY_INCLUDE_BRANCH = new TaskProperty("ds-task-reencode-include-branch", TaskMessages.INFO_DISPLAY_NAME_REENCODE_INCLUDE_BRANCH.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_INCLUDE_BRANCH.get(), String.class, false, true, false);
        PROPERTY_EXCLUDE_BRANCH = new TaskProperty("ds-task-reencode-exclude-branch", TaskMessages.INFO_DISPLAY_NAME_REENCODE_EXCLUDE_BRANCH.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_EXCLUDE_BRANCH.get(), String.class, false, true, false);
        PROPERTY_INCLUDE_FILTER = new TaskProperty("ds-task-reencode-include-filter", TaskMessages.INFO_DISPLAY_NAME_REENCODE_INCLUDE_FILTER.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_INCLUDE_FILTER.get(), String.class, false, true, false);
        PROPERTY_EXCLUDE_FILTER = new TaskProperty("ds-task-reencode-exclude-filter", TaskMessages.INFO_DISPLAY_NAME_REENCODE_EXCLUDE_FILTER.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_EXCLUDE_FILTER.get(), String.class, false, true, false);
        PROPERTY_MAX_ENTRIES_PER_SECOND = new TaskProperty("ds-task-reencode-max-entries-per-second", TaskMessages.INFO_DISPLAY_NAME_REENCODE_MAX_ENTRIES_PER_SECOND.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_MAX_ENTRIES_PER_SECOND.get(), Long.class, false, false, false);
        PROPERTY_SKIP_FULLY_UNCACHED = new TaskProperty("ds-task-reencode-skip-fully-uncached-entries", TaskMessages.INFO_DISPLAY_NAME_REENCODE_SKIP_FULLY_UNCACHED.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_SKIP_FULLY_UNCACHED.get(), Boolean.class, false, false, false);
        PROPERTY_SKIP_PARTIALLY_UNCACHED = new TaskProperty("ds-task-reencode-skip-partially-uncached-entries", TaskMessages.INFO_DISPLAY_NAME_REENCODE_SKIP_PARTIALLY_UNCACHED.get(), TaskMessages.INFO_DESCRIPTION_REENCODE_SKIP_PARTIALLY_UNCACHED.get(), Boolean.class, false, false, false);
    }
}
