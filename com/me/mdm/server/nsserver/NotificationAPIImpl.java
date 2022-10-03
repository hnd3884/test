package com.me.mdm.server.nsserver;

import com.me.devicemanagement.framework.server.api.NSResponseAPI;
import java.util.Properties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.nsclient.NSClient;
import com.me.mdm.server.nsclient.NSRequest;
import com.me.mdm.server.nsclient.NSResponse;
import com.me.devicemanagement.framework.server.api.DCNotificationServiceAPI;

public class NotificationAPIImpl implements DCNotificationServiceAPI
{
    public NSResponse sendRequest(final Long resourceID, final String command, final String resourceType) throws Exception {
        final NSRequest nsrequest = new NSRequest();
        nsrequest.createNSRequestData(resourceID, command);
        final NSClient nsclient = new NSClient();
        nsclient.setPort(this.getNSPort());
        final NSResponse nsresponse = nsclient.sendRequest(nsrequest);
        return nsresponse;
    }
    
    public boolean isNSEnabled() {
        return NSUtil.getInstance().isNSEnabled();
    }
    
    public int getNSPort() {
        return NSUtil.getInstance().getNSPort();
    }
    
    public String getNSMessageAlerts() {
        return null;
    }
    
    public boolean isNSComponentEnabled() {
        return NSUtil.getInstance().isNSComponentEnabled();
    }
    
    public HashMap getLiveResourceMap() {
        return NSLiveResourceUtil.getInstance().getLiveResourceMap();
    }
    
    public ArrayList<Long> getLiveResourceList() {
        return NSLiveResourceUtil.getInstance().getLiveResourceList();
    }
    
    public HashMap getLiveListFromNS() throws IOException {
        return NSLiveResourceUtil.getInstance().getLiveListFromNS();
    }
    
    public boolean getLiveStatusFromNS(final long resourceId) throws IOException {
        return NSLiveResourceUtil.getInstance().getLiveStatusFromNS(resourceId);
    }
    
    public void initiatePatchScan(final long deviceId, final Properties userProps, final HashMap taskInfoMap) throws Exception {
    }
    
    public void removeResFromLiveList(final Long resourceId) {
        NSLiveResourceUtil.getInstance().removeResFromLiveList(resourceId);
    }
    
    public void syncLiveStatus() {
    }
    
    public void updateLiveStatusForResource(final long resID, final int status) {
    }
}
