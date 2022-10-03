package com.me.devicemanagement.onpremise.server.mail;

import java.util.ArrayList;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import java.util.List;
import java.util.logging.Logger;

public class MailHandler
{
    private static String sourceClass;
    private static Logger logger;
    private static MailHandler mailHandler;
    private static List<MailListener> mailListenerList;
    
    private MailHandler() {
    }
    
    public static synchronized MailHandler getInstance() {
        if (MailHandler.mailHandler == null) {
            MailHandler.mailHandler = new MailHandler();
        }
        return MailHandler.mailHandler;
    }
    
    public void addToMailQueue(final MailDetails mailDetails, final int priority) {
        try {
            final DCQueue queue = DCQueueHandler.getQueue("mail-queue");
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = mailDetails;
            queue.addToQueue(queueData);
        }
        catch (final Exception ex) {
            MailHandler.logger.log(Level.SEVERE, "Exception occurred while adding to Mail Queue : {0}", ex);
        }
    }
    
    public void addMailListener(final MailListener mailListener) {
        MailHandler.logger.log(Level.INFO, "addMailListener() called : {0}", mailListener.getClass().getName());
        MailHandler.mailListenerList.add(mailListener);
    }
    
    public void removeMailListener(final MailListener mailListener) {
        MailHandler.logger.log(Level.INFO, "removeMailListener() called : {0}", mailListener.getClass().getName());
        MailHandler.mailListenerList.remove(mailListener);
    }
    
    public void invokeMailConfigureListener() {
        for (int s = 0; s < MailHandler.mailListenerList.size(); ++s) {
            MailHandler.logger.log(Level.INFO, "invokeMailConfigureListener() called : {0}", MailHandler.mailListenerList.get(s).getClass().getName());
            final MailListener listener = MailHandler.mailListenerList.get(s);
            listener.mailConfigured();
        }
    }
    
    static {
        MailHandler.sourceClass = "MailHandler";
        MailHandler.logger = Logger.getLogger(MailHandler.class.getName());
        MailHandler.mailHandler = new MailHandler();
        MailHandler.mailListenerList = new ArrayList<MailListener>();
    }
}
