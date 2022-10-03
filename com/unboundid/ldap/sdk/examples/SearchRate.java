package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.concurrent.CyclicBarrier;
import com.unboundid.util.ResultCodeCounter;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ColumnFormatter;
import com.unboundid.util.HorizontalAlignment;
import com.unboundid.util.FormattableColumn;
import com.unboundid.util.OutputFormat;
import java.util.concurrent.Semaphore;
import java.io.IOException;
import com.unboundid.util.FixedRateBarrier;
import java.util.Collection;
import java.util.List;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import java.util.StringTokenizer;
import com.unboundid.ldap.sdk.controls.SortKey;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.ldap.sdk.Control;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.DereferencePolicy;
import java.text.ParseException;
import com.unboundid.util.ValuePattern;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import java.util.Set;
import com.unboundid.util.RateAdjustor;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.WakeableSleeper;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanArgument;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SearchRate extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = 3345838530404592182L;
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private BooleanArgument asynchronousMode;
    private BooleanArgument csvFormat;
    private BooleanArgument suppressErrors;
    private BooleanArgument typesOnly;
    private ControlArgument control;
    private FileArgument sampleRateFile;
    private FileArgument variableRateData;
    private FilterArgument assertionFilter;
    private IntegerArgument collectionInterval;
    private IntegerArgument iterationsBeforeReconnect;
    private IntegerArgument maxOutstandingRequests;
    private IntegerArgument numIntervals;
    private IntegerArgument numThreads;
    private IntegerArgument randomSeed;
    private IntegerArgument ratePerSecond;
    private IntegerArgument simplePageSize;
    private IntegerArgument sizeLimit;
    private IntegerArgument timeLimitSeconds;
    private IntegerArgument warmUpIntervals;
    private ScopeArgument scope;
    private StringArgument attributes;
    private StringArgument baseDN;
    private StringArgument dereferencePolicy;
    private StringArgument filter;
    private StringArgument ldapURL;
    private StringArgument proxyAs;
    private StringArgument sortOrder;
    private StringArgument timestampFormat;
    private final WakeableSleeper sleeper;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final SearchRate searchRate = new SearchRate(outStream, errStream);
        return searchRate.runTool(args);
    }
    
    public SearchRate(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.stopRequested = new AtomicBoolean(false);
        this.runningThreads = new AtomicInteger(0);
        this.sleeper = new WakeableSleeper();
    }
    
    @Override
    public String getToolName() {
        return "searchrate";
    }
    
    @Override
    public String getToolDescription() {
        return "Perform repeated searches against an LDAP directory server.";
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
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        String description = "The base DN to use for the searches.  It may be a simple DN or a value pattern to specify a range of DNs (e.g., \"uid=user.[1-1000],ou=People,dc=example,dc=com\").  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  This argument must not be used in conjunction with the --ldapURL argument.";
        (this.baseDN = new StringArgument('b', "baseDN", false, 1, "{dn}", description, "")).setArgumentGroupName("Search Arguments");
        this.baseDN.addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDN);
        description = "The scope to use for the searches.  It should be 'base', 'one', 'sub', or 'subord'.  If this is not provided, then a default scope of 'sub' will be used.  This argument must not be used in conjunction with the --ldapURL argument.";
        (this.scope = new ScopeArgument('s', "scope", false, "{scope}", description, SearchScope.SUB)).setArgumentGroupName("Search Arguments");
        parser.addArgument(this.scope);
        description = "The filter to use for the searches.  It may be a simple filter or a value pattern to specify a range of filters (e.g., \"(uid=user.[1-1000])\").  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  Exactly one of this argument and the --ldapURL arguments must be provided.";
        (this.filter = new StringArgument('f', "filter", false, 1, "{filter}", description)).setArgumentGroupName("Search Arguments");
        parser.addArgument(this.filter);
        description = "The name of an attribute to include in entries returned from the searches.  Multiple attributes may be requested by providing this argument multiple times.  If no request attributes are provided, then the entries returned will include all user attributes.  This argument must not be used in conjunction with the --ldapURL argument.";
        (this.attributes = new StringArgument('A', "attribute", false, 0, "{name}", description)).setArgumentGroupName("Search Arguments");
        parser.addArgument(this.attributes);
        description = "An LDAP URL that provides the base DN, scope, filter, and requested attributes to use for the search requests (the address and port components of the URL, if present, will be ignored).  It may be a simple LDAP URL or a value pattern to specify a range of URLs.  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  If this argument is provided, then none of the --baseDN, --scope, --filter, or --attribute arguments may be used.";
        (this.ldapURL = new StringArgument(null, "ldapURL", false, 1, "{url}", description)).setArgumentGroupName("Search Arguments");
        this.ldapURL.addLongIdentifier("ldap-url", true);
        parser.addArgument(this.ldapURL);
        description = "The maximum number of entries that the server should return in response to each search request.  A value of zero indicates that the client does not wish to impose any limit on the number of entries that are returned (although the server may impose its own limit).  If this is not provided, then a default value of zero will be used.";
        (this.sizeLimit = new IntegerArgument('z', "sizeLimit", false, 1, "{num}", description, 0, Integer.MAX_VALUE, 0)).setArgumentGroupName("Search Arguments");
        this.sizeLimit.addLongIdentifier("size-limit", true);
        parser.addArgument(this.sizeLimit);
        description = "The maximum length of time, in seconds, that the server should spend processing each search request.  A value of zero indicates that the client does not wish to impose any limit on the server's processing time (although the server may impose its own limit).  If this is not provided, then a default value of zero will be used.";
        (this.timeLimitSeconds = new IntegerArgument('l', "timeLimitSeconds", false, 1, "{seconds}", description, 0, Integer.MAX_VALUE, 0)).setArgumentGroupName("Search Arguments");
        this.timeLimitSeconds.addLongIdentifier("time-limit-seconds", true);
        this.timeLimitSeconds.addLongIdentifier("timeLimit", true);
        this.timeLimitSeconds.addLongIdentifier("time-limit", true);
        parser.addArgument(this.timeLimitSeconds);
        final Set<String> derefAllowedValues = StaticUtils.setOf("never", "always", "search", "find");
        description = "The alias dereferencing policy to use for search requests.  The value should be one of 'never', 'always', 'search', or 'find'.  If this is not provided, then a default value of 'never' will be used.";
        (this.dereferencePolicy = new StringArgument(null, "dereferencePolicy", false, 1, "{never|always|search|find}", description, derefAllowedValues, "never")).setArgumentGroupName("Search Arguments");
        this.dereferencePolicy.addLongIdentifier("dereference-policy", true);
        parser.addArgument(this.dereferencePolicy);
        description = "Indicates that server should only include the names of the attributes contained in matching entries rather than both names and values.";
        (this.typesOnly = new BooleanArgument(null, "typesOnly", 1, description)).setArgumentGroupName("Search Arguments");
        this.typesOnly.addLongIdentifier("types-only", true);
        parser.addArgument(this.typesOnly);
        description = "Indicates that search requests should include the assertion request control with the specified filter.";
        (this.assertionFilter = new FilterArgument(null, "assertionFilter", false, 1, "{filter}", description)).setArgumentGroupName("Request Control Arguments");
        this.assertionFilter.addLongIdentifier("assertion-filter", true);
        parser.addArgument(this.assertionFilter);
        description = "Indicates that search requests should include the simple paged results control with the specified page size.";
        (this.simplePageSize = new IntegerArgument(null, "simplePageSize", false, 1, "{size}", description, 1, Integer.MAX_VALUE)).setArgumentGroupName("Request Control Arguments");
        this.simplePageSize.addLongIdentifier("simple-page-size", true);
        parser.addArgument(this.simplePageSize);
        description = "Indicates that search requests should include the server-side sort request control with the specified sort order.  This should be a comma-delimited list in which each item is an attribute name, optionally preceded by a plus or minus sign (to indicate ascending or descending order; where ascending order is the default), and optionally followed by a colon and the name or OID of the desired ordering matching rule (if this is not provided, the the attribute type's default ordering rule will be used).";
        (this.sortOrder = new StringArgument(null, "sortOrder", false, 1, "{sortOrder}", description)).setArgumentGroupName("Request Control Arguments");
        this.sortOrder.addLongIdentifier("sort-order", true);
        parser.addArgument(this.sortOrder);
        description = "Indicates that the proxied authorization control (as defined in RFC 4370) should be used to request that operations be processed using an alternate authorization identity.  This may be a simple authorization ID or it may be a value pattern to specify a range of identities.  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.";
        (this.proxyAs = new StringArgument('Y', "proxyAs", false, 1, "{authzID}", description)).setArgumentGroupName("Request Control Arguments");
        this.proxyAs.addLongIdentifier("proxy-as", true);
        parser.addArgument(this.proxyAs);
        description = "Indicates that search requests should include the specified request control.  This may be provided multiple times to include multiple request controls.";
        (this.control = new ControlArgument('J', "control", false, 0, null, description)).setArgumentGroupName("Request Control Arguments");
        parser.addArgument(this.control);
        description = "The number of threads to use to perform the searches.  If this is not provided, then a default of one thread will be used.";
        (this.numThreads = new IntegerArgument('t', "numThreads", true, 1, "{num}", description, 1, Integer.MAX_VALUE, 1)).setArgumentGroupName("Rate Management Arguments");
        this.numThreads.addLongIdentifier("num-threads", true);
        parser.addArgument(this.numThreads);
        description = "The length of time in seconds between output lines.  If this is not provided, then a default interval of five seconds will be used.";
        (this.collectionInterval = new IntegerArgument('i', "intervalDuration", true, 1, "{num}", description, 1, Integer.MAX_VALUE, 5)).setArgumentGroupName("Rate Management Arguments");
        this.collectionInterval.addLongIdentifier("interval-duration", true);
        parser.addArgument(this.collectionInterval);
        description = "The maximum number of intervals for which to run.  If this is not provided, then the tool will run until it is interrupted.";
        (this.numIntervals = new IntegerArgument('I', "numIntervals", true, 1, "{num}", description, 1, Integer.MAX_VALUE, Integer.MAX_VALUE)).setArgumentGroupName("Rate Management Arguments");
        this.numIntervals.addLongIdentifier("num-intervals", true);
        parser.addArgument(this.numIntervals);
        description = "The number of search iterations that should be processed on a connection before that connection is closed and replaced with a newly-established (and authenticated, if appropriate) connection.  If this is not provided, then connections will not be periodically closed and re-established.";
        (this.iterationsBeforeReconnect = new IntegerArgument(null, "iterationsBeforeReconnect", false, 1, "{num}", description, 0)).setArgumentGroupName("Rate Management Arguments");
        this.iterationsBeforeReconnect.addLongIdentifier("iterations-before-reconnect", true);
        parser.addArgument(this.iterationsBeforeReconnect);
        description = "The target number of searches to perform per second.  It is still necessary to specify a sufficient number of threads for achieving this rate.  If neither this option nor --variableRateData is provided, then the tool will run at the maximum rate for the specified number of threads.";
        (this.ratePerSecond = new IntegerArgument('r', "ratePerSecond", false, 1, "{searches-per-second}", description, 1, Integer.MAX_VALUE)).setArgumentGroupName("Rate Management Arguments");
        this.ratePerSecond.addLongIdentifier("rate-per-second", true);
        parser.addArgument(this.ratePerSecond);
        final String variableRateDataArgName = "variableRateData";
        final String generateSampleRateFileArgName = "generateSampleRateFile";
        description = RateAdjustor.getVariableRateDataArgumentDescription("generateSampleRateFile");
        (this.variableRateData = new FileArgument(null, "variableRateData", false, 1, "{path}", description, true, true, true, false)).setArgumentGroupName("Rate Management Arguments");
        this.variableRateData.addLongIdentifier("variable-rate-data", true);
        parser.addArgument(this.variableRateData);
        description = RateAdjustor.getGenerateSampleVariableRateFileDescription("variableRateData");
        (this.sampleRateFile = new FileArgument(null, "generateSampleRateFile", false, 1, "{path}", description, false, true, true, false)).setArgumentGroupName("Rate Management Arguments");
        this.sampleRateFile.addLongIdentifier("generate-sample-rate-file", true);
        this.sampleRateFile.setUsageArgument(true);
        parser.addArgument(this.sampleRateFile);
        parser.addExclusiveArgumentSet(this.variableRateData, this.sampleRateFile, new Argument[0]);
        description = "The number of intervals to complete before beginning overall statistics collection.  Specifying a nonzero number of warm-up intervals gives the client and server a chance to warm up without skewing performance results.";
        (this.warmUpIntervals = new IntegerArgument(null, "warmUpIntervals", true, 1, "{num}", description, 0, Integer.MAX_VALUE, 0)).setArgumentGroupName("Rate Management Arguments");
        this.warmUpIntervals.addLongIdentifier("warm-up-intervals", true);
        parser.addArgument(this.warmUpIntervals);
        description = "Indicates the format to use for timestamps included in the output.  A value of 'none' indicates that no timestamps should be included.  A value of 'with-date' indicates that both the date and the time should be included.  A value of 'without-date' indicates that only the time should be included.";
        final Set<String> allowedFormats = StaticUtils.setOf("none", "with-date", "without-date");
        (this.timestampFormat = new StringArgument(null, "timestampFormat", true, 1, "{format}", description, allowedFormats, "none")).addLongIdentifier("timestamp-format", true);
        parser.addArgument(this.timestampFormat);
        description = "Indicates that the client should operate in asynchronous mode, in which it will not be necessary to wait for a response to a previous request before sending the next request.  Either the '--ratePerSecond' or the '--maxOutstandingRequests' argument must be provided to limit the number of outstanding requests.";
        parser.addArgument(this.asynchronousMode = new BooleanArgument('a', "asynchronous", description));
        description = "Specifies the maximum number of outstanding requests that should be allowed when operating in asynchronous mode.";
        (this.maxOutstandingRequests = new IntegerArgument('O', "maxOutstandingRequests", false, 1, "{num}", description, 1, Integer.MAX_VALUE, (Integer)null)).addLongIdentifier("max-outstanding-requests", true);
        parser.addArgument(this.maxOutstandingRequests);
        description = "Indicates that information about the result codes for failed operations should not be displayed.";
        (this.suppressErrors = new BooleanArgument(null, "suppressErrorResultCodes", 1, description)).addLongIdentifier("suppress-error-result-codes", true);
        parser.addArgument(this.suppressErrors);
        description = "Generate output in CSV format rather than a display-friendly format";
        parser.addArgument(this.csvFormat = new BooleanArgument('c', "csv", 1, description));
        description = "Specifies the seed to use for the random number generator.";
        (this.randomSeed = new IntegerArgument('R', "randomSeed", false, 1, "{value}", description)).addLongIdentifier("random-seed", true);
        parser.addArgument(this.randomSeed);
        parser.addExclusiveArgumentSet(this.baseDN, this.ldapURL, new Argument[0]);
        parser.addExclusiveArgumentSet(this.scope, this.ldapURL, new Argument[0]);
        parser.addExclusiveArgumentSet(this.filter, this.ldapURL, new Argument[0]);
        parser.addExclusiveArgumentSet(this.attributes, this.ldapURL, new Argument[0]);
        parser.addRequiredArgumentSet(this.filter, this.ldapURL, new Argument[0]);
        parser.addDependentArgumentSet(this.asynchronousMode, this.ratePerSecond, this.maxOutstandingRequests);
        parser.addDependentArgumentSet(this.maxOutstandingRequests, this.asynchronousMode, new Argument[0]);
        parser.addExclusiveArgumentSet(this.asynchronousMode, this.simplePageSize, new Argument[0]);
    }
    
    @Override
    protected boolean supportsMultipleServers() {
        return true;
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setUseSynchronousMode(!this.asynchronousMode.isPresent());
        return options;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        if (this.sampleRateFile.isPresent()) {
            try {
                RateAdjustor.writeSampleVariableRateFile(this.sampleRateFile.getValue());
                return ResultCode.SUCCESS;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.err("An error occurred while trying to write sample variable data rate file '", this.sampleRateFile.getValue().getAbsolutePath(), "':  ", StaticUtils.getExceptionMessage(e));
                return ResultCode.LOCAL_ERROR;
            }
        }
        Long seed;
        if (this.randomSeed.isPresent()) {
            seed = (long)this.randomSeed.getValue();
        }
        else {
            seed = null;
        }
        ValuePattern dnPattern;
        try {
            if (this.baseDN.getNumOccurrences() > 0) {
                dnPattern = new ValuePattern(this.baseDN.getValue(), seed);
            }
            else if (this.ldapURL.isPresent()) {
                dnPattern = null;
            }
            else {
                dnPattern = new ValuePattern("", seed);
            }
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            this.err("Unable to parse the base DN value pattern:  ", pe.getMessage());
            return ResultCode.PARAM_ERROR;
        }
        ValuePattern filterPattern;
        try {
            if (this.filter.isPresent()) {
                filterPattern = new ValuePattern(this.filter.getValue(), seed);
            }
            else {
                filterPattern = null;
            }
        }
        catch (final ParseException pe2) {
            Debug.debugException(pe2);
            this.err("Unable to parse the filter pattern:  ", pe2.getMessage());
            return ResultCode.PARAM_ERROR;
        }
        ValuePattern ldapURLPattern;
        try {
            if (this.ldapURL.isPresent()) {
                ldapURLPattern = new ValuePattern(this.ldapURL.getValue(), seed);
            }
            else {
                ldapURLPattern = null;
            }
        }
        catch (final ParseException pe3) {
            Debug.debugException(pe3);
            this.err("Unable to parse the LDAP URL pattern:  ", pe3.getMessage());
            return ResultCode.PARAM_ERROR;
        }
        ValuePattern authzIDPattern = null;
        Label_0387: {
            if (this.proxyAs.isPresent()) {
                try {
                    authzIDPattern = new ValuePattern(this.proxyAs.getValue(), seed);
                    break Label_0387;
                }
                catch (final ParseException pe4) {
                    Debug.debugException(pe4);
                    this.err("Unable to parse the proxied authorization pattern:  ", pe4.getMessage());
                    return ResultCode.PARAM_ERROR;
                }
            }
            authzIDPattern = null;
        }
        final String derefValue = StaticUtils.toLowerCase(this.dereferencePolicy.getValue());
        DereferencePolicy derefPolicy;
        if (derefValue.equals("always")) {
            derefPolicy = DereferencePolicy.ALWAYS;
        }
        else if (derefValue.equals("search")) {
            derefPolicy = DereferencePolicy.SEARCHING;
        }
        else if (derefValue.equals("find")) {
            derefPolicy = DereferencePolicy.FINDING;
        }
        else {
            derefPolicy = DereferencePolicy.NEVER;
        }
        final ArrayList<Control> controlList = new ArrayList<Control>(5);
        if (this.assertionFilter.isPresent()) {
            controlList.add(new AssertionRequestControl(this.assertionFilter.getValue()));
        }
        if (this.sortOrder.isPresent()) {
            final ArrayList<SortKey> sortKeys = new ArrayList<SortKey>(5);
            final StringTokenizer tokenizer = new StringTokenizer(this.sortOrder.getValue(), ",");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                boolean ascending;
                if (token.startsWith("+")) {
                    ascending = true;
                    token = token.substring(1);
                }
                else if (token.startsWith("-")) {
                    ascending = false;
                    token = token.substring(1);
                }
                else {
                    ascending = true;
                }
                final int colonPos = token.indexOf(58);
                String attributeName;
                String matchingRuleID;
                if (colonPos < 0) {
                    attributeName = token;
                    matchingRuleID = null;
                }
                else {
                    attributeName = token.substring(0, colonPos);
                    matchingRuleID = token.substring(colonPos + 1);
                }
                sortKeys.add(new SortKey(attributeName, matchingRuleID, !ascending));
            }
            controlList.add(new ServerSideSortRequestControl(sortKeys));
        }
        if (this.control.isPresent()) {
            controlList.addAll(this.control.getValues());
        }
        String[] attrs;
        if (this.attributes.isPresent()) {
            final List<String> attrList = this.attributes.getValues();
            attrs = new String[attrList.size()];
            attrList.toArray(attrs);
        }
        else {
            attrs = StaticUtils.NO_STRINGS;
        }
        FixedRateBarrier fixedRateBarrier = null;
        if (this.ratePerSecond.isPresent() || this.variableRateData.isPresent()) {
            final int intervalSeconds = this.collectionInterval.getValue();
            final int ratePerInterval = (this.ratePerSecond.getValue() == null) ? Integer.MAX_VALUE : (this.ratePerSecond.getValue() * intervalSeconds);
            fixedRateBarrier = new FixedRateBarrier(1000L * intervalSeconds, ratePerInterval);
        }
        RateAdjustor rateAdjustor = null;
        if (this.variableRateData.isPresent()) {
            try {
                rateAdjustor = RateAdjustor.newInstance(fixedRateBarrier, this.ratePerSecond.getValue(), this.variableRateData.getValue());
            }
            catch (final IOException | IllegalArgumentException e2) {
                Debug.debugException(e2);
                this.err("Initializing the variable rates failed: " + e2.getMessage());
                return ResultCode.PARAM_ERROR;
            }
        }
        Semaphore asyncSemaphore;
        if (this.maxOutstandingRequests.isPresent()) {
            asyncSemaphore = new Semaphore(this.maxOutstandingRequests.getValue());
        }
        else {
            asyncSemaphore = null;
        }
        boolean includeTimestamp;
        String timeFormat;
        if (this.timestampFormat.getValue().equalsIgnoreCase("with-date")) {
            includeTimestamp = true;
            timeFormat = "dd/MM/yyyy HH:mm:ss";
        }
        else if (this.timestampFormat.getValue().equalsIgnoreCase("without-date")) {
            includeTimestamp = true;
            timeFormat = "HH:mm:ss";
        }
        else {
            includeTimestamp = false;
            timeFormat = null;
        }
        int remainingWarmUpIntervals = this.warmUpIntervals.getValue();
        boolean warmUp;
        long totalIntervals;
        if (remainingWarmUpIntervals > 0) {
            warmUp = true;
            totalIntervals = 0L + this.numIntervals.getValue() + remainingWarmUpIntervals;
        }
        else {
            warmUp = true;
            totalIntervals = 0L + this.numIntervals.getValue();
        }
        OutputFormat outputFormat;
        if (this.csvFormat.isPresent()) {
            outputFormat = OutputFormat.CSV;
        }
        else {
            outputFormat = OutputFormat.COLUMNS;
        }
        final ColumnFormatter formatter = new ColumnFormatter(includeTimestamp, timeFormat, outputFormat, " ", new FormattableColumn[] { new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Searches/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Avg Dur ms" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Entries/Srch" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Errors/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Searches/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Avg Dur ms" }) });
        final AtomicLong searchCounter = new AtomicLong(0L);
        final AtomicLong entryCounter = new AtomicLong(0L);
        final AtomicLong errorCounter = new AtomicLong(0L);
        final AtomicLong searchDurations = new AtomicLong(0L);
        final ResultCodeCounter rcCounter = new ResultCodeCounter();
        final long intervalMillis = 1000L * this.collectionInterval.getValue();
        final CyclicBarrier barrier = new CyclicBarrier(this.numThreads.getValue() + 1);
        final SearchRateThread[] threads = new SearchRateThread[(int)this.numThreads.getValue()];
        for (int i = 0; i < threads.length; ++i) {
            LDAPConnection connection;
            try {
                connection = this.getConnection();
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err("Unable to connect to the directory server:  ", StaticUtils.getExceptionMessage(le));
                return le.getResultCode();
            }
            (threads[i] = new SearchRateThread(this, i, connection, this.asynchronousMode.isPresent(), dnPattern, this.scope.getValue(), derefPolicy, this.sizeLimit.getValue(), this.timeLimitSeconds.getValue(), this.typesOnly.isPresent(), filterPattern, attrs, ldapURLPattern, authzIDPattern, this.simplePageSize.getValue(), controlList, this.iterationsBeforeReconnect.getValue(), this.runningThreads, barrier, searchCounter, entryCounter, searchDurations, errorCounter, rcCounter, fixedRateBarrier, asyncSemaphore)).start();
        }
        for (final String headerLine : formatter.getHeaderLines(true)) {
            this.out(headerLine);
        }
        if (rateAdjustor != null && remainingWarmUpIntervals <= 0) {
            rateAdjustor.start();
        }
        try {
            barrier.await();
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
        }
        long overallStartTime = System.nanoTime();
        long nextIntervalStartTime = System.currentTimeMillis() + intervalMillis;
        boolean setOverallStartTime = false;
        long lastDuration = 0L;
        long lastNumEntries = 0L;
        long lastNumErrors = 0L;
        long lastNumSearches = 0L;
        long lastEndTime = System.nanoTime();
        for (long j = 0L; j < totalIntervals; ++j) {
            if (rateAdjustor != null && !rateAdjustor.isAlive()) {
                this.out("All of the rates in " + this.variableRateData.getValue().getName() + " have been completed.");
                break;
            }
            final long startTimeMillis = System.currentTimeMillis();
            final long sleepTimeMillis = nextIntervalStartTime - startTimeMillis;
            nextIntervalStartTime += intervalMillis;
            if (sleepTimeMillis > 0L) {
                this.sleeper.sleep(sleepTimeMillis);
            }
            if (this.stopRequested.get()) {
                break;
            }
            final long endTime = System.nanoTime();
            final long intervalDuration = endTime - lastEndTime;
            long numSearches;
            long numEntries;
            long numErrors;
            long totalDuration;
            if (warmUp && remainingWarmUpIntervals > 0) {
                numSearches = searchCounter.getAndSet(0L);
                numEntries = entryCounter.getAndSet(0L);
                numErrors = errorCounter.getAndSet(0L);
                totalDuration = searchDurations.getAndSet(0L);
            }
            else {
                numSearches = searchCounter.get();
                numEntries = entryCounter.get();
                numErrors = errorCounter.get();
                totalDuration = searchDurations.get();
            }
            final long recentNumSearches = numSearches - lastNumSearches;
            final long recentNumEntries = numEntries - lastNumEntries;
            final long recentNumErrors = numErrors - lastNumErrors;
            final long recentDuration = totalDuration - lastDuration;
            final double numSeconds = intervalDuration / 1.0E9;
            final double recentSearchRate = recentNumSearches / numSeconds;
            final double recentErrorRate = recentNumErrors / numSeconds;
            double recentEntriesPerSearch;
            double recentAvgDuration;
            if (recentNumSearches > 0L) {
                recentEntriesPerSearch = 1.0 * recentNumEntries / recentNumSearches;
                recentAvgDuration = 1.0 * recentDuration / recentNumSearches / 1000000.0;
            }
            else {
                recentEntriesPerSearch = 0.0;
                recentAvgDuration = 0.0;
            }
            if (warmUp && remainingWarmUpIntervals > 0) {
                this.out(formatter.formatRow(recentSearchRate, recentAvgDuration, recentEntriesPerSearch, recentErrorRate, "warming up", "warming up"));
                if (--remainingWarmUpIntervals == 0) {
                    this.out("Warm-up completed.  Beginning overall statistics collection.");
                    setOverallStartTime = true;
                    if (rateAdjustor != null) {
                        rateAdjustor.start();
                    }
                }
            }
            else {
                if (setOverallStartTime) {
                    overallStartTime = lastEndTime;
                    setOverallStartTime = false;
                }
                final double numOverallSeconds = (endTime - overallStartTime) / 1.0E9;
                final double overallSearchRate = numSearches / numOverallSeconds;
                double overallAvgDuration;
                if (numSearches > 0L) {
                    overallAvgDuration = 1.0 * totalDuration / numSearches / 1000000.0;
                }
                else {
                    overallAvgDuration = 0.0;
                }
                this.out(formatter.formatRow(recentSearchRate, recentAvgDuration, recentEntriesPerSearch, recentErrorRate, overallSearchRate, overallAvgDuration));
                lastNumSearches = numSearches;
                lastNumEntries = numEntries;
                lastNumErrors = numErrors;
                lastDuration = totalDuration;
            }
            final List<ObjectPair<ResultCode, Long>> rcCounts = rcCounter.getCounts(true);
            if (!this.suppressErrors.isPresent() && !rcCounts.isEmpty()) {
                this.err("\tError Results:");
                for (final ObjectPair<ResultCode, Long> p : rcCounts) {
                    this.err("\t", p.getFirst().getName(), ":  ", p.getSecond());
                }
            }
            lastEndTime = endTime;
        }
        if (rateAdjustor != null) {
            rateAdjustor.shutDown();
        }
        ResultCode resultCode = ResultCode.SUCCESS;
        for (final SearchRateThread t : threads) {
            t.signalShutdown();
        }
        for (final SearchRateThread t : threads) {
            final ResultCode r = t.waitForShutdown();
            if (resultCode == ResultCode.SUCCESS) {
                resultCode = r;
            }
        }
        return resultCode;
    }
    
    public void stopRunning() {
        this.stopRequested.set(true);
        this.sleeper.wakeup();
        while (true) {
            final int stillRunning = this.runningThreads.get();
            if (stillRunning <= 0) {
                break;
            }
            try {
                Thread.sleep(1L);
            }
            catch (final Exception ex) {}
        }
    }
    
    int getMaxOutstandingRequests() {
        if (this.maxOutstandingRequests.isPresent()) {
            return this.maxOutstandingRequests.getValue();
        }
        return -1;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--scope", "sub", "--filter", "(uid=user.[1-1000000])", "--attribute", "givenName", "--attribute", "sn", "--attribute", "mail", "--numThreads", "10" };
        String description = "Test search performance by searching randomly across a set of one million users located below 'dc=example,dc=com' with ten concurrent threads.  The entries returned to the client will include the givenName, sn, and mail attributes.";
        examples.put(args, description);
        args = new String[] { "--generateSampleRateFile", "variable-rate-data.txt" };
        description = "Generate a sample variable rate definition file that may be used in conjunction with the --variableRateData argument.  The sample file will include comments that describe the format for data to be included in this file.";
        examples.put(args, description);
        return examples;
    }
}
