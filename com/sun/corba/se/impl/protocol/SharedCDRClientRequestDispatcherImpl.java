package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import java.io.IOException;
import java.security.AccessController;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;

public class SharedCDRClientRequestDispatcherImpl extends CorbaClientRequestDispatcherImpl
{
    @Override
    public InputObject marshalingComplete(final Object o, final OutputObject outputObject) throws ApplicationException, RemarshalException {
        ORB orb = null;
        MessageMediator messageMediator = null;
        try {
            messageMediator = outputObject.getMessageMediator();
            orb = (ORB)messageMediator.getBroker();
            if (orb.subcontractDebugFlag) {
                this.dprint(".marshalingComplete->: " + this.opAndId((CorbaMessageMediator)messageMediator));
            }
            final CDROutputObject cdrOutputObject = (CDROutputObject)outputObject;
            final ByteBufferWithInfo byteBufferWithInfo = cdrOutputObject.getByteBufferWithInfo();
            cdrOutputObject.getMessageHeader().setSize(byteBufferWithInfo.byteBuffer, byteBufferWithInfo.getSize());
            final CDRInputObject inputObject = AccessController.doPrivileged((PrivilegedAction<CDRInputObject>)new PrivilegedAction<CDRInputObject>() {
                final /* synthetic */ ByteBuffer val$inBuffer = byteBufferWithInfo.byteBuffer;
                final /* synthetic */ Message val$inMsg = cdrOutputObject.getMessageHeader();
                
                @Override
                public CDRInputObject run() {
                    return new CDRInputObject(orb, null, this.val$inBuffer, this.val$inMsg);
                }
            });
            messageMediator.setInputObject(inputObject);
            inputObject.setMessageMediator(messageMediator);
            ((CorbaMessageMediatorImpl)messageMediator).handleRequestRequest((CorbaMessageMediator)messageMediator);
            try {
                inputObject.close();
            }
            catch (final IOException ex) {
                if (orb.transportDebugFlag) {
                    this.dprint(".marshalingComplete: ignoring IOException - " + ex.toString());
                }
            }
            final CDROutputObject cdrOutputObject2 = (CDROutputObject)messageMediator.getOutputObject();
            final ByteBufferWithInfo byteBufferWithInfo2 = cdrOutputObject2.getByteBufferWithInfo();
            cdrOutputObject2.getMessageHeader().setSize(byteBufferWithInfo2.byteBuffer, byteBufferWithInfo2.getSize());
            final CDRInputObject inputObject2 = AccessController.doPrivileged((PrivilegedAction<CDRInputObject>)new PrivilegedAction<CDRInputObject>() {
                final /* synthetic */ ByteBuffer val$inBuffer2 = byteBufferWithInfo2.byteBuffer;
                final /* synthetic */ Message val$inMsg2 = cdrOutputObject2.getMessageHeader();
                
                @Override
                public CDRInputObject run() {
                    return new CDRInputObject(orb, null, this.val$inBuffer2, this.val$inMsg2);
                }
            });
            messageMediator.setInputObject(inputObject2);
            inputObject2.setMessageMediator(messageMediator);
            inputObject2.unmarshalHeader();
            return this.processResponse(orb, (CorbaMessageMediator)messageMediator, inputObject2);
        }
        finally {
            if (orb.subcontractDebugFlag) {
                this.dprint(".marshalingComplete<-: " + this.opAndId((CorbaMessageMediator)messageMediator));
            }
        }
    }
    
    @Override
    protected void dprint(final String s) {
        ORBUtility.dprint("SharedCDRClientRequestDispatcherImpl", s);
    }
}
