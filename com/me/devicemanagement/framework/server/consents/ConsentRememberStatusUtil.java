package com.me.devicemanagement.framework.server.consents;

import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import javax.transaction.NotSupportedException;
import com.me.devicemanagement.framework.server.exception.SyMException;
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

public class ConsentRememberStatusUtil
{
    static Logger log;
    
    public static DataObject getRememberStatusDetailDO(final Criteria criteria) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConsentRememberStatus"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            return dataObject;
        }
        catch (final DataAccessException dae) {
            ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while retrieving data from ConsentRememberStatusDetail" + dae);
            return null;
        }
    }
    
    public static JSONObject getRowAsJsonObject(final Row consentRememberStatusDetailRow) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CONSENT_ID", (Object)consentRememberStatusDetailRow.get("CONSENT_ID"));
            jsonObject.put("REMEMBER_STATUS_ID", (Object)consentRememberStatusDetailRow.get("REMEMBER_STATUS_ID"));
            jsonObject.put("USER_ID", (Object)consentRememberStatusDetailRow.get("USER_ID"));
            jsonObject.put("LAST_MODIFIED_TIME", (Object)consentRememberStatusDetailRow.get("LAST_MODIFIED_TIME"));
            return jsonObject;
        }
        catch (final JSONException jsonExp) {
            ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while creating or adding data in jsonobject" + jsonExp);
            return null;
        }
    }
    
    public static JSONArray getConsentRememberDetail(final Criteria criteria) {
        final JSONArray consentList = new JSONArray();
        try {
            final DataObject dataObject = getRememberStatusDetailDO(criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows((String)null);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final JSONObject jsonObject = getRowAsJsonObject(row);
                    if (jsonObject != null) {
                        consentList.put((Object)jsonObject);
                    }
                }
            }
        }
        catch (final DataAccessException dae) {
            ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while trying to get consent status list" + dae);
            return null;
        }
        return consentList;
    }
    
    public static JSONObject getRememberMyChoiceDetail(final Long consent_id, final Long user_id) {
        final Criteria consent_id_cri = new Criteria(new Column("ConsentRememberStatus", "CONSENT_ID"), (Object)consent_id, 0);
        final Criteria user_id_cri = new Criteria(new Column("ConsentRememberStatus", "USER_ID"), (Object)user_id, 0);
        final Criteria criteria = consent_id_cri.and(user_id_cri);
        final DataObject dataObject = getRememberStatusDetailDO(criteria);
        if (!dataObject.isEmpty()) {
            try {
                final Row row = dataObject.getFirstRow("ConsentRememberStatus");
                final JSONObject jsonObject = getRowAsJsonObject(row);
                return jsonObject;
            }
            catch (final DataAccessException dae) {
                ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while retrieving DO " + dae);
                return null;
            }
        }
        return null;
    }
    
    public static JSONArray getRememberMyChoiceDetails(final Long[] consent_ids, final Long user_id) {
        final Criteria consent_id_cri = new Criteria(new Column("ConsentRememberStatus", "CONSENT_ID"), (Object)consent_ids, 8);
        final Criteria user_id_cri = new Criteria(new Column("ConsentRememberStatus", "USER_ID"), (Object)user_id, 0);
        final Criteria criteria = consent_id_cri.and(user_id_cri);
        final JSONArray list = getConsentRememberDetail(criteria);
        if (list.length() > 0) {
            return list;
        }
        return null;
    }
    
    public static boolean isRememberMyChoiceApplicable(final Long consent_id, final Long user_id) {
        return getRememberMyChoiceDetail(consent_id, user_id) != null;
    }
    
    public static boolean isRememberMyChoiceApplicable(final Long[] consent_ids, final Long user_id) {
        return getRememberMyChoiceDetails(consent_ids, user_id) != null;
    }
    
    public static int saveConsentRememberMyChoice(final Long user_id, final Long consent_id, final Long customer_id, final boolean status, final JSONObject consentRememberEventDetails) throws DataAccessException, SyMException, NotSupportedException, JSONException {
        final Long time = System.currentTimeMillis();
        if (ConsentsUtil.isUserSpecificConsent(consent_id)) {
            final boolean isExist = isRememberMyChoiceApplicable(consent_id, user_id);
            if (status) {
                if (!isExist) {
                    try {
                        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                        final Row row = new Row("ConsentRememberStatus");
                        row.set("CONSENT_ID", (Object)consent_id);
                        row.set("USER_ID", (Object)user_id);
                        row.set("LAST_MODIFIED_TIME", (Object)time);
                        dataObject.addRow(row);
                        SyMUtil.getPersistence().update(dataObject);
                    }
                    catch (final DataAccessException dae) {
                        ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while saving consents" + dae);
                        throw dae;
                    }
                    logConsentRememberDetails(user_id, consent_id, consentRememberEventDetails, customer_id, 1);
                }
            }
            else if (isExist) {
                deleteRememberStatusDetail(consent_id, user_id);
                logConsentRememberDetails(user_id, consent_id, consentRememberEventDetails, customer_id, 2);
            }
        }
        return 1000;
    }
    
    public static void logConsentRememberDetails(final Long user_id, final Long consent_id, final JSONObject consentRememberEventDetails, final Long customer_id, final int status) throws DataAccessException, SyMException, JSONException {
        if (ConsentStatusUtil.isConsentActionLoggerEnabled && ConsentActionLoggerUtil.addLog(user_id, status, consent_id, true) == 1001) {
            throw new DataAccessException();
        }
        try {
            if (ConsentStatusUtil.isConsentEventLoggerEnabled && consentRememberEventDetails != null) {
                try {
                    final int event_id = (int)consentRememberEventDetails.get("event_id");
                    final String userName = DMUserHandler.getUserNameFromUserID(user_id);
                    final String remarks = consentRememberEventDetails.get("remarks").toString();
                    Object remarksArgs = null;
                    if (consentRememberEventDetails.has("remarksArgs")) {
                        remarksArgs = consentRememberEventDetails.get("remarksArgs");
                    }
                    HashMap resMap = null;
                    if (consentRememberEventDetails.has("resMap")) {
                        resMap = (HashMap)consentRememberEventDetails.get("resMap");
                    }
                    if (DCEventLogUtil.getInstance().addEvent(event_id, userName, resMap, remarks, remarksArgs, true, customer_id, consent_id) == 1001L) {
                        throw new DataAccessException();
                    }
                }
                catch (final JSONException jsonException) {
                    ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while retrieving consent event details" + jsonException);
                    throw jsonException;
                }
            }
        }
        catch (final SyMException symException) {
            ConsentRememberStatusUtil.log.log(Level.INFO, "Exception while saving consent remember event" + symException);
            throw symException;
        }
    }
    
    public static int deleteRememberStatusDetail(final Long consent_id, final Long user_id) throws DataAccessException, NotSupportedException {
        try {
            if (ConsentsUtil.isUserSpecificConsent(consent_id)) {
                Criteria deleteCriteria = new Criteria(new Column("ConsentRememberStatus", "CONSENT_ID"), (Object)consent_id, 0);
                if (user_id == null) {
                    throw new NotSupportedException("User specific consent does not have user id, it leads to delete all the data");
                }
                deleteCriteria = deleteCriteria.and(new Criteria(new Column("ConsentRememberStatus", "USER_ID"), (Object)user_id, 0));
                SyMUtil.getPersistence().delete(deleteCriteria);
            }
        }
        catch (final DataAccessException dae) {
            ConsentRememberStatusUtil.log.log(Level.INFO, "exception while deleting consent status" + dae);
            throw dae;
        }
        return 1000;
    }
    
    static {
        ConsentRememberStatusUtil.log = Logger.getLogger(ConsentsUtil.class.getName());
    }
}
