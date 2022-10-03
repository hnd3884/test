package com.me.ems.onpremise.common.oauth;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class OauthDataHandler
{
    private static OauthDataHandler oauthDataHandler;
    private static Logger logger;
    
    public static OauthDataHandler getInstance() {
        if (OauthDataHandler.oauthDataHandler == null) {
            OauthDataHandler.oauthDataHandler = new OauthDataHandler();
        }
        return OauthDataHandler.oauthDataHandler;
    }
    
    private Row getOauthCredentialRow(Row row, final String clientId, final String clientSecret, final String authUrl, final String tokenUrl, final String scope, final String accessToken, final String refreshToken, final Long expiresAt) {
        if (row == null) {
            row = new Row("OauthCredential");
        }
        row.set("CLIENT_ID", (Object)clientId);
        row.set("CLIENT_SECRET", (Object)clientSecret);
        row.set("AUTH_URL", (Object)authUrl);
        row.set("TOKEN_URL", (Object)tokenUrl);
        row.set("SCOPE", (Object)scope);
        row.set("ACCESS_TOKEN", (Object)accessToken);
        row.set("REFRESH_TOKEN", (Object)refreshToken);
        row.set("EXPIRES_AT", (Object)expiresAt);
        return row;
    }
    
    public Long addOrUpdateOauthCredential(Long credentialId, final String clientId, final String clientSecret, final String authUrl, final String tokenUrl, final String scope, final String accessToken, final String refreshToken, final Long expiresAt) throws DataAccessException {
        Criteria criteria = null;
        if (credentialId != null) {
            criteria = new Criteria(Column.getColumn("OauthCredential", "CREDENTIAL_ID"), (Object)credentialId, 0);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthCredential"));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "CREDENTIAL_ID"));
        final DataObject dObj = SyMUtil.getPersistence().get(selectQuery);
        if (dObj.isEmpty()) {
            final Row oauthCredentialRow = this.getOauthCredentialRow(null, clientId, clientSecret, authUrl, tokenUrl, scope, accessToken, refreshToken, expiresAt);
            dObj.addRow(oauthCredentialRow);
            final DataObject resultDo = SyMUtil.getPersistence().add(dObj);
            if (resultDo != null && !resultDo.isEmpty()) {
                credentialId = (Long)resultDo.getFirstRow("OauthCredential").get("CREDENTIAL_ID");
            }
        }
        else {
            Row oauthCredentialRow = dObj.getRow("OauthCredential");
            credentialId = oauthCredentialRow.getLong("CREDENTIAL_ID");
            oauthCredentialRow = this.getOauthCredentialRow(oauthCredentialRow, clientId, clientSecret, authUrl, tokenUrl, scope, accessToken, refreshToken, expiresAt);
            dObj.updateRow(oauthCredentialRow);
            SyMUtil.getPersistence().update(dObj);
        }
        return credentialId;
    }
    
    public JSONObject getOauthCredential(final Long credentialId) {
        JSONObject jsonObject = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthCredential"));
        if (credentialId != null) {
            selectQuery.setCriteria(new Criteria(Column.getColumn("OauthCredential", "CREDENTIAL_ID"), (Object)credentialId, 0));
        }
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "CREDENTIAL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "CLIENT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "CLIENT_SECRET"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "EXPIRES_AT"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "ACCESS_TOKEN"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "REFRESH_TOKEN"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "SCOPE"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "AUTH_URL"));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "TOKEN_URL"));
        try {
            final DataObject dObj = SyMUtil.getPersistence().get(selectQuery);
            if (!dObj.isEmpty()) {
                jsonObject = new JSONObject();
                final Row r = dObj.getFirstRow("OauthCredential");
                jsonObject.put("CREDENTIAL_ID", r.get("CREDENTIAL_ID"));
                jsonObject.put("CLIENT_ID", r.get("CLIENT_ID"));
                jsonObject.put("CLIENT_SECRET", r.get("CLIENT_SECRET"));
                jsonObject.put("EXPIRES_AT", r.get("EXPIRES_AT"));
                jsonObject.put("ACCESS_TOKEN", r.get("ACCESS_TOKEN"));
                jsonObject.put("REFRESH_TOKEN", r.get("REFRESH_TOKEN"));
                jsonObject.put("SCOPE", r.get("SCOPE"));
                jsonObject.put("AUTH_URL", r.get("AUTH_URL"));
                jsonObject.put("TOKEN_URL", r.get("TOKEN_URL"));
            }
        }
        catch (final DataAccessException e) {
            OauthDataHandler.logger.log(Level.WARNING, "SMTP Exception: Exception in getOauthCredential", (Throwable)e);
        }
        return jsonObject;
    }
    
    public void updateAccessTokenAndExpiry(final Long credentialId, final String accessToken, final long expiresAt) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OauthCredential"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("OauthCredential", "CREDENTIAL_ID"), (Object)credentialId, 0));
        selectQuery.addSelectColumn(Column.getColumn("OauthCredential", "CREDENTIAL_ID"));
        try {
            final DataObject dObj = SyMUtil.getPersistence().get(selectQuery);
            if (!dObj.isEmpty()) {
                final Row row = dObj.getRow("OauthCredential");
                row.set("ACCESS_TOKEN", (Object)accessToken);
                row.set("EXPIRES_AT", (Object)expiresAt);
                dObj.updateRow(row);
                SyMUtil.getPersistence().update(dObj);
            }
        }
        catch (final DataAccessException e) {
            OauthDataHandler.logger.log(Level.WARNING, "SMTP Exception: Exception in getOauthCredential", (Throwable)e);
        }
    }
    
    public JSONObject getProbeHandlerObject() {
        final JSONObject oauthCredential = this.getOauthCredential(null);
        JSONObject tokens = null;
        if (oauthCredential != null) {
            tokens = new JSONObject();
            tokens.put("ACCESS_TOKEN", oauthCredential.get("ACCESS_TOKEN"));
            tokens.put("REFRESH_TOKEN", oauthCredential.get("REFRESH_TOKEN"));
            tokens.put("EXPIRES_AT", oauthCredential.get("EXPIRES_AT"));
        }
        return tokens;
    }
    
    public void deleteOAuthCredentials() {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OauthCredential");
        try {
            DataAccess.delete(deleteQuery);
        }
        catch (final DataAccessException e) {
            OauthDataHandler.logger.log(Level.WARNING, "SMTP Exception: Exception in deleteOAuthCredentials", (Throwable)e);
        }
    }
    
    static {
        OauthDataHandler.oauthDataHandler = null;
        OauthDataHandler.logger = Logger.getLogger(OauthDataHandler.class.getName());
    }
}
