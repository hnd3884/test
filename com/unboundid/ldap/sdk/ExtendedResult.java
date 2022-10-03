package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ExtendedResult extends LDAPResult
{
    private static final byte TYPE_EXTENDED_RESPONSE_OID = -118;
    private static final byte TYPE_EXTENDED_RESPONSE_VALUE = -117;
    private static final long serialVersionUID = -6885923482396647963L;
    private final ASN1OctetString value;
    private final String oid;
    
    public ExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String oid, final ASN1OctetString value, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
        this.oid = oid;
        this.value = value;
    }
    
    public ExtendedResult(final LDAPResult result) {
        super(result);
        this.oid = null;
        this.value = null;
    }
    
    public ExtendedResult(final LDAPException exception) {
        this(exception.toLDAPResult());
    }
    
    protected ExtendedResult(final ExtendedResult extendedResult) {
        this(extendedResult.getMessageID(), extendedResult.getResultCode(), extendedResult.getDiagnosticMessage(), extendedResult.getMatchedDN(), extendedResult.getReferralURLs(), extendedResult.getOID(), extendedResult.getValue(), extendedResult.getResponseControls());
    }
    
    static ExtendedResult readExtendedResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence protocolOpSequence = reader.beginSequence();
            final ResultCode resultCode = ResultCode.valueOf(reader.readEnumerated());
            String matchedDN = reader.readString();
            if (matchedDN.length() == 0) {
                matchedDN = null;
            }
            String diagnosticMessage = reader.readString();
            if (diagnosticMessage.length() == 0) {
                diagnosticMessage = null;
            }
            String[] referralURLs = null;
            String oid = null;
            ASN1OctetString value = null;
            while (protocolOpSequence.hasMoreElements()) {
                final byte type = (byte)reader.peek();
                switch (type) {
                    case -93: {
                        final ArrayList<String> refList = new ArrayList<String>(1);
                        final ASN1StreamReaderSequence refSequence = reader.beginSequence();
                        while (refSequence.hasMoreElements()) {
                            refList.add(reader.readString());
                        }
                        referralURLs = new String[refList.size()];
                        refList.toArray(referralURLs);
                        continue;
                    }
                    case -118: {
                        oid = reader.readString();
                        continue;
                    }
                    case -117: {
                        value = new ASN1OctetString(type, reader.readBytes());
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_EXTENDED_RESULT_INVALID_ELEMENT.get(StaticUtils.toHex(type)));
                    }
                }
            }
            Control[] controls = ExtendedResult.NO_CONTROLS;
            if (messageSequence.hasMoreElements()) {
                final ArrayList<Control> controlList = new ArrayList<Control>(1);
                final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
                while (controlSequence.hasMoreElements()) {
                    controlList.add(Control.readFrom(reader));
                }
                controls = new Control[controlList.size()];
                controlList.toArray(controls);
            }
            return new ExtendedResult(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, oid, value, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_EXTENDED_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public final String getOID() {
        return this.oid;
    }
    
    public final boolean hasValue() {
        return this.value != null;
    }
    
    public final ASN1OctetString getValue() {
        return this.value;
    }
    
    public String getExtendedResultName() {
        return this.oid;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
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
                buffer.append(referralURLs[i]);
            }
            buffer.append('}');
        }
        if (this.oid != null) {
            buffer.append(", oid=");
            buffer.append(this.oid);
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
