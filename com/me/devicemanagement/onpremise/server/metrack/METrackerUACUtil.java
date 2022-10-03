package com.me.devicemanagement.onpremise.server.metrack;

import java.util.List;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.db.api.RelationalAPI;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import java.util.logging.Logger;

public class METrackerUACUtil
{
    private static Logger logger;
    
    public static Map getUserDetailsMap() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            final Join userStatusJoin = new Join("AaaUser", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join aaacountJoin = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join aaacountStatusJoin = new Join("AaaAccount", "AaaAccountStatusExtn", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            final Join aaauserLinkDetailsJoin = new Join("AaaUser", "AaaUserLinkDetails", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1);
            selectQuery.addJoin(userStatusJoin);
            selectQuery.addJoin(aaacountJoin);
            selectQuery.addJoin(aaacountStatusJoin);
            selectQuery.addJoin(aaauserLinkDetailsJoin);
            final Criteria activeUser = new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0, false);
            selectQuery.setCriteria(activeUser);
            selectQuery.addSelectColumn(new Column("AaaUser", "*"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "*"));
            selectQuery.addSelectColumn(new Column("AaaAccountStatusExtn", "*"));
            selectQuery.addSelectColumn(new Column("AaaUserLinkDetails", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Map userdtlsMap = new HashMap();
            final Criteria activeAccountUser = new Criteria(new Column("AaaAccountStatusExtn", "STATUS"), (Object)0, 0);
            userdtlsMap.put("activeUser", String.valueOf(METrackerUtil.getDOSize(dataObject, "AaaAccountStatusExtn", activeAccountUser)));
            final Criteria inActiveAccountUser = new Criteria(new Column("AaaAccountStatusExtn", "STATUS"), (Object)1, 0);
            userdtlsMap.put("inActiveUser", String.valueOf(METrackerUtil.getDOSize(dataObject, "AaaAccountStatusExtn", inActiveAccountUser)));
            Criteria criteria = new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)"-", 0);
            userdtlsMap.put("localAuthUser", String.valueOf(METrackerUtil.getDOSize(dataObject, "AaaLogin", criteria)));
            criteria = new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)"-", 1);
            userdtlsMap.put("adAuthUser", String.valueOf(METrackerUtil.getDOSize(dataObject, "AaaLogin", criteria)));
            criteria = new Criteria(new Column("AaaUserLinkDetails", "EXPIRY_TIME"), (Object)System.currentTimeMillis(), 7).and(new Criteria(Column.getColumn("AaaUserLinkDetails", "TOKEN_TYPE"), (Object)101, 0));
            userdtlsMap.put("expiredInvite", String.valueOf(METrackerUtil.getDOSize(dataObject, "AaaUserLinkDetails", criteria)));
            criteria = new Criteria(new Column("AaaUserLinkDetails", "EXPIRY_TIME"), (Object)System.currentTimeMillis(), 7).and(new Criteria(Column.getColumn("AaaUserLinkDetails", "TOKEN_TYPE"), (Object)102, 0));
            userdtlsMap.put("expiredForgetPassword", String.valueOf(METrackerUtil.getDOSize(dataObject, "AaaUserLinkDetails", criteria)));
            return userdtlsMap;
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerUACUtil.logger, "METrackerUACUtil", "trackUserDetails", "Exception : ", (Throwable)e);
            return null;
        }
    }
    
    public static Map getUserMailDetailsMap() {
        final Map userMailDetails = new HashMap();
        Properties loginProperties = new Properties();
        loginProperties = METrackerUtil.getMETrackParam(loginProperties, "newUserMailServerFailure", null);
        loginProperties = METrackerUtil.getMETrackParam(loginProperties, "resetPasswordMailServerFailure", null);
        loginProperties = METrackerUtil.getMETrackParam(loginProperties, "resetPasswordInitiated", null);
        if (!loginProperties.isEmpty()) {
            final String newUserMailFailedCount = loginProperties.getProperty("newUserMailServerFailure");
            final String resetPasswordMailFailedCount = loginProperties.getProperty("resetPasswordMailServerFailure");
            final String resetPasswordInitiatedCount = loginProperties.getProperty("resetPasswordInitiated");
            if (newUserMailFailedCount != null) {
                userMailDetails.put("newUserMailServerFailure", Integer.parseInt(newUserMailFailedCount));
            }
            if (resetPasswordMailFailedCount != null) {
                userMailDetails.put("resetPasswordMailServerFailure", Integer.parseInt(resetPasswordMailFailedCount));
            }
            if (resetPasswordInitiatedCount != null) {
                userMailDetails.put("resetPasswordInitiated", Integer.parseInt(resetPasswordInitiatedCount));
            }
        }
        return userMailDetails;
    }
    
    public static int trackuserCreateRoleDetails() {
        int userCreateRoleCount = 0;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UMRole"));
            selectQuery.addSelectColumn(new Column("UMRole", "UM_ROLE_ID"));
            final Criteria criteria = new Criteria(new Column("UMRole", "ADMIN_NAME"), (Object)EventConstant.DC_SYSTEM_USER, 1);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            userCreateRoleCount = METrackerUtil.getDOSize(dataObject, "UMRole");
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerUACUtil.logger, "METrackerUACUtil", "trackSDPUserDetails", "Exception : ", (Throwable)e);
        }
        return userCreateRoleCount;
    }
    
    public static JSONObject getLocalizationDetails() {
        final JSONObject localizationJSON = new JSONObject();
        int nonEnglishUsers = 0;
        Connection conn = null;
        DataSet ds = null;
        try {
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            final List columnsToSelect = new ArrayList();
            final Column langCodeCol = new Column("AaaUserProfile", "LANGUAGE_CODE");
            final Column countryCodeCol = new Column("AaaUserProfile", "COUNTRY_CODE");
            columnsToSelect.add(langCodeCol);
            columnsToSelect.add(countryCodeCol);
            columnsToSelect.add(new Column((String)null, "*").count());
            sq.addSelectColumns(columnsToSelect);
            sq.addJoin(new Join("AaaUserProfile", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            sq.addJoin(new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            sq.setCriteria(new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0, false));
            final List gbcList = new ArrayList();
            gbcList.add(langCodeCol);
            gbcList.add(countryCodeCol);
            final GroupByClause gbc = new GroupByClause(gbcList);
            sq.setGroupByClause(gbc);
            conn = relAPI.getConnection();
            ds = relAPI.executeQuery((Query)sq, conn);
            if (ds != null) {
                while (ds.next()) {
                    final String langCode = (String)ds.getValue(1);
                    final String countryCode = (String)ds.getValue(2);
                    final Integer count = (Integer)ds.getValue(3);
                    if (langCode != null && !langCode.equalsIgnoreCase("en") && count != null) {
                        nonEnglishUsers += count;
                    }
                    localizationJSON.put(langCode + "_" + countryCode, (Object)count);
                }
                localizationJSON.put("nonEnglishUsers", nonEnglishUsers);
            }
        }
        catch (final Exception ex) {
            METrackerUACUtil.logger.log(Level.SEVERE, "Exception in getting the localization details.", ex);
            try {
                ds.close();
                conn.close();
            }
            catch (final Exception ex) {
                METrackerUACUtil.logger.log(Level.SEVERE, "Exception in closing the dataset", ex);
            }
        }
        finally {
            try {
                ds.close();
                conn.close();
            }
            catch (final Exception ex2) {
                METrackerUACUtil.logger.log(Level.SEVERE, "Exception in closing the dataset", ex2);
            }
        }
        return localizationJSON;
    }
    
    static {
        METrackerUACUtil.logger = Logger.getLogger("METrackLog");
    }
}
