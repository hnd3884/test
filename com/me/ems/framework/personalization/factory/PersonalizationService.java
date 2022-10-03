package com.me.ems.framework.personalization.factory;

import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;

public interface PersonalizationService
{
    Map<String, Object> showPersonalisePage(final User p0) throws APIException;
    
    Map<String, Object> updatePersonalizationDetails(final Map<String, Object> p0, final User p1, final HttpServletRequest p2) throws APIException;
    
    Map<String, Object> getUserDP(final User p0) throws APIException;
    
    Map<String, Object> getActiveSession(final User p0) throws APIException;
    
    Response deleteActiveSession(final User p0, final String p1, final HttpServletRequest p2) throws APIException;
    
    Response closeAllSessions(final User p0) throws APIException;
}
