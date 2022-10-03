package com.me.mdm.server.security.mac.recoverylock;

import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecoveryLockSequentialCommand
{
    private static final Logger LOGGER;
    private static final String SUB_CMD_PRE_PROCESSOR = "com.me.mdm.server.profiles.mac.recoverylock.preprocess.RecoveryLockSeqCommandPreProcessor";
    
    public static void addSequentialCommand(final long collectionId) {
        try {
            if (!recLockSeqCmdExistsForCollection(collectionId)) {
                RecoveryLockSequentialCommand.LOGGER.log(Level.INFO, "Sequential command doesn't exist for collection: {0}", new Object[] { collectionId });
                final JSONObject recLockSeqCmd = prepareSeqCmd(collectionId);
                SeqCmdDBUtil.getInstance().addSequentialCommands(recLockSeqCmd);
                RecoveryLockSequentialCommand.LOGGER.log(Level.INFO, "Sequential command added for collection: {0}", new Object[] { collectionId });
            }
        }
        catch (final Exception e) {
            RecoveryLockSequentialCommand.LOGGER.log(Level.SEVERE, e, () -> "Exception while adding Recovery Lock sequential command : " + n);
        }
    }
    
    private static boolean recLockSeqCmdExistsForCollection(final long collectionId) throws Exception {
        final String recLockCmdUuid = getRecoveryLockBaseCommandUuid(collectionId);
        final Long baseCommandId = DeviceCommandRepository.getInstance().getCommandID(recLockCmdUuid);
        final Long seqCommandID = (Long)DBUtil.getValueFromDB("MdCommandToSequentialCommand", "COMMAND_ID", (Object)baseCommandId, "SEQUENTIAL_COMMAND_ID");
        RecoveryLockSequentialCommand.LOGGER.log(Level.INFO, "Sequential command ID for collection: {0} is {1}, Base Command: {2}", new Object[] { collectionId, seqCommandID, baseCommandId });
        return seqCommandID != null;
    }
    
    private static JSONObject prepareSeqCmd(final long collectionId) {
        final JSONArray subCmd = prepareSubCommands(collectionId);
        final JSONObject seqCmd = constructSeqCmd(collectionId, subCmd);
        final JSONArray seqCommands = new JSONArray();
        seqCommands.put((Object)seqCmd);
        final JSONObject seqCmdWrapper = new JSONObject();
        seqCmdWrapper.put("SequentialCommands", (Object)seqCommands);
        return seqCmdWrapper;
    }
    
    private static JSONObject constructSeqCmd(final long collectionId, final JSONArray subCmd) {
        final long baseCmdId = addBaseCmd(collectionId);
        final long seqCmdId = addSeqCmd(collectionId);
        RecoveryLockSequentialCommand.LOGGER.log(Level.INFO, "Base Command and Sequential Command added for collection: {0}. Base Command: {1}, Seq Command: {2}", new Object[] { collectionId, baseCmdId, seqCmdId });
        final JSONObject seqCmd = new JSONObject();
        seqCmd.put("basecmdID", baseCmdId);
        seqCmd.put("SequentialCommandId", seqCmdId);
        seqCmd.put("allowImmediateProcessing", false);
        seqCmd.put("params", (Object)getSequentialCmdLevelParams(collectionId));
        seqCmd.put("timeout", 60000);
        seqCmd.put("subCommands", (Object)subCmd);
        return seqCmd;
    }
    
    private static JSONObject getSequentialCmdLevelParams(final long collectionId) {
        final JSONObject cmdLevelParams = new JSONObject();
        cmdLevelParams.put("CollectionID", collectionId);
        return cmdLevelParams;
    }
    
    private static long addSeqCmd(final long collectionId) {
        final String commandUuid = getRecoveryLockSeqCommandUuid(collectionId);
        return DeviceCommandRepository.getInstance().addSequentialCommand(commandUuid);
    }
    
    private static long addBaseCmd(final long collectionId) {
        final String commandUuid = getRecoveryLockBaseCommandUuid(collectionId);
        return DeviceCommandRepository.getInstance().addCommand(commandUuid);
    }
    
    private static JSONArray prepareSubCommands(final long collectionId) {
        final JSONArray subCmdArray = new JSONArray();
        subCmdArray.put((Object)createSubCommand(1, RecoveryLock.PRE_SECURITY.command));
        subCmdArray.put((Object)createSubCommand(2, RecoveryLock.VERIFY_PASSWORD.command));
        subCmdArray.put((Object)createSubCommand(3, getRecoveryLockBaseCommandUuid(collectionId)));
        subCmdArray.put((Object)createSubCommand(4, RecoveryLock.CLEAR_PASSWORD.command));
        subCmdArray.put((Object)createSubCommand(5, RecoveryLock.POST_SECURITY.command));
        RecoveryLockSequentialCommand.LOGGER.log(Level.INFO, "Sequential Sub Commands created for collection: {0}", new Object[] { collectionId });
        return subCmdArray;
    }
    
    private static JSONObject createSubCommand(final int order, final String commandName) {
        final JSONObject subCommand = new JSONObject();
        final long commandId = DeviceCommandRepository.getInstance().addCommand(commandName);
        subCommand.put("cmd_id", commandId);
        subCommand.put("order", order);
        subCommand.put("handler", (Object)"com.me.mdm.server.profiles.mac.recoverylock.preprocess.RecoveryLockSeqCommandPreProcessor");
        return subCommand;
    }
    
    private static String getRecoveryLockSeqCommandUuid(final Long collectionID) {
        return "Sequential;".concat("SetRecoveryLock").concat(";Colln=").concat(collectionID.toString());
    }
    
    private static String getRecoveryLockBaseCommandUuid(final Long collectionID) {
        return "SetRecoveryLock".concat(";Colln=").concat(collectionID.toString());
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
