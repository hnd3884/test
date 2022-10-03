package com.adventnet.persistence.migration;

import com.adventnet.persistence.DataObject;

public class DoXmlChangeNotifyObject implements XmlChangeNotifyObject
{
    private DataObject existingXMLDO;
    private DataObject latestXMLDO;
    private DataObject xmlDiffDO;
    
    public DoXmlChangeNotifyObject() {
        this.existingXMLDO = null;
        this.latestXMLDO = null;
        this.xmlDiffDO = null;
    }
    
    @Override
    public void setExistingXMLDO(final DataObject exisDO) {
        this.existingXMLDO = exisDO;
    }
    
    @Override
    public void setLatestXMLDO(final DataObject latestDO) {
        this.latestXMLDO = latestDO;
    }
    
    @Override
    public void setXMLDiffDO(final DataObject xmlDifDO) {
        this.xmlDiffDO = xmlDifDO;
    }
    
    @Override
    public DataObject getExistingXMLDO() {
        return this.existingXMLDO;
    }
    
    @Override
    public DataObject getLatestXMLDO() {
        return this.latestXMLDO;
    }
    
    @Override
    public DataObject getXMLDiffDO() {
        return this.xmlDiffDO;
    }
}
