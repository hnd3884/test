package com.sun.jndi.ldap;

import javax.naming.event.NamingExceptionEvent;
import javax.naming.ldap.LdapName;
import java.util.EventObject;
import javax.naming.event.NamingEvent;
import javax.naming.InterruptedNamingException;
import javax.naming.Binding;
import javax.naming.ldap.HasControls;
import javax.naming.CompositeName;
import com.sun.jndi.toolkit.ctx.Continuation;
import javax.naming.ldap.Control;
import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.event.EventContext;
import javax.naming.event.NamingListener;
import java.util.Vector;

final class NamingEventNotifier implements Runnable
{
    private static final boolean debug = false;
    private Vector<NamingListener> namingListeners;
    private Thread worker;
    private LdapCtx context;
    private EventContext eventSrc;
    private EventSupport support;
    private NamingEnumeration<SearchResult> results;
    NotifierArgs info;
    
    NamingEventNotifier(final EventSupport support, final LdapCtx eventSrc, final NotifierArgs info, final NamingListener namingListener) throws NamingException {
        this.info = info;
        this.support = support;
        PersistentSearchControl persistentSearchControl;
        try {
            persistentSearchControl = new PersistentSearchControl(info.mask, true, true, true);
        }
        catch (final IOException rootCause) {
            final NamingException ex = new NamingException("Problem creating persistent search control");
            ex.setRootCause(rootCause);
            throw ex;
        }
        this.context = (LdapCtx)eventSrc.newInstance(new Control[] { persistentSearchControl });
        this.eventSrc = eventSrc;
        (this.namingListeners = new Vector<NamingListener>()).addElement(namingListener);
        (this.worker = Obj.helper.createThread(this)).setDaemon(true);
        this.worker.start();
    }
    
    void addNamingListener(final NamingListener namingListener) {
        this.namingListeners.addElement(namingListener);
    }
    
    void removeNamingListener(final NamingListener namingListener) {
        this.namingListeners.removeElement(namingListener);
    }
    
    boolean hasNamingListeners() {
        return this.namingListeners.size() > 0;
    }
    
    @Override
    public void run() {
        try {
            final Continuation continuation = new Continuation();
            continuation.setError(this, this.info.name);
            this.results = this.context.searchAux((this.info.name == null || this.info.name.equals("")) ? new CompositeName() : new CompositeName().add(this.info.name), this.info.filter, this.info.controls, true, false, continuation);
            ((LdapSearchEnumeration)this.results).setStartName(this.context.currentParsedDN);
            while (this.results.hasMore()) {
                final SearchResult searchResult = this.results.next();
                final Control[] array = (searchResult instanceof HasControls) ? ((HasControls)searchResult).getControls() : null;
                if (array != null) {
                    final int n = 0;
                    if (n >= array.length || !(array[n] instanceof EntryChangeResponseControl)) {
                        continue;
                    }
                    final EntryChangeResponseControl entryChangeResponseControl = (EntryChangeResponseControl)array[n];
                    final long changeNumber = entryChangeResponseControl.getChangeNumber();
                    switch (entryChangeResponseControl.getChangeType()) {
                        case 1: {
                            this.fireObjectAdded(searchResult, changeNumber);
                            continue;
                        }
                        case 2: {
                            this.fireObjectRemoved(searchResult, changeNumber);
                            continue;
                        }
                        case 4: {
                            this.fireObjectChanged(searchResult, changeNumber);
                            continue;
                        }
                        case 8: {
                            this.fireObjectRenamed(searchResult, entryChangeResponseControl.getPreviousDN(), changeNumber);
                            continue;
                        }
                    }
                }
            }
        }
        catch (final InterruptedNamingException ex) {}
        catch (final NamingException ex2) {
            this.fireNamingException(ex2);
            this.support.removeDeadNotifier(this.info);
        }
        finally {
            this.cleanup();
        }
    }
    
    private void cleanup() {
        try {
            if (this.results != null) {
                this.results.close();
                this.results = null;
            }
            if (this.context != null) {
                this.context.close();
                this.context = null;
            }
        }
        catch (final NamingException ex) {}
    }
    
    void stop() {
        if (this.worker != null) {
            this.worker.interrupt();
            this.worker = null;
        }
    }
    
    private void fireObjectAdded(final Binding binding, final long n) {
        if (this.namingListeners == null || this.namingListeners.size() == 0) {
            return;
        }
        this.support.queueEvent(new NamingEvent(this.eventSrc, 0, binding, null, new Long(n)), this.namingListeners);
    }
    
    private void fireObjectRemoved(final Binding binding, final long n) {
        if (this.namingListeners == null || this.namingListeners.size() == 0) {
            return;
        }
        this.support.queueEvent(new NamingEvent(this.eventSrc, 1, null, binding, new Long(n)), this.namingListeners);
    }
    
    private void fireObjectChanged(final Binding binding, final long n) {
        if (this.namingListeners == null || this.namingListeners.size() == 0) {
            return;
        }
        this.support.queueEvent(new NamingEvent(this.eventSrc, 3, binding, new Binding(binding.getName(), (Object)null, binding.isRelative()), new Long(n)), this.namingListeners);
    }
    
    private void fireObjectRenamed(final Binding binding, final String s, final long n) {
        if (this.namingListeners == null || this.namingListeners.size() == 0) {
            return;
        }
        Binding binding2 = null;
        try {
            final LdapName ldapName = new LdapName(s);
            if (ldapName.startsWith(this.context.currentParsedDN)) {
                binding2 = new Binding(ldapName.getSuffix(this.context.currentParsedDN.size()).toString(), (Object)null);
            }
        }
        catch (final NamingException ex) {}
        if (binding2 == null) {
            binding2 = new Binding(s, (Object)null, false);
        }
        this.support.queueEvent(new NamingEvent(this.eventSrc, 2, binding, binding2, new Long(n)), this.namingListeners);
    }
    
    private void fireNamingException(final NamingException ex) {
        if (this.namingListeners == null || this.namingListeners.size() == 0) {
            return;
        }
        this.support.queueEvent(new NamingExceptionEvent(this.eventSrc, ex), this.namingListeners);
    }
}
