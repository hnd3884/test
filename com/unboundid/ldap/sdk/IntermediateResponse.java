package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;
import com.unboundid.ldap.protocol.LDAPResponse;
import java.io.Serializable;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class IntermediateResponse implements Serializable, LDAPResponse
{
    protected static final byte TYPE_INTERMEDIATE_RESPONSE_OID = Byte.MIN_VALUE;
    protected static final byte TYPE_INTERMEDIATE_RESPONSE_VALUE = -127;
    private static final Control[] NO_CONTROLS;
    private static final long serialVersionUID = 218434694212935869L;
    private final ASN1OctetString value;
    private final Control[] controls;
    private final int messageID;
    private final String oid;
    
    public IntermediateResponse(final String oid, final ASN1OctetString value) {
        this(-1, oid, value, IntermediateResponse.NO_CONTROLS);
    }
    
    public IntermediateResponse(final int messageID, final String oid, final ASN1OctetString value) {
        this(messageID, oid, value, IntermediateResponse.NO_CONTROLS);
    }
    
    public IntermediateResponse(final String oid, final ASN1OctetString value, final Control[] controls) {
        this(-1, oid, value, controls);
    }
    
    public IntermediateResponse(final int messageID, final String oid, final ASN1OctetString value, final Control[] controls) {
        this.messageID = messageID;
        this.oid = oid;
        this.value = value;
        if (controls == null) {
            this.controls = IntermediateResponse.NO_CONTROLS;
        }
        else {
            this.controls = controls;
        }
    }
    
    protected IntermediateResponse(final IntermediateResponse intermediateResponse) {
        this.messageID = intermediateResponse.messageID;
        this.oid = intermediateResponse.oid;
        this.value = intermediateResponse.value;
        this.controls = intermediateResponse.controls;
    }
    
    static IntermediateResponse readFrom(final int messageID, final ASN1StreamReaderSequence messageSequence, final ASN1StreamReader reader) throws LDAPException {
        try {
            String oid = null;
            ASN1OctetString value = null;
            final ASN1StreamReaderSequence opSequence = reader.beginSequence();
            while (opSequence.hasMoreElements()) {
                final byte type = (byte)reader.peek();
                switch (type) {
                    case Byte.MIN_VALUE: {
                        oid = reader.readString();
                        continue;
                    }
                    case -127: {
                        value = new ASN1OctetString(type, reader.readBytes());
                        continue;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_INTERMEDIATE_RESPONSE_INVALID_ELEMENT.get(StaticUtils.toHex(type)));
                    }
                }
            }
            Control[] controls;
            if (messageSequence.hasMoreElements()) {
                final ArrayList<Control> controlList = new ArrayList<Control>(1);
                final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
                while (controlSequence.hasMoreElements()) {
                    controlList.add(Control.readFrom(reader));
                }
                controls = new Control[controlList.size()];
                controlList.toArray(controls);
            }
            else {
                controls = IntermediateResponse.NO_CONTROLS;
            }
            return new IntermediateResponse(messageID, oid, value, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_INTERMEDIATE_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public int getMessageID() {
        return this.messageID;
    }
    
    public final String getOID() {
        return this.oid;
    }
    
    public final ASN1OctetString getValue() {
        return this.value;
    }
    
    public final Control[] getControls() {
        return this.controls;
    }
    
    public final Control getControl(final String oid) {
        for (final Control c : this.controls) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        return null;
    }
    
    public String getIntermediateResponseName() {
        return this.oid;
    }
    
    public String valueToString() {
        return null;
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IntermediateResponse(");
        boolean added = false;
        if (this.messageID >= 0) {
            buffer.append("messageID=");
            buffer.append(this.messageID);
            added = true;
        }
        if (this.oid != null) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("oid='");
            buffer.append(this.oid);
            buffer.append('\'');
            added = true;
        }
        if (this.controls.length > 0) {
            if (added) {
                buffer.append(", ");
            }
            buffer.append("controls={");
            for (int i = 0; i < this.controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    static {
        NO_CONTROLS = new Control[0];
    }
}
