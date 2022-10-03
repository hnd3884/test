package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.Arrays;
import com.unboundid.util.StaticUtils;
import java.util.Map;
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
public final class BackupTask extends Task
{
    static final String BACKUP_TASK_CLASS = "com.unboundid.directory.server.tasks.BackupTask";
    private static final String ATTR_BACKEND_ID = "ds-task-backup-backend-id";
    private static final String ATTR_BACKUP_ALL = "ds-task-backup-all";
    private static final String ATTR_BACKUP_DIRECTORY = "ds-backup-directory-path";
    private static final String ATTR_BACKUP_ID = "ds-backup-id";
    private static final String ATTR_COMPRESS = "ds-task-backup-compress";
    private static final String ATTR_ENCRYPT = "ds-task-backup-encrypt";
    private static final String ATTR_ENCRYPTION_PASSPHRASE_FILE = "ds-task-backup-encryption-passphrase-file";
    private static final String ATTR_ENCRYPTION_SETTINGS_DEFINITION_ID = "ds-task-backup-encryption-settings-definition-id";
    private static final String ATTR_HASH = "ds-task-backup-hash";
    private static final String ATTR_INCREMENTAL = "ds-task-backup-incremental";
    private static final String ATTR_INCREMENTAL_BASE_ID = "ds-task-backup-incremental-base-id";
    private static final String ATTR_MAX_MEGABYTES_PER_SECOND = "ds-task-backup-max-megabytes-per-second";
    private static final String ATTR_RETAIN_PREVIOUS_FULL_BACKUP_AGE = "ds-task-backup-retain-previous-full-backup-age";
    private static final String ATTR_RETAIN_PREVIOUS_FULL_BACKUP_COUNT = "ds-task-backup-retain-previous-full-backup-count";
    private static final String ATTR_SIGN_HASH = "ds-task-backup-sign-hash";
    private static final String OC_BACKUP_TASK = "ds-task-backup";
    private static final TaskProperty PROPERTY_BACKUP_DIRECTORY;
    private static final TaskProperty PROPERTY_BACKEND_ID;
    private static final TaskProperty PROPERTY_BACKUP_ID;
    private static final TaskProperty PROPERTY_INCREMENTAL;
    private static final TaskProperty PROPERTY_INCREMENTAL_BASE_ID;
    private static final TaskProperty PROPERTY_COMPRESS;
    private static final TaskProperty PROPERTY_ENCRYPT;
    private static final TaskProperty PROPERTY_ENCRYPTION_PASSPHRASE_FILE;
    private static final TaskProperty PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID;
    private static final TaskProperty PROPERTY_HASH;
    private static final TaskProperty PROPERTY_SIGN_HASH;
    private static final TaskProperty PROPERTY_MAX_MEGABYTES_PER_SECOND;
    private static final TaskProperty PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_AGE;
    private static final TaskProperty PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_COUNT;
    private static final long serialVersionUID = 2637190942057174423L;
    private final boolean compress;
    private final boolean encrypt;
    private final boolean hash;
    private final boolean signHash;
    private final boolean incremental;
    private final Integer maxMegabytesPerSecond;
    private final Integer retainPreviousFullBackupCount;
    private final List<String> backendIDs;
    private final String backupDirectory;
    private final String backupID;
    private final String encryptionPassphraseFile;
    private final String encryptionSettingsDefinitionID;
    private final String incrementalBaseID;
    private final String retainPreviousFullBackupAge;
    
    public BackupTask() {
        this.compress = false;
        this.encrypt = false;
        this.hash = false;
        this.signHash = false;
        this.incremental = false;
        this.maxMegabytesPerSecond = null;
        this.retainPreviousFullBackupCount = null;
        this.backendIDs = null;
        this.backupDirectory = null;
        this.backupID = null;
        this.encryptionPassphraseFile = null;
        this.encryptionSettingsDefinitionID = null;
        this.incrementalBaseID = null;
        this.retainPreviousFullBackupAge = null;
    }
    
    public BackupTask(final String taskID, final String backupDirectory, final String backendID) {
        this(taskID, backupDirectory, (backendID == null) ? null : Collections.singletonList(backendID), null, false, null, false, false, false, false, null, null, null, null, null);
    }
    
