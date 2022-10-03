package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.io.Serializable;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPResult implements Serializable, LDAPResponse
{
    static final byte TYPE_REFERRAL_URLS = -93;
    private static final long serialVersionUID = 2215819095653175991L;
    private final Byte protocolOpType;
    private final Control[] responseControls;
    private final int messageID;
    private final ResultCode resultCode;
    private final String diagnosticMessage;
    private final String matchedDN;
    private final String[] referralURLs;
    
    protected LDAPResult(final LDAPResult result) {
        this.protocolOpType = result.protocolOpType;
        this.messageID = result.messageID;
        this.resultCode = result.resultCode;
        this.diagnosticMessage = result.diagnosticMessage;
        this.matchedDN = result.matchedDN;
        this.referralURLs = result.referralURLs;
        this.responseControls = result.responseControls;
    }
    
    public LDAPResult(final int messageID, final ResultCode resultCode) {
        this(null, messageID, resultCode, null, null, StaticUtils.NO_STRINGS, LDAPResult.NO_CONTROLS);
    }
    
    public LDAPResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Control[] responseControls) {
        this(null, messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
    }
    
    public LDAPResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final List<String> referralURLs, final List<Control> responseControls) {
        this(null, messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
    }
    
    private LDAPResult(final Byte protocolOpType, final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Control[] responseControls) {
        this.protocolOpType = protocolOpType;
        this.messageID = messageID;
        this.resultCode = resultCode;
        this.diagnosticMessage = diagnosticMessage;
        this.matchedDN = matchedDN;
        if (referralURLs == null) {
            this.referralURLs = StaticUtils.NO_STRINGS;
        }
        else {
            this.referralURLs = referralURLs;
        }
        if (responseControls == null) {
            this.responseControls = LDAPResult.NO_CONTROLS;
        }
        else {
            this.responseControls = responseControls;
        }
    }
    
    private LDAPResult(final Byte protocolOpType, final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final List<String> referralURLs, final List<Control> responseControls) {
        this.protocolOpType = protocolOpType;
        this.messageID = messageID;
        this.resultCode = resultCode;
        this.diagnosticMessage = diagnosticMessage;
        this.matchedDN = matchedDN;
        if (referralURLs == null || referralURLs.isEmpty()) {
            this.referralURLs = StaticUtils.NO_STRINGS;
        }
        else {
            referralURLs.toArray(this.referralURLs = new String[referralURLs.size()]);
        }
        if (responseControls == null || responseControls.isEmpty()) {
            this.responseControls = LDAPResult.NO_CONTROLS;
        }
        else {
            responseControls.toArray(this.responseControls = new Control[responseControls.size()]);
        }
    }
    
    static LDAPResult readLDAPResultFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence protocolOpSequence = reader.beginSequence();
            final byte protocolOpType = protocolOpSequence.getType();
            final ResultCode resultCode = ResultCode.valueOf(reader.readEnumerated());
            String matchedDN = reader.readString();
            if (matchedDN.isEmpty()) {
                matchedDN = null;
            }
            String diagnosticMessage = reader.readString();
            if (diagnosticMessage.isEmpty()) {
                diagnosticMessage = null;
            }
            String[] referralURLs = StaticUtils.NO_STRINGS;
            if (protocolOpSequence.hasMoreElements()) {
                final ArrayList<String> refList = new ArrayList<String>(1);
                final ASN1StreamReaderSequence refSequence = reader.beginSequence();
                while (refSequence.hasMoreElements()) {
                    refList.add(reader.readString());
                }
                referralURLs = new String[refList.size()];
                refList.toArray(referralURLs);
            }
            Control[] responseControls = LDAPResult.NO_CONTROLS;
            if (messageSequence.hasMoreElements()) {
                final ArrayList<Control> controlList = new ArrayList<Control>(1);
                final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
                while (controlSequence.hasMoreElements()) {
                    controlList.add(Control.readFrom(reader));
                }
                responseControls = new Control[controlList.size()];
                controlList.toArray(responseControls);
            }
            return new LDAPResult(protocolOpType, messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, responseControls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_RESULT_CANNOT_DECODE.get(ae.getMessage()), ae);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public final int getMessageID() {
        return this.messageID;
    }
    
    public final ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public final String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    public final String getMatchedDN() {
        return this.matchedDN;
    }
    
    public final String[] getReferralURLs() {
        return this.referralURLs;
    }
    
    public final Control[] getResponseControls() {
        return this.responseControls;
    }
    
    public final boolean hasResponseControl() {
        return this.responseControls.length > 0;
    }
    
    public final boolean hasResponseControl(final String oid) {
        for (final Control c : this.responseControls) {
            if (c.getOID().equals(oid)) {
                return true;
            }
        }
        return false;
    }
    
    public final Control getResponseControl(final String oid) {
        for (final Control c : this.responseControls) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        return null;
    }
    
    public String getResultString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("result code='");
        buffer.append(this.resultCode);
        buffer.append('\'');
        if (this.diagnosticMessage != null && !this.diagnosticMessage.isEmpty()) {
            buffer.append(" diagnostic message='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        if (this.matchedDN != null && !this.matchedDN.isEmpty()) {
            buffer.append("  matched DN='");
            buffer.append(this.matchedDN);
            buffer.append('\'');
        }
        if (this.referralURLs != null && this.referralURLs.length > 0) {
            buffer.append("  referral URLs={");
            for (int i = 0; i < this.referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        return buffer.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPResult(resultCode=");
        buffer.append(this.resultCode);
        if (this.messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(this.messageID);
        }
        if (this.protocolOpType != null) {
            switch (this.protocolOpType) {
                case 105: {
                    buffer.append(", opType='add'");
                    break;
                }
                case 97: {
                    buffer.append(", opType='bind'");
                    break;
                }
                case 111: {
                    buffer.append(", opType='compare'");
                    break;
                }
                case 107: {
                    buffer.append(", opType='delete'");
                    break;
                }
                case 120: {
                    buffer.append(", opType='extended'");
                    break;
                }
                case 103: {
                    buffer.append(", opType='modify'");
                    break;
                }
                case 109: {
                    buffer.append(", opType='modify DN'");
                    break;
                }
                case 101: {
                    buffer.append(", opType='search'");
                    break;
                }
            }
        }
        if (this.diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(this.diagnosticMessage);
            buffer.append('\'');
        }
        if (this.matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(this.matchedDN);
            buffer.append('\'');
        }
        if (this.referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < this.referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        if (this.responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int i = 0; i < this.responseControls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.responseControls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
