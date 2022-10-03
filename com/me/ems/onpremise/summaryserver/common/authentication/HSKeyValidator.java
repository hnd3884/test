package com.me.ems.onpremise.summaryserver.common.authentication;

import javax.net.ssl.SSLHandshakeException;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class HSKeyValidator
{
    public static Logger logger;
    private static HSKeyValidator instance;
    private static HashMap<String, Long> validKeyMap;
    
    public static HashMap<String, Long> getValidKeyMap() {
        return HSKeyValidator.validKeyMap;
    }
    
    public static synchronized HSKeyValidator getInstance() {
        if (HSKeyValidator.instance == null) {
            HSKeyValidator.instance = new HSKeyValidator();
        }
        return HSKeyValidator.instance;
    }
    
    private static void storeKey(final String key) {
        HSKeyValidator.validKeyMap.clear();
        HSKeyValidator.validKeyMap.put(key, System.currentTimeMillis());
    }
    
    public Boolean isValidHSKey(final String hsKey, final Long probeId) {
        getInstance();
        final HashMap validKeyMap = getValidKeyMap();
        if (validKeyMap.containsKey(hsKey)) {
            if (validKeyMap.get(hsKey) + 180000L > System.currentTimeMillis()) {
                return true;
            }
            HSKeyValidator.logger.log(Level.SEVERE, "Old HandShake Key Provided");
            return false;
        }
        else {
            if (this.isFromAuthenticatedServer(hsKey, probeId, false)) {
                storeKey(hsKey);
                return true;
            }
            HSKeyValidator.logger.log(Level.SEVERE, "Hand shake key is not from summary server");
            return false;
        }
    }
    
    private Boolean isFromAuthenticatedServer(final String key, final Long probeId, final boolean isRetry) {
        String response = "failure";
        String serverName = "";
        String portNumber = "";
        String protocol = "";
        String baseURL = "";
        try {
            if (probeId == null) {
                final HashMap summaryProps = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerDetails();
                serverName = summaryProps.get("host");
                portNumber = summaryProps.get("port");
                protocol = summaryProps.get("protocol");
            }
            else {
                final HashMap probeRow = ProbeUtil.getInstance().getProbeDetail(probeId);
                serverName = probeRow.get("HOST");
                portNumber = probeRow.get("PORT").toString();
                protocol = probeRow.get("PROTOCOL");
            }
            baseURL = protocol + "://" + serverName + ":" + portNumber + "/servlets/ProbeHSKeyAuthenticator";
            final URL urlObj = new URL(baseURL);
            final HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();
            conn.setRequestProperty("handshake-key", key);
            conn.setRequestMethod("POST");
            if (protocol.equalsIgnoreCase("https")) {}
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = rd.readLine();
            rd.close();
            if (response.equalsIgnoreCase("SUCCESS")) {
                return true;
            }
        }
        catch (final SSLHandshakeException ex) {
            if (!isRetry) {
                HSKeyValidator.logger.log(Level.INFO, "Exception occurred while validating hs key, retrying to get certficates");
                HttpsHandlerUtil.processCertificateFromServer(baseURL, probeId);
                this.isFromAuthenticatedServer(key, probeId, true);
            }
            else {
                HSKeyValidator.logger.log(Level.INFO, "Exception occurred while validating hs key, for the second time", ex);
            }
        }
        catch (final Exception e) {
            HSKeyValidator.logger.log(Level.SEVERE, "Exception occurred while validating hs key", e);
        }
        return false;
    }
    
    static {
        HSKeyValidator.logger = Logger.getLogger("probeActionsLogger");
        HSKeyValidator.instance = null;
        HSKeyValidator.validKeyMap = new HashMap<String, Long>();
    }
}
