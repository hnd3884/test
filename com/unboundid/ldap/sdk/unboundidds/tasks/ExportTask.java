package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Arrays;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.util.Debug;
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
public final class ExportTask extends Task
{
    static final String EXPORT_TASK_CLASS = "com.unboundid.directory.server.tasks.ExportTask";
    private static final String ATTR_APPEND_TO_LDIF = "ds-task-export-append-to-ldif";
    private static final String ATTR_BACKEND_ID = "ds-task-export-backend-id";
    private static final String ATTR_COMPRESS = "ds-task-export-compress-ldif";
    private static final String ATTR_ENCRYPT = "ds-task-export-encrypt-ldif";
    private static final String ATTR_ENCRYPTION_PASSPHRASE_FILE = "ds-task-export-encryption-passphrase-file";
    private static final String ATTR_ENCRYPTION_SETTINGS_DEFINITION_ID = "ds-task-export-encryption-settings-definition-id";
    private static final String ATTR_EXCLUDE_ATTRIBUTE = "ds-task-export-exclude-attribute";
    private static final String ATTR_EXCLUDE_BRANCH = "ds-task-export-exclude-branch";
    private static final String ATTR_EXCLUDE_FILTER = "ds-task-export-exclude-filter";
    private static final String ATTR_INCLUDE_ATTRIBUTE = "ds-task-export-include-attribute";
    private static final String ATTR_INCLUDE_BRANCH = "ds-task-export-include-branch";
    private static final String ATTR_INCLUDE_FILTER = "ds-task-export-include-filter";
    private static final String ATTR_LDIF_FILE = "ds-task-export-ldif-file";
    private static final String ATTR_MAX_MEGABYTES_PER_SECOND = "ds-task-export-max-megabytes-per-second";
    private static final String ATTR_SIGN = "ds-task-export-sign-hash";
    private static final String ATTR_WRAP_COLUMN = "ds-task-export-wrap-column";
    private static final String OC_EXPORT_TASK = "ds-task-export";
    private static final TaskProperty PROPERTY_BACKEND_ID;
    private static final TaskProperty PROPERTY_LDIF_FILE;
    private static final TaskProperty PROPERTY_APPEND_TO_LDIF;
    private static final TaskProperty PROPERTY_INCLUDE_BRANCH;
    private static final TaskProperty PROPERTY_EXCLUDE_BRANCH;
    private static final TaskProperty PROPERTY_INCLUDE_FILTER;
    private static final TaskProperty PROPERTY_EXCLUDE_FILTER;
    private static final TaskProperty PROPERTY_INCLUDE_ATTRIBUTE;
    private static final TaskProperty PROPERTY_EXCLUDE_ATTRIBUTE;
    private static final TaskProperty PROPERTY_WRAP_COLUMN;
    private static final TaskProperty PROPERTY_COMPRESS;
    private static final TaskProperty PROPERTY_ENCRYPT;
    private static final TaskProperty PROPERTY_ENCRYPTION_PASSPHRASE_FILE;
    private static final TaskProperty PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID;
    private static final TaskProperty PROPERTY_SIGN;
    private static final TaskProperty PROPERTY_MAX_MEGABYTES_PER_SECOND;
    private static final long serialVersionUID = -6807534587873728959L;
    private final boolean appendToLDIF;
    private final boolean compress;
    private final boolean encrypt;
    private final boolean sign;
    private final int wrapColumn;
    private final Integer maxMegabytesPerSecond;
    private final List<String> excludeAttributes;
    private final List<String> excludeBranches;
    private final List<String> excludeFilters;
    private final List<String> includeAttributes;
    private final List<String> includeBranches;
    private final List<String> includeFilters;
    private final String backendID;
    private final String encryptionPassphraseFile;
    private final String encryptionSettingsDefinitionID;
    private final String ldifFile;
    
    public ExportTask() {
        this.appendToLDIF = false;
        this.compress = false;
        this.encrypt = false;
        this.sign = false;
        this.wrapColumn = -1;
        this.maxMegabytesPerSecond = null;
        this.encryptionPassphraseFile = null;
        this.encryptionSettingsDefinitionID = null;
        this.excludeAttributes = null;
        this.excludeBranches = null;
        this.excludeFilters = null;
        this.includeAttributes = null;
        this.includeBranches = null;
        this.includeFilters = null;
        this.backendID = null;
        this.ldifFile = null;
    }
    
