package com.me.devicemanagement.framework.server.authentication;

import com.me.ems.framework.uac.api.v1.model.User;
import java.sql.SQLException;
import com.me.devicemanagement.framework.server.deletionfw.DeletionQueueFailedException;
import com.me.devicemanagement.framework.server.deletionfw.DependentDeletionFailedException;
import com.me.devicemanagement.framework.server.deletionfw.DeletionFramework;
import org.json.JSONException;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.persistence.WritableDataObject;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import org.glassfish.jersey.internal.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.TimeZone;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.persistence.Persistence;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import java.util.Optional;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import java.util.Hashtable;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Logger;

public class DMUserHandler
{
    protected static Logger logger;
    protected static final Integer USER_HANDLING_LOCK;
    public static final String ADMINISTRATOR_ROLE = "Administrator";
    public static final String SUMMARY_SERVER_ADMIN_ROLE = "Summary Server Admin";
    public static final String CUSTOMER_ADMINISTRATOR_ROLE = "Customer Administrator";
    public static final String GUEST_ROLE = "Guest";
    public static final String CA_WRITE_ROLE = "CA_Write";
    public static final String SETTINGS_WRITE_ROLE = "Common_Write";
    public static final String TECHNICIAN_ROLE = "Technician";
    public static final String MDM_CLIENT_ROLE = "Mobile Device User";
    public static final String USERLOCALE = "USERLOCALE";
    public static final String USERTIMEZONEID = "USERTIMEZONEID";
    public static final String USERTIMEFORMAT = "TIMEFORMAT";
    public static final String USERDATEFORMAT = "DATEFORMAT";
    public static final String CLIENTUSER_ROLE = "ClientUser";
    public static final int SCOPE_ALL = 0;
    public static final int SCOPE_CG = 1;
    public static final int SCOPE_RO = 2;
    protected static List<String> defaultAdminRoles;
    
    public static boolean updateADUserForAuth(final Long loginID, final Long roleID) {
        boolean status = false;
        try {
            final Criteria criteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("UsersRoleMapping", criteria);
            final Row row = dataObject.getRow("UsersRoleMapping");
            row.set("UM_ROLE_ID", (Object)roleID);
            dataObject.updateRow(row);
            SyMUtil.getPersistence().update(dataObject);
            status = true;
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in updating user Account", e);
        }
        return status;
    }
    
