package com.me.mdm.server.profiles.mac.configresponseprocessor;

import java.util.HashMap;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class MacFirmwareFirmwarePostSecurityInfoResponseProcessor extends MacFirmwareSequentialCommandGeneralResponseProcessor
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        final Long resourceID = JSONUtil.optLongForUVH(params, "resourceId", Long.valueOf(-1L));
        final String commandUDID = params.optString("strCommandUuid");
        try {
            params = super.processSeqQueuedCommand(params);
            final String strData = String.valueOf(params.get("strData"));
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("SecurityInfo", strData);
            final HashMap hsmap = PlistWrapper.getInstance().getHashFromDict(nsDict);
            MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, hsmap);
            new MacFirmwareSucessHandler().successHandler(resourceID);
            SeqCmdRepository.getInstance().processSeqCommand(params);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacFirmware: Exception in " + this.getClass().getName() + " processQueuedCommand for params " + params, ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUDID);
        }
        return params;
    }
}
