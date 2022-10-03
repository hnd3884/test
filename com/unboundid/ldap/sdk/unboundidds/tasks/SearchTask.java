package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.Date;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.List;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchTask extends Task
{
    static final String SEARCH_TASK_CLASS = "com.unboundid.directory.server.tasks.SearchTask";
    private static final String ATTR_BASE_DN = "ds-task-search-base-dn";
    private static final String ATTR_SCOPE = "ds-task-search-scope";
    private static final String ATTR_FILTER = "ds-task-search-filter";
    private static final String ATTR_RETURN_ATTR = "ds-task-search-return-attribute";
    private static final String ATTR_AUTHZ_DN = "ds-task-search-authz-dn";
    private static final String ATTR_OUTPUT_FILE = "ds-task-search-output-file";
    private static final String OC_SEARCH_TASK = "ds-task-search";
    private static final TaskProperty PROPERTY_BASE_DN;
    private static final Object[] ALLOWED_SCOPE_VALUES;
    private static final TaskProperty PROPERTY_SCOPE;
    private static final TaskProperty PROPERTY_FILTER;
    private static final TaskProperty PROPERTY_REQUESTED_ATTR;
    private static final TaskProperty PROPERTY_AUTHZ_DN;
    private static final TaskProperty PROPERTY_OUTPUT_FILE;
    private static final long serialVersionUID = -1742374271508548328L;
    private final Filter filter;
    private final List<String> attributes;
    private final SearchScope scope;
    private final String authzDN;
    private final String baseDN;
    private final String outputFile;
    
    public SearchTask() {
        this.filter = null;
        this.attributes = null;
        this.scope = null;
        this.authzDN = null;
        this.baseDN = null;
        this.outputFile = null;
    }
    
    public SearchTask(final String taskID, final String baseDN, final SearchScope scope, final Filter filter, final List<String> attributes, final String outputFile) {
        this(taskID, baseDN, scope, filter, attributes, outputFile, null, null, null, null, null, null);
    }
    
    public SearchTask(final String taskID, final String baseDN, final SearchScope scope, final Filter filter, final List<String> attributes, final String outputFile, final String authzDN) {
        this(taskID, baseDN, scope, filter, attributes, outputFile, authzDN, null, null, null, null, null);
    }
    
    public SearchTask(final String taskID, final String baseDN, final SearchScope scope, final Filter filter, final List<String> attributes, final String outputFile, final String authzDN, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, baseDN, scope, filter, attributes, outputFile, authzDN, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public SearchTask(final String taskID, final String baseDN, final SearchScope scope, final Filter filter, final List<String> attributes, final String outputFile, final String authzDN, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.SearchTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(baseDN, scope, filter, outputFile);
        this.baseDN = baseDN;
        this.scope = scope;
        this.filter = filter;
        this.outputFile = outputFile;
        this.authzDN = authzDN;
        if (attributes == null) {
            this.attributes = Collections.emptyList();
        }
        else {
            this.attributes = Collections.unmodifiableList((List<? extends String>)attributes);
        }
    }
    
    public SearchTask(final Entry entry) throws TaskException {
        super(entry);
        this.baseDN = entry.getAttributeValue("ds-task-search-base-dn");
        if (this.baseDN == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_ENTRY_NO_BASE_DN.get(entry.getDN()));
        }
        final String scopeStr = StaticUtils.toLowerCase(entry.getAttributeValue("ds-task-search-scope"));
        if (scopeStr == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_ENTRY_NO_SCOPE.get(entry.getDN()));
        }
        if (scopeStr.equals("base") || scopeStr.equals("baseobject") || scopeStr.equals("0")) {
            this.scope = SearchScope.BASE;
        }
        else if (scopeStr.equals("one") || scopeStr.equals("onelevel") || scopeStr.equals("singlelevel") || scopeStr.equals("1")) {
            this.scope = SearchScope.ONE;
        }
        else if (scopeStr.equals("sub") || scopeStr.equals("subtree") || scopeStr.equals("wholesubtree") || scopeStr.equals("2")) {
            this.scope = SearchScope.SUB;
        }
        else {
            if (!scopeStr.equals("subord") && !scopeStr.equals("subordinate") && !scopeStr.equals("subordinatesubtree") && !scopeStr.equals("3")) {
                throw new TaskException(TaskMessages.ERR_SEARCH_TASK_ENTRY_INVALID_SCOPE.get(entry.getDN(), scopeStr));
            }
            this.scope = SearchScope.SUBORDINATE_SUBTREE;
        }
        final String filterStr = entry.getAttributeValue("ds-task-search-filter");
        if (filterStr == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_ENTRY_NO_FILTER.get(entry.getDN()));
        }
        try {
            this.filter = Filter.create(filterStr);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_ENTRY_INVALID_FILTER.get(entry.getDN(), filterStr), le);
        }
        final String[] attrs = entry.getAttributeValues("ds-task-search-return-attribute");
        if (attrs == null) {
            this.attributes = Collections.emptyList();
        }
        else {
            this.attributes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])attrs));
        }
        this.authzDN = entry.getAttributeValue("ds-task-search-authz-dn");
        this.outputFile = entry.getAttributeValue("ds-task-search-output-file");
        if (this.outputFile == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_ENTRY_NO_OUTPUT_FILE.get(entry.getDN()));
        }
    }
    
    public SearchTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.SearchTask", properties);
        Filter tmpFilter = null;
        SearchScope tmpScope = null;
        String tmpAuthzDN = null;
        String tmpBaseDN = null;
        String tmpFile = null;
        String[] tmpAttrs = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = StaticUtils.toLowerCase(p.getAttributeName());
            final List<Object> values = entry.getValue();
            if (attrName.equals("ds-task-search-base-dn")) {
                tmpBaseDN = Task.parseString(p, values, null);
            }
            else if (attrName.equals("ds-task-search-scope")) {
                final String scopeStr = StaticUtils.toLowerCase(Task.parseString(p, values, null));
                if (scopeStr == null) {
                    continue;
                }
                if (scopeStr.equals("base") || scopeStr.equals("baseobject") || scopeStr.equals("0")) {
                    tmpScope = SearchScope.BASE;
                }
                else if (scopeStr.equals("one") || scopeStr.equals("onelevel") || scopeStr.equals("singlelevel") || scopeStr.equals("1")) {
                    tmpScope = SearchScope.ONE;
                }
                else if (scopeStr.equals("sub") || scopeStr.equals("subtree") || scopeStr.equals("wholesubtree") || scopeStr.equals("2")) {
                    tmpScope = SearchScope.SUB;
                }
                else {
                    if (!scopeStr.equals("subord") && !scopeStr.equals("subordinate") && !scopeStr.equals("subordinatesubtree") && !scopeStr.equals("3")) {
                        throw new TaskException(TaskMessages.ERR_SEARCH_TASK_INVALID_SCOPE_PROPERTY.get(scopeStr));
                    }
                    tmpScope = SearchScope.SUBORDINATE_SUBTREE;
                }
            }
            else if (attrName.equals("ds-task-search-filter")) {
                final String filterStr = Task.parseString(p, values, null);
                if (filterStr == null) {
                    continue;
                }
                try {
                    tmpFilter = Filter.create(filterStr);
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    throw new TaskException(TaskMessages.ERR_SEARCH_TASK_INVALID_FILTER_PROPERTY.get(filterStr), le);
                }
            }
            else if (attrName.equals("ds-task-search-return-attribute")) {
                tmpAttrs = Task.parseStrings(p, values, null);
            }
            else if (attrName.equals("ds-task-search-output-file")) {
                tmpFile = Task.parseString(p, values, null);
            }
            else {
                if (!attrName.equals("ds-task-search-authz-dn")) {
                    continue;
                }
                tmpAuthzDN = Task.parseString(p, values, null);
            }
        }
        this.baseDN = tmpBaseDN;
        if (this.baseDN == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_NO_BASE_PROPERTY.get());
        }
        this.scope = tmpScope;
        if (this.scope == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_NO_SCOPE_PROPERTY.get());
        }
        this.filter = tmpFilter;
        if (this.filter == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_NO_FILTER_PROPERTY.get());
        }
        this.outputFile = tmpFile;
        if (this.outputFile == null) {
            throw new TaskException(TaskMessages.ERR_SEARCH_TASK_NO_OUTPUT_FILE_PROPERTY.get());
        }
        if (tmpAttrs == null) {
            this.attributes = Collections.emptyList();
        }
        else {
            this.attributes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])tmpAttrs));
        }
        this.authzDN = tmpAuthzDN;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_SEARCH.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_SEARCH.get();
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public SearchScope getScope() {
        return this.scope;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public List<String> getAttributes() {
        return this.attributes;
    }
    
    public String getAuthzDN() {
        return this.authzDN;
    }
    
    public String getOutputFile() {
        return this.outputFile;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-search");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final LinkedList<Attribute> attrs = new LinkedList<Attribute>();
        attrs.add(new Attribute("ds-task-search-base-dn", this.baseDN));
        attrs.add(new Attribute("ds-task-search-scope", String.valueOf(this.scope.intValue())));
        attrs.add(new Attribute("ds-task-search-filter", this.filter.toString()));
        attrs.add(new Attribute("ds-task-search-output-file", this.outputFile));
        if (this.attributes != null && !this.attributes.isEmpty()) {
            attrs.add(new Attribute("ds-task-search-return-attribute", this.attributes));
        }
        if (this.authzDN != null) {
            attrs.add(new Attribute("ds-task-search-authz-dn", this.authzDN));
        }
        return Collections.unmodifiableList((List<? extends Attribute>)attrs);
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final LinkedList<TaskProperty> props = new LinkedList<TaskProperty>();
        props.add(SearchTask.PROPERTY_BASE_DN);
        props.add(SearchTask.PROPERTY_SCOPE);
        props.add(SearchTask.PROPERTY_FILTER);
        props.add(SearchTask.PROPERTY_REQUESTED_ATTR);
        props.add(SearchTask.PROPERTY_AUTHZ_DN);
        props.add(SearchTask.PROPERTY_OUTPUT_FILE);
        return Collections.unmodifiableList((List<? extends TaskProperty>)props);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(6));
        props.put(SearchTask.PROPERTY_BASE_DN, (List<Object>)Collections.singletonList(this.baseDN));
        props.put(SearchTask.PROPERTY_SCOPE, (List<Object>)Collections.singletonList(String.valueOf(this.scope.intValue())));
        props.put(SearchTask.PROPERTY_FILTER, (List<Object>)Collections.singletonList(this.filter.toString()));
        if (this.attributes != null && !this.attributes.isEmpty()) {
            final LinkedList<Object> attrObjects = new LinkedList<Object>();
            attrObjects.addAll(this.attributes);
            props.put(SearchTask.PROPERTY_REQUESTED_ATTR, Collections.unmodifiableList((List<?>)attrObjects));
        }
        if (this.authzDN != null) {
            props.put(SearchTask.PROPERTY_AUTHZ_DN, (List<Object>)Collections.singletonList(this.authzDN));
        }
        props.put(SearchTask.PROPERTY_OUTPUT_FILE, (List<Object>)Collections.singletonList(this.outputFile));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BASE_DN = new TaskProperty("ds-task-search-base-dn", TaskMessages.INFO_SEARCH_TASK_DISPLAY_NAME_BASE_DN.get(), TaskMessages.INFO_SEARCH_TASK_DESCRIPTION_BASE_DN.get(), String.class, true, false, false);
        ALLOWED_SCOPE_VALUES = new Object[] { "base", "baseobject", "0", "one", "onelevel", "singlelevel", "1", "sub", "subtree", "wholesubtree", "2", "subord", "subordinate", "subordinatesubtree", "3" };
        PROPERTY_SCOPE = new TaskProperty("ds-task-search-scope", TaskMessages.INFO_SEARCH_TASK_DISPLAY_NAME_SCOPE.get(), TaskMessages.INFO_SEARCH_TASK_DESCRIPTION_SCOPE.get(), String.class, true, false, false, SearchTask.ALLOWED_SCOPE_VALUES);
        PROPERTY_FILTER = new TaskProperty("ds-task-search-filter", TaskMessages.INFO_SEARCH_TASK_DISPLAY_NAME_FILTER.get(), TaskMessages.INFO_SEARCH_TASK_DESCRIPTION_FILTER.get(), String.class, true, false, false);
        PROPERTY_REQUESTED_ATTR = new TaskProperty("ds-task-search-return-attribute", TaskMessages.INFO_SEARCH_TASK_DISPLAY_NAME_RETURN_ATTR.get(), TaskMessages.INFO_SEARCH_TASK_DESCRIPTION_RETURN_ATTR.get(), String.class, false, true, false);
        PROPERTY_AUTHZ_DN = new TaskProperty("ds-task-search-authz-dn", TaskMessages.INFO_SEARCH_TASK_DISPLAY_NAME_AUTHZ_DN.get(), TaskMessages.INFO_SEARCH_TASK_DESCRIPTION_AUTHZ_DN.get(), String.class, false, false, true);
        PROPERTY_OUTPUT_FILE = new TaskProperty("ds-task-search-output-file", TaskMessages.INFO_SEARCH_TASK_DISPLAY_NAME_OUTPUT_FILE.get(), TaskMessages.INFO_SEARCH_TASK_DESCRIPTION_NAME_OUTPUT_FILE.get(), String.class, true, false, false);
    }
}
