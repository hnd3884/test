package com.me.mdm.core.enrollment.settings;

import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;

public class GroupAssignmentHandler
{
    public JSONArray createGroupRules(final JSONArray groupRules, final Long userAssignmentRuleID, final DataObject dataObject, final Long customerID) throws Exception {
        if (groupRules == null || groupRules.length() == 0) {
            return null;
        }
        for (int i = 0; i < groupRules.length(); ++i) {
            final JSONObject groupRule = groupRules.getJSONObject(i);
            final Long groupResourceID = groupRule.optLong("GROUP_RESOURCE_ID".toLowerCase());
            if (!this.validateGroupCustomerRelation(groupResourceID, customerID)) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
            final Row row = new Row("GroupAssignmentRule");
            row.set("ON_BOARD_RULE_ID", (Object)userAssignmentRuleID);
            final Row groupRow = new Row("GroupRuleMapping");
            groupRow.set("GROUP_RULE_ID", row.get("GROUP_RULE_ID"));
            groupRow.set("GROUP_RESOURCE_ID", (Object)groupResourceID);
            dataObject.addRow(row);
            dataObject.addRow(groupRow);
        }
        final Iterator iterator = dataObject.getRows("GroupAssignmentRule");
        final JSONArray groupRule2 = new JSONArray();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            final Row resRow = dataObject.getRow("GroupRuleMapping", new Criteria(Column.getColumn("GroupRuleMapping", "GROUP_RULE_ID"), row2.get("GROUP_RULE_ID"), 0));
            groupRule2.put((Object)getGroupRuleJSON(row2, resRow));
        }
        return groupRule2;
    }
    
    private boolean validateGroupCustomerRelation(final Long groupResourceID, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0).and(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupResourceID, 0));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        return !MDMUtil.getPersistenceLite().get(selectQuery).isEmpty();
    }
    
    protected static JSONObject getGroupRuleJSON(final Row row, final Row resRow) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("GROUP_RULE_ID".toLowerCase(), row.get("GROUP_RULE_ID"));
        if (resRow != null) {
            jsonObject.put("GROUP_RESOURCE_ID".toLowerCase(), resRow.get("GROUP_RESOURCE_ID"));
        }
        return jsonObject;
    }
    
    public void getGroupJoin(final SelectQuery selectQuery) {
        if (selectQuery.getTableList().contains(new Table("OnBoardingRule"))) {
            selectQuery.addJoin(new Join("OnBoardingRule", "GroupAssignmentRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
            selectQuery.addJoin(new Join("GroupAssignmentRule", "GroupRuleMapping", new String[] { "GROUP_RULE_ID" }, new String[] { "GROUP_RULE_ID" }, 1));
            selectQuery.addJoin(new Join("GroupRuleMapping", "Resource", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        }
    }
}
