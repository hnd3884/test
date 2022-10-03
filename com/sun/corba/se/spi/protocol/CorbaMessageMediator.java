package com.sun.corba.se.spi.protocol;

import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA.Request;
import java.nio.ByteBuffer;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import org.omg.CORBA.portable.ResponseHandler;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface CorbaMessageMediator extends MessageMediator, ResponseHandler
{
    void setReplyHeader(final LocateReplyOrReplyMessage p0);
    
    LocateReplyMessage getLocateReplyHeader();
    
    ReplyMessage getReplyHeader();
    
    void setReplyExceptionDetailMessage(final String p0);
    
    RequestMessage getRequestHeader();
    
    GIOPVersion getGIOPVersion();
    
    byte getEncodingVersion();
    
    int getRequestId();
    
    Integer getRequestIdInteger();
    
    boolean isOneWay();
    
    short getAddrDisposition();
    
    String getOperationName();
    
    ServiceContexts getRequestServiceContexts();
    
    ServiceContexts getReplyServiceContexts();
    
    Message getDispatchHeader();
    
    void setDispatchHeader(final Message p0);
    
    ByteBuffer getDispatchBuffer();
    
    void setDispatchBuffer(final ByteBuffer p0);
    
    int getThreadPoolToUse();
    
    byte getStreamFormatVersion();
    
    byte getStreamFormatVersionForReply();
    
    void sendCancelRequestIfFinalFragmentNotSent();
    
    void setDIIInfo(final Request p0);
    
    boolean isDIIRequest();
    
    Exception unmarshalDIIUserException(final String p0, final InputStream p1);
    
    void setDIIException(final Exception p0);
    
    void handleDIIReply(final InputStream p0);
    
    boolean isSystemExceptionReply();
    
    boolean isUserExceptionReply();
    
    boolean isLocationForwardReply();
    
    boolean isDifferentAddrDispositionRequestedReply();
    
    short getAddrDispositionReply();
    
    IOR getForwardedIOR();
    
    SystemException getSystemExceptionReply();
    
    ObjectKey getObjectKey();
    
    void setProtocolHandler(final CorbaProtocolHandler p0);
    
    CorbaProtocolHandler getProtocolHandler();
    
    OutputStream createReply();
    
    OutputStream createExceptionReply();
    
    boolean executeReturnServantInResponseConstructor();
    
    void setExecuteReturnServantInResponseConstructor(final boolean p0);
    
    boolean executeRemoveThreadInfoInResponseConstructor();
    
    void setExecuteRemoveThreadInfoInResponseConstructor(final boolean p0);
    
    boolean executePIInResponseConstructor();
    
    void setExecutePIInResponseConstructor(final boolean p0);
}
