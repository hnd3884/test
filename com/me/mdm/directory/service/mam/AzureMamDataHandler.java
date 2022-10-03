package com.me.mdm.directory.service.mam;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;

public class AzureMamDataHandler
{
    private static AzureMamDataHandler azureMamDataHandler;
    
    public static AzureMamDataHandler getInstance() {
        if (AzureMamDataHandler.azureMamDataHandler == null) {
            AzureMamDataHandler.azureMamDataHandler = new AzureMamDataHandler();
        }
        return AzureMamDataHandler.azureMamDataHandler;
    }
    
    public void addOrUpdateAzureMamProps(final JSONObject data) throws DataAccessException {
        final Long customerId = data.getLong("CUSTOMER_ID");
        final Long addedBy = data.optLong("ADDED_BY");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AzureMamProps"));
        sQuery.setCriteria(new Criteria(new Column("AzureMamProps", "CUSTOMER_ID"), (Object)customerId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Long oauth_id = data.getLong("AUTH_TOKEN_ID");
            final Row row = new Row("AzureMamProps");
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("AUTH_TOKEN_ID", (Object)oauth_id);
            row.set("ADDED_BY", (Object)addedBy);
            row.set("DOMAIN_NAME", (Object)data.optString("DOMAIN_NAME", "--"));
            row.set("AZURE_UPN", (Object)data.optString("AZURE_UPN", "--"));
            row.set("ADDED_TIME", (Object)System.currentTimeMillis());
            dO.addRow(row);
        }
        else {
            final Row row2 = dO.getRow("AzureMamProps");
            if (data.has("AUTH_TOKEN_ID")) {
                row2.set("AUTH_TOKEN_ID", (Object)data.getLong("AUTH_TOKEN_ID"));
            }
            if (data.has("ADDED_BY")) {
                row2.set("ADDED_BY", (Object)addedBy);
            }
            if (data.has("DOMAIN_NAME")) {
                row2.set("DOMAIN_NAME", (Object)data.optString("DOMAIN_NAME", "--"));
            }
            if (data.has("AZURE_UPN")) {
                row2.set("AZURE_UPN", (Object)data.optString("AZURE_UPN", "--"));
            }
            row2.set("ADDED_TIME", (Object)System.currentTimeMillis());
            dO.updateRow(row2);
        }
        DataAccess.update(dO);
    }
    
    public JSONObject getAzureMamDetails(final Long customerId) throws JSONException {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AzureMamProps"));
            sQuery.setCriteria(new Criteria(new Column("AzureMamProps", "CUSTOMER_ID"), (Object)customerId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(sQuery);
            if (dO.isEmpty()) {
                return null;
            }
            final Row row = dO.getRow("AzureMamProps");
            final JSONObject azureMamDetails = new JSONObject();
            azureMamDetails.put("AUTH_TOKEN_ID", row.get("AUTH_TOKEN_ID"));
            final String userName = DMUserHandler.getUserNameFromUserID((Long)row.get("ADDED_BY"));
            azureMamDetails.put("ADDED_BY", (Object)userName);
            azureMamDetails.put("ADDED_TIME", row.get("ADDED_TIME"));
            azureMamDetails.put("DOMAIN_NAME", row.get("DOMAIN_NAME"));
            azureMamDetails.put("AZURE_UPN", row.get("AZURE_UPN"));
            return azureMamDetails;
        }
        catch (final DataAccessException | SyMException ex) {
            Logger.getLogger(AzureMamDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    static {
        AzureMamDataHandler.azureMamDataHandler = null;
    }
}
