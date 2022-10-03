package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.Principal;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public final class RequestMessage_1_2 extends Message_1_2 implements RequestMessage
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private byte response_flags;
    private byte[] reserved;
    private TargetAddress target;
    private String operation;
    private ServiceContexts service_contexts;
    private ObjectKey objectKey;
    
    RequestMessage_1_2(final ORB orb) {
        this.orb = null;
        this.wrapper = null;
        this.response_flags = 0;
        this.reserved = null;
        this.target = null;
        this.operation = null;
        this.service_contexts = null;
        this.objectKey = null;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
    }
    
    RequestMessage_1_2(final ORB orb, final int request_id, final byte response_flags, final byte[] reserved, final TargetAddress target, final String operation, final ServiceContexts service_contexts) {
        super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)0, 0);
        this.orb = null;
        this.wrapper = null;
        this.response_flags = 0;
        this.reserved = null;
        this.target = null;
        this.operation = null;
        this.service_contexts = null;
        this.objectKey = null;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.request_id = request_id;
        this.response_flags = response_flags;
        this.reserved = reserved;
        this.target = target;
        this.operation = operation;
        this.service_contexts = service_contexts;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public boolean isResponseExpected() {
        return (this.response_flags & 0x1) == 0x1;
    }
    
    @Override
    public byte[] getReserved() {
        return this.reserved;
    }
    
    @Override
    public ObjectKey getObjectKey() {
        if (this.objectKey == null) {
            this.objectKey = MessageBase.extractObjectKey(this.target, this.orb);
        }
        return this.objectKey;
    }
    
    @Override
    public String getOperation() {
        return this.operation;
    }
    
    @Override
    public Principal getPrincipal() {
        return null;
    }
    
    @Override
    public ServiceContexts getServiceContexts() {
        return this.service_contexts;
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.request_id = inputStream.read_ulong();
        this.response_flags = inputStream.read_octet();
        this.reserved = new byte[3];
        for (int i = 0; i < 3; ++i) {
            this.reserved[i] = inputStream.read_octet();
        }
        this.target = TargetAddressHelper.read(inputStream);
        this.getObjectKey();
        this.operation = inputStream.read_string();
        this.service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)inputStream);
        ((CDRInputStream)inputStream).setHeaderPadding(true);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        outputStream.write_ulong(this.request_id);
        outputStream.write_octet(this.response_flags);
        MessageBase.nullCheck(this.reserved);
        if (this.reserved.length != 3) {
            throw this.wrapper.badReservedLength(CompletionStatus.COMPLETED_MAYBE);
        }
        for (int i = 0; i < 3; ++i) {
            outputStream.write_octet(this.reserved[i]);
        }
        MessageBase.nullCheck(this.target);
        TargetAddressHelper.write(outputStream, this.target);
        outputStream.write_string(this.operation);
        if (this.service_contexts != null) {
            this.service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)outputStream, GIOPVersion.V1_2);
        }
        else {
            ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)outputStream);
        }
        ((CDROutputStream)outputStream).setHeaderPadding(true);
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
