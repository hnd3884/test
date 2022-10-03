package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressReferentialIntegrityUpdatesRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.ReplicationRepairRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationRequestControl;
import java.util.concurrent.TimeUnit;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationRemoteLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationLocalLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToServerRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetServerIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetBackendSetIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.NoOpRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.ldap.sdk.controls.ManageDsaITRequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV1RequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.HardDeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeleteRequestControl;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.util.SubtreeDeleterResult;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ObjectPair;
import java.io.Reader;
import java.io.InputStreamReader;
import com.unboundid.util.Base64;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPConnectionPoolHealthCheck;
import com.unboundid.ldap.sdk.PostConnectProcessor;
import com.unboundid.util.CommandLineTool;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartAdministrativeSessionPostConnectProcessor;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartAdministrativeSessionExtendedRequest;
import java.nio.charset.Charset;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.unboundidds.controls.GetUserResourceLimitsRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetAuthorizationEntryRequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.args.Argument;
import java.io.ByteArrayInputStream;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.SubtreeDeleter;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToBackendSetRequestControl;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import java.io.InputStream;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.ldif.LDIFWriter;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DurationArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPDelete extends LDAPCommandLineTool implements UnsolicitedNotificationHandler
{
    private static final int WRAP_COLUMN;
    private ArgumentParser parser;
    private BooleanArgument authorizationIdentity;
    private BooleanArgument clientSideSubtreeDelete;
    private BooleanArgument continueOnError;
    private BooleanArgument dryRun;
    private BooleanArgument followReferrals;
    private BooleanArgument getBackendSetID;
    private BooleanArgument getServerID;
    private BooleanArgument getUserResourceLimits;
    private BooleanArgument hardDelete;
    private BooleanArgument manageDsaIT;
    private BooleanArgument noOperation;
    private BooleanArgument replicationRepair;
    private BooleanArgument retryFailedOperations;
    private BooleanArgument softDelete;
    private BooleanArgument serverSideSubtreeDelete;
    private BooleanArgument suppressReferentialIntegrityUpdates;
    private BooleanArgument useAdministrativeSession;
    private BooleanArgument useAssuredReplication;
    private BooleanArgument verbose;
    private ControlArgument bindControl;
    private ControlArgument deleteControl;
    private DNArgument entryDN;
    private DNArgument proxyV1As;
    private DNArgument searchBaseDN;
    private DurationArgument assuredReplicationTimeout;
    private FileArgument dnFile;
    private FileArgument encryptionPassphraseFile;
    private FileArgument deleteEntriesMatchingFiltersFromFile;
    private FileArgument rejectFile;
    private FilterArgument assertionFilter;
    private FilterArgument deleteEntriesMatchingFilter;
    private IntegerArgument ratePerSecond;
    private IntegerArgument searchPageSize;
    private StringArgument assuredReplicationLocalLevel;
    private StringArgument assuredReplicationRemoteLevel;
    private StringArgument characterSet;
    private StringArgument getAuthorizationEntryAttribute;
    private StringArgument operationPurpose;
    private StringArgument preReadAttribute;
    private StringArgument proxyAs;
    private StringArgument routeToBackendSet;
    private StringArgument routeToServer;
    private final AtomicReference<LDIFWriter> rejectWriter;
    private volatile FixedRateBarrier deleteRateLimiter;
    private final InputStream in;
    private volatile LDAPConnectionPool connectionPool;
    private volatile List<Control> deleteControls;
    private volatile List<Control> searchControls;
    private final List<RouteToBackendSetRequestControl> routeToBackendSetRequestControls;
    private volatile SubtreeDeleter subtreeDeleter;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.in, System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final InputStream in, final OutputStream out, final OutputStream err, final String... args) {
        final LDAPDelete ldapDelete = new LDAPDelete(in, out, err);
        return ldapDelete.runTool(args);
    }
    
    public LDAPDelete(final OutputStream out, final OutputStream err) {
        this(null, out, err);
    }
    
    public LDAPDelete(final InputStream in, final OutputStream out, final OutputStream err) {
        super(out, err);
        this.parser = null;
        this.authorizationIdentity = null;
        this.clientSideSubtreeDelete = null;
        this.continueOnError = null;
        this.dryRun = null;
        this.followReferrals = null;
        this.getBackendSetID = null;
        this.getServerID = null;
        this.getUserResourceLimits = null;
        this.hardDelete = null;
        this.manageDsaIT = null;
        this.noOperation = null;
        this.replicationRepair = null;
        this.retryFailedOperations = null;
        this.softDelete = null;
        this.serverSideSubtreeDelete = null;
        this.suppressReferentialIntegrityUpdates = null;
        this.useAdministrativeSession = null;
        this.useAssuredReplication = null;
        this.verbose = null;
        this.bindControl = null;
        this.deleteControl = null;
        this.entryDN = null;
        this.proxyV1As = null;
        this.searchBaseDN = null;
        this.assuredReplicationTimeout = null;
        this.dnFile = null;
        this.encryptionPassphraseFile = null;
        this.deleteEntriesMatchingFiltersFromFile = null;
        this.rejectFile = null;
        this.assertionFilter = null;
        this.deleteEntriesMatchingFilter = null;
        this.ratePerSecond = null;
        this.searchPageSize = null;
        this.assuredReplicationLocalLevel = null;
        this.assuredReplicationRemoteLevel = null;
        this.characterSet = null;
        this.getAuthorizationEntryAttribute = null;
        this.operationPurpose = null;
        this.preReadAttribute = null;
        this.proxyAs = null;
        this.routeToBackendSet = null;
        this.routeToServer = null;
        this.rejectWriter = new AtomicReference<LDIFWriter>();
        this.deleteRateLimiter = null;
        this.connectionPool = null;
        this.deleteControls = Collections.emptyList();
        this.searchControls = Collections.emptyList();
        this.routeToBackendSetRequestControls = new ArrayList<RouteToBackendSetRequestControl>(10);
        this.subtreeDeleter = null;
        if (in == null) {
            this.in = new ByteArrayInputStream(StaticUtils.NO_BYTES);
        }
        else {
            this.in = in;
        }
    }
    
    @Override
    public String getToolName() {
        return "ldapdelete";
    }
    
    @Override
    public String getToolDescription() {
        return ToolMessages.INFO_LDAPDELETE_TOOL_DESCRIPTION.get();
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
        return Integer.MAX_VALUE;
    }
    
    @Override
    public String getTrailingArgumentsPlaceholder() {
        return ToolMessages.INFO_LDAPDELETE_TRAILING_ARGS_PLACEHOLDER.get();
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
    
    public boolean supportsOutputFile() {
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
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        this.parser = parser;
        final String argGroupData = ToolMessages.INFO_LDAPDELETE_ARG_GROUP_DATA.get();
        (this.entryDN = new DNArgument('b', "entryDN", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_DN.get())).addLongIdentifier("entry-dn", true);
        this.entryDN.addLongIdentifier("dn", true);
        this.entryDN.addLongIdentifier("dnToDelete", true);
        this.entryDN.addLongIdentifier("dn-to-delete", true);
        this.entryDN.addLongIdentifier("entry", true);
        this.entryDN.addLongIdentifier("entryToDelete", true);
        this.entryDN.addLongIdentifier("entry-to-delete", true);
        this.entryDN.setArgumentGroupName(argGroupData);
        parser.addArgument(this.entryDN);
        (this.dnFile = new FileArgument('f', "dnFile", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_DN_FILE.get(), true, true, true, false)).addLongIdentifier("dn-file", true);
        this.dnFile.addLongIdentifier("dnFilename", true);
        this.dnFile.addLongIdentifier("dn-filename", true);
        this.dnFile.addLongIdentifier("deleteEntriesWithDNsFromFile", true);
        this.dnFile.addLongIdentifier("delete-entries0-with-dns-from-file", true);
        this.dnFile.addLongIdentifier("file", true);
        this.dnFile.addLongIdentifier("filename", true);
        this.dnFile.setArgumentGroupName(argGroupData);
        parser.addArgument(this.dnFile);
        (this.deleteEntriesMatchingFilter = new FilterArgument(null, "deleteEntriesMatchingFilter", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_DELETE_ENTRIES_MATCHING_FILTER.get())).addLongIdentifier("delete-entries-matching-filter", true);
        this.deleteEntriesMatchingFilter.addLongIdentifier("deleteFilter", true);
        this.deleteEntriesMatchingFilter.addLongIdentifier("delete-filter", true);
        this.deleteEntriesMatchingFilter.addLongIdentifier("deleteSearchFilter", true);
        this.deleteEntriesMatchingFilter.addLongIdentifier("delete-search-filter", true);
        this.deleteEntriesMatchingFilter.addLongIdentifier("filter", true);
        this.deleteEntriesMatchingFilter.setArgumentGroupName(argGroupData);
        parser.addArgument(this.deleteEntriesMatchingFilter);
        (this.deleteEntriesMatchingFiltersFromFile = new FileArgument(null, "deleteEntriesMatchingFiltersFromFile", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_DELETE_ENTRIES_MATCHING_FILTER_FILE.get(), true, true, true, false)).addLongIdentifier("delete-entries-matching-filters-from-file", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("deleteEntriesMatchingFilterFromFile", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("delete-entries-matching-filter-from-file", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("deleteFilterFile", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("delete-filter-file", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("deleteSearchFilterFile", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("delete-search-filter-file", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("filterFile", true);
        this.deleteEntriesMatchingFiltersFromFile.addLongIdentifier("filter-file", true);
        this.deleteEntriesMatchingFiltersFromFile.setArgumentGroupName(argGroupData);
        parser.addArgument(this.deleteEntriesMatchingFiltersFromFile);
        (this.searchBaseDN = new DNArgument(null, "searchBaseDN", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_SEARCH_BASE_DN.get(), DN.NULL_DN)).addLongIdentifier("search-base-dn", true);
        this.searchBaseDN.addLongIdentifier("baseDN", true);
        this.searchBaseDN.addLongIdentifier("base-dn", true);
        this.searchBaseDN.setArgumentGroupName(argGroupData);
        parser.addArgument(this.searchBaseDN);
        (this.searchPageSize = new IntegerArgument(null, "searchPageSize", false, 1, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_SEARCH_PAGE_SIZE.get(), 1, Integer.MAX_VALUE)).addLongIdentifier("search-page-size", true);
        this.searchPageSize.addLongIdentifier("simplePagedResultsPageSize", true);
        this.searchPageSize.addLongIdentifier("simple-paged-results-page-size", true);
        this.searchPageSize.addLongIdentifier("pageSize", true);
        this.searchPageSize.addLongIdentifier("page-size", true);
        this.searchPageSize.setArgumentGroupName(argGroupData);
        parser.addArgument(this.searchPageSize);
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_ENCRYPTION_PW_FILE.get(), true, true, true, false)).addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryptionPINFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-pin-file", true);
        this.encryptionPassphraseFile.setArgumentGroupName(argGroupData);
        parser.addArgument(this.encryptionPassphraseFile);
        (this.characterSet = new StringArgument('i', "characterSet", false, 1, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_CHARSET.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_CHARSET.get(), "UTF-8")).addLongIdentifier("character-set", true);
        this.characterSet.addLongIdentifier("charSet", true);
        this.characterSet.addLongIdentifier("char-set", true);
        this.characterSet.addLongIdentifier("encoding", true);
        this.characterSet.setArgumentGroupName(argGroupData);
        parser.addArgument(this.characterSet);
        (this.rejectFile = new FileArgument('R', "rejectFile", false, 1, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_REJECT_FILE.get(), false, true, true, false)).addLongIdentifier("reject-file", true);
        this.rejectFile.addLongIdentifier("errorFile", true);
        this.rejectFile.addLongIdentifier("error-file", true);
        this.rejectFile.addLongIdentifier("failureFile", true);
        this.rejectFile.addLongIdentifier("failure-file", true);
        this.rejectFile.setArgumentGroupName(argGroupData);
        parser.addArgument(this.rejectFile);
        (this.verbose = new BooleanArgument('v', "verbose", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_VERBOSE.get())).setArgumentGroupName(argGroupData);
        parser.addArgument(this.verbose);
        final BooleanArgument scriptFriendly = new BooleanArgument(null, "scriptFriendly", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_SCRIPT_FRIENDLY.get());
        scriptFriendly.addLongIdentifier("script-friendly", true);
        scriptFriendly.setArgumentGroupName(argGroupData);
        scriptFriendly.setHidden(true);
        parser.addArgument(scriptFriendly);
        final String argGroupOp = ToolMessages.INFO_LDAPDELETE_ARG_GROUP_OPERATION.get();
        (this.retryFailedOperations = new BooleanArgument(null, "retryFailedOperations", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_RETRY_FAILED_OPS.get())).addLongIdentifier("retry-failed-operations", true);
        this.retryFailedOperations.addLongIdentifier("retryFailedOps", true);
        this.retryFailedOperations.addLongIdentifier("retry-failed-ops", true);
        this.retryFailedOperations.addLongIdentifier("retry", true);
        this.retryFailedOperations.setArgumentGroupName(argGroupOp);
        parser.addArgument(this.retryFailedOperations);
        (this.dryRun = new BooleanArgument('n', "dryRun", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_DRY_RUN.get())).addLongIdentifier("dry-run", true);
        this.dryRun.setArgumentGroupName(argGroupOp);
        parser.addArgument(this.dryRun);
        (this.continueOnError = new BooleanArgument('c', "continueOnError", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_CONTINUE_ON_ERROR.get())).addLongIdentifier("continue-on-error", true);
        this.continueOnError.setArgumentGroupName(argGroupOp);
        parser.addArgument(this.continueOnError);
        (this.followReferrals = new BooleanArgument(null, "followReferrals", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_FOLLOW_REFERRALS.get())).addLongIdentifier("follow-referrals");
        this.followReferrals.setArgumentGroupName(argGroupOp);
        parser.addArgument(this.followReferrals);
        (this.useAdministrativeSession = new BooleanArgument(null, "useAdministrativeSession", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_USE_ADMIN_SESSION.get())).addLongIdentifier("use-administrative-session", true);
        this.useAdministrativeSession.addLongIdentifier("useAdminSession", true);
        this.useAdministrativeSession.addLongIdentifier("use-admin-session", true);
        this.useAdministrativeSession.setArgumentGroupName(argGroupOp);
        parser.addArgument(this.useAdministrativeSession);
        (this.ratePerSecond = new IntegerArgument('r', "ratePerSecond", false, 1, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_RATE_PER_SECOND.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_RATE_PER_SECOND.get(), 1, Integer.MAX_VALUE)).addLongIdentifier("rate-per-second", true);
        this.ratePerSecond.addLongIdentifier("deletesPerSecond", true);
        this.ratePerSecond.addLongIdentifier("deletes-per-second", true);
        this.ratePerSecond.addLongIdentifier("operationsPerSecond", true);
        this.ratePerSecond.addLongIdentifier("operations-per-second", true);
        this.ratePerSecond.addLongIdentifier("opsPerSecond", true);
        this.ratePerSecond.addLongIdentifier("ops-per-second", true);
        this.ratePerSecond.setArgumentGroupName(argGroupOp);
        parser.addArgument(this.ratePerSecond);
        final IntegerArgument ldapVersion = new IntegerArgument('V', "ldapVersion", false, 1, "{version}", ToolMessages.INFO_LDAPDELETE_ARG_DESC_LDAP_VERSION.get(), 3, 3, 3);
        ldapVersion.addLongIdentifier("ldap-version", true);
        ldapVersion.setArgumentGroupName(argGroupOp);
        ldapVersion.setHidden(true);
        parser.addArgument(ldapVersion);
        final String argGroupControls = ToolMessages.INFO_LDAPDELETE_ARG_GROUP_CONTROLS.get();
        (this.clientSideSubtreeDelete = new BooleanArgument(null, "clientSideSubtreeDelete", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_CLIENT_SIDE_SUB_DEL.get())).addLongIdentifier("client-side-subtree-delete", true);
        this.clientSideSubtreeDelete.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.clientSideSubtreeDelete);
        (this.serverSideSubtreeDelete = new BooleanArgument('x', "serverSideSubtreeDelete", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_SERVER_SIDE_SUB_DEL.get())).addLongIdentifier("server-side-subtree-delete", true);
        this.serverSideSubtreeDelete.addLongIdentifier("deleteSubtree", true);
        this.serverSideSubtreeDelete.addLongIdentifier("delete-subtree", true);
        this.serverSideSubtreeDelete.addLongIdentifier("useSubtreeDeleteControl", true);
        this.serverSideSubtreeDelete.addLongIdentifier("use-subtree-delete-control", true);
        this.serverSideSubtreeDelete.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.serverSideSubtreeDelete);
        (this.softDelete = new BooleanArgument('s', "softDelete", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_SOFT_DELETE.get())).addLongIdentifier("soft-delete", true);
        this.softDelete.addLongIdentifier("useSoftDelete", true);
        this.softDelete.addLongIdentifier("use-soft-delete", true);
        this.softDelete.addLongIdentifier("useSoftDeleteControl", true);
        this.softDelete.addLongIdentifier("use-soft-delete-control", true);
        this.softDelete.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.softDelete);
        (this.hardDelete = new BooleanArgument(null, "hardDelete", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_HARD_DELETE.get())).addLongIdentifier("hard-delete", true);
        this.hardDelete.addLongIdentifier("useHardDelete", true);
        this.hardDelete.addLongIdentifier("use-hard-delete", true);
        this.hardDelete.addLongIdentifier("useHardDeleteControl", true);
        this.hardDelete.addLongIdentifier("use-hard-delete-control", true);
        this.hardDelete.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.hardDelete);
        (this.proxyAs = new StringArgument('Y', "proxyAs", false, 1, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_AUTHZ_ID.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_PROXY_AS.get())).addLongIdentifier("proxy-as", true);
        this.proxyAs.addLongIdentifier("proxyV2As", true);
        this.proxyAs.addLongIdentifier("proxy-v2-as", true);
        this.proxyAs.addLongIdentifier("proxiedAuth", true);
        this.proxyAs.addLongIdentifier("proxied-auth", true);
        this.proxyAs.addLongIdentifier("proxiedAuthorization", true);
        this.proxyAs.addLongIdentifier("proxied-authorization", true);
        this.proxyAs.addLongIdentifier("useProxiedAuth", true);
        this.proxyAs.addLongIdentifier("use-proxied-auth", true);
        this.proxyAs.addLongIdentifier("useProxiedAuthorization", true);
        this.proxyAs.addLongIdentifier("use-proxied-authorization", true);
        this.proxyAs.addLongIdentifier("useProxiedAuthControl", true);
        this.proxyAs.addLongIdentifier("use-proxied-auth-control", true);
        this.proxyAs.addLongIdentifier("useProxiedAuthorizationControl", true);
        this.proxyAs.addLongIdentifier("use-proxied-authorization-control", true);
        this.proxyAs.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.proxyAs);
        (this.proxyV1As = new DNArgument(null, "proxyV1As", false, 1, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_PROXY_V1_AS.get())).addLongIdentifier("proxy-v1-as", true);
        this.proxyV1As.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.proxyV1As);
        (this.manageDsaIT = new BooleanArgument(null, "useManageDsaIT", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_MANAGE_DSA_IT.get())).addLongIdentifier("use-manage-dsa-it", true);
        this.manageDsaIT.addLongIdentifier("manageDsaIT", true);
        this.manageDsaIT.addLongIdentifier("manage-dsa-it", true);
        this.manageDsaIT.addLongIdentifier("manageDsaITControl", true);
        this.manageDsaIT.addLongIdentifier("manage-dsa-it-control", true);
        this.manageDsaIT.addLongIdentifier("useManageDsaITControl", true);
        this.manageDsaIT.addLongIdentifier("use-manage-dsa-it-control", true);
        this.manageDsaIT.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.manageDsaIT);
        (this.assertionFilter = new FilterArgument(null, "assertionFilter", false, 1, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_ASSERTION_FILTER.get())).addLongIdentifier("assertion-filter", true);
        this.assertionFilter.addLongIdentifier("useAssertionFilter", true);
        this.assertionFilter.addLongIdentifier("use-assertion-filter", true);
        this.assertionFilter.addLongIdentifier("assertionControl", true);
        this.assertionFilter.addLongIdentifier("assertion-control", true);
        this.assertionFilter.addLongIdentifier("useAssertionControl", true);
        this.assertionFilter.addLongIdentifier("use-assertion-control", true);
        this.assertionFilter.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.assertionFilter);
        (this.preReadAttribute = new StringArgument(null, "preReadAttribute", false, 0, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_PRE_READ_ATTR.get())).addLongIdentifier("pre-read-attribute", true);
        this.preReadAttribute.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.preReadAttribute);
        (this.noOperation = new BooleanArgument(null, "noOperation", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_NO_OP.get())).addLongIdentifier("no-operation", true);
        this.noOperation.addLongIdentifier("noOp", true);
        this.noOperation.addLongIdentifier("no-op", true);
        this.noOperation.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.noOperation);
        (this.getBackendSetID = new BooleanArgument(null, "getBackendSetID", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_GET_BACKEND_SET_ID.get())).addLongIdentifier("get-backend-set-id", true);
        this.getBackendSetID.addLongIdentifier("useGetBackendSetID", true);
        this.getBackendSetID.addLongIdentifier("use-get-backend-set-id", true);
        this.getBackendSetID.addLongIdentifier("useGetBackendSetIDControl", true);
        this.getBackendSetID.addLongIdentifier("use-get-backend-set-id-control", true);
        this.getBackendSetID.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.getBackendSetID);
        (this.routeToBackendSet = new StringArgument(null, "routeToBackendSet", false, 0, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_ROUTE_TO_BACKEND_SET.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_ROUTE_TO_BACKEND_SET.get())).addLongIdentifier("route-to-backend-set", true);
        this.routeToBackendSet.addLongIdentifier("useRouteToBackendSet", true);
        this.routeToBackendSet.addLongIdentifier("use0route-to-backend-set", true);
        this.routeToBackendSet.addLongIdentifier("useRouteToBackendSetControl", true);
        this.routeToBackendSet.addLongIdentifier("use-route-to-backend-set-control", true);
        this.routeToBackendSet.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.routeToBackendSet);
        (this.getServerID = new BooleanArgument(null, "getServerID", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_GET_SERVER_ID.get())).addLongIdentifier("get-server-id", true);
        this.getServerID.addLongIdentifier("getBackendServerID", true);
        this.getServerID.addLongIdentifier("get-backend-server-id", true);
        this.getServerID.addLongIdentifier("useGetServerID", true);
        this.getServerID.addLongIdentifier("use-get-server-id", true);
        this.getServerID.addLongIdentifier("useGetServerIDControl", true);
        this.getServerID.addLongIdentifier("use-get-server-id-control", true);
        this.getServerID.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.getServerID);
        (this.routeToServer = new StringArgument(null, "routeToServer", false, 1, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_ID.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_ROUTE_TO_SERVER.get())).addLongIdentifier("route-to-server", true);
        this.routeToServer.addLongIdentifier("routeToBackendServer", true);
        this.routeToServer.addLongIdentifier("route-to-backend-server", true);
        this.routeToServer.addLongIdentifier("useRouteToServer", true);
        this.routeToServer.addLongIdentifier("use-route-to-server", true);
        this.routeToServer.addLongIdentifier("useRouteToBackendServer", true);
        this.routeToServer.addLongIdentifier("use-route-to-backend-server", true);
        this.routeToServer.addLongIdentifier("useRouteToServerControl", true);
        this.routeToServer.addLongIdentifier("use-route-to-server-control", true);
        this.routeToServer.addLongIdentifier("useRouteToBackendServerControl", true);
        this.routeToServer.addLongIdentifier("use-route-to-backend-server-control", true);
        this.routeToServer.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.routeToServer);
        (this.useAssuredReplication = new BooleanArgument(null, "useAssuredReplication", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_USE_ASSURED_REPLICATION.get())).addLongIdentifier("use-assured-replication", true);
        this.useAssuredReplication.addLongIdentifier("assuredReplication", true);
        this.useAssuredReplication.addLongIdentifier("assured-replication", true);
        this.useAssuredReplication.addLongIdentifier("assuredReplicationControl", true);
        this.useAssuredReplication.addLongIdentifier("assured-replication-control", true);
        this.useAssuredReplication.addLongIdentifier("useAssuredReplicationControl", true);
        this.useAssuredReplication.addLongIdentifier("use-assured-replication-control", true);
        this.useAssuredReplication.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.useAssuredReplication);
        (this.assuredReplicationLocalLevel = new StringArgument(null, "assuredReplicationLocalLevel", false, 1, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_ASSURED_REPLICATION_LOCAL_LEVEL.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_ASSURED_REPLICATION_LOCAL_LEVEL.get(), StaticUtils.setOf("none", "received-any-server", "processed-all-servers"))).addLongIdentifier("assured-replication-local-level", true);
        this.assuredReplicationLocalLevel.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.assuredReplicationLocalLevel);
        (this.assuredReplicationRemoteLevel = new StringArgument(null, "assuredReplicationRemoteLevel", false, 1, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_ASSURED_REPLICATION_REMOTE_LEVEL.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_ASSURED_REPLICATION_REMOTE_LEVEL.get(), StaticUtils.setOf("none", "received-any-remote-location", "received-all-remote-locations", "processed-all-remote-servers"))).addLongIdentifier("assured-replication-remote-level", true);
        this.assuredReplicationRemoteLevel.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.assuredReplicationRemoteLevel);
        (this.assuredReplicationTimeout = new DurationArgument(null, "assuredReplicationTimeout", false, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_ASSURED_REPLICATION_TIMEOUT.get())).addLongIdentifier("assured-replication-timeout", true);
        this.assuredReplicationTimeout.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.assuredReplicationTimeout);
        (this.replicationRepair = new BooleanArgument(null, "replicationRepair", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_REPLICATION_REPAIR.get())).addLongIdentifier("replication-repair", true);
        this.replicationRepair.addLongIdentifier("replicationRepairControl", true);
        this.replicationRepair.addLongIdentifier("replication-repair-control", true);
        this.replicationRepair.addLongIdentifier("useReplicationRepair", true);
        this.replicationRepair.addLongIdentifier("use-replication-repair", true);
        this.replicationRepair.addLongIdentifier("useReplicationRepairControl", true);
        this.replicationRepair.addLongIdentifier("use-replication-repair-control", true);
        this.replicationRepair.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.replicationRepair);
        (this.suppressReferentialIntegrityUpdates = new BooleanArgument(null, "suppressReferentialIntegrityUpdates", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_SUPPRESS_REFINT_UPDATES.get())).addLongIdentifier("suppress-referential-integrity-updates", true);
        this.suppressReferentialIntegrityUpdates.addLongIdentifier("useSuppressReferentialIntegrityUpdates", true);
        this.suppressReferentialIntegrityUpdates.addLongIdentifier("use-suppress-referential-integrity-updates", true);
        this.suppressReferentialIntegrityUpdates.addLongIdentifier("useSuppressReferentialIntegrityUpdatesControl", true);
        this.suppressReferentialIntegrityUpdates.addLongIdentifier("use-suppress-referential-integrity-updates-control", true);
        this.suppressReferentialIntegrityUpdates.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.suppressReferentialIntegrityUpdates);
        (this.operationPurpose = new StringArgument(null, "operationPurpose", false, 1, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_OP_PURPOSE.get())).addLongIdentifier("operation-purpose", true);
        this.operationPurpose.addLongIdentifier("operationPurposeControl", true);
        this.operationPurpose.addLongIdentifier("operation-purpose-control", true);
        this.operationPurpose.addLongIdentifier("useOperationPurpose", true);
        this.operationPurpose.addLongIdentifier("use-operation-purpose", true);
        this.operationPurpose.addLongIdentifier("useOperationPurposeControl", true);
        this.operationPurpose.addLongIdentifier("use-operation-purpose-control", true);
        this.operationPurpose.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.operationPurpose);
        (this.authorizationIdentity = new BooleanArgument('E', "authorizationIdentity", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_AUTHZ_ID.get())).addLongIdentifier("authorization-identity", true);
        this.authorizationIdentity.addLongIdentifier("useAuthorizationIdentity", true);
        this.authorizationIdentity.addLongIdentifier("use-authorization-identity", true);
        this.authorizationIdentity.addLongIdentifier("useAuthorizationIdentityControl", true);
        this.authorizationIdentity.addLongIdentifier("use-authorization-identity-control", true);
        this.authorizationIdentity.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.authorizationIdentity);
        (this.getAuthorizationEntryAttribute = new StringArgument(null, "getAuthorizationEntryAttribute", false, 0, ToolMessages.INFO_LDAPDELETE_ARG_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPDELETE_ARG_DESC_GET_AUTHZ_ENTRY_ATTR.get())).addLongIdentifier("get-authorization-entry-attribute", true);
        this.getAuthorizationEntryAttribute.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.getAuthorizationEntryAttribute);
        (this.getUserResourceLimits = new BooleanArgument(null, "getUserResourceLimits", 1, ToolMessages.INFO_LDAPDELETE_ARG_DESC_GET_USER_RESOURCE_LIMITS.get())).addLongIdentifier("get-user-resource-limits", true);
        this.getUserResourceLimits.addLongIdentifier("getUserResourceLimitsControl", true);
        this.getUserResourceLimits.addLongIdentifier("get-user-resource-limits-control", true);
        this.getUserResourceLimits.addLongIdentifier("useGetUserResourceLimits", true);
        this.getUserResourceLimits.addLongIdentifier("use-get-user-resource-limits", true);
        this.getUserResourceLimits.addLongIdentifier("useGetUserResourceLimitsControl", true);
        this.getUserResourceLimits.addLongIdentifier("use-get-user-resource-limits-control", true);
        this.getUserResourceLimits.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.getUserResourceLimits);
        (this.deleteControl = new ControlArgument('J', "deleteControl", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_DELETE_CONTROL.get())).addLongIdentifier("delete-control", true);
        this.deleteControl.addLongIdentifier("operationControl", true);
        this.deleteControl.addLongIdentifier("operation-control", true);
        this.deleteControl.addLongIdentifier("control", true);
        this.deleteControl.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.deleteControl);
        (this.bindControl = new ControlArgument(null, "bindControl", false, 0, null, ToolMessages.INFO_LDAPDELETE_ARG_DESC_BIND_CONTROL.get())).addLongIdentifier("bind-control", true);
        this.bindControl.setArgumentGroupName(argGroupControls);
        parser.addArgument(this.bindControl);
        parser.addExclusiveArgumentSet(this.entryDN, this.dnFile, this.deleteEntriesMatchingFilter, this.deleteEntriesMatchingFiltersFromFile);
        parser.addDependentArgumentSet(this.searchBaseDN, this.deleteEntriesMatchingFilter, this.deleteEntriesMatchingFiltersFromFile);
        parser.addDependentArgumentSet(this.searchPageSize, this.deleteEntriesMatchingFilter, this.deleteEntriesMatchingFiltersFromFile, this.clientSideSubtreeDelete);
        parser.addExclusiveArgumentSet(this.followReferrals, this.manageDsaIT, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.followReferrals, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.preReadAttribute, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.getBackendSetID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.getServerID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.noOperation, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.softDelete, this.hardDelete, new Argument[0]);
    }
    
    @Override
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
        if (!this.parser.getTrailingArguments().isEmpty()) {
            for (final Argument a : Arrays.asList(this.entryDN, this.dnFile, this.deleteEntriesMatchingFilter, this.deleteEntriesMatchingFiltersFromFile)) {
                if (a.isPresent()) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPDELETE_TRAILING_ARG_CONFLICT.get(a.getIdentifierString()));
                }
            }
        }
        if (this.routeToBackendSet.isPresent()) {
            final List<String> values = this.routeToBackendSet.getValues();
            final Map<String, List<String>> idsByRP = new LinkedHashMap<String, List<String>>(StaticUtils.computeMapCapacity(values.size()));
            for (final String value : values) {
                final int colonPos = value.indexOf(58);
                if (colonPos <= 0) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPDELETE_ROUTE_TO_BACKEND_SET_INVALID_FORMAT.get(value, this.routeToBackendSet.getIdentifierString()));
                }
                final String rpID = value.substring(0, colonPos);
                final String bsID = value.substring(colonPos + 1);
                List<String> idsForRP = idsByRP.get(rpID);
                if (idsForRP == null) {
                    idsForRP = new ArrayList<String>(values.size());
                    idsByRP.put(rpID, idsForRP);
                }
                idsForRP.add(bsID);
            }
            for (final Map.Entry<String, List<String>> e : idsByRP.entrySet()) {
                final String rpID2 = e.getKey();
                final List<String> bsIDs = e.getValue();
                this.routeToBackendSetRequestControls.add(RouteToBackendSetRequestControl.createAbsoluteRoutingRequest(true, rpID2, bsIDs));
            }
        }
    }
    
    @Override
    protected List<Control> getBindControls() {
        final ArrayList<Control> bindControls = new ArrayList<Control>(10);
        if (this.bindControl.isPresent()) {
            bindControls.addAll(this.bindControl.getValues());
        }
        if (this.authorizationIdentity.isPresent()) {
            bindControls.add(new AuthorizationIdentityRequestControl(true));
        }
        if (this.getAuthorizationEntryAttribute.isPresent()) {
            bindControls.add(new GetAuthorizationEntryRequestControl(true, true, this.getAuthorizationEntryAttribute.getValues()));
        }
        if (this.getUserResourceLimits.isPresent()) {
            bindControls.add(new GetUserResourceLimitsRequestControl(true));
        }
        return bindControls;
    }
    
    @Override
    protected boolean supportsMultipleServers() {
        return true;
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
        this.searchControls = this.getSearchControls();
        this.deleteControls = this.getDeleteControls();
        if (this.ratePerSecond.isPresent()) {
            this.deleteRateLimiter = new FixedRateBarrier(1000L, this.ratePerSecond.getValue());
        }
        if (this.clientSideSubtreeDelete.isPresent()) {
            (this.subtreeDeleter = new SubtreeDeleter()).setAdditionalSearchControls(this.searchControls);
            this.subtreeDeleter.setAdditionalSearchControls(this.deleteControls);
            this.subtreeDeleter.setDeleteRateLimiter(this.deleteRateLimiter);
            if (this.searchPageSize.isPresent()) {
                this.subtreeDeleter.setSimplePagedResultsPageSize(this.searchPageSize.getValue());
            }
        }
        char[] encryptionPassphrase = null;
        Charset charset = null;
        Label_0222: {
            if (this.encryptionPassphraseFile.isPresent()) {
                try {
                    encryptionPassphrase = this.getPasswordFileReader().readPassword(this.encryptionPassphraseFile.getValue());
                    break Label_0222;
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    this.commentToErr(e.getMessage());
                    return e.getResultCode();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    this.commentToErr(ToolMessages.ERR_LDAPDELETE_CANNOT_READ_ENCRYPTION_PW_FILE.get(this.encryptionPassphraseFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e2)));
                    return ResultCode.LOCAL_ERROR;
                }
            }
            encryptionPassphrase = null;
            try {
                charset = Charset.forName(this.characterSet.getValue());
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_UNSUPPORTED_CHARSET.get(this.characterSet.getValue()));
                return ResultCode.PARAM_ERROR;
            }
        }
        StartAdministrativeSessionPostConnectProcessor p;
        if (this.useAdministrativeSession.isPresent()) {
            p = new StartAdministrativeSessionPostConnectProcessor(new StartAdministrativeSessionExtendedRequest(this.getToolName(), true, new Control[0]));
        }
        else {
            p = null;
        }
        try {
            (this.connectionPool = this.getConnectionPool(1, 2, 0, p, null, true, new ReportBindResultLDAPConnectionPoolHealthCheck(this, true, this.verbose.isPresent()))).setRetryFailedOperationsDueToInvalidConnections(this.retryFailedOperations.isPresent());
        }
        catch (final LDAPException e4) {
            Debug.debugException(e4);
            if (e4.getResultCode() != ResultCode.INVALID_CREDENTIALS) {
                for (final String line : ResultUtils.formatResult(e4, true, 0, LDAPDelete.WRAP_COLUMN)) {
                    this.err(line);
                }
            }
            return e4.getResultCode();
        }
        final AtomicReference<ResultCode> returnCode = new AtomicReference<ResultCode>();
        if (this.entryDN.isPresent()) {
            this.deleteFromEntryDNArgument(returnCode);
        }
        else if (this.dnFile.isPresent()) {
            this.deleteFromDNFile(returnCode, charset, encryptionPassphrase);
        }
        else if (this.deleteEntriesMatchingFilter.isPresent()) {
            this.deleteFromFilters(returnCode);
        }
        else if (this.deleteEntriesMatchingFiltersFromFile.isPresent()) {
            this.deleteFromFilterFile(returnCode, charset, encryptionPassphrase);
        }
        else if (!this.parser.getTrailingArguments().isEmpty()) {
            this.deleteFromTrailingArguments(returnCode);
        }
        else {
            this.deleteFromStandardInput(returnCode, charset, encryptionPassphrase);
        }
        final LDIFWriter rw = this.rejectWriter.get();
        if (rw != null) {
            try {
                rw.close();
            }
            catch (final Exception e5) {
                Debug.debugException(e5);
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_ERROR_CLOSING_REJECT_WRITER.get(this.rejectFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e5)));
                returnCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
            }
        }
        this.connectionPool.close();
        returnCode.compareAndSet(null, ResultCode.SUCCESS);
        return returnCode.get();
    }
    
    private void deleteFromEntryDNArgument(final AtomicReference<ResultCode> returnCode) {
        for (final DN dn : this.entryDN.getValues()) {
            if (!this.deleteEntry(dn.toString(), returnCode) && !this.continueOnError.isPresent()) {
                return;
            }
        }
    }
    
    private void deleteFromDNFile(final AtomicReference<ResultCode> returnCode, final Charset charset, final char[] encryptionPassphrase) {
        final List<char[]> potentialPassphrases = new ArrayList<char[]>(this.dnFile.getValues().size());
        if (encryptionPassphrase != null) {
            potentialPassphrases.add(encryptionPassphrase);
        }
        for (final File f : this.dnFile.getValues()) {
            if (this.verbose.isPresent()) {
                this.commentToOut(ToolMessages.INFO_LDAPDELETE_READING_DNS_FROM_FILE.get(f.getAbsolutePath()));
                this.out(new Object[0]);
            }
            try (final FileInputStream fis = new FileInputStream(f)) {
                if (!this.deleteDNsFromInputStream(returnCode, fis, charset, potentialPassphrases) && !this.continueOnError.isPresent()) {
                    return;
                }
            }
            catch (final Exception e) {
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_ERROR_OPENING_DN_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
                if (!this.continueOnError.isPresent()) {
                    return;
                }
                continue;
            }
        }
    }
    
    private boolean deleteDNsFromInputStream(final AtomicReference<ResultCode> returnCode, final InputStream inputStream, final Charset charset, final List<char[]> potentialPassphrases) throws IOException, GeneralSecurityException {
        boolean successful = true;
        long lineNumber = 0L;
        final BufferedReader reader = this.getBufferedReader(inputStream, charset, potentialPassphrases);
        while (true) {
            final String line = reader.readLine();
            ++lineNumber;
            if (line == null) {
                return successful;
            }
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            if (this.deleteDNFromInputStream(returnCode, line)) {
                continue;
            }
            if (!this.continueOnError.isPresent()) {
                return false;
            }
            successful = false;
        }
    }
    
    private boolean deleteDNFromInputStream(final AtomicReference<ResultCode> returnCode, final String rawString) {
        final String lowerString = StaticUtils.toLowerCase(rawString);
        if (lowerString.startsWith("dn::")) {
            final String base64EncodedDN = rawString.substring(4).trim();
            if (base64EncodedDN.isEmpty()) {
                returnCode.compareAndSet(null, ResultCode.PARAM_ERROR);
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_BASE64_DN_EMPTY.get(rawString));
                return false;
            }
            String base64DecodedDN;
            try {
                base64DecodedDN = Base64.decodeToString(base64EncodedDN);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                returnCode.compareAndSet(null, ResultCode.PARAM_ERROR);
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_BASE64_DN_NOT_BASE64.get(rawString));
                return false;
            }
            return this.deleteEntry(base64DecodedDN, returnCode);
        }
        else {
            if (!lowerString.startsWith("dn:")) {
                return this.deleteEntry(rawString, returnCode);
            }
            final String dn = rawString.substring(3).trim();
            if (dn.isEmpty()) {
                returnCode.compareAndSet(null, ResultCode.PARAM_ERROR);
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_DN_EMPTY.get(rawString));
                return false;
            }
            return this.deleteEntry(dn, returnCode);
        }
    }
    
    private BufferedReader getBufferedReader(final InputStream inputStream, final Charset charset, final List<char[]> potentialPassphrases) throws IOException, GeneralSecurityException {
        final ObjectPair<InputStream, char[]> decryptedInputStreamData = ToolUtils.getPossiblyPassphraseEncryptedInputStream(inputStream, potentialPassphrases, !this.encryptionPassphraseFile.isPresent(), ToolMessages.INFO_LDAPDELETE_ENCRYPTION_PASSPHRASE_PROMPT.get(), ToolMessages.ERR_LDAPDELETE_ENCRYPTION_PASSPHRASE_ERROR.get(), this.getOut(), this.getErr());
        final InputStream decryptedInputStream = decryptedInputStreamData.getFirst();
        final char[] passphrase = decryptedInputStreamData.getSecond();
        if (passphrase != null) {
            boolean isExistingPassphrase = false;
            for (final char[] existingPassphrase : potentialPassphrases) {
                if (Arrays.equals(passphrase, existingPassphrase)) {
                    isExistingPassphrase = true;
                    break;
                }
            }
            if (!isExistingPassphrase) {
                potentialPassphrases.add(passphrase);
            }
        }
        final InputStream decompressedInputStream = ToolUtils.getPossiblyGZIPCompressedInputStream(decryptedInputStream);
        final InputStreamReader inputStreamReader = new InputStreamReader(decompressedInputStream, charset);
        return new BufferedReader(inputStreamReader);
    }
    
    private void deleteFromFilters(final AtomicReference<ResultCode> returnCode) {
        for (final Filter f : this.deleteEntriesMatchingFilter.getValues()) {
            if (!this.searchAndDelete(f.toString(), returnCode) && !this.continueOnError.isPresent()) {
                return;
            }
        }
    }
    
    private void deleteFromFilterFile(final AtomicReference<ResultCode> returnCode, final Charset charset, final char[] encryptionPassphrase) {
        final List<char[]> potentialPassphrases = new ArrayList<char[]>(this.dnFile.getValues().size());
        if (encryptionPassphrase != null) {
            potentialPassphrases.add(encryptionPassphrase);
        }
        for (final File f : this.deleteEntriesMatchingFiltersFromFile.getValues()) {
            if (this.verbose.isPresent()) {
                this.commentToOut(ToolMessages.INFO_LDAPDELETE_READING_FILTERS_FROM_FILE.get(f.getAbsolutePath()));
                this.out(new Object[0]);
            }
            try (final FileInputStream fis = new FileInputStream(f);
                 final BufferedReader reader = this.getBufferedReader(fis, charset, potentialPassphrases)) {
                while (true) {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.startsWith("#")) {
                        continue;
                    }
                    if (!this.searchAndDelete(line, returnCode) && !this.continueOnError.isPresent()) {
                        return;
                    }
                }
            }
            catch (final IOException | GeneralSecurityException e) {
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_ERROR_READING_FILTER_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
                if (!this.continueOnError.isPresent()) {
                    return;
                }
                continue;
            }
        }
    }
    
    private boolean searchAndDelete(final String filterString, final AtomicReference<ResultCode> returnCode) {
        boolean successful = true;
        final AtomicLong entriesDeleted = new AtomicLong(0L);
        for (final DN baseDN : this.searchBaseDN.getValues()) {
            if (this.searchPageSize.isPresent()) {
                successful &= this.doPagedSearchAndDelete(baseDN.toString(), filterString, returnCode, entriesDeleted);
            }
            else {
                successful &= this.doNonPagedSearchAndDelete(baseDN.toString(), filterString, returnCode, entriesDeleted);
            }
        }
        if (successful && entriesDeleted.get() == 0L) {
            this.commentToErr(ToolMessages.ERR_LDAPDELETE_SEARCH_RETURNED_NO_ENTRIES.get(filterString));
            returnCode.compareAndSet(null, ResultCode.NO_RESULTS_RETURNED);
            successful = false;
        }
        return successful;
    }
    
    private boolean doPagedSearchAndDelete(final String baseDN, final String filterString, final AtomicReference<ResultCode> returnCode, final AtomicLong entriesDeleted) {
        ASN1OctetString cookie = null;
        final TreeSet<DN> matchingEntryDNs = new TreeSet<DN>();
        final LDAPDeleteSearchListener searchListener = new LDAPDeleteSearchListener(this, matchingEntryDNs, baseDN, filterString, returnCode);
    Label_0027_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        final ArrayList<Control> requestControls = new ArrayList<Control>(10);
                        requestControls.addAll(this.searchControls);
                        requestControls.add(new SimplePagedResultsControl(this.searchPageSize.getValue(), cookie, true));
                        final SearchRequest searchRequest = new SearchRequest(searchListener, baseDN, SearchScope.SUB, DereferencePolicy.NEVER, 0, 0, false, filterString, new String[] { "1.1" });
                        searchRequest.setControls(requestControls);
                        if (this.verbose.isPresent()) {
                            this.commentToOut(ToolMessages.INFO_LDAPDELETE_ISSUING_SEARCH_REQUEST.get(String.valueOf(searchRequest)));
                        }
                        final SearchResult searchResult = this.connectionPool.search(searchRequest);
                        if (this.verbose.isPresent()) {
                            this.commentToOut(ToolMessages.INFO_LDAPDELETE_RECEIVED_SEARCH_RESULT.get(String.valueOf(searchResult)));
                        }
                        final SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);
                        if (responseControl == null) {
                            throw new LDAPException(ResultCode.CONTROL_NOT_FOUND, ToolMessages.ERR_LDAPDELETE_MISSING_PAGED_RESULTS_RESPONSE.get(searchResult));
                        }
                        if (!responseControl.moreResultsToReturn()) {
                            break Label_0027_Outer;
                        }
                        cookie = responseControl.getCookie();
                    }
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    returnCode.compareAndSet(null, e.getResultCode());
                    this.commentToErr(ToolMessages.ERR_LDAPDELETE_SEARCH_ERROR.get(baseDN, filterString, String.valueOf(e.getResultCode()), e.getMessage()));
                    continue Label_0027_Outer;
                }
                continue;
            }
        }
        boolean allSuccessful = true;
        final Iterator<DN> iterator = matchingEntryDNs.descendingIterator();
        while (iterator.hasNext()) {
            if (this.deleteEntry(iterator.next().toString(), returnCode)) {
                entriesDeleted.incrementAndGet();
            }
            else {
                allSuccessful = false;
                if (!this.continueOnError.isPresent()) {
                    break;
                }
                continue;
            }
        }
        return allSuccessful;
    }
    
    private boolean doNonPagedSearchAndDelete(final String baseDN, final String filterString, final AtomicReference<ResultCode> returnCode, final AtomicLong entriesDeleted) {
        final TreeSet<DN> matchingEntryDNs = new TreeSet<DN>();
        final LDAPDeleteSearchListener searchListener = new LDAPDeleteSearchListener(this, matchingEntryDNs, baseDN, filterString, returnCode);
        try {
            final SearchRequest searchRequest = new SearchRequest(searchListener, baseDN, SearchScope.SUB, DereferencePolicy.NEVER, 0, 0, false, filterString, new String[] { "1.1" });
            searchRequest.setControls(this.searchControls);
            if (this.verbose.isPresent()) {
                this.commentToOut(ToolMessages.INFO_LDAPDELETE_ISSUING_SEARCH_REQUEST.get(String.valueOf(searchRequest)));
            }
            final SearchResult searchResult = this.connectionPool.search(searchRequest);
            if (this.verbose.isPresent()) {
                this.commentToOut(ToolMessages.INFO_LDAPDELETE_RECEIVED_SEARCH_RESULT.get(String.valueOf(searchResult)));
            }
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            returnCode.compareAndSet(null, e.getResultCode());
            this.commentToErr(ToolMessages.ERR_LDAPDELETE_SEARCH_ERROR.get(baseDN, filterString, String.valueOf(e.getResultCode()), e.getMessage()));
        }
        boolean allSuccessful = true;
        final Iterator<DN> iterator = matchingEntryDNs.descendingIterator();
        while (iterator.hasNext()) {
            if (this.deleteEntry(iterator.next().toString(), returnCode)) {
                entriesDeleted.incrementAndGet();
            }
            else {
                allSuccessful = false;
                if (!this.continueOnError.isPresent()) {
                    break;
                }
                continue;
            }
        }
        return allSuccessful;
    }
    
    private void deleteFromTrailingArguments(final AtomicReference<ResultCode> returnCode) {
        for (final String dn : this.parser.getTrailingArguments()) {
            if (!this.deleteEntry(dn, returnCode) && !this.continueOnError.isPresent()) {
                return;
            }
        }
    }
    
    private void deleteFromStandardInput(final AtomicReference<ResultCode> returnCode, final Charset charset, final char[] encryptionPassphrase) {
        final List<char[]> potentialPassphrases = new ArrayList<char[]>(1);
        if (encryptionPassphrase != null) {
            potentialPassphrases.add(encryptionPassphrase);
        }
        this.commentToOut(ToolMessages.INFO_LDAPDELETE_READING_FROM_STDIN.get());
        this.out(new Object[0]);
        try {
            this.deleteDNsFromInputStream(returnCode, this.in, charset, potentialPassphrases);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            returnCode.compareAndSet(null, ResultCode.LOCAL_ERROR);
            this.commentToErr(ToolMessages.ERR_LDAPDELETE_ERROR_READING_STDIN.get(StaticUtils.getExceptionMessage(e)));
        }
    }
    
    private boolean deleteEntry(final String dn, final AtomicReference<ResultCode> returnCode) {
        if (this.subtreeDeleter == null) {
            this.commentToOut(ToolMessages.INFO_LDAPDELETE_DELETING_ENTRY.get(dn));
        }
        else {
            this.commentToOut(ToolMessages.INFO_LDAPDELETE_CLIENT_SIDE_SUBTREE_DELETING.get(dn));
        }
        if (this.dryRun.isPresent()) {
            this.commentToOut(ToolMessages.INFO_LDAPDELETE_NOT_DELETING_BECAUSE_OF_DRY_RUN.get(dn));
            return true;
        }
        if (this.subtreeDeleter == null) {
            if (this.deleteRateLimiter != null) {
                this.deleteRateLimiter.await();
            }
            final DeleteRequest deleteRequest = new DeleteRequest(dn);
            deleteRequest.setControls(this.deleteControls);
            LDAPResult deleteResult;
            boolean successlful;
            try {
                if (this.verbose.isPresent()) {
                    this.commentToOut(ToolMessages.INFO_LDAPDELETE_SENDING_DELETE_REQUEST.get(String.valueOf(deleteRequest)));
                }
                deleteResult = this.connectionPool.delete(deleteRequest);
                successlful = true;
            }
            catch (final LDAPException e) {
                Debug.debugException(e);
                deleteResult = e.toLDAPResult();
                successlful = false;
            }
            for (final String resultLine : ResultUtils.formatResult(deleteResult, true, 0, LDAPDelete.WRAP_COLUMN)) {
                if (successlful) {
                    this.out(resultLine);
                }
                else {
                    this.err(resultLine);
                }
            }
            final ResultCode deleteResultCode = deleteResult.getResultCode();
            if (deleteResultCode != ResultCode.SUCCESS && deleteResultCode != ResultCode.NO_OPERATION) {
                returnCode.compareAndSet(null, deleteResultCode);
                this.writeToRejects(deleteRequest, deleteResult);
                this.err(new Object[0]);
                return false;
            }
            this.out(new Object[0]);
            return true;
        }
        else {
            SubtreeDeleterResult subtreeDeleterResult;
            try {
                subtreeDeleterResult = this.subtreeDeleter.delete(this.connectionPool, dn);
            }
            catch (final LDAPException e2) {
                Debug.debugException(e2);
                this.commentToErr(e2.getMessage());
                this.writeToRejects(new DeleteRequest(dn), e2.toLDAPResult());
                returnCode.compareAndSet(null, e2.getResultCode());
                return false;
            }
            if (!subtreeDeleterResult.completelySuccessful()) {
                this.commentToErr(ToolMessages.ERR_LDAPDELETE_CLIENT_SIDE_SUBTREE_DEL_FAILED.get());
                this.err(new Object[0]);
                final SearchResult searchError = subtreeDeleterResult.getSearchError();
                if (searchError != null) {
                    returnCode.compareAndSet(null, searchError.getResultCode());
                    this.commentToErr(ToolMessages.ERR_LDAPDELETE_CLIENT_SIDE_SUBTREE_DEL_SEARCH_ERROR.get(dn));
                    for (final String line : ResultUtils.formatResult(searchError, true, 0, LDAPDelete.WRAP_COLUMN)) {
                        this.err(line);
                    }
                    this.err(new Object[0]);
                }
                for (final Map.Entry<DN, LDAPResult> deleteError : subtreeDeleterResult.getDeleteErrorsDescendingMap().entrySet()) {
                    final String failureDN = deleteError.getKey().toString();
                    final LDAPResult failureResult = deleteError.getValue();
                    returnCode.compareAndSet(null, failureResult.getResultCode());
                    this.commentToErr(ToolMessages.ERR_LDAPDELETE_CLIENT_SIDE_SUBTREE_DEL_DEL_ERROR.get(failureDN, dn));
                    this.writeToRejects(new DeleteRequest(failureDN), failureResult);
                    for (final String line2 : ResultUtils.formatResult(failureResult, true, 0, LDAPDelete.WRAP_COLUMN)) {
                        this.err(line2);
                    }
                    this.err(new Object[0]);
                }
                return false;
            }
            final long entriesDeleted = subtreeDeleterResult.getEntriesDeleted();
            if (entriesDeleted == 0L) {
                final DeleteRequest deleteRequest2 = new DeleteRequest(dn);
                final LDAPResult result = new LDAPResult(-1, ResultCode.NO_SUCH_OBJECT, ToolMessages.ERR_LDAPDELETE_CLIENT_SIDE_SUBTREE_DEL_NO_BASE_ENTRY.get(dn), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
                for (final String line3 : ResultUtils.formatResult(result, true, 0, LDAPDelete.WRAP_COLUMN)) {
                    this.err(line3);
                }
                this.writeToRejects(deleteRequest2, result);
                returnCode.compareAndSet(null, ResultCode.NO_SUCH_OBJECT);
                this.err(new Object[0]);
                return false;
            }
            if (entriesDeleted == 1L) {
                this.commentToOut(ToolMessages.INFO_LDAPDELETE_CLIENT_SIDE_SUBTREE_DEL_ONLY_BASE.get(dn));
                this.out(new Object[0]);
                return true;
            }
            final long numSubordinates = entriesDeleted - 1L;
            this.commentToOut(ToolMessages.INFO_LDAPDELETE_CLIENT_SIDE_SUBTREE_DEL_WITH_SUBS.get(dn, numSubordinates));
            this.out(new Object[0]);
            return true;
        }
    }
    
    private void writeToRejects(final DeleteRequest deleteRequest, final LDAPResult deleteResult) {
        if (!this.rejectFile.isPresent()) {
            return;
        }
        LDIFWriter w;
        try {
            w = this.rejectWriter.get();
            if (w == null) {
                w = new LDIFWriter(this.rejectFile.getValue());
                this.rejectWriter.set(w);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.commentToErr(ToolMessages.ERR_LDAPDELETE_WRITE_TO_REJECTS_FAILED.get(StaticUtils.getExceptionMessage(e)));
            return;
        }
        try {
            boolean firstLine = true;
            for (final String commentLine : ResultUtils.formatResult(deleteResult, false, 0, LDAPDelete.WRAP_COLUMN - 2)) {
                w.writeComment(commentLine, firstLine, false);
                firstLine = false;
            }
            w.writeChangeRecord(deleteRequest.toLDIFChangeRecord());
            w.flush();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.commentToErr(ToolMessages.ERR_LDAPDELETE_WRITE_TO_REJECTS_FAILED.get(StaticUtils.getExceptionMessage(e)));
        }
    }
    
    private List<Control> getDeleteControls() {
        final List<Control> controlList = new ArrayList<Control>(10);
        if (this.deleteControl.isPresent()) {
            controlList.addAll(this.deleteControl.getValues());
        }
        controlList.addAll(this.routeToBackendSetRequestControls);
        if (this.serverSideSubtreeDelete.isPresent()) {
            controlList.add(new SubtreeDeleteRequestControl(true));
        }
        if (this.softDelete.isPresent()) {
            controlList.add(new SoftDeleteRequestControl(true, true));
        }
        if (this.hardDelete.isPresent() && !this.clientSideSubtreeDelete.isPresent()) {
            controlList.add(new HardDeleteRequestControl(true));
        }
        if (this.proxyAs.isPresent()) {
            controlList.add(new ProxiedAuthorizationV2RequestControl(this.proxyAs.getValue()));
        }
        if (this.proxyV1As.isPresent()) {
            controlList.add(new ProxiedAuthorizationV1RequestControl(this.proxyV1As.getValue().toString()));
        }
        if (this.manageDsaIT.isPresent() && !this.clientSideSubtreeDelete.isPresent()) {
            controlList.add(new ManageDsaITRequestControl(true));
        }
        if (this.assertionFilter.isPresent()) {
            controlList.add(new AssertionRequestControl(this.assertionFilter.getValue(), true));
        }
        if (this.preReadAttribute.isPresent()) {
            controlList.add(new PreReadRequestControl(true, (String[])this.preReadAttribute.getValues().toArray(StaticUtils.NO_STRINGS)));
        }
        if (this.noOperation.isPresent()) {
            controlList.add(new NoOpRequestControl());
        }
        if (this.getBackendSetID.isPresent()) {
            controlList.add(new GetBackendSetIDRequestControl(true));
        }
        if (this.getServerID.isPresent()) {
            controlList.add(new GetServerIDRequestControl(true));
        }
        if (this.routeToServer.isPresent()) {
            controlList.add(new RouteToServerRequestControl(true, this.routeToServer.getValue(), false, false, false));
        }
        if (this.useAssuredReplication.isPresent()) {
            AssuredReplicationLocalLevel localLevel = null;
            if (this.assuredReplicationLocalLevel.isPresent()) {
                final String level = this.assuredReplicationLocalLevel.getValue();
                if (level.equalsIgnoreCase("none")) {
                    localLevel = AssuredReplicationLocalLevel.NONE;
                }
                else if (level.equalsIgnoreCase("received-any-server")) {
                    localLevel = AssuredReplicationLocalLevel.RECEIVED_ANY_SERVER;
                }
                else if (level.equalsIgnoreCase("processed-all-servers")) {
                    localLevel = AssuredReplicationLocalLevel.PROCESSED_ALL_SERVERS;
                }
            }
            AssuredReplicationRemoteLevel remoteLevel = null;
            if (this.assuredReplicationRemoteLevel.isPresent()) {
                final String level2 = this.assuredReplicationRemoteLevel.getValue();
                if (level2.equalsIgnoreCase("none")) {
                    remoteLevel = AssuredReplicationRemoteLevel.NONE;
                }
                else if (level2.equalsIgnoreCase("received-any-remote-location")) {
                    remoteLevel = AssuredReplicationRemoteLevel.RECEIVED_ANY_REMOTE_LOCATION;
                }
                else if (level2.equalsIgnoreCase("received-all-remote-locations")) {
                    remoteLevel = AssuredReplicationRemoteLevel.RECEIVED_ALL_REMOTE_LOCATIONS;
                }
                else if (level2.equalsIgnoreCase("processed-all-remote-servers")) {
                    remoteLevel = AssuredReplicationRemoteLevel.PROCESSED_ALL_REMOTE_SERVERS;
                }
            }
            Long timeoutMillis = null;
            if (this.assuredReplicationTimeout.isPresent()) {
                timeoutMillis = this.assuredReplicationTimeout.getValue(TimeUnit.MILLISECONDS);
            }
            final AssuredReplicationRequestControl c = new AssuredReplicationRequestControl(true, localLevel, localLevel, remoteLevel, remoteLevel, timeoutMillis, false);
            controlList.add(c);
        }
        if (this.replicationRepair.isPresent()) {
            controlList.add(new ReplicationRepairRequestControl());
        }
        if (this.suppressReferentialIntegrityUpdates.isPresent()) {
            controlList.add(new SuppressReferentialIntegrityUpdatesRequestControl(true));
        }
        if (this.operationPurpose.isPresent()) {
            controlList.add(new OperationPurposeRequestControl(true, "ldapdelete", "4.0.14", LDAPDelete.class.getName() + ".getDeleteControls", this.operationPurpose.getValue()));
        }
        return Collections.unmodifiableList((List<? extends Control>)controlList);
    }
    
    private List<Control> getSearchControls() {
        final List<Control> controlList = new ArrayList<Control>(10);
        controlList.addAll(this.routeToBackendSetRequestControls);
        if (this.manageDsaIT.isPresent()) {
            controlList.add(new ManageDsaITRequestControl(true));
        }
        if (this.proxyV1As.isPresent()) {
            controlList.add(new ProxiedAuthorizationV1RequestControl(this.proxyV1As.getValue().toString()));
        }
        if (this.proxyAs.isPresent()) {
            controlList.add(new ProxiedAuthorizationV2RequestControl(this.proxyAs.getValue()));
        }
        if (this.operationPurpose.isPresent()) {
            controlList.add(new OperationPurposeRequestControl(true, "ldapdelete", "4.0.14", LDAPDelete.class.getName() + ".getSearchControls", this.operationPurpose.getValue()));
        }
        if (this.routeToServer.isPresent()) {
            controlList.add(new RouteToServerRequestControl(true, this.routeToServer.getValue(), false, false, false));
        }
        return Collections.unmodifiableList((List<? extends Control>)controlList);
    }
    
    @Override
    public void handleUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        final ArrayList<String> lines = new ArrayList<String>(10);
        ResultUtils.formatUnsolicitedNotification(lines, notification, true, 0, LDAPDelete.WRAP_COLUMN);
        for (final String line : lines) {
            this.err(line);
        }
        this.err(new Object[0]);
    }
    
    void commentToOut(final String message) {
        for (final String line : StaticUtils.wrapLine(message, LDAPDelete.WRAP_COLUMN - 2)) {
            this.out("# ", line);
        }
    }
    
    void commentToErr(final String message) {
        for (final String line : StaticUtils.wrapLine(message, LDAPDelete.WRAP_COLUMN - 2)) {
            this.err("# ", line);
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(4));
        examples.put(new String[] { "--hostname", "ds.example.com", "--port", "636", "--useSSL", "--bindDN", "uid=admin,dc=example,dc=com", "uid=test.user,ou=People,dc=example,dc=com" }, ToolMessages.INFO_LDAPDELETE_EXAMPLE_1.get());
        examples.put(new String[] { "--hostname", "ds.example.com", "--port", "636", "--useSSL", "--trustStorePath", "trust-store.jks", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPasswordFile", "admin-password.txt", "--dnFile", "dns-to-delete.txt" }, ToolMessages.INFO_LDAPDELETE_EXAMPLE_2.get());
        examples.put(new String[] { "--hostname", "ds.example.com", "--port", "389", "--useStartTLS", "--trustStorePath", "trust-store.jks", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPasswordFile", "admin-password.txt", "--deleteEntriesMatchingFilter", "(description=delete)" }, ToolMessages.INFO_LDAPDELETE_EXAMPLE_3.get());
        examples.put(new String[] { "--hostname", "ds.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com" }, ToolMessages.INFO_LDAPDELETE_EXAMPLE_4.get());
        return examples;
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
