package com.me.mdm.server.profiles.config;

import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.sym.server.mdm.ios.MDMRequestTypeConstants;
import java.util.logging.Level;
import com.dd.plist.NSDictionary;
import java.util.ArrayList;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.ios.payload.PayloadTypeConstants;
import com.me.mdm.webclient.formbean.MDMAppleCustomProfileFormBean;
import com.me.mdm.server.profiles.AppleCustomProfileHandler;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import java.util.logging.Logger;

public class IOSCustomProfileConfigHandler extends DefaultConfigHandler
{
    private Logger logger;
    
    public IOSCustomProfileConfigHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            final String customProfilePath = serverJSON.optString("CUSTOM_PROFILE_PATH", "");
            if (this.isFileSizeGreater(customProfilePath, 5242880L)) {
                throw new APIHTTPException("PAY0001", new Object[0]);
            }
            final Integer customProfileType = serverJSON.optInt("CUSTOM_PROFILE_TYPE", (int)AppleCustomProfileHandler.CUSTOM_CONFIGURATION);
            final NSDictionary rootDict = new MDMAppleCustomProfileFormBean().getDictionaryFromStream(customProfilePath);
            final Boolean existingPayload = serverJSON.optBoolean("ALLOW_EXISTING_PAYLOAD");
            if (customProfileType.equals(AppleCustomProfileHandler.CUSTOM_CONFIGURATION)) {
                final List<String> payloadList = PayloadTypeConstants.getSupportedPayloadTypes();
                final NSArray payloadArray = (NSArray)rootDict.get((Object)"PayloadContent");
                if (payloadArray == null) {
                    throw new APIHTTPException("PAY0010", new Object[0]);
                }
                if (!existingPayload) {
                    final List<String> alreadyAvailablePayload = new ArrayList<String>();
                    for (int i = 0; i < payloadArray.count(); ++i) {
                        final NSDictionary payloadDictionary = (NSDictionary)payloadArray.objectAtIndex(i);
                        final String payloadType = payloadDictionary.get((Object)"PayloadType").toString();
                        if (payloadList.contains(payloadType)) {
                            alreadyAvailablePayload.add(payloadType);
                        }
                    }
                    if (!alreadyAvailablePayload.isEmpty()) {
                        throw new APIHTTPException("PAY0011", new Object[] { alreadyAvailablePayload.toString() });
                    }
                }
            }
            else if (customProfileType.equals(AppleCustomProfileHandler.CUSTOM_COMMAND)) {
                final NSDictionary requestDict = (NSDictionary)rootDict.get((Object)"Command");
                if (requestDict == null) {
                    throw new APIHTTPException("PAY0010", new Object[0]);
                }
                final String requestType = requestDict.get((Object)"RequestType").toString();
                this.logger.log(Level.INFO, "Custom command request type:{0}", new Object[] { requestType });
                if (!existingPayload && MDMRequestTypeConstants.getSupportedRequestTypes().contains(requestType)) {
                    final List<String> alreadyAvailablePayload = new ArrayList<String>();
                    alreadyAvailablePayload.add(requestType);
                    throw new APIHTTPException("PAY0011", new Object[] { alreadyAvailablePayload.toString() });
                }
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in custom config handler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (!dataObject.isEmpty() && configJSON.has("payload_id") && dataObject.containsTable("AppleCustomProfilesDataExtn")) {
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            final Row customProfileRow = dataObject.getRow("AppleCustomProfilesDataExtn");
            configJSON.put(this.getSubConfigProperties(templateConfigProperties, "CUSTOM_PROFILE_TYPE").getString("alias"), customProfileRow.get("CUSTOM_PROFILE_TYPE"));
        }
    }
}
