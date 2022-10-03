package com.me.idps.core.upgrade;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;

public class AzureOAuth210902
{
    private boolean closeMsgForCustomer(final String msgName, final Long customerID) {
        final boolean succeddedInClosing = MessageProvider.getInstance().hideMessage(msgName, customerID);
        IDPSlogger.UPGRADE.log(Level.INFO, "closed {0} for {1} : {2}", new Object[] { msgName, customerID, succeddedInClosing });
        return succeddedInClosing;
    }
    
    public void handleUpgrade(final Long customerID) {
        boolean succeddedInClosing = true;
        succeddedInClosing &= this.closeMsgForCustomer("IDP_RE_OAUTH", customerID);
        succeddedInClosing &= this.closeMsgForCustomer("IDP_AZURE_OAUTH_MSG", customerID);
        succeddedInClosing &= this.closeMsgForCustomer("IDP_AZURE_INVALID_CLIENT_MSG", customerID);
        if (!succeddedInClosing) {
            ApiFactoryProvider.getCacheAccessAPI().putCache("AzureOAuth210902", (Object)true, 2);
        }
        else {
            ApiFactoryProvider.getCacheAccessAPI().removeCache("AzureOAuth210902", 2);
        }
    }
}
