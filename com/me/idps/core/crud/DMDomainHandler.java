package com.me.idps.core.crud;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DMDomainHandler
{
    static final int DOMAINS_ADDED = 301;
    static final int DOMAINS_DELETED = 302;
    static final int DOMAINS_UPDATED = 303;
    static final int DOMAIN_PRE_DELETE = 304;
    private static DMDomainHandler dmDomainHandler;
    private List<DMDomainListener> domainListenerList;
    
    private DMDomainHandler() {
        this.domainListenerList = new LinkedList<DMDomainListener>();
    }
    
    public static synchronized DMDomainHandler getInstance() {
        if (DMDomainHandler.dmDomainHandler == null) {
            DMDomainHandler.dmDomainHandler = new DMDomainHandler();
        }
        return DMDomainHandler.dmDomainHandler;
    }
    
    public void addDomainListener(final DMDomainListener listener) {
        this.domainListenerList.add(listener);
    }
    
    void removeDomainListener(final DMDomainListener listener) {
        this.domainListenerList.remove(listener);
    }
    
    void invokeDomainListeners(final DomainEvent[] domainEventArr, final int operation) {
        if (DomainDataProvider.getIsDmDomainSyncTDPresent() == 2) {
            if (operation == 301) {
                for (final DMDomainListener listener : this.domainListenerList) {
                    listener.domainsAdded(domainEventArr);
                }
            }
            else if (operation == 303) {
                for (final DMDomainListener listener : this.domainListenerList) {
                    listener.domainsUpdated(domainEventArr);
                }
            }
            else if (operation == 302) {
                for (final DMDomainListener listener : this.domainListenerList) {
                    listener.domainsDeleted(domainEventArr);
                }
            }
            else if (operation == 304) {
                for (final DMDomainListener listener : this.domainListenerList) {
                    listener.domainsPreDelete(domainEventArr);
                }
            }
        }
    }
    
    static {
        DMDomainHandler.dmDomainHandler = null;
    }
}
