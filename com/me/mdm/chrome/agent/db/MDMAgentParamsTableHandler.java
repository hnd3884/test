package com.me.mdm.chrome.agent.db;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;

public class MDMAgentParamsTableHandler
{
    Context context;
    
    public MDMAgentParamsTableHandler(final Context context) {
        this.context = context;
    }
    
    public String getStringValue(final String key) {
        return this.getValueForKey(key);
    }
    
    public String getStringValue(final String key, final String defaultValue) {
        String value = this.getValueForKey(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    public int getIntValue(final String key) {
        final String stringValue;
        final int returnValue = ((stringValue = this.getValueForKey(key)) != null) ? Integer.parseInt(stringValue) : -1;
        return returnValue;
    }
    
    public int getIntValue(final String key, final int defaultValue) {
        final String stringValue;
        final int returnValue = ((stringValue = this.getValueForKey(key)) != null) ? Integer.parseInt(stringValue) : defaultValue;
        return returnValue;
    }
    
    public long getLongValue(final String key) {
        final String stringValue;
        final long returnValue = ((stringValue = this.getValueForKey(key)) != null) ? Long.parseLong(stringValue) : -1L;
        return returnValue;
    }
    
    public boolean getBooleanValue(final String key) {
        final String stringValue;
        final boolean returnValue = (stringValue = this.getValueForKey(key)) != null && Boolean.parseBoolean(stringValue);
        return returnValue;
    }
    
    public boolean getBooleanValue(final String key, final boolean defaultValue) {
        final String stringValue;
        final boolean returnValue = ((stringValue = this.getValueForKey(key)) != null) ? Boolean.parseBoolean(stringValue) : defaultValue;
        return returnValue;
    }
    
    public JSONObject getJSONObject(final String key) {
        JSONObject returnValue = null;
        try {
            final String stringValue;
            returnValue = (((stringValue = this.getValueForKey(key)) != null) ? new JSONObject(stringValue) : null);
        }
        catch (final JSONException ex) {}
        return returnValue;
    }
    
    public JSONArray getJSONArray(final String key) {
        JSONArray returnValue = null;
        try {
            final String stringValue;
            returnValue = (((stringValue = this.getValueForKey(key)) != null) ? new JSONArray(stringValue) : null);
        }
        catch (final JSONException ex) {}
        return returnValue;
    }
    
    public JSONArray optJSONArray(final String key) {
        JSONArray returnValue = null;
        try {
            final String stringValue;
            returnValue = (((stringValue = this.getValueForKey(key)) != null) ? new JSONArray(stringValue) : new JSONArray());
        }
        catch (final JSONException ex) {}
        return returnValue;
    }
    
    public void addStringValue(final String key, final String value) {
        this.insertKeyValue(key, value);
    }
    
    public void addIntValue(final String key, final int value) {
        final String stringValue = String.valueOf(value);
        this.insertKeyValue(key, stringValue);
    }
    
    public void addLongValue(final String key, final long value) {
        final String stringValue = String.valueOf(value);
        this.insertKeyValue(key, stringValue);
    }
    
    public void addBooleanValue(final String key, final boolean value) {
        final String stringValue = String.valueOf(value);
        this.insertKeyValue(key, stringValue);
    }
    
    public void addJSONObject(final String key, final JSONObject jsonObject) {
        final String stringValue = jsonObject.toString();
        this.insertKeyValue(key, stringValue);
    }
    
    public void addJSONArray(final String key, final JSONArray jsonArray) {
        final String stringValue = jsonArray.toString();
        this.insertKeyValue(key, stringValue);
    }
    
    public void removeValue(final String key) {
        this.deleteKey(key);
    }
    
    public void removeAllValues() {
        this.deleteKey(null);
    }
    
    private String getValueForKey(final String key) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeAgentParams"));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria udidCriteria = new Criteria(new Column("ChromeAgentParams", "UDID"), (Object)this.context.getUdid(), 0);
            final Criteria customerIdCriteria = new Criteria(new Column("ChromeAgentParams", "CUSTOMER_ID"), (Object)this.context.getCustomerId(), 0);
            final Criteria keyCriteria = new Criteria(new Column("ChromeAgentParams", "PARAM_NAME"), (Object)key, 0);
            sQuery.setCriteria(udidCriteria.and(keyCriteria).and(customerIdCriteria));
            final DataObject dO = DataAccess.get(sQuery);
            if (!dO.isEmpty()) {
                return (String)dO.getFirstValue("ChromeAgentParams", "PARAM_VALUE");
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMAgentParamsTableHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return null;
    }
    
    private void insertKeyValue(final String key, final String value) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ChromeAgentParams"));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria udidCriteria = new Criteria(new Column("ChromeAgentParams", "UDID"), (Object)this.context.getUdid(), 0);
            final Criteria customerIdCriteria = new Criteria(new Column("ChromeAgentParams", "CUSTOMER_ID"), (Object)this.context.getCustomerId(), 0);
            final Criteria keyCriteria = new Criteria(new Column("ChromeAgentParams", "PARAM_NAME"), (Object)key, 0);
            sQuery.setCriteria(udidCriteria.and(keyCriteria).and(customerIdCriteria));
            final DataObject dO = DataAccess.get(sQuery);
            if (dO.isEmpty()) {
                final Row row = new Row("ChromeAgentParams");
                row.set("CUSTOMER_ID", (Object)this.context.getCustomerId());
                row.set("UDID", (Object)this.context.getUdid());
                row.set("PARAM_NAME", (Object)key);
                row.set("PARAM_VALUE", (Object)value);
                dO.addRow(row);
            }
            else {
                final Row row = dO.getRow("ChromeAgentParams");
                row.set("PARAM_VALUE", (Object)value);
                dO.updateRow(row);
            }
            DataAccess.update(dO);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMAgentParamsTableHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    private void deleteKey(final String key) {
        try {
            final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("ChromeAgentParams");
            final Criteria udidCriteria = new Criteria(new Column("ChromeAgentParams", "UDID"), (Object)this.context.getUdid(), 0);
            final Criteria customerIdCriteria = new Criteria(new Column("ChromeAgentParams", "CUSTOMER_ID"), (Object)this.context.getCustomerId(), 0);
            final Criteria criteria = udidCriteria.and(customerIdCriteria);
            if (key != null) {
                final Criteria keyCriteria = new Criteria(new Column("ChromeAgentParams", "PARAM_NAME"), (Object)key, 0);
                dQuery.setCriteria(criteria.and(keyCriteria));
            }
            dQuery.setCriteria(criteria);
            DataAccess.delete(dQuery);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMAgentParamsTableHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
}
