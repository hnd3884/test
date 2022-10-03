package com.me.mdm.server.factory;

import com.me.mdm.server.easmanagement.pss.PSSException;
import com.adventnet.ds.query.SelectQuery;
import org.json.simple.JSONObject;

public interface ConditionalExchangeAccessAPI
{
    void addTaskToQueue(final JSONObject p0);
    
    void customizeQuery(final SelectQuery p0, final Long p1);
    
    int getPSSstate(final Long p0);
    
    boolean getReadFromFile();
    
    void incrementTaskList(final Long p0);
    
    void initiateSession(final JSONObject p0, final JSONObject p1) throws PSSException;
    
    void decrementTaskList(final Long p0);
    
    void closeSession(final Long p0);
    
    void closeAllSessions();
}
