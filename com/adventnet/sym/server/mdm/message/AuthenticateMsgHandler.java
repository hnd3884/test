package com.adventnet.sym.server.mdm.message;

import java.util.logging.Logger;

public class AuthenticateMsgHandler implements MessageListener
{
    public static Logger logger;
    
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        return Boolean.TRUE;
    }
    
    static {
        AuthenticateMsgHandler.logger = Logger.getLogger("MDMLogger");
    }
}
