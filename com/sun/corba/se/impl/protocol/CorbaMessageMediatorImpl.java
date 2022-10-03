package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message_1_2;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import java.util.EmptyStackException;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.UnknownException;
import com.sun.corba.se.spi.ior.Writeable;
import com.sun.corba.se.spi.protocol.ForwardException;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.impl.protocol.giopmsgheaders.AddressingDispositionHelper;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_0;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_2;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.impl.corba.RequestImpl;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA_2_3.portable.InputStream;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.encoding.InputObject;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.pept.broker.Broker;
import org.omg.CORBA.Principal;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import org.omg.CORBA.Request;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

public class CorbaMessageMediatorImpl implements CorbaMessageMediator, CorbaProtocolHandler, MessageHandler
{
    protected ORB orb;
    protected ORBUtilSystemException wrapper;
    protected InterceptorsSystemException interceptorWrapper;
    protected CorbaContactInfo contactInfo;
    protected CorbaConnection connection;
    protected short addrDisposition;
    protected CDROutputObject outputObject;
    protected CDRInputObject inputObject;
    protected Message messageHeader;
    protected RequestMessage requestHeader;
    protected LocateReplyOrReplyMessage replyHeader;
    protected String replyExceptionDetailMessage;
    protected IOR replyIOR;
    protected Integer requestIdInteger;
    protected Message dispatchHeader;
    protected ByteBuffer dispatchByteBuffer;
    protected byte streamFormatVersion;
    protected boolean streamFormatVersionSet;
    protected Request diiRequest;
    protected boolean cancelRequestAlreadySent;
    protected ProtocolHandler protocolHandler;
    protected boolean _executeReturnServantInResponseConstructor;
    protected boolean _executeRemoveThreadInfoInResponseConstructor;
    protected boolean _executePIInResponseConstructor;
    protected boolean isThreadDone;
    
    public CorbaMessageMediatorImpl(final ORB orb, final ContactInfo contactInfo, final Connection connection, final GIOPVersion giopVersion, final IOR ior, final int n, final short addrDisposition, final String s, final boolean b) {
        this(orb, connection);
        this.contactInfo = (CorbaContactInfo)contactInfo;
        this.addrDisposition = addrDisposition;
        this.streamFormatVersion = this.getStreamFormatVersionForThisRequest(this.contactInfo.getEffectiveTargetIOR(), giopVersion);
        this.streamFormatVersionSet = true;
        this.requestHeader = MessageBase.createRequest(this.orb, giopVersion, ORBUtility.getEncodingVersion(orb, ior), n, !b, this.contactInfo.getEffectiveTargetIOR(), this.addrDisposition, s, new ServiceContexts(orb), null);
    }
    
    public CorbaMessageMediatorImpl(final ORB orb, final Connection connection) {
        this.streamFormatVersionSet = false;
        this.cancelRequestAlreadySent = false;
        this._executeReturnServantInResponseConstructor = false;
        this._executeRemoveThreadInfoInResponseConstructor = false;
        this._executePIInResponseConstructor = false;
        this.isThreadDone = false;
        this.orb = orb;
        this.connection = (CorbaConnection)connection;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.interceptorWrapper = InterceptorsSystemException.get(orb, "rpc.protocol");
    }
    
    public CorbaMessageMediatorImpl(final ORB orb, final CorbaConnection corbaConnection, final Message dispatchHeader, final ByteBuffer dispatchByteBuffer) {
        this(orb, corbaConnection);
        this.dispatchHeader = dispatchHeader;
        this.dispatchByteBuffer = dispatchByteBuffer;
    }
    
    @Override
    public Broker getBroker() {
        return this.orb;
    }
    
    @Override
    public ContactInfo getContactInfo() {
        return this.contactInfo;
    }
    
    @Override
    public Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public void initializeMessage() {
        this.getRequestHeader().write(this.outputObject);
    }
    
    @Override
    public void finishSendingRequest() {
        this.outputObject.finishSendingMessage();
    }
    
    @Override
    public InputObject waitForResponse() {
        if (this.getRequestHeader().isResponseExpected()) {
            return this.connection.waitForResponse(this);
        }
        return null;
    }
    
    @Override
    public void setOutputObject(final OutputObject outputObject) {
        this.outputObject = (CDROutputObject)outputObject;
    }
    
    @Override
    public OutputObject getOutputObject() {
        return this.outputObject;
    }
    
    @Override
    public void setInputObject(final InputObject inputObject) {
        this.inputObject = (CDRInputObject)inputObject;
    }
    
    @Override
    public InputObject getInputObject() {
        return this.inputObject;
    }
    
    @Override
    public void setReplyHeader(final LocateReplyOrReplyMessage replyHeader) {
        this.replyHeader = replyHeader;
        this.replyIOR = replyHeader.getIOR();
    }
    
    @Override
    public LocateReplyMessage getLocateReplyHeader() {
        return (LocateReplyMessage)this.replyHeader;
    }
    
    @Override
    public ReplyMessage getReplyHeader() {
        return (ReplyMessage)this.replyHeader;
    }
    
    @Override
    public void setReplyExceptionDetailMessage(final String replyExceptionDetailMessage) {
        this.replyExceptionDetailMessage = replyExceptionDetailMessage;
    }
    
    @Override
    public RequestMessage getRequestHeader() {
        return this.requestHeader;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        if (this.messageHeader != null) {
            return this.messageHeader.getGIOPVersion();
        }
        return this.getRequestHeader().getGIOPVersion();
    }
    
    @Override
    public byte getEncodingVersion() {
        if (this.messageHeader != null) {
            return this.messageHeader.getEncodingVersion();
        }
        return this.getRequestHeader().getEncodingVersion();
    }
    
    @Override
    public int getRequestId() {
        return this.getRequestHeader().getRequestId();
    }
    
    @Override
    public Integer getRequestIdInteger() {
        if (this.requestIdInteger == null) {
            this.requestIdInteger = new Integer(this.getRequestHeader().getRequestId());
        }
        return this.requestIdInteger;
    }
    
    @Override
    public boolean isOneWay() {
        return !this.getRequestHeader().isResponseExpected();
    }
    
    @Override
    public short getAddrDisposition() {
        return this.addrDisposition;
    }
    
    @Override
    public String getOperationName() {
        return this.getRequestHeader().getOperation();
    }
    
    @Override
    public ServiceContexts getRequestServiceContexts() {
        return this.getRequestHeader().getServiceContexts();
    }
    
    @Override
    public ServiceContexts getReplyServiceContexts() {
        return this.getReplyHeader().getServiceContexts();
    }
    
