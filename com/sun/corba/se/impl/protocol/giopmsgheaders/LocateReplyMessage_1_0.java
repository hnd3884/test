package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public final class LocateReplyMessage_1_0 extends Message_1_0 implements LocateReplyMessage
{
    private ORB orb;
    private int request_id;
    private int locate_status;
    private IOR ior;
    
    LocateReplyMessage_1_0(final ORB orb) {
        this.orb = null;
        this.request_id = 0;
        this.locate_status = 0;
        this.ior = null;
        this.orb = orb;
    }
    
    LocateReplyMessage_1_0(final ORB orb, final int request_id, final int locate_status, final IOR ior) {
        super(1195986768, false, (byte)4, 0);
        this.orb = null;
        this.request_id = 0;
        this.locate_status = 0;
        this.ior = null;
        this.orb = orb;
        this.request_id = request_id;
        this.locate_status = locate_status;
        this.ior = ior;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public int getReplyStatus() {
        return this.locate_status;
    }
    
    @Override
    public short getAddrDisposition() {
        return 0;
    }
    
    @Override
    public SystemException getSystemException(final String s) {
        return null;
    }
    
    @Override
    public IOR getIOR() {
        return this.ior;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.request_id = inputStream.read_ulong();
        isValidReplyStatus(this.locate_status = inputStream.read_long());
        if (this.locate_status == 2) {
            this.ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)inputStream);
        }
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        outputStream.write_ulong(this.request_id);
        outputStream.write_long(this.locate_status);
    }
    
    public static void isValidReplyStatus(final int n) {
        switch (n) {
            case 0:
            case 1:
            case 2: {
                return;
            }
            default: {
                throw ORBUtilSystemException.get("rpc.protocol").illegalReplyStatus(CompletionStatus.COMPLETED_MAYBE);
            }
        }
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
