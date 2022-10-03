package com.sun.jndi.ldap;

import javax.naming.event.EventContext;
import javax.naming.event.NamingExceptionEvent;
import java.util.EventObject;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotification;
import java.util.Iterator;
import javax.naming.directory.SearchControls;
import javax.naming.NamingException;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.ObjectChangeListener;
import javax.naming.event.NamingListener;
import javax.naming.ldap.UnsolicitedNotificationListener;
import java.util.Vector;
import java.util.Hashtable;

final class EventSupport
{
    private static final boolean debug = false;
    private LdapCtx ctx;
    private Hashtable<NotifierArgs, NamingEventNotifier> notifiers;
    private Vector<UnsolicitedNotificationListener> unsolicited;
    private EventQueue eventQueue;
    
    EventSupport(final LdapCtx ctx) {
        this.notifiers = new Hashtable<NotifierArgs, NamingEventNotifier>(11);
        this.unsolicited = null;
        this.ctx = ctx;
    }
    
    synchronized void addNamingListener(final String s, final int n, final NamingListener namingListener) throws NamingException {
        if (namingListener instanceof ObjectChangeListener || namingListener instanceof NamespaceChangeListener) {
            final NotifierArgs notifierArgs = new NotifierArgs(s, n, namingListener);
            final NamingEventNotifier namingEventNotifier = this.notifiers.get(notifierArgs);
            if (namingEventNotifier == null) {
                this.notifiers.put(notifierArgs, new NamingEventNotifier(this, this.ctx, notifierArgs, namingListener));
            }
            else {
                namingEventNotifier.addNamingListener(namingListener);
            }
        }
        if (namingListener instanceof UnsolicitedNotificationListener) {
            if (this.unsolicited == null) {
                this.unsolicited = new Vector<UnsolicitedNotificationListener>(3);
            }
            this.unsolicited.addElement((UnsolicitedNotificationListener)namingListener);
        }
    }
    
    synchronized void addNamingListener(final String s, final String s2, final SearchControls searchControls, final NamingListener namingListener) throws NamingException {
        if (namingListener instanceof ObjectChangeListener || namingListener instanceof NamespaceChangeListener) {
            final NotifierArgs notifierArgs = new NotifierArgs(s, s2, searchControls, namingListener);
            final NamingEventNotifier namingEventNotifier = this.notifiers.get(notifierArgs);
            if (namingEventNotifier == null) {
                this.notifiers.put(notifierArgs, new NamingEventNotifier(this, this.ctx, notifierArgs, namingListener));
            }
            else {
                namingEventNotifier.addNamingListener(namingListener);
            }
        }
        if (namingListener instanceof UnsolicitedNotificationListener) {
            if (this.unsolicited == null) {
                this.unsolicited = new Vector<UnsolicitedNotificationListener>(3);
            }
            this.unsolicited.addElement((UnsolicitedNotificationListener)namingListener);
        }
    }
    
    synchronized void removeNamingListener(final NamingListener namingListener) {
        final Iterator<NamingEventNotifier> iterator = this.notifiers.values().iterator();
        while (iterator.hasNext()) {
            final NamingEventNotifier namingEventNotifier = iterator.next();
            if (namingEventNotifier != null) {
                namingEventNotifier.removeNamingListener(namingListener);
                if (namingEventNotifier.hasNamingListeners()) {
                    continue;
                }
                namingEventNotifier.stop();
                iterator.remove();
            }
        }
        if (this.unsolicited != null) {
            this.unsolicited.removeElement(namingListener);
        }
    }
    
    synchronized boolean hasUnsolicited() {
        return this.unsolicited != null && this.unsolicited.size() > 0;
    }
    
    synchronized void removeDeadNotifier(final NotifierArgs notifierArgs) {
        this.notifiers.remove(notifierArgs);
    }
    
    synchronized void fireUnsolicited(final Object o) {
        if (this.unsolicited == null || this.unsolicited.size() == 0) {
            return;
        }
        if (o instanceof UnsolicitedNotification) {
            this.queueEvent(new UnsolicitedNotificationEvent(this.ctx, (UnsolicitedNotification)o), this.unsolicited);
        }
        else if (o instanceof NamingException) {
            this.queueEvent(new NamingExceptionEvent(this.ctx, (NamingException)o), this.unsolicited);
            this.unsolicited = null;
        }
    }
    
    synchronized void cleanup() {
        if (this.notifiers != null) {
            final Iterator<NamingEventNotifier> iterator = this.notifiers.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().stop();
            }
            this.notifiers = null;
        }
        if (this.eventQueue != null) {
            this.eventQueue.stop();
            this.eventQueue = null;
        }
    }
    
    synchronized void queueEvent(final EventObject eventObject, final Vector<? extends NamingListener> vector) {
        if (this.eventQueue == null) {
            this.eventQueue = new EventQueue();
        }
        this.eventQueue.enqueue(eventObject, (Vector<NamingListener>)vector.clone());
    }
}
