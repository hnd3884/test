package com.zoho.clustering.filerepl.slave.api;

import java.io.InputStream;
import com.zoho.clustering.filerepl.event.EventList;
import com.zoho.clustering.filerepl.event.EventLogPosition;

public interface MasterStub
{
    EventList getEvents(final EventLogPosition p0, final int p1) throws APIException, APIResourceException;
    
    InputStream downloadFile(final int p0, final String p1) throws APIException, APIResourceException;
    
    String takeSnapshot(final boolean p0) throws APIException, APIResourceException;
    
    InputStream downloadSnapshot(final String p0) throws APIException, APIResourceException;
}
