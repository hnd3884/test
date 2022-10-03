package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Collection;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Control;
import java.util.Arrays;
import com.unboundid.ldap.sdk.LDAPRequest;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MultiUpdateExtendedRequest extends ExtendedRequest
{
    public static final String MULTI_UPDATE_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.17";
    private static final long serialVersionUID = 6101686180473949142L;
    private final List<LDAPRequest> requests;
    private final MultiUpdateErrorBehavior errorBehavior;
    
    public MultiUpdateExtendedRequest(final MultiUpdateErrorBehavior errorBehavior, final LDAPRequest... requests) throws LDAPException {
        this(errorBehavior, Arrays.asList(requests), new Control[0]);
    }
    
    public MultiUpdateExtendedRequest(final MultiUpdateErrorBehavior errorBehavior, final LDAPRequest[] requests, final Control... controls) throws LDAPException {
        this(errorBehavior, Arrays.asList(requests), controls);
    }
    
    public MultiUpdateExtendedRequest(final MultiUpdateErrorBehavior errorBehavior, final List<LDAPRequest> requests, final Control... controls) throws LDAPException {
        super("1.3.6.1.4.1.30221.2.6.17", encodeValue(errorBehavior, requests), controls);
        this.errorBehavior = errorBehavior;
        final ArrayList<LDAPRequest> requestList = new ArrayList<LDAPRequest>(requests.size());
        for (final LDAPRequest r : requests) {
            switch (r.getOperationType()) {
                case ADD:
                case DELETE:
                case MODIFY:
                case MODIFY_DN:
                case EXTENDED: {
                    requestList.add(r);
                    continue;
                }
                default: {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_REQUEST_INVALID_REQUEST_TYPE.get(r.getOperationType().name()));
                }
            }
        }
        this.requests = Collections.unmodifiableList((List<? extends LDAPRequest>)requestList);
    }
    
    private MultiUpdateExtendedRequest(final MultiUpdateErrorBehavior errorBehavior, final List<LDAPRequest> requests, final ASN1OctetString encodedValue, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.17", encodedValue, controls);
        this.errorBehavior = errorBehavior;
        this.requests = requests;
    }
    
    public MultiUpdateExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] ve = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.errorBehavior = MultiUpdateErrorBehavior.valueOf(ASN1Enumerated.decodeAsEnumerated(ve[0]).intValue());
            if (this.errorBehavior == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_REQUEST_INVALID_ERROR_BEHAVIOR.get(ASN1Enumerated.decodeAsEnumerated(ve[0]).intValue()));
            }
            final ASN1Element[] requestSequenceElements = ASN1Sequence.decodeAsSequence(ve[1]).elements();
            final ArrayList<LDAPRequest> rl = new ArrayList<LDAPRequest>(requestSequenceElements.length);
            for (final ASN1Element rse : requestSequenceElements) {
                final ASN1Element[] requestElements = ASN1Sequence.decodeAsSequence(rse).elements();
                Control[] controls;
                if (requestElements.length == 2) {
                    controls = Control.decodeControls(ASN1Sequence.decodeAsSequence(requestElements[1]));
                }
                else {
                    controls = StaticUtils.NO_CONTROLS;
                }
                switch (requestElements[0].getType()) {
                    case 104: {
                        rl.add(AddRequestProtocolOp.decodeProtocolOp(requestElements[0]).toAddRequest(controls));
                        break;
                    }
                    case 74: {
                        rl.add(DeleteRequestProtocolOp.decodeProtocolOp(requestElements[0]).toDeleteRequest(controls));
                        break;
                    }
                    case 119: {
                        rl.add(ExtendedRequestProtocolOp.decodeProtocolOp(requestElements[0]).toExtendedRequest(controls));
                        break;
                    }
                    case 102: {
                        rl.add(ModifyRequestProtocolOp.decodeProtocolOp(requestElements[0]).toModifyRequest(controls));
                        break;
                    }
                    case 108: {
                        rl.add(ModifyDNRequestProtocolOp.decodeProtocolOp(requestElements[0]).toModifyDNRequest(controls));
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_REQUEST_INVALID_OP_TYPE.get(StaticUtils.toHex(requestElements[0].getType())));
                    }
                }
            }
            this.requests = Collections.unmodifiableList((List<? extends LDAPRequest>)rl);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_MULTI_UPDATE_REQUEST_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final MultiUpdateErrorBehavior errorBehavior, final List<LDAPRequest> requests) {
        final ArrayList<ASN1Element> requestElements = new ArrayList<ASN1Element>(requests.size());
        for (final LDAPRequest r : requests) {
            final ArrayList<ASN1Element> rsElements = new ArrayList<ASN1Element>(2);
            switch (r.getOperationType()) {
                case ADD: {
                    rsElements.add(((AddRequest)r).encodeProtocolOp());
                    break;
                }
                case DELETE: {
                    rsElements.add(((DeleteRequest)r).encodeProtocolOp());
                    break;
                }
                case MODIFY: {
                    rsElements.add(((ModifyRequest)r).encodeProtocolOp());
                    break;
                }
                case MODIFY_DN: {
                    rsElements.add(((ModifyDNRequest)r).encodeProtocolOp());
                    break;
                }
                case EXTENDED: {
                    rsElements.add(((ExtendedRequest)r).encodeProtocolOp());
                    break;
                }
            }
            if (r.hasControl()) {
                rsElements.add(Control.encodeControls(r.getControls()));
            }
            requestElements.add(new ASN1Sequence(rsElements));
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1Enumerated(errorBehavior.intValue()), new ASN1Sequence(requestElements) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public MultiUpdateExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new MultiUpdateExtendedResult(extendedResponse);
    }
    
    public MultiUpdateErrorBehavior getErrorBehavior() {
        return this.errorBehavior;
    }
    
    public List<LDAPRequest> getRequests() {
        return this.requests;
    }
    
    @Override
    public MultiUpdateExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public MultiUpdateExtendedRequest duplicate(final Control[] controls) {
        final MultiUpdateExtendedRequest r = new MultiUpdateExtendedRequest(this.errorBehavior, this.requests, this.getValue(), controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_MULTI_UPDATE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("MultiUpdateExtendedRequest(errorBehavior=");
        buffer.append(this.errorBehavior.name());
        buffer.append(", requests={");
        final Iterator<LDAPRequest> iterator = this.requests.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
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
