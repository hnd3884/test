package com.me.devicemanagement.onpremise.server.authentication;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.TimeZone;
import com.adventnet.authentication.util.AuthDBUtil;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import java.util.Locale;
import java.util.List;
import com.adventnet.authentication.Credential;
import com.adventnet.authentication.util.AuthUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.AuthUtilAccessAPI;

public class AuthUtilAccessImpl implements AuthUtilAccessAPI
{
    private static final Logger LOGGER;
    
    public Long getLoginID() throws Exception {
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            return credential.getLoginId();
        }
        return null;
    }
    
    public Long getAccountID() throws Exception {
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            return credential.getAccountId();
        }
        return null;
    }
    
    public Long getUserID() throws Exception {
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            return credential.getUserId();
        }
        return null;
    }
    
    public String getLoginName() throws Exception {
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            return credential.getLoginName();
        }
        return null;
    }
    
    public String getSystemUserName() {
        final String SYSTEM_USER_NAME = "DC-SYSTEM-USER";
        return SYSTEM_USER_NAME;
    }
    
    public List<String> getRoles() throws Exception {
        final Credential credential = AuthUtil.getUserCredential();
        if (credential != null) {
            return credential.getRoles();
        }
        return null;
    }
    
    public Locale getUserLocale() throws Exception {
        AuthUtilAccessImpl.LOGGER.log(Level.FINE, "getLocale method is called...");
        Locale locale = Locale.getDefault();
        try {
            locale = DMUserHandler.getUserLocaleFromCache();
            if (locale == null) {
                locale = DMUserHandler.getUserLocaleFromDB();
                final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                if (userID != null && locale != null) {
                    DMUserHandler.updateUserDataInCache(userID + "_" + "USERLOCALE", (Object)locale);
                }
            }
        }
        catch (final DataAccessException dae) {
            AuthUtilAccessImpl.LOGGER.log(Level.SEVERE, "DataAccessException exception while getting locale from db...", (Throwable)dae);
        }
        catch (final Exception e) {
            AuthUtilAccessImpl.LOGGER.log(Level.SEVERE, "Exception exception while getting locale from db...", e);
        }
        return locale;
    }
    
    public Locale getUserLocaleFromDB(final Long loginID) {
        String languageCode = "en";
        String countryCode = "US";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "*"));
            final Join userJoin = new Join("AaaUserProfile", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join loginJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            query.addJoin(userJoin);
            query.addJoin(loginJoin);
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            final Row userRow = dobj.getRow("AaaUserProfile");
            if (userRow != null) {
                languageCode = (String)userRow.get("LANGUAGE_CODE");
                countryCode = (String)userRow.get("COUNTRY_CODE");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(AuthUtilAccessImpl.class.getName()).log(Level.SEVERE, "Exception while getting user locale from db : ", e);
        }
        return new Locale(languageCode, countryCode);
    }
    
    public String getUsedLocales() {
        String usedLocales = "en";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "USER_ID"));
            query.addSelectColumn(new Column("AaaUserProfile", "LANGUAGE_CODE"));
            final Join userJoin = new Join("AaaUserProfile", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join loginJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Criteria criteria = new Criteria(Column.getColumn("AaaUserProfile", "LANGUAGE_CODE"), (Object)usedLocales, 1);
            query.addJoin(userJoin);
            query.addJoin(loginJoin);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Iterator userItr = dobj.getRows("AaaUserProfile");
                while (userItr.hasNext()) {
                    final Row row = userItr.next();
                    usedLocales = usedLocales + "," + (String)row.get("LANGUAGE_CODE");
                }
            }
        }
        catch (final Exception e) {
            AuthUtilAccessImpl.LOGGER.log(Level.WARNING, "Exception while getting used locale from db : ", e);
        }
        return usedLocales;
    }
    
    public void setUserCredential(final String loginName, final String serviceName, final String domainName, final Long userID) {
        final Credential credential = AuthDBUtil.constructCredential(loginName, serviceName, domainName);
        AuthUtil.setUserCredential(credential);
    }
    
    public String getUserTimeZoneID() {
        String userTimeZoneID = TimeZone.getDefault().getID();
        try {
            userTimeZoneID = DMUserHandler.getUserTimeZoneIDFromCache();
            if (userTimeZoneID == null) {
                userTimeZoneID = DMUserHandler.getUserTimeZoneIDFromDB();
                final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                if (userTimeZoneID != null && userID != null) {
                    DMUserHandler.updateUserDataInCache(userID + "_" + "USERTIMEZONEID", (Object)userTimeZoneID);
                }
            }
        }
        catch (final Exception e) {
            AuthUtilAccessImpl.LOGGER.log(Level.SEVERE, "Exception while getting user time zone id...", e);
        }
        return userTimeZoneID;
    }
    
    public TimeZone getUserTimeZone() {
        TimeZone userTimeZone = TimeZone.getDefault();
        try {
            final String userTimeZoneID = this.getUserTimeZoneID();
            userTimeZone = TimeZone.getTimeZone(userTimeZoneID);
        }
        catch (final Exception e) {
            AuthUtilAccessImpl.LOGGER.log(Level.SEVERE, "Exception while getting user time zone value...", e);
        }
        return userTimeZone;
    }
    
    public Boolean isUserExistsInDB(final String email) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public SelectQuery getActiveUsersCriteria(final SelectQuery userQuery) {
        userQuery.addJoin(new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        userQuery.addSelectColumn(new Column("AaaUserStatus", "USER_ID"));
        userQuery.addSelectColumn(new Column("AaaUserStatus", "STATUS"));
        final Criteria existingCri = userQuery.getCriteria();
        Criteria activeStatusCri = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0, (boolean)Boolean.FALSE);
        if (existingCri != null) {
            activeStatusCri = activeStatusCri.and(existingCri);
        }
        userQuery.setCriteria(activeStatusCri);
        return userQuery;
    }
    
    public String getCustomerNameFromID(final Long customerID) {
        String customerName = null;
        try {
            customerName = CustomerInfoUtil.getInstance().getCustomerNameFromID(customerID);
        }
        catch (final SyMException ex) {
            AuthUtilAccessImpl.LOGGER.log(Level.SEVERE, "Exception in getting the customer name from customer id " + customerID);
        }
        return customerName;
    }
    
    public Boolean isSuperAdminVerified() {
        return Boolean.TRUE;
    }
    
    public String getEmailForUser(final Long userId) {
        String email = "";
        final Properties contactProp = DMUserHandler.getContactInfoProp(userId);
        email = ((Hashtable<K, String>)contactProp).get("EMAIL_ID");
        return email;
    }
    
    public String getDomainName() {
        return AuthUtil.getUserCredential().getDomainName();
    }
    
    public String getEncryptedPassword(final String key) {
        return AuthUtil.getEncryptedPassword(key, "ADAKKAMAMARARULAARIRULUYITHUVIDUM");
    }
    
    public String getUsernameFromEmail(final String email) {
        DataObject dataObject = null;
        String userName = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            selectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            selectQuery.addSelectColumn(new Column("AaaContactInfo", "*"));
            final Criteria criteria = new Criteria(new Column("AaaContactInfo", "EMAILID"), (Object)email, 0);
            selectQuery.setCriteria(criteria);
            dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Row userRow = dataObject.getFirstRow("AaaUser");
            userName = (String)userRow.get("FIRST_NAME");
        }
        catch (final Exception ex) {
            AuthUtilAccessImpl.LOGGER.log(Level.WARNING, "Exception in getContactInfoDO", ex);
        }
        return userName;
    }
    
    public long getCurrentServerTime() {
        return System.currentTimeMillis();
    }
    
    public void flushCredentials() {
        AuthUtil.flushCredentials();
    }
    
    static {
        LOGGER = Logger.getLogger(AuthUtilAccessImpl.class.getName());
    }
}
