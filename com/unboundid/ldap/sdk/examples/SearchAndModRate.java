package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;
import com.unboundid.util.ResultCodeCounter;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ColumnFormatter;
import com.unboundid.util.HorizontalAlignment;
import com.unboundid.util.FormattableColumn;
import com.unboundid.util.OutputFormat;
import java.io.IOException;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.PermissiveModifyRequestControl;
import java.util.Collection;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.ldap.sdk.Control;
import java.util.ArrayList;
import java.text.ParseException;
import com.unboundid.util.ValuePattern;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import java.util.Set;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.RateAdjustor;
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
public final class SearchAndModRate extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = 3242469381380526294L;
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private BooleanArgument csvFormat;
    private BooleanArgument permissiveModify;
    private BooleanArgument suppressErrors;
    private ControlArgument modifyControl;
    private ControlArgument searchControl;
    private FileArgument sampleRateFile;
    private FileArgument variableRateData;
    private FilterArgument modifyAssertionFilter;
    private FilterArgument searchAssertionFilter;
    private IntegerArgument collectionInterval;
    private IntegerArgument iterationsBeforeReconnect;
    private IntegerArgument numIntervals;
    private IntegerArgument numThreads;
    private IntegerArgument randomSeed;
    private IntegerArgument ratePerSecond;
    private IntegerArgument simplePageSize;
    private IntegerArgument valueLength;
    private IntegerArgument warmUpIntervals;
    private ScopeArgument scopeArg;
    private StringArgument baseDN;
    private StringArgument characterSet;
    private StringArgument filter;
    private StringArgument modifyAttributes;
    private StringArgument postReadAttribute;
    private StringArgument preReadAttribute;
    private StringArgument proxyAs;
    private StringArgument returnAttributes;
    private StringArgument timestampFormat;
    private final WakeableSleeper sleeper;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final SearchAndModRate searchAndModRate = new SearchAndModRate(outStream, errStream);
        return searchAndModRate.runTool(args);
    }
    
    public SearchAndModRate(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.stopRequested = new AtomicBoolean(false);
        this.runningThreads = new AtomicInteger(0);
        this.sleeper = new WakeableSleeper();
    }
    
    @Override
    public String getToolName() {
        return "search-and-mod-rate";
    }
    
    @Override
    public String getToolDescription() {
        return "Perform repeated searches against an LDAP directory server and modify each entry returned.";
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
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        String description = "The base DN to use for the searches.  It may be a simple DN or a value pattern to specify a range of DNs (e.g., \"uid=user.[1-1000],ou=People,dc=example,dc=com\").  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  This must be provided.";
        (this.baseDN = new StringArgument('b', "baseDN", true, 1, "{dn}", description)).setArgumentGroupName("Search And Modification Arguments");
        this.baseDN.addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDN);
        description = "The scope to use for the searches.  It should be 'base', 'one', 'sub', or 'subord'.  If this is not provided, then a default scope of 'sub' will be used.";
        (this.scopeArg = new ScopeArgument('s', "scope", false, "{scope}", description, SearchScope.SUB)).setArgumentGroupName("Search And Modification Arguments");
        parser.addArgument(this.scopeArg);
        description = "The filter to use for the searches.  It may be a simple filter or a value pattern to specify a range of filters (e.g., \"(uid=user.[1-1000])\").  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  This must be provided.";
        (this.filter = new StringArgument('f', "filter", true, 1, "{filter}", description)).setArgumentGroupName("Search And Modification Arguments");
        parser.addArgument(this.filter);
        description = "The name of an attribute to include in entries returned from the searches.  Multiple attributes may be requested by providing this argument multiple times.  If no request attributes are provided, then the entries returned will include all user attributes.";
        (this.returnAttributes = new StringArgument('A', "attribute", false, 0, "{name}", description)).setArgumentGroupName("Search And Modification Arguments");
        parser.addArgument(this.returnAttributes);
        description = "The name of the attribute to modify.  Multiple attributes may be specified by providing this argument multiple times.  At least one attribute must be specified.";
        (this.modifyAttributes = new StringArgument('m', "modifyAttribute", true, 0, "{name}", description)).setArgumentGroupName("Search And Modification Arguments");
        this.modifyAttributes.addLongIdentifier("modify-attribute", true);
        parser.addArgument(this.modifyAttributes);
        description = "The length in bytes to use when generating values for the modifications.  If this is not provided, then a default length of ten bytes will be used.";
        (this.valueLength = new IntegerArgument('l', "valueLength", true, 1, "{num}", description, 1, Integer.MAX_VALUE, 10)).setArgumentGroupName("Search And Modification Arguments");
        this.valueLength.addLongIdentifier("value-length", true);
        parser.addArgument(this.valueLength);
        description = "The set of characters to use to generate the values for the modifications.  It should only include ASCII characters.  If this is not provided, then a default set of lowercase alphabetic characters will be used.";
        (this.characterSet = new StringArgument('C', "characterSet", true, 1, "{chars}", description, "abcdefghijklmnopqrstuvwxyz")).setArgumentGroupName("Search And Modification Arguments");
        this.characterSet.addLongIdentifier("character-set", true);
        parser.addArgument(this.characterSet);
        description = "Indicates that search requests should include the assertion request control with the specified filter.";
        (this.searchAssertionFilter = new FilterArgument(null, "searchAssertionFilter", false, 1, "{filter}", description)).setArgumentGroupName("Request Control Arguments");
        this.searchAssertionFilter.addLongIdentifier("search-assertion-filter", true);
        parser.addArgument(this.searchAssertionFilter);
        description = "Indicates that modify requests should include the assertion request control with the specified filter.";
        (this.modifyAssertionFilter = new FilterArgument(null, "modifyAssertionFilter", false, 1, "{filter}", description)).setArgumentGroupName("Request Control Arguments");
        this.modifyAssertionFilter.addLongIdentifier("modify-assertion-filter", true);
        parser.addArgument(this.modifyAssertionFilter);
        description = "Indicates that search requests should include the simple paged results control with the specified page size.";
        (this.simplePageSize = new IntegerArgument(null, "simplePageSize", false, 1, "{size}", description, 1, Integer.MAX_VALUE)).setArgumentGroupName("Request Control Arguments");
        this.simplePageSize.addLongIdentifier("simple-page-size", true);
        parser.addArgument(this.simplePageSize);
        description = "Indicates that modify requests should include the permissive modify request control.";
        (this.permissiveModify = new BooleanArgument(null, "permissiveModify", 1, description)).setArgumentGroupName("Request Control Arguments");
        this.permissiveModify.addLongIdentifier("permissive-modify", true);
        parser.addArgument(this.permissiveModify);
        description = "Indicates that modify requests should include the pre-read request control with the specified requested attribute.  This argument may be provided multiple times to indicate that multiple requested attributes should be included in the pre-read request control.";
        (this.preReadAttribute = new StringArgument(null, "preReadAttribute", false, 0, "{attribute}", description)).setArgumentGroupName("Request Control Arguments");
        this.preReadAttribute.addLongIdentifier("pre-read-attribute", true);
        parser.addArgument(this.preReadAttribute);
        description = "Indicates that modify requests should include the post-read request control with the specified requested attribute.  This argument may be provided multiple times to indicate that multiple requested attributes should be included in the post-read request control.";
        (this.postReadAttribute = new StringArgument(null, "postReadAttribute", false, 0, "{attribute}", description)).setArgumentGroupName("Request Control Arguments");
        this.postReadAttribute.addLongIdentifier("post-read-attribute", true);
        parser.addArgument(this.postReadAttribute);
        description = "Indicates that the proxied authorization control (as defined in RFC 4370) should be used to request that operations be processed using an alternate authorization identity.  This may be a simple authorization ID or it may be a value pattern to specify a range of identities.  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.";
        (this.proxyAs = new StringArgument('Y', "proxyAs", false, 1, "{authzID}", description)).setArgumentGroupName("Request Control Arguments");
        this.proxyAs.addLongIdentifier("proxy-as", true);
        parser.addArgument(this.proxyAs);
        description = "Indicates that search requests should include the specified request control.  This may be provided multiple times to include multiple search request controls.";
        (this.searchControl = new ControlArgument(null, "searchControl", false, 0, null, description)).setArgumentGroupName("Request Control Arguments");
        this.searchControl.addLongIdentifier("search-control", true);
        parser.addArgument(this.searchControl);
        description = "Indicates that modify requests should include the specified request control.  This may be provided multiple times to include multiple modify request controls.";
        (this.modifyControl = new ControlArgument(null, "modifyControl", false, 0, null, description)).setArgumentGroupName("Request Control Arguments");
        this.modifyControl.addLongIdentifier("modify-control", true);
        parser.addArgument(this.modifyControl);
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
        description = "The number of search and modify iterations that should be processed on a connection before that connection is closed and replaced with a newly-established (and authenticated, if appropriate) connection.  If this is not provided, then connections will not be periodically closed and re-established.";
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
        description = "Indicates that information about the result codes for failed operations should not be displayed.";
        (this.suppressErrors = new BooleanArgument(null, "suppressErrorResultCodes", 1, description)).addLongIdentifier("suppress-error-result-codes", true);
        parser.addArgument(this.suppressErrors);
        description = "Generate output in CSV format rather than a display-friendly format";
        parser.addArgument(this.csvFormat = new BooleanArgument('c', "csv", 1, description));
        description = "Specifies the seed to use for the random number generator.";
        (this.randomSeed = new IntegerArgument('R', "randomSeed", false, 1, "{value}", description)).addLongIdentifier("random-seed", true);
        parser.addArgument(this.randomSeed);
    }
    
    @Override
    protected boolean supportsMultipleServers() {
        return true;
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setUseSynchronousMode(true);
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
            dnPattern = new ValuePattern(this.baseDN.getValue(), seed);
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            this.err("Unable to parse the base DN value pattern:  ", pe.getMessage());
            return ResultCode.PARAM_ERROR;
        }
        ValuePattern filterPattern;
        try {
            filterPattern = new ValuePattern(this.filter.getValue(), seed);
        }
        catch (final ParseException pe2) {
            Debug.debugException(pe2);
            this.err("Unable to parse the filter pattern:  ", pe2.getMessage());
            return ResultCode.PARAM_ERROR;
        }
        ValuePattern authzIDPattern = null;
        Label_0265: {
            if (this.proxyAs.isPresent()) {
                try {
                    authzIDPattern = new ValuePattern(this.proxyAs.getValue(), seed);
                    break Label_0265;
                }
                catch (final ParseException pe3) {
                    Debug.debugException(pe3);
                    this.err("Unable to parse the proxied authorization pattern:  ", pe3.getMessage());
                    return ResultCode.PARAM_ERROR;
                }
            }
            authzIDPattern = null;
        }
        final ArrayList<Control> searchControls = new ArrayList<Control>(5);
        if (this.searchAssertionFilter.isPresent()) {
            searchControls.add(new AssertionRequestControl(this.searchAssertionFilter.getValue()));
        }
        if (this.searchControl.isPresent()) {
            searchControls.addAll(this.searchControl.getValues());
        }
        final ArrayList<Control> modifyControls = new ArrayList<Control>(5);
        if (this.modifyAssertionFilter.isPresent()) {
            modifyControls.add(new AssertionRequestControl(this.modifyAssertionFilter.getValue()));
        }
        if (this.permissiveModify.isPresent()) {
            modifyControls.add(new PermissiveModifyRequestControl());
        }
        if (this.preReadAttribute.isPresent()) {
            final List<String> attrList = this.preReadAttribute.getValues();
            final String[] attrArray = new String[attrList.size()];
            attrList.toArray(attrArray);
            modifyControls.add(new PreReadRequestControl(attrArray));
        }
        if (this.postReadAttribute.isPresent()) {
            final List<String> attrList = this.postReadAttribute.getValues();
            final String[] attrArray = new String[attrList.size()];
            attrList.toArray(attrArray);
            modifyControls.add(new PostReadRequestControl(attrArray));
        }
        if (this.modifyControl.isPresent()) {
            modifyControls.addAll(this.modifyControl.getValues());
        }
        String[] returnAttrs;
        if (this.returnAttributes.isPresent()) {
            final List<String> attrList2 = this.returnAttributes.getValues();
            returnAttrs = new String[attrList2.size()];
            attrList2.toArray(returnAttrs);
        }
        else {
            returnAttrs = StaticUtils.NO_STRINGS;
        }
        final String[] modAttrs = new String[this.modifyAttributes.getValues().size()];
        this.modifyAttributes.getValues().toArray(modAttrs);
        final byte[] charSet = StaticUtils.getBytes(this.characterSet.getValue());
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
        final ColumnFormatter formatter = new ColumnFormatter(includeTimestamp, timeFormat, outputFormat, " ", new FormattableColumn[] { new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Searches/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Srch Dur ms" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Mods/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Mod Dur ms" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Errors/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Searches/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Srch Dur ms" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Mods/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Mod Dur ms" }) });
        final AtomicLong searchCounter = new AtomicLong(0L);
        final AtomicLong errorCounter = new AtomicLong(0L);
        final AtomicLong modCounter = new AtomicLong(0L);
        final AtomicLong modDurations = new AtomicLong(0L);
        final AtomicLong searchDurations = new AtomicLong(0L);
        final ResultCodeCounter rcCounter = new ResultCodeCounter();
        final long intervalMillis = 1000L * this.collectionInterval.getValue();
        final Random random = new Random();
        final CyclicBarrier barrier = new CyclicBarrier(this.numThreads.getValue() + 1);
        final SearchAndModRateThread[] threads = new SearchAndModRateThread[(int)this.numThreads.getValue()];
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
            (threads[i] = new SearchAndModRateThread(this, i, connection, dnPattern, this.scopeArg.getValue(), filterPattern, returnAttrs, modAttrs, this.valueLength.getValue(), charSet, authzIDPattern, this.simplePageSize.getValue(), searchControls, modifyControls, this.iterationsBeforeReconnect.getValue(), random.nextLong(), this.runningThreads, barrier, searchCounter, modCounter, searchDurations, modDurations, errorCounter, rcCounter, fixedRateBarrier)).start();
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
        long lastSearchDuration = 0L;
        long lastModDuration = 0L;
        long lastNumErrors = 0L;
        long lastNumSearches = 0L;
        long lastNumMods = 0L;
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
            long numMods;
            long numErrors;
            long totalSearchDuration;
            long totalModDuration;
            if (warmUp && remainingWarmUpIntervals > 0) {
                numSearches = searchCounter.getAndSet(0L);
                numMods = modCounter.getAndSet(0L);
                numErrors = errorCounter.getAndSet(0L);
                totalSearchDuration = searchDurations.getAndSet(0L);
                totalModDuration = modDurations.getAndSet(0L);
            }
            else {
                numSearches = searchCounter.get();
                numMods = modCounter.get();
                numErrors = errorCounter.get();
                totalSearchDuration = searchDurations.get();
                totalModDuration = modDurations.get();
            }
            final long recentNumSearches = numSearches - lastNumSearches;
            final long recentNumMods = numMods - lastNumMods;
            final long recentNumErrors = numErrors - lastNumErrors;
            final long recentSearchDuration = totalSearchDuration - lastSearchDuration;
            final long recentModDuration = totalModDuration - lastModDuration;
            final double numSeconds = intervalDuration / 1.0E9;
            final double recentSearchRate = recentNumSearches / numSeconds;
            final double recentModRate = recentNumMods / numSeconds;
            final double recentErrorRate = recentNumErrors / numSeconds;
            double recentAvgSearchDuration;
            if (recentNumSearches > 0L) {
                recentAvgSearchDuration = 1.0 * recentSearchDuration / recentNumSearches / 1000000.0;
            }
            else {
                recentAvgSearchDuration = 0.0;
            }
            double recentAvgModDuration;
            if (recentNumMods > 0L) {
                recentAvgModDuration = 1.0 * recentModDuration / recentNumMods / 1000000.0;
            }
            else {
                recentAvgModDuration = 0.0;
            }
            if (warmUp && remainingWarmUpIntervals > 0) {
                this.out(formatter.formatRow(recentSearchRate, recentAvgSearchDuration, recentModRate, recentAvgModDuration, recentErrorRate, "warming up", "warming up", "warming up", "warming up"));
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
                final double overallModRate = numMods / numOverallSeconds;
                double overallAvgSearchDuration;
                if (numSearches > 0L) {
                    overallAvgSearchDuration = 1.0 * totalSearchDuration / numSearches / 1000000.0;
                }
                else {
                    overallAvgSearchDuration = 0.0;
                }
                double overallAvgModDuration;
                if (numMods > 0L) {
                    overallAvgModDuration = 1.0 * totalModDuration / numMods / 1000000.0;
                }
                else {
                    overallAvgModDuration = 0.0;
                }
                this.out(formatter.formatRow(recentSearchRate, recentAvgSearchDuration, recentModRate, recentAvgModDuration, recentErrorRate, overallSearchRate, overallAvgSearchDuration, overallModRate, overallAvgModDuration));
                lastNumSearches = numSearches;
                lastNumMods = numMods;
                lastNumErrors = numErrors;
                lastSearchDuration = totalSearchDuration;
                lastModDuration = totalModDuration;
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
        for (final SearchAndModRateThread t : threads) {
            final ResultCode r = t.stopRunning();
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
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--scope", "sub", "--filter", "(uid=user.[1-1000000])", "--attribute", "givenName", "--attribute", "sn", "--attribute", "mail", "--modifyAttribute", "description", "--valueLength", "10", "--characterSet", "abcdefghijklmnopqrstuvwxyz0123456789", "--numThreads", "10" };
        String description = "Test search and modify performance by searching randomly across a set of one million users located below 'dc=example,dc=com' with ten concurrent threads.  The entries returned to the client will include the givenName, sn, and mail attributes, and the description attribute of each entry returned will be replaced with a string of ten randomly-selected alphanumeric characters.";
        examples.put(args, description);
        args = new String[] { "--generateSampleRateFile", "variable-rate-data.txt" };
        description = "Generate a sample variable rate definition file that may be used in conjunction with the --variableRateData argument.  The sample file will include comments that describe the format for data to be included in this file.";
        examples.put(args, description);
        return examples;
    }
}
