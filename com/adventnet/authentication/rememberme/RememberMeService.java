package com.adventnet.authentication.rememberme;

import com.adventnet.persistence.DataObject;
import javax.security.auth.Subject;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface RememberMeService
{
    void updateRememberMeInfo(final HttpServletRequest p0, final HttpServletResponse p1);
    
    Map<String, Object> hasValidAuthToken(final HttpServletRequest p0);
    
    void removeRememberMeInfo(final HttpServletRequest p0, final HttpServletResponse p1);
    
    Subject constructSubject(final Subject p0, final Map<String, Object> p1, final HttpServletRequest p2);
    
    DataObject getRememberMeDetails(final String p0);
}
