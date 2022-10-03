package com.me.mdm.server.seqcommands;

public class SeqCmdConstants
{
    public static final String SEQUENTIAL_COMMANDS = "SequentialCommands";
    public static final String SEQUENTIAL_COMMAND_ID = "SequentialCommandId";
    public static final String SUB_COMMANDS = "subCommands";
    public static final String BASE_CMD_ID = "basecmdID";
    public static final String COMMAND_ID = "cmd_id";
    public static final String ORDER = "order";
    public static final String HANDLER = "handler";
    public static final String ACTION = "action";
    public static final String RESOURCE_ID = "resourceID";
    public static final String PARAMS = "params";
    public static final String IS_NOTIFY = "isNotify";
    public static final String COMMAND_UUID = "commandUUID";
    public static final String CURRENT_COMMAND_PARAMS = "CurCmdParam";
    public static final String PREVIOUS_COMMAND_PARAMS = "PrevCmdParams";
    public static final String COMMAND_SCOPE_PARAMS = "cmdScopeParams";
    public static final String INITIAL_PARAMS = "initialParams";
    public static final String QUEUED_COMMANDS = "QueuedCommands";
    public static final String COMMAND_LEVEL_PARAMS = "CommandLevelParams";
    public static final String TIMEOUT = "timeout";
    public static final String ALLOW_IMMEDIATE_PROCESSING = "allowImmediateProcessing";
    public static final SequentialSubCommand NO_SEQUENTIAL_COMMAND_EXECUTING;
    public static final int NO_SEQUENTIAL_COMMAND_FOR_BASE_CMD = -1;
    public static final SequentialSubCommand NO_NEXT_SUB_COMMAND;
    public static final Long NO_NEXT_COMMAND;
    public static final int FIRST_COMMAND = 1;
    public static final Long INITIALIZE;
    public static final int PROCESS_LATER = 4;
    public static final int NOT_NOW = 5;
    public static final int RETRY_HANDLER = 3;
    public static final int FAILURE_HANDLER = 2;
    public static final int SUCCESS_HANDLER = 1;
    public static final Long ABORT_COMMAND;
    public static final String CMD_HANDLER = "handler";
    public static final String CMD_ORDER = "order";
    public static final String CMD_ID = "cmd_id";
    public static final String SUBCOMMANDS = "subCommands";
    public static final String BASECMDID = "basecmdID";
    public static final String SEQCMDID = "SequentialCommandId";
    public static final String CMD_TIMEOUT = "timeout";
    public static final String SEQCMDS = "SequentialCommands";
    public static final String RETRYCNT = "retryCount";
    
    static {
        NO_SEQUENTIAL_COMMAND_EXECUTING = null;
        NO_NEXT_SUB_COMMAND = null;
        NO_NEXT_COMMAND = -1L;
        INITIALIZE = -1L;
        ABORT_COMMAND = -2L;
    }
}
