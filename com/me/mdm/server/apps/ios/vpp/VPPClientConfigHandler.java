package com.me.mdm.server.apps.ios.vpp;

import java.util.Hashtable;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.UUID;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.apps.vpp.VPPServiceConfigHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPResponseProcessor;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class VPPClientConfigHandler
{
    public Logger logger;
    private static VPPClientConfigHandler vppClientConfigHandler;
    
    public VPPClientConfigHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPClientConfigHandler getInstance() {
        if (VPPClientConfigHandler.vppClientConfigHandler == null) {
            VPPClientConfigHandler.vppClientConfigHandler = new VPPClientConfigHandler();
        }
        return VPPClientConfigHandler.vppClientConfigHandler;
    }
    
    private Boolean checkIsErrorInClientConfig(final Properties prop) {
        final String status = ((Hashtable<K, String>)prop).get("status");
        if (prop.containsKey("errorNumber") || (status != null && status.equalsIgnoreCase("error"))) {
            return true;
        }
        return false;
    }
    
    private JSONObject checkVppClientContent(final Properties prop, final Long businessStoreID) throws Exception {
        final JSONObject resultJson = new JSONObject();
        String clientContext = ((Hashtable<K, String>)prop).get("clientContext");
        if (clientContext != null && clientContext.equals("token being used in v2")) {
            this.logger.log(Level.INFO, "token being used in v2 - case in businessStoreID: {0}. Hence, handling checking if it is present in other MDM", new Object[] { businessStoreID });
            final JSONObject clientContextJSON = VPPTokenDataHandler.getInstance().getNewClientContextJSON(VPPTokenDataHandler.getInstance().getVppToken(businessStoreID));
            if (clientContextJSON != null) {
                if (clientContextJSON.length() <= 0) {
                    this.logger.log(Level.INFO, "The token isn't present in any other MDM : Business StoreID: {0}", new Object[] { businessStoreID });
                    clientContext = "";
                }
                else {
                    clientContext = clientContextJSON.toString();
                }
                ((Hashtable<String, String>)prop).put("clientConfigSrvUrl", clientContext);
            }
        }
        final String vpptokenUUID = VPPTokenDataHandler.getInstance().getVppTokenUUID(businessStoreID);
        try {
            String guid = "--";
            if (!clientContext.equals("")) {
                final JSONObject clientContextJson = new JSONObject(clientContext);
                guid = clientContextJson.optString("guid", "--");
                if (!vpptokenUUID.equals(guid) && !guid.equals("--")) {
                    resultJson.put("status", (Object)"error");
                    resultJson.put("clientContext", (Object)clientContext);
                }
                else if (vpptokenUUID.equals("--") || guid.equals("--")) {
                    resultJson.put("status", (Object)"success");
                    resultJson.put("isClientConfigSet", false);
                }
                else {
                    resultJson.put("status", (Object)"success");
                    resultJson.put("isClientConfigSet", true);
                }
            }
            else {
                resultJson.put("status", (Object)"success");
                resultJson.put("isClientConfigSet", false);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception in checkVppClientContent ", ex);
            this.logger.log(Level.SEVERE, "ClientContext: {0} -  is not as expected. Hence, setting client context again", new Object[] { clientContext });
            resultJson.put("status", (Object)"success");
            resultJson.put("isClientConfigSet", false);
        }
        return resultJson;
    }
    
    public void addVppClientConfig(final String clientContextString, final Long businessStoreID, final Long customerID) {
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getVPPClientConfigCommand(clientContextString);
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for adding client config via clientConfigSrvUrl of businessStoreID: {0} - {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "clientConfigSrvUrl", sToken, businessStoreID);
            this.logger.log(Level.INFO, "Response received for clientConfigSrvUrl of businessStoreID: {0}", new Object[] { businessStoreID });
            final Properties prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "clientConfigSrvUrl");
            if (prop.containsKey("errorMessage")) {
                this.logger.log(Level.WARNING, "Error: Response for clientConfigSrvUrl of businessStoreID: {0}", new Object[] { businessStoreID, responseJSON });
            }
            else if (!prop.containsKey("LOCATION_ID") && prop.containsKey("ORGANISATION_NAME")) {
                this.logger.log(Level.WARNING, "No location details found for uploaded token of businessStoreID: {0} belonging to organization: {1}. (Legacy Token)", new Object[] { businessStoreID, ((Hashtable<K, Object>)prop).get("ORGANISATION_NAME") });
            }
            else if (prop.containsKey("LOCATION_ID")) {
                this.logger.log(Level.INFO, "Response received for clientConfigSrvUrl of businessStoreID: {0} - Location Name: {1}", new Object[] { businessStoreID, ((Hashtable<K, Object>)prop).get("LOCATION_NAME") });
            }
            else {
                this.logger.log(Level.WARNING, "No location or orgName found");
            }
            if (prop != null && !prop.containsKey("errorMessage")) {
                this.populateCountryCode(prop, businessStoreID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while adding addVppClientConfig ", e);
        }
    }
    
    private void populateCountryCode(final Properties clientConfigProp, final Long storeID) {
        try {
            final String countryCode = ((Hashtable<K, Object>)clientConfigProp).get("COUNTRY_CODE").toString();
            VPPTokenDataHandler.getInstance().updateCountryCode(countryCode, storeID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception while adding populateCountryCode ", e);
        }
    }
    
    public void generateAndAddVppClientConfig(final Long businessStoreID, final Long customerID) {
        VPPServiceConfigHandler.getInstance().checkAndFetchServiceUrl();
        final String vppTokenUUID = this.generateVPPTokenUUID();
        VPPTokenDataHandler.getInstance().updateVppTokenUUID(vppTokenUUID, businessStoreID);
        final JSONObject clientContextStr = this.createVppClientContext(vppTokenUUID);
        this.addVppClientConfig(clientContextStr.toString(), businessStoreID, customerID);
        MessageProvider.getInstance().hideMessage("VPP_USED_IN_OTHER_MDM", customerID);
    }
    
    private String generateVPPTokenUUID() {
        return UUID.randomUUID().toString();
    }
    
    private JSONObject createVppClientContext(final String vppTokenUUID) {
        final JSONObject clientContext = new JSONObject();
        try {
            final String productName = ProductUrlLoader.getInstance().getValue("displayname");
            clientContext.put("hostname", (Object)productName);
            clientContext.put("guid", (Object)vppTokenUUID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception in createVppClientContext ", ex);
        }
        return clientContext;
    }
    
    public Properties getVppClientConfig(final Long businessStoreID, final Long customerID) throws Exception {
        Properties prop = new Properties();
        try {
            final String sToken = VPPTokenDataHandler.getInstance().getVppToken(businessStoreID);
            final String command = new VPPAPIRequestGenerator(sToken).getVPPClientConfigCommand();
            final String dummyCommand = command.replace(sToken, "*****");
            this.logger.log(Level.INFO, "Request for clientConfigSrvUrl of businessStoreID: {0} - {1}", new Object[] { businessStoreID, dummyCommand });
            final JSONObject responseJSON = VPPAppAPIRequestHandler.getInstance().getVppServerResponse(command, "clientConfigSrvUrl", sToken, businessStoreID);
            prop = (Properties)VPPResponseProcessor.getInstance().processResponse(responseJSON, "clientConfigSrvUrl");
            this.logger.log(Level.INFO, "Response received for clientConfigSrvUrl of businessStoreID: {0}", new Object[] { businessStoreID });
            if (prop.containsKey("errorMessage")) {
                this.logger.log(Level.WARNING, "Error: Response for clientConfigSrvUrl of businessStoreID: {0}", new Object[] { businessStoreID, responseJSON });
            }
            else if (!prop.containsKey("LOCATION_ID") && prop.containsKey("ORGANISATION_NAME")) {
                this.logger.log(Level.WARNING, "No location details found for uploaded token of businessStoreID: {0} belonging to organization: {1}.(Legacy Token)", new Object[] { businessStoreID, ((Hashtable<K, Object>)prop).get("ORGANISATION_NAME") });
            }
            else if (prop.containsKey("LOCATION_ID")) {
                this.logger.log(Level.INFO, "Response received for clientConfigSrvUrl of businessStoreID: {0} - Location Name: {1}", new Object[] { businessStoreID, ((Hashtable<K, Object>)prop).get("LOCATION_NAME") });
            }
            else {
                this.logger.log(Level.WARNING, "No location or orgName found");
            }
            if (businessStoreID != null) {
                ((Hashtable<String, Long>)prop).put("BUSINESSSTORE_ID", businessStoreID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "VPPClientConfigHandler: Exception in getVppClientConfig() ", ex);
        }
        return prop;
    }
    
    public JSONObject checkAndUpdateVPPClientConfig(final Long businessStoreID, final Long customerID) {
        final JSONObject resultJson = new JSONObject();
        try {
            VPPServiceConfigHandler.getInstance().checkAndFetchServiceUrl();
            final Properties clientConfigProp = this.getVppClientConfig(businessStoreID, customerID);
            ((Hashtable<String, Long>)clientConfigProp).put("CUSTOMER_ID", customerID);
            final Boolean isError = this.checkIsErrorInClientConfig(clientConfigProp);
            if (!isError) {
                final JSONObject clientContextJson = this.checkVppClientContent(clientConfigProp, businessStoreID);
                final String status = clientContextJson.optString("status", "");
                if (status.equalsIgnoreCase("error")) {
                    final String clientContext = clientContextJson.optString("clientContext");
                    resultJson.put("status", (Object)"error");
                    resultJson.put("errorNumber", 888801);
                    resultJson.put("errorMessage", (Object)"differentClientContext");
                    resultJson.put("clientContext", (Object)clientContext);
                }
                else if (status.equalsIgnoreCase("success")) {
                    resultJson.put("status", (Object)"success");
                    final Boolean isClientConfigSet = clientContextJson.optBoolean("isClientConfigSet");
                    if (!isClientConfigSet) {
                        this.generateAndAddVppClientConfig(businessStoreID, customerID);
                    }
                    VPPTokenDataHandler.getInstance().addorUpdateVppTokenDetails(clientConfigProp);
                }
                else if (MDMStringUtils.isEmpty(status)) {
                    resultJson.put("status", (Object)"error");
                    resultJson.put("errorNumber", 888800);
                    resultJson.put("errorMessage", (Object)MDMI18N.getI18Nmsg("mdm.vpp.sync.failureCommonMessage"));
                }
            }
            else {
                resultJson.put("status", (Object)"error");
                resultJson.put("errorNumber", ((Hashtable<K, Object>)clientConfigProp).get("errorNumber"));
                resultJson.put("errorMessage", ((Hashtable<K, Object>)clientConfigProp).get("errorMessage"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception in checkAndUpdateVPPClientConfig ", ex);
            try {
                resultJson.put("status", (Object)"error");
                resultJson.put("errorNumber", 888802);
                resultJson.put("errorMessage", (Object)MDMI18N.getMsg("mdm.vpp.sync.failureCommonMessage", new Object[0]));
            }
            catch (final Exception ex2) {
                this.logger.log(Level.SEVERE, " Exception in setting error json in checkAndUpdateVPPClientConfig ", ex2);
            }
        }
        return resultJson;
    }
    
    static {
        VPPClientConfigHandler.vppClientConfigHandler = null;
    }
}
