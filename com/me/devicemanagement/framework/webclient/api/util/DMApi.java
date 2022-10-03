package com.me.devicemanagement.framework.webclient.api.util;

import org.json.JSONObject;
import java.util.List;
import com.adventnet.persistence.DataObject;
import javax.servlet.http.HttpServletRequest;

public interface DMApi
{
    DataObject getAPIKeyDO(final HttpServletRequest p0) throws Exception;
    
    boolean isResourcesManagedByCustomer(final String p0, final List p1) throws Exception;
    
    boolean getRolesAndCheckAuthorization(final Long p0, final String p1, final String p2) throws Exception;
    
    boolean checkProductValidity(final String p0);
    
    default String urltoValidateSDPticket() throws Exception {
        return null;
    }
    
    boolean isResourcesManagedByTech(final Long p0, final List p1);
    
    boolean isInvalidEdition(final List p0);
    
    void mobileAppLoginEntry(final JSONObject p0, final String p1);
    
    void handlePostLogin(final JSONObject p0, final String p1, final String p2, final String p3);
}
