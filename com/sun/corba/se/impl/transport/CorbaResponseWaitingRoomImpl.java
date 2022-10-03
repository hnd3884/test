package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.util.Iterator;
import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.protocol.MessageMediator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;

public class CorbaResponseWaitingRoomImpl implements CorbaResponseWaitingRoom
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private CorbaConnection connection;
    private final Map<Integer, OutCallDesc> out_calls;
    
    public CorbaResponseWaitingRoomImpl(final ORB orb, final CorbaConnection connection) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
        this.connection = connection;
        this.out_calls = Collections.synchronizedMap(new HashMap<Integer, OutCallDesc>());
    }
    
    @Override
    public void registerWaiter(final MessageMediator messageMediator) {
        final CorbaMessageMediator messageMediator2 = (CorbaMessageMediator)messageMediator;
        if (this.orb.transportDebugFlag) {
            this.dprint(".registerWaiter: " + this.opAndId(messageMediator2));
        }
        final Integer requestIdInteger = messageMediator2.getRequestIdInteger();
        final OutCallDesc outCallDesc = new OutCallDesc();
        outCallDesc.thread = Thread.currentThread();
        outCallDesc.messageMediator = messageMediator2;
        this.out_calls.put(requestIdInteger, outCallDesc);
    }
    
    @Override
    public void unregisterWaiter(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        if (this.orb.transportDebugFlag) {
            this.dprint(".unregisterWaiter: " + this.opAndId(corbaMessageMediator));
        }
        this.out_calls.remove(corbaMessageMediator.getRequestIdInteger());
    }
    
    @Override
    public InputObject waitForResponse(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        try {
            InputObject inputObject = null;
            if (this.orb.transportDebugFlag) {
                this.dprint(".waitForResponse->: " + this.opAndId(corbaMessageMediator));
            }
            final Integer requestIdInteger = corbaMessageMediator.getRequestIdInteger();
            if (corbaMessageMediator.isOneWay()) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".waitForResponse: one way - not waiting: " + this.opAndId(corbaMessageMediator));
                }
                return null;
            }
            final OutCallDesc outCallDesc = this.out_calls.get(requestIdInteger);
            if (outCallDesc == null) {
                throw this.wrapper.nullOutCall(CompletionStatus.COMPLETED_MAYBE);
            }
            synchronized (outCallDesc.done) {
                while (outCallDesc.inputObject == null && outCallDesc.exception == null) {
                    try {
                        if (this.orb.transportDebugFlag) {
                            this.dprint(".waitForResponse: waiting: " + this.opAndId(corbaMessageMediator));
                        }
                        outCallDesc.done.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
                if (outCallDesc.exception != null) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".waitForResponse: exception: " + this.opAndId(corbaMessageMediator));
                    }
                    throw outCallDesc.exception;
                }
                inputObject = outCallDesc.inputObject;
            }
            if (inputObject != null) {
                ((CDRInputObject)inputObject).unmarshalHeader();
            }
            return inputObject;
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".waitForResponse<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    @Override
    public void responseReceived(final InputObject inputObject) {
        final CDRInputObject cdrInputObject = (CDRInputObject)inputObject;
        final LocateReplyOrReplyMessage replyHeader = (LocateReplyOrReplyMessage)cdrInputObject.getMessageHeader();
        final Integer n = new Integer(replyHeader.getRequestId());
        final OutCallDesc outCallDesc = this.out_calls.get(n);
        if (this.orb.transportDebugFlag) {
            this.dprint(".responseReceived: id/" + n + ": " + replyHeader);
        }
        if (outCallDesc == null) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".responseReceived: id/" + n + ": no waiter: " + replyHeader);
            }
            return;
        }
        synchronized (outCallDesc.done) {
            final CorbaMessageMediator messageMediator = (CorbaMessageMediator)outCallDesc.messageMediator;
            if (this.orb.transportDebugFlag) {
                this.dprint(".responseReceived: " + this.opAndId(messageMediator) + ": notifying waiters");
            }
            messageMediator.setReplyHeader(replyHeader);
            messageMediator.setInputObject(inputObject);
            cdrInputObject.setMessageMediator(messageMediator);
            outCallDesc.inputObject = inputObject;
            outCallDesc.done.notify();
        }
    }
    
    @Override
    public int numberRegistered() {
        return this.out_calls.size();
    }
    
    @Override
    public void signalExceptionToAllWaiters(final SystemException exception) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".signalExceptionToAllWaiters: " + exception);
        }
        synchronized (this.out_calls) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".signalExceptionToAllWaiters: out_calls size :" + this.out_calls.size());
            }
            for (final OutCallDesc outCallDesc : this.out_calls.values()) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".signalExceptionToAllWaiters: signaling " + outCallDesc);
                }
                synchronized (outCallDesc.done) {
                    try {
                        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)outCallDesc.messageMediator;
                        final CDRInputObject cdrInputObject = (CDRInputObject)corbaMessageMediator.getInputObject();
                        if (cdrInputObject == null) {
                            continue;
                        }
                        ((BufferManagerReadStream)cdrInputObject.getBufferManager()).cancelProcessing(corbaMessageMediator.getRequestId());
                    }
                    catch (final Exception ex) {}
                    finally {
                        outCallDesc.inputObject = null;
                        outCallDesc.exception = exception;
                        outCallDesc.done.notifyAll();
                    }
                }
            }
        }
    }
    
    @Override
    public MessageMediator getMessageMediator(final int n) {
        final OutCallDesc outCallDesc = this.out_calls.get(new Integer(n));
        if (outCallDesc == null) {
            return null;
        }
        return outCallDesc.messageMediator;
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaResponseWaitingRoomImpl", s);
    }
    
    protected String opAndId(final CorbaMessageMediator corbaMessageMediator) {
        return ORBUtility.operationNameAndRequestId(corbaMessageMediator);
    }
    
    static final class OutCallDesc
    {
        Object done;
        Thread thread;
        MessageMediator messageMediator;
        SystemException exception;
        InputObject inputObject;
        
        OutCallDesc() {
            this.done = new Object();
        }
    }
}
