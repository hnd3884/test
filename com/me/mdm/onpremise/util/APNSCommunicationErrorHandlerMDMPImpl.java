package com.me.mdm.onpremise.util;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.mdm.server.ios.apns.APNSCommunicationErrorHandler;

public class APNSCommunicationErrorHandlerMDMPImpl extends APNSCommunicationErrorHandler
{
    public long handleAPNSWakeupFailure(final Throwable ex) {
        final Long err = super.handleAPNSWakeupFailure(ex);
        return err;
    }
    
    protected void handleCommunicationException(final String errorMessage) {
        if (errorMessage != null && errorMessage.contains("Unable to tunnel through")) {
            MDMMessageHandler.getInstance().messageAction("PROXY_NOT_CONFIGURED", (Long)null);
        }
        else {
            MessageProvider.getInstance().unhideMessage("APNS_PORT_BLOCKED");
        }
    }
}
