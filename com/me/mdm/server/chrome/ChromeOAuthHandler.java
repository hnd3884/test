package com.me.mdm.server.chrome;

import java.util.Arrays;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.i18n.I18N;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import org.json.JSONObject;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import java.util.logging.Level;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.chromedevicemanagement.v1.ChromeDeviceManagement;
import com.google.chromedevicemanagement.v1.model.ListEnterprisesResponse;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.me.mdm.server.apps.android.afw.GoogleAPINetworkManager;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.json.JSONException;
import java.util.Collection;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import java.util.logging.Logger;
import java.util.List;

public class ChromeOAuthHandler
{
    public static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final List<String> SCOPES;
    private static final String CLINET_ID = "ClinetId";
    private static final String CLINET_SECRET = "ClinetSecret";
    public static final String INSUFFICIENT_GSUITE_LICENSE = "insufficient upgrades to manage their devices";
    public static final String UNKNOWN_DOMAIN = "Unknown domain or API caller does not have permission";
    public Logger logger;
    
    public ChromeOAuthHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public String generateChromeOAuthURL(final String adminEmailId) throws JSONException, Exception {
        final String clientId = String.valueOf(this.getGoogleOAuthProjectDetailsForChromeMgmt().get("ClinetId"));
        final String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(clientId, "urn:ietf:wg:oauth:2.0:oob", (Collection)ChromeOAuthHandler.SCOPES).setAccessType("offline").set("login_hint", (Object)adminEmailId).build();
        return authorizationUrl;
    }
    
    public String getEnterpriseID(final String accessToken, final String domainName, final boolean dirIntegOnly) throws Exception {
        String enterpriseId = "--";
        try {
            final GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            final HttpTransport httpTransport = GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured();
            final JsonFactory jsonFactory = (JsonFactory)JacksonFactory.getDefaultInstance();
            final ListEnterprisesResponse enterpriseResp = (ListEnterprisesResponse)new ChromeDeviceManagement.Builder(httpTransport, jsonFactory, (HttpRequestInitializer)credential).setApplicationName("validateapplication").build().enterprises().list().setEnterpriseName(domainName).execute();
            enterpriseId = enterpriseResp.getEnterprise().getEnterpriseId();
        }
        catch (final Exception ex) {
            if (!dirIntegOnly) {
                throw ex;
            }
            Logger.getLogger(ChromeManagementHandler.class.getName()).log(Level.WARNING, "integrating only for directory integration.. hence enterprise check can be bypassed");
        }
        return enterpriseId;
    }
    
    public JSONObject validateAuthCode(final String authCode, final String domainName, final boolean dirIntegOnly) throws Exception {
        final JSONObject resultJSON = new JSONObject();
        final JSONObject googleProjectDetails = this.getGoogleOAuthProjectDetailsForChromeMgmt();
        try {
            final String clientId = String.valueOf(googleProjectDetails.get("ClinetId"));
            final String clientSecret = String.valueOf(googleProjectDetails.get("ClinetSecret"));
            final GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured(), (JsonFactory)new JacksonFactory(), clientId, clientSecret, authCode, "urn:ietf:wg:oauth:2.0:oob").execute();
            final String accessToken = response.getAccessToken();
            final String refreshToken = response.getRefreshToken();
            String enterpriseId = "--";
            if (!dirIntegOnly) {
                enterpriseId = this.getEnterpriseID(accessToken, domainName, dirIntegOnly);
            }
            final JSONObject dataJSON = new JSONObject();
            dataJSON.put("ClientId", (Object)clientId);
            dataJSON.put("RedirectURI", (Object)"urn:ietf:wg:oauth:2.0:oob");
            dataJSON.put("ClientSecret", (Object)clientSecret);
            dataJSON.put("RefreshToken", (Object)refreshToken);
            dataJSON.put("EnterpriseId", (Object)enterpriseId);
            resultJSON.put("Data", (Object)dataJSON);
            resultJSON.put("Status", (Object)"Success");
            return resultJSON;
        }
        catch (final Exception ex) {
            Logger.getLogger(ChromeOAuthHandler.class.getName()).log(Level.SEVERE, "Exception occurred in validateAuthCode()", ex);
            resultJSON.put("Status", (Object)"Error");
            resultJSON.put("ErrorMessage", (Object)this.getIntegrationFailureErrorMsg(ex));
            return resultJSON;
        }
    }
    
    private JSONObject getGoogleOAuthProjectDetailsForChromeMgmt() throws JSONException, Exception {
        final JSONObject detailsJSON = new JSONObject();
        detailsJSON.put("ClinetId", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("CHROMEMGMT_CLIENT_ID"));
        detailsJSON.put("ClinetSecret", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("CHROMEMGMT_CLIENT_SECRET"));
        return detailsJSON;
    }
    
    private String getIntegrationFailureErrorMsg(final Exception ex) throws Exception {
        String errorMsg;
        try {
            if (ex instanceof GoogleJsonResponseException && ((GoogleJsonResponseException)ex).getDetails().getMessage().contains("insufficient upgrades to manage their devices")) {
                errorMsg = "mdm.chrome.enroll.insufficient_gsuite_license@@@<l>$(mdmUrl)/help/enrollment/mdm_enroll_chromebooks.html?$(traceurl)#overview";
            }
            else if (ex instanceof GoogleJsonResponseException && ((GoogleJsonResponseException)ex).getDetails().getMessage().contains("Unknown domain or API caller does not have permission")) {
                errorMsg = "mdm.chrome.enroll.unknown_domain";
            }
            else if (ex instanceof TokenResponseException) {
                errorMsg = "mdm.chrome.enroll.invalid_code";
            }
            else {
                final String supportMessage = I18N.getMsg("mdm.chrome.enroll.integ_error_message", new Object[0]);
                errorMsg = "mdm.android.appmgmt.unknown_error@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(supportMessage);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(ChromeOAuthHandler.class.getName()).log(Level.SEVERE, "Exception occurred in getIntegrationFailureErrorMsg()", ex);
            errorMsg = "mdm.android.appmgmt.unknown_error@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(null);
        }
        errorMsg = MDMI18N.getMsg(errorMsg, false, false);
        return errorMsg;
    }
    
    static {
        SCOPES = Arrays.asList("https://www.googleapis.com/auth/admin.directory.user", "https://www.googleapis.com/auth/admin.directory.device.chromeos", "https://www.googleapis.com/auth/chromedevicemanagementapi");
    }
}
