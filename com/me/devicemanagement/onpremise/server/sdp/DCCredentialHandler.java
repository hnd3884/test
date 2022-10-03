package com.me.devicemanagement.onpremise.server.sdp;

import com.adventnet.authentication.util.AuthUtil;
import java.util.Locale;
import java.util.TimeZone;
import com.adventnet.authentication.Credential;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class DCCredentialHandler
{
    private static Logger logger;
    private static String serviceName;
    public static Map<String, ArrayList> localUserRoleMap;
    public static Map<String, Map> domainToUserRoleMap;
    
    public static void init() {
        try {
            DCCredentialHandler.localUserRoleMap = new HashMap<String, ArrayList>();
            DCCredentialHandler.domainToUserRoleMap = new HashMap<String, Map>();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaService"));
            final String[] serviceIdCol = { "SERVICE_ID" };
            query.addJoin(new Join("AaaService", "AaaRole", serviceIdCol, serviceIdCol, 2));
            final String[] roleIdCol = { "ROLE_ID" };
            query.addJoin(new Join("AaaRole", "AaaAuthorizedRole", roleIdCol, roleIdCol, 1));
            query.addSelectColumn(new Column((String)null, "*"));
            query.setCriteria(new Criteria(new Column("AaaService", "NAME"), (Object)new String("System"), 0));
            final DataObject roleDO = DataAccess.get(query);
            final SelectQuery query2 = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            final String[] loginIdCol = { "LOGIN_ID" };
            query2.addJoin(new Join("AaaLogin", "AaaAccount", loginIdCol, loginIdCol, 2));
            query2.addSelectColumn(new Column((String)null, "*"));
            final DataObject loginDO = DataAccess.get(query2);
            final Iterator<Row> loginRows = loginDO.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final ArrayList<String> defUserRoles = new ArrayList<String>();
                final Row loginRow = loginRows.next();
                final long loginID = (long)loginRow.get("LOGIN_ID");
                final String loginName = (String)loginRow.get("NAME");
                final String domainName = (String)loginRow.get("DOMAINNAME");
                final Criteria c1 = new Criteria(new Column("AaaAccount", "LOGIN_ID"), (Object)loginID, 0);
                final Iterator<Row> accRows = loginDO.getRows("AaaAccount", c1);
                while (accRows.hasNext()) {
                    final Row accRow = accRows.next();
                    final Long userAccID = (Long)accRow.get("ACCOUNT_ID");
                    final List<Long> userAuthRoles = new ArrayList<Long>();
                    final Criteria c2 = new Criteria(new Column("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)userAccID, 0);
                    final Iterator<Row> authRoleRows = roleDO.getRows("AaaAuthorizedRole", c2);
                    while (authRoleRows.hasNext()) {
                        final Row authRoleRow = authRoleRows.next();
                        final long authRoleId = (long)authRoleRow.get("ROLE_ID");
                        userAuthRoles.add(authRoleId);
                    }
                    final Criteria crit = new Criteria(Column.getColumn("AaaRole", "ROLE_ID"), (Object)userAuthRoles.toArray(), 8);
                    final Iterator<Row> roleRows = roleDO.getRows("AaaRole", crit);
                    while (roleRows.hasNext()) {
                        final Row roleRow = roleRows.next();
                        final long roleID = (long)roleRow.get("ROLE_ID");
                        final String roleName = (String)roleRow.get("NAME");
                        if (userAuthRoles.contains(roleID)) {
                            defUserRoles.add(roleName);
                        }
                    }
                    if (domainName == null || domainName.equalsIgnoreCase("-") || domainName.trim().length() == 0) {
                        DCCredentialHandler.localUserRoleMap.put(loginName, defUserRoles);
                    }
                    else {
                        Map domainUserMap = DCCredentialHandler.domainToUserRoleMap.get(domainName.toLowerCase());
                        if (domainUserMap == null) {
                            domainUserMap = new HashMap();
                        }
                        domainUserMap.put(loginName, defUserRoles);
                        DCCredentialHandler.domainToUserRoleMap.put(domainName.toLowerCase(), domainUserMap);
                    }
                }
            }
        }
        catch (final Exception e) {
            DCCredentialHandler.logger.log(Level.WARNING, "Exception while getting the role:", e);
            e.printStackTrace();
        }
        DCCredentialHandler.logger.log(Level.WARNING, "init() domainToUserRoleMap : \n{0} ", DCCredentialHandler.domainToUserRoleMap);
        DCCredentialHandler.logger.log(Level.WARNING, "init() localUserRoleMap : \n{0} ", DCCredentialHandler.localUserRoleMap);
    }
    
    public static DCHttpRequest handle(final HttpServletRequest request, final Ticket t) {
        Credential cr = (Credential)request.getSession().getAttribute("com.adventnet.authentication.Credential");
        if (cr == null) {
            cr = constructCredential(t);
            if (cr != null) {
                request.getSession().setAttribute("com.adventnet.authentication.Credential", (Object)cr);
            }
        }
        setCredential(cr);
        return new DCHttpRequest(request, t.principal, t.domainName, t.roles, t.properties);
    }
    
    private static Credential constructCredential(final Ticket t) {
        final String loginName = t.principal;
        final String domainName = t.domainName;
        final String roleNames = t.roles;
        Long userId = new Long(0L);
        Long loginId = new Long(0L);
        Long accId = new Long(0L);
        final Long sessionId = new Long(0L);
        DataObject accountDO = null;
        try {
            accountDO = getAccountDO(loginName, domainName, DCCredentialHandler.serviceName);
            if (accountDO == null || accountDO.isEmpty()) {
                return null;
            }
            userId = (Long)accountDO.getFirstValue("AaaLogin", "USER_ID");
            loginId = (Long)accountDO.getFirstValue("AaaLogin", "LOGIN_ID");
            accId = (Long)accountDO.getFirstValue("AaaAccount", "ACCOUNT_ID");
            final String[] roleList = getRolesAsArray(roleNames);
            final ArrayList list = new ArrayList();
            for (int i = 0; i < roleList.length; ++i) {
                list.add(roleList[i]);
            }
            final Credential cr = new Credential(userId, loginId, accId, sessionId, (Long)null, loginName, DCCredentialHandler.serviceName, t.ipaddress, (TimeZone)null, (Locale)null, (List)list, domainName);
            return cr;
        }
        catch (final Exception e) {
            DCCredentialHandler.logger.log(Level.WARNING, "Exception while creating credential..", e);
            return null;
        }
    }
    
    private static String[] getRolesAsArray(final String roleNames) {
        if (roleNames == null) {
            return new String[0];
        }
        return roleNames.split(";");
    }
    
    public static DataObject getAccountDO(final String loginName, final String domainName, final String serviceName) {
        try {
            DataObject accountDobj = null;
            final List<String> accountTables = new ArrayList<String>();
            accountTables.add("AaaLogin");
            accountTables.add("AaaAccount");
            accountTables.add("AaaService");
            Criteria criteria = new Criteria(new Column("AaaService", "NAME"), (Object)serviceName, 0, false);
            criteria = criteria.and(new Criteria(new Column("AaaLogin", "NAME"), (Object)loginName, 0, false));
            criteria = criteria.and(new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
            accountDobj = DataAccess.get((List)accountTables, criteria);
            return accountDobj;
        }
        catch (final Exception e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }
    
    protected static void setCredential(final Object cr) {
        AuthUtil.setUserCredential((Credential)cr);
    }
    
    public static ArrayList getUserRole(final String userName, final String domainName) {
        DCCredentialHandler.logger.log(Level.WARNING, "getUserRole() getting user role for user {0} domainName {1}  ", new Object[] { userName, domainName });
        ArrayList userRole = null;
        if (domainName == null || domainName.equalsIgnoreCase("-") || domainName.trim().equalsIgnoreCase("none")) {
            userRole = DCCredentialHandler.localUserRoleMap.get(userName);
            DCCredentialHandler.logger.log(Level.WARNING, "getUserRole() role for user {0} userRole {1}  ", new Object[] { userName, userRole });
        }
        else {
            final Map userRoleMap = DCCredentialHandler.domainToUserRoleMap.get(domainName.toLowerCase());
            if (userRoleMap != null) {
                userRole = userRoleMap.get(userName);
            }
            DCCredentialHandler.logger.log(Level.WARNING, "getUserRole() role for user {0} domainName {1} userRole {2}  ", new Object[] { userName, domainName, userRole });
        }
        DCCredentialHandler.logger.log(Level.WARNING, "getUserRole() Final user role for user {0} domainName {1} userRole {2}  ", new Object[] { userName, domainName, userRole });
        return userRole;
    }
    
    static {
        DCCredentialHandler.logger = Logger.getLogger("SDPLogger");
        DCCredentialHandler.serviceName = "System";
        DCCredentialHandler.localUserRoleMap = new HashMap<String, ArrayList>();
        DCCredentialHandler.domainToUserRoleMap = new HashMap<String, Map>();
    }
}
