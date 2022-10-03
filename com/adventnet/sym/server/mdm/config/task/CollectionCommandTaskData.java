package com.adventnet.sym.server.mdm.config.task;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import java.util.List;

public class CollectionCommandTaskData
{
    private int platform;
    private String commandName;
    private List resourceList;
    private Long customerId;
    private List collectionList;
    private Map<Long, Long> profileCollectionMap;
    private Properties taskProperties;
    private Boolean isAppSilentInstall;
    private String loggedInUserIdALong;
    private HashMap<Long, List> collectionToApplicableResource;
    
    public CollectionCommandTaskData() {
        this.resourceList = null;
        this.customerId = null;
        this.collectionList = null;
        this.profileCollectionMap = null;
        this.taskProperties = null;
        this.loggedInUserIdALong = null;
        this.collectionToApplicableResource = null;
    }
    
    public HashMap<Long, List> getCollectionToApplicableResource() {
        return this.collectionToApplicableResource;
    }
    
    public void setCollectionToApplicableResource(final HashMap<Long, List> collectionToApplicableResource) {
        this.collectionToApplicableResource = collectionToApplicableResource;
    }
    
    public int getPlatform() {
        return this.platform;
    }
    
    public void setPlatform(final int platform) {
        this.platform = platform;
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public void setCommandName(final String commandName) {
        this.commandName = commandName;
    }
    
    public List getResourceList() {
        return this.resourceList;
    }
    
    public void setResourceList(final List resourceList) {
        this.resourceList = resourceList;
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
    
    public Map<Long, Long> getProfileCollectionMap() {
        return this.profileCollectionMap;
    }
    
    public void setProfileCollectionMap(final Map<Long, Long> profileCollectionMap) {
        this.profileCollectionMap = profileCollectionMap;
    }
    
    public Properties getTaskProperties() {
        return this.taskProperties;
    }
    
    public void setTaskProperties(final Properties taskProperties) {
        this.taskProperties = taskProperties;
    }
    
    public boolean isAppSilentInstall() {
        return this.isAppSilentInstall;
    }
    
    public void setAppSilentInstall(final Boolean appSilentInstall) {
        this.isAppSilentInstall = appSilentInstall;
    }
    
    public List getCollectionList() {
        return this.collectionList;
    }
    
    public void setCollectionList(final List collectionList) {
        this.collectionList = collectionList;
    }
    
    public String getLoggedInUserId() {
        return this.loggedInUserIdALong;
    }
    
    public void setLoggedInUserId(final String loggedInUserIdALong) {
        this.loggedInUserIdALong = loggedInUserIdALong;
    }
    
    public Long getProfileIdForCollection(final Long collectionId) {
        for (final Long profileId : this.profileCollectionMap.keySet()) {
            if (collectionId.equals(this.profileCollectionMap.get(profileId))) {
                return profileId;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Platform:" + this.platform + "; Command name:" + this.commandName + "; Resource list:" + this.resourceList + "; Customer Id:" + this.customerId + "; Collection list:" + this.collectionList + "; Profile collection map:" + this.profileCollectionMap + "; isAppSilentInstall:" + this.isAppSilentInstall + ";Loggedin user:" + this.loggedInUserIdALong + "; TaskProperties:" + this.taskProperties;
    }
}
