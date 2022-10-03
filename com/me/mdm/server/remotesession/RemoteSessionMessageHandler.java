package com.me.mdm.server.remotesession;

import java.util.logging.Level;
import java.net.URL;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class RemoteSessionMessageHandler
{
    private static final Logger LOGGER;
    
    public JSONObject getRemoteSessionInfo(final Long resourceID, final Long customerId) {
        final JSONObject remoteSessionInfo = new JSONObject();
        String sessionKey = null;
        URL assistUrl = null;
        try {
            final RemoteSessionManager remoteSessionMgr = new RemoteSessionManager();
            final int status = remoteSessionMgr.getSessionStatus(resourceID);
            sessionKey = remoteSessionMgr.getSessionKey(resourceID);
            assistUrl = new URL(MDMApiFactoryProvider.getAssistAuthTokenHandler().getAssistSessionUrl(customerId));
            if (status != remoteSessionMgr.getSessionStoppedCode()) {
                remoteSessionInfo.put("SessionKey", (Object)sessionKey);
                remoteSessionInfo.put("SessionAppServerUrl", (Object)(assistUrl.getProtocol() + "://" + assistUrl.getAuthority()));
            }
            else {
                remoteSessionInfo.put("Status", (Object)"RemoteSessionInfoNotAvailable");
                remoteSessionInfo.put("StatusCode", remoteSessionMgr.getNoSessionInfoErrorcode());
            }
        }
        catch (final Exception ex) {
            RemoteSessionMessageHandler.LOGGER.log(Level.SEVERE, "Exception in getting the getRemoteSessionInfo", ex);
        }
        return remoteSessionInfo;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
