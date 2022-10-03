package com.me.mdm.core.auth;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class MDMPurposeAPIKeyGenerator implements MDMAPIKeyGeneratorAPI
{
    private static Logger logger;
    protected static final String PURPOSE_API_KEY = "encapiKey";
    protected static final String GENERATE_NEW_PURPOSETOKEN = "GENERATE_NEW_PURPOSETOKEN";
    
    @Override
    public abstract APIKey createAPIKey(final JSONObject p0) throws Exception;
    
    @Override
    public APIKey generateAPIKey(final JSONObject json) {
        try {
            json.put("PURPOSE_TOKEN", (Object)this.getPurposeToken(json));
            return this.createAPIKey(json);
        }
        catch (final Exception ex) {
            MDMPurposeAPIKeyGenerator.logger.log(Level.SEVERE, "Exception while creating Purpose API key : ", ex);
            return null;
        }
    }
    
    @Override
    public APIKey updateAPIKey(final JSONObject json) {
        return this.generateAPIKey(json);
    }
    
    @Override
    public APIKey getAPIKey(final JSONObject json) {
        return this.generateAPIKey(json);
    }
    
    @Override
    public abstract boolean validateAPIKey(final JSONObject p0);
    
    @Override
    public void revokeAPIKey(final JSONObject json) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected boolean validateAPIKeyWithReceivedKey(final JSONObject json) {
        final String receivedTokenValue = json.optString("RECEIVED_TOKEN");
        final int purposeKey = json.optInt("PURPOSE_KEY");
        String purposeKeyInDevice = null;
        if (!json.has("CUSTOMER_ID")) {
            purposeKeyInDevice = MDMPurposeTokenCreator.getPurposeToken(purposeKey);
        }
        else {
            purposeKeyInDevice = MDMPurposeTokenCreator.getPurposeToken(purposeKey, json.getLong("CUSTOMER_ID"));
        }
        MDMPurposeAPIKeyGenerator.logger.log(Level.FINE, "MDMPurposeAPIKeyGenerator: purposeKeyInDevice : {0} receivedTokenValue : {1}", new Object[] { purposeKeyInDevice, receivedTokenValue });
        return receivedTokenValue.equals(purposeKeyInDevice);
    }
    
    protected String getPurposeToken(final JSONObject json) throws Exception {
        if (!json.has("CUSTOMER_ID")) {
            return json.optBoolean("GENERATE_NEW_PURPOSETOKEN", false) ? MDMPurposeTokenCreator.addOrUpdatePurposeToken(json.getInt("PURPOSE_KEY"), true) : MDMPurposeTokenCreator.getPurposeToken(json.getInt("PURPOSE_KEY"));
        }
        final Long customerID = json.getLong("CUSTOMER_ID");
        return json.optBoolean("GENERATE_NEW_PURPOSETOKEN", false) ? MDMPurposeTokenCreator.addOrUpdatePurposeToken(json.getInt("PURPOSE_KEY"), true, customerID) : MDMPurposeTokenCreator.getPurposeToken(json.getInt("PURPOSE_KEY"), customerID);
    }
    
    static {
        MDMPurposeAPIKeyGenerator.logger = Logger.getLogger("MDMLogger");
    }
}
