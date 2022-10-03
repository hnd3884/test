package com.unboundid.ldap.listener;

import com.unboundid.util.Debug;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.Entry;
import java.util.Collection;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.ldap.protocol.SearchResultEntryProtocolOp;
import java.util.List;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CannedResponseRequestHandler extends LDAPListenerRequestHandler implements Serializable
{
    private static final long serialVersionUID = 6199105854736880833L;
    private final AddResponseProtocolOp addResponseProtocolOp;
    private final BindResponseProtocolOp bindResponseProtocolOp;
    private final CompareResponseProtocolOp compareResponseProtocolOp;
    private final DeleteResponseProtocolOp deleteResponseProtocolOp;
    private final ExtendedResponseProtocolOp extendedResponseProtocolOp;
    private final ModifyResponseProtocolOp modifyResponseProtocolOp;
    private final ModifyDNResponseProtocolOp modifyDNResponseProtocolOp;
    private final List<SearchResultEntryProtocolOp> searchEntryProtocolOps;
    private final List<SearchResultReferenceProtocolOp> searchReferenceProtocolOps;
    private final SearchResultDoneProtocolOp searchResultDoneProtocolOp;
    private final LDAPListenerClientConnection clientConnection;
    
    public CannedResponseRequestHandler() {
        this(ResultCode.SUCCESS, null, null, null);
    }
    
    public CannedResponseRequestHandler(final ResultCode resultCode, final String matchedDN, final String diagnosticMessage, final List<String> referralURLs) {
        this(resultCode, matchedDN, diagnosticMessage, referralURLs, null, null);
    }
    
    public CannedResponseRequestHandler(final ResultCode resultCode, final String matchedDN, final String diagnosticMessage, final List<String> referralURLs, final Collection<? extends Entry> searchEntries, final Collection<SearchResultReference> searchReferences) {
        Validator.ensureNotNull(resultCode);
        this.clientConnection = null;
        final int rc = resultCode.intValue();
        this.addResponseProtocolOp = new AddResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs);
        this.bindResponseProtocolOp = new BindResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs, null);
        this.compareResponseProtocolOp = new CompareResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs);
        this.deleteResponseProtocolOp = new DeleteResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs);
        this.extendedResponseProtocolOp = new ExtendedResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs, null, null);
        this.modifyResponseProtocolOp = new ModifyResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs);
        this.modifyDNResponseProtocolOp = new ModifyDNResponseProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs);
        this.searchResultDoneProtocolOp = new SearchResultDoneProtocolOp(rc, matchedDN, diagnosticMessage, referralURLs);
        if (searchEntries == null || searchEntries.isEmpty()) {
            this.searchEntryProtocolOps = Collections.emptyList();
        }
        else {
            final ArrayList<SearchResultEntryProtocolOp> l = new ArrayList<SearchResultEntryProtocolOp>(searchEntries.size());
            for (final Entry e : searchEntries) {
                l.add(new SearchResultEntryProtocolOp(e));
            }
            this.searchEntryProtocolOps = Collections.unmodifiableList((List<? extends SearchResultEntryProtocolOp>)l);
        }
        if (searchReferences == null || searchReferences.isEmpty()) {
            this.searchReferenceProtocolOps = Collections.emptyList();
        }
        else {
            final ArrayList<SearchResultReferenceProtocolOp> i = new ArrayList<SearchResultReferenceProtocolOp>(searchReferences.size());
            for (final SearchResultReference r : searchReferences) {
                i.add(new SearchResultReferenceProtocolOp(r));
            }
            this.searchReferenceProtocolOps = Collections.unmodifiableList((List<? extends SearchResultReferenceProtocolOp>)i);
        }
    }
    
    private CannedResponseRequestHandler(final CannedResponseRequestHandler h, final LDAPListenerClientConnection c) {
        this.addResponseProtocolOp = h.addResponseProtocolOp;
        this.bindResponseProtocolOp = h.bindResponseProtocolOp;
        this.compareResponseProtocolOp = h.compareResponseProtocolOp;
        this.deleteResponseProtocolOp = h.deleteResponseProtocolOp;
        this.extendedResponseProtocolOp = h.extendedResponseProtocolOp;
        this.modifyResponseProtocolOp = h.modifyResponseProtocolOp;
        this.modifyDNResponseProtocolOp = h.modifyDNResponseProtocolOp;
        this.searchEntryProtocolOps = h.searchEntryProtocolOps;
        this.searchReferenceProtocolOps = h.searchReferenceProtocolOps;
        this.searchResultDoneProtocolOp = h.searchResultDoneProtocolOp;
        this.clientConnection = c;
    }
    
    @Override
    public CannedResponseRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        return new CannedResponseRequestHandler(this, connection);
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.addResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.bindResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.compareResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.deleteResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.extendedResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.modifyResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        return new LDAPMessage(messageID, this.modifyDNResponseProtocolOp, Collections.emptyList());
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        for (final SearchResultEntryProtocolOp e : this.searchEntryProtocolOps) {
            try {
                this.clientConnection.sendSearchResultEntry(messageID, e, new Control[0]);
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        for (final SearchResultReferenceProtocolOp r : this.searchReferenceProtocolOps) {
            try {
                this.clientConnection.sendSearchResultReference(messageID, r, new Control[0]);
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        return new LDAPMessage(messageID, this.searchResultDoneProtocolOp, Collections.emptyList());
    }
}
