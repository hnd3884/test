package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import sun.corba.SharedSecrets;
import org.omg.CORBA.SystemException;
import org.omg.IOP.TaggedProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.impl.protocol.AddressingDispositionException;
import com.sun.corba.se.spi.ior.ObjectKey;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.Principal;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.impl.encoding.CDRInputStream_1_0;
import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import java.io.IOException;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.nio.ByteBuffer;

public abstract class MessageBase implements Message
{
    public byte[] giopHeader;
    private ByteBuffer byteBuffer;
    private int threadPoolToUse;
    byte encodingVersion;
    private static ORBUtilSystemException wrapper;
    
    public MessageBase() {
        this.encodingVersion = 0;
    }
    
    public static String typeToString(final int n) {
        return typeToString((byte)n);
    }
    
    public static String typeToString(final byte b) {
        final String string = b + "/";
        String s = null;
        switch (b) {
            case 0: {
                s = string + "GIOPRequest";
                break;
            }
            case 1: {
                s = string + "GIOPReply";
                break;
            }
            case 2: {
                s = string + "GIOPCancelRequest";
                break;
            }
            case 3: {
                s = string + "GIOPLocateRequest";
                break;
            }
            case 4: {
                s = string + "GIOPLocateReply";
                break;
            }
            case 5: {
                s = string + "GIOPCloseConnection";
                break;
            }
            case 6: {
                s = string + "GIOPMessageError";
                break;
            }
            case 7: {
                s = string + "GIOPFragment";
                break;
            }
            default: {
                s = string + "Unknown";
                break;
            }
        }
        return s;
    }
    
    public static MessageBase readGIOPMessage(final ORB orb, final CorbaConnection corbaConnection) {
        return (MessageBase)readGIOPBody(orb, corbaConnection, readGIOPHeader(orb, corbaConnection));
    }
    
