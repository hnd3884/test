package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.extensions.CancelExtendedRequest;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Iterator;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1OctetString;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import java.util.Set;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import java.util.Map;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.DNArgument;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class IdentifyUniqueAttributeConflicts extends LDAPCommandLineTool implements SearchResultListener
{
    private static final String BEHAVIOR_UNIQUE_WITHIN_ATTR = "unique-within-each-attribute";
    private static final String BEHAVIOR_UNIQUE_ACROSS_ATTRS_INCLUDING_SAME = "unique-across-all-attributes-including-in-same-entry";
    private static final String BEHAVIOR_UNIQUE_ACROSS_ATTRS_EXCEPT_SAME = "unique-across-all-attributes-except-in-same-entry";
    private static final String BEHAVIOR_UNIQUE_IN_COMBINATION = "unique-in-combination";
    private static final int DEFAULT_TIME_LIMIT_SECONDS = 10;
    private static final long serialVersionUID = 4216291898088659008L;
    private final AtomicBoolean timeLimitExceeded;
    private final AtomicLong entriesExamined;
    private final AtomicLong combinationConflictCounts;
    private boolean allowConflictsInSameEntry;
    private boolean uniqueAcrossAttributes;
    private boolean uniqueInCombination;
    private DNArgument baseDNArgument;
    private FilterArgument filterArgument;
    private IntegerArgument pageSizeArgument;
    private IntegerArgument timeLimitArgument;
    private LDAPConnectionPool findConflictsPool;
    private final Map<String, AtomicLong> conflictCounts;
    private String[] attributes;
    private String[] baseDNs;
    private StringArgument attributeArgument;
    private StringArgument multipleAttributeBehaviorArgument;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final IdentifyUniqueAttributeConflicts tool = new IdentifyUniqueAttributeConflicts(outStream, errStream);
        return tool.runTool(args);
    }
    
    public IdentifyUniqueAttributeConflicts(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.baseDNArgument = null;
        this.filterArgument = null;
        this.pageSizeArgument = null;
        this.attributeArgument = null;
        this.multipleAttributeBehaviorArgument = null;
        this.findConflictsPool = null;
        this.allowConflictsInSameEntry = false;
        this.uniqueAcrossAttributes = false;
        this.uniqueInCombination = false;
        this.attributes = null;
        this.baseDNs = null;
        this.timeLimitArgument = null;
        this.timeLimitExceeded = new AtomicBoolean(false);
        this.entriesExamined = new AtomicLong(0L);
        this.combinationConflictCounts = new AtomicLong(0L);
        this.conflictCounts = new TreeMap<String, AtomicLong>();
    }
    
    @Override
    public String getToolName() {
        return "identify-unique-attribute-conflicts";
    }
    
    @Override
    public String getToolDescription() {
        return "This tool may be used to identify unique attribute conflicts.  That is, it may identify values of one or more attributes which are supposed to exist only in a single entry but are found in multiple entries.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public boolean supportsInteractiveMode() {
        return true;
    }
    
    @Override
    public boolean defaultsToInteractiveMode() {
        return true;
    }
    
    @Override
    protected boolean supportsOutputFile() {
        return true;
    }
    
    @Override
    protected boolean defaultToPromptForBindPassword() {
        return true;
    }
    
    @Override
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean includeAlternateLongIdentifiers() {
        return true;
    }
    
    @Override
    protected boolean supportsSSLDebugging() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        String description = "The search base DN(s) to use to find entries with attributes for which to find uniqueness conflicts.  At least one base DN must be specified.";
        (this.baseDNArgument = new DNArgument('b', "baseDN", true, 0, "{dn}", description)).addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDNArgument);
        description = "A filter that will be used to identify the set of entries in which to identify uniqueness conflicts.  If this is not specified, then all entries containing the target attribute(s) will be examined.";
        parser.addArgument(this.filterArgument = new FilterArgument('f', "filter", false, 1, "{filter}", description));
        description = "The attributes for which to find uniqueness conflicts.  At least one attribute must be specified, and each attribute must be indexed for equality searches.";
        parser.addArgument(this.attributeArgument = new StringArgument('A', "attribute", true, 0, "{attr}", description));
        description = "Indicates the behavior to exhibit if multiple unique attributes are provided.  Allowed values are 'unique-within-each-attribute' (indicates that each value only needs to be unique within its own attribute type), 'unique-across-all-attributes-including-in-same-entry' (indicates that each value needs to be unique across all of the specified attributes), 'unique-across-all-attributes-except-in-same-entry' (indicates each value needs to be unique across all of the specified attributes, except that multiple attributes in the same entry are allowed to share the same value), and 'unique-in-combination' (indicates that every combination of the values of the specified attributes must be unique across each entry).";
        final Set<String> allowedValues = StaticUtils.setOf("unique-within-each-attribute", "unique-across-all-attributes-including-in-same-entry", "unique-across-all-attributes-except-in-same-entry", "unique-in-combination");
        (this.multipleAttributeBehaviorArgument = new StringArgument('m', "multipleAttributeBehavior", false, 1, "{behavior}", description, allowedValues, "unique-within-each-attribute")).addLongIdentifier("multiple-attribute-behavior", true);
        parser.addArgument(this.multipleAttributeBehaviorArgument);
        description = "The maximum number of entries to retrieve at a time when attempting to find uniqueness conflicts.  This requires that the authenticated user have permission to use the simple paged results control, but it can avoid problems with the server sending entries too quickly for the client to handle.  By default, the simple paged results control will not be used.";
        (this.pageSizeArgument = new IntegerArgument('z', "simplePageSize", false, 1, "{num}", description, 1, Integer.MAX_VALUE)).addLongIdentifier("simple-page-size", true);
        parser.addArgument(this.pageSizeArgument);
        description = "The time limit in seconds that will be used for search requests attempting to identify conflicts for each value of any of the unique attributes.  This time limit is used to avoid sending expensive unindexed search requests that can consume significant server resources.  If any of these search operations fails in a way that indicates the requested time limit was exceeded, the tool will abort its processing.  A value of zero indicates that no time limit will be enforced.  If this argument is not provided, a default time limit of 10 will be used.";
        (this.timeLimitArgument = new IntegerArgument('l', "timeLimitSeconds", false, 1, "{num}", description, 0, Integer.MAX_VALUE, 10)).addLongIdentifier("timeLimit", true);
        this.timeLimitArgument.addLongIdentifier("time-limit-seconds", true);
        this.timeLimitArgument.addLongIdentifier("time-limit", true);
        parser.addArgument(this.timeLimitArgument);
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setUseSynchronousMode(true);
        options.setResponseTimeoutMillis(0L);
        return options;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final List<String> attrList = this.attributeArgument.getValues();
        final String multiAttrBehavior = this.multipleAttributeBehaviorArgument.getValue();
        if (attrList.size() > 1) {
            if (multiAttrBehavior.equalsIgnoreCase("unique-across-all-attributes-including-in-same-entry")) {
                this.uniqueAcrossAttributes = true;
                this.uniqueInCombination = false;
                this.allowConflictsInSameEntry = false;
            }
            else if (multiAttrBehavior.equalsIgnoreCase("unique-across-all-attributes-except-in-same-entry")) {
                this.uniqueAcrossAttributes = true;
                this.uniqueInCombination = false;
                this.allowConflictsInSameEntry = true;
            }
            else if (multiAttrBehavior.equalsIgnoreCase("unique-in-combination")) {
                this.uniqueAcrossAttributes = false;
                this.uniqueInCombination = true;
                this.allowConflictsInSameEntry = true;
            }
            else {
                this.uniqueAcrossAttributes = false;
                this.uniqueInCombination = false;
                this.allowConflictsInSameEntry = true;
            }
        }
        else {
            this.uniqueAcrossAttributes = false;
            this.uniqueInCombination = false;
            this.allowConflictsInSameEntry = true;
        }
        final List<DN> dnList = this.baseDNArgument.getValues();
        this.baseDNs = new String[dnList.size()];
        for (int i = 0; i < this.baseDNs.length; ++i) {
            this.baseDNs[i] = dnList.get(i).toString();
        }
        LDAPConnectionPool findUniqueAttributesPool;
        try {
            findUniqueAttributesPool = this.getConnectionPool(1, 1);
            findUniqueAttributesPool.setRetryFailedOperationsDueToInvalidConnections(true);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err("Unable to establish a connection to the directory server:  ", StaticUtils.getExceptionMessage(le));
            return le.getResultCode();
        }
        try {
            try {
                (this.findConflictsPool = this.getConnectionPool(1, 1)).setRetryFailedOperationsDueToInvalidConnections(true);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err("Unable to establish a connection to the directory server:  ", StaticUtils.getExceptionMessage(le));
                return le.getResultCode();
            }
            attrList.toArray(this.attributes = new String[attrList.size()]);
            Filter filter;
            if (this.attributes.length == 1) {
                filter = Filter.createPresenceFilter(this.attributes[0]);
                this.conflictCounts.put(this.attributes[0], new AtomicLong(0L));
            }
            else if (this.uniqueInCombination) {
                final Filter[] andComps = new Filter[this.attributes.length];
                for (int j = 0; j < this.attributes.length; ++j) {
                    andComps[j] = Filter.createPresenceFilter(this.attributes[j]);
                    this.conflictCounts.put(this.attributes[j], new AtomicLong(0L));
                }
                filter = Filter.createANDFilter(andComps);
            }
            else {
                final Filter[] orComps = new Filter[this.attributes.length];
                for (int j = 0; j < this.attributes.length; ++j) {
                    orComps[j] = Filter.createPresenceFilter(this.attributes[j]);
                    this.conflictCounts.put(this.attributes[j], new AtomicLong(0L));
                }
                filter = Filter.createORFilter(orComps);
            }
            if (this.filterArgument.isPresent()) {
                filter = Filter.createANDFilter(this.filterArgument.getValue(), filter);
            }
            final String[] arr$ = this.baseDNs;
            final int len$ = arr$.length;
            int i$ = 0;
        Label_0611:
            while (i$ < len$) {
                final String baseDN = arr$[i$];
                ASN1OctetString cookie = null;
                while (true) {
                    while (!this.timeLimitExceeded.get()) {
                        final SearchRequest searchRequest = new SearchRequest(this, baseDN, SearchScope.SUB, filter, this.attributes);
                        if (this.pageSizeArgument.isPresent()) {
                            searchRequest.addControl(new SimplePagedResultsControl(this.pageSizeArgument.getValue(), cookie, false));
                        }
                        SearchResult searchResult;
                        try {
                            searchResult = findUniqueAttributesPool.search(searchRequest);
                        }
                        catch (final LDAPSearchException lse) {
                            Debug.debugException(lse);
                            try {
                                searchResult = this.findConflictsPool.search(searchRequest);
                            }
                            catch (final LDAPSearchException lse2) {
                                Debug.debugException(lse2);
                                searchResult = lse2.getSearchResult();
                            }
                        }
                        if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                            this.err("An error occurred while attempting to search for unique attributes in entries below " + baseDN + ":  " + searchResult.getDiagnosticMessage());
                            return searchResult.getResultCode();
                        }
                        SimplePagedResultsControl pagedResultsResponse;
                        try {
                            pagedResultsResponse = SimplePagedResultsControl.get(searchResult);
                        }
                        catch (final LDAPException le2) {
                            Debug.debugException(le2);
                            this.err("An error occurred while attempting to decode a simple paged results response control in the response to a search for entries below " + baseDN + ":  " + StaticUtils.getExceptionMessage(le2));
                            return le2.getResultCode();
                        }
                        if (pagedResultsResponse != null) {
                            if (pagedResultsResponse.moreResultsToReturn()) {
                                cookie = pagedResultsResponse.getCookie();
                            }
                            else {
                                cookie = null;
                            }
                        }
                        if (cookie == null) {
                            ++i$;
                            continue Label_0611;
                        }
                    }
                    continue;
                }
            }
            boolean conflictFound = false;
            if (this.uniqueInCombination) {
                final long count = this.combinationConflictCounts.get();
                if (count > 0L) {
                    conflictFound = true;
                    this.err("Found " + count + " total conflicts.");
                }
            }
            else {
                for (final Map.Entry<String, AtomicLong> e : this.conflictCounts.entrySet()) {
                    final long numConflicts = e.getValue().get();
                    if (numConflicts > 0L) {
                        if (!conflictFound) {
                            this.err(new Object[0]);
                            conflictFound = true;
                        }
                        this.err("Found " + numConflicts + " unique value conflicts in attribute " + e.getKey());
                    }
                }
            }
            if (conflictFound) {
                return ResultCode.CONSTRAINT_VIOLATION;
            }
            if (this.timeLimitExceeded.get()) {
                return ResultCode.TIME_LIMIT_EXCEEDED;
            }
            this.out("No unique attribute conflicts were found.");
            return ResultCode.SUCCESS;
        }
        finally {
            findUniqueAttributesPool.close();
            if (this.findConflictsPool != null) {
                this.findConflictsPool.close();
            }
        }
    }
    
    public long getCombinationConflictCounts() {
        return this.combinationConflictCounts.get();
    }
    
    public Map<String, AtomicLong> getConflictCounts() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends AtomicLong>)this.conflictCounts);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=john.doe,ou=People,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--attribute", "uid", "--simplePageSize", "100" };
        exampleMap.put(args, "Identify any values of the uid attribute that are not unique across all entries below dc=example,dc=com.");
        return exampleMap;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        if (this.timeLimitExceeded.get()) {
            return;
        }
        if (this.uniqueInCombination) {
            this.checkForConflictsInCombination(searchEntry);
            return;
        }
        try {
            if (!this.allowConflictsInSameEntry) {
                boolean conflictFound = false;
                for (int i = 0; i < this.attributes.length; ++i) {
                    final List<Attribute> l1 = searchEntry.getAttributesWithOptions(this.attributes[i], null);
                    if (l1 != null) {
                        for (int j = i + 1; j < this.attributes.length; ++j) {
                            final List<Attribute> l2 = searchEntry.getAttributesWithOptions(this.attributes[j], null);
                            if (l2 != null) {
                                for (final Attribute a1 : l1) {
                                    for (final String value : a1.getValues()) {
                                        for (final Attribute a2 : l2) {
                                            if (a2.hasValue(value)) {
                                                this.err("Value '", value, "' in attribute ", a1.getName(), " of entry '", searchEntry.getDN(), " is also present in attribute ", a2.getName(), " of the same entry.");
                                                conflictFound = true;
                                                this.conflictCounts.get(this.attributes[i]).incrementAndGet();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (conflictFound) {
                    return;
                }
            }
            for (final String attrName : this.attributes) {
                final List<Attribute> attrList = searchEntry.getAttributesWithOptions(attrName, null);
                for (final Attribute a3 : attrList) {
                    for (final String value : a3.getValues()) {
                        Filter filter;
                        if (this.uniqueAcrossAttributes) {
                            final Filter[] orComps = new Filter[this.attributes.length];
                            for (int k = 0; k < this.attributes.length; ++k) {
                                orComps[k] = Filter.createEqualityFilter(this.attributes[k], value);
                            }
                            filter = Filter.createORFilter(orComps);
                        }
                        else {
                            filter = Filter.createEqualityFilter(attrName, value);
                        }
                        if (this.filterArgument.isPresent()) {
                            filter = Filter.createANDFilter(this.filterArgument.getValue(), filter);
                        }
                    Label_1137:
                        for (final String baseDN : this.baseDNs) {
                            final SearchRequest searchRequest = new SearchRequest(baseDN, SearchScope.SUB, DereferencePolicy.NEVER, 2, this.timeLimitArgument.getValue(), false, filter, new String[] { "1.1" });
                            SearchResult searchResult;
                            try {
                                searchResult = this.findConflictsPool.search(searchRequest);
                            }
                            catch (final LDAPSearchException lse) {
                                Debug.debugException(lse);
                                if (lse.getResultCode() == ResultCode.TIME_LIMIT_EXCEEDED) {
                                    this.timeLimitExceeded.set(true);
                                    try {
                                        this.findConflictsPool.processExtendedOperation(new CancelExtendedRequest(searchEntry.getMessageID()));
                                    }
                                    catch (final Exception e) {
                                        Debug.debugException(e);
                                    }
                                    this.err("A server-side time limit was exceeded when searching below base DN '" + baseDN + "' with filter '" + filter + "', which likely means that the search " + "request is not indexed in the server.  Check the " + "server configuration to ensure that any appropriate " + "indexes are in place.  To indicate that searches " + "should not request any time limit, use the " + this.timeLimitArgument.getIdentifierString() + " to indicate a time limit of zero seconds.");
                                    return;
                                }
                                if (lse.getResultCode().isConnectionUsable()) {
                                    searchResult = lse.getSearchResult();
                                }
                                else {
                                    try {
                                        searchResult = this.findConflictsPool.search(searchRequest);
                                    }
                                    catch (final LDAPSearchException lse2) {
                                        Debug.debugException(lse2);
                                        searchResult = lse2.getSearchResult();
                                    }
                                }
                            }
                            for (final SearchResultEntry e2 : searchResult.getSearchEntries()) {
                                try {
                                    if (DN.equals(searchEntry.getDN(), e2.getDN())) {
                                        continue;
                                    }
                                }
                                catch (final Exception ex) {
                                    Debug.debugException(ex);
                                }
                                this.err("Value '", value, "' in attribute ", a3.getName(), " of entry '" + searchEntry.getDN(), "' is also present in entry '", e2.getDN(), "'.");
                                this.conflictCounts.get(attrName).incrementAndGet();
                                break Label_1137;
                            }
                            if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                                this.err("An error occurred while attempting to search for conflicts with " + a3.getName() + " value '" + value + "' (as found in entry '" + searchEntry.getDN() + "') below '" + baseDN + "':  " + searchResult.getDiagnosticMessage());
                                this.conflictCounts.get(attrName).incrementAndGet();
                                break;
                            }
                        }
                    }
                }
            }
        }
        finally {
            final long count = this.entriesExamined.incrementAndGet();
            if (count % 1000L == 0L) {
                this.out(count, " entries examined");
            }
        }
    }
    
    private void checkForConflictsInCombination(final SearchResultEntry entry) {
        final ArrayList<Filter> andComponents = new ArrayList<Filter>(this.attributes.length + 1);
        for (final String attrName : this.attributes) {
            final LinkedHashSet<Filter> values = new LinkedHashSet<Filter>(StaticUtils.computeMapCapacity(5));
            for (final Attribute a : entry.getAttributesWithOptions(attrName, null)) {
                for (final byte[] value : a.getValueByteArrays()) {
                    final Filter equalityFilter = Filter.createEqualityFilter(attrName, value);
                    values.add(Filter.createEqualityFilter(attrName, value));
                }
            }
            switch (values.size()) {
                case 0: {
                    return;
                }
                case 1: {
                    andComponents.add(values.iterator().next());
                    break;
                }
                default: {
                    andComponents.add(Filter.createORFilter(values));
                    break;
                }
            }
        }
        if (this.filterArgument.isPresent()) {
            andComponents.add(this.filterArgument.getValue());
        }
        final Filter filter = Filter.createANDFilter(andComponents);
    Label_0724:
        for (final DN baseDN : this.baseDNArgument.getValues()) {
            final SearchRequest searchRequest = new SearchRequest(baseDN.toString(), SearchScope.SUB, DereferencePolicy.NEVER, 2, this.timeLimitArgument.getValue(), false, filter, new String[] { "1.1" });
            SearchResult searchResult;
            try {
                searchResult = this.findConflictsPool.search(searchRequest);
            }
            catch (final LDAPSearchException lse) {
                Debug.debugException(lse);
                if (lse.getResultCode() == ResultCode.TIME_LIMIT_EXCEEDED) {
                    this.timeLimitExceeded.set(true);
                    try {
                        this.findConflictsPool.processExtendedOperation(new CancelExtendedRequest(entry.getMessageID()));
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                    this.err("A server-side time limit was exceeded when searching below base DN '" + baseDN + "' with filter '" + filter + "', which likely means that the search request is not indexed " + "in the server.  Check the server configuration to ensure " + "that any appropriate indexes are in place.  To indicate that " + "searches should not request any time limit, use the " + this.timeLimitArgument.getIdentifierString() + " to indicate a time limit of zero seconds.");
                    return;
                }
                if (lse.getResultCode().isConnectionUsable()) {
                    searchResult = lse.getSearchResult();
                }
                else {
                    try {
                        searchResult = this.findConflictsPool.search(searchRequest);
                    }
                    catch (final LDAPSearchException lse2) {
                        Debug.debugException(lse2);
                        searchResult = lse2.getSearchResult();
                    }
                }
            }
            for (final SearchResultEntry e2 : searchResult.getSearchEntries()) {
                try {
                    if (DN.equals(entry.getDN(), e2.getDN())) {
                        continue;
                    }
                }
                catch (final Exception ex) {
                    Debug.debugException(ex);
                }
                this.err("Entry '" + entry.getDN() + " has a combination of values that " + "are also present in entry '" + e2.getDN() + "'.");
                this.combinationConflictCounts.incrementAndGet();
                break Label_0724;
            }
            if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                this.err("An error occurred while attempting to search for conflicts  with entry '" + entry.getDN() + "' below '" + baseDN + "':  " + searchResult.getDiagnosticMessage());
                this.combinationConflictCounts.incrementAndGet();
                break;
            }
        }
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
    }
}
