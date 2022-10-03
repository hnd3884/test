package com.me.mdm.server.apps.blacklist.batchprocessor;

import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;

public interface BatchProcessorInterface
{
    void processDOData(final DataObject p0, final HashMap p1) throws Exception;
    
    int processDSData(final DMDataSetWrapper p0, final HashMap p1) throws Exception;
}
