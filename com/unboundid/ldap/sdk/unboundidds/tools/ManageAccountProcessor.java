package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.TimestampArgument;
import java.util.Date;
import com.unboundid.util.args.BooleanValueArgument;
import com.unboundid.util.args.SubCommand;
import java.util.Iterator;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityError;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityWarning;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateAccountUsabilityNotice;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedResult;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateOperation;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.util.FixedRateBarrier;
import com.unboundid.util.args.ArgumentParser;

final class ManageAccountProcessor
{
    private final ArgumentParser parser;
    private final boolean suppressEmptyResultOperations;
    private final FixedRateBarrier rateLimiter;
    private final LDAPConnectionPool pool;
    private final LDIFWriter outputWriter;
    private final LDIFWriter rejectWriter;
    private final LinkedBlockingQueue<String> dnQueue;
    private final List<ManageAccountProcessorThread> processorThreads;
    private final ManageAccount manageAccount;
    private final PasswordPolicyStateOperation pwpStateOperation;
    private final String commandLine;
    
    ManageAccountProcessor(final ManageAccount manageAccount, final LDAPConnectionPool pool, final FixedRateBarrier rateLimiter, final LDIFWriter outputWriter, final LDIFWriter rejectWriter) throws LDAPException {
        this.manageAccount = manageAccount;
        this.pool = pool;
        this.rateLimiter = rateLimiter;
        this.outputWriter = outputWriter;
        this.rejectWriter = rejectWriter;
        this.parser = manageAccount.getArgumentParser();
        this.suppressEmptyResultOperations = this.parser.getBooleanArgument("suppressEmptyResultOperations").isPresent();
        final StringBuilder commandBuffer = new StringBuilder();
        this.pwpStateOperation = this.createPasswordPolicyStateOperation(commandBuffer);
        this.commandLine = commandBuffer.toString();
        final int numThreads = this.parser.getIntegerArgument("numThreads").getValue();
        if (numThreads > 1) {
            this.dnQueue = new LinkedBlockingQueue<String>(100);
            this.processorThreads = new ArrayList<ManageAccountProcessorThread>(numThreads);
            for (int i = 1; i <= numThreads; ++i) {
                final ManageAccountProcessorThread processorThread = new ManageAccountProcessorThread(i, this);
                processorThread.start();
                this.processorThreads.add(processorThread);
            }
        }
        else {
            this.dnQueue = null;
            this.processorThreads = Collections.emptyList();
        }
    }
    
