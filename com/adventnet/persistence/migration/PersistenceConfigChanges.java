package com.adventnet.persistence.migration;

import java.io.File;
import java.util.List;
import java.util.HashMap;
import com.adventnet.persistence.ConfigurationParser;

public class PersistenceConfigChanges
{
    private ConfigurationParser cp;
    private HashMap confNameVsValue;
    private HashMap confNameVsProps;
    private HashMap confNameVsList;
    private List configToBeReplaced;
    private File extendedConfigXml;
    
    public ConfigurationParser getCPObject() {
        return this.cp;
    }
    
    public void setCPObject(final ConfigurationParser cp) {
        this.cp = cp;
    }
    
    public HashMap getConfNameVsValue() {
        return this.confNameVsValue;
    }
    
    public void setConfNameVsValue(final HashMap confNameVsValue) {
        this.confNameVsValue = confNameVsValue;
    }
    
    public HashMap getConfNameVsProps() {
        return this.confNameVsProps;
    }
    
    public void setConfNameVsProps(final HashMap confNameVsProps) {
        this.confNameVsProps = confNameVsProps;
    }
    
    public HashMap getConfNameVsList() {
        return this.confNameVsList;
    }
    
    public void setConfNameVsList(final HashMap confNameVsList) {
        this.confNameVsList = confNameVsList;
    }
    
    public List getConfigToBeReplaced() {
        return this.configToBeReplaced;
    }
    
    public void setConfigToBeReplaced(final List configToBeReplaced) {
        this.configToBeReplaced = configToBeReplaced;
    }
    
    public File getExtendedConfigXml() {
        return this.extendedConfigXml;
    }
    
    public void setExtendedConfigXml(final File extendedConfigXml) {
        this.extendedConfigXml = extendedConfigXml;
    }
}
