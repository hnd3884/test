package com.me.mdm.server.apps.android.afw.usermgmt;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.json.GoogleJsonError;
import java.util.Arrays;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.me.mdm.server.chrome.ChromeManagementHandler;
import com.google.api.client.googleapis.batch.BatchRequest;
import org.json.JSONArray;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.services.directory.model.UserName;
import java.util.Iterator;
import java.io.IOException;
import org.json.JSONException;
import com.google.api.services.directory.model.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.api.services.directory.model.Users;
import com.google.api.client.http.HttpRequestInitializer;
import com.me.mdm.server.apps.android.afw.GoogleAPINetworkManager;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import java.util.Collection;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.json.JSONObject;
import com.google.api.services.directory.Directory;
import java.util.List;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.me.idps.core.factory.GsuiteAccessAPI;
import com.me.mdm.server.apps.businessstore.UserDirectory;

public class GoogleUsersDirectory implements UserDirectory, GsuiteAccessAPI
{
    private static final String APPLICATION_NAME = "ManageEngine EMM/1.0";
    private static final JsonFactory JSON_FACTORY;
    HttpTransport http_Transport;
    private static final List<String> ADMIN_DIRECTORY_SCOPES;
    Directory directory;
    JSONObject responseJSON;
    
    public GoogleUsersDirectory() {
        this.http_Transport = null;
        this.directory = null;
        this.responseJSON = null;
    }
    
    private GoogleCredential getGoogleCredential(final JSONObject jsonObject) throws Exception {
        if (jsonObject.getInt("OAUTH_TYPE") == GoogleForWorkSettings.OAUTH_TYPE_ESA) {
            final String credentialFilePath = String.valueOf(jsonObject.get("ESA_CREDENTIAL_JSON_PATH"));
            final String esaEMailAddress = String.valueOf(jsonObject.get("ESA_EMAIL_ID"));
            final String serviceAccountUser = String.valueOf(jsonObject.get("DOMAIN_ADMIN_EMAIL_ID"));
            return new GoogleCredential.Builder().setJsonFactory(GoogleUsersDirectory.JSON_FACTORY).setServiceAccountId(esaEMailAddress).setServiceAccountPrivateKey(GoogleForWorkSettings.getPrivateKeyFromCredentialJSONFile(credentialFilePath)).setTransport(this.http_Transport).setServiceAccountScopes((Collection)GoogleUsersDirectory.ADMIN_DIRECTORY_SCOPES).setServiceAccountUser(serviceAccountUser).build();
        }
        final JSONObject bearerJSON = jsonObject.getJSONObject("GoogleBearerTokenDetails");
        final String refreshToken = String.valueOf(bearerJSON.get("REFRESH_TOKEN"));
        final String clientId = String.valueOf(bearerJSON.get("CLIENT_ID"));
        final String clientSecret = String.valueOf(bearerJSON.get("CLIENT_SECRET"));
        final GoogleRefreshTokenRequest request = new GoogleRefreshTokenRequest(this.http_Transport, GoogleUsersDirectory.JSON_FACTORY, refreshToken, clientId, clientSecret);
        return new GoogleCredential().setAccessToken(request.execute().getAccessToken());
    }
    
    public void initialize(final JSONObject jsonObject) throws Exception {
        this.http_Transport = GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured();
        final String accessToken = jsonObject.optString("accessToken");
        final GoogleCredential credential = this.getGoogleCredential(jsonObject);
        if (accessToken != null && !accessToken.isEmpty()) {
            credential.setAccessToken(accessToken);
        }
        this.directory = new Directory.Builder(this.http_Transport, GoogleUsersDirectory.JSON_FACTORY, (HttpRequestInitializer)credential).setApplicationName("ManageEngine EMM/1.0").build();
    }
    
