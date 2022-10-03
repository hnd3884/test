package com.me.devicemanagement.framework.server.factory;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import javax.servlet.http.HttpServletRequest;

public interface RestAPIUtil
{
    DataObject getAuthDO(final HttpServletRequest p0) throws Exception;
    
    boolean authenticateRequest(final DataObject p0, final Object p1);
    
    HashMap fillLoginParams(final HashMap p0, final DataObject p1) throws Exception;
    
    default boolean isCSRFAttackPresent(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final boolean isValidAuthToken, final Object entity) {
        return false;
    }
}
