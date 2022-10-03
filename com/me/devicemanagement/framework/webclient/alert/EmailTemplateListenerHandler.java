package com.me.devicemanagement.framework.webclient.alert;

import java.util.LinkedList;

public class EmailTemplateListenerHandler
{
    protected LinkedList<EmailTemplateChangeListener> emailTemplateChangeListenerLinkedList;
    private static EmailTemplateListenerHandler emailTemplateListenerHandler;
    
    public EmailTemplateListenerHandler() {
        this.emailTemplateChangeListenerLinkedList = new LinkedList<EmailTemplateChangeListener>();
    }
    
    public static synchronized EmailTemplateListenerHandler getInstance() {
        if (EmailTemplateListenerHandler.emailTemplateListenerHandler == null) {
            EmailTemplateListenerHandler.emailTemplateListenerHandler = new EmailTemplateListenerHandler();
        }
        return EmailTemplateListenerHandler.emailTemplateListenerHandler;
    }
    
    public void addEmailTemplateListener(final EmailTemplateChangeListener listener) {
        this.emailTemplateChangeListenerLinkedList.add(listener);
    }
    
    public void invokeEmailTemplateChangeSuccessListeners(final EmailTemplateChangeEvent emailTemplateChangeEvent) {
        for (int listener = 0; listener < this.emailTemplateChangeListenerLinkedList.size(); ++listener) {
            final EmailTemplateChangeListener emailTemplateChangeListener = this.emailTemplateChangeListenerLinkedList.get(listener);
            emailTemplateChangeListener.emailTemplateModified(emailTemplateChangeEvent);
        }
    }
    
    static {
        EmailTemplateListenerHandler.emailTemplateListenerHandler = null;
    }
}
