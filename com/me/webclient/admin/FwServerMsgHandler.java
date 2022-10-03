package com.me.webclient.admin;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class FwServerMsgHandler implements MsgHandler
{
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
        if (msgName.equalsIgnoreCase("MDMP_FWS_NOT_REACHABLE")) {
            String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            msgContent = msgContent.replace("{0}", request.getAttribute("URL").toString());
            ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
        }
        return msgProperties;
    }
}
