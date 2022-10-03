package com.me.mdm.server.notification;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.ios.apns.APNsWakeUpProcessor;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.mdm.server.ios.apns.LRUAPNSConnectionMap;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.MemoryOnlyDCQueueDataProcessor;

public class NotificationProcessor extends MemoryOnlyDCQueueDataProcessor
{
    private Logger logger;
    private static LRUAPNSConnectionMap connectionMap;
    
    public NotificationProcessor() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processData(final DCQueueData qData) {
        this.logger.log(Level.INFO, "Device notification Queue processing started {0}", qData.queueData.toString());
        HashMap<Long, List<Long>> notificationMap = null;
        final String sNotificationType = (String)qData.queueData;
        int notificationType;
        Iterator<Map.Entry<Long, List<Long>>> iterator;
        Map.Entry customerToListPair;
        Long customerId;
        List resourceList;
        WakeUpProcessor wakeUpProcessor;
        Criteria inProgressCriteria;
        for (notificationType = Integer.parseInt(sNotificationType), notificationMap = NotificationHandler.getInstance().getResourcesForNotification(notificationType); notificationMap.size() > 0; notificationMap = NotificationHandler.getInstance().getResourcesForNotification(notificationType)) {
            iterator = notificationMap.entrySet().iterator();
            while (iterator.hasNext()) {
                customerToListPair = iterator.next();
                customerId = customerToListPair.getKey();
                resourceList = customerToListPair.getValue();
                wakeUpProcessor = getWakeupProcessor(notificationType, customerId);
                if (wakeUpProcessor != null) {
                    NotificationHandler.getInstance().updateNotificationStatus(resourceList, 3, notificationType, null);
                    wakeUpProcessor.wakeUpDevices(resourceList, notificationType);
                }
                else {
                    this.logger.log(Level.INFO, "Reslult hash is empty. Wake up failure for resources {0}", resourceList);
                    inProgressCriteria = new Criteria(new Column("DeviceNotification", "STATUS"), (Object)3, 0);
                    NotificationHandler.getInstance().updateNotificationStatus(resourceList, 2, notificationType, inProgressCriteria);
                }
            }
            try {
                Thread.sleep(30000L);
            }
            catch (final InterruptedException ex) {
                this.logger.log(Level.SEVERE, "Exception while waking ios device", ex);
            }
        }
    }
    
    private static WakeUpProcessor getWakeupProcessor(final int notificationType, final Long customerId) {
        WakeUpProcessor wakeUpProcessor = WakeUpProcessor.getWakeUpProcessor(notificationType, customerId);
        if (wakeUpProcessor != null && wakeUpProcessor instanceof APNsWakeUpProcessor && MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("holdAPNSConnection")) {
            final APNsWakeUpProcessor wrapper = NotificationProcessor.connectionMap.addOrGet("1");
            if (wrapper != null) {
                wakeUpProcessor = wrapper;
            }
        }
        return wakeUpProcessor;
    }
    
    static {
        NotificationProcessor.connectionMap = new LRUAPNSConnectionMap();
    }
}
