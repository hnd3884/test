package com.adventnet.sym.server.mdm.message;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.HashMap;
import java.util.logging.Logger;

public class MDMMessageHandler
{
    public static Logger logger;
    private static MDMMessageHandler messageHandler;
    private static HashMap<String, MessageListener> handlerMap;
    
    public static MDMMessageHandler getInstance() {
        if (MDMMessageHandler.messageHandler == null) {
            MDMMessageHandler.messageHandler = new MDMMessageHandler();
            MDMMessageHandler.handlerMap = getMessageHandler();
        }
        return MDMMessageHandler.messageHandler;
    }
    
    public void messageAction(final String messageConstant, final Long customerId) {
        final MessageListener msgLister = MDMMessageHandler.handlerMap.get(messageConstant);
        final Boolean isClose = msgLister.getMessageStatus(customerId);
        if (isClose) {
            MessageProvider.getInstance().hideMessage(messageConstant, customerId);
        }
        else {
            MessageProvider.getInstance().unhideMessage(messageConstant, customerId);
        }
    }
    
    private static HashMap getMessageHandler() {
        final HashMap<String, MessageListener> handler = new HashMap<String, MessageListener>();
        handler.put("NO_DEVICE_ENROLLED", new DeviceEnrollMessageHandler());
        handler.put("NO_PROFILE_ADDED", new ProfileMessageHandler());
        handler.put("NO_APP_ADDED", new AppMessageHandler());
        handler.put("NO_GROUP_ADDED", new GroupMessageHandler());
        handler.put("AUTHENTICATE_DEVICE_ENROLLMENT", new AuthenticateMsgHandler());
        handler.put("PROXY_NOT_CONFIGURED", new ProxyMsgHandler());
        handler.put("NAT_RECOMMENDATION", new NatSettingsHandler());
        handler.put("AET_NOT_UPLOADED", new AETMessageHandler());
        handler.put("AET_NOT_UPLOADED_INV", new AETMessageHandler());
        handler.put("LICENSE_LIMIT_REACHED", new LicenseMessageHandler());
        handler.put("DOWNLOAD_PPKG_TOOL", new DownloadPPKGToolMessageHandler());
        handler.put("DOWNLOAD_LAPTOP_TOOL", new DownloadLaptopEnrollmentToolMessageHandler());
        handler.put("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", new MSPLicenseMessageHandler());
        handler.put("SSL_CERTIFICATE_EXPIRED", new SSLCertificateMessageHandler());
        handler.put("MAIL_SERVER_NOT_CONFIGURED", new MailServerMessageHandler());
        handler.put("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", new UEMCentralLicenseMessageHandler());
        handler.put("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", new UEMCentralLicenseWarningMessageHandler());
        return handler;
    }
    
    public static void addMessageHandler(final String constant, final MessageListener messageListener) {
        MDMMessageHandler.handlerMap.put(constant, messageListener);
    }
    
    static {
        MDMMessageHandler.logger = Logger.getLogger("MDMLogger");
        MDMMessageHandler.messageHandler = null;
        MDMMessageHandler.handlerMap = new HashMap<String, MessageListener>();
    }
}
