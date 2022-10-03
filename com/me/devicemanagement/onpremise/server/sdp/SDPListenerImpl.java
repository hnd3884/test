package com.me.devicemanagement.onpremise.server.sdp;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class SDPListenerImpl extends AbstractUserListener
{
    private static Logger logger;
    private static final int USER_EVENT = 333;
    
    public void userAdded(final UserEvent userEvent) {
        try {
            this.addToSdpQueue();
        }
        catch (final Exception ex) {
            SDPListenerImpl.logger.log(Level.SEVERE, "Exception Occurred in SDPListenerImpl - userAdded : ", ex);
        }
        SDPListenerImpl.logger.log(Level.INFO, "SDP changes handled for userAdded");
    }
    
    public void userModified(final UserEvent userEvent) {
        try {
            this.addToSdpQueue();
        }
        catch (final Exception ex) {
            SDPListenerImpl.logger.log(Level.SEVERE, "Exception Occurred in SDPListenerImpl - userModified : ", ex);
        }
        SDPListenerImpl.logger.log(Level.INFO, "SDP changes handled for userModified");
    }
    
    public void userDeleted(final UserEvent userEvent) {
        try {
            this.addToSdpQueue();
        }
        catch (final Exception ex) {
            SDPListenerImpl.logger.log(Level.SEVERE, "Exception Occurred in SDPListenerImpl - userDeleted : ", ex);
        }
        SDPListenerImpl.logger.log(Level.INFO, "SDP changes handled for userDeleted");
    }
    
    private void addToSdpQueue() {
        try {
            final JSONObject qdata = new JSONObject();
            final DCQueue queue = DCQueueHandler.getQueue("sdp-data-listener");
            final DCQueueData queueData = new DCQueueData();
            final Long postedTime = System.currentTimeMillis();
            queueData.postTime = postedTime;
            queueData.fileName = "sdpData-listener-" + postedTime + ".txt";
            queueData.queueData = qdata;
            queueData.queueDataType = 333;
            queue.addToQueue(queueData);
        }
        catch (final Exception e) {
            SDPListenerImpl.logger.log(Level.INFO, "SDPListener: Exception in addtoSDP queue Message", e);
        }
    }
    
    static {
        SDPListenerImpl.logger = Logger.getLogger("UserManagementLogger");
    }
}
