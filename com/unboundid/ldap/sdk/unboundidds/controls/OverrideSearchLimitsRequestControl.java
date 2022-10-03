package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OverrideSearchLimitsRequestControl extends Control
{
    public static final String OVERRIDE_SEARCH_LIMITS_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.56";
    private static final long serialVersionUID = 3685279915414141978L;
    private final Map<String, String> properties;
    
    public OverrideSearchLimitsRequestControl(final String propertyName, final String propertyValue) {
        this(Collections.singletonMap(propertyName, propertyValue), false);
    }
    
    public OverrideSearchLimitsRequestControl(final Map<String, String> properties, final boolean isCritical) {
        super("1.3.6.1.4.1.30221.2.5.56", isCritical, encodeValue(properties));
        this.properties = Collections.unmodifiableMap((Map<? extends String, ? extends String>)new LinkedHashMap<String, String>(properties));
    }
    
    public OverrideSearchLimitsRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_NO_VALUE.get());
        }
        final LinkedHashMap<String, String> propertyMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        try {
            for (final ASN1Element valueElement : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                final ASN1Element[] propertyElements = ASN1Sequence.decodeAsSequence(valueElement).elements();
                final String propertyName = ASN1OctetString.decodeAsOctetString(propertyElements[0]).stringValue();
                final String propertyValue = ASN1OctetString.decodeAsOctetString(propertyElements[1]).stringValue();
                if (propertyName.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_EMPTY_PROPERTY_NAME.get());
                }
                if (propertyValue.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_EMPTY_PROPERTY_VALUE.get(propertyName));
                }
                if (propertyMap.containsKey(propertyName)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_DUPLICATE_PROPERTY_NAME.get(propertyName));
                }
                propertyMap.put(propertyName, propertyValue);
            }
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            throw e;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (propertyMap.isEmpty()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_OVERRIDE_SEARCH_LIMITS_REQUEST_CONTROL_NO_PROPERTIES.get());
        }
        this.properties = Collections.unmodifiableMap((Map<? extends String, ? extends String>)propertyMap);
    }
    
    static ASN1OctetString encodeValue(final Map<String, String> properties) {
        Validator.ensureTrue(properties != null && !properties.isEmpty(), "OverrideSearchLimitsRequestControl.<init>properties must not be null or empty");
        final ArrayList<ASN1Element> propertyElements = new ArrayList<ASN1Element>(properties.size());
        for (final Map.Entry<String, String> e : properties.entrySet()) {
            final String propertyName = e.getKey();
            final String propertyValue = e.getValue();
            Validator.ensureTrue(propertyName != null && !propertyName.isEmpty(), "OverrideSearchLimitsRequestControl.<init>properties keys must not be null or empty");
            Validator.ensureTrue(propertyValue != null && !propertyValue.isEmpty(), "OverrideSearchLimitsRequestControl.<init>properties values must not be null or empty");
            propertyElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(propertyName), new ASN1OctetString(propertyValue) }));
        }
        return new ASN1OctetString(new ASN1Sequence(propertyElements).encode());
    }
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    public String getProperty(final String propertyName) {
        Validator.ensureTrue(propertyName != null && !propertyName.isEmpty(), "OverrideSearchLimitsRequestControl.getProperty.propertyName must not be null or empty.");
        return this.properties.get(propertyName);
    }
    
    public Boolean getPropertyAsBoolean(final String propertyName, final Boolean defaultValue) {
        final String propertyValue = this.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        final String lowerCase = StaticUtils.toLowerCase(propertyValue);
        switch (lowerCase) {
            case "true":
            case "t":
            case "yes":
            case "y":
            case "on":
            case "1": {
                return Boolean.TRUE;
            }
            case "false":
            case "f":
            case "no":
            case "n":
            case "off":
            case "0": {
                return Boolean.FALSE;
            }
            default: {
                return defaultValue;
            }
        }
    }
    
    public Integer getPropertyAsInteger(final String propertyName, final Integer defaultValue) {
        final String propertyValue = this.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(propertyValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return defaultValue;
        }
    }
    
    public Long getPropertyAsLong(final String propertyName, final Long defaultValue) {
        final String propertyValue = this.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(propertyValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return defaultValue;
        }
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_OVERRIDE_SEARCH_LIMITS_REQUEST_CONTROL_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("OverrideSearchLimitsRequestControl(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", properties={");
        final Iterator<Map.Entry<String, String>> iterator = this.properties.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> e = iterator.next();
            buffer.append('\'');
            buffer.append(e.getKey());
            buffer.append("'='");
            buffer.append(e.getValue());
            buffer.append('\'');
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
