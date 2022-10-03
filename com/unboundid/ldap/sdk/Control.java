package com.unboundid.ldap.sdk;

import com.unboundid.ldap.sdk.controls.ControlHelper;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1StreamReader;
import java.util.Collection;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class Control implements Serializable
{
    private static final byte CONTROLS_TYPE = -96;
    private static final ConcurrentHashMap<String, DecodeableControl> decodeableControlMap;
    private static final long serialVersionUID = 4440956109070220054L;
    private final ASN1OctetString value;
    private final boolean isCritical;
    private final String oid;
    
    protected Control() {
        this.oid = null;
        this.isCritical = true;
        this.value = null;
    }
    
    protected Control(final Control control) {
        this.oid = control.oid;
        this.isCritical = control.isCritical;
        this.value = control.value;
    }
    
    public Control(final String oid) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.isCritical = false;
        this.value = null;
    }
    
    public Control(final String oid, final boolean isCritical) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.isCritical = isCritical;
        this.value = null;
    }
    
    public Control(final String oid, final boolean isCritical, final ASN1OctetString value) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.isCritical = isCritical;
        this.value = value;
    }
    
    public final String getOID() {
        return this.oid;
    }
    
    public final boolean isCritical() {
        return this.isCritical;
    }
    
    public final boolean hasValue() {
        return this.value != null;
    }
    
    public final ASN1OctetString getValue() {
        return this.value;
    }
    
    public final void writeTo(final ASN1Buffer writer) {
        final ASN1BufferSequence controlSequence = writer.beginSequence();
        writer.addOctetString(this.oid);
        if (this.isCritical) {
            writer.addBoolean(true);
        }
        if (this.value != null) {
            writer.addOctetString(this.value.getValue());
        }
        controlSequence.end();
    }
    
    public final ASN1Sequence encode() {
        final ArrayList<ASN1Element> elementList = new ArrayList<ASN1Element>(3);
        elementList.add(new ASN1OctetString(this.oid));
        if (this.isCritical) {
            elementList.add(new ASN1Boolean(this.isCritical));
        }
        if (this.value != null) {
            elementList.add(new ASN1OctetString(this.value.getValue()));
        }
        return new ASN1Sequence(elementList);
    }
    
    public static Control readFrom(final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
            final String oid = reader.readString();
            boolean isCritical = false;
            ASN1OctetString value = null;
            while (controlSequence.hasMoreElements()) {
                final byte type = (byte)reader.peek();
                switch (type) {
                    case 1: {
                        isCritical = reader.readBoolean();
                        continue;
                    }
                    case 4: {
                        value = new ASN1OctetString(reader.readBytes());
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROL_INVALID_TYPE.get(StaticUtils.toHex(type)));
                    }
                }
            }
            return decode(oid, isCritical, value);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROL_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static Control decode(final ASN1Sequence controlSequence) throws LDAPException {
        final ASN1Element[] elements = controlSequence.elements();
        if (elements.length < 1 || elements.length > 3) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROL_DECODE_INVALID_ELEMENT_COUNT.get(elements.length));
        }
        final String oid = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        boolean isCritical = false;
        ASN1OctetString value = null;
        if (elements.length == 2) {
            switch (elements[1].getType()) {
                case 1: {
                    try {
                        isCritical = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
                        break;
                    }
                    catch (final ASN1Exception ae) {
                        Debug.debugException(ae);
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROL_DECODE_CRITICALITY.get(StaticUtils.getExceptionMessage(ae)), ae);
                    }
                }
                case 4: {
                    value = ASN1OctetString.decodeAsOctetString(elements[1]);
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROL_INVALID_TYPE.get(StaticUtils.toHex(elements[1].getType())));
                }
            }
        }
        else if (elements.length == 3) {
            try {
                isCritical = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
            }
            catch (final ASN1Exception ae) {
                Debug.debugException(ae);
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROL_DECODE_CRITICALITY.get(StaticUtils.getExceptionMessage(ae)), ae);
            }
            value = ASN1OctetString.decodeAsOctetString(elements[2]);
        }
        return decode(oid, isCritical, value);
    }
    
    public static Control decode(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        final DecodeableControl decodeableControl = Control.decodeableControlMap.get(oid);
        if (decodeableControl == null) {
            return new Control(oid, isCritical, value);
        }
        try {
            return decodeableControl.decodeControl(oid, isCritical, value);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return new Control(oid, isCritical, value);
        }
    }
    
    public static ASN1Sequence encodeControls(final Control[] controls) {
        final ASN1Sequence[] controlElements = new ASN1Sequence[controls.length];
        for (int i = 0; i < controls.length; ++i) {
            controlElements[i] = controls[i].encode();
        }
        return new ASN1Sequence((byte)(-96), (ASN1Element[])controlElements);
    }
    
    public static Control[] decodeControls(final ASN1Sequence controlSequence) throws LDAPException {
        final ASN1Element[] controlElements = controlSequence.elements();
        final Control[] controls = new Control[controlElements.length];
        for (int i = 0; i < controlElements.length; ++i) {
            try {
                controls[i] = decode(ASN1Sequence.decodeAsSequence(controlElements[i]));
            }
            catch (final ASN1Exception ae) {
                Debug.debugException(ae);
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CONTROLS_DECODE_ELEMENT_NOT_SEQUENCE.get(StaticUtils.getExceptionMessage(ae)), ae);
            }
        }
        return controls;
    }
    
    public static void registerDecodeableControl(final String oid, final DecodeableControl controlInstance) {
        Control.decodeableControlMap.put(oid, controlInstance);
    }
    
    public static void deregisterDecodeableControl(final String oid) {
        Control.decodeableControlMap.remove(oid);
    }
    
    @Override
    public final int hashCode() {
        int hashCode = this.oid.hashCode();
        if (this.isCritical) {
            ++hashCode;
        }
        if (this.value != null) {
            hashCode += this.value.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Control)) {
            return false;
        }
        final Control c = (Control)o;
        if (!this.oid.equals(c.oid)) {
            return false;
        }
        if (this.isCritical != c.isCritical) {
            return false;
        }
        if (this.value == null) {
            if (c.value != null) {
                return false;
            }
        }
        else {
            if (c.value == null) {
                return false;
            }
            if (!this.value.equals(c.value)) {
                return false;
            }
        }
        return true;
    }
    
    public String getControlName() {
        return this.oid;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("Control(oid=");
        buffer.append(this.oid);
        buffer.append(", isCritical=");
        buffer.append(this.isCritical);
        buffer.append(", value=");
        if (this.value == null) {
            buffer.append("{null}");
        }
        else {
            buffer.append("{byte[");
            buffer.append(this.value.getValue().length);
            buffer.append("]}");
        }
        buffer.append(')');
    }
    
    static {
        decodeableControlMap = new ConcurrentHashMap<String, DecodeableControl>(StaticUtils.computeMapCapacity(50));
        ControlHelper.registerDefaultResponseControls();
        com.unboundid.ldap.sdk.experimental.ControlHelper.registerDefaultResponseControls();
        com.unboundid.ldap.sdk.unboundidds.controls.ControlHelper.registerDefaultResponseControls();
    }
}
