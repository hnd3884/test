package com.adventnet.persistence.migration;

import com.adventnet.persistence.DataObject;

public class DefaultDOXMLChangeListener implements DOXMLChangeListener
{
    @Override
    public boolean preInvoke(final String xmlFileName, final XmlChangeNotifyObject doXmlChangeNotifyObject) throws Exception {
        return true;
    }
    
    @Override
    public void postInvoke(final String xmlFileName, final DataObject dataObject) throws Exception {
    }
    
    @Override
    public int getPreference(final String fileToBeUpdated) {
        return 2;
    }
    
    @Override
    public boolean skipXML(final String fileToBeUpdated) throws Exception {
        return fileToBeUpdated.contains("conf/Persistence/error-codes.xml");
    }
}
