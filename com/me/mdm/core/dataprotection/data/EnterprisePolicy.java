package com.me.mdm.core.dataprotection.data;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.dataprotection.windows.WindowsConfigRule;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.List;

public class EnterprisePolicy
{
    public Long policyID;
    public String policyName;
    public String policyDescription;
    public Long createdBy;
    public Long lastModifiedBy;
    public Long createdTime;
    public Long modifiedTime;
    public Long customerID;
    public List<EnterpriseRule> rules;
    public int error;
    public static final int NO_POLICY_DEFINED = 1;
    
    private Row getRow() {
        final Row row = new Row("CorporatePolicy");
        row.set("POLICY_NAME", (Object)this.policyName);
        row.set("CREATED_BY", (Object)this.createdBy);
        row.set("POLICY_DESCRIPTION", (Object)this.policyDescription);
        row.set("LAST_MODIFIED_BY", (Object)this.lastModifiedBy);
        row.set("CREATION_TIME", (Object)this.createdTime);
        row.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
        return row;
    }
    
    private DataObject createPolicyDO() throws DataAccessException {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        final Row row = this.getRow();
        final Row customerRow = new Row("CorporatePolicyToCustomerRel");
        customerRow.set("POLICY_ID", row.get("POLICY_ID"));
        customerRow.set("CUSTOMER_ID", (Object)this.customerID);
        dataObject.addRow(row);
        dataObject.addRow(customerRow);
        for (final EnterpriseRule enterpriseRule : this.rules) {
            enterpriseRule.createRowAndUpdateDo(dataObject);
        }
        return dataObject;
    }
    
    public void createAndPersistPolicy() throws DataAccessException {
        final DataObject dataObject = this.createPolicyDO();
        MDMUtil.getPersistence().update(dataObject);
        final Row row = dataObject.getRow("CorporatePolicy");
        this.policyID = (Long)row.get("POLICY_ID");
    }
    
    public EnterprisePolicy() {
        this.error = 0;
    }
    
    public EnterprisePolicy(final Long policyID, final Long customerID) throws DataAccessException {
        this.error = 0;
        final List list = new ArrayList();
        list.add(policyID);
        final DataObject dataObject = this.getPolicyFromDB(list, customerID);
        if (dataObject.isEmpty()) {
            this.error = 1;
        }
        final Row corporatePolicyRow = dataObject.getRow("CorporatePolicy", new Criteria(Column.getColumn("CorporatePolicy", "POLICY_ID"), (Object)policyID, 0));
        this.policyID = policyID;
        this.createdTime = (Long)corporatePolicyRow.get("CREATION_TIME");
        this.modifiedTime = (Long)corporatePolicyRow.get("LAST_MODIFIED_TIME");
        this.lastModifiedBy = (Long)corporatePolicyRow.get("LAST_MODIFIED_BY");
        this.policyName = (String)corporatePolicyRow.get("POLICY_NAME");
        this.policyDescription = (String)corporatePolicyRow.get("POLICY_DESCRIPTION");
        this.createdBy = (Long)corporatePolicyRow.get("CREATED_BY");
        this.rules = new ArrayList<EnterpriseRule>();
        final Iterator iterator = dataObject.getRows("EnterpriseRules");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Integer ruleType = (Integer)row.get("RULE_TYPE");
            String tableName = "";
            EnterpriseRule enterpriseRule = null;
            switch (ruleType) {
                case 2: {
                    tableName = "EnterpriseApplications";
                    final Row ruleRow = dataObject.getRow(tableName, new Criteria(Column.getColumn(tableName, "RULE_ID"), row.get("RULE_ID"), 0));
                    enterpriseRule = new AppRule(row, ruleRow);
                    break;
                }
                case 3: {
                    tableName = "WindowsEnterpriseConfig";
                    final Row ruleRow = dataObject.getRow(tableName, new Criteria(Column.getColumn(tableName, "RULE_ID"), row.get("RULE_ID"), 0));
                    enterpriseRule = new WindowsConfigRule(row, ruleRow);
                    break;
                }
                case 1: {
                    tableName = "EnterpriseNetworkLimit";
                    final Row ruleRow = dataObject.getRow(tableName, new Criteria(Column.getColumn(tableName, "RULE_ID"), row.get("RULE_ID"), 0));
                    enterpriseRule = new NetworkRule(row, ruleRow);
                    break;
                }
            }
            this.rules.add(enterpriseRule);
        }
    }
    
    public DataObject getPolicyFromDB(final List<Long> policyIDList, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CorporatePolicy"));
        selectQuery.addJoin(new Join("CorporatePolicy", "RuleToPolicy", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
        selectQuery.addJoin(new Join("CorporatePolicy", "CorporatePolicyToCustomerRel", new String[] { "POLICY_ID" }, new String[] { "POLICY_ID" }, 2));
        selectQuery.addJoin(new Join("RuleToPolicy", "EnterpriseRules", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 2));
        selectQuery.addJoin(new Join("EnterpriseRules", "EnterpriseConfiguration", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
        selectQuery.addJoin(new Join("EnterpriseRules", "EnterpriseNetworkLimit", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
        selectQuery.addJoin(new Join("EnterpriseRules", "EnterpriseApplications", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
        selectQuery.addJoin(new Join("EnterpriseConfiguration", "WindowsEnterpriseConfig", new String[] { "RULE_ID" }, new String[] { "RULE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("CorporatePolicy", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnterpriseRules", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnterpriseNetworkLimit", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnterpriseApplications", "*"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsEnterpriseConfig", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnterpriseConfiguration", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("CorporatePolicy", "POLICY_ID"), (Object)policyIDList.toArray(), 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("CorporatePolicyToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(customerCriteria.and(criteria));
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
}
