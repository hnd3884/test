package com.adventnet.persistence.migration;

import com.adventnet.persistence.DataObject;

public interface XmlChangeNotifyObject
{
    void setExistingXMLDO(final DataObject p0);
    
    void setLatestXMLDO(final DataObject p0);
    
    void setXMLDiffDO(final DataObject p0);
    
    DataObject getExistingXMLDO();
    
    DataObject getLatestXMLDO();
    
    DataObject getXMLDiffDO();
}
