package com.me.devicemanagement.framework.server.consents;

import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.UpdateQueryImpl;
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

public class ConsentStatusUtil
{
    public static final int CONSENT_PROVIDED = 1;
    public static final int CONSENT_DENIED = 2;
    public static final int CONSENT_ENABLED = 3;
    public static final int CONSENT_DISABLED = 4;
    static Logger log;
    private static String consentActionLoggerConfig;
    private static String consentEventLoggerConfig;
    public static boolean isConsentActionLoggerEnabled;
    public static boolean isConsentEventLoggerEnabled;
    
    public static DataObject getConsentStatusDetailDO(final Criteria criteria) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConsentStatus"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            return SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final DataAccessException dae) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while retrieving data from ConsentStatusDetail" + dae);
            return null;
        }
    }
    
    public static Long getConsentGroupId(final Long consentId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Consent"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Consent", "CONSENT_ID"), (Object)consentId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("Consent");
                return (Long)row.get("CONSENT_GROUP_ID");
            }
        }
        catch (final DataAccessException dae) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while retrieving data from ConsentGroupID", (Throwable)dae);
        }
        return null;
    }
    
    public static JSONObject getConsentStatusRowAsJsonObject(final Row consentStatusDetailRow) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CONSENT_ID", (Object)consentStatusDetailRow.get("CONSENT_ID"));
            jsonObject.put("STATUS", (int)consentStatusDetailRow.get("STATUS"));
            jsonObject.put("CONSENT_STATUS_ID", (Object)consentStatusDetailRow.get("CONSENT_STATUS_ID"));
            jsonObject.put("USER_ID", (Object)consentStatusDetailRow.get("USER_ID"));
            jsonObject.put("LAST_MODIFIED_TIME", (Object)consentStatusDetailRow.get("LAST_MODIFIED_TIME"));
            return jsonObject;
        }
        catch (final JSONException jsonExp) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while creating or adding data in jsonobject" + jsonExp);
            return null;
        }
    }
    
    public static JSONArray getConsentStatusDetail(final Criteria criteria) {
        final JSONArray consentList = new JSONArray();
        try {
            final DataObject dataObject = getConsentStatusDetailDO(criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows((String)null);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final JSONObject jsonObject = getConsentStatusRowAsJsonObject(row);
                    if (jsonObject != null) {
                        consentList.put((Object)jsonObject);
                    }
                }
            }
        }
        catch (final DataAccessException dae) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while trying to get consent status list" + dae);
            return null;
        }
        return consentList;
    }
    
    public static JSONObject getConsentStatusDetail(final Long consent_id) {
        return getConsentStatusDetail(consent_id, null);
    }
    
    public static JSONObject getConsentStatusDetail(final Long consent_id, final Long user_id) {
        Criteria consent_id_criteria = new Criteria(new Column("ConsentStatus", "CONSENT_ID"), (Object)consent_id, 0);
        if (user_id != null) {
            final Criteria user_id_criteria = new Criteria(new Column("ConsentStatus", "USER_ID"), (Object)user_id, 0);
            consent_id_criteria = consent_id_criteria.and(user_id_criteria);
        }
        final DataObject dataObject = getConsentStatusDetailDO(consent_id_criteria);
        try {
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ConsentStatus");
                final JSONObject jsonObject = getConsentStatusRowAsJsonObject(row);
                return jsonObject;
            }
        }
        catch (final DataAccessException dae) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while retrieving DO " + dae);
            return null;
        }
        return null;
    }
    
    public static JSONArray getConsentStatusDetails(final Long[] consent_ids) {
        final Criteria consent_id_criteria = new Criteria(new Column("ConsentStatus", "CONSENT_ID"), (Object)consent_ids, 8);
        return getConsentStatusDetail(consent_id_criteria);
    }
    
    public static int getConsentStatus(final Long consent_id) {
        final JSONObject jsonObject = getConsentStatusDetail(consent_id);
        try {
            if (jsonObject != null) {
                final int consentstatus = (int)jsonObject.get("STATUS");
                return consentstatus;
            }
        }
        catch (final JSONException jsonExp) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while retriving jsonobject" + jsonExp);
        }
        return 0;
    }
    
    public static boolean isConsentProvided(final Long consent_id) {
        final int status = getConsentStatus(consent_id);
        return status == 1;
    }
    
    public static boolean isConsentAlreadyExist(final Long consent_id) {
        return isConsentAlreadyExist(consent_id, null);
    }
    
    public static boolean isConsentAlreadyExist(final Long consent_id, final Long user_id) {
        return getConsentStatusDetail(consent_id, user_id) != null;
    }
    
    public static int saveConsentStatus(final Long user_id, final int status, final Long consent_id, final Long customer_id) throws SyMException, DataAccessException, NotSupportedException, JSONException {
        return saveConsentStatus(user_id, status, consent_id, null, null, customer_id, null);
    }
    
    public static int saveConsentStatus(final Long user_id, final int status, final Long consent_id, final JSONObject consentEventDetails, final Long customer_id) throws SyMException, DataAccessException, NotSupportedException, JSONException {
        return saveConsentStatus(user_id, status, consent_id, consentEventDetails, null, customer_id, null);
    }
    
    public static int saveConsentStatus(final Long user_id, final int status, final Long consent_id, final Long customer_id, final Boolean isRememberStatusEnabled) throws SyMException, DataAccessException, NotSupportedException, JSONException {
        return saveConsentStatus(user_id, status, consent_id, null, null, customer_id, isRememberStatusEnabled);
    }
    
    public static int saveConsentStatus(final Long user_id, final int status, final Long consent_id, final JSONObject consentEventDetails, final JSONObject consentRememberEventDetails, final Long customer_id, final Boolean isRememberStatusEnabled) throws SyMException, DataAccessException, NotSupportedException, JSONException {
        final Long time = System.currentTimeMillis();
        final Boolean is_user_specific_consent = ConsentsUtil.isUserSpecificConsent(consent_id);
        Boolean if_already_exist;
        if (is_user_specific_consent) {
            if_already_exist = isConsentAlreadyExist(consent_id, user_id);
        }
        else {
            if_already_exist = isConsentAlreadyExist(consent_id, null);
        }
        if (if_already_exist) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ConsentStatus");
            Criteria criteria = new Criteria(new Column("ConsentStatus", "CONSENT_ID"), (Object)consent_id, 0);
            if (is_user_specific_consent) {
                final Criteria user_criteria = new Criteria(new Column("ConsentStatus", "USER_ID"), (Object)user_id, 0);
                criteria = criteria.and(user_criteria);
            }
            updateQuery.setCriteria(criteria);
            updateQuery.setUpdateColumn("USER_ID", (Object)user_id);
            updateQuery.setUpdateColumn("STATUS", (Object)status);
            updateQuery.setUpdateColumn("LAST_MODIFIED_TIME", (Object)time);
            try {
                SyMUtil.getPersistence().update(updateQuery);
            }
            catch (final DataAccessException dae) {
                ConsentStatusUtil.log.log(Level.INFO, "Exception while updating the consent id" + consent_id + dae);
                throw dae;
            }
        }
        else {
            try {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                final Row row = new Row("ConsentStatus");
                row.set("CONSENT_ID", (Object)consent_id);
                row.set("USER_ID", (Object)user_id);
                row.set("STATUS", (Object)status);
                row.set("LAST_MODIFIED_TIME", (Object)time);
                dataObject.addRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
            catch (final DataAccessException dae2) {
                ConsentStatusUtil.log.log(Level.INFO, "Exception while saving consents" + dae2);
                throw dae2;
            }
        }
        try {
            if (isRememberStatusEnabled != null) {
                ConsentRememberStatusUtil.saveConsentRememberMyChoice(user_id, consent_id, customer_id, isRememberStatusEnabled, consentRememberEventDetails);
            }
        }
        catch (final NotSupportedException not) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while storing remember my choice" + not);
            throw not;
        }
        catch (final Exception excep) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while storing remember my choice" + excep);
            throw excep;
        }
        try {
            if (ConsentStatusUtil.isConsentActionLoggerEnabled && ConsentActionLoggerUtil.addLog(user_id, status, consent_id, false) == 1001) {
                return 1401;
            }
            if (ConsentStatusUtil.isConsentEventLoggerEnabled && consentEventDetails != null) {
                try {
                    final int event_id = (int)consentEventDetails.get("event_id");
                    final String userName = DMUserHandler.getUserNameFromUserID(user_id);
                    final String remarks = consentEventDetails.get("remarks").toString();
                    Object remarksArgs = null;
                    if (consentEventDetails.has("remarksArgs")) {
                        remarksArgs = consentEventDetails.get("remarksArgs");
                    }
                    HashMap resMap = null;
                    if (consentEventDetails.has("resMap")) {
                        resMap = (HashMap)consentEventDetails.get("resMap");
                    }
                    if (DCEventLogUtil.getInstance().addEvent(event_id, userName, resMap, remarks, remarksArgs, true, customer_id, consent_id) == null) {
                        return 1403;
                    }
                }
                catch (final JSONException jsonException) {
                    ConsentStatusUtil.log.log(Level.INFO, "Exception while retrieving consent event details" + jsonException);
                    return 1403;
                }
            }
        }
        catch (final SyMException symException) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while getting username" + symException);
            throw symException;
        }
        return 1000;
    }
    
    public static int deleteConsentStatusDetails(final Long consent_id) throws Exception {
        return deleteConsentStatusDetails(consent_id, null);
    }
    
    public static int deleteUserSpecificConsentStatusDetails(final Long consent_id, final Long user_id) throws Exception {
        return deleteConsentStatusDetails(consent_id, user_id);
    }
    
    public static int deleteConsentStatusDetails(final Long consent_id, final Long user_id) throws Exception {
        try {
            Criteria deleteCriteria = new Criteria(new Column("ConsentStatus", "CONSENT_ID"), (Object)consent_id, 0);
            if (user_id != null) {
                deleteCriteria = deleteCriteria.and(new Criteria(new Column("ConsentStatus", "USER_ID"), (Object)user_id, 0));
            }
            SyMUtil.getPersistence().delete(deleteCriteria);
            ConsentRememberStatusUtil.deleteRememberStatusDetail(consent_id, user_id);
            return 1000;
        }
        catch (final DataAccessException dae) {
            ConsentStatusUtil.log.log(Level.INFO, "exception while deleting consent status" + dae);
            throw dae;
        }
        catch (final NotSupportedException notSupportedExcep) {
            ConsentStatusUtil.log.log(Level.INFO, notSupportedExcep.getMessage() + notSupportedExcep);
            throw notSupportedExcep;
        }
    }
    
    static {
        ConsentStatusUtil.log = Logger.getLogger(ConsentsUtil.class.getName());
        ConsentStatusUtil.consentActionLoggerConfig = "consent_action_logger";
        ConsentStatusUtil.consentEventLoggerConfig = "consent_event_logger";
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            final String consentActionLoggerConf = String.valueOf(((JSONObject)frameworkConfigurations.get(ConsentStatusUtil.consentActionLoggerConfig)).get("enable"));
            ConsentStatusUtil.isConsentActionLoggerEnabled = Boolean.parseBoolean(consentActionLoggerConf);
            final String consentEventLoggerConf = String.valueOf(((JSONObject)frameworkConfigurations.get(ConsentStatusUtil.consentEventLoggerConfig)).get("enable"));
            ConsentStatusUtil.isConsentEventLoggerEnabled = Boolean.parseBoolean(consentEventLoggerConf);
        }
        catch (final JSONException jsonExcep) {
            ConsentStatusUtil.log.log(Level.INFO, "Exception while retrieving data from framework configuration" + jsonExcep);
        }
    }
}
