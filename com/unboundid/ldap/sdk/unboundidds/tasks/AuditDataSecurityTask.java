package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Filter;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collections;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.Date;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AuditDataSecurityTask extends Task
{
    static final String AUDIT_DATA_SECURITY_TASK_CLASS = "com.unboundid.directory.server.tasks.AuditDataSecurityTask";
    private static final String ATTR_INCLUDE_AUDITOR = "ds-task-audit-data-security-include-auditor";
    private static final String ATTR_EXCLUDE_AUDITOR = "ds-task-audit-data-security-exclude-auditor";
    private static final String ATTR_BACKEND_ID = "ds-task-audit-data-security-backend-id";
    private static final String ATTR_REPORT_FILTER = "ds-task-audit-data-security-report-filter";
    private static final String ATTR_OUTPUT_DIRECTORY = "ds-task-audit-data-security-output-directory";
    private static final String OC_AUDIT_DATA_SECURITY_TASK = "ds-task-audit-data-security";
    private static final TaskProperty PROPERTY_INCLUDE_AUDITOR;
    private static final TaskProperty PROPERTY_EXCLUDE_AUDITOR;
    private static final TaskProperty PROPERTY_BACKEND_ID;
    private static final TaskProperty PROPERTY_REPORT_FILTER;
    private static final TaskProperty PROPERTY_OUTPUT_DIRECTORY;
    private static final long serialVersionUID = -4994621474763299632L;
    private final List<String> backendIDs;
    private final List<String> excludeAuditors;
    private final List<String> includeAuditors;
    private final List<String> reportFilters;
    private final String outputDirectory;
    
    public AuditDataSecurityTask() {
        this.excludeAuditors = null;
        this.includeAuditors = null;
        this.backendIDs = null;
        this.reportFilters = null;
        this.outputDirectory = null;
    }
    
    public AuditDataSecurityTask(final List<String> includeAuditors, final List<String> excludeAuditors, final List<String> backendIDs, final List<String> reportFilters, final String outputDirectory) {
        this(null, includeAuditors, excludeAuditors, backendIDs, reportFilters, outputDirectory, null, null, null, null, null);
    }
    
    public AuditDataSecurityTask(final String taskID, final List<String> includeAuditors, final List<String> excludeAuditors, final List<String> backendIDs, final List<String> reportFilters, final String outputDirectory, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, includeAuditors, excludeAuditors, backendIDs, reportFilters, outputDirectory, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public AuditDataSecurityTask(final String taskID, final List<String> includeAuditors, final List<String> excludeAuditors, final List<String> backendIDs, final List<String> reportFilters, final String outputDirectory, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.AuditDataSecurityTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.includeAuditors = getStringList(includeAuditors);
        this.excludeAuditors = getStringList(excludeAuditors);
        this.backendIDs = getStringList(backendIDs);
        this.reportFilters = getStringList(reportFilters);
        this.outputDirectory = outputDirectory;
        Validator.ensureTrue(this.includeAuditors.isEmpty() || this.excludeAuditors.isEmpty(), "You cannot request both include and exclude auditors.");
    }
    
    public AuditDataSecurityTask(final Entry entry) throws TaskException {
        super(entry);
        this.includeAuditors = Collections.unmodifiableList((List<? extends String>)StaticUtils.toNonNullList(entry.getAttributeValues("ds-task-audit-data-security-include-auditor")));
        this.excludeAuditors = Collections.unmodifiableList((List<? extends String>)StaticUtils.toNonNullList(entry.getAttributeValues("ds-task-audit-data-security-exclude-auditor")));
        this.backendIDs = Collections.unmodifiableList((List<? extends String>)StaticUtils.toNonNullList(entry.getAttributeValues("ds-task-audit-data-security-backend-id")));
        this.reportFilters = Collections.unmodifiableList((List<? extends String>)StaticUtils.toNonNullList(entry.getAttributeValues("ds-task-audit-data-security-report-filter")));
        this.outputDirectory = entry.getAttributeValue("ds-task-audit-data-security-output-directory");
    }
    
    public AuditDataSecurityTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.AuditDataSecurityTask", properties);
        String outputDir = null;
        final LinkedList<String> includeAuditorsList = new LinkedList<String>();
        final LinkedList<String> excludeAuditorsList = new LinkedList<String>();
        final LinkedList<String> backendIDList = new LinkedList<String>();
        final LinkedList<String> reportFilterList = new LinkedList<String>();
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = StaticUtils.toLowerCase(p.getAttributeName());
            final List<Object> values = entry.getValue();
            if (attrName.equals("ds-task-audit-data-security-include-auditor")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                includeAuditorsList.addAll(Arrays.asList(s));
            }
            else if (attrName.equals("ds-task-audit-data-security-exclude-auditor")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                excludeAuditorsList.addAll(Arrays.asList(s));
            }
            else if (attrName.equals("ds-task-audit-data-security-backend-id")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                backendIDList.addAll(Arrays.asList(s));
            }
            else if (attrName.equals("ds-task-audit-data-security-report-filter")) {
                final String[] s = Task.parseStrings(p, values, null);
                if (s == null) {
                    continue;
                }
                reportFilterList.addAll(Arrays.asList(s));
            }
            else {
                if (!attrName.equals("ds-task-audit-data-security-output-directory")) {
                    continue;
                }
                outputDir = Task.parseString(p, values, null);
            }
        }
        this.includeAuditors = Collections.unmodifiableList((List<? extends String>)includeAuditorsList);
        this.excludeAuditors = Collections.unmodifiableList((List<? extends String>)excludeAuditorsList);
        this.backendIDs = Collections.unmodifiableList((List<? extends String>)backendIDList);
        this.reportFilters = Collections.unmodifiableList((List<? extends String>)reportFilterList);
        this.outputDirectory = outputDir;
        if (!this.includeAuditors.isEmpty() && !this.excludeAuditors.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_AUDIT_DATA_SECURITY_BOTH_INCLUDE_AND_EXCLUDE_AUDITORS.get());
        }
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_AUDIT_DATA_SECURITY.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_AUDIT_DATA_SECURITY.get();
    }
    
    public List<String> getIncludeAuditors() {
        return this.includeAuditors;
    }
    
    public List<String> getExcludeAuditors() {
        return this.excludeAuditors;
    }
    
    public List<String> getBackendIDs() {
        return this.backendIDs;
    }
    
    public List<String> getReportFilterStrings() {
        return this.reportFilters;
    }
    
    public List<Filter> getReportFilters() throws LDAPException {
        if (this.reportFilters.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList<Filter> filterList = new ArrayList<Filter>(this.reportFilters.size());
        for (final String filter : this.reportFilters) {
            filterList.add(Filter.create(filter));
        }
        return Collections.unmodifiableList((List<? extends Filter>)filterList);
    }
    
    public String getOutputDirectory() {
        return this.outputDirectory;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-audit-data-security");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final LinkedList<Attribute> attrList = new LinkedList<Attribute>();
        if (!this.includeAuditors.isEmpty()) {
            attrList.add(new Attribute("ds-task-audit-data-security-include-auditor", this.includeAuditors));
        }
        if (!this.excludeAuditors.isEmpty()) {
            attrList.add(new Attribute("ds-task-audit-data-security-exclude-auditor", this.excludeAuditors));
        }
        if (!this.backendIDs.isEmpty()) {
            attrList.add(new Attribute("ds-task-audit-data-security-backend-id", this.backendIDs));
        }
        if (!this.reportFilters.isEmpty()) {
            attrList.add(new Attribute("ds-task-audit-data-security-report-filter", this.reportFilters));
        }
        if (this.outputDirectory != null) {
            attrList.add(new Attribute("ds-task-audit-data-security-output-directory", this.outputDirectory));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(AuditDataSecurityTask.PROPERTY_INCLUDE_AUDITOR, AuditDataSecurityTask.PROPERTY_EXCLUDE_AUDITOR, AuditDataSecurityTask.PROPERTY_BACKEND_ID, AuditDataSecurityTask.PROPERTY_REPORT_FILTER, AuditDataSecurityTask.PROPERTY_OUTPUT_DIRECTORY));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(5));
        if (!this.includeAuditors.isEmpty()) {
            props.put(AuditDataSecurityTask.PROPERTY_INCLUDE_AUDITOR, Collections.unmodifiableList((List<?>)this.includeAuditors));
        }
        if (!this.excludeAuditors.isEmpty()) {
            props.put(AuditDataSecurityTask.PROPERTY_EXCLUDE_AUDITOR, Collections.unmodifiableList((List<?>)this.excludeAuditors));
        }
        if (!this.backendIDs.isEmpty()) {
            props.put(AuditDataSecurityTask.PROPERTY_BACKEND_ID, Collections.unmodifiableList((List<?>)this.backendIDs));
        }
        if (!this.reportFilters.isEmpty()) {
            props.put(AuditDataSecurityTask.PROPERTY_REPORT_FILTER, Collections.unmodifiableList((List<?>)this.reportFilters));
        }
        if (this.outputDirectory != null) {
            props.put(AuditDataSecurityTask.PROPERTY_OUTPUT_DIRECTORY, (List<Object>)Collections.singletonList(this.outputDirectory));
        }
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    private static List<String> getStringList(final List<String> l) {
        if (l == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)l);
    }
    
    static {
        PROPERTY_INCLUDE_AUDITOR = new TaskProperty("ds-task-audit-data-security-include-auditor", TaskMessages.INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_INCLUDE_AUDITOR.get(), TaskMessages.INFO_AUDIT_DATA_SECURITY_DESCRIPTION_INCLUDE_AUDITOR.get(), String.class, false, true, false);
        PROPERTY_EXCLUDE_AUDITOR = new TaskProperty("ds-task-audit-data-security-exclude-auditor", TaskMessages.INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_EXCLUDE_AUDITOR.get(), TaskMessages.INFO_AUDIT_DATA_SECURITY_DESCRIPTION_EXCLUDE_AUDITOR.get(), String.class, false, true, false);
        PROPERTY_BACKEND_ID = new TaskProperty("ds-task-audit-data-security-backend-id", TaskMessages.INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_BACKEND_ID.get(), TaskMessages.INFO_AUDIT_DATA_SECURITY_DESCRIPTION_BACKEND_ID.get(), String.class, false, true, false);
        PROPERTY_REPORT_FILTER = new TaskProperty("ds-task-audit-data-security-report-filter", TaskMessages.INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_REPORT_FILTER.get(), TaskMessages.INFO_AUDIT_DATA_SECURITY_DESCRIPTION_REPORT_FILTER.get(), String.class, false, true, false);
        PROPERTY_OUTPUT_DIRECTORY = new TaskProperty("ds-task-audit-data-security-output-directory", TaskMessages.INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_OUTPUT_DIR.get(), TaskMessages.INFO_AUDIT_DATA_SECURITY_DESCRIPTION_OUTPUT_DIR.get(), String.class, false, false, false);
    }
}
