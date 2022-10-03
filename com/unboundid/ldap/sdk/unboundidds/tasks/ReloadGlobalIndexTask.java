package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.Date;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReloadGlobalIndexTask extends Task
{
    static final String RELOAD_GLOBAL_INDEX_TASK_CLASS = "com.unboundid.directory.proxy.tasks.ReloadTask";
    private static final String ATTR_BACKGROUND_RELOAD = "ds-task-reload-background";
    private static final String ATTR_BASE_DN = "ds-task-reload-base-dn";
    private static final String ATTR_INDEX_NAME = "ds-task-reload-index-name";
    private static final String ATTR_MAX_ENTRIES_PER_SECOND = "ds-task-search-entry-per-second";
    private static final String ATTR_RELOAD_FROM_DS = "ds-task-reload-from-ds";
    private static final String OC_RELOAD_GLOBAL_INDEX_TASK = "ds-task-reload-index";
    private static final TaskProperty PROPERTY_BACKGROUND_RELOAD;
    private static final TaskProperty PROPERTY_BASE_DN;
    private static final TaskProperty PROPERTY_INDEX_NAME;
    private static final TaskProperty PROPERTY_MAX_ENTRIES_PER_SECOND;
    static final TaskProperty PROPERTY_RELOAD_FROM_DS;
    private static final long serialVersionUID = 9152807987055252560L;
    private final Boolean reloadFromDS;
    private final Boolean reloadInBackground;
    private final List<String> indexNames;
    private final Long maxEntriesPerSecond;
    private final String baseDN;
    
    public ReloadGlobalIndexTask() {
        this.reloadFromDS = null;
        this.reloadInBackground = null;
        this.indexNames = null;
        this.maxEntriesPerSecond = null;
        this.baseDN = null;
    }
    
    public ReloadGlobalIndexTask(final String taskID, final String baseDN, final List<String> indexNames, final Boolean reloadFromDS, final Boolean reloadInBackground, final Long maxEntriesPerSecond) {
        this(taskID, baseDN, indexNames, reloadFromDS, reloadInBackground, maxEntriesPerSecond, null, null, null, null, null);
    }
    
    public ReloadGlobalIndexTask(final String taskID, final String baseDN, final List<String> indexNames, final Boolean reloadFromDS, final Boolean reloadInBackground, final Long maxEntriesPerSecond, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, baseDN, indexNames, reloadFromDS, reloadInBackground, maxEntriesPerSecond, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ReloadGlobalIndexTask(final String taskID, final String baseDN, final List<String> indexNames, final Boolean reloadFromDS, final Boolean reloadInBackground, final Long maxEntriesPerSecond, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.proxy.tasks.ReloadTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(baseDN);
        this.baseDN = baseDN;
        this.reloadFromDS = reloadFromDS;
        this.reloadInBackground = reloadInBackground;
        this.maxEntriesPerSecond = maxEntriesPerSecond;
        if (indexNames == null) {
            this.indexNames = Collections.emptyList();
        }
        else {
            this.indexNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(indexNames));
        }
    }
    
    public ReloadGlobalIndexTask(final Entry entry) throws TaskException {
        super(entry);
        this.baseDN = entry.getAttributeValue("ds-task-reload-base-dn");
        if (this.baseDN == null) {
            throw new TaskException(TaskMessages.ERR_RELOAD_GLOBAL_INDEX_MISSING_REQUIRED_ATTR.get("ds-task-reload-base-dn"));
        }
        final String[] nameArray = entry.getAttributeValues("ds-task-reload-index-name");
        if (nameArray == null || nameArray.length == 0) {
            this.indexNames = Collections.emptyList();
        }
        else {
            this.indexNames = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])nameArray));
        }
        this.reloadFromDS = entry.getAttributeValueAsBoolean("ds-task-reload-from-ds");
        this.reloadInBackground = entry.getAttributeValueAsBoolean("ds-task-reload-background");
        this.maxEntriesPerSecond = entry.getAttributeValueAsLong("ds-task-search-entry-per-second");
    }
    
    public ReloadGlobalIndexTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.proxy.tasks.ReloadTask", properties);
        final List<String> attrs = new ArrayList<String>(10);
        Boolean background = null;
        Boolean fromDS = null;
        Long maxPerSecond = null;
        String baseDNStr = null;
        for (final Map.Entry<TaskProperty, List<Object>> e : properties.entrySet()) {
            final TaskProperty p = e.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = e.getValue();
            if (attrName.equalsIgnoreCase("ds-task-reload-base-dn")) {
                baseDNStr = Task.parseString(p, values, null);
            }
            else if (attrName.equalsIgnoreCase("ds-task-reload-index-name")) {
                final String[] nameArray = Task.parseStrings(p, values, null);
                if (nameArray == null) {
                    continue;
                }
                attrs.addAll(Arrays.asList(nameArray));
            }
            else if (attrName.equalsIgnoreCase("ds-task-reload-from-ds")) {
                fromDS = Task.parseBoolean(p, values, null);
            }
            else if (attrName.equalsIgnoreCase("ds-task-reload-background")) {
                background = Task.parseBoolean(p, values, null);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-search-entry-per-second")) {
                    continue;
                }
                maxPerSecond = Task.parseLong(p, values, null);
            }
        }
        if (baseDNStr == null) {
            throw new TaskException(TaskMessages.ERR_RELOAD_GLOBAL_INDEX_MISSING_REQUIRED_PROPERTY.get("ds-task-reload-base-dn"));
        }
        this.baseDN = baseDNStr;
        this.indexNames = Collections.unmodifiableList((List<? extends String>)attrs);
        this.reloadFromDS = fromDS;
        this.reloadInBackground = background;
        this.maxEntriesPerSecond = maxPerSecond;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_RELOAD_GLOBAL_INDEX.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_RELOAD_GLOBAL_INDEX.get();
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public List<String> getIndexNames() {
        return this.indexNames;
    }
    
    public Boolean reloadFromDS() {
        return this.reloadFromDS;
    }
    
    public Boolean reloadInBackground() {
        return this.reloadInBackground;
    }
    
    public Long getMaxEntriesPerSecond() {
        return this.maxEntriesPerSecond;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-reload-index");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(5);
        attrList.add(new Attribute("ds-task-reload-base-dn", this.baseDN));
        if (!this.indexNames.isEmpty()) {
            attrList.add(new Attribute("ds-task-reload-index-name", this.indexNames));
        }
        if (this.reloadFromDS != null) {
            attrList.add(new Attribute("ds-task-reload-from-ds", String.valueOf(this.reloadFromDS)));
        }
        if (this.reloadInBackground != null) {
            attrList.add(new Attribute("ds-task-reload-background", String.valueOf(this.reloadInBackground)));
        }
        if (this.maxEntriesPerSecond != null) {
            attrList.add(new Attribute("ds-task-search-entry-per-second", String.valueOf(this.maxEntriesPerSecond)));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(ReloadGlobalIndexTask.PROPERTY_BASE_DN, ReloadGlobalIndexTask.PROPERTY_INDEX_NAME, ReloadGlobalIndexTask.PROPERTY_RELOAD_FROM_DS, ReloadGlobalIndexTask.PROPERTY_BACKGROUND_RELOAD, ReloadGlobalIndexTask.PROPERTY_MAX_ENTRIES_PER_SECOND));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(15));
        props.put(ReloadGlobalIndexTask.PROPERTY_BASE_DN, (List<Object>)Collections.singletonList(this.baseDN));
        props.put(ReloadGlobalIndexTask.PROPERTY_INDEX_NAME, Collections.unmodifiableList((List<?>)this.indexNames));
        if (this.reloadFromDS == null) {
            props.put(ReloadGlobalIndexTask.PROPERTY_RELOAD_FROM_DS, Collections.emptyList());
        }
        else {
            props.put(ReloadGlobalIndexTask.PROPERTY_RELOAD_FROM_DS, (List<Object>)Collections.singletonList(this.reloadFromDS));
        }
        if (this.reloadInBackground == null) {
            props.put(ReloadGlobalIndexTask.PROPERTY_BACKGROUND_RELOAD, Collections.emptyList());
        }
        else {
            props.put(ReloadGlobalIndexTask.PROPERTY_BACKGROUND_RELOAD, (List<Object>)Collections.singletonList(this.reloadInBackground));
        }
        if (this.maxEntriesPerSecond == null) {
            props.put(ReloadGlobalIndexTask.PROPERTY_MAX_ENTRIES_PER_SECOND, Collections.emptyList());
        }
        else {
            props.put(ReloadGlobalIndexTask.PROPERTY_MAX_ENTRIES_PER_SECOND, (List<Object>)Collections.singletonList(this.maxEntriesPerSecond));
        }
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKGROUND_RELOAD = new TaskProperty("ds-task-reload-background", TaskMessages.INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_BACKGROUND.get(), TaskMessages.INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_BACKGROUND.get(), Boolean.class, false, false, false);
        PROPERTY_BASE_DN = new TaskProperty("ds-task-reload-base-dn", TaskMessages.INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_BASE_DN.get(), TaskMessages.INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_BASE_DN.get(), String.class, true, false, false);
        PROPERTY_INDEX_NAME = new TaskProperty("ds-task-reload-index-name", TaskMessages.INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_ATTR_NAME.get(), TaskMessages.INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_ATTR_NAME.get(), String.class, false, true, false);
        PROPERTY_MAX_ENTRIES_PER_SECOND = new TaskProperty("ds-task-search-entry-per-second", TaskMessages.INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_MAX_ENTRIES_PER_SECOND.get(), TaskMessages.INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_MAX_ENTRIES_PER_SECOND.get(), Long.class, false, false, false);
        PROPERTY_RELOAD_FROM_DS = new TaskProperty("ds-task-reload-from-ds", TaskMessages.INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_RELOAD_FROM_DS.get(), TaskMessages.INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_RELOAD_FROM_DS.get(), Boolean.class, false, false, false);
    }
}
