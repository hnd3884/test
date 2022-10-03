package com.sun.corba.se.impl.interceptors;

import java.util.Stack;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Policy;
import org.omg.CORBA.BAD_PARAM;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfo;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import org.omg.CORBA.Request;
import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.protocol.RetryType;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import java.util.HashMap;
import org.omg.IOP.CodecFactory;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;

public class PIHandlerImpl implements PIHandler
{
    boolean printPushPopEnabled;
    int pushLevel;
    private ORB orb;
    InterceptorsSystemException wrapper;
    ORBUtilSystemException orbutilWrapper;
    OMGSystemException omgWrapper;
    private int serverRequestIdCounter;
    CodecFactory codecFactory;
    String[] arguments;
    private InterceptorList interceptorList;
    private boolean hasIORInterceptors;
    private boolean hasClientInterceptors;
    private boolean hasServerInterceptors;
    private InterceptorInvoker interceptorInvoker;
    private PICurrent current;
    private HashMap policyFactoryTable;
    private static final short[] REPLY_MESSAGE_TO_PI_REPLY_STATUS;
    private ThreadLocal threadLocalClientRequestInfoStack;
    private ThreadLocal threadLocalServerRequestInfoStack;
    
    private void printPush() {
        if (!this.printPushPopEnabled) {
            return;
        }
        this.printSpaces(this.pushLevel);
        ++this.pushLevel;
        System.out.println("PUSH");
    }
    
    private void printPop() {
        if (!this.printPushPopEnabled) {
            return;
        }
        this.printSpaces(--this.pushLevel);
        System.out.println("POP");
    }
    
    private void printSpaces(final int n) {
        for (int i = 0; i < n; ++i) {
            System.out.print(" ");
        }
    }
    
    @Override
    public void close() {
        this.orb = null;
        this.wrapper = null;
        this.orbutilWrapper = null;
        this.omgWrapper = null;
        this.codecFactory = null;
        this.arguments = null;
        this.interceptorList = null;
        this.interceptorInvoker = null;
        this.current = null;
        this.policyFactoryTable = null;
        this.threadLocalClientRequestInfoStack = null;
        this.threadLocalServerRequestInfoStack = null;
    }
    
