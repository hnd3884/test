package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum TaskMessages
{
    ERR_ADD_SCHEMA_FILE_TASK_NO_FILES("Entry ''{0}'' cannot be parsed as an add schema file task because it does not have any values for the ds-task-schema-file-name attribute."), 
    ERR_ALERT_ENTRY_NO_ELEMENTS("The provided entry cannot be parsed as an alert task definition because it does not contain either an alert type and alert message or set of alert types to add or remove from the set of degraded or unavailable alert types."), 
    ERR_ALERT_PROPERTIES_NO_ELEMENTS("The provided property set cannot be used to construct an alert task because it does not contain either an alert type and alert message or set of alert types to add or remove from the set of degraded or unavailable alert types."), 
    ERR_ALERT_TYPE_AND_MESSAGE_INTERDEPENDENT("If either an alert type or alert message is present, then the other must also be present."), 
    ERR_AUDIT_DATA_SECURITY_BOTH_INCLUDE_AND_EXCLUDE_AUDITORS("You cannot specify both include and exclude auditors when creating an audit data security task."), 
    ERR_BACKUP_NO_BACKUP_DIRECTORY("Entry ''{0}'' cannot be parsed as a backup task because it does not specify the path to the backup directory in which to write the backup files."), 
    ERR_DELAY_CANNOT_PARSE_ATTR_VALUE_AS_DURATION("Unable to parse the value of the ''{0}'' attribute as a duration:  {1}"), 
    ERR_DELAY_ENTRY_MALFORMED_URL("Unable to parse {0} value ''{1}'' as an LDAP URL:  {2}"), 
    ERR_DELAY_INVALID_SEARCH_DURATION("If a total search duration per LDAP URL is provided, the value must be greater than the time between searches and the search time limit."), 
    ERR_DELAY_INVALID_SEARCH_INTERVAL("If a time between searches is provided, the value must be greater than zero and less than the total duration for each search URL."), 
    ERR_DELAY_INVALID_SEARCH_TIME_LIMIT("If a search time limit is provided, the value must be greater than zero and less than the total duration for each search URL."), 
    ERR_DELAY_INVALID_SLEEP_DURATION("If a sleep duration is provided, the value must be greater than zero."), 
    ERR_DELAY_INVALID_TIMEOUT_STATE("The provided timeout return state was invalid.  The value must be one of ''{0}'', ''{1}'', or ''{2}''."), 
    ERR_DELAY_INVALID_WAIT_FOR_QUEUE_IDLE("If the task should wait for the work queue to become idle, the duration must be greater than zero."), 
    ERR_DELAY_URL_WITHOUT_REQUIRED_PARAM("If the task is to perform searches using criteria from an LDAP URL, then a time between searches, search time limit, and total search duration for each LDAP URL must be provided."), 
    ERR_DISCONNECT_TASK_CONN_ID_NOT_LONG("Entry ''{0}'' cannot be parsed as a disconnect client task because the connection ID value ''{1}'' could not be parsed as an integer."), 
    ERR_DISCONNECT_TASK_NO_CONN_ID("Entry ''{0}'' cannot be parsed as a disconnect client task because it does not specify the connection ID for the client to disconnect."), 
    ERR_DUMP_DB_ENTRY_MISSING_BACKEND_ID("Entry {0} cannot be parsed as a dump DB details task entry because it is missing attribute {1} to specify the backend ID for the backend to examine."), 
    ERR_EXEC_ENTRY_MISSING_COMMAND_PATH("Unable to decode an exec task from entry ''{0}'' because it is missing the required {1} attribute."), 
    ERR_EXEC_INVALID_STATE_FOR_NONZERO_EXIT_CODE("If a task state is provided for use in the case of a nonzero exit code, the value must be one of ''{0}'', ''{1}'', or ''{2}''."), 
    ERR_EXEC_MISSING_PATH("The command path must not be null or empty."), 
    ERR_EXEC_PROPERTIES_MISSING_COMMAND_PATH("Unable to decode an exec task from the provided set of properties because the command path was not specified."), 
    ERR_EXPORT_TASK_CANNOT_PARSE_WRAP_COLUMN("Entry ''{0}'' cannot be parsed as an export task because value ''{1}'' of attribute ds-task-export-wrap-column cannot be parsed as an integer."), 
    ERR_EXPORT_TASK_NO_BACKEND_ID("Entry ''{0}'' cannot be parsed as an export task because it does not specify the backend ID of the backend to export."), 
    ERR_EXPORT_TASK_NO_LDIF_FILE("Entry ''{0}'' cannot be parsed as an export task because it does not specify the path to the LDIF file to be written."), 
    ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_AGE("Unable to decode entry ''{0}'' as a file retention task because ''{1}'' is not a valid retain file age value:  {2}"), 
    ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_COUNT("Unable to decode entry ''{0}'' as a file retention task because ''{1}'' is not a valid retain file count value.  The value must be a non-negative integer indicating the minimum number of files to retain."), 
    ERR_FILE_RETENTION_ENTRY_INVALID_RETAIN_SIZE("Unable to decode entry ''{0}'' as a file retention task because ''{1}'' is not a valid retain aggregate file size value.  It must be a positive integer representing the minimum number of bytes to retain."), 
    ERR_FILE_RETENTION_ENTRY_INVALID_TIMESTAMP_FORMAT("Unable to decode entry ''{0}'' as a file retention task because ''{1}'' is not a valid timestamp format.  The allowed timestamp format values are:  {2}."), 
    ERR_FILE_RETENTION_ENTRY_MISSING_REQUIRED_ATTR("Unable to decode entry ''{0}'' as a file retention task because it is missing required attribute ''{1}''."), 
    ERR_FILE_RETENTION_ENTRY_MISSING_RETENTION_CRITERIA("Unable to decode entry ''{0}'' as a file retention task because it does not contain any retention criteria.  At least one of the ''{1}'', ''{2}'', or ''{3}'' attributes must be provided."), 
    ERR_FILE_RETENTION_MISSING_REQUIRED_PROPERTY("Unable to create a file retention task from the provided set of properties because required property ''{0}'' was not provided."), 
    ERR_FILE_RETENTION_MISSING_RETENTION_PROPERTY("Unable to create a file retention task from the provided set of properties because none of the ''{0}'', ''{1}'', or ''{2}'' properties was provided.  At least one of these properties must be given."), 
    ERR_GROOVY_SCRIPTED_TASK_NO_CLASS("No fully-qualified Groovy class name was provided in task entry {0}."), 
    ERR_IMPORT_TASK_NO_BACKEND_ID_OR_INCLUDE_BRANCHES("Neither a backend ID nor set of include branches was specified."), 
    ERR_IMPORT_TASK_NO_LDIF("Entry ''{0}'' cannot be parsed as an import task because it does not specify the path to the LDIF file(s) containing the data to import."), 
    ERR_REBUILD_TASK_INVALID_MAX_THREADS("Entry ''{0}'' cannot be parsed as a rebuild task because value ''{1}'' for attribute ds-task-rebuild-max-threads cannot be parsed as an integer."), 
    ERR_REBUILD_TASK_NO_BASE_DN("Entry ''{0}'' cannot be parsed as a rebuild task because it does not specify the base DN for which to rebuild the indexes."), 
    ERR_REBUILD_TASK_NO_INDEXES("Entry ''{0}'' cannot be parsed as a rebuild task because it does not specify any indexes to rebuild."), 
    ERR_REENCODE_TASK_MISSING_REQUIRED_ATTR("Entry ''{0}'' cannot be parsed as a re-encode entries task because it does not have a value for the required ''{1}'' attribute."), 
    ERR_REENCODE_TASK_MISSING_REQUIRED_PROPERTY("Property ''{0}'' is required for a re-encode entries task but was not provided."), 
    ERR_RELOAD_GLOBAL_INDEX_MISSING_REQUIRED_ATTR("Unable to create a reload global index task from the provided entry because the required ''{0}'' attribute was not included."), 
    ERR_RELOAD_GLOBAL_INDEX_MISSING_REQUIRED_PROPERTY("Unable to create a reload global index task from the provided set of properties because the required ''{0}'' property was not included."), 
    ERR_RESTORE_NO_BACKUP_DIRECTORY("Entry ''{0}'' cannot be parsed as a restore task because it does not specify the path to the backup directory in which the backup resides."), 
    ERR_SEARCH_TASK_ENTRY_INVALID_FILTER("Entry ''{0}'' cannot be parsed as a search task because it has an invalid value of ''{1}'' for the ds-task-search-filter attribute."), 
    ERR_SEARCH_TASK_ENTRY_INVALID_SCOPE("Entry ''{0}'' cannot be parsed as a search task because it has an invalid value of ''{1}'' for the ds-task-search-scope attribute."), 
    ERR_SEARCH_TASK_ENTRY_NO_BASE_DN("Entry ''{0}'' cannot be parsed as a search task because it does not have any value for the ds-task-search-base-dn attribute."), 
    ERR_SEARCH_TASK_ENTRY_NO_FILTER("Entry ''{0}'' cannot be parsed as a search task because it does not have any value for the ds-task-search-filter attribute."), 
    ERR_SEARCH_TASK_ENTRY_NO_OUTPUT_FILE("Entry ''{0}'' cannot be parsed as a search task because it does not have any value for the ds-task-search-output-file attribute."), 
    ERR_SEARCH_TASK_ENTRY_NO_SCOPE("Entry ''{0}'' cannot be parsed as a search task because it does not have any value for the ds-task-search-scope attribute."), 
    ERR_SEARCH_TASK_INVALID_FILTER_PROPERTY("The provided value ''{0}'' is not a valid search filter."), 
    ERR_SEARCH_TASK_INVALID_SCOPE_PROPERTY("The provided value ''{0}'' is not a valid search scope."), 
    ERR_SEARCH_TASK_NO_BASE_PROPERTY("No property was provided to specify the search base DN."), 
    ERR_SEARCH_TASK_NO_FILTER_PROPERTY("No property was provided to specify the search filter."), 
    ERR_SEARCH_TASK_NO_OUTPUT_FILE_PROPERTY("No property was provided to specify the output file."), 
    ERR_SEARCH_TASK_NO_SCOPE_PROPERTY("No property was provided to specify the search scope."), 
    ERR_TASK_CANNOT_PARSE_ACTUAL_START_TIME("Entry ''{0}'' cannot be parsed as a scheduled task because the actual start time value ''{1}'' could not be interpreted as a generalized time:  {2}"), 
    ERR_TASK_CANNOT_PARSE_BOOLEAN("Entry ''{0}'' contains an invalid value ''{1}'' for attribute {2}.  The value must be either ''true'' or ''false''."), 
    ERR_TASK_CANNOT_PARSE_COMPLETION_TIME("Entry ''{0}'' cannot be parsed as a scheduled task because the completion time value ''{1}'' could not be interpreted as a generalized time:  {2}"), 
    ERR_TASK_CANNOT_PARSE_SCHEDULED_START_TIME("Entry ''{0}'' cannot be parsed as a scheduled task because the scheduled start time value ''{1}'' could not be interpreted as a generalized time:  {2}"), 
    ERR_TASK_INVALID_STATE("Entry ''{0}'' cannot be parsed as a scheduled task because it has an unrecognized task state ''{1}''."), 
    ERR_TASK_MANAGER_WAIT_INTERRUPTED("The thread waiting for task ''{0}'' to complete was interrupted."), 
    ERR_TASK_MANAGER_WAIT_NO_SUCH_TASK("No entry for task ''{0}'' exists in the Directory Server."), 
    ERR_TASK_MISSING_OC("Entry ''{0}'' cannot be parsed as a scheduled task entry because it is missing the ds-task object class."), 
    ERR_TASK_NO_CLASS("Entry ''{0}'' cannot be parsed as a scheduled task because it does not contain a task class name."), 
    ERR_TASK_NO_ID("Entry ''{0}'' cannot be parsed as a scheduled task because it does not contain a task ID."), 
    ERR_TASK_NO_STATE("Entry ''{0}'' cannot be parsed as a scheduled task because it does not contain a task state."), 
    ERR_TASK_PROPERTY_NOT_MULTIVALUED("Multiple values were provided for task property ''{0}'', but only a single value is allowed."), 
    ERR_TASK_PROPERTY_VALUE_NOT_ALLOWED("Task property ''{0}'' contains value ''{1}'' which is not contained in the set of allowed values for that property."), 
    ERR_TASK_PROPERTY_VALUE_NOT_BOOLEAN("Task property ''{0}'' contains a value which cannot be parsed as a Boolean."), 
    ERR_TASK_PROPERTY_VALUE_NOT_DATE("Task property ''{0}'' contains a value which cannot be parsed as a date."), 
    ERR_TASK_PROPERTY_VALUE_NOT_LONG("Task property ''{0}'' contains a value which cannot be parsed as a long."), 
    ERR_TASK_PROPERTY_VALUE_NOT_STRING("Task property ''{0}'' contains a value which cannot be parsed as a string."), 
    ERR_TASK_REQUIRED_PROPERTY_WITHOUT_VALUES("Task property ''{0}'' is required, but no values were provided."), 
    ERR_THIRD_PARTY_TASK_NO_CLASS("No fully-qualified task class name was provided in task entry {0}."), 
    INFO_ALERT_DESCRIPTION_ADD_DEGRADED("The name of an alert type to add to the set of degraded alert types."), 
    INFO_ALERT_DESCRIPTION_ADD_UNAVAILABLE("The name of an alert type to add to the set of unavailable alert types."), 
    INFO_ALERT_DESCRIPTION_MESSAGE("The message for the administrative alert to generate.  If this is provided, then an alert type must also be provided."), 
    INFO_ALERT_DESCRIPTION_REMOVE_DEGRADED("The name of an alert type to remove from the set of degraded alert types."), 
    INFO_ALERT_DESCRIPTION_REMOVE_UNAVAILABLE("The name of an alert type to remove from the set of unavailable alert types."), 
    INFO_ALERT_DESCRIPTION_TYPE("The alert type for the administrative alert to generate.  If this is provided, then an alert message must also be provided."), 
    INFO_ALERT_DISPLAY_NAME_ADD_DEGRADED("Add Degraded Alert Type"), 
    INFO_ALERT_DISPLAY_NAME_ADD_UNAVAILABLE("Add Unavailable Alert Type"), 
    INFO_ALERT_DISPLAY_NAME_MESSAGE("Alert Message"), 
    INFO_ALERT_DISPLAY_NAME_REMOVE_DEGRADED("Remove Degraded Alert Type"), 
    INFO_ALERT_DISPLAY_NAME_REMOVE_UNAVAILABLE("Remove Unavailable Alert Type"), 
    INFO_ALERT_DISPLAY_NAME_TYPE("Alert Type"), 
    INFO_AUDIT_DATA_SECURITY_DESCRIPTION_BACKEND_ID("The backend IDs of the backends in which data should be audited.  If no backend IDs are specified, then the audit will examine data in all backends that support this capability."), 
    INFO_AUDIT_DATA_SECURITY_DESCRIPTION_EXCLUDE_AUDITOR("The names of the auditors to be excluded when examining the security of the data.  If excluded auditors are specified, then you must not also specify included auditors.  If neither included nor excluded auditors are provided, then the audit will use all enabled auditors configured in the server."), 
    INFO_AUDIT_DATA_SECURITY_DESCRIPTION_INCLUDE_AUDITOR("The names of the auditors to be used in the course of examining the security of the data.  If included auditors are specified, then you must not also specify excluded auditors.  If neither included nor excluded auditors are provided, then the audit will use all enabled auditors configured in the server."), 
    INFO_AUDIT_DATA_SECURITY_DESCRIPTION_OUTPUT_DIR("The path to the directory on the server filesystem in which report output files should be written.  If this is not specified, then a default location will be used."), 
    INFO_AUDIT_DATA_SECURITY_DESCRIPTION_REPORT_FILTER("A set of filters that may be used to indicate which entries should be examined during the audit.  If no report filters are specified, then all entries in the selected backends will be included."), 
    INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_BACKEND_ID("Audit Backend IDs"), 
    INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_EXCLUDE_AUDITOR("Excluded Auditors"), 
    INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_INCLUDE_AUDITOR("Included Auditors"), 
    INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_OUTPUT_DIR("Output Directory"), 
    INFO_AUDIT_DATA_SECURITY_DISPLAY_NAME_REPORT_FILTER("Report Filters"), 
    INFO_DELAY_DESCRIPTION_SEARCH_DURATION("The total length of time to continue repeating searches using criteria obtained from each provided LDAP URL."), 
    INFO_DELAY_DESCRIPTION_SEARCH_INTERVAL("The length of time between searches using the criteria provided by an LDAP URL.  If a search using that criteria does not match any entries and the total duration for that URL has not yet been reached, the task will sleep for this length of time before re-issuing the search."), 
    INFO_DELAY_DESCRIPTION_SEARCH_TIME_LIMIT("The maximum length of time to wait for the response to any single search request created from the criteria associated with a provided LDAP URL."), 
    INFO_DELAY_DESCRIPTION_SEARCH_URL("A set of LDAP URLs that provide criteria for search operations that are eventually expected to return one or more entries."), 
    INFO_DELAY_DESCRIPTION_SLEEP_DURATION("The length of time that the task should sleep.  If the task will also wait until the work queue is idle or perform additional searches, then this sleep will come after those conditions have been satisfied."), 
    INFO_DELAY_DESCRIPTION_TIMEOUT_RETURN_STATE("The final task state that should be used if a timeout is encountered while waiting for the work queue to become idle or for a search to match one or more entries."), 
    INFO_DELAY_DESCRIPTION_WAIT_FOR_WORK_QUEUE_IDLE("The length of time to wait for the work queue to report that all worker threads are idle and there are no outstanding operations to process."), 
    INFO_DELAY_DISPLAY_NAME_SEARCH_DURATION("Total Search Duration for Each LDAP URL"), 
    INFO_DELAY_DISPLAY_NAME_SEARCH_INTERVAL("Time Between Searches"), 
    INFO_DELAY_DISPLAY_NAME_SEARCH_TIME_LIMIT("Search Time Limit"), 
    INFO_DELAY_DISPLAY_NAME_SEARCH_URL("LDAP URLs for Searches Expected to Return Entries"), 
    INFO_DELAY_DISPLAY_NAME_SLEEP_DURATION("Sleep Duration"), 
    INFO_DELAY_DISPLAY_NAME_TIMEOUT_RETURN_STATE("Task Return State in Case of a Timeout"), 
    INFO_DELAY_DISPLAY_NAME_WAIT_FOR_WORK_QUEUE_IDLE("Length of Time to Wait for the Work Queue to Be Idle"), 
    INFO_DESCRIPTION_ALERT_ON_ERROR("Indicates whether the server should generate an alert notification if this task fails to complete successfully."), 
    INFO_DESCRIPTION_ALERT_ON_START("Indicates whether the server should generate an alert notification when this task starts running."), 
    INFO_DESCRIPTION_ALERT_ON_SUCCESS("Indicates whether the server should generate an alert notification if this task completes successfully."), 
    INFO_DESCRIPTION_APPEND_TO_DB("Indicates whether the data in the specified LDIF files should be appended to the existing data rather than replacing it."), 
    INFO_DESCRIPTION_APPEND_TO_LDIF("Indicates whether to append to the specified LDIF file if it already exists.  Otherwise, it will be overwritten."), 
    INFO_DESCRIPTION_BACKEND_ID_BACKUP("The backend ID of the backend to be archived.  Multiple backend IDs may be specified as separate values.  If no backend IDs are provided, then all supported backends will be archived."), 
    INFO_DESCRIPTION_BACKEND_ID_EXPORT("The backend ID of the backend from which the data is to be exported."), 
    INFO_DESCRIPTION_BACKEND_ID_IMPORT("The backend ID of the backend into which the data is to be imported.  If this is not specified, then one or more include branches must be defined."), 
    INFO_DESCRIPTION_BACKUP_DIRECTORY_BACKUP("The path to the directory in which the backup files should be placed.  If multiple backends are to be archived, then this should be the parent directory of the backup directories for each backend."), 
    INFO_DESCRIPTION_BACKUP_DIRECTORY_RESTORE("The path to the directory containing the backup to be restored."), 
    INFO_DESCRIPTION_BACKUP_ID_BACKUP("The backup ID to use for the backup.  If no backup ID is specified, then the server will generate one."), 
    INFO_DESCRIPTION_BACKUP_ID_RESTORE("The backup ID of the backup that should be restored.  If this is not provided, then the most recent backup contained in the specified backup directory will be used."), 
    INFO_DESCRIPTION_BACKUP_MAX_MEGABYTES_PER_SECOND("The maximum backup rate in megabytes per second at which the backup should be written."), 
    INFO_DESCRIPTION_BACKUP_RETAIN_AGE("The minimum age of previous backups that should be retained."), 
    INFO_DESCRIPTION_BACKUP_RETAIN_COUNT("The minimum number of previous backups that should be retained."), 
    INFO_DESCRIPTION_BASE_DN_REBUILD("The base DN of the backend below which the index data should be rebuilt."), 
    INFO_DESCRIPTION_CLEAR_BACKEND("Indicates whether to clear all data in the backend when performing an import and one or more include branches is specified.  If this is ''false'', then only data below the specified include branch(es) will be cleared, and data below other base DNs will be preserved."), 
    INFO_DESCRIPTION_COMPRESS_BACKUP("Indicates whether the contents of the backup should be compressed."), 
    INFO_DESCRIPTION_COMPRESS_EXPORT("Indicates whether to compress the exported LDIF data."), 
    INFO_DESCRIPTION_DEPENDENCY_ID("The task ID of another task that must complete before this task will be eligible to start running.  If this is not provided, then the task will be eligible to start at any time."), 
    INFO_DESCRIPTION_DISCONNECT_CONN_ID("The connection ID for the client connection to be disconnected."), 
    INFO_DESCRIPTION_DISCONNECT_MESSAGE("A message that provides additional information about the disconnection.  If notification is to be sent to the client, then this message will be included in that notification."), 
    INFO_DESCRIPTION_DISCONNECT_NOTIFY("Indicates whether to send the client a notice of disconnection message before terminating the connection."), 
    INFO_DESCRIPTION_ENCRYPTION_PASSPHRASE_FILE("The path to a file containing the passphrase to use to generate the encryption key."), 
    INFO_DESCRIPTION_ENCRYPTION_SETTINGS_DEFINITION_ID("The identifier for the encryption settings definition that to use to generate the encryption key."), 
    INFO_DESCRIPTION_ENCRYPT_BACKUP("Indicates whether the contents of the backup should be encrypted."), 
    INFO_DESCRIPTION_ENCRYPT_EXPORT("Indicates whether to encrypt the exported LDIF data."), 
    INFO_DESCRIPTION_ENTER_LOCKDOWN_REASON("An optional reason for putting the server into lockdown mode."), 
    INFO_DESCRIPTION_EXCLUDE_ATTRIBUTE_EXPORT("The name of an attribute that should be excluded from exported entries.  If this is not provided, then all attributes will be included in the export.  Otherwise, none of only the specified set of attributes will be included."), 
    INFO_DESCRIPTION_EXCLUDE_ATTRIBUTE_IMPORT("The name of an attribute that should be excluded from imported entries.  If this is not provided, then all attributes will be included.  Otherwise, all attributes other than the excluded attributes will be included."), 
    INFO_DESCRIPTION_EXCLUDE_BRANCH_EXPORT("The base DN of a branch that is to be excluded from the export.  If this is not provided, then all data in the backend will be eligible for inclusion in the export.  Otherwise, only data that does not exist below any of the exclude branches will be included."), 
    INFO_DESCRIPTION_EXCLUDE_BRANCH_IMPORT("The base DN of a branch to exclude from the import.  If no exclude branches are provided, then all data in the provided LDIF files will be imported.  Otherwise, only data that does not exist below one of the specified exclude branches will be imported."), 
    INFO_DESCRIPTION_EXCLUDE_FILTER_EXPORT("A filter that will be applied to an entry to determine whether it should be excluded from the export.  If this is not provided, then all entries will be eligible for inclusion in the export.  Otherwise, no entries that match one of the provided exclude filters will be included."), 
    INFO_DESCRIPTION_EXCLUDE_FILTER_IMPORT("A filter that will be applied to an entry to determine whether it should be excluded from the import.  If this is not provided, then no entries will be excluded from the import."), 
    INFO_DESCRIPTION_EXPORT_MAX_MEGABYTES_PER_SECOND("The maximum backup rate in megabytes per second at which the backup should be written."), 
    INFO_DESCRIPTION_FAILED_DEPENDENCY_ACTION("Specifies the action to take with this task if any task on which it depends does not complete successfully.  Allowed values include ''cancel'', ''disable'', or ''process''.  If this is not provided, then the task will be disabled if any of the tasks on which it depends does not complete successfully."), 
    INFO_DESCRIPTION_GROOVY_SCRIPTED_TASK_ARG("An argument to provide to the scripted task.  It should be in the form name=value."), 
    INFO_DESCRIPTION_GROOVY_SCRIPTED_TASK_CLASS("The fully-qualified name of the Groovy class providing the logic for the scripted task."), 
    INFO_DESCRIPTION_HASH_BACKUP("Indicates whether to calculate a hash of the backup contents, which can be used to verify the integrity of the backup."), 
    INFO_DESCRIPTION_INCLUDE_ATTRIBUTE_EXPORT("The name of an attribute that should be included in exported entries.  If this is not provided, then all attributes will be included in the export.  Otherwise, only the specified set of attributes will be included."), 
    INFO_DESCRIPTION_INCLUDE_ATTRIBUTE_IMPORT("The name of an attribute that should be included in imported entries.  If this is not provided, then all attributes will be included.  Otherwise, only the specified attributes will be included."), 
    INFO_DESCRIPTION_INCLUDE_BRANCH_EXPORT("The base DN of a branch that is to be included in the export.  If this is not provided, then all data in the backend will be eligible for inclusion in the export.  Otherwise, only data that exists below one of the include branches will be included."), 
    INFO_DESCRIPTION_INCLUDE_BRANCH_IMPORT("The base DN of a branch to include in the import.  If no include branches are provided, then a backend ID must be given, and all data in the provided LDIF files will be imported.  Otherwise, only data that exists below one of the specified include branches will be imported."), 
    INFO_DESCRIPTION_INCLUDE_FILTER_EXPORT("A filter that will be applied to an entry to determine whether it should be included in the export.  If this is not provided, then all entries will be eligible for inclusion in the export.  Otherwise, only entries that match one of the provided include filters will be included."), 
    INFO_DESCRIPTION_INCLUDE_FILTER_IMPORT("A filter that will be applied to an entry to determine whether it should be included in the import.  If this is not provided, then all entries will be included in the import."), 
    INFO_DESCRIPTION_INCREMENTAL("Indicates whether to attempt an incremental backup rather than a full backup.  An incremental backup includes only that information which has changed since the previous backup.  Note that if the associated backend does not support incremental backups, then a full backup will be performed."), 
    INFO_DESCRIPTION_INCREMENTAL_BASE_ID("The backup ID for the previous backup on which to base an incremental backup.  If this is not provided, then the server will base the incremental backup on the last available backup in the backup directory.  This will be ignored for non-incremental backups."), 
    INFO_DESCRIPTION_INDEX_REBUILD("The name of the index to be rebuilt.  For attribute indexes, this is the name of the associated attribute.  For VLV indexes, this is the name of the VLV index.  Multiple indexes can be rebuilt by providing multiple index names as separate values."), 
    INFO_DESCRIPTION_IS_COMPRESSED_IMPORT("Indicates whether the LDIF data to be imported is compressed."), 
    INFO_DESCRIPTION_IS_ENCRYPTED_IMPORT("Indicates whether the LDIF data to be imported is encrypted."), 
    INFO_DESCRIPTION_LDIF_FILE_EXPORT("The path to the LDIF file to which the exported data is to be written."), 
    INFO_DESCRIPTION_LDIF_FILE_IMPORT("Specifies the path to the LDIF file containing the data to be imported.  Multiple LDIF files can be specified as separate values, and the data contained in them will be processed in the order they were specified."), 
    INFO_DESCRIPTION_LEAVE_LOCKDOWN_REASON("An optional reason for taking the server out of lockdown mode."), 
    INFO_DESCRIPTION_MAX_THREADS_REBUILD("The maximum number of concurrent threads to use when rebuilding index data.  A value less than or equal to zero indicates that there should be no limit to the number of threads that may be used."), 
    INFO_DESCRIPTION_NOTIFY_ON_COMPLETION("The e-mail address of an individual that should be sent a notification message whenever this task completes.  The notification message will be sent regardless of whether the task completes successfully."), 
    INFO_DESCRIPTION_NOTIFY_ON_ERROR("The e-mail address of an individual that should be sent a notification message if this task is unable to complete successfully.  No message will be sent to this address if the task does complete successfully."), 
    INFO_DESCRIPTION_NOTIFY_ON_START("The e-mail address of an individual that should be sent a notification message when this task starts running."), 
    INFO_DESCRIPTION_NOTIFY_ON_SUCCESS("The e-mail address of an individual that should be sent a notification message if this task completes successfully.  No message will be sent to this address if the task does not complete successfully."), 
    INFO_DESCRIPTION_OVERWRITE_REJECTS("Indicates whether to overwrite an existing reject file if it exists.  Otherwise, the server will append to the file."), 
    INFO_DESCRIPTION_REENCODE_BACKEND_ID("The backend ID for the backend in which the re-encode processing is to be performed."), 
    INFO_DESCRIPTION_REENCODE_EXCLUDE_BRANCH("The base DN of a branch to exclude from re-encode processing."), 
    INFO_DESCRIPTION_REENCODE_EXCLUDE_FILTER("A filter to use to identify entries to exclude from re-encode processing."), 
    INFO_DESCRIPTION_REENCODE_INCLUDE_BRANCH("The base DN of a branch to include in re-encode processing."), 
    INFO_DESCRIPTION_REENCODE_INCLUDE_FILTER("A filter to use to identify entries to include in re-encode processing."), 
    INFO_DESCRIPTION_REENCODE_MAX_ENTRIES_PER_SECOND("The maximum number of entries to be re-encoded per second."), 
    INFO_DESCRIPTION_REENCODE_SKIP_FULLY_UNCACHED("Indicates whether to skip re-encode processing for entries that are stored completely uncached."), 
    INFO_DESCRIPTION_REENCODE_SKIP_PARTIALLY_UNCACHED("Indicates whether to skip re-encode processing for entries that are stored with a mix of cached and uncached attributes."), 
    INFO_DESCRIPTION_REJECT_FILE("The path to a file to which information about rejected entries should be written.  If this is not specified, then no reject file will be written."), 
    INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_ATTR_NAME("The name(s) of the attributes for which to reload index information.  If this is not provided, then all indexes will be reloaded."), 
    INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_BACKGROUND("Indicates whether to perform the reload in a background thread so that the task completes immediately."), 
    INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_BASE_DN("The base DN of the entry-balancing request processor for which to reload global index information.  This must be specified."), 
    INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_MAX_ENTRIES_PER_SECOND("An optional target maximum rate at which entries should be reloaded.  A value of zero indicates no limit.  If this is not specified, then the rate limit will be determined from the Directory Proxy Server configuration."), 
    INFO_DESCRIPTION_RELOAD_GLOBAL_INDEX_RELOAD_FROM_DS("Indicates whether to retrieve the index information from backend Directory Server instances rather than a peer Directory Proxy Server instance."), 
    INFO_DESCRIPTION_REPLACE_EXISTING("Indicates whether to replace an existing entry if it is contained in the LDIF file when appending to the database.  This is ignored when not operating in append mode."), 
    INFO_DESCRIPTION_RESTART_SERVER("Indicates that the server should be restarted instead of shut down."), 
    INFO_DESCRIPTION_SCHEDULED_START_TIME("The earliest time that this task should be allowed to start running.  If it is specified, then it must be in generalized time format (e.g., 'YYYYMMDDhhmmssZ' for UTC or 'YYYYMMDDhhmmss+0500' to specify a UTC offset).  If this is not provided, then the task will be eligible to start at any time."), 
    INFO_DESCRIPTION_SCHEMA_FILE("The name (without path information) of the file whose contents should be added to the server schema.  The file must exist in the server''s schema directory."), 
    INFO_DESCRIPTION_SHUTDOWN_MESSAGE("A human-readable message that may be used to provide information about the reason for the shutdown."), 
    INFO_DESCRIPTION_SIGN_EXPORT("Indicates whether to include a digital signature at the end of the export data which can be used to ensure that data has not been altered."), 
    INFO_DESCRIPTION_SIGN_HASH_BACKUP("Indicates whether the backup hash should be digitally signed to ensure that it cannot be altered."), 
    INFO_DESCRIPTION_SKIP_SCHEMA_VALIDATION("Indicates whether the server should skip schema validation for the entries being imported."), 
    INFO_DESCRIPTION_STRIP_TRAILING_SPACES("Indicates whether the server should strip illegal trailing spaces from LDIF records rather than rejecting those records."), 
    INFO_DESCRIPTION_TASK_ID("The unique identifier for the task.  It must not already be in use by any task available in the Directory Server.  If this is not provided, then a UUID will be generated for use as the task ID."), 
    INFO_DESCRIPTION_THIRD_PARTY_TASK_ARG("An argument to provide to the third-party task.  It should be in the form name=value."), 
    INFO_DESCRIPTION_THIRD_PARTY_TASK_CLASS("The fully-qualified name of the Java class providing the logic for the third-party task."), 
    INFO_DESCRIPTION_VERIFY_ONLY("Indicates whether the server should attempt to verify whether it should be possible to restore the specified backup without actually performing the restore."), 
    INFO_DESCRIPTION_WRAP_COLUMN("The column at which long lines should be wrapped when writing the LDIF data.  If this is not provided, or if the provided value is less than or equal to zero, then no wrapping will be performed."), 
    INFO_DISPLAY_NAME_ALERT_ON_ERROR("Alert on Error"), 
    INFO_DISPLAY_NAME_ALERT_ON_START("Alert on Start"), 
    INFO_DISPLAY_NAME_ALERT_ON_SUCCESS("Alert on Success"), 
    INFO_DISPLAY_NAME_APPEND_TO_DB("Append to Existing Data"), 
    INFO_DISPLAY_NAME_APPEND_TO_LDIF("Append to LDIF File"), 
    INFO_DISPLAY_NAME_BACKEND_ID("Backend ID"), 
    INFO_DISPLAY_NAME_BACKUP_DIRECTORY("Backup Directory Path"), 
    INFO_DISPLAY_NAME_BACKUP_ID("Backup ID"), 
    INFO_DISPLAY_NAME_BACKUP_MAX_MEGABYTES_PER_SECOND("Maximum Megabytes Per Second"), 
    INFO_DISPLAY_NAME_BACKUP_RETAIN_AGE("Retain Previous Backup Age"), 
    INFO_DISPLAY_NAME_BACKUP_RETAIN_COUNT("Retain Previous Backup Count"), 
    INFO_DISPLAY_NAME_BASE_DN_REBUILD("Backend Base DN"), 
    INFO_DISPLAY_NAME_CLEAR_BACKEND("Clear the Entire Backend"), 
    INFO_DISPLAY_NAME_COMPRESS("Compress"), 
    INFO_DISPLAY_NAME_DEPENDENCY_ID("Depends on Task"), 
    INFO_DISPLAY_NAME_DISCONNECT_CONN_ID("Connection ID"), 
    INFO_DISPLAY_NAME_DISCONNECT_MESSAGE("Disconnect Message"), 
    INFO_DISPLAY_NAME_DISCONNECT_NOTIFY("Notify Client Before Disconnecting"), 
    INFO_DISPLAY_NAME_ENCRYPT("Encrypt"), 
    INFO_DISPLAY_NAME_ENCRYPTION_PASSPHRASE_FILE("Encryption Passphrase File"), 
    INFO_DISPLAY_NAME_ENCRYPTION_SETTINGS_DEFINITION_ID("Encryption Settings Definition ID"), 
    INFO_DISPLAY_NAME_ENTER_LOCKDOWN_REASON("Reason"), 
    INFO_DISPLAY_NAME_EXCLUDE_ATTRIBUTE("Exclude Attribute"), 
    INFO_DISPLAY_NAME_EXCLUDE_BRANCH("Include Branch DN"), 
    INFO_DISPLAY_NAME_EXCLUDE_FILTER("Exclude Filter"), 
    INFO_DISPLAY_NAME_EXPORT_MAX_MEGABYTES_PER_SECOND("Maximum Megabytes Per Second"), 
    INFO_DISPLAY_NAME_FAILED_DEPENDENCY_ACTION("Failed Dependency Action"), 
    INFO_DISPLAY_NAME_GROOVY_SCRIPTED_TASK_ARG("Groovy-Scripted Task Argument"), 
    INFO_DISPLAY_NAME_GROOVY_SCRIPTED_TASK_CLASS("Groovy-Scripted Task Class Name"), 
    INFO_DISPLAY_NAME_HASH("Calculate Hash"), 
    INFO_DISPLAY_NAME_INCLUDE_ATTRIBUTE("Include Attribute"), 
    INFO_DISPLAY_NAME_INCLUDE_BRANCH("Include Branch DN"), 
    INFO_DISPLAY_NAME_INCLUDE_FILTER("Include Filter"), 
    INFO_DISPLAY_NAME_INCREMENTAL("Incremental Backup"), 
    INFO_DISPLAY_NAME_INCREMENTAL_BASE_ID("Incremental Base ID"), 
    INFO_DISPLAY_NAME_INDEX_REBUILD("Index Name"), 
    INFO_DISPLAY_NAME_IS_COMPRESSED_IMPORT("LDIF Data Is Compressed"), 
    INFO_DISPLAY_NAME_IS_ENCRYPTED_IMPORT("LDIF Data Is Encrypted"), 
    INFO_DISPLAY_NAME_LDIF_FILE("LDIF File Path"), 
    INFO_DISPLAY_NAME_LEAVE_LOCKDOWN_REASON("Reason"), 
    INFO_DISPLAY_NAME_MAX_THREADS_REBUILD("Maximum Concurrent Rebuild Threads"), 
    INFO_DISPLAY_NAME_NOTIFY_ON_COMPLETION("Notify on Completion"), 
    INFO_DISPLAY_NAME_NOTIFY_ON_ERROR("Notify on Error"), 
    INFO_DISPLAY_NAME_NOTIFY_ON_START("Notify on Start"), 
    INFO_DISPLAY_NAME_NOTIFY_ON_SUCCESS("Notify on Success"), 
    INFO_DISPLAY_NAME_OVERWRITE_REJECTS("Overwrite Existing Reject File"), 
    INFO_DISPLAY_NAME_REENCODE_BACKEND_ID("Backend ID"), 
    INFO_DISPLAY_NAME_REENCODE_EXCLUDE_BRANCH("Exclude Branch"), 
    INFO_DISPLAY_NAME_REENCODE_EXCLUDE_FILTER("Exclude Filter"), 
    INFO_DISPLAY_NAME_REENCODE_INCLUDE_BRANCH("Include Branch"), 
    INFO_DISPLAY_NAME_REENCODE_INCLUDE_FILTER("Include Filter"), 
    INFO_DISPLAY_NAME_REENCODE_MAX_ENTRIES_PER_SECOND("Maximum Entries to Re-Encode Per Second"), 
    INFO_DISPLAY_NAME_REENCODE_SKIP_FULLY_UNCACHED("Skip Fully Uncached Entries"), 
    INFO_DISPLAY_NAME_REENCODE_SKIP_PARTIALLY_UNCACHED("Skip Partially Uncached Entries"), 
    INFO_DISPLAY_NAME_REJECT_FILE("Reject File Path"), 
    INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_ATTR_NAME("Attribute Name"), 
    INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_BACKGROUND("Reload in a Background Thread"), 
    INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_BASE_DN("Entry-Balancing Request Processor Base DN"), 
    INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_MAX_ENTRIES_PER_SECOND("Maximum Reload Rate (Entries/Second)"), 
    INFO_DISPLAY_NAME_RELOAD_GLOBAL_INDEX_RELOAD_FROM_DS("Reload from Backend Directory Servers"), 
    INFO_DISPLAY_NAME_REPLACE_EXISTING("Replace Existing Entries when Appending"), 
    INFO_DISPLAY_NAME_RESTART_SERVER("Restart Instead of Shut Down"), 
    INFO_DISPLAY_NAME_SCHEDULED_START_TIME("Scheduled Start Time"), 
    INFO_DISPLAY_NAME_SCHEMA_FILE("Schema File Name"), 
    INFO_DISPLAY_NAME_SHUTDOWN_MESSAGE("Shutdown Message"), 
    INFO_DISPLAY_NAME_SIGN("Sign Export"), 
    INFO_DISPLAY_NAME_SIGN_HASH("Sign Hash"), 
    INFO_DISPLAY_NAME_SKIP_SCHEMA_VALIDATION("Skip Schema Validation"), 
    INFO_DISPLAY_NAME_STRIP_TRAILING_SPACES("Strip Trailing Spaces"), 
    INFO_DISPLAY_NAME_TASK_ID("Task ID"), 
    INFO_DISPLAY_NAME_THIRD_PARTY_TASK_ARG("Third-Party Task Argument"), 
    INFO_DISPLAY_NAME_THIRD_PARTY_TASK_CLASS("Third-Party Task Class Name"), 
    INFO_DISPLAY_NAME_VERIFY_ONLY("Verify Without Restoring"), 
    INFO_DISPLAY_NAME_WRAP_COLUMN("Wrap Column"), 
    INFO_DUMP_DB_DESCRIPTION_BACKEND_ID("The backend ID for the backend whose contents should be examined."), 
    INFO_DUMP_DB_DISPLAY_NAME_BACKEND_ID("Backend ID"), 
    INFO_EXEC_DESCRIPTION_COMMAND_ARGUMENTS("A string containing the arguments to use when running the command."), 
    INFO_EXEC_DESCRIPTION_COMMAND_OUTPUT_FILE("A string containing the path (on the server filesystem) to the output file to which the command output should be written.  The path may be absolute or relative, and if it is relative, then it will be interpreted as relative to the server root."), 
    INFO_EXEC_DESCRIPTION_COMMAND_PATH("The absolute path (on the server filesystem) for the command to execute."), 
    INFO_EXEC_DESCRIPTION_LOG_COMMAND_OUTPUT("Indicates whether the command's output should be recorded in the server's error log."), 
    INFO_EXEC_DESCRIPTION_TASK_STATE_FOR_NONZERO_EXIT_CODE("The task state that should be used if the command completes with a nonzero exit code.  Valid values include 'STOPPED_BY_ERROR', 'COMPLETED_WITH_ERRORS', and 'COMPLETED_SUCCESSFULLY'."), 
    INFO_EXEC_DESCRIPTION_WORKING_DIRECTORY("The path on the server to use as the working directory when executing the command.  If this is not specified, the server root will be used as the default working directory."), 
    INFO_EXEC_DISPLAY_NAME_COMMAND_ARGUMENTS("Command Arguments"), 
    INFO_EXEC_DISPLAY_NAME_COMMAND_OUTPUT_FILE("Command Output File"), 
    INFO_EXEC_DISPLAY_NAME_COMMAND_PATH("Command Path"), 
    INFO_EXEC_DISPLAY_NAME_LOG_COMMAND_OUTPUT("Log Command Output to Server Error Log"), 
    INFO_EXEC_DISPLAY_NAME_TASK_STATE_FOR_NONZERO_EXIT_CODE("Task Completion State for Nonzero Exit Code"), 
    INFO_EXEC_DISPLAY_NAME_WORKING_DIRECTORY("Working Directory"), 
    INFO_FILE_RETENTION_DESCRIPTION_FILENAME_PATTERN("The pattern to use to identify the matching files.  It may contain zero or more asterisks to use as wildcards, and at most one occurrence of the token '${timestamp}' to specify the position of a timestamp in the filename."), 
    INFO_FILE_RETENTION_DESCRIPTION_RETAIN_AGE("The minimum age (in milliseconds) of files to retain."), 
    INFO_FILE_RETENTION_DESCRIPTION_RETAIN_COUNT("The minimum number of files to retain."), 
    INFO_FILE_RETENTION_DESCRIPTION_RETAIN_SIZE("The minimum aggregate size (in bytes) of files to retain."), 
    INFO_FILE_RETENTION_DESCRIPTION_TARGET_DIRECTORY("The path to the directory (on the server filesystem) that contains the files to examine."), 
    INFO_FILE_RETENTION_DESCRIPTION_TIMESTAMP_FORMAT("The format to use for timestamp values in the filename pattern."), 
    INFO_FILE_RETENTION_DISPLAY_NAME_FILENAME_PATTERN("Filename Pattern"), 
    INFO_FILE_RETENTION_DISPLAY_NAME_RETAIN_AGE("Retain File Age in Milliseconds"), 
    INFO_FILE_RETENTION_DISPLAY_NAME_RETAIN_COUNT("Retain File Count"), 
    INFO_FILE_RETENTION_DISPLAY_NAME_RETAIN_SIZE("Retain Aggregate File Size in Bytes"), 
    INFO_FILE_RETENTION_DISPLAY_NAME_TARGET_DIRECTORY("Target Directory"), 
    INFO_FILE_RETENTION_DISPLAY_NAME_TIMESTAMP_FORMAT("Timestamp Format"), 
    INFO_ROTATE_LOG_DESCRIPTION_PATH("The path to a log file to be rotated.  It may be absolute, or it may be relative to the server root.  Multiple paths may be specified if rotation should be performed for multiple log files.  If no paths are given, the server will rotate all applicable log files."), 
    INFO_ROTATE_LOG_DISPLAY_NAME_PATH("Path"), 
    INFO_SEARCH_TASK_DESCRIPTION_AUTHZ_DN("The DN of the user as whom the search should be processed.  If this is not specified, then the search will be processed as an internal root user."), 
    INFO_SEARCH_TASK_DESCRIPTION_BASE_DN("The base DN to use for the search."), 
    INFO_SEARCH_TASK_DESCRIPTION_FILTER("The filter to use for the search."), 
    INFO_SEARCH_TASK_DESCRIPTION_NAME_OUTPUT_FILE("The path (on the server filesystem) to the LDIF file to be written with entries matching the search criteria."), 
    INFO_SEARCH_TASK_DESCRIPTION_RETURN_ATTR("The name of an attribute to include in matching entries, or a grouping token like '*' (for all user attributes), '+' (for all operational attributes), or '@inetOrgPerson' (for all attributes in the inetOrgPerson object class).  If this is not provided then all user attributes will be selected."), 
    INFO_SEARCH_TASK_DESCRIPTION_SCOPE("The scope to use for the search.  The value must be one of 'base', 'one', 'sub', or 'subordinate'."), 
    INFO_SEARCH_TASK_DISPLAY_NAME_AUTHZ_DN("Authorization DN"), 
    INFO_SEARCH_TASK_DISPLAY_NAME_BASE_DN("Search Base DN"), 
    INFO_SEARCH_TASK_DISPLAY_NAME_FILTER("Search Filter"), 
    INFO_SEARCH_TASK_DISPLAY_NAME_OUTPUT_FILE("Output File"), 
    INFO_SEARCH_TASK_DISPLAY_NAME_RETURN_ATTR("Attribute to Return"), 
    INFO_SEARCH_TASK_DISPLAY_NAME_SCOPE("Search Scope"), 
    INFO_TASK_DESCRIPTION_ADD_SCHEMA_FILE("May be used to add the contents of one or more files to the server schema."), 
    INFO_TASK_DESCRIPTION_ALERT("Cause the Directory Server to generate administrative alerts and/or manage the set of degraded and unavailable alert types reported in the general monitor entry."), 
    INFO_TASK_DESCRIPTION_AUDIT_DATA_SECURITY("This task may be used to initiate a data security audit in the Directory Server to look for potential issues in the data which may pose a security risk to the directory environment."), 
    INFO_TASK_DESCRIPTION_BACKUP("May be used to back up the contents of one or more Directory Server backends."), 
    INFO_TASK_DESCRIPTION_DELAY("Sleeps for a specified period of time or waits for a given condition to be satisfied."), 
    INFO_TASK_DESCRIPTION_DISCONNECT_CLIENT("May be used to terminate a client connection."), 
    INFO_TASK_DESCRIPTION_DUMP_DB("Dump statistics about the contents of a Directory Server backend which stores its content in a Berkeley DB Java Edition database environment.  It reports information about the total and average size of keys and values in each database, along with the relative sizes of the databases."), 
    INFO_TASK_DESCRIPTION_ENTER_LOCKDOWN_MODE("May be used to cause the server to enter lockdown mode.  While in lockdown mode, the server will reject any connection attempts from remote clients, and will reject any request from non-root users."), 
    INFO_TASK_DESCRIPTION_EXEC("Executes a specified command on the server system with a given set of arguments.  The command to execute must be given as an absolute path, and must be listed in a file containing whitelisted commands in the server's config directory.  The requester must also have the exec-task privilege, and the exec task must be included in the set of allowed tasks defined in the global configuration."), 
    INFO_TASK_DESCRIPTION_EXPORT("May be used to export the contents of a Directory Server backend to LDIF."), 
    INFO_TASK_DESCRIPTION_FILE_RETENTION("Examines files in a specified directory that match a given pattern, retaining the most recent files in accordance with a provided set of count, age, or size criteria, and removing any older files that are outside of that criteria."), 
    INFO_TASK_DESCRIPTION_GENERIC("A generic task which does not require any custom properties."), 
    INFO_TASK_DESCRIPTION_GROOVY_SCRIPTED_TASK("Invoke a task encapsulated in a Groovy script using the UnboundID Server SDK."), 
    INFO_TASK_DESCRIPTION_IMPORT("May be used to import LDIF data into a Directory Server backend."), 
    INFO_TASK_DESCRIPTION_LEAVE_LOCKDOWN_MODE("May be used to cause the server to leave lockdown mode and resume normal operation."), 
    INFO_TASK_DESCRIPTION_REBUILD("May be used to generate or rebuild Directory Server indexes."), 
    INFO_TASK_DESCRIPTION_REENCODE_ENTRIES("This task may be used to cause the server to re-encode all or a specified set of entries in a local DB backend.  This may be used to apply encoding changes around the use of features like compression, encryption, cryptographic digests, and/or uncached attributes or entries."), 
    INFO_TASK_DESCRIPTION_REFRESH_ENCRYPTION_SETTINGS("Cause the Directory Server to immediately reload the encryption settings database from disk in order to make any changes available to the server"), 
    INFO_TASK_DESCRIPTION_RELOAD_GLOBAL_INDEX("Reload the data for one or more entry-balancing global indexes from backend Directory Servers or a peer Directory Proxy Server."), 
    INFO_TASK_DESCRIPTION_RELOAD_HTTP_CONNECTION_HANDLER_CERTIFICATES("Dynamically reload the certificate key and trust stores for all HTTP connection handler instances configured with support for HTTPS."), 
    INFO_TASK_DESCRIPTION_RESTORE("May be used to restore the contents of a Directory Server backend from a backup."), 
    INFO_TASK_DESCRIPTION_ROTATE_LOG("Triggers the rotation for one or more specified log files, or for all applicable log files if no paths are given."), 
    INFO_TASK_DESCRIPTION_SEARCH("May be used to process an internal search within the Directory Server."), 
    INFO_TASK_DESCRIPTION_SHUTDOWN("May be used to shut down or restart the Directory Server."), 
    INFO_TASK_DESCRIPTION_SYNCHRONIZE_ENCRYPTION_SETTINGS("Synchronize the encryption settings definitions across all of the servers in the topology."), 
    INFO_TASK_DESCRIPTION_THIRD_PARTY_TASK("Invoke a third-party task developed using the UnboundID Server SDK."), 
    INFO_TASK_NAME_ADD_SCHEMA_FILE("Add Schema File"), 
    INFO_TASK_NAME_ALERT("Administrative Alert"), 
    INFO_TASK_NAME_AUDIT_DATA_SECURITY("Audit Data Security"), 
    INFO_TASK_NAME_BACKUP("Backup"), 
    INFO_TASK_NAME_DELAY("Delay"), 
    INFO_TASK_NAME_DISCONNECT_CLIENT("Disconnect Client"), 
    INFO_TASK_NAME_DUMP_DB("Dump DB Details"), 
    INFO_TASK_NAME_ENTER_LOCKDOWN_MODE("Enter Lockdown Mode"), 
    INFO_TASK_NAME_EXEC("Execute Command"), 
    INFO_TASK_NAME_EXPORT("LDIF Export"), 
    INFO_TASK_NAME_FILE_RETENTION("File Retention"), 
    INFO_TASK_NAME_GENERIC("Generic"), 
    INFO_TASK_NAME_GROOVY_SCRIPTED_TASK("Groovy-Scripted Task"), 
    INFO_TASK_NAME_IMPORT("LDIF Import"), 
    INFO_TASK_NAME_LEAVE_LOCKDOWN_MODE("Leave Lockdown Mode"), 
    INFO_TASK_NAME_REBUILD("Rebuild Index"), 
    INFO_TASK_NAME_REENCODE_ENTRIES("Re-Encode Entries Task"), 
    INFO_TASK_NAME_REFRESH_ENCRYPTION_SETTINGS("Refresh Encryption Settings Task"), 
    INFO_TASK_NAME_RELOAD_GLOBAL_INDEX("Reload Global Index"), 
    INFO_TASK_NAME_RELOAD_HTTP_CONNECTION_HANDLER_CERTIFICATES("Reload HTTP Connection Handler Certificates"), 
    INFO_TASK_NAME_RESTORE("Restore"), 
    INFO_TASK_NAME_ROTATE_LOG("Rotate Log"), 
    INFO_TASK_NAME_SEARCH("Search"), 
    INFO_TASK_NAME_SHUTDOWN("Shutdown"), 
    INFO_TASK_NAME_SYNCHRONIZE_ENCRYPTION_SETTINGS("Synchronize Encryption Settings"), 
    INFO_TASK_NAME_THIRD_PARTY_TASK("Third-Party Task");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<TaskMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<TaskMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private TaskMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = TaskMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (TaskMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = TaskMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                TaskMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (TaskMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = TaskMessages.MESSAGES.get(this);
        if (f == null) {
            if (TaskMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(TaskMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            TaskMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (TaskMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = TaskMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (TaskMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = TaskMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                TaskMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-task");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<TaskMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<TaskMessages, MessageFormat>(100);
    }
}
