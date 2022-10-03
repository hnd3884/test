package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Date;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.WakeableSleeper;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.args.Argument;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.ArgumentParser;
import java.text.SimpleDateFormat;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPSearch extends LDAPCommandLineTool implements SearchResultListener
{
    private static final SimpleDateFormat DATE_FORMAT;
    private static final long serialVersionUID = 7465188734621412477L;
    private ArgumentParser parser;
    private boolean repeat;
    private BooleanArgument followReferrals;
    private BooleanArgument terseMode;
    private ControlArgument bindControls;
    private ControlArgument searchControls;
    private IntegerArgument numSearches;
    private IntegerArgument repeatIntervalMillis;
    private DNArgument baseDN;
    private ScopeArgument scopeArg;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final LDAPSearch ldapSearch = new LDAPSearch(outStream, errStream);
        return ldapSearch.runTool(args);
    }
    
    public LDAPSearch(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
    }
    
    @Override
    public String getToolName() {
        return "ldapsearch";
    }
    
    @Override
    public String getToolDescription() {
        return "Search an LDAP directory server.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public int getMinTrailingArguments() {
        return 1;
    }
    
    @Override
    public int getMaxTrailingArguments() {
        return -1;
    }
    
    @Override
    public String getTrailingArgumentsPlaceholder() {
        return "{filter} [attr1 [attr2 [...]]]";
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
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean defaultToPromptForBindPassword() {
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
        this.parser = parser;
        String description = "The base DN to use for the search.  This must be provided.";
        (this.baseDN = new DNArgument('b', "baseDN", true, 1, "{dn}", description)).addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDN);
        description = "The scope to use for the search.  It should be 'base', 'one', 'sub', or 'subord'.  If this is not provided, then a default scope of 'sub' will be used.";
        parser.addArgument(this.scopeArg = new ScopeArgument('s', "scope", false, "{scope}", description, SearchScope.SUB));
        description = "Follow any referrals encountered during processing.";
        (this.followReferrals = new BooleanArgument('R', "followReferrals", description)).addLongIdentifier("follow-referrals", true);
        parser.addArgument(this.followReferrals);
        description = "Information about a control to include in the bind request.";
        (this.bindControls = new ControlArgument(null, "bindControl", false, 0, null, description)).addLongIdentifier("bind-control", true);
        parser.addArgument(this.bindControls);
        description = "Information about a control to include in search requests.";
        parser.addArgument(this.searchControls = new ControlArgument('J', "control", false, 0, null, description));
        description = "Generate terse output with minimal additional information.";
        parser.addArgument(this.terseMode = new BooleanArgument('t', "terse", description));
        description = "Specifies the length of time in milliseconds to sleep before repeating the same search.  If this is not provided, then the search will only be performed once.";
        (this.repeatIntervalMillis = new IntegerArgument('i', "repeatIntervalMillis", false, 1, "{millis}", description, 0, Integer.MAX_VALUE)).addLongIdentifier("repeat-interval-millis", true);
        parser.addArgument(this.repeatIntervalMillis);
        description = "Specifies the number of times that the search should be performed.  If this argument is present, then the --repeatIntervalMillis argument must also be provided to specify the length of time between searches.  If --repeatIntervalMillis is used without --numSearches, then the search will be repeated until the tool is interrupted.";
        (this.numSearches = new IntegerArgument('n', "numSearches", false, 1, "{count}", description, 1, Integer.MAX_VALUE)).addLongIdentifier("num-searches", true);
        parser.addArgument(this.numSearches);
        parser.addDependentArgumentSet(this.numSearches, this.repeatIntervalMillis, new Argument[0]);
    }
    
    @Override
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
        if (this.parser.getTrailingArguments().isEmpty()) {
            throw new ArgumentException("At least one trailing argument must be provided to specify the search filter.  Additional trailing arguments are allowed to specify the attributes to return in search result entries.");
        }
        try {
            Filter.create(this.parser.getTrailingArguments().get(0));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException("The first trailing argument value could not be parsed as a valid LDAP search filter.", e);
        }
    }
    
    @Override
    protected List<Control> getBindControls() {
        return this.bindControls.getValues();
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final List<String> trailingArguments = this.parser.getTrailingArguments();
        if (trailingArguments.isEmpty()) {
            this.err("No search filter was provided.");
            this.err(new Object[0]);
            this.err(this.parser.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
            return ResultCode.PARAM_ERROR;
        }
        Filter filter;
        try {
            filter = Filter.create(trailingArguments.get(0));
        }
        catch (final LDAPException le) {
            this.err("Invalid search filter:  ", le.getMessage());
            return le.getResultCode();
        }
        String[] attributesToReturn;
        if (trailingArguments.size() > 1) {
            attributesToReturn = new String[trailingArguments.size() - 1];
            for (int i = 1; i < trailingArguments.size(); ++i) {
                attributesToReturn[i - 1] = trailingArguments.get(i);
            }
        }
        else {
            attributesToReturn = StaticUtils.NO_STRINGS;
        }
        LDAPConnection connection;
        try {
            connection = this.getConnection();
            if (!this.terseMode.isPresent()) {
                this.out("# Connected to ", connection.getConnectedAddress(), ':', connection.getConnectedPort());
            }
        }
        catch (final LDAPException le2) {
            this.err("Error connecting to the directory server:  ", le2.getMessage());
            return le2.getResultCode();
        }
        final SearchRequest searchRequest = new SearchRequest(this, this.baseDN.getStringValue(), this.scopeArg.getValue(), DereferencePolicy.NEVER, 0, 0, false, filter, attributesToReturn);
        searchRequest.setFollowReferrals(this.followReferrals.isPresent());
        final List<Control> controlList = this.searchControls.getValues();
        if (controlList != null) {
            searchRequest.setControls(controlList);
        }
        boolean infinite;
        int numIterations;
        if (this.repeatIntervalMillis.isPresent()) {
            this.repeat = true;
            if (this.numSearches.isPresent()) {
                infinite = false;
                numIterations = this.numSearches.getValue();
            }
            else {
                infinite = true;
                numIterations = Integer.MAX_VALUE;
            }
        }
        else {
            infinite = false;
            this.repeat = false;
            numIterations = 1;
        }
        ResultCode resultCode = ResultCode.SUCCESS;
        long lastSearchTime = System.currentTimeMillis();
        final WakeableSleeper sleeper = new WakeableSleeper();
        for (int j = 0; infinite || j < numIterations; ++j) {
            if (this.repeat && j > 0) {
                final long sleepTime = lastSearchTime + this.repeatIntervalMillis.getValue() - System.currentTimeMillis();
                if (sleepTime > 0L) {
                    sleeper.sleep(sleepTime);
                }
                lastSearchTime = System.currentTimeMillis();
            }
            try {
                final SearchResult searchResult = connection.search(searchRequest);
                if (!this.repeat && !this.terseMode.isPresent()) {
                    this.out("# The search operation was processed successfully.");
                    this.out("# Entries returned:  ", searchResult.getEntryCount());
                    this.out("# References returned:  ", searchResult.getReferenceCount());
                }
            }
            catch (final LDAPException le3) {
                this.err("An error occurred while processing the search:  ", le3.getMessage());
                this.err("Result Code:  ", le3.getResultCode().intValue(), " (", le3.getResultCode().getName(), ')');
                if (le3.getMatchedDN() != null) {
                    this.err("Matched DN:  ", le3.getMatchedDN());
                }
                if (le3.getReferralURLs() != null) {
                    for (final String url : le3.getReferralURLs()) {
                        this.err("Referral URL:  ", url);
                    }
                }
                if (resultCode == ResultCode.SUCCESS) {
                    resultCode = le3.getResultCode();
                }
                if (!le3.getResultCode().isConnectionUsable()) {
                    break;
                }
            }
        }
        connection.close();
        if (!this.terseMode.isPresent()) {
            this.out(new Object[0]);
            this.out("# Disconnected from the server");
        }
        return resultCode;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry entry) {
        if (this.repeat) {
            this.out("# ", LDAPSearch.DATE_FORMAT.format(new Date()));
        }
        this.out(entry.toLDIFString());
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference reference) {
        if (this.repeat) {
            this.out("# ", LDAPSearch.DATE_FORMAT.format(new Date()));
        }
        this.out(reference.toString());
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--scope", "sub", "(uid=jdoe)", "givenName", "sn", "mail" };
        final String description = "Perform a search in the directory server to find all entries matching the filter '(uid=jdoe)' anywhere below 'dc=example,dc=com'.  Include only the givenName, sn, and mail attributes in the entries that are returned.";
        examples.put(args, "Perform a search in the directory server to find all entries matching the filter '(uid=jdoe)' anywhere below 'dc=example,dc=com'.  Include only the givenName, sn, and mail attributes in the entries that are returned.");
        return examples;
    }
    
    static {
        DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss.SSS");
    }
}
