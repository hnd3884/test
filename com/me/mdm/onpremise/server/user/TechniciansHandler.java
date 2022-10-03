package com.me.mdm.onpremise.server.user;

import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import java.util.Iterator;
import java.util.Locale;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.me.mdm.server.user.BaseTechniciansHandler;

public class TechniciansHandler extends BaseTechniciansHandler
{
    private static Logger logger;
    
    private DataObject getUserDO(final Long userId, final Long customerId) {
        DataObject dataObject = null;
        try {
            final SelectQuery selectQuery = this.getUserQuery();
            final Criteria userIdCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userId, 0);
            final Criteria activeCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
            final Criteria criteria = this.getBaseUserCriteria(customerId);
            selectQuery.setCriteria(userIdCriteria.and(criteria).and(activeCriteria));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final DataAccessException e) {
            TechniciansHandler.logger.log(Level.SEVERE, " -- getUserDO()   >   Error, ", (Throwable)e);
        }
        return dataObject;
    }
    
    public SelectQuery getUserQuery() {
        final SelectQuery query = super.getUserQuery();
        query.addJoin(new Join("AaaLogin", "AaaLoginExtn", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1));
        query.addJoin(new Join("AaaUser", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 1));
        query.addJoin(new Join("LoginUserCustomerMapping", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        query.addSelectColumn(new Column("AaaContactInfo", "EMAILID"));
        query.addSelectColumn(new Column("AaaContactInfo", "MOBILE"));
        query.addSelectColumn(new Column("AaaContactInfo", "LANDLINE"));
        query.addSelectColumn(new Column("AaaLoginExtn", "LOGIN_ID"));
        query.addSelectColumn(new Column("AaaLoginExtn", "CREATION_TIME"));
        query.addSelectColumn(new Column("AaaLoginExtn", "MODIFIED_TIME"));
        query.addSelectColumn(new Column("CustomerInfo", "CUSTOMER_ID"));
        query.addSelectColumn(new Column("LoginUserCustomerMapping", "CUSTOMER_ID"));
        query.addSelectColumn(new Column("LoginUserCustomerMapping", "DC_USER_ID"));
        return query;
    }
    
    private DataObject getUserNameSearchDO(final String userName, final Long customerId, final int constraint) throws DataAccessException {
        final SelectQuery selectQuery = this.getUserQuery();
        Criteria criteria = null;
        if (!userName.equals("admin")) {
            criteria = this.getBaseUserCriteria(customerId);
        }
        final Criteria userNameCriteria = new Criteria(Column.getColumn("AaaUser", "FIRST_NAME"), (Object)userName, constraint, false);
        final Criteria activeCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
        selectQuery.setCriteria((criteria == null) ? userNameCriteria.and(activeCriteria) : criteria.and(userNameCriteria).and(activeCriteria));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    private DataObject getUserEmailSearchDO(final String userEmail, final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = this.getUserQuery();
        final Criteria criteria = this.getBaseUserCriteria(customerId);
        final Criteria userEmailCriteria = new Criteria(Column.getColumn("AaaContactInfo", "EMAILID"), (Object)userEmail, 12, false);
        final Criteria activeCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
        selectQuery.setCriteria(userEmailCriteria.and(criteria).and(activeCriteria));
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public Criteria getBaseUserCriteria(final Long customerId) {
        final Criteria customerCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria customerCriteria2 = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)null, 0);
        final Criteria umRoleCriteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_ID"), (Object)null, 1);
        final Criteria aaaLoginCriteia = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)null, 1);
        return customerCriteria.or(customerCriteria2).and(umRoleCriteria).and(aaaLoginCriteia);
    }
    
    protected JSONObject getUserJSONFromDO(final DataObject dataObject) throws Exception {
        if (dataObject == null || dataObject.isEmpty()) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final JSONObject userJSON = super.getUserJSONFromDO(dataObject);
        final String userName = dataObject.getFirstValue("AaaUser", "FIRST_NAME").toString();
        final String userEmail = dataObject.getFirstValue("AaaContactInfo", "EMAILID").toString();
        final String userPNumber = (String)dataObject.getFirstValue("AaaContactInfo", "MOBILE");
        final String userLandlineNumber = (String)dataObject.getFirstValue("AaaContactInfo", "LANDLINE");
        final Long createdTime = (Long)dataObject.getFirstValue("AaaLoginExtn", "CREATION_TIME");
        final Long modifiedTime = (Long)dataObject.getFirstValue("AaaLoginExtn", "MODIFIED_TIME");
        userJSON.put("user_name", (Object)userName);
        userJSON.put("user_email", (Object)userEmail);
        userJSON.put("user_pnumber", (Object)userPNumber);
        userJSON.put("user_landline_number", (Object)userLandlineNumber);
        userJSON.put("created_time", (Object)createdTime);
        userJSON.put("modified_time", (Object)modifiedTime);
        return userJSON;
    }
    
    public JSONObject getUserDetails(final JSONObject requestJSON) throws Exception {
        final Long userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
        final String userName = requestJSON.optString("search_user_name", "--");
        final String userEmail = requestJSON.optString("user_email", "--");
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customer_id", Long.valueOf(-1L));
        JSONObject userJSONObject = new JSONObject();
        if (userId != -1L) {
            final DataObject dataObject = this.getUserDO(userId, customerId);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIHTTPException("USR003", new Object[] { userId });
            }
            userJSONObject = this.getUserJSONFromDO(dataObject);
        }
        else if (!userName.equalsIgnoreCase("--")) {
            final DataObject dataObject = this.getUserNameSearchDO(userName, customerId, 12);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIHTTPException("USR003", new Object[] { userName });
            }
            final JSONArray tempArray = this.getUserJSONArrayFromDO(dataObject);
            userJSONObject.put("users", (Object)tempArray);
        }
        else if (!userEmail.equalsIgnoreCase("--")) {
            final DataObject dataObject = this.getUserEmailSearchDO(userEmail, customerId);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIHTTPException("USR003", new Object[] { userEmail });
            }
            final JSONArray tempArray = this.getUserJSONArrayFromDO(dataObject);
            userJSONObject.put("users", (Object)tempArray);
        }
        else {
            userJSONObject = this.getAllUserDetails(customerId);
        }
        return userJSONObject;
    }
    
    private JSONObject getAllUserDetails(final Long customerId) throws Exception {
        try {
            final SelectQuery selectQuery = this.getUserQuery();
            final Criteria customerCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria activeCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
            selectQuery.setCriteria(customerCriteria.and(activeCriteria));
            final DataObject resultJSONArray = MDMUtil.getPersistence().get(selectQuery);
            final JSONObject resultJSON = new JSONObject();
            final JSONArray usersJSONArray = this.getUserJSONArrayFromDO(resultJSONArray);
            final DataObject dataObject = this.getUserNameSearchDO("admin", customerId, 0);
            if (dataObject != null && !dataObject.isEmpty()) {
                final JSONArray tempJSONArray = this.getUserJSONArrayFromDO(dataObject);
                usersJSONArray.put((Object)tempJSONArray.getJSONObject(0));
            }
            resultJSON.put("users", (Object)usersJSONArray);
            return resultJSON;
        }
        catch (final Exception e) {
            TechniciansHandler.logger.log(Level.SEVERE, " -- getAllUserDetails()   >   Error, ", e);
            throw e;
        }
    }
    
    private JSONArray getUserJSONArrayFromDO(final DataObject dataObject) throws JSONException, DataAccessException {
        final JSONArray usersJSONArray = new JSONArray();
        if (dataObject != null && !dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("AaaUser");
            while (iterator.hasNext()) {
                final Row aaaUserRow = iterator.next();
                final JSONObject userJSON = super.getUserDetailsJSON(aaaUserRow, dataObject);
                final Long loginId = JSONUtil.optLongForUVH(userJSON, "login_id", Long.valueOf(-1L));
                final Long userId = JSONUtil.optLongForUVH(userJSON, "user_id", Long.valueOf(-1L));
                final String userName = (String)aaaUserRow.get("FIRST_NAME");
                final Row aaaLoginExtnRow = dataObject.getRow("AaaLoginExtn", new Criteria(Column.getColumn("AaaLoginExtn", "LOGIN_ID"), (Object)loginId, 0));
                if (aaaLoginExtnRow != null) {
                    final Long createdTime = (Long)aaaLoginExtnRow.get("CREATION_TIME");
                    final Long modifiedTime = (Long)aaaLoginExtnRow.get("MODIFIED_TIME");
                    userJSON.put("created_time", (Object)createdTime);
                    userJSON.put("modified_time", (Object)modifiedTime);
                }
                final Row aaaUserContactInfoRow = dataObject.getRow("AaaUserContactInfo", new Criteria(Column.getColumn("AaaUserContactInfo", "USER_ID"), (Object)userId, 0));
                final Long contactInfoId = (Long)aaaUserContactInfoRow.get("CONTACTINFO_ID");
                final Row aaaContactInfoRow = dataObject.getRow("AaaContactInfo", new Criteria(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID"), (Object)contactInfoId, 0));
                final String userEmail = (String)aaaContactInfoRow.get("EMAILID");
                final String userPNumber = (String)aaaContactInfoRow.get("MOBILE");
                final String userLandlineNumber = (String)aaaContactInfoRow.get("LANDLINE");
                final Row aaaUserProfileInfoRow = dataObject.getRow("AaaUserProfile", new Criteria(Column.getColumn("AaaUserProfile", "USER_ID"), (Object)userId, 0));
                final String countryCode = (String)aaaUserProfileInfoRow.get("COUNTRY_CODE");
                final String languageCode = (String)aaaUserProfileInfoRow.get("LANGUAGE_CODE");
                userJSON.put("user_name", (Object)userName);
                userJSON.put("user_email", (Object)userEmail);
                userJSON.put("user_pnumber", (Object)userPNumber);
                userJSON.put("user_landline_number", (Object)userLandlineNumber);
                userJSON.put("user_locale", (Object)new Locale(languageCode, countryCode));
                usersJSONArray.put((Object)userJSON);
            }
        }
        return usersJSONArray;
    }
    
    public Boolean isEmailExistForOtherUser(final String emailID, final Long userID) {
        Boolean isEmailExist = false;
        try {
            final SelectQuery selectUserEmail = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
            selectUserEmail.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectUserEmail.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectUserEmail.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            selectUserEmail.addSelectColumn(new Column("AaaContactInfo", "*"));
            final ArrayList criteriaList = new ArrayList();
            criteriaList.add(userID);
            criteriaList.add(DBUtil.getUVHValue("AaaUser:user_id:3"));
            selectUserEmail.setCriteria(new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)criteriaList.toArray(), 9));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectUserEmail);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator itr = dataObject.getRows("AaaContactInfo");
                while (itr.hasNext()) {
                    final Row userContactRow = itr.next();
                    final String userEmail = (String)userContactRow.get("EMAILID");
                    if (userEmail != null && !userEmail.equalsIgnoreCase("") && emailID.equalsIgnoreCase(userEmail)) {
                        isEmailExist = true;
                        break;
                    }
                }
            }
            return isEmailExist;
        }
        catch (final Exception e) {
            TechniciansHandler.logger.log(Level.SEVERE, "Exception in retrieving the isEmailExistForOtherUser data for deleting user {0}", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    public static TechniciansHandler getTechniciansInstance(final JSONObject apiRequest, final JSONObject requestJSON) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            final Long customerId = APIUtil.getLongFilter(apiRequest, "customer_id");
            requestJSON.put("customer_id", (Object)customerId);
            return new TechniciansMSPHandler();
        }
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        requestJSON.put("customer_id", (Object)customerId);
        return new TechniciansHandler();
    }
    
    protected String validateCustomerId(final JSONObject apiRequest, final String roleId) throws Exception {
        return String.valueOf(APIUtil.getCustomerID(apiRequest));
    }
    
    static {
        TechniciansHandler.logger = Logger.getLogger("UserManagementLogger");
    }
}
