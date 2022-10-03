package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.util.Debug;
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
public final class RebuildTask extends Task
{
    static final String REBUILD_TASK_CLASS = "com.unboundid.directory.server.tasks.RebuildTask";
    private static final String ATTR_BASE_DN = "ds-task-rebuild-base-dn";
    private static final String ATTR_INDEX = "ds-task-rebuild-index";
    private static final String ATTR_MAX_THREADS = "ds-task-rebuild-max-threads";
    private static final String OC_REBUILD_TASK = "ds-task-rebuild";
    private static final TaskProperty PROPERTY_BASE_DN;
    private static final TaskProperty PROPERTY_INDEX;
    private static final TaskProperty PROPERTY_MAX_THREADS;
    private static final long serialVersionUID = 6015907901926792443L;
    private final int maxThreads;
    private final String baseDN;
    private final List<String> indexes;
    
    public RebuildTask() {
        this.baseDN = null;
        this.maxThreads = -1;
        this.indexes = null;
    }
    
    public RebuildTask(final String taskID, final String baseDN, final List<String> indexes) {
        this(taskID, baseDN, indexes, -1, null, null, null, null, null);
    }
    
    public RebuildTask(final String taskID, final String baseDN, final List<String> indexes, final int maxThreads, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, baseDN, indexes, maxThreads, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public RebuildTask(final String taskID, final String baseDN, final List<String> indexes, final int maxThreads, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.RebuildTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(baseDN, indexes);
        Validator.ensureFalse(indexes.isEmpty(), "RebuildTask.indexes must not be empty.");
        this.baseDN = baseDN;
        this.indexes = Collections.unmodifiableList((List<? extends String>)indexes);
        this.maxThreads = maxThreads;
    }
    
    public RebuildTask(final Entry entry) throws TaskException {
        super(entry);
        this.baseDN = entry.getAttributeValue("ds-task-rebuild-base-dn");
        if (this.baseDN == null) {
            throw new TaskException(TaskMessages.ERR_REBUILD_TASK_NO_BASE_DN.get(this.getTaskEntryDN()));
        }
        final String[] indexArray = entry.getAttributeValues("ds-task-rebuild-index");
        if (indexArray == null || indexArray.length == 0) {
            throw new TaskException(TaskMessages.ERR_REBUILD_TASK_NO_INDEXES.get(this.getTaskEntryDN()));
        }
        this.indexes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])indexArray));
        final String threadsStr = entry.getAttributeValue("ds-task-rebuild-max-threads");
        if (threadsStr == null) {
            this.maxThreads = -1;
        }
        else {
            try {
                this.maxThreads = Integer.parseInt(threadsStr);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new TaskException(TaskMessages.ERR_REBUILD_TASK_INVALID_MAX_THREADS.get(this.getTaskEntryDN(), threadsStr), e);
            }
        }
    }
    
    public RebuildTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.RebuildTask", properties);
        long t = -1L;
        String b = null;
        String[] i = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-rebuild-base-dn")) {
                b = Task.parseString(p, values, b);
            }
            else if (attrName.equalsIgnoreCase("ds-task-rebuild-index")) {
                i = Task.parseStrings(p, values, i);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-rebuild-max-threads")) {
                    continue;
                }
                t = Task.parseLong(p, values, t);
            }
        }
        if (b == null) {
            throw new TaskException(TaskMessages.ERR_REBUILD_TASK_NO_BASE_DN.get(this.getTaskEntryDN()));
        }
        if (i == null) {
            throw new TaskException(TaskMessages.ERR_REBUILD_TASK_NO_INDEXES.get(this.getTaskEntryDN()));
        }
        this.baseDN = b;
        this.indexes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])i));
        this.maxThreads = (int)t;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_REBUILD.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_REBUILD.get();
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public List<String> getIndexNames() {
        return this.indexes;
    }
    
    public int getMaxRebuildThreads() {
        return this.maxThreads;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-rebuild");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(3);
        attrs.add(new Attribute("ds-task-rebuild-base-dn", this.baseDN));
        attrs.add(new Attribute("ds-task-rebuild-index", this.indexes));
        if (this.maxThreads > 0) {
            attrs.add(new Attribute("ds-task-rebuild-max-threads", String.valueOf(this.maxThreads)));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(RebuildTask.PROPERTY_BASE_DN, RebuildTask.PROPERTY_INDEX, RebuildTask.PROPERTY_MAX_THREADS);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        props.put(RebuildTask.PROPERTY_BASE_DN, (List<Object>)Collections.singletonList(this.baseDN));
        props.put(RebuildTask.PROPERTY_INDEX, Collections.unmodifiableList((List<?>)this.indexes));
        props.put(RebuildTask.PROPERTY_MAX_THREADS, (List<Object>)Collections.singletonList((long)this.maxThreads));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BASE_DN = new TaskProperty("ds-task-rebuild-base-dn", TaskMessages.INFO_DISPLAY_NAME_BASE_DN_REBUILD.get(), TaskMessages.INFO_DESCRIPTION_BASE_DN_REBUILD.get(), String.class, true, false, false);
        PROPERTY_INDEX = new TaskProperty("ds-task-rebuild-index", TaskMessages.INFO_DISPLAY_NAME_INDEX_REBUILD.get(), TaskMessages.INFO_DESCRIPTION_INDEX_REBUILD.get(), String.class, true, true, false);
        PROPERTY_MAX_THREADS = new TaskProperty("ds-task-rebuild-max-threads", TaskMessages.INFO_DISPLAY_NAME_MAX_THREADS_REBUILD.get(), TaskMessages.INFO_DESCRIPTION_MAX_THREADS_REBUILD.get(), Long.class, false, false, true);
    }
}
