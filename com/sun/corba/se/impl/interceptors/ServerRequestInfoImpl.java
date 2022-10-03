package com.sun.corba.se.impl.interceptors;

import org.omg.PortableServer.DynamicImplementation;
import com.sun.corba.se.spi.legacy.connection.Connection;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.IOP.ServiceContext;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.PortableServer.Servant;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.CORBA.Policy;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import org.omg.Dynamic.Parameter;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import java.util.ArrayList;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.PortableInterceptor.ServerRequestInfo;

public final class ServerRequestInfoImpl extends RequestInfoImpl implements ServerRequestInfo
{
    static final int CALL_RECEIVE_REQUEST_SERVICE_CONTEXT = 0;
    static final int CALL_RECEIVE_REQUEST = 0;
    static final int CALL_INTERMEDIATE_NONE = 1;
    static final int CALL_SEND_REPLY = 0;
    static final int CALL_SEND_EXCEPTION = 1;
    static final int CALL_SEND_OTHER = 2;
    private boolean forwardRequestRaisedInEnding;
    private CorbaMessageMediator request;
    private Object servant;
    private byte[] objectId;
    private ObjectKeyTemplate oktemp;
    private byte[] adapterId;
    private String[] adapterName;
    private ArrayList addReplyServiceContextQueue;
    private ReplyMessage replyMessage;
    private String targetMostDerivedInterface;
    private NVList dsiArguments;
    private Any dsiResult;
    private Any dsiException;
    private boolean isDynamic;
    private ObjectAdapter objectAdapter;
    private int serverRequestId;
    private Parameter[] cachedArguments;
    private Any cachedSendingException;
    private HashMap cachedRequestServiceContexts;
    private HashMap cachedReplyServiceContexts;
    protected static final int MID_SENDING_EXCEPTION = 14;
    protected static final int MID_OBJECT_ID = 15;
    protected static final int MID_ADAPTER_ID = 16;
    protected static final int MID_TARGET_MOST_DERIVED_INTERFACE = 17;
    protected static final int MID_GET_SERVER_POLICY = 18;
    protected static final int MID_SET_SLOT = 19;
    protected static final int MID_TARGET_IS_A = 20;
    protected static final int MID_ADD_REPLY_SERVICE_CONTEXT = 21;
    protected static final int MID_SERVER_ID = 22;
    protected static final int MID_ORB_ID = 23;
    protected static final int MID_ADAPTER_NAME = 24;
    private static final boolean[][] validCall;
    
    @Override
    void reset() {
        super.reset();
        this.forwardRequestRaisedInEnding = false;
        this.request = null;
        this.servant = null;
        this.objectId = null;
        this.oktemp = null;
        this.adapterId = null;
        this.adapterName = null;
        this.addReplyServiceContextQueue = null;
        this.replyMessage = null;
        this.targetMostDerivedInterface = null;
        this.dsiArguments = null;
        this.dsiResult = null;
        this.dsiException = null;
        this.isDynamic = false;
        this.objectAdapter = null;
        this.serverRequestId = this.myORB.getPIHandler().allocateServerRequestId();
        this.cachedArguments = null;
        this.cachedSendingException = null;
        this.cachedRequestServiceContexts = null;
        this.cachedReplyServiceContexts = null;
        this.startingPointCall = 0;
        this.intermediatePointCall = 0;
        this.endingPointCall = 0;
    }
    
    ServerRequestInfoImpl(final ORB orb) {
        super(orb);
        this.startingPointCall = 0;
        this.intermediatePointCall = 0;
        this.endingPointCall = 0;
        this.serverRequestId = orb.getPIHandler().allocateServerRequestId();
    }
    
    @Override
    public Any sending_exception() {
        this.checkAccess(14);
        if (this.cachedSendingException == null) {
            Any cachedSendingException;
            if (this.dsiException != null) {
                cachedSendingException = this.dsiException;
            }
            else {
                if (this.exception == null) {
                    throw this.wrapper.exceptionUnavailable();
                }
                cachedSendingException = this.exceptionToAny(this.exception);
            }
            this.cachedSendingException = cachedSendingException;
        }
        return this.cachedSendingException;
    }
    
    @Override
    public byte[] object_id() {
        this.checkAccess(15);
        if (this.objectId == null) {
            throw this.stdWrapper.piOperationNotSupported6();
        }
        return this.objectId;
    }
    
    private void checkForNullTemplate() {
        if (this.oktemp == null) {
            throw this.stdWrapper.piOperationNotSupported7();
        }
    }
    
    @Override
    public String server_id() {
        this.checkAccess(22);
        this.checkForNullTemplate();
        return Integer.toString(this.oktemp.getServerId());
    }
    
    @Override
    public String orb_id() {
        this.checkAccess(23);
        return this.myORB.getORBData().getORBId();
    }
    
