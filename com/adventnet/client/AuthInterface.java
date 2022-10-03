package com.adventnet.client;

import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletRequest;

public interface AuthInterface
{
    Long getAccountID();
    
    String getLoginName();
    
    Long getUserID();
    
    boolean userExists(final String p0);
    
    Object encrypt(final Object p0, final Object p1, final HttpServletRequest p2);
    
    Object encrypt(final Object p0);
    
    String getListViewTotalHtmlString(final ViewContext p0);
    
    List<Long> getAccountIDs(final List<String> p0) throws Exception;
}
