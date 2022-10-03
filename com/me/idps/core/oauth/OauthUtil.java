package com.me.idps.core.oauth;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.me.idps.core.util.IdpsUtil;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import java.util.Iterator;
import java.util.Set;
import java.util.Properties;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;

public class OauthUtil
{
    private static OauthUtil util;
    
    public static OauthUtil getInstance() {
        if (OauthUtil.util == null) {
            OauthUtil.util = new OauthUtil();
        }
        return OauthUtil.util;
    }
    
    public Long registerOauth(final JSONObject object) {
        Long oauthId = null;
        try {
            oauthId = OauthDataHandler.getInstance().addOrUpdateOauthTokens(object);
        }
        catch (final Exception e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Error in Register Oauth ", e);
        }
        return oauthId;
    }
    
    private JSONObject convertPropertiesToJson(final Properties p) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final Set<Object> keys = ((Hashtable<Object, V>)p).keySet();
        final Iterator<Object> itr = keys.iterator();
        while (itr != null && itr.hasNext()) {
            final Object key = itr.next();
            final Object value = ((Hashtable<K, Object>)p).get(key);
            jsonObject.put((String)key, value);
        }
        return jsonObject;
    }
    
    private static Boolean isTokenValidByTokenJSON(final JSONObject tokenJSON) {
        final Long expiresAt = Long.parseLong(tokenJSON.optString("EXPIRES_AT", "-1"));
        if (expiresAt != -1L) {
            return expiresAt > System.currentTimeMillis();
        }
        return false;
    }
    
    private List registerScopes(final Long oauthId, final String[] value) {
        final List<String> values = new ArrayList<String>(Arrays.asList(value));
        final List scopes = OauthDataHandler.getInstance().addOrUpdateScopes(values);
        final List<Long> scopesId = new ArrayList<Long>();
        if (scopes == null) {
            return null;
        }
        for (final Properties scope : scopes) {
            final Long scopeId = ((Hashtable<K, Long>)scope).get("OAUTH_SCOPES_ID");
            scopesId.add(scopeId);
        }
        OauthDataHandler.getInstance().addOrUpdateOauthScopesMapping(oauthId, scopesId);
        return scopes;
    }
    
    public Long register(final Long customerID, final JSONObject oauth) throws Exception {
        Long oauthId = null;
        final Long userID = null;
        if (oauth.has("domain")) {
            final int domainType = oauth.getInt("domain");
            final int oauthType = oauth.getInt("OAUTH_TYPE");
            final JSONObject oauthObject = new JSONObject();
            oauthObject.put("OAUTH_TYPE", oauthType);
            final Properties metadata = IdpsFactoryProvider.getOauthImpl(oauthType).fetchMetadata(customerID, userID);
            if (metadata != null && metadata.containsKey("OAUTH_METADATA_ID")) {
                oauthObject.put("OAUTH_METADATA_ID", ((Hashtable<K, Object>)metadata).get("OAUTH_METADATA_ID"));
            }
            oauthObject.put("CREATED_BY", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            oauthObject.put("STATUS", 0);
            final JSONObject tokenJson = new JSONObject();
            tokenJson.put("OAUTH_TYPE", oauthType);
            tokenJson.put("OAUTH_CLIENT_ID", (Object)metadata.getProperty("OAUTH_CLIENT_ID"));
            tokenJson.put("OAUTH_CLIENT_SECRET", (Object)metadata.getProperty("OAUTH_CLIENT_SECRET"));
            if (oauth.has("scope")) {
                tokenJson.put("scope", oauth.get("scope"));
            }
            if (oauth.has("code")) {
                tokenJson.put("code", oauth.get("code"));
            }
            final JSONObject tokenJsonObject = this.registerTokens(customerID, tokenJson);
            if (tokenJsonObject.has("domain")) {
                final JSONObject domainProps = tokenJsonObject.optJSONObject("domain");
                final String ref_user = domainProps.optString("CRD_USERNAME", "--");
                oauthObject.put("REFERENCE_USER", (Object)ref_user);
                final String domain_name = domainProps.optString("NAME", "--");
                final Properties dmProps = DMDomainDataHandler.getInstance().getDomainProps(domain_name, customerID, domainType);
                final Long domainId = ((Hashtable<K, Long>)dmProps).get("DOMAIN_ID");
                if (domainId != null) {
                    oauthId = OauthDataHandler.getInstance().getOauthIdFromDomain(domainId, customerID, null, oauthType);
                }
                if (oauthId == null) {
                    oauthId = -1L;
                }
                oauthObject.put("OAUTH_TOKEN_ID", (Object)oauthId);
            }
            else if (oauth.has("OAUTH_TOKEN_ID")) {
                oauthObject.put("OAUTH_TOKEN_ID", oauth.get("OAUTH_TOKEN_ID"));
            }
            final Long oauthTokenId = this.registerOauth(oauthObject);
            OauthIdThreadLocal.setOauthId(oauthTokenId);
            oauthId = oauthTokenId;
            if (tokenJsonObject.has("scope")) {
                final String[] scopes = (String[])tokenJsonObject.get("scope");
                final List s = this.registerScopes(oauthTokenId, scopes);
                if (s == null) {
                    throw new OauthException("invalid_request");
                }
            }
            if (tokenJsonObject.has("tokens")) {
                final JSONArray tokens = tokenJsonObject.getJSONArray("tokens");
                for (int i = 0; i < tokens.length(); ++i) {
                    final JSONObject j = tokens.getJSONObject(i);
                    j.put("OAUTH_TOKEN_ID", (Object)oauthTokenId);
                    OauthDataHandler.getInstance().addOrUpdateAuthToken(j);
                }
            }
            if (tokenJsonObject.has("domain")) {
                final JSONObject domainProps2 = tokenJsonObject.optJSONObject("domain");
                domainProps2.remove("domain_type");
                switch (domainType) {
                    case 3: {
                        IdpsFactoryProvider.getIdpsAccessAPI(3).addOrUpdateAD(IdpsUtil.convertJsonToHashMap(domainProps2));
                        break;
                    }
                }
            }
        }
        return oauthId;
    }
    
    private JSONObject registerTokens(final Long customerID, final JSONObject oauthJson) throws OauthException {
        JSONObject tokenData = null;
        try {
            if (oauthJson.has("OAUTH_TYPE")) {
                final int oauthType = oauthJson.getInt("OAUTH_TYPE");
                final JSONObject tokenJsonObject = IdpsFactoryProvider.getOauthImpl(oauthType).generateTokens(customerID, oauthJson.optString("OAUTH_CLIENT_ID"), oauthJson.optString("OAUTH_CLIENT_SECRET"), oauthJson.optString("code"), oauthJson.optString("scope"));
                if (tokenJsonObject != null) {
                    tokenData = tokenJsonObject;
                }
            }
        }
        catch (final JSONException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Error in Register tokens ", (Throwable)e);
        }
        return tokenData;
    }
    
    public String fetchAccessTokenFromDomainId(final Long domainId, final Long customerID, final String[] scopes, final int oauthType) throws OauthException {
        final Long aLong = OauthDataHandler.getInstance().getOauthIdFromDomain(domainId, customerID, scopes, oauthType);
        if (aLong == null) {
            IdpsFactoryProvider.getOauthImpl(oauthType).fetchMetadata(customerID, null);
            throw new OauthException("invalid_request");
        }
        return this.fetchAccessTokenFromOauthId(aLong);
    }
    
    private Long getCustomerID(final Long oauthTokenId) throws DataAccessException {
        if (ApiFactoryProvider.getUtilAccessAPI().isMSP()) {
            Long customerID = null;
            final Criteria criteria = new Criteria(Column.getColumn("OauthTokens", "OAUTH_TOKEN_ID"), (Object)oauthTokenId, 0);
            final SelectQuery oauthMetaQuery = OauthDataHandler.getInstance().getOAuthBaseQuery();
            oauthMetaQuery.setCriteria(criteria);
            oauthMetaQuery.addSelectColumn(Column.getColumn("OAuthMetaPurposeRel", "CUSTOMER_ID"));
            oauthMetaQuery.addSelectColumn(Column.getColumn("OAuthMetaPurposeRel", "OAUTH_META_MAP_ID"));
            final DataObject dobj = IdpsUtil.getPersistenceLite().get(oauthMetaQuery);
            if (dobj != null && dobj.containsTable("OAuthMetaPurposeRel")) {
                final Row oauthTokenRow = dobj.getRow("OauthTokens", criteria);
                final Long oauthMetaID = (Long)oauthTokenRow.get("OAUTH_METADATA_ID");
                final Row oauthMetaMapRow = dobj.getRow("OAuthMetaPurposeRel", new Criteria(Column.getColumn("OAuthMetaPurposeRel", "OAUTH_METADATA_ID"), (Object)oauthMetaID, 0));
                customerID = (Long)oauthMetaMapRow.get("CUSTOMER_ID");
            }
            return customerID;
        }
        return CustomerInfoUtil.getInstance().getCustomerId();
    }
    
    public String fetchAccessTokenFromOauthId(final Long oauthTokenId) throws OauthException {
        String access_token = null;
        try {
            if (oauthTokenId != -1L) {
                final List accessToken = OauthDataHandler.getInstance().getAuthTokensByOauthId(oauthTokenId, 2);
                final Long customerID = this.getCustomerID(oauthTokenId);
                JSONObject accessTokenJSON = null;
                if (accessToken != null && accessToken.size() > 0) {
                    for (final Object o : accessToken) {
                        final Properties p = (Properties)o;
                        if (((Hashtable<K, Integer>)p).get("TOKEN_TYPE") == 2) {
                            accessTokenJSON = this.convertPropertiesToJson(p);
                            break;
                        }
                    }
                }
                if (accessTokenJSON != null) {
                    if (isTokenValidByTokenJSON(accessTokenJSON)) {
                        access_token = accessTokenJSON.getString("TOKEN_VALUE");
                    }
                    else {
                        final Properties oauthToken = OauthDataHandler.getInstance().getOauthTokensById(oauthTokenId, null, true);
                        final int oauthType = ((Hashtable<K, Integer>)oauthToken).get("OAUTH_TYPE");
                        final Properties metadata = IdpsFactoryProvider.getOauthImpl(oauthType).fetchMetadata(customerID, null);
                        final String clientId = metadata.getProperty("OAUTH_CLIENT_ID");
                        final String clientSecret = metadata.getProperty("OAUTH_CLIENT_SECRET");
                        final List refreshToken = OauthDataHandler.getInstance().getAuthTokensByOauthId(oauthTokenId, 1);
                        JSONObject refreshTokenJson = new JSONObject();
                        if (refreshToken != null && refreshToken.size() > 0) {
                            for (final Object o2 : refreshToken) {
                                final Properties p2 = (Properties)o2;
                                if (((Hashtable<K, Integer>)p2).get("TOKEN_TYPE") == 1) {
                                    refreshTokenJson = this.convertPropertiesToJson(p2);
                                    break;
                                }
                            }
                        }
                        String refreshTokenString = null;
                        if (refreshTokenJson.has("TOKEN_VALUE")) {
                            refreshTokenString = refreshTokenJson.optString("TOKEN_VALUE");
                        }
                        StringBuilder scopes = new StringBuilder("--");
                        if (oauthToken.containsKey("scope")) {
                            final List scopesList = ((Hashtable<K, List>)oauthToken).get("scope");
                            scopes = new StringBuilder();
                            final Iterator i = scopesList.iterator();
                            while (i.hasNext()) {
                                final Properties p3 = i.next();
                                scopes.append(p3.getProperty("VALUE"));
                                if (i.hasNext()) {
                                    scopes.append(" ");
                                }
                            }
                        }
                        final List tokens = IdpsFactoryProvider.getOauthImpl(oauthType).generateAccessTokenFromRefreshToken(customerID, clientId, clientSecret, refreshTokenString, scopes.toString());
                        for (final Object object : tokens) {
                            final Properties token = (Properties)object;
                            final JSONObject jsonObject = this.convertPropertiesToJson(token);
                            jsonObject.put("OAUTH_TOKEN_ID", (Object)oauthTokenId);
                            OauthDataHandler.getInstance().addOrUpdateAuthToken(jsonObject);
                            if (jsonObject.getInt("TOKEN_TYPE") == 2) {
                                access_token = jsonObject.getString("TOKEN_VALUE");
                            }
                        }
                    }
                }
            }
        }
        catch (final JSONException | DataAccessException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Error in JSON FetchaccessToken from oauth id", e);
        }
        return access_token;
    }
    
    public boolean revokeRefreshTokenByTokenId(final Long customerID, final Long userID, final Long oauthTokenId) {
        final Properties oauthProps = OauthDataHandler.getInstance().getOauthTokensById(oauthTokenId, null, false);
        final int oauthType = ((Hashtable<K, Integer>)oauthProps).get("OAUTH_TYPE");
        final Properties metadata = IdpsFactoryProvider.getOauthImpl(oauthType).fetchMetadata(customerID, userID);
        final String clientId = metadata.getProperty("OAUTH_CLIENT_ID");
        final String clientSecret = metadata.getProperty("OAUTH_CLIENT_SECRET");
        final Properties refreshToken = OauthDataHandler.getInstance().getAuthTokensByOauthId(oauthTokenId, 1).get(0);
        return refreshToken != null && refreshToken.containsKey("TOKEN_VALUE") && IdpsFactoryProvider.getOauthImpl(oauthType).revokeTokens(clientId, clientSecret, refreshToken.getProperty("TOKEN_VALUE"));
    }
    
    public boolean revokeAndRemoveOauthTokenByOauthTokenId(final Long customerID, final Long userID, final Long oauthTokenId) throws Exception {
        if (oauthTokenId != -1L) {
            this.revokeRefreshTokenByTokenId(customerID, userID, oauthTokenId);
            OauthDataHandler.getInstance().deleteOauthTokenFromOauthTokenId(oauthTokenId);
            return true;
        }
        return false;
    }
    
    static {
        OauthUtil.util = null;
    }
}
