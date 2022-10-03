package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.ModifyRequest;
import java.util.Collections;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = -6294739625253826184L;
    private final List<Modification> modifications;
    private final String dn;
    
    public ModifyRequestProtocolOp(final String dn, final List<Modification> modifications) {
        this.dn = dn;
        this.modifications = Collections.unmodifiableList((List<? extends Modification>)modifications);
    }
    
    public ModifyRequestProtocolOp(final ModifyRequest request) {
        this.dn = request.getDN();
        this.modifications = request.getModifications();
    }
    
    ModifyRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            reader.beginSequence();
            Validator.ensureNotNull(this.dn = reader.readString());
            final ArrayList<Modification> mods = new ArrayList<Modification>(5);
            final ASN1StreamReaderSequence modSequence = reader.beginSequence();
            while (modSequence.hasMoreElements()) {
                mods.add(Modification.readFrom(reader));
            }
            this.modifications = Collections.unmodifiableList((List<? extends Modification>)mods);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MODIFY_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public List<Modification> getModifications() {
        return this.modifications;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 102;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> modElements = new ArrayList<ASN1Element>(this.modifications.size());
        for (final Modification m : this.modifications) {
            modElements.add(m.encode());
        }
        return new ASN1Sequence((byte)102, new ASN1Element[] { new ASN1OctetString(this.dn), new ASN1Sequence(modElements) });
    }
    
    public static ModifyRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String dn = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final ASN1Element[] modElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
            final ArrayList<Modification> mods = new ArrayList<Modification>(modElements.length);
            for (final ASN1Element e : modElements) {
                mods.add(Modification.decode(ASN1Sequence.decodeAsSequence(e)));
            }
            return new ModifyRequestProtocolOp(dn, mods);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MODIFY_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer writer) {
        final ASN1BufferSequence opSequence = writer.beginSequence((byte)102);
        writer.addOctetString(this.dn);
        final ASN1BufferSequence modSequence = writer.beginSequence();
        for (final Modification m : this.modifications) {
            m.writeTo(writer);
        }
        modSequence.end();
        opSequence.end();
    }
    
    public ModifyRequest toModifyRequest(final Control... controls) {
        return new ModifyRequest(this.dn, this.modifications, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ModifyRequestProtocolOp(dn='");
        buffer.append(this.dn);
        buffer.append("', mods={");
        final Iterator<Modification> iterator = this.modifications.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("})");
    }
}
