package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Collection;
import com.unboundid.util.Validator;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TransactionSettingsRequestControl extends Control
{
    public static final String TRANSACTION_SETTINGS_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.38";
    private static final byte TYPE_TXN_NAME = Byte.MIN_VALUE;
    private static final byte TYPE_COMMIT_DURABILITY = -127;
    private static final byte TYPE_BACKEND_LOCK_BEHAVIOR = -126;
    private static final byte TYPE_BACKEND_LOCK_TIMEOUT = -125;
    private static final byte TYPE_RETRY_ATTEMPTS = -124;
    private static final byte TYPE_TXN_LOCK_TIMEOUT = -91;
    private static final byte TYPE_RETURN_RESPONSE_CONTROL = -122;
    private static final long serialVersionUID = -4749344077745581287L;
    private final boolean returnResponseControl;
    private final Integer retryAttempts;
    private final Long backendLockTimeoutMillis;
    private final Long maxTxnLockTimeoutMillis;
    private final Long minTxnLockTimeoutMillis;
    private final String transactionName;
    private final TransactionSettingsBackendLockBehavior backendLockBehavior;
    private final TransactionSettingsCommitDurability commitDurability;
    
    public TransactionSettingsRequestControl(final boolean isCritical, final String transactionName, final TransactionSettingsCommitDurability commitDurability, final TransactionSettingsBackendLockBehavior backendLockBehavior, final Long backendLockTimeoutMillis, final Integer retryAttempts, final Long minTxnLockTimeoutMillis, final Long maxTxnLockTimeoutMillis) {
        this(isCritical, transactionName, commitDurability, backendLockBehavior, backendLockTimeoutMillis, retryAttempts, minTxnLockTimeoutMillis, maxTxnLockTimeoutMillis, false);
    }
    
    public TransactionSettingsRequestControl(final boolean isCritical, final String transactionName, final TransactionSettingsCommitDurability commitDurability, final TransactionSettingsBackendLockBehavior backendLockBehavior, final Long backendLockTimeoutMillis, final Integer retryAttempts, final Long minTxnLockTimeoutMillis, final Long maxTxnLockTimeoutMillis, final boolean returnResponseControl) {
        super("1.3.6.1.4.1.30221.2.5.38", isCritical, encodeValue(transactionName, commitDurability, backendLockBehavior, backendLockTimeoutMillis, retryAttempts, minTxnLockTimeoutMillis, maxTxnLockTimeoutMillis, returnResponseControl));
        this.transactionName = transactionName;
        this.commitDurability = commitDurability;
        this.backendLockBehavior = backendLockBehavior;
        this.backendLockTimeoutMillis = backendLockTimeoutMillis;
        this.minTxnLockTimeoutMillis = minTxnLockTimeoutMillis;
        this.maxTxnLockTimeoutMillis = maxTxnLockTimeoutMillis;
        this.retryAttempts = retryAttempts;
        this.returnResponseControl = returnResponseControl;
    }
    
    public TransactionSettingsRequestControl(final Control c) throws LDAPException {
        super(c);
        final ASN1OctetString value = c.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_MISSING_VALUE.get());
        }
        try {
            boolean responseControl = false;
            Integer numRetries = null;
            Long backendTimeout = null;
            Long maxTxnLockTimeout = null;
            Long minTxnLockTimeout = null;
            String txnName = null;
            TransactionSettingsCommitDurability durability = null;
            TransactionSettingsBackendLockBehavior lockBehavior = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        txnName = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        durability = TransactionSettingsCommitDurability.valueOf(ASN1Enumerated.decodeAsEnumerated(e).intValue());
                        if (durability == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_UNKNOWN_DURABILITY.get(ASN1Enumerated.decodeAsEnumerated(e).intValue()));
                        }
                        break;
                    }
                    case -126: {
                        lockBehavior = TransactionSettingsBackendLockBehavior.valueOf(ASN1Enumerated.decodeAsEnumerated(e).intValue());
                        if (lockBehavior == null) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_UNKNOWN_LOCK_BEHAVIOR.get(ASN1Enumerated.decodeAsEnumerated(e).intValue()));
                        }
                        break;
                    }
                    case -125: {
                        backendTimeout = ASN1Long.decodeAsLong(e).longValue();
                        if (backendTimeout < 0L) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_INVALID_BACKEND_LOCK_TIMEOUT.get(backendTimeout));
                        }
                        break;
                    }
                    case -124: {
                        numRetries = ASN1Integer.decodeAsInteger(e).intValue();
                        if (numRetries < 0) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_INVALID_RETRY_ATTEMPTS.get(numRetries));
                        }
                        break;
                    }
                    case -91: {
                        final ASN1Element[] timeoutElements = ASN1Sequence.decodeAsSequence(e).elements();
                        minTxnLockTimeout = ASN1Long.decodeAsLong(timeoutElements[0]).longValue();
                        maxTxnLockTimeout = ASN1Long.decodeAsLong(timeoutElements[1]).longValue();
                        if (minTxnLockTimeout < 0L) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_INVALID_MIN_TXN_LOCK_TIMEOUT.get(minTxnLockTimeout));
                        }
                        if (maxTxnLockTimeout < minTxnLockTimeout) {
                            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_INVALID_MAX_TXN_LOCK_TIMEOUT.get(maxTxnLockTimeout, minTxnLockTimeout));
                        }
                        break;
                    }
                    case -122: {
                        responseControl = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_UNRECOGNIZED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.transactionName = txnName;
            this.commitDurability = durability;
            this.backendLockBehavior = lockBehavior;
            this.backendLockTimeoutMillis = backendTimeout;
            this.minTxnLockTimeoutMillis = minTxnLockTimeout;
            this.maxTxnLockTimeoutMillis = maxTxnLockTimeout;
            this.retryAttempts = numRetries;
            this.returnResponseControl = responseControl;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_TXN_SETTINGS_REQUEST_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final String transactionName, final TransactionSettingsCommitDurability commitDurability, final TransactionSettingsBackendLockBehavior backendLockBehavior, final Long backendLockTimeoutMillis, final Integer retryAttempts, final Long minTxnLockTimeoutMillis, final Long maxTxnLockTimeoutMillis, final boolean returnResponseControl) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(7);
        if (transactionName != null) {
            elements.add(new ASN1OctetString((byte)(-128), transactionName));
        }
        if (commitDurability != null) {
            elements.add(new ASN1Enumerated((byte)(-127), commitDurability.intValue()));
        }
        if (backendLockBehavior != null) {
            elements.add(new ASN1Enumerated((byte)(-126), backendLockBehavior.intValue()));
        }
        if (backendLockTimeoutMillis != null) {
            Validator.ensureTrue(backendLockTimeoutMillis >= 0L, "If a backend lock timeout is specified, then it must be greater than or equal to zero.");
            elements.add(new ASN1Long((byte)(-125), backendLockTimeoutMillis));
        }
        if (retryAttempts != null) {
            Validator.ensureTrue(retryAttempts >= 0, "If specified, the number of retry attempts must be greater than or equal to zero.");
            elements.add(new ASN1Integer((byte)(-124), retryAttempts));
        }
        if (minTxnLockTimeoutMillis != null) {
            Validator.ensureTrue(maxTxnLockTimeoutMillis != null, "If a minimum transaction lock timeout is specified, then a maximum transaction lock timeout must also be specified.");
            Validator.ensureTrue(minTxnLockTimeoutMillis > 0L, "If a minimum transaction lock timeout is specified, then it must be greater than zero.");
            Validator.ensureTrue(maxTxnLockTimeoutMillis >= minTxnLockTimeoutMillis, "If a minimum transaction lock timeout is specified, then it must be less than or equal to the minimum transaction lock timeout.");
            elements.add(new ASN1Sequence((byte)(-91), new ASN1Element[] { new ASN1Long(minTxnLockTimeoutMillis), new ASN1Long(maxTxnLockTimeoutMillis) }));
        }
        else {
            Validator.ensureTrue(maxTxnLockTimeoutMillis == null, "If a maximum transaction lock timeout is specified, then a minimum transaction lock timeout must also be specified.");
        }
        if (returnResponseControl) {
            elements.add(new ASN1Boolean((byte)(-122), true));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getTransactionName() {
        return this.transactionName;
    }
    
    public TransactionSettingsCommitDurability getCommitDurability() {
        return this.commitDurability;
    }
    
    public TransactionSettingsBackendLockBehavior getBackendLockBehavior() {
        return this.backendLockBehavior;
    }
    
    public Long getBackendLockTimeoutMillis() {
        return this.backendLockTimeoutMillis;
    }
    
    public Integer getRetryAttempts() {
        return this.retryAttempts;
    }
    
    public Long getMinTxnLockTimeoutMillis() {
        return this.minTxnLockTimeoutMillis;
    }
    
    public Long getMaxTxnLockTimeoutMillis() {
        return this.maxTxnLockTimeoutMillis;
    }
    
    public boolean returnResponseControl() {
        return this.returnResponseControl;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_TXN_SETTINGS_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("TransactionSettingsRequestControl(isCritical=");
        buffer.append(this.isCritical());
        if (this.transactionName != null) {
            buffer.append(", transactionName='");
            buffer.append(this.transactionName);
            buffer.append('\'');
        }
        if (this.commitDurability != null) {
            buffer.append(", commitDurability='");
            buffer.append(this.commitDurability.name());
            buffer.append('\'');
        }
        if (this.backendLockBehavior != null) {
            buffer.append(", backendLockBehavior='");
            buffer.append(this.backendLockBehavior.name());
            buffer.append('\'');
        }
        if (this.backendLockTimeoutMillis != null) {
            buffer.append(", backendLockTimeoutMillis=");
            buffer.append(this.backendLockTimeoutMillis);
        }
        if (this.retryAttempts != null) {
            buffer.append(", retryAttempts=");
            buffer.append(this.retryAttempts);
        }
        if (this.minTxnLockTimeoutMillis != null) {
            buffer.append(", minTxnLockTimeoutMillis=");
            buffer.append(this.minTxnLockTimeoutMillis);
        }
        if (this.maxTxnLockTimeoutMillis != null) {
            buffer.append(", maxTxnLockTimeoutMillis=");
            buffer.append(this.maxTxnLockTimeoutMillis);
        }
        buffer.append(", returnResponseControl=");
        buffer.append(this.returnResponseControl);
        buffer.append(')');
    }
}