    void process(final String dn) {
        if (this.dnQueue == null) {
            if (this.pwpStateOperation == null) {
                this.process(new PasswordPolicyStateExtendedRequest(dn, new PasswordPolicyStateOperation[0]));
            }
            else {
                this.process(new PasswordPolicyStateExtendedRequest(dn, new PasswordPolicyStateOperation[] { this.pwpStateOperation }));
            }
        }
        else {
            while (!this.manageAccount.cancelRequested()) {
                try {
                    if (this.dnQueue.offer(dn, 100L, TimeUnit.MILLISECONDS)) {
                        return;
                    }
                    continue;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
    }
    
    PasswordPolicyStateExtendedRequest getRequest() {
        if (this.manageAccount.cancelRequested()) {
            return null;
        }
        String dn = this.dnQueue.poll();
        while (dn == null) {
            if (this.manageAccount.cancelRequested()) {
                return null;
            }
            if (this.manageAccount.allDNsProvided()) {
                dn = this.dnQueue.poll();
                if (dn == null) {
                    return null;
                }
                break;
            }
            else {
                try {
                    dn = this.dnQueue.poll(100L, TimeUnit.MILLISECONDS);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (!(e instanceof InterruptedException)) {
                        continue;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (this.pwpStateOperation == null) {
            return new PasswordPolicyStateExtendedRequest(dn, new PasswordPolicyStateOperation[0]);
        }
        return new PasswordPolicyStateExtendedRequest(dn, new PasswordPolicyStateOperation[] { this.pwpStateOperation });
    }
    
    void process(final PasswordPolicyStateExtendedRequest request) {
        LDAPConnection conn;
        try {
            conn = this.pool.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.handleResult(request, le);
            return;
        }
        boolean alreadyReleased = false;
        boolean releaseAsDefunct = true;
        try {
            if (this.rateLimiter != null) {
                this.rateLimiter.await();
            }
            PasswordPolicyStateExtendedResult result;
            try {
                result = (PasswordPolicyStateExtendedResult)conn.processExtendedOperation(request);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                try {
                    result = new PasswordPolicyStateExtendedResult(new ExtendedResult(le2));
                }
                catch (final LDAPException le3) {
                    Debug.debugException(le3);
                    result = null;
                }
            }
            if (result != null && result.getResultCode().isConnectionUsable()) {
                this.handleResult(request, result);
                releaseAsDefunct = false;
                return;
            }
            try {
                alreadyReleased = true;
                conn = this.pool.replaceDefunctConnection(conn);
                alreadyReleased = false;
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                this.handleResult(request, le2);
                return;
            }
            try {
                result = (PasswordPolicyStateExtendedResult)conn.processExtendedOperation(request);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                try {
                    result = new PasswordPolicyStateExtendedResult(new ExtendedResult(le2));
                }
                catch (final LDAPException le3) {
                    Debug.debugException(le3);
                    this.handleResult(request, le2);
                    return;
                }
            }
            if (result.getResultCode().isConnectionUsable()) {
                releaseAsDefunct = false;
            }
            this.handleResult(request, result);
        }
        finally {
            if (!alreadyReleased) {
                if (releaseAsDefunct) {
                    this.pool.releaseDefunctConnection(conn);
                }
                else {
                    this.pool.releaseConnection(conn);
                }
            }
        }
    }
    
    private void handleResult(final PasswordPolicyStateExtendedRequest request, final LDAPException le) {
        try {
            final PasswordPolicyStateExtendedResult result = new PasswordPolicyStateExtendedResult(new ExtendedResult(le));
            this.handleResult(request, result);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.handleResult(this.createResultEntry(request, le.toLDAPResult()), true);
        }
    }
    
    private void handleResult(final PasswordPolicyStateExtendedRequest request, final PasswordPolicyStateExtendedResult result) {
        this.handleResult(this.createResultEntry(request, result), result.getResultCode() != ResultCode.SUCCESS);
    }
    
    void handleMessage(final String message, final boolean isFailure) {
        synchronized (this.outputWriter) {
            try {
                this.outputWriter.writeComment(message, true, true);
                this.outputWriter.flush();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        if (isFailure && this.rejectWriter != null) {
            synchronized (this.rejectWriter) {
                try {
                    this.rejectWriter.writeComment(message, true, true);
                    this.rejectWriter.flush();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
    }
    
    private Entry createResultEntry(final PasswordPolicyStateExtendedRequest request, final LDAPResult result) {
        final Entry e = new Entry(request.getUserDN());
        e.addAttribute("base-command-line", this.commandLine + " --targetDN " + StaticUtils.cleanExampleCommandLineArgument(e.getDN()));
        e.addAttribute("result-code", String.valueOf(result.getResultCode().intValue()));
        final String resultCodeName = result.getResultCode().getName();
        if (resultCodeName != null) {
            e.addAttribute("result-code-name", resultCodeName);
        }
        final String diagnosticMessage = result.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            e.addAttribute("diagnostic-message", diagnosticMessage);
        }
        final String matchedDN = result.getMatchedDN();
        if (matchedDN != null) {
            e.addAttribute("matched-dn", matchedDN);
        }
        final String[] referralURLs = result.getReferralURLs();
        if (referralURLs != null && referralURLs.length > 0) {
            e.addAttribute("referral-url", referralURLs);
        }
        if (!(result instanceof PasswordPolicyStateExtendedResult)) {
            return e;
        }
        final PasswordPolicyStateExtendedResult r = (PasswordPolicyStateExtendedResult)result;
        for (final PasswordPolicyStateOperation o : r.getOperations()) {
            final String[] values = o.getStringValues();
            if (values.length == 0 && this.suppressEmptyResultOperations) {
                continue;
            }
            final ManageAccountSubCommandType subcommandType = ManageAccountSubCommandType.forOperationType(o.getOperationType());
            String attrName;
            if (subcommandType == null) {
                if (o.getOperationType() == 39) {
                    attrName = "get-password-history";
                }
                else {
                    attrName = "unrecognized-operation-type-" + o.getOperationType();
                }
            }
            else {
                attrName = subcommandType.getPrimaryName();
            }
            if (values.length == 0) {
                e.addAttribute(attrName, "");
            }
            else {
                switch (subcommandType) {
                    case GET_ACCOUNT_USABILITY_NOTICES: {
                        final String[] notices = new String[values.length];
                        for (int i = 0; i < values.length; ++i) {
                            try {
                                notices[i] = new PasswordPolicyStateAccountUsabilityNotice(values[i]).getMessage();
                            }
                            catch (final Exception ex) {
                                Debug.debugException(ex);
                                notices[i] = values[i];
                            }
                        }
                        e.addAttribute(attrName, notices);
                        continue;
                    }
                    case GET_ACCOUNT_USABILITY_WARNINGS: {
                        final String[] warnings = new String[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            try {
                                warnings[j] = new PasswordPolicyStateAccountUsabilityWarning(values[j]).getMessage();
                            }
                            catch (final Exception ex2) {
                                Debug.debugException(ex2);
                                warnings[j] = values[j];
                            }
                        }
                        e.addAttribute(attrName, warnings);
                        continue;
                    }
                    case GET_ACCOUNT_USABILITY_ERRORS: {
                        final String[] errors = new String[values.length];
                        for (int k = 0; k < values.length; ++k) {
                            try {
                                errors[k] = new PasswordPolicyStateAccountUsabilityError(values[k]).getMessage();
                            }
                            catch (final Exception ex3) {
                                Debug.debugException(ex3);
                                errors[k] = values[k];
                            }
                        }
                        e.addAttribute(attrName, errors);
                        continue;
                    }
                    default: {
                        e.addAttribute(attrName, values);
                        continue;
                    }
                }
            }
        }
        return e;
    }
    
    private void handleResult(final Entry resultEntry, final boolean isFailure) {
        synchronized (this.outputWriter) {
            try {
                this.outputWriter.writeEntry(resultEntry);
                this.outputWriter.flush();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        if (isFailure && this.rejectWriter != null) {
            synchronized (this.rejectWriter) {
                try {
                    this.rejectWriter.writeEntry(resultEntry);
                    this.rejectWriter.flush();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
    }
    
    private PasswordPolicyStateOperation createPasswordPolicyStateOperation(final StringBuilder commandBuffer) throws LDAPException {
        final SubCommand subcommand = this.parser.getSelectedSubCommand();
        if (subcommand == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_MANAGE_ACCT_PROCESSOR_NO_SUBCOMMAND.get(this.manageAccount.getToolName()));
        }
        final ManageAccountSubCommandType subcommandType = ManageAccountSubCommandType.forName(subcommand.getPrimaryName());
        if (subcommandType == null) {
            throw new LDAPException(ResultCode.LOCAL_ERROR, ToolMessages.ERR_MANAGE_ACCT_PROCESSOR_UNSUPPORTED_SUBCOMMAND.get(subcommand.getPrimaryName(), this.manageAccount.getToolName()));
        }
        commandBuffer.append(this.manageAccount.getToolName());
        commandBuffer.append(' ');
        commandBuffer.append(subcommandType.getPrimaryName());
        switch (subcommandType) {
            case GET_ALL: {
                return null;
            }
            case GET_PASSWORD_POLICY_DN: {
                return PasswordPolicyStateOperation.createGetPasswordPolicyDNOperation();
            }
            case GET_ACCOUNT_IS_USABLE: {
                return PasswordPolicyStateOperation.createGetAccountIsUsableOperation();
            }
            case GET_ACCOUNT_USABILITY_NOTICES: {
                return PasswordPolicyStateOperation.createGetAccountUsabilityNoticesOperation();
            }
            case GET_ACCOUNT_USABILITY_WARNINGS: {
                return PasswordPolicyStateOperation.createGetAccountUsabilityWarningsOperation();
            }
            case GET_ACCOUNT_USABILITY_ERRORS: {
                return PasswordPolicyStateOperation.createGetAccountUsabilityErrorsOperation();
            }
            case GET_PASSWORD_CHANGED_TIME: {
                return PasswordPolicyStateOperation.createGetPasswordChangedTimeOperation();
            }
            case SET_PASSWORD_CHANGED_TIME: {
                return PasswordPolicyStateOperation.createSetPasswordChangedTimeOperation(getDate(subcommand, commandBuffer));
            }
            case CLEAR_PASSWORD_CHANGED_TIME: {
                return PasswordPolicyStateOperation.createClearPasswordChangedTimeOperation();
            }
            case GET_ACCOUNT_IS_DISABLED: {
                return PasswordPolicyStateOperation.createGetAccountDisabledStateOperation();
            }
            case SET_ACCOUNT_IS_DISABLED: {
                return PasswordPolicyStateOperation.createSetAccountDisabledStateOperation(getBoolean(subcommand, commandBuffer));
            }
            case CLEAR_ACCOUNT_IS_DISABLED: {
                return PasswordPolicyStateOperation.createClearAccountDisabledStateOperation();
            }
            case GET_ACCOUNT_ACTIVATION_TIME: {
                return PasswordPolicyStateOperation.createGetAccountActivationTimeOperation();
            }
            case SET_ACCOUNT_ACTIVATION_TIME: {
                return PasswordPolicyStateOperation.createSetAccountActivationTimeOperation(getDate(subcommand, commandBuffer));
            }
            case CLEAR_ACCOUNT_ACTIVATION_TIME: {
                return PasswordPolicyStateOperation.createClearAccountActivationTimeOperation();
            }
            case GET_SECONDS_UNTIL_ACCOUNT_ACTIVATION: {
                return PasswordPolicyStateOperation.createGetSecondsUntilAccountActivationOperation();
            }
            case GET_ACCOUNT_IS_NOT_YET_ACTIVE: {
                return PasswordPolicyStateOperation.createGetAccountIsNotYetActiveOperation();
            }
            case GET_ACCOUNT_EXPIRATION_TIME: {
                return PasswordPolicyStateOperation.createGetAccountExpirationTimeOperation();
            }
            case SET_ACCOUNT_EXPIRATION_TIME: {
                return PasswordPolicyStateOperation.createSetAccountExpirationTimeOperation(getDate(subcommand, commandBuffer));
            }
            case CLEAR_ACCOUNT_EXPIRATION_TIME: {
                return PasswordPolicyStateOperation.createClearAccountExpirationTimeOperation();
            }
            case GET_SECONDS_UNTIL_ACCOUNT_EXPIRATION: {
                return PasswordPolicyStateOperation.createGetSecondsUntilAccountExpirationOperation();
            }
            case GET_ACCOUNT_IS_EXPIRED: {
                return PasswordPolicyStateOperation.createGetAccountIsExpiredOperation();
            }
            case GET_PASSWORD_EXPIRATION_WARNED_TIME: {
                return PasswordPolicyStateOperation.createGetPasswordExpirationWarnedTimeOperation();
            }
            case SET_PASSWORD_EXPIRATION_WARNED_TIME: {
                return PasswordPolicyStateOperation.createSetPasswordExpirationWarnedTimeOperation(getDate(subcommand, commandBuffer));
            }
            case CLEAR_PASSWORD_EXPIRATION_WARNED_TIME: {
                return PasswordPolicyStateOperation.createClearPasswordExpirationWarnedTimeOperation();
            }
            case GET_SECONDS_UNTIL_PASSWORD_EXPIRATION_WARNING: {
                return PasswordPolicyStateOperation.createGetSecondsUntilPasswordExpirationWarningOperation();
            }
            case GET_PASSWORD_EXPIRATION_TIME: {
                return PasswordPolicyStateOperation.createGetPasswordExpirationTimeOperation();
            }
            case GET_SECONDS_UNTIL_PASSWORD_EXPIRATION: {
                return PasswordPolicyStateOperation.createGetSecondsUntilPasswordExpirationOperation();
            }
            case GET_PASSWORD_IS_EXPIRED: {
                return PasswordPolicyStateOperation.createGetPasswordIsExpiredOperation();
            }
            case GET_ACCOUNT_IS_FAILURE_LOCKED: {
                return PasswordPolicyStateOperation.createGetAccountIsFailureLockedOperation();
            }
            case SET_ACCOUNT_IS_FAILURE_LOCKED: {
                return PasswordPolicyStateOperation.createSetAccountIsFailureLockedOperation(getBoolean(subcommand, commandBuffer));
            }
            case GET_FAILURE_LOCKOUT_TIME: {
                return PasswordPolicyStateOperation.createGetFailureLockoutTimeOperation();
            }
            case GET_SECONDS_UNTIL_AUTHENTICATION_FAILURE_UNLOCK: {
                return PasswordPolicyStateOperation.createGetSecondsUntilAuthenticationFailureUnlockOperation();
            }
            case GET_AUTHENTICATION_FAILURE_TIMES: {
                return PasswordPolicyStateOperation.createGetAuthenticationFailureTimesOperation();
            }
            case ADD_AUTHENTICATION_FAILURE_TIME: {
                return PasswordPolicyStateOperation.createAddAuthenticationFailureTimeOperation(getDates(subcommand, commandBuffer));
            }
            case SET_AUTHENTICATION_FAILURE_TIMES: {
                return PasswordPolicyStateOperation.createSetAuthenticationFailureTimesOperation(getDates(subcommand, commandBuffer));
            }
            case CLEAR_AUTHENTICATION_FAILURE_TIMES: {
                return PasswordPolicyStateOperation.createClearAuthenticationFailureTimesOperation();
            }
            case GET_REMAINING_AUTHENTICATION_FAILURE_COUNT: {
                return PasswordPolicyStateOperation.createGetRemainingAuthenticationFailureCountOperation();
            }
            case GET_ACCOUNT_IS_IDLE_LOCKED: {
                return PasswordPolicyStateOperation.createGetAccountIsIdleLockedOperation();
            }
            case GET_SECONDS_UNTIL_IDLE_LOCKOUT: {
                return PasswordPolicyStateOperation.createGetSecondsUntilIdleLockoutOperation();
            }
            case GET_IDLE_LOCKOUT_TIME: {
                return PasswordPolicyStateOperation.createGetIdleLockoutTimeOperation();
            }
            case GET_MUST_CHANGE_PASSWORD: {
                return PasswordPolicyStateOperation.createGetPasswordResetStateOperation();
            }
            case SET_MUST_CHANGE_PASSWORD: {
                return PasswordPolicyStateOperation.createSetPasswordResetStateOperation(getBoolean(subcommand, commandBuffer));
            }
            case CLEAR_MUST_CHANGE_PASSWORD: {
                return PasswordPolicyStateOperation.createClearPasswordResetStateOperation();
            }
            case GET_ACCOUNT_IS_PASSWORD_RESET_LOCKED: {
                return PasswordPolicyStateOperation.createGetAccountIsResetLockedOperation();
            }
            case GET_SECONDS_UNTIL_PASSWORD_RESET_LOCKOUT: {
                return PasswordPolicyStateOperation.createGetSecondsUntilPasswordResetLockoutOperation();
            }
            case GET_PASSWORD_RESET_LOCKOUT_TIME: {
                return PasswordPolicyStateOperation.createGetResetLockoutTimeOperation();
            }
            case GET_LAST_LOGIN_TIME: {
                return PasswordPolicyStateOperation.createGetLastLoginTimeOperation();
            }
            case SET_LAST_LOGIN_TIME: {
                return PasswordPolicyStateOperation.createSetLastLoginTimeOperation(getDate(subcommand, commandBuffer));
            }
            case CLEAR_LAST_LOGIN_TIME: {
                return PasswordPolicyStateOperation.createClearLastLoginTimeOperation();
            }
            case GET_LAST_LOGIN_IP_ADDRESS: {
                return PasswordPolicyStateOperation.createGetLastLoginIPAddressOperation();
            }
            case SET_LAST_LOGIN_IP_ADDRESS: {
                return PasswordPolicyStateOperation.createSetLastLoginIPAddressOperation(getString(subcommand, commandBuffer));
            }
            case CLEAR_LAST_LOGIN_IP_ADDRESS: {
                return PasswordPolicyStateOperation.createClearLastLoginIPAddressOperation();
            }
            case GET_GRACE_LOGIN_USE_TIMES: {
                return PasswordPolicyStateOperation.createGetGraceLoginUseTimesOperation();
            }
            case ADD_GRACE_LOGIN_USE_TIME: {
                return PasswordPolicyStateOperation.createAddGraceLoginUseTimeOperation(getDates(subcommand, commandBuffer));
            }
            case SET_GRACE_LOGIN_USE_TIMES: {
                return PasswordPolicyStateOperation.createSetGraceLoginUseTimesOperation(getDates(subcommand, commandBuffer));
            }
            case CLEAR_GRACE_LOGIN_USE_TIMES: {
                return PasswordPolicyStateOperation.createClearGraceLoginUseTimesOperation();
            }
            case GET_REMAINING_GRACE_LOGIN_COUNT: {
                return PasswordPolicyStateOperation.createGetRemainingGraceLoginCountOperation();
            }
            case GET_PASSWORD_CHANGED_BY_REQUIRED_TIME: {
                return PasswordPolicyStateOperation.createGetPasswordChangedByRequiredTimeOperation();
            }
            case SET_PASSWORD_CHANGED_BY_REQUIRED_TIME: {
                return PasswordPolicyStateOperation.createSetPasswordChangedByRequiredTimeOperation(getDate(subcommand, commandBuffer));
            }
            case CLEAR_PASSWORD_CHANGED_BY_REQUIRED_TIME: {
                return PasswordPolicyStateOperation.createClearPasswordChangedByRequiredTimeOperation();
            }
            case GET_SECONDS_UNTIL_REQUIRED_PASSWORD_CHANGE_TIME: {
                return PasswordPolicyStateOperation.createGetSecondsUntilRequiredChangeTimeOperation();
            }
            case GET_PASSWORD_HISTORY_COUNT: {
                return PasswordPolicyStateOperation.createGetPasswordHistoryCountOperation();
            }
            case CLEAR_PASSWORD_HISTORY: {
                return PasswordPolicyStateOperation.createClearPasswordHistoryOperation();
            }
            case GET_HAS_RETIRED_PASSWORD: {
                return PasswordPolicyStateOperation.createHasRetiredPasswordOperation();
            }
            case GET_PASSWORD_RETIRED_TIME: {
                return PasswordPolicyStateOperation.createGetPasswordRetiredTimeOperation();
            }
            case GET_RETIRED_PASSWORD_EXPIRATION_TIME: {
                return PasswordPolicyStateOperation.createGetRetiredPasswordExpirationTimeOperation();
            }
            case CLEAR_RETIRED_PASSWORD: {
                return PasswordPolicyStateOperation.createPurgeRetiredPasswordOperation();
            }
            case GET_AVAILABLE_SASL_MECHANISMS: {
                return PasswordPolicyStateOperation.createGetAvailableSASLMechanismsOperation();
            }
            case GET_AVAILABLE_OTP_DELIVERY_MECHANISMS: {
                return PasswordPolicyStateOperation.createGetAvailableOTPDeliveryMechanismsOperation();
            }
            case GET_HAS_TOTP_SHARED_SECRET: {
                return PasswordPolicyStateOperation.createHasTOTPSharedSecret();
            }
            case ADD_TOTP_SHARED_SECRET: {
                return PasswordPolicyStateOperation.createAddTOTPSharedSecretOperation(getStrings(subcommand, commandBuffer));
            }
            case REMOVE_TOTP_SHARED_SECRET: {
                return PasswordPolicyStateOperation.createRemoveTOTPSharedSecretOperation(getStrings(subcommand, commandBuffer));
            }
            case SET_TOTP_SHARED_SECRETS: {
                return PasswordPolicyStateOperation.createSetTOTPSharedSecretsOperation(getStrings(subcommand, commandBuffer));
            }
            case CLEAR_TOTP_SHARED_SECRETS: {
                return PasswordPolicyStateOperation.createClearTOTPSharedSecretsOperation();
            }
            case GET_HAS_REGISTERED_YUBIKEY_PUBLIC_ID: {
                return PasswordPolicyStateOperation.createHasYubiKeyPublicIDOperation();
            }
            case GET_REGISTERED_YUBIKEY_PUBLIC_IDS: {
                return PasswordPolicyStateOperation.createGetRegisteredYubiKeyPublicIDsOperation();
            }
            case ADD_REGISTERED_YUBIKEY_PUBLIC_ID: {
                return PasswordPolicyStateOperation.createAddRegisteredYubiKeyPublicIDOperation(getStrings(subcommand, commandBuffer));
            }
            case REMOVE_REGISTERED_YUBIKEY_PUBLIC_ID: {
                return PasswordPolicyStateOperation.createRemoveRegisteredYubiKeyPublicIDOperation(getStrings(subcommand, commandBuffer));
            }
            case SET_REGISTERED_YUBIKEY_PUBLIC_IDS: {
                return PasswordPolicyStateOperation.createSetRegisteredYubiKeyPublicIDsOperation(getStrings(subcommand, commandBuffer));
            }
            case CLEAR_REGISTERED_YUBIKEY_PUBLIC_IDS: {
                return PasswordPolicyStateOperation.createClearRegisteredYubiKeyPublicIDsOperation();
            }
            case GET_HAS_STATIC_PASSWORD: {
                return PasswordPolicyStateOperation.createHasStaticPasswordOperation();
            }
            default: {
                throw new LDAPException(ResultCode.LOCAL_ERROR, ToolMessages.ERR_MANAGE_ACCT_PROCESSOR_UNSUPPORTED_SUBCOMMAND.get(subcommand.getPrimaryName(), this.manageAccount.getToolName()));
            }
        }
    }
    
    private static boolean getBoolean(final SubCommand subcommand, final StringBuilder commandBuffer) {
        final ArgumentParser parser = subcommand.getArgumentParser();
        final BooleanValueArgument arg = parser.getBooleanValueArgument("operationValue");
        final boolean booleanValue = arg.getValue();
        if (arg.isPresent()) {
            commandBuffer.append(' ');
            commandBuffer.append(arg.getIdentifierString());
            commandBuffer.append(' ');
            commandBuffer.append(booleanValue);
        }
        return booleanValue;
    }
    
    private static Date getDate(final SubCommand subcommand, final StringBuilder commandBuffer) {
        final ArgumentParser parser = subcommand.getArgumentParser();
        final TimestampArgument arg = parser.getTimestampArgument("operationValue");
        final Date dateValue = arg.getValue();
        if (arg.isPresent()) {
            commandBuffer.append(' ');
            commandBuffer.append(arg.getIdentifierString());
            commandBuffer.append(' ');
            commandBuffer.append(StaticUtils.encodeGeneralizedTime(dateValue));
        }
        return dateValue;
    }
    
    private static Date[] getDates(final SubCommand subcommand, final StringBuilder commandBuffer) {
        final ArgumentParser parser = subcommand.getArgumentParser();
        final TimestampArgument arg = parser.getTimestampArgument("operationValue");
        final List<Date> dateList = arg.getValues();
        final Date[] dateArray = new Date[dateList.size()];
        dateList.toArray(dateArray);
        if (arg.isPresent()) {
            for (final Date d : dateArray) {
                commandBuffer.append(' ');
                commandBuffer.append(arg.getIdentifierString());
                commandBuffer.append(' ');
                commandBuffer.append(StaticUtils.encodeGeneralizedTime(d));
            }
        }
        return dateArray;
    }
    
    private static String getString(final SubCommand subcommand, final StringBuilder commandBuffer) {
        final ArgumentParser parser = subcommand.getArgumentParser();
        final StringArgument arg = parser.getStringArgument("operationValue");
        final String stringValue = arg.getValue();
        if (arg.isPresent()) {
            commandBuffer.append(' ');
            commandBuffer.append(arg.getIdentifierString());
            commandBuffer.append(' ');
            commandBuffer.append(StaticUtils.cleanExampleCommandLineArgument(stringValue));
        }
        return stringValue;
    }
    
    private static String[] getStrings(final SubCommand subcommand, final StringBuilder commandBuffer) {
        final ArgumentParser parser = subcommand.getArgumentParser();
        final StringArgument arg = parser.getStringArgument("operationValue");
        final List<String> stringList = arg.getValues();
        final String[] stringArray = new String[stringList.size()];
        stringList.toArray(stringArray);
        if (arg.isPresent()) {
            for (final String s : stringArray) {
                commandBuffer.append(' ');
                commandBuffer.append(arg.getIdentifierString());
                commandBuffer.append(' ');
                commandBuffer.append(StaticUtils.cleanExampleCommandLineArgument(s));
            }
        }
        return stringArray;
    }
    
    void waitForCompletion() {
        if (this.dnQueue == null) {
            return;
        }
        while (!this.manageAccount.cancelRequested()) {
            if (this.manageAccount.allDNsProvided() && this.dnQueue.peek() == null) {
                for (final ManageAccountProcessorThread t : this.processorThreads) {
                    try {
                        t.join();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        if (!(e instanceof InterruptedException)) {
                            continue;
                        }
                        Thread.currentThread().interrupt();
                    }
                }
                return;
            }
            try {
                Thread.sleep(10L);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
            }
        }
    }
}
