package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UniquenessResponseControl extends Control implements DecodeableControl
{
    public static final String UNIQUENESS_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.53";
    private static final byte TYPE_UNIQUENESS_ID = Byte.MIN_VALUE;
    private static final byte TYPE_PRE_COMMIT_VALIDATION_PASSED = -127;
    private static final byte TYPE_POST_COMMIT_VALIDATION_PASSED = -126;
    private static final byte TYPE_VALIDATION_MESSAGE = -125;
    private static final long serialVersionUID = 5090348902351420617L;
    private final Boolean postCommitValidationPassed;
    private final Boolean preCommitValidationPassed;
    private final String uniquenessID;
    private final String validationMessage;
    
    UniquenessResponseControl() {
        this.uniquenessID = null;
        this.preCommitValidationPassed = null;
        this.postCommitValidationPassed = null;
        this.validationMessage = null;
    }
    
    public UniquenessResponseControl(final String uniquenessID, final Boolean preCommitValidationPassed, final Boolean postCommitValidationPassed, final String validationMessage) {
        super("1.3.6.1.4.1.30221.2.5.53", false, encodeValue(uniquenessID, preCommitValidationPassed, postCommitValidationPassed, validationMessage));
        Validator.ensureNotNull(uniquenessID);
        this.uniquenessID = uniquenessID;
        this.preCommitValidationPassed = preCommitValidationPassed;
        this.postCommitValidationPassed = postCommitValidationPassed;
        this.validationMessage = validationMessage;
    }
    
    private static ASN1OctetString encodeValue(final String uniquenessID, final Boolean preCommitValidationPassed, final Boolean postCommitValidationPassed, final String validationMessage) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString((byte)(-128), uniquenessID));
        if (preCommitValidationPassed != null) {
            elements.add(new ASN1Boolean((byte)(-127), preCommitValidationPassed));
        }
        if (postCommitValidationPassed != null) {
            elements.add(new ASN1Boolean((byte)(-126), postCommitValidationPassed));
        }
        if (validationMessage != null) {
            elements.add(new ASN1OctetString((byte)(-125), validationMessage));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public UniquenessResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_RES_DECODE_NO_VALUE.get());
        }
        try {
            String id = null;
            Boolean prePassed = null;
            Boolean postPassed = null;
            String message = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        id = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        prePassed = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -126: {
                        postPassed = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                        break;
                    }
                    case -125: {
                        message = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_RES_DECODE_UNKNOWN_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            if (id == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_RES_DECODE_NO_UNIQUENESS_ID.get());
            }
            this.uniquenessID = id;
            this.preCommitValidationPassed = prePassed;
            this.postCommitValidationPassed = postPassed;
            this.validationMessage = message;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_RES_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public UniquenessResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new UniquenessResponseControl(oid, isCritical, value);
    }
    
    public static Map<String, UniquenessResponseControl> get(final LDAPResult result) throws LDAPException {
        final Control[] responseControls = result.getResponseControls();
        if (responseControls.length == 0) {
            return Collections.emptyMap();
        }
        final LinkedHashMap<String, UniquenessResponseControl> controlMap = new LinkedHashMap<String, UniquenessResponseControl>(StaticUtils.computeMapCapacity(responseControls.length));
        for (final Control c : responseControls) {
            if (c.getOID().equals("1.3.6.1.4.1.30221.2.5.53")) {
                UniquenessResponseControl urc;
                if (c instanceof UniquenessResponseControl) {
                    urc = (UniquenessResponseControl)c;
                }
                else {
                    urc = new UniquenessResponseControl().decodeControl(c.getOID(), c.isCritical(), c.getValue());
                }
                final String uniquenessID = urc.getUniquenessID();
                if (controlMap.containsKey(uniquenessID)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_UNIQUENESS_RES_GET_ID_CONFLICT.get(uniquenessID));
                }
                controlMap.put(uniquenessID, urc);
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends UniquenessResponseControl>)controlMap);
    }
    
    public boolean uniquenessConflictFound() {
        return this.preCommitValidationPassed == Boolean.FALSE || this.postCommitValidationPassed == Boolean.FALSE;
    }
    
    public String getUniquenessID() {
        return this.uniquenessID;
    }
    
    public UniquenessValidationResult getPreCommitValidationResult() {
        if (this.preCommitValidationPassed == null) {
            return UniquenessValidationResult.VALIDATION_NOT_ATTEMPTED;
        }
        if (this.preCommitValidationPassed) {
            return UniquenessValidationResult.VALIDATION_PASSED;
        }
        return UniquenessValidationResult.VALIDATION_FAILED;
    }
    
    public Boolean getPreCommitValidationPassed() {
        return this.preCommitValidationPassed;
    }
    
    public UniquenessValidationResult getPostCommitValidationResult() {
        if (this.postCommitValidationPassed == null) {
            return UniquenessValidationResult.VALIDATION_NOT_ATTEMPTED;
        }
        if (this.postCommitValidationPassed) {
            return UniquenessValidationResult.VALIDATION_PASSED;
        }
        return UniquenessValidationResult.VALIDATION_FAILED;
    }
    
    public Boolean getPostCommitValidationPassed() {
        return this.postCommitValidationPassed;
    }
    
    public String getValidationMessage() {
        return this.validationMessage;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_UNIQUENESS_RES_CONTROL_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UniquenessResponseControl(uniquenessID='");
        buffer.append(this.uniquenessID);
        buffer.append("', preCommitValidationResult='");
        buffer.append(this.getPreCommitValidationResult().getName());
        buffer.append("', preCommitValidationResult='");
        buffer.append(this.getPostCommitValidationResult().getName());
        buffer.append('\'');
        if (this.validationMessage != null) {
            buffer.append(", validationMessage='");
            buffer.append(this.validationMessage);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