    public static boolean setRoleForUser(final Long loginId, final Long roleID) throws DataAccessException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("UsersRoleMapping", "LOGIN_ID"), (Object)loginId, 0);
            selectQuery.setCriteria(criteria);
            DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.size("UsersRoleMapping") > 0) {
                final Row roleRow = dataObject.getRow("UsersRoleMapping");
                dataObject.deleteRow(roleRow);
                SyMUtil.getPersistence().update(dataObject);
                dataObject = SyMUtil.getPersistence().constructDataObject();
            }
            final Row roleRow = new Row("UsersRoleMapping");
            roleRow.set("LOGIN_ID", (Object)loginId);
            roleRow.set("UM_ROLE_ID", (Object)roleID);
            dataObject.addRow(roleRow);
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in setting role for user");
            e.printStackTrace();
            throw e;
        }
        return true;
    }
    
    public static String getDCUser(final Long loginID) {
        Criteria criteria = null;
        String userName = null;
        try {
            criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            if (!dataObject.isEmpty()) {
                final Row loginrow = dataObject.getRow("AaaLogin");
                userName = (String)loginrow.get("NAME");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", e);
        }
        return userName;
    }
    
    public static String getDCUserDomain(final Long loginID) {
        Criteria criteria = null;
        String domainName = null;
        try {
            criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            if (!dataObject.isEmpty()) {
                final Row loginrow = dataObject.getRow("AaaLogin");
                domainName = (String)loginrow.get("DOMAINNAME");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", e);
        }
        return domainName;
    }
    
    public static Long getDCUserID(final Long loginID) {
        Criteria criteria = null;
        Long userID = null;
        try {
            criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            if (!dataObject.isEmpty()) {
                final Row loginrow = dataObject.getRow("AaaLogin");
                userID = (Long)loginrow.get("USER_ID");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", e);
        }
        return userID;
    }
    
    public static Long getDCUserID(final String userName) {
        Criteria criteria = null;
        Long userID = null;
        try {
            criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)userName, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            if (!dataObject.isEmpty()) {
                final Row loginrow = dataObject.getRow("AaaLogin");
                userID = (Long)loginrow.get("USER_ID");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", e);
        }
        return userID;
    }
    
    @Deprecated
    public static ArrayList<Hashtable> getDCUsers() {
        return getDCUsers(null);
    }
    
    @Deprecated
    public static ArrayList<Hashtable> getDCUsers(Criteria roleCriteria) {
        final ArrayList<Hashtable> list = new ArrayList<Hashtable>();
        Criteria criteria = null;
        try {
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            final Join userLoginJoin = new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join rolesJoin = new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            final Join customerMapping = new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 1);
            selectQuery.addJoin(userLoginJoin);
            selectQuery.addJoin(rolesJoin);
            selectQuery.addJoin(customerMapping);
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            selectQuery.addSelectColumn(new Column("UMRole", "*"));
            selectQuery.addSelectColumn(new Column("UsersRoleMapping", "*"));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("AaaLogin", "NAME"), true);
            selectQuery.addSortColumn(sortColumn);
            selectQuery = ApiFactoryProvider.getAuthUtilAccessAPI().getActiveUsersCriteria(selectQuery);
            if (roleCriteria != null) {
                final Criteria activeUsersCriteria = selectQuery.getCriteria();
                if (activeUsersCriteria != null) {
                    roleCriteria = roleCriteria.and(activeUsersCriteria);
                }
                selectQuery.setCriteria(roleCriteria);
            }
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator loginRows = dataObject.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final Row loginrow = loginRows.next();
                final Long loginID = (Long)loginrow.get("LOGIN_ID");
                final Long userID = Long.parseLong(loginrow.get("USER_ID").toString());
                final Hashtable hashtable = new Hashtable();
                hashtable.put("LOGIN_ID", loginrow.get("LOGIN_ID"));
                hashtable.put("USER_ID", userID);
                hashtable.put("NAME", loginrow.get("NAME"));
                String domainName = (String)loginrow.get("DOMAINNAME");
                domainName = ((domainName == null || domainName.equalsIgnoreCase("-")) ? I18N.getMsg("dc.admin.fos.local", new Object[0]) : domainName);
                hashtable.put("DOMAINNAME", domainName);
                criteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginID, 0);
                Row mappingrow = dataObject.getRow("UsersRoleMapping", criteria);
                final Long roleID = (Long)mappingrow.get("UM_ROLE_ID");
                criteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleID, 0);
                mappingrow = dataObject.getRow("UMRole", criteria);
                hashtable.put("ROLE", mappingrow.get("UM_ROLE_NAME"));
                list.add(hashtable);
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", e);
        }
        return list;
    }
    
    private static SelectQuery constructUserFetchQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
        final Join userLoginJoin = new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
        final Join rolesJoin = new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
        final Join customerMapping = new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 1);
        selectQuery.addJoin(userLoginJoin);
        selectQuery.addJoin(rolesJoin);
        selectQuery.addJoin(customerMapping);
        selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
        selectQuery.addSelectColumn(new Column("UMRole", "*"));
        selectQuery.addSelectColumn(new Column("UsersRoleMapping", "*"));
        selectQuery.addSelectColumn(Column.getColumn("LoginUserCustomerMapping", "*"));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("AaaLogin", "NAME"), true);
        selectQuery.addSortColumn(sortColumn);
        return ApiFactoryProvider.getAuthUtilAccessAPI().getActiveUsersCriteria(selectQuery);
    }
    
    public static List<Hashtable> getDCUsers(Criteria roleCriteria, final Optional<Long> customerID) throws Exception {
        final SelectQuery selectQuery = constructUserFetchQuery();
        if (roleCriteria != null) {
            final Criteria activeUsersCriteria = selectQuery.getCriteria();
            if (activeUsersCriteria != null) {
                roleCriteria = roleCriteria.and(activeUsersCriteria);
            }
            selectQuery.setCriteria(roleCriteria);
        }
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        return constructUserList(dataObject, customerID);
    }
    
    private static List<Hashtable> constructUserList(final DataObject dataObject, final Optional<Long> customerID) throws Exception {
        final List<Hashtable> list = new ArrayList<Hashtable>();
        final Iterator loginRows = dataObject.getRows("AaaLogin");
        while (loginRows.hasNext()) {
            final Row loginrow = loginRows.next();
            final Long loginID = (Long)loginrow.get("LOGIN_ID");
            final Long userID = Long.parseLong(loginrow.get("USER_ID").toString());
            final Hashtable hashtable = new Hashtable();
            hashtable.put("LOGIN_ID", loginrow.get("LOGIN_ID"));
            hashtable.put("USER_ID", userID);
            hashtable.put("NAME", loginrow.get("NAME"));
            String domainName = (String)loginrow.get("DOMAINNAME");
            domainName = ((domainName == null || domainName.equalsIgnoreCase("-")) ? I18N.getMsg("dc.admin.fos.local", new Object[0]) : domainName);
            hashtable.put("DOMAINNAME", domainName);
            Criteria criteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginID, 0);
            Row mappingrow = dataObject.getRow("UsersRoleMapping", criteria);
            final Long roleID = (Long)mappingrow.get("UM_ROLE_ID");
            criteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleID, 0);
            mappingrow = dataObject.getRow("UMRole", criteria);
            final Iterator rows = dataObject.getRows("LoginUserCustomerMapping", new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0));
            if (customerID.isPresent()) {
                final Long customerId = customerID.get();
                boolean isManagingCurrCustomer = false;
                int customerCount = 0;
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    if (row.get("CUSTOMER_ID").equals(customerId)) {
                        isManagingCurrCustomer = true;
                    }
                    ++customerCount;
                }
                hashtable.put("OnlyCustomer", isManagingCurrCustomer && customerCount == 1);
            }
            hashtable.put("ROLE", mappingrow.get("UM_ROLE_NAME"));
            list.add(hashtable);
        }
        return list;
    }
    
    public static SelectQuery getDCUsersQuery() {
        SelectQuery selectQuery = null;
        try {
            selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            final Join aaaUserJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join loginJoin = new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join rolesJoin = new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            selectQuery.addJoin(aaaUserJoin);
            selectQuery.addJoin(loginJoin);
            selectQuery.addJoin(rolesJoin);
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "NAME"));
            selectQuery.addSelectColumn(new Column("UMRole", "UM_ROLE_NAME"));
        }
        catch (final Exception exp) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", exp);
        }
        return selectQuery;
    }
    
    public static boolean isUserAccountAvailable(final String userName, String domainName) {
        boolean isAvailable = false;
        if (domainName != null && !domainName.equalsIgnoreCase("-")) {
            addOrupdateDomainInfo(domainName);
        }
        try {
            domainName = ((domainName == null || domainName.equalsIgnoreCase("null")) ? "-" : domainName);
            Criteria criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)userName, 0, false);
            final Criteria criteria2 = new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false);
            criteria = criteria.and(criteria2);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            if (!dataObject.isEmpty()) {
                isAvailable = true;
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error occured while checking user account status", e);
        }
        return isAvailable;
    }
    
    public static void addOrupdateDomainInfo(final String domainName) {
        try {
            final Criteria criteria = new Criteria(new Column("ActiveDirectoryInfo", "DEFAULTDOMAIN"), (Object)domainName, 0, false);
            final DataObject dataObject = SyMUtil.getPersistence().get("ActiveDirectoryInfo", criteria);
            if (dataObject.isEmpty()) {
                final Properties domainProps = SoMADUtil.getInstance().getManagedDomainInfo(domainName);
                final Boolean isSSL = ((Hashtable<K, Boolean>)domainProps).get("USE_SSL");
                int port;
                try {
                    port = Integer.parseInt(((Hashtable<K, String>)domainProps).get("PORT_NO").trim());
                }
                catch (final Exception e) {
                    DMUserHandler.logger.log(Level.SEVERE, "addOrupdateDomainInfo: Error while parsing port from MANAGEDDOMAINCONFIG table", e);
                    port = (isSSL ? 636 : 389);
                }
                final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
                if (productCode == null || !productCode.equals("DCMSP")) {
                    SoMADUtil.getInstance().addOrUpdateActiveDirectoryInfo(domainProps.getProperty("DOMAIN_NETBIOS_NAME"), domainProps.getProperty("DC_NAME"), "", "", isSSL, port);
                }
            }
        }
        catch (final Exception e2) {
            DMUserHandler.logger.log(Level.SEVERE, "Error occured while checking user account status", e2);
        }
    }
    
    public static Long getLoginId() {
        return getLoginId(null);
    }
    
    public static Long getLoginId(final String moduleName) {
        Long loginId = null;
        try {
            loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            try {
                final List<String> roles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
                if (!CustomerInfoUtil.isSAS && roles != null && roles.contains("Patch_Edition_Role") && !moduleName.startsWith("PatchMgmt_") && !"PatchMgmt".equalsIgnoreCase(moduleName) && !"Admin".equalsIgnoreCase(moduleName) && !"Home".equalsIgnoreCase(moduleName) && !"Reports".equalsIgnoreCase(moduleName)) {
                    loginId = getDummyTechId();
                }
            }
            catch (final Exception ee) {
                DMUserHandler.logger.log(Level.SEVERE, "Error in getLoginId ", ee);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DMUserHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loginId;
    }
    
    public static long getDummyTechId() {
        final String cacheName = "DUMMY_TECHNICIAN";
        Long dummyTechId = new Long(-1L);
        if (ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 3) != null) {
            dummyTechId = (Long)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 3);
        }
        else {
            dummyTechId = getLoginIdForUser("dummy");
            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, dummyTechId, 3);
        }
        return dummyTechId;
    }
    
    public static Long getLoginIdForUser(final String userName) {
        return getLoginIdForUser(userName, null);
    }
    
    public static Long getLoginIdForUser(final String userName, String domainName) {
        Long loginID = null;
        try {
            domainName = ((domainName == null || domainName.equalsIgnoreCase("null")) ? "-" : domainName);
            Criteria criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)userName, 0, false);
            criteria = criteria.and(new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.containsTable("AaaLogin")) {
                final Row row = dataObject.getFirstRow("AaaLogin");
                if (row != null && row.get("LOGIN_ID") != null) {
                    loginID = (Long)row.get("LOGIN_ID");
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error occured while checking user account status", e);
        }
        return loginID;
    }
    
    public static Long getLoginIdForUserId(final Long userId) {
        Long loginID = null;
        try {
            final Criteria criteria = new Criteria(new Column("AaaLogin", "USER_ID"), (Object)userId, 0, false);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.containsTable("AaaLogin")) {
                final Row row = dataObject.getFirstRow("AaaLogin");
                if (row != null && row.get("LOGIN_ID") != null) {
                    loginID = (Long)row.get("LOGIN_ID");
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error occured while checking user account status", e);
        }
        return loginID;
    }
    
    public static Long getUserIdForLoginId(final Long loginId) {
        Long userId = null;
        try {
            final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginId, 0, false);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.containsTable("AaaLogin")) {
                final Row row = dataObject.getFirstRow("AaaLogin");
                if (row != null && row.get("USER_ID") != null) {
                    userId = (Long)row.get("USER_ID");
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error occured while checking user account status");
            e.printStackTrace();
        }
        return userId;
    }
    
    public static Long getRoleIdForUser(final Long loginID) {
        Long roleId = null;
        try {
            Criteria criteria = null;
            criteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            final Join roleJoin = new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            selectQuery.addJoin(roleJoin);
            selectQuery.addSelectColumn(new Column("UsersRoleMapping", "*"));
            selectQuery.addSelectColumn(new Column("UMRole", "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row roleRow = dataObject.getRow("UMRole");
            roleId = (Long)roleRow.get("UM_ROLE_ID");
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting role properties of user", e);
        }
        return roleId;
    }
    
    public static String getRoleForUser(final Long loginID) {
        String roleName = null;
        try {
            Criteria criteria = null;
            criteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            final Join roleJoin = new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            selectQuery.addJoin(roleJoin);
            selectQuery.addSelectColumn(new Column("UsersRoleMapping", "*"));
            selectQuery.addSelectColumn(new Column("UMRole", "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row roleRow = dataObject.getRow("UMRole");
            roleName = (String)roleRow.get("UM_ROLE_NAME");
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting role properties of user", e);
        }
        return roleName;
    }
    
    public static String getRoleNameForRoleId(final String roleId) {
        String roleName = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
            final Criteria criteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleId, 0);
            selectQuery.addSelectColumn(new Column("UMRole", "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row roleRow = dataObject.getRow("UMRole");
            roleName = (String)roleRow.get("UM_ROLE_NAME");
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting role name of the Role Id", e);
        }
        return roleName;
    }
    
    public static String getRoleForUser(final String loginName) {
        String roleName = null;
        try {
            Criteria criteria = null;
            criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)loginName, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            final Join loginJoin = new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join roleJoin = new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            selectQuery.addJoin(loginJoin);
            selectQuery.addJoin(roleJoin);
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            selectQuery.addSelectColumn(new Column("UsersRoleMapping", "*"));
            selectQuery.addSelectColumn(new Column("UMRole", "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row roleRow = dataObject.getRow("UMRole");
            roleName = (String)roleRow.get("UM_ROLE_NAME");
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting role properties of user", e);
        }
        return roleName;
    }
    
    public static int getUsersCountWithLogin() {
        int count = -1;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(new Column("AaaUser", "*"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            selectQuery.addSelectColumn(new Column("AaaUserStatus", "*"));
            final Criteria criteria = new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"DISABLED", 1);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            count = dataObject.size("AaaLogin");
        }
        catch (final Exception exp) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getUsersCountWithLogin", exp);
        }
        return count;
    }
    
    public static List getRoleList(final String roleid) throws Exception {
        return getRoleList(roleid, LicenseProvider.getInstance().getProductType());
    }
    
    public static List getRoleList(final String roleid, final String productType) throws Exception {
        final List roleList = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("UMRoleModuleRelation"));
            final Join join = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 1);
            sq.addJoin(join);
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria c = new Criteria(Column.getColumn("UMRoleModuleRelation", "UM_ROLE_ID"), (Object)roleid, 0);
            sq.addJoin(new Join("UMModule", "DCUserModuleExtn", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            if (LicenseProvider.getInstance().getMDMLicenseAPI() != null) {
                final String mdmEdition = LicenseProvider.getInstance().getMDMLicenseAPI().getMDMLiceseEditionType();
                if (mdmEdition != null) {
                    final String s = mdmEdition;
                    LicenseProvider.getInstance().getMDMLicenseAPI();
                    if (s.equalsIgnoreCase("Standard")) {
                        sq.addJoin(new Join("UMModule", "DCUserModule", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
                        final Criteria mdmEditionCrti = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 0, false).negate().and(new Criteria(Column.getColumn("DCUserModule", "MODULE_NAME"), (Object)"MDM", 10, false).or(new Criteria(Column.getColumn("DCUserModule", "MODULE_NAME"), (Object)"ModernMgmt", 10, false))).negate();
                        c = c.and(mdmEditionCrti);
                    }
                }
            }
            if (productType != null && !productType.equals("")) {
                if (productType.equalsIgnoreCase("standard")) {
                    c = c.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12));
                }
                else if (productType.equalsIgnoreCase("TOOLSADDON")) {
                    c = c.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"T", 12));
                }
            }
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            final String licenseVersion = LicenseProvider.getInstance().getLicenseVersion();
            final String uemTrailEnableParameter = UserMgmtUtil.getUserMgmtParameter("UEMTrailEnable");
            if (licenseType != null && (!licenseType.equalsIgnoreCase("R") || ((licenseVersion == null || !licenseVersion.equalsIgnoreCase("UEM")) && !productType.equalsIgnoreCase("UEM") && (uemTrailEnableParameter == null || !uemTrailEnableParameter.equals("Enabled")))) && !licenseType.equalsIgnoreCase("F") && !licenseType.equalsIgnoreCase("T")) {
                c = c.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"U", 0).negate());
            }
            sq.setCriteria(c);
            final DataObject dataObj = SyMUtil.getPersistence().get(sq);
            if (dataObj != null) {
                final Iterator ite = dataObj.getRows("UMModule");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    roleList.add(r.get("ROLE_ID"));
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while get  the RoleList :", e);
        }
        return roleList;
    }
    
    public static List getAvailableUserIDs(final Criteria criteria) throws Exception {
        final ArrayList list = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator loginRows = dataObject.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final Row loginrow = loginRows.next();
                final Long userID = (Long)loginrow.get("USER_ID");
                list.add(userID);
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting the list of user IDs", e);
        }
        return list;
    }
    
    public static List getAvailableUserIDs() throws Exception {
        final ArrayList list = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator loginRows = dataObject.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final Row loginrow = loginRows.next();
                final Long userID = (Long)loginrow.get("USER_ID");
                list.add(userID);
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting the list of user IDs", e);
        }
        return list;
    }
    
    public static String getRoleID(final String roleName) {
        final String licType = LicenseProvider.getInstance().getLicenseType();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria roleNameCriteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)roleName, 0);
            final Join join = new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 1);
            final Join join2 = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 1);
            if (licType != null && licType.equalsIgnoreCase("Standard")) {
                final Criteria cri = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12);
                sq.addJoin(join);
                sq.addJoin(join2);
                roleNameCriteria = cri.and(roleNameCriteria);
            }
            else if (licType != null && licType.equalsIgnoreCase("TOOLSADDON")) {
                final Criteria cri = new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"T", 12);
                sq.addJoin(join);
                sq.addJoin(join2);
                roleNameCriteria = cri.and(roleNameCriteria);
            }
            sq.setCriteria(roleNameCriteria);
            final Persistence per = SyMUtil.getPersistence();
            final DataObject dataObj = per.get(sq);
            if (dataObj.size("UMRole") > 0) {
                final Row roleRow = dataObj.getRow("UMRole");
                final String roleID = "" + roleRow.get("UM_ROLE_ID");
                return roleID;
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error occured in  getRoleList  method", e);
        }
        return null;
    }
    
    public static boolean isUserInAdminRole(final Long loginID) {
        boolean superUser = false;
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaLogin"));
            final Join userRoleJoin = new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join roleModuleJoin = new Join("UsersRoleMapping", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            final Join moduleRelationJoin = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2);
            final Join aaaRoleJoin = new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            final Criteria roleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"Common_Write", 0);
            final Criteria userCriteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            sq.setCriteria(roleCriteria.and(userCriteria));
            sq.addJoin(userRoleJoin);
            sq.addJoin(roleModuleJoin);
            sq.addJoin(moduleRelationJoin);
            sq.addJoin(aaaRoleJoin);
            sq.addSelectColumn(new Column((String)null, "*"));
            final DataObject adminDO = SyMUtil.getPersistence().get((SelectQuery)sq);
            if (adminDO.size("AaaLogin") > 0) {
                superUser = true;
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while checking super admin by login ID :", e);
        }
        return superUser;
    }
    
    public static ArrayList getLoginIDsForCustomer(final Long customerID) {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        final Set loginIDs = new HashSet();
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaLogin"));
            sq.addSelectColumn(new Column("AaaLogin", "*"));
            if (isMSP && customerID != null) {
                sq.addJoin(new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 2));
                final Criteria customerCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 0, false);
                sq.setCriteria(customerCriteria);
            }
            final DataObject adminDO = SyMUtil.getPersistence().get((SelectQuery)sq);
            if (!adminDO.isEmpty()) {
                final Iterator aaaLoginRows = adminDO.getRows("AaaLogin");
                while (aaaLoginRows.hasNext()) {
                    final Row aaaLoginRow = aaaLoginRows.next();
                    final Long loginID = (Long)aaaLoginRow.get("LOGIN_ID");
                    loginIDs.add(loginID);
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting LoginIDs From AAARoleName :", e);
        }
        return new ArrayList(loginIDs);
    }
    
    public static Long getUserID(final String loginName) throws SyMException {
        try {
            final Criteria cri = new Criteria(Column.getColumn("AaaUser", "FIRST_NAME"), (Object)loginName, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)sq);
            final DataObject dataObject = DataAccess.get(sq);
            final Long userID = (Long)dataObject.getFirstValue("AaaUser", "USER_ID");
            return userID;
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting serviceName for loginName: " + loginName, e);
            throw new SyMException(1002, e);
        }
    }
    
    public static String getUserNameFromUserID(final Long user_id) throws SyMException {
        try {
            final Criteria cri = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)user_id, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(sq);
            final String userName = (String)dataObject.getFirstValue("AaaUser", "FIRST_NAME");
            return userName;
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting serviceName for User id: " + user_id, e);
            throw new SyMException(1002, e);
        }
    }
    
    public static boolean isDefaultAdministratorRole(final String roleId) {
        return roleId == null && isDefaultAdministratorRole(Long.valueOf(roleId));
    }
    
    public static boolean isDefaultAdministratorRole(final Long roleId) {
        boolean isDefaultAdministratorRole = false;
        try {
            final Criteria criteria = new Criteria(new Column("UMRole", "UM_ROLE_ID"), (Object)roleId, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("UMRole", criteria);
            final Row roleRow = dataObject.getRow("UMRole");
            final String roleName = (String)roleRow.get("UM_ROLE_NAME");
            if (DMUserHandler.defaultAdminRoles.contains(roleName)) {
                isDefaultAdministratorRole = true;
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting role properties of user", e);
        }
        return isDefaultAdministratorRole;
    }
    
    public static String getCustomerIdListForRole(final String role_ID, final String selectedCustomerIDS) throws Exception {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            if (role_ID != null) {
                final boolean isDefaultAdministratorRole = isDefaultAdministratorRole(Long.valueOf(role_ID));
                if (isDefaultAdministratorRole) {
                    final StringBuffer customeridsSbf = new StringBuffer();
                    final Long[] customerids = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
                    for (int length = customerids.length, i = 0; i < length; ++i) {
                        customeridsSbf.append(customerids[i]);
                        if (i < length - 1) {
                            customeridsSbf.append(",");
                        }
                    }
                    return customeridsSbf.toString();
                }
            }
            return selectedCustomerIDS;
        }
        final String defaulCustomerId = CustomerInfoUtil.getInstance().getDefaultCustomer() + "";
        return defaulCustomerId;
    }
    
    public static ArrayList<Hashtable> getUserListForRole(final String roleName) {
        final Criteria criteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)roleName, 0);
        final ArrayList<Hashtable> usersList = getDCUsers(criteria);
        return usersList;
    }
    
    public static ArrayList<Hashtable> getDefaultAdministratorRoleUserList() {
        final Criteria criteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)DMUserHandler.defaultAdminRoles.toArray(), 8);
        final ArrayList<Hashtable> usersList = getDCUsers(criteria);
        return usersList;
    }
    
    public static ArrayList<Long> getAdminRoleUserIds() {
        final ArrayList<Long> userIDs = new ArrayList<Long>();
        try {
            for (final Hashtable adminLoginID : getDefaultAdministratorRoleUserList()) {
                userIDs.add(adminLoginID.get("USER_ID"));
            }
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.WARNING, "Exception getLoggedInAndAdminRoleUserIds() : " + ex);
        }
        return userIDs;
    }
    
    public static boolean isOtherAdministratorAvailable() {
        final ArrayList<Long> usersList = getAdminRoleUserIds();
        return usersList.size() > 1;
    }
    
    public static int getDefaultTechCount() {
        int technicianCount = 2;
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equals("T")) {
            technicianCount = 2;
        }
        return technicianCount;
    }
    
    public static int getADUserCount() {
        int adUserCount = 0;
        try {
            final Criteria criteria = new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)"-", 1);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
            adUserCount = (dataObject.isEmpty() ? adUserCount : dataObject.size("AaaLogin"));
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting aduser count ", e);
        }
        return adUserCount;
    }
    
    public static ArrayList getLoginIDsForAAARoleName(final String aaaRoleName) {
        final ArrayList loginIDs = new ArrayList();
        try {
            final DataObject adminDO = getLoginDOForAAARoleName(aaaRoleName, null);
            if (adminDO != null && !adminDO.isEmpty()) {
                final Iterator aaaAccountRows = adminDO.getRows("AaaAccount");
                while (aaaAccountRows.hasNext()) {
                    final Row aaaAccountRow = aaaAccountRows.next();
                    final Long loginID = (Long)aaaAccountRow.get("LOGIN_ID");
                    loginIDs.add(loginID);
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting LoginIDs From AAARoleName :", e);
        }
        return loginIDs;
    }
    
    public static DataObject getLoginDOForAAARoleName(final String aaaRoleName, final Criteria criteria) {
        DataObject loginDO = null;
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaRole"));
            final Join aaaAuthorizedRoleJoin = new Join("AaaRole", "AaaAuthorizedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            final Join aaaAccountJoin = new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            sq.addJoin(aaaAuthorizedRoleJoin);
            sq.addJoin(aaaAccountJoin);
            sq.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
            sq.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
            Criteria aaaRoleNameCrit = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)aaaRoleName, 0, false);
            if (criteria != null) {
                aaaRoleNameCrit = aaaRoleNameCrit.and(criteria);
            }
            sq.setCriteria(aaaRoleNameCrit);
            DMUserHandler.logger.log(Level.FINE, "getLoginIDsForAAARoleName query : " + RelationalAPI.getInstance().getSelectSQL((Query)sq));
            loginDO = SyMUtil.getPersistence().get((SelectQuery)sq);
            if (loginDO.isEmpty()) {
                return null;
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting LoginIDs From AAARoleName :", e);
        }
        return loginDO;
    }
    
    public static ArrayList getLoginIDsForAAARoleName(final String aaaRoleName, final Long customerID) {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        final HashSet<Long> loginIDs = new HashSet<Long>();
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaRole"));
            final Join aaaAuthorizedRoleJoin = new Join("AaaRole", "AaaAuthorizedRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            final Join aaaAccountJoin = new Join("AaaAuthorizedRole", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            sq.addJoin(aaaAuthorizedRoleJoin);
            sq.addJoin(aaaAccountJoin);
            sq.addSelectColumn(new Column("AaaAccount", "*"));
            Criteria aaaRoleNameCrit = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)aaaRoleName, 0, false);
            if (isMSP) {
                sq.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                sq.addJoin(new Join("AaaLogin", "LoginUserCustomerMapping", new String[] { "USER_ID" }, new String[] { "DC_USER_ID" }, 2));
                aaaRoleNameCrit = aaaRoleNameCrit.and(new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerID, 0, false));
            }
            sq.setCriteria(aaaRoleNameCrit);
            DMUserHandler.logger.log(Level.FINEST, "getLoginIDsForAAARoleName with customer id query : " + RelationalAPI.getInstance().getSelectSQL((Query)sq));
            final DataObject adminDO = SyMUtil.getPersistence().get((SelectQuery)sq);
            if (!adminDO.isEmpty()) {
                final Iterator aaaAccountRows = adminDO.getRows("AaaAccount");
                while (aaaAccountRows.hasNext()) {
                    final Row aaaAccountRow = aaaAccountRows.next();
                    final Long loginID = (Long)aaaAccountRow.get("LOGIN_ID");
                    loginIDs.add(loginID);
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting LoginIDs From AAARoleName :", e);
        }
        return new ArrayList((Collection<? extends E>)loginIDs);
    }
    
    public static boolean isUserInRole(final Long loginID, final String roleName) {
        boolean userRoleStatus = false;
        try {
            final Criteria aaaLoginIDCrit = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)loginID, 0, false);
            final DataObject adminDO = getLoginDOForAAARoleName(roleName, aaaLoginIDCrit);
            if (adminDO != null && !adminDO.isEmpty()) {
                userRoleStatus = Boolean.TRUE;
            }
            else {
                userRoleStatus = Boolean.FALSE;
            }
            DMUserHandler.logger.log(Level.FINE, "USERROLESTATUS" + userRoleStatus);
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while checking user in role by given login ID :", e);
        }
        return userRoleStatus;
    }
    
    public static HashMap<String, String> getContactInfo(final Long userID) {
        final HashMap<String, String> contactInfoMap = new HashMap<String, String>();
        try {
            final DataObject dataObject = getContactInfoDO(userID);
            if (!dataObject.isEmpty()) {
                final Iterator aaaContactInfoRows = dataObject.getRows("AaaContactInfo");
                while (aaaContactInfoRows.hasNext()) {
                    final Row row = aaaContactInfoRows.next();
                    final String emailID = (String)row.get("EMAILID");
                    final String phone = (String)row.get("LANDLINE");
                    contactInfoMap.put("EMAILID", emailID);
                    contactInfoMap.put("LANDLINE", phone);
                }
            }
        }
        catch (final Exception exp) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while get  the getContactInfoProp :", exp);
        }
        return contactInfoMap;
    }
    
    public static Properties getContactInfoProp(final Long userID) {
        final Properties prop = new Properties();
        try {
            final DataObject dataObject = getContactInfoDO(userID);
            if (!dataObject.isEmpty()) {
                final Iterator aaaContactInfoRows = dataObject.getRows("AaaContactInfo");
                while (aaaContactInfoRows.hasNext()) {
                    final Row row = aaaContactInfoRows.next();
                    final String emailID = (String)row.get("EMAILID");
                    final Long contactInfoID = (Long)row.get("CONTACTINFO_ID");
                    ((Hashtable<String, String>)prop).put("EMAIL_ID", emailID);
                    ((Hashtable<String, Long>)prop).put("contactInfoID", contactInfoID);
                }
            }
        }
        catch (final Exception exp) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while get  the getContactInfoProp :", exp);
        }
        return prop;
    }
    
    public static boolean isUserManagingAllComputers(final Long loginID) {
        return isUserInRole(loginID, "All_Managed_Computer");
    }
    
    public static Long getDCSystemUserId() {
        Criteria criteria = null;
        Long userID = null;
        try {
            criteria = new Criteria(new Column("AaaUser", "FIRST_NAME"), (Object)EventConstant.DC_SYSTEM_USER, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaUser", criteria);
            if (!dataObject.isEmpty()) {
                final Row loginrow = dataObject.getRow("AaaUser");
                userID = (Long)loginrow.get("USER_ID");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting Help desk user details", e);
        }
        return userID;
    }
    
    public static DataObject getContactInfoDO(final Long userID) {
        DataObject dataObject = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            selectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            selectQuery.addSelectColumn(new Column("AaaContactInfo", "*"));
            final Criteria criteria = new Criteria(new Column("AaaUser", "USER_ID"), (Object)userID, 0);
            selectQuery.setCriteria(criteria);
            dataObject = SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in getContactInfoDO", ex);
        }
        return dataObject;
    }
    
    public static SelectQuery getLoginAccountQuery() {
        SelectQuery selectQuery = null;
        try {
            selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            final Join aaaUserJoin = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            selectQuery.addJoin(aaaUserJoin);
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(new Column("AaaAccount", "ACCOUNT_ID"));
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in handling getDCUsersRoleQuery", e);
            e.printStackTrace();
        }
        return selectQuery;
    }
    
    public void addorUpdateContactEmail(final Long userID, final String email) {
        try {
            final DataObject dataObject = getContactInfoDO(userID);
            Row contactRow = null;
            if (!dataObject.isEmpty()) {
                contactRow = dataObject.getRow("AaaContactInfo");
                contactRow.set("EMAILID", (Object)email);
                dataObject.updateRow(contactRow);
                SyMUtil.getPersistence().update(dataObject);
            }
            else {
                contactRow = new Row("AaaContactInfo");
                contactRow.set("EMAILID", (Object)email);
                dataObject.addRow(contactRow);
                SyMUtil.getPersistence().add(dataObject);
            }
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in addorUpdateContactInfo", ex);
        }
    }
    
    public static List getSupportedLangList() {
        try {
            final List langList = DBUtil.getDistinctColumnValue("AaaUserProfile", "LANGUAGE_CODE", null);
            return langList;
        }
        catch (final Exception ex) {
            Logger.getLogger(DMUserHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static Locale getUserLocaleFromCache() throws Exception {
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        return (Locale)ApiFactoryProvider.getCacheAccessAPI().getCache(userID + "_" + "USERLOCALE", 3);
    }
    
    public static Locale getUserLocaleFromDB() throws Exception {
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        return getUserLocaleFromDB(userID);
    }
    
    public static Locale getUserLocaleFromDB(final Long userID) throws Exception {
        Locale locale = Locale.getDefault();
        if (userID != null) {
            DMUserHandler.logger.log(Level.INFO, "DBQUERY happened for getUserLocaleFromDB method and argument is : " + userID);
            final Persistence per = SyMUtil.getPersistence();
            final Criteria c = new Criteria(new Column("AaaUserProfile", "USER_ID"), (Object)userID, 0);
            final DataObject dataObject = per.get("AaaUserProfile", c);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("AaaUserProfile");
                final String language = row.get("LANGUAGE_CODE").toString();
                final String country = row.get("COUNTRY_CODE").toString();
                locale = new Locale(language, country);
            }
        }
        return locale;
    }
    
    public static String getUserTimeZoneIDFromCache(final Long userID) throws Exception {
        return (String)ApiFactoryProvider.getCacheAccessAPI().getCache(userID + "_" + "USERTIMEZONEID", 3);
    }
    
    public static String getUserTimeZoneIDFromCache() throws Exception {
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        return getUserTimeZoneIDFromCache(userID);
    }
    
    public static String getUserTimeZoneIDFromDB() throws Exception {
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        return getUserTimeZoneIDFromDB(userID);
    }
    
    public static String getUserTimeZoneID() throws Exception {
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        return getUserTimeZoneID(userID);
    }
    
    public static String getUserTimeZoneID(final Long userID) throws Exception {
        String userTimeZoneID = TimeZone.getDefault().getID();
        userTimeZoneID = getUserTimeZoneIDFromCache(userID);
        if (userTimeZoneID == null) {
            userTimeZoneID = getUserTimeZoneIDFromDB(userID);
            if (userTimeZoneID != null && userID != null) {
                updateUserDataInCache(userID + "_" + "USERTIMEZONEID", userTimeZoneID);
            }
        }
        return userTimeZoneID;
    }
    
    public static String getUserTimeZoneIDFromDB(final Long userID) throws Exception {
        String userTimeZoneID = TimeZone.getDefault().getID();
        try {
            if (userID != null) {
                DMUserHandler.logger.log(Level.INFO, "DBQUERY happened for getUserTimeZoneIDFromDB and argument is........." + userID);
                final Persistence per = SyMUtil.getPersistence();
                final Criteria c = new Criteria(new Column("AaaUserProfile", "USER_ID"), (Object)userID, 0);
                final DataObject dataObject = per.get("AaaUserProfile", c);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("AaaUserProfile");
                    userTimeZoneID = row.get("TIMEZONE").toString();
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while getting user time zone id...", e);
        }
        return userTimeZoneID;
    }
    
    public static String getUserDateTimeFormatFromCache(final String columnName) {
        return getUserDateTimeFormatFromCache(columnName, null);
    }
    
    public static String getUserDateTimeFormatFromCache(final String columnName, Long userID) {
        String value = null;
        try {
            if (userID == null) {
                userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            }
            if (userID != null) {
                value = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(userID + "_" + columnName, 3);
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception in getUserDateTimeFormatFromCache method...", e);
        }
        return value;
    }
    
    public static String getUserDateTimeFormatFromDB(final String columnName) {
        return getUserDateTimeFormatFromDB(columnName, null);
    }
    
    public static String getUserDateTimeFormatFromDB(final String columnName, Long userID) {
        String userFormat = null;
        try {
            final Persistence per = SyMUtil.getPersistence();
            if (userID == null) {
                userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            }
            if (userID != null) {
                DMUserHandler.logger.log(Level.FINE, "DBQUERY happened for getUserDateTimeFormatFromDB method and argument is........." + columnName);
                final Criteria c = new Criteria(new Column("UserSettings", "USER_ACCOUNT_ID"), (Object)userID, 0);
                final DataObject dataObject = per.get("UserSettings", c);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("UserSettings");
                    userFormat = row.get(columnName).toString();
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while getting user date and time format...", e);
        }
        return userFormat;
    }
    
    public static void updateUserDataInCache(final String key, final Object value) {
        DMUserHandler.logger.log(Level.FINE, "Going to update the cache with paramenter..." + key + " and " + value);
        try {
            ApiFactoryProvider.getCacheAccessAPI().putCache(key, value, 3);
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while updating cache...", e);
        }
    }
    
    public static String getUserTimeFormat() {
        final String userTimeFormat = getUserDateTimeFormat("TIMEFORMAT");
        DMUserHandler.logger.log(Level.FINEST, "return value from userTimeFormat : " + userTimeFormat);
        return userTimeFormat;
    }
    
    public static String getUserDateTimeFormat(final String columnName) {
        return getUserDateTimeFormat(columnName, null);
    }
    
    public static String getUserDateTimeFormat(final String columnName, Long userID) {
        String userFormat = "";
        try {
            userFormat = getUserDateTimeFormatFromCache(columnName, userID);
            if (userFormat == null) {
                DMUserHandler.logger.log(Level.FINE, "user formate is null going to fetch from db for..." + columnName);
                userFormat = getUserDateTimeFormatFromDB(columnName, userID);
                if (userFormat == null) {
                    userFormat = (columnName.equalsIgnoreCase("DATEFORMAT") ? SyMUtil.getDefaultDateFormat() : SyMUtil.getDefaultTimeFormat());
                }
                if (userID == null) {
                    userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                }
                if (userID != null) {
                    updateUserDataInCache(userID + "_" + columnName, userFormat);
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while getting user date and time format...", e);
        }
        return userFormat;
    }
    
    public static String getUserDateFormat() {
        DMUserHandler.logger.log(Level.FINE, "getUserDateFormat method is called...");
        final String userDateFormat = getUserDateTimeFormat("DATEFORMAT");
        return userDateFormat;
    }
    
    public static String getUsersDateFormat(final String columnName) {
        DMUserHandler.logger.log(Level.FINE, "getDateFormat method is called..." + columnName);
        String dateFormat = "";
        try {
            final Persistence per = SyMUtil.getPersistence();
            final Criteria criteria = new Criteria(new Column("DefaultTimeFormat", "TIME_FORMAT"), (Object)columnName, 0);
            final DataObject dataObject = per.get("DefaultTimeFormat", criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("DefaultTimeFormat");
                dateFormat = row.get("DATE_FORMAT").toString();
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while getDateFormat...", e);
        }
        if (dateFormat.isEmpty()) {
            dateFormat = SyMUtil.getDefaultDateFormat();
        }
        return dateFormat;
    }
    
    public static TreeMap<String, Long> getAuthorizedRolesForAccId(final Long accId) {
        final TreeMap<String, Long> rowMap = new TreeMap<String, Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaAuthorizedRole"));
            selectQuery.addSelectColumn(new Column("AaaAuthorizedRole", "ROLE_ID"));
            selectQuery.addSelectColumn(new Column("AaaRole", "NAME"));
            selectQuery.addSelectColumn(new Column("AaaRole", "ROLE_ID"));
            selectQuery.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)accId, 0));
            final DataObject dObj = SyMUtil.getPersistence().get(selectQuery);
            if (!dObj.isEmpty()) {
                final Iterator rowItr = dObj.getRows("AaaRole");
                while (rowItr.hasNext()) {
                    final Row objRow = rowItr.next();
                    if (objRow != null) {
                        rowMap.put((String)objRow.get("NAME"), (Long)objRow.get("ROLE_ID"));
                    }
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in getting Authorized roles for acc id", e);
            e.printStackTrace();
        }
        return rowMap;
    }
    
    public static HashMap<String, HashMap> getLoginDataForUser(final String userName, String domainName) {
        final HashMap<String, HashMap> loginData = new HashMap<String, HashMap>();
        DataObject loginDO = getLoginDO(userName, domainName);
        if (loginDO != null && !loginDO.isEmpty()) {
            try {
                final HashMap userMap = new HashMap();
                final Row loginRow = loginDO.getRow("AaaLogin");
                userMap.put("user_id", loginRow.get("USER_ID"));
                userMap.put("user_name", loginRow.get("NAME"));
                domainName = (String)loginRow.get("DOMAINNAME");
                userMap.put("auth_type", (domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) ? "Local Authentication" : "AD Authentication");
                final Row contactInfoRow = loginDO.getRow("AaaContactInfo");
                if (contactInfoRow != null) {
                    userMap.put("email", contactInfoRow.get("EMAILID"));
                    userMap.put("phone_number", contactInfoRow.get("MOBILE"));
                }
                else {
                    userMap.put("email", "");
                    userMap.put("phone_number", "");
                }
                loginData.put("user_data", userMap);
                final HashMap permissionMap = new HashMap();
                final Row accRow = loginDO.getRow("AaaAccount");
                final Long accId = (Long)accRow.get("ACCOUNT_ID");
                final Map<String, Long> authorizedRoleMap = getAuthorizedRolesForAccId(accId);
                final List readPriv = new ArrayList();
                final List writePriv = new ArrayList();
                final List adminPriv = new ArrayList();
                for (final Map.Entry<String, Long> roleEntry : authorizedRoleMap.entrySet()) {
                    final String roleName = roleEntry.getKey();
                    if (roleName.endsWith("Read")) {
                        readPriv.add(roleName);
                    }
                    else if (roleName.endsWith("Write")) {
                        writePriv.add(roleName);
                    }
                    else {
                        if (!roleName.endsWith("Admin")) {
                            continue;
                        }
                        adminPriv.add(roleName);
                    }
                }
                permissionMap.put("read", readPriv);
                permissionMap.put("write", writePriv);
                permissionMap.put("admin", adminPriv);
                loginData.put("user_permissions", permissionMap);
                final HashMap authMap = new HashMap();
                Row apiKeyDefnRow = loginDO.getRow("APIKeyDetails", new Criteria(new Column("APIKeyDetails", "SERVICE_TYPE"), (Object)"301", 0));
                if (apiKeyDefnRow == null) {
                    addOrUpdateAPIKeyForLoginId(Long.parseLong(String.valueOf(loginRow.get("LOGIN_ID"))));
                    loginDO = getLoginDO(userName, domainName);
                    apiKeyDefnRow = loginDO.getRow("APIKeyDetails");
                }
                authMap.put("auth_token", Base64.decodeAsString(apiKeyDefnRow.get("APIKEY") + ""));
                loginData.put("auth_data", authMap);
            }
            catch (final DataAccessException ex) {
                DMUserHandler.logger.log(Level.SEVERE, "Exception in getting the login data for user.", (Throwable)ex);
            }
        }
        return loginData;
    }
    
    public static DataObject getLoginDO(final String userName, String domainName) {
        domainName = ((domainName == null || domainName.equalsIgnoreCase("null")) ? "-" : domainName);
        Criteria criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)userName, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0));
        return getLoginDO(criteria);
    }
    
    public static DataObject getLoginDO(final Criteria criteria) {
        try {
            SelectQuery selectQuery = getLoginUserDetailsQuery();
            selectQuery.setCriteria(criteria);
            selectQuery = ApiFactoryProvider.getAuthUtilAccessAPI().getActiveUsersCriteria(selectQuery);
            final DataObject loginDO = DataAccess.get(selectQuery);
            return loginDO;
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in handling getLoginDO", ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static SelectQuery getLoginUserDetailsQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "*"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "*"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyDetails", "*"));
        final Join aaaUserJoin = new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        final Join aaaAccountJoin = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
        final Join userContanctInfoJoin = new Join("AaaLogin", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1);
        final Join contactDetailsJoin = new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 1);
        final Join apiKeyDefnJoin = new Join("AaaLogin", "APIKeyDetails", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 1);
        selectQuery.addJoin(aaaUserJoin);
        selectQuery.addJoin(aaaAccountJoin);
        selectQuery.addJoin(userContanctInfoJoin);
        selectQuery.addJoin(contactDetailsJoin);
        selectQuery.addJoin(apiKeyDefnJoin);
        return selectQuery;
    }
    
    public static void addOrUpdateAPIKeyForLoginId(final Long loginId) {
        final Properties apiProps = new Properties();
        try {
            apiProps.setProperty("apiKey", AuthenticationKeyUtil.getInstance().generateTechAPIKey());
            apiProps.setProperty("loginID", String.valueOf(loginId));
            apiProps.setProperty("SERVICE_TYPE", "301");
            apiProps.setProperty("SCOPE", "REST API");
            AuthenticationKeyUtil.getInstance().addOrUpdateAPIKey(apiProps);
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateAPIKey for login Id " + loginId, ex);
        }
    }
    
    public static List getRoleNameListForLoginUser(final Long loginId) {
        List roleNameList = new ArrayList();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("UsersRoleMapping"));
            final Join umRoleModuleRelationJoin = new Join("UsersRoleMapping", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2);
            final Join umModuleJoin = new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2);
            final Join aaaRoleJoin = new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            final Criteria loginIdCriteria = new Criteria(new Column("UsersRoleMapping", "LOGIN_ID"), (Object)loginId, 0);
            sQuery.addJoin(umRoleModuleRelationJoin);
            sQuery.addJoin(umModuleJoin);
            sQuery.addJoin(aaaRoleJoin);
            sQuery.setCriteria(loginIdCriteria);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = DataAccess.get(sQuery);
            if (!dO.isEmpty()) {
                final Iterator rowIter = dO.getRows("AaaRole");
                roleNameList = Arrays.asList(DBUtil.getColumnValues(rowIter, "NAME"));
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception in getRoleNameListForLoginUser", e);
        }
        return roleNameList;
    }
    
    public static void mapEnterpriseRoleForExistingUsers() {
        try {
            final SelectQuery selectQuery = getLoginAccountQuery();
            final Join aaaAccountJoin = new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            selectQuery.addJoin(aaaAccountJoin);
            selectQuery.addSelectColumn(new Column("AaaAuthorizedRole", "*"));
            final DataObject aaaAccountDo = SyMUtil.getPersistence().get(selectQuery);
            DMUserHandler.logger.log(Level.INFO, "Following users are available :" + aaaAccountDo);
            final WritableDataObject writableDO = new WritableDataObject();
            if (!aaaAccountDo.isEmpty()) {
                final List<Long> featureRoleIds = getEnterpriseRoleIds();
                DMUserHandler.logger.log(Level.INFO, "Following feature role ids are available " + featureRoleIds);
                for (final Long featureRoleID : featureRoleIds) {
                    final Iterator aaaAccItr = aaaAccountDo.getRows("AaaAccount");
                    while (aaaAccItr.hasNext()) {
                        final Row accRow = aaaAccItr.next();
                        if (accRow != null) {
                            final Long accountId = (Long)accRow.get("ACCOUNT_ID");
                            final Row accAuthRow = new Row("AaaAuthorizedRole");
                            accAuthRow.set("ACCOUNT_ID", (Object)accountId);
                            accAuthRow.set("ROLE_ID", (Object)featureRoleID);
                            if (aaaAccountDo.getRow("AaaAuthorizedRole", accAuthRow) != null) {
                                continue;
                            }
                            writableDO.addRow(accAuthRow);
                        }
                    }
                }
            }
            if (!writableDO.isEmpty()) {
                DMUserHandler.logger.log(Level.INFO, "going to add Feature role for users :" + writableDO);
                SyMUtil.getPersistence().add((DataObject)writableDO);
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.INFO, "Exception in mapping the PatchEdition role to users ", e);
        }
    }
    
    public static void deleteEnterpriseRoleForExistingUsers() {
        try {
            final List<Long> featureRoleIds = getEnterpriseRoleIds();
            deleteEnterpriseRoleForExistingUsers(featureRoleIds);
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.INFO, "Exception in deleting the PatchEdition role to users ", e);
        }
    }
    
    public static void deleteEnterpriseRoleForExistingUsers(final List<Long> featureRoleIds) throws Exception {
        if (!featureRoleIds.isEmpty()) {
            final Criteria roleCriteria = new Criteria(new Column("AaaAuthorizedRole", "ROLE_ID"), (Object)featureRoleIds.toArray(new Long[featureRoleIds.size()]), 8);
            SyMUtil.getPersistence().delete(roleCriteria);
        }
        DMUserHandler.logger.log(Level.INFO, "Feature role for existing users deleted." + featureRoleIds);
    }
    
    public static List<Long> getEnterpriseRoleIds() throws Exception {
        return getEnterpriseRoleIds(null);
    }
    
    public static List<Long> getEnterpriseRoleIds(final Criteria cri) throws Exception {
        final DataObject featureRoleDObj = getEnterpriseRoleDO(cri);
        return DBUtil.getColumnValuesAsList(featureRoleDObj.getRows("AaaRole"), "ROLE_ID");
    }
    
    private static DataObject getEnterpriseRoleDO(Criteria cri) throws Exception {
        final SelectQuery featureRoleQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaRole"));
        featureRoleQuery.addSelectColumn(new Column("AaaRole", "ROLE_ID"));
        featureRoleQuery.addSelectColumn(new Column("AaaRole", "NAME"));
        featureRoleQuery.addJoin(new Join("AaaRole", "AaaRoleEdition", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 1));
        if (cri != null) {
            cri = cri.and(new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"*_Epr", 2));
        }
        else {
            cri = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)"*_Epr", 2);
        }
        featureRoleQuery.setCriteria(cri);
        final DataObject featureRoleDObj = SyMUtil.getPersistence().get(featureRoleQuery);
        return featureRoleDObj;
    }
    
    public static List<String> getEnterpriseRoleNames(final Criteria criteria) throws Exception {
        final DataObject featureRoleDObj = getEnterpriseRoleDO(criteria);
        return DBUtil.getColumnValuesAsList(featureRoleDObj.getRows("AaaRole"), "NAME");
    }
    
    public void addUser(DataObject addUserDO, final JSONObject addUserJObj) throws Exception {
        final String userName = (String)addUserJObj.get("userName");
        final String emailID = addUserJObj.optString("USER_EMAIL_ID");
        final String loginName = (String)addUserJObj.get("loginName");
        final String phNum = addUserJObj.optString("USER_PH_NO");
        final String domainName = addUserJObj.optString("domainName", "-");
        final String description = addUserJObj.optString("description", (String)null);
        final Long service_id = (Long)addUserJObj.get("service_id");
        final Long accountprofile_id = (Long)addUserJObj.get("accountprofile_id");
        final JSONArray roleIdsArray = (JSONArray)addUserJObj.opt("roleIdsList");
        final List roleIdsList = getIdsFromJsonArray(roleIdsArray);
        addUserDO = this.addUserInAAAUser(addUserDO, userName, description);
        addUserDO = this.addUserInAAALogin(addUserDO, loginName, domainName);
        addUserDO = this.addUserInAAAAccount(addUserDO, service_id, accountprofile_id);
        addUserDO = this.addUserInAAAAuthorizedRole(addUserDO, roleIdsList);
        addUserDO = this.addUserInAAAContactInfo(addUserDO, emailID, phNum);
        addUserDO = this.addUserInAAAUserContactInfo(addUserDO);
        addUserDO = this.addOrUpdateAccountStatusExtnRow(addUserDO, 0, 1);
        if (addUserJObj.has("summaryGroupID")) {
            final Object summaryGroupID = addUserJObj.get("summaryGroupID");
            if (summaryGroupID != null) {
                addUserDO = this.addUserInUserSummarymapping(addUserDO, summaryGroupID);
            }
        }
    }
    
    public DataObject addUserInAAAUser(final DataObject newuser, final String username, final String description) throws DataAccessException {
        final Row users = new Row("AaaUser");
        users.set("FIRST_NAME", (Object)username);
        users.set("CREATEDTIME", (Object)new Date().getTime());
        if (description != null) {
            users.set("DESCRIPTION", (Object)description);
        }
        newuser.addRow(users);
        return newuser;
    }
    
    public DataObject addUserInAAALogin(final DataObject newuser, final String loginName, String domainName) throws DataAccessException {
        final Row login = new Row("AaaLogin");
        login.set("NAME", (Object)loginName);
        login.set("USER_ID", newuser.getFirstValue("AaaUser", 1));
        domainName = ((domainName != null) ? domainName.toLowerCase() : domainName);
        login.set("DOMAINNAME", (Object)domainName);
        newuser.addRow(login);
        return newuser;
    }
    
    public DataObject addUserInAAAAccount(final DataObject newuser, final Long service_id, final Long accountprofile_id) throws DataAccessException {
        final Row accRow = new Row("AaaAccount");
        accRow.set("SERVICE_ID", (Object)service_id);
        accRow.set("ACCOUNTPROFILE_ID", (Object)accountprofile_id);
        accRow.set("LOGIN_ID", newuser.getFirstValue("AaaLogin", "LOGIN_ID"));
        accRow.set("CREATEDTIME", (Object)System.currentTimeMillis());
        newuser.addRow(accRow);
        return newuser;
    }
    
    public DataObject addUserInAAAAuthorizedRole(final DataObject newUser, final List roleIdsList) throws DataAccessException {
        for (int i = 0; i < roleIdsList.size(); ++i) {
            final Long id = roleIdsList.get(i);
            final Row accAuthRow = new Row("AaaAuthorizedRole");
            accAuthRow.set("ACCOUNT_ID", newUser.getFirstValue("AaaAccount", "ACCOUNT_ID"));
            accAuthRow.set("ROLE_ID", (Object)id);
            newUser.addRow(accAuthRow);
        }
        return newUser;
    }
    
    public DataObject addUserInAAAContactInfo(final DataObject newUser, final String emailID, final String phNum) throws DataAccessException {
        final Row accContactInfoRow = new Row("AaaContactInfo");
        accContactInfoRow.set("EMAILID", (Object)emailID);
        accContactInfoRow.set("LANDLINE", (Object)phNum);
        newUser.addRow(accContactInfoRow);
        return newUser;
    }
    
    public DataObject addUserInAAAUserContactInfo(final DataObject newUser) throws DataAccessException {
        final Row accUserContactInfoRow = new Row("AaaUserContactInfo");
        accUserContactInfoRow.set("USER_ID", newUser.getFirstValue("AaaUser", "USER_ID"));
        accUserContactInfoRow.set("CONTACTINFO_ID", newUser.getFirstValue("AaaContactInfo", "CONTACTINFO_ID"));
        newUser.addRow(accUserContactInfoRow);
        return newUser;
    }
    
    public DataObject addUserInUserSummarymapping(final DataObject newUser, final Object summaryGroupID) throws DataAccessException {
        final Row UserSummaryMappingRow = new Row("UserSummaryMapping");
        UserSummaryMappingRow.set("SUMMARYGROUP_ID", summaryGroupID);
        UserSummaryMappingRow.set("LOGIN_ID", newUser.getFirstValue("AaaLogin", "LOGIN_ID"));
        newUser.addRow(UserSummaryMappingRow);
        return newUser;
    }
    
    public DataObject addOrUpdateUserInUserSummaryMapping(final Object loginID, final Object summaryGroupID) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("UserSummaryMapping", "LOGIN_ID"), loginID, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("UserSummaryMapping", criteria);
        if (dataObject.isEmpty()) {
            final Row row = new Row("UserSummaryMapping");
            row.set("SUMMARYGROUP_ID", summaryGroupID);
            row.set("LOGIN_ID", loginID);
            dataObject.addRow(row);
            SyMUtil.getPersistence().add(dataObject);
        }
        else {
            final Row row = dataObject.getFirstRow("UserSummaryMapping");
            row.set("SUMMARYGROUP_ID", summaryGroupID);
            dataObject.updateRow(row);
            SyMUtil.getPersistence().update(dataObject);
        }
        return dataObject;
    }
    
    public void updateUser(final JSONObject modifyUserJObj) throws Exception {
        final Long loginID = (Long)modifyUserJObj.get("loginID");
        final JSONArray roleIdsArray = (JSONArray)modifyUserJObj.opt("roleIdsList");
        final List roleIdsList = getIdsFromJsonArray(roleIdsArray);
        if (modifyUserJObj.optString("roleChanged").equalsIgnoreCase("true")) {
            this.updateAAAAuthorizedRole(loginID, roleIdsList);
            if (modifyUserJObj.has("summaryGroupID")) {
                final Object summaryGroupID = modifyUserJObj.get("summaryGroupID");
                this.addOrUpdateUserInUserSummaryMapping(loginID, summaryGroupID);
            }
        }
    }
    
    public void updateAAAAuthorizedRole(final Long loginID, final List roleIdsList) throws DataAccessException {
        final Criteria c1 = new Criteria(new Column("AaaAccount", "LOGIN_ID"), (Object)loginID, 0);
        final Row aaaAccRow = SyMUtil.getPersistence().get("AaaAccount", c1).getFirstRow("AaaAccount");
        final Long accountId = (Long)((aaaAccRow != null) ? aaaAccRow.get("ACCOUNT_ID") : 0L);
        final Criteria criteria1 = new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)accountId, 0);
        SyMUtil.getPersistence().delete(criteria1);
        final WritableDataObject dobj = new WritableDataObject();
        for (int i = 0; i < roleIdsList.size(); ++i) {
            final Long id = roleIdsList.get(i);
            final Row accAuthRow = new Row("AaaAuthorizedRole");
            accAuthRow.set("ACCOUNT_ID", (Object)accountId);
            accAuthRow.set("ROLE_ID", (Object)id);
            dobj.addRow(accAuthRow);
        }
        SyMUtil.getPersistence().add((DataObject)dobj);
    }
    
    protected static List<Long> getIdsFromJsonArray(final JSONArray jsonArray) throws JSONException {
        final List<Long> arrayList = new ArrayList<Long>();
        try {
            for (int size = jsonArray.length(), i = 0; i < size; ++i) {
                final Long oldList = jsonArray.getLong(i);
                arrayList.add(oldList);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    
    public void deleteUser(final Long loginID, final Long userID) throws DataAccessException, DependentDeletionFailedException, DeletionQueueFailedException {
        final Criteria selectCriteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
        DMUserHandler.logger.log(Level.INFO, " User delete : before aaalogin delete");
        DeletionFramework.asyncPersistenceDependentDataDeletion("AaaLogin", selectCriteria);
        DMUserHandler.logger.log(Level.INFO, " User delete : after aaalogin delete");
        final Criteria crtiteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0);
        SyMUtil.getPersistence().delete(crtiteria);
        DMUserHandler.logger.log(Level.INFO, " User delete : after LOGINUSERCUSTOMERMAPPING delete");
    }
    
    public static List<Long> getRoleIdsFromRoleName(final List roleNameList) throws SQLException {
        final List<Long> roleIdsList = new ArrayList<Long>();
        final Criteria roleIdsCriteria = new Criteria(new Column("AaaRole", "NAME"), (Object)roleNameList.toArray(), 8);
        try {
            final DataObject roleIdsDO = SyMUtil.getPersistence().get("AaaRole", roleIdsCriteria);
            if (roleIdsDO != null) {
                final Iterator roleIdsIterator = roleIdsDO.getRows("AaaRole");
                while (roleIdsIterator.hasNext()) {
                    final Row r = roleIdsIterator.next();
                    roleIdsList.add((Long)r.get("ROLE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while getting registration list", ex);
            throw new SQLException();
        }
        return roleIdsList;
    }
    
    public static Object getSummaryGroupID(final String roleid) {
        Object sgID = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("RoleSummaryGroupMapping"));
            sq.addSelectColumn(new Column((String)null, "*"));
            final Criteria c = new Criteria(Column.getColumn("RoleSummaryGroupMapping", "UM_ROLE_ID"), (Object)roleid, 0);
            sq.setCriteria(c);
            final DataObject dataObj = SyMUtil.getPersistence().get(sq);
            final Row r = dataObj.getFirstRow("RoleSummaryGroupMapping");
            sgID = r.get("SUMMARYGROUP_ID");
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while getting the SummaryGroupID :", e);
        }
        return sgID;
    }
    
    public static void updateUserSettings(final Long userID, final String columnName, final Object columnValue) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("UserSettings", "USER_ACCOUNT_ID"), (Object)userID, 0);
            final DataObject settingsDO = SyMUtil.getPersistence().get("UserSettings", criteria);
            Row userSettings = settingsDO.getRow("UserSettings");
            if (userSettings != null) {
                userSettings.set(columnName, columnValue);
                settingsDO.updateRow(userSettings);
                SyMUtil.getPersistence().update(settingsDO);
            }
            else {
                userSettings = new Row("UserSettings");
                userSettings.set("USER_ACCOUNT_ID", (Object)userID);
                userSettings.set("SESSION_EXPIRY_TIME", (Object)900);
                userSettings.set("SHOW_HELP_CARD", (Object)Boolean.TRUE);
                userSettings.set("REFRESH_TIME", (Object)new Integer(60));
                userSettings.set(columnName, columnValue);
                settingsDO.addRow(userSettings);
                SyMUtil.getPersistence().add(settingsDO);
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Exception while updating user settings :", e);
        }
    }
    
    public static Long getUserID(final Long loginID) throws SyMException {
        try {
            final Criteria cri = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", cri);
            return dataObject.isEmpty() ? null : ((Long)dataObject.getFirstValue("AaaLogin", "USER_ID"));
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.WARNING, "Exception while getting useer Id  for loginID: " + loginID, e);
            throw new SyMException(1002, e);
        }
    }
    
    public static Row setDefaultProperties(final Long loginID) {
        Row userSettingsRow = null;
        try {
            final DataObject settingsDO = SyMUtil.getPersistence().get("UserSettings", new Criteria(Column.getColumn("UserSettings", "USER_ACCOUNT_ID"), (Object)loginID, 0));
            if (settingsDO.isEmpty()) {
                userSettingsRow = new Row("UserSettings");
                userSettingsRow.set("USER_ACCOUNT_ID", (Object)loginID);
                userSettingsRow.set("SESSION_EXPIRY_TIME", (Object)new Integer(900));
                userSettingsRow.set("REFRESH_TIME", (Object)new Integer(60));
                userSettingsRow.set("SHOW_HELP_CARD", (Object)Boolean.TRUE);
                userSettingsRow.set("CONFIGURATION_VIEW", (Object)"myview");
                userSettingsRow.set("THEME", (Object)"sdp-blue");
                settingsDO.addRow(userSettingsRow);
                SyMUtil.getPersistence().add(settingsDO);
            }
            else {
                userSettingsRow = settingsDO.getRow("UserSettings");
            }
        }
        catch (final Exception exp) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in setDefaultProperties for loginID: " + loginID, exp);
        }
        return userSettingsRow;
    }
    
    public static String getUserName(final Long loginID) {
        String userName = null;
        try {
            final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final SelectQuery selectUserName = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            selectUserName.addSelectColumn(new Column("AaaUser", "*"));
            selectUserName.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectUserName.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectUserName);
            if (!dataObject.isEmpty()) {
                final Row userRow = dataObject.getRow("AaaUser");
                userName = (String)userRow.get("FIRST_NAME");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting user name");
            e.printStackTrace();
        }
        return userName;
    }
    
    public static String getUserEmailID(final Long userID) {
        String emailID = null;
        try {
            final DataObject dataObject = getUserContactDO(userID);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row contactInfoRow = dataObject.getRow("AaaContactInfo");
                emailID = (String)contactInfoRow.get("EMAILID");
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting user email : " + e);
        }
        return emailID;
    }
    
    public static DataObject getUserContactDO(final Long userID) throws DataAccessException {
        final SelectQuery selectUserEmail = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
        final Criteria criteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userID, 0);
        selectUserEmail.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectUserEmail.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectUserEmail.addSelectColumn(new Column("AaaContactInfo", "*"));
        selectUserEmail.setCriteria(criteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectUserEmail);
        return dataObject;
    }
    
    public static HashMap getUserContactProps(final Long userID, final HashMap moreUserContactDetails) throws Exception {
        final HashMap userContactInfo = getUserContactProps(userID);
        if (moreUserContactDetails != null && !moreUserContactDetails.isEmpty()) {
            userContactInfo.putAll(moreUserContactDetails);
        }
        return userContactInfo;
    }
    
    public static HashMap getUserContactProps(final Long userID) throws Exception {
        final HashMap userContactProps = new HashMap();
        try {
            final DataObject dataObject = getUserContactDO(userID);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row contactInfoRow = dataObject.getRow("AaaContactInfo");
                final List columnsList = contactInfoRow.getColumns();
                for (int i = 1; i < columnsList.size(); ++i) {
                    final String columnName = columnsList.get(i);
                    final String data = (String)contactInfoRow.get(columnName);
                    if (data != null && !data.equalsIgnoreCase("")) {
                        if (!SyMUtil.getUserParameter(userID, "isEmailExistForOtherUser").equalsIgnoreCase("true") || !columnName.equalsIgnoreCase("EMAILID")) {
                            userContactProps.put(columnName, data.toLowerCase());
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            DMUserHandler.logger.log(Level.SEVERE, "Error in getting user Contact Props : " + e);
        }
        return userContactProps;
    }
    
    public static Long getLoginScope(final Long loginID) throws DataAccessException {
        final Row loginExtnRow = new Row("DCLoginExtn");
        loginExtnRow.set("LOGIN_ID", (Object)loginID);
        final DataObject dobj = SyMUtil.getPersistence().get("DCLoginExtn", loginExtnRow);
        if (dobj.isEmpty()) {
            return 0L;
        }
        final Row loginScope = dobj.getFirstRow("DCLoginExtn");
        return (Long)loginScope.get("SCOPE");
    }
    
    public static Map<String, Object> getLoginDetails(final Long loginID) {
        final Map<String, Object> loginDetails = new HashMap<String, Object>();
        final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
        final DataObject loginDO = getLoginDO(criteria);
        try {
            if (loginDO != null && !loginDO.isEmpty()) {
                final Row loginRow = loginDO.getFirstRow("AaaLogin");
                String domainName = (String)loginRow.get("DOMAINNAME");
                domainName = ((domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) ? null : domainName);
                loginDetails.put("NAME", loginRow.get("NAME"));
                loginDetails.put("DOMAINNAME", domainName);
                loginDetails.put("USER_ID", loginRow.get("USER_ID"));
                loginDetails.put("LOGIN_ID", loginRow.get("LOGIN_ID"));
            }
        }
        catch (final Exception ex) {
            DMUserHandler.logger.log(Level.WARNING, "Exception in getLoginDetails", ex);
        }
        return loginDetails;
    }
    
    public User constructDCUserObject(DataObject loginDO) {
        User user = null;
        if (loginDO != null && !loginDO.isEmpty()) {
            user = new User();
            try {
                final Row loginRow = loginDO.getRow("AaaLogin");
                final String userName = (String)loginRow.get("NAME");
                final String domainName = (String)loginRow.get("DOMAINNAME");
                final String displayName = CustomerInfoUtil.isSAS() ? ApiFactoryProvider.getUtilAccessAPI().getUserNameForCurrentUser() : userName;
                user.setUserID((Long)loginRow.get("USER_ID"));
                user.setLoginID((Long)loginRow.get("LOGIN_ID"));
                user.setName(userName);
                user.setDisplayName(displayName);
                final Row loginExtnRow = loginDO.getRow("DCLoginExtn");
                if (loginExtnRow != null) {
                    user.setScope((Long)loginExtnRow.get("SCOPE"));
                }
                else {
                    user.setScope(0L);
                }
                final boolean isLocalAuth = domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-");
                user.setDomainName(isLocalAuth ? null : domainName);
                user.setAuthType(isLocalAuth ? "Local Authentication" : "AD Authentication");
                final Row contactInfoRow = loginDO.getRow("AaaContactInfo");
                if (contactInfoRow != null) {
                    user.setEmail((String)contactInfoRow.get("EMAILID"));
                    user.setPhoneNumber((String)contactInfoRow.get("MOBILE"));
                }
                else {
                    user.setEmail("");
                    user.setPhoneNumber("");
                }
                final HashMap permissionMap = new HashMap();
                final Row accRow = loginDO.getRow("AaaAccount");
                final Long accId = (Long)accRow.get("ACCOUNT_ID");
                final Map<String, Long> authorizedRoleMap = getAuthorizedRolesForAccId(accId);
                final List readPriv = new ArrayList();
                final List writePriv = new ArrayList();
                final List adminPriv = new ArrayList();
                final List generalPriv = new ArrayList();
                for (final Map.Entry<String, Long> roleEntry : authorizedRoleMap.entrySet()) {
                    final String roleName = roleEntry.getKey();
                    if (roleName.endsWith("Read")) {
                        readPriv.add(roleName);
                    }
                    else if (roleName.endsWith("Write")) {
                        writePriv.add(roleName);
                    }
                    else if (roleName.endsWith("Admin")) {
                        adminPriv.add(roleName);
                    }
                    else {
                        generalPriv.add(roleName);
                    }
                }
                permissionMap.put("read", readPriv);
                permissionMap.put("write", writePriv);
                permissionMap.put("admin", adminPriv);
                permissionMap.put("general", generalPriv);
                user.setRoles(permissionMap);
                Row apiKeyDefnRow = loginDO.getRow("APIKeyDetails", new Criteria(new Column("APIKeyDetails", "SERVICE_TYPE"), (Object)"301", 0));
                if (apiKeyDefnRow == null) {
                    addOrUpdateAPIKeyForLoginId(Long.parseLong(String.valueOf(loginRow.get("LOGIN_ID"))));
                    loginDO = getLoginDO(userName, domainName);
                    apiKeyDefnRow = loginDO.getRow("APIKeyDetails");
                }
                user.setAuthToken(Base64.decodeAsString(apiKeyDefnRow.get("APIKEY") + ""));
                user.setTimeFormat(SyMUtil.getUserTimeFormat(user.getUserID()));
                user.setDateFormat(SyMUtil.getUserDateTimeFormat(user.getUserID(), "DATEFORMAT"));
                user.setUserLocale(SyMUtil.getUserLocale());
                user.setUserTimeZone(SyMUtil.getUserTimeZone());
            }
            catch (final DataAccessException ex) {
                user = null;
                DMUserHandler.logger.log(Level.SEVERE, "Exception in getting the login data for user.", (Throwable)ex);
            }
        }
        return user;
    }
    
    public static boolean isAllProbeUser(final Long loginId) {
        return isUserInRole(loginId, "All_Managed_Probes");
    }
    
    public DataObject addOrUpdateAccountStatusExtnRow(final DataObject addUserDO, final Integer status, final Integer remarks) throws DataAccessException {
        final Row aaaAccountRow = addUserDO.getRow("AaaAccount");
        Row accountStatusExtn = addUserDO.getRow("AaaAccountStatusExtn");
        if (accountStatusExtn == null) {
            accountStatusExtn = new Row("AaaAccountStatusExtn");
            accountStatusExtn.set("ACCOUNT_ID", aaaAccountRow.get("ACCOUNT_ID"));
            accountStatusExtn.set("STATUS", (Object)status);
            accountStatusExtn.set("REMARKS", (Object)remarks);
            addUserDO.addRow(accountStatusExtn);
        }
        else {
            if (status != null) {
                accountStatusExtn.set("STATUS", (Object)status);
            }
            if (remarks != null) {
                accountStatusExtn.set("REMARKS", (Object)remarks);
            }
            addUserDO.updateRow(accountStatusExtn);
        }
        return addUserDO;
    }
    
    static {
        DMUserHandler.logger = Logger.getLogger("UserManagementLogger");
        USER_HANDLING_LOCK = new Integer(1);
        DMUserHandler.defaultAdminRoles = new ArrayList<String>();
        if (SyMUtil.isProbeServer()) {
            DMUserHandler.defaultAdminRoles.add("Administrator");
            DMUserHandler.defaultAdminRoles.add("Summary Server Admin");
        }
        else if (SyMUtil.isSummaryServer()) {
            DMUserHandler.defaultAdminRoles.add("Summary Server Admin");
        }
        else {
            DMUserHandler.defaultAdminRoles.add("Administrator");
        }
    }
}
