package com.me.mdm.onpremise.remotesession;

import com.me.idps.core.oauth.OauthException;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.oauth.OauthUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;

public class AssistAuthTokenHandler
{
    public void addOrUpdateAssistAuthTokenDetails(final JSONObject dataJSON) throws JSONException, DataAccessException {
        final Long customerId = dataJSON.getLong("CUSTOMER_ID");
        String emailId = "--";
        if (dataJSON.has("EMAIL_ADDRESS")) {
            emailId = dataJSON.getString("EMAIL_ADDRESS");
        }
        Long authTokenId = null;
        if (dataJSON.has("AUTH_TOKEN_ID")) {
            authTokenId = dataJSON.getLong("AUTH_TOKEN_ID");
        }
        Integer tokenParam = -1;
        if (dataJSON.has("TOKEN_PARAM")) {
            tokenParam = dataJSON.optInt("TOKEN_PARAM", -1);
        }
        Long addedBy = null;
        if (dataJSON.has("ADDED_BY")) {
            addedBy = dataJSON.getLong("ADDED_BY");
        }
        String domain = "--";
        if (dataJSON.has("CUSTOMER_COUNTRY_CODE")) {
            domain = String.valueOf(dataJSON.get("CUSTOMER_COUNTRY_CODE"));
        }
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AssistIntegrationDetails"));
        sQuery.setCriteria(new Criteria(new Column("AssistIntegrationDetails", "CUSTOMER_ID"), (Object)customerId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("AssistIntegrationDetails");
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("AUTH_TOKEN_ID", (Object)authTokenId);
            row.set("EMAIL_ADDRESS", (Object)emailId);
            row.set("ADDED_BY", (Object)addedBy);
            row.set("ADDED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("CUSTOMER_COUNTRY_CODE", (Object)domain);
            row.set("TOKEN_PARAM", (Object)tokenParam);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("AssistIntegrationDetails");
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("AUTH_TOKEN_ID", (Object)authTokenId);
            row.set("EMAIL_ADDRESS", (Object)emailId);
            row.set("ADDED_BY", (Object)addedBy);
            row.set("ADDED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("CUSTOMER_COUNTRY_CODE", (Object)domain);
            if (tokenParam != -1) {
                row.set("TOKEN_PARAM", (Object)tokenParam);
            }
            dO.updateRow(row);
        }
        DataAccess.update(dO);
    }
    
    public boolean isAssistIntegrated(final Long customerId) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AssistIntegrationDetails"));
            sQuery.setCriteria(new Criteria(new Column("AssistIntegrationDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(sQuery);
            return !dO.isEmpty();
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AssistAuthTokenHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return false;
        }
    }
    
    public String getAuthToken(final Long customerId) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AssistIntegrationDetails"));
            sQuery.setCriteria(new Criteria(new Column("AssistIntegrationDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(sQuery);
            if (dO.isEmpty()) {
                return null;
            }
            final Long oauthTokenId = (Long)dO.getRow("AssistIntegrationDetails").get("AUTH_TOKEN_ID");
            return OauthUtil.getInstance().fetchAccessTokenFromOauthId(oauthTokenId);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AssistAuthTokenHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
        catch (final OauthException e) {
            IDPSlogger.OAUTH.log(Level.SEVERE, "Error while fetching oauthId for zoho assist", (Throwable)e);
            return null;
        }
    }
    
    public JSONObject getAssistAccountDetails(final Long customerId) throws JSONException {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AssistIntegrationDetails"));
            sQuery.setCriteria(new Criteria(new Column("AssistIntegrationDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(sQuery);
            if (dO.isEmpty()) {
                return null;
            }
            final Row row = dO.getRow("AssistIntegrationDetails");
            final JSONObject assistAccountDetails = new JSONObject();
            assistAccountDetails.put("AUTH_TOKEN_ID", row.get("AUTH_TOKEN_ID"));
            assistAccountDetails.put("AUTH_TOKEN", row.get("AUTH_TOKEN"));
            assistAccountDetails.put("EMAIL_ADDRESS", row.get("EMAIL_ADDRESS"));
            assistAccountDetails.put("TOKEN_PARAM", row.get("TOKEN_PARAM"));
            assistAccountDetails.put("CUSTOMER_COUNTRY_CODE", row.get("CUSTOMER_COUNTRY_CODE"));
            return assistAccountDetails;
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AssistAuthTokenHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
    
    public void resetAssistIntegDetails(final Long customerId) throws DataAccessException {
        DataAccess.delete("AssistIntegrationDetails", new Criteria(new Column("AssistIntegrationDetails", "CUSTOMER_ID"), (Object)customerId, 0));
    }
}
