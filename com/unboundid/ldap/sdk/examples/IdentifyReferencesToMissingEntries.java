package com.unboundid.ldap.sdk.examples;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Set;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.LinkedHashMap;
import java.util.Collections;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Iterator;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import java.util.Map;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.DNArgument;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class IdentifyReferencesToMissingEntries extends LDAPCommandLineTool implements SearchResultListener
{
    private static final long serialVersionUID = 1981894839719501258L;
    private final AtomicLong entriesExamined;
    private DNArgument baseDNArgument;
    private IntegerArgument pageSizeArgument;
    private LDAPConnectionPool getReferencedEntriesPool;
    private final Map<String, AtomicLong> missingReferenceCounts;
    private String[] attributes;
    private StringArgument attributeArgument;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final IdentifyReferencesToMissingEntries tool = new IdentifyReferencesToMissingEntries(outStream, errStream);
        return tool.runTool(args);
    }
    
    public IdentifyReferencesToMissingEntries(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.baseDNArgument = null;
        this.pageSizeArgument = null;
        this.attributeArgument = null;
        this.getReferencedEntriesPool = null;
        this.entriesExamined = new AtomicLong(0L);
        this.missingReferenceCounts = new TreeMap<String, AtomicLong>();
    }
    
    @Override
    public String getToolName() {
        return "identify-references-to-missing-entries";
    }
    
    @Override
    public String getToolDescription() {
        return "This tool may be used to identify entries containing one or more attributes which reference entries that do not exist.  This may require the ability to perform unindexed searches and/or the ability to use the simple paged results control.";
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
        String description = "The search base DN(s) to use to find entries with references to other entries.  At least one base DN must be specified.";
        (this.baseDNArgument = new DNArgument('b', "baseDN", true, 0, "{dn}", description)).addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDNArgument);
        description = "The attribute(s) for which to find missing references.  At least one attribute must be specified, and each attribute must be indexed for equality searches and have values which are DNs.";
        parser.addArgument(this.attributeArgument = new StringArgument('A', "attribute", true, 0, "{attr}", description));
        description = "The maximum number of entries to retrieve at a time when attempting to find entries with references to other entries.  This requires that the authenticated user have permission to use the simple paged results control, but it can avoid problems with the server sending entries too quickly for the client to handle.  By default, the simple paged results control will not be used.";
        (this.pageSizeArgument = new IntegerArgument('z', "simplePageSize", false, 1, "{num}", description, 1, Integer.MAX_VALUE)).addLongIdentifier("simple-page-size", true);
        parser.addArgument(this.pageSizeArgument);
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
        LDAPConnectionPool findReferencesPool;
        try {
            findReferencesPool = this.getConnectionPool(1, 1);
            findReferencesPool.setRetryFailedOperationsDueToInvalidConnections(true);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err("Unable to establish a connection to the directory server:  ", StaticUtils.getExceptionMessage(le));
            return le.getResultCode();
        }
        try {
            try {
                (this.getReferencedEntriesPool = this.getConnectionPool(1, 1)).setRetryFailedOperationsDueToInvalidConnections(true);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err("Unable to establish a connection to the directory server:  ", StaticUtils.getExceptionMessage(le));
                return le.getResultCode();
            }
            final List<String> attrList = this.attributeArgument.getValues();
            attrList.toArray(this.attributes = new String[attrList.size()]);
            Filter filter;
            if (this.attributes.length == 1) {
                filter = Filter.createPresenceFilter(this.attributes[0]);
                this.missingReferenceCounts.put(this.attributes[0], new AtomicLong(0L));
            }
            else {
                final Filter[] orComps = new Filter[this.attributes.length];
                for (int i = 0; i < this.attributes.length; ++i) {
                    orComps[i] = Filter.createPresenceFilter(this.attributes[i]);
                    this.missingReferenceCounts.put(this.attributes[i], new AtomicLong(0L));
                }
                filter = Filter.createORFilter(orComps);
            }
            for (final DN baseDN : this.baseDNArgument.getValues()) {
                ASN1OctetString cookie = null;
                do {
                    final SearchRequest searchRequest = new SearchRequest(this, baseDN.toString(), SearchScope.SUB, filter, this.attributes);
                    if (this.pageSizeArgument.isPresent()) {
                        searchRequest.addControl(new SimplePagedResultsControl(this.pageSizeArgument.getValue(), cookie, false));
                    }
                    SearchResult searchResult;
                    try {
                        searchResult = findReferencesPool.search(searchRequest);
                    }
                    catch (final LDAPSearchException lse) {
                        Debug.debugException(lse);
                        try {
                            searchResult = findReferencesPool.search(searchRequest);
                        }
                        catch (final LDAPSearchException lse2) {
                            Debug.debugException(lse2);
                            searchResult = lse2.getSearchResult();
                        }
                    }
                    if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                        this.err("An error occurred while attempting to search for missing references to entries below " + baseDN + ":  " + searchResult.getDiagnosticMessage());
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
                    if (pagedResultsResponse == null) {
                        continue;
                    }
                    if (pagedResultsResponse.moreResultsToReturn()) {
                        cookie = pagedResultsResponse.getCookie();
                    }
                    else {
                        cookie = null;
                    }
                } while (cookie != null);
            }
            boolean missingReferenceFound = false;
            for (final Map.Entry<String, AtomicLong> e : this.missingReferenceCounts.entrySet()) {
                final long numMissing = e.getValue().get();
                if (numMissing > 0L) {
                    if (!missingReferenceFound) {
                        this.err(new Object[0]);
                        missingReferenceFound = true;
                    }
                    this.err("Found " + numMissing + ' ' + e.getKey() + " references to entries that do not exist.");
                }
            }
            if (missingReferenceFound) {
                return ResultCode.CONSTRAINT_VIOLATION;
            }
            this.out("No references were found to entries that do not exist.");
            return ResultCode.SUCCESS;
        }
        finally {
            findReferencesPool.close();
            if (this.getReferencedEntriesPool != null) {
                this.getReferencedEntriesPool.close();
            }
        }
    }
    
    public Map<String, AtomicLong> getMissingReferenceCounts() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends AtomicLong>)this.missingReferenceCounts);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=john.doe,ou=People,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--attribute", "member", "--attribute", "uniqueMember", "--simplePageSize", "100" };
        exampleMap.put(args, "Identify all entries below dc=example,dc=com in which either the member or uniqueMember attribute references an entry that does not exist.");
        return exampleMap;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        try {
            for (final String attr : this.attributes) {
                final List<Attribute> attrList = searchEntry.getAttributesWithOptions(attr, null);
                for (final Attribute a : attrList) {
                    for (final String value : a.getValues()) {
                        try {
                            final SearchResultEntry e = this.getReferencedEntriesPool.getEntry(value, "1.1");
                            if (e == null) {
                                this.err("Entry '", searchEntry.getDN(), "' includes attribute ", a.getName(), " that references entry '", value, "' which does not exist.");
                                this.missingReferenceCounts.get(attr).incrementAndGet();
                            }
                        }
                        catch (final LDAPException le) {
                            Debug.debugException(le);
                            this.err("An error occurred while attempting to determine whether entry '" + value + "' referenced in attribute " + a.getName() + " of entry '" + searchEntry.getDN() + "' exists:  " + StaticUtils.getExceptionMessage(le));
                            this.missingReferenceCounts.get(attr).incrementAndGet();
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
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
    }
}
