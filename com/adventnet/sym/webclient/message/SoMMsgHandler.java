package com.adventnet.sym.webclient.message;

import java.util.Hashtable;
import java.util.logging.Level;
import com.adventnet.sym.webclient.common.SYMClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class SoMMsgHandler implements MsgHandler
{
    private Logger somLogger;
    
    public SoMMsgHandler() {
        this.somLogger = Logger.getLogger("SoMLogger");
    }
    
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        try {
            final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
            String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            if (msgName.equalsIgnoreCase("DOMAIN_PASSWORD_CHANGED_WRITE")) {
                final String domains = SYMClientUtil.getPasswordChangedDomainWrite();
                msgContent = msgContent.replace("{0}", domains);
            }
            else if (msgName.equalsIgnoreCase("DOMAIN_PASSWORD_CHANGED_READ")) {
                final String domains = SYMClientUtil.getPasswordChangedRead();
                msgContent = msgContent.replace("{0}", domains);
            }
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        }
        catch (final Exception e) {
            this.somLogger.log(Level.WARNING, "Exception in SoMMsgHandler...............", e);
        }
        return msgProperties;
    }
}