    public PIHandlerImpl(final ORB orb, final String[] arguments) {
        this.printPushPopEnabled = false;
        this.pushLevel = 0;
        this.serverRequestIdCounter = 0;
        this.codecFactory = null;
        this.arguments = null;
        this.threadLocalClientRequestInfoStack = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new RequestInfoStack();
            }
        };
        this.threadLocalServerRequestInfoStack = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new RequestInfoStack();
            }
        };
        this.orb = orb;
        this.wrapper = InterceptorsSystemException.get(orb, "rpc.protocol");
        this.orbutilWrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.omgWrapper = OMGSystemException.get(orb, "rpc.protocol");
        this.arguments = arguments;
        this.codecFactory = new CodecFactoryImpl(orb);
        this.interceptorList = new InterceptorList(this.wrapper);
        this.current = new PICurrent(orb);
        this.interceptorInvoker = new InterceptorInvoker(orb, this.interceptorList, this.current);
        orb.getLocalResolver().register("PICurrent", ClosureFactory.makeConstant(this.current));
        orb.getLocalResolver().register("CodecFactory", ClosureFactory.makeConstant(this.codecFactory));
    }
    
    @Override
    public void initialize() {
        if (this.orb.getORBData().getORBInitializers() != null) {
            final ORBInitInfoImpl orbInitInfo = this.createORBInitInfo();
            this.current.setORBInitializing(true);
            this.preInitORBInitializers(orbInitInfo);
            this.postInitORBInitializers(orbInitInfo);
            this.interceptorList.sortInterceptors();
            this.current.setORBInitializing(false);
            orbInitInfo.setStage(2);
            this.hasIORInterceptors = this.interceptorList.hasInterceptorsOfType(2);
            this.hasClientInterceptors = true;
            this.hasServerInterceptors = this.interceptorList.hasInterceptorsOfType(1);
            this.interceptorInvoker.setEnabled(true);
        }
    }
    
    @Override
    public void destroyInterceptors() {
        this.interceptorList.destroyAll();
    }
    
    @Override
    public void objectAdapterCreated(final ObjectAdapter objectAdapter) {
        if (!this.hasIORInterceptors) {
            return;
        }
        this.interceptorInvoker.objectAdapterCreated(objectAdapter);
    }
    
    @Override
    public void adapterManagerStateChanged(final int n, final short n2) {
        if (!this.hasIORInterceptors) {
            return;
        }
        this.interceptorInvoker.adapterManagerStateChanged(n, n2);
    }
    
    @Override
    public void adapterStateChanged(final ObjectReferenceTemplate[] array, final short n) {
        if (!this.hasIORInterceptors) {
            return;
        }
        this.interceptorInvoker.adapterStateChanged(array, n);
    }
    
    @Override
    public void disableInterceptorsThisThread() {
        if (!this.hasClientInterceptors) {
            return;
        }
        final RequestInfoStack requestInfoStack = this.threadLocalClientRequestInfoStack.get();
        ++requestInfoStack.disableCount;
    }
    
    @Override
    public void enableInterceptorsThisThread() {
        if (!this.hasClientInterceptors) {
            return;
        }
        final RequestInfoStack requestInfoStack = this.threadLocalClientRequestInfoStack.get();
        --requestInfoStack.disableCount;
    }
    
    @Override
    public void invokeClientPIStartingPoint() throws RemarshalException {
        if (!this.hasClientInterceptors) {
            return;
        }
        if (!this.isClientPIEnabledForThisThread()) {
            return;
        }
        final ClientRequestInfoImpl peekClientRequestInfoImplStack = this.peekClientRequestInfoImplStack();
        this.interceptorInvoker.invokeClientInterceptorStartingPoint(peekClientRequestInfoImplStack);
        final short replyStatus = peekClientRequestInfoImplStack.getReplyStatus();
        if (replyStatus == 1 || replyStatus == 3) {
            final Exception invokeClientPIEndingPoint = this.invokeClientPIEndingPoint(this.convertPIReplyStatusToReplyMessage(replyStatus), peekClientRequestInfoImplStack.getException());
            if (invokeClientPIEndingPoint == null) {}
            if (invokeClientPIEndingPoint instanceof SystemException) {
                throw (SystemException)invokeClientPIEndingPoint;
            }
            if (invokeClientPIEndingPoint instanceof RemarshalException) {
                throw (RemarshalException)invokeClientPIEndingPoint;
            }
            if (invokeClientPIEndingPoint instanceof UserException || invokeClientPIEndingPoint instanceof ApplicationException) {
                throw this.wrapper.exceptionInvalid();
            }
        }
        else if (replyStatus != -1) {
            throw this.wrapper.replyStatusNotInit();
        }
    }
    
    @Override
    public Exception makeCompletedClientRequest(final int n, final Exception ex) {
        return this.handleClientPIEndingPoint(n, ex, false);
    }
    
    @Override
    public Exception invokeClientPIEndingPoint(final int n, final Exception ex) {
        return this.handleClientPIEndingPoint(n, ex, true);
    }
    
    public Exception handleClientPIEndingPoint(final int n, Exception exception, final boolean b) {
        if (!this.hasClientInterceptors) {
            return exception;
        }
        if (!this.isClientPIEnabledForThisThread()) {
            return exception;
        }
        short replyStatus = PIHandlerImpl.REPLY_MESSAGE_TO_PI_REPLY_STATUS[n];
        final ClientRequestInfoImpl peekClientRequestInfoImplStack = this.peekClientRequestInfoImplStack();
        peekClientRequestInfoImplStack.setReplyStatus(replyStatus);
        peekClientRequestInfoImplStack.setException(exception);
        if (b) {
            this.interceptorInvoker.invokeClientInterceptorEndingPoint(peekClientRequestInfoImplStack);
            replyStatus = peekClientRequestInfoImplStack.getReplyStatus();
        }
        if (replyStatus == 3 || replyStatus == 4) {
            peekClientRequestInfoImplStack.reset();
            if (b) {
                peekClientRequestInfoImplStack.setRetryRequest(RetryType.AFTER_RESPONSE);
            }
            else {
                peekClientRequestInfoImplStack.setRetryRequest(RetryType.BEFORE_RESPONSE);
            }
            exception = new RemarshalException();
        }
        else if (replyStatus == 1 || replyStatus == 2) {
            exception = peekClientRequestInfoImplStack.getException();
        }
        return exception;
    }
    
    @Override
    public void initiateClientPIRequest(final boolean b) {
        if (!this.hasClientInterceptors) {
            return;
        }
        if (!this.isClientPIEnabledForThisThread()) {
            return;
        }
        final RequestInfoStack requestInfoStack = this.threadLocalClientRequestInfoStack.get();
        ClientRequestInfoImpl clientRequestInfoImpl = null;
        if (!requestInfoStack.empty()) {
            clientRequestInfoImpl = requestInfoStack.peek();
        }
        if (!b && clientRequestInfoImpl != null && clientRequestInfoImpl.isDIIInitiate()) {
            clientRequestInfoImpl.setDIIInitiate(false);
        }
        else {
            if (clientRequestInfoImpl == null || !clientRequestInfoImpl.getRetryRequest().isRetry()) {
                clientRequestInfoImpl = new ClientRequestInfoImpl(this.orb);
                requestInfoStack.push(clientRequestInfoImpl);
                this.printPush();
            }
            clientRequestInfoImpl.setRetryRequest(RetryType.NONE);
            clientRequestInfoImpl.incrementEntryCount();
            clientRequestInfoImpl.setReplyStatus((short)(-1));
            if (b) {
                clientRequestInfoImpl.setDIIInitiate(true);
            }
        }
    }
    
    @Override
    public void cleanupClientPIRequest() {
        if (!this.hasClientInterceptors) {
            return;
        }
        if (!this.isClientPIEnabledForThisThread()) {
            return;
        }
        final ClientRequestInfoImpl peekClientRequestInfoImplStack = this.peekClientRequestInfoImplStack();
        if (!peekClientRequestInfoImplStack.getRetryRequest().equals(RetryType.BEFORE_RESPONSE) && peekClientRequestInfoImplStack.getReplyStatus() == -1) {
            this.invokeClientPIEndingPoint(2, this.wrapper.unknownRequestInvoke(CompletionStatus.COMPLETED_MAYBE));
        }
        peekClientRequestInfoImplStack.decrementEntryCount();
        if (peekClientRequestInfoImplStack.getEntryCount() == 0 && !peekClientRequestInfoImplStack.getRetryRequest().isRetry()) {
            this.threadLocalClientRequestInfoStack.get().pop();
            this.printPop();
        }
    }
    
    @Override
    public void setClientPIInfo(final CorbaMessageMediator info) {
        if (!this.hasClientInterceptors) {
            return;
        }
        if (!this.isClientPIEnabledForThisThread()) {
            return;
        }
        this.peekClientRequestInfoImplStack().setInfo(info);
    }
    
    @Override
    public void setClientPIInfo(final RequestImpl diiRequest) {
        if (!this.hasClientInterceptors) {
            return;
        }
        if (!this.isClientPIEnabledForThisThread()) {
            return;
        }
        this.peekClientRequestInfoImplStack().setDIIRequest(diiRequest);
    }
    
    @Override
    public void invokeServerPIStartingPoint() {
        if (!this.hasServerInterceptors) {
            return;
        }
        final ServerRequestInfoImpl peekServerRequestInfoImplStack = this.peekServerRequestInfoImplStack();
        this.interceptorInvoker.invokeServerInterceptorStartingPoint(peekServerRequestInfoImplStack);
        this.serverPIHandleExceptions(peekServerRequestInfoImplStack);
    }
    
    @Override
    public void invokeServerPIIntermediatePoint() {
        if (!this.hasServerInterceptors) {
            return;
        }
        final ServerRequestInfoImpl peekServerRequestInfoImplStack = this.peekServerRequestInfoImplStack();
        this.interceptorInvoker.invokeServerInterceptorIntermediatePoint(peekServerRequestInfoImplStack);
        peekServerRequestInfoImplStack.releaseServant();
        this.serverPIHandleExceptions(peekServerRequestInfoImplStack);
    }
    
    @Override
    public void invokeServerPIEndingPoint(final ReplyMessage replyMessage) {
        if (!this.hasServerInterceptors) {
            return;
        }
        final ServerRequestInfoImpl peekServerRequestInfoImplStack = this.peekServerRequestInfoImplStack();
        peekServerRequestInfoImplStack.setReplyMessage(replyMessage);
        peekServerRequestInfoImplStack.setCurrentExecutionPoint(2);
        if (!peekServerRequestInfoImplStack.getAlreadyExecuted()) {
            final short replyStatus = PIHandlerImpl.REPLY_MESSAGE_TO_PI_REPLY_STATUS[replyMessage.getReplyStatus()];
            if (replyStatus == 3 || replyStatus == 4) {
                peekServerRequestInfoImplStack.setForwardRequest(replyMessage.getIOR());
            }
            final Exception exception = peekServerRequestInfoImplStack.getException();
            if (!peekServerRequestInfoImplStack.isDynamic() && replyStatus == 2) {
                peekServerRequestInfoImplStack.setException(this.omgWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE));
            }
            peekServerRequestInfoImplStack.setReplyStatus(replyStatus);
            this.interceptorInvoker.invokeServerInterceptorEndingPoint(peekServerRequestInfoImplStack);
            final short replyStatus2 = peekServerRequestInfoImplStack.getReplyStatus();
            final Exception exception2 = peekServerRequestInfoImplStack.getException();
            if (replyStatus2 == 1 && exception2 != exception) {
                throw (SystemException)exception2;
            }
            if (replyStatus2 == 3) {
                if (replyStatus != 3) {
                    throw new ForwardException(this.orb, peekServerRequestInfoImplStack.getForwardRequestIOR());
                }
                if (peekServerRequestInfoImplStack.isForwardRequestRaisedInEnding()) {
                    replyMessage.setIOR(peekServerRequestInfoImplStack.getForwardRequestIOR());
                }
            }
        }
    }
    
    @Override
    public void setServerPIInfo(final Exception exception) {
        if (!this.hasServerInterceptors) {
            return;
        }
        this.peekServerRequestInfoImplStack().setException(exception);
    }
    
    @Override
    public void setServerPIInfo(final NVList dsiArguments) {
        if (!this.hasServerInterceptors) {
            return;
        }
        this.peekServerRequestInfoImplStack().setDSIArguments(dsiArguments);
    }
    
    @Override
    public void setServerPIExceptionInfo(final Any dsiException) {
        if (!this.hasServerInterceptors) {
            return;
        }
        this.peekServerRequestInfoImplStack().setDSIException(dsiException);
    }
    
    @Override
    public void setServerPIInfo(final Any dsiResult) {
        if (!this.hasServerInterceptors) {
            return;
        }
        this.peekServerRequestInfoImplStack().setDSIResult(dsiResult);
    }
    
    @Override
    public void initializeServerPIInfo(final CorbaMessageMediator corbaMessageMediator, final ObjectAdapter objectAdapter, final byte[] array, final ObjectKeyTemplate objectKeyTemplate) {
        if (!this.hasServerInterceptors) {
            return;
        }
        final RequestInfoStack requestInfoStack = this.threadLocalServerRequestInfoStack.get();
        final ServerRequestInfoImpl serverRequestInfoImpl = new ServerRequestInfoImpl(this.orb);
        requestInfoStack.push(serverRequestInfoImpl);
        this.printPush();
        corbaMessageMediator.setExecutePIInResponseConstructor(true);
        serverRequestInfoImpl.setInfo(corbaMessageMediator, objectAdapter, array, objectKeyTemplate);
    }
    
    @Override
    public void setServerPIInfo(final Object o, final String s) {
        if (!this.hasServerInterceptors) {
            return;
        }
        this.peekServerRequestInfoImplStack().setInfo(o, s);
    }
    
    @Override
    public void cleanupServerPIRequest() {
        if (!this.hasServerInterceptors) {
            return;
        }
        this.threadLocalServerRequestInfoStack.get().pop();
        this.printPop();
    }
    
    private void serverPIHandleExceptions(final ServerRequestInfoImpl serverRequestInfoImpl) {
        final int endingPointCall = serverRequestInfoImpl.getEndingPointCall();
        if (endingPointCall == 1) {
            throw (SystemException)serverRequestInfoImpl.getException();
        }
        if (endingPointCall == 2 && serverRequestInfoImpl.getForwardRequestException() != null) {
            throw new ForwardException(this.orb, serverRequestInfoImpl.getForwardRequestIOR());
        }
    }
    
    private int convertPIReplyStatusToReplyMessage(final short n) {
        int n2 = 0;
        for (int i = 0; i < PIHandlerImpl.REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; ++i) {
            if (PIHandlerImpl.REPLY_MESSAGE_TO_PI_REPLY_STATUS[i] == n) {
                n2 = i;
                break;
            }
        }
        return n2;
    }
    
    private ClientRequestInfoImpl peekClientRequestInfoImplStack() {
        final RequestInfoStack requestInfoStack = this.threadLocalClientRequestInfoStack.get();
        if (!requestInfoStack.empty()) {
            return (ClientRequestInfoImpl)requestInfoStack.peek();
        }
        throw this.wrapper.clientInfoStackNull();
    }
    
    private ServerRequestInfoImpl peekServerRequestInfoImplStack() {
        final RequestInfoStack requestInfoStack = this.threadLocalServerRequestInfoStack.get();
        if (!requestInfoStack.empty()) {
            return (ServerRequestInfoImpl)requestInfoStack.peek();
        }
        throw this.wrapper.serverInfoStackNull();
    }
    
    private boolean isClientPIEnabledForThisThread() {
        return this.threadLocalClientRequestInfoStack.get().disableCount == 0;
    }
    
    private void preInitORBInitializers(final ORBInitInfoImpl orbInitInfoImpl) {
        orbInitInfoImpl.setStage(0);
        for (int i = 0; i < this.orb.getORBData().getORBInitializers().length; ++i) {
            final ORBInitializer orbInitializer = this.orb.getORBData().getORBInitializers()[i];
            if (orbInitializer != null) {
                try {
                    orbInitializer.pre_init(orbInitInfoImpl);
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private void postInitORBInitializers(final ORBInitInfoImpl orbInitInfoImpl) {
        orbInitInfoImpl.setStage(1);
        for (int i = 0; i < this.orb.getORBData().getORBInitializers().length; ++i) {
            final ORBInitializer orbInitializer = this.orb.getORBData().getORBInitializers()[i];
            if (orbInitializer != null) {
                try {
                    orbInitializer.post_init(orbInitInfoImpl);
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private ORBInitInfoImpl createORBInitInfo() {
        return new ORBInitInfoImpl(this.orb, this.arguments, this.orb.getORBData().getORBId(), this.codecFactory);
    }
    
    @Override
    public void register_interceptor(final Interceptor interceptor, final int n) throws DuplicateName {
        if (n >= 3 || n < 0) {
            throw this.wrapper.typeOutOfRange(new Integer(n));
        }
        if (interceptor.name() == null) {
            throw this.wrapper.nameNull();
        }
        this.interceptorList.register_interceptor(interceptor, n);
    }
    
    @Override
    public Current getPICurrent() {
        return this.current;
    }
    
    private void nullParam() throws BAD_PARAM {
        throw this.orbutilWrapper.nullParam();
    }
    
    @Override
    public Policy create_policy(final int n, final Any any) throws PolicyError {
        if (any == null) {
            this.nullParam();
        }
        if (this.policyFactoryTable == null) {
            throw new PolicyError("There is no PolicyFactory Registered for type " + n, (short)0);
        }
        final PolicyFactory policyFactory = this.policyFactoryTable.get(new Integer(n));
        if (policyFactory == null) {
            throw new PolicyError(" Could Not Find PolicyFactory for the Type " + n, (short)0);
        }
        return policyFactory.create_policy(n, any);
    }
    
    @Override
    public void registerPolicyFactory(final int n, final PolicyFactory policyFactory) {
        if (this.policyFactoryTable == null) {
            this.policyFactoryTable = new HashMap();
        }
        final Integer n2 = new Integer(n);
        if (this.policyFactoryTable.get(n2) == null) {
            this.policyFactoryTable.put(n2, policyFactory);
            return;
        }
        throw this.omgWrapper.policyFactoryRegFailed(new Integer(n));
    }
    
    @Override
    public synchronized int allocateServerRequestId() {
        return this.serverRequestIdCounter++;
    }
    
    static {
        REPLY_MESSAGE_TO_PI_REPLY_STATUS = new short[] { 0, 2, 1, 3, 3, 4 };
    }
    
    private final class RequestInfoStack extends Stack
    {
        public int disableCount;
        
        private RequestInfoStack() {
            this.disableCount = 0;
        }
    }
}
