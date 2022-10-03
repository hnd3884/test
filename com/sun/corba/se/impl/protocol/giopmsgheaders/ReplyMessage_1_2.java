package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public final class ReplyMessage_1_2 extends Message_1_2 implements ReplyMessage
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private int reply_status;
    private ServiceContexts service_contexts;
    private IOR ior;
    private String exClassName;
    private int minorCode;
    private CompletionStatus completionStatus;
    private short addrDisposition;
    
    ReplyMessage_1_2(final ORB orb) {
        this.orb = null;
        this.wrapper = null;
        this.reply_status = 0;
        this.service_contexts = null;
        this.ior = null;
        this.exClassName = null;
        this.minorCode = 0;
        this.completionStatus = null;
        this.addrDisposition = 0;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
    }
    
    ReplyMessage_1_2(final ORB orb, final int request_id, final int reply_status, final ServiceContexts service_contexts, final IOR ior) {
        super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)1, 0);
        this.orb = null;
        this.wrapper = null;
        this.reply_status = 0;
        this.service_contexts = null;
        this.ior = null;
        this.exClassName = null;
        this.minorCode = 0;
        this.completionStatus = null;
        this.addrDisposition = 0;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.request_id = request_id;
        this.reply_status = reply_status;
        this.service_contexts = service_contexts;
        this.ior = ior;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public int getReplyStatus() {
        return this.reply_status;
    }
    
    @Override
    public short getAddrDisposition() {
        return this.addrDisposition;
    }
    
    @Override
    public ServiceContexts getServiceContexts() {
        return this.service_contexts;
    }
    
    @Override
    public void setServiceContexts(final ServiceContexts service_contexts) {
        this.service_contexts = service_contexts;
    }
    
    @Override
    public SystemException getSystemException(final String s) {
        return MessageBase.getSystemException(this.exClassName, this.minorCode, this.completionStatus, s, this.wrapper);
    }
    
    @Override
    public IOR getIOR() {
        return this.ior;
    }
    
    @Override
    public void setIOR(final IOR ior) {
        this.ior = ior;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.request_id = inputStream.read_ulong();
        isValidReplyStatus(this.reply_status = inputStream.read_long());
        this.service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)inputStream);
        ((CDRInputStream)inputStream).setHeaderPadding(true);
        if (this.reply_status == 2) {
            this.exClassName = ORBUtility.classNameOf(inputStream.read_string());
            this.minorCode = inputStream.read_long();
            final int read_long = inputStream.read_long();
            switch (read_long) {
                case 0: {
                    this.completionStatus = CompletionStatus.COMPLETED_YES;
                    break;
                }
                case 1: {
                    this.completionStatus = CompletionStatus.COMPLETED_NO;
                    break;
                }
                case 2: {
                    this.completionStatus = CompletionStatus.COMPLETED_MAYBE;
                    break;
                }
                default: {
                    throw this.wrapper.badCompletionStatusInReply(CompletionStatus.COMPLETED_MAYBE, new Integer(read_long));
                }
            }
        }
        else if (this.reply_status != 1) {
            if (this.reply_status == 3 || this.reply_status == 4) {
                this.ior = IORFactories.makeIOR((org.omg.CORBA_2_3.portable.InputStream)inputStream);
            }
            else if (this.reply_status == 5) {
                this.addrDisposition = AddressingDispositionHelper.read(inputStream);
            }
        }
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        outputStream.write_ulong(this.request_id);
        outputStream.write_long(this.reply_status);
        if (this.service_contexts != null) {
            this.service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)outputStream, GIOPVersion.V1_2);
        }
        else {
            ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)outputStream);
        }
        ((CDROutputStream)outputStream).setHeaderPadding(true);
    }
    
    public static void isValidReplyStatus(final int n) {
        switch (n) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
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
