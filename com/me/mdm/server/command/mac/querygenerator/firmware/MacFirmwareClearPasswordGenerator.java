package com.me.mdm.server.command.mac.querygenerator.firmware;

import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacPayloadHandler;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class MacFirmwareClearPasswordGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        final String commandUUID = deviceCommand.commandUUID;
        switch (commandUUID) {
            case "MacFirmwareClearPasscode": {
                final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, deviceCommand.commandUUID);
                if (subCommand != null) {
                    final JSONObject commandParams = subCommand.params;
                    final JSONObject commandScopeParams = commandParams.getJSONObject("cmdScopeParams");
                    final Long firmwarePasswordID = commandScopeParams.getLong("existingPasswordID");
                    final String existingPassword = MacFirmwareUtil.getMDMManagedPassword(firmwarePasswordID);
                    return MacPayloadHandler.getInstance().createClearFirmwarePasswordCommand(existingPassword).toString();
                }
                break;
            }
        }
        return null;
    }
}
