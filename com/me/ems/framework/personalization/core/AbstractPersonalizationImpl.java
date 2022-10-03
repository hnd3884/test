package com.me.ems.framework.personalization.core;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;

public abstract class AbstractPersonalizationImpl implements PersonalizationAPI
{
    @Override
    public Map<String, Object> getPersonalizeSettings(final User user) throws Exception {
        return Collections.emptyMap();
    }
    
    @Override
    public Map<String, Object> updatePersonalizeSettings(final Map<String, Object> detailsMap, final User user, final HttpServletRequest request) throws Exception {
        return Collections.emptyMap();
    }
    
    @Override
    public Map<String, Object> getUserImage(final User user) throws Exception {
        return Collections.emptyMap();
    }
    
    @Override
    public boolean deleteActiveSession(final Long sessionID, final HttpServletRequest request, final User user) throws Exception {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    @Override
    public Map<String, Object> getActiveSessions(final User user) throws Exception {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    @Override
    public void closeAllSessions(final User user) throws Exception {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    @Override
    public Map<String, Object> updateTimeZoneAndLanguage(final Map<String, Object> detailsMap, final User user) throws Exception {
        return Collections.emptyMap();
    }
    
    @Override
    public void validateTimeZoneAndLanguage(final Map<String, Object> detailsMap) throws Exception {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
