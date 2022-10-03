package com.me.devicemanagement.onpremise.server.admin;

import java.io.IOException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.onpremise.server.sdp.Ticket;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import java.util.Properties;

public interface DMJiraAPI
{
    Properties getJiraCredentials();
    
    void addMETrackingForJiraIframe();
    
    Ticket setValuesAfterJiraLogin(final DataObject p0, final String p1, final HttpServletRequest p2) throws DataAccessException;
    
    int checkSessionWithJiraServer(final String p0, final String p1, final String p2, final String p3, final String p4) throws IOException;
}