    public BackupTask(final String taskID, final String backupDirectory, final List<String> backendIDs, final String backupID, final boolean incremental, final String incrementalBaseID, final boolean compress, final boolean encrypt, final boolean hash, final boolean signHash, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backupDirectory, backendIDs, backupID, incremental, incrementalBaseID, compress, encrypt, null, null, hash, signHash, null, null, null, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnCompletion, notifyOnError);
    }
    
    public BackupTask(final String taskID, final String backupDirectory, final List<String> backendIDs, final String backupID, final boolean incremental, final String incrementalBaseID, final boolean compress, final boolean encrypt, final String encryptionPassphraseFile, final String encryptionSettingsDefinitionID, final boolean hash, final boolean signHash, final Integer maxMegabytesPerSecond, final Integer retainPreviousFullBackupCount, final String retainPreviousFullBackupAge, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) {
        this(taskID, backupDirectory, backendIDs, backupID, incremental, incrementalBaseID, compress, encrypt, encryptionPassphraseFile, encryptionSettingsDefinitionID, hash, signHash, maxMegabytesPerSecond, retainPreviousFullBackupCount, retainPreviousFullBackupAge, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public BackupTask(final String taskID, final String backupDirectory, final List<String> backendIDs, final String backupID, final boolean incremental, final String incrementalBaseID, final boolean compress, final boolean encrypt, final String encryptionPassphraseFile, final String encryptionSettingsDefinitionID, final boolean hash, final boolean signHash, final Integer maxMegabytesPerSecond, final Integer retainPreviousFullBackupCount, final String retainPreviousFullBackupAge, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.BackupTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNull(backupDirectory);
        this.backupDirectory = backupDirectory;
        this.backupID = backupID;
        this.incremental = incremental;
        this.incrementalBaseID = incrementalBaseID;
        this.compress = compress;
        this.encrypt = encrypt;
        this.encryptionPassphraseFile = encryptionPassphraseFile;
        this.encryptionSettingsDefinitionID = encryptionSettingsDefinitionID;
        this.hash = hash;
        this.signHash = signHash;
        this.maxMegabytesPerSecond = maxMegabytesPerSecond;
        this.retainPreviousFullBackupCount = retainPreviousFullBackupCount;
        this.retainPreviousFullBackupAge = retainPreviousFullBackupAge;
        if (backendIDs == null) {
            this.backendIDs = Collections.emptyList();
        }
        else {
            this.backendIDs = Collections.unmodifiableList((List<? extends String>)backendIDs);
        }
    }
    
    public BackupTask(final Entry entry) throws TaskException {
        super(entry);
        this.backupDirectory = entry.getAttributeValue("ds-backup-directory-path");
        if (this.backupDirectory == null) {
            throw new TaskException(TaskMessages.ERR_BACKUP_NO_BACKUP_DIRECTORY.get(this.getTaskEntryDN()));
        }
        this.backendIDs = Task.parseStringList(entry, "ds-task-backup-backend-id");
        this.backupID = entry.getAttributeValue("ds-backup-id");
        this.incremental = Task.parseBooleanValue(entry, "ds-task-backup-incremental", false);
        this.incrementalBaseID = entry.getAttributeValue("ds-task-backup-incremental-base-id");
        this.compress = Task.parseBooleanValue(entry, "ds-task-backup-compress", false);
        this.encrypt = Task.parseBooleanValue(entry, "ds-task-backup-encrypt", false);
        this.encryptionPassphraseFile = entry.getAttributeValue("ds-task-backup-encryption-passphrase-file");
        this.encryptionSettingsDefinitionID = entry.getAttributeValue("ds-task-backup-encryption-settings-definition-id");
        this.hash = Task.parseBooleanValue(entry, "ds-task-backup-hash", false);
        this.signHash = Task.parseBooleanValue(entry, "ds-task-backup-sign-hash", false);
        this.maxMegabytesPerSecond = entry.getAttributeValueAsInteger("ds-task-backup-max-megabytes-per-second");
        this.retainPreviousFullBackupCount = entry.getAttributeValueAsInteger("ds-task-backup-retain-previous-full-backup-count");
        this.retainPreviousFullBackupAge = entry.getAttributeValue("ds-task-backup-retain-previous-full-backup-age");
    }
    
    public BackupTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.BackupTask", properties);
        boolean c = false;
        boolean e = false;
        boolean h = false;
        boolean i = false;
        boolean s = false;
        Integer maxMB = null;
        Integer retainCount = null;
        String bDir = null;
        String bkID = null;
        String incID = null;
        String encID = null;
        String encPWFile = null;
        String retainAge = null;
        String[] beIDs = StaticUtils.NO_STRINGS;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = p.getAttributeName();
            final List<Object> values = entry.getValue();
            if (attrName.equalsIgnoreCase("ds-backup-directory-path")) {
                bDir = Task.parseString(p, values, bDir);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-backend-id")) {
                beIDs = Task.parseStrings(p, values, beIDs);
            }
            else if (attrName.equalsIgnoreCase("ds-backup-id")) {
                bkID = Task.parseString(p, values, bkID);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-incremental")) {
                i = Task.parseBoolean(p, values, i);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-incremental-base-id")) {
                incID = Task.parseString(p, values, incID);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-compress")) {
                c = Task.parseBoolean(p, values, c);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-encrypt")) {
                e = Task.parseBoolean(p, values, e);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-encryption-passphrase-file")) {
                encPWFile = Task.parseString(p, values, encPWFile);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-encryption-settings-definition-id")) {
                encID = Task.parseString(p, values, encID);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-hash")) {
                h = Task.parseBoolean(p, values, h);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-sign-hash")) {
                s = Task.parseBoolean(p, values, s);
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-max-megabytes-per-second")) {
                final Long maxMBLong = Task.parseLong(p, values, null);
                if (maxMBLong == null) {
                    maxMB = null;
                }
                else {
                    maxMB = maxMBLong.intValue();
                }
            }
            else if (attrName.equalsIgnoreCase("ds-task-backup-retain-previous-full-backup-count")) {
                final Long retainCountLong = Task.parseLong(p, values, null);
                if (retainCountLong == null) {
                    retainCount = null;
                }
                else {
                    retainCount = retainCountLong.intValue();
                }
            }
            else {
                if (!attrName.equalsIgnoreCase("ds-task-backup-retain-previous-full-backup-age")) {
                    continue;
                }
                retainAge = Task.parseString(p, values, retainAge);
            }
        }
        if (bDir == null) {
            throw new TaskException(TaskMessages.ERR_BACKUP_NO_BACKUP_DIRECTORY.get(this.getTaskEntryDN()));
        }
        this.backupDirectory = bDir;
        this.backendIDs = Arrays.asList(beIDs);
        this.backupID = bkID;
        this.incremental = i;
        this.incrementalBaseID = incID;
        this.compress = c;
        this.encrypt = e;
        this.encryptionPassphraseFile = encPWFile;
        this.encryptionSettingsDefinitionID = encID;
        this.hash = h;
        this.signHash = s;
        this.maxMegabytesPerSecond = maxMB;
        this.retainPreviousFullBackupCount = retainCount;
        this.retainPreviousFullBackupAge = retainAge;
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_BACKUP.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_BACKUP.get();
    }
    
    public String getBackupDirectory() {
        return this.backupDirectory;
    }
    
    public boolean backupAll() {
        return this.backendIDs.isEmpty();
    }
    
    public List<String> getBackendIDs() {
        return this.backendIDs;
    }
    
    public String getBackupID() {
        return this.backupID;
    }
    
    public boolean incremental() {
        return this.incremental;
    }
    
    public String getIncrementalBaseID() {
        return this.incrementalBaseID;
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
    
    public boolean hash() {
        return this.hash;
    }
    
    public boolean signHash() {
        return this.signHash;
    }
    
    public Integer getMaxMegabytesPerSecond() {
        return this.maxMegabytesPerSecond;
    }
    
    public Integer getRetainPreviousFullBackupCount() {
        return this.retainPreviousFullBackupCount;
    }
    
    public String getRetainPreviousFullBackupAge() {
        return this.retainPreviousFullBackupAge;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-backup");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final ArrayList<Attribute> attrs = new ArrayList<Attribute>(20);
        attrs.add(new Attribute("ds-backup-directory-path", this.backupDirectory));
        attrs.add(new Attribute("ds-task-backup-incremental", String.valueOf(this.incremental)));
        attrs.add(new Attribute("ds-task-backup-compress", String.valueOf(this.compress)));
        attrs.add(new Attribute("ds-task-backup-encrypt", String.valueOf(this.encrypt)));
        attrs.add(new Attribute("ds-task-backup-hash", String.valueOf(this.hash)));
        attrs.add(new Attribute("ds-task-backup-sign-hash", String.valueOf(this.signHash)));
        if (this.backendIDs.isEmpty()) {
            attrs.add(new Attribute("ds-task-backup-all", "true"));
        }
        else {
            attrs.add(new Attribute("ds-task-backup-backend-id", this.backendIDs));
        }
        if (this.backupID != null) {
            attrs.add(new Attribute("ds-backup-id", this.backupID));
        }
        if (this.incrementalBaseID != null) {
            attrs.add(new Attribute("ds-task-backup-incremental-base-id", this.incrementalBaseID));
        }
        if (this.encryptionPassphraseFile != null) {
            attrs.add(new Attribute("ds-task-backup-encryption-passphrase-file", this.encryptionPassphraseFile));
        }
        if (this.encryptionSettingsDefinitionID != null) {
            attrs.add(new Attribute("ds-task-backup-encryption-settings-definition-id", this.encryptionSettingsDefinitionID));
        }
        if (this.maxMegabytesPerSecond != null) {
            attrs.add(new Attribute("ds-task-backup-max-megabytes-per-second", String.valueOf(this.maxMegabytesPerSecond)));
        }
        if (this.retainPreviousFullBackupCount != null) {
            attrs.add(new Attribute("ds-task-backup-retain-previous-full-backup-count", String.valueOf(this.retainPreviousFullBackupCount)));
        }
        if (this.retainPreviousFullBackupAge != null) {
            attrs.add(new Attribute("ds-task-backup-retain-previous-full-backup-age", this.retainPreviousFullBackupAge));
        }
        return attrs;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        final List<TaskProperty> propList = Arrays.asList(BackupTask.PROPERTY_BACKUP_DIRECTORY, BackupTask.PROPERTY_BACKEND_ID, BackupTask.PROPERTY_BACKUP_ID, BackupTask.PROPERTY_INCREMENTAL, BackupTask.PROPERTY_INCREMENTAL_BASE_ID, BackupTask.PROPERTY_COMPRESS, BackupTask.PROPERTY_ENCRYPT, BackupTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, BackupTask.PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID, BackupTask.PROPERTY_HASH, BackupTask.PROPERTY_SIGN_HASH, BackupTask.PROPERTY_MAX_MEGABYTES_PER_SECOND, BackupTask.PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_COUNT, BackupTask.PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_AGE);
        return Collections.unmodifiableList((List<? extends TaskProperty>)propList);
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(20));
        props.put(BackupTask.PROPERTY_BACKUP_DIRECTORY, (List<Object>)Collections.singletonList(this.backupDirectory));
        props.put(BackupTask.PROPERTY_BACKEND_ID, Collections.unmodifiableList((List<?>)this.backendIDs));
        if (this.backupID == null) {
            props.put(BackupTask.PROPERTY_BACKUP_ID, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_BACKUP_ID, (List<Object>)Collections.singletonList(this.backupID));
        }
        props.put(BackupTask.PROPERTY_INCREMENTAL, (List<Object>)Collections.singletonList(this.incremental));
        if (this.incrementalBaseID == null) {
            props.put(BackupTask.PROPERTY_INCREMENTAL_BASE_ID, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_INCREMENTAL_BASE_ID, (List<Object>)Collections.singletonList(this.incrementalBaseID));
        }
        props.put(BackupTask.PROPERTY_COMPRESS, (List<Object>)Collections.singletonList(this.compress));
        props.put(BackupTask.PROPERTY_ENCRYPT, (List<Object>)Collections.singletonList(this.encrypt));
        if (this.encryptionPassphraseFile == null) {
            props.put(BackupTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_ENCRYPTION_PASSPHRASE_FILE, (List<Object>)Collections.singletonList(this.encryptionPassphraseFile));
        }
        if (this.encryptionSettingsDefinitionID == null) {
            props.put(BackupTask.PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID, (List<Object>)Collections.singletonList(this.encryptionSettingsDefinitionID));
        }
        props.put(BackupTask.PROPERTY_HASH, (List<Object>)Collections.singletonList(this.hash));
        props.put(BackupTask.PROPERTY_SIGN_HASH, (List<Object>)Collections.singletonList(this.signHash));
        if (this.maxMegabytesPerSecond == null) {
            props.put(BackupTask.PROPERTY_MAX_MEGABYTES_PER_SECOND, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_MAX_MEGABYTES_PER_SECOND, (List<Object>)Collections.singletonList((long)this.maxMegabytesPerSecond));
        }
        if (this.retainPreviousFullBackupCount == null) {
            props.put(BackupTask.PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_COUNT, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_COUNT, (List<Object>)Collections.singletonList((long)this.retainPreviousFullBackupCount));
        }
        if (this.retainPreviousFullBackupAge == null) {
            props.put(BackupTask.PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_AGE, Collections.emptyList());
        }
        else {
            props.put(BackupTask.PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_AGE, (List<Object>)Collections.singletonList(this.retainPreviousFullBackupAge));
        }
        props.putAll((Map<?, ?>)super.getTaskPropertyValues());
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_BACKUP_DIRECTORY = new TaskProperty("ds-backup-directory-path", TaskMessages.INFO_DISPLAY_NAME_BACKUP_DIRECTORY.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_DIRECTORY_BACKUP.get(), String.class, true, false, false);
        PROPERTY_BACKEND_ID = new TaskProperty("ds-task-backup-backend-id", TaskMessages.INFO_DISPLAY_NAME_BACKEND_ID.get(), TaskMessages.INFO_DESCRIPTION_BACKEND_ID_BACKUP.get(), String.class, false, true, false);
        PROPERTY_BACKUP_ID = new TaskProperty("ds-backup-id", TaskMessages.INFO_DISPLAY_NAME_BACKUP_ID.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_ID_BACKUP.get(), String.class, false, false, true);
        PROPERTY_INCREMENTAL = new TaskProperty("ds-task-backup-incremental", TaskMessages.INFO_DISPLAY_NAME_INCREMENTAL.get(), TaskMessages.INFO_DESCRIPTION_INCREMENTAL.get(), Boolean.class, false, false, false);
        PROPERTY_INCREMENTAL_BASE_ID = new TaskProperty("ds-task-backup-incremental-base-id", TaskMessages.INFO_DISPLAY_NAME_INCREMENTAL_BASE_ID.get(), TaskMessages.INFO_DESCRIPTION_INCREMENTAL_BASE_ID.get(), String.class, false, false, true);
        PROPERTY_COMPRESS = new TaskProperty("ds-task-backup-compress", TaskMessages.INFO_DISPLAY_NAME_COMPRESS.get(), TaskMessages.INFO_DESCRIPTION_COMPRESS_BACKUP.get(), Boolean.class, false, false, false);
        PROPERTY_ENCRYPT = new TaskProperty("ds-task-backup-encrypt", TaskMessages.INFO_DISPLAY_NAME_ENCRYPT.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPT_BACKUP.get(), Boolean.class, false, false, false);
        PROPERTY_ENCRYPTION_PASSPHRASE_FILE = new TaskProperty("ds-task-backup-encryption-passphrase-file", TaskMessages.INFO_DISPLAY_NAME_ENCRYPTION_PASSPHRASE_FILE.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPTION_PASSPHRASE_FILE.get(), String.class, false, false, true);
        PROPERTY_ENCRYPTION_SETTINGS_DEFINITION_ID = new TaskProperty("ds-task-backup-encryption-settings-definition-id", TaskMessages.INFO_DISPLAY_NAME_ENCRYPTION_SETTINGS_DEFINITION_ID.get(), TaskMessages.INFO_DESCRIPTION_ENCRYPTION_SETTINGS_DEFINITION_ID.get(), String.class, false, false, true);
        PROPERTY_HASH = new TaskProperty("ds-task-backup-hash", TaskMessages.INFO_DISPLAY_NAME_HASH.get(), TaskMessages.INFO_DESCRIPTION_HASH_BACKUP.get(), Boolean.class, false, false, false);
        PROPERTY_SIGN_HASH = new TaskProperty("ds-task-backup-sign-hash", TaskMessages.INFO_DISPLAY_NAME_SIGN_HASH.get(), TaskMessages.INFO_DESCRIPTION_SIGN_HASH_BACKUP.get(), Boolean.class, false, false, false);
        PROPERTY_MAX_MEGABYTES_PER_SECOND = new TaskProperty("ds-task-backup-max-megabytes-per-second", TaskMessages.INFO_DISPLAY_NAME_BACKUP_MAX_MEGABYTES_PER_SECOND.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_MAX_MEGABYTES_PER_SECOND.get(), Long.class, false, false, true);
        PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_AGE = new TaskProperty("ds-task-backup-retain-previous-full-backup-age", TaskMessages.INFO_DISPLAY_NAME_BACKUP_RETAIN_AGE.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_RETAIN_AGE.get(), String.class, false, false, true);
        PROPERTY_RETAIN_PREVIOUS_FULL_BACKUP_COUNT = new TaskProperty("ds-task-backup-retain-previous-full-backup-count", TaskMessages.INFO_DISPLAY_NAME_BACKUP_RETAIN_COUNT.get(), TaskMessages.INFO_DESCRIPTION_BACKUP_RETAIN_COUNT.get(), Long.class, false, false, true);
    }
}
