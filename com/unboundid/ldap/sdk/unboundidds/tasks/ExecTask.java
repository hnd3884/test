package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Collections;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExecTask extends Task
{
    static final String EXEC_TASK_CLASS = "com.unboundid.directory.server.tasks.ExecTask";
    private static final String ATTR_COMMAND_PATH = "ds-task-exec-command-path";
    private static final String ATTR_COMMAND_ARGUMENTS = "ds-task-exec-command-arguments";
    private static final String ATTR_COMMAND_OUTPUT_FILE = "ds-task-exec-command-output-file";
    private static final String ATTR_LOG_COMMAND_OUTPUT = "ds-task-exec-log-command-output";
    private static final String ATTR_TASK_STATE_FOR_NONZERO_EXIT_CODE = "ds-task-exec-task-completion-state-for-nonzero-exit-code";
    private static final String ATTR_WORKING_DIRECTORY = "ds-task-exec-working-directory";
    private static final String OC_EXEC_TASK = "ds-task-exec";
    private static final TaskProperty PROPERTY_COMMAND_PATH;
    private static final TaskProperty PROPERTY_COMMAND_ARGUMENTS;
    private static final TaskProperty PROPERTY_COMMAND_OUTPUT_FILE;
    private static final TaskProperty PROPERTY_LOG_COMMAND_OUTPUT;
    private static final TaskProperty PROPERTY_TASK_STATE_FOR_NONZERO_EXIT_CODE;
    private static final TaskProperty PROPERTY_WORKING_DIRECTORY;
    private static final long serialVersionUID = -1647609631634328008L;
    private final Boolean logCommandOutput;
    private final String commandArguments;
    private final String commandOutputFile;
    private final String commandPath;
    private final String taskStateForNonZeroExitCode;
    private final String workingDirectory;
    
    public ExecTask() {
        this.commandPath = null;
        this.commandArguments = null;
        this.commandOutputFile = null;
        this.logCommandOutput = null;
        this.taskStateForNonZeroExitCode = null;
        this.workingDirectory = null;
    }
    
    public ExecTask(final String commandPath, final String commandArguments, final String commandOutputFile, final Boolean logCommandOutput, final TaskState taskStateForNonZeroExitCode) throws TaskException {
        this(null, commandPath, commandArguments, commandOutputFile, logCommandOutput, taskStateForNonZeroExitCode, null, null, null, null, null);
    }
    
    public ExecTask(final String commandPath, final String commandArguments, final String commandOutputFile, final Boolean logCommandOutput, final TaskState taskStateForNonZeroExitCode, final String workingDirectory) throws TaskException {
        this(null, commandPath, commandArguments, commandOutputFile, logCommandOutput, taskStateForNonZeroExitCode, workingDirectory, null, null, null, null, null, null, null, null, null, null);
    }
    
    public ExecTask(final String taskID, final String commandPath, final String commandArguments, final String commandOutputFile, final Boolean logCommandOutput, final TaskState taskStateForNonZeroExitCode, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnCompletion, final List<String> notifyOnError) throws TaskException {
        this(taskID, commandPath, commandArguments, commandOutputFile, logCommandOutput, taskStateForNonZeroExitCode, scheduledStartTime, dependencyIDs, failedDependencyAction, null, notifyOnCompletion, null, notifyOnError, null, null, null);
    }
    
    public ExecTask(final String taskID, final String commandPath, final String commandArguments, final String commandOutputFile, final Boolean logCommandOutput, final TaskState taskStateForNonZeroExitCode, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) throws TaskException {
        this(taskID, commandPath, commandArguments, commandOutputFile, logCommandOutput, taskStateForNonZeroExitCode, null, scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
    }
    
    public ExecTask(final String taskID, final String commandPath, final String commandArguments, final String commandOutputFile, final Boolean logCommandOutput, final TaskState taskStateForNonZeroExitCode, final String workingDirectory, final Date scheduledStartTime, final List<String> dependencyIDs, final FailedDependencyAction failedDependencyAction, final List<String> notifyOnStart, final List<String> notifyOnCompletion, final List<String> notifyOnSuccess, final List<String> notifyOnError, final Boolean alertOnStart, final Boolean alertOnSuccess, final Boolean alertOnError) throws TaskException {
        super(taskID, "com.unboundid.directory.server.tasks.ExecTask", scheduledStartTime, dependencyIDs, failedDependencyAction, notifyOnStart, notifyOnCompletion, notifyOnSuccess, notifyOnError, alertOnStart, alertOnSuccess, alertOnError);
        this.commandPath = commandPath;
        this.commandArguments = commandArguments;
        this.commandOutputFile = commandOutputFile;
        this.logCommandOutput = logCommandOutput;
        this.workingDirectory = workingDirectory;
        if (commandPath == null || commandPath.isEmpty()) {
            throw new TaskException(TaskMessages.ERR_EXEC_MISSING_PATH.get());
        }
        if (taskStateForNonZeroExitCode == null) {
            this.taskStateForNonZeroExitCode = null;
        }
        else {
            switch (taskStateForNonZeroExitCode) {
                case STOPPED_BY_ERROR:
                case COMPLETED_WITH_ERRORS:
                case COMPLETED_SUCCESSFULLY: {
                    this.taskStateForNonZeroExitCode = taskStateForNonZeroExitCode.name();
                    break;
                }
                default: {
                    throw new TaskException(TaskMessages.ERR_EXEC_INVALID_STATE_FOR_NONZERO_EXIT_CODE.get(TaskState.STOPPED_BY_ERROR.name(), TaskState.COMPLETED_WITH_ERRORS.name(), TaskState.COMPLETED_SUCCESSFULLY.name()));
                }
            }
        }
    }
    
    public ExecTask(final Entry entry) throws TaskException {
        super(entry);
        this.commandPath = entry.getAttributeValue("ds-task-exec-command-path");
        if (this.commandPath == null) {
            throw new TaskException(TaskMessages.ERR_EXEC_ENTRY_MISSING_COMMAND_PATH.get(entry.getDN(), "ds-task-exec-command-path"));
        }
        this.commandArguments = entry.getAttributeValue("ds-task-exec-command-arguments");
        this.commandOutputFile = entry.getAttributeValue("ds-task-exec-command-output-file");
        this.logCommandOutput = entry.getAttributeValueAsBoolean("ds-task-exec-log-command-output");
        this.taskStateForNonZeroExitCode = entry.getAttributeValue("ds-task-exec-task-completion-state-for-nonzero-exit-code");
        this.workingDirectory = entry.getAttributeValue("ds-task-exec-working-directory");
    }
    
    public ExecTask(final Map<TaskProperty, List<Object>> properties) throws TaskException {
        super("com.unboundid.directory.server.tasks.ExecTask", properties);
        String path = null;
        String arguments = null;
        String outputFile = null;
        Boolean logOutput = null;
        String nonZeroExitState = null;
        String workingDir = null;
        for (final Map.Entry<TaskProperty, List<Object>> entry : properties.entrySet()) {
            final TaskProperty p = entry.getKey();
            final String attrName = StaticUtils.toLowerCase(p.getAttributeName());
            final List<Object> values = entry.getValue();
            if (attrName.equals("ds-task-exec-command-path")) {
                path = Task.parseString(p, values, path);
            }
            else if (attrName.equals("ds-task-exec-command-arguments")) {
                arguments = Task.parseString(p, values, arguments);
            }
            else if (attrName.equals("ds-task-exec-command-output-file")) {
                outputFile = Task.parseString(p, values, outputFile);
            }
            else if (attrName.equals("ds-task-exec-log-command-output")) {
                logOutput = Task.parseBoolean(p, values, logOutput);
            }
            else if (attrName.equals("ds-task-exec-task-completion-state-for-nonzero-exit-code")) {
                nonZeroExitState = Task.parseString(p, values, nonZeroExitState);
            }
            else {
                if (!attrName.equals("ds-task-exec-working-directory")) {
                    continue;
                }
                workingDir = Task.parseString(p, values, workingDir);
            }
        }
        this.commandPath = path;
        this.commandArguments = arguments;
        this.commandOutputFile = outputFile;
        this.logCommandOutput = logOutput;
        this.taskStateForNonZeroExitCode = nonZeroExitState;
        this.workingDirectory = workingDir;
        if (this.commandPath == null) {
            throw new TaskException(TaskMessages.ERR_EXEC_PROPERTIES_MISSING_COMMAND_PATH.get());
        }
    }
    
    @Override
    public String getTaskName() {
        return TaskMessages.INFO_TASK_NAME_EXEC.get();
    }
    
    @Override
    public String getTaskDescription() {
        return TaskMessages.INFO_TASK_DESCRIPTION_EXEC.get();
    }
    
    public String getCommandPath() {
        return this.commandPath;
    }
    
    public String getCommandArguments() {
        return this.commandArguments;
    }
    
    public String getCommandOutputFile() {
        return this.commandOutputFile;
    }
    
    public Boolean logCommandOutput() {
        return this.logCommandOutput;
    }
    
    public String getTaskStateForNonZeroExitCode() {
        return this.taskStateForNonZeroExitCode;
    }
    
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }
    
    @Override
    protected List<String> getAdditionalObjectClasses() {
        return Collections.singletonList("ds-task-exec");
    }
    
    @Override
    protected List<Attribute> getAdditionalAttributes() {
        final LinkedList<Attribute> attrList = new LinkedList<Attribute>();
        attrList.add(new Attribute("ds-task-exec-command-path", this.commandPath));
        if (this.commandArguments != null) {
            attrList.add(new Attribute("ds-task-exec-command-arguments", this.commandArguments));
        }
        if (this.commandOutputFile != null) {
            attrList.add(new Attribute("ds-task-exec-command-output-file", this.commandOutputFile));
        }
        if (this.logCommandOutput != null) {
            attrList.add(new Attribute("ds-task-exec-log-command-output", String.valueOf(this.logCommandOutput)));
        }
        if (this.taskStateForNonZeroExitCode != null) {
            attrList.add(new Attribute("ds-task-exec-task-completion-state-for-nonzero-exit-code", this.taskStateForNonZeroExitCode));
        }
        if (this.workingDirectory != null) {
            attrList.add(new Attribute("ds-task-exec-working-directory", this.workingDirectory));
        }
        return attrList;
    }
    
    @Override
    public List<TaskProperty> getTaskSpecificProperties() {
        return Collections.unmodifiableList((List<? extends TaskProperty>)Arrays.asList(ExecTask.PROPERTY_COMMAND_PATH, ExecTask.PROPERTY_COMMAND_ARGUMENTS, ExecTask.PROPERTY_COMMAND_OUTPUT_FILE, ExecTask.PROPERTY_LOG_COMMAND_OUTPUT, ExecTask.PROPERTY_TASK_STATE_FOR_NONZERO_EXIT_CODE, ExecTask.PROPERTY_WORKING_DIRECTORY));
    }
    
    @Override
    public Map<TaskProperty, List<Object>> getTaskPropertyValues() {
        final LinkedHashMap<TaskProperty, List<Object>> props = new LinkedHashMap<TaskProperty, List<Object>>(StaticUtils.computeMapCapacity(StaticUtils.computeMapCapacity(6)));
        props.put(ExecTask.PROPERTY_COMMAND_PATH, (List<Object>)Collections.singletonList(this.commandPath));
        if (this.commandArguments != null) {
            props.put(ExecTask.PROPERTY_COMMAND_ARGUMENTS, (List<Object>)Collections.singletonList(this.commandArguments));
        }
        if (this.commandOutputFile != null) {
            props.put(ExecTask.PROPERTY_COMMAND_OUTPUT_FILE, (List<Object>)Collections.singletonList(this.commandOutputFile));
        }
        if (this.logCommandOutput != null) {
            props.put(ExecTask.PROPERTY_LOG_COMMAND_OUTPUT, (List<Object>)Collections.singletonList(this.logCommandOutput));
        }
        if (this.taskStateForNonZeroExitCode != null) {
            props.put(ExecTask.PROPERTY_TASK_STATE_FOR_NONZERO_EXIT_CODE, (List<Object>)Collections.singletonList(this.taskStateForNonZeroExitCode));
        }
        if (this.workingDirectory != null) {
            props.put(ExecTask.PROPERTY_WORKING_DIRECTORY, (List<Object>)Collections.singletonList(this.workingDirectory));
        }
        return Collections.unmodifiableMap((Map<? extends TaskProperty, ? extends List<Object>>)props);
    }
    
    static {
        PROPERTY_COMMAND_PATH = new TaskProperty("ds-task-exec-command-path", TaskMessages.INFO_EXEC_DISPLAY_NAME_COMMAND_PATH.get(), TaskMessages.INFO_EXEC_DESCRIPTION_COMMAND_PATH.get(), String.class, true, false, false);
        PROPERTY_COMMAND_ARGUMENTS = new TaskProperty("ds-task-exec-command-arguments", TaskMessages.INFO_EXEC_DISPLAY_NAME_COMMAND_ARGUMENTS.get(), TaskMessages.INFO_EXEC_DESCRIPTION_COMMAND_ARGUMENTS.get(), String.class, false, false, false);
        PROPERTY_COMMAND_OUTPUT_FILE = new TaskProperty("ds-task-exec-command-output-file", TaskMessages.INFO_EXEC_DISPLAY_NAME_COMMAND_OUTPUT_FILE.get(), TaskMessages.INFO_EXEC_DESCRIPTION_COMMAND_OUTPUT_FILE.get(), String.class, false, false, false);
        PROPERTY_LOG_COMMAND_OUTPUT = new TaskProperty("ds-task-exec-log-command-output", TaskMessages.INFO_EXEC_DISPLAY_NAME_LOG_COMMAND_OUTPUT.get(), TaskMessages.INFO_EXEC_DESCRIPTION_LOG_COMMAND_OUTPUT.get(), Boolean.class, false, false, false);
        PROPERTY_TASK_STATE_FOR_NONZERO_EXIT_CODE = new TaskProperty("ds-task-exec-task-completion-state-for-nonzero-exit-code", TaskMessages.INFO_EXEC_DISPLAY_NAME_TASK_STATE_FOR_NONZERO_EXIT_CODE.get(), TaskMessages.INFO_EXEC_DESCRIPTION_TASK_STATE_FOR_NONZERO_EXIT_CODE.get(), String.class, false, false, false, new String[] { "STOPPED_BY_ERROR", "STOPPED-BY-ERROR", "COMPLETED_WITH_ERRORS", "COMPLETED-WITH-ERRORS", "COMPLETED_SUCCESSFULLY", "COMPLETED-SUCCESSFULLY" });
        PROPERTY_WORKING_DIRECTORY = new TaskProperty("ds-task-exec-working-directory", TaskMessages.INFO_EXEC_DISPLAY_NAME_WORKING_DIRECTORY.get(), TaskMessages.INFO_EXEC_DESCRIPTION_WORKING_DIRECTORY.get(), String.class, false, false, false);
    }
}
