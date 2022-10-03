package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import java.text.ParseException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Control;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import java.util.Collections;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordPolicyStateExtendedResult extends ExtendedResult
{
    private static final long serialVersionUID = 7140468768443263344L;
    private final Map<Integer, PasswordPolicyStateOperation> operations;
    private final String userDN;
    
    public PasswordPolicyStateExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.userDN = null;
            this.operations = Collections.emptyMap();
            return;
        }
        ASN1Element[] elements;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_RESPONSE_VALUE_NOT_SEQUENCE.get(e), e);
        }
        if (elements.length < 1 || elements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_RESPONSE_INVALID_ELEMENT_COUNT.get(elements.length));
        }
        this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        final LinkedHashMap<Integer, PasswordPolicyStateOperation> ops = new LinkedHashMap<Integer, PasswordPolicyStateOperation>(StaticUtils.computeMapCapacity(20));
        if (elements.length == 2) {
            try {
                final ASN1Element[] arr$;
                final ASN1Element[] opElements = arr$ = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                for (final ASN1Element e2 : arr$) {
                    final PasswordPolicyStateOperation op = PasswordPolicyStateOperation.decode(e2);
                    ops.put(op.getOperationType(), op);
                }
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PWP_STATE_RESPONSE_CANNOT_DECODE_OPS.get(e3), e3);
            }
        }
        this.operations = Collections.unmodifiableMap((Map<? extends Integer, ? extends PasswordPolicyStateOperation>)ops);
    }
    
    public PasswordPolicyStateExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String userDN, final PasswordPolicyStateOperation[] operations, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(userDN, operations), responseControls);
        this.userDN = userDN;
        if (operations == null || operations.length == 0) {
            this.operations = Collections.emptyMap();
        }
        else {
            final LinkedHashMap<Integer, PasswordPolicyStateOperation> ops = new LinkedHashMap<Integer, PasswordPolicyStateOperation>(StaticUtils.computeMapCapacity(operations.length));
            for (final PasswordPolicyStateOperation o : operations) {
                ops.put(o.getOperationType(), o);
            }
            this.operations = Collections.unmodifiableMap((Map<? extends Integer, ? extends PasswordPolicyStateOperation>)ops);
        }
    }
    
    private static ASN1OctetString encodeValue(final String userDN, final PasswordPolicyStateOperation[] operations) {
        if (userDN == null && (operations == null || operations.length == 0)) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        elements.add(new ASN1OctetString(userDN));
        if (operations != null && operations.length > 0) {
            final ASN1Element[] opElements = new ASN1Element[operations.length];
            for (int i = 0; i < operations.length; ++i) {
                opElements[i] = operations[i].encode();
            }
            elements.add(new ASN1Sequence(opElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getUserDN() {
        return this.userDN;
    }
    
    public Iterable<PasswordPolicyStateOperation> getOperations() {
        return this.operations.values();
    }
    
    public PasswordPolicyStateOperation getOperation(final int opType) {
        return this.operations.get(opType);
    }
    
    public String getStringValue(final int opType) {
        final PasswordPolicyStateOperation op = this.operations.get(opType);
        if (op == null) {
            return null;
        }
        return op.getStringValue();
    }
    
    public String[] getStringValues(final int opType) {
        final PasswordPolicyStateOperation op = this.operations.get(opType);
        if (op == null) {
            return null;
        }
        return op.getStringValues();
    }
    
    public boolean getBooleanValue(final int opType) throws NoSuchElementException, IllegalStateException {
        final PasswordPolicyStateOperation op = this.operations.get(opType);
        if (op == null) {
            throw new NoSuchElementException(ExtOpMessages.ERR_PWP_STATE_RESPONSE_NO_SUCH_OPERATION.get());
        }
        return op.getBooleanValue();
    }
    
    public int getIntValue(final int opType) throws NoSuchElementException, IllegalStateException {
        final PasswordPolicyStateOperation op = this.operations.get(opType);
        if (op == null) {
            throw new NoSuchElementException(ExtOpMessages.ERR_PWP_STATE_RESPONSE_NO_SUCH_OPERATION.get());
        }
        return op.getIntValue();
    }
    
    public Date getGeneralizedTimeValue(final int opType) throws ParseException {
        final PasswordPolicyStateOperation op = this.operations.get(opType);
        if (op == null) {
            return null;
        }
        return op.getGeneralizedTimeValue();
    }
    
    public Date[] getGeneralizedTimeValues(final int opType) throws ParseException {
        final PasswordPolicyStateOperation op = this.operations.get(opType);
        if (op == null) {
            return null;
        }
        return op.getGeneralizedTimeValues();
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_PW_POLICY_STATE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordPolicyStateExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        buffer.append(", userDN='");
        buffer.append(this.userDN);
        buffer.append("', operations={");
        final Iterator<PasswordPolicyStateOperation> iterator = this.operations.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(diagnosticMessage);
            buffer.append('\'');
        }
        final String matchedDN = this.getMatchedDN();
        if (matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(matchedDN);
            buffer.append('\'');
        }
        final String[] referralURLs = this.getReferralURLs();
        if (referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        final Control[] responseControls = this.getResponseControls();
        if (responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int j = 0; j < responseControls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(responseControls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
