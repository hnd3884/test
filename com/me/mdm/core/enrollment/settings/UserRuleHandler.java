package com.me.mdm.core.enrollment.settings;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentConstants;
import java.util.List;

public class UserRuleHandler
{
    List<Integer> delegatedRuleTypeList;
    
    public UserRuleHandler() {
        this.delegatedRuleTypeList = Arrays.asList(MDMEnrollmentConstants.UserAssignmentRules.UserRules.FIRST_LOGGED_IN_USER_TYPE, MDMEnrollmentConstants.UserAssignmentRules.UserRules.WORKGROUP_USER);
    }
    
    public JSONArray createUserRules(final JSONArray userRules, final Long userAssignmentRuleID, final DataObject dataObject, final Long customerID) throws Exception {
        if (userRules == null || userRules.length() == 0 || !this.validateUserRule(userRules)) {
            return null;
        }
        for (int i = 0; i < userRules.length(); ++i) {
            final JSONObject userRule = userRules.getJSONObject(i);
            final Integer ruleType = (Integer)userRule.get("RULE_TYPE".toLowerCase());
            Long managedUserID = userRule.optLong("MANAGED_USER_ID".toLowerCase(), -1L);
            if (managedUserID == -1L) {
                managedUserID = null;
            }
            else if (!this.validateUserCustomerRelation(managedUserID, customerID)) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            final Integer include = userRule.optInt("CRITERIA".toLowerCase(), (int)MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA);
            final Row row = new Row("UserRule");
            row.set("ON_BOARD_RULE_ID", (Object)userAssignmentRuleID);
            row.set("RULE_TYPE", (Object)ruleType);
            row.set("CRITERIA", (Object)include);
            final Row userRow = new Row("UserRuleMapping");
            userRow.set("USER_RULE_ID", row.get("USER_RULE_ID"));
            userRow.set("MANAGED_USER_ID", (Object)managedUserID);
            dataObject.addRow(row);
            dataObject.addRow(userRow);
        }
        final Iterator iterator = dataObject.getRows("UserRule");
        final JSONArray userRuleArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            final Row userRow2 = dataObject.getRow("UserRuleMapping", new Criteria(Column.getColumn("UserRuleMapping", "USER_RULE_ID"), row2.get("USER_RULE_ID"), 0));
            userRuleArray.put((Object)getUserJSON(row2, userRow2));
        }
        return userRuleArray;
    }
    
    public void getUserRuleJoin(final SelectQuery selectQuery) {
        if (selectQuery.getTableList().contains(new Table("OnBoardingRule"))) {
            selectQuery.addJoin(new Join("OnBoardingRule", "UserRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
            selectQuery.addJoin(new Join("UserRule", "UserRuleMapping", new String[] { "USER_RULE_ID" }, new String[] { "USER_RULE_ID" }, 1));
            selectQuery.addJoin(new Join("UserRuleMapping", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        }
    }
    
    protected static JSONObject getUserJSON(final Row row, final Row userRow) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("USER_RULE_ID".toLowerCase(), row.get("USER_RULE_ID"));
        jsonObject.put("RULE_TYPE".toLowerCase(), row.get("RULE_TYPE"));
        jsonObject.put("CRITERIA".toLowerCase(), row.get("CRITERIA"));
        if (userRow != null) {
            jsonObject.put("MANAGED_USER_ID".toLowerCase(), userRow.get("MANAGED_USER_ID"));
        }
        return jsonObject;
    }
    
    public Boolean isDelegatedRuleSet(final Iterator rows) {
        Boolean isDelegatedRule = Boolean.FALSE;
        while (rows.hasNext()) {
            final Row row = rows.next();
            final int ruleType = (int)row.get("RULE_TYPE");
            if (this.delegatedRuleTypeList.contains(ruleType)) {
                isDelegatedRule = Boolean.TRUE;
                break;
            }
        }
        return isDelegatedRule;
    }
    
    public boolean validateUserRule(final JSONArray rules) {
        Boolean valid = Boolean.TRUE;
        Boolean hasInclude = Boolean.FALSE;
        final int prevRuleType = rules.getJSONObject(0).getInt("RULE_TYPE".toLowerCase());
        for (int i = 0; i < rules.length(); ++i) {
            final JSONObject rule = rules.getJSONObject(i);
            final int ruleType = rule.getInt("RULE_TYPE".toLowerCase());
            final int include = rule.optInt("CRITERIA".toLowerCase(), (int)MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA);
            final Long managedUserID = rule.optLong("MANAGED_USER_ID".toLowerCase(), -1L);
            if (prevRuleType != ruleType && (!this.delegatedRuleTypeList.contains(prevRuleType) || !this.delegatedRuleTypeList.contains(ruleType))) {
                valid = Boolean.FALSE;
                break;
            }
            if (include == MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA) {
                hasInclude = Boolean.TRUE;
            }
            if (ruleType == MDMEnrollmentConstants.UserAssignmentRules.UserRules.SAME_USER_TYPE && (managedUserID == -1L || rules.length() > 1)) {
                valid = Boolean.FALSE;
                break;
            }
            if (ruleType == MDMEnrollmentConstants.UserAssignmentRules.UserRules.AUTHENTICATED_USER_TYPE && rules.length() > 1) {
                valid = Boolean.FALSE;
                break;
            }
        }
        if (!hasInclude) {
            valid = Boolean.FALSE;
        }
        return valid;
    }
    
    private Boolean validateUserCustomerRelation(final Long userID, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedUser"));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)userID, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        if (MDMUtil.getPersistenceLite().get(selectQuery).isEmpty()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
