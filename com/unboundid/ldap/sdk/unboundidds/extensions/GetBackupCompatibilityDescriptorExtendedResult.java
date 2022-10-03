package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedResult;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetBackupCompatibilityDescriptorExtendedResult extends ExtendedResult
{
    public static final String GET_BACKUP_COMPATIBILITY_DESCRIPTOR_RESULT_OID = "1.3.6.1.4.1.30221.2.6.31";
    private static final byte TYPE_DESCRIPTOR = Byte.MIN_VALUE;
    private static final byte TYPE_PROPERTIES = -95;
    private static final long serialVersionUID = -2493658329210480765L;
    private final ASN1OctetString descriptor;
    private final List<String> properties;
    
    public GetBackupCompatibilityDescriptorExtendedResult(final ExtendedResult result) throws LDAPException {
        super(result);
        final ASN1OctetString value = result.getValue();
        if (value == null) {
            this.descriptor = null;
            this.properties = Collections.emptyList();
            return;
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.descriptor = elements[0].decodeAsOctetString();
            if (elements.length > 1) {
                final ASN1Element[] propElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                final ArrayList<String> propList = new ArrayList<String>(propElements.length);
                for (final ASN1Element e : propElements) {
                    propList.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                }
                this.properties = Collections.unmodifiableList((List<? extends String>)propList);
            }
            else {
                this.properties = Collections.emptyList();
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_BACKUP_COMPAT_RESULT_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public GetBackupCompatibilityDescriptorExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final ASN1OctetString descriptor, final Collection<String> properties, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (descriptor == null) ? null : "1.3.6.1.4.1.30221.2.6.31", encodeValue(descriptor, properties), responseControls);
        if (descriptor == null) {
            this.descriptor = null;
        }
        else {
            this.descriptor = new ASN1OctetString((byte)(-128), descriptor.getValue());
        }
        if (properties == null) {
            this.properties = Collections.emptyList();
        }
        else {
            this.properties = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(properties));
        }
    }
    
    public static ASN1OctetString encodeValue(final ASN1OctetString descriptor, final Collection<String> properties) {
        if (descriptor == null) {
            Validator.ensureTrue(properties == null || properties.isEmpty(), "The properties must be null or empty if the descriptor is null.");
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        elements.add(new ASN1OctetString((byte)(-128), descriptor.getValue()));
        if (properties != null && !properties.isEmpty()) {
            final ArrayList<ASN1Element> propElements = new ArrayList<ASN1Element>(properties.size());
            for (final String property : properties) {
                propElements.add(new ASN1OctetString(property));
            }
            elements.add(new ASN1Sequence((byte)(-95), propElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ASN1OctetString getDescriptor() {
        return this.descriptor;
    }
    
    public List<String> getProperties() {
        return this.properties;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_GET_BACKUP_COMPAT.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetBackupCompatibilityDescriptorExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.descriptor != null) {
            buffer.append(", descriptorLength=");
            buffer.append(this.descriptor.getValueLength());
        }
        if (!this.properties.isEmpty()) {
            buffer.append(", descriptorProperties={");
            final Iterator<String> iterator = this.properties.iterator();
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
