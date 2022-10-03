package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.Base64;
import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import java.util.Collection;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPRuntimeException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ChangeLogEntry;
import com.unboundid.ldap.sdk.unboundidds.UnboundIDChangeLogEntry;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.IntermediateResponse;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ChangelogEntryIntermediateResponse extends IntermediateResponse
{
    public static final String CHANGELOG_ENTRY_INTERMEDIATE_RESPONSE_OID = "1.3.6.1.4.1.30221.2.6.11";
    private static final long serialVersionUID = 5616371094806687752L;
    private final ASN1OctetString resumeToken;
    private final UnboundIDChangeLogEntry changeLogEntry;
    private final String serverID;
    
    public ChangelogEntryIntermediateResponse(final ChangeLogEntry changeLogEntry, final String serverID, final ASN1OctetString resumeToken, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.11", encodeValue(changeLogEntry, serverID, resumeToken), controls);
        if (changeLogEntry instanceof UnboundIDChangeLogEntry) {
            this.changeLogEntry = (UnboundIDChangeLogEntry)changeLogEntry;
        }
        else {
            try {
                this.changeLogEntry = new UnboundIDChangeLogEntry(changeLogEntry);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPRuntimeException(le);
            }
        }
        this.serverID = serverID;
        this.resumeToken = resumeToken;
    }
    
    public ChangelogEntryIntermediateResponse(final IntermediateResponse r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CHANGELOG_ENTRY_IR_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CHANGELOG_ENTRY_IR_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final ASN1Element[] valueElements = valueSequence.elements();
        if (valueElements.length != 4) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CHANGELOG_ENTRY_IR_INVALID_VALUE_COUNT.get(valueElements.length));
        }
        this.resumeToken = ASN1OctetString.decodeAsOctetString(valueElements[0]);
        this.serverID = ASN1OctetString.decodeAsOctetString(valueElements[1]).stringValue();
        final String dn = ASN1OctetString.decodeAsOctetString(valueElements[2]).stringValue();
        try {
            final ASN1Element[] attrsElements = ASN1Sequence.decodeAsSequence(valueElements[3]).elements();
            final ArrayList<Attribute> attributes = new ArrayList<Attribute>(attrsElements.length);
            for (final ASN1Element e2 : attrsElements) {
                attributes.add(Attribute.decode(ASN1Sequence.decodeAsSequence(e2)));
            }
            this.changeLogEntry = new UnboundIDChangeLogEntry(new Entry(dn, attributes));
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_CHANGELOG_ENTRY_IR_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    private static ASN1OctetString encodeValue(final ChangeLogEntry changeLogEntry, final String serverID, final ASN1OctetString resumeToken) {
        Validator.ensureNotNull(changeLogEntry);
        Validator.ensureNotNull(serverID);
        Validator.ensureNotNull(resumeToken);
        final Collection<Attribute> attrs = changeLogEntry.getAttributes();
        final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(attrs.size());
        for (final Attribute a : attrs) {
            attrElements.add(a.encode());
        }
        final ASN1Sequence s = new ASN1Sequence(new ASN1Element[] { resumeToken, new ASN1OctetString(serverID), new ASN1OctetString(changeLogEntry.getDN()), new ASN1Sequence(attrElements) });
        return new ASN1OctetString(s.encode());
    }
    
    public UnboundIDChangeLogEntry getChangeLogEntry() {
        return this.changeLogEntry;
    }
    
    public String getServerID() {
        return this.serverID;
    }
    
    public ASN1OctetString getResumeToken() {
        return this.resumeToken;
    }
    
    @Override
    public String getIntermediateResponseName() {
        return ExtOpMessages.INFO_CHANGELOG_ENTRY_IR_NAME.get();
    }
    
    @Override
    public String valueToString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("changeNumber='");
        buffer.append(this.changeLogEntry.getChangeNumber());
        buffer.append("' changeType='");
        buffer.append(this.changeLogEntry.getChangeType().getName());
        buffer.append("' targetDN='");
        buffer.append(this.changeLogEntry.getTargetDN());
        buffer.append("' serverID='");
        buffer.append(this.serverID);
        buffer.append("' resumeToken='");
        Base64.encode(this.resumeToken.getValue(), buffer);
        buffer.append('\'');
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ChangelogEntryIntermediateResponse(");
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append("messageID=");
            buffer.append(messageID);
            buffer.append(", ");
        }
        buffer.append("changelogEntry=");
        this.changeLogEntry.toString(buffer);
        buffer.append(", serverID='");
        buffer.append(this.serverID);
        buffer.append("', resumeToken='");
        Base64.encode(this.resumeToken.getValue(), buffer);
        buffer.append('\'');
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
