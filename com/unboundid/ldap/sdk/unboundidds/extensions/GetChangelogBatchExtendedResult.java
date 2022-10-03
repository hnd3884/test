package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.Base64;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetChangelogBatchExtendedResult extends ExtendedResult
{
    private static final byte TYPE_RESUME_TOKEN = Byte.MIN_VALUE;
    private static final byte TYPE_MORE_CHANGES_AVAILABLE = -127;
    private static final byte TYPE_CHANGES_ALREADY_PURGED = -126;
    private static final byte TYPE_ADDITIONAL_INFO = -125;
    private static final byte TYPE_ESTIMATED_CHANGES_REMAINING = -124;
    private static final long serialVersionUID = -1997815252100989148L;
    private final ASN1OctetString resumeToken;
    private final boolean changesAlreadyPurged;
    private final boolean moreChangesAvailable;
    private final int estimatedChangesRemaining;
    private final int entryCount;
    private final List<ChangelogEntryIntermediateResponse> entryList;
    private final String additionalInfo;
    
    public GetChangelogBatchExtendedResult(final LDAPResult r) {
        super(r.getMessageID(), r.getResultCode(), r.getDiagnosticMessage(), r.getMatchedDN(), r.getReferralURLs(), null, null, r.getResponseControls());
        this.resumeToken = null;
        this.changesAlreadyPurged = false;
        this.moreChangesAvailable = false;
        this.estimatedChangesRemaining = -1;
        this.entryCount = -1;
        this.entryList = null;
        this.additionalInfo = null;
    }
    
    public GetChangelogBatchExtendedResult(final LDAPResult r, final int entryCount, final ASN1OctetString resumeToken, final boolean moreChangesAvailable, final boolean changesAlreadyPurged, final String additionalInfo) {
        this(r, entryCount, resumeToken, moreChangesAvailable, -1, changesAlreadyPurged, additionalInfo);
    }
    
    public GetChangelogBatchExtendedResult(final LDAPResult r, final int entryCount, final ASN1OctetString resumeToken, final boolean moreChangesAvailable, final int estimatedChangesRemaining, final boolean changesAlreadyPurged, final String additionalInfo) {
        super(r.getMessageID(), r.getResultCode(), r.getDiagnosticMessage(), r.getMatchedDN(), r.getReferralURLs(), null, encodeValue(resumeToken, moreChangesAvailable, estimatedChangesRemaining, changesAlreadyPurged, additionalInfo), r.getResponseControls());
        this.resumeToken = resumeToken;
        this.moreChangesAvailable = moreChangesAvailable;
        this.changesAlreadyPurged = changesAlreadyPurged;
        this.additionalInfo = additionalInfo;
        if (estimatedChangesRemaining >= 0) {
            this.estimatedChangesRemaining = estimatedChangesRemaining;
        }
        else {
            this.estimatedChangesRemaining = -1;
        }
        this.entryList = null;
        if (entryCount < 0) {
            this.entryCount = -1;
        }
        else {
            this.entryCount = entryCount;
        }
    }
    
    public GetChangelogBatchExtendedResult(final ExtendedResult extendedResult, final int entryCount) throws LDAPException {
        this(extendedResult, entryCount, null);
    }
    
    public GetChangelogBatchExtendedResult(final ExtendedResult extendedResult, final List<ChangelogEntryIntermediateResponse> entryList) throws LDAPException {
        this(extendedResult, entryList.size(), entryList);
    }
    
    private GetChangelogBatchExtendedResult(final ExtendedResult r, final int entryCount, final List<ChangelogEntryIntermediateResponse> entryList) throws LDAPException {
        super(r);
        if (entryList == null) {
            this.entryList = null;
        }
        else {
            this.entryList = Collections.unmodifiableList((List<? extends ChangelogEntryIntermediateResponse>)entryList);
        }
        if (entryCount < 0) {
            this.entryCount = -1;
        }
        else {
            this.entryCount = entryCount;
        }
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            if (entryList != null && !entryList.isEmpty()) {
                this.resumeToken = entryList.get(entryList.size() - 1).getResumeToken();
            }
            else {
                this.resumeToken = null;
            }
            this.moreChangesAvailable = false;
            this.estimatedChangesRemaining = -1;
            this.changesAlreadyPurged = false;
            this.additionalInfo = null;
            return;
        }
        ASN1Element[] valueElements;
        try {
            valueElements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_RES_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        ASN1OctetString token = null;
        Boolean moreChanges = null;
        boolean missingChanges = false;
        int changesRemaining = -1;
        String message = null;
        try {
            for (final ASN1Element e2 : valueElements) {
                final byte type = e2.getType();
                switch (type) {
                    case Byte.MIN_VALUE: {
                        token = ASN1OctetString.decodeAsOctetString(e2);
                        break;
                    }
                    case -127: {
                        moreChanges = ASN1Boolean.decodeAsBoolean(e2).booleanValue();
                        break;
                    }
                    case -126: {
                        missingChanges = ASN1Boolean.decodeAsBoolean(e2).booleanValue();
                        break;
                    }
                    case -125: {
                        message = ASN1OctetString.decodeAsOctetString(e2).stringValue();
                        break;
                    }
                    case -124: {
                        changesRemaining = ASN1Integer.decodeAsInteger(e2).intValue();
                        if (changesRemaining < 0) {
                            changesRemaining = -1;
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_RES_UNEXPECTED_VALUE_ELEMENT.get(StaticUtils.toHex(type)));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_RES_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
        if (moreChanges == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CHANGELOG_BATCH_RES_MISSING_MORE.get());
        }
        this.resumeToken = token;
        this.moreChangesAvailable = moreChanges;
        this.changesAlreadyPurged = missingChanges;
        this.estimatedChangesRemaining = changesRemaining;
        this.additionalInfo = message;
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString resumeToken, final boolean moreChangesAvailable, final int estimatedChangesRemaining, final boolean changesAlreadyPurged, final String additionalInfo) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(5);
        if (resumeToken != null) {
            elements.add(new ASN1OctetString((byte)(-128), resumeToken.getValue()));
        }
        elements.add(new ASN1Boolean((byte)(-127), moreChangesAvailable));
        if (estimatedChangesRemaining >= 0) {
            elements.add(new ASN1Integer((byte)(-124), estimatedChangesRemaining));
        }
        if (changesAlreadyPurged) {
            elements.add(new ASN1Boolean((byte)(-126), changesAlreadyPurged));
        }
        if (additionalInfo != null) {
            elements.add(new ASN1OctetString((byte)(-125), additionalInfo));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ASN1OctetString getResumeToken() {
        return this.resumeToken;
    }
    
    public boolean moreChangesAvailable() {
        return this.moreChangesAvailable;
    }
    
    public int getEstimatedChangesRemaining() {
        return this.estimatedChangesRemaining;
    }
    
    public boolean changesAlreadyPurged() {
        return this.changesAlreadyPurged;
    }
    
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }
    
    public int getEntryCount() {
        return this.entryCount;
    }
    
    public List<ChangelogEntryIntermediateResponse> getChangelogEntries() {
        return this.entryList;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_GET_CHANGELOG_BATCH_RES_NAME.get();
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
        if (this.resumeToken != null) {
            buffer.append(", resumeToken='");
            Base64.encode(this.resumeToken.getValue(), buffer);
            buffer.append('\'');
        }
        buffer.append(", moreChangesAvailable=");
        buffer.append(this.moreChangesAvailable);
        buffer.append(", estimatedChangesRemaining=");
        buffer.append(this.estimatedChangesRemaining);
        buffer.append(", changesAlreadyPurged=");
        buffer.append(this.changesAlreadyPurged);
        if (this.additionalInfo != null) {
            buffer.append(", additionalInfo='");
            buffer.append(this.additionalInfo);
            buffer.append('\'');
        }
        buffer.append(", entryCount=");
        buffer.append(this.entryCount);
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