    @Override
    public synchronized String[] adapter_name() {
        this.checkAccess(24);
        if (this.adapterName == null) {
            this.checkForNullTemplate();
            this.adapterName = this.oktemp.getObjectAdapterId().getAdapterName();
        }
        return this.adapterName;
    }
    
    @Override
    public synchronized byte[] adapter_id() {
        this.checkAccess(16);
        if (this.adapterId == null) {
            this.checkForNullTemplate();
            this.adapterId = this.oktemp.getAdapterId();
        }
        return this.adapterId;
    }
    
    @Override
    public String target_most_derived_interface() {
        this.checkAccess(17);
        return this.targetMostDerivedInterface;
    }
    
    @Override
    public Policy get_server_policy(final int n) {
        Policy effectivePolicy = null;
        if (this.objectAdapter != null) {
            effectivePolicy = this.objectAdapter.getEffectivePolicy(n);
        }
        return effectivePolicy;
    }
    
    @Override
    public void set_slot(final int n, final Any any) throws InvalidSlot {
        this.slotTable.set_slot(n, any);
    }
    
    @Override
    public boolean target_is_a(final String s) {
        this.checkAccess(20);
        boolean b;
        if (this.servant instanceof Servant) {
            b = ((Servant)this.servant)._is_a(s);
        }
        else {
            if (!StubAdapter.isStub(this.servant)) {
                throw this.wrapper.servantInvalid();
            }
            b = ((org.omg.CORBA.Object)this.servant)._is_a(s);
        }
        return b;
    }
    
    @Override
    public void add_reply_service_context(final ServiceContext service_context, final boolean replace) {
        if (this.currentExecutionPoint == 2) {
            ServiceContexts serviceContexts = this.replyMessage.getServiceContexts();
            if (serviceContexts == null) {
                serviceContexts = new ServiceContexts(this.myORB);
                this.replyMessage.setServiceContexts(serviceContexts);
            }
            if (this.cachedReplyServiceContexts == null) {
                this.cachedReplyServiceContexts = new HashMap();
            }
            this.addServiceContext(this.cachedReplyServiceContexts, serviceContexts, service_context, replace);
        }
        final AddReplyServiceContextCommand addReplyServiceContextCommand = new AddReplyServiceContextCommand();
        addReplyServiceContextCommand.service_context = service_context;
        addReplyServiceContextCommand.replace = replace;
        if (this.addReplyServiceContextQueue == null) {
            this.addReplyServiceContextQueue = new ArrayList();
        }
        this.enqueue(addReplyServiceContextCommand);
    }
    
    @Override
    public int request_id() {
        return this.serverRequestId;
    }
    
    @Override
    public String operation() {
        return this.request.getOperationName();
    }
    
    @Override
    public Parameter[] arguments() {
        this.checkAccess(2);
        if (this.cachedArguments == null) {
            if (!this.isDynamic) {
                throw this.stdWrapper.piOperationNotSupported1();
            }
            if (this.dsiArguments == null) {
                throw this.stdWrapper.piOperationNotSupported8();
            }
            this.cachedArguments = this.nvListToParameterArray(this.dsiArguments);
        }
        return this.cachedArguments;
    }
    
    @Override
    public TypeCode[] exceptions() {
        this.checkAccess(3);
        throw this.stdWrapper.piOperationNotSupported2();
    }
    
    @Override
    public String[] contexts() {
        this.checkAccess(4);
        throw this.stdWrapper.piOperationNotSupported3();
    }
    
    @Override
    public String[] operation_context() {
        this.checkAccess(5);
        throw this.stdWrapper.piOperationNotSupported4();
    }
    
    @Override
    public Any result() {
        this.checkAccess(6);
        if (!this.isDynamic) {
            throw this.stdWrapper.piOperationNotSupported5();
        }
        if (this.dsiResult == null) {
            throw this.wrapper.piDsiResultIsNull();
        }
        return this.dsiResult;
    }
    
    @Override
    public boolean response_expected() {
        return !this.request.isOneWay();
    }
    
    @Override
    public org.omg.CORBA.Object forward_reference() {
        this.checkAccess(10);
        if (this.replyStatus != 3) {
            throw this.stdWrapper.invalidPiCall1();
        }
        return this.getForwardRequestException().forward;
    }
    
    @Override
    public ServiceContext get_request_service_context(final int n) {
        this.checkAccess(12);
        if (this.cachedRequestServiceContexts == null) {
            this.cachedRequestServiceContexts = new HashMap();
        }
        return this.getServiceContext(this.cachedRequestServiceContexts, this.request.getRequestServiceContexts(), n);
    }
    
    @Override
    public ServiceContext get_reply_service_context(final int n) {
        this.checkAccess(13);
        if (this.cachedReplyServiceContexts == null) {
            this.cachedReplyServiceContexts = new HashMap();
        }
        return this.getServiceContext(this.cachedReplyServiceContexts, this.replyMessage.getServiceContexts(), n);
    }
    
