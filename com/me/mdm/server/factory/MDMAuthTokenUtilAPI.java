package com.me.mdm.server.factory;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import java.util.Properties;

public interface MDMAuthTokenUtilAPI
{
    Properties createAuthToken(final String p0);
    
    String getURLWithAuthToken(final HashMap p0);
    
    String getURLWithAuthTokenAndUDID(final HashMap p0);
    
    String replaceAuthTokenPlaceHolder(final String p0, final String p1, final String p2, final boolean p3);
    
    Boolean authenticateDevice(final Long p0, final String p1);
    
    String getOrAddAuthToken(final Properties p0) throws DataAccessException, SyMException;
    
    Long authenticateUser(final String p0) throws SecurityException, Exception;
    
    Properties appendEnrollmentPropertiesForEnrollemntInMail(final Long p0, final Integer p1, final Properties p2, final Properties p3) throws Exception;
    
    JSONObject appendEnrollmentPropertiesForEnrollmentInUI(final Long p0, final Integer p1, final JSONObject p2) throws Exception;
    
    Properties appendProductSpecificSMSProperties(final Long p0, final Integer p1, final Properties p2, final Properties p3) throws Exception;
    
    Properties appendManagedUserProperties(final Properties p0) throws Exception;
    
    String getAdminEnrollAuthenticationUrl(final JSONObject p0) throws Exception;
    
    Long getAuthenticatedUserId(final Long p0) throws Exception;
    
    default Long getAuthenticatedUserId(final Long deviceForEnrollmentId, final String emailAddress, final String domainName) throws Exception {
        return -1L;
    }
    
    Boolean isActiveDirectoryOrZohoAccountAuthApplicable(final Long p0) throws Exception;
    
    String getCustomerPhoneNumber(final String p0);
    
    String getPlatformLogoPath(final int p0);
}
