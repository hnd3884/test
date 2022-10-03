package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.unboundidds.controls.PurgePasswordRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RetirePasswordRequestControl;
import com.unboundid.ldap.sdk.Modification;
import java.util.SortedMap;
import com.unboundid.util.SubtreeDeleterResult;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.util.SubtreeDeleter;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordValidationDetailsRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.UndeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordUpdateBehaviorRequestControlProperties;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordUpdateBehaviorRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.UniquenessRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.UniquenessValidationLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.UniquenessMultipleAttributeBehavior;
import com.unboundid.ldap.sdk.unboundidds.controls.UniquenessRequestControlProperties;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import java.util.StringTokenizer;
import com.unboundid.ldap.sdk.controls.ManageDsaITRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.ReplicationRepairRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.HardDeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationRequestControl;
import java.util.concurrent.TimeUnit;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationRemoteLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationLocalLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressReferentialIntegrityUpdatesRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToServerRequestControl;
import com.unboundid.ldap.sdk.controls.PermissiveModifyRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.NameWithEntryUUIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.IgnoreNoUserModificationRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetServerIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetBackendSetIDRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GeneratePasswordRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.NoOpRequestControl;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.HashSet;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateErrorBehavior;
import com.unboundid.ldap.sdk.extensions.EndTransactionExtendedRequest;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.util.DNFileReader;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.FilterFileReader;
import java.io.File;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldif.LDIFException;
import java.io.IOException;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldif.TrailingSpaceBehavior;
import com.unboundid.ldif.LDIFReaderChangeRecordTranslator;
import com.unboundid.ldif.LDIFReaderEntryTranslator;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.ldap.sdk.controls.TransactionSpecificationRequestControl;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedRequest;
import com.unboundid.ldap.sdk.extensions.StartTransactionExtendedResult;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV1RequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.LDAPConnectionPoolHealthCheck;
import com.unboundid.ldap.sdk.PostConnectProcessor;
import com.unboundid.util.CommandLineTool;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartAdministrativeSessionPostConnectProcessor;
import com.unboundid.ldap.sdk.unboundidds.extensions.StartAdministrativeSessionExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressOperationalAttributeUpdateRequestControl;
import java.util.EnumSet;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressType;
import com.unboundid.ldap.sdk.unboundidds.controls.PasswordPolicyRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetUserResourceLimitsRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.GetAuthorizationEntryRequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.unboundid.ldap.sdk.Control;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import com.unboundid.util.args.ArgumentException;
import java.util.Set;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import java.io.ByteArrayInputStream;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToBackendSetRequestControl;
import java.util.List;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DurationArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPModify extends LDAPCommandLineTool implements UnsolicitedNotificationHandler
{
    private static final int WRAP_COLUMN;
    private static final String ATTR_AUTH_PASSWORD = "authPassword";
    private static final String ATTR_UNDELETE_FROM_DN = "ds-undelete-from-dn";
    private static final String ATTR_USER_PASSWORD = "userPassword";
    private static final String ARG_ASSURED_REPLICATION_LOCAL_LEVEL = "assuredReplicationLocalLevel";
    private static final String ARG_ASSURED_REPLICATION_REMOTE_LEVEL = "assuredReplicationRemoteLevel";
    private static final String ARG_ASSURED_REPLICATION_TIMEOUT = "assuredReplicationTimeout";
    private static final String ARG_LDIF_FILE = "ldifFile";
    private static final String ARG_SEARCH_PAGE_SIZE = "searchPageSize";
    private BooleanArgument allowUndelete;
    private BooleanArgument assuredReplication;
    private BooleanArgument authorizationIdentity;
    private BooleanArgument clientSideSubtreeDelete;
    private BooleanArgument continueOnError;
    private BooleanArgument defaultAdd;
    private BooleanArgument dryRun;
    private BooleanArgument followReferrals;
    private BooleanArgument generatePassword;
    private BooleanArgument getBackendSetID;
    private BooleanArgument getServerID;
    private BooleanArgument getUserResourceLimits;
    private BooleanArgument hardDelete;
    private BooleanArgument ignoreNoUserModification;
    private BooleanArgument manageDsaIT;
    private BooleanArgument nameWithEntryUUID;
    private BooleanArgument noOperation;
    private BooleanArgument passwordValidationDetails;
    private BooleanArgument permissiveModify;
    private BooleanArgument purgeCurrentPassword;
    private BooleanArgument replicationRepair;
    private BooleanArgument retireCurrentPassword;
    private BooleanArgument retryFailedOperations;
    private BooleanArgument softDelete;
    private BooleanArgument stripTrailingSpaces;
    private BooleanArgument serverSideSubtreeDelete;
    private BooleanArgument suppressReferentialIntegrityUpdates;
    private BooleanArgument useAdministrativeSession;
    private BooleanArgument usePasswordPolicyControl;
    private BooleanArgument useTransaction;
    private BooleanArgument verbose;
    private ControlArgument addControl;
    private ControlArgument bindControl;
    private ControlArgument deleteControl;
    private ControlArgument modifyControl;
    private ControlArgument modifyDNControl;
    private ControlArgument operationControl;
    private DNArgument modifyEntryWithDN;
    private DNArgument proxyV1As;
    private DNArgument uniquenessBaseDN;
    private DurationArgument assuredReplicationTimeout;
    private FileArgument encryptionPassphraseFile;
    private FileArgument ldifFile;
    private FileArgument modifyEntriesMatchingFiltersFromFile;
    private FileArgument modifyEntriesWithDNsFromFile;
    private FileArgument rejectFile;
    private FilterArgument assertionFilter;
    private FilterArgument modifyEntriesMatchingFilter;
    private FilterArgument uniquenessFilter;
    private IntegerArgument ratePerSecond;
    private IntegerArgument searchPageSize;
    private StringArgument assuredReplicationLocalLevel;
    private StringArgument assuredReplicationRemoteLevel;
    private StringArgument characterSet;
    private StringArgument getAuthorizationEntryAttribute;
    private StringArgument multiUpdateErrorBehavior;
    private StringArgument operationPurpose;
    private StringArgument passwordUpdateBehavior;
    private StringArgument postReadAttribute;
    private StringArgument preReadAttribute;
    private StringArgument proxyAs;
    private StringArgument routeToBackendSet;
    private StringArgument routeToServer;
    private StringArgument suppressOperationalAttributeUpdates;
    private StringArgument uniquenessAttribute;
    private StringArgument uniquenessMultipleAttributeBehavior;
    private StringArgument uniquenessPostCommitValidationLevel;
    private StringArgument uniquenessPreCommitValidationLevel;
    private final AtomicBoolean rejectWritten;
    private final InputStream in;
    private final List<RouteToBackendSetRequestControl> routeToBackendSetRequestControls;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.in, System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(Math.min(resultCode.intValue(), 255));
        }
    }
    
    public static ResultCode main(final InputStream in, final OutputStream out, final OutputStream err, final String... args) {
        final LDAPModify tool = new LDAPModify(in, out, err);
        return tool.runTool(args);
    }
    
    public LDAPModify(final OutputStream out, final OutputStream err) {
        this(null, out, err);
    }
    
    public LDAPModify(final InputStream in, final OutputStream out, final OutputStream err) {
        super(out, err);
        this.allowUndelete = null;
        this.assuredReplication = null;
        this.authorizationIdentity = null;
        this.clientSideSubtreeDelete = null;
        this.continueOnError = null;
        this.defaultAdd = null;
        this.dryRun = null;
        this.followReferrals = null;
        this.generatePassword = null;
        this.getBackendSetID = null;
        this.getServerID = null;
        this.getUserResourceLimits = null;
        this.hardDelete = null;
        this.ignoreNoUserModification = null;
        this.manageDsaIT = null;
        this.nameWithEntryUUID = null;
        this.noOperation = null;
        this.passwordValidationDetails = null;
        this.permissiveModify = null;
        this.purgeCurrentPassword = null;
        this.replicationRepair = null;
        this.retireCurrentPassword = null;
        this.retryFailedOperations = null;
        this.softDelete = null;
        this.stripTrailingSpaces = null;
        this.serverSideSubtreeDelete = null;
        this.suppressReferentialIntegrityUpdates = null;
        this.useAdministrativeSession = null;
        this.usePasswordPolicyControl = null;
        this.useTransaction = null;
        this.verbose = null;
        this.addControl = null;
        this.bindControl = null;
        this.deleteControl = null;
        this.modifyControl = null;
        this.modifyDNControl = null;
        this.operationControl = null;
        this.modifyEntryWithDN = null;
        this.proxyV1As = null;
        this.uniquenessBaseDN = null;
        this.assuredReplicationTimeout = null;
        this.encryptionPassphraseFile = null;
        this.ldifFile = null;
        this.modifyEntriesMatchingFiltersFromFile = null;
        this.modifyEntriesWithDNsFromFile = null;
        this.rejectFile = null;
        this.assertionFilter = null;
        this.modifyEntriesMatchingFilter = null;
        this.uniquenessFilter = null;
        this.ratePerSecond = null;
        this.searchPageSize = null;
        this.assuredReplicationLocalLevel = null;
        this.assuredReplicationRemoteLevel = null;
        this.characterSet = null;
        this.getAuthorizationEntryAttribute = null;
        this.multiUpdateErrorBehavior = null;
        this.operationPurpose = null;
        this.passwordUpdateBehavior = null;
        this.postReadAttribute = null;
        this.preReadAttribute = null;
        this.proxyAs = null;
        this.routeToBackendSet = null;
        this.routeToServer = null;
        this.suppressOperationalAttributeUpdates = null;
        this.uniquenessAttribute = null;
        this.uniquenessMultipleAttributeBehavior = null;
        this.uniquenessPostCommitValidationLevel = null;
        this.uniquenessPreCommitValidationLevel = null;
        this.routeToBackendSetRequestControls = new ArrayList<RouteToBackendSetRequestControl>(10);
        if (in == null) {
            this.in = new ByteArrayInputStream(StaticUtils.NO_BYTES);
        }
        else {
            this.in = in;
        }
        this.rejectWritten = new AtomicBoolean(false);
    }
    
    @Override
    public String getToolName() {
        return "ldapmodify";
    }
    
    @Override
    public String getToolDescription() {
        return ToolMessages.INFO_LDAPMODIFY_TOOL_DESCRIPTION.get("ldifFile");
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
        (this.ldifFile = new FileArgument('f', "ldifFile", false, -1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_LDIF_FILE.get(), true, true, true, false)).addLongIdentifier("filename", true);
        this.ldifFile.addLongIdentifier("ldif-file", true);
        this.ldifFile.addLongIdentifier("file-name", true);
        this.ldifFile.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.ldifFile);
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ENCRYPTION_PW_FILE.get(), true, true, true, false)).addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        this.encryptionPassphraseFile.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.encryptionPassphraseFile);
        (this.characterSet = new StringArgument('i', "characterSet", false, 1, ToolMessages.INFO_LDAPMODIFY_PLACEHOLDER_CHARSET.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_CHARACTER_SET.get(), "UTF-8")).addLongIdentifier("encoding", true);
        this.characterSet.addLongIdentifier("character-set", true);
        this.characterSet.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.characterSet);
        (this.rejectFile = new FileArgument('R', "rejectFile", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_REJECT_FILE.get(), false, true, true, false)).addLongIdentifier("reject-file", true);
        this.rejectFile.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.rejectFile);
        (this.verbose = new BooleanArgument('v', "verbose", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_VERBOSE.get())).setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.verbose);
        (this.modifyEntriesMatchingFilter = new FilterArgument(null, "modifyEntriesMatchingFilter", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MODIFY_ENTRIES_MATCHING_FILTER.get("searchPageSize"))).addLongIdentifier("modify-entries-matching-filter", true);
        this.modifyEntriesMatchingFilter.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.modifyEntriesMatchingFilter);
        (this.modifyEntriesMatchingFiltersFromFile = new FileArgument(null, "modifyEntriesMatchingFiltersFromFile", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MODIFY_FILTER_FILE.get("searchPageSize"), true, false, true, false)).addLongIdentifier("modify-entries-matching-filters-from-file", true);
        this.modifyEntriesMatchingFiltersFromFile.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.modifyEntriesMatchingFiltersFromFile);
        (this.modifyEntryWithDN = new DNArgument(null, "modifyEntryWithDN", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MODIFY_ENTRY_DN.get())).addLongIdentifier("modify-entry-with-dn", true);
        this.modifyEntryWithDN.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.modifyEntryWithDN);
        (this.modifyEntriesWithDNsFromFile = new FileArgument(null, "modifyEntriesWithDNsFromFile", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MODIFY_DN_FILE.get(), true, false, true, false)).addLongIdentifier("modify-entries-with-dns-from-file", true);
        this.modifyEntriesWithDNsFromFile.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.modifyEntriesWithDNsFromFile);
        (this.searchPageSize = new IntegerArgument(null, "searchPageSize", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_SEARCH_PAGE_SIZE.get(this.modifyEntriesMatchingFilter.getIdentifierString(), this.modifyEntriesMatchingFiltersFromFile.getIdentifierString()), 1, Integer.MAX_VALUE)).addLongIdentifier("search-page-size", true);
        this.searchPageSize.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.searchPageSize);
        (this.retryFailedOperations = new BooleanArgument(null, "retryFailedOperations", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_RETRY_FAILED_OPERATIONS.get())).addLongIdentifier("retry-failed-operations", true);
        this.retryFailedOperations.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.retryFailedOperations);
        (this.dryRun = new BooleanArgument('n', "dryRun", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_DRY_RUN.get())).addLongIdentifier("dry-run", true);
        this.dryRun.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.dryRun);
        (this.defaultAdd = new BooleanArgument('a', "defaultAdd", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_DEFAULT_ADD.get())).addLongIdentifier("default-add", true);
        this.defaultAdd.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.defaultAdd);
        (this.continueOnError = new BooleanArgument('c', "continueOnError", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_CONTINUE_ON_ERROR.get())).addLongIdentifier("continue-on-error", true);
        this.continueOnError.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.continueOnError);
        (this.stripTrailingSpaces = new BooleanArgument(null, "stripTrailingSpaces", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_STRIP_TRAILING_SPACES.get())).addLongIdentifier("strip-trailing-spaces", true);
        this.stripTrailingSpaces.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        parser.addArgument(this.stripTrailingSpaces);
        (this.followReferrals = new BooleanArgument(null, "followReferrals", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_FOLLOW_REFERRALS.get())).addLongIdentifier("follow-referrals", true);
        this.followReferrals.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.followReferrals);
        (this.proxyAs = new StringArgument('Y', "proxyAs", false, 1, ToolMessages.INFO_PLACEHOLDER_AUTHZID.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PROXY_AS.get())).addLongIdentifier("proxyV2As", true);
        this.proxyAs.addLongIdentifier("proxy-as", true);
        this.proxyAs.addLongIdentifier("proxy-v2-as", true);
        this.proxyAs.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.proxyAs);
        (this.proxyV1As = new DNArgument(null, "proxyV1As", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PROXY_V1_AS.get())).addLongIdentifier("proxy-v1-as", true);
        this.proxyV1As.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.proxyV1As);
        (this.useAdministrativeSession = new BooleanArgument(null, "useAdministrativeSession", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_USE_ADMIN_SESSION.get())).addLongIdentifier("use-administrative-session", true);
        this.useAdministrativeSession.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.useAdministrativeSession);
        (this.operationPurpose = new StringArgument(null, "operationPurpose", false, 1, ToolMessages.INFO_PLACEHOLDER_PURPOSE.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_OPERATION_PURPOSE.get())).addLongIdentifier("operation-purpose", true);
        this.operationPurpose.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.operationPurpose);
        (this.manageDsaIT = new BooleanArgument(null, "useManageDsaIT", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MANAGE_DSA_IT.get())).addLongIdentifier("manageDsaIT", true);
        this.manageDsaIT.addLongIdentifier("use-manage-dsa-it", true);
        this.manageDsaIT.addLongIdentifier("manage-dsa-it", true);
        this.manageDsaIT.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.manageDsaIT);
        (this.useTransaction = new BooleanArgument(null, "useTransaction", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_USE_TRANSACTION.get())).addLongIdentifier("use-transaction", true);
        this.useTransaction.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.useTransaction);
        final Set<String> multiUpdateErrorBehaviorAllowedValues = StaticUtils.setOf("atomic", "abort-on-error", "continue-on-error");
        (this.multiUpdateErrorBehavior = new StringArgument(null, "multiUpdateErrorBehavior", false, 1, "{atomic|abort-on-error|continue-on-error}", ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MULTI_UPDATE_ERROR_BEHAVIOR.get(), multiUpdateErrorBehaviorAllowedValues)).addLongIdentifier("multi-update-error-behavior", true);
        this.multiUpdateErrorBehavior.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.multiUpdateErrorBehavior);
        (this.assertionFilter = new FilterArgument(null, "assertionFilter", false, 1, ToolMessages.INFO_PLACEHOLDER_FILTER.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ASSERTION_FILTER.get())).addLongIdentifier("assertion-filter", true);
        this.assertionFilter.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.assertionFilter);
        (this.authorizationIdentity = new BooleanArgument('E', "authorizationIdentity", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_AUTHZ_IDENTITY.get())).addLongIdentifier("reportAuthzID", true);
        this.authorizationIdentity.addLongIdentifier("authorization-identity", true);
        this.authorizationIdentity.addLongIdentifier("report-authzID", true);
        this.authorizationIdentity.addLongIdentifier("report-authz-id", true);
        this.authorizationIdentity.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.authorizationIdentity);
        (this.generatePassword = new BooleanArgument(null, "generatePassword", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_GENERATE_PASSWORD.get())).addLongIdentifier("generatePW", true);
        this.generatePassword.addLongIdentifier("generate-password", true);
        this.generatePassword.addLongIdentifier("generate-pw", true);
        this.generatePassword.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.generatePassword);
        (this.getAuthorizationEntryAttribute = new StringArgument(null, "getAuthorizationEntryAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_GET_AUTHZ_ENTRY_ATTR.get())).addLongIdentifier("get-authorization-entry-attribute", true);
        this.getAuthorizationEntryAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getAuthorizationEntryAttribute);
        (this.getBackendSetID = new BooleanArgument(null, "getBackendSetID", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_GET_BACKEND_SET_ID.get())).addLongIdentifier("get-backend-set-id", true);
        this.getBackendSetID.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getBackendSetID);
        (this.getServerID = new BooleanArgument(null, "getServerID", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_GET_SERVER_ID.get())).addLongIdentifier("get-server-id", true);
        this.getServerID.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getServerID);
        (this.getUserResourceLimits = new BooleanArgument(null, "getUserResourceLimits", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_GET_USER_RESOURCE_LIMITS.get())).addLongIdentifier("get-user-resource-limits", true);
        this.getUserResourceLimits.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.getUserResourceLimits);
        (this.ignoreNoUserModification = new BooleanArgument(null, "ignoreNoUserModification", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_IGNORE_NO_USER_MOD.get())).addLongIdentifier("ignore-no-user-modification", true);
        this.ignoreNoUserModification.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.ignoreNoUserModification);
        (this.preReadAttribute = new StringArgument(null, "preReadAttribute", false, -1, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PRE_READ_ATTRIBUTE.get())).addLongIdentifier("preReadAttributes", true);
        this.preReadAttribute.addLongIdentifier("pre-read-attribute", true);
        this.preReadAttribute.addLongIdentifier("pre-read-attributes", true);
        this.preReadAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.preReadAttribute);
        (this.postReadAttribute = new StringArgument(null, "postReadAttribute", false, -1, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_POST_READ_ATTRIBUTE.get())).addLongIdentifier("postReadAttributes", true);
        this.postReadAttribute.addLongIdentifier("post-read-attribute", true);
        this.postReadAttribute.addLongIdentifier("post-read-attributes", true);
        this.postReadAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.postReadAttribute);
        (this.routeToBackendSet = new StringArgument(null, "routeToBackendSet", false, 0, ToolMessages.INFO_LDAPMODIFY_ARG_PLACEHOLDER_ROUTE_TO_BACKEND_SET.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ROUTE_TO_BACKEND_SET.get())).addLongIdentifier("route-to-backend-set", true);
        this.routeToBackendSet.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.routeToBackendSet);
        (this.routeToServer = new StringArgument(null, "routeToServer", false, 1, ToolMessages.INFO_LDAPMODIFY_ARG_PLACEHOLDER_ROUTE_TO_SERVER.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ROUTE_TO_SERVER.get())).addLongIdentifier("route-to-server", true);
        this.routeToServer.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.routeToServer);
        (this.assuredReplication = new BooleanArgument(null, "useAssuredReplication", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ASSURED_REPLICATION.get("assuredReplicationLocalLevel", "assuredReplicationRemoteLevel", "assuredReplicationTimeout"))).addLongIdentifier("assuredReplication", true);
        this.assuredReplication.addLongIdentifier("use-assured-replication", true);
        this.assuredReplication.addLongIdentifier("assured-replication", true);
        this.assuredReplication.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.assuredReplication);
        final Set<String> assuredReplicationLocalLevelAllowedValues = StaticUtils.setOf("none", "received-any-server", "processed-all-servers");
        (this.assuredReplicationLocalLevel = new StringArgument(null, "assuredReplicationLocalLevel", false, 1, ToolMessages.INFO_PLACEHOLDER_LEVEL.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ASSURED_REPL_LOCAL_LEVEL.get(this.assuredReplication.getIdentifierString()), assuredReplicationLocalLevelAllowedValues)).addLongIdentifier("assured-replication-local-level", true);
        this.assuredReplicationLocalLevel.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.assuredReplicationLocalLevel);
        final Set<String> assuredReplicationRemoteLevelAllowedValues = StaticUtils.setOf("none", "received-any-remote-location", "received-all-remote-locations", "processed-all-remote-servers");
        (this.assuredReplicationRemoteLevel = new StringArgument(null, "assuredReplicationRemoteLevel", false, 1, ToolMessages.INFO_PLACEHOLDER_LEVEL.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ASSURED_REPL_REMOTE_LEVEL.get(this.assuredReplication.getIdentifierString()), assuredReplicationRemoteLevelAllowedValues)).addLongIdentifier("assured-replication-remote-level", true);
        this.assuredReplicationRemoteLevel.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.assuredReplicationRemoteLevel);
        (this.assuredReplicationTimeout = new DurationArgument(null, "assuredReplicationTimeout", false, ToolMessages.INFO_PLACEHOLDER_TIMEOUT.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ASSURED_REPL_TIMEOUT.get(this.assuredReplication.getIdentifierString()))).setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.assuredReplicationTimeout);
        (this.replicationRepair = new BooleanArgument(null, "replicationRepair", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_REPLICATION_REPAIR.get())).addLongIdentifier("replication-repair", true);
        this.replicationRepair.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.replicationRepair);
        (this.nameWithEntryUUID = new BooleanArgument(null, "nameWithEntryUUID", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_NAME_WITH_ENTRY_UUID.get())).addLongIdentifier("name-with-entryUUID", true);
        this.nameWithEntryUUID.addLongIdentifier("name-with-entry-uuid", true);
        this.nameWithEntryUUID.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.nameWithEntryUUID);
        (this.noOperation = new BooleanArgument(null, "noOperation", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_NO_OPERATION.get())).addLongIdentifier("noOp", true);
        this.noOperation.addLongIdentifier("no-operation", true);
        this.noOperation.addLongIdentifier("no-op", true);
        this.noOperation.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.noOperation);
        (this.passwordUpdateBehavior = new StringArgument(null, "passwordUpdateBehavior", false, 0, ToolMessages.INFO_LDAPMODIFY_PLACEHOLDER_NAME_EQUALS_VALUE.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PW_UPDATE_BEHAVIOR.get())).addLongIdentifier("password-update-behavior", true);
        this.passwordUpdateBehavior.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.passwordUpdateBehavior);
        (this.passwordValidationDetails = new BooleanArgument(null, "getPasswordValidationDetails", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PASSWORD_VALIDATION_DETAILS.get("userPassword", "authPassword"))).addLongIdentifier("passwordValidationDetails", true);
        this.passwordValidationDetails.addLongIdentifier("get-password-validation-details", true);
        this.passwordValidationDetails.addLongIdentifier("password-validation-details", true);
        this.passwordValidationDetails.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.passwordValidationDetails);
        (this.permissiveModify = new BooleanArgument(null, "permissiveModify", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PERMISSIVE_MODIFY.get())).addLongIdentifier("permissive-modify", true);
        this.permissiveModify.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.permissiveModify);
        (this.clientSideSubtreeDelete = new BooleanArgument(null, "clientSideSubtreeDelete", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_CLIENT_SIDE_SUBTREE_DELETE.get())).addLongIdentifier("client-side-subtree-delete", true);
        this.clientSideSubtreeDelete.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.clientSideSubtreeDelete);
        (this.serverSideSubtreeDelete = new BooleanArgument(null, "serverSideSubtreeDelete", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_SERVER_SIDE_SUBTREE_DELETE.get())).addLongIdentifier("server-side-subtree-delete", true);
        this.serverSideSubtreeDelete.addLongIdentifier("subtreeDelete", true);
        this.serverSideSubtreeDelete.addLongIdentifier("subtree-delete", true);
        this.serverSideSubtreeDelete.addLongIdentifier("subtreeDeleteControl", true);
        this.serverSideSubtreeDelete.addLongIdentifier("subtree-delete-control", true);
        this.serverSideSubtreeDelete.addLongIdentifier("useSubtreeDeleteControl", true);
        this.serverSideSubtreeDelete.addLongIdentifier("use-subtree-delete-control", true);
        this.serverSideSubtreeDelete.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.serverSideSubtreeDelete);
        (this.softDelete = new BooleanArgument('s', "softDelete", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_SOFT_DELETE.get())).addLongIdentifier("useSoftDelete", true);
        this.softDelete.addLongIdentifier("soft-delete", true);
        this.softDelete.addLongIdentifier("use-soft-delete", true);
        this.softDelete.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.softDelete);
        (this.hardDelete = new BooleanArgument(null, "hardDelete", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_HARD_DELETE.get())).addLongIdentifier("hard-delete", true);
        this.hardDelete.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.hardDelete);
        (this.allowUndelete = new BooleanArgument(null, "allowUndelete", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ALLOW_UNDELETE.get("ds-undelete-from-dn"))).addLongIdentifier("allow-undelete", true);
        this.allowUndelete.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.allowUndelete);
        (this.retireCurrentPassword = new BooleanArgument(null, "retireCurrentPassword", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_RETIRE_CURRENT_PASSWORD.get("userPassword", "authPassword"))).addLongIdentifier("retire-current-password", true);
        this.retireCurrentPassword.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.retireCurrentPassword);
        (this.purgeCurrentPassword = new BooleanArgument(null, "purgeCurrentPassword", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PURGE_CURRENT_PASSWORD.get("userPassword", "authPassword"))).addLongIdentifier("purge-current-password", true);
        this.purgeCurrentPassword.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.purgeCurrentPassword);
        final Set<String> suppressOperationalAttributeUpdatesAllowedValues = StaticUtils.setOf("last-access-time", "last-login-time", "last-login-ip", "lastmod");
        (this.suppressOperationalAttributeUpdates = new StringArgument(null, "suppressOperationalAttributeUpdates", false, -1, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_SUPPRESS_OP_ATTR_UPDATES.get(), suppressOperationalAttributeUpdatesAllowedValues)).addLongIdentifier("suppress-operational-attribute-updates", true);
        this.suppressOperationalAttributeUpdates.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.suppressOperationalAttributeUpdates);
        (this.suppressReferentialIntegrityUpdates = new BooleanArgument(null, "suppressReferentialIntegrityUpdates", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_SUPPRESS_REFERINT_UPDATES.get())).addLongIdentifier("suppress-referential-integrity-updates", true);
        this.suppressReferentialIntegrityUpdates.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.suppressReferentialIntegrityUpdates);
        (this.usePasswordPolicyControl = new BooleanArgument(null, "usePasswordPolicyControl", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_PASSWORD_POLICY.get())).addLongIdentifier("use-password-policy-control", true);
        this.usePasswordPolicyControl.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.usePasswordPolicyControl);
        (this.uniquenessAttribute = new StringArgument(null, "uniquenessAttribute", false, 0, ToolMessages.INFO_PLACEHOLDER_ATTR.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_UNIQUE_ATTR.get())).addLongIdentifier("uniquenessAttributeType", true);
        this.uniquenessAttribute.addLongIdentifier("uniqueAttribute", true);
        this.uniquenessAttribute.addLongIdentifier("uniqueAttributeType", true);
        this.uniquenessAttribute.addLongIdentifier("uniqueness-attribute", true);
        this.uniquenessAttribute.addLongIdentifier("uniqueness-attribute-type", true);
        this.uniquenessAttribute.addLongIdentifier("unique-attribute", true);
        this.uniquenessAttribute.addLongIdentifier("unique-attribute-type", true);
        this.uniquenessAttribute.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.uniquenessAttribute);
        (this.uniquenessFilter = new FilterArgument(null, "uniquenessFilter", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_UNIQUE_FILTER.get())).addLongIdentifier("uniqueness-filter", true);
        this.uniquenessFilter.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.uniquenessFilter);
        (this.uniquenessBaseDN = new DNArgument(null, "uniquenessBaseDN", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_UNIQUE_BASE_DN.get())).addLongIdentifier("uniqueness-base-dn", true);
        this.uniquenessBaseDN.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.uniquenessBaseDN);
        parser.addDependentArgumentSet(this.uniquenessBaseDN, this.uniquenessAttribute, this.uniquenessFilter);
        final Set<String> mabValues = StaticUtils.setOf("unique-within-each-attribute", "unique-across-all-attributes-including-in-same-entry", "unique-across-all-attributes-except-in-same-entry", "unique-in-combination");
        (this.uniquenessMultipleAttributeBehavior = new StringArgument(null, "uniquenessMultipleAttributeBehavior", false, 1, ToolMessages.INFO_LDAPMODIFY_PLACEHOLDER_BEHAVIOR.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_UNIQUE_MULTIPLE_ATTRIBUTE_BEHAVIOR.get(), mabValues)).addLongIdentifier("uniqueness-multiple-attribute-behavior", true);
        this.uniquenessMultipleAttributeBehavior.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.uniquenessMultipleAttributeBehavior);
        parser.addDependentArgumentSet(this.uniquenessMultipleAttributeBehavior, this.uniquenessAttribute, new Argument[0]);
        final Set<String> vlValues = StaticUtils.setOf("none", "all-subtree-views", "all-backend-sets", "all-available-backend-servers");
        (this.uniquenessPreCommitValidationLevel = new StringArgument(null, "uniquenessPreCommitValidationLevel", false, 1, ToolMessages.INFO_LDAPMODIFY_PLACEHOLDER_LEVEL.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_UNIQUE_PRE_COMMIT_LEVEL.get(), vlValues)).addLongIdentifier("uniqueness-pre-commit-validation-level", true);
        this.uniquenessPreCommitValidationLevel.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.uniquenessPreCommitValidationLevel);
        parser.addDependentArgumentSet(this.uniquenessPreCommitValidationLevel, this.uniquenessAttribute, this.uniquenessFilter);
        (this.uniquenessPostCommitValidationLevel = new StringArgument(null, "uniquenessPostCommitValidationLevel", false, 1, ToolMessages.INFO_LDAPMODIFY_PLACEHOLDER_LEVEL.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_UNIQUE_POST_COMMIT_LEVEL.get(), vlValues)).addLongIdentifier("uniqueness-post-commit-validation-level", true);
        this.uniquenessPostCommitValidationLevel.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.uniquenessPostCommitValidationLevel);
        parser.addDependentArgumentSet(this.uniquenessPostCommitValidationLevel, this.uniquenessAttribute, this.uniquenessFilter);
        (this.operationControl = new ControlArgument('J', "control", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_OP_CONTROL.get())).setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.operationControl);
        (this.addControl = new ControlArgument(null, "addControl", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_ADD_CONTROL.get())).addLongIdentifier("add-control", true);
        this.addControl.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.addControl);
        (this.bindControl = new ControlArgument(null, "bindControl", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_BIND_CONTROL.get())).addLongIdentifier("bind-control", true);
        this.bindControl.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.bindControl);
        (this.deleteControl = new ControlArgument(null, "deleteControl", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_DELETE_CONTROL.get())).addLongIdentifier("delete-control", true);
        this.deleteControl.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.deleteControl);
        (this.modifyControl = new ControlArgument(null, "modifyControl", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MODIFY_CONTROL.get())).addLongIdentifier("modify-control", true);
        this.modifyControl.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.modifyControl);
        (this.modifyDNControl = new ControlArgument(null, "modifyDNControl", false, 0, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_MODIFY_DN_CONTROL.get())).addLongIdentifier("modify-dn-control", true);
        this.modifyDNControl.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_CONTROLS.get());
        parser.addArgument(this.modifyDNControl);
        (this.ratePerSecond = new IntegerArgument('r', "ratePerSecond", false, 1, ToolMessages.INFO_PLACEHOLDER_NUM.get(), ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_RATE_PER_SECOND.get(), 1, Integer.MAX_VALUE)).addLongIdentifier("rate-per-second", true);
        this.ratePerSecond.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_OPS.get());
        parser.addArgument(this.ratePerSecond);
        final BooleanArgument scriptFriendly = new BooleanArgument(null, "scriptFriendly", 1, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_SCRIPT_FRIENDLY.get());
        scriptFriendly.addLongIdentifier("script-friendly", true);
        scriptFriendly.setArgumentGroupName(ToolMessages.INFO_LDAPMODIFY_ARG_GROUP_DATA.get());
        scriptFriendly.setHidden(true);
        parser.addArgument(scriptFriendly);
        final IntegerArgument ldapVersion = new IntegerArgument('V', "ldapVersion", false, 1, null, ToolMessages.INFO_LDAPMODIFY_ARG_DESCRIPTION_LDAP_VERSION.get());
        ldapVersion.addLongIdentifier("ldap-version", true);
        ldapVersion.setHidden(true);
        parser.addArgument(ldapVersion);
        parser.addDependentArgumentSet(this.assuredReplicationLocalLevel, this.assuredReplication, new Argument[0]);
        parser.addDependentArgumentSet(this.assuredReplicationRemoteLevel, this.assuredReplication, new Argument[0]);
        parser.addDependentArgumentSet(this.assuredReplicationTimeout, this.assuredReplication, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.multiUpdateErrorBehavior, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.rejectFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.retryFailedOperations, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.continueOnError, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.followReferrals, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.nameWithEntryUUID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.noOperation, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.modifyEntriesMatchingFilter, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.modifyEntriesMatchingFiltersFromFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.modifyEntryWithDN, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.modifyEntriesWithDNsFromFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.useTransaction, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.ratePerSecond, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.rejectFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.retryFailedOperations, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.continueOnError, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.followReferrals, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.nameWithEntryUUID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.noOperation, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.modifyEntriesMatchingFilter, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.modifyEntriesMatchingFiltersFromFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.modifyEntryWithDN, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.modifyEntriesWithDNsFromFile, new Argument[0]);
        parser.addExclusiveArgumentSet(this.multiUpdateErrorBehavior, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.softDelete, this.hardDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.softDelete, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.softDelete, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.followReferrals, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.preReadAttribute, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.getBackendSetID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.getServerID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.noOperation, new Argument[0]);
        parser.addExclusiveArgumentSet(this.clientSideSubtreeDelete, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.retireCurrentPassword, this.purgeCurrentPassword, new Argument[0]);
        parser.addExclusiveArgumentSet(this.followReferrals, this.manageDsaIT, new Argument[0]);
        parser.addExclusiveArgumentSet(this.proxyAs, this.proxyV1As, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.allowUndelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.defaultAdd, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.hardDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.ignoreNoUserModification, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.nameWithEntryUUID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.softDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.suppressReferentialIntegrityUpdates, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.addControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.deleteControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFilter, this.modifyDNControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.allowUndelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.defaultAdd, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.hardDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.ignoreNoUserModification, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.nameWithEntryUUID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.softDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.suppressReferentialIntegrityUpdates, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.addControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.deleteControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesMatchingFiltersFromFile, this.modifyDNControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.allowUndelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.defaultAdd, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.hardDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.ignoreNoUserModification, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.nameWithEntryUUID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.softDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.suppressReferentialIntegrityUpdates, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.addControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.deleteControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntryWithDN, this.modifyDNControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.allowUndelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.defaultAdd, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.dryRun, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.hardDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.ignoreNoUserModification, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.nameWithEntryUUID, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.softDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.clientSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.serverSideSubtreeDelete, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.suppressReferentialIntegrityUpdates, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.addControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.deleteControl, new Argument[0]);
        parser.addExclusiveArgumentSet(this.modifyEntriesWithDNsFromFile, this.modifyDNControl, new Argument[0]);
    }
    
    @Override
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
        if (this.routeToBackendSet.isPresent()) {
            final List<String> values = this.routeToBackendSet.getValues();
            final Map<String, List<String>> idsByRP = new LinkedHashMap<String, List<String>>(StaticUtils.computeMapCapacity(values.size()));
            for (final String value : values) {
                final int colonPos = value.indexOf(58);
                if (colonPos <= 0) {
                    throw new ArgumentException(ToolMessages.ERR_LDAPMODIFY_ROUTE_TO_BACKEND_SET_INVALID_FORMAT.get(value, this.routeToBackendSet.getIdentifierString()));
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
        final ArrayList<Control> addControls = new ArrayList<Control>(10);
        final ArrayList<Control> deleteControls = new ArrayList<Control>(10);
        final ArrayList<Control> modifyControls = new ArrayList<Control>(10);
        final ArrayList<Control> modifyDNControls = new ArrayList<Control>(10);
        final ArrayList<Control> searchControls = new ArrayList<Control>(10);
        try {
            this.createRequestControls(addControls, deleteControls, modifyControls, modifyDNControls, searchControls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            for (final String line : ResultUtils.formatResult(le, true, 0, LDAPModify.WRAP_COLUMN)) {
                this.err(line);
            }
            return le.getResultCode();
        }
        String encryptionPassphrase = null;
        if (this.encryptionPassphraseFile.isPresent()) {
            try {
                encryptionPassphrase = ToolUtils.readEncryptionPassphraseFromFile(this.encryptionPassphraseFile.getValue());
            }
            catch (final LDAPException e) {
                Debug.debugException(e);
                this.wrapErr(0, LDAPModify.WRAP_COLUMN, e.getMessage());
                return e.getResultCode();
            }
        }
        LDAPConnectionPool connectionPool = null;
        LDIFReader ldifReader = null;
        LDIFWriter rejectWriter = null;
        try {
            try {
                StartAdministrativeSessionPostConnectProcessor p;
                if (this.useAdministrativeSession.isPresent()) {
                    p = new StartAdministrativeSessionPostConnectProcessor(new StartAdministrativeSessionExtendedRequest(this.getToolName(), true, new Control[0]));
                }
                else {
                    p = null;
                }
                if (!this.dryRun.isPresent()) {
                    connectionPool = this.getConnectionPool(1, 2, 0, p, null, true, new ReportBindResultLDAPConnectionPoolHealthCheck(this, true, this.verbose.isPresent()));
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                if (le2.getResultCode() != ResultCode.INVALID_CREDENTIALS) {
                    for (final String line2 : ResultUtils.formatResult(le2, true, 0, LDAPModify.WRAP_COLUMN)) {
                        this.err(line2);
                    }
                }
                return le2.getResultCode();
            }
            if (connectionPool != null && this.retryFailedOperations.isPresent()) {
                connectionPool.setRetryFailedOperationsDueToInvalidConnections(true);
            }
            if (connectionPool != null) {
                try {
                    final LDAPConnection connection = connectionPool.getConnection();
                    final String hostPort = connection.getHostPort();
                    connectionPool.releaseConnection(connection);
                    this.commentToOut(ToolMessages.INFO_LDAPMODIFY_CONNECTION_ESTABLISHED.get(hostPort));
                    this.out(new Object[0]);
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                }
            }
            ASN1OctetString txnID;
            if (this.useTransaction.isPresent()) {
                Control[] startTxnControls;
                if (this.proxyAs.isPresent()) {
                    startTxnControls = new Control[] { new ProxiedAuthorizationV2RequestControl(this.proxyAs.getValue()) };
                }
                else if (this.proxyV1As.isPresent()) {
                    startTxnControls = new Control[] { new ProxiedAuthorizationV1RequestControl(this.proxyV1As.getValue()) };
                }
                else {
                    startTxnControls = StaticUtils.NO_CONTROLS;
                }
                try {
                    final StartTransactionExtendedResult startTxnResult = (StartTransactionExtendedResult)connectionPool.processExtendedOperation(new StartTransactionExtendedRequest(startTxnControls));
                    if (startTxnResult.getResultCode() != ResultCode.SUCCESS) {
                        this.commentToErr(ToolMessages.ERR_LDAPMODIFY_CANNOT_START_TXN.get(startTxnResult.getResultString()));
                        return startTxnResult.getResultCode();
                    }
                    txnID = startTxnResult.getTransactionID();
                    final TransactionSpecificationRequestControl c = new TransactionSpecificationRequestControl(txnID);
                    addControls.add(c);
                    deleteControls.add(c);
                    modifyControls.add(c);
                    modifyDNControls.add(c);
                    String txnIDString;
                    if (StaticUtils.isPrintableString(txnID.getValue())) {
                        txnIDString = txnID.stringValue();
                    }
                    else {
                        final StringBuilder hexBuffer = new StringBuilder();
                        StaticUtils.toHex(txnID.getValue(), ":", hexBuffer);
                        txnIDString = hexBuffer.toString();
                    }
                    this.commentToOut(ToolMessages.INFO_LDAPMODIFY_STARTED_TXN.get(txnIDString));
                }
                catch (final LDAPException le3) {
                    Debug.debugException(le3);
                    this.commentToErr(ToolMessages.ERR_LDAPMODIFY_CANNOT_START_TXN.get(StaticUtils.getExceptionMessage(le3)));
                    return le3.getResultCode();
                }
            }
            else {
                txnID = null;
            }
            try {
                InputStream ldifInputStream;
                if (this.ldifFile.isPresent()) {
                    ldifInputStream = ToolUtils.getInputStreamForLDIFFiles(this.ldifFile.getValues(), encryptionPassphrase, this.getOut(), this.getErr()).getFirst();
                }
                else {
                    ldifInputStream = this.in;
                }
                ldifReader = new LDIFReader(ldifInputStream, 0, null, null, this.characterSet.getValue());
            }
            catch (final Exception e2) {
                this.commentToErr(ToolMessages.ERR_LDAPMODIFY_CANNOT_CREATE_LDIF_READER.get(StaticUtils.getExceptionMessage(e2)));
                return ResultCode.LOCAL_ERROR;
            }
            if (this.stripTrailingSpaces.isPresent()) {
                ldifReader.setTrailingSpaceBehavior(TrailingSpaceBehavior.STRIP);
            }
            if (this.rejectFile.isPresent()) {
                try {
                    rejectWriter = new LDIFWriter(this.rejectFile.getValue());
                    rejectWriter.setWrapColumn(Integer.MAX_VALUE);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    this.commentToErr(ToolMessages.ERR_LDAPMODIFY_CANNOT_CREATE_REJECT_WRITER.get(this.rejectFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e2)));
                    return ResultCode.LOCAL_ERROR;
                }
            }
            FixedRateBarrier rateLimiter;
            if (this.ratePerSecond.isPresent()) {
                rateLimiter = new FixedRateBarrier(1000L, this.ratePerSecond.getValue());
            }
            else {
                rateLimiter = null;
            }
            boolean commitTransaction = true;
            ResultCode resultCode = null;
            final ArrayList<LDAPRequest> multiUpdateRequests = new ArrayList<LDAPRequest>(10);
            final boolean isBulkModify = this.modifyEntriesMatchingFilter.isPresent() || this.modifyEntriesMatchingFiltersFromFile.isPresent() || this.modifyEntryWithDN.isPresent() || this.modifyEntriesWithDNsFromFile.isPresent();
            while (true) {
                if (rateLimiter != null && !isBulkModify) {
                    rateLimiter.await();
                }
                LDIFChangeRecord changeRecord;
                try {
                    changeRecord = ldifReader.readChangeRecord(this.defaultAdd.isPresent());
                }
                catch (final IOException ioe) {
                    Debug.debugException(ioe);
                    final String message = ToolMessages.ERR_LDAPMODIFY_IO_ERROR_READING_CHANGE.get(StaticUtils.getExceptionMessage(ioe));
                    this.commentToErr(message);
                    this.writeRejectedChange(rejectWriter, message, null);
                    commitTransaction = false;
                    resultCode = ResultCode.LOCAL_ERROR;
                    break;
                }
                catch (final LDIFException le4) {
                    Debug.debugException(le4);
                    final StringBuilder buffer = new StringBuilder();
                    if (le4.mayContinueReading() && !this.useTransaction.isPresent()) {
                        buffer.append(ToolMessages.ERR_LDAPMODIFY_RECOVERABLE_LDIF_ERROR_READING_CHANGE.get(le4.getLineNumber(), StaticUtils.getExceptionMessage(le4)));
                    }
                    else {
                        buffer.append(ToolMessages.ERR_LDAPMODIFY_UNRECOVERABLE_LDIF_ERROR_READING_CHANGE.get(le4.getLineNumber(), StaticUtils.getExceptionMessage(le4)));
                    }
                    if (resultCode == null || resultCode == ResultCode.SUCCESS) {
                        resultCode = ResultCode.LOCAL_ERROR;
                    }
                    if (le4.getDataLines() != null && !le4.getDataLines().isEmpty()) {
                        buffer.append(StaticUtils.EOL);
                        buffer.append(StaticUtils.EOL);
                        buffer.append(ToolMessages.ERR_LDAPMODIFY_INVALID_LINES.get());
                        buffer.append(StaticUtils.EOL);
                        for (final String s : le4.getDataLines()) {
                            buffer.append(s);
                            buffer.append(StaticUtils.EOL);
                        }
                    }
                    final String message2 = buffer.toString();
                    this.commentToErr(message2);
                    this.writeRejectedChange(rejectWriter, message2, null);
                    if (le4.mayContinueReading() && !this.useTransaction.isPresent()) {
                        continue;
                    }
                    commitTransaction = false;
                    resultCode = ResultCode.LOCAL_ERROR;
                    break;
                }
                if (changeRecord == null) {
                    break;
                }
                if (this.modifyEntriesMatchingFilter.isPresent()) {
                    for (final Filter filter : this.modifyEntriesMatchingFilter.getValues()) {
                        final ResultCode rc = this.handleModifyMatchingFilter(connectionPool, changeRecord, this.modifyEntriesMatchingFilter.getIdentifierString(), filter, searchControls, modifyControls, rateLimiter, rejectWriter);
                        if (rc != ResultCode.SUCCESS && (resultCode == null || resultCode == ResultCode.SUCCESS || resultCode == ResultCode.NO_OPERATION)) {
                            resultCode = rc;
                        }
                    }
                }
                if (this.modifyEntriesMatchingFiltersFromFile.isPresent()) {
                    for (final File f : this.modifyEntriesMatchingFiltersFromFile.getValues()) {
                        Label_2039: {
                            FilterFileReader filterReader;
                            try {
                                filterReader = new FilterFileReader(f);
                                break Label_2039;
                            }
                            catch (final Exception e3) {
                                Debug.debugException(e3);
                                this.commentToErr(ToolMessages.ERR_LDAPMODIFY_ERROR_OPENING_FILTER_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e3)));
                                return ResultCode.LOCAL_ERROR;
                            }
                            try {
                                while (true) {
                                    Filter filter2;
                                    try {
                                        filter2 = filterReader.readFilter();
                                    }
                                    catch (final IOException ioe2) {
                                        Debug.debugException(ioe2);
                                        this.commentToErr(ToolMessages.ERR_LDAPMODIFY_IO_ERROR_READING_FILTER_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(ioe2)));
                                        return ResultCode.LOCAL_ERROR;
                                    }
                                    catch (final LDAPException le5) {
                                        Debug.debugException(le5);
                                        this.commentToErr(le5.getMessage());
                                        if (this.continueOnError.isPresent()) {
                                            if (resultCode != null && resultCode != ResultCode.SUCCESS && resultCode != ResultCode.NO_OPERATION) {
                                                continue;
                                            }
                                            resultCode = le5.getResultCode();
                                            continue;
                                        }
                                        return le5.getResultCode();
                                    }
                                    if (filter2 == null) {
                                        break;
                                    }
                                    final ResultCode rc2 = this.handleModifyMatchingFilter(connectionPool, changeRecord, this.modifyEntriesMatchingFiltersFromFile.getIdentifierString(), filter2, searchControls, modifyControls, rateLimiter, rejectWriter);
                                    if (rc2 == ResultCode.SUCCESS || (resultCode != null && resultCode != ResultCode.SUCCESS && resultCode != ResultCode.NO_OPERATION)) {
                                        continue;
                                    }
                                    resultCode = rc2;
                                }
                            }
                            finally {
                                try {
                                    filterReader.close();
                                }
                                catch (final Exception e4) {
                                    Debug.debugException(e4);
                                }
                            }
                        }
                    }
                }
                if (this.modifyEntryWithDN.isPresent()) {
                    for (final DN dn : this.modifyEntryWithDN.getValues()) {
                        final ResultCode rc = this.handleModifyWithDN(connectionPool, changeRecord, this.modifyEntryWithDN.getIdentifierString(), dn, modifyControls, rateLimiter, rejectWriter);
                        if (rc != ResultCode.SUCCESS && (resultCode == null || resultCode == ResultCode.SUCCESS || resultCode == ResultCode.NO_OPERATION)) {
                            resultCode = rc;
                        }
                    }
                }
                if (this.modifyEntriesWithDNsFromFile.isPresent()) {
                    for (final File f : this.modifyEntriesWithDNsFromFile.getValues()) {
                        Label_2693: {
                            DNFileReader dnReader;
                            try {
                                dnReader = new DNFileReader(f);
                                break Label_2693;
                            }
                            catch (final Exception e3) {
                                Debug.debugException(e3);
                                this.commentToErr(ToolMessages.ERR_LDAPMODIFY_ERROR_OPENING_DN_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e3)));
                                return ResultCode.LOCAL_ERROR;
                            }
                            try {
                                while (true) {
                                    DN dn2;
                                    try {
                                        dn2 = dnReader.readDN();
                                    }
                                    catch (final IOException ioe2) {
                                        Debug.debugException(ioe2);
                                        this.commentToErr(ToolMessages.ERR_LDAPMODIFY_IO_ERROR_READING_DN_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(ioe2)));
                                        return ResultCode.LOCAL_ERROR;
                                    }
                                    catch (final LDAPException le5) {
                                        Debug.debugException(le5);
                                        this.commentToErr(le5.getMessage());
                                        if (this.continueOnError.isPresent()) {
                                            if (resultCode != null && resultCode != ResultCode.SUCCESS && resultCode != ResultCode.NO_OPERATION) {
                                                continue;
                                            }
                                            resultCode = le5.getResultCode();
                                            continue;
                                        }
                                        return le5.getResultCode();
                                    }
                                    if (dn2 == null) {
                                        break;
                                    }
                                    final ResultCode rc2 = this.handleModifyWithDN(connectionPool, changeRecord, this.modifyEntriesWithDNsFromFile.getIdentifierString(), dn2, modifyControls, rateLimiter, rejectWriter);
                                    if (rc2 == ResultCode.SUCCESS || (resultCode != null && resultCode != ResultCode.SUCCESS && resultCode != ResultCode.NO_OPERATION)) {
                                        continue;
                                    }
                                    resultCode = rc2;
                                }
                            }
                            finally {
                                try {
                                    dnReader.close();
                                }
                                catch (final Exception e5) {
                                    Debug.debugException(e5);
                                }
                            }
                        }
                    }
                }
                if (isBulkModify) {
                    continue;
                }
                try {
                    ResultCode rc3;
                    if (changeRecord instanceof LDIFAddChangeRecord) {
                        rc3 = this.doAdd((LDIFAddChangeRecord)changeRecord, addControls, connectionPool, multiUpdateRequests, rejectWriter);
                    }
                    else if (changeRecord instanceof LDIFDeleteChangeRecord) {
                        rc3 = this.doDelete((LDIFDeleteChangeRecord)changeRecord, deleteControls, connectionPool, multiUpdateRequests, rejectWriter);
                    }
                    else if (changeRecord instanceof LDIFModifyChangeRecord) {
                        rc3 = this.doModify((LDIFModifyChangeRecord)changeRecord, modifyControls, connectionPool, multiUpdateRequests, rejectWriter);
                    }
                    else {
                        if (!(changeRecord instanceof LDIFModifyDNChangeRecord)) {
                            this.commentToErr(ToolMessages.ERR_LDAPMODIFY_UNSUPPORTED_CHANGE_RECORD_HEADER.get());
                            for (final String line3 : changeRecord.toLDIF()) {
                                this.err("#      " + line3);
                            }
                            throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_LDAPMODIFY_UNSUPPORTED_CHANGE_RECORD_HEADER.get() + changeRecord.toString());
                        }
                        rc3 = this.doModifyDN((LDIFModifyDNChangeRecord)changeRecord, modifyDNControls, connectionPool, multiUpdateRequests, rejectWriter);
                    }
                    if (resultCode != null || rc3 == ResultCode.SUCCESS) {
                        continue;
                    }
                    resultCode = rc3;
                }
                catch (final LDAPException le6) {
                    Debug.debugException(le6);
                    commitTransaction = false;
                    if (!this.continueOnError.isPresent()) {
                        resultCode = le6.getResultCode();
                        break;
                    }
                    if (resultCode != null && resultCode != ResultCode.SUCCESS && resultCode != ResultCode.NO_OPERATION) {
                        continue;
                    }
                    resultCode = le6.getResultCode();
                }
            }
            if (this.useTransaction.isPresent()) {
                final EndTransactionExtendedRequest endTxnRequest = new EndTransactionExtendedRequest(txnID, commitTransaction, new Control[0]);
                LDAPResult endTxnResult;
                try {
                    endTxnResult = connectionPool.processExtendedOperation(endTxnRequest);
                }
                catch (final LDAPException le7) {
                    endTxnResult = le7.toLDAPResult();
                }
                this.displayResult(endTxnResult, false);
                if ((resultCode == null || resultCode == ResultCode.SUCCESS) && endTxnResult.getResultCode() != ResultCode.SUCCESS) {
                    resultCode = endTxnResult.getResultCode();
                }
            }
            else if (this.multiUpdateErrorBehavior.isPresent()) {
                MultiUpdateErrorBehavior errorBehavior;
                if (this.multiUpdateErrorBehavior.getValue().equalsIgnoreCase("atomic")) {
                    errorBehavior = MultiUpdateErrorBehavior.ATOMIC;
                }
                else if (this.multiUpdateErrorBehavior.getValue().equalsIgnoreCase("abort-on-error")) {
                    errorBehavior = MultiUpdateErrorBehavior.ABORT_ON_ERROR;
                }
                else {
                    errorBehavior = MultiUpdateErrorBehavior.CONTINUE_ON_ERROR;
                }
                Control[] multiUpdateControls;
                if (this.proxyAs.isPresent()) {
                    multiUpdateControls = new Control[] { new ProxiedAuthorizationV2RequestControl(this.proxyAs.getValue()) };
                }
                else if (this.proxyV1As.isPresent()) {
                    multiUpdateControls = new Control[] { new ProxiedAuthorizationV1RequestControl(this.proxyV1As.getValue()) };
                }
                else {
                    multiUpdateControls = StaticUtils.NO_CONTROLS;
                }
                ExtendedResult multiUpdateResult;
                try {
                    this.commentToOut(ToolMessages.INFO_LDAPMODIFY_SENDING_MULTI_UPDATE_REQUEST.get());
                    final MultiUpdateExtendedRequest multiUpdateRequest = new MultiUpdateExtendedRequest(errorBehavior, multiUpdateRequests, multiUpdateControls);
                    multiUpdateResult = connectionPool.processExtendedOperation(multiUpdateRequest);
                }
                catch (final LDAPException le8) {
                    multiUpdateResult = new ExtendedResult(le8);
                }
                this.displayResult(multiUpdateResult, false);
                resultCode = multiUpdateResult.getResultCode();
            }
            if (resultCode == null) {
                return ResultCode.SUCCESS;
            }
            return resultCode;
        }
        finally {
            if (rejectWriter != null) {
                try {
                    rejectWriter.close();
                }
                catch (final Exception e6) {
                    Debug.debugException(e6);
                }
            }
            if (ldifReader != null) {
                try {
                    ldifReader.close();
                }
                catch (final Exception e6) {
                    Debug.debugException(e6);
                }
            }
            if (connectionPool != null) {
                try {
                    connectionPool.close();
                }
                catch (final Exception e6) {
                    Debug.debugException(e6);
                }
            }
        }
    }
    
    private ResultCode handleModifyMatchingFilter(final LDAPConnectionPool connectionPool, final LDIFChangeRecord changeRecord, final String argIdentifierString, final Filter filter, final List<Control> searchControls, final List<Control> modifyControls, final FixedRateBarrier rateLimiter, final LDIFWriter rejectWriter) {
        if (!(changeRecord instanceof LDIFModifyChangeRecord)) {
            this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_NON_MODIFY_WITH_BULK.get(argIdentifierString), changeRecord);
            return ResultCode.PARAM_ERROR;
        }
        final LDIFModifyChangeRecord modifyChangeRecord = (LDIFModifyChangeRecord)changeRecord;
        final HashSet<DN> processedDNs = new HashSet<DN>(StaticUtils.computeMapCapacity(100));
        ASN1OctetString pagedResultsCookie = null;
        long entriesProcessed = 0L;
        ResultCode resultCode = ResultCode.SUCCESS;
        while (true) {
            final LDAPModifySearchListener listener = new LDAPModifySearchListener(this, modifyChangeRecord, filter, modifyControls, connectionPool, rateLimiter, rejectWriter, processedDNs);
            final SearchRequest searchRequest = new SearchRequest(listener, modifyChangeRecord.getDN(), SearchScope.SUB, filter, new String[] { "1.1" });
            searchRequest.setControls(searchControls);
            if (this.searchPageSize.isPresent()) {
                searchRequest.addControl(new SimplePagedResultsControl(this.searchPageSize.getValue(), pagedResultsCookie));
            }
            LDAPConnection connection;
            try {
                connection = connectionPool.getConnection();
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_CANNOT_GET_SEARCH_CONNECTION.get(modifyChangeRecord.getDN(), String.valueOf(filter), StaticUtils.getExceptionMessage(le)), modifyChangeRecord, le.toLDAPResult());
                return le.getResultCode();
            }
            boolean connectionValid = false;
            SearchResult searchResult;
            try {
                try {
                    searchResult = connection.search(searchRequest);
                }
                catch (final LDAPSearchException lse) {
                    searchResult = lse.getSearchResult();
                }
                if (searchResult.getResultCode() == ResultCode.SUCCESS) {
                    connectionValid = true;
                }
                else {
                    if (searchResult.getResultCode().isConnectionUsable()) {
                        connectionValid = true;
                        this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_SEARCH_FAILED.get(modifyChangeRecord.getDN(), String.valueOf(filter)), modifyChangeRecord, searchResult);
                        return searchResult.getResultCode();
                    }
                    if (!this.retryFailedOperations.isPresent()) {
                        this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_SEARCH_FAILED.get(modifyChangeRecord.getDN(), String.valueOf(filter)), modifyChangeRecord, searchResult);
                        return searchResult.getResultCode();
                    }
                    try {
                        connection = connectionPool.replaceDefunctConnection(connection);
                    }
                    catch (final LDAPException le2) {
                        Debug.debugException(le2);
                        this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_SEARCH_FAILED_CANNOT_RECONNECT.get(modifyChangeRecord.getDN(), String.valueOf(filter)), modifyChangeRecord, searchResult);
                        return searchResult.getResultCode();
                    }
                    try {
                        searchResult = connection.search(searchRequest);
                    }
                    catch (final LDAPSearchException lse) {
                        Debug.debugException(lse);
                        searchResult = lse.getSearchResult();
                    }
                    if (searchResult.getResultCode() != ResultCode.SUCCESS) {
                        connectionValid = searchResult.getResultCode().isConnectionUsable();
                        this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_SEARCH_FAILED.get(modifyChangeRecord.getDN(), String.valueOf(filter)), modifyChangeRecord, searchResult);
                        return searchResult.getResultCode();
                    }
                    connectionValid = true;
                }
            }
            finally {
                if (connectionValid) {
                    connectionPool.releaseConnection(connection);
                }
                else {
                    connectionPool.releaseDefunctConnection(connection);
                }
            }
            if (resultCode == ResultCode.SUCCESS && listener.getResultCode() != ResultCode.SUCCESS) {
                resultCode = listener.getResultCode();
            }
            entriesProcessed += searchResult.getEntryCount();
            if (!this.searchPageSize.isPresent()) {
                this.commentToOut(ToolMessages.INFO_LDAPMODIFY_SEARCH_COMPLETED.get(entriesProcessed, modifyChangeRecord.getDN(), String.valueOf(filter)));
                if (this.verbose.isPresent()) {
                    for (final String resultLine : ResultUtils.formatResult(searchResult, true, 0, LDAPModify.WRAP_COLUMN)) {
                        this.out(resultLine);
                    }
                }
                this.out(new Object[0]);
                return resultCode;
            }
            SimplePagedResultsControl responseControl;
            try {
                responseControl = SimplePagedResultsControl.get(searchResult);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_CANNOT_DECODE_PAGED_RESULTS_CONTROL.get(modifyChangeRecord.getDN(), String.valueOf(filter)), modifyChangeRecord, le3.toLDAPResult());
                return le3.getResultCode();
            }
            if (responseControl == null) {
                this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_MISSING_PAGED_RESULTS_RESPONSE.get(modifyChangeRecord.getDN(), String.valueOf(filter)), modifyChangeRecord);
                return ResultCode.CONTROL_NOT_FOUND;
            }
            pagedResultsCookie = responseControl.getCookie();
            if (!responseControl.moreResultsToReturn()) {
                this.commentToOut(ToolMessages.INFO_LDAPMODIFY_SEARCH_COMPLETED.get(entriesProcessed, modifyChangeRecord.getDN(), String.valueOf(filter)));
                if (this.verbose.isPresent()) {
                    for (final String resultLine2 : ResultUtils.formatResult(searchResult, true, 0, LDAPModify.WRAP_COLUMN)) {
                        this.out(resultLine2);
                    }
                }
                this.out(new Object[0]);
                return resultCode;
            }
            if (!this.verbose.isPresent()) {
                continue;
            }
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_SEARCH_COMPLETED_MORE_PAGES.get(modifyChangeRecord.getDN(), String.valueOf(filter), entriesProcessed));
            for (final String resultLine2 : ResultUtils.formatResult(searchResult, true, 0, LDAPModify.WRAP_COLUMN)) {
                this.out(resultLine2);
            }
            this.out(new Object[0]);
        }
    }
    
    private ResultCode handleModifyWithDN(final LDAPConnectionPool connectionPool, final LDIFChangeRecord changeRecord, final String argIdentifierString, final DN dn, final List<Control> modifyControls, final FixedRateBarrier rateLimiter, final LDIFWriter rejectWriter) {
        if (!(changeRecord instanceof LDIFModifyChangeRecord)) {
            this.writeRejectedChange(rejectWriter, ToolMessages.ERR_LDAPMODIFY_NON_MODIFY_WITH_BULK.get(argIdentifierString), changeRecord);
            return ResultCode.PARAM_ERROR;
        }
        final LDIFModifyChangeRecord originalChangeRecord = (LDIFModifyChangeRecord)changeRecord;
        final LDIFModifyChangeRecord updatedChangeRecord = new LDIFModifyChangeRecord(dn.toString(), originalChangeRecord.getModifications(), originalChangeRecord.getControls());
        if (rateLimiter != null) {
            rateLimiter.await();
        }
        try {
            return this.doModify(updatedChangeRecord, modifyControls, connectionPool, null, rejectWriter);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return le.getResultCode();
        }
    }
    
    private void createRequestControls(final List<Control> addControls, final List<Control> deleteControls, final List<Control> modifyControls, final List<Control> modifyDNControls, final List<Control> searchControls) throws LDAPException {
        if (this.addControl.isPresent()) {
            addControls.addAll(this.addControl.getValues());
        }
        if (this.deleteControl.isPresent()) {
            deleteControls.addAll(this.deleteControl.getValues());
        }
        if (this.modifyControl.isPresent()) {
            modifyControls.addAll(this.modifyControl.getValues());
        }
        if (this.modifyDNControl.isPresent()) {
            modifyDNControls.addAll(this.modifyDNControl.getValues());
        }
        if (this.operationControl.isPresent()) {
            addControls.addAll(this.operationControl.getValues());
            deleteControls.addAll(this.operationControl.getValues());
            modifyControls.addAll(this.operationControl.getValues());
            modifyDNControls.addAll(this.operationControl.getValues());
        }
        addControls.addAll(this.routeToBackendSetRequestControls);
        deleteControls.addAll(this.routeToBackendSetRequestControls);
        modifyControls.addAll(this.routeToBackendSetRequestControls);
        modifyDNControls.addAll(this.routeToBackendSetRequestControls);
        if (this.noOperation.isPresent()) {
            final NoOpRequestControl c = new NoOpRequestControl();
            addControls.add(c);
            deleteControls.add(c);
            modifyControls.add(c);
            modifyDNControls.add(c);
        }
        if (this.generatePassword.isPresent()) {
            addControls.add(new GeneratePasswordRequestControl());
        }
        if (this.getBackendSetID.isPresent()) {
            final GetBackendSetIDRequestControl c2 = new GetBackendSetIDRequestControl(false);
            addControls.add(c2);
            deleteControls.add(c2);
            modifyControls.add(c2);
            modifyDNControls.add(c2);
        }
        if (this.getServerID.isPresent()) {
            final GetServerIDRequestControl c3 = new GetServerIDRequestControl(false);
            addControls.add(c3);
            deleteControls.add(c3);
            modifyControls.add(c3);
            modifyDNControls.add(c3);
        }
        if (this.ignoreNoUserModification.isPresent()) {
            addControls.add(new IgnoreNoUserModificationRequestControl(false));
            modifyControls.add(new IgnoreNoUserModificationRequestControl(false));
        }
        if (this.nameWithEntryUUID.isPresent()) {
            addControls.add(new NameWithEntryUUIDRequestControl(true));
        }
        if (this.permissiveModify.isPresent()) {
            modifyControls.add(new PermissiveModifyRequestControl(false));
        }
        if (this.routeToServer.isPresent()) {
            final RouteToServerRequestControl c4 = new RouteToServerRequestControl(false, this.routeToServer.getValue(), false, false, false);
            addControls.add(c4);
            deleteControls.add(c4);
            modifyControls.add(c4);
            modifyDNControls.add(c4);
        }
        if (this.suppressReferentialIntegrityUpdates.isPresent()) {
            final SuppressReferentialIntegrityUpdatesRequestControl c5 = new SuppressReferentialIntegrityUpdatesRequestControl(true);
            deleteControls.add(c5);
            modifyDNControls.add(c5);
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
                else if (s.equalsIgnoreCase("last-login-ip")) {
                    suppressTypes.add(SuppressType.LAST_LOGIN_IP);
                }
                else {
                    if (!s.equalsIgnoreCase("lastmod")) {
                        continue;
                    }
                    suppressTypes.add(SuppressType.LASTMOD);
                }
            }
            final SuppressOperationalAttributeUpdateRequestControl c6 = new SuppressOperationalAttributeUpdateRequestControl(suppressTypes);
            addControls.add(c6);
            deleteControls.add(c6);
            modifyControls.add(c6);
            modifyDNControls.add(c6);
        }
        if (this.usePasswordPolicyControl.isPresent()) {
            final PasswordPolicyRequestControl c7 = new PasswordPolicyRequestControl();
            addControls.add(c7);
            modifyControls.add(c7);
        }
        if (this.assuredReplication.isPresent()) {
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
            final AssuredReplicationRequestControl c8 = new AssuredReplicationRequestControl(true, localLevel, localLevel, remoteLevel, remoteLevel, timeoutMillis, false);
            addControls.add(c8);
            deleteControls.add(c8);
            modifyControls.add(c8);
            modifyDNControls.add(c8);
        }
        if (this.hardDelete.isPresent() && !this.clientSideSubtreeDelete.isPresent()) {
            deleteControls.add(new HardDeleteRequestControl(true));
        }
        if (this.replicationRepair.isPresent()) {
            final ReplicationRepairRequestControl c9 = new ReplicationRepairRequestControl();
            addControls.add(c9);
            deleteControls.add(c9);
            modifyControls.add(c9);
            modifyDNControls.add(c9);
        }
        if (this.softDelete.isPresent()) {
            deleteControls.add(new SoftDeleteRequestControl(true, true));
        }
        if (this.serverSideSubtreeDelete.isPresent()) {
            deleteControls.add(new SubtreeDeleteRequestControl());
        }
        if (this.assertionFilter.isPresent()) {
            final AssertionRequestControl c10 = new AssertionRequestControl(this.assertionFilter.getValue(), true);
            addControls.add(c10);
            deleteControls.add(c10);
            modifyControls.add(c10);
            modifyDNControls.add(c10);
        }
        if (this.operationPurpose.isPresent()) {
            final OperationPurposeRequestControl c11 = new OperationPurposeRequestControl(false, "ldapmodify", "4.0.14", LDAPModify.class.getName() + ".createRequestControls", this.operationPurpose.getValue());
            addControls.add(c11);
            deleteControls.add(c11);
            modifyControls.add(c11);
            modifyDNControls.add(c11);
        }
        if (this.manageDsaIT.isPresent()) {
            final ManageDsaITRequestControl c12 = new ManageDsaITRequestControl(true);
            addControls.add(c12);
            if (!this.clientSideSubtreeDelete.isPresent()) {
                deleteControls.add(c12);
            }
            modifyControls.add(c12);
            modifyDNControls.add(c12);
        }
        if (this.passwordUpdateBehavior.isPresent()) {
            final PasswordUpdateBehaviorRequestControl c13 = createPasswordUpdateBehaviorRequestControl(this.passwordUpdateBehavior.getIdentifierString(), this.passwordUpdateBehavior.getValues());
            addControls.add(c13);
            modifyControls.add(c13);
        }
        if (this.preReadAttribute.isPresent()) {
            final ArrayList<String> attrList = new ArrayList<String>(10);
            for (final String value : this.preReadAttribute.getValues()) {
                final StringTokenizer tokenizer = new StringTokenizer(value, ", ");
                while (tokenizer.hasMoreTokens()) {
                    attrList.add(tokenizer.nextToken());
                }
            }
            final String[] attrArray = attrList.toArray(StaticUtils.NO_STRINGS);
            final PreReadRequestControl c14 = new PreReadRequestControl(attrArray);
            deleteControls.add(c14);
            modifyControls.add(c14);
            modifyDNControls.add(c14);
        }
        if (this.postReadAttribute.isPresent()) {
            final ArrayList<String> attrList = new ArrayList<String>(10);
            for (final String value : this.postReadAttribute.getValues()) {
                final StringTokenizer tokenizer = new StringTokenizer(value, ", ");
                while (tokenizer.hasMoreTokens()) {
                    attrList.add(tokenizer.nextToken());
                }
            }
            final String[] attrArray = attrList.toArray(StaticUtils.NO_STRINGS);
            final PostReadRequestControl c15 = new PostReadRequestControl(attrArray);
            addControls.add(c15);
            modifyControls.add(c15);
            modifyDNControls.add(c15);
        }
        if (this.proxyAs.isPresent() && !this.useTransaction.isPresent() && !this.multiUpdateErrorBehavior.isPresent()) {
            final ProxiedAuthorizationV2RequestControl c16 = new ProxiedAuthorizationV2RequestControl(this.proxyAs.getValue());
            addControls.add(c16);
            deleteControls.add(c16);
            modifyControls.add(c16);
            modifyDNControls.add(c16);
            searchControls.add(c16);
        }
        if (this.proxyV1As.isPresent() && !this.useTransaction.isPresent() && !this.multiUpdateErrorBehavior.isPresent()) {
            final ProxiedAuthorizationV1RequestControl c17 = new ProxiedAuthorizationV1RequestControl(this.proxyV1As.getValue());
            addControls.add(c17);
            deleteControls.add(c17);
            modifyControls.add(c17);
            modifyDNControls.add(c17);
            searchControls.add(c17);
        }
        if (this.uniquenessAttribute.isPresent() || this.uniquenessFilter.isPresent()) {
            UniquenessRequestControlProperties uniquenessProperties;
            if (this.uniquenessAttribute.isPresent()) {
                uniquenessProperties = new UniquenessRequestControlProperties(this.uniquenessAttribute.getValues());
                if (this.uniquenessFilter.isPresent()) {
                    uniquenessProperties.setFilter(this.uniquenessFilter.getValue());
                }
            }
            else {
                uniquenessProperties = new UniquenessRequestControlProperties(this.uniquenessFilter.getValue());
            }
            if (this.uniquenessBaseDN.isPresent()) {
                uniquenessProperties.setBaseDN(this.uniquenessBaseDN.getStringValue());
            }
            if (this.uniquenessMultipleAttributeBehavior.isPresent()) {
                final String lowerCase;
                final String value2 = lowerCase = this.uniquenessMultipleAttributeBehavior.getValue().toLowerCase();
                switch (lowerCase) {
                    case "unique-within-each-attribute": {
                        uniquenessProperties.setMultipleAttributeBehavior(UniquenessMultipleAttributeBehavior.UNIQUE_WITHIN_EACH_ATTRIBUTE);
                        break;
                    }
                    case "unique-across-all-attributes-including-in-same-entry": {
                        uniquenessProperties.setMultipleAttributeBehavior(UniquenessMultipleAttributeBehavior.UNIQUE_ACROSS_ALL_ATTRIBUTES_INCLUDING_IN_SAME_ENTRY);
                        break;
                    }
                    case "unique-across-all-attributes-except-in-same-entry": {
                        uniquenessProperties.setMultipleAttributeBehavior(UniquenessMultipleAttributeBehavior.UNIQUE_ACROSS_ALL_ATTRIBUTES_EXCEPT_IN_SAME_ENTRY);
                        break;
                    }
                    case "unique-in-combination": {
                        uniquenessProperties.setMultipleAttributeBehavior(UniquenessMultipleAttributeBehavior.UNIQUE_IN_COMBINATION);
                        break;
                    }
                }
            }
            if (this.uniquenessPreCommitValidationLevel.isPresent()) {
                final String lowerCase2;
                final String value2 = lowerCase2 = this.uniquenessPreCommitValidationLevel.getValue().toLowerCase();
                switch (lowerCase2) {
                    case "none": {
                        uniquenessProperties.setPreCommitValidationLevel(UniquenessValidationLevel.NONE);
                        break;
                    }
                    case "all-subtree-views": {
                        uniquenessProperties.setPreCommitValidationLevel(UniquenessValidationLevel.ALL_SUBTREE_VIEWS);
                        break;
                    }
                    case "all-backend-sets": {
                        uniquenessProperties.setPreCommitValidationLevel(UniquenessValidationLevel.ALL_BACKEND_SETS);
                        break;
                    }
                    case "all-available-backend-servers": {
                        uniquenessProperties.setPreCommitValidationLevel(UniquenessValidationLevel.ALL_AVAILABLE_BACKEND_SERVERS);
                        break;
                    }
                }
            }
            if (this.uniquenessPostCommitValidationLevel.isPresent()) {
                final String lowerCase3;
                final String value2 = lowerCase3 = this.uniquenessPostCommitValidationLevel.getValue().toLowerCase();
                switch (lowerCase3) {
                    case "none": {
                        uniquenessProperties.setPostCommitValidationLevel(UniquenessValidationLevel.NONE);
                        break;
                    }
                    case "all-subtree-views": {
                        uniquenessProperties.setPostCommitValidationLevel(UniquenessValidationLevel.ALL_SUBTREE_VIEWS);
                        break;
                    }
                    case "all-backend-sets": {
                        uniquenessProperties.setPostCommitValidationLevel(UniquenessValidationLevel.ALL_BACKEND_SETS);
                        break;
                    }
                    case "all-available-backend-servers": {
                        uniquenessProperties.setPostCommitValidationLevel(UniquenessValidationLevel.ALL_AVAILABLE_BACKEND_SERVERS);
                        break;
                    }
                }
            }
            final UniquenessRequestControl c18 = new UniquenessRequestControl(true, null, uniquenessProperties);
            addControls.add(c18);
            modifyControls.add(c18);
            modifyDNControls.add(c18);
        }
    }
    
    static PasswordUpdateBehaviorRequestControl createPasswordUpdateBehaviorRequestControl(final String argIdentifier, final List<String> argValues) throws LDAPException {
        final PasswordUpdateBehaviorRequestControlProperties properties = new PasswordUpdateBehaviorRequestControlProperties();
        for (final String argValue : argValues) {
            int delimiterPos = argValue.indexOf(61);
            if (delimiterPos < 0) {
                delimiterPos = argValue.indexOf(58);
            }
            if (delimiterPos <= 0 || delimiterPos >= argValue.length() - 1) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_LDAPMODIFY_MALFORMED_PW_UPDATE_BEHAVIOR.get(argValue, argIdentifier));
            }
            final String name = argValue.substring(0, delimiterPos).trim();
            final String value = argValue.substring(delimiterPos + 1).trim();
            if (name.equalsIgnoreCase("is-self-change") || name.equalsIgnoreCase("self-change") || name.equalsIgnoreCase("isSelfChange") || name.equalsIgnoreCase("selfChange")) {
                properties.setIsSelfChange(parseBooleanValue(name, value));
            }
            else if (name.equalsIgnoreCase("allow-pre-encoded-password") || name.equalsIgnoreCase("allow-pre-encoded-passwords") || name.equalsIgnoreCase("allow-pre-encoded") || name.equalsIgnoreCase("allowPreEncodedPassword") || name.equalsIgnoreCase("allowPreEncodedPasswords") || name.equalsIgnoreCase("allowPreEncoded")) {
                properties.setAllowPreEncodedPassword(parseBooleanValue(name, value));
            }
            else if (name.equalsIgnoreCase("skip-password-validation") || name.equalsIgnoreCase("skip-password-validators") || name.equalsIgnoreCase("skip-validation") || name.equalsIgnoreCase("skip-validators") || name.equalsIgnoreCase("skipPasswordValidation") || name.equalsIgnoreCase("skipPasswordValidators") || name.equalsIgnoreCase("skipValidation") || name.equalsIgnoreCase("skipValidators")) {
                properties.setSkipPasswordValidation(parseBooleanValue(name, value));
            }
            else if (name.equalsIgnoreCase("ignore-password-history") || name.equalsIgnoreCase("skip-password-history") || name.equalsIgnoreCase("ignore-history") || name.equalsIgnoreCase("skip-history") || name.equalsIgnoreCase("ignorePasswordHistory") || name.equalsIgnoreCase("skipPasswordHistory") || name.equalsIgnoreCase("ignoreHistory") || name.equalsIgnoreCase("skipHistory")) {
                properties.setIgnorePasswordHistory(parseBooleanValue(name, value));
            }
            else if (name.equalsIgnoreCase("ignore-minimum-password-age") || name.equalsIgnoreCase("ignore-min-password-age") || name.equalsIgnoreCase("ignore-password-age") || name.equalsIgnoreCase("skip-minimum-password-age") || name.equalsIgnoreCase("skip-min-password-age") || name.equalsIgnoreCase("skip-password-age") || name.equalsIgnoreCase("ignoreMinimumPasswordAge") || name.equalsIgnoreCase("ignoreMinPasswordAge") || name.equalsIgnoreCase("ignorePasswordAge") || name.equalsIgnoreCase("skipMinimumPasswordAge") || name.equalsIgnoreCase("skipMinPasswordAge") || name.equalsIgnoreCase("skipPasswordAge")) {
                properties.setIgnoreMinimumPasswordAge(parseBooleanValue(name, value));
            }
            else if (name.equalsIgnoreCase("password-storage-scheme") || name.equalsIgnoreCase("password-scheme") || name.equalsIgnoreCase("storage-scheme") || name.equalsIgnoreCase("scheme") || name.equalsIgnoreCase("passwordStorageScheme") || name.equalsIgnoreCase("passwordScheme") || name.equalsIgnoreCase("storageScheme")) {
                properties.setPasswordStorageScheme(value);
            }
            else {
                if (!name.equalsIgnoreCase("must-change-password") && !name.equalsIgnoreCase("mustChangePassword")) {
                    continue;
                }
                properties.setMustChangePassword(parseBooleanValue(name, value));
            }
        }
        return new PasswordUpdateBehaviorRequestControl(properties, true);
    }
    
    private static boolean parseBooleanValue(final String name, final String value) throws LDAPException {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("1")) {
            return true;
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("f") || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("n") || value.equalsIgnoreCase("0")) {
            return false;
        }
        throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_LDAPMODIFY_INVALID_PW_UPDATE_BOOLEAN_VALUE.get(value, name));
    }
    
    private ResultCode doAdd(final LDIFAddChangeRecord changeRecord, final List<Control> controls, final LDAPConnectionPool pool, final List<LDAPRequest> multiUpdateRequests, final LDIFWriter rejectWriter) throws LDAPException {
        final AddRequest addRequest = changeRecord.toAddRequest(true);
        for (final Control c : controls) {
            addRequest.addControl(c);
        }
        if (this.allowUndelete.isPresent() && addRequest.hasAttribute("ds-undelete-from-dn")) {
            addRequest.addControl(new UndeleteRequestControl());
        }
        if (this.passwordValidationDetails.isPresent()) {
            final Entry entryToAdd = addRequest.toEntry();
            if (!entryToAdd.getAttributesWithOptions("userPassword", null).isEmpty() || !entryToAdd.getAttributesWithOptions("authPassword", null).isEmpty()) {
                addRequest.addControl(new PasswordValidationDetailsRequestControl());
            }
        }
        if (this.multiUpdateErrorBehavior.isPresent()) {
            multiUpdateRequests.add(addRequest);
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_ADD_ADDED_TO_MULTI_UPDATE.get(addRequest.getDN()));
            return ResultCode.SUCCESS;
        }
        if (this.dryRun.isPresent()) {
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_ADD.get(addRequest.getDN(), this.dryRun.getIdentifierString()));
            return ResultCode.SUCCESS;
        }
        this.commentToOut(ToolMessages.INFO_LDAPMODIFY_ADDING_ENTRY.get(addRequest.getDN()));
        if (this.verbose.isPresent()) {
            for (final String ldifLine : addRequest.toLDIFChangeRecord().toLDIF(LDAPModify.WRAP_COLUMN)) {
                this.out(ldifLine);
            }
            this.out(new Object[0]);
        }
        LDAPResult addResult;
        try {
            addResult = pool.add(addRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            addResult = le.toLDAPResult();
        }
        this.displayResult(addResult, this.useTransaction.isPresent());
        switch (addResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                break;
            }
            case 122: {
                this.writeRejectedChange(rejectWriter, ToolMessages.INFO_LDAPMODIFY_ASSERTION_FAILED.get(addRequest.getDN(), String.valueOf(this.assertionFilter.getValue())), addRequest.toLDIFChangeRecord(), addResult);
                throw new LDAPException(addResult);
            }
            default: {
                this.writeRejectedChange(rejectWriter, null, addRequest.toLDIFChangeRecord(), addResult);
                if (this.useTransaction.isPresent() || !this.continueOnError.isPresent()) {
                    throw new LDAPException(addResult);
                }
                break;
            }
        }
        return addResult.getResultCode();
    }
    
    private ResultCode doDelete(final LDIFDeleteChangeRecord changeRecord, final List<Control> controls, final LDAPConnectionPool pool, final List<LDAPRequest> multiUpdateRequests, final LDIFWriter rejectWriter) throws LDAPException {
        if (this.clientSideSubtreeDelete.isPresent()) {
            return this.doClientSideSubtreeDelete(changeRecord, controls, pool, rejectWriter);
        }
        final DeleteRequest deleteRequest = changeRecord.toDeleteRequest(true);
        for (final Control c : controls) {
            deleteRequest.addControl(c);
        }
        if (this.multiUpdateErrorBehavior.isPresent()) {
            multiUpdateRequests.add(deleteRequest);
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DELETE_ADDED_TO_MULTI_UPDATE.get(deleteRequest.getDN()));
            return ResultCode.SUCCESS;
        }
        if (this.dryRun.isPresent()) {
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_DELETE.get(deleteRequest.getDN(), this.dryRun.getIdentifierString()));
            return ResultCode.SUCCESS;
        }
        this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DELETING_ENTRY.get(deleteRequest.getDN()));
        if (this.verbose.isPresent()) {
            for (final String ldifLine : deleteRequest.toLDIFChangeRecord().toLDIF(LDAPModify.WRAP_COLUMN)) {
                this.out(ldifLine);
            }
            this.out(new Object[0]);
        }
        LDAPResult deleteResult;
        try {
            deleteResult = pool.delete(deleteRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            deleteResult = le.toLDAPResult();
        }
        this.displayResult(deleteResult, this.useTransaction.isPresent());
        switch (deleteResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                break;
            }
            case 122: {
                this.writeRejectedChange(rejectWriter, ToolMessages.INFO_LDAPMODIFY_ASSERTION_FAILED.get(deleteRequest.getDN(), String.valueOf(this.assertionFilter.getValue())), deleteRequest.toLDIFChangeRecord(), deleteResult);
                throw new LDAPException(deleteResult);
            }
            default: {
                this.writeRejectedChange(rejectWriter, null, deleteRequest.toLDIFChangeRecord(), deleteResult);
                if (this.useTransaction.isPresent() || !this.continueOnError.isPresent()) {
                    throw new LDAPException(deleteResult);
                }
                break;
            }
        }
        return deleteResult.getResultCode();
    }
    
    private ResultCode doClientSideSubtreeDelete(final LDIFChangeRecord changeRecord, final List<Control> controls, final LDAPConnectionPool pool, final LDIFWriter rejectWriter) throws LDAPException {
        List<Control> additionalControls;
        if (changeRecord.getControls().isEmpty()) {
            additionalControls = controls;
        }
        else {
            additionalControls = new ArrayList<Control>(controls.size() + changeRecord.getControls().size());
            additionalControls.addAll(changeRecord.getControls());
            additionalControls.addAll(controls);
        }
        final SubtreeDeleter subtreeDeleter = new SubtreeDeleter();
        subtreeDeleter.setAdditionalDeleteControls(additionalControls);
        this.commentToOut(ToolMessages.INFO_LDAPMODIFY_CLIENT_SIDE_DELETING_SUBTREE.get(changeRecord.getDN()));
        final SubtreeDeleterResult subtreeDeleterResult = subtreeDeleter.delete(pool, changeRecord.getDN());
        LDAPResult finalResult;
        if (subtreeDeleterResult.completelySuccessful()) {
            final long entriesDeleted = subtreeDeleterResult.getEntriesDeleted();
            if (entriesDeleted == 0L) {
                finalResult = new LDAPResult(-1, ResultCode.NO_SUCH_OBJECT, ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_SUCCEEDED_WITH_0_ENTRIES.get(changeRecord.getDN()), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
            else if (entriesDeleted == 1L) {
                finalResult = new LDAPResult(-1, ResultCode.SUCCESS, ToolMessages.INFO_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_SUCCEEDED_WITH_1_ENTRY.get(changeRecord.getDN()), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
            else {
                finalResult = new LDAPResult(-1, ResultCode.SUCCESS, ToolMessages.INFO_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_SUCCEEDED_WITH_ENTRIES.get(subtreeDeleterResult.getEntriesDeleted(), changeRecord.getDN()), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
            }
        }
        else {
            final SearchResult searchError = subtreeDeleterResult.getSearchError();
            if (searchError != null) {
                this.commentToErr(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_SEARCH_ERROR.get());
                this.displayResult(searchError, false);
                this.err("#");
            }
            final SortedMap<DN, LDAPResult> deleteErrors = subtreeDeleterResult.getDeleteErrorsDescendingMap();
            for (final Map.Entry<DN, LDAPResult> deleteError : deleteErrors.entrySet()) {
                this.commentToErr(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_ERROR.get(String.valueOf(deleteError.getKey())));
                this.displayResult(deleteError.getValue(), false);
                this.err("#");
            }
            ResultCode resultCode = ResultCode.OTHER;
            final StringBuilder buffer = new StringBuilder();
            buffer.append(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_FINAL_ERR_BASE.get());
            if (searchError != null) {
                resultCode = searchError.getResultCode();
                buffer.append("  ");
                buffer.append(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_FINAL_SEARCH_ERR.get());
            }
            if (!deleteErrors.isEmpty()) {
                resultCode = deleteErrors.values().iterator().next().getResultCode();
                buffer.append("  ");
                final int numDeleteErrors = deleteErrors.size();
                if (numDeleteErrors == 1) {
                    buffer.append(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_FINAL_DEL_ERR_COUNT_1.get());
                }
                else {
                    buffer.append(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_FINAL_DEL_ERR_COUNT.get(numDeleteErrors));
                }
            }
            buffer.append("  ");
            final long deletedCount = subtreeDeleterResult.getEntriesDeleted();
            if (deletedCount == 1L) {
                buffer.append(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_FINAL_DEL_COUNT_1.get());
            }
            else {
                buffer.append(ToolMessages.ERR_LDAPMODIFY_CLIENT_SIDE_SUB_DEL_FINAL_DEL_COUNT.get(deletedCount));
            }
            finalResult = new LDAPResult(-1, resultCode, buffer.toString(), null, StaticUtils.NO_STRINGS, StaticUtils.NO_CONTROLS);
        }
        this.displayResult(finalResult, this.useTransaction.isPresent());
        switch (finalResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                break;
            }
            default: {
                this.writeRejectedChange(rejectWriter, null, changeRecord, finalResult);
                if (!this.continueOnError.isPresent()) {
                    throw new LDAPException(finalResult);
                }
                break;
            }
        }
        return finalResult.getResultCode();
    }
    
    ResultCode doModify(final LDIFModifyChangeRecord changeRecord, final List<Control> controls, final LDAPConnectionPool pool, final List<LDAPRequest> multiUpdateRequests, final LDIFWriter rejectWriter) throws LDAPException {
        final ModifyRequest modifyRequest = changeRecord.toModifyRequest(true);
        for (final Control c : controls) {
            modifyRequest.addControl(c);
        }
        if (this.retireCurrentPassword.isPresent() || this.purgeCurrentPassword.isPresent() || this.passwordValidationDetails.isPresent()) {
            for (final Modification m : modifyRequest.getModifications()) {
                final String baseName = m.getAttribute().getBaseName();
                if (baseName.equalsIgnoreCase("userPassword") || baseName.equalsIgnoreCase("authPassword")) {
                    if (this.retireCurrentPassword.isPresent()) {
                        modifyRequest.addControl(new RetirePasswordRequestControl(false));
                    }
                    else if (this.purgeCurrentPassword.isPresent()) {
                        modifyRequest.addControl(new PurgePasswordRequestControl(false));
                    }
                    if (this.passwordValidationDetails.isPresent()) {
                        modifyRequest.addControl(new PasswordValidationDetailsRequestControl());
                        break;
                    }
                    break;
                }
            }
        }
        if (this.multiUpdateErrorBehavior.isPresent()) {
            multiUpdateRequests.add(modifyRequest);
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_MODIFY_ADDED_TO_MULTI_UPDATE.get(modifyRequest.getDN()));
            return ResultCode.SUCCESS;
        }
        if (this.dryRun.isPresent()) {
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_MODIFY.get(modifyRequest.getDN(), this.dryRun.getIdentifierString()));
            return ResultCode.SUCCESS;
        }
        this.commentToOut(ToolMessages.INFO_LDAPMODIFY_MODIFYING_ENTRY.get(modifyRequest.getDN()));
        if (this.verbose.isPresent()) {
            for (final String ldifLine : modifyRequest.toLDIFChangeRecord().toLDIF(LDAPModify.WRAP_COLUMN)) {
                this.out(ldifLine);
            }
            this.out(new Object[0]);
        }
        LDAPResult modifyResult;
        try {
            modifyResult = pool.modify(modifyRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            modifyResult = le.toLDAPResult();
        }
        this.displayResult(modifyResult, this.useTransaction.isPresent());
        switch (modifyResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                break;
            }
            case 122: {
                this.writeRejectedChange(rejectWriter, ToolMessages.INFO_LDAPMODIFY_ASSERTION_FAILED.get(modifyRequest.getDN(), String.valueOf(this.assertionFilter.getValue())), modifyRequest.toLDIFChangeRecord(), modifyResult);
                throw new LDAPException(modifyResult);
            }
            default: {
                this.writeRejectedChange(rejectWriter, null, modifyRequest.toLDIFChangeRecord(), modifyResult);
                if (this.useTransaction.isPresent() || !this.continueOnError.isPresent()) {
                    throw new LDAPException(modifyResult);
                }
                break;
            }
        }
        return modifyResult.getResultCode();
    }
    
    private ResultCode doModifyDN(final LDIFModifyDNChangeRecord changeRecord, final List<Control> controls, final LDAPConnectionPool pool, final List<LDAPRequest> multiUpdateRequests, final LDIFWriter rejectWriter) throws LDAPException {
        final ModifyDNRequest modifyDNRequest = changeRecord.toModifyDNRequest(true);
        for (final Control c : controls) {
            modifyDNRequest.addControl(c);
        }
        if (this.multiUpdateErrorBehavior.isPresent()) {
            multiUpdateRequests.add(modifyDNRequest);
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_MODIFY_DN_ADDED_TO_MULTI_UPDATE.get(modifyDNRequest.getDN()));
            return ResultCode.SUCCESS;
        }
        DN newDN = null;
        try {
            newDN = changeRecord.getNewDN();
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        if (this.dryRun.isPresent()) {
            if (modifyDNRequest.getNewSuperiorDN() == null) {
                if (newDN == null) {
                    this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_RENAME.get(modifyDNRequest.getDN(), this.dryRun.getIdentifierString()));
                }
                else {
                    this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_RENAME_TO.get(modifyDNRequest.getDN(), newDN.toString(), this.dryRun.getIdentifierString()));
                }
            }
            else if (newDN == null) {
                this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_MOVE.get(modifyDNRequest.getDN(), this.dryRun.getIdentifierString()));
            }
            else {
                this.commentToOut(ToolMessages.INFO_LDAPMODIFY_DRY_RUN_MOVE_TO.get(modifyDNRequest.getDN(), newDN.toString(), this.dryRun.getIdentifierString()));
            }
            return ResultCode.SUCCESS;
        }
        final String currentDN = modifyDNRequest.getDN();
        if (modifyDNRequest.getNewSuperiorDN() == null) {
            if (newDN == null) {
                this.commentToOut(ToolMessages.INFO_LDAPMODIFY_MOVING_ENTRY.get(currentDN));
            }
            else {
                this.commentToOut(ToolMessages.INFO_LDAPMODIFY_MOVING_ENTRY_TO.get(currentDN, newDN.toString()));
            }
        }
        else if (newDN == null) {
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_RENAMING_ENTRY.get(currentDN));
        }
        else {
            this.commentToOut(ToolMessages.INFO_LDAPMODIFY_RENAMING_ENTRY_TO.get(currentDN, newDN.toString()));
        }
        if (this.verbose.isPresent()) {
            for (final String ldifLine : modifyDNRequest.toLDIFChangeRecord().toLDIF(LDAPModify.WRAP_COLUMN)) {
                this.out(ldifLine);
            }
            this.out(new Object[0]);
        }
        LDAPResult modifyDNResult;
        try {
            modifyDNResult = pool.modifyDN(modifyDNRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            modifyDNResult = le.toLDAPResult();
        }
        this.displayResult(modifyDNResult, this.useTransaction.isPresent());
        switch (modifyDNResult.getResultCode().intValue()) {
            case 0:
            case 16654: {
                break;
            }
            case 122: {
                this.writeRejectedChange(rejectWriter, ToolMessages.INFO_LDAPMODIFY_ASSERTION_FAILED.get(modifyDNRequest.getDN(), String.valueOf(this.assertionFilter.getValue())), modifyDNRequest.toLDIFChangeRecord(), modifyDNResult);
                throw new LDAPException(modifyDNResult);
            }
            default: {
                this.writeRejectedChange(rejectWriter, null, modifyDNRequest.toLDIFChangeRecord(), modifyDNResult);
                if (this.useTransaction.isPresent() || !this.continueOnError.isPresent()) {
                    throw new LDAPException(modifyDNResult);
                }
                break;
            }
        }
        return modifyDNResult.getResultCode();
    }
    
    private void displayResult(final LDAPResult result, final boolean inTransaction) {
        final ArrayList<String> resultLines = new ArrayList<String>(10);
        ResultUtils.formatResult(resultLines, result, true, inTransaction, 0, LDAPModify.WRAP_COLUMN);
        if (result.getResultCode() == ResultCode.SUCCESS) {
            for (final String line : resultLines) {
                this.out(line);
            }
            this.out(new Object[0]);
        }
        else {
            for (final String line : resultLines) {
                this.err(line);
            }
            this.err(new Object[0]);
        }
    }
    
    private void commentToOut(final String message) {
        for (final String line : StaticUtils.wrapLine(message, LDAPModify.WRAP_COLUMN - 2)) {
            this.out("# ", line);
        }
    }
    
    private void commentToErr(final String message) {
        for (final String line : StaticUtils.wrapLine(message, LDAPModify.WRAP_COLUMN - 2)) {
            this.err("# ", line);
        }
    }
    
    private void writeRejectedChange(final LDIFWriter writer, final String comment, final LDIFChangeRecord changeRecord, final LDAPResult ldapResult) {
        if (writer == null) {
            return;
        }
        final StringBuilder buffer = new StringBuilder();
        if (comment != null) {
            buffer.append(comment);
            buffer.append(StaticUtils.EOL);
            buffer.append(StaticUtils.EOL);
        }
        final ArrayList<String> resultLines = new ArrayList<String>(10);
        ResultUtils.formatResult(resultLines, ldapResult, false, false, 0, 0);
        for (final String resultLine : resultLines) {
            buffer.append(resultLine);
            buffer.append(StaticUtils.EOL);
        }
        this.writeRejectedChange(writer, buffer.toString(), changeRecord);
    }
    
    void writeRejectedChange(final LDIFWriter writer, final String comment, final LDIFChangeRecord changeRecord) {
        if (writer == null) {
            return;
        }
        if (this.rejectWritten.compareAndSet(false, true)) {
            try {
                writer.writeVersionHeader();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        try {
            if (comment != null) {
                writer.writeComment(comment, true, false);
            }
            if (changeRecord != null) {
                writer.writeChangeRecord(changeRecord);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.commentToErr(ToolMessages.ERR_LDAPMODIFY_UNABLE_TO_WRITE_REJECTED_CHANGE.get(this.rejectFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
        }
    }
    
    @Override
    public void handleUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        final ArrayList<String> lines = new ArrayList<String>(10);
        ResultUtils.formatUnsolicitedNotification(lines, notification, true, 0, LDAPModify.WRAP_COLUMN);
        for (final String line : lines) {
            this.err(line);
        }
        this.err(new Object[0]);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        final String[] args1 = { "--hostname", "ldap.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--defaultAdd" };
        examples.put(args1, ToolMessages.INFO_LDAPMODIFY_EXAMPLE_1.get());
        final String[] args2 = { "--hostname", "ds1.example.com", "--port", "636", "--hostname", "ds2.example.com", "--port", "636", "--useSSL", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--filename", "changes.ldif", "--modifyEntriesMatchingFilter", "(objectClass=person)", "--searchPageSize", "100" };
        examples.put(args2, ToolMessages.INFO_LDAPMODIFY_EXAMPLE_2.get());
        return examples;
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
