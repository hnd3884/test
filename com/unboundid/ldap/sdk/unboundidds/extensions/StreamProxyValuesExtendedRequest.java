package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Integer;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StreamProxyValuesExtendedRequest extends ExtendedRequest
{
    public static final String STREAM_PROXY_VALUES_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.8";
    private static final byte TYPE_BASE_DN = Byte.MIN_VALUE;
    private static final byte TYPE_INCLUDE_DNS = -95;
    private static final byte TYPE_ATTRIBUTES = -94;
    private static final byte TYPE_VALUES_PER_RESPONSE = -125;
    private static final byte TYPE_BACKEND_SETS = -92;
    private static final byte TYPE_SCOPE = Byte.MIN_VALUE;
    private static final byte TYPE_RELATIVE = -127;
    private static final long serialVersionUID = 2528621021697410806L;
    private final boolean returnRelativeDNs;
    private final int valuesPerResponse;
    private final List<StreamProxyValuesBackendSet> backendSets;
    private final List<String> attributes;
    private final SearchScope dnScope;
    private final String baseDN;
    
    public StreamProxyValuesExtendedRequest(final String baseDN, final SearchScope dnScope, final boolean returnRelativeDNs, final List<String> attributes, final int valuesPerResponse, final List<StreamProxyValuesBackendSet> backendSets, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.8", encodeValue(baseDN, dnScope, returnRelativeDNs, attributes, valuesPerResponse, backendSets), controls);
        this.baseDN = baseDN;
        this.dnScope = dnScope;
        this.returnRelativeDNs = returnRelativeDNs;
        this.backendSets = Collections.unmodifiableList((List<? extends StreamProxyValuesBackendSet>)backendSets);
        if (attributes == null) {
            this.attributes = Collections.emptyList();
        }
        else {
            this.attributes = Collections.unmodifiableList((List<? extends String>)attributes);
        }
        if (valuesPerResponse < 0) {
            this.valuesPerResponse = 0;
        }
        else {
            this.valuesPerResponse = valuesPerResponse;
        }
    }
    
    public StreamProxyValuesExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_REQUEST_NO_VALUE.get());
        }
        boolean tmpRelative = true;
        int tmpNumValues = 0;
        final ArrayList<String> tmpAttrs = new ArrayList<String>(10);
        SearchScope tmpScope = null;
        String tmpBaseDN = null;
        final ArrayList<StreamProxyValuesBackendSet> tmpBackendSets = new ArrayList<StreamProxyValuesBackendSet>(10);
        try {
            final ASN1Element[] arr$;
            final ASN1Element[] svElements = arr$ = ASN1Element.decode(value.getValue()).decodeAsSequence().elements();
            for (final ASN1Element svElement : arr$) {
                switch (svElement.getType()) {
                    case Byte.MIN_VALUE: {
                        tmpBaseDN = svElement.decodeAsOctetString().stringValue();
                        break;
                    }
                    case -95: {
                        final ASN1Element[] arr$2;
                        final ASN1Element[] idElements = arr$2 = svElement.decodeAsSequence().elements();
                        for (final ASN1Element idElement : arr$2) {
                            switch (idElement.getType()) {
                                case Byte.MIN_VALUE: {
                                    final int scopeValue = idElement.decodeAsEnumerated().intValue();
                                    tmpScope = SearchScope.definedValueOf(scopeValue);
                                    if (tmpScope == null) {
                                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_REQUEST_INVALID_SCOPE.get(scopeValue));
                                    }
                                    break;
                                }
                                case -127: {
                                    tmpRelative = idElement.decodeAsBoolean().booleanValue();
                                    break;
                                }
                                default: {
                                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_REQUEST_INVALID_INCLUDE_DNS_TYPE.get(StaticUtils.toHex(idElement.getType())));
                                }
                            }
                        }
                        break;
                    }
                    case -94: {
                        final ASN1Element[] arr$3;
                        final ASN1Element[] attrElements = arr$3 = svElement.decodeAsSequence().elements();
                        for (final ASN1Element attrElement : arr$3) {
                            tmpAttrs.add(attrElement.decodeAsOctetString().stringValue());
                        }
                        break;
                    }
                    case -125: {
                        tmpNumValues = svElement.decodeAsInteger().intValue();
                        if (tmpNumValues < 0) {
                            tmpNumValues = 0;
                            break;
                        }
                        break;
                    }
                    case -92: {
                        final ASN1Element[] arr$4;
                        final ASN1Element[] backendSetElements = arr$4 = svElement.decodeAsSequence().elements();
                        for (final ASN1Element setElement : arr$4) {
                            tmpBackendSets.add(StreamProxyValuesBackendSet.decode(setElement));
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_REQUEST_INVALID_SEQUENCE_TYPE.get(StaticUtils.toHex(svElement.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        if (tmpBaseDN == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_REQUEST_NO_BASE_DN.get());
        }
        this.baseDN = tmpBaseDN;
        this.dnScope = tmpScope;
        this.returnRelativeDNs = tmpRelative;
        this.backendSets = Collections.unmodifiableList((List<? extends StreamProxyValuesBackendSet>)tmpBackendSets);
        this.attributes = Collections.unmodifiableList((List<? extends String>)tmpAttrs);
        this.valuesPerResponse = tmpNumValues;
    }
    
    private static ASN1OctetString encodeValue(final String baseDN, final SearchScope scope, final boolean relativeDNs, final List<String> attributes, final int valuesPerResponse, final List<StreamProxyValuesBackendSet> backendSets) {
        Validator.ensureNotNull(baseDN, backendSets);
        Validator.ensureFalse(backendSets.isEmpty());
        final ArrayList<ASN1Element> svElements = new ArrayList<ASN1Element>(4);
        svElements.add(new ASN1OctetString((byte)(-128), baseDN));
        if (scope != null) {
            final ArrayList<ASN1Element> idElements = new ArrayList<ASN1Element>(2);
            idElements.add(new ASN1Enumerated((byte)(-128), scope.intValue()));
            if (!relativeDNs) {
                idElements.add(new ASN1Boolean((byte)(-127), relativeDNs));
            }
            svElements.add(new ASN1Sequence((byte)(-95), idElements));
        }
        if (attributes != null && !attributes.isEmpty()) {
            final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(attributes.size());
            for (final String s : attributes) {
                attrElements.add(new ASN1OctetString(s));
            }
            svElements.add(new ASN1Sequence((byte)(-94), attrElements));
        }
        if (valuesPerResponse > 0) {
            svElements.add(new ASN1Integer((byte)(-125), valuesPerResponse));
        }
        final ASN1Element[] backendSetElements = new ASN1Element[backendSets.size()];
        for (int i = 0; i < backendSetElements.length; ++i) {
            backendSetElements[i] = backendSets.get(i).encode();
        }
        svElements.add(new ASN1Sequence((byte)(-92), backendSetElements));
        return new ASN1OctetString(new ASN1Sequence(svElements).encode());
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public SearchScope getDNScope() {
        return this.dnScope;
    }
    
    public boolean returnRelativeDNs() {
        return this.returnRelativeDNs;
    }
    
    public List<String> getAttributes() {
        return this.attributes;
    }
    
    public int getValuesPerResponse() {
        return this.valuesPerResponse;
    }
    
    public List<StreamProxyValuesBackendSet> getBackendSets() {
        return this.backendSets;
    }
    
    @Override
    public StreamProxyValuesExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public StreamProxyValuesExtendedRequest duplicate(final Control[] controls) {
        final StreamProxyValuesExtendedRequest r = new StreamProxyValuesExtendedRequest(this.baseDN, this.dnScope, this.returnRelativeDNs, this.attributes, this.valuesPerResponse, this.backendSets, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_STREAM_PROXY_VALUES.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StreamProxyValuesExtendedRequest(baseDN='");
        buffer.append(this.baseDN);
        buffer.append('\'');
        if (this.dnScope != null) {
            buffer.append(", scope='");
            buffer.append(this.dnScope.getName());
            buffer.append("', returnRelativeDNs=");
            buffer.append(this.returnRelativeDNs);
        }
        buffer.append(", attributes={");
        if (!this.attributes.isEmpty()) {
            final Iterator<String> iterator = this.attributes.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
        }
        buffer.append('}');
        if (this.valuesPerResponse > 0) {
            buffer.append(", valuesPerResponse=");
            buffer.append(this.valuesPerResponse);
        }
        buffer.append(", backendSets={");
        final Iterator<StreamProxyValuesBackendSet> setIterator = this.backendSets.iterator();
        while (setIterator.hasNext()) {
            setIterator.next().toString(buffer);
            if (setIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
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
