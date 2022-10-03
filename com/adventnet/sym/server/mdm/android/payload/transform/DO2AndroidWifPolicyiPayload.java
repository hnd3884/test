package com.adventnet.sym.server.mdm.android.payload.transform;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidWifiPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidWifPolicyiPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidWifiPayload wifiPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("WifiPolicy");
            while (iterator.hasNext()) {
                wifiPayload = new AndroidWifiPayload("1.0", "com.mdm.mobiledevice.passcode", "WiFi Policy");
                final Row row = iterator.next();
                final long configDataItemID = (long)row.get("CONFIG_DATA_ITEM_ID");
                final String ssid = (String)row.get("SERVICE_SET_IDENTIFIER");
                final boolean autoJoin = (boolean)row.get("AUTO_JOIN");
                final int securityType = (int)row.get("SECURITY_TYPE");
                wifiPayload.setSSID(ssid);
                wifiPayload.setAutoJoin(autoJoin);
                String wifiType = "NONE";
                switch (securityType) {
                    case 0: {
                        wifiType = "NONE";
                        break;
                    }
                    case 1: {
                        wifiType = "WEP";
                        wifiPayload = this.updateWifiWEPConfig(wifiPayload, dataObject);
                        break;
                    }
                    case 2: {
                        wifiType = "PSK";
                        wifiPayload = this.updateWifiPSKConfig(wifiPayload, dataObject);
                        break;
                    }
                    case 3: {
                        wifiType = "EAP";
                        wifiPayload = this.updateWifiEAPConfig(wifiPayload, dataObject);
                        break;
                    }
                }
                wifiPayload.setSecurityType(wifiType);
                wifiPayload = this.updateProxyConfig(wifiPayload, row);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2AndroidWifPolicyiPayload.class.getName()).log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return wifiPayload;
    }
    
    private AndroidWifiPayload updateProxyConfig(final AndroidWifiPayload wifiPayload, final Row row) throws Exception {
        final int proxyType = (int)row.get("PROXY_TYPE");
        final JSONObject proxyData = new JSONObject();
        if (proxyType == 2) {
            final String proxyServer = (String)row.get("PROXY_SERVER");
            final int proxyPort = (int)row.get("PROXY_SERVER_PORT");
            final String byPassUrl = (String)row.get("BYPASS_PROXY_URL");
            wifiPayload.setProxySetting("Manual");
            proxyData.put("ProxyServer", (Object)proxyServer);
            proxyData.put("ProxyPort", proxyPort);
            if (byPassUrl != null && !byPassUrl.equals("")) {
                final String[] urls = byPassUrl.split(",");
                final JSONArray urlArr = new JSONArray();
                for (final String url : urls) {
                    urlArr.put((Object)url);
                }
                proxyData.put("BypassProxyUrl", (Object)urlArr);
            }
        }
        else {
            wifiPayload.setProxySetting("None");
        }
        wifiPayload.setProxyData(proxyData);
        return wifiPayload;
    }
    
    private AndroidWifiPayload updateWifiWEPConfig(final AndroidWifiPayload wifiPayload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("WifiNonEnterprise");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            String wep = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordId = (Long)row.get("PASSWORD_ID");
                wep = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
            }
            wifiPayload.setDefaultWepIndex(0);
            wifiPayload.setWep(wep);
            wifiPayload.setPassword(wep);
        }
        return wifiPayload;
    }
    
    private AndroidWifiPayload updateWifiPSKConfig(final AndroidWifiPayload wifiPayload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("WifiNonEnterprise");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            String preSharedKey = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordId = (Long)row.get("PASSWORD_ID");
                preSharedKey = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
            }
            wifiPayload.setPreSharedKey(preSharedKey);
            wifiPayload.setPassword(preSharedKey);
        }
        return wifiPayload;
    }
    
    private AndroidWifiPayload updateWifiEAPConfig(AndroidWifiPayload wifiPayload, final DataObject dataObject) throws Exception {
        final Iterator iterator = dataObject.getRows("WifiEnterprise");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final int eapMethod = this.parseMethod(row);
            final int phase2Val = (int)row.get("INNER_IDENTITY");
            final String identity = (String)row.get("USER_NAME");
            final String anonumousIdentity = (String)row.get("OUTER_IDENTITY");
            final Long caCertID = (Long)row.get("CERTIFICATE_ID");
            final Long userCertID = (Long)row.get("IDENTITY_CERTIFICATE_ID");
            wifiPayload.setEAPMethod(this.getEAPMethod(eapMethod));
            wifiPayload.setPhase2(this.getEAPPhase2(phase2Val));
            wifiPayload.setIdentity(identity);
            wifiPayload.setAnonymousIdentity(anonumousIdentity);
            String password = "";
            if (row.get("PASSWORD_ID") != null) {
                final Long passwordId = (Long)row.get("PASSWORD_ID");
                password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(passwordId.toString());
            }
            wifiPayload.setPassword(password);
            if (caCertID != null && caCertID != -1L) {
                wifiPayload = this.getCertificate(wifiPayload, caCertID, dataObject, true);
            }
            if (userCertID != null && userCertID != -1L) {
                wifiPayload = this.getCertificate(wifiPayload, userCertID, dataObject, false);
            }
        }
        return wifiPayload;
    }
    
    private int parseMethod(final Row wifiEnterprise) {
        int flag = 1;
        final boolean peap = (boolean)wifiEnterprise.get("PEAP");
        final boolean tls = (boolean)wifiEnterprise.get("TLS");
        final boolean ttls = (boolean)wifiEnterprise.get("TTLS");
        final boolean pwd = (boolean)wifiEnterprise.get("EAP_FAST");
        if (peap) {
            flag = 1;
        }
        if (tls) {
            flag = 2;
        }
        if (ttls) {
            flag = 3;
        }
        if (pwd) {
            flag = 4;
        }
        return flag;
    }
    
    private String getEAPMethod(final int method) {
        String eapMethod = "PEAP";
        switch (method) {
            case 1: {
                eapMethod = "PEAP";
                break;
            }
            case 2: {
                eapMethod = "TLS";
                break;
            }
            case 3: {
                eapMethod = "TTLS";
                break;
            }
            case 4: {
                eapMethod = "EAP-PWD";
                break;
            }
        }
        return eapMethod;
    }
    
    private String getEAPPhase2(final int type) {
        String phase2Val = "NONE";
        switch (type) {
            case 0: {
                phase2Val = "NONE";
                break;
            }
            case 1: {
                phase2Val = "PAP";
                break;
            }
            case 2: {
                phase2Val = "MSCHAP";
                break;
            }
            case 3: {
                phase2Val = "MSCHAPV2";
                break;
            }
            case 4: {
                phase2Val = "GTC";
                break;
            }
        }
        return phase2Val;
    }
    
    private AndroidWifiPayload getCertificate(final AndroidWifiPayload wifiPayload, final Long certID, final DataObject dataObject, final boolean isCACert) throws Exception {
        final Long customerID = (Long)dataObject.getRow("CollnToCustomerRel").get("CUSTOMER_ID");
        final DataObject certDO = ProfileCertificateUtil.getInstance().getCertificateInfo(customerID, certID);
        final Criteria identitycertCriteria = new Criteria(new Column("Certificates", "CERTIFICATE_RESOURCE_ID"), (Object)certID, 0);
        final DataObject certificatesDO = ProfileCertificateUtil.getCertificateDO(identitycertCriteria);
        final Row identityRow = certificatesDO.getFirstRow("Certificates");
        final int type = (int)identityRow.get("CERTIFICATE_TYPE");
        if (type == 0) {
            wifiPayload.setEnrollType("Raw");
        }
        else if (type == 1) {
            wifiPayload.setEnrollType("Scep");
        }
        if (certDO != null && type == 0) {
            final Row certRow = certDO.getFirstRow("CredentialCertificateInfo");
            final String displayName = (String)certRow.get("CERTIFICATE_DISPLAY_NAME");
            final String certFileName = (String)certRow.get("CERTIFICATE_FILE_NAME");
            final Long certificateId = (Long)certRow.get("CERTIFICATE_ID");
            final String password = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificatePassword(certificateId.toString());
            final String certificate = PayloadSecretFieldsHandler.getInstance().constructPayloadCertificate(certificateId.toString());
            final String certType = (certFileName.toUpperCase().endsWith("P12") || certFileName.toLowerCase().endsWith(".pfx")) ? "PKCS12" : "X509";
            if (isCACert) {
                wifiPayload.setCACertName(displayName);
                wifiPayload.setCACertPassword(password);
                wifiPayload.setCACertType(certType);
                wifiPayload.setCACertContent(certificate);
            }
            else {
                wifiPayload.setClientCertName(displayName);
                wifiPayload.setClientCertPassword(password);
                wifiPayload.setClientCertType(certType);
                wifiPayload.setClientCertContent(certificate);
            }
        }
        return wifiPayload;
    }
    
    byte[] certificateBytesArray(final String fileName) throws IOException, Exception {
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = ApiFactoryProvider.getFileAccessAPI().getInputStream(fileName);
            if (null != is) {
                final long length = ApiFactoryProvider.getFileAccessAPI().getFileSize(fileName);
                if (length > 2147483647L) {
                    throw new IOException("The file is too big");
                }
                bytes = new byte[(int)length];
                int offset = 0;
                for (int numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {}
                if (offset < bytes.length) {
                    throw new IOException("The file was not completely read: " + ApiFactoryProvider.getFileAccessAPI().getFileName(fileName));
                }
            }
        }
        catch (final FileNotFoundException e) {
            Logger.getLogger(DO2AndroidWifPolicyiPayload.class.getName()).log(Level.SEVERE, "Exception in createPayload", e);
        }
        finally {
            is.close();
        }
        return bytes;
    }
}
