package com.adventnet.mfw.notification;

import com.adventnet.persistence.OperationInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.threadpool.ThreadPoolManager;
import com.adventnet.mfw.message.ListenerToFilter;
import java.util.function.Consumer;
import com.adventnet.mfw.message.Messenger;
import javax.transaction.RollbackException;
import java.util.logging.Level;
import java.util.HashMap;
import javax.transaction.SystemException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.mfw.threadpool.ThreadPoolException;
import com.adventnet.mfw.message.MessageAggregator;
import javax.transaction.Transaction;
import java.util.Map;
import java.util.logging.Logger;
import javax.transaction.Synchronization;

public class CommitNotifier implements Synchronization
{
    private static Logger out;
    private static Map<Transaction, MessageAggregator> messageAggregatorMap;
    private static CommitNotifier commitNotifier;
    
    private CommitNotifier() throws ThreadPoolException {
    }
    
    public static CommitNotifier getInstance() throws ThreadPoolException {
        if (CommitNotifier.commitNotifier == null) {
            CommitNotifier.commitNotifier = new CommitNotifier();
        }
        return CommitNotifier.commitNotifier;
    }
    
    protected void addAndNotifyMessage(final Object message) throws SystemException, ThreadPoolException {
        final Transaction transaction = DataAccess.getTransactionManager().getTransaction();
        final MessageAggregator messageAggregator = this.getMessageAggregatorForCurrentTransaction(transaction);
        messageAggregator.addMessage(message);
    }
    
    private MessageAggregator getMessageAggregatorForCurrentTransaction(final Transaction transaction) {
        if (CommitNotifier.messageAggregatorMap == null) {
            CommitNotifier.messageAggregatorMap = new HashMap<Transaction, MessageAggregator>();
        }
        MessageAggregator messageAggregator = null;
        if (CommitNotifier.messageAggregatorMap.containsKey(transaction)) {
            messageAggregator = CommitNotifier.messageAggregatorMap.get(transaction);
        }
        synchronized (CommitNotifier.messageAggregatorMap) {
            if (messageAggregator == null) {
                try {
                    this.registerTransaction(transaction);
                    messageAggregator = new MessageAggregator();
                }
                catch (final IllegalStateException | RollbackException | SystemException | ThreadPoolException e) {
                    CommitNotifier.out.log(Level.SEVERE, "Exception occured while creating aggregator for transaction", e);
                }
                CommitNotifier.messageAggregatorMap.put(transaction, messageAggregator);
            }
        }
        return messageAggregator;
    }
    
    private void registerTransaction(final Transaction transaction) throws IllegalStateException, RollbackException, SystemException, ThreadPoolException {
        transaction.registerSynchronization((Synchronization)this);
    }
    
    private void remove(final Transaction transaction) {
        synchronized (CommitNotifier.messageAggregatorMap) {
            CommitNotifier.messageAggregatorMap.remove(transaction);
        }
    }
    
    private void notify(final Object message) throws ThreadPoolException {
        final List<ListenerToFilter> messageListeners = Messenger.getAsyncListenerToFilter(Messenger.Topics.COMMIT_TOPIC.get());
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
                        try {
                            ThreadPoolManager.getInstance().submit("Notification Processor", task);
                        }
                        catch (final ThreadPoolException e) {
                            CommitNotifier.out.log(Level.SEVERE, "Exception while processing commit notification asynchronously");
                        }
                    }
                }
            });
        }
    }
    
    public void afterCompletion(final int status) {
        Transaction transaction = null;
        try {
            if (status == 3) {
                transaction = DataAccess.getTransactionManager().getTransaction();
                final MessageAggregator messageAggregator = CommitNotifier.messageAggregatorMap.get(transaction);
                final ArrayList<OperationInfo> operationInfos = (ArrayList<OperationInfo>)messageAggregator.getMessage();
                if (operationInfos != null) {
                    this.sendNotification(operationInfos);
                }
            }
        }
        catch (final Exception e) {
            CommitNotifier.out.log(Level.SEVERE, "Exception occured while publishing message to NotificationTopic", e);
        }
        finally {
            this.remove(transaction);
        }
    }
    
    private void sendNotification(final Serializable obj) throws Exception {
        if (obj != null) {
            final List<OperationInfo> opInfos = (ArrayList)obj;
            this.notify(opInfos);
        }
    }
    
    public void beforeCompletion() {
    }
    
    static {
        CommitNotifier.out = Logger.getLogger(CommitNotifier.class.getName());
    }
}