    @Override
    public void sendCancelRequestIfFinalFragmentNotSent() {
        if (!this.sentFullMessage() && this.sentFragment() && !this.cancelRequestAlreadySent) {
            try {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".sendCancelRequestIfFinalFragmentNotSent->: " + this.opAndId(this));
                }
                this.connection.sendCancelRequestWithLock(this.getGIOPVersion(), this.getRequestId());
                this.cancelRequestAlreadySent = true;
            }
            catch (final IOException ex) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".sendCancelRequestIfFinalFragmentNotSent: !ERROR : " + this.opAndId(this), ex);
                }
                throw this.interceptorWrapper.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_MAYBE, ex);
            }
            finally {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".sendCancelRequestIfFinalFragmentNotSent<-: " + this.opAndId(this));
                }
            }
        }
    }
    
    public boolean sentFullMessage() {
        return this.outputObject.getBufferManager().sentFullMessage();
    }
    
    public boolean sentFragment() {
        return this.outputObject.getBufferManager().sentFragment();
    }
    
    @Override
    public void setDIIInfo(final Request diiRequest) {
        this.diiRequest = diiRequest;
    }
    
    @Override
    public boolean isDIIRequest() {
        return this.diiRequest != null;
    }
    
    @Override
    public Exception unmarshalDIIUserException(final String s, final InputStream inputStream) {
        if (!this.isDIIRequest()) {
            return null;
        }
        final ExceptionList exceptions = this.diiRequest.exceptions();
        try {
            for (int i = 0; i < exceptions.count(); ++i) {
                final TypeCode item = exceptions.item(i);
                if (item.id().equals(s)) {
                    final Any create_any = this.orb.create_any();
                    create_any.read_value(inputStream, item);
                    return new UnknownUserException(create_any);
                }
            }
        }
        catch (final Exception ex) {
            throw this.wrapper.unexpectedDiiException(ex);
        }
        return this.wrapper.unknownCorbaExc(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public void setDIIException(final Exception ex) {
        this.diiRequest.env().exception(ex);
    }
    
    @Override
    public void handleDIIReply(final InputStream inputStream) {
        if (!this.isDIIRequest()) {
            return;
        }
        ((RequestImpl)this.diiRequest).unmarshalReply(inputStream);
    }
    
    @Override
    public Message getDispatchHeader() {
        return this.dispatchHeader;
    }
    
    @Override
    public void setDispatchHeader(final Message dispatchHeader) {
        this.dispatchHeader = dispatchHeader;
    }
    
    @Override
    public ByteBuffer getDispatchBuffer() {
        return this.dispatchByteBuffer;
    }
    
    @Override
    public void setDispatchBuffer(final ByteBuffer dispatchByteBuffer) {
        this.dispatchByteBuffer = dispatchByteBuffer;
    }
    
    @Override
    public int getThreadPoolToUse() {
        int threadPoolToUse = 0;
        final Message dispatchHeader = this.getDispatchHeader();
        if (dispatchHeader != null) {
            threadPoolToUse = dispatchHeader.getThreadPoolToUse();
        }
        return threadPoolToUse;
    }
    
    @Override
    public byte getStreamFormatVersion() {
        if (this.streamFormatVersionSet) {
            return this.streamFormatVersion;
        }
        return this.getStreamFormatVersionForReply();
    }
    
    @Override
    public byte getStreamFormatVersionForReply() {
        final MaxStreamFormatVersionServiceContext maxStreamFormatVersionServiceContext = (MaxStreamFormatVersionServiceContext)this.getRequestServiceContexts().get(17);
        if (maxStreamFormatVersionServiceContext != null) {
            return (byte)Math.min(ORBUtility.getMaxStreamFormatVersion(), maxStreamFormatVersionServiceContext.getMaximumStreamFormatVersion());
        }
        if (this.getGIOPVersion().lessThan(GIOPVersion.V1_3)) {
            return 1;
        }
        return 2;
    }
    
    @Override
    public boolean isSystemExceptionReply() {
        return this.replyHeader.getReplyStatus() == 2;
    }
    
    @Override
    public boolean isUserExceptionReply() {
        return this.replyHeader.getReplyStatus() == 1;
    }
    
    @Override
    public boolean isLocationForwardReply() {
        return this.replyHeader.getReplyStatus() == 3 || this.replyHeader.getReplyStatus() == 4;
    }
    
    @Override
    public boolean isDifferentAddrDispositionRequestedReply() {
        return this.replyHeader.getReplyStatus() == 5;
    }
    
    @Override
    public short getAddrDispositionReply() {
        return this.replyHeader.getAddrDisposition();
    }
    
    @Override
    public IOR getForwardedIOR() {
        return this.replyHeader.getIOR();
    }
    
    @Override
    public SystemException getSystemExceptionReply() {
        return this.replyHeader.getSystemException(this.replyExceptionDetailMessage);
    }
    
    @Override
    public ObjectKey getObjectKey() {
        return this.getRequestHeader().getObjectKey();
    }
    
    @Override
    public void setProtocolHandler(final CorbaProtocolHandler corbaProtocolHandler) {
        throw this.wrapper.methodShouldNotBeCalled();
    }
    
    @Override
    public CorbaProtocolHandler getProtocolHandler() {
        return this;
    }
    
    @Override
    public OutputStream createReply() {
        this.getProtocolHandler().createResponse(this, null);
        return (org.omg.CORBA_2_3.portable.OutputStream)this.getOutputObject();
    }
    
    @Override
    public OutputStream createExceptionReply() {
        this.getProtocolHandler().createUserExceptionResponse(this, null);
        return (org.omg.CORBA_2_3.portable.OutputStream)this.getOutputObject();
    }
    
    @Override
    public boolean executeReturnServantInResponseConstructor() {
        return this._executeReturnServantInResponseConstructor;
    }
    
    @Override
    public void setExecuteReturnServantInResponseConstructor(final boolean executeReturnServantInResponseConstructor) {
        this._executeReturnServantInResponseConstructor = executeReturnServantInResponseConstructor;
    }
    
    @Override
    public boolean executeRemoveThreadInfoInResponseConstructor() {
        return this._executeRemoveThreadInfoInResponseConstructor;
    }
    
    @Override
    public void setExecuteRemoveThreadInfoInResponseConstructor(final boolean executeRemoveThreadInfoInResponseConstructor) {
        this._executeRemoveThreadInfoInResponseConstructor = executeRemoveThreadInfoInResponseConstructor;
    }
    
    @Override
    public boolean executePIInResponseConstructor() {
        return this._executePIInResponseConstructor;
    }
    
    @Override
    public void setExecutePIInResponseConstructor(final boolean executePIInResponseConstructor) {
        this._executePIInResponseConstructor = executePIInResponseConstructor;
    }
    
    private byte getStreamFormatVersionForThisRequest(final IOR ior, final GIOPVersion giopVersion) {
        final byte maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
        final Iterator iteratorById = this.contactInfo.getEffectiveTargetIOR().getProfile().getTaggedProfileTemplate().iteratorById(38);
        if (iteratorById.hasNext()) {
            return (byte)Math.min(maxStreamFormatVersion, ((MaxStreamFormatVersionComponent)iteratorById.next()).getMaxStreamFormatVersion());
        }
        if (giopVersion.lessThan(GIOPVersion.V1_3)) {
            return 1;
        }
        return 2;
    }
    
    @Override
    public boolean handleRequest(final MessageMediator messageMediator) {
        try {
            this.dispatchHeader.callback(this);
        }
        catch (final IOException ex) {}
        return this.isThreadDone;
    }
    
    private void setWorkThenPoolOrResumeSelect(final Message message) {
        if (this.getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
            this.resumeSelect(message);
        }
        else {
            this.isThreadDone = true;
            this.orb.getTransportManager().getSelector(0).unregisterForEvent(this.getConnection().getEventHandler());
            this.orb.getTransportManager().getSelector(0).registerForEvent(this.getConnection().getEventHandler());
        }
    }
    
    private void setWorkThenReadOrResumeSelect(final Message message) {
        if (this.getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
            this.resumeSelect(message);
        }
        else {
            this.isThreadDone = false;
        }
    }
    
    private void resumeSelect(final Message message) {
        if (this.transportDebug()) {
            this.dprint(".resumeSelect:->");
            String s = "?";
            if (message instanceof RequestMessage) {
                s = new Integer(((RequestMessage)message).getRequestId()).toString();
            }
            else if (message instanceof ReplyMessage) {
                s = new Integer(((ReplyMessage)message).getRequestId()).toString();
            }
            else if (message instanceof FragmentMessage_1_2) {
                s = new Integer(((FragmentMessage_1_2)message).getRequestId()).toString();
            }
            this.dprint(".resumeSelect: id/" + s + " " + this.getConnection());
        }
        this.orb.getTransportManager().getSelector(0).registerInterestOps(this.getConnection().getEventHandler());
        if (this.transportDebug()) {
            this.dprint(".resumeSelect:<-");
        }
    }
    
    private void setInputObject() {
        if (this.getConnection().getContactInfo() != null) {
            this.inputObject = (CDRInputObject)this.getConnection().getContactInfo().createInputObject(this.orb, this);
        }
        else {
            if (this.getConnection().getAcceptor() == null) {
                throw new RuntimeException("CorbaMessageMediatorImpl.setInputObject");
            }
            this.inputObject = (CDRInputObject)this.getConnection().getAcceptor().createInputObject(this.orb, this);
        }
        this.inputObject.setMessageMediator(this);
        this.setInputObject(this.inputObject);
    }
    
    private void signalResponseReceived() {
        this.connection.getResponseWaitingRoom().responseReceived(this.inputObject);
    }
    
    @Override
    public void handleInput(final Message message) throws IOException {
        try {
            this.messageHeader = message;
            if (this.transportDebug()) {
                this.dprint(".handleInput->: " + MessageBase.typeToString(message.getType()));
            }
            this.setWorkThenReadOrResumeSelect(message);
            switch (message.getType()) {
                case 5: {
                    if (this.transportDebug()) {
                        this.dprint(".handleInput: CloseConnection: purging");
                    }
                    this.connection.purgeCalls(this.wrapper.connectionRebind(), true, false);
                    break;
                }
                case 6: {
                    if (this.transportDebug()) {
                        this.dprint(".handleInput: MessageError: purging");
                    }
                    this.connection.purgeCalls(this.wrapper.recvMsgError(), true, false);
                    break;
                }
                default: {
                    if (this.transportDebug()) {
                        this.dprint(".handleInput: ERROR: " + MessageBase.typeToString(message.getType()));
                    }
                    throw this.wrapper.badGiopRequestType();
                }
            }
            this.releaseByteBufferToPool();
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".handleInput<-: " + MessageBase.typeToString(message.getType()));
            }
        }
    }
    
    @Override
    public void handleInput(final RequestMessage_1_0 workThenPoolOrResumeSelect) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.0->: " + workThenPoolOrResumeSelect);
            }
            try {
                this.requestHeader = workThenPoolOrResumeSelect;
                this.messageHeader = workThenPoolOrResumeSelect;
                this.setInputObject();
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(workThenPoolOrResumeSelect);
            }
            this.getProtocolHandler().handleRequest(workThenPoolOrResumeSelect, this);
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.0: !!ERROR!!: " + workThenPoolOrResumeSelect, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.0<-: " + workThenPoolOrResumeSelect);
            }
        }
    }
    
    @Override
    public void handleInput(final RequestMessage_1_1 workThenPoolOrResumeSelect) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.1->: " + workThenPoolOrResumeSelect);
            }
            try {
                this.requestHeader = workThenPoolOrResumeSelect;
                this.messageHeader = workThenPoolOrResumeSelect;
                this.setInputObject();
                this.connection.serverRequest_1_1_Put(this);
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(workThenPoolOrResumeSelect);
            }
            this.getProtocolHandler().handleRequest(workThenPoolOrResumeSelect, this);
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.1: !!ERROR!!: " + workThenPoolOrResumeSelect, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.1<-: " + workThenPoolOrResumeSelect);
            }
        }
    }
    
    @Override
    public void handleInput(final RequestMessage_1_2 workThenPoolOrResumeSelect) throws IOException {
        try {
            try {
                this.requestHeader = workThenPoolOrResumeSelect;
                ((Message_1_2)(this.messageHeader = workThenPoolOrResumeSelect)).unmarshalRequestID(this.dispatchByteBuffer);
                this.setInputObject();
                if (this.transportDebug()) {
                    this.dprint(".REQUEST 1.2->: id/" + workThenPoolOrResumeSelect.getRequestId() + ": " + workThenPoolOrResumeSelect);
                }
                this.connection.serverRequestMapPut(workThenPoolOrResumeSelect.getRequestId(), this);
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(workThenPoolOrResumeSelect);
            }
            this.getProtocolHandler().handleRequest(workThenPoolOrResumeSelect, this);
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.2: id/" + workThenPoolOrResumeSelect.getRequestId() + ": !!ERROR!!: " + workThenPoolOrResumeSelect, t);
            }
            this.connection.serverRequestMapRemove(workThenPoolOrResumeSelect.getRequestId());
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.2<-: id/" + workThenPoolOrResumeSelect.getRequestId() + ": " + workThenPoolOrResumeSelect);
            }
        }
        finally {
            this.connection.serverRequestMapRemove(workThenPoolOrResumeSelect.getRequestId());
            if (this.transportDebug()) {
                this.dprint(".REQUEST 1.2<-: id/" + workThenPoolOrResumeSelect.getRequestId() + ": " + workThenPoolOrResumeSelect);
            }
        }
    }
    
    @Override
    public void handleInput(final ReplyMessage_1_0 workThenReadOrResumeSelect) throws IOException {
        try {
            try {
                if (this.transportDebug()) {
                    this.dprint(".REPLY 1.0->: " + workThenReadOrResumeSelect);
                }
                this.replyHeader = workThenReadOrResumeSelect;
                this.messageHeader = workThenReadOrResumeSelect;
                this.setInputObject();
                this.inputObject.unmarshalHeader();
                this.signalResponseReceived();
            }
            finally {
                this.setWorkThenReadOrResumeSelect(workThenReadOrResumeSelect);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.0: !!ERROR!!: " + workThenReadOrResumeSelect, t);
            }
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.0<-: " + workThenReadOrResumeSelect);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.0<-: " + workThenReadOrResumeSelect);
            }
        }
    }
    
    @Override
    public void handleInput(final ReplyMessage_1_1 replyMessage_1_1) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.1->: " + replyMessage_1_1);
            }
            this.replyHeader = replyMessage_1_1;
            this.messageHeader = replyMessage_1_1;
            this.setInputObject();
            if (replyMessage_1_1.moreFragmentsToFollow()) {
                this.connection.clientReply_1_1_Put(this);
                this.setWorkThenPoolOrResumeSelect(replyMessage_1_1);
                this.inputObject.unmarshalHeader();
                this.signalResponseReceived();
            }
            else {
                this.inputObject.unmarshalHeader();
                this.signalResponseReceived();
                this.setWorkThenReadOrResumeSelect(replyMessage_1_1);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.1: !!ERROR!!: " + replyMessage_1_1);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.1<-: " + replyMessage_1_1);
            }
        }
    }
    
    @Override
    public void handleInput(final ReplyMessage_1_2 workThenReadOrResumeSelect) throws IOException {
        try {
            try {
                this.replyHeader = workThenReadOrResumeSelect;
                ((Message_1_2)(this.messageHeader = workThenReadOrResumeSelect)).unmarshalRequestID(this.dispatchByteBuffer);
                if (this.transportDebug()) {
                    this.dprint(".REPLY 1.2->: id/" + workThenReadOrResumeSelect.getRequestId() + ": more?: " + workThenReadOrResumeSelect.moreFragmentsToFollow() + ": " + workThenReadOrResumeSelect);
                }
                this.setInputObject();
                this.signalResponseReceived();
            }
            finally {
                this.setWorkThenReadOrResumeSelect(workThenReadOrResumeSelect);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.2: id/" + workThenReadOrResumeSelect.getRequestId() + ": !!ERROR!!: " + workThenReadOrResumeSelect, t);
            }
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.2<-: id/" + workThenReadOrResumeSelect.getRequestId() + ": " + workThenReadOrResumeSelect);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".REPLY 1.2<-: id/" + workThenReadOrResumeSelect.getRequestId() + ": " + workThenReadOrResumeSelect);
            }
        }
    }
    
    @Override
    public void handleInput(final LocateRequestMessage_1_0 locateRequestMessage_1_0) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.0->: " + locateRequestMessage_1_0);
            }
            try {
                this.messageHeader = locateRequestMessage_1_0;
                this.setInputObject();
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(locateRequestMessage_1_0);
            }
            this.getProtocolHandler().handleRequest(locateRequestMessage_1_0, this);
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.0: !!ERROR!!: " + locateRequestMessage_1_0, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.0<-: " + locateRequestMessage_1_0);
            }
        }
    }
    
    @Override
    public void handleInput(final LocateRequestMessage_1_1 locateRequestMessage_1_1) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.1->: " + locateRequestMessage_1_1);
            }
            try {
                this.messageHeader = locateRequestMessage_1_1;
                this.setInputObject();
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(locateRequestMessage_1_1);
            }
            this.getProtocolHandler().handleRequest(locateRequestMessage_1_1, this);
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.1: !!ERROR!!: " + locateRequestMessage_1_1, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.1<-:" + locateRequestMessage_1_1);
            }
        }
    }
    
    @Override
    public void handleInput(final LocateRequestMessage_1_2 locateRequestMessage_1_2) throws IOException {
        try {
            try {
                ((Message_1_2)(this.messageHeader = locateRequestMessage_1_2)).unmarshalRequestID(this.dispatchByteBuffer);
                this.setInputObject();
                if (this.transportDebug()) {
                    this.dprint(".LOCATE_REQUEST 1.2->: id/" + locateRequestMessage_1_2.getRequestId() + ": " + locateRequestMessage_1_2);
                }
                if (locateRequestMessage_1_2.moreFragmentsToFollow()) {
                    this.connection.serverRequestMapPut(locateRequestMessage_1_2.getRequestId(), this);
                }
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(locateRequestMessage_1_2);
            }
            this.getProtocolHandler().handleRequest(locateRequestMessage_1_2, this);
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.2: id/" + locateRequestMessage_1_2.getRequestId() + ": !!ERROR!!: " + locateRequestMessage_1_2, t);
            }
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.2<-: id/" + locateRequestMessage_1_2.getRequestId() + ": " + locateRequestMessage_1_2);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REQUEST 1.2<-: id/" + locateRequestMessage_1_2.getRequestId() + ": " + locateRequestMessage_1_2);
            }
        }
    }
    
    @Override
    public void handleInput(final LocateReplyMessage_1_0 locateReplyMessage_1_0) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.0->:" + locateReplyMessage_1_0);
            }
            try {
                this.messageHeader = locateReplyMessage_1_0;
                this.setInputObject();
                this.inputObject.unmarshalHeader();
                this.signalResponseReceived();
            }
            finally {
                this.setWorkThenReadOrResumeSelect(locateReplyMessage_1_0);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.0: !!ERROR!!: " + locateReplyMessage_1_0, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.0<-: " + locateReplyMessage_1_0);
            }
        }
    }
    
    @Override
    public void handleInput(final LocateReplyMessage_1_1 locateReplyMessage_1_1) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.1->: " + locateReplyMessage_1_1);
            }
            try {
                this.messageHeader = locateReplyMessage_1_1;
                this.setInputObject();
                this.inputObject.unmarshalHeader();
                this.signalResponseReceived();
            }
            finally {
                this.setWorkThenReadOrResumeSelect(locateReplyMessage_1_1);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.1: !!ERROR!!: " + locateReplyMessage_1_1, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.1<-: " + locateReplyMessage_1_1);
            }
        }
    }
    
    @Override
    public void handleInput(final LocateReplyMessage_1_2 locateReplyMessage_1_2) throws IOException {
        try {
            try {
                ((Message_1_2)(this.messageHeader = locateReplyMessage_1_2)).unmarshalRequestID(this.dispatchByteBuffer);
                this.setInputObject();
                if (this.transportDebug()) {
                    this.dprint(".LOCATE_REPLY 1.2->: id/" + locateReplyMessage_1_2.getRequestId() + ": " + locateReplyMessage_1_2);
                }
                this.signalResponseReceived();
            }
            finally {
                this.setWorkThenPoolOrResumeSelect(locateReplyMessage_1_2);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.2: id/" + locateReplyMessage_1_2.getRequestId() + ": !!ERROR!!: " + locateReplyMessage_1_2, t);
            }
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.2<-: id/" + locateReplyMessage_1_2.getRequestId() + ": " + locateReplyMessage_1_2);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".LOCATE_REPLY 1.2<-: id/" + locateReplyMessage_1_2.getRequestId() + ": " + locateReplyMessage_1_2);
            }
        }
    }
    
    @Override
    public void handleInput(final FragmentMessage_1_1 fragmentMessage_1_1) throws IOException {
        try {
            if (this.transportDebug()) {
                this.dprint(".FRAGMENT 1.1->: more?: " + fragmentMessage_1_1.moreFragmentsToFollow() + ": " + fragmentMessage_1_1);
            }
            try {
                this.messageHeader = fragmentMessage_1_1;
                CDRInputStream cdrInputStream = null;
                MessageMediator messageMediator;
                if (this.connection.isServer()) {
                    messageMediator = this.connection.serverRequest_1_1_Get();
                }
                else {
                    messageMediator = this.connection.clientReply_1_1_Get();
                }
                if (messageMediator != null) {
                    cdrInputStream = (CDRInputObject)messageMediator.getInputObject();
                }
                if (cdrInputStream == null) {
                    if (this.transportDebug()) {
                        this.dprint(".FRAGMENT 1.1: ++++DISCARDING++++: " + fragmentMessage_1_1);
                    }
                    this.releaseByteBufferToPool();
                    return;
                }
                cdrInputStream.getBufferManager().processFragment(this.dispatchByteBuffer, fragmentMessage_1_1);
                if (!fragmentMessage_1_1.moreFragmentsToFollow()) {
                    if (this.connection.isServer()) {
                        this.connection.serverRequest_1_1_Remove();
                    }
                    else {
                        this.connection.clientReply_1_1_Remove();
                    }
                }
            }
            finally {
                this.setWorkThenReadOrResumeSelect(fragmentMessage_1_1);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".FRAGMENT 1.1: !!ERROR!!: " + fragmentMessage_1_1, t);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".FRAGMENT 1.1<-: " + fragmentMessage_1_1);
            }
        }
    }
    
    @Override
    public void handleInput(final FragmentMessage_1_2 fragmentMessage_1_2) throws IOException {
        try {
            try {
                ((Message_1_2)(this.messageHeader = fragmentMessage_1_2)).unmarshalRequestID(this.dispatchByteBuffer);
                if (this.transportDebug()) {
                    this.dprint(".FRAGMENT 1.2->: id/" + fragmentMessage_1_2.getRequestId() + ": more?: " + fragmentMessage_1_2.moreFragmentsToFollow() + ": " + fragmentMessage_1_2);
                }
                InputObject inputObject = null;
                MessageMediator messageMediator;
                if (this.connection.isServer()) {
                    messageMediator = this.connection.serverRequestMapGet(fragmentMessage_1_2.getRequestId());
                }
                else {
                    messageMediator = this.connection.clientRequestMapGet(fragmentMessage_1_2.getRequestId());
                }
                if (messageMediator != null) {
                    inputObject = messageMediator.getInputObject();
                }
                if (inputObject == null) {
                    if (this.transportDebug()) {
                        this.dprint(".FRAGMENT 1.2: id/" + fragmentMessage_1_2.getRequestId() + ": ++++DISCARDING++++: " + fragmentMessage_1_2);
                    }
                    this.releaseByteBufferToPool();
                    return;
                }
                ((CDRInputObject)inputObject).getBufferManager().processFragment(this.dispatchByteBuffer, fragmentMessage_1_2);
                if (!this.connection.isServer()) {}
            }
            finally {
                this.setWorkThenReadOrResumeSelect(fragmentMessage_1_2);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".FRAGMENT 1.2: id/" + fragmentMessage_1_2.getRequestId() + ": !!ERROR!!: " + fragmentMessage_1_2, t);
            }
            if (this.transportDebug()) {
                this.dprint(".FRAGMENT 1.2<-: id/" + fragmentMessage_1_2.getRequestId() + ": " + fragmentMessage_1_2);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".FRAGMENT 1.2<-: id/" + fragmentMessage_1_2.getRequestId() + ": " + fragmentMessage_1_2);
            }
        }
    }
    
    @Override
    public void handleInput(final CancelRequestMessage cancelRequestMessage) throws IOException {
        try {
            try {
                this.messageHeader = cancelRequestMessage;
                this.setInputObject();
                this.inputObject.unmarshalHeader();
                if (this.transportDebug()) {
                    this.dprint(".CANCEL->: id/" + cancelRequestMessage.getRequestId() + ": " + cancelRequestMessage.getGIOPVersion() + ": " + cancelRequestMessage);
                }
                this.processCancelRequest(cancelRequestMessage.getRequestId());
                this.releaseByteBufferToPool();
            }
            finally {
                this.setWorkThenReadOrResumeSelect(cancelRequestMessage);
            }
        }
        catch (final Throwable t) {
            if (this.transportDebug()) {
                this.dprint(".CANCEL: id/" + cancelRequestMessage.getRequestId() + ": !!ERROR!!: " + cancelRequestMessage, t);
            }
            if (this.transportDebug()) {
                this.dprint(".CANCEL<-: id/" + cancelRequestMessage.getRequestId() + ": " + cancelRequestMessage.getGIOPVersion() + ": " + cancelRequestMessage);
            }
        }
        finally {
            if (this.transportDebug()) {
                this.dprint(".CANCEL<-: id/" + cancelRequestMessage.getRequestId() + ": " + cancelRequestMessage.getGIOPVersion() + ": " + cancelRequestMessage);
            }
        }
    }
    
    private void throwNotImplemented() {
        this.isThreadDone = false;
        this.throwNotImplemented("");
    }
    
    private void throwNotImplemented(final String s) {
        throw new RuntimeException("CorbaMessageMediatorImpl: not implemented " + s);
    }
    
    private void dprint(final String s, final Throwable t) {
        this.dprint(s);
        t.printStackTrace(System.out);
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("CorbaMessageMediatorImpl", s);
    }
    
    protected String opAndId(final CorbaMessageMediator corbaMessageMediator) {
        return ORBUtility.operationNameAndRequestId(corbaMessageMediator);
    }
    
    private boolean transportDebug() {
        return this.orb.transportDebugFlag;
    }
    
    private final void processCancelRequest(final int n) {
        if (!this.connection.isServer()) {
            return;
        }
        MessageMediator messageMediator = this.connection.serverRequestMapGet(n);
        if (messageMediator == null) {
            messageMediator = this.connection.serverRequest_1_1_Get();
            if (messageMediator == null) {
                return;
            }
            final int requestId = ((CorbaMessageMediator)messageMediator).getRequestId();
            if (requestId != n) {
                return;
            }
            if (requestId == 0) {
                return;
            }
        }
        else {
            ((CorbaMessageMediator)messageMediator).getRequestId();
        }
        if (((CorbaMessageMediator)messageMediator).getRequestHeader().getType() != 0) {
            this.wrapper.badMessageTypeForCancel();
        }
        ((BufferManagerReadStream)((CDRInputObject)messageMediator.getInputObject()).getBufferManager()).cancelProcessing(n);
    }
    
    @Override
    public void handleRequest(final RequestMessage requestMessage, final CorbaMessageMediator corbaMessageMediator) {
        try {
            this.beginRequest(corbaMessageMediator);
            try {
                this.handleRequestRequest(corbaMessageMediator);
                if (corbaMessageMediator.isOneWay()) {
                    return;
                }
            }
            catch (final Throwable t) {
                if (corbaMessageMediator.isOneWay()) {
                    return;
                }
                this.handleThrowableDuringServerDispatch(corbaMessageMediator, t, CompletionStatus.COMPLETED_MAYBE);
            }
            this.sendResponse(corbaMessageMediator);
        }
        catch (final Throwable t2) {
            this.dispatchError(corbaMessageMediator, "RequestMessage", t2);
        }
        finally {
            this.endRequest(corbaMessageMediator);
        }
    }
    
    @Override
    public void handleRequest(final LocateRequestMessage locateRequestMessage, final CorbaMessageMediator corbaMessageMediator) {
        try {
            this.beginRequest(corbaMessageMediator);
            try {
                this.handleLocateRequest(corbaMessageMediator);
            }
            catch (final Throwable t) {
                this.handleThrowableDuringServerDispatch(corbaMessageMediator, t, CompletionStatus.COMPLETED_MAYBE);
            }
            this.sendResponse(corbaMessageMediator);
        }
        catch (final Throwable t2) {
            this.dispatchError(corbaMessageMediator, "LocateRequestMessage", t2);
        }
        finally {
            this.endRequest(corbaMessageMediator);
        }
    }
    
    private void beginRequest(final CorbaMessageMediator corbaMessageMediator) {
        if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag) {
            this.dprint(".handleRequest->:");
        }
        this.connection.serverRequestProcessingBegins();
    }
    
    private void dispatchError(final CorbaMessageMediator corbaMessageMediator, final String s, final Throwable t) {
        if (this.orb.subcontractDebugFlag) {
            this.dprint(".handleRequest: " + this.opAndId(corbaMessageMediator) + ": !!ERROR!!: " + s, t);
        }
    }
    
    private void sendResponse(final CorbaMessageMediator corbaMessageMediator) {
        if (this.orb.subcontractDebugFlag) {
            this.dprint(".handleRequest: " + this.opAndId(corbaMessageMediator) + ": sending response");
        }
        final CDROutputObject cdrOutputObject = (CDROutputObject)corbaMessageMediator.getOutputObject();
        if (cdrOutputObject != null) {
            cdrOutputObject.finishSendingMessage();
        }
    }
    
    private void endRequest(final CorbaMessageMediator corbaMessageMediator) {
        final ORB orb = (ORB)corbaMessageMediator.getBroker();
        if (orb.subcontractDebugFlag) {
            this.dprint(".handleRequest<-: " + this.opAndId(corbaMessageMediator));
        }
        try {
            try (final OutputObject outputObject = corbaMessageMediator.getOutputObject()) {}
            try (final InputObject inputObject = corbaMessageMediator.getInputObject()) {}
        }
        catch (final IOException ex) {
            if (orb.subcontractDebugFlag) {
                this.dprint(".endRequest: IOException:" + ex.getMessage(), ex);
            }
        }
        finally {
            ((CorbaConnection)corbaMessageMediator.getConnection()).serverRequestProcessingEnds();
        }
    }
    
    protected void handleRequestRequest(final CorbaMessageMediator corbaMessageMediator) {
        ((CDRInputObject)corbaMessageMediator.getInputObject()).unmarshalHeader();
        final ORB orb = (ORB)corbaMessageMediator.getBroker();
        synchronized (orb) {
            orb.checkShutdownState();
        }
        final ObjectKey objectKey = corbaMessageMediator.getObjectKey();
        if (orb.subcontractDebugFlag) {
            this.dprint(".handleRequest: " + this.opAndId(corbaMessageMediator) + ": dispatching to scid: " + objectKey.getTemplate().getSubcontractId());
        }
        final CorbaServerRequestDispatcher serverRequestDispatcher = objectKey.getServerRequestDispatcher(orb);
        if (orb.subcontractDebugFlag) {
            this.dprint(".handleRequest: " + this.opAndId(corbaMessageMediator) + ": dispatching to sc: " + serverRequestDispatcher);
        }
        if (serverRequestDispatcher == null) {
            throw this.wrapper.noServerScInDispatch();
        }
        try {
            orb.startingDispatch();
            serverRequestDispatcher.dispatch(corbaMessageMediator);
        }
        finally {
            orb.finishedDispatch();
        }
    }
    
    protected void handleLocateRequest(final CorbaMessageMediator messageMediator) {
        final ORB orb = (ORB)messageMediator.getBroker();
        final LocateRequestMessage locateRequestMessage = (LocateRequestMessage)messageMediator.getDispatchHeader();
        IOR locate = null;
        short expectedAddrDisp = -1;
        LocateReplyMessage locateReplyMessage;
        try {
            ((CDRInputObject)messageMediator.getInputObject()).unmarshalHeader();
            final CorbaServerRequestDispatcher serverRequestDispatcher = locateRequestMessage.getObjectKey().getServerRequestDispatcher(orb);
            if (serverRequestDispatcher == null) {
                return;
            }
            locate = serverRequestDispatcher.locate(locateRequestMessage.getObjectKey());
            if (locate == null) {
                locateReplyMessage = MessageBase.createLocateReply(orb, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 1, null);
            }
            else {
                locateReplyMessage = MessageBase.createLocateReply(orb, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 2, locate);
            }
        }
        catch (final AddressingDispositionException ex) {
            locateReplyMessage = MessageBase.createLocateReply(orb, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 5, null);
            expectedAddrDisp = ex.expectedAddrDisp();
        }
        catch (final RequestCanceledException ex2) {
            return;
        }
        catch (final Exception ex3) {
            locateReplyMessage = MessageBase.createLocateReply(orb, locateRequestMessage.getGIOPVersion(), locateRequestMessage.getEncodingVersion(), locateRequestMessage.getRequestId(), 0, null);
        }
        final CDROutputObject appropriateOutputObject = this.createAppropriateOutputObject(messageMediator, locateRequestMessage, locateReplyMessage);
        messageMediator.setOutputObject(appropriateOutputObject);
        appropriateOutputObject.setMessageMediator(messageMediator);
        locateReplyMessage.write(appropriateOutputObject);
        if (locate != null) {
            locate.write(appropriateOutputObject);
        }
        if (expectedAddrDisp != -1) {
            AddressingDispositionHelper.write(appropriateOutputObject, expectedAddrDisp);
        }
    }
    
    private CDROutputObject createAppropriateOutputObject(final CorbaMessageMediator corbaMessageMediator, final Message message, final LocateReplyMessage locateReplyMessage) {
        CDROutputObject cdrOutputObject;
        if (message.getGIOPVersion().lessThan(GIOPVersion.V1_2)) {
            cdrOutputObject = OutputStreamFactory.newCDROutputObject((ORB)corbaMessageMediator.getBroker(), this, GIOPVersion.V1_0, (CorbaConnection)corbaMessageMediator.getConnection(), locateReplyMessage, (byte)1);
        }
        else {
            cdrOutputObject = OutputStreamFactory.newCDROutputObject((ORB)corbaMessageMediator.getBroker(), corbaMessageMediator, locateReplyMessage, (byte)1);
        }
        return cdrOutputObject;
    }
    
    @Override
    public void handleThrowableDuringServerDispatch(final CorbaMessageMediator corbaMessageMediator, final Throwable t, final CompletionStatus completionStatus) {
        if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag) {
            this.dprint(".handleThrowableDuringServerDispatch: " + this.opAndId(corbaMessageMediator) + ": " + t);
        }
        this.handleThrowableDuringServerDispatch(corbaMessageMediator, t, completionStatus, 1);
    }
    
    protected void handleThrowableDuringServerDispatch(final CorbaMessageMediator corbaMessageMediator, final Throwable t, final CompletionStatus completionStatus, final int n) {
        if (n > 10) {
            if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag) {
                this.dprint(".handleThrowableDuringServerDispatch: " + this.opAndId(corbaMessageMediator) + ": cannot handle: " + t);
            }
            final RuntimeException ex = new RuntimeException("handleThrowableDuringServerDispatch: cannot create response.");
            ex.initCause(t);
            throw ex;
        }
        try {
            if (t instanceof ForwardException) {
                this.createLocationForward(corbaMessageMediator, ((ForwardException)t).getIOR(), null);
                return;
            }
            if (t instanceof AddressingDispositionException) {
                this.handleAddressingDisposition(corbaMessageMediator, (AddressingDispositionException)t);
                return;
            }
            this.createSystemExceptionResponse(corbaMessageMediator, this.convertThrowableToSystemException(t, completionStatus), null);
        }
        catch (final Throwable t2) {
            this.handleThrowableDuringServerDispatch(corbaMessageMediator, t2, completionStatus, n + 1);
        }
    }
    
    protected SystemException convertThrowableToSystemException(final Throwable t, final CompletionStatus completionStatus) {
        if (t instanceof SystemException) {
            return (SystemException)t;
        }
        if (t instanceof RequestCanceledException) {
            return this.wrapper.requestCanceled(t);
        }
        return this.wrapper.runtimeexception(CompletionStatus.COMPLETED_MAYBE, t);
    }
    
    protected void handleAddressingDisposition(final CorbaMessageMediator corbaMessageMediator, final AddressingDispositionException ex) {
        switch (corbaMessageMediator.getRequestHeader().getType()) {
            case 0: {
                final ReplyMessage reply = MessageBase.createReply((ORB)corbaMessageMediator.getBroker(), corbaMessageMediator.getGIOPVersion(), corbaMessageMediator.getEncodingVersion(), corbaMessageMediator.getRequestId(), 5, null, null);
                final CDROutputObject cdrOutputObject = OutputStreamFactory.newCDROutputObject((ORB)corbaMessageMediator.getBroker(), this, corbaMessageMediator.getGIOPVersion(), (CorbaConnection)corbaMessageMediator.getConnection(), reply, (byte)1);
                corbaMessageMediator.setOutputObject(cdrOutputObject);
                cdrOutputObject.setMessageMediator(corbaMessageMediator);
                reply.write(cdrOutputObject);
                AddressingDispositionHelper.write(cdrOutputObject, ex.expectedAddrDisp());
                return;
            }
            case 3: {
                final LocateReplyMessage locateReply = MessageBase.createLocateReply((ORB)corbaMessageMediator.getBroker(), corbaMessageMediator.getGIOPVersion(), corbaMessageMediator.getEncodingVersion(), corbaMessageMediator.getRequestId(), 5, null);
                final short expectedAddrDisp = ex.expectedAddrDisp();
                final CDROutputObject appropriateOutputObject = this.createAppropriateOutputObject(corbaMessageMediator, corbaMessageMediator.getRequestHeader(), locateReply);
                corbaMessageMediator.setOutputObject(appropriateOutputObject);
                appropriateOutputObject.setMessageMediator(corbaMessageMediator);
                locateReply.write(appropriateOutputObject);
                final Writeable writeable = null;
                if (writeable != null) {
                    writeable.write(appropriateOutputObject);
                }
                if (expectedAddrDisp != -1) {
                    AddressingDispositionHelper.write(appropriateOutputObject, expectedAddrDisp);
                }
            }
            default: {}
        }
    }
    
    @Override
    public CorbaMessageMediator createResponse(final CorbaMessageMediator corbaMessageMediator, final ServiceContexts serviceContexts) {
        return this.createResponseHelper(corbaMessageMediator, this.getServiceContextsForReply(corbaMessageMediator, null));
    }
    
    @Override
    public CorbaMessageMediator createUserExceptionResponse(final CorbaMessageMediator corbaMessageMediator, final ServiceContexts serviceContexts) {
        return this.createResponseHelper(corbaMessageMediator, this.getServiceContextsForReply(corbaMessageMediator, null), true);
    }
    
    @Override
    public CorbaMessageMediator createUnknownExceptionResponse(final CorbaMessageMediator corbaMessageMediator, final UnknownException ex) {
        final UNKNOWN unknown = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
        final ServiceContexts serviceContexts = new ServiceContexts((ORB)corbaMessageMediator.getBroker());
        serviceContexts.put(new UEInfoServiceContext(unknown));
        return this.createSystemExceptionResponse(corbaMessageMediator, unknown, serviceContexts);
    }
    
    @Override
    public CorbaMessageMediator createSystemExceptionResponse(final CorbaMessageMediator corbaMessageMediator, final SystemException serverPIInfo, final ServiceContexts serviceContexts) {
        if (corbaMessageMediator.getConnection() != null) {
            final CorbaMessageMediatorImpl corbaMessageMediatorImpl = (CorbaMessageMediatorImpl)((CorbaConnection)corbaMessageMediator.getConnection()).serverRequestMapGet(corbaMessageMediator.getRequestId());
            OutputObject outputObject = null;
            if (corbaMessageMediatorImpl != null) {
                outputObject = corbaMessageMediatorImpl.getOutputObject();
            }
            if (outputObject != null && corbaMessageMediatorImpl.sentFragment() && !corbaMessageMediatorImpl.sentFullMessage()) {
                return corbaMessageMediatorImpl;
            }
        }
        if (corbaMessageMediator.executePIInResponseConstructor()) {
            ((ORB)corbaMessageMediator.getBroker()).getPIHandler().setServerPIInfo(serverPIInfo);
        }
        if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag && serverPIInfo != null) {
            this.dprint(".createSystemExceptionResponse: " + this.opAndId(corbaMessageMediator), serverPIInfo);
        }
        final ServiceContexts serviceContextsForReply = this.getServiceContextsForReply(corbaMessageMediator, serviceContexts);
        this.addExceptionDetailMessage(corbaMessageMediator, serverPIInfo, serviceContextsForReply);
        final CorbaMessageMediator responseHelper = this.createResponseHelper(corbaMessageMediator, serviceContextsForReply, false);
        ORBUtility.writeSystemException(serverPIInfo, (OutputStream)responseHelper.getOutputObject());
        return responseHelper;
    }
    
    private void addExceptionDetailMessage(final CorbaMessageMediator corbaMessageMediator, final SystemException ex, final ServiceContexts serviceContexts) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        ex.printStackTrace(printWriter);
        printWriter.flush();
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)corbaMessageMediator.getBroker());
        encapsOutputStream.putEndian();
        encapsOutputStream.write_wstring(byteArrayOutputStream.toString());
        serviceContexts.put(new UnknownServiceContext(14, encapsOutputStream.toByteArray()));
    }
    
    @Override
    public CorbaMessageMediator createLocationForward(final CorbaMessageMediator corbaMessageMediator, final IOR ior, final ServiceContexts serviceContexts) {
        return this.createResponseHelper(corbaMessageMediator, MessageBase.createReply((ORB)corbaMessageMediator.getBroker(), corbaMessageMediator.getGIOPVersion(), corbaMessageMediator.getEncodingVersion(), corbaMessageMediator.getRequestId(), 3, this.getServiceContextsForReply(corbaMessageMediator, serviceContexts), ior), ior);
    }
    
    protected CorbaMessageMediator createResponseHelper(final CorbaMessageMediator corbaMessageMediator, final ServiceContexts serviceContexts) {
        return this.createResponseHelper(corbaMessageMediator, MessageBase.createReply((ORB)corbaMessageMediator.getBroker(), corbaMessageMediator.getGIOPVersion(), corbaMessageMediator.getEncodingVersion(), corbaMessageMediator.getRequestId(), 0, serviceContexts, null), null);
    }
    
    protected CorbaMessageMediator createResponseHelper(final CorbaMessageMediator corbaMessageMediator, final ServiceContexts serviceContexts, final boolean b) {
        return this.createResponseHelper(corbaMessageMediator, MessageBase.createReply((ORB)corbaMessageMediator.getBroker(), corbaMessageMediator.getGIOPVersion(), corbaMessageMediator.getEncodingVersion(), corbaMessageMediator.getRequestId(), b ? 1 : 2, serviceContexts, null), null);
    }
    
    protected CorbaMessageMediator createResponseHelper(final CorbaMessageMediator messageMediator, final ReplyMessage replyHeader, final IOR ior) {
        this.runServantPostInvoke(messageMediator);
        this.runInterceptors(messageMediator, replyHeader);
        this.runRemoveThreadInfo(messageMediator);
        if (((ORB)messageMediator.getBroker()).subcontractDebugFlag) {
            this.dprint(".createResponseHelper: " + this.opAndId(messageMediator) + ": " + replyHeader);
        }
        messageMediator.setReplyHeader(replyHeader);
        OutputObject outputObject;
        if (messageMediator.getConnection() == null) {
            outputObject = OutputStreamFactory.newCDROutputObject(this.orb, messageMediator, messageMediator.getReplyHeader(), messageMediator.getStreamFormatVersion(), 0);
        }
        else {
            outputObject = messageMediator.getConnection().getAcceptor().createOutputObject(messageMediator.getBroker(), messageMediator);
        }
        messageMediator.setOutputObject(outputObject);
        messageMediator.getOutputObject().setMessageMediator(messageMediator);
        replyHeader.write((OutputStream)messageMediator.getOutputObject());
        if (replyHeader.getIOR() != null) {
            replyHeader.getIOR().write((org.omg.CORBA_2_3.portable.OutputStream)messageMediator.getOutputObject());
        }
        return messageMediator;
    }
    
    protected void runServantPostInvoke(final CorbaMessageMediator corbaMessageMediator) {
        if (corbaMessageMediator.executeReturnServantInResponseConstructor()) {
            corbaMessageMediator.setExecuteReturnServantInResponseConstructor(false);
            corbaMessageMediator.setExecuteRemoveThreadInfoInResponseConstructor(true);
            try {
                final ObjectAdapter oa = ((ORB)corbaMessageMediator.getBroker()).peekInvocationInfo().oa();
                try {
                    oa.returnServant();
                }
                catch (final Throwable t) {
                    this.wrapper.unexpectedException(t);
                    if (t instanceof Error) {
                        throw (Error)t;
                    }
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException)t;
                    }
                }
                finally {
                    oa.exit();
                }
            }
            catch (final EmptyStackException ex) {
                throw this.wrapper.emptyStackRunServantPostInvoke(ex);
            }
        }
    }
    
    protected void runInterceptors(final CorbaMessageMediator corbaMessageMediator, final ReplyMessage replyMessage) {
        if (corbaMessageMediator.executePIInResponseConstructor()) {
            ((ORB)corbaMessageMediator.getBroker()).getPIHandler().invokeServerPIEndingPoint(replyMessage);
            ((ORB)corbaMessageMediator.getBroker()).getPIHandler().cleanupServerPIRequest();
            corbaMessageMediator.setExecutePIInResponseConstructor(false);
        }
    }
    
    protected void runRemoveThreadInfo(final CorbaMessageMediator corbaMessageMediator) {
        if (corbaMessageMediator.executeRemoveThreadInfoInResponseConstructor()) {
            corbaMessageMediator.setExecuteRemoveThreadInfoInResponseConstructor(false);
            ((ORB)corbaMessageMediator.getBroker()).popInvocationInfo();
        }
    }
    
    protected ServiceContexts getServiceContextsForReply(final CorbaMessageMediator corbaMessageMediator, ServiceContexts serviceContexts) {
        final CorbaConnection corbaConnection = (CorbaConnection)corbaMessageMediator.getConnection();
        if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag) {
            this.dprint(".getServiceContextsForReply: " + this.opAndId(corbaMessageMediator) + ": " + corbaConnection);
        }
        if (serviceContexts == null) {
            serviceContexts = new ServiceContexts((ORB)corbaMessageMediator.getBroker());
        }
        if (corbaConnection != null && !corbaConnection.isPostInitialContexts()) {
            corbaConnection.setPostInitialContexts();
            final SendingContextServiceContext sendingContextServiceContext = new SendingContextServiceContext(((ORB)corbaMessageMediator.getBroker()).getFVDCodeBaseIOR());
            if (serviceContexts.get(sendingContextServiceContext.getId()) != null) {
                throw this.wrapper.duplicateSendingContextServiceContext();
            }
            serviceContexts.put(sendingContextServiceContext);
            if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag) {
                this.dprint(".getServiceContextsForReply: " + this.opAndId(corbaMessageMediator) + ": added SendingContextServiceContext");
            }
        }
        final ORBVersionServiceContext orbVersionServiceContext = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
        if (serviceContexts.get(orbVersionServiceContext.getId()) != null) {
            throw this.wrapper.duplicateOrbVersionServiceContext();
        }
        serviceContexts.put(orbVersionServiceContext);
        if (((ORB)corbaMessageMediator.getBroker()).subcontractDebugFlag) {
            this.dprint(".getServiceContextsForReply: " + this.opAndId(corbaMessageMediator) + ": added ORB version service context");
        }
        return serviceContexts;
    }
    
    private void releaseByteBufferToPool() {
        if (this.dispatchByteBuffer != null) {
            this.orb.getByteBufferPool().releaseByteBuffer(this.dispatchByteBuffer);
            if (this.transportDebug()) {
                final int identityHashCode = System.identityHashCode(this.dispatchByteBuffer);
                final StringBuffer sb = new StringBuffer();
                sb.append(".handleInput: releasing ByteBuffer (" + identityHashCode + ") to ByteBufferPool");
                this.dprint(sb.toString());
            }
        }
    }
}
