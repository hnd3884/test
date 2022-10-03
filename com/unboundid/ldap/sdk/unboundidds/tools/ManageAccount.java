package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.unboundid.util.FilterFileReader;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.DNFileReader;
import java.io.File;
import java.io.FileOutputStream;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.Iterator;
import com.unboundid.util.args.SubCommand;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentValueValidator;
import com.unboundid.util.args.IPAddressArgumentValueValidator;
import com.unboundid.util.args.BooleanValueArgument;
import com.unboundid.util.args.TimestampArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.RateAdjustor;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.FixedRateBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ManageAccount extends LDAPCommandLineTool implements UnsolicitedNotificationHandler
{
    private static final int WRAP_COLUMN;
    private static final String ARG_APPEND_TO_REJECT_FILE = "appendToRejectFile";
    static final String ARG_BASE_DN = "baseDN";
    private static final String ARG_GENERATE_SAMPLE_RATE_FILE = "generateSampleRateFile";
    private static final String ARG_DN_INPUT_FILE = "dnInputFile";
    private static final String ARG_FILTER_INPUT_FILE = "filterInputFile";
    static final String ARG_NUM_SEARCH_THREADS = "numSearchThreads";
    static final String ARG_NUM_THREADS = "numThreads";
    private static final String ARG_RATE_PER_SECOND = "ratePerSecond";
    private static final String ARG_REJECT_FILE = "rejectFile";
    static final String ARG_SIMPLE_PAGE_SIZE = "simplePageSize";
    static final String ARG_SUPPRESS_EMPTY_RESULT_OPERATIONS = "suppressEmptyResultOperations";
    private static final String ARG_TARGET_DN = "targetDN";
    private static final String ARG_TARGET_FILTER = "targetFilter";
    private static final String ARG_TARGET_USER_ID = "targetUserID";
    static final String ARG_USER_ID_ATTRIBUTE = "userIDAttribute";
    private static final String ARG_USER_ID_INPUT_FILE = "userIDInputFile";
    private static final String ARG_VARIABLE_RATE_DATA = "variableRateData";
    private static final DN DEFAULT_BASE_DN;
    private static final String DEFAULT_USER_ID_ATTRIBUTE = "uid";
    private static final String EXAMPLE_TARGET_USER_DN = "uid=jdoe,ou=People,dc=example,dc=com";
    private volatile ArgumentParser parser;
    private final AtomicBoolean allDNsProvided;
    private final AtomicBoolean allFiltersProvided;
    private final AtomicBoolean cancelRequested;
    private volatile FixedRateBarrier rateLimiter;
    private final LDAPConnectionOptions connectionOptions;
    private volatile LDIFWriter outputWriter;
    private volatile LDIFWriter rejectWriter;
    private volatile ManageAccountSearchProcessor searchProcessor;
    private volatile RateAdjustor rateAdjustor;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final ManageAccount tool = new ManageAccount(out, err);
        final boolean origCommentAboutBase64EncodedValues = LDIFWriter.commentAboutBase64EncodedValues();
        LDIFWriter.setCommentAboutBase64EncodedValues(true);
        try {
            return tool.runTool(args);
        }
        finally {
            LDIFWriter.setCommentAboutBase64EncodedValues(origCommentAboutBase64EncodedValues);
        }
    }
    
    public ManageAccount(final OutputStream out, final OutputStream err) {
        super(out, err);
        (this.connectionOptions = new LDAPConnectionOptions()).setUnsolicitedNotificationHandler(this);
        this.allDNsProvided = new AtomicBoolean(false);
        this.allFiltersProvided = new AtomicBoolean(false);
        this.cancelRequested = new AtomicBoolean(false);
        this.parser = null;
        this.rateLimiter = null;
        this.rateAdjustor = null;
        this.outputWriter = null;
        this.rejectWriter = null;
        this.searchProcessor = null;
    }
    
    @Override
    public String getToolName() {
        return "manage-account";
    }
    
    @Override
    public String getToolDescription() {
        return ToolMessages.INFO_MANAGE_ACCT_TOOL_DESC.get();
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
    
    @Override
    protected boolean supportsOutputFile() {
        return true;
    }
    
    @Override
    protected boolean supportsAuthentication() {
        return true;
    }
    
    @Override
    protected boolean defaultToPromptForBindPassword() {
        return true;
    }
    
    @Override
    protected boolean supportsSASLHelp() {
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
    protected boolean supportsMultipleServers() {
        return true;
    }
    
    @Override
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        this.parser = parser;
        final String currentGeneralizedTime = StaticUtils.encodeGeneralizedTime(System.currentTimeMillis());
        final String olderGeneralizedTime = StaticUtils.encodeGeneralizedTime(System.currentTimeMillis() - 12345L);
        final DNArgument targetDN = new DNArgument('b', "targetDN", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_TARGET_DN.get());
        targetDN.addLongIdentifier("userDN", true);
        targetDN.addLongIdentifier("target-dn", true);
        targetDN.addLongIdentifier("user-dn", true);
        targetDN.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(targetDN);
        final FileArgument dnInputFile = new FileArgument(null, "dnInputFile", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_DN_FILE.get(), true, true, true, false);
        dnInputFile.addLongIdentifier("targetDNFile", true);
        dnInputFile.addLongIdentifier("userDNFile", true);
        dnInputFile.addLongIdentifier("dn-input-file", true);
        dnInputFile.addLongIdentifier("target-dn-file", true);
        dnInputFile.addLongIdentifier("user-dn-file", true);
        dnInputFile.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(dnInputFile);
        final FilterArgument targetFilter = new FilterArgument(null, "targetFilter", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_TARGET_FILTER.get("baseDN"));
        targetFilter.addLongIdentifier("target-filter", true);
        targetFilter.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(targetFilter);
        final FileArgument filterInputFile = new FileArgument(null, "filterInputFile", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_FILTER_INPUT_FILE.get("baseDN"), true, true, true, false);
        filterInputFile.addLongIdentifier("targetFilterFile", true);
        filterInputFile.addLongIdentifier("filter-input-file", true);
        filterInputFile.addLongIdentifier("target-filter-file", true);
        filterInputFile.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(filterInputFile);
        final StringArgument targetUserID = new StringArgument(null, "targetUserID", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_TARGET_USER_ID.get("baseDN", "userIDAttribute"));
        targetUserID.addLongIdentifier("userID", true);
        targetUserID.addLongIdentifier("target-user-id", true);
        targetUserID.addLongIdentifier("user-id", true);
        targetUserID.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(targetUserID);
        final FileArgument userIDInputFile = new FileArgument(null, "userIDInputFile", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_USER_ID_INPUT_FILE.get("baseDN", "userIDAttribute"), true, true, true, false);
        userIDInputFile.addLongIdentifier("targetUserIDFile", true);
        userIDInputFile.addLongIdentifier("user-id-input-file", true);
        userIDInputFile.addLongIdentifier("target-user-id-file", true);
        userIDInputFile.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(userIDInputFile);
        final StringArgument userIDAttribute = new StringArgument(null, "userIDAttribute", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_USER_ID_ATTR.get("targetUserID", "userIDInputFile", "uid"), "uid");
        userIDAttribute.addLongIdentifier("user-id-attribute", true);
        userIDAttribute.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(userIDAttribute);
        final DNArgument baseDN = new DNArgument(null, "baseDN", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_BASE_DN.get("targetFilter", "filterInputFile", "targetUserID", "userIDInputFile"), ManageAccount.DEFAULT_BASE_DN);
        baseDN.addLongIdentifier("base-dn", true);
        baseDN.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get());
        parser.addArgument(baseDN);
        final IntegerArgument simplePageSize = new IntegerArgument('z', "simplePageSize", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_SIMPLE_PAGE_SIZE.get(this.getToolName()), 1, Integer.MAX_VALUE);
        simplePageSize.addLongIdentifier("simple-page-size", true);
        simplePageSize.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_TARGET_USER_ARGS.get(this.getToolName()));
        parser.addArgument(simplePageSize);
        parser.addRequiredArgumentSet(targetDN, dnInputFile, targetFilter, filterInputFile, targetUserID, userIDInputFile);
        final IntegerArgument numThreads = new IntegerArgument('t', "numThreads", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_NUM_THREADS.get(this.getToolName()), 1, Integer.MAX_VALUE, 1);
        numThreads.addLongIdentifier("num-threads", true);
        numThreads.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_PERFORMANCE.get());
        parser.addArgument(numThreads);
        final IntegerArgument numSearchThreads = new IntegerArgument(null, "numSearchThreads", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_NUM_SEARCH_THREADS.get(this.getToolName()), 1, Integer.MAX_VALUE, 1);
        numSearchThreads.addLongIdentifier("num-search-threads", true);
        numSearchThreads.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_PERFORMANCE.get());
        parser.addArgument(numSearchThreads);
        final IntegerArgument ratePerSecond = new IntegerArgument('r', "ratePerSecond", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_RATE_PER_SECOND.get("variableRateData"), 1, Integer.MAX_VALUE);
        ratePerSecond.addLongIdentifier("rate-per-second", true);
        ratePerSecond.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_PERFORMANCE.get());
        parser.addArgument(ratePerSecond);
        final FileArgument variableRateData = new FileArgument(null, "variableRateData", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_VARIABLE_RATE_DATA.get("ratePerSecond"), true, true, true, false);
        variableRateData.addLongIdentifier("variable-rate-data", true);
        variableRateData.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_PERFORMANCE.get());
        parser.addArgument(variableRateData);
        final FileArgument generateSampleRateFile = new FileArgument(null, "generateSampleRateFile", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_GENERATE_SAMPLE_RATE_FILE.get("variableRateData"), false, true, true, false);
        generateSampleRateFile.addLongIdentifier("generate-sample-rate-file", true);
        generateSampleRateFile.setArgumentGroupName(ToolMessages.INFO_MANAGE_ACCT_ARG_GROUP_PERFORMANCE.get());
        generateSampleRateFile.setUsageArgument(true);
        parser.addArgument(generateSampleRateFile);
        final FileArgument rejectFile = new FileArgument('R', "rejectFile", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_REJECT_FILE.get(), false, true, true, false);
        rejectFile.addLongIdentifier("reject-file", true);
        parser.addArgument(rejectFile);
        final BooleanArgument appendToRejectFile = new BooleanArgument(null, "appendToRejectFile", 1, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_APPEND_TO_REJECT_FILE.get(rejectFile.getIdentifierString()));
        appendToRejectFile.addLongIdentifier("append-to-reject-file", true);
        parser.addArgument(appendToRejectFile);
        parser.addDependentArgumentSet(appendToRejectFile, rejectFile, new Argument[0]);
        final BooleanArgument suppressEmptyResultOperations = new BooleanArgument(null, "suppressEmptyResultOperations", 1, ToolMessages.INFO_MANAGE_ACCT_ARG_DESC_SUPPRESS_EMPTY_RESULT_OPERATIONS.get(this.getToolName()));
        parser.addArgument(suppressEmptyResultOperations);
        this.createSubCommand(ManageAccountSubCommandType.GET_ALL, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ALL_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_POLICY_DN, ToolMessages.INFO_MANAGE_ACCT_SC_GET_POLICY_DN_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_USABLE, ToolMessages.INFO_MANAGE_ACCT_SC_GET_IS_USABLE_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_USABILITY_NOTICES, ToolMessages.INFO_MANAGE_ACCT_SC_GET_USABILITY_NOTICES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_USABILITY_WARNINGS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_USABILITY_WARNINGS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_USABILITY_ERRORS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_USABILITY_ERRORS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_CHANGED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_CHANGED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setPWChangedTimeParser = createSubCommandParser(ManageAccountSubCommandType.SET_PASSWORD_CHANGED_TIME);
        final TimestampArgument setPWChangedTimeValueArg = new TimestampArgument('O', "passwordChangedTime", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_PW_CHANGED_TIME_ARG_VALUE.get());
        setPWChangedTimeValueArg.addLongIdentifier("operationValue", true);
        setPWChangedTimeValueArg.addLongIdentifier("password-changed-time", true);
        setPWChangedTimeValueArg.addLongIdentifier("operation-value", true);
        setPWChangedTimeParser.addArgument(setPWChangedTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_PASSWORD_CHANGED_TIME, setPWChangedTimeParser, createSubCommandExample(ManageAccountSubCommandType.SET_PASSWORD_CHANGED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_SET_PW_CHANGED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", currentGeneralizedTime), "--passwordChangedTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_PASSWORD_CHANGED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_PW_CHANGED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_DISABLED, ToolMessages.INFO_MANAGE_ACCT_SC_GET_IS_DISABLED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setAcctDisabledParser = createSubCommandParser(ManageAccountSubCommandType.SET_ACCOUNT_IS_DISABLED);
        final BooleanValueArgument setAcctDisabledValueArg = new BooleanValueArgument('O', "accountIsDisabled", true, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_IS_DISABLED_ARG_VALUE.get());
        setAcctDisabledValueArg.addLongIdentifier("operationValue", true);
        setAcctDisabledValueArg.addLongIdentifier("account-is-disabled", true);
        setAcctDisabledValueArg.addLongIdentifier("operation-value", true);
        setAcctDisabledParser.addArgument(setAcctDisabledValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_ACCOUNT_IS_DISABLED, setAcctDisabledParser, createSubCommandExample(ManageAccountSubCommandType.SET_ACCOUNT_IS_DISABLED, ToolMessages.INFO_MANAGE_ACCT_SC_SET_IS_DISABLED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), "--accountIsDisabled", "true"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_ACCOUNT_IS_DISABLED, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_IS_DISABLED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_ACTIVATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_ACT_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setAcctActivationTimeParser = createSubCommandParser(ManageAccountSubCommandType.SET_ACCOUNT_ACTIVATION_TIME);
        final TimestampArgument setAcctActivationTimeValueArg = new TimestampArgument('O', "accountActivationTime", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_ACCT_ACT_TIME_ARG_VALUE.get());
        setAcctActivationTimeValueArg.addLongIdentifier("operationValue", true);
        setAcctActivationTimeValueArg.addLongIdentifier("account-activation-time", true);
        setAcctActivationTimeValueArg.addLongIdentifier("operation-value", true);
        setAcctActivationTimeParser.addArgument(setAcctActivationTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_ACCOUNT_ACTIVATION_TIME, setAcctActivationTimeParser, createSubCommandExample(ManageAccountSubCommandType.SET_ACCOUNT_ACTIVATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_SET_ACCT_ACT_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", currentGeneralizedTime), "--accountActivationTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_ACCOUNT_ACTIVATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_ACCT_ACT_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_ACCOUNT_ACTIVATION, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_ACCT_ACT_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_NOT_YET_ACTIVE, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_NOT_YET_ACTIVE_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_EXPIRATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_EXP_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setAcctExpirationTimeParser = createSubCommandParser(ManageAccountSubCommandType.SET_ACCOUNT_EXPIRATION_TIME);
        final TimestampArgument setAcctExpirationTimeValueArg = new TimestampArgument('O', "accountExpirationTime", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_ACCT_EXP_TIME_ARG_VALUE.get());
        setAcctExpirationTimeValueArg.addLongIdentifier("operationValue", true);
        setAcctExpirationTimeValueArg.addLongIdentifier("account-expiration-time", true);
        setAcctExpirationTimeValueArg.addLongIdentifier("operation-value", true);
        setAcctExpirationTimeParser.addArgument(setAcctExpirationTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_ACCOUNT_EXPIRATION_TIME, setAcctExpirationTimeParser, createSubCommandExample(ManageAccountSubCommandType.SET_ACCOUNT_EXPIRATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_SET_ACCT_EXP_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", currentGeneralizedTime), "--accountExpirationTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_ACCOUNT_EXPIRATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_ACCT_EXP_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_ACCOUNT_EXPIRATION, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_ACCT_EXP_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_EXPIRED, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_IS_EXPIRED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_EXPIRATION_WARNED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_EXP_WARNED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setPWExpWarnedTimeParser = createSubCommandParser(ManageAccountSubCommandType.SET_PASSWORD_EXPIRATION_WARNED_TIME);
        final TimestampArgument setPWExpWarnedTimeValueArg = new TimestampArgument('O', "passwordExpirationWarnedTime", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_PW_EXP_WARNED_TIME_ARG_VALUE.get());
        setPWExpWarnedTimeValueArg.addLongIdentifier("operationValue", true);
        setPWExpWarnedTimeValueArg.addLongIdentifier("password-expiration-warned-time", true);
        setPWExpWarnedTimeValueArg.addLongIdentifier("operation-value", true);
        setPWExpWarnedTimeParser.addArgument(setPWExpWarnedTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_PASSWORD_EXPIRATION_WARNED_TIME, setPWExpWarnedTimeParser, createSubCommandExample(ManageAccountSubCommandType.SET_PASSWORD_EXPIRATION_WARNED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_SET_PW_EXP_WARNED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", currentGeneralizedTime), "--passwordExpirationWarnedTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_PASSWORD_EXPIRATION_WARNED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_PW_EXP_WARNED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_PASSWORD_EXPIRATION_WARNING, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_PW_EXP_WARNING_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_EXPIRATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_EXP_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_PASSWORD_EXPIRATION, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_PW_EXP_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_IS_EXPIRED, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_IS_EXPIRED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_FAILURE_LOCKED, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_FAILURE_LOCKED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setIsFailureLockedParser = createSubCommandParser(ManageAccountSubCommandType.SET_ACCOUNT_IS_FAILURE_LOCKED);
        final BooleanValueArgument setIsFailureLockedValueArg = new BooleanValueArgument('O', "accountIsFailureLocked", true, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_ACCT_FAILURE_LOCKED_ARG_VALUE.get());
        setIsFailureLockedValueArg.addLongIdentifier("operationValue", true);
        setIsFailureLockedValueArg.addLongIdentifier("account-is-failure-locked", true);
        setIsFailureLockedValueArg.addLongIdentifier("operation-value", true);
        setIsFailureLockedParser.addArgument(setIsFailureLockedValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_ACCOUNT_IS_FAILURE_LOCKED, setIsFailureLockedParser, createSubCommandExample(ManageAccountSubCommandType.SET_ACCOUNT_IS_FAILURE_LOCKED, ToolMessages.INFO_MANAGE_ACCT_SC_SET_ACCT_FAILURE_LOCKED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), "--accountIsFailureLocked", "true"));
        this.createSubCommand(ManageAccountSubCommandType.GET_FAILURE_LOCKOUT_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_FAILURE_LOCKED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_AUTHENTICATION_FAILURE_UNLOCK, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_FAILURE_UNLOCK_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_AUTHENTICATION_FAILURE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_GET_AUTH_FAILURE_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser addAuthFailureTimeParser = createSubCommandParser(ManageAccountSubCommandType.ADD_AUTHENTICATION_FAILURE_TIME);
        final TimestampArgument addAuthFailureTimeValueArg = new TimestampArgument('O', "authenticationFailureTime", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_AUTH_FAILURE_TIME_ARG_VALUE.get());
        addAuthFailureTimeValueArg.addLongIdentifier("operationValue", true);
        addAuthFailureTimeValueArg.addLongIdentifier("authentication-failure-time", true);
        addAuthFailureTimeValueArg.addLongIdentifier("operation-value", true);
        addAuthFailureTimeParser.addArgument(addAuthFailureTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.ADD_AUTHENTICATION_FAILURE_TIME, addAuthFailureTimeParser, createSubCommandExample(ManageAccountSubCommandType.ADD_AUTHENTICATION_FAILURE_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_AUTH_FAILURE_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), new String[0]));
        final ArgumentParser setAuthFailureTimesParser = createSubCommandParser(ManageAccountSubCommandType.SET_AUTHENTICATION_FAILURE_TIMES);
        final TimestampArgument setAuthFailureTimesValueArg = new TimestampArgument('O', "authenticationFailureTime", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_AUTH_FAILURE_TIMES_ARG_VALUE.get());
        setAuthFailureTimesValueArg.addLongIdentifier("operationValue", true);
        setAuthFailureTimesValueArg.addLongIdentifier("authentication-failure-time", true);
        setAuthFailureTimesValueArg.addLongIdentifier("operation-value", true);
        setAuthFailureTimesParser.addArgument(setAuthFailureTimesValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_AUTHENTICATION_FAILURE_TIMES, setAuthFailureTimesParser, createSubCommandExample(ManageAccountSubCommandType.SET_AUTHENTICATION_FAILURE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_SET_AUTH_FAILURE_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", olderGeneralizedTime, currentGeneralizedTime), "--authenticationFailureTime", olderGeneralizedTime, "--authenticationFailureTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_AUTHENTICATION_FAILURE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_AUTH_FAILURE_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_REMAINING_AUTHENTICATION_FAILURE_COUNT, ToolMessages.INFO_MANAGE_ACCT_SC_GET_REMAINING_FAILURE_COUNT_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_IDLE_LOCKED, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_IDLE_LOCKED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_IDLE_LOCKOUT, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_IDLE_LOCKOUT_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_IDLE_LOCKOUT_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_IDLE_LOCKOUT_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_MUST_CHANGE_PASSWORD, ToolMessages.INFO_MANAGE_ACCT_SC_GET_MUST_CHANGE_PW_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setPWIsResetParser = createSubCommandParser(ManageAccountSubCommandType.SET_MUST_CHANGE_PASSWORD);
        final BooleanValueArgument setPWIsResetValueArg = new BooleanValueArgument('O', "mustChangePassword", true, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_MUST_CHANGE_PW_ARG_VALUE.get());
        setPWIsResetValueArg.addLongIdentifier("passwordIsReset", true);
        setPWIsResetValueArg.addLongIdentifier("operationValue", true);
        setPWIsResetValueArg.addLongIdentifier("must-change-password", true);
        setPWIsResetValueArg.addLongIdentifier("password-is-reset", true);
        setPWIsResetValueArg.addLongIdentifier("operation-value", true);
        setPWIsResetParser.addArgument(setPWIsResetValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_MUST_CHANGE_PASSWORD, setPWIsResetParser, createSubCommandExample(ManageAccountSubCommandType.SET_MUST_CHANGE_PASSWORD, ToolMessages.INFO_MANAGE_ACCT_SC_SET_MUST_CHANGE_PW_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), "--mustChangePassword", "true"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_MUST_CHANGE_PASSWORD, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_MUST_CHANGE_PW_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_ACCOUNT_IS_PASSWORD_RESET_LOCKED, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ACCT_IS_RESET_LOCKED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_PASSWORD_RESET_LOCKOUT, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECONDS_UNTIL_RESET_LOCKOUT_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_RESET_LOCKOUT_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_RESET_LOCKOUT_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_LAST_LOGIN_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_LAST_LOGIN_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setLastLoginTimeParser = createSubCommandParser(ManageAccountSubCommandType.SET_LAST_LOGIN_TIME);
        final TimestampArgument setLastLoginTimeValueArg = new TimestampArgument('O', "lastLoginTime", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_LAST_LOGIN_TIME_ARG_VALUE.get());
        setLastLoginTimeValueArg.addLongIdentifier("operationValue", true);
        setLastLoginTimeValueArg.addLongIdentifier("last-login-time", true);
        setLastLoginTimeValueArg.addLongIdentifier("operation-value", true);
        setLastLoginTimeParser.addArgument(setLastLoginTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_LAST_LOGIN_TIME, setLastLoginTimeParser, createSubCommandExample(ManageAccountSubCommandType.SET_LAST_LOGIN_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_SET_LAST_LOGIN_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", currentGeneralizedTime), "--lastLoginTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_LAST_LOGIN_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_LAST_LOGIN_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_LAST_LOGIN_IP_ADDRESS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_LAST_LOGIN_IP_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setLastLoginIPParser = createSubCommandParser(ManageAccountSubCommandType.SET_LAST_LOGIN_IP_ADDRESS);
        final StringArgument setLastLoginIPValueArg = new StringArgument('O', "lastLoginIPAddress", true, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_LAST_LOGIN_IP_ARG_VALUE.get());
        setLastLoginIPValueArg.addLongIdentifier("operationValue", true);
        setLastLoginIPValueArg.addLongIdentifier("last-login-ip-address", true);
        setLastLoginIPValueArg.addLongIdentifier("operation-value", true);
        setLastLoginIPValueArg.addValueValidator(new IPAddressArgumentValueValidator());
        setLastLoginIPParser.addArgument(setLastLoginIPValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_LAST_LOGIN_IP_ADDRESS, setLastLoginIPParser, createSubCommandExample(ManageAccountSubCommandType.SET_LAST_LOGIN_IP_ADDRESS, ToolMessages.INFO_MANAGE_ACCT_SC_SET_LAST_LOGIN_IP_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", "1.2.3.4"), "--lastLoginIPAddress", "1.2.3.4"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_LAST_LOGIN_IP_ADDRESS, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_LAST_LOGIN_IP_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_GRACE_LOGIN_USE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_GET_GRACE_LOGIN_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser addGraceLoginTimeParser = createSubCommandParser(ManageAccountSubCommandType.ADD_GRACE_LOGIN_USE_TIME);
        final TimestampArgument addGraceLoginTimeValueArg = new TimestampArgument('O', "graceLoginUseTime", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_GRACE_LOGIN_TIME_ARG_VALUE.get());
        addGraceLoginTimeValueArg.addLongIdentifier("operationValue", true);
        addGraceLoginTimeValueArg.addLongIdentifier("grace-login-use-time", true);
        addGraceLoginTimeValueArg.addLongIdentifier("operation-value", true);
        addGraceLoginTimeParser.addArgument(addGraceLoginTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.ADD_GRACE_LOGIN_USE_TIME, addGraceLoginTimeParser, createSubCommandExample(ManageAccountSubCommandType.ADD_GRACE_LOGIN_USE_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_GRACE_LOGIN_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), new String[0]));
        final ArgumentParser setGraceLoginTimesParser = createSubCommandParser(ManageAccountSubCommandType.SET_GRACE_LOGIN_USE_TIMES);
        final TimestampArgument setGraceLoginTimesValueArg = new TimestampArgument('O', "graceLoginUseTime", false, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_GRACE_LOGIN_TIMES_ARG_VALUE.get());
        setGraceLoginTimesValueArg.addLongIdentifier("operationValue", true);
        setGraceLoginTimesValueArg.addLongIdentifier("grace-login-use-time", true);
        setGraceLoginTimesValueArg.addLongIdentifier("operation-value", true);
        setGraceLoginTimesParser.addArgument(setGraceLoginTimesValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_GRACE_LOGIN_USE_TIMES, setGraceLoginTimesParser, createSubCommandExample(ManageAccountSubCommandType.SET_GRACE_LOGIN_USE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_SET_GRACE_LOGIN_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", olderGeneralizedTime, currentGeneralizedTime), "--graceLoginUseTime", olderGeneralizedTime, "--graceLoginUseTime", currentGeneralizedTime));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_GRACE_LOGIN_USE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_GRACE_LOGIN_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_REMAINING_GRACE_LOGIN_COUNT, ToolMessages.INFO_MANAGE_ACCT_SC_GET_REMAINING_GRACE_LOGIN_COUNT_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_CHANGED_BY_REQUIRED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_CHANGED_BY_REQ_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser setPWChangedByReqTimeParser = createSubCommandParser(ManageAccountSubCommandType.SET_PASSWORD_CHANGED_BY_REQUIRED_TIME);
        final TimestampArgument setPWChangedByReqTimeValueArg = new TimestampArgument('O', "passwordChangedByRequiredTime", false, 1, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_PW_CHANGED_BY_REQ_TIME_ARG_VALUE.get());
        setPWChangedByReqTimeValueArg.addLongIdentifier("operationValue", true);
        setPWChangedByReqTimeValueArg.addLongIdentifier("password-changed-by-required-time", true);
        setPWChangedByReqTimeValueArg.addLongIdentifier("operation-value", true);
        setPWChangedByReqTimeParser.addArgument(setPWChangedByReqTimeValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_PASSWORD_CHANGED_BY_REQUIRED_TIME, setPWChangedByReqTimeParser, createSubCommandExample(ManageAccountSubCommandType.SET_PASSWORD_CHANGED_BY_REQUIRED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_SET_PW_CHANGED_BY_REQ_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), new String[0]));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_PASSWORD_CHANGED_BY_REQUIRED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_PW_CHANGED_BY_REQ_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_SECONDS_UNTIL_REQUIRED_PASSWORD_CHANGE_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_SECS_UNTIL_REQ_CHANGE_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_HISTORY_COUNT, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_HISTORY_COUNT_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_PASSWORD_HISTORY, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_PW_HISTORY_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_HAS_RETIRED_PASSWORD, ToolMessages.INFO_MANAGE_ACCT_SC_GET_HAS_RETIRED_PW_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_PASSWORD_RETIRED_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_PW_RETIRED_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_RETIRED_PASSWORD_EXPIRATION_TIME, ToolMessages.INFO_MANAGE_ACCT_SC_GET_RETIRED_PW_EXP_TIME_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_RETIRED_PASSWORD, ToolMessages.INFO_MANAGE_ACCT_SC_PURGE_RETIRED_PW_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_AVAILABLE_SASL_MECHANISMS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_AVAILABLE_SASL_MECHS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_AVAILABLE_OTP_DELIVERY_MECHANISMS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_AVAILABLE_OTP_MECHS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_HAS_TOTP_SHARED_SECRET, ToolMessages.INFO_MANAGE_ACCT_SC_GET_HAS_TOTP_SHARED_SECRET_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser addTOTPSharedSecretParser = createSubCommandParser(ManageAccountSubCommandType.ADD_TOTP_SHARED_SECRET);
        final StringArgument addTOTPSharedSecretValueArg = new StringArgument('O', "totpSharedSecret", true, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_YUBIKEY_ID_ARG_VALUE.get());
        addTOTPSharedSecretValueArg.addLongIdentifier("operationValue", true);
        addTOTPSharedSecretValueArg.addLongIdentifier("totp-shared-secret", true);
        addTOTPSharedSecretValueArg.addLongIdentifier("operation-value", true);
        addTOTPSharedSecretValueArg.setSensitive(true);
        addTOTPSharedSecretParser.addArgument(addTOTPSharedSecretValueArg);
        this.createSubCommand(ManageAccountSubCommandType.ADD_TOTP_SHARED_SECRET, addTOTPSharedSecretParser, createSubCommandExample(ManageAccountSubCommandType.ADD_TOTP_SHARED_SECRET, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_TOTP_SHARED_SECRET_EXAMPLE.get("abcdefghijklmnop", "uid=jdoe,ou=People,dc=example,dc=com"), "--totpSharedSecret", "abcdefghijklmnop"));
        final ArgumentParser removeTOTPSharedSecretParser = createSubCommandParser(ManageAccountSubCommandType.REMOVE_TOTP_SHARED_SECRET);
        final StringArgument removeTOTPSharedSecretValueArg = new StringArgument('O', "totpSharedSecret", true, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_REMOVE_YUBIKEY_ID_ARG_VALUE.get());
        removeTOTPSharedSecretValueArg.addLongIdentifier("operationValue", true);
        removeTOTPSharedSecretValueArg.addLongIdentifier("totp-shared-secret", true);
        removeTOTPSharedSecretValueArg.addLongIdentifier("operation-value", true);
        removeTOTPSharedSecretValueArg.setSensitive(true);
        removeTOTPSharedSecretParser.addArgument(removeTOTPSharedSecretValueArg);
        this.createSubCommand(ManageAccountSubCommandType.REMOVE_TOTP_SHARED_SECRET, removeTOTPSharedSecretParser, createSubCommandExample(ManageAccountSubCommandType.REMOVE_TOTP_SHARED_SECRET, ToolMessages.INFO_MANAGE_ACCT_SC_REMOVE_TOTP_SHARED_SECRET_EXAMPLE.get("abcdefghijklmnop", "uid=jdoe,ou=People,dc=example,dc=com"), "--totpSharedSecret", "abcdefghijklmnop"));
        final ArgumentParser setTOTPSharedSecretsParser = createSubCommandParser(ManageAccountSubCommandType.SET_TOTP_SHARED_SECRETS);
        final StringArgument setTOTPSharedSecretsValueArg = new StringArgument('O', "totpSharedSecret", true, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_TOTP_SHARED_SECRETS_ARG_VALUE.get());
        setTOTPSharedSecretsValueArg.addLongIdentifier("operationValue", true);
        setTOTPSharedSecretsValueArg.addLongIdentifier("totp-shared-secret", true);
        setTOTPSharedSecretsValueArg.addLongIdentifier("operation-value", true);
        setTOTPSharedSecretsValueArg.setSensitive(true);
        setTOTPSharedSecretsParser.addArgument(setTOTPSharedSecretsValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_TOTP_SHARED_SECRETS, setTOTPSharedSecretsParser, createSubCommandExample(ManageAccountSubCommandType.SET_TOTP_SHARED_SECRETS, ToolMessages.INFO_MANAGE_ACCT_SC_SET_TOTP_SHARED_SECRETS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", "abcdefghijklmnop"), "--totpSharedSecret", "abcdefghijklmnop"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_TOTP_SHARED_SECRETS, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_TOTP_SHARED_SECRETS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_HAS_REGISTERED_YUBIKEY_PUBLIC_ID, ToolMessages.INFO_MANAGE_ACCT_SC_GET_HAS_YUBIKEY_ID_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_REGISTERED_YUBIKEY_PUBLIC_IDS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_YUBIKEY_IDS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        final ArgumentParser addRegisteredYubiKeyPublicIDParser = createSubCommandParser(ManageAccountSubCommandType.ADD_REGISTERED_YUBIKEY_PUBLIC_ID);
        final StringArgument addRegisteredYubiKeyPublicIDValueArg = new StringArgument('O', "publicID", true, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_YUBIKEY_ID_ARG_VALUE.get());
        addRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("operationValue", true);
        addRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("public-id", true);
        addRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("operation-value", true);
        addRegisteredYubiKeyPublicIDParser.addArgument(addRegisteredYubiKeyPublicIDValueArg);
        this.createSubCommand(ManageAccountSubCommandType.ADD_REGISTERED_YUBIKEY_PUBLIC_ID, addRegisteredYubiKeyPublicIDParser, createSubCommandExample(ManageAccountSubCommandType.ADD_REGISTERED_YUBIKEY_PUBLIC_ID, ToolMessages.INFO_MANAGE_ACCT_SC_ADD_YUBIKEY_ID_EXAMPLE.get("abcdefghijkl", "uid=jdoe,ou=People,dc=example,dc=com"), "--publicID", "abcdefghijkl"));
        final ArgumentParser removeRegisteredYubiKeyPublicIDParser = createSubCommandParser(ManageAccountSubCommandType.REMOVE_REGISTERED_YUBIKEY_PUBLIC_ID);
        final StringArgument removeRegisteredYubiKeyPublicIDValueArg = new StringArgument('O', "publicID", true, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_REMOVE_YUBIKEY_ID_ARG_VALUE.get());
        removeRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("operationValue", true);
        removeRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("public-id", true);
        removeRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("operation-value", true);
        removeRegisteredYubiKeyPublicIDParser.addArgument(removeRegisteredYubiKeyPublicIDValueArg);
        this.createSubCommand(ManageAccountSubCommandType.REMOVE_REGISTERED_YUBIKEY_PUBLIC_ID, removeRegisteredYubiKeyPublicIDParser, createSubCommandExample(ManageAccountSubCommandType.REMOVE_REGISTERED_YUBIKEY_PUBLIC_ID, ToolMessages.INFO_MANAGE_ACCT_SC_REMOVE_YUBIKEY_ID_EXAMPLE.get("abcdefghijkl", "uid=jdoe,ou=People,dc=example,dc=com"), "--publicID", "abcdefghijkl"));
        final ArgumentParser setRegisteredYubiKeyPublicIDParser = createSubCommandParser(ManageAccountSubCommandType.SET_REGISTERED_YUBIKEY_PUBLIC_IDS);
        final StringArgument setRegisteredYubiKeyPublicIDValueArg = new StringArgument('O', "publicID", true, 0, null, ToolMessages.INFO_MANAGE_ACCT_SC_SET_YUBIKEY_IDS_ARG_VALUE.get());
        setRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("operationValue", true);
        setRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("public-id", true);
        setRegisteredYubiKeyPublicIDValueArg.addLongIdentifier("operation-value", true);
        setRegisteredYubiKeyPublicIDParser.addArgument(setRegisteredYubiKeyPublicIDValueArg);
        this.createSubCommand(ManageAccountSubCommandType.SET_REGISTERED_YUBIKEY_PUBLIC_IDS, setRegisteredYubiKeyPublicIDParser, createSubCommandExample(ManageAccountSubCommandType.SET_REGISTERED_YUBIKEY_PUBLIC_IDS, ToolMessages.INFO_MANAGE_ACCT_SC_SET_YUBIKEY_IDS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com", "abcdefghijkl"), "--publicID", "abcdefghijkl"));
        this.createSubCommand(ManageAccountSubCommandType.CLEAR_REGISTERED_YUBIKEY_PUBLIC_IDS, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_YUBIKEY_IDS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
        this.createSubCommand(ManageAccountSubCommandType.GET_HAS_STATIC_PASSWORD, ToolMessages.INFO_MANAGE_ACCT_SC_GET_HAS_STATIC_PW_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"));
    }
    
    private static ArgumentParser createSubCommandParser(final ManageAccountSubCommandType type) throws ArgumentException {
        return new ArgumentParser(type.getPrimaryName(), type.getDescription());
    }
    
    private static LinkedHashMap<String[], String> createSubCommandExample(final ManageAccountSubCommandType t, final String description, final String... args) {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        createSubCommandExample(examples, t, description, args);
        return examples;
    }
    
    private static void createSubCommandExample(final LinkedHashMap<String[], String> examples, final ManageAccountSubCommandType t, final String description, final String... args) {
        final ArrayList<String> argList = new ArrayList<String>(10 + args.length);
        argList.add(t.getPrimaryName());
        argList.add("--hostname");
        argList.add("server.example.com");
        argList.add("--port");
        argList.add("389");
        argList.add("--bindDN");
        argList.add("uid=admin,dc=example,dc=com");
        argList.add("--promptForBindPassword");
        argList.add("--targetDN");
        argList.add("uid=jdoe,ou=People,dc=example,dc=com");
        if (args.length > 0) {
            argList.addAll(Arrays.asList(args));
        }
        final String[] argArray = new String[argList.size()];
        argList.toArray(argArray);
        examples.put(argArray, description);
    }
    
    private void createSubCommand(final ManageAccountSubCommandType subcommandType, final String exampleDescription) throws ArgumentException {
        final ArgumentParser subcommandParser = createSubCommandParser(subcommandType);
        final LinkedHashMap<String[], String> examples = createSubCommandExample(subcommandType, exampleDescription, new String[0]);
        this.createSubCommand(subcommandType, subcommandParser, examples);
    }
    
    private void createSubCommand(final ManageAccountSubCommandType subcommandType, final ArgumentParser subcommandParser, final LinkedHashMap<String[], String> examples) throws ArgumentException {
        final SubCommand subCommand = new SubCommand(subcommandType.getPrimaryName(), subcommandType.getDescription(), subcommandParser, examples);
        for (final String alternateName : subcommandType.getAlternateNames()) {
            subCommand.addName(alternateName, true);
        }
        this.parser.addSubCommand(subCommand);
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        return this.connectionOptions;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final FileArgument generateSampleRateFile = this.parser.getFileArgument("generateSampleRateFile");
        if (generateSampleRateFile.isPresent()) {
            try {
                RateAdjustor.writeSampleVariableRateFile(generateSampleRateFile.getValue());
                return ResultCode.SUCCESS;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.wrapErr(0, ManageAccount.WRAP_COLUMN, ToolMessages.ERR_MANAGE_ACCT_CANNOT_GENERATE_SAMPLE_RATE_FILE.get(generateSampleRateFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e)));
                return ResultCode.LOCAL_ERROR;
            }
        }
        final IntegerArgument ratePerSecond = this.parser.getIntegerArgument("ratePerSecond");
        final FileArgument variableRateData = this.parser.getFileArgument("variableRateData");
        if (ratePerSecond.isPresent() || variableRateData.isPresent()) {
            if (ratePerSecond.isPresent()) {
                this.rateLimiter = new FixedRateBarrier(1000L, ratePerSecond.getValue());
            }
            else {
                this.rateLimiter = new FixedRateBarrier(1000L, Integer.MAX_VALUE);
            }
            if (variableRateData.isPresent()) {
                try {
                    this.rateAdjustor = RateAdjustor.newInstance(this.rateLimiter, ratePerSecond.getValue(), variableRateData.getValue());
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    this.wrapErr(0, ManageAccount.WRAP_COLUMN, ToolMessages.ERR_MANAGE_ACCT_CANNOT_CREATE_RATE_ADJUSTOR.get(variableRateData.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e2)));
                    return ResultCode.PARAM_ERROR;
                }
            }
        }
        final int numSearchThreads = this.parser.getIntegerArgument("numSearchThreads").getValue();
        LDAPConnectionPool pool;
        try {
            final int numOperationThreads = this.parser.getIntegerArgument("numThreads").getValue();
            pool = this.getConnectionPool(numOperationThreads, numOperationThreads + numSearchThreads);
            pool.setRetryFailedOperationsDueToInvalidConnections(false);
            pool.setMaxConnectionAgeMillis(1800000L);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.wrapErr(0, ManageAccount.WRAP_COLUMN, ToolMessages.ERR_MANAGE_ACCT_CANNOT_CREATE_CONNECTION_POOL.get(this.getToolName(), le.getMessage()));
            return le.getResultCode();
        }
        try {
            this.outputWriter = new LDIFWriter(this.getOut());
            final FileArgument rejectFile = this.parser.getFileArgument("rejectFile");
            if (rejectFile.isPresent()) {
                final BooleanArgument appendToRejectFile = this.parser.getBooleanArgument("appendToRejectFile");
                try {
                    this.rejectWriter = new LDIFWriter(new FileOutputStream(rejectFile.getValue(), appendToRejectFile.isPresent()));
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    this.wrapErr(0, ManageAccount.WRAP_COLUMN, ToolMessages.ERR_MANAGE_ACCT_CANNOT_CREATE_REJECT_WRITER.get(rejectFile.getValue().getAbsolutePath(), StaticUtils.getExceptionMessage(e3)));
                    return ResultCode.LOCAL_ERROR;
                }
            }
            ManageAccountProcessor processor;
            try {
                processor = new ManageAccountProcessor(this, pool, this.rateLimiter, this.outputWriter, this.rejectWriter);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                this.wrapErr(0, ManageAccount.WRAP_COLUMN, ToolMessages.ERR_MANAGE_ACCT_CANNOT_CREATE_PROCESSOR.get(StaticUtils.getExceptionMessage(le2)));
                return le2.getResultCode();
            }
            if (this.rateAdjustor != null) {
                this.rateAdjustor.start();
            }
            final DNArgument targetDN = this.parser.getDNArgument("targetDN");
            if (targetDN.isPresent()) {
                for (final DN dn : targetDN.getValues()) {
                    if (this.cancelRequested()) {
                        return ResultCode.USER_CANCELED;
                    }
                    processor.process(dn.toString());
                }
            }
            final FileArgument dnInputFile = this.parser.getFileArgument("dnInputFile");
            if (dnInputFile.isPresent()) {
                for (final File f : dnInputFile.getValues()) {
                    DNFileReader reader = null;
                    try {
                        reader = new DNFileReader(f);
                        while (!this.cancelRequested()) {
                            DN dn2;
                            try {
                                dn2 = reader.readDN();
                            }
                            catch (final LDAPException le3) {
                                Debug.debugException(le3);
                                processor.handleMessage(le3.getMessage(), true);
                                continue;
                            }
                            if (dn2 != null) {
                                processor.process(dn2.toString());
                            }
                        }
                        return ResultCode.USER_CANCELED;
                    }
                    catch (final Exception e4) {
                        Debug.debugException(e4);
                        processor.handleMessage(ToolMessages.ERR_MANAGE_ACCT_ERROR_READING_DN_FILE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e4)), true);
                        if (reader == null) {
                            continue;
                        }
                        try {
                            reader.close();
                        }
                        catch (final Exception e5) {
                            Debug.debugException(e5);
                        }
                    }
                    finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            }
                            catch (final Exception e6) {
                                Debug.debugException(e6);
                            }
                        }
                    }
                }
            }
            final FilterArgument targetFilter = this.parser.getFilterArgument("targetFilter");
            if (targetFilter.isPresent()) {
                this.searchProcessor = new ManageAccountSearchProcessor(this, processor, pool);
                for (final Filter f2 : targetFilter.getValues()) {
                    this.searchProcessor.processFilter(f2);
                }
            }
            final FileArgument filterInputFile = this.parser.getFileArgument("filterInputFile");
            if (filterInputFile.isPresent()) {
                if (this.searchProcessor == null) {
                    this.searchProcessor = new ManageAccountSearchProcessor(this, processor, pool);
                }
                for (final File f3 : filterInputFile.getValues()) {
                    FilterFileReader reader2 = null;
                    try {
                        reader2 = new FilterFileReader(f3);
                        while (!this.cancelRequested()) {
                            Filter filter;
                            try {
                                filter = reader2.readFilter();
                            }
                            catch (final LDAPException le4) {
                                Debug.debugException(le4);
                                processor.handleMessage(le4.getMessage(), true);
                                continue;
                            }
                            if (filter != null) {
                                this.searchProcessor.processFilter(filter);
                            }
                        }
                        return ResultCode.USER_CANCELED;
                    }
                    catch (final Exception e7) {
                        Debug.debugException(e7);
                        processor.handleMessage(ToolMessages.ERR_MANAGE_ACCT_ERROR_READING_FILTER_FILE.get(f3.getAbsolutePath(), StaticUtils.getExceptionMessage(e7)), true);
                        if (reader2 == null) {
                            continue;
                        }
                        try {
                            reader2.close();
                        }
                        catch (final Exception e8) {
                            Debug.debugException(e8);
                        }
                    }
                    finally {
                        if (reader2 != null) {
                            try {
                                reader2.close();
                            }
                            catch (final Exception e9) {
                                Debug.debugException(e9);
                            }
                        }
                    }
                }
            }
            final StringArgument targetUserID = this.parser.getStringArgument("targetUserID");
            if (targetUserID.isPresent()) {
                if (this.searchProcessor == null) {
                    this.searchProcessor = new ManageAccountSearchProcessor(this, processor, pool);
                }
                for (final String userID : targetUserID.getValues()) {
                    this.searchProcessor.processUserID(userID);
                }
            }
            final FileArgument userIDInputFile = this.parser.getFileArgument("userIDInputFile");
            if (userIDInputFile.isPresent()) {
                if (this.searchProcessor == null) {
                    this.searchProcessor = new ManageAccountSearchProcessor(this, processor, pool);
                }
                for (final File f4 : userIDInputFile.getValues()) {
                    BufferedReader reader3 = null;
                    try {
                        reader3 = new BufferedReader(new FileReader(f4));
                        while (!this.cancelRequested()) {
                            final String line = reader3.readLine();
                            if (line != null) {
                                if (line.length() == 0) {
                                    continue;
                                }
                                if (line.startsWith("#")) {
                                    continue;
                                }
                                this.searchProcessor.processUserID(line.trim());
                            }
                        }
                        return ResultCode.USER_CANCELED;
                    }
                    catch (final Exception e10) {
                        Debug.debugException(e10);
                        processor.handleMessage(ToolMessages.ERR_MANAGE_ACCT_ERROR_READING_USER_ID_FILE.get(f4.getAbsolutePath(), StaticUtils.getExceptionMessage(e10)), true);
                        if (reader3 == null) {
                            continue;
                        }
                        try {
                            reader3.close();
                        }
                        catch (final Exception e11) {
                            Debug.debugException(e11);
                        }
                    }
                    finally {
                        if (reader3 != null) {
                            try {
                                reader3.close();
                            }
                            catch (final Exception e12) {
                                Debug.debugException(e12);
                            }
                        }
                    }
                }
            }
            this.allFiltersProvided.set(true);
            if (this.searchProcessor != null) {
                this.searchProcessor.waitForCompletion();
            }
            this.allDNsProvided.set(true);
            processor.waitForCompletion();
        }
        finally {
            pool.close();
            if (this.rejectWriter != null) {
                try {
                    this.rejectWriter.close();
                }
                catch (final Exception e13) {
                    Debug.debugException(e13);
                }
            }
        }
        return ResultCode.SUCCESS;
    }
    
    ArgumentParser getArgumentParser() {
        return this.parser;
    }
    
    boolean cancelRequested() {
        return this.cancelRequested.get();
    }
    
    boolean allDNsProvided() {
        return this.allDNsProvided.get();
    }
    
    boolean allFiltersProvided() {
        return this.allFiltersProvided.get();
    }
    
    @Override
    protected boolean registerShutdownHook() {
        return true;
    }
    
    @Override
    protected void doShutdownHookProcessing(final ResultCode resultCode) {
        this.cancelRequested.set(true);
        if (this.rateLimiter != null) {
            this.rateLimiter.shutdownRequested();
        }
        if (this.searchProcessor != null) {
            this.searchProcessor.cancelSearches();
        }
    }
    
    @Override
    public void handleUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        final String message = ToolMessages.NOTE_MANAGE_ACCT_UNSOLICITED_NOTIFICATION.get(String.valueOf(connection), String.valueOf(notification));
        if (this.outputWriter == null) {
            this.err(new Object[0]);
            this.err("* " + message);
            this.err(new Object[0]);
        }
        else {
            try {
                this.outputWriter.writeComment(message, true, true);
                this.outputWriter.flush();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(4));
        createSubCommandExample(examples, ManageAccountSubCommandType.GET_ALL, ToolMessages.INFO_MANAGE_ACCT_SC_GET_ALL_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), new String[0]);
        createSubCommandExample(examples, ManageAccountSubCommandType.GET_ACCOUNT_USABILITY_ERRORS, ToolMessages.INFO_MANAGE_ACCT_SC_GET_USABILITY_ERRORS_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), new String[0]);
        createSubCommandExample(examples, ManageAccountSubCommandType.SET_ACCOUNT_IS_DISABLED, ToolMessages.INFO_MANAGE_ACCT_SC_SET_IS_DISABLED_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), "--accountIsDisabled", "true");
        createSubCommandExample(examples, ManageAccountSubCommandType.CLEAR_AUTHENTICATION_FAILURE_TIMES, ToolMessages.INFO_MANAGE_ACCT_SC_CLEAR_AUTH_FAILURE_TIMES_EXAMPLE.get("uid=jdoe,ou=People,dc=example,dc=com"), new String[0]);
        return examples;
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
        DEFAULT_BASE_DN = DN.NULL_DN;
    }
}
