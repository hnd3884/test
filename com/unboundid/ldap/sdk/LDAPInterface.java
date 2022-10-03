package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.ldif.LDIFException;
import java.util.Collection;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface LDAPInterface
{
    RootDSE getRootDSE() throws LDAPException;
    
    Schema getSchema() throws LDAPException;
    
    Schema getSchema(final String p0) throws LDAPException;
    
    SearchResultEntry getEntry(final String p0) throws LDAPException;
    
    SearchResultEntry getEntry(final String p0, final String... p1) throws LDAPException;
    
    LDAPResult add(final String p0, final Attribute... p1) throws LDAPException;
    
    LDAPResult add(final String p0, final Collection<Attribute> p1) throws LDAPException;
    
    LDAPResult add(final Entry p0) throws LDAPException;
    
    LDAPResult add(final String... p0) throws LDIFException, LDAPException;
    
    LDAPResult add(final AddRequest p0) throws LDAPException;
    
    LDAPResult add(final ReadOnlyAddRequest p0) throws LDAPException;
    
    CompareResult compare(final String p0, final String p1, final String p2) throws LDAPException;
    
    CompareResult compare(final CompareRequest p0) throws LDAPException;
    
    CompareResult compare(final ReadOnlyCompareRequest p0) throws LDAPException;
    
    LDAPResult delete(final String p0) throws LDAPException;
    
    LDAPResult delete(final DeleteRequest p0) throws LDAPException;
    
    LDAPResult delete(final ReadOnlyDeleteRequest p0) throws LDAPException;
    
    LDAPResult modify(final String p0, final Modification p1) throws LDAPException;
    
    LDAPResult modify(final String p0, final Modification... p1) throws LDAPException;
    
    LDAPResult modify(final String p0, final List<Modification> p1) throws LDAPException;
    
    LDAPResult modify(final String... p0) throws LDIFException, LDAPException;
    
    LDAPResult modify(final ModifyRequest p0) throws LDAPException;
    
    LDAPResult modify(final ReadOnlyModifyRequest p0) throws LDAPException;
    
    LDAPResult modifyDN(final String p0, final String p1, final boolean p2) throws LDAPException;
    
    LDAPResult modifyDN(final String p0, final String p1, final boolean p2, final String p3) throws LDAPException;
    
    LDAPResult modifyDN(final ModifyDNRequest p0) throws LDAPException;
    
    LDAPResult modifyDN(final ReadOnlyModifyDNRequest p0) throws LDAPException;
    
    SearchResult search(final String p0, final SearchScope p1, final String p2, final String... p3) throws LDAPSearchException;
    
    SearchResult search(final String p0, final SearchScope p1, final Filter p2, final String... p3) throws LDAPSearchException;
    
    SearchResult search(final SearchResultListener p0, final String p1, final SearchScope p2, final String p3, final String... p4) throws LDAPSearchException;
    
    SearchResult search(final SearchResultListener p0, final String p1, final SearchScope p2, final Filter p3, final String... p4) throws LDAPSearchException;
    
    SearchResult search(final String p0, final SearchScope p1, final DereferencePolicy p2, final int p3, final int p4, final boolean p5, final String p6, final String... p7) throws LDAPSearchException;
    
    SearchResult search(final String p0, final SearchScope p1, final DereferencePolicy p2, final int p3, final int p4, final boolean p5, final Filter p6, final String... p7) throws LDAPSearchException;
    
    SearchResult search(final SearchResultListener p0, final String p1, final SearchScope p2, final DereferencePolicy p3, final int p4, final int p5, final boolean p6, final String p7, final String... p8) throws LDAPSearchException;
    
    SearchResult search(final SearchResultListener p0, final String p1, final SearchScope p2, final DereferencePolicy p3, final int p4, final int p5, final boolean p6, final Filter p7, final String... p8) throws LDAPSearchException;
    
    SearchResult search(final SearchRequest p0) throws LDAPSearchException;
    
    SearchResult search(final ReadOnlySearchRequest p0) throws LDAPSearchException;
    
    SearchResultEntry searchForEntry(final String p0, final SearchScope p1, final String p2, final String... p3) throws LDAPSearchException;
    
    SearchResultEntry searchForEntry(final String p0, final SearchScope p1, final Filter p2, final String... p3) throws LDAPSearchException;
    
    SearchResultEntry searchForEntry(final String p0, final SearchScope p1, final DereferencePolicy p2, final int p3, final boolean p4, final String p5, final String... p6) throws LDAPSearchException;
    
    SearchResultEntry searchForEntry(final String p0, final SearchScope p1, final DereferencePolicy p2, final int p3, final boolean p4, final Filter p5, final String... p6) throws LDAPSearchException;
    
    SearchResultEntry searchForEntry(final SearchRequest p0) throws LDAPSearchException;
    
    SearchResultEntry searchForEntry(final ReadOnlySearchRequest p0) throws LDAPSearchException;
}
