package com.zoho.migration;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public interface TableLevelConfigurationHandler
{
    void handleForInsert(final Row p0, final DataObject p1) throws Exception;
    
    void handleForUpdate(final Row p0, final DataObject p1) throws Exception;
    
    void handleForDelete(final Row p0, final DataObject p1) throws Exception;
}
