package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExcludeBranchRequestControl extends Control
{
    public static final String EXCLUDE_BRANCH_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.17";
    private static final byte TYPE_BASE_DNS = -96;
    private static final long serialVersionUID = -8599554860060612417L;
    private final List<String> baseDNs;
    
    public ExcludeBranchRequestControl(final Collection<String> baseDNs) {
        this(true, baseDNs);
    }
    
    public ExcludeBranchRequestControl(final String... baseDNs) {
        this(true, baseDNs);
    }
    
    public ExcludeBranchRequestControl(final boolean isCritical, final String... baseDNs) {
        super("1.3.6.1.4.1.30221.2.5.17", isCritical, encodeValue(baseDNs));
        this.baseDNs = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])baseDNs));
    }
    
    public ExcludeBranchRequestControl(final boolean isCritical, final Collection<String> baseDNs) {
        super("1.3.6.1.4.1.30221.2.5.17", isCritical, encodeValue(baseDNs));
        this.baseDNs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(baseDNs));
    }
    
    public ExcludeBranchRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_EXCLUDE_BRANCH_MISSING_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            valueSequence = ASN1Sequence.decodeAsSequence(value.getValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_EXCLUDE_BRANCH_VALUE_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            final ASN1Element[] elements = valueSequence.elements();
            final ASN1Element[] dnElements = ASN1Sequence.decodeAsSequence(elements[0]).elements();
            final ArrayList<String> dnList = new ArrayList<String>(dnElements.length);
            for (final ASN1Element e2 : dnElements) {
                dnList.add(ASN1OctetString.decodeAsOctetString(e2).stringValue());
            }
            this.baseDNs = Collections.unmodifiableList((List<? extends String>)dnList);
            if (this.baseDNs.isEmpty()) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_EXCLUDE_BRANCH_NO_BASE_DNS.get());
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_EXCLUDE_BRANCH_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String... baseDNs) {
        Validator.ensureNotNull(baseDNs);
        return encodeValue(Arrays.asList(baseDNs));
    }
    
    private static ASN1OctetString encodeValue(final Collection<String> baseDNs) {
        Validator.ensureNotNull(baseDNs);
        Validator.ensureFalse(baseDNs.isEmpty());
        final ArrayList<ASN1Element> dnElements = new ArrayList<ASN1Element>(baseDNs.size());
        for (final String s : baseDNs) {
            dnElements.add(new ASN1OctetString(s));
        }
        final ASN1Sequence baseDNSequence = new ASN1Sequence((byte)(-96), dnElements);
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { baseDNSequence });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public List<String> getBaseDNs() {
        return this.baseDNs;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_EXCLUDE_BRANCH.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExcludeBranchRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", baseDNs={");
        final Iterator<String> iterator = this.baseDNs.iterator();
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