    @Override
    public JSONObject getUsers(final JSONObject jsonObject) {
        this.responseJSON = new JSONObject();
        final JSONObject usersJSON = new JSONObject();
        try {
            final String eMailDomainName = String.valueOf(jsonObject.get("MANAGED_DOMAIN_NAME"));
            String nextPageToken = null;
            do {
                final Directory.Users.List list = this.directory.users().list();
                list.setDomain(eMailDomainName);
                list.setMaxResults(Integer.valueOf(500));
                if (nextPageToken != null) {
                    list.setPageToken(nextPageToken);
                }
                final Users usersList = (Users)list.execute();
                final List<User> users = usersList.getUsers();
                if (users == null || users.isEmpty()) {
                    Logger.getLogger(GoogleUsersDirectory.class.getName()).log(Level.INFO, "No Users Found");
                }
                else {
                    for (final User user : users) {
                        final JSONObject userDetailsJsonObject = new JSONObject();
                        userDetailsJsonObject.put("BS_STORE_ID", (Object)user.getId());
                        final String primaryEmail = user.getPrimaryEmail();
                        userDetailsJsonObject.put("BS_MDM_ID", (Object)primaryEmail);
                        usersJSON.put(primaryEmail, (Object)userDetailsJsonObject);
                        userDetailsJsonObject.put("mail", (Object)primaryEmail);
                        userDetailsJsonObject.put("mail", (Object)user.getPrimaryEmail());
                        userDetailsJsonObject.put("objectGUID", (Object)user.getId());
                        if (user.getName() == null) {
                            userDetailsJsonObject.put("givenName", (Object)"--");
                            userDetailsJsonObject.put("sn", (Object)"--");
                            userDetailsJsonObject.put("displayName", (Object)primaryEmail);
                        }
                        else {
                            userDetailsJsonObject.put("givenName", (Object)user.getName().getGivenName());
                            userDetailsJsonObject.put("sn", (Object)user.getName().getFamilyName());
                            userDetailsJsonObject.put("displayName", (Object)user.getName().getFullName());
                        }
                    }
                }
                nextPageToken = usersList.getNextPageToken();
            } while (nextPageToken != null && !nextPageToken.isEmpty());
            this.responseJSON.put("users", (Object)usersJSON);
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", (Throwable)e);
        }
        catch (final IOException e2) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", e2);
        }
        return this.responseJSON;
    }
    
    public JSONObject getUserForIdentifer(final String identifier) {
        final JSONObject userDetailsJsonObject = new JSONObject();
        try {
            final User user = (User)this.directory.users().get(identifier).execute();
            userDetailsJsonObject.put("BS_STORE_ID", (Object)user.getId());
            final String primaryEmail = user.getPrimaryEmail();
            userDetailsJsonObject.put("BS_MDM_ID", (Object)primaryEmail);
            userDetailsJsonObject.put("EMAIL_ADDRESS", (Object)primaryEmail);
            userDetailsJsonObject.put("USER_GUID", (Object)user.getId());
            if (user.getName() == null) {
                userDetailsJsonObject.put("FIRST_NAME", (Object)"--");
                userDetailsJsonObject.put("LAST_NAME", (Object)"--");
                userDetailsJsonObject.put("DISPLAY_NAME", (Object)primaryEmail);
            }
            else {
                userDetailsJsonObject.put("FIRST_NAME", (Object)user.getName().getGivenName());
                userDetailsJsonObject.put("LAST_NAME", (Object)user.getName().getFamilyName());
                userDetailsJsonObject.put("DISPLAY_NAME", (Object)user.getName().getFullName());
            }
            return userDetailsJsonObject;
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", (Throwable)e);
        }
        catch (final IOException e2) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", e2);
        }
        return userDetailsJsonObject;
    }
    
    public JSONObject getUser(final JSONObject jsonObject, final String identifier) {
        this.responseJSON = new JSONObject();
        try {
            final User user = (User)this.directory.users().get(identifier).execute();
            final JSONObject jsonArray = new JSONObject();
            final JSONObject userDetailsJsonObject = new JSONObject();
            userDetailsJsonObject.put("BS_STORE_ID", (Object)user.getId());
            userDetailsJsonObject.put("BS_MDM_ID", (Object)user.getPrimaryEmail());
            userDetailsJsonObject.put("EMAIL_ADDRESS", (Object)user.getPrimaryEmail());
            userDetailsJsonObject.put("USER_GUID", (Object)user.getId());
            if (user.getName() == null) {
                userDetailsJsonObject.put("FIRST_NAME", (Object)"--");
                userDetailsJsonObject.put("LAST_NAME", (Object)"--");
                userDetailsJsonObject.put("DISPLAY_NAME", (Object)user.getPrimaryEmail());
            }
            else {
                userDetailsJsonObject.put("FIRST_NAME", (Object)user.getName().getGivenName());
                userDetailsJsonObject.put("LAST_NAME", (Object)user.getName().getFamilyName());
                userDetailsJsonObject.put("DISPLAY_NAME", (Object)user.getName().getFullName());
            }
            jsonArray.put(user.getId(), (Object)userDetailsJsonObject);
            this.responseJSON.put("users", (Object)jsonArray);
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", (Throwable)e);
        }
        catch (final IOException e2) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", e2);
        }
        return this.responseJSON;
    }
    
    @Override
    public JSONObject addUser(final JSONObject jsonObject) {
        try {
            this.responseJSON = new JSONObject();
            final JSONArray users = jsonObject.getJSONArray("users");
            final int totalappstobeinstalled = users.length();
            final BatchRequest batchRequest = this.directory.batch();
            for (int i = 0; i < totalappstobeinstalled; ++i) {
                final JSONObject userDetailsJSON = users.getJSONObject(i);
                final String userName = String.valueOf(userDetailsJSON.get("UserName"));
                final String userEMailAddress = String.valueOf(userDetailsJSON.get("UserIdentifier"));
                final String userPassword = String.valueOf(userDetailsJSON.get("UserPassword"));
                final UserName un = new UserName();
                un.setFamilyName(userName);
                un.setGivenName(userName);
                un.setFullName(userName);
                final User user = new User();
                user.setName(un);
                user.setPassword(userPassword);
                user.setPrimaryEmail(userEMailAddress);
                this.directory.users().insert(user).queue(batchRequest, (JsonBatchCallback)new UserAddStatusCallback());
            }
            batchRequest.execute();
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMAppMgmtLogger").log(Level.WARNING, "Exception", exp);
        }
        return this.responseJSON;
    }
    
    @Override
    public JSONObject deleteUser(final JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject updateUser(final JSONObject jsonObject) {
        try {
            this.responseJSON = new JSONObject();
            final JSONArray users = jsonObject.getJSONArray("users");
            final int totalappstobeinstalled = users.length();
            final BatchRequest batchRequest = this.directory.batch();
            for (int i = 0; i < totalappstobeinstalled; ++i) {
                final JSONObject userDetailsJSON = users.getJSONObject(i);
                final String userEMailAddress = String.valueOf(userDetailsJSON.get("UserEMailAddress"));
                final String userPassword = String.valueOf(userDetailsJSON.get("UserPassword"));
                final User user = new User();
                user.setPassword(userPassword);
                user.setPrimaryEmail(userEMailAddress);
                this.directory.users().update(userEMailAddress, user).queue(batchRequest, (JsonBatchCallback)new UserAddStatusCallback());
            }
            batchRequest.execute();
        }
        catch (final Exception ex) {}
        return this.responseJSON;
    }
    
    public void close() {
        try {
            this.directory.getRequestFactory().getTransport().shutdown();
        }
        catch (final IOException ex) {
            Logger.getLogger(GoogleUsersDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public JSONObject getUsersForIdps(final Long customerId) throws Exception {
        final JSONObject googleESADetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        this.initialize(googleESADetails);
        final JSONObject users = this.getUsers(googleESADetails);
        return users;
    }
    
    public boolean updateEnterpriseID(final JSONObject googleESAJSON) throws Exception {
        final Long customerID = googleESAJSON.getLong("CUSTOMER_ID");
        final String domainName = googleESAJSON.getString("MANAGED_DOMAIN_NAME");
        final GoogleCredential gc = this.getGoogleCredential(googleESAJSON);
        final String accessToken = gc.getAccessToken();
        final boolean updated = ChromeManagementHandler.updateEnterpriseID(customerID, domainName, accessToken);
        return updated;
    }
    
    static {
        JSON_FACTORY = (JsonFactory)JacksonFactory.getDefaultInstance();
        ADMIN_DIRECTORY_SCOPES = Arrays.asList("https://www.googleapis.com/auth/admin.directory.user");
    }
    
    private class UserAddStatusCallback extends JsonBatchCallback<User>
    {
        public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public void onSuccess(final User user, final HttpHeaders hh) throws IOException {
            try {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("UserEMailAddress", (Object)user.getPrimaryEmail());
                jsonObject.put("UserID", (Object)user.getId());
                GoogleUsersDirectory.this.responseJSON.put(user.getPrimaryEmail(), (Object)jsonObject);
            }
            catch (final Exception ex) {}
        }
    }
}
