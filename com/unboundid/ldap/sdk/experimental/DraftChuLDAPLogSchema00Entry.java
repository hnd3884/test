package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.asn1.ASN1Sequence;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import com.unboundid.ldap.sdk.ReadOnlyEntry;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class DraftChuLDAPLogSchema00Entry extends ReadOnlyEntry
{
    public static final String ATTR_AUTHORIZATION_IDENTITY_DN = "reqAuthzID";
    public static final String ATTR_DIAGNOSTIC_MESSAGE = "reqMessage";
    public static final String ATTR_OPERATION_TYPE = "reqType";
    public static final String ATTR_PROCESSING_END_TIME = "reqEnd";
    public static final String ATTR_PROCESSING_START_TIME = "reqStart";
    public static final String ATTR_REFERRAL_URL = "reqReferral";
    public static final String ATTR_REQUEST_CONTROL = "reqControls";
    public static final String ATTR_RESPONSE_CONTROL = "reqRespControls";
    public static final String ATTR_RESULT_CODE = "reqResult";
    public static final String ATTR_SESSION_ID = "reqSession";
    public static final String ATTR_TARGET_ENTRY_DN = "reqDN";
    private static final long serialVersionUID = -7279669732772403236L;
    private final Date processingEndTimeDate;
    private final Date processingStartTimeDate;
    private final List<Control> requestControls;
    private final List<Control> responseControls;
    private final List<String> referralURLs;
    private final OperationType operationType;
    private final ResultCode resultCode;
    private final String authorizationIdentityDN;
    private final String diagnosticMessage;
    private final String processingEndTimeString;
    private final String processingStartTimeString;
    private final String sessionID;
    private final String targetEntryDN;
    
    DraftChuLDAPLogSchema00Entry(final Entry entry, final OperationType operationType) throws LDAPException {
        super(entry);
        this.operationType = operationType;
        this.processingStartTimeString = entry.getAttributeValue("reqStart");
        if (this.processingStartTimeString == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqStart"));
        }
        try {
            this.processingStartTimeDate = StaticUtils.decodeGeneralizedTime(this.processingStartTimeString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_CANNOT_DECODE_TIME.get(entry.getDN(), "reqStart", this.processingStartTimeString), e);
        }
        this.processingEndTimeString = entry.getAttributeValue("reqEnd");
        if (this.processingEndTimeString == null) {
            this.processingEndTimeDate = null;
        }
        else {
            try {
                this.processingEndTimeDate = StaticUtils.decodeGeneralizedTime(this.processingEndTimeString);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_CANNOT_DECODE_TIME.get(entry.getDN(), "reqEnd", this.processingEndTimeString), e);
            }
        }
        this.sessionID = entry.getAttributeValue("reqSession");
        if (this.sessionID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqSession"));
        }
        this.targetEntryDN = entry.getAttributeValue("reqDN");
        if (this.targetEntryDN == null && operationType != OperationType.ABANDON && operationType != OperationType.EXTENDED && operationType != OperationType.UNBIND) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqDN"));
        }
        this.authorizationIdentityDN = entry.getAttributeValue("reqAuthzID");
        this.requestControls = decodeControls(entry, "reqControls");
        this.responseControls = decodeControls(entry, "reqRespControls");
        final String resultCodeString = entry.getAttributeValue("reqResult");
        if (resultCodeString == null) {
            this.resultCode = null;
        }
        else {
            try {
                this.resultCode = ResultCode.valueOf(Integer.parseInt(resultCodeString));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_RESULT_CODE_ERROR.get(entry.getDN(), resultCodeString, "reqResult"), e2);
            }
        }
        this.diagnosticMessage = entry.getAttributeValue("reqMessage");
        final String[] referralArray = entry.getAttributeValues("reqReferral");
        if (referralArray == null) {
            this.referralURLs = Collections.emptyList();
        }
        else {
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)StaticUtils.toList(referralArray));
        }
    }
    
    private static List<Control> decodeControls(final Entry entry, final String attributeName) throws LDAPException {
        final byte[][] values = entry.getAttributeValueByteArrays(attributeName);
        if (values == null || values.length == 0) {
            return Collections.emptyList();
        }
        final ArrayList<Control> controls = new ArrayList<Control>(values.length);
        for (final byte[] controlBytes : values) {
            try {
                controls.add(Control.decode(ASN1Sequence.decodeAsSequence(controlBytes)));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_CONTROL_ERROR.get(entry.getDN(), attributeName, StaticUtils.getExceptionMessage(e)), e);
            }
        }
        return Collections.unmodifiableList((List<? extends Control>)controls);
    }
    
    public final OperationType getOperationType() {
        return this.operationType;
    }
    
    public final String getTargetEntryDN() {
        return this.targetEntryDN;
    }
    
    public final String getProcessingStartTimeString() {
        return this.processingStartTimeString;
    }
    
    public final Date getProcessingStartTimeDate() {
        return this.processingStartTimeDate;
    }
    
    public final String getProcessingEndTimeString() {
        return this.processingEndTimeString;
    }
    
    public final Date getProcessingEndTimeDate() {
        return this.processingEndTimeDate;
    }
    
    public final String getSessionID() {
        return this.sessionID;
    }
    
    public final List<Control> getRequestControls() {
        return this.requestControls;
    }
    
    final Control[] getRequestControlArray() {
        return this.requestControls.toArray(StaticUtils.NO_CONTROLS);
    }
    
    public final ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public final String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public final List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    public final List<Control> getResponseControls() {
        return this.responseControls;
    }
    
    public final String getAuthorizationIdentityDN() {
        return this.authorizationIdentityDN;
    }
    
    public final LDAPResult toLDAPResult() {
        if (this.resultCode == null) {
            return null;
        }
        return new LDAPResult(-1, this.resultCode, this.diagnosticMessage, null, this.referralURLs, this.responseControls);
    }
    
    public static DraftChuLDAPLogSchema00Entry decode(final Entry entry) throws LDAPException {
        final String opType = entry.getAttributeValue("reqType");
        if (opType == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_NO_OP_TYPE.get(entry.getDN(), "reqType"));
        }
        final String lowerOpType = StaticUtils.toLowerCase(opType);
        if (lowerOpType.equals("abandon")) {
            return new DraftChuLDAPLogSchema00AbandonEntry(entry);
        }
        if (lowerOpType.equals("add")) {
            return new DraftChuLDAPLogSchema00AddEntry(entry);
        }
        if (lowerOpType.equals("bind")) {
            return new DraftChuLDAPLogSchema00BindEntry(entry);
        }
        if (lowerOpType.equals("compare")) {
            return new DraftChuLDAPLogSchema00CompareEntry(entry);
        }
        if (lowerOpType.equals("delete")) {
            return new DraftChuLDAPLogSchema00DeleteEntry(entry);
        }
        if (lowerOpType.startsWith("extended")) {
            return new DraftChuLDAPLogSchema00ExtendedEntry(entry);
        }
        if (lowerOpType.equals("modify")) {
            return new DraftChuLDAPLogSchema00ModifyEntry(entry);
        }
        if (lowerOpType.equals("modrdn")) {
            return new DraftChuLDAPLogSchema00ModifyDNEntry(entry);
        }
        if (lowerOpType.equals("search")) {
            return new DraftChuLDAPLogSchema00SearchEntry(entry);
        }
        if (lowerOpType.equals("unbind")) {
            return new DraftChuLDAPLogSchema00UnbindEntry(entry);
        }
        throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_UNRECOGNIZED_OP_TYPE.get(entry.getDN(), "reqType", opType));
    }
}
