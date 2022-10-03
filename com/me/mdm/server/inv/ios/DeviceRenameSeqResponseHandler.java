package com.me.mdm.server.inv.ios;

import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.IOSBaseSeqCmdResponseHandler;

public class DeviceRenameSeqResponseHandler extends IOSBaseSeqCmdResponseHandler
{
    private static final Logger LOGGER;
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        DeviceRenameSeqResponseHandler.LOGGER.log(Level.FINE, "IOS DeviceRename Seq Response Success Handler. Params:{0}", params);
        return super.onSuccess(params);
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        DeviceRenameSeqResponseHandler.LOGGER.log(Level.INFO, "IOS DeviceRename Seq Response failure Handler");
        final Long resourceID = params.optLong("resourceID");
        DeviceCommandRepository.getInstance().assignCommandToDevice("SingletonRestriction", resourceID);
        return super.onFailure(params);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
