package com.me.idps.core.service.azure;

import com.me.idps.core.oauth.OauthException;
import java.util.Properties;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class AzureOauthLoginImpl
{
    private AzureLoginDetails getAzureLoginDetails(final JSONObject authDetails) throws JSONException {
        final JSONObject userDomainDetails = authDetails.getJSONObject("domain");
        final String tenantId = userDomainDetails.getString("tid");
        final String domainName = userDomainDetails.getString("NAME");
        final String userPrincipalName = userDomainDetails.getString("CRD_USERNAME");
        return new AzureLoginDetails((String)null, tenantId, domainName, userPrincipalName, (String)null);
    }
    
    public AzureLoginDetails getUserDetails(final String azureCode) throws JSONException, OauthException {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final AzureOauthImpl azureOauth = (AzureOauthImpl)IdpsFactoryProvider.getOauthImpl(201);
        final Properties azureMetadata = azureOauth.fetchMetadata(customerId, null);
        final String clientId = azureMetadata.getProperty("OAUTH_CLIENT_ID");
        final String clientSecret = azureMetadata.getProperty("OAUTH_CLIENT_SECRET");
        final JSONObject authDetails = azureOauth.generateTokens(customerId, clientId, clientSecret, azureCode, "https://graph.windows.net/User.Read offline_access");
        final AzureLoginDetails ald = this.getAzureLoginDetails(authDetails);
        return ald;
    }
    
    public class AzureLoginDetails
    {
        private String guid;
        private String tenantId;
        private String domainName;
        private String userPrincipalName;
        private String emailAddress;
        
        private AzureLoginDetails() {
        }
        
        private AzureLoginDetails(final String guid, final String tenantId, final String domainName, final String userPrincipalName, final String emailAddress) {
            this.guid = guid;
            this.tenantId = tenantId;
            this.domainName = domainName;
            this.userPrincipalName = userPrincipalName;
            this.emailAddress = emailAddress;
        }
        
        public String getDomainName() {
            return this.domainName;
        }
        
        public String getUserPrincipalName() {
            return this.userPrincipalName;
        }
        
        public String getEmailAddress() {
            return this.emailAddress;
        }
        
        public void setEmailAddress(final String emailAddress) {
            this.emailAddress = emailAddress;
        }
    }
}
