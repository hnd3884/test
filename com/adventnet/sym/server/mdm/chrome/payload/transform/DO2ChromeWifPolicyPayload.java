package com.adventnet.sym.server.mdm.chrome.payload.transform;

import com.adventnet.sym.server.mdm.chrome.payload.ChromePayload;
import org.json.JSONObject;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeWifiPayload;
import com.adventnet.persistence.DataObject;

public class DO2ChromeWifPolicyPayload implements DO2ChromePayload
{
    @Override
    public ChromeWifiPayload createPayload(final DataObject dataObject) {
        ChromeWifiPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("WifiPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new ChromeWifiPayload("1.0", "WiFi", "WiFi");
                final String ssid = (String)row.get("SERVICE_SET_IDENTIFIER");
                final boolean autoJoin = (boolean)row.get("AUTO_JOIN");
                final int securityType = (int)row.get("SECURITY_TYPE");
                final boolean hiddenNetwork = (boolean)row.get("HIDDEN_NETWORK");
                String wifiType = "None";
                payload.setSSID(ssid);
                payload.setAutoJoin(autoJoin);
                payload.setHiddenNetwork(hiddenNetwork);
                switch (securityType) {
                    case 0: {
                        wifiType = "None";
                        break;
                    }
                    case 1: {
                        wifiType = "WEP-PSK";
                        payload = this.updateWifiWEPConfig(payload, dataObject);
                        break;
                    }
                    case 2: {
                        wifiType = "WPA-PSK";
                        payload = this.updateWifiWEPConfig(payload, dataObject);
                        break;
                    }
                    case 4: {
                        wifiType = "WEP-8021X";
                        payload = this.updateWifiAdvancedWepConfig(payload, dataObject);
                        break;
                    }
                    case 5: {
                        wifiType = "WPA-EAP";
                        payload = this.updateWifiAdvancedWepConfig(payload, dataObject);
                        break;
                    }
                }
                payload.setSecurityType(wifiType);
                payload = this.updateProxyConfig(payload, dataObject.getRows("PayloadProxyConfig").next());
            }
        }
        catch (final Exception ex) {
            DO2ChromeWifPolicyPayload.LOGGER.log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return payload;
    }
    
    private ChromeWifiPayload updateWifiWEPConfig(final ChromeWifiPayload wifiPayload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("WifiNonEnterprise");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            String wep = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordId = (Long)row.get("PASSWORD_ID");
                wep = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
            }
            wifiPayload.setPassword(wep);
        }
        return wifiPayload;
    }
    
    private ChromeWifiPayload updateWifiAdvancedWepConfig(final ChromeWifiPayload wifiPayload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("WifiEnterprise");
        wifiPayload.initEAPSettings();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String authProtocol = new ONCPayloadUtil().getAuthProtocol(row);
            wifiPayload.setAuthProtocol(authProtocol);
            final int innerProtocol = (int)row.get("INNER_IDENTITY");
            wifiPayload.setInnerProtocol(new ONCPayloadUtil().getInnerProtocol(innerProtocol));
            final String identity = (String)row.get("USER_NAME");
            final String anonumousIdentity = (String)row.get("OUTER_IDENTITY");
            String password = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordId = (Long)row.get("PASSWORD_ID");
                password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
            }
            wifiPayload.setouterIdentity(anonumousIdentity);
            wifiPayload.setIdentity(identity);
            wifiPayload.setEnterprisePassword(password);
            final Long certficateID = (Long)row.get("CERTIFICATE_ID");
            final Boolean dontValidate = (Boolean)row.get("DONT_VALIDATE");
            if (dontValidate) {
                wifiPayload.setUseSystemCAs(true);
            }
            else {
                if (certficateID <= 0L) {
                    continue;
                }
                wifiPayload.setCertificate(certficateID.toString(), false);
            }
        }
        return wifiPayload;
    }
    
    private ChromeWifiPayload updateProxyConfig(final ChromeWifiPayload wifiPayload, final Row row) throws Exception {
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
        wifiPayload.setProxySettings(proxySettings);
        return wifiPayload;
    }
}
