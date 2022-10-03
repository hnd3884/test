package com.me.mdm.onpremise.server.settings.proxy;

import java.util.Hashtable;
import java.util.StringTokenizer;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class ProxyCertificateMsgHandler implements MsgHandler
{
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) throws Exception {
        final String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
        ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent.replace("{0}", this.getCertName()));
        return msgProperties;
    }
    
    private String getCertName() {
        final String certName = SyMUtil.getSyMParameter("MDM_PROXY_CERT_NAME");
        if (certName != null) {
            final StringTokenizer tokenizer = new StringTokenizer(certName, ",");
            while (tokenizer.hasMoreElements()) {
                final String token = ((String)tokenizer.nextElement()).trim();
                if (token.startsWith("CN=")) {
                    final String commonName = token.substring(3);
                    if (commonName.length() > 0) {
                        return commonName;
                    }
                    continue;
                }
            }
        }
        return certName;
    }
}