    private void enqueue(final AddReplyServiceContextCommand addReplyServiceContextCommand) {
        final int size = this.addReplyServiceContextQueue.size();
        boolean b = false;
        int i = 0;
        while (i < size) {
            final AddReplyServiceContextCommand addReplyServiceContextCommand2 = this.addReplyServiceContextQueue.get(i);
            if (addReplyServiceContextCommand2.service_context.context_id == addReplyServiceContextCommand.service_context.context_id) {
                b = true;
                if (addReplyServiceContextCommand.replace) {
                    this.addReplyServiceContextQueue.set(i, addReplyServiceContextCommand);
                    break;
                }
                throw this.stdWrapper.serviceContextAddFailed(new Integer(addReplyServiceContextCommand2.service_context.context_id));
            }
            else {
                ++i;
            }
        }
        if (!b) {
            this.addReplyServiceContextQueue.add(addReplyServiceContextCommand);
        }
    }
    
    @Override
    protected void setCurrentExecutionPoint(final int currentExecutionPoint) {
        super.setCurrentExecutionPoint(currentExecutionPoint);
        if (currentExecutionPoint == 2 && this.addReplyServiceContextQueue != null) {
            for (int size = this.addReplyServiceContextQueue.size(), i = 0; i < size; ++i) {
                final AddReplyServiceContextCommand addReplyServiceContextCommand = this.addReplyServiceContextQueue.get(i);
                try {
                    this.add_reply_service_context(addReplyServiceContextCommand.service_context, addReplyServiceContextCommand.replace);
                }
                catch (final BAD_INV_ORDER bad_INV_ORDER) {}
            }
        }
    }
    
    protected void setInfo(final CorbaMessageMediator request, final ObjectAdapter objectAdapter, final byte[] objectId, final ObjectKeyTemplate oktemp) {
        this.request = request;
        this.objectId = objectId;
        this.oktemp = oktemp;
        this.objectAdapter = objectAdapter;
        this.connection = (Connection)request.getConnection();
    }
    
    protected void setDSIArguments(final NVList dsiArguments) {
        this.dsiArguments = dsiArguments;
    }
    
    protected void setDSIException(final Any dsiException) {
        this.dsiException = dsiException;
        this.cachedSendingException = null;
    }
    
    protected void setDSIResult(final Any dsiResult) {
        this.dsiResult = dsiResult;
    }
    
    @Override
    protected void setException(final Exception exception) {
        super.setException(exception);
        this.dsiException = null;
        this.cachedSendingException = null;
    }
    
    protected void setInfo(final Object servant, final String targetMostDerivedInterface) {
        this.servant = servant;
        this.targetMostDerivedInterface = targetMostDerivedInterface;
        this.isDynamic = (servant instanceof DynamicImplementation || servant instanceof org.omg.CORBA.DynamicImplementation);
    }
    
    void setReplyMessage(final ReplyMessage replyMessage) {
        this.replyMessage = replyMessage;
    }
    
    @Override
    protected void setReplyStatus(final short replyStatus) {
        super.setReplyStatus(replyStatus);
        switch (replyStatus) {
            case 0: {
                this.endingPointCall = 0;
                break;
            }
            case 1:
            case 2: {
                this.endingPointCall = 1;
                break;
            }
            case 3:
            case 4: {
                this.endingPointCall = 2;
                break;
            }
        }
    }
    
    void releaseServant() {
        this.servant = null;
    }
    
    void setForwardRequestRaisedInEnding() {
        this.forwardRequestRaisedInEnding = true;
    }
    
    boolean isForwardRequestRaisedInEnding() {
        return this.forwardRequestRaisedInEnding;
    }
    
    boolean isDynamic() {
        return this.isDynamic;
    }
    
    @Override
    protected void checkAccess(final int n) {
        int n2 = 0;
        Label_0084: {
            switch (this.currentExecutionPoint) {
                case 0: {
                    n2 = 0;
                    break;
                }
                case 1: {
                    n2 = 1;
                    break;
                }
                case 2: {
                    switch (this.endingPointCall) {
                        case 0: {
                            n2 = 2;
                            break Label_0084;
                        }
                        case 1: {
                            n2 = 3;
                            break Label_0084;
                        }
                        case 2: {
                            n2 = 4;
                            break Label_0084;
                        }
                    }
                    break;
                }
            }
        }
        if (!ServerRequestInfoImpl.validCall[n][n2]) {
            throw this.stdWrapper.invalidPiCall2();
        }
    }
    
    static {
        validCall = new boolean[][] { { true, true, true, true, true }, { true, true, true, true, true }, { false, true, true, false, false }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, true, false, false }, { false, false, true, false, false }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, true, true, true }, { false, false, false, false, true }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, true, true, true }, { false, false, false, true, false }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, false, false, false }, { true, true, true, true, true }, { true, true, true, true, true }, { false, true, false, false, false }, { true, true, true, true, true }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, true, true, true } };
    }
    
    private class AddReplyServiceContextCommand
    {
        ServiceContext service_context;
        boolean replace;
    }
}