    public static MessageBase readGIOPHeader(final ORB orb, final CorbaConnection corbaConnection) {
        MessageBase messageBase = null;
        final ReadTimeouts transportTCPReadTimeouts = orb.getORBData().getTransportTCPReadTimeouts();
        ByteBuffer read;
        try {
            read = corbaConnection.read(12, 0, 12, transportTCPReadTimeouts.get_max_giop_header_time_to_wait());
        }
        catch (final IOException ex) {
            throw MessageBase.wrapper.ioexceptionWhenReadingConnection(ex);
        }
        if (orb.giopDebugFlag) {
            dprint(".readGIOPHeader: " + typeToString(read.get(7)));
            dprint(".readGIOPHeader: GIOP header is: ");
            final ByteBuffer readOnlyBuffer = read.asReadOnlyBuffer();
            readOnlyBuffer.position(0).limit(12);
            final ByteBufferWithInfo byteBufferWithInfo = new ByteBufferWithInfo(orb, readOnlyBuffer);
            byteBufferWithInfo.buflen = 12;
            CDRInputStream_1_0.printBuffer(byteBufferWithInfo);
        }
        final int n = (read.get(0) << 24 & 0xFF000000) | (read.get(1) << 16 & 0xFF0000) | (read.get(2) << 8 & 0xFF00) | (read.get(3) << 0 & 0xFF);
        if (n != 1195986768) {
            throw MessageBase.wrapper.giopMagicError(CompletionStatus.COMPLETED_MAYBE);
        }
        byte value = 0;
        if (read.get(4) == 13 && read.get(5) <= 1 && read.get(5) > 0 && orb.getORBData().isJavaSerializationEnabled()) {
            value = read.get(5);
            read.put(4, (byte)1);
            read.put(5, (byte)2);
        }
        final GIOPVersion giopVersion = orb.getORBData().getGIOPVersion();
        if (orb.giopDebugFlag) {
            dprint(".readGIOPHeader: Message GIOP version: " + read.get(4) + '.' + read.get(5));
            dprint(".readGIOPHeader: ORB Max GIOP Version: " + giopVersion);
        }
        if ((read.get(4) > giopVersion.getMajor() || (read.get(4) == giopVersion.getMajor() && read.get(5) > giopVersion.getMinor())) && read.get(7) != 6) {
            throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
        }
        AreFragmentsAllowed(read.get(4), read.get(5), read.get(6), read.get(7));
        switch (read.get(7)) {
            case 0: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating RequestMessage");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    messageBase = new RequestMessage_1_0(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new RequestMessage_1_1(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new RequestMessage_1_2(orb);
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            case 3: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating LocateRequestMessage");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    messageBase = new LocateRequestMessage_1_0(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new LocateRequestMessage_1_1(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new LocateRequestMessage_1_2(orb);
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            case 2: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating CancelRequestMessage");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    messageBase = new CancelRequestMessage_1_0();
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new CancelRequestMessage_1_1();
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new CancelRequestMessage_1_2();
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            case 1: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating ReplyMessage");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    messageBase = new ReplyMessage_1_0(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new ReplyMessage_1_1(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new ReplyMessage_1_2(orb);
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            case 4: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating LocateReplyMessage");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    messageBase = new LocateReplyMessage_1_0(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new LocateReplyMessage_1_1(orb);
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new LocateReplyMessage_1_2(orb);
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            case 5:
            case 6: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating Message for CloseConnection or MessageError");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    messageBase = new Message_1_0();
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new Message_1_1();
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new Message_1_1();
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            case 7: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: creating FragmentMessage");
                }
                if (read.get(4) == 1 && read.get(5) == 0) {
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 1) {
                    messageBase = new FragmentMessage_1_1();
                    break;
                }
                if (read.get(4) == 1 && read.get(5) == 2) {
                    messageBase = new FragmentMessage_1_2();
                    break;
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            default: {
                if (orb.giopDebugFlag) {
                    dprint(".readGIOPHeader: UNKNOWN MESSAGE TYPE: " + read.get(7));
                }
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
        }
        if (read.get(4) == 1 && read.get(5) == 0) {
            final Message_1_0 message_1_0 = (Message_1_0)messageBase;
            message_1_0.magic = n;
            message_1_0.GIOP_version = new GIOPVersion(read.get(4), read.get(5));
            message_1_0.byte_order = (read.get(6) == 1);
            messageBase.threadPoolToUse = 0;
            message_1_0.message_type = read.get(7);
            message_1_0.message_size = readSize(read.get(8), read.get(9), read.get(10), read.get(11), message_1_0.isLittleEndian()) + 12;
        }
        else {
            final Message_1_1 message_1_2 = (Message_1_1)messageBase;
            message_1_2.magic = n;
            message_1_2.GIOP_version = new GIOPVersion(read.get(4), read.get(5));
            message_1_2.flags = (byte)(read.get(6) & 0x3);
            messageBase.threadPoolToUse = (read.get(6) >>> 2 & 0x3F);
            message_1_2.message_type = read.get(7);
            message_1_2.message_size = readSize(read.get(8), read.get(9), read.get(10), read.get(11), message_1_2.isLittleEndian()) + 12;
        }
        if (orb.giopDebugFlag) {
            dprint(".readGIOPHeader: header construction complete.");
            final ByteBuffer readOnlyBuffer2 = read.asReadOnlyBuffer();
            final byte[] giopHeader = new byte[12];
            readOnlyBuffer2.position(0).limit(12);
            readOnlyBuffer2.get(giopHeader, 0, giopHeader.length);
            messageBase.giopHeader = giopHeader;
        }
        messageBase.setByteBuffer(read);
        messageBase.setEncodingVersion(value);
        return messageBase;
    }
    
    public static Message readGIOPBody(final ORB orb, final CorbaConnection corbaConnection, final Message message) {
        final ReadTimeouts transportTCPReadTimeouts = orb.getORBData().getTransportTCPReadTimeouts();
        final ByteBuffer byteBuffer = message.getByteBuffer();
        byteBuffer.position(12);
        final int n = message.getSize() - 12;
        ByteBuffer read;
        try {
            read = corbaConnection.read(byteBuffer, 12, n, transportTCPReadTimeouts.get_max_time_to_wait());
        }
        catch (final IOException ex) {
            throw MessageBase.wrapper.ioexceptionWhenReadingConnection(ex);
        }
        message.setByteBuffer(read);
        if (orb.giopDebugFlag) {
            dprint(".readGIOPBody: received message:");
            final ByteBuffer readOnlyBuffer = read.asReadOnlyBuffer();
            readOnlyBuffer.position(0).limit(message.getSize());
            CDRInputStream_1_0.printBuffer(new ByteBufferWithInfo(orb, readOnlyBuffer));
        }
        return message;
    }
    
    private static RequestMessage createRequest(final ORB orb, final GIOPVersion giopVersion, final byte encodingVersion, final int n, final boolean b, final byte[] array, final String s, final ServiceContexts serviceContexts, final Principal principal) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new RequestMessage_1_0(orb, serviceContexts, n, b, array, s, principal);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new RequestMessage_1_1(orb, serviceContexts, n, b, new byte[] { 0, 0, 0 }, array, s, principal);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            byte b2;
            if (b) {
                b2 = 3;
            }
            else {
                b2 = 0;
            }
            final TargetAddress targetAddress = new TargetAddress();
            targetAddress.object_key(array);
            final RequestMessage_1_2 requestMessage_1_2 = new RequestMessage_1_2(orb, n, b2, new byte[] { 0, 0, 0 }, targetAddress, s, serviceContexts);
            requestMessage_1_2.setEncodingVersion(encodingVersion);
            return requestMessage_1_2;
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static RequestMessage createRequest(final ORB orb, final GIOPVersion giopVersion, final byte encodingVersion, final int n, final boolean b, final IOR ior, final short n2, final String s, final ServiceContexts serviceContexts, final Principal principal) {
        IIOPProfile iiopProfile = ior.getProfile();
        RequestMessage request;
        if (n2 == 0) {
            iiopProfile = ior.getProfile();
            request = createRequest(orb, giopVersion, encodingVersion, n, b, iiopProfile.getObjectKey().getBytes(orb), s, serviceContexts, principal);
        }
        else {
            if (!giopVersion.equals(GIOPVersion.V1_2)) {
                throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
            }
            byte b2;
            if (b) {
                b2 = 3;
            }
            else {
                b2 = 0;
            }
            final TargetAddress targetAddress = new TargetAddress();
            if (n2 == 1) {
                iiopProfile = ior.getProfile();
                targetAddress.profile(iiopProfile.getIOPProfile());
            }
            else {
                if (n2 != 2) {
                    throw MessageBase.wrapper.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO);
                }
                targetAddress.ior(new IORAddressingInfo(0, ior.getIOPIOR()));
            }
            request = new RequestMessage_1_2(orb, n, b2, new byte[] { 0, 0, 0 }, targetAddress, s, serviceContexts);
            request.setEncodingVersion(encodingVersion);
        }
        if (giopVersion.supportsIORIIOPProfileComponents()) {
            int requestPartitioningId = 0;
            final Iterator iteratorById = iiopProfile.getTaggedProfileTemplate().iteratorById(1398099457);
            if (iteratorById.hasNext()) {
                requestPartitioningId = ((RequestPartitioningComponent)iteratorById.next()).getRequestPartitioningId();
            }
            if (requestPartitioningId < 0 || requestPartitioningId > 63) {
                throw MessageBase.wrapper.invalidRequestPartitioningId(new Integer(requestPartitioningId), new Integer(0), new Integer(63));
            }
            request.setThreadPoolToUse(requestPartitioningId);
        }
        return request;
    }
    
