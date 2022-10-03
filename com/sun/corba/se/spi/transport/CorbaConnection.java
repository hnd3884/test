package com.sun.corba.se.spi.transport;

import java.nio.channels.SocketChannel;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.sun.corba.se.pept.transport.Connection;

public interface CorbaConnection extends Connection, com.sun.corba.se.spi.legacy.connection.Connection
{
    public static final int OPENING = 1;
    public static final int ESTABLISHED = 2;
    public static final int CLOSE_SENT = 3;
    public static final int CLOSE_RECVD = 4;
    public static final int ABORT = 5;
    
    boolean shouldUseDirectByteBuffers();
    
    boolean shouldReadGiopHeaderOnly();
    
    ByteBuffer read(final int p0, final int p1, final int p2, final long p3) throws IOException;
    
    ByteBuffer read(final ByteBuffer p0, final int p1, final int p2, final long p3) throws IOException;
    
    void write(final ByteBuffer p0) throws IOException;
    
    void dprint(final String p0);
    
    int getNextRequestId();
    
    ORB getBroker();
    
    CodeSetComponentInfo.CodeSetContext getCodeSetContext();
    
    void setCodeSetContext(final CodeSetComponentInfo.CodeSetContext p0);
    
    MessageMediator clientRequestMapGet(final int p0);
    
    void clientReply_1_1_Put(final MessageMediator p0);
    
    MessageMediator clientReply_1_1_Get();
    
    void clientReply_1_1_Remove();
    
    void serverRequest_1_1_Put(final MessageMediator p0);
    
    MessageMediator serverRequest_1_1_Get();
    
    void serverRequest_1_1_Remove();
    
    boolean isPostInitialContexts();
    
    void setPostInitialContexts();
    
    void purgeCalls(final SystemException p0, final boolean p1, final boolean p2);
    
    void setCodeBaseIOR(final IOR p0);
    
    IOR getCodeBaseIOR();
    
    CodeBase getCodeBase();
    
    void sendCloseConnection(final GIOPVersion p0) throws IOException;
    
    void sendMessageError(final GIOPVersion p0) throws IOException;
    
    void sendCancelRequest(final GIOPVersion p0, final int p1) throws IOException;
    
    void sendCancelRequestWithLock(final GIOPVersion p0, final int p1) throws IOException;
    
    ResponseWaitingRoom getResponseWaitingRoom();
    
    void serverRequestMapPut(final int p0, final CorbaMessageMediator p1);
    
    CorbaMessageMediator serverRequestMapGet(final int p0);
    
    void serverRequestMapRemove(final int p0);
    
    SocketChannel getSocketChannel();
    
    void serverRequestProcessingBegins();
    
    void serverRequestProcessingEnds();
    
    void closeConnectionResources();
}
