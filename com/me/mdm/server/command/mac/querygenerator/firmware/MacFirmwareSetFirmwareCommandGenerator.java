package com.me.mdm.server.command.mac.querygenerator.firmware;

import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class MacFirmwareSetFirmwareCommandGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        final String s = deviceCommand.commandUUID.split(";")[0];
        switch (s) {
            case "MacFirmwareSetPasscode": {
                final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, deviceCommand.commandUUID);
                if (subCommand != null) {
                    final JSONObject commandParams = subCommand.params;
                    final JSONObject commandScopeParams = commandParams.getJSONObject("cmdScopeParams");
                    final Long currentPasswordID = commandScopeParams.optLong("existingPasswordID", -1L);
                    final Long newPasswordID = commandScopeParams.getLong("newPasswordID");
                    String newPassword = null;
                    String currentPwd = null;
                    if (currentPasswordID != -1L) {
                        currentPwd = MacFirmwareUtil.getMDMManagedPassword(currentPasswordID);
                    }
                    newPassword = MacFirmwareUtil.getMDMManagedPassword(newPasswordID);
                    return MacPayloadHandler.getInstance().createSetFirmwarePasswordCommand(newPassword, currentPwd, deviceCommand.commandUUID).toString();
                }
                break;
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
