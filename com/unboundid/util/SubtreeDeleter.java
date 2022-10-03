package com.unboundid.util;

import com.unboundid.ldap.sdk.AbstractConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.SubentriesRequestControl;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import java.util.Iterator;
import com.unboundid.ldap.sdk.SearchRequest;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldap.sdk.LDAPSearchException;
import java.util.SortedSet;
import java.util.TreeSet;
import com.unboundid.ldap.sdk.unboundidds.extensions.SetSubtreeAccessibilityExtendedRequest;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeletedEntryAccessRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.ReturnConflictEntriesRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.PermitUnindexedSearchRequestControl;
import com.unboundid.ldap.sdk.controls.ManageDsaITRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.HardDeleteRequestControl;
import com.unboundid.ldap.sdk.RootDSE;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPInterface;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import java.util.List;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SubtreeDeleter
{
    private boolean deleteBaseEntry;
    private boolean useHardDeleteControlIfAvailable;
    private boolean useManageDSAITControlIfAvailable;
    private boolean usePermitUnindexedSearchControlIfAvailable;
    private boolean useReturnConflictEntriesRequestControlIfAvailable;
    private boolean useSimplePagedResultsControlIfAvailable;
    private boolean useSoftDeletedEntryAccessControlIfAvailable;
    private boolean useSubentriesControlIfAvailable;
    private boolean useSetSubtreeAccessibilityOperationIfAvailable;
    private int searchRequestSizeLimit;
    private int simplePagedResultsPageSize;
    private FixedRateBarrier deleteRateLimiter;
    private List<Control> additionalSearchControls;
    private List<Control> additionalDeleteControls;
    
    public SubtreeDeleter() {
        this.deleteBaseEntry = true;
        this.useHardDeleteControlIfAvailable = true;
        this.useManageDSAITControlIfAvailable = true;
        this.usePermitUnindexedSearchControlIfAvailable = false;
        this.useReturnConflictEntriesRequestControlIfAvailable = true;
        this.useSimplePagedResultsControlIfAvailable = true;
        this.useSoftDeletedEntryAccessControlIfAvailable = true;
        this.useSubentriesControlIfAvailable = true;
        this.useSetSubtreeAccessibilityOperationIfAvailable = false;
        this.searchRequestSizeLimit = 0;
        this.simplePagedResultsPageSize = 100;
        this.deleteRateLimiter = null;
        this.additionalSearchControls = Collections.emptyList();
        this.additionalDeleteControls = Collections.emptyList();
    }
    
    public boolean deleteBaseEntry() {
        return this.deleteBaseEntry;
    }
    
    public void setDeleteBaseEntry(final boolean deleteBaseEntry) {
        this.deleteBaseEntry = deleteBaseEntry;
    }
    
    public boolean useSetSubtreeAccessibilityOperationIfAvailable() {
        return this.useSetSubtreeAccessibilityOperationIfAvailable;
    }
    
    public void setUseSetSubtreeAccessibilityOperationIfAvailable(final boolean useSetSubtreeAccessibilityOperationIfAvailable) {
        this.useSetSubtreeAccessibilityOperationIfAvailable = useSetSubtreeAccessibilityOperationIfAvailable;
    }
    
    public boolean useSimplePagedResultsControlIfAvailable() {
        return this.useSimplePagedResultsControlIfAvailable;
    }
    
    public void setUseSimplePagedResultsControlIfAvailable(final boolean useSimplePagedResultsControlIfAvailable) {
        this.useSimplePagedResultsControlIfAvailable = useSimplePagedResultsControlIfAvailable;
    }
    
    public int getSimplePagedResultsPageSize() {
        return this.simplePagedResultsPageSize;
    }
    
    public void setSimplePagedResultsPageSize(final int simplePagedResultsPageSize) {
        Validator.ensureTrue(simplePagedResultsPageSize >= 1, "SubtreeDeleter.simplePagedResultsPageSize must be greater than or equal to 1.");
        this.simplePagedResultsPageSize = simplePagedResultsPageSize;
    }
    
    public boolean useManageDSAITControlIfAvailable() {
        return this.useManageDSAITControlIfAvailable;
    }
    
    public void setUseManageDSAITControlIfAvailable(final boolean useManageDSAITControlIfAvailable) {
        this.useManageDSAITControlIfAvailable = useManageDSAITControlIfAvailable;
    }
    
    public boolean usePermitUnindexedSearchControlIfAvailable() {
        return this.usePermitUnindexedSearchControlIfAvailable;
    }
    
    public void setUsePermitUnindexedSearchControlIfAvailable(final boolean usePermitUnindexedSearchControlIfAvailable) {
        this.usePermitUnindexedSearchControlIfAvailable = usePermitUnindexedSearchControlIfAvailable;
    }
    
    public boolean useSubentriesControlIfAvailable() {
        return this.useSubentriesControlIfAvailable;
    }
    
    public void setUseSubentriesControlIfAvailable(final boolean useSubentriesControlIfAvailable) {
        this.useSubentriesControlIfAvailable = useSubentriesControlIfAvailable;
    }
    
    public boolean useReturnConflictEntriesRequestControlIfAvailable() {
        return this.useReturnConflictEntriesRequestControlIfAvailable;
    }
    
    public void setUseReturnConflictEntriesRequestControlIfAvailable(final boolean useReturnConflictEntriesRequestControlIfAvailable) {
        this.useReturnConflictEntriesRequestControlIfAvailable = useReturnConflictEntriesRequestControlIfAvailable;
    }
    
    public boolean useSoftDeletedEntryAccessControlIfAvailable() {
        return this.useSoftDeletedEntryAccessControlIfAvailable;
    }
    
    public void setUseSoftDeletedEntryAccessControlIfAvailable(final boolean useSoftDeletedEntryAccessControlIfAvailable) {
        this.useSoftDeletedEntryAccessControlIfAvailable = useSoftDeletedEntryAccessControlIfAvailable;
    }
    
    public boolean useHardDeleteControlIfAvailable() {
        return this.useHardDeleteControlIfAvailable;
    }
    
    public void setUseHardDeleteControlIfAvailable(final boolean useHardDeleteControlIfAvailable) {
        this.useHardDeleteControlIfAvailable = useHardDeleteControlIfAvailable;
    }
    
    public List<Control> getAdditionalSearchControls() {
        return this.additionalSearchControls;
    }
    
    public void setAdditionalSearchControls(final Control... additionalSearchControls) {
        this.setAdditionalSearchControls(Arrays.asList(additionalSearchControls));
    }
    
    public void setAdditionalSearchControls(final List<Control> additionalSearchControls) {
        this.additionalSearchControls = Collections.unmodifiableList((List<? extends Control>)new ArrayList<Control>(additionalSearchControls));
    }
    
    public List<Control> getAdditionalDeleteControls() {
        return this.additionalDeleteControls;
    }
    
    public void setAdditionalDeleteControls(final Control... additionalDeleteControls) {
        this.setAdditionalDeleteControls(Arrays.asList(additionalDeleteControls));
    }
    
    public void setAdditionalDeleteControls(final List<Control> additionalDeleteControls) {
        this.additionalDeleteControls = Collections.unmodifiableList((List<? extends Control>)new ArrayList<Control>(additionalDeleteControls));
    }
    
    public int getSearchRequestSizeLimit() {
        return this.searchRequestSizeLimit;
    }
    
    public void setSearchRequestSizeLimit(final int searchRequestSizeLimit) {
        if (searchRequestSizeLimit <= 0) {
            this.searchRequestSizeLimit = 0;
        }
        else {
            this.searchRequestSizeLimit = searchRequestSizeLimit;
        }
    }
    
    public FixedRateBarrier getDeleteRateLimiter() {
        return this.deleteRateLimiter;
    }
    
    public void setDeleteRateLimiter(final FixedRateBarrier deleteRateLimiter) {
        this.deleteRateLimiter = deleteRateLimiter;
    }
    
    public SubtreeDeleterResult delete(final LDAPInterface connection, final String baseDN) throws LDAPException {
        return this.delete(connection, new DN(baseDN));
    }
    
    public SubtreeDeleterResult delete(final LDAPInterface connection, final DN baseDN) {
        final AtomicReference<RootDSE> rootDSE = new AtomicReference<RootDSE>();
        final boolean useSetSubtreeAccessibility = this.useSetSubtreeAccessibilityOperationIfAvailable && supportsExtendedRequest(connection, rootDSE, "1.3.6.1.4.1.30221.2.6.19") && supportsExtendedRequest(connection, rootDSE, "1.3.6.1.4.1.4203.1.11.3");
        final boolean usePagedResults = this.useSimplePagedResultsControlIfAvailable && supportsControl(connection, rootDSE, "1.2.840.113556.1.4.319");
        final boolean useSubentries = this.useSubentriesControlIfAvailable && supportsControl(connection, rootDSE, "1.3.6.1.4.1.7628.5.101.1");
        final List<Control> searchControls = new ArrayList<Control>(10);
        searchControls.addAll(this.additionalSearchControls);
        final List<Control> deleteControls = new ArrayList<Control>(10);
        deleteControls.addAll(this.additionalDeleteControls);
        if (this.useHardDeleteControlIfAvailable && supportsControl(connection, rootDSE, "1.3.6.1.4.1.30221.2.5.22")) {
            deleteControls.add(new HardDeleteRequestControl(false));
        }
        if (this.useManageDSAITControlIfAvailable && supportsControl(connection, rootDSE, "2.16.840.1.113730.3.4.2")) {
            final ManageDsaITRequestControl c = new ManageDsaITRequestControl(false);
            searchControls.add(c);
            deleteControls.add(c);
        }
        if (this.usePermitUnindexedSearchControlIfAvailable && supportsControl(connection, rootDSE, "1.3.6.1.4.1.30221.2.5.55")) {
            searchControls.add(new PermitUnindexedSearchRequestControl(false));
        }
        if (this.useReturnConflictEntriesRequestControlIfAvailable && supportsControl(connection, rootDSE, "1.3.6.1.4.1.30221.2.5.13")) {
            searchControls.add(new ReturnConflictEntriesRequestControl(false));
        }
        if (this.useSoftDeletedEntryAccessControlIfAvailable && supportsControl(connection, rootDSE, "1.3.6.1.4.1.30221.2.5.24")) {
            searchControls.add(new SoftDeletedEntryAccessRequestControl(false, true, false));
        }
        return delete(connection, baseDN, this.deleteBaseEntry, useSetSubtreeAccessibility, usePagedResults, this.searchRequestSizeLimit, this.simplePagedResultsPageSize, useSubentries, searchControls, deleteControls, this.deleteRateLimiter);
    }
    
    private static SubtreeDeleterResult delete(final LDAPInterface connection, final DN baseDN, final boolean deleteBaseEntry, final boolean useSetSubtreeAccessibilityOperation, final boolean useSimplePagedResultsControl, final int searchRequestSizeLimit, final int pageSize, final boolean useSubentriesControl, final List<Control> searchControls, final List<Control> deleteControls, final FixedRateBarrier deleteRateLimiter) {
        if (useSetSubtreeAccessibilityOperation) {
            final ExtendedResult setInaccessibleResult = setInaccessible(connection, baseDN);
            if (setInaccessibleResult != null) {
                return new SubtreeDeleterResult(setInaccessibleResult, false, null, 0L, new TreeMap<DN, LDAPResult>());
            }
        }
        SubtreeDeleterResult result;
        if (useSimplePagedResultsControl) {
            result = deleteEntriesWithSimplePagedResults(connection, baseDN, deleteBaseEntry, searchRequestSizeLimit, pageSize, useSubentriesControl, searchControls, deleteControls, deleteRateLimiter);
        }
        else {
            result = deleteEntriesWithoutSimplePagedResults(connection, baseDN, deleteBaseEntry, searchRequestSizeLimit, useSubentriesControl, searchControls, deleteControls, deleteRateLimiter);
        }
        if (!result.completelySuccessful() || !useSetSubtreeAccessibilityOperation) {
            return new SubtreeDeleterResult(null, useSetSubtreeAccessibilityOperation, result.getSearchError(), result.getEntriesDeleted(), result.getDeleteErrorsTreeMap());
        }
        final ExtendedResult removeAccessibilityRestrictionResult = removeAccessibilityRestriction(connection, baseDN);
        if (removeAccessibilityRestrictionResult.getResultCode() == ResultCode.SUCCESS) {
            return new SubtreeDeleterResult(null, false, null, result.getEntriesDeleted(), result.getDeleteErrorsTreeMap());
        }
        return new SubtreeDeleterResult(removeAccessibilityRestrictionResult, true, null, result.getEntriesDeleted(), result.getDeleteErrorsTreeMap());
    }
    
    private static ExtendedResult setInaccessible(final LDAPInterface connection, final DN baseDN) {
        final ExtendedResult genericWhoAmIResult = processExtendedOperation(connection, new WhoAmIExtendedRequest());
        if (genericWhoAmIResult.getResultCode() != ResultCode.SUCCESS) {
            return genericWhoAmIResult;
        }
        final WhoAmIExtendedResult whoAmIResult = (WhoAmIExtendedResult)genericWhoAmIResult;
        final String authzID = whoAmIResult.getAuthorizationID();
        if (!authzID.startsWith("dn:")) {
            return new ExtendedResult(-1, ResultCode.LOCAL_ERROR, UtilityMessages.ERR_SUBTREE_DELETER_INTERFACE_WHO_AM_I_AUTHZ_ID_NOT_DN.get(authzID), null, StaticUtils.NO_STRINGS, null, null, StaticUtils.NO_CONTROLS);
        }
        final String authzDN = authzID.substring(3);
        final ExtendedResult setInaccessibleResult = processExtendedOperation(connection, SetSubtreeAccessibilityExtendedRequest.createSetHiddenRequest(baseDN.toString(), authzDN, new Control[0]));
        if (setInaccessibleResult.getResultCode() == ResultCode.SUCCESS) {
            return null;
        }
        return setInaccessibleResult;
    }
    
    private static SubtreeDeleterResult deleteEntriesWithSimplePagedResults(final LDAPInterface connection, final DN baseDN, final boolean deleteBaseEntry, final int searchRequestSizeLimit, final int pageSize, final boolean useSubentriesControl, final List<Control> searchControls, final List<Control> deleteControls, final FixedRateBarrier deleteRateLimiter) {
        final TreeSet<DN> dnsToDelete = new TreeSet<DN>();
        if (useSubentriesControl) {
            try {
                final SearchRequest searchRequest = createSubentriesSearchRequest(baseDN, 0, searchControls, dnsToDelete);
                doPagedResultsSearch(connection, searchRequest, pageSize);
            }
            catch (final LDAPSearchException e) {
                Debug.debugException(e);
                return new SubtreeDeleterResult(null, false, e.getSearchResult(), 0L, new TreeMap<DN, LDAPResult>());
            }
        }
        try {
            final SearchRequest searchRequest = createNonSubentriesSearchRequest(baseDN, 0, searchControls, dnsToDelete);
            doPagedResultsSearch(connection, searchRequest, pageSize);
        }
        catch (final LDAPSearchException e) {
            Debug.debugException(e);
            return new SubtreeDeleterResult(null, false, e.getSearchResult(), 0L, new TreeMap<DN, LDAPResult>());
        }
        if (!deleteBaseEntry) {
            dnsToDelete.remove(baseDN);
        }
        final AtomicReference<SearchResult> searchError = new AtomicReference<SearchResult>();
        final AtomicLong entriesDeleted = new AtomicLong(0L);
        final TreeMap<DN, LDAPResult> deleteErrors = new TreeMap<DN, LDAPResult>();
        final Iterator<DN> iterator = dnsToDelete.descendingIterator();
        while (iterator.hasNext()) {
            final DN dn = iterator.next();
            if (!deleteErrors.containsKey(dn) && !deleteEntry(connection, dn, deleteControls, entriesDeleted, deleteErrors, deleteRateLimiter, searchRequestSizeLimit, searchControls, useSubentriesControl, searchError)) {
                for (DN parentDN = dn.getParent(); parentDN != null && parentDN.isDescendantOf(baseDN, true); parentDN = parentDN.getParent()) {
                    if (deleteErrors.containsKey(parentDN)) {
                        break;
                    }
                    deleteErrors.put(parentDN, new LDAPResult(-1, ResultCode.NOT_ALLOWED_ON_NONLEAF, UtilityMessages.ERR_SUBTREE_DELETER_SKIPPING_UNDELETABLE_ANCESTOR.get(String.valueOf(parentDN), String.valueOf(dn)), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS));
                }
            }
        }
        return new SubtreeDeleterResult(null, false, null, entriesDeleted.get(), deleteErrors);
    }
    
    private static SearchRequest createSubentriesSearchRequest(final DN baseDN, final int searchRequestSizeLimit, final List<Control> controls, final SortedSet<DN> dnSet) {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ldapSubentry");
        final SubtreeDeleterSearchResultListener searchListener = new SubtreeDeleterSearchResultListener(baseDN, filter, dnSet);
        final SearchRequest searchRequest = new SearchRequest(searchListener, baseDN.toString(), SearchScope.SUB, DereferencePolicy.NEVER, searchRequestSizeLimit, 0, false, filter, new String[] { "1.1" });
        for (final Control c : controls) {
            searchRequest.addControl(c);
        }
        searchRequest.addControl(new SubentriesRequestControl(false));
        return searchRequest;
    }
    
    private static SearchRequest createNonSubentriesSearchRequest(final DN baseDN, final int searchRequestSizeLimit, final List<Control> controls, final SortedSet<DN> dnSet) {
        final Filter filter = Filter.createPresenceFilter("objectClass");
        final SubtreeDeleterSearchResultListener searchListener = new SubtreeDeleterSearchResultListener(baseDN, filter, dnSet);
        final SearchRequest searchRequest = new SearchRequest(searchListener, baseDN.toString(), SearchScope.SUB, DereferencePolicy.NEVER, searchRequestSizeLimit, 0, false, filter, new String[] { "1.1" });
        for (final Control c : controls) {
            searchRequest.addControl(c);
        }
        return searchRequest;
    }
    
    private static void doPagedResultsSearch(final LDAPInterface connection, final SearchRequest searchRequest, final int pageSize) throws LDAPSearchException {
        final SubtreeDeleterSearchResultListener searchListener = (SubtreeDeleterSearchResultListener)searchRequest.getSearchResultListener();
        ASN1OctetString pagedResultsCookie = null;
        while (true) {
            final SearchRequest pagedResultsSearchRequest = searchRequest.duplicate();
            pagedResultsSearchRequest.addControl(new SimplePagedResultsControl(pageSize, pagedResultsCookie, true));
            SearchResult searchResult;
            try {
                searchResult = connection.search(pagedResultsSearchRequest);
            }
            catch (final LDAPSearchException e) {
                Debug.debugException(e);
                searchResult = e.getSearchResult();
            }
            if (searchResult.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                return;
            }
            if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                throw new LDAPSearchException(searchResult);
            }
            if (searchListener.getFirstException() != null) {
                throw new LDAPSearchException(searchListener.getFirstException());
            }
            SimplePagedResultsControl responseControl;
            try {
                responseControl = SimplePagedResultsControl.get(searchResult);
            }
            catch (final LDAPException e2) {
                Debug.debugException(e2);
                throw new LDAPSearchException(e2);
            }
            if (responseControl == null) {
                throw new LDAPSearchException(ResultCode.CONTROL_NOT_FOUND, UtilityMessages.ERR_SUBTREE_DELETER_MISSING_PAGED_RESULTS_RESPONSE.get(searchRequest.getBaseDN(), searchRequest.getFilter()));
            }
            if (!responseControl.moreResultsToReturn()) {
                return;
            }
            pagedResultsCookie = responseControl.getCookie();
        }
    }
    
    private static boolean deleteEntry(final LDAPInterface connection, final DN dn, final List<Control> deleteControls, final AtomicLong entriesDeleted, final SortedMap<DN, LDAPResult> deleteErrors, final FixedRateBarrier deleteRateLimiter, final int searchRequestSizeLimit, final List<Control> searchControls, final boolean useSubentriesControl, final AtomicReference<SearchResult> searchError) {
        if (deleteRateLimiter != null) {
            deleteRateLimiter.await();
        }
        LDAPResult deleteResult;
        try {
            deleteResult = connection.delete(dn.toString());
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            deleteResult = e.toLDAPResult();
        }
        final ResultCode resultCode = deleteResult.getResultCode();
        if (resultCode == ResultCode.SUCCESS) {
            entriesDeleted.incrementAndGet();
            return true;
        }
        if (resultCode == ResultCode.NO_SUCH_OBJECT) {
            return true;
        }
        if (resultCode == ResultCode.NOT_ALLOWED_ON_NONLEAF) {
            return searchAndDelete(connection, dn, searchRequestSizeLimit, searchControls, useSubentriesControl, searchError, deleteControls, entriesDeleted, deleteErrors, deleteRateLimiter);
        }
        deleteErrors.put(dn, deleteResult);
        return false;
    }
    
    private static boolean searchAndDelete(final LDAPInterface connection, final DN baseDN, final int searchRequestSizeLimit, final List<Control> searchControls, final boolean useSubentriesControl, final AtomicReference<SearchResult> searchError, final List<Control> deleteControls, final AtomicLong entriesDeleted, final SortedMap<DN, LDAPResult> deleteErrors, final FixedRateBarrier deleteRateLimiter) {
        while (true) {
            SearchResult subentriesSearchResult = null;
            final TreeSet<DN> dnsToDelete = new TreeSet<DN>();
            if (useSubentriesControl) {
                try {
                    subentriesSearchResult = connection.search(createSubentriesSearchRequest(baseDN, searchRequestSizeLimit, searchControls, dnsToDelete));
                }
                catch (final LDAPSearchException e) {
                    Debug.debugException(e);
                    subentriesSearchResult = e.getSearchResult();
                }
            }
            SearchResult nonSubentriesSearchResult;
            try {
                nonSubentriesSearchResult = connection.search(createNonSubentriesSearchRequest(baseDN, searchRequestSizeLimit, searchControls, dnsToDelete));
            }
            catch (final LDAPSearchException e2) {
                Debug.debugException(e2);
                nonSubentriesSearchResult = e2.getSearchResult();
            }
            if (dnsToDelete.isEmpty()) {
                if (subentriesSearchResult != null) {
                    switch (subentriesSearchResult.getResultCode().intValue()) {
                        case 0:
                        case 32: {
                            break;
                        }
                        default: {
                            searchError.compareAndSet(null, subentriesSearchResult);
                            return false;
                        }
                    }
                }
                switch (nonSubentriesSearchResult.getResultCode().intValue()) {
                    case 0:
                    case 32: {
                        return true;
                    }
                    default: {
                        searchError.compareAndSet(null, nonSubentriesSearchResult);
                        return false;
                    }
                }
            }
            else {
                boolean anySuccessful = false;
                boolean allSuccessful = true;
                final TreeSet<DN> ancestorsToSkip = new TreeSet<DN>();
                final DeleteRequest deleteRequest = new DeleteRequest("");
                deleteRequest.setControls(deleteControls);
                for (final DN dn : dnsToDelete.descendingSet()) {
                    if (deleteErrors.containsKey(dn)) {
                        allSuccessful = false;
                    }
                    else if (ancestorsToSkip.contains(dn)) {
                        allSuccessful = false;
                    }
                    else {
                        if (deleteRateLimiter != null) {
                            deleteRateLimiter.await();
                        }
                        LDAPResult deleteResult;
                        try {
                            deleteRequest.setDN(dn);
                            deleteResult = connection.delete(deleteRequest);
                        }
                        catch (final LDAPException e3) {
                            Debug.debugException(e3);
                            deleteResult = e3.toLDAPResult();
                        }
                        switch (deleteResult.getResultCode().intValue()) {
                            case 0: {
                                anySuccessful = true;
                                entriesDeleted.incrementAndGet();
                                continue;
                            }
                            case 32: {
                                anySuccessful = true;
                                continue;
                            }
                            case 66: {
                                if (dn.equals(baseDN)) {
                                    allSuccessful = false;
                                    continue;
                                }
                                if (searchAndDelete(connection, dn, searchRequestSizeLimit, searchControls, useSubentriesControl, searchError, deleteControls, entriesDeleted, deleteErrors, deleteRateLimiter)) {
                                    anySuccessful = true;
                                    continue;
                                }
                                allSuccessful = false;
                                for (DN parentDN = dn.getParent(); parentDN != null; parentDN = parentDN.getParent()) {
                                    ancestorsToSkip.add(parentDN);
                                }
                                continue;
                            }
                            default: {
                                deleteErrors.put(dn, deleteResult);
                                for (DN parentDN = dn.getParent(); parentDN != null && parentDN.isDescendantOf(baseDN, true); parentDN = parentDN.getParent()) {
                                    deleteErrors.put(parentDN, new LDAPResult(-1, ResultCode.NOT_ALLOWED_ON_NONLEAF, UtilityMessages.ERR_SUBTREE_DELETER_SKIPPING_UNDELETABLE_ANCESTOR.get(String.valueOf(parentDN), String.valueOf(dn)), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS));
                                }
                                allSuccessful = false;
                                continue;
                            }
                        }
                    }
                }
                if (subentriesSearchResult != null) {
                    switch (subentriesSearchResult.getResultCode().intValue()) {
                        case 0:
                        case 32: {
                            break;
                        }
                        case 4: {
                            if (!anySuccessful) {
                                searchError.compareAndSet(null, subentriesSearchResult);
                                break;
                            }
                            break;
                        }
                        default: {
                            searchError.compareAndSet(null, subentriesSearchResult);
                            break;
                        }
                    }
                }
                switch (nonSubentriesSearchResult.getResultCode().intValue()) {
                    case 0:
                    case 32: {
                        break;
                    }
                    case 4: {
                        if (!anySuccessful) {
                            searchError.compareAndSet(null, nonSubentriesSearchResult);
                            break;
                        }
                        break;
                    }
                    default: {
                        searchError.compareAndSet(null, nonSubentriesSearchResult);
                        break;
                    }
                }
                if (allSuccessful) {
                    if (dnsToDelete.contains(baseDN)) {
                        return true;
                    }
                    continue;
                }
                else {
                    if (!anySuccessful) {
                        return false;
                    }
                    continue;
                }
            }
        }
    }
    
    private static SubtreeDeleterResult deleteEntriesWithoutSimplePagedResults(final LDAPInterface connection, final DN baseDN, final boolean deleteBaseEntry, final int searchRequestSizeLimit, final boolean useSubentriesControl, final List<Control> searchControls, final List<Control> deleteControls, final FixedRateBarrier deleteRateLimiter) {
        final TreeSet<DN> dnsToDelete = new TreeSet<DN>();
        final AtomicReference<SearchResult> searchError = new AtomicReference<SearchResult>();
        final AtomicLong entriesDeleted = new AtomicLong(0L);
        final TreeMap<DN, LDAPResult> deleteErrors = new TreeMap<DN, LDAPResult>();
        if (useSubentriesControl) {
            final SearchRequest searchRequest = createSubentriesSearchRequest(baseDN, searchRequestSizeLimit, searchControls, dnsToDelete);
            searchAndDelete(connection, baseDN, searchRequest, useSubentriesControl, searchControls, dnsToDelete, searchError, deleteBaseEntry, deleteControls, deleteRateLimiter, entriesDeleted, deleteErrors);
        }
        final SearchRequest searchRequest = createNonSubentriesSearchRequest(baseDN, searchRequestSizeLimit, searchControls, dnsToDelete);
        searchAndDelete(connection, baseDN, searchRequest, useSubentriesControl, searchControls, dnsToDelete, searchError, deleteBaseEntry, deleteControls, deleteRateLimiter, entriesDeleted, deleteErrors);
        return new SubtreeDeleterResult(null, false, searchError.get(), entriesDeleted.get(), deleteErrors);
    }
    
    private static void searchAndDelete(final LDAPInterface connection, final DN baseDN, final SearchRequest searchRequest, final boolean useSubentriesControl, final List<Control> searchControls, final TreeSet<DN> dnsToDelete, final AtomicReference<SearchResult> searchError, final boolean deleteBaseEntry, final List<Control> deleteControls, final FixedRateBarrier deleteRateLimiter, final AtomicLong entriesDeleted, final SortedMap<DN, LDAPResult> deleteErrors) {
        while (true) {
            final long beforeDeleteCount = entriesDeleted.get();
            SearchResult searchResult;
            try {
                searchResult = connection.search(searchRequest);
            }
            catch (final LDAPSearchException e) {
                Debug.debugException(e);
                searchResult = e.getSearchResult();
            }
            if (searchError.get() == null) {
                final ResultCode searchResultCode = searchResult.getResultCode();
                if (searchResultCode != ResultCode.SUCCESS) {
                    if (searchResultCode == ResultCode.NO_SUCH_OBJECT) {
                        return;
                    }
                    if (searchResultCode != ResultCode.SIZE_LIMIT_EXCEEDED) {
                        searchError.compareAndSet(null, searchResult);
                    }
                }
            }
            if (!deleteBaseEntry) {
                dnsToDelete.remove(baseDN);
            }
            final Iterator<DN> dnIterator = dnsToDelete.descendingIterator();
            while (dnIterator.hasNext()) {
                final DN dnToDelete = dnIterator.next();
                dnIterator.remove();
                if (!deleteErrors.containsKey(dnToDelete) && !deleteEntry(connection, dnToDelete, deleteControls, entriesDeleted, deleteErrors, deleteRateLimiter, searchRequest.getSizeLimit(), searchControls, useSubentriesControl, searchError)) {
                    for (DN parentDN = dnToDelete.getParent(); parentDN != null && parentDN.isDescendantOf(baseDN, true); parentDN = parentDN.getParent()) {
                        if (deleteErrors.containsKey(parentDN)) {
                            break;
                        }
                        deleteErrors.put(parentDN, new LDAPResult(-1, ResultCode.NOT_ALLOWED_ON_NONLEAF, UtilityMessages.ERR_SUBTREE_DELETER_SKIPPING_UNDELETABLE_ANCESTOR.get(String.valueOf(parentDN), String.valueOf(dnToDelete)), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS));
                    }
                }
            }
            final long afterDeleteCount = entriesDeleted.get();
            if (afterDeleteCount == beforeDeleteCount) {
                if (searchResult.getResultCode() == ResultCode.SIZE_LIMIT_EXCEEDED) {
                    searchError.compareAndSet(null, searchResult);
                }
            }
        }
    }
    
    private static ExtendedResult removeAccessibilityRestriction(final LDAPInterface connection, final DN baseDN) {
        return processExtendedOperation(connection, SetSubtreeAccessibilityExtendedRequest.createSetAccessibleRequest(baseDN.toString(), new Control[0]));
    }
    
    private static ExtendedResult processExtendedOperation(final LDAPInterface connection, final ExtendedRequest request) {
        try {
            if (connection instanceof LDAPConnection) {
                return ((LDAPConnection)connection).processExtendedOperation(request);
            }
            if (connection instanceof AbstractConnectionPool) {
                return ((AbstractConnectionPool)connection).processExtendedOperation(request);
            }
            return new ExtendedResult(-1, ResultCode.PARAM_ERROR, UtilityMessages.ERR_SUBTREE_DELETER_INTERFACE_EXTOP_NOT_SUPPORTED.get(connection.getClass().getName()), null, StaticUtils.NO_STRINGS, null, null, StaticUtils.NO_CONTROLS);
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            return new ExtendedResult(e);
        }
    }
    
    private static boolean supportsExtendedRequest(final LDAPInterface connection, final AtomicReference<RootDSE> rootDSE, final String oid) {
        final RootDSE dse = getRootDSE(connection, rootDSE);
        return dse != null && dse.supportsExtendedOperation(oid);
    }
    
    private static boolean supportsControl(final LDAPInterface connection, final AtomicReference<RootDSE> rootDSE, final String oid) {
        final RootDSE dse = getRootDSE(connection, rootDSE);
        return dse != null && dse.supportsControl(oid);
    }
    
    private static RootDSE getRootDSE(final LDAPInterface connection, final AtomicReference<RootDSE> rootDSE) {
        final RootDSE dse = rootDSE.get();
        if (dse != null) {
            return dse;
        }
        try {
            return connection.getRootDSE();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SubtreeDeleter(deleteBaseEntry=");
        buffer.append(this.deleteBaseEntry);
        buffer.append(", useSetSubtreeAccessibilityOperationIfAvailable=");
        buffer.append(this.useSetSubtreeAccessibilityOperationIfAvailable);
        if (this.useSimplePagedResultsControlIfAvailable) {
            buffer.append(", useSimplePagedResultsControlIfAvailable=true, pageSize=");
            buffer.append(this.simplePagedResultsPageSize);
        }
        else {
            buffer.append(", useSimplePagedResultsControlIfAvailable=false");
        }
        buffer.append(", useManageDSAITControlIfAvailable=");
        buffer.append(this.useManageDSAITControlIfAvailable);
        buffer.append(", usePermitUnindexedSearchControlIfAvailable=");
        buffer.append(this.usePermitUnindexedSearchControlIfAvailable);
        buffer.append(", useSubentriesControlIfAvailable=");
        buffer.append(this.useSubentriesControlIfAvailable);
        buffer.append(", useReturnConflictEntriesRequestControlIfAvailable=");
        buffer.append(this.useReturnConflictEntriesRequestControlIfAvailable);
        buffer.append(", useSoftDeletedEntryAccessControlIfAvailable=");
        buffer.append(this.useSoftDeletedEntryAccessControlIfAvailable);
        buffer.append(", useHardDeleteControlIfAvailable=");
        buffer.append(this.useHardDeleteControlIfAvailable);
        buffer.append(", additionalSearchControls={ ");
        final Iterator<Control> searchControlIterator = this.additionalSearchControls.iterator();
        while (searchControlIterator.hasNext()) {
            buffer.append(searchControlIterator.next());
            if (searchControlIterator.hasNext()) {
                buffer.append(',');
            }
            buffer.append(' ');
        }
        buffer.append("}, additionalDeleteControls={");
        final Iterator<Control> deleteControlIterator = this.additionalSearchControls.iterator();
        while (deleteControlIterator.hasNext()) {
            buffer.append(deleteControlIterator.next());
            if (deleteControlIterator.hasNext()) {
                buffer.append(',');
            }
            buffer.append(' ');
        }
        buffer.append("}, searchRequestSizeLimit=");
        buffer.append(this.searchRequestSizeLimit);
        buffer.append(')');
    }
}
