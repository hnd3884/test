package com.adventnet.authentication.listener;

import javax.servlet.http.HttpSessionBindingEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSessionAttributeListener;

public class AttributeListener implements HttpSessionAttributeListener
{
    private static Logger logger;
    
    public AttributeListener() {
        AttributeListener.logger.log(Level.FINER, "initialized....");
    }
    
    public void attributeAdded(final HttpSessionBindingEvent event) {
        final String name = event.getName();
        final Object value = event.getValue();
        AttributeListener.logger.log(Level.FINER, "attribute by name : {0} added with value : {1}", new Object[] { name, value });
    }
    
    public void attributeRemoved(final HttpSessionBindingEvent event) {
        final String name = event.getName();
        final Object value = event.getValue();
        AttributeListener.logger.log(Level.FINER, "attribute by name : {0} removed with value : {1}", new Object[] { name, value });
    }
    
    public void attributeReplaced(final HttpSessionBindingEvent event) {
        final String name = event.getName();
        final Object value = event.getValue();
        AttributeListener.logger.log(Level.FINER, "attribute by name : {0} replaced with value : {1}", new Object[] { name, value });
    }
    
    static {
        AttributeListener.logger = Logger.getLogger(AttributeListener.class.getName());
    }
}
