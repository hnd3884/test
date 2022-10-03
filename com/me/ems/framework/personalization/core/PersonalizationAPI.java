package com.me.ems.framework.personalization.core;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;

public interface PersonalizationAPI
{
    Map<String, Object> getPersonalizeSettings(final User p0) throws Exception;
    
    Map<String, Object> updatePersonalizeSettings(final Map<String, Object> p0, final User p1, final HttpServletRequest p2) throws Exception;
    
    Map<String, Object> getUserImage(final User p0) throws Exception;
    
    Map<String, Object> getActiveSessions(final User p0) throws Exception;
    
    boolean deleteActiveSession(final Long p0, final HttpServletRequest p1, final User p2) throws Exception;
    
    void closeAllSessions(final User p0) throws Exception;
    
    Map<String, Object> updateTimeZoneAndLanguage(final Map<String, Object> p0, final User p1) throws Exception;
    
    void validateTimeZoneAndLanguage(final Map<String, Object> p0) throws Exception;
}
