package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.concurrent.CyclicBarrier;
import com.unboundid.util.ResultCodeCounter;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ColumnFormatter;
import com.unboundid.util.HorizontalAlignment;
import com.unboundid.util.FormattableColumn;
import com.unboundid.util.OutputFormat;
import java.util.Collection;
import com.unboundid.ldap.sdk.experimental.DraftBeheraLDAPPasswordPolicy10RequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.unboundid.ldap.sdk.Control;
import java.util.ArrayList;
import java.io.IOException;
import com.unboundid.util.FixedRateBarrier;
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
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
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
public final class AuthRate extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = 6918029871717330547L;
    private final AtomicBoolean stopRequested;
    private final AtomicInteger runningThreads;
    private BooleanArgument authorizationIdentityRequestControl;
    private BooleanArgument bindOnly;
    private BooleanArgument csvFormat;
    private BooleanArgument passwordPolicyRequestControl;
    private BooleanArgument suppressErrorsArgument;
    private ControlArgument bindControl;
    private ControlArgument searchControl;
    private FileArgument sampleRateFile;
    private FileArgument variableRateData;
    private IntegerArgument collectionInterval;
    private IntegerArgument numIntervals;
    private IntegerArgument numThreads;
    private IntegerArgument randomSeed;
    private IntegerArgument ratePerSecond;
    private IntegerArgument warmUpIntervals;
    private StringArgument attributes;
    private StringArgument authType;
    private StringArgument baseDN;
    private StringArgument filter;
    private ScopeArgument scopeArg;
    private StringArgument timestampFormat;
    private StringArgument userPassword;
    private final WakeableSleeper sleeper;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final AuthRate authRate = new AuthRate(outStream, errStream);
        return authRate.runTool(args);
    }
    
    public AuthRate(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.stopRequested = new AtomicBoolean(false);
        this.runningThreads = new AtomicInteger(0);
        this.sleeper = new WakeableSleeper();
    }
    
    @Override
    public String getToolName() {
        return "authrate";
    }
    
    @Override
    public String getToolDescription() {
        return "Perform repeated authentications against an LDAP directory server, where each authentication consists of a search to find a user followed by a bind to verify the credentials for that user.";
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
        String description = "The base DN to use for the searches.  It may be a simple DN or a value pattern to specify a range of DNs (e.g., \"uid=user.[1-1000],ou=People,dc=example,dc=com\").  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  This must be provided.";
        (this.baseDN = new StringArgument('b', "baseDN", true, 1, "{dn}", description)).setArgumentGroupName("Search and Authentication Arguments");
        this.baseDN.addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDN);
        description = "The scope to use for the searches.  It should be 'base', 'one', 'sub', or 'subord'.  If this is not provided, a default scope of 'sub' will be used.";
        (this.scopeArg = new ScopeArgument('s', "scope", false, "{scope}", description, SearchScope.SUB)).setArgumentGroupName("Search and Authentication Arguments");
        parser.addArgument(this.scopeArg);
        description = "The filter to use for the searches.  It may be a simple filter or a value pattern to specify a range of filters (e.g., \"(uid=user.[1-1000])\").  See https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html for complete details about the value pattern syntax.  This must be provided.";
        (this.filter = new StringArgument('f', "filter", true, 1, "{filter}", description)).setArgumentGroupName("Search and Authentication Arguments");
        parser.addArgument(this.filter);
        description = "The name of an attribute to include in entries returned from the searches.  Multiple attributes may be requested by providing this argument multiple times.  If no return attributes are specified, then entries will be returned with all user attributes.";
        (this.attributes = new StringArgument('A', "attribute", false, 0, "{name}", description)).setArgumentGroupName("Search and Authentication Arguments");
        parser.addArgument(this.attributes);
        description = "The password to use when binding as the users returned from the searches.  This must be provided.";
        (this.userPassword = new StringArgument('C', "credentials", true, 1, "{password}", description)).setSensitive(true);
        this.userPassword.setArgumentGroupName("Search and Authentication Arguments");
        parser.addArgument(this.userPassword);
        description = "Indicates that the tool should only perform bind operations without the initial search.  If this argument is provided, then the base DN pattern will be used to obtain the bind DNs.";
        (this.bindOnly = new BooleanArgument('B', "bindOnly", 1, description)).setArgumentGroupName("Search and Authentication Arguments");
        this.bindOnly.addLongIdentifier("bind-only", true);
        parser.addArgument(this.bindOnly);
        description = "The type of authentication to perform.  Allowed values are:  SIMPLE, CRAM-MD5, DIGEST-MD5, and PLAIN.  If no value is provided, then SIMPLE authentication will be performed.";
        final Set<String> allowedAuthTypes = StaticUtils.setOf("simple", "cram-md5", "digest-md5", "plain");
        (this.authType = new StringArgument('a', "authType", true, 1, "{authType}", description, allowedAuthTypes, "simple")).setArgumentGroupName("Search and Authentication Arguments");
        this.authType.addLongIdentifier("auth-type", true);
        parser.addArgument(this.authType);
        description = "Indicates that bind requests should include the authorization identity request control as described in RFC 3829.";
        (this.authorizationIdentityRequestControl = new BooleanArgument(null, "authorizationIdentityRequestControl", 1, description)).setArgumentGroupName("Request Control Arguments");
        this.authorizationIdentityRequestControl.addLongIdentifier("authorization-identity-request-control", true);
        parser.addArgument(this.authorizationIdentityRequestControl);
        description = "Indicates that bind requests should include the password policy request control as described in draft-behera-ldap-password-policy-10.";
        (this.passwordPolicyRequestControl = new BooleanArgument(null, "passwordPolicyRequestControl", 1, description)).setArgumentGroupName("Request Control Arguments");
        this.passwordPolicyRequestControl.addLongIdentifier("password-policy-request-control", true);
        parser.addArgument(this.passwordPolicyRequestControl);
        description = "Indicates that search requests should include the specified request control.  This may be provided multiple times to include multiple search request controls.";
        (this.searchControl = new ControlArgument(null, "searchControl", false, 0, null, description)).setArgumentGroupName("Request Control Arguments");
        this.searchControl.addLongIdentifier("search-control", true);
        parser.addArgument(this.searchControl);
        description = "Indicates that bind requests should include the specified request control.  This may be provided multiple times to include multiple modify request controls.";
        (this.bindControl = new ControlArgument(null, "bindControl", false, 0, null, description)).setArgumentGroupName("Request Control Arguments");
        this.bindControl.addLongIdentifier("bind-control", true);
        parser.addArgument(this.bindControl);
        description = "The number of threads to use to perform the authentication processing.  If this is not provided, then a default of one thread will be used.";
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
        description = "The target number of authorizations to perform per second.  It is still necessary to specify a sufficient number of threads for achieving this rate.  If neither this option nor --variableRateData is provided, then the tool will run at the maximum rate for the specified number of threads.";
        (this.ratePerSecond = new IntegerArgument('r', "ratePerSecond", false, 1, "{auths-per-second}", description, 1, Integer.MAX_VALUE)).setArgumentGroupName("Rate Management Arguments");
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
        (this.suppressErrorsArgument = new BooleanArgument(null, "suppressErrorResultCodes", 1, description)).addLongIdentifier("suppress-error-result-codes", true);
        parser.addArgument(this.suppressErrorsArgument);
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
        final ArrayList<Control> bindControls = new ArrayList<Control>(5);
        if (this.authorizationIdentityRequestControl.isPresent()) {
            bindControls.add(new AuthorizationIdentityRequestControl());
        }
        if (this.passwordPolicyRequestControl.isPresent()) {
            bindControls.add(new DraftBeheraLDAPPasswordPolicy10RequestControl());
        }
        bindControls.addAll(this.bindControl.getValues());
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
        final ColumnFormatter formatter = new ColumnFormatter(includeTimestamp, timeFormat, outputFormat, " ", new FormattableColumn[] { new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Auths/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Avg Dur ms" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Recent", "Errors/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Auths/Sec" }), new FormattableColumn(12, HorizontalAlignment.RIGHT, new String[] { "Overall", "Avg Dur ms" }) });
        final AtomicLong authCounter = new AtomicLong(0L);
        final AtomicLong errorCounter = new AtomicLong(0L);
        final AtomicLong authDurations = new AtomicLong(0L);
        final ResultCodeCounter rcCounter = new ResultCodeCounter();
        final long intervalMillis = 1000L * this.collectionInterval.getValue();
        final CyclicBarrier barrier = new CyclicBarrier(this.numThreads.getValue() + 1);
        final AuthRateThread[] threads = new AuthRateThread[(int)this.numThreads.getValue()];
        for (int i = 0; i < threads.length; ++i) {
            LDAPConnection searchConnection;
            LDAPConnection bindConnection;
            try {
                searchConnection = this.getConnection();
                bindConnection = this.getConnection();
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err("Unable to connect to the directory server:  ", StaticUtils.getExceptionMessage(le));
                return le.getResultCode();
            }
            (threads[i] = new AuthRateThread(this, i, searchConnection, bindConnection, dnPattern, this.scopeArg.getValue(), filterPattern, attrs, this.userPassword.getValue(), this.bindOnly.isPresent(), this.authType.getValue(), this.searchControl.getValues(), bindControls, this.runningThreads, barrier, authCounter, authDurations, errorCounter, rcCounter, fixedRateBarrier)).start();
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
        long lastNumErrors = 0L;
        long lastNumAuths = 0L;
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
            long numAuths;
            long numErrors;
            long totalDuration;
            if (warmUp && remainingWarmUpIntervals > 0) {
                numAuths = authCounter.getAndSet(0L);
                numErrors = errorCounter.getAndSet(0L);
                totalDuration = authDurations.getAndSet(0L);
            }
            else {
                numAuths = authCounter.get();
                numErrors = errorCounter.get();
                totalDuration = authDurations.get();
            }
            final long recentNumAuths = numAuths - lastNumAuths;
            final long recentNumErrors = numErrors - lastNumErrors;
            final long recentDuration = totalDuration - lastDuration;
            final double numSeconds = intervalDuration / 1.0E9;
            final double recentAuthRate = recentNumAuths / numSeconds;
            final double recentErrorRate = recentNumErrors / numSeconds;
            double recentAvgDuration;
            if (recentNumAuths > 0L) {
                recentAvgDuration = 1.0 * recentDuration / recentNumAuths / 1000000.0;
            }
            else {
                recentAvgDuration = 0.0;
            }
            if (warmUp && remainingWarmUpIntervals > 0) {
                this.out(formatter.formatRow(recentAuthRate, recentAvgDuration, recentErrorRate, "warming up", "warming up"));
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
                final double overallAuthRate = numAuths / numOverallSeconds;
                double overallAvgDuration;
                if (numAuths > 0L) {
                    overallAvgDuration = 1.0 * totalDuration / numAuths / 1000000.0;
                }
                else {
                    overallAvgDuration = 0.0;
                }
                this.out(formatter.formatRow(recentAuthRate, recentAvgDuration, recentErrorRate, overallAuthRate, overallAvgDuration));
                lastNumAuths = numAuths;
                lastNumErrors = numErrors;
                lastDuration = totalDuration;
            }
            final List<ObjectPair<ResultCode, Long>> rcCounts = rcCounter.getCounts(true);
            if (!this.suppressErrorsArgument.isPresent() && !rcCounts.isEmpty()) {
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
        for (final AuthRateThread t : threads) {
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
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--scope", "sub", "--filter", "(uid=user.[1-1000000])", "--credentials", "password", "--numThreads", "10" };
        String description = "Test authentication performance by searching randomly across a set of one million users located below 'dc=example,dc=com' with ten concurrent threads and performing simple binds with a password of 'password'.  The searches will be performed anonymously.";
        examples.put(args, description);
        args = new String[] { "--generateSampleRateFile", "variable-rate-data.txt" };
        description = "Generate a sample variable rate definition file that may be used in conjunction with the --variableRateData argument.  The sample file will include comments that describe the format for data to be included in this file.";
        examples.put(args, description);
        return examples;
    }
}
