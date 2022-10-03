package com.me.devicemanagement.framework.server.authentication;

import com.adventnet.ds.query.SelectQuery;
import java.util.TimeZone;
import java.util.Locale;
import java.util.List;

public interface AuthUtilAccessAPI
{
    Long getLoginID() throws Exception;
    
    Long getAccountID() throws Exception;
    
    Long getUserID() throws Exception;
    
    String getLoginName() throws Exception;
    
    String getSystemUserName();
    
    List<String> getRoles() throws Exception;
    
    Locale getUserLocale() throws Exception;
    
    Locale getUserLocaleFromDB(final Long p0);
    
    String getUsedLocales();
    
    void setUserCredential(final String p0, final String p1, final String p2, final Long p3);
    
    String getUserTimeZoneID();
    
    TimeZone getUserTimeZone();
    
    Boolean isUserExistsInDB(final String p0);
    
    SelectQuery getActiveUsersCriteria(final SelectQuery p0);
    
    String getCustomerNameFromID(final Long p0);
    
    Boolean isSuperAdminVerified();
    
    String getEmailForUser(final Long p0);
    
    String getDomainName();
    
    String getEncryptedPassword(final String p0);
    
    String getUsernameFromEmail(final String p0);
    
    long getCurrentServerTime();
    
    void flushCredentials();
    
    default String getUserTimeFormat() {
        return DMUserHandler.getUserTimeFormat();
    }
}
