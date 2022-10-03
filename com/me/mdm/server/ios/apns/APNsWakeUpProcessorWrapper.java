package com.me.mdm.server.ios.apns;

import java.lang.reflect.InvocationTargetException;
import javax.resource.NotSupportedException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.lang.reflect.Constructor;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Logger;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

public class APNsWakeUpProcessorWrapper extends APNsWakeUpProcessor
{
    private LinkedBlockingQueue<Hashtable> notificationQueue;
    private static Logger logger;
    static final String RESOURCE_LIST = "Resource_List";
    static final String NOTIFICATION_TYPE = "Notification_Type";
    static final String DISPATCHER_CLASS = "APNS_DISPATCHER_CLASS";
    APNSDispatcher dispatcher;
    
    private APNsWakeUpProcessorWrapper() throws Exception {
        this.notificationQueue = new LinkedBlockingQueue<Hashtable>();
        this.dispatcher = null;
        this.startDispatcherQueue();
    }
    
    private void startDispatcherQueue() throws Exception {
        final String classname = ProductClassLoader.getSingleImplProductClass("APNS_DISPATCHER_CLASS");
        final Class<?> cl = Class.forName(classname);
        final Constructor<?> cons = cl.getConstructor(APNsWakeUpProcessorWrapper.class, LinkedBlockingQueue.class);
        (this.dispatcher = (APNSDispatcher)cons.newInstance(this, this.notificationQueue)).start();
    }
    
    public static APNsWakeUpProcessorWrapper getInstance() {
        try {
            return new APNsWakeUpProcessorWrapper();
        }
        catch (final Exception e) {
            APNsWakeUpProcessorWrapper.logger.log(Level.SEVERE, "Exception occurred - ", e);
            return null;
        }
    }
    
    public void stopQueue() {
        this.dispatcher.setStopQueue();
    }
    
    public boolean queueInProgress() {
        return this.dispatcher.notificationQueue.size() != 0 || this.dispatcher.inProgress;
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        APNsWakeUpProcessorWrapper.logger.log(Level.INFO, "Data will be Added to Dispatcher Queue - {0}", resourceList);
        final Hashtable qData = new Hashtable();
        qData.put("Resource_List", resourceList);
        qData.put("Notification_Type", notificationType);
        this.notificationQueue.add(qData);
        return new HashMap();
    }
    
    void wakeUpAllDevices(final List resourceList, final int notificationType) {
        super.wakeUpDevices(resourceList, notificationType);
    }
    
    @Override
    protected void disconnectPushyClient() {
        this.disconnectPushyClient(false);
    }
    
    void disconnectPushyClient(final boolean forceDisconnect) {
        if (forceDisconnect) {
            super.disconnectPushyClient();
        }
    }
    
    public static void closeCurrentUserClient() throws NotSupportedException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        final String classname = ProductClassLoader.getSingleImplProductClass("APNS_DISPATCHER_CLASS");
        final Method method = Class.forName(classname).getDeclaredMethod("closeCurrentUserConnection", Boolean.TYPE);
        method.invoke(null, true);
    }
    
    static {
        APNsWakeUpProcessorWrapper.logger = Logger.getLogger(APNsWakeUpProcessorWrapper.class.getName());
    }
}
