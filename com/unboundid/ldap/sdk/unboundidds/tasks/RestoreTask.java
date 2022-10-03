package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Arrays;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RestoreTask extends Task
{
    static final String RESTORE_TASK_CLASS = "com.unboundid.directory.server.tasks.RestoreTask";
    private static final String ATTR_BACKUP_DIRECTORY = "ds-backup-directory-path";
    private static final String ATTR_BACKUP_ID = "ds-backup-id";
    private static final String ATTR_ENCRYPTION_PASSPHRASE_FILE = "ds-task-restore-encryption-passphrase-file";
    private static final String ATTR_VERIFY_ONLY = "ds-task-restore-verify-only";
    private static final String OC_RESTORE_TASK = "ds-task-restore";
    private static final TaskProperty PROPERTY_BACKUP_DIRECTORY;
    private static final TaskProperty PROPERTY_BACKUP_ID;
    private static final TaskProperty PROPERTY_ENCRYPTION_PASSPHRASE_FILE;
    private static final TaskProperty PROPERTY_VERIFY_ONLY;
    private static final long serialVersionUID = -8441221098187125379L;
    private final boolean verifyOnly;
    private final String backupDirectory;
    private final String encryptionPassphraseFile;
    private final String backupID;
    
    public RestoreTask() {
        this.verifyOnly = false;
        this.backupDirectory = null;
        this.backupID = null;
        this.encryptionPassphraseFile = null;
    }
    
    public RestoreTask(final String taskID, final String backupDirectory, final String backupID, final boolean verifyOnly) {
        this(taskID, backupDirectory, backupID, verifyOnly, null, null, null, null, null);
    }
    
    public RestoreTask(final String taskID, final String backupDirectory, final String backupID, final boolean verifyOnly, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backupDirectory, backupID, verifyOnly, null, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError);
    }
    
    public RestoreTask(final String taskID, final String backupDirectory, final String backupID, final boolean verifyOnly, final String encryptionPassphraseFile, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backupDirectory, backupID, verifyOnly, encryptionPassphraseFile, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public RestoreTask(final String taskID, final String backupDirectory, final String backupID, final boolean verifyOnly, final String encryptionPassphraseFile, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.RestoreTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(backupDirectory);
        this.backupDirectory = backupDirectory;
        this.backupID = backupID;
        this.verifyOnly = verifyOnly;
        this.encryptionPassphraseFile = encryptionPassphraseFile;
    }
    
    public RestoreTask(final Entry entry) throws TaskException {
        super(entry);
        this.backupDirectory = entry.getAttributeValue("ds-backup-directory-path");
        if (this.backupDirectory == null) {
            throw new TaskException(TaskMessages.ERR_RESTORE_NO_BACKUP_DIRECTORY.get(this.getTaskEntryDN()));
        }
        this.backupID = entry.getAttributeValue("ds-backup-id");
        this.verifyOnly = Task.parseBooleanValue(entry, "ds-task-restore-verify-only", false);
        this.encryptionPassphraseFile = entry.getAttributeValue("ds-task-restore-encryption-passphrase-file");
    }
    
    public RestoreTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.RestoreTask", properties);
        boolean v = false;
        String b = null;
        String f = null;
        String i = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-backup-directory-path")) {
                b = Task.parseString(p, values, b);
            }
            else if (attrName.equalsIgnoreCase("ds-backup-id")) {
                i = Task.parseString(p, values, i);
            }
            else if (attrName.equalsIgnoreCase("ds-task-restore-verify-only")) {
                v = Task.parseBoolean(p, values, v);
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-restore-encryption-passphrase-file")) {
                    continue;
                }
                f = Task.parseString(p, values, f);
            }
        }
        if (b == null) {
            throw new TaskException(TaskMessages.ERR_RESTORE_NO_BACKUP_DIRECTORY.get(this.getTaskEntryDN()));
        }
        this.backupDirectory = b;
        this.backupID = i;
        this.verifyOnly = v;
        this.encryptionPassphraseFile = f;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_RESTORE.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_RESTORE.get();
    }
    
    public String getBackupDirectory() {
        return this.backupDirectory;
    }
    
    public String getBackupID() {
        return this.backupID;
    }
    
    public boolean verifyOnly() {
        return this.verifyOnly;
    }
    
    public String getEncryptionPassphraseFile() {
        return this.encryptionPassphraseFile;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-restore");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(10);
        attrs.add(new Attribute("ds-backup-directory-path", this.backupDirectory));
        attrs.add(new Attribute("ds-task-restore-verify-only", String.valueOf(this.verifyOnly)));
        if (this.backupID != null) {
            attrs.add(new Attribute("ds-backup-id", this.backupID));
        }
        if (this.encryptionPassphraseFile != null) {
            attrs.add(new Attribute("ds-task-restore-encryption-passphrase-file", this.encryptionPassphraseFile));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(RestoreTask.PROPERTY_BACKUP_DIRECTORY, RestoreTask.PROPERTY_BACKUP_ID, RestoreTask.PROPERTY_VERIFY_ONLY, RestoreTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(10));
        props.put(RestoreTask.PROPERTY_BACKUP_DIRECTORY, (List<Object>)Collections.singletonList(this.backupDirectory));
        if (this.backupID == null) {
            props.put(RestoreTask.PROPERTY_BACKUP_ID, Collections.emptyList());
        }
        else {
            props.put(RestoreTask.PROPERTY_BACKUP_ID, (List<Object>)Collections.singletonList(this.backupID));
        }
        props.put(RestoreTask.PROPERTY_VERIFY_ONLY, (List<Object>)Collections.singletonList(this.verifyOnly));
        if (this.encryptionPassphraseFile == null) {
            props.put(RestoreTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, Collections.emptyList());
        }
        else {
            props.put(RestoreTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, (List<Object>)Collections.singletonList(this.encryptionPassphraseFile));
        }
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKUP_DIRECTORY = new TaskProperty("ds-backup-directory-path", TaskMessages.INFO_DISPLAY_NAME_BACKUP_DIRECTORY.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_DIRECTORY_RESTORE.get(), String.class, true, false, false);
        PROPERTY_BACKUP_ID = new TaskProperty("ds-backup-id", TaskMessages.INFO_DISPLAY_NAME_BACKUP_ID.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_ID_RESTORE.get(), String.class, false, false, true);
        PROPERTY_ENCRYPTION_PASSPHRASE_FILE = new TaskProperty("ds-task-restore-encryption-passphrase-file", TaskMessages.INFO_DISPLAY_NAME_ENCRYPTION_PASSPHRASE_FILE.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPTION_PASSPHRASE_FILE.get(), String.class, false, false, true);
        PROPERTY_VERIFY_ONLY = new TaskProperty("ds-task-restore-verify-only", TaskMessages.INFO_DISPLAY_NAME_VERIFY_ONLY.get(), TaskMessages.INFO_DESCRIPTION_VERIFY_ONLY.get(), Boolean.class, false, false, false);
    }
}
