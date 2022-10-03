package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Null;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.ExtendedRequest;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetConfigurationExtendedRequest extends ExtendedRequest
{
    public static final String GET_CONFIG_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.28";
    private static final long serialVersionUID = 2953462215986675988L;
    private final GetConfigurationType configurationType;
    private final String fileName;
    
    public GetConfigurationExtendedRequest(final ExtendedRequest r) throws LDAPException {
        super(r);
        final ASN1OctetString value = r.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONFIG_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            switch (elements[0].getType()) {
                case Byte.MIN_VALUE: {
                    this.configurationType = GetConfigurationType.ACTIVE;
                    this.fileName = null;
                    break;
                }
                case -127: {
                    this.configurationType = GetConfigurationType.BASELINE;
                    this.fileName = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
                    break;
                }
                case -126: {
                    this.configurationType = GetConfigurationType.ARCHIVED;
                    this.fileName = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONFIG_REQUEST_UNEXPECTED_CONFIG_TYPE.get(StaticUtils.toHex(elements[0].getType())));
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONFIG_REQUEST_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private GetConfigurationExtendedRequest(final GetConfigurationType configurationType, final String fileName, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.28", encodeValue(configurationType, fileName), controls);
        this.configurationType = configurationType;
        this.fileName = fileName;
    }
    
    private static ASN1OctetString encodeValue(final GetConfigurationType configurationType, final String fileName) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(0);
        switch (configurationType) {
            case ACTIVE: {
                elements.add(new ASN1Null(configurationType.getBERType()));
                break;
            }
            case BASELINE:
            case ARCHIVED: {
                elements.add(new ASN1OctetString(configurationType.getBERType(), fileName));
                break;
            }
            default: {
                return null;
            }
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public static GetConfigurationExtendedRequest createGetActiveConfigurationRequest(final Control... controls) {
        return new GetConfigurationExtendedRequest(GetConfigurationType.ACTIVE, null, controls);
    }
    
    public static GetConfigurationExtendedRequest createGetBaselineConfigurationRequest(final String fileName, final Control... controls) {
        Validator.ensureNotNull(fileName);
        return new GetConfigurationExtendedRequest(GetConfigurationType.BASELINE, fileName, controls);
    }
    
    public static GetConfigurationExtendedRequest createGetArchivedConfigurationRequest(final String fileName, final Control... controls) {
        Validator.ensureNotNull(fileName);
        return new GetConfigurationExtendedRequest(GetConfigurationType.ARCHIVED, fileName, controls);
    }
    
    public GetConfigurationType getConfigurationType() {
        return this.configurationType;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public GetConfigurationExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new GetConfigurationExtendedResult(extendedResponse);
    }
    
    @Override
    public GetConfigurationExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public GetConfigurationExtendedRequest duplicate(final Control[] controls) {
        final GetConfigurationExtendedRequest r = new GetConfigurationExtendedRequest(this.configurationType, this.fileName, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_GET_CONFIG.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetConfigurationsExtendedRequest(configType=");
        buffer.append(this.configurationType.name());
        if (this.fileName != null) {
            buffer.append(", fileName='");
            buffer.append(this.fileName);
            buffer.append('\'');
        }
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
