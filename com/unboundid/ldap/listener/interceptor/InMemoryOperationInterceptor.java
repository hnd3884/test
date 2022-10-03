package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class InMemoryOperationInterceptor
{
    public void processAddRequest(final InMemoryInterceptedAddRequest request) throws LDAPException {
    }
    
    public void processAddResult(final InMemoryInterceptedAddResult result) {
    }
    
    public void processSimpleBindRequest(final InMemoryInterceptedSimpleBindRequest request) throws LDAPException {
    }
    
    public void processSimpleBindResult(final InMemoryInterceptedSimpleBindResult result) {
    }
    
    public void processSASLBindRequest(final InMemoryInterceptedSASLBindRequest request) throws LDAPException {
    }
    
    public void processSASLBindResult(final InMemoryInterceptedSASLBindResult result) {
    }
    
    public void processCompareRequest(final InMemoryInterceptedCompareRequest request) throws LDAPException {
    }
    
    public void processCompareResult(final InMemoryInterceptedCompareResult result) {
    }
    
    public void processDeleteRequest(final InMemoryInterceptedDeleteRequest request) throws LDAPException {
    }
    
    public void processDeleteResult(final InMemoryInterceptedDeleteResult result) {
    }
    
    public void processExtendedRequest(final InMemoryInterceptedExtendedRequest request) throws LDAPException {
    }
    
    public void processExtendedResult(final InMemoryInterceptedExtendedResult result) {
    }
    
    public void processModifyRequest(final InMemoryInterceptedModifyRequest request) throws LDAPException {
    }
    
    public void processModifyResult(final InMemoryInterceptedModifyResult result) {
    }
    
    public void processModifyDNRequest(final InMemoryInterceptedModifyDNRequest request) throws LDAPException {
    }
    
    public void processModifyDNResult(final InMemoryInterceptedModifyDNResult result) {
    }
    
    public void processSearchRequest(final InMemoryInterceptedSearchRequest request) throws LDAPException {
    }
    
    public void processSearchEntry(final InMemoryInterceptedSearchEntry entry) {
    }
    
    public void processSearchReference(final InMemoryInterceptedSearchReference reference) {
    }
    
    public void processSearchResult(final InMemoryInterceptedSearchResult result) {
    }
    
    public void processIntermediateResponse(final InMemoryInterceptedIntermediateResponse response) {
    }
}
