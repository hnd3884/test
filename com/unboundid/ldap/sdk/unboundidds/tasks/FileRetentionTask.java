package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.DurationArgument;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.Validator;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FileRetentionTask extends Task
{
    static final String FILE_RETENTION_TASK_CLASS = "com.unboundid.directory.server.tasks.FileRetentionTask";
    private static final String ATTR_TARGET_DIRECTORY = "ds-task-file-retention-target-directory";
    private static final String ATTR_FILENAME_PATTERN = "ds-task-file-retention-filename-pattern";
    private static final String ATTR_TIMESTAMP_FORMAT = "ds-task-file-retention-timestamp-format";
    private static final String ATTR_RETAIN_FILE_COUNT = "ds-task-file-retention-retain-file-count";
    private static final String ATTR_RETAIN_FILE_AGE = "ds-task-file-retention-retain-file-age";
    private static final String ATTR_RETAIN_AGGREGATE_FILE_SIZE_BYTES = "ds-task-file-retention-retain-aggregate-file-size-bytes";
    private static final String OC_FILE_RETENTION_TASK = "ds-task-file-retention";
    private static final TaskProperty PROPERTY_TARGET_DIRECTORY;
    private static final TaskProperty PROPERTY_FILENAME_PATTERN;
    private static final TaskProperty PROPERTY_TIMESTAMP_FORMAT;
    private static final TaskProperty PROPERTY_RETAIN_FILE_COUNT;
    private static final TaskProperty PROPERTY_RETAIN_FILE_AGE_MILLIS;
    private static final TaskProperty PROPERTY_RETAIN_AGGREGATE_FILE_SIZE_BYTES;
    private static final long serialVersionUID = 7401251158315611295L;
    private final FileRetentionTaskTimestampFormat timestampFormat;
    private final Integer retainFileCount;
    private final Long retainAggregateFileSizeBytes;
    private final Long retainFileAgeMillis;
    private final String filenamePattern;
    private final String targetDirectory;
    
    public FileRetentionTask() {
        this.targetDirectory = null;
        this.filenamePattern = null;
        this.timestampFormat = null;
        this.retainFileCount = null;
        this.retainFileAgeMillis = null;
        this.retainAggregateFileSizeBytes = null;
    }
    
    public FileRetentionTask(final String targetDirectory, final String filenamePattern, final FileRetentionTaskTimestampFormat timestampFormat, final Integer retainFileCount, final Long retainFileAgeMillis, final Long retainAggregateFileSizeBytes) {
        this(null, targetDirectory, filenamePattern, timestampFormat, retainFileCount, retainFileAgeMillis, retainAggregateFileSizeBytes, null, null, null, null, null, null, null, null, null, null);
    }
    
    public FileRetentionTask(final String taskID, final String targetDirectory, final String filenamePattern, final FileRetentionTaskTimestampFormat timestampFormat, final Integer retainFileCount, final Long retainFileAgeMillis, final Long retainAggregateFileSizeBytes, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) {
        super(taskID, "com.unboundid.directory.server.tasks.FileRetentionTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        Validator.ensureNotNullOrEmpty(targetDirectory, "FileRetentionTask.targetDirectory must not be null or empty");
        Validator.ensureNotNullOrEmpty(filenamePattern, "FileRetentionTask.filenamePattern must not be null or empty");
        Validator.ensureNotNullWithMessage(timestampFormat, "FileRetentionTask.timestampFormat must not be null");
        Validator.ensureTrue(retainFileCount != null || retainFileAgeMillis != null || retainAggregateFileSizeBytes != null, "At least one of retainFileCount, retainFileAgeMillis, and retainAggregateFileSizeBytes must be non-null");
        Validator.ensureTrue(retainFileCount == null || retainFileCount >= 0, "FileRetentionTask.retainFileCount must not be negative");
        Validator.ensureTrue(retainFileAgeMillis == null || retainFileAgeMillis > 0L, "FileRetentionTask.retainFileAgeMillis must not be negative or zero");
        Validator.ensureTrue(retainAggregateFileSizeBytes == null || retainAggregateFileSizeBytes > 0L, "FileRetentionTask.retainAggregateFileSizeBytes must not be negative or zero");
        this.targetDirectory = targetDirectory;
        this.filenamePattern = filenamePattern;
        this.timestampFormat = timestampFormat;
        this.retainFileCount = retainFileCount;
        this.retainFileAgeMillis = retainFileAgeMillis;
        this.retainAggregateFileSizeBytes = retainAggregateFileSizeBytes;
    }
    
    public FileRetentionTask(final Entry entry) throws TaskException {
        super(entry);
        this.targetDirectory = entry.getAttributeValue("ds-task-file-retention-target-directory");
        if (this.targetDirectory == null || this.targetDirectory.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_MISSING_REQUIRED_ATTR.get(entry.getDN(), "ds-task-file-retention-target-directory"));
        }
        this.filenamePattern = entry.getAttributeValue("ds-task-file-retention-filename-pattern");
        if (this.filenamePattern == null || this.filenamePattern.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_MISSING_REQUIRED_ATTR.get(entry.getDN(), "ds-task-file-retention-filename-pattern"));
        }
        final String timestampFormatName = entry.getAttributeValue("ds-task-file-retention-timestamp-format");
        if (timestampFormatName == null) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_MISSING_REQUIRED_ATTR.get(entry.getDN(), "ds-task-file-retention-timestamp-format"));
        }
        this.timestampFormat = FileRetentionTaskTimestampFormat.forName(timestampFormatName);
        if (this.timestampFormat == null) {
            final StringBuilder validFormats = new StringBuilder();
            for (final FileRetentionTaskTimestampFormat f : FileRetentionTaskTimestampFormat.values()) {
                if (validFormats.length() > 0) {
                    validFormats.append(", ");
                }
                validFormats.append(f.name());
            }
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_INVALID_TIMESTAMP_FORMAT.get(entry.getDN(), timestampFormatName, validFormats.toString()));
        }
        final String retainFileCountString = entry.getAttributeValue("ds-task-file-retention-retain-file-count");
        if (retainFileCountString == null) {
            this.retainFileCount = null;
        }
        else {
            try {
                this.retainFileCount = Integer.parseInt(retainFileCountString);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_COUNT.get(entry.getDN(), retainFileCountString), e);
            }
            if (this.retainFileCount < 0) {
                throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_COUNT.get(entry.getDN(), retainFileCountString));
            }
        }
        final String retainFileAgeString = entry.getAttributeValue("ds-task-file-retention-retain-file-age");
        if (retainFileAgeString == null) {
            this.retainFileAgeMillis = null;
        }
        else {
            try {
                this.retainFileAgeMillis = DurationArgument.parseDuration(retainFileAgeString, TimeUnit.MILLISECONDS);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_AGE.get(entry.getDN(), retainFileAgeString, StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
        final String retainAggregateFileSizeBytesString = entry.getAttributeValue("ds-task-file-retention-retain-aggregate-file-size-bytes");
        if (retainAggregateFileSizeBytesString == null) {
            this.retainAggregateFileSizeBytes = null;
        }
        else {
            try {
                this.retainAggregateFileSizeBytes = Long.parseLong(retainAggregateFileSizeBytesString);
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_SIZE.get(entry.getDN(), retainAggregateFileSizeBytesString), e3);
            }
            if (this.retainAggregateFileSizeBytes <= 0L) {
                throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_SIZE.get(entry.getDN(), retainAggregateFileSizeBytesString));
            }
        }
        if (this.retainFileCount == null && this.retainFileAgeMillis == null && this.retainAggregateFileSizeBytes == null) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_ENTRY_MISSING_RETENTION_CRITERIA.get(entry.getDN(), "ds-task-file-retention-retain-file-count", "ds-task-file-retention-retain-file-age", "ds-task-file-retention-retain-aggregate-file-size-bytes"));
        }
    }
    
    public FileRetentionTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.FileRetentionTask", properties);
        String directory = null;
        String pattern = null;
        FileRetentionTaskTimestampFormat format = null;
        Long count = null;
        Long age = null;
        Long size = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = StaticUtils.toLowerCase(p.getAttributeName());
            final List<Object> values = entry.getValue();
            final String s = attrName;
            switch (s) {
                case "ds-task-file-retention-target-directory": {
                    directory = Task.parseString(p, values, null);
                    continue;
                }
                case "ds-task-file-retention-filename-pattern": {
                    pattern = Task.parseString(p, values, null);
                    continue;
                }
                case "ds-task-file-retention-timestamp-format": {
                    final String formatName = Task.parseString(p, values, null);
                    format = FileRetentionTaskTimestampFormat.forName(formatName);
                    continue;
                }
                case "ds-task-file-retention-retain-file-count": {
                    count = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-file-retention-retain-file-age": {
                    age = Task.parseLong(p, values, null);
                    continue;
                }
                case "ds-task-file-retention-retain-aggregate-file-size-bytes": {
                    size = Task.parseLong(p, values, null);
                    continue;
                }
            }
        }
        this.targetDirectory = directory;
        this.filenamePattern = pattern;
        this.timestampFormat = format;
        this.retainFileAgeMillis = age;
        this.retainAggregateFileSizeBytes = size;
        if (count == null) {
            this.retainFileCount = null;
        }
        else {
            this.retainFileCount = count.intValue();
        }
        if (this.targetDirectory == null || this.targetDirectory.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_MISSING_REQUIRED_PROPERTY.get("ds-task-file-retention-target-directory"));
        }
        if (this.filenamePattern == null || this.filenamePattern.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_MISSING_REQUIRED_PROPERTY.get("ds-task-file-retention-filename-pattern"));
        }
        if (this.timestampFormat == null) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_MISSING_REQUIRED_PROPERTY.get("ds-task-file-retention-timestamp-format"));
        }
        if (this.retainFileCount == null && this.retainFileAgeMillis == null && this.retainAggregateFileSizeBytes == null) {
            throw new TaskException(TaskMessages.ERR_FILE_RETENTION_MISSING_RETENTION_PROPERTY.get("ds-task-file-retention-retain-file-count", "ds-task-file-retention-retain-file-age", "ds-task-file-retention-retain-aggregate-file-size-bytes"));
        }
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_FILE_RETENTION.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_FILE_RETENTION.get();
    }
    
    public String getTargetDirectory() {
        return this.targetDirectory;
    }
    
    public String getFilenamePattern() {
        return this.filenamePattern;
    }
    
    public FileRetentionTaskTimestampFormat getTimestampFormat() {
        return this.timestampFormat;
    }
    
    public Integer getRetainFileCount() {
        return this.retainFileCount;
    }
    
    public Long getRetainFileAgeMillis() {
        return this.retainFileAgeMillis;
    }
    
    public Long getRetainAggregateFileSizeBytes() {
        return this.retainAggregateFileSizeBytes;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-file-retention");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final LinkedList<Attribute> attrList = new LinkedList<Attribute>();
        attrList.add(new Attribute("ds-task-file-retention-target-directory", this.targetDirectory));
        attrList.add(new Attribute("ds-task-file-retention-filename-pattern", this.filenamePattern));
        attrList.add(new Attribute("ds-task-file-retention-timestamp-format", this.timestampFormat.name()));
        if (this.retainFileCount != null) {
            attrList.add(new Attribute("ds-task-file-retention-retain-file-count", String.valueOf(this.retainFileCount)));
        }
        if (this.retainFileAgeMillis != null) {
            final long retainFileAgeNanos = this.retainFileAgeMillis * 1000000L;
            final String retainFileAgeString = DurationArgument.nanosToDuration(retainFileAgeNanos);
            attrList.add(new Attribute("ds-task-file-retention-retain-file-age", retainFileAgeString));
        }
        if (this.retainAggregateFileSizeBytes != null) {
            attrList.add(new Attribute("ds-task-file-retention-retain-aggregate-file-size-bytes", String.valueOf(this.retainAggregateFileSizeBytes)));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(FileRetentionTask.PROPERTY_TARGET_DIRECTORY, FileRetentionTask.PROPERTY_FILENAME_PATTERN, FileRetentionTask.PROPERTY_TIMESTAMP_FORMAT, FileRetentionTask.PROPERTY_RETAIN_FILE_COUNT, FileRetentionTask.PROPERTY_RETAIN_FILE_AGE_MILLIS, FileRetentionTask.PROPERTY_RETAIN_AGGREGATE_FILE_SIZE_BYTES));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(6));
        props.put(FileRetentionTask.PROPERTY_TARGET_DIRECTORY, (List<Object>)Collections.singletonList(this.targetDirectory));
        props.put(FileRetentionTask.PROPERTY_FILENAME_PATTERN, (List<Object>)Collections.singletonList(this.filenamePattern));
        props.put(FileRetentionTask.PROPERTY_TIMESTAMP_FORMAT, (List<Object>)Collections.singletonList(this.timestampFormat.name()));
        if (this.retainFileCount != null) {
            props.put(FileRetentionTask.PROPERTY_RETAIN_FILE_COUNT, (List<Object>)Collections.singletonList((long)this.retainFileCount));
        }
        if (this.retainFileAgeMillis != null) {
            props.put(FileRetentionTask.PROPERTY_RETAIN_FILE_AGE_MILLIS, (List<Object>)Collections.singletonList(this.retainFileAgeMillis));
        }
        if (this.retainAggregateFileSizeBytes != null) {
            props.put(FileRetentionTask.PROPERTY_RETAIN_AGGREGATE_FILE_SIZE_BYTES, (List<Object>)Collections.singletonList(this.retainAggregateFileSizeBytes));
        }
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_TARGET_DIRECTORY = new TaskProperty("ds-task-file-retention-target-directory", TaskMessages.INFO_FILE_RETENTION_DISPLAY_NAME_TARGET_DIRECTORY.get(), TaskMessages.INFO_FILE_RETENTION_DESCRIPTION_TARGET_DIRECTORY.get(), String.class, true, false, false);
        PROPERTY_FILENAME_PATTERN = new TaskProperty("ds-task-file-retention-filename-pattern", TaskMessages.INFO_FILE_RETENTION_DISPLAY_NAME_FILENAME_PATTERN.get(), TaskMessages.INFO_FILE_RETENTION_DESCRIPTION_FILENAME_PATTERN.get(), String.class, true, false, false);
        PROPERTY_TIMESTAMP_FORMAT = new TaskProperty("ds-task-file-retention-timestamp-format", TaskMessages.INFO_FILE_RETENTION_DISPLAY_NAME_TIMESTAMP_FORMAT.get(), TaskMessages.INFO_FILE_RETENTION_DESCRIPTION_TIMESTAMP_FORMAT.get(), String.class, true, false, false, new String[] { FileRetentionTaskTimestampFormat.GENERALIZED_TIME_UTC_WITH_MILLISECONDS.name(), FileRetentionTaskTimestampFormat.GENERALIZED_TIME_UTC_WITH_SECONDS.name(), FileRetentionTaskTimestampFormat.GENERALIZED_TIME_UTC_WITH_MINUTES.name(), FileRetentionTaskTimestampFormat.LOCAL_TIME_WITH_MILLISECONDS.name(), FileRetentionTaskTimestampFormat.LOCAL_TIME_WITH_SECONDS.name(), FileRetentionTaskTimestampFormat.LOCAL_TIME_WITH_MINUTES.name(), FileRetentionTaskTimestampFormat.LOCAL_DATE.name() });
        PROPERTY_RETAIN_FILE_COUNT = new TaskProperty("ds-task-file-retention-retain-file-count", TaskMessages.INFO_FILE_RETENTION_DISPLAY_NAME_RETAIN_COUNT.get(), TaskMessages.INFO_FILE_RETENTION_DESCRIPTION_RETAIN_COUNT.get(), Long.class, false, false, false);
        PROPERTY_RETAIN_FILE_AGE_MILLIS = new TaskProperty("ds-task-file-retention-retain-file-age", TaskMessages.INFO_FILE_RETENTION_DISPLAY_NAME_RETAIN_AGE.get(), TaskMessages.INFO_FILE_RETENTION_DESCRIPTION_RETAIN_AGE.get(), Long.class, false, false, false);
        PROPERTY_RETAIN_AGGREGATE_FILE_SIZE_BYTES = new TaskProperty("ds-task-file-retention-retain-aggregate-file-size-bytes", TaskMessages.INFO_FILE_RETENTION_DISPLAY_NAME_RETAIN_SIZE.get(), TaskMessages.INFO_FILE_RETENTION_DESCRIPTION_RETAIN_SIZE.get(), Long.class, false, false, false);
    }
}
