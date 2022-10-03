package com.me.mdm.mdmmigration.source;

import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.certificate.CryptographyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UEMMigrationService
{
    private final Logger logger;
    public static final String MIGRATION_TOPIC_AFW = "ManagedGooglePlay";
    public static final String MIGRATION_TOPIC_EMMUSERS = "EMMUsersAndAccounts";
    
    public UEMMigrationService() {
        this.logger = Logger.getLogger("MDMMigrationLogger");
    }
    
    public Map updateMigrationFeatureParams(final Map requestParam) {
        final Map result = new HashMap();
        try {
            final JSONArray featureParams = JSONUtil.mapToJSON(requestParam).getJSONArray("featureparams");
            for (int i = 0; i < featureParams.length(); ++i) {
                final JSONObject feature_Params_json = (JSONObject)featureParams.get(i);
                if (feature_Params_json.has("paramname")) {
                    final String param_name = feature_Params_json.getString("paramname");
                    if (param_name.equals("DoNotVerifyAppleSignedContent") || param_name.equals("MigrationTarget") || param_name.equals("AgentMigration") || param_name.equals("MDMMigration")) {
                        MDMFeatureParamsHandler.updateMDMFeatureParameter(feature_Params_json.getString("paramname"), feature_Params_json.getBoolean("paramvalue"));
                        this.logger.log(Level.INFO, "{0} Feature Param is enabled", param_name);
                        result.put("msg", "Migration Feature Params Enabled");
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in MigrationFeatureParamsServie ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    public Map isBuildCompatibleForMigration() {
        final Map result = new HashMap();
        try {
            String static_build = "";
            final DMHttpRequest dmHttpRequest = new DMHttpRequest();
            dmHttpRequest.url = "https://mdmdatabase.manageengine.com/MISC/migration_build/build.txt";
            dmHttpRequest.method = "GET";
            final DMHttpResponse dmHttpResponse = MDMUtil.executeDMHttpRequest(dmHttpRequest);
            final String responseString = dmHttpResponse.responseBodyAsString;
            final Properties p = new Properties();
            p.load(new StringReader(responseString));
            final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
            final String buildVersion = MDMUtil.getProductProperty("productversion");
            if (productCode.startsWith("MDM")) {
                static_build = p.getProperty("MDM_BUILD");
            }
            else if (productCode.startsWith("DC")) {
                static_build = p.getProperty("DC_BUILD");
            }
            if (new VersionChecker().isGreaterOrEqual(buildVersion, static_build)) {
                result.put("is_build_latest", true);
            }
            else {
                result.put("is_build_latest", false);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in isBuildLatest ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    public AFWResponseModel getMigrationDataForRequest(final AFWRequestModel request) throws Exception {
        try {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MDMMigration")) {
                throw new APIHTTPException("COM0015", new Object[] { "Migration should be enabled for the server for this action. Contact support to enable it" });
            }
            AFWResponseModel response = new AFWResponseModel();
            final String topic = request.getTopic();
            final String publicKey = request.getKey();
            this.logger.log(Level.INFO, "Migration data fetch request for {0}" + topic);
            if (topic == null) {
                throw new APIHTTPException("COM0005", new Object[] { "Missing mandatory param : topic" });
            }
            if (topic.equals("ManagedGooglePlay")) {
                response = new AFWMigrationDataFetcher().fetchAFWDetailsForMigration(request);
            }
            else {
                if (!topic.equals("EMMUsersAndAccounts")) {
                    throw new APIHTTPException("COM0005", new Object[] { "Value of topic is not from allowed values " + topic });
                }
                response = new AFWMigrationDataFetcher().fetchAFWUsersAndAccounts(request);
            }
            if (response.getData() != null) {
                response = (AFWResponseModel)new ObjectMapper().readValue(new CryptographyUtil().encrypt(response.getData(), publicKey).toString(), (Class)AFWResponseModel.class);
                return response;
            }
            return new AFWResponseModel();
        }
        catch (final APIHTTPException e) {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72517, null, null, "mdm.migration.afw.failure", null, null);
            this.logger.log(Level.SEVERE, "API Exception", e);
            throw e;
        }
        catch (final Exception e2) {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72517, null, null, "mdm.migration.afw.failure", null, null);
            this.logger.log(Level.SEVERE, "Exception when getting migration data", e2);
            throw e2;
        }
    }
}
