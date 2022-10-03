package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchResultReference implements Serializable, LDAPResponse
{
    private static final long serialVersionUID = 5675961266319346053L;
    private final Control[] controls;
    private final int messageID;
    private final String[] referralURLs;
    
    public SearchResultReference(final String[] referralURLs, final Control[] controls) {
        this(-1, referralURLs, controls);
    }
    
    public SearchResultReference(final int messageID, final String[] referralURLs, final Control[] controls) {
        Validator.ensureNotNull(referralURLs);
        this.messageID = messageID;
        this.referralURLs = referralURLs;
        if (controls == null) {
            this.controls = SearchResultReference.NO_CONTROLS;
        }
        else {
            this.controls = controls;
        }
    }
    
    static SearchResultReference readSearchReferenceFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        try {
            final ArrayList<String> refList = new ArrayList<String>(5);
            final ASN1StreamReaderSequence refSequence = reader.beginSequence();
            while (refSequence.hasMoreElements()) {
                refList.add(reader.readString());
            }
            final String[] referralURLs = new String[refList.size()];
            refList.toArray(referralURLs);
            Control[] controls = SearchResultReference.NO_CONTROLS;
            if (messageSequence.hasMoreElements()) {
                final ArrayList<Control> controlList = new ArrayList<Control>(5);
                final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
                while (controlSequence.hasMoreElements()) {
                    controlList.add(Control.readFrom(reader));
                }
                controls = new Control[controlList.size()];
                controlList.toArray(controls);
            }
            return new SearchResultReference(messageID, referralURLs, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_SEARCH_REFERENCE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public int getMessageID() {
        return this.messageID;
    }
    
    public String[] getReferralURLs() {
        return this.referralURLs;
    }
    
    public Control[] getControls() {
        return this.controls;
    }
    
    public Control getControl(final String oid) {
        for (final Control c : this.controls) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SearchResultReference(referralURLs={");
        for (int i = 0; i < this.referralURLs.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.referralURLs[i]);
        }
        buffer.append('}');
        if (this.messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(this.messageID);
        }
        buffer.append(", controls={");
        for (int i = 0; i < this.controls.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            this.controls[i].toString(buffer);
        }
        buffer.append("})");
    }
}
