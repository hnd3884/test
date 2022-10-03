package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.unboundidds.controls.PermitUnindexedSearchRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RejectUnindexedSearchRequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV1RequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetEffectiveRightsRequestControl;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.ExcludeBranchRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.VirtualAttributesOnlyRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToServerRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RealAttributesOnlyRequestControl;
import com.unboundid.ldap.sdk.controls.ManageDsaITRequestControl;
import com.unboundid.ldap.sdk.controls.SubentriesRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeletedEntryAccessRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.ReturnConflictEntriesRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetServerIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetBackendSetIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.AccountUsableRequestControl;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.util.FilterFileReader;
import java.io.IOException;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.LDAPURL;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.ldap.sdk.LDAPConnectionPoolHealthCheck;
import com.unboundid.ldap.sdk.PostConnectProcessor;
import com.unboundid.util.CommandLineTool;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartAdministrativeSessionPostConnectProcessor;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartAdministrativeSessionExtendedRequest;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.util.TeeOutputStream;
import java.util.zip.GZIPOutputStream;
import com.unboundid.util.PassphraseEncryptedOutputStream;
import java.io.FileOutputStream;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.OutputFormat;
import com.unboundid.ldap.sdk.transformations.MoveSubtreeTransformation;
import com.unboundid.ldap.sdk.transformations.RenameAttributeTransformation;
import com.unboundid.ldap.sdk.transformations.ScrambleAttributeTransformation;
import com.unboundid.ldap.sdk.transformations.RedactAttributeTransformation;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.transformations.ExcludeAttributeTransformation;
import com.unboundid.ldap.sdk.unboundidds.controls.JoinRequestValue;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.unboundidds.controls.JoinBaseDN;
import com.unboundid.ldap.sdk.unboundidds.controls.JoinRule;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.persist.PersistUtils;
import java.util.StringTokenizer;
import com.unboundid.ldap.sdk.controls.SortKey;
import com.unboundid.ldap.sdk.controls.PersistentSearchChangeType;
import java.util.Map;
import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.controls.MatchedValuesFilter;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Filter;
import java.util.Iterator;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressOperationalAttributeUpdateRequestControl;
import java.util.EnumSet;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressType;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordPolicyRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetUserResourceLimitsRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetAuthorizationEntryRequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import java.util.Collection;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.args.Argument;
import java.util.Collections;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.ldap.sdk.transformations.EntryTransformation;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.controls.VirtualListViewRequestControl;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import com.unboundid.ldap.sdk.controls.PersistentSearchRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.OverrideSearchLimitsRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.MatchingEntryCountRequestControl;
import com.unboundid.ldap.sdk.controls.MatchedValuesRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToBackendSetRequestControl;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.controls.JoinRequestControl;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPSearch extends LDAPCommandLineTool implements UnsolicitedNotificationHandler
{
    private static int WRAP_COLUMN;
    private BooleanArgument accountUsable;
    private BooleanArgument authorizationIdentity;
    private BooleanArgument compressOutput;
    private BooleanArgument continueOnError;
    private BooleanArgument countEntries;
    private BooleanArgument dontWrap;
    private BooleanArgument dryRun;
    private BooleanArgument encryptOutput;
    private BooleanArgument followReferrals;
    private BooleanArgument hideRedactedValueCount;
    private BooleanArgument getBackendSetID;
    private BooleanArgument getServerID;
    private BooleanArgument getUserResourceLimits;
    private BooleanArgument includeReplicationConflictEntries;
    private BooleanArgument includeSubentries;
    private BooleanArgument joinRequireMatch;
    private BooleanArgument manageDsaIT;
    private BooleanArgument permitUnindexedSearch;
    private BooleanArgument realAttributesOnly;
    private BooleanArgument rejectUnindexedSearch;
    private BooleanArgument retryFailedOperations;
    private BooleanArgument separateOutputFilePerSearch;
    private BooleanArgument suppressBase64EncodedValueComments;
    private BooleanArgument teeResultsToStandardOut;
    private BooleanArgument useAdministrativeSession;
    private BooleanArgument usePasswordPolicyControl;
    private BooleanArgument terse;
    private BooleanArgument typesOnly;
    private BooleanArgument verbose;
    private BooleanArgument virtualAttributesOnly;
    private ControlArgument bindControl;
    private ControlArgument searchControl;
    private DNArgument baseDN;
    private DNArgument excludeBranch;
    private DNArgument moveSubtreeFrom;
    private DNArgument moveSubtreeTo;
    private DNArgument proxyV1As;
    private FileArgument encryptionPassphraseFile;
    private FileArgument filterFile;
    private FileArgument ldapURLFile;
    private FileArgument outputFile;
    private FilterArgument assertionFilter;
    private FilterArgument filter;
    private FilterArgument joinFilter;
    private FilterArgument matchedValuesFilter;
    private IntegerArgument joinSizeLimit;
    private IntegerArgument ratePerSecond;
    private IntegerArgument scrambleRandomSeed;
    private IntegerArgument simplePageSize;
    private IntegerArgument sizeLimit;
    private IntegerArgument timeLimitSeconds;
    private IntegerArgument wrapColumn;
    private ScopeArgument joinScope;
    private ScopeArgument scope;
    private StringArgument dereferencePolicy;
    private StringArgument excludeAttribute;
    private StringArgument getAuthorizationEntryAttribute;
    private StringArgument getEffectiveRightsAttribute;
    private StringArgument getEffectiveRightsAuthzID;
    private StringArgument includeSoftDeletedEntries;
    private StringArgument joinBaseDN;
    private StringArgument joinRequestedAttribute;
    private StringArgument joinRule;
    private StringArgument matchingEntryCountControl;
    private StringArgument operationPurpose;
    private StringArgument outputFormat;
    private StringArgument overrideSearchLimit;
    private StringArgument persistentSearch;
    private StringArgument proxyAs;
    private StringArgument redactAttribute;
    private StringArgument renameAttributeFrom;
    private StringArgument renameAttributeTo;
    private StringArgument requestedAttribute;
    private StringArgument routeToBackendSet;
    private StringArgument routeToServer;
    private StringArgument scrambleAttribute;
    private StringArgument scrambleJSONField;
    private StringArgument sortOrder;
    private StringArgument suppressOperationalAttributeUpdates;
    private StringArgument virtualListView;
    private volatile ArgumentParser parser;
    private volatile JoinRequestControl joinRequestControl;
    private final List<RouteToBackendSetRequestControl> routeToBackendSetRequestControls;
    private volatile MatchedValuesRequestControl matchedValuesRequestControl;
    private volatile MatchingEntryCountRequestControl matchingEntryCountRequestControl;
    private volatile OverrideSearchLimitsRequestControl overrideSearchLimitsRequestControl;
    private volatile PersistentSearchRequestControl persistentSearchRequestControl;
    private volatile ServerSideSortRequestControl sortRequestControl;
    private volatile VirtualListViewRequestControl vlvRequestControl;
    private volatile DereferencePolicy derefPolicy;
    private final AtomicLong outputFileCounter;
    private volatile PrintStream errStream;
    private volatile PrintStream outStream;
    private volatile LDAPSearchOutputHandler outputHandler;
    private volatile List<EntryTransformation> entryTransformations;
    private String encryptionPassphrase;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(Math.min(resultCode.intValue(), 255));
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final LDAPSearch tool = new LDAPSearch(out, err);
        return tool.runTool(args);
    }
    
    public LDAPSearch(final OutputStream out, final OutputStream err) {
        super(out, err);
        this.accountUsable = null;
        this.authorizationIdentity = null;
        this.compressOutput = null;
        this.continueOnError = null;
        this.countEntries = null;
        this.dontWrap = null;
        this.dryRun = null;
        this.encryptOutput = null;
        this.followReferrals = null;
        this.hideRedactedValueCount = null;
        this.getBackendSetID = null;
        this.getServerID = null;
        this.getUserResourceLimits = null;
        this.includeReplicationConflictEntries = null;
        this.includeSubentries = null;
        this.joinRequireMatch = null;
        this.manageDsaIT = null;
        this.permitUnindexedSearch = null;
        this.realAttributesOnly = null;
        this.rejectUnindexedSearch = null;
        this.retryFailedOperations = null;
        this.separateOutputFilePerSearch = null;
        this.suppressBase64EncodedValueComments = null;
        this.teeResultsToStandardOut = null;
        this.useAdministrativeSession = null;
        this.usePasswordPolicyControl = null;
        this.terse = null;
        this.typesOnly = null;
        this.verbose = null;
        this.virtualAttributesOnly = null;
        this.bindControl = null;
        this.searchControl = null;
        this.baseDN = null;
        this.excludeBranch = null;
        this.moveSubtreeFrom = null;
        this.moveSubtreeTo = null;
        this.proxyV1As = null;
        this.encryptionPassphraseFile = null;
        this.filterFile = null;
        this.ldapURLFile = null;
        this.outputFile = null;
        this.assertionFilter = null;
        this.filter = null;
        this.joinFilter = null;
        this.matchedValuesFilter = null;
        this.joinSizeLimit = null;
        this.ratePerSecond = null;
        this.scrambleRandomSeed = null;
        this.simplePageSize = null;
        this.sizeLimit = null;
        this.timeLimitSeconds = null;
        this.wrapColumn = null;
        this.joinScope = null;
        this.scope = null;
        this.dereferencePolicy = null;
        this.excludeAttribute = null;
        this.getAuthorizationEntryAttribute = null;
        this.getEffectiveRightsAttribute = null;
        this.getEffectiveRightsAuthzID = null;
        this.includeSoftDeletedEntries = null;
        this.joinBaseDN = null;
        this.joinRequestedAttribute = null;
        this.joinRule = null;
        this.matchingEntryCountControl = null;
        this.operationPurpose = null;
        this.outputFormat = null;
        this.overrideSearchLimit = null;
        this.persistentSearch = null;
        this.proxyAs = null;
        this.redactAttribute = null;
        this.renameAttributeFrom = null;
        this.renameAttributeTo = null;
        this.requestedAttribute = null;
        this.routeToBackendSet = null;
        this.routeToServer = null;
        this.scrambleAttribute = null;
        this.scrambleJSONField = null;
        this.sortOrder = null;
        this.suppressOperationalAttributeUpdates = null;
        this.virtualListView = null;
        this.parser = null;
        this.joinRequestControl = null;
        this.routeToBackendSetRequestControls = new ArrayList<RouteToBackendSetRequestControl>(10);
        this.matchedValuesRequestControl = null;
        this.matchingEntryCountRequestControl = null;
        this.overrideSearchLimitsRequestControl = null;
        this.persistentSearchRequestControl = null;
        this.sortRequestControl = null;
        this.vlvRequestControl = null;
        this.derefPolicy = null;
        this.outputFileCounter = new AtomicLong(1L);
        this.errStream = null;
        this.outStream = null;
        this.outputHandler = new LDIFLDAPSearchOutputHandler(this, LDAPSearch.WRAP_COLUMN);
        this.entryTransformations = null;
        this.encryptionPassphrase = null;
    }
    
    @Override
    public String getToolName() {
        return "ldapsearch";
    }
    
    @Override
    public String getToolDescription() {
        return ToolMessages.INFO_LDAPSEARCH_TOOL_DESCRIPTION.get();
    }
    
    @Override
    public List<String> getAdditionalDescriptionParagraphs() {
        return Arrays.asList(ToolMessages.INFO_LDAPSEARCH_ADDITIONAL_DESCRIPTION_PARAGRAPH_1.get(), ToolMessages.INFO_LDAPSEARCH_ADDITIONAL_DESCRIPTION_PARAGRAPH_2.get());
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public int getMinTrailingArguments() {
        return 0;
    }
    
    @Override
    public int getMaxTrailingArguments() {
        return -1;
    }
    
    @Override
    public String getTrailingArgumentsPlaceholder() {
        return ToolMessages.INFO_LDAPSEARCH_TRAILING_ARGS_PLACEHOLDER.get();
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
    protected Set<Character> getSuppressedShortIdentifiers() {
        return Collections.singleton('T');
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        this.parser = parser;
        (this.baseDN = new DNArgument('b', "baseDN", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_BASE_DN.get())).addLongIdentifier("base-dn", true);
        this.baseDN.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.baseDN);
        (this.scope = new ScopeArgument('s', "scope", false, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SCOPE.get(), SearchScope.SUB)).addLongIdentifier("searchScope", true);
        this.scope.addLongIdentifier("search-scope", true);
        this.scope.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.scope);
        (this.sizeLimit = new IntegerArgument('z', "sizeLimit", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SIZE_LIMIT.get(), 0, Integer.MAX_VALUE, 0)).addLongIdentifier("size-limit", true);
        this.sizeLimit.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.sizeLimit);
        (this.timeLimitSeconds = new IntegerArgument('l', "timeLimitSeconds", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_TIME_LIMIT.get(), 0, Integer.MAX_VALUE, 0)).addLongIdentifier("timeLimit", true);
        this.timeLimitSeconds.addLongIdentifier("time-limit-seconds", true);
        this.timeLimitSeconds.addLongIdentifier("time-limit", true);
        this.timeLimitSeconds.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.timeLimitSeconds);
        final Set<String> derefAllowedValues = StaticUtils.setOf("never", "always", "search", "find");
        (this.dereferencePolicy = new StringArgument('a', "dereferencePolicy", false, 1, "{never|always|search|find}", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_DEREFERENCE_POLICY.get(), derefAllowedValues, "never")).addLongIdentifier("dereference-policy", true);
        this.dereferencePolicy.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.dereferencePolicy);
        (this.typesOnly = new BooleanArgument('A', "typesOnly", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_TYPES_ONLY.get())).addLongIdentifier("types-only", true);
        this.typesOnly.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.typesOnly);
        (this.requestedAttribute = new StringArgument(null, "requestedAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_REQUESTED_ATTR.get())).addLongIdentifier("requested-attribute", true);
        this.requestedAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.requestedAttribute);
        (this.filter = new FilterArgument(null, "filter", false, 0, ToolMessages.INFO_PLACEHOLDER_FILTER.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_FILTER.get())).setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.filter);
        (this.filterFile = new FileArgument('f', "filterFile", false, 0, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_FILTER_FILE.get(), true, true, true, false)).addLongIdentifier("filename", true);
        this.filterFile.addLongIdentifier("filter-file", true);
        this.filterFile.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.filterFile);
        (this.ldapURLFile = new FileArgument(null, "ldapURLFile", false, 0, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_LDAP_URL_FILE.get(), true, true, true, false)).addLongIdentifier("ldap-url-file", true);
        this.ldapURLFile.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.ldapURLFile);
        (this.followReferrals = new BooleanArgument(null, "followReferrals", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_FOLLOW_REFERRALS.get())).addLongIdentifier("follow-referrals", true);
        this.followReferrals.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.followReferrals);
        (this.retryFailedOperations = new BooleanArgument(null, "retryFailedOperations", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_RETRY_FAILED_OPERATIONS.get())).addLongIdentifier("retry-failed-operations", true);
        this.retryFailedOperations.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.retryFailedOperations);
        (this.continueOnError = new BooleanArgument('c', "continueOnError", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_CONTINUE_ON_ERROR.get())).addLongIdentifier("continue-on-error", true);
        this.continueOnError.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.continueOnError);
        (this.ratePerSecond = new IntegerArgument('r', "ratePerSecond", false, 1, ToolMessages.INFO_PLACEHOLDER_NUM.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_RATE_PER_SECOND.get(), 1, Integer.MAX_VALUE)).addLongIdentifier("rate-per-second", true);
        this.ratePerSecond.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.ratePerSecond);
        (this.useAdministrativeSession = new BooleanArgument(null, "useAdministrativeSession", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_USE_ADMIN_SESSION.get())).addLongIdentifier("use-administrative-session", true);
        this.useAdministrativeSession.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.useAdministrativeSession);
        (this.dryRun = new BooleanArgument('n', "dryRun", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_DRY_RUN.get())).addLongIdentifier("dry-run", true);
        this.dryRun.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        parser.addArgument(this.dryRun);
        (this.wrapColumn = new IntegerArgument(null, "wrapColumn", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_WRAP_COLUMN.get(), 0, Integer.MAX_VALUE)).addLongIdentifier("wrap-column", true);
        this.wrapColumn.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.wrapColumn);
        (this.dontWrap = new BooleanArgument('T', "dontWrap", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_DONT_WRAP.get())).addLongIdentifier("doNotWrap", true);
        this.dontWrap.addLongIdentifier("dont-wrap", true);
        this.dontWrap.addLongIdentifier("do-not-wrap", true);
        this.dontWrap.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.dontWrap);
        (this.suppressBase64EncodedValueComments = new BooleanArgument(null, "suppressBase64EncodedValueComments", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SUPPRESS_BASE64_COMMENTS.get())).addLongIdentifier("suppress-base64-encoded-value-comments", true);
        this.suppressBase64EncodedValueComments.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.suppressBase64EncodedValueComments);
        (this.countEntries = new BooleanArgument(null, "countEntries", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_COUNT_ENTRIES.get())).addLongIdentifier("count-entries", true);
        this.countEntries.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_OPS.get());
        this.countEntries.setHidden(true);
        parser.addArgument(this.countEntries);
        (this.outputFile = new FileArgument(null, "outputFile", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_OUTPUT_FILE.get(), false, true, true, false)).addLongIdentifier("output-file", true);
        this.outputFile.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.outputFile);
        (this.compressOutput = new BooleanArgument(null, "compressOutput", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_COMPRESS_OUTPUT.get())).addLongIdentifier("compress-output", true);
        this.compressOutput.addLongIdentifier("compress", true);
        this.compressOutput.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.compressOutput);
        (this.encryptOutput = new BooleanArgument(null, "encryptOutput", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_ENCRYPT_OUTPUT.get())).addLongIdentifier("encrypt-output", true);
        this.encryptOutput.addLongIdentifier("encrypt", true);
        this.encryptOutput.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.encryptOutput);
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_ENCRYPTION_PW_FILE.get(), true, true, true, false)).addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        this.encryptionPassphraseFile.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.encryptionPassphraseFile);
        (this.separateOutputFilePerSearch = new BooleanArgument(null, "separateOutputFilePerSearch", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SEPARATE_OUTPUT_FILES.get())).addLongIdentifier("separate-output-file-per-search", true);
        this.separateOutputFilePerSearch.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.separateOutputFilePerSearch);
        (this.teeResultsToStandardOut = new BooleanArgument(null, "teeResultsToStandardOut", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_TEE.get("outputFile"))).addLongIdentifier("tee-results-to-standard-out", true);
        this.teeResultsToStandardOut.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.teeResultsToStandardOut);
        final Set<String> outputFormatAllowedValues = StaticUtils.setOf("ldif", "json", "csv", "tab-delimited");
        (this.outputFormat = new StringArgument(null, "outputFormat", false, 1, "{ldif|json|csv|tab-delimited}", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_OUTPUT_FORMAT.get(this.requestedAttribute.getIdentifierString(), this.ldapURLFile.getIdentifierString()), outputFormatAllowedValues, "ldif")).addLongIdentifier("output-format", true);
        this.outputFormat.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.outputFormat);
        (this.terse = new BooleanArgument(null, "terse", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_TERSE.get())).setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.terse);
        (this.verbose = new BooleanArgument('v', "verbose", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_VERBOSE.get())).setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_DATA.get());
        parser.addArgument(this.verbose);
        (this.bindControl = new ControlArgument(null, "bindControl", false, 0, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_BIND_CONTROL.get())).addLongIdentifier("bind-control", true);
        this.bindControl.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.bindControl);
        (this.searchControl = new ControlArgument('J', "control", false, 0, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SEARCH_CONTROL.get())).addLongIdentifier("searchControl", true);
        this.searchControl.addLongIdentifier("search-control", true);
        this.searchControl.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.searchControl);
        (this.authorizationIdentity = new BooleanArgument('E', "authorizationIdentity", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_AUTHZ_IDENTITY.get())).addLongIdentifier("reportAuthzID", true);
        this.authorizationIdentity.addLongIdentifier("authorization-identity", true);
        this.authorizationIdentity.addLongIdentifier("report-authzid", true);
        this.authorizationIdentity.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.authorizationIdentity);
        (this.assertionFilter = new FilterArgument(null, "assertionFilter", false, 1, ToolMessages.INFO_PLACEHOLDER_FILTER.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_ASSERTION_FILTER.get())).addLongIdentifier("assertion-filter", true);
        this.assertionFilter.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.assertionFilter);
        (this.accountUsable = new BooleanArgument(null, "accountUsable", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_ACCOUNT_USABLE.get())).addLongIdentifier("account-usable", true);
        this.accountUsable.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.accountUsable);
        (this.excludeBranch = new DNArgument(null, "excludeBranch", false, 0, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_EXCLUDE_BRANCH.get())).addLongIdentifier("exclude-branch", true);
        this.excludeBranch.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.excludeBranch);
        (this.getAuthorizationEntryAttribute = new StringArgument(null, "getAuthorizationEntryAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_GET_AUTHZ_ENTRY_ATTR.get())).addLongIdentifier("get-authorization-entry-attribute", true);
        this.getAuthorizationEntryAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getAuthorizationEntryAttribute);
        (this.getBackendSetID = new BooleanArgument(null, "getBackendSetID", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_GET_BACKEND_SET_ID.get())).addLongIdentifier("get-backend-set-id", true);
        this.getBackendSetID.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getBackendSetID);
        (this.getEffectiveRightsAuthzID = new StringArgument('g', "getEffectiveRightsAuthzID", false, 1, ToolMessages.INFO_PLACEHOLDER_AUTHZID.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_GET_EFFECTIVE_RIGHTS_AUTHZID.get("getEffectiveRightsAttribute"))).addLongIdentifier("get-effective-rights-authzid", true);
        this.getEffectiveRightsAuthzID.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getEffectiveRightsAuthzID);
        (this.getEffectiveRightsAttribute = new StringArgument('e', "getEffectiveRightsAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_GET_EFFECTIVE_RIGHTS_ATTR.get())).addLongIdentifier("get-effective-rights-attribute", true);
        this.getEffectiveRightsAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getEffectiveRightsAttribute);
        (this.getServerID = new BooleanArgument(null, "getServerID", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_GET_SERVER_ID.get())).addLongIdentifier("get-server-id", true);
        this.getServerID.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getServerID);
        (this.getUserResourceLimits = new BooleanArgument(null, "getUserResourceLimits", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_GET_USER_RESOURCE_LIMITS.get())).addLongIdentifier("get-user-resource-limits", true);
        this.getUserResourceLimits.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getUserResourceLimits);
        (this.includeReplicationConflictEntries = new BooleanArgument(null, "includeReplicationConflictEntries", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_INCLUDE_REPL_CONFLICTS.get())).addLongIdentifier("include-replication-conflict-entries", true);
        this.includeReplicationConflictEntries.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.includeReplicationConflictEntries);
        final Set<String> softDeleteAllowedValues = StaticUtils.setOf("with-non-deleted-entries", "without-non-deleted-entries", "deleted-entries-in-undeleted-form");
        (this.includeSoftDeletedEntries = new StringArgument(null, "includeSoftDeletedEntries", false, 1, "{with-non-deleted-entries|without-non-deleted-entries|deleted-entries-in-undeleted-form}", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_INCLUDE_SOFT_DELETED.get(), softDeleteAllowedValues)).addLongIdentifier("include-soft-deleted-entries", true);
        this.includeSoftDeletedEntries.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.includeSoftDeletedEntries);
        (this.includeSubentries = new BooleanArgument(null, "includeSubentries", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_INCLUDE_SUBENTRIES.get())).addLongIdentifier("includeLDAPSubentries", true);
        this.includeSubentries.addLongIdentifier("include-subentries", true);
        this.includeSubentries.addLongIdentifier("include-ldap-subentries", true);
        this.includeSubentries.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.includeSubentries);
        (this.joinRule = new StringArgument(null, "joinRule", false, 1, "{dn:sourceAttr|reverse-dn:targetAttr|equals:sourceAttr:targetAttr|contains:sourceAttr:targetAttr }", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_RULE.get())).addLongIdentifier("join-rule", true);
        this.joinRule.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinRule);
        (this.joinBaseDN = new StringArgument(null, "joinBaseDN", false, 1, "{search-base|source-entry-dn|{dn}}", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_BASE_DN.get())).addLongIdentifier("join-base-dn", true);
        this.joinBaseDN.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinBaseDN);
        (this.joinScope = new ScopeArgument(null, "joinScope", false, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_SCOPE.get())).addLongIdentifier("join-scope", true);
        this.joinScope.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinScope);
        (this.joinSizeLimit = new IntegerArgument(null, "joinSizeLimit", false, 1, ToolMessages.INFO_PLACEHOLDER_NUM.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_SIZE_LIMIT.get(), 0, Integer.MAX_VALUE)).addLongIdentifier("join-size-limit", true);
        this.joinSizeLimit.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinSizeLimit);
        (this.joinFilter = new FilterArgument(null, "joinFilter", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_FILTER.get())).addLongIdentifier("join-filter", true);
        this.joinFilter.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinFilter);
        (this.joinRequestedAttribute = new StringArgument(null, "joinRequestedAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_ATTR.get())).addLongIdentifier("join-requested-attribute", true);
        this.joinRequestedAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinRequestedAttribute);
        (this.joinRequireMatch = new BooleanArgument(null, "joinRequireMatch", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_JOIN_REQUIRE_MATCH.get())).addLongIdentifier("join-require-match", true);
        this.joinRequireMatch.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.joinRequireMatch);
        (this.manageDsaIT = new BooleanArgument(null, "manageDsaIT", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_MANAGE_DSA_IT.get())).addLongIdentifier("manage-dsa-it", true);
        this.manageDsaIT.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.manageDsaIT);
        (this.matchedValuesFilter = new FilterArgument(null, "matchedValuesFilter", false, 0, ToolMessages.INFO_PLACEHOLDER_FILTER.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_MATCHED_VALUES_FILTER.get())).addLongIdentifier("matched-values-filter", true);
        this.matchedValuesFilter.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.matchedValuesFilter);
        (this.matchingEntryCountControl = new StringArgument(null, "matchingEntryCountControl", false, 1, "{examineCount=NNN[:alwaysExamine][:allowUnindexed][:skipResolvingExplodedIndexes][:fastShortCircuitThreshold=NNN][:slowShortCircuitThreshold=NNN][:debug]}", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_MATCHING_ENTRY_COUNT_CONTROL.get())).addLongIdentifier("matchingEntryCount", true);
        this.matchingEntryCountControl.addLongIdentifier("matching-entry-count-control", true);
        this.matchingEntryCountControl.addLongIdentifier("matching-entry-count", true);
        this.matchingEntryCountControl.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.matchingEntryCountControl);
        (this.operationPurpose = new StringArgument(null, "operationPurpose", false, 1, ToolMessages.INFO_PLACEHOLDER_PURPOSE.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_OPERATION_PURPOSE.get())).addLongIdentifier("operation-purpose", true);
        this.operationPurpose.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.operationPurpose);
        (this.overrideSearchLimit = new StringArgument(null, "overrideSearchLimit", false, 0, ToolMessages.INFO_LDAPSEARCH_NAME_VALUE_PLACEHOLDER.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_OVERRIDE_SEARCH_LIMIT.get())).addLongIdentifier("overrideSearchLimits", true);
        this.overrideSearchLimit.addLongIdentifier("override-search-limit", true);
        this.overrideSearchLimit.addLongIdentifier("override-search-limits", true);
        this.overrideSearchLimit.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.overrideSearchLimit);
        (this.persistentSearch = new StringArgument('C', "persistentSearch", false, 1, "ps[:changetype[:changesonly[:entrychgcontrols]]]", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_PERSISTENT_SEARCH.get())).addLongIdentifier("persistent-search", true);
        this.persistentSearch.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.persistentSearch);
        (this.permitUnindexedSearch = new BooleanArgument(null, "permitUnindexedSearch", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_PERMIT_UNINDEXED_SEARCH.get())).addLongIdentifier("permitUnindexedSearches", true);
        this.permitUnindexedSearch.addLongIdentifier("permitUnindexed", true);
        this.permitUnindexedSearch.addLongIdentifier("permitIfUnindexed", true);
        this.permitUnindexedSearch.addLongIdentifier("permit-unindexed-search", true);
        this.permitUnindexedSearch.addLongIdentifier("permit-unindexed-searches", true);
        this.permitUnindexedSearch.addLongIdentifier("permit-unindexed", true);
        this.permitUnindexedSearch.addLongIdentifier("permit-if-unindexed", true);
        this.permitUnindexedSearch.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.permitUnindexedSearch);
        (this.proxyAs = new StringArgument('Y', "proxyAs", false, 1, ToolMessages.INFO_PLACEHOLDER_AUTHZID.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_PROXY_AS.get())).addLongIdentifier("proxy-as", true);
        this.proxyAs.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.proxyAs);
        (this.proxyV1As = new DNArgument(null, "proxyV1As", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_PROXY_V1_AS.get())).addLongIdentifier("proxy-v1-as", true);
        this.proxyV1As.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.proxyV1As);
        (this.rejectUnindexedSearch = new BooleanArgument(null, "rejectUnindexedSearch", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_REJECT_UNINDEXED_SEARCH.get())).addLongIdentifier("rejectUnindexedSearches", true);
        this.rejectUnindexedSearch.addLongIdentifier("rejectUnindexed", true);
        this.rejectUnindexedSearch.addLongIdentifier("rejectIfUnindexed", true);
        this.rejectUnindexedSearch.addLongIdentifier("reject-unindexed-search", true);
        this.rejectUnindexedSearch.addLongIdentifier("reject-unindexed-searches", true);
        this.rejectUnindexedSearch.addLongIdentifier("reject-unindexed", true);
        this.rejectUnindexedSearch.addLongIdentifier("reject-if-unindexed", true);
        this.rejectUnindexedSearch.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.rejectUnindexedSearch);
        (this.routeToBackendSet = new StringArgument(null, "routeToBackendSet", false, 0, ToolMessages.INFO_LDAPSEARCH_ARG_PLACEHOLDER_ROUTE_TO_BACKEND_SET.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_ROUTE_TO_BACKEND_SET.get())).addLongIdentifier("route-to-backend-set", true);
        this.routeToBackendSet.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.routeToBackendSet);
        (this.routeToServer = new StringArgument(null, "routeToServer", false, 1, ToolMessages.INFO_LDAPSEARCH_ARG_PLACEHOLDER_ROUTE_TO_SERVER.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_ROUTE_TO_SERVER.get())).addLongIdentifier("route-to-server", true);
        this.routeToServer.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.routeToServer);
        final Set<String> suppressOperationalAttributeUpdatesAllowedValues = StaticUtils.setOf("last-access-time", "last-login-time", "last-login-ip", "lastmod");
        (this.suppressOperationalAttributeUpdates = new StringArgument(null, "suppressOperationalAttributeUpdates", false, -1, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SUPPRESS_OP_ATTR_UPDATES.get(), suppressOperationalAttributeUpdatesAllowedValues)).addLongIdentifier("suppress-operational-attribute-updates", true);
        this.suppressOperationalAttributeUpdates.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.suppressOperationalAttributeUpdates);
        (this.usePasswordPolicyControl = new BooleanArgument(null, "usePasswordPolicyControl", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_PASSWORD_POLICY.get())).addLongIdentifier("use-password-policy-control", true);
        this.usePasswordPolicyControl.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.usePasswordPolicyControl);
        (this.realAttributesOnly = new BooleanArgument(null, "realAttributesOnly", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_REAL_ATTRS_ONLY.get())).addLongIdentifier("real-attributes-only", true);
        this.realAttributesOnly.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.realAttributesOnly);
        (this.sortOrder = new StringArgument('S', "sortOrder", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SORT_ORDER.get())).addLongIdentifier("sort-order", true);
        this.sortOrder.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.sortOrder);
        (this.simplePageSize = new IntegerArgument(null, "simplePageSize", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_PAGE_SIZE.get(), 1, Integer.MAX_VALUE)).addLongIdentifier("simple-page-size", true);
        this.simplePageSize.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.simplePageSize);
        (this.virtualAttributesOnly = new BooleanArgument(null, "virtualAttributesOnly", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_VIRTUAL_ATTRS_ONLY.get())).addLongIdentifier("virtual-attributes-only", true);
        this.virtualAttributesOnly.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.virtualAttributesOnly);
        (this.virtualListView = new StringArgument('G', "virtualListView", false, 1, "{before:after:index:count | before:after:value}", ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_VLV.get("sortOrder"))).addLongIdentifier("vlv", true);
        this.virtualListView.addLongIdentifier("virtual-list-view", true);
        this.virtualListView.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.virtualListView);
        (this.excludeAttribute = new StringArgument(null, "excludeAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_EXCLUDE_ATTRIBUTE.get())).addLongIdentifier("exclude-attribute", true);
        this.excludeAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.excludeAttribute);
        (this.redactAttribute = new StringArgument(null, "redactAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_REDACT_ATTRIBUTE.get())).addLongIdentifier("redact-attribute", true);
        this.redactAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.redactAttribute);
        (this.hideRedactedValueCount = new BooleanArgument(null, "hideRedactedValueCount", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_HIDE_REDACTED_VALUE_COUNT.get())).addLongIdentifier("hide-redacted-value-count", true);
        this.hideRedactedValueCount.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.hideRedactedValueCount);
        (this.scrambleAttribute = new StringArgument(null, "scrambleAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SCRAMBLE_ATTRIBUTE.get())).addLongIdentifier("scramble-attribute", true);
        this.scrambleAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.scrambleAttribute);
        (this.scrambleJSONField = new StringArgument(null, "scrambleJSONField", false, 0, ToolMessages.INFO_PLACEHOLDER_FIELD_NAME.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SCRAMBLE_JSON_FIELD.get())).addLongIdentifier("scramble-json-field", true);
        this.scrambleJSONField.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.scrambleJSONField);
        (this.scrambleRandomSeed = new IntegerArgument(null, "scrambleRandomSeed", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SCRAMBLE_RANDOM_SEED.get())).addLongIdentifier("scramble-random-seed", true);
        this.scrambleRandomSeed.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.scrambleRandomSeed);
        (this.renameAttributeFrom = new StringArgument(null, "renameAttributeFrom", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_RENAME_ATTRIBUTE_FROM.get())).addLongIdentifier("rename-attribute-from", true);
        this.renameAttributeFrom.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.renameAttributeFrom);
        (this.renameAttributeTo = new StringArgument(null, "renameAttributeTo", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_RENAME_ATTRIBUTE_TO.get())).addLongIdentifier("rename-attribute-to", true);
        this.renameAttributeTo.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.renameAttributeTo);
        (this.moveSubtreeFrom = new DNArgument(null, "moveSubtreeFrom", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_MOVE_SUBTREE_FROM.get())).addLongIdentifier("move-subtree-from", true);
        this.moveSubtreeFrom.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.moveSubtreeFrom);
        (this.moveSubtreeTo = new DNArgument(null, "moveSubtreeTo", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_MOVE_SUBTREE_TO.get())).addLongIdentifier("move-subtree-to", true);
        this.moveSubtreeTo.setArgumentGroupName(ToolMessages.INFO_LDAPSEARCH_ARG_GROUP_TRANSFORMATIONS.get());
        parser.addArgument(this.moveSubtreeTo);
        final BooleanArgument scriptFriendly = new BooleanArgument(null, "scriptFriendly", 1, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_SCRIPT_FRIENDLY.get());
        scriptFriendly.addLongIdentifier("script-friendly", true);
        scriptFriendly.setHidden(true);
        parser.addArgument(scriptFriendly);
        final IntegerArgument ldapVersion = new IntegerArgument('V', "ldapVersion", false, 1, null, ToolMessages.INFO_LDAPSEARCH_ARG_DESCRIPTION_LDAP_VERSION.get());
        ldapVersion.addLongIdentifier("ldap-version", true);
        ldapVersion.setHidden(true);
        parser.addArgument(ldapVersion);
        parser.addExclusiveArgumentSet(this.baseDN, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.scope, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.requestedAttribute, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.filter, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.filterFile, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.followReferrals, this.manageDsaIT, new Argument[0]);
        parser.addExclusiveArgumentSet(this.persistentSearch, this.filterFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.persistentSearch, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.realAttributesOnly, this.virtualAttributesOnly, new Argument[0]);
        parser.addExclusiveArgumentSet(this.simplePageSize, this.virtualListView, new Argument[0]);
        parser.addExclusiveArgumentSet(this.terse, this.verbose, new Argument[0]);
        parser.addDependentArgumentSet(this.getEffectiveRightsAttribute, this.getEffectiveRightsAuthzID, new Argument[0]);
        parser.addDependentArgumentSet(this.virtualListView, this.sortOrder, new Argument[0]);
        parser.addExclusiveArgumentSet(this.rejectUnindexedSearch, this.permitUnindexedSearch, new Argument[0]);
        parser.addDependentArgumentSet(this.separateOutputFilePerSearch, this.outputFile, new Argument[0]);
        parser.addDependentArgumentSet(this.separateOutputFilePerSearch, this.filter, this.filterFile, this.ldapURLFile);
        parser.addDependentArgumentSet(this.teeResultsToStandardOut, this.outputFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.wrapColumn, this.dontWrap, new Argument[0]);
        parser.addDependentArgumentSet(this.joinBaseDN, this.joinRule, new Argument[0]);
        parser.addDependentArgumentSet(this.joinScope, this.joinRule, new Argument[0]);
        parser.addDependentArgumentSet(this.joinSizeLimit, this.joinRule, new Argument[0]);
        parser.addDependentArgumentSet(this.joinFilter, this.joinRule, new Argument[0]);
        parser.addDependentArgumentSet(this.joinRequestedAttribute, this.joinRule, new Argument[0]);
        parser.addDependentArgumentSet(this.joinRequireMatch, this.joinRule, new Argument[0]);
        parser.addExclusiveArgumentSet(this.countEntries, this.filter, new Argument[0]);
        parser.addExclusiveArgumentSet(this.countEntries, this.filterFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.countEntries, this.ldapURLFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.countEntries, this.persistentSearch, new Argument[0]);
        parser.addDependentArgumentSet(this.hideRedactedValueCount, this.redactAttribute, new Argument[0]);
        parser.addDependentArgumentSet(this.scrambleJSONField, this.scrambleAttribute, new Argument[0]);
        parser.addDependentArgumentSet(this.scrambleRandomSeed, this.scrambleAttribute, new Argument[0]);
        parser.addDependentArgumentSet(this.renameAttributeFrom, this.renameAttributeTo, new Argument[0]);
        parser.addDependentArgumentSet(this.renameAttributeTo, this.renameAttributeFrom, new Argument[0]);
        parser.addDependentArgumentSet(this.moveSubtreeFrom, this.moveSubtreeTo, new Argument[0]);
        parser.addDependentArgumentSet(this.moveSubtreeTo, this.moveSubtreeFrom, new Argument[0]);
        parser.addDependentArgumentSet(this.compressOutput, this.outputFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.compressOutput, this.teeResultsToStandardOut, new Argument[0]);
        parser.addDependentArgumentSet(this.encryptOutput, this.outputFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.encryptOutput, this.teeResultsToStandardOut, new Argument[0]);
        parser.addDependentArgumentSet(this.encryptionPassphraseFile, this.encryptOutput, new Argument[0]);
    }
    
    @Override
    protected List<Control> getBindControls() {
        final ArrayList<Control> bindControls = new ArrayList<Control>(10);
        if (this.bindControl.isPresent()) {
            bindControls.addAll(this.bindControl.getValues());
        }
        if (this.authorizationIdentity.isPresent()) {
            bindControls.add(new AuthorizationIdentityRequestControl(false));
        }
        if (this.getAuthorizationEntryAttribute.isPresent()) {
            bindControls.add(new GetAuthorizationEntryRequestControl(true, true, this.getAuthorizationEntryAttribute.getValues()));
        }
        if (this.getUserResourceLimits.isPresent()) {
            bindControls.add(new GetUserResourceLimitsRequestControl());
        }
        if (this.usePasswordPolicyControl.isPresent()) {
            bindControls.add(new PasswordPolicyRequestControl());
        }
        if (this.suppressOperationalAttributeUpdates.isPresent()) {
            final EnumSet<SuppressType> suppressTypes = EnumSet.noneOf(SuppressType.class);
            for (final String s : this.suppressOperationalAttributeUpdates.getValues()) {
                if (s.equalsIgnoreCase("last-access-time")) {
                    suppressTypes.add(SuppressType.LAST_ACCESS_TIME);
                }
                else if (s.equalsIgnoreCase("last-login-time")) {
                    suppressTypes.add(SuppressType.LAST_LOGIN_TIME);
                }
                else {
                    if (!s.equalsIgnoreCase("last-login-ip")) {
                        continue;
                    }
                    suppressTypes.add(SuppressType.LAST_LOGIN_IP);
                }
            }
            bindControls.add(new SuppressOperationalAttributeUpdateRequestControl(suppressTypes));
        }
        return bindControls;
    }
    
    @Override
    protected boolean supportsMultipleServers() {
        return true;
    }
    
    @Override
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
        if (this.wrapColumn.isPresent()) {
            final int wc = this.wrapColumn.getValue();
            if (wc <= 0) {
                LDAPSearch.WRAP_COLUMN = Integer.MAX_VALUE;
            }
            else {
                LDAPSearch.WRAP_COLUMN = wc;
            }
        }
        else if (this.dontWrap.isPresent()) {
            LDAPSearch.WRAP_COLUMN = Integer.MAX_VALUE;
        }
        final List<String> trailingArgs = this.parser.getTrailingArguments();
        if (this.ldapURLFile.isPresent() && !trailingArgs.isEmpty()) {
            throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_TRAILING_ARGS_WITH_URL_FILE.get(this.ldapURLFile.getIdentifierString()));
        }
        if ((this.filter.isPresent() || this.filterFile.isPresent()) && !trailingArgs.isEmpty()) {
            try {
                Filter.create(trailingArgs.get(0));
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_TRAILING_FILTER_WITH_FILTER_FILE.get(this.filterFile.getIdentifierString()));
            }
            catch (final LDAPException ex) {}
        }
        if (!this.ldapURLFile.isPresent() && !this.filter.isPresent() && !this.filterFile.isPresent()) {
            if (trailingArgs.isEmpty()) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_NO_TRAILING_ARGS.get(this.filterFile.getIdentifierString(), this.ldapURLFile.getIdentifierString()));
            }
            try {
                Filter.create(trailingArgs.get(0));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_FIRST_TRAILING_ARG_NOT_FILTER.get(trailingArgs.get(0)), e);
            }
        }
        for (final String s : trailingArgs) {
            if (s.startsWith("-")) {
                this.commentToErr(ToolMessages.WARN_LDAPSEARCH_TRAILING_ARG_STARTS_WITH_DASH.get(s));
                break;
            }
        }
        if (this.matchedValuesFilter.isPresent()) {
            final List<Filter> filterList = this.matchedValuesFilter.getValues();
            final MatchedValuesFilter[] matchedValuesFilters = new MatchedValuesFilter[filterList.size()];
            for (int i = 0; i < matchedValuesFilters.length; ++i) {
                try {
                    matchedValuesFilters[i] = MatchedValuesFilter.create(filterList.get(i));
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_INVALID_MATCHED_VALUES_FILTER.get(filterList.get(i).toString()), e2);
                }
            }
            this.matchedValuesRequestControl = new MatchedValuesRequestControl(true, matchedValuesFilters);
        }
        if (this.matchingEntryCountControl.isPresent()) {
            boolean allowUnindexed = false;
            boolean alwaysExamine = false;
            boolean debug = false;
            boolean skipResolvingExplodedIndexes = false;
            Integer examineCount = null;
            Long fastShortCircuitThreshold = null;
            Long slowShortCircuitThreshold = null;
            try {
                for (final String element : this.matchingEntryCountControl.getValue().toLowerCase().split(":")) {
                    if (element.startsWith("examinecount=")) {
                        examineCount = Integer.parseInt(element.substring(13));
                    }
                    else if (element.equals("allowunindexed")) {
                        allowUnindexed = true;
                    }
                    else if (element.equals("alwaysexamine")) {
                        alwaysExamine = true;
                    }
                    else if (element.equals("skipresolvingexplodedindexes")) {
                        skipResolvingExplodedIndexes = true;
                    }
                    else if (element.startsWith("fastshortcircuitthreshold=")) {
                        fastShortCircuitThreshold = Long.parseLong(element.substring(26));
                    }
                    else if (element.startsWith("slowshortcircuitthreshold=")) {
                        slowShortCircuitThreshold = Long.parseLong(element.substring(26));
                    }
                    else {
                        if (!element.equals("debug")) {
                            throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_MATCHING_ENTRY_COUNT_INVALID_VALUE.get(this.matchingEntryCountControl.getIdentifierString()));
                        }
                        debug = true;
                    }
                }
            }
            catch (final ArgumentException ae) {
                Debug.debugException(ae);
                throw ae;
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_MATCHING_ENTRY_COUNT_INVALID_VALUE.get(this.matchingEntryCountControl.getIdentifierString()), e3);
            }
            if (examineCount == null) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_MATCHING_ENTRY_COUNT_INVALID_VALUE.get(this.matchingEntryCountControl.getIdentifierString()));
            }
            this.matchingEntryCountRequestControl = new MatchingEntryCountRequestControl(true, examineCount, alwaysExamine, allowUnindexed, skipResolvingExplodedIndexes, fastShortCircuitThreshold, slowShortCircuitThreshold, debug);
        }
        if (this.overrideSearchLimit.isPresent()) {
            final LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
            for (final String value : this.overrideSearchLimit.getValues()) {
                final int equalPos = value.indexOf(61);
                if (equalPos < 0) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OVERRIDE_LIMIT_NO_EQUAL.get(this.overrideSearchLimit.getIdentifierString()));
                }
                if (equalPos == 0) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OVERRIDE_LIMIT_EMPTY_PROPERTY_NAME.get(this.overrideSearchLimit.getIdentifierString()));
                }
                final String propertyName = value.substring(0, equalPos);
                if (properties.containsKey(propertyName)) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OVERRIDE_LIMIT_DUPLICATE_PROPERTY_NAME.get(this.overrideSearchLimit.getIdentifierString(), propertyName));
                }
                if (equalPos == value.length() - 1) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OVERRIDE_LIMIT_EMPTY_PROPERTY_VALUE.get(this.overrideSearchLimit.getIdentifierString(), propertyName));
                }
                properties.put(propertyName, value.substring(equalPos + 1));
            }
            this.overrideSearchLimitsRequestControl = new OverrideSearchLimitsRequestControl(properties, false);
        }
        if (this.persistentSearch.isPresent()) {
            boolean changesOnly = true;
            boolean returnECs = true;
            EnumSet<PersistentSearchChangeType> changeTypes = EnumSet.allOf(PersistentSearchChangeType.class);
            try {
                final String[] elements = this.persistentSearch.getValue().toLowerCase().split(":");
                if (elements.length == 0) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_PERSISTENT_SEARCH_INVALID_VALUE.get(this.persistentSearch.getIdentifierString()));
                }
                final String header = StaticUtils.toLowerCase(elements[0]);
                if (!header.equals("ps") && !header.equals("persist") && !header.equals("persistent") && !header.equals("psearch") && !header.equals("persistentsearch")) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_PERSISTENT_SEARCH_INVALID_VALUE.get(this.persistentSearch.getIdentifierString()));
                }
                if (elements.length > 1) {
                    final String ctString = StaticUtils.toLowerCase(elements[1]);
                    if (ctString.equals("any")) {
                        changeTypes = EnumSet.allOf(PersistentSearchChangeType.class);
                    }
                    else {
                        changeTypes.clear();
                        for (final String t : ctString.split(",")) {
                            if (t.equals("add")) {
                                changeTypes.add(PersistentSearchChangeType.ADD);
                            }
                            else if (t.equals("del") || t.equals("delete")) {
                                changeTypes.add(PersistentSearchChangeType.DELETE);
                            }
                            else if (t.equals("mod") || t.equals("modify")) {
                                changeTypes.add(PersistentSearchChangeType.MODIFY);
                            }
                            else {
                                if (!t.equals("moddn") && !t.equals("modrdn") && !t.equals("modifydn") && !t.equals("modifyrdn")) {
                                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_PERSISTENT_SEARCH_INVALID_VALUE.get(this.persistentSearch.getIdentifierString()));
                                }
                                changeTypes.add(PersistentSearchChangeType.MODIFY_DN);
                            }
                        }
                    }
                }
                if (elements.length > 2) {
                    if (elements[2].equalsIgnoreCase("true") || elements[2].equals("1")) {
                        changesOnly = true;
                    }
                    else {
                        if (!elements[2].equalsIgnoreCase("false") && !elements[2].equals("0")) {
                            throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_PERSISTENT_SEARCH_INVALID_VALUE.get(this.persistentSearch.getIdentifierString()));
                        }
                        changesOnly = false;
                    }
                }
                if (elements.length > 3) {
                    if (elements[3].equalsIgnoreCase("true") || elements[3].equals("1")) {
                        returnECs = true;
                    }
                    else {
                        if (!elements[3].equalsIgnoreCase("false") && !elements[3].equals("0")) {
                            throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_PERSISTENT_SEARCH_INVALID_VALUE.get(this.persistentSearch.getIdentifierString()));
                        }
                        returnECs = false;
                    }
                }
            }
            catch (final ArgumentException ae2) {
                Debug.debugException(ae2);
                throw ae2;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_PERSISTENT_SEARCH_INVALID_VALUE.get(this.persistentSearch.getIdentifierString()), e2);
            }
            this.persistentSearchRequestControl = new PersistentSearchRequestControl(changeTypes, changesOnly, returnECs, true);
        }
        if (this.sortOrder.isPresent()) {
            final ArrayList<SortKey> sortKeyList = new ArrayList<SortKey>(5);
            final StringTokenizer tokenizer = new StringTokenizer(this.sortOrder.getValue(), ", ");
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                boolean ascending;
                String attributeName;
                if (token.startsWith("-")) {
                    ascending = false;
                    attributeName = token.substring(1);
                }
                else if (token.startsWith("+")) {
                    ascending = true;
                    attributeName = token.substring(1);
                }
                else {
                    ascending = true;
                    attributeName = token;
                }
                final int colonPos = attributeName.indexOf(58);
                String matchingRuleID;
                if (colonPos >= 0) {
                    matchingRuleID = attributeName.substring(colonPos + 1);
                    attributeName = attributeName.substring(0, colonPos);
                }
                else {
                    matchingRuleID = null;
                }
                final StringBuilder invalidReason = new StringBuilder();
                if (!PersistUtils.isValidLDAPName(attributeName, false, invalidReason)) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_SORT_ORDER_INVALID_VALUE.get(this.sortOrder.getIdentifierString()));
                }
                sortKeyList.add(new SortKey(attributeName, matchingRuleID, !ascending));
            }
            if (sortKeyList.isEmpty()) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_SORT_ORDER_INVALID_VALUE.get(this.sortOrder.getIdentifierString()));
            }
            final SortKey[] sortKeyArray = new SortKey[sortKeyList.size()];
            sortKeyList.toArray(sortKeyArray);
            this.sortRequestControl = new ServerSideSortRequestControl(sortKeyArray);
        }
        if (this.virtualListView.isPresent()) {
            try {
                final String[] elements2 = this.virtualListView.getValue().split(":");
                if (elements2.length == 4) {
                    this.vlvRequestControl = new VirtualListViewRequestControl(Integer.parseInt(elements2[2]), Integer.parseInt(elements2[0]), Integer.parseInt(elements2[1]), Integer.parseInt(elements2[3]), null);
                }
                else {
                    if (elements2.length != 3) {
                        throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_VLV_INVALID_VALUE.get(this.virtualListView.getIdentifierString()));
                    }
                    this.vlvRequestControl = new VirtualListViewRequestControl(elements2[2], Integer.parseInt(elements2[0]), Integer.parseInt(elements2[1]), null);
                }
            }
            catch (final ArgumentException ae3) {
                Debug.debugException(ae3);
                throw ae3;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_VLV_INVALID_VALUE.get(this.virtualListView.getIdentifierString()), e);
            }
        }
        if (this.joinRule.isPresent()) {
            JoinRule rule;
            try {
                final String[] elements3 = this.joinRule.getValue().toLowerCase().split(":");
                final String ruleName = StaticUtils.toLowerCase(elements3[0]);
                if (ruleName.equals("dn")) {
                    rule = JoinRule.createDNJoin(elements3[1]);
                }
                else if (ruleName.equals("reverse-dn") || ruleName.equals("reversedn")) {
                    rule = JoinRule.createReverseDNJoin(elements3[1]);
                }
                else if (ruleName.equals("equals") || ruleName.equals("equality")) {
                    rule = JoinRule.createEqualityJoin(elements3[1], elements3[2], false);
                }
                else {
                    if (!ruleName.equals("contains") && !ruleName.equals("substring")) {
                        throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_JOIN_RULE_INVALID_VALUE.get(this.joinRule.getIdentifierString()));
                    }
                    rule = JoinRule.createContainsJoin(elements3[1], elements3[2], false);
                }
            }
            catch (final ArgumentException ae4) {
                Debug.debugException(ae4);
                throw ae4;
            }
            catch (final Exception e4) {
                Debug.debugException(e4);
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_JOIN_RULE_INVALID_VALUE.get(this.joinRule.getIdentifierString()), e4);
            }
            JoinBaseDN joinBase;
            if (this.joinBaseDN.isPresent()) {
                final String s2 = StaticUtils.toLowerCase(this.joinBaseDN.getValue());
                if (s2.equals("search-base") || s2.equals("search-base-dn")) {
                    joinBase = JoinBaseDN.createUseSearchBaseDN();
                }
                else if (s2.equals("source-entry-dn") || s2.equals("source-dn")) {
                    joinBase = JoinBaseDN.createUseSourceEntryDN();
                }
                else {
                    try {
                        final DN dn = new DN(this.joinBaseDN.getValue());
                        joinBase = JoinBaseDN.createUseCustomBaseDN(this.joinBaseDN.getValue());
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_JOIN_BASE_DN_INVALID_VALUE.get(this.joinBaseDN.getIdentifierString()), e2);
                    }
                }
            }
            else {
                joinBase = JoinBaseDN.createUseSearchBaseDN();
            }
            String[] joinAttrs;
            if (this.joinRequestedAttribute.isPresent()) {
                final List<String> valueList = this.joinRequestedAttribute.getValues();
                joinAttrs = new String[valueList.size()];
                valueList.toArray(joinAttrs);
            }
            else {
                joinAttrs = null;
            }
            this.joinRequestControl = new JoinRequestControl(new JoinRequestValue(rule, joinBase, this.joinScope.getValue(), DereferencePolicy.NEVER, this.joinSizeLimit.getValue(), this.joinFilter.getValue(), joinAttrs, this.joinRequireMatch.isPresent(), null));
        }
        if (this.routeToBackendSet.isPresent()) {
            final List<String> values = this.routeToBackendSet.getValues();
            final Map<String, List<String>> idsByRP = new LinkedHashMap<String, List<String>>(StaticUtils.computeMapCapacity(values.size()));
            for (final String value2 : values) {
                final int colonPos2 = value2.indexOf(58);
                if (colonPos2 <= 0) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_ROUTE_TO_BACKEND_SET_INVALID_FORMAT.get(value2, this.routeToBackendSet.getIdentifierString()));
                }
                final String rpID = value2.substring(0, colonPos2);
                final String bsID = value2.substring(colonPos2 + 1);
                List<String> idsForRP = idsByRP.get(rpID);
                if (idsForRP == null) {
                    idsForRP = new ArrayList<String>(values.size());
                    idsByRP.put(rpID, idsForRP);
                }
                idsForRP.add(bsID);
            }
            for (final Map.Entry<String, List<String>> e5 : idsByRP.entrySet()) {
                final String rpID2 = e5.getKey();
                final List<String> bsIDs = e5.getValue();
                this.routeToBackendSetRequestControls.add(RouteToBackendSetRequestControl.createAbsoluteRoutingRequest(true, rpID2, bsIDs));
            }
        }
        final String derefStr = StaticUtils.toLowerCase(this.dereferencePolicy.getValue());
        if (derefStr.equals("always")) {
            this.derefPolicy = DereferencePolicy.ALWAYS;
        }
        else if (derefStr.equals("search")) {
            this.derefPolicy = DereferencePolicy.SEARCHING;
        }
        else if (derefStr.equals("find")) {
            this.derefPolicy = DereferencePolicy.FINDING;
        }
        else {
            this.derefPolicy = DereferencePolicy.NEVER;
        }
        final ArrayList<EntryTransformation> transformations = new ArrayList<EntryTransformation>(5);
        if (this.excludeAttribute.isPresent()) {
            transformations.add(new ExcludeAttributeTransformation(null, this.excludeAttribute.getValues()));
        }
        if (this.redactAttribute.isPresent()) {
            transformations.add(new RedactAttributeTransformation(null, true, !this.hideRedactedValueCount.isPresent(), this.redactAttribute.getValues()));
        }
        if (this.scrambleAttribute.isPresent()) {
            Long randomSeed;
            if (this.scrambleRandomSeed.isPresent()) {
                randomSeed = (long)this.scrambleRandomSeed.getValue();
            }
            else {
                randomSeed = null;
            }
            transformations.add(new ScrambleAttributeTransformation(null, randomSeed, true, this.scrambleAttribute.getValues(), this.scrambleJSONField.getValues()));
        }
        if (this.renameAttributeFrom.isPresent()) {
            if (this.renameAttributeFrom.getNumOccurrences() != this.renameAttributeTo.getNumOccurrences()) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_RENAME_ATTRIBUTE_MISMATCH.get());
            }
            final Iterator<String> sourceIterator = this.renameAttributeFrom.getValues().iterator();
            final Iterator<String> targetIterator = this.renameAttributeTo.getValues().iterator();
            while (sourceIterator.hasNext()) {
                transformations.add(new RenameAttributeTransformation(null, sourceIterator.next(), targetIterator.next(), true));
            }
        }
        if (this.moveSubtreeFrom.isPresent()) {
            if (this.moveSubtreeFrom.getNumOccurrences() != this.moveSubtreeTo.getNumOccurrences()) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_MOVE_SUBTREE_MISMATCH.get());
            }
            final Iterator<DN> sourceIterator2 = this.moveSubtreeFrom.getValues().iterator();
            final Iterator<DN> targetIterator2 = this.moveSubtreeTo.getValues().iterator();
            while (sourceIterator2.hasNext()) {
                transformations.add(new MoveSubtreeTransformation(sourceIterator2.next(), targetIterator2.next()));
            }
        }
        if (!transformations.isEmpty()) {
            this.entryTransformations = transformations;
        }
        final String outputFormatStr = StaticUtils.toLowerCase(this.outputFormat.getValue());
        if (outputFormatStr.equals("json")) {
            this.outputHandler = new JSONLDAPSearchOutputHandler(this);
        }
        else if (outputFormatStr.equals("csv") || outputFormatStr.equals("tab-delimited")) {
            if (this.ldapURLFile.isPresent()) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OUTPUT_FORMAT_NOT_SUPPORTED_WITH_URLS.get(this.outputFormat.getValue(), this.ldapURLFile.getIdentifierString()));
            }
            final List<String> requestedAttributes = this.requestedAttribute.getValues();
            if (requestedAttributes == null || requestedAttributes.isEmpty()) {
                throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OUTPUT_FORMAT_REQUIRES_REQUESTED_ATTR_ARG.get(this.outputFormat.getValue(), this.requestedAttribute.getIdentifierString()));
            }
            switch (trailingArgs.size()) {
                case 0: {
                    break;
                }
                case 1: {
                    if (this.filter.isPresent() || this.filterFile.isPresent()) {
                        throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OUTPUT_FORMAT_REQUIRES_REQUESTED_ATTR_ARG.get(this.outputFormat.getValue(), this.requestedAttribute.getIdentifierString()));
                    }
                    break;
                }
                default: {
                    throw new ArgumentException(ToolMessages.ERR_LDAPSEARCH_OUTPUT_FORMAT_REQUIRES_REQUESTED_ATTR_ARG.get(this.outputFormat.getValue(), this.requestedAttribute.getIdentifierString()));
                }
            }
            this.outputHandler = new ColumnFormatterLDAPSearchOutputHandler(this, outputFormatStr.equals("csv") ? OutputFormat.CSV : OutputFormat.TAB_DELIMITED_TEXT, requestedAttributes, LDAPSearch.WRAP_COLUMN);
        }
        else {
            this.outputHandler = new LDIFLDAPSearchOutputHandler(this, LDAPSearch.WRAP_COLUMN);
        }
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setUseSynchronousMode(true);
        options.setFollowReferrals(this.followReferrals.isPresent());
        options.setUnsolicitedNotificationHandler(this);
        options.setResponseTimeoutMillis(0L);
        return options;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        Label_0115: {
            if (this.encryptOutput.isPresent()) {
                if (this.encryptionPassphraseFile.isPresent()) {
                    try {
                        this.encryptionPassphrase = ToolUtils.readEncryptionPassphraseFromFile(this.encryptionPassphraseFile.getValue());
                        break Label_0115;
                    }
                    catch (final LDAPException e) {
                        Debug.debugException(e);
                        this.wrapErr(0, LDAPSearch.WRAP_COLUMN, e.getMessage());
                        return e.getResultCode();
                    }
                }
                try {
                    this.encryptionPassphrase = ToolUtils.promptForEncryptionPassphrase(false, true, this.getOut(), this.getErr());
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    this.wrapErr(0, LDAPSearch.WRAP_COLUMN, e.getMessage());
                    return e.getResultCode();
                }
            }
        }
        if (this.outputFile.isPresent()) {
            if (!this.separateOutputFilePerSearch.isPresent()) {
                try {
                    OutputStream s = new FileOutputStream(this.outputFile.getValue());
                    if (this.encryptOutput.isPresent()) {
                        s = new PassphraseEncryptedOutputStream(this.encryptionPassphrase, s);
                    }
                    if (this.compressOutput.isPresent()) {
                        s = new GZIPOutputStream(s);
                    }
                    if (this.teeResultsToStandardOut.isPresent()) {
                        this.outStream = new PrintStream(new TeeOutputStream(new OutputStream[] { s, this.getOut() }));
                    }
                    else {
                        this.outStream = new PrintStream(s);
                    }
                    this.errStream = this.outStream;
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    this.wrapErr(0, LDAPSearch.WRAP_COLUMN, ToolMessages.ERR_LDAPSEARCH_CANNOT_OPEN_OUTPUT_FILE.get(this.outputFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e2)));
                    return ResultCode.LOCAL_ERROR;
                }
                this.outputHandler.formatHeader();
            }
        }
        else {
            this.outputHandler.formatHeader();
        }
        final List<Control> searchControls = this.getSearchControls();
        final boolean originalCommentAboutBase64EncodedValues = LDIFWriter.commentAboutBase64EncodedValues();
        LDIFWriter.setCommentAboutBase64EncodedValues(!this.suppressBase64EncodedValueComments.isPresent());
        LDAPConnectionPool pool = null;
        try {
            if (!this.dryRun.isPresent()) {
                try {
                    StartAdministrativeSessionPostConnectProcessor p;
                    if (this.useAdministrativeSession.isPresent()) {
                        p = new StartAdministrativeSessionPostConnectProcessor(new StartAdministrativeSessionExtendedRequest(this.getToolName(), true, new Control[0]));
                    }
                    else {
                        p = null;
                    }
                    pool = this.getConnectionPool(1, 1, 0, p, null, true, new ReportBindResultLDAPConnectionPoolHealthCheck(this, true, false));
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    this.commentToErr(ToolMessages.ERR_LDAPSEARCH_CANNOT_CREATE_CONNECTION_POOL.get(StaticUtils.getExceptionMessage(le)));
                    return le.getResultCode();
                }
                if (this.retryFailedOperations.isPresent()) {
                    pool.setRetryFailedOperationsDueToInvalidConnections(true);
                }
            }
            FixedRateBarrier rateLimiter;
            if (this.ratePerSecond.isPresent()) {
                rateLimiter = new FixedRateBarrier(1000L, this.ratePerSecond.getValue());
            }
            else {
                rateLimiter = null;
            }
            if (this.ldapURLFile.isPresent()) {
                return this.searchWithLDAPURLs(pool, rateLimiter, searchControls);
            }
            final ArrayList<String> attrList = new ArrayList<String>(10);
            if (this.requestedAttribute.isPresent()) {
                attrList.addAll(this.requestedAttribute.getValues());
            }
            final List<String> trailingArgs = this.parser.getTrailingArguments();
            if (!trailingArgs.isEmpty()) {
                final Iterator<String> trailingArgIterator = trailingArgs.iterator();
                if (!this.filter.isPresent() && !this.filterFile.isPresent()) {
                    trailingArgIterator.next();
                }
                while (trailingArgIterator.hasNext()) {
                    attrList.add(trailingArgIterator.next());
                }
            }
            final String[] attributes = new String[attrList.size()];
            attrList.toArray(attributes);
            ResultCode resultCode = ResultCode.SUCCESS;
            if (this.filter.isPresent() || this.filterFile.isPresent()) {
                if (this.filter.isPresent()) {
                    for (final Filter f : this.filter.getValues()) {
                        final ResultCode rc = this.searchWithFilter(pool, f, attributes, rateLimiter, searchControls);
                        if (rc != ResultCode.SUCCESS) {
                            if (resultCode == ResultCode.SUCCESS) {
                                resultCode = rc;
                            }
                            if (!this.continueOnError.isPresent()) {
                                return resultCode;
                            }
                            continue;
                        }
                    }
                }
                if (this.filterFile.isPresent()) {
                    final ResultCode rc2 = this.searchWithFilterFile(pool, attributes, rateLimiter, searchControls);
                    if (rc2 != ResultCode.SUCCESS) {
                        if (resultCode == ResultCode.SUCCESS) {
                            resultCode = rc2;
                        }
                        if (!this.continueOnError.isPresent()) {
                            return resultCode;
                        }
                    }
                }
            }
            else {
                Filter f2;
                try {
                    final String filterStr = this.parser.getTrailingArguments().iterator().next();
                    f2 = Filter.create(filterStr);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                    this.displayResult(le2.toLDAPResult());
                    return le2.getResultCode();
                }
                resultCode = this.searchWithFilter(pool, f2, attributes, rateLimiter, searchControls);
            }
            return resultCode;
        }
        finally {
            if (pool != null) {
                try {
                    pool.close();
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
            if (this.outStream != null) {
                try {
                    this.outStream.close();
                    this.outStream = null;
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
            if (this.errStream != null) {
                try {
                    this.errStream.close();
                    this.errStream = null;
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
            LDIFWriter.setCommentAboutBase64EncodedValues(originalCommentAboutBase64EncodedValues);
        }
    }
    
    private ResultCode searchWithLDAPURLs(final LDAPConnectionPool pool, final FixedRateBarrier rateLimiter, final List<Control> searchControls) {
        ResultCode resultCode = ResultCode.SUCCESS;
        for (final File f : this.ldapURLFile.getValues()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(f));
                while (true) {
                    final String line = reader.readLine();
                    if (line != null) {
                        if (line.length() == 0) {
                            continue;
                        }
                        if (line.startsWith("#")) {
                            continue;
                        }
                        LDAPURL url;
                        try {
                            url = new LDAPURL(line);
                        }
                        catch (final LDAPException le) {
                            Debug.debugException(le);
                            this.commentToErr(ToolMessages.ERR_LDAPSEARCH_MALFORMED_LDAP_URL.get(f.getAbsolutePath(), line));
                            if (resultCode == ResultCode.SUCCESS) {
                                resultCode = le.getResultCode();
                            }
                            if (this.continueOnError.isPresent()) {
                                continue;
                            }
                            return resultCode;
                        }
                        final SearchRequest searchRequest = new SearchRequest(new LDAPSearchListener(this.outputHandler, this.entryTransformations), url.getBaseDN().toString(), url.getScope(), this.derefPolicy, this.sizeLimit.getValue(), this.timeLimitSeconds.getValue(), this.typesOnly.isPresent(), url.getFilter(), url.getAttributes());
                        final ResultCode rc = this.doSearch(pool, searchRequest, rateLimiter, searchControls);
                        if (rc == ResultCode.SUCCESS) {
                            continue;
                        }
                        if (resultCode == ResultCode.SUCCESS) {
                            resultCode = rc;
                        }
                        if (!this.continueOnError.isPresent()) {
                            return resultCode;
                        }
                        continue;
                    }
                }
            }
            catch (final IOException ioe) {
                this.commentToErr(ToolMessages.ERR_LDAPSEARCH_CANNOT_READ_LDAP_URL_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(ioe)));
                return ResultCode.LOCAL_ERROR;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                }
            }
        }
        return resultCode;
    }
    
    private ResultCode searchWithFilterFile(final LDAPConnectionPool pool, final String[] attributes, final FixedRateBarrier rateLimiter, final List<Control> searchControls) {
        ResultCode resultCode = ResultCode.SUCCESS;
        for (final File f : this.filterFile.getValues()) {
            FilterFileReader reader = null;
            try {
                reader = new FilterFileReader(f);
                while (true) {
                    Filter searchFilter;
                    try {
                        searchFilter = reader.readFilter();
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        this.commentToErr(ToolMessages.ERR_LDAPSEARCH_MALFORMED_FILTER.get(f.getAbsolutePath(), le.getMessage()));
                        if (resultCode == ResultCode.SUCCESS) {
                            resultCode = le.getResultCode();
                        }
                        if (this.continueOnError.isPresent()) {
                            continue;
                        }
                        return resultCode;
                    }
                    if (searchFilter != null) {
                        final ResultCode rc = this.searchWithFilter(pool, searchFilter, attributes, rateLimiter, searchControls);
                        if (rc == ResultCode.SUCCESS) {
                            continue;
                        }
                        if (resultCode == ResultCode.SUCCESS) {
                            resultCode = rc;
                        }
                        if (!this.continueOnError.isPresent()) {
                            return resultCode;
                        }
                        continue;
                    }
                }
            }
            catch (final IOException ioe) {
                Debug.debugException(ioe);
                this.commentToErr(ToolMessages.ERR_LDAPSEARCH_CANNOT_READ_FILTER_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(ioe)));
                return ResultCode.LOCAL_ERROR;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                }
            }
        }
        return resultCode;
    }
    
    private ResultCode searchWithFilter(final LDAPConnectionPool pool, final Filter filter, final String[] attributes, final FixedRateBarrier rateLimiter, final List<Control> searchControls) {
        String baseDNString;
        if (this.baseDN.isPresent()) {
            baseDNString = this.baseDN.getStringValue();
        }
        else {
            baseDNString = "";
        }
        final SearchRequest searchRequest = new SearchRequest(new LDAPSearchListener(this.outputHandler, this.entryTransformations), baseDNString, this.scope.getValue(), this.derefPolicy, this.sizeLimit.getValue(), this.timeLimitSeconds.getValue(), this.typesOnly.isPresent(), filter, attributes);
        return this.doSearch(pool, searchRequest, rateLimiter, searchControls);
    }
    
    private ResultCode doSearch(final LDAPConnectionPool pool, final SearchRequest searchRequest, final FixedRateBarrier rateLimiter, final List<Control> searchControls) {
        if (this.separateOutputFilePerSearch.isPresent()) {
            try {
                final String path = this.outputFile.getValue().getAbsolutePath() + '.' + this.outputFileCounter.getAndIncrement();
                OutputStream s = new FileOutputStream(path);
                if (this.encryptOutput.isPresent()) {
                    s = new PassphraseEncryptedOutputStream(this.encryptionPassphrase, s);
                }
                if (this.compressOutput.isPresent()) {
                    s = new GZIPOutputStream(s);
                }
                if (this.teeResultsToStandardOut.isPresent()) {
                    this.outStream = new PrintStream(new TeeOutputStream(new OutputStream[] { s, this.getOut() }));
                }
                else {
                    this.outStream = new PrintStream(s);
                }
                this.errStream = this.outStream;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.wrapErr(0, LDAPSearch.WRAP_COLUMN, ToolMessages.ERR_LDAPSEARCH_CANNOT_OPEN_OUTPUT_FILE.get(this.outputFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
                return ResultCode.LOCAL_ERROR;
            }
            this.outputHandler.formatHeader();
        }
        try {
            if (rateLimiter != null) {
                rateLimiter.await();
            }
            ASN1OctetString pagedResultsCookie = null;
            boolean multiplePages = false;
            long totalEntries = 0L;
            long totalReferences = 0L;
            SearchResult searchResult;
            try {
                while (true) {
                    searchRequest.setControls(searchControls);
                    if (this.simplePageSize.isPresent()) {
                        searchRequest.addControl(new SimplePagedResultsControl(this.simplePageSize.getValue(), pagedResultsCookie));
                    }
                    if (this.dryRun.isPresent()) {
                        searchResult = new SearchResult(-1, ResultCode.SUCCESS, ToolMessages.INFO_LDAPSEARCH_DRY_RUN_REQUEST_NOT_SENT.get(this.dryRun.getIdentifierString(), String.valueOf(searchRequest)), null, null, 0, 0, null);
                        break;
                    }
                    if (!this.terse.isPresent() && (this.verbose.isPresent() || this.persistentSearch.isPresent() || this.filterFile.isPresent() || this.ldapURLFile.isPresent() || (this.filter.isPresent() && this.filter.getNumOccurrences() > 1))) {
                        this.commentToOut(ToolMessages.INFO_LDAPSEARCH_SENDING_SEARCH_REQUEST.get(String.valueOf(searchRequest)));
                    }
                    searchResult = pool.search(searchRequest);
                    if (searchResult.getEntryCount() > 0) {
                        totalEntries += searchResult.getEntryCount();
                    }
                    if (searchResult.getReferenceCount() > 0) {
                        totalReferences += searchResult.getReferenceCount();
                    }
                    if (this.simplePageSize.isPresent()) {
                        try {
                            final SimplePagedResultsControl pagedResultsControl = SimplePagedResultsControl.get(searchResult);
                            if (pagedResultsControl == null) {
                                throw new LDAPSearchException(new SearchResult(searchResult.getMessageID(), ResultCode.CONTROL_NOT_FOUND, ToolMessages.ERR_LDAPSEARCH_MISSING_PAGED_RESULTS_RESPONSE_CONTROL.get(), searchResult.getMatchedDN(), searchResult.getReferralURLs(), searchResult.getSearchEntries(), searchResult.getSearchReferences(), searchResult.getEntryCount(), searchResult.getReferenceCount(), searchResult.getResponseControls()));
                            }
                            if (pagedResultsControl.moreResultsToReturn()) {
                                if (this.verbose.isPresent()) {
                                    this.commentToOut(ToolMessages.INFO_LDAPSEARCH_INTERMEDIATE_PAGED_SEARCH_RESULT.get());
                                    this.displayResult(searchResult);
                                }
                                multiplePages = true;
                                pagedResultsCookie = pagedResultsControl.getCookie();
                                continue;
                            }
                        }
                        catch (final LDAPException le) {
                            Debug.debugException(le);
                            throw new LDAPSearchException(new SearchResult(searchResult.getMessageID(), ResultCode.CONTROL_NOT_FOUND, ToolMessages.ERR_LDAPSEARCH_CANNOT_DECODE_PAGED_RESULTS_RESPONSE_CONTROL.get(StaticUtils.getExceptionMessage(le)), searchResult.getMatchedDN(), searchResult.getReferralURLs(), searchResult.getSearchEntries(), searchResult.getSearchReferences(), searchResult.getEntryCount(), searchResult.getReferenceCount(), searchResult.getResponseControls()));
                        }
                        break;
                    }
                    break;
                }
            }
            catch (final LDAPSearchException lse) {
                Debug.debugException(lse);
                searchResult = lse.toLDAPResult();
                if (searchResult.getEntryCount() > 0) {
                    totalEntries += searchResult.getEntryCount();
                }
                if (searchResult.getReferenceCount() > 0) {
                    totalReferences += searchResult.getReferenceCount();
                }
            }
            if (searchResult.getResultCode() != ResultCode.SUCCESS || searchResult.getDiagnosticMessage() != null || !this.terse.isPresent()) {
                this.displayResult(searchResult);
            }
            if (multiplePages && !this.terse.isPresent()) {
                this.commentToOut(ToolMessages.INFO_LDAPSEARCH_TOTAL_SEARCH_ENTRIES.get(totalEntries));
                if (totalReferences > 0L) {
                    this.commentToOut(ToolMessages.INFO_LDAPSEARCH_TOTAL_SEARCH_REFERENCES.get(totalReferences));
                }
            }
            if (this.countEntries.isPresent()) {
                return ResultCode.valueOf((int)Math.min(totalEntries, 255L));
            }
            return searchResult.getResultCode();
        }
        finally {
            if (this.separateOutputFilePerSearch.isPresent()) {
                try {
                    this.outStream.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
                this.outStream = null;
                this.errStream = null;
            }
        }
    }
    
    private List<Control> getSearchControls() {
        final ArrayList<Control> controls = new ArrayList<Control>(10);
        if (this.searchControl.isPresent()) {
            controls.addAll(this.searchControl.getValues());
        }
        if (this.joinRequestControl != null) {
            controls.add(this.joinRequestControl);
        }
        if (this.matchedValuesRequestControl != null) {
            controls.add(this.matchedValuesRequestControl);
        }
        if (this.matchingEntryCountRequestControl != null) {
            controls.add(this.matchingEntryCountRequestControl);
        }
        if (this.overrideSearchLimitsRequestControl != null) {
            controls.add(this.overrideSearchLimitsRequestControl);
        }
        if (this.persistentSearchRequestControl != null) {
            controls.add(this.persistentSearchRequestControl);
        }
        if (this.sortRequestControl != null) {
            controls.add(this.sortRequestControl);
        }
        if (this.vlvRequestControl != null) {
            controls.add(this.vlvRequestControl);
        }
        controls.addAll(this.routeToBackendSetRequestControls);
        if (this.accountUsable.isPresent()) {
            controls.add(new AccountUsableRequestControl(true));
        }
        if (this.getBackendSetID.isPresent()) {
            controls.add(new GetBackendSetIDRequestControl(false));
        }
        if (this.getServerID.isPresent()) {
            controls.add(new GetServerIDRequestControl(false));
        }
        if (this.includeReplicationConflictEntries.isPresent()) {
            controls.add(new ReturnConflictEntriesRequestControl(true));
        }
        if (this.includeSoftDeletedEntries.isPresent()) {
            final String valueStr = StaticUtils.toLowerCase(this.includeSoftDeletedEntries.getValue());
            if (valueStr.equals("with-non-deleted-entries")) {
                controls.add(new SoftDeletedEntryAccessRequestControl(true, true, false));
            }
            else if (valueStr.equals("without-non-deleted-entries")) {
                controls.add(new SoftDeletedEntryAccessRequestControl(true, false, false));
            }
            else {
                controls.add(new SoftDeletedEntryAccessRequestControl(true, false, true));
            }
        }
        if (this.includeSubentries.isPresent()) {
            controls.add(new SubentriesRequestControl(true));
        }
        if (this.manageDsaIT.isPresent()) {
            controls.add(new ManageDsaITRequestControl(true));
        }
        if (this.realAttributesOnly.isPresent()) {
            controls.add(new RealAttributesOnlyRequestControl(true));
        }
        if (this.routeToServer.isPresent()) {
            controls.add(new RouteToServerRequestControl(false, this.routeToServer.getValue(), false, false, false));
        }
        if (this.virtualAttributesOnly.isPresent()) {
            controls.add(new VirtualAttributesOnlyRequestControl(true));
        }
        if (this.excludeBranch.isPresent()) {
            final ArrayList<String> dns = new ArrayList<String>(this.excludeBranch.getValues().size());
            for (final DN dn : this.excludeBranch.getValues()) {
                dns.add(dn.toString());
            }
            controls.add(new ExcludeBranchRequestControl(true, dns));
        }
        if (this.assertionFilter.isPresent()) {
            controls.add(new AssertionRequestControl(this.assertionFilter.getValue(), true));
        }
        if (this.getEffectiveRightsAuthzID.isPresent()) {
            String[] attributes;
            if (this.getEffectiveRightsAttribute.isPresent()) {
                attributes = new String[this.getEffectiveRightsAttribute.getValues().size()];
                for (int i = 0; i < attributes.length; ++i) {
                    attributes[i] = this.getEffectiveRightsAttribute.getValues().get(i);
                }
            }
            else {
                attributes = StaticUtils.NO_STRINGS;
            }
            controls.add(new GetEffectiveRightsRequestControl(true, this.getEffectiveRightsAuthzID.getValue(), attributes));
        }
        if (this.operationPurpose.isPresent()) {
            controls.add(new OperationPurposeRequestControl(true, "ldapsearch", "4.0.14", "LDAPSearch.getSearchControls", this.operationPurpose.getValue()));
        }
        if (this.proxyAs.isPresent()) {
            controls.add(new ProxiedAuthorizationV2RequestControl(this.proxyAs.getValue()));
        }
        if (this.proxyV1As.isPresent()) {
            controls.add(new ProxiedAuthorizationV1RequestControl(this.proxyV1As.getValue()));
        }
        if (this.suppressOperationalAttributeUpdates.isPresent()) {
            final EnumSet<SuppressType> suppressTypes = EnumSet.noneOf(SuppressType.class);
            for (final String s : this.suppressOperationalAttributeUpdates.getValues()) {
                if (s.equalsIgnoreCase("last-access-time")) {
                    suppressTypes.add(SuppressType.LAST_ACCESS_TIME);
                }
                else if (s.equalsIgnoreCase("last-login-time")) {
                    suppressTypes.add(SuppressType.LAST_LOGIN_TIME);
                }
                else {
                    if (!s.equalsIgnoreCase("last-login-ip")) {
                        continue;
                    }
                    suppressTypes.add(SuppressType.LAST_LOGIN_IP);
                }
            }
            controls.add(new SuppressOperationalAttributeUpdateRequestControl(suppressTypes));
        }
        if (this.rejectUnindexedSearch.isPresent()) {
            controls.add(new RejectUnindexedSearchRequestControl());
        }
        if (this.permitUnindexedSearch.isPresent()) {
            controls.add(new PermitUnindexedSearchRequestControl());
        }
        return controls;
    }
    
    private void displayResult(final LDAPResult result) {
        this.outputHandler.formatResult(result);
    }
    
    void writeOut(final String message) {
        if (this.outStream == null) {
            this.out(message);
        }
        else {
            this.outStream.println(message);
        }
    }
    
    private void writeErr(final String message) {
        if (this.errStream == null) {
            this.err(message);
        }
        else {
            this.errStream.println(message);
        }
    }
    
    private void commentToOut(final String message) {
        if (this.terse.isPresent()) {
            return;
        }
        for (final String line : StaticUtils.wrapLine(message, LDAPSearch.WRAP_COLUMN - 2)) {
            this.writeOut("# " + line);
        }
    }
    
    private void commentToErr(final String message) {
        for (final String line : StaticUtils.wrapLine(message, LDAPSearch.WRAP_COLUMN - 2)) {
            this.writeErr("# " + line);
        }
    }
    
    void setOutputHandler(final LDAPSearchOutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }
    
    @Override
    public void handleUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        this.outputHandler.formatUnsolicitedNotification(connection, notification);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(5));
        String[] args = { "--hostname", "directory.example.com", "--port", "389", "--bindDN", "uid=jdoe,ou=People,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "ou=People,dc=example,dc=com", "--searchScope", "sub", "(uid=jqpublic)", "givenName", "sn", "mail" };
        examples.put(args, ToolMessages.INFO_LDAPSEARCH_EXAMPLE_1.get());
        args = new String[] { "--hostname", "directory.example.com", "--port", "636", "--useSSL", "--saslOption", "mech=PLAIN", "--saslOption", "authID=u:jdoe", "--bindPasswordFile", "/path/to/password/file", "--baseDN", "ou=People,dc=example,dc=com", "--searchScope", "sub", "--filterFile", "/path/to/filter/file", "--outputFile", "/path/to/base/output/file", "--separateOutputFilePerSearch", "--requestedAttribute", "*", "--requestedAttribute", "+" };
        examples.put(args, ToolMessages.INFO_LDAPSEARCH_EXAMPLE_2.get());
        args = new String[] { "--hostname", "directory.example.com", "--port", "389", "--useStartTLS", "--trustStorePath", "/path/to/truststore/file", "--baseDN", "", "--searchScope", "base", "--outputFile", "/path/to/output/file", "--teeResultsToStandardOut", "(objectClass=*)", "*", "+" };
        examples.put(args, ToolMessages.INFO_LDAPSEARCH_EXAMPLE_3.get());
        args = new String[] { "--hostname", "directory.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--baseDN", "dc=example,dc=com", "--searchScope", "sub", "--outputFile", "/path/to/output/file", "--simplePageSize", "100", "(objectClass=*)", "*", "+" };
        examples.put(args, ToolMessages.INFO_LDAPSEARCH_EXAMPLE_4.get());
        args = new String[] { "--hostname", "directory.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--baseDN", "dc=example,dc=com", "--searchScope", "sub", "(&(givenName=John)(sn=Doe))", "debugsearchindex" };
        examples.put(args, ToolMessages.INFO_LDAPSEARCH_EXAMPLE_5.get());
        return examples;
    }
    
    static {
        LDAPSearch.WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
