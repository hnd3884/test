package com.me.mdm.server.settings;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MdComplianceRulesHandler
{
    public Logger logger;
    private static MdComplianceRulesHandler complianceRuleHandler;
    
    public MdComplianceRulesHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static MdComplianceRulesHandler getInstance() {
        if (MdComplianceRulesHandler.complianceRuleHandler == null) {
            MdComplianceRulesHandler.complianceRuleHandler = new MdComplianceRulesHandler();
        }
        return MdComplianceRulesHandler.complianceRuleHandler;
    }
    
    public void addOrUpdateComplianceRules(final JSONObject settingsData) {
        this.logger.log(Level.INFO, "UpdateAndroidCompliance rules begins");
        try {
            final Long customerId = settingsData.optLong("CUSTOMER_ID");
            final Long userID = settingsData.optLong("UPDATED_BY", -1L);
            final Criteria customerIdCrit = new Criteria(Column.getColumn("MDComplianceRules", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject complianceRulesDO = MDMUtil.getPersistence().get("MDComplianceRules", customerIdCrit);
            Row rulesRow = null;
            if (complianceRulesDO.isEmpty()) {
                rulesRow = new Row("MDComplianceRules");
                rulesRow.set("CUSTOMER_ID", (Object)customerId);
                rulesRow.set("CORPORATE_WIPE_ROOTED_DEVICES", (Object)settingsData.optBoolean("CORPORATE_WIPE_ROOTED_DEVICES", false));
                rulesRow.set("WIPE_INTEGRITY_FAILED_DEVICES", (Object)settingsData.optBoolean("WIPE_INTEGRITY_FAILED_DEVICES", false));
                rulesRow.set("WIPE_CTS_FAILED_DEVICES", (Object)settingsData.optBoolean("WIPE_CTS_FAILED_DEVICES", false));
                if (userID != -1L) {
                    rulesRow.set("UPDATED_BY", (Object)userID);
                    rulesRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                }
                complianceRulesDO.addRow(rulesRow);
            }
            else {
                rulesRow = complianceRulesDO.getRow("MDComplianceRules");
                rulesRow.set("CORPORATE_WIPE_ROOTED_DEVICES", (Object)settingsData.optBoolean("CORPORATE_WIPE_ROOTED_DEVICES", false));
                if (settingsData.has("WIPE_CTS_FAILED_DEVICES") && settingsData.has("WIPE_INTEGRITY_FAILED_DEVICES")) {
                    rulesRow.set("WIPE_INTEGRITY_FAILED_DEVICES", (Object)settingsData.optBoolean("WIPE_INTEGRITY_FAILED_DEVICES", false));
                    rulesRow.set("WIPE_CTS_FAILED_DEVICES", (Object)settingsData.optBoolean("WIPE_CTS_FAILED_DEVICES", false));
                }
                if (userID != -1L) {
                    rulesRow.set("UPDATED_BY", (Object)userID);
                    rulesRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                }
                complianceRulesDO.updateRow(rulesRow);
            }
            MDMUtil.getPersistence().update(complianceRulesDO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in handleAndroidComplianceRules", ex);
        }
    }
    
    public JSONObject getAndroidComplianceRules(final Long customerId) {
        final JSONObject complianceRules = new JSONObject();
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("MDComplianceRules", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject complianceRulesDO = MDMUtil.getPersistence().get("MDComplianceRules", cusCri);
            if (!complianceRulesDO.isEmpty() && complianceRulesDO != null) {
                final Row row = complianceRulesDO.getFirstRow("MDComplianceRules");
                complianceRules.put("CORPORATE_WIPE_ROOTED_DEVICES", row.get("CORPORATE_WIPE_ROOTED_DEVICES"));
                complianceRules.put("UPDATED_BY", row.get("UPDATED_BY"));
                complianceRules.put("WIPE_INTEGRITY_FAILED_DEVICES", row.get("WIPE_INTEGRITY_FAILED_DEVICES"));
                complianceRules.put("WIPE_CTS_FAILED_DEVICES", row.get("WIPE_CTS_FAILED_DEVICES"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getComplianceRules", ex);
        }
        return complianceRules;
    }
    
    public void addDefaultComplianceRules(final Long customerId) throws JSONException {
        final JSONObject complianceRules = new JSONObject();
        complianceRules.put("CORPORATE_WIPE_ROOTED_DEVICES", false);
        complianceRules.put("WIPE_INTEGRITY_FAILED_DEVICES", false);
        complianceRules.put("WIPE_CTS_FAILED_DEVICES", false);
        complianceRules.put("CUSTOMER_ID", (Object)customerId);
        this.addOrUpdateComplianceRules(complianceRules);
    }
    
    public Object getComplianceRuleConfigUserId(final Long customerId) {
        Object updatedBy = null;
        try {
            final SelectQuery complainceQuery = (SelectQuery)new SelectQueryImpl(new Table("MDComplianceRules"));
            final Criteria customerIdCrit = new Criteria(Column.getColumn("MDComplianceRules", "CUSTOMER_ID"), (Object)customerId, 0);
            complainceQuery.setCriteria(customerIdCrit);
            complainceQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject complianceRulesDO = MDMUtil.getPersistence().get(complainceQuery);
            Row rulesRow = null;
            rulesRow = complianceRulesDO.getRow("MDComplianceRules");
            updatedBy = rulesRow.get("UPDATED_BY");
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MdComplianceRulesHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return updatedBy;
    }
    
    static {
        MdComplianceRulesHandler.complianceRuleHandler = null;
    }
}
