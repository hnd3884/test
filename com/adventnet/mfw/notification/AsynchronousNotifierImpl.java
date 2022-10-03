package com.adventnet.mfw.notification;

import java.util.List;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.message.ListenerToFilter;
import java.util.function.Consumer;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.mfw.threadpool.ThreadPoolException;
import java.util.Properties;
import com.adventnet.mfw.threadpool.ThreadPoolManager;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.concurrent.ThreadPoolExecutor;

public class AsynchronousNotifierImpl implements Notifier
{
    ThreadPoolExecutor executor;
    
    public AsynchronousNotifierImpl(final ThreadPoolExecutor threadPoolExecutor) {
        this.executor = threadPoolExecutor;
    }
    
    public AsynchronousNotifierImpl() throws ThreadPoolException {
        final Properties asyncPoolProps = PersistenceInitializer.getConfigurationProps("AsynchronousNotifier");
        final Integer poolSize = (asyncPoolProps != null && asyncPoolProps.getProperty("PoolSize") != null) ? Integer.valueOf(Integer.parseInt(asyncPoolProps.getProperty("PoolSize"))) : null;
        final Integer maxPoolSize = (asyncPoolProps != null && asyncPoolProps.getProperty("MaxPoolSize") != null) ? Integer.valueOf(Integer.parseInt(asyncPoolProps.getProperty("MaxPoolSize"))) : null;
        final Long aliveTime = (asyncPoolProps != null && asyncPoolProps.getProperty("AliveTime") != null) ? Long.valueOf(Long.parseLong(asyncPoolProps.getProperty("AliveTime"))) : null;
        final Integer queueSize = (asyncPoolProps != null && asyncPoolProps.getProperty("QueueSize") != null) ? Integer.valueOf(Integer.parseInt(asyncPoolProps.getProperty("QueueSize"))) : null;
        if (asyncPoolProps != null) {
            this.executor = ThreadPoolManager.getInstance().createThreadPoolExecutor("Notification Processor", poolSize, maxPoolSize, aliveTime, queueSize);
        }
        else {
            this.executor = ThreadPoolManager.getInstance().createDefaultExecutor("Notification Processor");
        }
    }
    
    @Override
    public void notify(final String topicName, final Object message) {
        if (topicName.equals(Messenger.Topics.COMMIT_TOPIC.get())) {
            try {
                final CommitNotifier commitNotifier = CommitNotifier.getInstance();
                commitNotifier.addAndNotifyMessage(message);
                return;
            }
            catch (final Exception e) {
                throw new RuntimeException("Exception while adding message to CommitTopic - " + e);
            }
        }
        this.notifyAsync(topicName, message);
    }
    
    private void notifyAsync(final String topicName, final Object message) {
        final List<ListenerToFilter> messageListeners = Messenger.getAsyncListenerToFilter(topicName);
        if (messageListeners != null) {
            final Object publishObject = message;
            messageListeners.forEach(new Consumer<ListenerToFilter>() {
                @Override
                public void accept(final ListenerToFilter lisToFil) {
                    final MessageListener ml = lisToFil.getListener();
                    final MessageFilter filter = lisToFil.getFilter();
                    if (filter == null || (filter != null && filter.matches(publishObject))) {
                        final Runnable task = () -> {
                            final Object val$publishObject = publishObject;
                            ml.onMessage(publishObject);
                            return;
                        };
                        AsynchronousNotifierImpl.this.executor.submit(task);
                    }
                }
            });
        }
    }
}
