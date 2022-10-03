package com.me.devicemanagement.framework.server.consents;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class ConsentActionLoggerUtil
{
    static Logger log;
    
    public static DataObject getConsentActionLoggerDetailDO(final Criteria criteria) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConsentActionLogger"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            return SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final DataAccessException dae) {
            ConsentActionLoggerUtil.log.log(Level.INFO, "Exception while retrieving data from ConsentStatusDetail" + dae);
            return null;
        }
    }
    
    public static JSONObject getConsentActionLoggerRowAsJsonObject(final Row consentStatusDetailRow) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CONSENT_ID", (Object)consentStatusDetailRow.get("CONSENT_ID"));
            jsonObject.put("STATUS", (int)consentStatusDetailRow.get("STATUS"));
            jsonObject.put("IS_REMEMBER_STATUS", (boolean)consentStatusDetailRow.get("IS_REMEMBER_STATUS"));
            jsonObject.put("USER_ID", (Object)consentStatusDetailRow.get("USER_ID"));
            jsonObject.put("LAST_MODIFIED_TIME", (Object)consentStatusDetailRow.get("LAST_MODIFIED_TIME"));
            jsonObject.put("ACTION_LOGGER_ID", (Object)consentStatusDetailRow.get("ACTION_LOGGER_ID"));
            return jsonObject;
        }
        catch (final JSONException jsonExp) {
            ConsentActionLoggerUtil.log.log(Level.INFO, "Exception while creating or adding data in jsonobject" + jsonExp);
            return null;
        }
    }
    
    public static JSONArray getConsentActionDetail(final Criteria criteria) {
        final JSONArray consentList = new JSONArray();
        try {
            final DataObject dataObject = getConsentActionLoggerDetailDO(criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows((String)null);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final JSONObject jsonObject = getConsentActionLoggerRowAsJsonObject(row);
                    consentList.put((Object)jsonObject);
                }
            }
        }
        catch (final DataAccessException dae) {
            ConsentActionLoggerUtil.log.log(Level.INFO, "Exception while trying to get consent status list" + dae);
            return null;
        }
        return consentList;
    }
    
    public static JSONArray getConsentHistoryDetails(Criteria criteria) {
        final Criteria excludeRememberLogCriteria = new Criteria(new Column("ConsentActionLogger", "IS_REMEMBER_STATUS"), (Object)true, 1);
        criteria = criteria.and(excludeRememberLogCriteria);
        return getConsentActionDetail(criteria);
    }
    
    public static JSONArray getConsentRememberActionDetails(Criteria criteria) {
        final Criteria excludeRememberLogCriteria = new Criteria(new Column("ConsentActionLogger", "IS_REMEMBER_STATUS"), (Object)true, 0);
        criteria = criteria.and(excludeRememberLogCriteria);
        return getConsentActionDetail(criteria);
    }
    
    public static JSONArray getConsentRememberActionLogByUser(final Long user_id) {
        final Criteria consent_id_criteria = new Criteria(new Column("ConsentActionLogger", "USER_ID"), (Object)user_id, 0);
        final JSONArray list = getConsentRememberActionDetails(consent_id_criteria);
        if (list.length() > 0) {
            return list;
        }
        return null;
    }
    
    public static JSONArray getConsentActionLogByUser(final Long user_id) {
        final Criteria consent_id = new Criteria(new Column("ConsentActionLogger", "USER_ID"), (Object)user_id, 0);
        final JSONArray list = getConsentHistoryDetails(consent_id);
        if (list.length() > 0) {
            return list;
        }
        return null;
    }
    
    public static JSONArray getConsentActionLogByTime(final Long timeStamp) {
        final Criteria consent_id = new Criteria(new Column("ConsentActionLogger", "LAST_MODIFIED_TIME"), (Object)timeStamp, 0);
        final JSONArray list = getConsentHistoryDetails(consent_id);
        if (list.length() > 0) {
            return list;
        }
        return null;
    }
    
    public static JSONArray getConsentActionLogByStatus(final int status) {
        final Criteria consent_id = new Criteria(new Column("ConsentActionLogger", "STATUS"), (Object)status, 0);
        final JSONArray list = getConsentHistoryDetails(consent_id);
        if (list.length() > 0) {
            return list;
        }
        return null;
    }
    
    public static int addLog(final Long user_id, final int status, final Long consent_id, final Boolean isRememberStatus) {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row row = new Row("ConsentActionLogger");
            row.set("CONSENT_ID", (Object)consent_id);
            row.set("USER_ID", (Object)user_id);
            row.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            row.set("IS_REMEMBER_STATUS", (Object)isRememberStatus);
            row.set("STATUS", (Object)status);
            dataObject.addRow(row);
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException dae) {
            ConsentActionLoggerUtil.log.log(Level.INFO, "Exception while saving logs" + dae);
            return 1001;
        }
        return 1000;
    }
    
    static {
        ConsentActionLoggerUtil.log = Logger.getLogger(ConsentsUtil.class.getName());
    }
}
