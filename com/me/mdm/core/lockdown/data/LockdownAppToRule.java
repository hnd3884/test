package com.me.mdm.core.lockdown.data;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.core.lockdown.LockdownHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.List;

public class LockdownAppToRule extends LockdownRule
{
    public List<LockdownApplication> lockdownApplications;
    
    public LockdownAppToRule(final int platform) {
        super(1, platform);
        this.lockdownApplications = new ArrayList<LockdownApplication>();
    }
    
    public LockdownAppToRule(final Row row, final Iterator iterator) {
        super(row);
        this.lockdownApplications = new ArrayList<LockdownApplication>();
        while (iterator.hasNext()) {
            final Row appRow = iterator.next();
            this.lockdownApplications.add(new LockdownApplication(appRow));
        }
    }
    
    public void addApplicationToList(final LockdownApplication lockdownApplication) {
        this.lockdownApplications.add(lockdownApplication);
    }
    
    private DataObject getExisitingAppsDO() throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("LockdownApplications"));
        selectQuery.addSelectColumn(Column.getColumn("LockdownApplications", "*"));
        final List<String> idList = new ArrayList<String>();
        final Iterator iterator = this.lockdownApplications.iterator();
        while (iterator.hasNext()) {
            idList.add(iterator.next().identifier);
        }
        selectQuery.setCriteria(new Criteria(Column.getColumn("LockdownApplications", "APP_IDENTIFIER"), (Object)idList.toArray(), 8));
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    @Override
    public Row createRowAndUpdateDo(final DataObject dataObject) throws DataAccessException {
        final Row ruleRow = super.createRowAndUpdateDo(dataObject);
        final Iterator iterator = this.lockdownApplications.iterator();
        final DataObject appsDO = this.getExisitingAppsDO();
        while (iterator.hasNext()) {
            final LockdownApplication app = iterator.next();
            Row appRow = appsDO.getRow("LockdownApplications", new Criteria(Column.getColumn("LockdownApplications", "APP_IDENTIFIER"), (Object)app.identifier, 0));
            if (appRow == null) {
                appRow = new Row("LockdownApplications");
                appRow.set("APP_IDENTIFIER", (Object)app.identifier);
                appRow.set("APP_TYPE", (Object)LockdownHandler.getAppType(app.identifier));
                dataObject.addRow(appRow);
            }
            final Row appRuleRow = new Row("LockdownRuleToApp");
            appRuleRow.set("APP_ID", appRow.get("APP_ID"));
            appRuleRow.set("RULE_ID", ruleRow.get("RULE_ID"));
            dataObject.addRow(appRuleRow);
        }
        return ruleRow;
    }
    
    @Override
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        for (final LockdownApplication lockdownApplication : this.lockdownApplications) {
            jsonArray.put((Object)lockdownApplication.toJSON());
        }
        jsonObject.put("allowed_apps", (Object)jsonArray);
        return jsonObject;
    }
}
