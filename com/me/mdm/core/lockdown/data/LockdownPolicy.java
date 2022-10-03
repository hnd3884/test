package com.me.mdm.core.lockdown.data;

import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import java.util.List;

public class LockdownPolicy
{
    public Long policyID;
    public String policyName;
    public String policyDescription;
    public Long createdBy;
    public Long lastModifiedBy;
    public Long createdTime;
    public Long modifiedTime;
    public Long customerID;
    public List<LockdownRule> rules;
    public int error;
    public static final int NO_POLICY_DEFINED = 1;
    public static final int NO_ERROR = 0;
    
    public LockdownPolicy() {
        this.error = 0;
        this.rules = new ArrayList<LockdownRule>();
    }
    
    public LockdownPolicy(final Long policyID, final Long customerID) throws DataAccessException {
        this.error = 0;
        final List<Long> list = new ArrayList<Long>();
        list.add(policyID);
        this.policyID = policyID;
        final DataObject dataObject = this.getPolicyFromDB(list, customerID);
        this.populateLockdownDataFromDO(dataObject);
    }
    
    public void populateLockdownDataFromDO(final DataObject dataObject) throws DataAccessException {
        if (dataObject.isEmpty()) {
            this.error = 1;
        }
        final Row corporatePolicyRow = dataObject.getRow("LockdownPolicy", new Criteria(Column.getColumn("LockdownPolicy", "POLICY_ID"), (Object)this.policyID, 0));
        this.policyID = this.policyID;
        this.createdTime = (Long)corporatePolicyRow.get("CREATION_TIME");
        this.modifiedTime = (Long)corporatePolicyRow.get("LAST_MODIFIED_TIME");
        this.lastModifiedBy = (Long)corporatePolicyRow.get("LAST_MODIFIED_BY");
        this.policyName = (String)corporatePolicyRow.get("POLICY_NAME");
        this.policyDescription = (String)corporatePolicyRow.get("POLICY_DESCRIPTION");
        this.createdBy = (Long)corporatePolicyRow.get("CREATED_BY");
        this.rules = new ArrayList<LockdownRule>();
        final Iterator iterator = dataObject.getRows("LockdownRules");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Integer ruleType = (Integer)row.get("RULE_TYPE");
            String tableName = "";
            LockdownRule enterpriseRule = null;
            switch (ruleType) {
                case 1: {
                    tableName = "LockdownApplications";
                    final Iterator apps = dataObject.getRows(tableName);
                    enterpriseRule = new LockdownAppToRule(row, apps);
                    break;
                }
                case 2: {
                    tableName = "WindowsLockdownConfig";
                    final Row ruleRow = dataObject.getRow(tableName, new Criteria(Column.getColumn(tableName, "RULE_ID"), row.get("RULE_ID"), 0));
                    enterpriseRule = new WindowsLockdownConfig(row, ruleRow);
                    break;
                }
            }
            this.rules.add(enterpriseRule);
        }
    }
    
    private DataObject getPolicyFromDB(final List<Long> list, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("LockdownPolicy"));
        selectQuery.addJoin(new Join("LockdownPolicy", "LockdownRules", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
        selectQuery.addJoin(new Join("LockdownRules", "LockdownRuleToApp", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
        selectQuery.addJoin(new Join("LockdownRules", "WindowsLockdownConfig", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
        selectQuery.addJoin(new Join("LockdownRuleToApp", "LockdownApplications", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        selectQuery.addJoin(new Join("LockdownPolicy", "LockdownPolicyToCustomerRel", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("LockdownPolicy", "*"));
        selectQuery.addSelectColumn(Column.getColumn("LockdownRules", "*"));
        selectQuery.addSelectColumn(Column.getColumn("LockdownRuleToApp", "*"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsLockdownConfig", "*"));
        selectQuery.addSelectColumn(Column.getColumn("LockdownApplications", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("LockdownPolicy", "POLICY_ID"), (Object)list.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("LockdownPolicyToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(customerCriteria.and(criteria));
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    private Row getRow() {
        final Row row = new Row("LockdownPolicy");
        row.set("POLICY_NAME", (Object)this.policyName);
        row.set("POLICY_DESCRIPTION", (Object)this.policyDescription);
        row.set("CREATED_BY", (Object)this.createdBy);
        row.set("LAST_MODIFIED_BY", (Object)this.lastModifiedBy);
        row.set("CREATION_TIME", (Object)this.createdTime);
        row.set("LAST_MODIFIED_TIME", (Object)this.modifiedTime);
        return row;
    }
    
    private DataObject createPolicyDO() throws DataAccessException {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Row row = this.getRow();
        dataObject.addRow(row);
        final Row customerRow = new Row("LockdownPolicyToCustomerRel");
        customerRow.set("POLICY_ID", row.get("POLICY_ID"));
        customerRow.set("CUSTOMER_ID", (Object)this.customerID);
        dataObject.addRow(customerRow);
        for (final LockdownRule lockdownRule : this.rules) {
            lockdownRule.createRowAndUpdateDo(dataObject);
        }
        return dataObject;
    }
    
    public void createAndPersistPolicy() throws DataAccessException {
        final DataObject dataObject = this.createPolicyDO();
        MDMUtil.getPersistence().update(dataObject);
        final Row row = dataObject.getRow("LockdownPolicy");
        this.policyID = (Long)row.get("POLICY_ID");
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("policy_id", (Object)this.policyID);
        jsonObject.put("created_at", (Object)this.createdTime);
        for (final LockdownRule lockdownRule : this.rules) {
            jsonObject.put((lockdownRule.ruleType == 1) ? "appconfig" : "configuration", (Object)lockdownRule.toJSON());
        }
        return jsonObject;
    }
}
