package com.me.mdm.server.factory;

import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface FileDownloadRegulatorAPI
{
    String getModule(final String p0);
    
    Long getCustomerID(final String p0);
    
    String getFileIDhint(final String p0);
    
    String getInternalRedirectPath(final HttpServletRequest p0);
    
    SelectQuery getLoginUserAuthorizationQuery(final List<Long> p0);
    
    JSONObject getDownloadRequestDetails(final HttpServletRequest p0);
    
    SelectQuery getAgentAuthorizationQuery(final List<String> p0, final List<Long> p1);
    
    Boolean isServeFromAppServer(final HttpServletRequest p0);
    
    Boolean isValidFileExtension(final String p0);
}
