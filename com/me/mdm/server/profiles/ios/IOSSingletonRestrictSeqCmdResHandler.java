package com.me.mdm.server.profiles.ios;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.ios.IOSBaseSeqCmdResponseHandler;

public class IOSSingletonRestrictSeqCmdResHandler extends IOSBaseSeqCmdResponseHandler
{
    private static final Logger LOGGER;
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        IOSSingletonRestrictSeqCmdResHandler.LOGGER.log(Level.FINE, "IOS Singleton restriction success handler. Params:{0}", params);
        return super.onSuccess(params);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
