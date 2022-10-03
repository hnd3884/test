package com.me.mdm.server.ios.apns;

import com.adventnet.ds.query.Criteria;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

public class APNSDispatcher extends Thread
{
    LinkedBlockingQueue<Hashtable> notificationQueue;
    protected static Logger logger;
    APNsWakeUpProcessorWrapper apNsWakeUpProcessorWrapper;
    boolean inProgress;
    boolean stopQueue;
    
    public APNSDispatcher(final APNsWakeUpProcessorWrapper wrapper, final LinkedBlockingQueue<Hashtable> notificationQueue) {
        this.apNsWakeUpProcessorWrapper = null;
        this.inProgress = true;
        this.stopQueue = false;
        this.notificationQueue = notificationQueue;
        this.apNsWakeUpProcessorWrapper = wrapper;
    }
    
    @Override
    public void run() {
        List resourceList = null;
        Integer notificationType = null;
        try {
            while (!this.stopQueue) {
                final Hashtable qData = this.notificationQueue.poll(100L, TimeUnit.MILLISECONDS);
                if (qData == null) {
                    this.inProgress = (this.notificationQueue.size() > 0);
                }
                else {
                    this.inProgress = true;
                    APNSDispatcher.logger.log(Level.INFO, "Data taken From Dispatcher Queue - {0}", qData);
                    resourceList = qData.get("Resource_List");
                    notificationType = qData.get("Notification_Type");
                    this.apNsWakeUpProcessorWrapper.wakeUpAllDevices(resourceList, notificationType);
                    resourceList = null;
                }
            }
            APNSDispatcher.logger.log(Level.INFO, "Stop Queue Enabled.. preparing to Stop Dispatcher Queue");
        }
        catch (final InterruptedException e) {
            APNSDispatcher.logger.log(Level.INFO, "Queue processing was interrupted and queue size - {0}", this.notificationQueue.size());
        }
        catch (final Exception e2) {
            APNSDispatcher.logger.log(Level.SEVERE, "Exception occurred - ", e2);
        }
        finally {
            if (resourceList != null) {
                NotificationHandler.getInstance().updateNotificationStatus(resourceList, 2, notificationType, null);
            }
            this.apNsWakeUpProcessorWrapper.disconnectPushyClient(true);
            this.inProgress = false;
        }
    }
    
    void setStopQueue() {
        this.stopQueue = true;
    }
    
    public static void closeCurrentUserConnection(final boolean publish) {
        APNSDispatcher.logger.log(Level.INFO, "Apns dispatcher: closing connection [1]");
        LRUAPNSConnectionMap.removeIfExists("1");
    }
    
    static {
        APNSDispatcher.logger = Logger.getLogger(APNSDispatcher.class.getName());
    }
}
