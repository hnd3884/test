package com.me.mdm.core.enrollment.settings;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;

public class AgentParamRuleHandler
{
    public JSONArray createAgentParamRules(final JSONArray agentParams, final Long userAssignmentRuleID, final DataObject dataObject) throws Exception {
        if (agentParams == null || agentParams.length() == 0) {
            return null;
        }
        for (int i = 0; i < agentParams.length(); ++i) {
            final JSONObject agentparams = agentParams.getJSONObject(i);
            final String agentParamName = agentparams.get("PARAM_VALUE".toLowerCase()).toString();
            final Integer paramType = (Integer)agentparams.get("PARAM_TYPE".toLowerCase());
            final Row row = new Row("AgentOnBoardParams");
            row.set("ON_BOARD_RULE_ID", (Object)userAssignmentRuleID);
            row.set("PARAM_VALUE", (Object)agentParamName);
            row.set("PARAM_TYPE", (Object)paramType);
            dataObject.addRow(row);
        }
        final Iterator iterator = dataObject.getRows("AgentOnBoardParams");
        final JSONArray groupRule = new JSONArray();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            groupRule.put((Object)getAgentParamsRuleJSON(row2));
        }
        return groupRule;
    }
    
    public static JSONObject getAgentParamsRuleJSON(final Row row) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("PARAM_VALUE".toLowerCase(), row.get("PARAM_VALUE"));
        jsonObject.put("PARAM_TYPE".toLowerCase(), row.get("PARAM_TYPE"));
        return jsonObject;
    }
    
    public void getAgentParams(final SelectQuery selectQuery) {
        if (selectQuery.getTableList().contains(new Table("OnBoardingRule"))) {
            selectQuery.addJoin(new Join("OnBoardingRule", "AgentOnBoardParams", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
        }
    }
}
