package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeEthernetPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeEthernetPolicyPayload implements DO2ChromePayload
{
    @Override
    public ChromeEthernetPayload createPayload(final DataObject dataObject) {
        ChromeEthernetPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("EthernetConfig");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ChromeEthernetPayload("1.0", "Ehternet", "Ethernet");
                final int type = (int)row.get("TYPE");
                payload.setType(type);
                if (type == 4) {
                    payload = this.setEthernetEAPSettings(dataObject, payload);
                }
                payload = this.updateProxyConfig(dataObject, payload);
            }
        }
        catch (final Exception ex) {
            DO2ChromeEthernetPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
    
    private ChromeEthernetPayload setEthernetEAPSettings(final DataObject dataObject, final ChromeEthernetPayload payload) throws DataAccessException, JSONException {
        final Iterator iterator = dataObject.getRows("PayloadWifiEnterprise");
        payload.initEAPSettings();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String authProtocol = new ONCPayloadUtil().getAuthProtocol(row);
            payload.setAuthProtocol(authProtocol);
            final int innerProtocol = (int)row.get("INNER_IDENTITY");
            payload.setInnerProtocol(new ONCPayloadUtil().getInnerProtocol(innerProtocol));
            final String identity = (String)row.get("USER_NAME");
            final String anonumousIdentity = (String)row.get("OUTER_IDENTITY");
            String password = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordId = (Long)row.get("PASSWORD_ID");
                password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
            }
            payload.setouterIdentity(anonumousIdentity);
            payload.setIdentity(identity);
            payload.setEnterprisePassword(password);
            final Long certficateID = (Long)row.get("CERTIFICATE_ID");
            payload.setCertificate(certficateID.toString(), false);
        }
        return payload;
    }
    
    private ChromeEthernetPayload updateProxyConfig(final DataObject dataObject, final ChromeEthernetPayload payload) throws Exception {
        final JSONObject proxySettings = new JSONObject();
        final Iterator iterator = dataObject.getRows("PayloadProxyConfig");
        if (iterator.hasNext()) {
            final Row row = iterator.next();
            final int proxyType = (int)row.get("PROXY_TYPE");
            String type = "Direct";
            if (proxyType == 1) {
                final String proxyServer = (String)row.get("PROXY_SERVER");
                final int proxyPort = (int)row.get("PROXY_SERVER_PORT");
                final String byPassUrl = (String)row.get("BYPASS_PROXY_URL");
                final String httpsProxyServer = (String)row.get("HTTPS_PROXY_SERVER");
                final Integer httpsProxyPort = (Integer)row.get("HTTPS_PROXY_PORT");
                final String ftpProxyServer = (String)row.get("FTP_PROXY_SERVER");
                final Integer ftpProxyPort = (Integer)row.get("FTP_PROXY_PORT");
                final String socksProxyServer = (String)row.get("SOCKS_PROXY_SERVER");
                final Integer socksProxyPort = (Integer)row.get("SOCKS_PROXY_PORT");
                type = "Manual";
                proxySettings.put("ExcludeDomains", (Object)byPassUrl);
                final JSONObject manualProxy = new JSONObject();
                final JSONObject httpProxy = new JSONObject();
                final JSONObject httpsProxy = new JSONObject();
                final JSONObject ftpProxy = new JSONObject();
                final JSONObject socksProxy = new JSONObject();
                httpProxy.put("Host", (Object)proxyServer);
                httpProxy.put("Port", proxyPort);
                httpsProxy.put("Host", (Object)httpsProxyServer);
                httpsProxy.put("Port", (Object)httpsProxyPort);
                ftpProxy.put("Host", (Object)ftpProxyServer);
                ftpProxy.put("Port", (Object)ftpProxyPort);
                socksProxy.put("Host", (Object)socksProxyServer);
                socksProxy.put("Port", (Object)socksProxyPort);
                manualProxy.put("HTTPProxy", (Object)httpProxy);
                manualProxy.put("SecureHTTPProxy", (Object)httpsProxy);
                manualProxy.put("FTPProxy", (Object)ftpProxy);
                manualProxy.put("SOCKS", (Object)socksProxy);
                proxySettings.put("Manual", (Object)manualProxy);
            }
            else if (proxyType == 2) {
                type = "PAC";
                final String pacURL = (String)row.get("PROXY_PAC_URL");
                proxySettings.put("PAC", (Object)pacURL);
            }
            proxySettings.put("Type", (Object)type);
        }
        payload.setProxySettings(proxySettings);
        return payload;
    }
}
