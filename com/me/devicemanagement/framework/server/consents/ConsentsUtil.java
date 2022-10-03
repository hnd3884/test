package com.me.devicemanagement.framework.server.consents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class ConsentsUtil
{
    public static final int COMMON_CONSENT = 1;
    public static final int USER_SPECIFIC_CONSENT = 2;
    public static final int OPTIONAL_CONSENT = 3;
    static Logger log;
    
    public static DataSet getConsentDetailDO(final Criteria criteria, final Connection connection) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Consent"));
        final Join consentGroupJoin = new Join("Consent", "ConsentGroupTable", new String[] { "CONSENT_GROUP_ID" }, new String[] { "CONSENT_GROUP_ID" }, 1);
        selectQuery.addJoin(consentGroupJoin);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            final DataSet dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, connection);
            return dataSet;
        }
        catch (final SQLException sqlException) {
            ConsentsUtil.log.log(Level.INFO, "Exception while getting connection ConsentDetail" + sqlException);
            return null;
        }
        catch (final QueryConstructionException queryConstructionExcep) {
            ConsentsUtil.log.log(Level.INFO, "Exception while constructing query for ConsentDetail" + queryConstructionExcep);
            return null;
        }
    }
    
    public static JSONObject getConsentRowAsJsonObject(final DataSet dataSet) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CONSENT_ID", (Object)dataSet.getValue("CONSENT_ID"));
            jsonObject.put("CONSENT_CATEGORY", (int)dataSet.getValue("CONSENT_CATEGORY"));
            jsonObject.put("CONSENT_NAME", dataSet.getValue("CONSENT_NAME"));
            jsonObject.put("CONSENT_DESCRIPTION", dataSet.getValue("CONSENT_DESCRIPTION"));
            jsonObject.put("CONSENT_GROUP_ID", (Object)dataSet.getValue("CONSENT_GROUP_ID"));
            jsonObject.put("CONSENT_GROUP_NAME", dataSet.getValue("CONSENT_GROUP_NAME"));
            return jsonObject;
        }
        catch (final JSONException jsonExp) {
            ConsentsUtil.log.log(Level.INFO, "Exception while creating or adding data in json object" + jsonExp);
        }
        catch (final SQLException sqlException) {
            ConsentsUtil.log.log(Level.INFO, "Exception while retrieving data from data set" + sqlException);
        }
        return null;
    }
    
    public static JSONArray getConsentList(final Criteria criteria) {
        final JSONArray consentList = new JSONArray();
        Connection connection = null;
        try {
            connection = getConnection();
            final DataSet dataSet = getConsentDetailDO(criteria, connection);
            if (dataSet != null) {
                while (dataSet.next()) {
                    final JSONObject jsonObject = getConsentRowAsJsonObject(dataSet);
                    if (jsonObject != null) {
                        consentList.put((Object)jsonObject);
                    }
                }
            }
            dataSet.close();
        }
        catch (final SQLException sqlException) {
            ConsentsUtil.log.log(Level.INFO, "Exception while retrieving data from data set" + sqlException);
            return null;
        }
        finally {
            closeConnection(connection);
        }
        return consentList;
    }
    
    public static JSONObject getConsent(final Long consent_id) {
        final Criteria criteria = new Criteria(new Column("Consent", "CONSENT_ID"), (Object)consent_id, 0);
        Connection connection = null;
        try {
            connection = getConnection();
            final DataSet dataSet = getConsentDetailDO(criteria, connection);
            if (dataSet.next()) {
                final JSONObject jsonObject = getConsentRowAsJsonObject(dataSet);
                dataSet.close();
                return jsonObject;
            }
        }
        catch (final SQLException sqlException) {
            ConsentsUtil.log.log(Level.INFO, "Exception while retrieving data from data set" + sqlException);
        }
        finally {
            closeConnection(connection);
        }
        return null;
    }
    
    public static JSONArray getConsentList(final Long[] consent_ids) {
        final Criteria criteria = new Criteria(new Column("Consent", "CONSENT_ID"), (Object)consent_ids, 8);
        return getConsentList(criteria);
    }
    
    public static int getConsentCategory(final Long consent_id) {
        final JSONObject jsonObject = getConsent(consent_id);
        try {
            if (jsonObject != null) {
                final int consentCategory = (int)jsonObject.get("CONSENT_CATEGORY");
                return consentCategory;
            }
        }
        catch (final JSONException jsonExp) {
            ConsentsUtil.log.log(Level.INFO, "Exception while retriving jsonobject");
        }
        return 0;
    }
    
    public static boolean isCommonConsent(final Long consent_id) {
        return getConsentCategory(consent_id) == 1;
    }
    
    public static boolean isUserSpecificConsent(final Long consent_id) {
        return getConsentCategory(consent_id) == 2;
    }
    
    public static boolean isOptionalConsent(final Long consent_id) {
        return getConsentCategory(consent_id) == 3;
    }
    
    public static JSONArray getConsentDetails(final String groupName) {
        final Criteria criteria = new Criteria(new Column("ConsentGroupTable", "CONSENT_GROUP_NAME"), (Object)groupName, 0);
        return getConsentList(criteria);
    }
    
    public static JSONArray getConsentDetails(final String[] groupName) {
        final Criteria criteria = new Criteria(new Column("ConsentGroupTable", "CONSENT_GROUP_NAME"), (Object)groupName, 8);
        return getConsentList(criteria);
    }
    
    public static JSONArray getConsentDetailsByConsentName(final String consentName) {
        final Criteria criteria = new Criteria(new Column("Consent", "CONSENT_NAME"), (Object)consentName, 0);
        return getConsentList(criteria);
    }
    
    private static Connection getConnection() throws SQLException {
        try {
            return RelationalAPI.getInstance().getConnection();
        }
        catch (final SQLException sqlException) {
            ConsentsUtil.log.log(Level.INFO, "Exception in creating DB connection" + sqlException);
            throw sqlException;
        }
    }
    
    private static int closeConnection(final Connection connection) {
        try {
            connection.close();
            return 1000;
        }
        catch (final SQLException sqlException) {
            ConsentsUtil.log.log(Level.INFO, "Exception in closing DB connection" + sqlException);
            return 1001;
        }
    }
    
    static {
        ConsentsUtil.log = Logger.getLogger(ConsentsUtil.class.getName());
    }
}
