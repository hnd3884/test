package com.me.mdm.chrome.agent.commands.profiles;

import java.util.logging.Level;
import java.util.List;
import com.me.mdm.chrome.agent.utils.ChromeAgentJSONUtil;
import java.io.IOException;
import org.json.JSONException;
import com.google.chromedevicemanagement.v1.model.OpenNetworkConfig;
import org.json.JSONArray;
import com.me.mdm.chrome.agent.Context;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class ONCPayload
{
    public Logger logger;
    private static final String TYPE = "Type";
    private static final String UNENCRYPTED_TYPE = "UnencryptedConfiguration";
    private static final String NETWORK_CONFIGS = "NetworkConfigurations";
    private static final String CERTIFICATES = "Certificates";
    private static final String CERTIFICATE = "Certificate";
    private JSONObject oncProfile;
    private Context context;
    
    public ONCPayload(final Context context) throws JSONException, IOException {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.oncProfile = new JSONObject();
        this.context = context;
        final OpenNetworkConfig config = this.getOpenNetworkConfig(context);
        if (config != null && config.getOpenNetworkConfig() != null && !config.getOpenNetworkConfig().isEmpty()) {
            this.oncProfile = new JSONObject(config.getOpenNetworkConfig());
        }
        else {
            (this.oncProfile = new JSONObject()).put("Type", (Object)"UnencryptedConfiguration");
        }
        if (!this.oncProfile.has("NetworkConfigurations")) {
            this.oncProfile.put("NetworkConfigurations", (Object)new JSONArray());
        }
        if (!this.oncProfile.has("Certificates")) {
            this.oncProfile.put("Certificates", (Object)new JSONArray());
        }
    }
    
    public void addNetworkConfig(final JSONObject networkConfig) throws JSONException {
        final JSONArray networkConfigs = this.oncProfile.optJSONArray("NetworkConfigurations");
        networkConfigs.put((Object)networkConfig);
        this.oncProfile.put("NetworkConfigurations", (Object)networkConfigs);
    }
    
    public void addCertificate(final JSONObject certificate) throws JSONException {
        final JSONArray certs = this.oncProfile.optJSONArray("Certificates");
        certs.put((Object)certificate);
        this.oncProfile.put("Certificates", (Object)certs);
    }
    
    public boolean removeNetworkConfigIfExist(final String guid) throws JSONException {
        final JSONArray networkConfigs = this.oncProfile.optJSONArray("NetworkConfigurations");
        final ChromeAgentJSONUtil jSONUtil = new ChromeAgentJSONUtil();
        if (networkConfigs != null) {
            final List<JSONObject> networkConfigList = jSONUtil.convertJSONArrayTOList(networkConfigs);
            for (int i = 0; i < networkConfigList.size(); ++i) {
                if (String.valueOf(networkConfigList.get(i).get("GUID")).equals(guid)) {
                    networkConfigList.remove(i);
                    this.oncProfile.put("NetworkConfigurations", (Object)jSONUtil.convertListToJSONArray(networkConfigList));
                    return true;
                }
            }
            this.oncProfile.put("NetworkConfigurations", (Object)jSONUtil.convertListToJSONArray(networkConfigList));
        }
        return false;
    }
    
    public boolean removeCertificateConfigIfExist(final String guid) throws JSONException {
        final JSONArray certficate = this.oncProfile.optJSONArray("Certificates");
        final ChromeAgentJSONUtil jSONUtil = new ChromeAgentJSONUtil();
        if (certficate != null) {
            final List<JSONObject> certConfigList = jSONUtil.convertJSONArrayTOList(certficate);
            for (int i = 0; i < certConfigList.size(); ++i) {
                if (String.valueOf(certConfigList.get(i).get("GUID")).equals(guid)) {
                    certConfigList.remove(i);
                    this.oncProfile.put("Certificates", (Object)jSONUtil.convertListToJSONArray(certConfigList));
                    return true;
                }
            }
            this.oncProfile.put("Certificates", (Object)jSONUtil.convertListToJSONArray(certConfigList));
        }
        return false;
    }
    
    private String getONCString() {
        return this.oncProfile.toString();
    }
    
    public void publishONCProfile() {
        try {
            final OpenNetworkConfig config = new OpenNetworkConfig();
            config.setOpenNetworkConfig(this.getONCString());
            this.setOpenNetworkConfig(this.context, config);
        }
        catch (final IOException ex) {
            Logger.getLogger("MDMChromeAgentLogger").log(Level.SEVERE, null, ex);
        }
    }
    
    protected abstract OpenNetworkConfig getOpenNetworkConfig(final Context p0) throws IOException;
    
    protected abstract void setOpenNetworkConfig(final Context p0, final OpenNetworkConfig p1) throws IOException;
}
