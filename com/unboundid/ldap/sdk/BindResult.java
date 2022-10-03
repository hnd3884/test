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
public class BindResult extends LDAPResult
{
    private static final byte TYPE_SERVER_SASL_CREDENTIALS = -121;
    private static final long serialVersionUID = 2211625049303605730L;
    private final ASN1OctetString serverSASLCredentials;
    
    public BindResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Control[] responseControls) {
        this(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls, null);
    }
    
    public BindResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Control[] responseControls, final ASN1OctetString serverSASLCredentials) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
        this.serverSASLCredentials = serverSASLCredentials;
    }
    
    public BindResult(final LDAPResult ldapResult) {
        super(ldapResult);
        this.serverSASLCredentials = null;
    }
    
    public BindResult(final LDAPException exception) {
        super(exception.toLDAPResult());
        if (exception instanceof LDAPBindException) {
            this.serverSASLCredentials = ((LDAPBindException)exception).getServerSASLCredentials();
        }
        else {
            this.serverSASLCredentials = null;
        }
    }
    
    protected BindResult(final BindResult bindResult) {
        super(bindResult);
        this.serverSASLCredentials = bindResult.serverSASLCredentials;
    }
    
    static BindResult readBindResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence protocolOpSequence = reader.beginSequence();
            final ResultCode resultCode = ResultCode.valueOf(reader.readEnumerated());
            String matchedDN = reader.readString();
            if (matchedDN.isEmpty()) {
                matchedDN = null;
            }
            String diagnosticMessage = reader.readString();
            if (diagnosticMessage.isEmpty()) {
                diagnosticMessage = null;
            }
            String[] referralURLs = null;
            ASN1OctetString serverSASLCredentials = null;
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
                    case -121: {
                        serverSASLCredentials = new ASN1OctetString(type, reader.readBytes());
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_BIND_RESULT_INVALID_ELEMENT.get(StaticUtils.toHex(type)));
                    }
                }
            }
            Control[] controls = BindResult.NO_CONTROLS;
            if (messageSequence.hasMoreElements()) {
                final ArrayList<Control> controlList = new ArrayList<Control>(1);
                final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
                while (controlSequence.hasMoreElements()) {
                    controlList.add(Control.readFrom(reader));
                }
                controls = new Control[controlList.size()];
                controlList.toArray(controls);
            }
            return new BindResult(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, controls, serverSASLCredentials);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_BIND_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public ASN1OctetString getServerSASLCredentials() {
        return this.serverSASLCredentials;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BindResult(resultCode=");
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
                buffer.append('\'');
                buffer.append(referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        buffer.append(", hasServerSASLCredentials=");
        buffer.append(this.serverSASLCredentials != null);
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
