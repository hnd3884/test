package com.sun.corba.se.impl.protocol.giopmsgheaders;

import java.io.IOException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.omg.CORBA.Principal;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.orb.ORB;

public final class RequestMessage_1_0 extends Message_1_0 implements RequestMessage
{
    private ORB orb;
    private ServiceContexts service_contexts;
    private int request_id;
    private boolean response_expected;
    private byte[] object_key;
    private String operation;
    private Principal requesting_principal;
    private ObjectKey objectKey;
    
    RequestMessage_1_0(final ORB orb) {
        this.orb = null;
        this.service_contexts = null;
        this.request_id = 0;
        this.response_expected = false;
        this.object_key = null;
        this.operation = null;
        this.requesting_principal = null;
        this.objectKey = null;
        this.orb = orb;
    }
    
    RequestMessage_1_0(final ORB orb, final ServiceContexts service_contexts, final int request_id, final boolean response_expected, final byte[] object_key, final String operation, final Principal requesting_principal) {
        super(1195986768, false, (byte)0, 0);
        this.orb = null;
        this.service_contexts = null;
        this.request_id = 0;
        this.response_expected = false;
        this.object_key = null;
        this.operation = null;
        this.requesting_principal = null;
        this.objectKey = null;
        this.orb = orb;
        this.service_contexts = service_contexts;
        this.request_id = request_id;
        this.response_expected = response_expected;
        this.object_key = object_key;
        this.operation = operation;
        this.requesting_principal = requesting_principal;
    }
    
    @Override
    public ServiceContexts getServiceContexts() {
        return this.service_contexts;
    }
    
    @Override
    public int getRequestId() {
        return this.request_id;
    }
    
    @Override
    public boolean isResponseExpected() {
        return this.response_expected;
    }
    
    @Override
    public byte[] getReserved() {
        return null;
    }
    
    @Override
    public ObjectKey getObjectKey() {
        if (this.objectKey == null) {
            this.objectKey = MessageBase.extractObjectKey(this.object_key, this.orb);
        }
        return this.objectKey;
    }
    
    @Override
    public String getOperation() {
        return this.operation;
    }
    
    @Override
    public Principal getPrincipal() {
        return this.requesting_principal;
    }
    
    @Override
    public void setThreadPoolToUse(final int n) {
    }
    
    @Override
    public void read(final InputStream inputStream) {
        super.read(inputStream);
        this.service_contexts = new ServiceContexts((org.omg.CORBA_2_3.portable.InputStream)inputStream);
        this.request_id = inputStream.read_ulong();
        this.response_expected = inputStream.read_boolean();
        final int read_long = inputStream.read_long();
        inputStream.read_octet_array(this.object_key = new byte[read_long], 0, read_long);
        this.operation = inputStream.read_string();
        this.requesting_principal = inputStream.read_Principal();
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        if (this.service_contexts != null) {
            this.service_contexts.write((org.omg.CORBA_2_3.portable.OutputStream)outputStream, GIOPVersion.V1_0);
        }
        else {
            ServiceContexts.writeNullServiceContext((org.omg.CORBA_2_3.portable.OutputStream)outputStream);
        }
        outputStream.write_ulong(this.request_id);
        outputStream.write_boolean(this.response_expected);
        MessageBase.nullCheck(this.object_key);
        outputStream.write_long(this.object_key.length);
        outputStream.write_octet_array(this.object_key, 0, this.object_key.length);
        outputStream.write_string(this.operation);
        if (this.requesting_principal != null) {
            outputStream.write_Principal(this.requesting_principal);
        }
        else {
            outputStream.write_long(0);
        }
    }
    
    @Override
    public void callback(final MessageHandler messageHandler) throws IOException {
        messageHandler.handleInput(this);
    }
}
