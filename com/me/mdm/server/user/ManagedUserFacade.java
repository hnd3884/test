package com.me.mdm.server.user;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.role.RBDAUtil;
import java.util.HashSet;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.util.DirectoryUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.me.mdm.server.config.ProfileAssociateHandler;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Collection;
import java.util.Arrays;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Properties;
import com.adventnet.sym.webclient.mdm.MDMEnrollAction;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.logging.Level;
import java.util.List;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ManagedUserFacade
{
    private static ManagedUserFacade instance;
    protected static Logger logger;
    
    public JSONObject addUser(final JSONObject request) throws APIHTTPException {
        try {
            final String userName = request.getJSONObject("msg_body").optString("user_name", (String)null);
            String userEmail = request.getJSONObject("msg_body").optString("email_id", (String)null);
            final boolean sendInvite = request.getJSONObject("msg_body").optBoolean("send_invite", false);
            if (sendInvite && !ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIHTTPException("MAS001", new Object[0]);
            }
            if (userEmail == null) {
                userEmail = request.getJSONObject("msg_body").optString("user_email", (String)null);
            }
            String domain = request.getJSONObject("msg_body").optString("ad_domain", (String)null);
            final String phone = request.getJSONObject("msg_body").optString("phone_number", (String)null);
            final StringBuilder errorMessage = new StringBuilder();
            if (userName == null) {
                errorMessage.append("user_name, ");
            }
            if (userEmail == null) {
                errorMessage.append("email_id, ");
            }
            if (errorMessage.length() != 0) {
                throw new APIHTTPException("COM0005", new Object[] { errorMessage.substring(0, errorMessage.length() - 2) });
            }
            if (domain == null) {
                domain = "MDM";
            }
            final JSONObject user = new JSONObject();
            user.put("NAME", (Object)userName);
            user.put("DOMAIN_NETBIOS_NAME", (Object)domain);
            user.put("EMAIL_ADDRESS", (Object)userEmail);
            user.put("PHONE_NUMBER", (Object)((phone == null) ? phone : phone.trim().replaceAll(" ", "")));
            user.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(request));
            user.put("DISPLAY_NAME", (Object)request.getJSONObject("msg_body").optString("display_name", userName));
            user.put("FIRST_NAME", (Object)request.getJSONObject("msg_body").optString("first_name", userName));
            this.ValidateAddUserInput(user);
            final Long userId = ManagedUserHandler.getInstance().addOrUpdateAndGetUserId(user);
            final List remarksList = new ArrayList();
            remarksList.add(userName);
            MDMEventLogHandler.getInstance().addEvent(2001, DMUserHandler.getUserNameFromUserID(APIUtil.getUserID(request)), "mdm.user.add_user", remarksList, APIUtil.getCustomerID(request), System.currentTimeMillis());
            final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
            logJSON.put((Object)"REMARKS", (Object)"create-success");
            logJSON.put((Object)"MANAGED_USER_ID", (Object)userId);
            MDMOneLineLogger.log(Level.INFO, "ADD_LOCAL_USER", logJSON);
            request.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject());
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").put("user_id", (Object)userId);
            if (sendInvite) {
                final Properties properties = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(domain, userName, null, userEmail, String.valueOf(2), APIUtil.getCustomerID(request), true, String.valueOf(0), false);
                ((Hashtable<String, Boolean>)properties).put("KNOX_LIC_DS", false);
                ((Hashtable<String, Boolean>)properties).put("isAzure", false);
                ((Hashtable<String, Boolean>)properties).put("byAdmin", false);
                boolean sendEmail = false;
                if (MDMUtil.getInstance().isValidEmail(userEmail)) {
                    sendEmail = true;
                }
                boolean sendSMS = false;
                if (MDMEnrollmentUtil.isValidPhone(phone)) {
                    CustomerInfoUtil.getInstance();
                    if (CustomerInfoUtil.isSAS()) {
                        sendSMS = true;
                    }
                }
                MDMEnrollmentUtil.getInstance().setEnrollmentInvitationProperties(properties, phone, String.valueOf(sendEmail), String.valueOf(sendSMS));
                new MDMEnrollAction().addEnrollmentRequest(properties);
            }
            return this.getUser(request);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void removeUsers(final JSONObject request) throws APIHTTPException {
        try {
            final Long mUserID = JSONUtil.optLongForUVH(request.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_id", Long.valueOf(0L));
            final Long customerID = APIUtil.getCustomerID(request);
            JSONArray muidArray = null;
            if (mUserID != 0L) {
                muidArray = new JSONArray();
                muidArray.put((Object)mUserID);
            }
            else if (request.getJSONObject("msg_body").has("user_ids")) {
                muidArray = request.getJSONObject("msg_body").optJSONArray("user_ids");
            }
            if (muidArray == null) {
                throw new APIHTTPException("COM0008", new Object[] { "user_id or user_ids" });
            }
            if (muidArray.length() != 0) {
                final Long[] removeList = new Long[muidArray.length()];
                for (int i = 0; i < muidArray.length(); ++i) {
                    removeList[i] = Long.parseLong(String.valueOf(muidArray.get(i)));
                }
                this.validateIfUserExists(Arrays.asList(removeList), customerID);
                ManagedUserHandler.getInstance().removeUser(removeList, customerID, APIUtil.getUserID(request));
            }
        }
        catch (final Exception e) {
            ManagedUserFacade.logger.log(Level.SEVERE, "Exception occured", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            if (!(e instanceof SyMException)) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            if (((SyMException)e).getErrorCode() == 52102) {
                throw new APIHTTPException("ENR00109", new Object[0]);
            }
        }
    }
    
    protected void disassociateAllAppsAndProfilesFromUser(final Long userID, final Long customerID, final Long loggedOnUser) {
        final Properties profilePropertiesMap = this.getProfilesAssociatedToUser(userID);
        HashMap profileCollectionMap = ((Hashtable<K, HashMap>)profilePropertiesMap).get("appMap");
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
        ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
        ((Hashtable<String, Boolean>)properties).put("isGroup", false);
        ((Hashtable<String, List<Long>>)properties).put("resourceList", Arrays.asList(userID));
        ((Hashtable<String, Long>)properties).put("loggedOnUser", loggedOnUser);
        ((Hashtable<String, Integer>)properties).put("resourceType", 2);
        if (!profileCollectionMap.isEmpty()) {
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, List<Long>>)properties).put("configSourceList", Arrays.asList(userID));
            ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
        }
        profileCollectionMap = ((Hashtable<K, HashMap>)profilePropertiesMap).get("profileMap");
        if (!profileCollectionMap.isEmpty()) {
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", false);
            ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
            ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
        }
    }
    
    public void disassociateAllAppsAndProfilesFromUsers(final Collection<Long> userIDs, final Long customerID, final Long loggedOnUser) {
        for (final Long userID : userIDs) {
            this.disassociateAllAppsAndProfilesFromUser(userID, customerID, loggedOnUser);
        }
    }
    
    public JSONObject modifyUsers(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long mUserID = APIUtil.getResourceID(apiRequest, "user_id");
            final Long customerId = APIUtil.getCustomerID(apiRequest);
            final ArrayList userList = new ArrayList();
            userList.add(mUserID);
            this.validateIfUserExists(userList, customerId);
            final JSONObject body = apiRequest.getJSONObject("msg_body");
            final Iterator itr = body.keys();
            final JSONObject inputJson = new JSONObject();
            while (itr.hasNext()) {
                final String key = itr.next();
                inputJson.put(key.toUpperCase(), body.get(key));
            }
            inputJson.put("MANAGED_USER_ID", (Object)mUserID);
            inputJson.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(apiRequest));
            final String email = inputJson.getString("USER_EMAIL");
            final String userName = inputJson.optString("USER_NAME");
            if (!MDMUtil.isStringEmpty(userName)) {
                inputJson.put("NAME", (Object)userName);
            }
            inputJson.put("EMAIL_ADDRESS", (Object)email);
            inputJson.remove("USER_EMAIL");
            ManagedUserHandler.getInstance().modifyUser(inputJson);
            return new JSONObject();
        }
        catch (final SyMException exp) {
            ManagedUserFacade.logger.log(Level.SEVERE, "Exception in modifyUsers", (Throwable)exp);
            if (exp.getErrorCode() == 51021) {
                throw new APIHTTPException("COM0005", new Object[] { "Email, Phone Number" });
            }
            if (exp.getErrorCode() == 51023) {
                throw new APIHTTPException("ENR00111", new Object[0]);
            }
            if (exp.getErrorCode() == 51022) {
                throw new APIHTTPException("ENR00110", new Object[0]);
            }
            if (exp.getErrorCode() == 52103) {
                throw new APIHTTPException("ENR00112", new Object[0]);
            }
            if (exp.getErrorCode() == 51014) {
                throw new APIHTTPException("COM0005", new Object[] { "AD User" });
            }
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception exp2) {
            ManagedUserFacade.logger.log(Level.SEVERE, "Exception in modifyUsers", exp2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getUsers(final JSONObject apiRequest) throws APIHTTPException {
        final JSONArray result = new JSONArray();
        Connection conn = null;
        DataSet ds = null;
        try {
            final Boolean selectAll = APIUtil.getBooleanFilter(apiRequest, "select_all", false);
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final PagingUtil pagingUtil = apiUtil.getPagingParams(apiRequest);
            final SelectQuery userQuery = this.getUsersBaseQuery(apiRequest);
            Column managedUserColumn = Column.getColumn("ManagedUser", "MANAGED_USER_ID");
            managedUserColumn = managedUserColumn.distinct();
            managedUserColumn.setColumnAlias("MANAGED_USER_ID");
            userQuery.addSelectColumn(managedUserColumn);
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID", "USER_ID"));
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "FIRST_NAME"));
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "MIDDLE_NAME"));
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "LAST_NAME"));
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"));
            userQuery.addSelectColumn(Column.getColumn("ManagedUser", "PHONE_NUMBER"));
            userQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            final SelectQuery countQuery = this.getUsersBaseQuery(apiRequest);
            Column selCol = new Column("Resource", "RESOURCE_ID");
            selCol = selCol.distinct();
            selCol = selCol.count();
            countQuery.addSelectColumn(selCol);
            final JSONObject response = new JSONObject();
            final int count = DBUtil.getRecordCount(countQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            response.put("metadata", (Object)meta);
            if (count != 0) {
                if (!selectAll) {
                    final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                    if (pagingJSON != null) {
                        response.put("paging", (Object)pagingJSON);
                    }
                    userQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                }
                userQuery.addSortColumn(new SortColumn("Resource", "NAME", true));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)userQuery, conn);
                while (ds.next()) {
                    final JSONObject groupMap = new JSONObject();
                    groupMap.put("user_id", (Object)ds.getValue("RESOURCE_ID").toString());
                    groupMap.put("user_name", ds.getValue("NAME"));
                    final String domainName = (String)ds.getValue("DOMAIN_NETBIOS_NAME");
                    groupMap.put("domain_name", (Object)domainName);
                    final String email = (String)ds.getValue("EMAIL_ADDRESS");
                    if (email != null) {
                        groupMap.put("user_email", (Object)email);
                    }
                    String phNum = (String)ds.getValue("PHONE_NUMBER");
                    phNum = ((phNum == null || phNum.equalsIgnoreCase("")) ? "--" : phNum);
                    groupMap.put("PHONE_NUMBER", (Object)phNum);
                    result.put((Object)groupMap);
                }
            }
            response.put("users", (Object)result);
            return response;
        }
        catch (final Exception e) {
            ManagedUserFacade.logger.log(Level.SEVERE, "Exception occured", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            this.closeConnection(conn, ds);
        }
    }
    
    private SelectQuery getUsersBaseQuery(final JSONObject apiRequest) {
        final String search = APIUtil.optStringFilter(apiRequest, "search", null);
        final String identifier = APIUtil.optStringFilter(apiRequest, "user_identifier", "name");
        final boolean isLost = APIUtil.getBooleanFilter(apiRequest, "is_lost", false);
        final String include = APIUtil.optStringFilter(apiRequest, "include", null);
        final String userTypeStr = APIUtil.optStringFilter(apiRequest, "user_type", "");
        final SelectQuery userQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        final Join userResourceJoin = new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join managedDeviceJoin = new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join lostModeTrackInfoJoin = new Join("ManagedUserToDevice", "LostModeTrackInfo", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1);
        userQuery.addJoin(userResourceJoin);
        userQuery.addJoin(managedDeviceJoin);
        userQuery.addJoin(lostModeTrackInfoJoin);
        Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0);
        if (include == null || !include.equalsIgnoreCase("ADUSER")) {
            criteria = criteria.and(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)null, 1));
        }
        if (search != null) {
            final String s = identifier;
            switch (s) {
                case "name": {
                    criteria = ((criteria != null) ? criteria.and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false)) : new Criteria(Column.getColumn("Resource", "NAME"), (Object)search, 12, false));
                    break;
                }
                case "email_id": {
                    criteria = ((criteria != null) ? criteria.and(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)search, 12, false)) : new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)search, 12, false));
                    break;
                }
            }
        }
        final Criteria nonDirUseCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 0, false);
        final Criteria dirUseCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1, false);
        if (userTypeStr != "") {
            final String[] userTypes = userTypeStr.split(",");
            final List userTypeList = Arrays.asList(userTypes);
            if (userTypeList.contains("901") && !userTypeList.contains("902")) {
                criteria = criteria.and(nonDirUseCri);
            }
            else if (!userTypeList.contains("901") && userTypeList.contains("902")) {
                criteria = criteria.and(dirUseCri);
            }
        }
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1));
        if (criteria != null) {
            userQuery.setCriteria(criteria.and(userNotInTrashCriteria));
        }
        else {
            userQuery.setCriteria(userNotInTrashCriteria);
        }
        if (isLost) {
            Criteria baseCriteria = userQuery.getCriteria();
            final Criteria lostCriteria = new Criteria(Column.getColumn("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new int[] { 2, 1, 3, 6, 4 }, 8);
            if (baseCriteria != null) {
                baseCriteria = baseCriteria.and(lostCriteria);
            }
            else {
                baseCriteria = lostCriteria;
            }
            userQuery.setCriteria(baseCriteria);
        }
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(apiRequest), 0).and(userQuery.getCriteria());
        userQuery.setCriteria(customerCriteria);
        return userQuery;
    }
    
    public JSONObject getUser(final JSONObject request) throws APIHTTPException {
        try {
            final Long userID = JSONUtil.optLongForUVH(request.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_id", Long.valueOf(0L));
            if (userID == 0L) {
                throw new APIHTTPException("COM0008", new Object[] { "user_id : " + userID });
            }
            final Long customerId = APIUtil.getCustomerID(request);
            final ArrayList userList = new ArrayList();
            userList.add(userID);
            this.validateIfUserExists(userList, customerId);
            final HashMap details = ManagedUserHandler.getInstance().getManagedUserDetails(userID);
            if (details == null) {
                throw new APIHTTPException("COM0008", new Object[] { "user_id : " + userID });
            }
            final JSONObject user = new JSONObject();
            if (!details.isEmpty()) {
                user.put("user_email", details.get("EMAIL_ADDRESS"));
                user.put("user_name", details.get("NAME"));
                user.put("user_id", (Object)String.valueOf(details.get("MANAGED_USER_ID")));
                String ph = details.get("PHONE_NUMBER");
                ph = ((ph == null || ph.equalsIgnoreCase("")) ? "--" : ph);
                user.put("phone_number", (Object)ph);
                if (!String.valueOf(details.get("DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                    user.put("ad_domain", details.get("DOMAIN_NETBIOS_NAME"));
                }
                else {
                    user.put("ad_domain", details.get("MDM"));
                }
                user.put("device_count", ManagedDeviceHandler.getInstance().getManagedDeviceCountForUser(userID, 2, null));
            }
            else {
                String user_email = DirectoryUtil.getInstance().getFirstDirObjAttrValue(userID, Long.valueOf(106L));
                user_email = ((user_email == null || user_email.equalsIgnoreCase("")) ? "--" : user_email);
                user.put("user_email", (Object)user_email);
                user.put("user_name", (Object)DirectoryUtil.getInstance().getFirstDirObjAttrValue(userID, Long.valueOf(104L)));
                user.put("user_id", (Object)userID);
                String ph2 = DirectoryUtil.getInstance().getFirstDirObjAttrValue(userID, Long.valueOf(114L));
                ph2 = ((ph2 == null || ph2.equalsIgnoreCase("")) ? "--" : ph2);
                user.put("phone_number", (Object)ph2);
                user.put("ad_domain", DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)userID, "DOMAIN_NETBIOS_NAME"));
                user.put("device_count", 0);
            }
            return user;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception e) {
            ManagedUserFacade.logger.log(Level.SEVERE, "Exception occured", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static ManagedUserFacade getInstance() {
        if (ManagedUserFacade.instance == null) {
            ManagedUserFacade.instance = new ManagedUserFacade();
        }
        return ManagedUserFacade.instance;
    }
    
    private void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            ManagedUserFacade.logger.log(Level.WARNING, "Exception occurred in closeConnection....", ex);
        }
    }
    
    public Object getAppsForUser(final JSONObject request) throws APIHTTPException {
        try {
            final JSONObject result = new JSONObject();
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
            final Long userID = APIUtil.getResourceID(request, "user_id");
            final Integer platform = APIUtil.getIntegerFilter(request, "platform");
            this.validateIfUserExists(Arrays.asList(userID), APIUtil.getCustomerID(request));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToUser"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)userID, 0));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), false));
            final SelectQuery cQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToUser"));
            Column selCol = new Column("MdAppCatalogToUser", "RESOURCE_ID");
            selCol = selCol.count();
            cQuery.addSelectColumn(selCol);
            cQuery.setCriteria(new Criteria(Column.getColumn("MdAppCatalogToUser", "RESOURCE_ID"), (Object)userID, 0));
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(DBUtil.getRecordCount(cQuery));
            if (pagingJSON != null) {
                result.put("paging", (Object)pagingJSON);
            }
            selectQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
            final DataObject appsDO = SyMUtil.getPersistence().get(selectQuery);
            final Iterator rows = appsDO.getRows("MdAppCatalogToUser");
            final JSONArray apps = new JSONArray();
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long appID = (Long)row.get("PUBLISHED_APP_ID");
                request.getJSONObject("msg_header").getJSONObject("resource_identifier").put("app_id", (Object)AppsUtil.getInstance().getAppPackageId(appID));
                final JSONObject temp = MDMRestAPIFactoryProvider.getAppFacade().getApp(request);
                if (platform != -1L) {
                    if (Integer.valueOf(String.valueOf(temp.get("platform_type"))) != platform) {
                        continue;
                    }
                    apps.put((Object)temp);
                }
                else {
                    apps.put((Object)temp);
                }
            }
            result.put("apps", (Object)apps);
            return result;
        }
        catch (final Exception e) {
            ManagedUserFacade.logger.log(Level.SEVERE, "Exception occured", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Properties getProfilesAssociatedToUser(final Long userId) {
        final Properties properties = new Properties();
        final HashMap<Long, Long> appMap = new HashMap<Long, Long>();
        final HashMap<Long, Long> profileMap = new HashMap<Long, Long>();
        Connection conn = null;
        DataSet ds = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForMDMResource"));
        query.addJoin(new Join("RecentProfileForMDMResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        query.addJoin(new Join("RecentProfileForMDMResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)userId, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"));
        query.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "COLLECTION_ID"));
        query.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID", "PROFILE_TABLE_PK"));
        query.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                switch (Integer.valueOf(String.valueOf(ds.getValue("PROFILE_TYPE")))) {
                    case 2: {
                        appMap.put((Long)ds.getValue("PROFILE_ID"), (Long)ds.getValue("COLLECTION_ID"));
                        continue;
                    }
                    case 1: {
                        profileMap.put((Long)ds.getValue("PROFILE_ID"), (Long)ds.getValue("COLLECTION_ID"));
                        continue;
                    }
                }
            }
            ((Hashtable<String, HashMap<Long, Long>>)properties).put("appMap", appMap);
            ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileMap", profileMap);
        }
        catch (final Exception e) {
            ManagedUserFacade.logger.log(Level.SEVERE, null, e);
        }
        finally {
            this.closeConnection(conn, ds);
        }
        return properties;
    }
    
    public List<Long> getListOfUsersWithProfilesAssociated(final Collection<Long> userIDs) {
        final HashSet<Long> users = new HashSet<Long>(userIDs);
        final List<Long> userIDList = new ArrayList<Long>();
        Connection conn = null;
        DataSet ds = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForMDMResource"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)users.toArray(), 8));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"));
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                userIDList.add(Long.valueOf(String.valueOf(ds.getValue("RESOURCE_ID"))));
            }
        }
        catch (final Exception e) {
            ManagedUserFacade.logger.log(Level.SEVERE, null, e);
        }
        finally {
            this.closeConnection(conn, ds);
        }
        return userIDList;
    }
    
    public void validateIfUserExists(Collection<Long> userIDs, final Long customerID) {
        try {
            userIDs = new HashSet<Long>(userIDs);
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)userIDs.toArray(), 8).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            final Criteria userCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0);
            criteria = criteria.and(userCri);
            selectQuery.setCriteria(criteria);
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("Resource");
            final ArrayList<Long> users = new ArrayList<Long>();
            while (rows.hasNext()) {
                users.add(Long.valueOf(String.valueOf(rows.next().get("RESOURCE_ID"))));
            }
            userIDs.removeAll(users);
            if (!userIDs.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(userIDs) });
            }
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 0);
            final Iterator inTrashIterator = dataObject.getRows("ManagedUser", userNotInTrashCriteria);
            final ArrayList inTrashList = (ArrayList)DBUtil.getColumnValuesAsList(inTrashIterator, "MANAGED_USER_ID");
            if (!inTrashList.isEmpty()) {
                throw new APIHTTPException("ENR00108", new Object[] { APIUtil.getCommaSeperatedString(inTrashList) });
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void ValidateAddUserInput(final JSONObject jsonObject) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "PHONE_NUMBER"));
        final Long customerID = (Long)jsonObject.get("CUSTOMER_ID");
        final String username = jsonObject.optString("NAME");
        final String domain = jsonObject.optString("DOMAIN_NETBIOS_NAME");
        final String userEmail = jsonObject.optString("EMAIL_ADDRESS");
        final String phone = jsonObject.optString("PHONE_NUMBER");
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1));
        Criteria criteria2 = null;
        if (username != null && !username.equalsIgnoreCase("")) {
            criteria2 = ((criteria2 == null) ? new Criteria(Column.getColumn("Resource", "NAME"), (Object)username, 0) : criteria2.and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)username, 0)));
        }
        if (domain != null && !domain.equalsIgnoreCase("") && !domain.equals("MDM")) {
            criteria2 = ((criteria2 == null) ? new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domain, 0) : criteria2.and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domain, 0)));
        }
        if (criteria2 != null) {
            selectQuery.setCriteria(criteria.and(criteria2));
        }
        else {
            selectQuery.setCriteria(criteria);
        }
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final List<String> invalidparams = new ArrayList<String>();
        if (!dataObject.isEmpty()) {
            if (username != null && !username.equalsIgnoreCase("")) {
                final Iterator iterator = dataObject.getRows("Resource", new Criteria(Column.getColumn("Resource", "NAME"), (Object)username, 0));
                if (iterator.hasNext()) {
                    invalidparams.add("username");
                }
            }
            if (domain != null && !domain.equalsIgnoreCase("") && !domain.equals("MDM")) {
                final Iterator iterator = dataObject.getRows("Resource", new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domain, 0));
                if (iterator.hasNext()) {
                    invalidparams.add("domain");
                }
            }
            if (!invalidparams.isEmpty()) {
                throw new APIHTTPException("COM0005", new Object[] { invalidparams.toString() });
            }
        }
    }
    
    static {
        ManagedUserFacade.logger = Logger.getLogger("MDMEnrollment");
    }
}
