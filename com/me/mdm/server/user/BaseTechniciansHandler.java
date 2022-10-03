package com.me.mdm.server.user;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.Locale;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;

public abstract class BaseTechniciansHandler
{
    public SelectQuery getUserQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
        query.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        query.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1));
        query.addJoin(new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1));
        query.addJoin(new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1));
        query.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        query.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 1));
        query.addJoin(new Join("AaaLogin", "UserSummaryMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1));
        query.addJoin(new Join("AaaUser", "AaaUserProfile", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        query.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        query.addSelectColumn(new Column("AaaUser", "USER_ID"));
        query.addSelectColumn(new Column("AaaUser", "FIRST_NAME"));
        query.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
        query.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
        query.addSelectColumn(new Column("UMRole", "UM_ROLE_ID"));
        query.addSelectColumn(new Column("UMRole", "UM_ROLE_NAME"));
        query.addSelectColumn(new Column("UsersRoleMapping", "LOGIN_ID"));
        query.addSelectColumn(new Column("UsersRoleMapping", "UM_ROLE_ID"));
        query.addSelectColumn(new Column("AaaAccount", "ACCOUNT_ID"));
        query.addSelectColumn(new Column("AaaUserContactInfo", "USER_ID"));
        query.addSelectColumn(new Column("AaaUserContactInfo", "CONTACTINFO_ID"));
        query.addSelectColumn(new Column("AaaContactInfo", "CONTACTINFO_ID"));
        query.addSelectColumn(new Column("AaaContactInfo", "LANDLINE"));
        query.addSelectColumn(new Column("UserSummaryMapping", "LOGIN_ID"));
        query.addSelectColumn(new Column("UserSummaryMapping", "SUMMARYGROUP_ID"));
        query.addSelectColumn(new Column("AaaUserProfile", "USER_ID"));
        query.addSelectColumn(new Column("AaaUserProfile", "COUNTRY_CODE"));
        query.addSelectColumn(new Column("AaaUserProfile", "LANGUAGE_CODE"));
        query.addSelectColumn(new Column("AaaUserStatus", "USER_ID"));
        query.addSelectColumn(new Column("AaaUserStatus", "STATUS"));
        return query;
    }
    
    protected JSONObject getUserDetailsJSON(final Row aaaUserRow, final DataObject dataObject) throws DataAccessException, JSONException {
        final JSONObject userJSON = new JSONObject();
        final Long userId = (Long)aaaUserRow.get("USER_ID");
        final Row aaaLoginRow = dataObject.getRow("AaaLogin", new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userId, 0));
        Long loginId;
        String domainName;
        if (aaaLoginRow == null) {
            loginId = DMUserHandler.getLoginIdForUserId(userId);
            domainName = "local";
        }
        else {
            loginId = (Long)aaaLoginRow.get("LOGIN_ID");
            domainName = (String)aaaLoginRow.get("DOMAINNAME");
        }
        userJSON.put("domain_name", (Object)(MDMUtil.getInstance().isEmpty(domainName) ? "local" : domainName));
        final Row usersRoleMappingRow = dataObject.getRow("UsersRoleMapping", new Criteria(Column.getColumn("UsersRoleMapping", "LOGIN_ID"), (Object)loginId, 0));
        if (usersRoleMappingRow != null) {
            final Long userRoleId = (Long)usersRoleMappingRow.get("UM_ROLE_ID");
            final Row umRoleRow = dataObject.getRow("UMRole", new Criteria(Column.getColumn("UMRole", "UM_ROLE_ID"), (Object)userRoleId, 0));
            final String userRoleName = (String)umRoleRow.get("UM_ROLE_NAME");
            userJSON.put("user_role_id", (Object)userRoleId);
            userJSON.put("role_name", (Object)userRoleName);
        }
        userJSON.put("user_id", (Object)userId);
        userJSON.put("login_id", (Object)loginId);
        return userJSON;
    }
    
    protected JSONObject getUserJSONFromDO(final DataObject dataObject) throws Exception {
        final JSONObject userJSON = new JSONObject();
        final Long userId = (Long)dataObject.getFirstValue("AaaUser", "USER_ID");
        final String userRole = dataObject.getFirstValue("UMRole", "UM_ROLE_ID").toString();
        final String userRoleName = dataObject.getFirstValue("UMRole", "UM_ROLE_NAME").toString();
        final String domainName = (String)dataObject.getFirstValue("AaaLogin", "DOMAINNAME");
        final Long loginId = (Long)dataObject.getFirstValue("AaaLogin", "LOGIN_ID");
        if (dataObject.containsTable("AaaUserProfile")) {
            final String countryCode = (String)dataObject.getFirstValue("AaaUserProfile", "COUNTRY_CODE");
            final String languageCode = (String)dataObject.getFirstValue("AaaUserProfile", "LANGUAGE_CODE");
            userJSON.put("user_locale", (Object)new Locale(languageCode, countryCode));
        }
        else {
            final Row row = MDMDBUtil.getRowFromDB("AaaUserProfile", "USER_ID", (Object)userId);
            if (row != null) {
                final String countryCode2 = (String)row.get("COUNTRY_CODE");
                final String languageCode2 = (String)row.get("LANGUAGE_CODE");
                userJSON.put("user_locale", (Object)new Locale(languageCode2, countryCode2));
            }
        }
        userJSON.put("user_role_id", (Object)userRole);
        userJSON.put("role_name", (Object)userRoleName);
        userJSON.put("domain_name", (Object)(MDMUtil.getInstance().isEmpty(domainName) ? "local" : domainName));
        userJSON.put("user_id", (Object)userId);
        if (CustomerInfoUtil.getInstance().isMSP() && !userRoleName.equalsIgnoreCase("Administrator")) {
            final Criteria loginUserCustomerMapping = new Criteria(new Column("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userId, 0);
            final Iterator<Row> iterator = dataObject.getRows("LoginUserCustomerMapping", loginUserCustomerMapping);
            final List customerIdList = new ArrayList();
            while (iterator.hasNext()) {
                customerIdList.add(String.valueOf(iterator.next().get("CUSTOMER_ID")));
            }
            userJSON.put("customer_id_list", (Collection)customerIdList);
        }
        final ArrayList groupList = (ArrayList)MDMGroupHandler.getAssignedCustomGroupsForUser(loginId);
        final JSONArray groupJSONArray = APIUtil.getJSONArrayFromList(groupList);
        final int size = groupJSONArray.length();
        final List groupIdList = new ArrayList();
        for (int i = 0; i < size; ++i) {
            groupIdList.add(((JSONObject)groupJSONArray.get(i)).get("custom_gp_id").toString());
        }
        if (groupIdList.size() > 0) {
            userJSON.put("user_scope", (Object)"DeviceCG");
            userJSON.put("group_ids", (Collection)groupIdList);
            userJSON.put("group_details", (Object)groupJSONArray);
        }
        else {
            userJSON.put("user_scope", (Object)"all");
        }
        return userJSON;
    }
    
    public int validateScope(final String role_ID, JSONArray customGroupJSONArray) throws APIHTTPException {
        if (customGroupJSONArray == null) {
            customGroupJSONArray = new JSONArray();
        }
        int mdmScope;
        if (customGroupJSONArray.length() == 0) {
            mdmScope = 0;
        }
        else {
            mdmScope = 1;
        }
        if (DMUserHandler.isDefaultAdministratorRole(Long.valueOf(role_ID)) && mdmScope == 1) {
            throw new APIHTTPException("USR002", new Object[0]);
        }
        return mdmScope;
    }
    
    public abstract Criteria getBaseUserCriteria(final Long p0);
}
