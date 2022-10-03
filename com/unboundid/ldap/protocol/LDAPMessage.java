package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import com.unboundid.asn1.ASN1StreamReader;
import java.util.Iterator;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Element;
import java.util.Arrays;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;
import java.io.Serializable;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPMessage implements Serializable
{
    public static final byte PROTOCOL_OP_TYPE_BIND_REQUEST = 96;
    public static final byte PROTOCOL_OP_TYPE_BIND_RESPONSE = 97;
    public static final byte PROTOCOL_OP_TYPE_UNBIND_REQUEST = 66;
    public static final byte PROTOCOL_OP_TYPE_SEARCH_REQUEST = 99;
    public static final byte PROTOCOL_OP_TYPE_SEARCH_RESULT_ENTRY = 100;
    public static final byte PROTOCOL_OP_TYPE_SEARCH_RESULT_REFERENCE = 115;
    public static final byte PROTOCOL_OP_TYPE_SEARCH_RESULT_DONE = 101;
    public static final byte PROTOCOL_OP_TYPE_MODIFY_REQUEST = 102;
    public static final byte PROTOCOL_OP_TYPE_MODIFY_RESPONSE = 103;
    public static final byte PROTOCOL_OP_TYPE_ADD_REQUEST = 104;
    public static final byte PROTOCOL_OP_TYPE_ADD_RESPONSE = 105;
    public static final byte PROTOCOL_OP_TYPE_DELETE_REQUEST = 74;
    public static final byte PROTOCOL_OP_TYPE_DELETE_RESPONSE = 107;
    public static final byte PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST = 108;
    public static final byte PROTOCOL_OP_TYPE_MODIFY_DN_RESPONSE = 109;
    public static final byte PROTOCOL_OP_TYPE_COMPARE_REQUEST = 110;
    public static final byte PROTOCOL_OP_TYPE_COMPARE_RESPONSE = 111;
    public static final byte PROTOCOL_OP_TYPE_ABANDON_REQUEST = 80;
    public static final byte PROTOCOL_OP_TYPE_EXTENDED_REQUEST = 119;
    public static final byte PROTOCOL_OP_TYPE_EXTENDED_RESPONSE = 120;
    public static final byte PROTOCOL_OP_TYPE_INTERMEDIATE_RESPONSE = 121;
    public static final byte MESSAGE_TYPE_CONTROLS = -96;
    private static final long serialVersionUID = 909272448857832592L;
    private final int messageID;
    private final ProtocolOp protocolOp;
    private final List<Control> controls;
    
    public LDAPMessage(final int messageID, final ProtocolOp protocolOp, final Control... controls) {
        this.messageID = messageID;
        this.protocolOp = protocolOp;
        if (controls == null) {
            this.controls = Collections.emptyList();
        }
        else {
            this.controls = Collections.unmodifiableList((List<? extends Control>)Arrays.asList((T[])controls));
        }
    }
    
    public LDAPMessage(final int messageID, final ProtocolOp protocolOp, final List<Control> controls) {
        this.messageID = messageID;
        this.protocolOp = protocolOp;
        if (controls == null) {
            this.controls = Collections.emptyList();
        }
        else {
            this.controls = Collections.unmodifiableList((List<? extends Control>)controls);
        }
    }
    
    public int getMessageID() {
        return this.messageID;
    }
    
    public ProtocolOp getProtocolOp() {
        return this.protocolOp;
    }
    
    public byte getProtocolOpType() {
        return this.protocolOp.getProtocolOpType();
    }
    
    public AbandonRequestProtocolOp getAbandonRequestProtocolOp() throws ClassCastException {
        return (AbandonRequestProtocolOp)this.protocolOp;
    }
    
    public AddRequestProtocolOp getAddRequestProtocolOp() throws ClassCastException {
        return (AddRequestProtocolOp)this.protocolOp;
    }
    
    public AddResponseProtocolOp getAddResponseProtocolOp() throws ClassCastException {
        return (AddResponseProtocolOp)this.protocolOp;
    }
    
    public BindRequestProtocolOp getBindRequestProtocolOp() throws ClassCastException {
        return (BindRequestProtocolOp)this.protocolOp;
    }
    
    public BindResponseProtocolOp getBindResponseProtocolOp() throws ClassCastException {
        return (BindResponseProtocolOp)this.protocolOp;
    }
    
    public CompareRequestProtocolOp getCompareRequestProtocolOp() throws ClassCastException {
        return (CompareRequestProtocolOp)this.protocolOp;
    }
    
    public CompareResponseProtocolOp getCompareResponseProtocolOp() throws ClassCastException {
        return (CompareResponseProtocolOp)this.protocolOp;
    }
    
    public DeleteRequestProtocolOp getDeleteRequestProtocolOp() throws ClassCastException {
        return (DeleteRequestProtocolOp)this.protocolOp;
    }
    
    public DeleteResponseProtocolOp getDeleteResponseProtocolOp() throws ClassCastException {
        return (DeleteResponseProtocolOp)this.protocolOp;
    }
    
    public ExtendedRequestProtocolOp getExtendedRequestProtocolOp() throws ClassCastException {
        return (ExtendedRequestProtocolOp)this.protocolOp;
    }
    
    public ExtendedResponseProtocolOp getExtendedResponseProtocolOp() throws ClassCastException {
        return (ExtendedResponseProtocolOp)this.protocolOp;
    }
    
    public ModifyRequestProtocolOp getModifyRequestProtocolOp() throws ClassCastException {
        return (ModifyRequestProtocolOp)this.protocolOp;
    }
    
    public ModifyResponseProtocolOp getModifyResponseProtocolOp() throws ClassCastException {
        return (ModifyResponseProtocolOp)this.protocolOp;
    }
    
    public ModifyDNRequestProtocolOp getModifyDNRequestProtocolOp() throws ClassCastException {
        return (ModifyDNRequestProtocolOp)this.protocolOp;
    }
    
    public ModifyDNResponseProtocolOp getModifyDNResponseProtocolOp() throws ClassCastException {
        return (ModifyDNResponseProtocolOp)this.protocolOp;
    }
    
    public SearchRequestProtocolOp getSearchRequestProtocolOp() throws ClassCastException {
        return (SearchRequestProtocolOp)this.protocolOp;
    }
    
    public SearchResultEntryProtocolOp getSearchResultEntryProtocolOp() throws ClassCastException {
        return (SearchResultEntryProtocolOp)this.protocolOp;
    }
    
    public SearchResultReferenceProtocolOp getSearchResultReferenceProtocolOp() throws ClassCastException {
        return (SearchResultReferenceProtocolOp)this.protocolOp;
    }
    
    public SearchResultDoneProtocolOp getSearchResultDoneProtocolOp() throws ClassCastException {
        return (SearchResultDoneProtocolOp)this.protocolOp;
    }
    
    public UnbindRequestProtocolOp getUnbindRequestProtocolOp() throws ClassCastException {
        return (UnbindRequestProtocolOp)this.protocolOp;
    }
    
    public IntermediateResponseProtocolOp getIntermediateResponseProtocolOp() throws ClassCastException {
        return (IntermediateResponseProtocolOp)this.protocolOp;
    }
    
    public List<Control> getControls() {
        return this.controls;
    }
    
    public ASN1Element encode() {
        if (this.controls.isEmpty()) {
            return new ASN1Sequence(new ASN1Element[] { new ASN1Integer(this.messageID), this.protocolOp.encodeProtocolOp() });
        }
        final Control[] controlArray = new Control[this.controls.size()];
        this.controls.toArray(controlArray);
        return new ASN1Sequence(new ASN1Element[] { new ASN1Integer(this.messageID), this.protocolOp.encodeProtocolOp(), Control.encodeControls(controlArray) });
    }
    
    public static LDAPMessage decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            if (elements.length < 2 || elements.length > 3) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_DECODE_VALUE_SEQUENCE_INVALID_ELEMENT_COUNT.get(elements.length));
            }
            final int messageID = ASN1Integer.decodeAsInteger(elements[0]).intValue();
            ProtocolOp protocolOp = null;
            switch (elements[1].getType()) {
                case 80: {
                    protocolOp = AbandonRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 104: {
                    protocolOp = AddRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 105: {
                    protocolOp = AddResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 96: {
                    protocolOp = BindRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 97: {
                    protocolOp = BindResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 110: {
                    protocolOp = CompareRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 111: {
                    protocolOp = CompareResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 74: {
                    protocolOp = DeleteRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 107: {
                    protocolOp = DeleteResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 119: {
                    protocolOp = ExtendedRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 120: {
                    protocolOp = ExtendedResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 121: {
                    protocolOp = IntermediateResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 102: {
                    protocolOp = ModifyRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 103: {
                    protocolOp = ModifyResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 108: {
                    protocolOp = ModifyDNRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 109: {
                    protocolOp = ModifyDNResponseProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 99: {
                    protocolOp = SearchRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 101: {
                    protocolOp = SearchResultDoneProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 100: {
                    protocolOp = SearchResultEntryProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 115: {
                    protocolOp = SearchResultReferenceProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                case 66: {
                    protocolOp = UnbindRequestProtocolOp.decodeProtocolOp(elements[1]);
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_DECODE_INVALID_PROTOCOL_OP_TYPE.get(StaticUtils.toHex(elements[1].getType())));
                }
            }
            Control[] controls;
            if (elements.length == 3) {
                controls = Control.decodeControls(ASN1Sequence.decodeAsSequence(elements[2]));
            }
            else {
                controls = null;
            }
            return new LDAPMessage(messageID, protocolOp, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_DECODE_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence messageSequence = buffer.beginSequence();
        buffer.addInteger(this.messageID);
        this.protocolOp.writeTo(buffer);
        if (!this.controls.isEmpty()) {
            final ASN1BufferSequence controlsSequence = buffer.beginSequence((byte)(-96));
            for (final Control c : this.controls) {
                c.writeTo(buffer);
            }
            controlsSequence.end();
        }
        messageSequence.end();
    }
    
    public static LDAPMessage readFrom(final ASN1StreamReader reader, final boolean ignoreSocketTimeout) throws LDAPException {
        ASN1StreamReaderSequence messageSequence;
        try {
            reader.setIgnoreSocketTimeout(false, ignoreSocketTimeout);
            messageSequence = reader.beginSequence();
            if (messageSequence == null) {
                return null;
            }
        }
        catch (final IOException ioe) {
            if (!(ioe instanceof SocketTimeoutException) && !(ioe instanceof InterruptedIOException)) {
                Debug.debugException(ioe);
            }
            throw new LDAPException(ResultCode.SERVER_DOWN, ProtocolMessages.ERR_MESSAGE_IO_ERROR.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            reader.setIgnoreSocketTimeout(ignoreSocketTimeout, ignoreSocketTimeout);
            final int messageID = reader.readInteger();
            final byte protocolOpType = (byte)reader.peek();
            ProtocolOp protocolOp = null;
            switch (protocolOpType) {
                case 96: {
                    protocolOp = new BindRequestProtocolOp(reader);
                    break;
                }
                case 97: {
                    protocolOp = new BindResponseProtocolOp(reader);
                    break;
                }
                case 66: {
                    protocolOp = new UnbindRequestProtocolOp(reader);
                    break;
                }
                case 99: {
                    protocolOp = new SearchRequestProtocolOp(reader);
                    break;
                }
                case 100: {
                    protocolOp = new SearchResultEntryProtocolOp(reader);
                    break;
                }
                case 115: {
                    protocolOp = new SearchResultReferenceProtocolOp(reader);
                    break;
                }
                case 101: {
                    protocolOp = new SearchResultDoneProtocolOp(reader);
                    break;
                }
                case 102: {
                    protocolOp = new ModifyRequestProtocolOp(reader);
                    break;
                }
                case 103: {
                    protocolOp = new ModifyResponseProtocolOp(reader);
                    break;
                }
                case 104: {
                    protocolOp = new AddRequestProtocolOp(reader);
                    break;
                }
                case 105: {
                    protocolOp = new AddResponseProtocolOp(reader);
                    break;
                }
                case 74: {
                    protocolOp = new DeleteRequestProtocolOp(reader);
                    break;
                }
                case 107: {
                    protocolOp = new DeleteResponseProtocolOp(reader);
                    break;
                }
                case 108: {
                    protocolOp = new ModifyDNRequestProtocolOp(reader);
                    break;
                }
                case 109: {
                    protocolOp = new ModifyDNResponseProtocolOp(reader);
                    break;
                }
                case 110: {
                    protocolOp = new CompareRequestProtocolOp(reader);
                    break;
                }
                case 111: {
                    protocolOp = new CompareResponseProtocolOp(reader);
                    break;
                }
                case 80: {
                    protocolOp = new AbandonRequestProtocolOp(reader);
                    break;
                }
                case 119: {
                    protocolOp = new ExtendedRequestProtocolOp(reader);
                    break;
                }
                case 120: {
                    protocolOp = new ExtendedResponseProtocolOp(reader);
                    break;
                }
                case 121: {
                    protocolOp = new IntermediateResponseProtocolOp(reader);
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_INVALID_PROTOCOL_OP_TYPE.get(StaticUtils.toHex(protocolOpType)));
                }
            }
            final ArrayList<Control> controls = new ArrayList<Control>(5);
            if (messageSequence.hasMoreElements()) {
                final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
                while (controlSequence.hasMoreElements()) {
                    controls.add(Control.readFrom(reader));
                }
            }
            return new LDAPMessage(messageID, protocolOp, controls);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            if (ioe instanceof SocketTimeoutException || ioe instanceof InterruptedIOException) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(ioe)));
            }
            throw new LDAPException(ResultCode.SERVER_DOWN, ProtocolMessages.ERR_MESSAGE_IO_ERROR.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static LDAPResponse readLDAPResponseFrom(final ASN1StreamReader reader, final boolean ignoreSocketTimeout) throws LDAPException {
        return readLDAPResponseFrom(reader, ignoreSocketTimeout, null);
    }
    
    public static LDAPResponse readLDAPResponseFrom(final ASN1StreamReader reader, final boolean ignoreSocketTimeout, final Schema schema) throws LDAPException {
        ASN1StreamReaderSequence messageSequence;
        try {
            reader.setIgnoreSocketTimeout(false, ignoreSocketTimeout);
            messageSequence = reader.beginSequence();
            if (messageSequence == null) {
                return null;
            }
        }
        catch (final IOException ioe) {
            if (!(ioe instanceof SocketTimeoutException) && !(ioe instanceof InterruptedIOException)) {
                Debug.debugException(ioe);
            }
            throw new LDAPException(ResultCode.SERVER_DOWN, ProtocolMessages.ERR_MESSAGE_IO_ERROR.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            reader.setIgnoreSocketTimeout(ignoreSocketTimeout, ignoreSocketTimeout);
            final int messageID = reader.readInteger();
            final byte protocolOpType = (byte)reader.peek();
            switch (protocolOpType) {
                case 103:
                case 105:
                case 107:
                case 109: {
                    return InternalSDKHelper.readLDAPResultFrom(messageID, messageSequence, reader);
                }
                case 97: {
                    return InternalSDKHelper.readBindResultFrom(messageID, messageSequence, reader);
                }
                case 111: {
                    return InternalSDKHelper.readCompareResultFrom(messageID, messageSequence, reader);
                }
                case 120: {
                    return InternalSDKHelper.readExtendedResultFrom(messageID, messageSequence, reader);
                }
                case 100: {
                    return InternalSDKHelper.readSearchResultEntryFrom(messageID, messageSequence, reader, schema);
                }
                case 115: {
                    return InternalSDKHelper.readSearchResultReferenceFrom(messageID, messageSequence, reader);
                }
                case 101: {
                    return InternalSDKHelper.readSearchResultFrom(messageID, messageSequence, reader);
                }
                case 121: {
                    return InternalSDKHelper.readIntermediateResponseFrom(messageID, messageSequence, reader);
                }
                case 66:
                case 74:
                case 80:
                case 96:
                case 99:
                case 102:
                case 104:
                case 108:
                case 110:
                case 119: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_PROTOCOL_OP_TYPE_NOT_RESPONSE.get(StaticUtils.toHex(protocolOpType)));
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_INVALID_PROTOCOL_OP_TYPE.get(StaticUtils.toHex(protocolOpType)));
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            if (ioe instanceof SocketTimeoutException || ioe instanceof InterruptedIOException) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(ioe)));
            }
            throw new LDAPException(ResultCode.SERVER_DOWN, ProtocolMessages.ERR_MESSAGE_IO_ERROR.get(StaticUtils.getExceptionMessage(ioe)), ioe);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_MESSAGE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPMessage(msgID=");
        buffer.append(this.messageID);
        buffer.append(", protocolOp=");
        this.protocolOp.toString(buffer);
        if (!this.controls.isEmpty()) {
            buffer.append(", controls={");
            final Iterator<Control> iterator = this.controls.iterator();
            while (iterator.hasNext()) {
                iterator.next().toString(buffer);
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
