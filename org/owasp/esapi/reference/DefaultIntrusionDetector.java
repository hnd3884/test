package org.owasp.esapi.reference;

import java.util.Date;
import java.util.Stack;
import java.util.HashMap;
import java.util.Iterator;
import org.owasp.esapi.SecurityConfiguration;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.EnterpriseSecurityException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.IntrusionDetector;

public class DefaultIntrusionDetector implements IntrusionDetector
{
    private final Logger logger;
    
    public DefaultIntrusionDetector() {
        this.logger = ESAPI.getLogger("IntrusionDetector");
    }
    
    @Override
    public void addException(final Exception e) {
        if (ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            return;
        }
        if (e instanceof EnterpriseSecurityException) {
            this.logger.warning(Logger.SECURITY_FAILURE, ((EnterpriseSecurityException)e).getLogMessage(), e);
        }
        else {
            this.logger.warning(Logger.SECURITY_FAILURE, e.getMessage(), e);
        }
        final User user = ESAPI.authenticator().getCurrentUser();
        final String eventName = e.getClass().getName();
        if (e instanceof IntrusionException) {
            return;
        }
        try {
            this.addSecurityEvent(user, eventName);
        }
        catch (final IntrusionException ex) {
            final SecurityConfiguration.Threshold quota = ESAPI.securityConfiguration().getQuota(eventName);
            for (final String action : quota.actions) {
                final String message = "User exceeded quota of " + quota.count + " per " + quota.interval + " seconds for event " + eventName + ". Taking actions " + quota.actions;
                this.takeSecurityAction(action, message);
            }
        }
    }
    
    @Override
    public void addEvent(final String eventName, final String logMessage) throws IntrusionException {
        if (ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            return;
        }
        this.logger.warning(Logger.SECURITY_FAILURE, "Security event " + eventName + " received : " + logMessage);
        final User user = ESAPI.authenticator().getCurrentUser();
        try {
            this.addSecurityEvent(user, "event." + eventName);
        }
        catch (final IntrusionException ex) {
            final SecurityConfiguration.Threshold quota = ESAPI.securityConfiguration().getQuota("event." + eventName);
            for (final String action : quota.actions) {
                final String message = "User exceeded quota of " + quota.count + " per " + quota.interval + " seconds for event " + eventName + ". Taking actions " + quota.actions;
                this.takeSecurityAction(action, message);
            }
        }
    }
    
    private void takeSecurityAction(final String action, final String message) {
        if (ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            return;
        }
        if (action.equals("log")) {
            this.logger.fatal(Logger.SECURITY_FAILURE, "INTRUSION - " + message);
        }
        final User user = ESAPI.authenticator().getCurrentUser();
        if (user == User.ANONYMOUS) {
            return;
        }
        if (action.equals("disable")) {
            user.disable();
        }
        if (action.equals("logout")) {
            user.logout();
        }
    }
    
    private void addSecurityEvent(final User user, final String eventName) {
        if (ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
            return;
        }
        if (user.isAnonymous()) {
            return;
        }
        final HashMap eventMap = user.getEventMap();
        final SecurityConfiguration.Threshold threshold = ESAPI.securityConfiguration().getQuota(eventName);
        if (threshold != null) {
            Event event = eventMap.get(eventName);
            if (event == null) {
                event = new Event(eventName);
                eventMap.put(eventName, event);
            }
            event.increment(threshold.count, threshold.interval);
        }
    }
    
    private static class Event
    {
        public String key;
        public Stack times;
        
        public Event(final String key) {
            this.times = new Stack();
            this.key = key;
        }
        
        public void increment(final int count, final long interval) throws IntrusionException {
            if (ESAPI.securityConfiguration().getDisableIntrusionDetection()) {
                return;
            }
            final Date now = new Date();
            this.times.add(0, now);
            while (this.times.size() > count) {
                this.times.remove(this.times.size() - 1);
            }
            if (this.times.size() == count) {
                final Date past = (Date)this.times.get(count - 1);
                final long plong = past.getTime();
                final long nlong = now.getTime();
                if (nlong - plong < interval * 1000L) {
                    throw new IntrusionException("Threshold exceeded", "Exceeded threshold for " + this.key);
                }
            }
        }
    }
}
