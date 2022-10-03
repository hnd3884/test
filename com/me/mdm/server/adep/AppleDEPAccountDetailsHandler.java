package com.me.mdm.server.adep;

import com.me.mdm.core.ios.adep.AppleDEPServerConstants;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppleDEPAccountDetailsHandler
{
    private static AppleDEPAccountDetailsHandler accountDetailsHandler;
    public static Logger logger;
    
    public static AppleDEPAccountDetailsHandler getInstance() {
        if (AppleDEPAccountDetailsHandler.accountDetailsHandler == null) {
            AppleDEPAccountDetailsHandler.accountDetailsHandler = new AppleDEPAccountDetailsHandler();
        }
        return AppleDEPAccountDetailsHandler.accountDetailsHandler;
    }
    
    public void manageAccountDetails(final Long customerID, final Long tokenID) throws Exception {
        final JSONObject accountJSON = AppleDEPWebServicetHandler.getInstance(tokenID, customerID).getAccountDetailsJSON();
        accountJSON.put("CUSTOMER_ID", (Object)customerID);
        accountJSON.put("DEP_TOKEN_ID", (Object)tokenID);
        this.addOrUpdateAccountDetails(accountJSON);
    }
    
    public void addOrUpdateAccountDetails(final JSONObject accountJSON) {
        try {
            final Long customerID = accountJSON.getLong("CUSTOMER_ID");
            final Long tokenID = accountJSON.getLong("DEP_TOKEN_ID");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
            sQuery.addJoin(new Join("DEPTokenDetails", "DEPAccountDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "*"));
            sQuery.addSelectColumn(Column.getColumn("DEPAccountDetails", "*"));
            Criteria cCusId = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            cCusId = cCusId.and(new Criteria(new Column("DEPAccountDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0));
            sQuery.setCriteria(cCusId);
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            final Iterator iter = DO.get("DEPAccountDetails", "DEP_TOKEN_ID");
            boolean isAdd = false;
            Row accountRow = null;
            if (!iter.hasNext()) {
                isAdd = true;
                accountRow = new Row("DEPAccountDetails");
                accountRow.set("DEP_TOKEN_ID", (Object)tokenID);
            }
            else {
                accountRow = DO.getFirstRow("DEPAccountDetails");
            }
            accountRow.set("SERVER_NAME", (Object)((accountJSON.optString("server_name").length() > 745) ? accountJSON.optString("server_name").substring(0, 745) : accountJSON.optString("server_name")));
            accountRow.set("SERVER_UDID", (Object)accountJSON.optString("server_uuid"));
            accountRow.set("FACILITATOR_EMAIL_ID", (Object)accountJSON.optString("facilitator_id"));
            accountRow.set("ADMIN_EMAIL_ID", (Object)accountJSON.optString("admin_id"));
            accountRow.set("ORG_NAME", (Object)accountJSON.optString("org_name"));
            accountRow.set("ORG_PHONE", (Object)accountJSON.optString("org_phone"));
            accountRow.set("ORG_EMAIL", (Object)accountJSON.optString("org_email"));
            accountRow.set("ORG_ADDRESS", (Object)accountJSON.optString("org_address"));
            accountRow.set("ORG_ID", (Object)accountJSON.optString("org_id"));
            accountRow.set("ORG_ID_HASH", (Object)accountJSON.optString("org_id_hash"));
            accountRow.set("ORG_TYPE", (Object)this.getOrgTypeFromResponse(accountJSON.optString("org_type", "")));
            accountRow.set("ORG_VERSION", (Object)this.getOrgVersionFromResponse(accountJSON.optString("org_version", "")));
            if (isAdd) {
                DO.addRow(accountRow);
                MDMUtil.getPersistence().update(DO);
            }
            else {
                DO.updateRow(accountRow);
                MDMUtil.getPersistence().update(DO);
            }
        }
        catch (final Exception ex) {
            AppleDEPAccountDetailsHandler.logger.log(Level.SEVERE, "Exception in add or update account details", ex);
        }
    }
    
    public JSONObject getAccountJSON(final Long customerID, final Long tokenID) {
        JSONObject accountJSON = null;
        Long expDateLong = null;
        String expDate = null;
        try {
            accountJSON = new JSONObject();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPAccountDetails"));
            sQuery.addJoin(new Join("DEPAccountDetails", "DEPTokenDetails", new String[] { "DEP_TOKEN_ID" }, new String[] { "DEP_TOKEN_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("DEPAccountDetails", "*"));
            sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "*"));
            Criteria cCusId = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            if (tokenID != null) {
                cCusId = cCusId.and(new Criteria(new Column("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)tokenID, 0));
            }
            sQuery.setCriteria(cCusId);
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Row accountRow = DO.getFirstRow("DEPAccountDetails");
                final Row tokenRow = DO.getFirstRow("DEPTokenDetails");
                accountJSON.put("SERVER_NAME", (Object)accountRow.get("SERVER_NAME"));
                accountJSON.put("SERVER_UDID", (Object)accountRow.get("SERVER_UDID"));
                accountJSON.put("FACILITATOR_EMAIL_ID", (Object)accountRow.get("FACILITATOR_EMAIL_ID"));
                accountJSON.put("ADMIN_EMAIL_ID", (Object)accountRow.get("ADMIN_EMAIL_ID"));
                accountJSON.put("ORG_NAME", (Object)accountRow.get("ORG_NAME"));
                accountJSON.put("ORG_PHONE", (Object)accountRow.get("ORG_PHONE"));
                accountJSON.put("ORG_EMAIL", (Object)accountRow.get("ORG_EMAIL"));
                accountJSON.put("ORG_ADDRESS", (Object)accountRow.get("ORG_ADDRESS"));
                accountJSON.put("DEP_TOKEN_ID", accountRow.get("DEP_TOKEN_ID"));
                accountJSON.put("ORG_ID", accountRow.get("ORG_ID"));
                accountJSON.put("ORG_TYPE", accountRow.get("ORG_TYPE"));
                accountJSON.put("ORG_VERSION", accountRow.get("ORG_VERSION"));
                accountJSON.put("ORG_ID_HASH", accountRow.get("ORG_ID_HASH"));
                expDateLong = (Long)tokenRow.get("ACCESS_TOKEN_EXPIRY_DATE");
                expDate = SyMUtil.getDate((long)expDateLong);
                accountJSON.put("ACCESS_TOKEN_EXPIRY_DATE", (Object)expDate);
            }
        }
        catch (final Exception e) {
            AppleDEPAccountDetailsHandler.logger.log(Level.SEVERE, "Exception in getAccountJSON", e);
        }
        return accountJSON;
    }
    
    private int getOrgVersionFromResponse(final String versionString) {
        switch (versionString) {
            case "v1": {
                return AppleDEPServerConstants.DEP_ORG_VERSION_APPLE_DEPLOYMENT_PROGRAMME;
            }
            case "v2": {
                return AppleDEPServerConstants.DEP_ORG_VERSION_APPLE_SCHOOL_MANAGER;
            }
            default: {
                return -1;
            }
        }
    }
    
    public int getOrgTypeFromResponse(final String typeString) {
        switch (typeString) {
            case "org": {
                return AppleDEPServerConstants.DEP_ORG_TYPE_ENTERPRISE_ORGANISATION;
            }
            case "edu": {
                return AppleDEPServerConstants.DEP_ORG_TYPE_EDUCATIONAL_INSTITUTION;
            }
            default: {
                return -1;
            }
        }
    }
    
    static {
        AppleDEPAccountDetailsHandler.accountDetailsHandler = null;
        AppleDEPAccountDetailsHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
