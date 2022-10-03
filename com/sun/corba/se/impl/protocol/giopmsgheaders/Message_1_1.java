package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.CompletionStatus;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class Message_1_1 extends MessageBase
{
    static final int UPPER_THREE_BYTES_OF_INT_MASK = 255;
    private static ORBUtilSystemException wrapper;
    int magic;
    GIOPVersion GIOP_version;
    byte flags;
    byte message_type;
    int message_size;
    
    Message_1_1() {
        this.magic = 0;
        this.GIOP_version = null;
        this.flags = 0;
        this.message_type = 0;
        this.message_size = 0;
    }
    
    Message_1_1(final int magic, final GIOPVersion giop_version, final byte flags, final byte message_type, final int message_size) {
        this.magic = 0;
        this.GIOP_version = null;
        this.flags = 0;
        this.message_type = 0;
        this.message_size = 0;
        this.magic = magic;
        this.GIOP_version = giop_version;
        this.flags = flags;
        this.message_type = message_type;
        this.message_size = message_size;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return this.GIOP_version;
    }
    
    @Override
    public int getType() {
        return this.message_type;
    }
    
    @Override
    public int getSize() {
        return this.message_size;
    }
    
    @Override
    public boolean isLittleEndian() {
        return (this.flags & 0x1) == 0x1;
    }
    
    @Override
    public boolean moreFragmentsToFollow() {
        return (this.flags & 0x2) == 0x2;
    }
    
    public void setThreadPoolToUse(final int n) {
        this.flags |= (byte)(n << 2 & 0xFF);
    }
    
    @Override
    public void setSize(final ByteBuffer byteBuffer, final int message_size) {
        this.message_size = message_size;
        final int n = message_size - 12;
        if (!this.isLittleEndian()) {
            byteBuffer.put(8, (byte)(n >>> 24 & 0xFF));
            byteBuffer.put(9, (byte)(n >>> 16 & 0xFF));
            byteBuffer.put(10, (byte)(n >>> 8 & 0xFF));
            byteBuffer.put(11, (byte)(n >>> 0 & 0xFF));
        }
        else {
            byteBuffer.put(8, (byte)(n >>> 0 & 0xFF));
            byteBuffer.put(9, (byte)(n >>> 8 & 0xFF));
            byteBuffer.put(10, (byte)(n >>> 16 & 0xFF));
            byteBuffer.put(11, (byte)(n >>> 24 & 0xFF));
        }
    }
    
    @Override
    public FragmentMessage createFragmentMessage() {
        switch (this.message_type) {
            case 2:
            case 5:
            case 6: {
                throw Message_1_1.wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
            }
            case 3:
            case 4: {
                if (this.GIOP_version.equals(GIOPVersion.V1_1)) {
                    throw Message_1_1.wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
                }
                break;
            }
        }
        if (this.GIOP_version.equals(GIOPVersion.V1_1)) {
            return new FragmentMessage_1_1(this);
        }
        if (this.GIOP_version.equals(GIOPVersion.V1_2)) {
            return new FragmentMessage_1_2(this);
        }
        throw Message_1_1.wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public void read(final InputStream inputStream) {
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_long(this.magic);
        MessageBase.nullCheck(this.GIOP_version);
        this.GIOP_version.write(outputStream);
        outputStream.write_octet(this.flags);
        outputStream.write_octet(this.message_type);
        outputStream.write_ulong(this.message_size);
    }
    
    static {
        Message_1_1.wrapper = ORBUtilSystemException.get("rpc.protocol");
    }
}
