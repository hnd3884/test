package com.google.api.client.googleapis.testing.compute;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.google.api.client.json.GenericJson;
import java.io.IOException;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Beta;
import com.google.api.client.testing.http.MockHttpTransport;

@Beta
public class MockMetadataServerTransport extends MockHttpTransport
{
    private static final String METADATA_SERVER_URL;
    private static final String METADATA_TOKEN_SERVER_URL;
    static final JsonFactory JSON_FACTORY;
    String accessToken;
    Integer tokenRequestStatusCode;
    
    public MockMetadataServerTransport(final String accessToken) {
        this.accessToken = accessToken;
    }
    
    public void setTokenRequestStatusCode(final Integer tokenRequestStatusCode) {
        this.tokenRequestStatusCode = tokenRequestStatusCode;
    }
    
    public LowLevelHttpRequest buildRequest(final String method, final String url) throws IOException {
        if (url.equals(MockMetadataServerTransport.METADATA_TOKEN_SERVER_URL)) {
            final MockLowLevelHttpRequest request = new MockLowLevelHttpRequest(url) {
                public LowLevelHttpResponse execute() throws IOException {
                    if (MockMetadataServerTransport.this.tokenRequestStatusCode != null) {
                        final MockLowLevelHttpResponse response = new MockLowLevelHttpResponse().setStatusCode((int)MockMetadataServerTransport.this.tokenRequestStatusCode).setContent("Token Fetch Error");
                        return (LowLevelHttpResponse)response;
                    }
                    final String metadataRequestHeader = this.getFirstHeaderValue("Metadata-Flavor");
                    if (!"Google".equals(metadataRequestHeader)) {
                        throw new IOException("Metadata request header not found.");
                    }
                    final GenericJson refreshContents = new GenericJson();
                    refreshContents.setFactory(MockMetadataServerTransport.JSON_FACTORY);
                    refreshContents.put("access_token", (Object)MockMetadataServerTransport.this.accessToken);
                    refreshContents.put("expires_in", (Object)3600000);
                    refreshContents.put("token_type", (Object)"Bearer");
                    final String refreshText = refreshContents.toPrettyString();
                    final MockLowLevelHttpResponse response2 = new MockLowLevelHttpResponse().setContentType("application/json; charset=UTF-8").setContent(refreshText);
                    return (LowLevelHttpResponse)response2;
                }
            };
            return (LowLevelHttpRequest)request;
        }
        if (url.equals(MockMetadataServerTransport.METADATA_SERVER_URL)) {
            final MockLowLevelHttpRequest request = new MockLowLevelHttpRequest(url) {
                public LowLevelHttpResponse execute() {
                    final MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                    response.addHeader("Metadata-Flavor", "Google");
                    return (LowLevelHttpResponse)response;
                }
            };
            return (LowLevelHttpRequest)request;
        }
        return super.buildRequest(method, url);
    }
    
    static {
        METADATA_SERVER_URL = OAuth2Utils.getMetadataServerUrl();
        METADATA_TOKEN_SERVER_URL = MockMetadataServerTransport.METADATA_SERVER_URL + "/computeMetadata/v1/instance/service-accounts/default/token";
        JSON_FACTORY = (JsonFactory)new GsonFactory();
    }
}