    public static ReplyMessage createReply(final ORB orb, final GIOPVersion giopVersion, final byte encodingVersion, final int n, final int n2, final ServiceContexts serviceContexts, final IOR ior) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new ReplyMessage_1_0(orb, serviceContexts, n, n2, ior);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new ReplyMessage_1_1(orb, serviceContexts, n, n2, ior);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            final ReplyMessage_1_2 replyMessage_1_2 = new ReplyMessage_1_2(orb, n, n2, serviceContexts, ior);
            replyMessage_1_2.setEncodingVersion(encodingVersion);
            return replyMessage_1_2;
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static LocateRequestMessage createLocateRequest(final ORB orb, final GIOPVersion giopVersion, final byte encodingVersion, final int n, final byte[] array) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new LocateRequestMessage_1_0(orb, n, array);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new LocateRequestMessage_1_1(orb, n, array);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            final TargetAddress targetAddress = new TargetAddress();
            targetAddress.object_key(array);
            final LocateRequestMessage_1_2 locateRequestMessage_1_2 = new LocateRequestMessage_1_2(orb, n, targetAddress);
            locateRequestMessage_1_2.setEncodingVersion(encodingVersion);
            return locateRequestMessage_1_2;
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static LocateReplyMessage createLocateReply(final ORB orb, final GIOPVersion giopVersion, final byte encodingVersion, final int n, final int n2, final IOR ior) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new LocateReplyMessage_1_0(orb, n, n2, ior);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new LocateReplyMessage_1_1(orb, n, n2, ior);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            final LocateReplyMessage_1_2 locateReplyMessage_1_2 = new LocateReplyMessage_1_2(orb, n, n2, ior);
            locateReplyMessage_1_2.setEncodingVersion(encodingVersion);
            return locateReplyMessage_1_2;
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static CancelRequestMessage createCancelRequest(final GIOPVersion giopVersion, final int n) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new CancelRequestMessage_1_0(n);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new CancelRequestMessage_1_1(n);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            return new CancelRequestMessage_1_2(n);
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static Message createCloseConnection(final GIOPVersion giopVersion) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new Message_1_0(1195986768, false, (byte)5, 0);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)5, 0);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)5, 0);
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static Message createMessageError(final GIOPVersion giopVersion) {
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return new Message_1_0(1195986768, false, (byte)6, 0);
        }
        if (giopVersion.equals(GIOPVersion.V1_1)) {
            return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)6, 0);
        }
        if (giopVersion.equals(GIOPVersion.V1_2)) {
            return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)6, 0);
        }
        throw MessageBase.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public static FragmentMessage createFragmentMessage(final GIOPVersion giopVersion) {
        return null;
    }
    
    public static int getRequestId(final Message message) {
        switch (message.getType()) {
            case 0: {
                return ((RequestMessage)message).getRequestId();
            }
            case 1: {
                return ((ReplyMessage)message).getRequestId();
            }
            case 3: {
                return ((LocateRequestMessage)message).getRequestId();
            }
            case 4: {
                return ((LocateReplyMessage)message).getRequestId();
            }
            case 2: {
                return ((CancelRequestMessage)message).getRequestId();
            }
            case 7: {
                return ((FragmentMessage)message).getRequestId();
            }
            default: {
                throw MessageBase.wrapper.illegalGiopMsgType(CompletionStatus.COMPLETED_MAYBE);
            }
        }
    }
    
    public static void setFlag(final ByteBuffer byteBuffer, final int n) {
        byteBuffer.put(6, (byte)(byteBuffer.get(6) | n));
    }
    
    public static void clearFlag(final byte[] array, final int n) {
        final int n2 = 6;
        array[n2] &= (byte)(0xFF ^ n);
    }
    
    private static void AreFragmentsAllowed(final byte b, final byte b2, final byte b3, final byte b4) {
        if (b == 1 && b2 == 0 && b4 == 7) {
            throw MessageBase.wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
        }
        if ((b3 & 0x2) == 0x2) {
            switch (b4) {
                case 2:
                case 5:
                case 6: {
                    throw MessageBase.wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
                }
                case 3:
                case 4: {
                    if (b == 1 && b2 == 1) {
                        throw MessageBase.wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
                    }
                    break;
                }
            }
        }
    }
    
    static ObjectKey extractObjectKey(final byte[] array, final ORB orb) {
        try {
            if (array != null) {
                final ObjectKey create = orb.getObjectKeyFactory().create(array);
                if (create != null) {
                    return create;
                }
            }
        }
        catch (final Exception ex) {}
        throw MessageBase.wrapper.invalidObjectKey();
    }
    
    static ObjectKey extractObjectKey(final TargetAddress targetAddress, final ORB orb) {
        final short giopTargetAddressPreference = orb.getORBData().getGIOPTargetAddressPreference();
        final short discriminator = targetAddress.discriminator();
        switch (giopTargetAddressPreference) {
            case 0: {
                if (discriminator != 0) {
                    throw new AddressingDispositionException((short)0);
                }
                break;
            }
            case 1: {
                if (discriminator != 1) {
                    throw new AddressingDispositionException((short)1);
                }
                break;
            }
            case 2: {
                if (discriminator != 2) {
                    throw new AddressingDispositionException((short)2);
                }
                break;
            }
            case 3: {
                break;
            }
            default: {
                throw MessageBase.wrapper.orbTargetAddrPreferenceInExtractObjectkeyInvalid();
            }
        }
        try {
            switch (discriminator) {
                case 0: {
                    final byte[] object_key = targetAddress.object_key();
                    if (object_key == null) {
                        break;
                    }
                    final ObjectKey create = orb.getObjectKeyFactory().create(object_key);
                    if (create != null) {
                        return create;
                    }
                    break;
                }
                case 1: {
                    final TaggedProfile profile = targetAddress.profile();
                    if (profile == null) {
                        break;
                    }
                    final ObjectKey objectKey = IIOPFactories.makeIIOPProfile(orb, profile).getObjectKey();
                    if (objectKey != null) {
                        return objectKey;
                    }
                    break;
                }
                case 2: {
                    final IORAddressingInfo ior = targetAddress.ior();
                    if (ior == null) {
                        break;
                    }
                    final ObjectKey objectKey2 = IIOPFactories.makeIIOPProfile(orb, ior.ior.profiles[ior.selected_profile_index]).getObjectKey();
                    if (objectKey2 != null) {
                        return objectKey2;
                    }
                    break;
                }
            }
        }
        catch (final Exception ex) {}
        throw MessageBase.wrapper.invalidObjectKey();
    }
    
    private static int readSize(final byte b, final byte b2, final byte b3, final byte b4, final boolean b5) {
        int n;
        int n2;
        int n3;
        int n4;
        if (!b5) {
            n = (b << 24 & 0xFF000000);
            n2 = (b2 << 16 & 0xFF0000);
            n3 = (b3 << 8 & 0xFF00);
            n4 = (b4 << 0 & 0xFF);
        }
        else {
            n = (b4 << 24 & 0xFF000000);
            n2 = (b3 << 16 & 0xFF0000);
            n3 = (b2 << 8 & 0xFF00);
            n4 = (b << 0 & 0xFF);
        }
        return n | n2 | n3 | n4;
    }
    
    static void nullCheck(final Object o) {
        if (o == null) {
            throw MessageBase.wrapper.nullNotAllowed();
        }
    }
    
    static SystemException getSystemException(final String s, final int minor, final CompletionStatus completed, final String s2, final ORBUtilSystemException ex) {
        SystemException ex2;
        try {
            final Class<?> loadClass = SharedSecrets.getJavaCorbaAccess().loadClass(s);
            if (s2 == null) {
                ex2 = (SystemException)loadClass.newInstance();
            }
            else {
                ex2 = (SystemException)loadClass.getConstructor(String.class).newInstance(s2);
            }
        }
        catch (final Exception ex3) {
            throw ex.badSystemExceptionInReply(CompletionStatus.COMPLETED_MAYBE, ex3);
        }
        ex2.minor = minor;
        ex2.completed = completed;
        return ex2;
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
    
    @Override
    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }
    
    @Override
    public void setByteBuffer(final ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }
    
    @Override
    public int getThreadPoolToUse() {
        return this.threadPoolToUse;
    }
    
    @Override
    public byte getEncodingVersion() {
        return this.encodingVersion;
    }
    
    @Override
    public void setEncodingVersion(final byte encodingVersion) {
        this.encodingVersion = encodingVersion;
    }
    
    private static void dprint(final String s) {
        ORBUtility.dprint("MessageBase", s);
    }
    
    static {
        MessageBase.wrapper = ORBUtilSystemException.get("rpc.protocol");
    }
}
