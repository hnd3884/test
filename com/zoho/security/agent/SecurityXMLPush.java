package com.zoho.security.agent;

import java.util.List;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import java.net.InetAddress;
import com.adventnet.iam.security.SecurityFilterProperties;
import com.adventnet.iam.security.SecurityXML;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.logging.Logger;

public class SecurityXMLPush
{
    private static final Logger LOGGER;
    private static String appSenseSecurityXMLUrl;
    
    public static void pushSecurityXML(final JSONArray hashes) throws Exception {
        final List<MultipartFile> list = new ArrayList<MultipartFile>();
        for (int i = 0; i < hashes.length(); ++i) {
            final SecurityXML xml = AppSenseAgent.getXmlFilesAsObject().get(hashes.getString(i));
            final MultipartFile file = new MultipartFile("xmlfile", xml.getFile(), "application/xml");
            list.add(file);
        }
        final String service = SecurityFilterProperties.getServiceName();
        final String account = System.getProperty("user.name", "sas");
        final String ip = InetAddress.getLocalHost().getHostAddress();
        final String params = "?iscsignature=" + SecurityUtil.sign() + "&service=" + service + "&account=" + account + "&ip=" + ip;
        final HttpCall call = new HttpCall(SecurityXMLPush.appSenseSecurityXMLUrl + params, "POST", list);
        call.triggerRequest();
        final int status = call.getResponseCode();
        if (200 != status) {
            SecurityXMLPush.LOGGER.log(Level.WARNING, "Security XML Push failed. Response Code : {0}", status);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityXMLPush.class.getName());
        SecurityXMLPush.appSenseSecurityXMLUrl = "http://appsense/zsecagent/v1/xmlfile";
    }
}
