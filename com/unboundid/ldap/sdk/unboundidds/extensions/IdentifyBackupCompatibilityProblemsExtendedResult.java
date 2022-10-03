package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedResult;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IdentifyBackupCompatibilityProblemsExtendedResult extends ExtendedResult
{
    public static final String IDENTIFY_BACKUP_COMPATIBILITY_PROBLEMS_RESULT_OID = "1.3.6.1.4.1.30221.2.6.33";
    private static final byte TYPE_ERRORS = -96;
    private static final byte TYPE_WARNINGS = -95;
    private static final long serialVersionUID = -6492859100961846933L;
    private final List<String> errorMessages;
    private final List<String> warningMessages;
    
    public IdentifyBackupCompatibilityProblemsExtendedResult(final ExtendedResult result) throws LDAPException {
        super(result);
        final ASN1OctetString value = result.getValue();
        if (value == null) {
            this.errorMessages = Collections.emptyList();
            this.warningMessages = Collections.emptyList();
            return;
        }
        try {
            List<String> errors = Collections.emptyList();
            List<String> warnings = Collections.emptyList();
            final ASN1Element[] arr$;
            final ASN1Element[] elements = arr$ = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            for (final ASN1Element e : arr$) {
                switch (e.getType()) {
                    case -96: {
                        final ASN1Element[] errorElements = ASN1Sequence.decodeAsSequence(e).elements();
                        final ArrayList<String> errorStrings = new ArrayList<String>(errorElements.length);
                        for (final ASN1Element errorElement : errorElements) {
                            errorStrings.add(ASN1OctetString.decodeAsOctetString(errorElement).stringValue());
                        }
                        errors = Collections.unmodifiableList((List<? extends String>)errorStrings);
                        break;
                    }
                    case -95: {
                        final ASN1Element[] warningElements = ASN1Sequence.decodeAsSequence(e).elements();
                        final ArrayList<String> warningStrings = new ArrayList<String>(warningElements.length);
                        for (final ASN1Element warningElement : warningElements) {
                            warningStrings.add(ASN1OctetString.decodeAsOctetString(warningElement).stringValue());
                        }
                        warnings = Collections.unmodifiableList((List<? extends String>)warningStrings);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_IDENTIFY_BACKUP_COMPAT_PROBLEMS_RESULT_UNEXPECTED_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            this.errorMessages = errors;
            this.warningMessages = warnings;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_BACKUP_COMPAT_RESULT_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public IdentifyBackupCompatibilityProblemsExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Collection<String> errorMessages, final Collection<String> warningMessages, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (resultCode == ResultCode.SUCCESS) ? "1.3.6.1.4.1.30221.2.6.33" : null, encodeValue(resultCode, errorMessages, warningMessages), responseControls);
        if (errorMessages == null) {
            this.errorMessages = Collections.emptyList();
        }
        else {
            this.errorMessages = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(errorMessages));
        }
        if (warningMessages == null) {
            this.warningMessages = Collections.emptyList();
        }
        else {
            this.warningMessages = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(warningMessages));
        }
    }
    
    public static ASN1OctetString encodeValue(final ResultCode resultCode, final Collection<String> errorMessages, final Collection<String> warningMessages) {
        if (resultCode != ResultCode.SUCCESS) {
            Validator.ensureTrue((errorMessages == null || errorMessages.isEmpty()) && (warningMessages == null || warningMessages.isEmpty()), "There must not be any warning or error messages with a non-success result.");
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (errorMessages != null && !errorMessages.isEmpty()) {
            final ArrayList<ASN1Element> msgElements = new ArrayList<ASN1Element>(errorMessages.size());
            for (final String s : errorMessages) {
                msgElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-96), msgElements));
        }
        if (warningMessages != null && !warningMessages.isEmpty()) {
            final ArrayList<ASN1Element> msgElements = new ArrayList<ASN1Element>(warningMessages.size());
            for (final String s : warningMessages) {
                msgElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-95), msgElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public List<String> getWarningMessages() {
        return this.warningMessages;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_IDENTIFY_BACKUP_COMPAT_PROBLEMS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IdentifyBackupCompatibilityProblemsExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (!this.errorMessages.isEmpty()) {
            buffer.append(", errorMessages={");
            final Iterator<String> iterator = this.errorMessages.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.warningMessages.isEmpty()) {
            buffer.append(", warningMessages={");
            final Iterator<String> iterator = this.warningMessages.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
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
