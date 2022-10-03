package com.adventnet.sym.server.mdm.security;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.Base64;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class AgentSecretHandler
{
    Logger mdmLogger;
    
    public AgentSecretHandler() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public String getNonceForId(final String uniqueId) {
        try {
            final SelectQuery safetyNetQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentSecretTokens"));
            safetyNetQuery.addSelectColumn(Column.getColumn("AgentSecretTokens", "TOKEN_ENCRYPTED"));
            safetyNetQuery.addSelectColumn(Column.getColumn("AgentSecretTokens", "AGENTSECRET_ID"));
            safetyNetQuery.setCriteria(new Criteria(Column.getColumn("AgentSecretTokens", "AGENTSECRET_ID"), (Object)uniqueId, 0));
            final DataObject dObj = DataAccess.get(safetyNetQuery);
            return new String(Base64.getDecoder().decode((String)dObj.getFirstRow("AgentSecretTokens").get("TOKEN_ENCRYPTED")));
        }
        catch (final DataAccessException exp) {
            this.mdmLogger.log(Level.SEVERE, " exception while getting the safety net nonce from table ", (Throwable)exp);
            return null;
        }
    }
    
    public void deleteSecretDetails(final String uniqueId) {
        try {
            DataAccess.delete(new Criteria(Column.getColumn("AgentSecretTokens", "AGENTSECRET_ID"), (Object)uniqueId, 0));
        }
        catch (final DataAccessException exp) {
            this.mdmLogger.log(Level.SEVERE, " deleteSafetyNetDetails : Cannot delete the row for safety net ID ", (Throwable)exp);
        }
    }
    
    public String generateNonce(final String udid) {
        return Base64.getEncoder().encodeToString((String.valueOf(udid) + String.valueOf(System.currentTimeMillis())).getBytes());
    }
    
    public Long insertSecretsRowInDb(final Long resourceID, final String nonce) throws DataAccessException {
        final Row secretsRow = new Row("AgentSecretTokens");
        secretsRow.set("TOKEN_ENCRYPTED", (Object)nonce);
        secretsRow.set("RESOURCE_ID", (Object)resourceID);
        secretsRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
        final SelectQuery safetyNetQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentSecretTokens"));
        safetyNetQuery.addSelectColumn(Column.getColumn("AgentSecretTokens", "*"));
        final DataObject dObj = DataAccess.get(safetyNetQuery);
        dObj.addRow(secretsRow);
        DataAccess.update(dObj);
        return (Long)secretsRow.get("AGENTSECRET_ID");
    }
    
    public Long getResourceIdFromSecretId(final String secretId) {
        try {
            final SelectQuery safetyNetQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentSecretTokens"));
            safetyNetQuery.addSelectColumn(Column.getColumn("AgentSecretTokens", "RESOURCE_ID"));
            safetyNetQuery.addSelectColumn(Column.getColumn("AgentSecretTokens", "AGENTSECRET_ID"));
            safetyNetQuery.setCriteria(new Criteria(Column.getColumn("AgentSecretTokens", "AGENTSECRET_ID"), (Object)secretId, 0));
            return (Long)DataAccess.get(safetyNetQuery).getFirstRow("AgentSecretTokens").get("RESOURCE_ID");
        }
        catch (final DataAccessException exp) {
            this.mdmLogger.log(Level.SEVERE, " getResourceIdFromSafetyNetId : Exception while getting the resource id from safetynet id");
            return null;
        }
    }
}
