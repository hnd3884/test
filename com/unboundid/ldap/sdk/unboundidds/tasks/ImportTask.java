package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import java.util.Arrays;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.Date;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ImportTask extends Task
{
    static final String IMPORT_TASK_CLASS = "com.unboundid.directory.server.tasks.ImportTask";
    private static final String ATTR_APPEND = "ds-task-import-append";
    private static final String ATTR_BACKEND_ID = "ds-task-import-backend-id";
    private static final String ATTR_CLEAR_BACKEND = "ds-task-import-clear-backend";
    private static final String ATTR_ENCRYPTION_PASSPHRASE_FILE = "ds-task-import-encryption-passphrase-file";
    private static final String ATTR_EXCLUDE_ATTRIBUTE = "ds-task-import-exclude-attribute";
    private static final String ATTR_EXCLUDE_BRANCH = "ds-task-import-exclude-branch";
    private static final String ATTR_EXCLUDE_FILTER = "ds-task-import-exclude-filter";
    private static final String ATTR_INCLUDE_ATTRIBUTE = "ds-task-import-include-attribute";
    private static final String ATTR_INCLUDE_BRANCH = "ds-task-import-include-branch";
    private static final String ATTR_INCLUDE_FILTER = "ds-task-import-include-filter";
    private static final String ATTR_IS_COMPRESSED = "ds-task-import-is-compressed";
    private static final String ATTR_IS_ENCRYPTED = "ds-task-import-is-encrypted";
    private static final String ATTR_LDIF_FILE = "ds-task-import-ldif-file";
    private static final String ATTR_OVERWRITE_REJECTS = "ds-task-import-overwrite-rejects";
    private static final String ATTR_REJECT_FILE = "ds-task-import-reject-file";
    private static final String ATTR_REPLACE_EXISTING = "ds-task-import-replace-existing";
    private static final String ATTR_SKIP_SCHEMA_VALIDATION = "ds-task-import-skip-schema-validation";
    private static final String ATTR_STRIP_TRAILING_SPACES = "ds-task-import-strip-trailing-spaces";
    private static final TaskProperty PROPERTY_BACKEND_ID;
    private static final TaskProperty PROPERTY_LDIF_FILE;
    private static final TaskProperty PROPERTY_APPEND;
    private static final TaskProperty PROPERTY_REPLACE_EXISTING;
    private static final TaskProperty PROPERTY_REJECT_FILE;
    private static final TaskProperty PROPERTY_OVERWRITE_REJECTS;
    private static final TaskProperty PROPERTY_CLEAR_BACKEND;
    private static final TaskProperty PROPERTY_INCLUDE_BRANCH;
    private static final TaskProperty PROPERTY_EXCLUDE_BRANCH;
    private static final TaskProperty PROPERTY_INCLUDE_FILTER;
    private static final TaskProperty PROPERTY_EXCLUDE_FILTER;
    private static final TaskProperty PROPERTY_INCLUDE_ATTRIBUTE;
    private static final TaskProperty PROPERTY_EXCLUDE_ATTRIBUTE;
    private static final TaskProperty PROPERTY_IS_COMPRESSED;
    private static final TaskProperty PROPERTY_IS_ENCRYPTED;
    private static final TaskProperty PROPERTY_ENCRYPTION_PASSPHRASE_FILE;
    private static final TaskProperty PROPERTY_SKIP_SCHEMA_VALIDATION;
    private static final TaskProperty PROPERTY_STRIP_TRAILING_SPACES;
    private static final String OC_IMPORT_TASK = "ds-task-import";
    private static final long serialVersionUID = 9114913680318281750L;
    private final boolean append;
    private final boolean clearBackend;
    private final boolean isCompressed;
    private final boolean isEncrypted;
    private final boolean overwriteRejects;
    private final boolean replaceExisting;
    private final boolean skipSchemaValidation;
    private final boolean stripTrailingSpaces;
    private final List<String> excludeAttributes;
    private final List<String> excludeBranches;
    private final List<String> excludeFilters;
    private final List<String> includeAttributes;
    private final List<String> includeBranches;
    private final List<String> includeFilters;
    private final List<String> ldifFiles;
    private final String backendID;
    private final String encryptionPassphraseFile;
    private final String rejectFile;
    
    public ImportTask() {
        this.append = false;
        this.clearBackend = false;
        this.isCompressed = false;
        this.isEncrypted = false;
        this.overwriteRejects = false;
        this.replaceExisting = false;
        this.skipSchemaValidation = false;
        this.stripTrailingSpaces = false;
        this.encryptionPassphraseFile = null;
        this.excludeAttributes = null;
        this.excludeBranches = null;
        this.excludeFilters = null;
        this.includeAttributes = null;
        this.includeBranches = null;
        this.includeFilters = null;
        this.ldifFiles = null;
        this.backendID = null;
        this.rejectFile = null;
    }
    
    public ImportTask(final String taskID, final String backendID, final String ldifFile) {
        this(taskID, Collections.singletonList(ldifFile), backendID, false, false, null, false, true, null, null, null, null, null, null, false, false, false, null, null, null, null, null);
        Validator.ensureNotNull(ldifFile);
    }
    
    public ImportTask(final String taskID, final List<String> ldifFiles, final String backendID, final boolean append, final boolean replaceExisting, final String rejectFile, final boolean overwriteRejects, final boolean clearBackend, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final boolean isCompressed, final boolean isEncrypted, final boolean skipSchemaValidation, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, ldifFiles, backendID, append, replaceExisting, rejectFile, overwriteRejects, clearBackend, includeBranches, excludeBranches, includeFilters, excludeFilters, includeAttributes, excludeAttributes, isCompressed, isEncrypted, skipSchemaValidation, false, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError);
    }
    
    public ImportTask(final String taskID, final List<String> ldifFiles, final String backendID, final boolean append, final boolean replaceExisting, final String rejectFile, final boolean overwriteRejects, final boolean clearBackend, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final boolean isCompressed, final boolean isEncrypted, final boolean skipSchemaValidation, final boolean stripTrailingSpaces, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, ldifFiles, backendID, append, replaceExisting, rejectFile, overwriteRejects, clearBackend, includeBranches, excludeBranches, includeFilters, excludeFilters, includeAttributes, excludeAttributes, isCompressed, isEncrypted, null, skipSchemaValidation, stripTrailingSpaces, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError);
    }
    
    public ImportTask(final String taskID, final List<String> ldifFiles, final String backendID, final boolean append, final boolean replaceExisting, final String rejectFile, final boolean overwriteRejects, final boolean clearBackend, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final boolean isCompressed, final boolean isEncrypted, final String encryptionPassphraseFile, final boolean skipSchemaValidation, final boolean stripTrailingSpaces, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, ldifFiles, backendID, append, replaceExisting, rejectFile, overwriteRejects, clearBackend, includeBranches, excludeBranches, includeFilters, excludeFilters, includeAttributes, excludeAttributes, isCompressed, isEncrypted, encryptionPassphraseFile, skipSchemaValidation, stripTrailingSpaces, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ImportTask(final String taskID, final List<String> ldifFiles, final String backendID, final boolean append, final boolean replaceExisting, final String rejectFile, final boolean overwriteRejects, final boolean clearBackend, final List<String> includeBranches, final List<String> excludeBranches, final List<String> includeFilters, final List<String> excludeFilters, final List<String> includeAttributes, final List<String> excludeAttributes, final boolean isCompressed, final boolean isEncrypted, final String encryptionPassphraseFile, final boolean skipSchemaValidation, final boolean stripTrailingSpaces, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.ImportTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(ldifFiles);
        Validator.ensureFalse(ldifFiles.isEmpty(), "ImportTask.ldifFiles must not be empty.");
        Validator.ensureFalse(backendID == null && (includeBranches == null || includeBranches.isEmpty()));
        Validator.ensureTrue(clearBackend || append || (includeBranches != null && !includeBranches.isEmpty()));
        this.ldifFiles = Collections.unmodifiableList((List<? extends String>)ldifFiles);
        this.backendID = backendID;
        this.append = append;
        this.replaceExisting = replaceExisting;
        this.rejectFile = rejectFile;
        this.overwriteRejects = overwriteRejects;
        this.clearBackend = clearBackend;
        this.isCompressed = isCompressed;
        this.isEncrypted = isEncrypted;
        this.encryptionPassphraseFile = encryptionPassphraseFile;
        this.skipSchemaValidation = skipSchemaValidation;
        this.stripTrailingSpaces = stripTrailingSpaces;
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
    
    public ImportTask(final Entry entry) throws TaskException {
        super(entry);
        final String[] files = entry.getAttributeValues("ds-task-import-ldif-file");
        if (files == null || files.length == 0) {
            throw new TaskException(TaskMessages.ERR_IMPORT_TASK_NO_LDIF.get(this.getTaskEntryDN()));
        }
        this.ldifFiles = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])files));
        this.backendID = entry.getAttributeValue("ds-task-import-backend-id");
        this.append = Task.parseBooleanValue(entry, "ds-task-import-append", false);
        this.replaceExisting = Task.parseBooleanValue(entry, "ds-task-import-replace-existing", false);
        this.rejectFile = entry.getAttributeValue("ds-task-import-reject-file");
        this.overwriteRejects = Task.parseBooleanValue(entry, "ds-task-import-overwrite-rejects", false);
        this.clearBackend = Task.parseBooleanValue(entry, "ds-task-import-clear-backend", false);
        this.includeBranches = Task.parseStringList(entry, "ds-task-import-include-branch");
        this.excludeBranches = Task.parseStringList(entry, "ds-task-import-exclude-branch");
        this.includeFilters = Task.parseStringList(entry, "ds-task-import-include-filter");
        this.excludeFilters = Task.parseStringList(entry, "ds-task-import-exclude-filter");
        this.includeAttributes = Task.parseStringList(entry, "ds-task-import-include-attribute");
        this.excludeAttributes = Task.parseStringList(entry, "ds-task-import-exclude-attribute");
        this.isCompressed = Task.parseBooleanValue(entry, "ds-task-import-is-compressed", false);
        this.isEncrypted = Task.parseBooleanValue(entry, "ds-task-import-is-encrypted", false);
        this.encryptionPassphraseFile = entry.getAttributeValue("ds-task-import-encryption-passphrase-file");
        this.skipSchemaValidation = Task.parseBooleanValue(entry, "ds-task-import-skip-schema-validation", false);
        this.stripTrailingSpaces = Task.parseBooleanValue(entry, "ds-task-import-strip-trailing-spaces", false);
    }
    
    public ImportTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.ImportTask", properties);
        boolean a = false;
        boolean c = false;
        boolean cB = true;
        boolean e = false;
        boolean o = false;
        boolean r = false;
        boolean ss = false;
        boolean st = false;
        String b = null;
        String pF = null;
        String rF = null;
        String[] eA = StaticUtils.NO_STRINGS;
        String[] eB = StaticUtils.NO_STRINGS;
        String[] eF = StaticUtils.NO_STRINGS;
        String[] iA = StaticUtils.NO_STRINGS;
        String[] iB = StaticUtils.NO_STRINGS;
        String[] iF = StaticUtils.NO_STRINGS;
        String[] l = StaticUtils.NO_STRINGS;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-task-import-backend-id")) {
                b = Task.parseString(p, values, b);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-ldif-file")) {
                l = Task.parseStrings(p, values, l);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-append")) {
                a = Task.parseBoolean(p, values, a);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-replace-existing")) {
                r = Task.parseBoolean(p, values, r);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-reject-file")) {
                rF = Task.parseString(p, values, rF);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-overwrite-rejects")) {
                o = Task.parseBoolean(p, values, o);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-clear-backend")) {
                cB = Task.parseBoolean(p, values, cB);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-include-branch")) {
                iB = Task.parseStrings(p, values, iB);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-exclude-branch")) {
                eB = Task.parseStrings(p, values, eB);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-include-filter")) {
                iF = Task.parseStrings(p, values, iF);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-exclude-filter")) {
                eF = Task.parseStrings(p, values, eF);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-include-attribute")) {
                iA = Task.parseStrings(p, values, iA);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-exclude-attribute")) {
                eA = Task.parseStrings(p, values, eA);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-is-compressed")) {
                c = Task.parseBoolean(p, values, c);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-is-encrypted")) {
                e = Task.parseBoolean(p, values, e);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-encryption-passphrase-file")) {
                pF = Task.parseString(p, values, pF);
            }
            else if (attrName.equalsIgnoreCase("ds-task-import-skip-schema-validation")) {
                ss = Task.parseBoolean(p, values, ss);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-import-strip-trailing-spaces")) {
                    continue;
                }
                st = Task.parseBoolean(p, values, st);
            }
        }
        if (b == null && iB.length == 0) {
            throw new TaskException(TaskMessages.ERR_IMPORT_TASK_NO_BACKEND_ID_OR_INCLUDE_BRANCHES.get(this.getTaskEntryDN()));
        }
        if (l == null) {
            throw new TaskException(TaskMessages.ERR_IMPORT_TASK_NO_LDIF.get(this.getTaskEntryDN()));
        }
        this.backendID = b;
        this.ldifFiles = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])l));
        this.append = a;
        this.replaceExisting = r;
        this.rejectFile = rF;
        this.overwriteRejects = o;
        this.clearBackend = cB;
        this.includeAttributes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iA));
        this.excludeAttributes = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eA));
        this.includeBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iB));
        this.excludeBranches = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eB));
        this.includeFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])iF));
        this.excludeFilters = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])eF));
        this.isCompressed = c;
        this.isEncrypted = e;
        this.encryptionPassphraseFile = pF;
        this.skipSchemaValidation = ss;
        this.stripTrailingSpaces = st;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_IMPORT.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_IMPORT.get();
    }
    
    public List<String> getLDIFFiles() {
        return this.ldifFiles;
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    public boolean append() {
        return this.append;
    }
    
    public boolean replaceExistingEntries() {
        return this.replaceExisting;
    }
    
    public String getRejectFile() {
        return this.rejectFile;
    }
    
    public boolean overwriteRejectFile() {
        return this.overwriteRejects;
    }
    
    public boolean clearBackend() {
        return this.clearBackend;
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
    
    public boolean isCompressed() {
        return this.isCompressed;
    }
    
    public boolean isEncrypted() {
        return this.isEncrypted;
    }
    
    public String getEncryptionPassphraseFile() {
        return this.encryptionPassphraseFile;
    }
    
    public boolean skipSchemaValidation() {
        return this.skipSchemaValidation;
    }
    
    public boolean stripTrailingSpaces() {
        return this.stripTrailingSpaces;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-import");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(20);
        attrs.add(new Attribute("ds-task-import-ldif-file", this.ldifFiles));
        attrs.add(new Attribute("ds-task-import-append", String.valueOf(this.append)));
        attrs.add(new Attribute("ds-task-import-replace-existing", String.valueOf(this.replaceExisting)));
        attrs.add(new Attribute("ds-task-import-overwrite-rejects", String.valueOf(this.overwriteRejects)));
        attrs.add(new Attribute("ds-task-import-clear-backend", String.valueOf(this.clearBackend)));
        attrs.add(new Attribute("ds-task-import-is-compressed", String.valueOf(this.isCompressed)));
        attrs.add(new Attribute("ds-task-import-is-encrypted", String.valueOf(this.isEncrypted)));
        attrs.add(new Attribute("ds-task-import-skip-schema-validation", String.valueOf(this.skipSchemaValidation)));
        if (this.stripTrailingSpaces) {
            attrs.add(new Attribute("ds-task-import-strip-trailing-spaces", String.valueOf(this.stripTrailingSpaces)));
        }
        if (this.backendID != null) {
            attrs.add(new Attribute("ds-task-import-backend-id", this.backendID));
        }
        if (this.rejectFile != null) {
            attrs.add(new Attribute("ds-task-import-reject-file", this.rejectFile));
        }
        if (!this.includeBranches.isEmpty()) {
            attrs.add(new Attribute("ds-task-import-include-branch", this.includeBranches));
        }
        if (!this.excludeBranches.isEmpty()) {
            attrs.add(new Attribute("ds-task-import-exclude-branch", this.excludeBranches));
        }
        if (!this.includeAttributes.isEmpty()) {
            attrs.add(new Attribute("ds-task-import-include-attribute", this.includeAttributes));
        }
        if (!this.excludeAttributes.isEmpty()) {
            attrs.add(new Attribute("ds-task-import-exclude-attribute", this.excludeAttributes));
        }
        if (!this.includeFilters.isEmpty()) {
            attrs.add(new Attribute("ds-task-import-include-filter", this.includeFilters));
        }
        if (!this.excludeFilters.isEmpty()) {
            attrs.add(new Attribute("ds-task-import-exclude-filter", this.excludeFilters));
        }
        if (this.encryptionPassphraseFile != null) {
            attrs.add(new Attribute("ds-task-import-encryption-passphrase-file", this.encryptionPassphraseFile));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(ImportTask.PROPERTY_BACKEND_ID, ImportTask.PROPERTY_LDIF_FILE, ImportTask.PROPERTY_APPEND, ImportTask.PROPERTY_REPLACE_EXISTING, ImportTask.PROPERTY_REJECT_FILE, ImportTask.PROPERTY_OVERWRITE_REJECTS, ImportTask.PROPERTY_CLEAR_BACKEND, ImportTask.PROPERTY_INCLUDE_BRANCH, ImportTask.PROPERTY_EXCLUDE_BRANCH, ImportTask.PROPERTY_INCLUDE_FILTER, ImportTask.PROPERTY_EXCLUDE_FILTER, ImportTask.PROPERTY_INCLUDE_ATTRIBUTE, ImportTask.PROPERTY_EXCLUDE_ATTRIBUTE, ImportTask.PROPERTY_IS_COMPRESSED, ImportTask.PROPERTY_IS_ENCRYPTED, ImportTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, ImportTask.PROPERTY_SKIP_SCHEMA_VALIDATION, ImportTask.PROPERTY_STRIP_TRAILING_SPACES);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(20));
        if (this.backendID == null) {
            props.put(ImportTask.PROPERTY_BACKEND_ID, Collections.emptyList());
        }
        else {
            props.put(ImportTask.PROPERTY_BACKEND_ID, (List<Object>)Collections.singletonList(this.backendID));
        }
        props.put(ImportTask.PROPERTY_LDIF_FILE, Collections.unmodifiableList((List<?>)this.ldifFiles));
        props.put(ImportTask.PROPERTY_APPEND, (List<Object>)Collections.singletonList(this.append));
        props.put(ImportTask.PROPERTY_REPLACE_EXISTING, (List<Object>)Collections.singletonList(this.replaceExisting));
        if (this.rejectFile == null) {
            props.put(ImportTask.PROPERTY_REJECT_FILE, Collections.emptyList());
        }
        else {
            props.put(ImportTask.PROPERTY_REJECT_FILE, (List<Object>)Collections.singletonList(this.rejectFile));
        }
        props.put(ImportTask.PROPERTY_OVERWRITE_REJECTS, (List<Object>)Collections.singletonList(this.overwriteRejects));
        props.put(ImportTask.PROPERTY_CLEAR_BACKEND, (List<Object>)Collections.singletonList(this.clearBackend));
        props.put(ImportTask.PROPERTY_INCLUDE_BRANCH, Collections.unmodifiableList((List<?>)this.includeBranches));
        props.put(ImportTask.PROPERTY_EXCLUDE_BRANCH, Collections.unmodifiableList((List<?>)this.excludeBranches));
        props.put(ImportTask.PROPERTY_INCLUDE_FILTER, Collections.unmodifiableList((List<?>)this.includeFilters));
        props.put(ImportTask.PROPERTY_EXCLUDE_FILTER, Collections.unmodifiableList((List<?>)this.excludeFilters));
        props.put(ImportTask.PROPERTY_INCLUDE_ATTRIBUTE, Collections.unmodifiableList((List<?>)this.includeAttributes));
        props.put(ImportTask.PROPERTY_EXCLUDE_ATTRIBUTE, Collections.unmodifiableList((List<?>)this.excludeAttributes));
        props.put(ImportTask.PROPERTY_IS_COMPRESSED, (List<Object>)Collections.singletonList(this.isCompressed));
        props.put(ImportTask.PROPERTY_IS_ENCRYPTED, (List<Object>)Collections.singletonList(this.isEncrypted));
        if (this.encryptionPassphraseFile == null) {
            props.put(ImportTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, Collections.emptyList());
        }
        else {
            props.put(ImportTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, (List<Object>)Collections.singletonList(this.encryptionPassphraseFile));
        }
        props.put(ImportTask.PROPERTY_SKIP_SCHEMA_VALIDATION, (List<Object>)Collections.singletonList(this.skipSchemaValidation));
        props.put(ImportTask.PROPERTY_STRIP_TRAILING_SPACES, (List<Object>)Collections.singletonList(this.stripTrailingSpaces));
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKEND_ID = new TaskProperty("ds-task-import-backend-id", TaskMessages.INFO_DISPLAY_NAME_BACKEND_ID.get(), TaskMessages.INFO_DESCRIPTION_BACKEND_ID_IMPORT.get(), String.class, false, false, false);
        PROPERTY_LDIF_FILE = new TaskProperty("ds-task-import-ldif-file", TaskMessages.INFO_DISPLAY_NAME_LDIF_FILE.get(), TaskMessages.INFO_DESCRIPTION_LDIF_FILE_IMPORT.get(), String.class, true, true, false);
        PROPERTY_APPEND = new TaskProperty("ds-task-import-append", TaskMessages.INFO_DISPLAY_NAME_APPEND_TO_DB.get(), TaskMessages.INFO_DESCRIPTION_APPEND_TO_DB.get(), Boolean.class, false, false, true);
        PROPERTY_REPLACE_EXISTING = new TaskProperty("ds-task-import-replace-existing", TaskMessages.INFO_DISPLAY_NAME_REPLACE_EXISTING.get(), TaskMessages.INFO_DESCRIPTION_REPLACE_EXISTING.get(), Boolean.class, false, false, true);
        PROPERTY_REJECT_FILE = new TaskProperty("ds-task-import-reject-file", TaskMessages.INFO_DISPLAY_NAME_REJECT_FILE.get(), TaskMessages.INFO_DESCRIPTION_REJECT_FILE.get(), String.class, false, false, false);
        PROPERTY_OVERWRITE_REJECTS = new TaskProperty("ds-task-import-overwrite-rejects", TaskMessages.INFO_DISPLAY_NAME_OVERWRITE_REJECTS.get(), TaskMessages.INFO_DESCRIPTION_OVERWRITE_REJECTS.get(), Boolean.class, false, false, true);
        PROPERTY_CLEAR_BACKEND = new TaskProperty("ds-task-import-clear-backend", TaskMessages.INFO_DISPLAY_NAME_CLEAR_BACKEND.get(), TaskMessages.INFO_DESCRIPTION_CLEAR_BACKEND.get(), Boolean.class, false, false, true);
        PROPERTY_INCLUDE_BRANCH = new TaskProperty("ds-task-import-include-branch", TaskMessages.INFO_DISPLAY_NAME_INCLUDE_BRANCH.get(), TaskMessages.INFO_DESCRIPTION_INCLUDE_BRANCH_IMPORT.get(), String.class, false, true, true);
        PROPERTY_EXCLUDE_BRANCH = new TaskProperty("ds-task-import-exclude-branch", TaskMessages.INFO_DISPLAY_NAME_EXCLUDE_BRANCH.get(), TaskMessages.INFO_DESCRIPTION_EXCLUDE_BRANCH_IMPORT.get(), String.class, false, true, true);
        PROPERTY_INCLUDE_FILTER = new TaskProperty("ds-task-import-include-filter", TaskMessages.INFO_DISPLAY_NAME_INCLUDE_FILTER.get(), TaskMessages.INFO_DESCRIPTION_INCLUDE_FILTER_IMPORT.get(), String.class, false, true, true);
        PROPERTY_EXCLUDE_FILTER = new TaskProperty("ds-task-import-exclude-filter", TaskMessages.INFO_DISPLAY_NAME_EXCLUDE_FILTER.get(), TaskMessages.INFO_DESCRIPTION_EXCLUDE_FILTER_IMPORT.get(), String.class, false, true, true);
        PROPERTY_INCLUDE_ATTRIBUTE = new TaskProperty("ds-task-import-include-attribute", TaskMessages.INFO_DISPLAY_NAME_INCLUDE_ATTRIBUTE.get(), TaskMessages.INFO_DESCRIPTION_INCLUDE_ATTRIBUTE_IMPORT.get(), String.class, false, true, true);
        PROPERTY_EXCLUDE_ATTRIBUTE = new TaskProperty("ds-task-import-exclude-attribute", TaskMessages.INFO_DISPLAY_NAME_EXCLUDE_ATTRIBUTE.get(), TaskMessages.INFO_DESCRIPTION_EXCLUDE_ATTRIBUTE_IMPORT.get(), String.class, false, true, true);
        PROPERTY_IS_COMPRESSED = new TaskProperty("ds-task-import-is-compressed", TaskMessages.INFO_DISPLAY_NAME_IS_COMPRESSED_IMPORT.get(), TaskMessages.INFO_DESCRIPTION_IS_COMPRESSED_IMPORT.get(), Boolean.class, false, false, false);
        PROPERTY_IS_ENCRYPTED = new TaskProperty("ds-task-import-is-encrypted", TaskMessages.INFO_DISPLAY_NAME_IS_ENCRYPTED_IMPORT.get(), TaskMessages.INFO_DESCRIPTION_IS_ENCRYPTED_IMPORT.get(), Boolean.class, false, false, false);
        PROPERTY_ENCRYPTION_PASSPHRASE_FILE = new TaskProperty("ds-task-import-encryption-passphrase-file", TaskMessages.INFO_DISPLAY_NAME_ENCRYPTION_PASSPHRASE_FILE.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPTION_PASSPHRASE_FILE.get(), String.class, false, false, true);
        PROPERTY_SKIP_SCHEMA_VALIDATION = new TaskProperty("ds-task-import-skip-schema-validation", TaskMessages.INFO_DISPLAY_NAME_SKIP_SCHEMA_VALIDATION.get(), TaskMessages.INFO_DESCRIPTION_SKIP_SCHEMA_VALIDATION.get(), Boolean.class, false, false, false);
        PROPERTY_STRIP_TRAILING_SPACES = new TaskProperty("ds-task-import-strip-trailing-spaces", TaskMessages.INFO_DISPLAY_NAME_STRIP_TRAILING_SPACES.get(), TaskMessages.INFO_DESCRIPTION_STRIP_TRAILING_SPACES.get(), Boolean.class, false, false, false);
    }
}
