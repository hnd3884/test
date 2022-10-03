package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import java.util.LinkedHashMap;
import java.util.Collections;
import com.unboundid.util.Validator;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PasswordQualityRequirement implements Serializable
{
    private static final byte TYPE_CLIENT_SIDE_VALIDATION_INFO = -95;
    private static final byte TYPE_CLIENT_SIDE_VALIDATION_PROPERTIES = -95;
    private static final long serialVersionUID = 2956655422853571644L;
    private final Map<String, String> clientSideValidationProperties;
    private final String clientSideValidationType;
    private final String description;
    
    public PasswordQualityRequirement(final String description) {
        this(description, null, null);
    }
    
    public PasswordQualityRequirement(final String description, final String clientSideValidationType, final Map<String, String> clientSideValidationProperties) {
        Validator.ensureNotNull(description);
        if (clientSideValidationType == null) {
            Validator.ensureTrue(clientSideValidationProperties == null || clientSideValidationProperties.isEmpty());
        }
        this.description = description;
        this.clientSideValidationType = clientSideValidationType;
        if (clientSideValidationProperties == null) {
            this.clientSideValidationProperties = Collections.emptyMap();
        }
        else {
            this.clientSideValidationProperties = Collections.unmodifiableMap((Map<? extends String, ? extends String>)new LinkedHashMap<String, String>(clientSideValidationProperties));
        }
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getClientSideValidationType() {
        return this.clientSideValidationType;
    }
    
    public Map<String, String> getClientSideValidationProperties() {
        return this.clientSideValidationProperties;
    }
    
    public ASN1Element encode() {
        final ArrayList<ASN1Element> requirementElements = new ArrayList<ASN1Element>(2);
        requirementElements.add(new ASN1OctetString(this.description));
        if (this.clientSideValidationType != null) {
            final ArrayList<ASN1Element> clientSideElements = new ArrayList<ASN1Element>(2);
            clientSideElements.add(new ASN1OctetString(this.clientSideValidationType));
            if (!this.clientSideValidationProperties.isEmpty()) {
                final ArrayList<ASN1Element> propertyElements = new ArrayList<ASN1Element>(this.clientSideValidationProperties.size());
                for (final Map.Entry<String, String> e : this.clientSideValidationProperties.entrySet()) {
                    propertyElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(e.getKey()), new ASN1OctetString(e.getValue()) }));
                }
                clientSideElements.add(new ASN1Set((byte)(-95), propertyElements));
            }
            requirementElements.add(new ASN1Sequence((byte)(-95), clientSideElements));
        }
        return new ASN1Sequence(requirementElements);
    }
    
    public static PasswordQualityRequirement decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] requirementElements = ASN1Sequence.decodeAsSequence(element).elements();
            final String description = ASN1OctetString.decodeAsOctetString(requirementElements[0]).stringValue();
            String clientSideValidationType = null;
            Map<String, String> clientSideValidationProperties = null;
            int i = 1;
            while (i < requirementElements.length) {
                final ASN1Element requirementElement = requirementElements[i];
                switch (requirementElement.getType()) {
                    case -95: {
                        final ASN1Element[] csvInfoElements = ASN1Sequence.decodeAsSequence(requirementElement).elements();
                        clientSideValidationType = ASN1OctetString.decodeAsOctetString(csvInfoElements[0]).stringValue();
                        int j = 1;
                        while (j < csvInfoElements.length) {
                            final ASN1Element csvInfoElement = csvInfoElements[j];
                            switch (csvInfoElement.getType()) {
                                case -95: {
                                    final ASN1Element[] csvPropElements = ASN1Sequence.decodeAsSequence(csvInfoElement).elements();
                                    clientSideValidationProperties = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(csvPropElements.length));
                                    for (final ASN1Element csvPropElement : csvPropElements) {
                                        final ASN1Element[] propElements = ASN1Sequence.decodeAsSequence(csvPropElement).elements();
                                        final String name = ASN1OctetString.decodeAsOctetString(propElements[0]).stringValue();
                                        final String value = ASN1OctetString.decodeAsOctetString(propElements[1]).stringValue();
                                        clientSideValidationProperties.put(name, value);
                                    }
                                    ++j;
                                    continue;
                                }
                                default: {
                                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_QUALITY_REQ_INVALID_CSV_ELEMENT_TYPE.get(StaticUtils.toHex(csvInfoElement.getType())));
                                }
                            }
                        }
                        ++i;
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_QUALITY_REQ_INVALID_REQ_ELEMENT_TYPE.get(StaticUtils.toHex(requirementElement.getType())));
                    }
                }
            }
            return new PasswordQualityRequirement(description, clientSideValidationType, clientSideValidationProperties);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_PW_QUALITY_REQ_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("PasswordQualityRequirement(description='");
        buffer.append(this.description);
        buffer.append('\'');
        if (this.clientSideValidationType != null) {
            buffer.append(", clientSideValidationType='");
            buffer.append(this.clientSideValidationType);
            buffer.append('\'');
            if (!this.clientSideValidationProperties.isEmpty()) {
                buffer.append(", clientSideValidationProperties={");
                final Iterator<Map.Entry<String, String>> iterator = this.clientSideValidationProperties.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, String> e = iterator.next();
                    buffer.append('\'');
                    buffer.append(e.getKey());
                    buffer.append("'='");
                    buffer.append(e.getValue());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(',');
                    }
                }
                buffer.append('}');
            }
        }
        buffer.append(')');
    }
}
