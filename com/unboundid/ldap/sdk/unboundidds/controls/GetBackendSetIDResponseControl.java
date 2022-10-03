package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.List;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetBackendSetIDResponseControl extends Control implements DecodeableControl
{
    public static final String GET_BACKEND_SET_ID_RESPONSE_OID = "1.3.6.1.4.1.30221.2.5.34";
    private static final long serialVersionUID = 117359364981309726L;
    private final Set<String> backendSetIDs;
    private final String entryBalancingRequestProcessorID;
    
    GetBackendSetIDResponseControl() {
        this.entryBalancingRequestProcessorID = null;
        this.backendSetIDs = null;
    }
    
    public GetBackendSetIDResponseControl(final String entryBalancingRequestProcessorID, final String backendSetID) {
        this(entryBalancingRequestProcessorID, Collections.singletonList(backendSetID));
    }
    
    public GetBackendSetIDResponseControl(final String entryBalancingRequestProcessorID, final Collection<String> backendSetIDs) {
        super("1.3.6.1.4.1.30221.2.5.34", false, encodeValue(entryBalancingRequestProcessorID, backendSetIDs));
        this.entryBalancingRequestProcessorID = entryBalancingRequestProcessorID;
        this.backendSetIDs = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(backendSetIDs));
    }
    
    public GetBackendSetIDResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_BACKEND_SET_ID_RESPONSE_MISSING_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.entryBalancingRequestProcessorID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final ASN1Element[] backendSetIDElements = ASN1Set.decodeAsSet(elements[1]).elements();
            final LinkedHashSet<String> setIDs = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(backendSetIDElements.length));
            for (final ASN1Element e : backendSetIDElements) {
                setIDs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
            }
            this.backendSetIDs = Collections.unmodifiableSet((Set<? extends String>)setIDs);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GET_BACKEND_SET_ID_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final String entryBalancingRequestProcessorID, final Collection<String> backendSetIDs) {
        Validator.ensureNotNull(entryBalancingRequestProcessorID);
        Validator.ensureNotNull(backendSetIDs);
        Validator.ensureFalse(backendSetIDs.isEmpty());
        final ArrayList<ASN1Element> backendSetIDElements = new ArrayList<ASN1Element>(backendSetIDs.size());
        for (final String s : backendSetIDs) {
            backendSetIDElements.add(new ASN1OctetString(s));
        }
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(entryBalancingRequestProcessorID), new ASN1Set(backendSetIDElements) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    @Override
    public GetBackendSetIDResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new GetBackendSetIDResponseControl(oid, isCritical, value);
    }
    
    public String getEntryBalancingRequestProcessorID() {
        return this.entryBalancingRequestProcessorID;
    }
    
    public Set<String> getBackendSetIDs() {
        return this.backendSetIDs;
    }
    
    public static GetBackendSetIDResponseControl get(final LDAPResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.3.6.1.4.1.30221.2.5.34");
        if (c == null) {
            return null;
        }
        if (c instanceof GetBackendSetIDResponseControl) {
            return (GetBackendSetIDResponseControl)c;
        }
        return new GetBackendSetIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static GetBackendSetIDResponseControl get(final SearchResultEntry entry) throws LDAPException {
        final Control c = entry.getControl("1.3.6.1.4.1.30221.2.5.34");
        if (c == null) {
            return null;
        }
        if (c instanceof GetBackendSetIDResponseControl) {
            return (GetBackendSetIDResponseControl)c;
        }
        return new GetBackendSetIDResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    public static List<GetBackendSetIDResponseControl> get(final ExtendedResult result) throws LDAPException {
        final Control[] controls = result.getResponseControls();
        if (controls.length == 0) {
            return Collections.emptyList();
        }
        final ArrayList<GetBackendSetIDResponseControl> decodedControls = new ArrayList<GetBackendSetIDResponseControl>(controls.length);
        for (final Control c : controls) {
            if (c instanceof GetBackendSetIDResponseControl) {
                decodedControls.add((GetBackendSetIDResponseControl)c);
            }
            else if (c.getOID().equals("1.3.6.1.4.1.30221.2.5.34")) {
                decodedControls.add(new GetBackendSetIDResponseControl(c.getOID(), c.isCritical(), c.getValue()));
            }
        }
        return Collections.unmodifiableList((List<? extends GetBackendSetIDResponseControl>)decodedControls);
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_BACKEND_SET_ID_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetBackendSetIDResponseControl(entryBalancingRequestProcessorID='");
        buffer.append(this.entryBalancingRequestProcessorID);
        buffer.append("', backendSetIDs={");
        final Iterator<String> iterator = this.backendSetIDs.iterator();
        while (iterator.hasNext()) {
            buffer.append('\'');
            buffer.append(iterator.next());
            buffer.append('\'');
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
