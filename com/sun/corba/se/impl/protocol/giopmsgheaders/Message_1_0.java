package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.CompletionStatus;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class Message_1_0 extends MessageBase
{
    private static ORBUtilSystemException wrapper;
    int magic;
    GIOPVersion GIOP_version;
    boolean byte_order;
    byte message_type;
    int message_size;
    
    Message_1_0() {
        this.magic = 0;
        this.GIOP_version = null;
        this.byte_order = false;
        this.message_type = 0;
        this.message_size = 0;
    }
    
    Message_1_0(final int magic, final boolean byte_order, final byte message_type, final int message_size) {
        this.magic = 0;
        this.GIOP_version = null;
        this.byte_order = false;
        this.message_type = 0;
        this.message_size = 0;
        this.magic = magic;
        this.GIOP_version = GIOPVersion.V1_0;
        this.byte_order = byte_order;
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
        return this.byte_order;
    }
    
    @Override
    public boolean moreFragmentsToFollow() {
        return false;
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
        throw Message_1_0.wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public void read(final InputStream inputStream) {
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_long(this.magic);
        MessageBase.nullCheck(this.GIOP_version);
        this.GIOP_version.write(outputStream);
        outputStream.write_boolean(this.byte_order);
        outputStream.write_octet(this.message_type);
        outputStream.write_ulong(this.message_size);
    }
    
    static {
        Message_1_0.wrapper = ORBUtilSystemException.get("rpc.protocol");
    }
}
