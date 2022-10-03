package com.adventnet.mfw.message;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.mfw.threadpool.ThreadPoolException;
import com.adventnet.mfw.notification.SynchronousNotifierImpl;
import com.adventnet.mfw.notification.AsynchronousNotifierImpl;
import java.util.LinkedHashMap;
import com.adventnet.mfw.notification.Notifier;
import java.util.HashMap;
import java.util.logging.Logger;

public class Messenger
{
    @Deprecated
    public static final String SERVER_STARTUP_NOTIFICATION = "startupNotification";
    @Deprecated
    public static final String SERVER_SHUTDOWN_NOTIFICATION = "SERVER_SHUTDOWN_NOTIFICATION";
    private static final Logger LOG;
    private static HashMap asyncSubMap;
    private static HashMap syncSubMap;
    private static Notifier syncNotifier;
    private static Notifier asyncNotifier;
    private static LinkedHashMap<String, Long> moduleVsTimeTaken;
    
    public static LinkedHashMap<String, Long> getModuleVsTimeTaken() {
        return Messenger.moduleVsTimeTaken;
    }
    
    public static void setModuleVsTimeTaken(final String module, final long time) {
        Messenger.moduleVsTimeTaken.put(module, time);
    }
    
    public static void initializeNotifiers(final boolean sync) throws ThreadPoolException {
        Messenger.asyncNotifier = ((Messenger.asyncNotifier == null && !sync) ? new AsynchronousNotifierImpl() : Messenger.asyncNotifier);
        Messenger.syncNotifier = ((Messenger.syncNotifier == null && sync) ? new SynchronousNotifierImpl() : Messenger.syncNotifier);
    }
    
    public static void publish(final String topic, final Object obj) throws Exception {
        if (!hasListener()) {
            return;
        }
        if (hasListener(topic, false)) {
            Messenger.asyncNotifier.notify(topic, obj);
        }
        if (hasListener(topic, true)) {
            Messenger.syncNotifier.notify(topic, obj);
        }
    }
    
    public static synchronized void subscribe(final String topic, final MessageListener listener, final boolean sync, final MessageFilter filter) throws Exception {
        initializeNotifiers(sync);
        if (sync && topic.equals(Topics.COMMIT_TOPIC.get())) {
            throw new IllegalArgumentException("CommitTopic always has to be asynchronous");
        }
        HashMap toOperate = Messenger.syncSubMap;
        if (!sync) {
            toOperate = Messenger.asyncSubMap;
        }
        ArrayList listenersList = toOperate.get(topic);
        if (listenersList == null) {
            listenersList = new ArrayList();
        }
        listenersList.add(new ListenerToFilter(listener, filter));
        toOperate.put(topic, listenersList);
        Messenger.LOG.log(Level.FINEST, "Subscribers list {0}", toOperate);
    }
    
    public static synchronized void unsubscribe(final String topic, final MessageListener listener) throws Exception {
        unsubscribe(topic, listener, true);
        unsubscribe(topic, listener, false);
    }
    
    public static synchronized void unsubscribe(final String topic, final MessageListener listener, final boolean sync) throws Exception {
        if (sync) {
            final ArrayList syncListenerList = Messenger.syncSubMap.get(topic);
            if (syncListenerList != null) {
                for (int i = 0; i < syncListenerList.size(); ++i) {
                    final ListenerToFilter lisToFil = syncListenerList.get(i);
                    final MessageListener lis = lisToFil.getListener();
                    if (lis.equals(listener)) {
                        syncListenerList.remove(i);
                    }
                }
            }
        }
        else {
            final ArrayList asyncListenerList = Messenger.asyncSubMap.get(topic);
            if (asyncListenerList != null) {
                for (int i = 0; i < asyncListenerList.size(); ++i) {
                    final ListenerToFilter lisToFil = asyncListenerList.get(i);
                    final MessageListener lis = lisToFil.getListener();
                    if (lis.equals(listener)) {
                        asyncListenerList.remove(i);
                    }
                }
            }
        }
    }
    
    public static List getListners(final String topic, final Object obj) {
        if (topic == null || obj == null) {
            throw new IllegalArgumentException("Topic/Obj cannot be null");
        }
        final List suscribers = Messenger.syncSubMap.get(topic);
        if (suscribers != null) {
            final ArrayList listeners = new ArrayList();
            for (int i = 0; i < suscribers.size(); ++i) {
                final ListenerToFilter temp = suscribers.get(i);
                if (temp.getFilter() == null || temp.getFilter().matches(obj)) {
                    listeners.add(temp.getListener());
                }
            }
            return listeners;
        }
        return Collections.EMPTY_LIST;
    }
    
    public static List getSyncListenerToFilter(final String topic) {
        if (topic == null) {
            throw new IllegalArgumentException("Topic/Obj cannot be null");
        }
        if (hasListener(topic, true)) {
            return Messenger.syncSubMap.get(topic);
        }
        return Collections.EMPTY_LIST;
    }
    
    public static List getAsyncListenerToFilter(final String topic) {
        if (topic == null) {
            throw new IllegalArgumentException("Topic/Obj cannot be null");
        }
        if (hasListener(topic, false)) {
            return Messenger.asyncSubMap.get(topic);
        }
        return Collections.EMPTY_LIST;
    }
    
    public static Boolean hasListener(final String topicName, final boolean sync) {
        if (sync) {
            return Messenger.syncSubMap.containsKey(topicName);
        }
        return Messenger.asyncSubMap.containsKey(topicName);
    }
    
    public static Boolean hasListener() {
        return !Messenger.syncSubMap.isEmpty() || !Messenger.asyncSubMap.isEmpty();
    }
    
    static {
        LOG = Logger.getLogger(Messenger.class.getName());
        Messenger.asyncSubMap = new HashMap();
        Messenger.syncSubMap = new HashMap();
        Messenger.moduleVsTimeTaken = new LinkedHashMap<String, Long>();
    }
    
    public enum Topics
    {
        SERVER_STARTUP_TOPIC("StartupNotification"), 
        SERVER_SHUTDOWN_TOPIC("ServerShutdownNotification"), 
        COMMIT_TOPIC("CommitTopic"), 
        PERSISTENCE_TOPIC("PersistenceTopic"), 
        DATAMODEL_TOPIC("DataModelTopic");
        
        private String topicName;
        
        private Topics(final String name) {
            this.topicName = name;
        }
        
        public String get() {
            return this.topicName;
        }
    }
}
