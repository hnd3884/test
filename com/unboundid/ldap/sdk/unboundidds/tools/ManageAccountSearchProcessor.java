package com.unboundid.ldap.sdk.unboundidds.tools;

import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.ArgumentParser;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.Filter;
import java.util.concurrent.LinkedBlockingQueue;

final class ManageAccountSearchProcessor
{
    private final LinkedBlockingQueue<Filter> filterQueue;
    private final ManageAccount manageAccount;
    private final ManageAccountProcessor manageAccountProcessor;
    private volatile ManageAccountSearchOperation activeSearchOperation;
    private final int simplePageSize;
    private final LDAPConnectionPool pool;
    private final List<ManageAccountSearchProcessorThread> searchProcessorThreads;
    private final String baseDN;
    private final String userIDAttribute;
    
    ManageAccountSearchProcessor(final ManageAccount manageAccount, final ManageAccountProcessor manageAccountProcessor, final LDAPConnectionPool pool) {
        this.manageAccount = manageAccount;
        this.manageAccountProcessor = manageAccountProcessor;
        this.pool = pool;
        final ArgumentParser parser = manageAccount.getArgumentParser();
        this.activeSearchOperation = null;
        this.baseDN = parser.getDNArgument("baseDN").getValue().toString();
        this.userIDAttribute = parser.getStringArgument("userIDAttribute").getValue();
        final IntegerArgument simplePageSizeArg = parser.getIntegerArgument("simplePageSize");
        if (simplePageSizeArg.isPresent()) {
            this.simplePageSize = simplePageSizeArg.getValue();
        }
        else {
            this.simplePageSize = -1;
        }
        final int numSearchThreads = parser.getIntegerArgument("numSearchThreads").getValue();
        if (numSearchThreads > 1) {
            this.filterQueue = new LinkedBlockingQueue<Filter>(100);
            this.searchProcessorThreads = new ArrayList<ManageAccountSearchProcessorThread>(numSearchThreads);
            for (int i = 1; i <= numSearchThreads; ++i) {
                final ManageAccountSearchProcessorThread t = new ManageAccountSearchProcessorThread(i, this);
                t.start();
                this.searchProcessorThreads.add(t);
            }
        }
        else {
            this.filterQueue = null;
            this.searchProcessorThreads = Collections.emptyList();
        }
    }
    
    void processFilter(final Filter filter) {
        if (this.filterQueue == null) {
            try {
                (this.activeSearchOperation = new ManageAccountSearchOperation(this.manageAccount, this.manageAccountProcessor, this.pool, this.baseDN, filter, this.simplePageSize)).doSearch();
            }
            finally {
                this.activeSearchOperation = null;
            }
        }
        else {
            while (!this.manageAccount.cancelRequested()) {
                try {
                    if (this.filterQueue.offer(filter, 100L, TimeUnit.MILLISECONDS)) {
                        return;
                    }
                    continue;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (!(e instanceof InterruptedException)) {
                        continue;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    void processFilter(final String filter) throws LDAPException {
        this.processFilter(Filter.create(filter));
    }
    
    void processUserID(final String userID) {
        this.processFilter(Filter.createEqualityFilter(this.userIDAttribute, userID));
    }
    
    ManageAccountSearchOperation getSearchOperation() {
        if (this.manageAccount.cancelRequested()) {
            return null;
        }
        Filter filter = this.filterQueue.poll();
        while (filter == null) {
            if (this.manageAccount.cancelRequested()) {
                return null;
            }
            if (this.manageAccount.allFiltersProvided()) {
                filter = this.filterQueue.poll();
                if (filter == null) {
                    return null;
                }
                break;
            }
            else {
                try {
                    filter = this.filterQueue.poll(100L, TimeUnit.MILLISECONDS);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (!(e instanceof InterruptedException)) {
                        continue;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        }
        return new ManageAccountSearchOperation(this.manageAccount, this.manageAccountProcessor, this.pool, this.baseDN, filter, this.simplePageSize);
    }
    
    void cancelSearches() {
        final ManageAccountSearchOperation o = this.activeSearchOperation;
        if (o != null) {
            o.cancelSearch();
        }
        for (final ManageAccountSearchProcessorThread t : this.searchProcessorThreads) {
            t.cancelSearch();
        }
    }
    
    void waitForCompletion() {
        if (this.filterQueue == null) {
            return;
        }
        while (!this.manageAccount.cancelRequested()) {
            if (this.manageAccount.allFiltersProvided() && this.filterQueue.peek() == null) {
                for (final ManageAccountSearchProcessorThread t : this.searchProcessorThreads) {
                    try {
                        t.join();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        if (!(e instanceof InterruptedException)) {
                            continue;
                        }
                        Thread.currentThread().interrupt();
                    }
                }
                return;
            }
            try {
                Thread.sleep(10L);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
    }
}
