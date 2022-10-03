package com.me.mdm.server.command.mac.querygenerator.firmware;

import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class MacFirmwareVerifyFirmwarePasswordGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        final int i = 0;
        final String commandUUID = deviceCommand.commandUUID;
        switch (commandUUID) {
            case "MacFirmwareVerifyPassword": {
                final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, deviceCommand.commandUUID);
                if (subCommand != null) {
                    final JSONObject commandParams = subCommand.params;
                    final Long currentPasswordID = commandParams.getJSONObject("cmdScopeParams").getLong("existingPasswordID");
                    final String currentPwd = MacFirmwareUtil.getMDMManagedPassword(currentPasswordID);
                    return MacPayloadHandler.getInstance().createVerifyFirmwarePasswordCommand(currentPwd).toString();
                }
                break;
            }
        }
        return null;
    }
}
