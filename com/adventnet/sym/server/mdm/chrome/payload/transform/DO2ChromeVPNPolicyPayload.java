package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeVPNPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeVPNPolicyPayload implements DO2ChromePayload
{
    @Override
    public ChromeVPNPayload createPayload(final DataObject dataObject) {
        ChromeVPNPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("VpnPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ChromeVPNPayload("1.0", "VPN", "VPN");
                final String connectionName = (String)row.get("CONNECTION_NAME");
                payload.setConnName(connectionName);
                final int connectionType = (int)row.get("CONNECTION_TYPE");
                payload.setConnType(connectionType);
                if (connectionType == 0) {
                    payload = this.setL2TPConfig(dataObject, payload);
                }
                else if (connectionType == 13) {
                    payload = this.setOpenVPNConfig(dataObject, payload);
                }
                payload = this.updateProxyConfig(payload, dataObject);
            }
        }
        catch (final Exception ex) {
            DO2ChromeVPNPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
    
    private ChromeVPNPayload setL2TPConfig(final DataObject dataObject, final ChromeVPNPayload payload) throws DataAccessException, JSONException {
        final Iterator iterator = dataObject.getRows("VpnL2TP");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String serverName = (String)row.get("SERVER_NAME");
            final String username = (String)row.get("ACCOUNT");
            String password = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordID = (Long)row.get("PASSWORD_ID");
                password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
            }
            final String sharedSecret = (String)row.get("SHARED_SECRET");
            payload.setServerName(serverName);
            final JSONObject l2tp = new JSONObject();
            l2tp.put("Password", (Object)password);
            l2tp.put("Username", (Object)username);
            payload.setL2TPConfig(l2tp);
        }
        return payload;
    }
    
    private ChromeVPNPayload setOpenVPNConfig(final DataObject dataObject, final ChromeVPNPayload payload) throws DataAccessException, JSONException {
        final Iterator iterator = dataObject.getRows("OpenVPNPolicy");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String serverName = (String)row.get("REMOTE_HOST");
            final Integer serverPort = (Integer)row.get("REMOTE_HOST_PORT");
            final String username = (String)row.get("USERNAME");
            String password = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordID = (Long)row.get("PASSWORD_ID");
                password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordID.toString());
            }
            final int protocl = (int)row.get("PROTOCOL");
            payload.setServerName(serverName);
            final JSONObject openVPN = new JSONObject();
            openVPN.put("Password", (Object)password);
            openVPN.put("Username", (Object)username);
            openVPN.put("Port", (Object)serverPort);
            openVPN.put("Proto", protocl);
            payload.setOpenVPNConfig(openVPN);
        }
        return payload;
    }
    
    private ChromeVPNPayload updateProxyConfig(final ChromeVPNPayload payload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("PayloadProxyConfig");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final JSONObject proxySettings = new JSONObject();
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
            payload.setProxySettings(proxySettings);
        }
        return payload;
    }
}
