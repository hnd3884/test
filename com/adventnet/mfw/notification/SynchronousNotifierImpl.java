package com.adventnet.mfw.notification;

import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import java.util.List;
import com.adventnet.mfw.message.ListenerToFilter;
import com.adventnet.mfw.message.Messenger;

public class SynchronousNotifierImpl implements Notifier
{
    @Override
    public void notify(final String topicName, final Object message) {
        final List messageListeners = Messenger.getSyncListenerToFilter(topicName);
        MessageListener listener = null;
        MessageFilter filter = null;
        for (int i = 0; i < messageListeners.size(); ++i) {
            final ListenerToFilter lisToFil = messageListeners.get(i);
            filter = lisToFil.getFilter();
            listener = lisToFil.getListener();
            if (filter != null) {
                if (filter == null || !filter.matches(message)) {
                    continue;
                }
            }
            try {
                listener.onMessage(message);
            }
            catch (final Throwable e) {
                if (!Boolean.parseBoolean(System.getProperty("ignore.notification.listener.exception", "false"))) {
                    throw e;
                }
                e.printStackTrace();
            }
        }
    }
}
