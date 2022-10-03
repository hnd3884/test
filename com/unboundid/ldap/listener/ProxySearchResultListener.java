package com.unboundid.ldap.listener;

import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import java.util.Arrays;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResultListener;

final class ProxySearchResultListener implements SearchResultListener
{
    private static final long serialVersionUID = -1581507251328572490L;
    private final int messageID;
    private final LDAPListenerClientConnection clientConnection;
    
    ProxySearchResultListener(final LDAPListenerClientConnection clientConnection, final int messageID) {
        this.clientConnection = clientConnection;
        this.messageID = messageID;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        try {
            this.clientConnection.sendSearchResultEntry(this.messageID, searchEntry, searchEntry.getControls());
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        try {
            final SearchResultReferenceProtocolOp searchResultReferenceProtocolOp = new SearchResultReferenceProtocolOp(Arrays.asList(searchReference.getReferralURLs()));
            this.clientConnection.sendSearchResultReference(this.messageID, searchResultReferenceProtocolOp, searchReference.getControls());
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
}