    public ExportTask(final String taskID, final String backendID, final String ldifFile) {
        this(taskID, backendID, ldifFile, false, null, null, null, null, null, null, -1, false, false, false, null, null, null, null, null);
    }
    
    public ExportTask(final String taskID, final String backendID, final String ldifFile, final boolean appendToLDIF, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final int wrapColumn, final boolean compress, final boolean encrypt, final boolean sign, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backendID, ldifFile, appendToLDIF, includeBranches, excludeBranches, includeFilters, excludeFilters, includeAttributes, excludeAttributes, wrapColumn, compress, encrypt, null, null, sign, null, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError);
    }
    
    public ExportTask(final String taskID, final String backendID, final String ldifFile, final boolean appendToLDIF, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final int wrapColumn, final boolean compress, final boolean encrypt, final String encryptionPassphraseFile, final String encryptionSettingsDefinitionID, final boolean sign, final Integer maxMegabytesPerSecond, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backendID, ldifFile, appendToLDIF, includeBranches, excludeBranches, includeFilters, excludeFilters, includeAttributes, excludeAttributes, wrapColumn, compress, encrypt, encryptionPassphraseFile, encryptionSettingsDefinitionID, sign, maxMegabytesPerSecond, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ExportTask(final String taskID, final String backendID, final String ldifFile, final boolean appendToLDIF, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final int wrapColumn, final boolean compress, final boolean encrypt, final String encryptionPassphraseFile, final String encryptionSettingsDefinitionID, final boolean sign, final Integer maxMegabytesPerSecond, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.ExportTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(backendID, ldifFile);
        this.backendID = backendID;
        this.ldifFile = ldifFile;
        this.appendToLDIF = appendToLDIF;
        this.wrapColumn = wrapColumn;
        this.compress = compress;
        this.encrypt = encrypt;
        this.encryptionPassphraseFile = encryptionPassphraseFile;
        this.encryptionSettingsDefinitionID = encryptionSettingsDefinitionID;
        this.sign = sign;
        this.maxMegabytesPerSecond = maxMegabytesPerSecond;
        if (includeBranches == null) {
            this.includeBranches = Collections.emptyList();
        }
        else {
            this.includeBranches = Collections.unmodifiableList((List<? extends String>)includeBranches);
        }
        if (excludeBranches == null) {
            this.excludeBranches = Collections.emptyList();
        }
        else {
            this.excludeBranches = Collections.unmodifiableList((List<? extends String>)excludeBranches);
        }
        if (includeFilters == null) {
            this.includeFilters = Collections.emptyList();
        }
        else {
            this.includeFilters = Collections.unmodifiableList((List<? extends String>)includeFilters);
        }
        if (excludeFilters == null) {
            this.excludeFilters = Collections.emptyList();
        }
        else {
            this.excludeFilters = Collections.unmodifiableList((List<? extends String>)excludeFilters);
        }
        if (includeAttributes == null) {
            this.includeAttributes = Collections.emptyList();
        }
        else {
            this.includeAttributes = Collections.unmodifiableList((List<? extends String>)includeAttributes);
        }
        if (excludeAttributes == null) {
            this.excludeAttributes = Collections.emptyList();
        }
        else {
            this.excludeAttributes = Collections.unmodifiableList((List<? extends String>)excludeAttributes);
        }
    }
    
    public ExportTask(final Entry entry) throws TaskException {
        super(entry);
        this.backendID = entry.getAttributeValue("ds-task-export-backend-id");
        if (this.backendID == null) {
            throw new TaskException(TaskMessages.ERR_EXPORT_TASK_NO_BACKEND_ID.get(this.getTaskEntryDN()));
        }
        this.ldifFile = entry.getAttributeValue("ds-task-export-ldif-file");
        if (this.ldifFile == null) {
            throw new TaskException(TaskMessages.ERR_EXPORT_TASK_NO_LDIF_FILE.get(this.getTaskEntryDN()));
        }
        this.appendToLDIF = Task.parseBooleanValue(entry, "ds-task-export-append-to-ldif", false);
        this.includeBranches = Task.parseStringList(entry, "ds-task-export-include-branch");
        this.excludeBranches = Task.parseStringList(entry, "ds-task-export-exclude-branch");
        this.includeFilters = Task.parseStringList(entry, "ds-task-export-include-filter");
        this.excludeFilters = Task.parseStringList(entry, "ds-task-export-exclude-filter");
        this.includeAttributes = Task.parseStringList(entry, "ds-task-export-include-attribute");
        this.excludeAttributes = Task.parseStringList(entry, "ds-task-export-exclude-attribute");
        final String wrapStr = entry.getAttributeValue("ds-task-export-wrap-column");
        if (wrapStr == null) {
            this.wrapColumn = -1;
        }
        else {
            try {
                this.wrapColumn = Integer.parseInt(wrapStr);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new TaskException(TaskMessages.ERR_EXPORT_TASK_CANNOT_PARSE_WRAP_COLUMN.get(this.getTaskEntryDN(), wrapStr), e);
            }
        }
        this.compress = Task.parseBooleanValue(entry, "ds-task-export-compress-ldif", false);
        this.encrypt = Task.parseBooleanValue(entry, "ds-task-export-encrypt-ldif", false);
        this.encryptionPassphraseFile = entry.getAttributeValue("ds-task-export-encryption-passphrase-file");
        this.encryptionSettingsDefinitionID = entry.getAttributeValue("ds-task-export-encryption-settings-definition-id");
        this.sign = Task.parseBooleanValue(entry, "ds-task-export-sign-hash", false);
        this.maxMegabytesPerSecond = entry.getAttributeValueAsInteger("ds-task-export-max-megabytes-per-second");
    }
    
    public ExportTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.ExportTask", properties);
        boolean a = false;
        boolean c = false;
        boolean e = false;
        boolean s = false;
        Integer maxMB = null;
        long w = 0L;
        String b = null;
        String encID = null;
        String encPWFile = null;
        String l = null;
        String[] eA = StaticUtils.NO_STRINGS;
        String[] eB = StaticUtils.NO_STRINGS;
        String[] eF = StaticUtils.NO_STRINGS;
        String[] iA = StaticUtils.NO_STRINGS;
        String[] iB = StaticUtils.NO_STRINGS;
        String[] iF = StaticUtils.NO_STRINGS;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-export-backend-id")) {
                b = Task.parseString(p, values, b);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-ldif-file")) {
                l = Task.parseString(p, values, l);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-append-to-ldif")) {
                a = Task.parseBoolean(p, values, a);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-include-branch")) {
                iB = Task.parseStrings(p, values, iB);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-exclude-branch")) {
                eB = Task.parseStrings(p, values, eB);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-include-filter")) {
                iF = Task.parseStrings(p, values, iF);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-exclude-filter")) {
                eF = Task.parseStrings(p, values, eF);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-include-attribute")) {
                iA = Task.parseStrings(p, values, iA);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-exclude-attribute")) {
                eA = Task.parseStrings(p, values, eA);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-wrap-column")) {
                w = Task.parseLong(p, values, w);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-compress-ldif")) {
                c = Task.parseBoolean(p, values, c);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-encrypt-ldif")) {
                e = Task.parseBoolean(p, values, e);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-encryption-passphrase-file")) {
                encPWFile = Task.parseString(p, values, encPWFile);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-encryption-settings-definition-id")) {
                encID = Task.parseString(p, values, encID);
            }
            else if (attrName.equalsIgnoreCase("ds-task-export-sign-hash")) {
                s = Task.parseBoolean(p, values, s);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-export-max-megabytes-per-second")) {
                    continue;
                }
                final Long maxMBLong = Task.parseLong(p, values, null);
                if (maxMBLong == null) {
                    maxMB = null;
                }
                else {
                    maxMB = maxMBLong.intValue();
                }
            }
        }
        if (b == null) {
            throw new TaskException(TaskMessages.ERR_EXPORT_TASK_NO_BACKEND_ID.get(this.getTaskEntryDN()));
        }
        if (l == null) {
            throw new TaskException(TaskMessages.ERR_EXPORT_TASK_NO_LDIF_FILE.get(this.getTaskEntryDN()));
        }
        this.backendID = b;
        this.ldifFile = l;
        this.appendToLDIF = a;
        this.includeAttributes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iA));
        this.excludeAttributes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eA));
        this.includeBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iB));
        this.excludeBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eB));
        this.includeFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iF));
        this.excludeFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eF));
        this.wrapColumn = (int)w;
        this.compress = c;
        this.encrypt = e;
        this.encryptionPassphraseFile = encPWFile;
        this.encryptionSettingsDefinitionID = encID;
        this.sign = s;
        this.maxMegabytesPerSecond = maxMB;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_EXPORT.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_EXPORT.get();
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    public String getLDIFFile() {
        return this.ldifFile;
    }
    
    public boolean appendToLDIF() {
        return this.appendToLDIF;
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
    
    public List<String> getIncludeAttributes() {
        return this.includeAttributes;
    }
    
    public List<String> getExcludeAttributes() {
        return this.excludeAttributes;
    }
    
    public int getWrapColumn() {
        return this.wrapColumn;
    }
    
    public boolean compress() {
        return this.compress;
    }
    
    public boolean encrypt() {
        return this.encrypt;
    }
    
    public String getEncryptionPassphraseFile() {
        return this.encryptionPassphraseFile;
    }
    
    public String getEncryptionSettingsDefinitionID() {
        return this.encryptionSettingsDefinitionID;
    }
    
    public boolean sign() {
        return this.sign;
    }
    
    public Integer getMaxMegabytesPerSecond() {
        return this.maxMegabytesPerSecond;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-export");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(20);
        attrs.add(new Attribute("ds-task-export-backend-id", this.backendID));
        attrs.add(new Attribute("ds-task-export-ldif-file", this.ldifFile));
        attrs.add(new Attribute("ds-task-export-append-to-ldif", String.valueOf(this.appendToLDIF)));
        attrs.add(new Attribute("ds-task-export-compress-ldif", String.valueOf(this.compress)));
        attrs.add(new Attribute("ds-task-export-encrypt-ldif", String.valueOf(this.encrypt)));
        attrs.add(new Attribute("ds-task-export-sign-hash", String.valueOf(this.sign)));
        if (!this.includeBranches.isEmpty()) {
            attrs.add(new Attribute("ds-task-export-include-branch", this.includeBranches));
        }
        if (!this.excludeBranches.isEmpty()) {
            attrs.add(new Attribute("ds-task-export-exclude-branch", this.excludeBranches));
        }
        if (!this.includeAttributes.isEmpty()) {
            attrs.add(new Attribute("ds-task-export-include-attribute", this.includeAttributes));
        }
        if (!this.excludeAttributes.isEmpty()) {
            attrs.add(new Attribute("ds-task-export-exclude-attribute", this.excludeAttributes));
        }
        if (!this.includeFilters.isEmpty()) {
            attrs.add(new Attribute("ds-task-export-include-filter", this.includeFilters));
        }
        if (!this.excludeFilters.isEmpty()) {
            attrs.add(new Attribute("ds-task-export-exclude-filter", this.excludeFilters));
        }
        if (this.wrapColumn > 0) {
            attrs.add(new Attribute("ds-task-export-wrap-column", String.valueOf(this.wrapColumn)));
        }
        if (this.encryptionPassphraseFile != null) {
            attrs.add(new Attribute("ds-task-export-encryption-passphrase-file", this.encryptionPassphraseFile));
        }
        if (this.encryptionSettingsDefinitionID != null) {
            attrs.add(new Attribute("ds-task-export-encryption-settings-definition-id", this.encryptionSettingsDefinitionID));
        }
        if (this.maxMegabytesPerSecond != null) {
            attrs.add(new Attribute("ds-task-export-max-megabytes-per-second", String.valueOf(this.maxMegabytesPerSecond)));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(ExportTask.PROPERTY_BACKEND_ID, ExportTask.PROPERTY_LDIF_FILE, ExportTask.PROPERTY_APPEND_TO_LDIF, ExportTask.PROPERTY_INCLUDE_BRANCH, ExportTask.PROPERTY_EXCLUDE_BRANCH, ExportTask.PROPERTY_INCLUDE_FILTER, ExportTask.PROPERTY_EXCLUDE_FILTER, ExportTask.PROPERTY_INCLUDE_ATTRIBUTE, ExportTask.PROPERTY_EXCLUDE_ATTRIBUTE, ExportTask.PROPERTY_WRAP_COLUMN, ExportTask.PROPERTY_COMPRESS, ExportTask.PROPERTY_ENCRYPT, ExportTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, ExportTask.PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID, ExportTask.PROPERTY_SIGN, ExportTask.PROPERTY_MAX_MEGABYTES_PER_SECOND);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(30));
        props.put(ExportTask.PROPERTY_BACKEND_ID, (List<Object>)Collections.singletonList(this.backendID));
        props.put(ExportTask.PROPERTY_LDIF_FILE, (List<Object>)Collections.singletonList(this.ldifFile));
        props.put(ExportTask.PROPERTY_APPEND_TO_LDIF, (List<Object>)Collections.singletonList(this.appendToLDIF));
        props.put(ExportTask.PROPERTY_INCLUDE_BRANCH, Collections.unmodifiableList((List<?>)this.includeBranches));
        props.put(ExportTask.PROPERTY_EXCLUDE_BRANCH, Collections.unmodifiableList((List<?>)this.excludeBranches));
        props.put(ExportTask.PROPERTY_INCLUDE_FILTER, Collections.unmodifiableList((List<?>)this.includeFilters));
        props.put(ExportTask.PROPERTY_EXCLUDE_FILTER, Collections.unmodifiableList((List<?>)this.excludeFilters));
        props.put(ExportTask.PROPERTY_INCLUDE_ATTRIBUTE, Collections.unmodifiableList((List<?>)this.includeAttributes));
        props.put(ExportTask.PROPERTY_EXCLUDE_ATTRIBUTE, Collections.unmodifiableList((List<?>)this.excludeAttributes));
        props.put(ExportTask.PROPERTY_WRAP_COLUMN, (List<Object>)Collections.singletonList((long)this.wrapColumn));
        props.put(ExportTask.PROPERTY_COMPRESS, (List<Object>)Collections.singletonList(this.compress));
        props.put(ExportTask.PROPERTY_ENCRYPT, (List<Object>)Collections.singletonList(this.encrypt));
        if (this.encryptionPassphraseFile == null) {
            props.put(ExportTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, Collections.emptyList());
        }
        else {
            props.put(ExportTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, (List<Object>)Collections.singletonList(this.encryptionPassphraseFile));
        }
        if (this.encryptionSettingsDefinitionID == null) {
            props.put(ExportTask.PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID, Collections.emptyList());
        }
        else {
            props.put(ExportTask.PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID, (List<Object>)Collections.singletonList(this.encryptionSettingsDefinitionID));
        }
        props.put(ExportTask.PROPERTY_SIGN, (List<Object>)Collections.singletonList(this.sign));
        if (this.maxMegabytesPerSecond == null) {
            props.put(ExportTask.PROPERTY_MAX_MEGABYTES_PER_SECOND, Collections.emptyList());
        }
        else {
            props.put(ExportTask.PROPERTY_MAX_MEGABYTES_PER_SECOND, (List<Object>)Collections.singletonList((long)this.maxMegabytesPerSecond));
        }
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKEND_ID = new TaskProperty("ds-task-export-backend-id", TaskMessages.INFO_DISPLAY_NAME_BACKEND_ID.get(), TaskMessages.INFO_DESCRIPTION_BACKEND_ID_EXPORT.get(), String.class, true, false, false);
        PROPERTY_LDIF_FILE = new TaskProperty("ds-task-export-ldif-file", TaskMessages.INFO_DISPLAY_NAME_LDIF_FILE.get(), TaskMessages.INFO_DESCRIPTION_LDIF_FILE_EXPORT.get(), String.class, true, false, false);
        PROPERTY_APPEND_TO_LDIF = new TaskProperty("ds-task-export-append-to-ldif", TaskMessages.INFO_DISPLAY_NAME_APPEND_TO_LDIF.get(), TaskMessages.INFO_DESCRIPTION_APPEND_TO_LDIF.get(), Boolean.class, false, false, true);
        PROPERTY_INCLUDE_BRANCH = new TaskProperty("ds-task-export-include-branch", TaskMessages.INFO_DISPLAY_NAME_INCLUDE_BRANCH.get(), TaskMessages.INFO_DESCRIPTION_INCLUDE_BRANCH_EXPORT.get(), String.class, false, true, true);
        PROPERTY_EXCLUDE_BRANCH = new TaskProperty("ds-task-export-exclude-branch", TaskMessages.INFO_DISPLAY_NAME_EXCLUDE_BRANCH.get(), TaskMessages.INFO_DESCRIPTION_EXCLUDE_BRANCH_EXPORT.get(), String.class, false, true, true);
        PROPERTY_INCLUDE_FILTER = new TaskProperty("ds-task-export-include-filter", TaskMessages.INFO_DISPLAY_NAME_INCLUDE_FILTER.get(), TaskMessages.INFO_DESCRIPTION_INCLUDE_FILTER_EXPORT.get(), String.class, false, true, true);
        PROPERTY_EXCLUDE_FILTER = new TaskProperty("ds-task-export-exclude-filter", TaskMessages.INFO_DISPLAY_NAME_EXCLUDE_FILTER.get(), TaskMessages.INFO_DESCRIPTION_EXCLUDE_FILTER_EXPORT.get(), String.class, false, true, true);
        PROPERTY_INCLUDE_ATTRIBUTE = new TaskProperty("ds-task-export-include-attribute", TaskMessages.INFO_DISPLAY_NAME_INCLUDE_ATTRIBUTE.get(), TaskMessages.INFO_DESCRIPTION_INCLUDE_ATTRIBUTE_EXPORT.get(), String.class, false, true, true);
        PROPERTY_EXCLUDE_ATTRIBUTE = new TaskProperty("ds-task-export-exclude-attribute", TaskMessages.INFO_DISPLAY_NAME_EXCLUDE_ATTRIBUTE.get(), TaskMessages.INFO_DESCRIPTION_EXCLUDE_ATTRIBUTE_EXPORT.get(), String.class, false, true, true);
        PROPERTY_WRAP_COLUMN = new TaskProperty("ds-task-export-wrap-column", TaskMessages.INFO_DISPLAY_NAME_WRAP_COLUMN.get(), TaskMessages.INFO_DESCRIPTION_WRAP_COLUMN.get(), Long.class, false, false, true);
        PROPERTY_COMPRESS = new TaskProperty("ds-task-export-compress-ldif", TaskMessages.INFO_DISPLAY_NAME_COMPRESS.get(), TaskMessages.INFO_DESCRIPTION_COMPRESS_EXPORT.get(), Boolean.class, false, false, false);
        PROPERTY_ENCRYPT = new TaskProperty("ds-task-export-encrypt-ldif", TaskMessages.INFO_DISPLAY_NAME_ENCRYPT.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPT_EXPORT.get(), Boolean.class, false, false, false);
        PROPERTY_ENCRYPTION_PASSPHRASE_FILE = new TaskProperty("ds-task-export-encryption-passphrase-file", TaskMessages.INFO_DISPLAY_NAME_ENCRYPTION_PASSPHRASE_FILE.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPTION_PASSPHRASE_FILE.get(), String.class, false, false, true);
        PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID = new TaskProperty("ds-task-export-encryption-settings-definition-id", TaskMessages.INFO_DISPLAY_NAME_ENCRYPTION_SETTINGS_DEFINITION_ID.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPTION_SETTINGS_DEFINITION_ID.get(), String.class, false, false, true);
        PROPERTY_SIGN = new TaskProperty("ds-task-export-sign-hash", TaskMessages.INFO_DISPLAY_NAME_SIGN.get(), TaskMessages.INFO_DESCRIPTION_SIGN_EXPORT.get(), Boolean.class, false, false, false);
        PROPERTY_MAX_MEGABYTES_PER_SECOND = new TaskProperty("ds-task-export-max-megabytes-per-second", TaskMessages.INFO_DISPLAY_NAME_EXPORT_MAX_MEGABYTES_PER_SECOND.get(), TaskMessages.INFO_DESCRIPTION_EXPORT_MAX_MEGABYTES_PER_SECOND.get(), Long.class, false, false, true);
    }
}
