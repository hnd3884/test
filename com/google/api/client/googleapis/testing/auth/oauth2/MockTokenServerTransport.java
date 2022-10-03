package com.google.api.client.googleapis.testing.auth.oauth2;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.googleapis.testing.TestUtils;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import java.io.IOException;
import com.google.api.client.http.LowLevelHttpRequest;
import java.util.HashMap;
import java.util.Map;
import com.google.api.client.json.JsonFactory;
import java.util.logging.Logger;
import com.google.api.client.util.Beta;
import com.google.api.client.testing.http.MockHttpTransport;

@Beta
public class MockTokenServerTransport extends MockHttpTransport
{
    private static final String LEGACY_TOKEN_SERVER_URL = "https://accounts.google.com/o/oauth2/token";
    private static final Logger LOGGER;
    static final String EXPECTED_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    static final JsonFactory JSON_FACTORY;
    final String tokenServerUrl;
    Map<String, String> serviceAccounts;
    Map<String, String> clients;
    Map<String, String> refreshTokens;
    
    public MockTokenServerTransport() {
        this("https://oauth2.googleapis.com/token");
    }
    
    public MockTokenServerTransport(final String tokenServerUrl) {
        this.serviceAccounts = new HashMap<String, String>();
        this.clients = new HashMap<String, String>();
        this.refreshTokens = new HashMap<String, String>();
        this.tokenServerUrl = tokenServerUrl;
    }
    
    public void addServiceAccount(final String email, final String accessToken) {
        this.serviceAccounts.put(email, accessToken);
    }
    
    public void addClient(final String clientId, final String clientSecret) {
        this.clients.put(clientId, clientSecret);
    }
    
    public void addRefreshToken(final String refreshToken, final String accessTokenToReturn) {
        this.refreshTokens.put(refreshToken, accessTokenToReturn);
    }
    
    public LowLevelHttpRequest buildRequest(final String method, final String url) throws IOException {
        if (url.equals(this.tokenServerUrl)) {
            return (LowLevelHttpRequest)this.buildTokenRequest(url);
        }
        if (url.equals("https://accounts.google.com/o/oauth2/token")) {
            MockTokenServerTransport.LOGGER.warning("Your configured token_uri is using a legacy endpoint. You may want to redownload your credentials.");
            return (LowLevelHttpRequest)this.buildTokenRequest(url);
        }
        return super.buildRequest(method, url);
    }
    
    private MockLowLevelHttpRequest buildTokenRequest(final String url) {
        return new MockLowLevelHttpRequest(url) {
            public LowLevelHttpResponse execute() throws IOException {
                final String content = this.getContentAsString();
                final Map<String, String> query = TestUtils.parseQuery(content);
                String accessToken = null;
                final String foundId = query.get("client_id");
                if (foundId != null) {
                    if (!MockTokenServerTransport.this.clients.containsKey(foundId)) {
                        throw new IOException("Client ID not found.");
                    }
                    final String foundSecret = query.get("client_secret");
                    final String expectedSecret = MockTokenServerTransport.this.clients.get(foundId);
                    if (foundSecret == null || !foundSecret.equals(expectedSecret)) {
                        throw new IOException("Client secret not found.");
                    }
                    final String foundRefresh = query.get("refresh_token");
                    if (!MockTokenServerTransport.this.refreshTokens.containsKey(foundRefresh)) {
                        throw new IOException("Refresh Token not found.");
                    }
                    accessToken = MockTokenServerTransport.this.refreshTokens.get(foundRefresh);
                }
                else {
                    if (!query.containsKey("grant_type")) {
                        throw new IOException("Unknown token type.");
                    }
                    final String grantType = query.get("grant_type");
                    if (!"urn:ietf:params:oauth:grant-type:jwt-bearer".equals(grantType)) {
                        throw new IOException("Unexpected Grant Type.");
                    }
                    final String assertion = query.get("assertion");
                    final JsonWebSignature signature = JsonWebSignature.parse(MockTokenServerTransport.JSON_FACTORY, assertion);
                    final String foundEmail = signature.getPayload().getIssuer();
                    if (!MockTokenServerTransport.this.serviceAccounts.containsKey(foundEmail)) {
                        throw new IOException("Service Account Email not found as issuer.");
                    }
                    accessToken = MockTokenServerTransport.this.serviceAccounts.get(foundEmail);
                    final String foundScopes = (String)signature.getPayload().get((Object)"scope");
                    if (foundScopes == null || foundScopes.length() == 0) {
                        throw new IOException("Scopes not found.");
                    }
                }
                final GenericJson refreshContents = new GenericJson();
                refreshContents.setFactory(MockTokenServerTransport.JSON_FACTORY);
                refreshContents.put("access_token", (Object)accessToken);
                refreshContents.put("expires_in", (Object)3600);
                refreshContents.put("token_type", (Object)"Bearer");
                final String refreshText = refreshContents.toPrettyString();
                final MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setContentType("application/json; charset=UTF-8").setContent(refreshText);
                return (LowLevelHttpResponse)response;
            }
        };
    }
    
    static {
        LOGGER = Logger.getLogger(MockTokenServerTransport.class.getName());
        JSON_FACTORY = (JsonFactory)new GsonFactory();
    }
}
