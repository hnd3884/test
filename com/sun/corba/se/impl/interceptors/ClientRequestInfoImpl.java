package com.sun.corba.se.impl.interceptors;

import org.omg.CORBA.BAD_INV_ORDER;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.IOP.ServiceContext;
import org.omg.CORBA.Policy;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import org.omg.IOP.TaggedComponent;
import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import org.omg.IOP.TaggedProfile;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.Request;
import com.sun.corba.se.spi.protocol.RetryType;
import org.omg.PortableInterceptor.ClientRequestInfo;

public final class ClientRequestInfoImpl extends RequestInfoImpl implements ClientRequestInfo
{
    static final int CALL_SEND_REQUEST = 0;
    static final int CALL_SEND_POLL = 1;
    static final int CALL_RECEIVE_REPLY = 0;
    static final int CALL_RECEIVE_EXCEPTION = 1;
    static final int CALL_RECEIVE_OTHER = 2;
    private RetryType retryRequest;
    private int entryCount;
    private Request request;
    private boolean diiInitiate;
    private CorbaMessageMediator messageMediator;
    private Object cachedTargetObject;
    private Object cachedEffectiveTargetObject;
    private Parameter[] cachedArguments;
    private TypeCode[] cachedExceptions;
    private String[] cachedContexts;
    private String[] cachedOperationContext;
    private String cachedReceivedExceptionId;
    private Any cachedResult;
    private Any cachedReceivedException;
    private TaggedProfile cachedEffectiveProfile;
    private HashMap cachedRequestServiceContexts;
    private HashMap cachedReplyServiceContexts;
    private HashMap cachedEffectiveComponents;
    protected boolean piCurrentPushed;
    protected static final int MID_TARGET = 14;
    protected static final int MID_EFFECTIVE_TARGET = 15;
    protected static final int MID_EFFECTIVE_PROFILE = 16;
    protected static final int MID_RECEIVED_EXCEPTION = 17;
    protected static final int MID_RECEIVED_EXCEPTION_ID = 18;
    protected static final int MID_GET_EFFECTIVE_COMPONENT = 19;
    protected static final int MID_GET_EFFECTIVE_COMPONENTS = 20;
    protected static final int MID_GET_REQUEST_POLICY = 21;
    protected static final int MID_ADD_REQUEST_SERVICE_CONTEXT = 22;
    private static final boolean[][] validCall;
    
    @Override
    void reset() {
        super.reset();
        this.retryRequest = RetryType.NONE;
        this.request = null;
        this.diiInitiate = false;
        this.messageMediator = null;
        this.cachedTargetObject = null;
        this.cachedEffectiveTargetObject = null;
        this.cachedArguments = null;
        this.cachedExceptions = null;
        this.cachedContexts = null;
        this.cachedOperationContext = null;
        this.cachedReceivedExceptionId = null;
        this.cachedResult = null;
        this.cachedReceivedException = null;
        this.cachedEffectiveProfile = null;
        this.cachedRequestServiceContexts = null;
        this.cachedReplyServiceContexts = null;
        this.cachedEffectiveComponents = null;
        this.piCurrentPushed = false;
        this.startingPointCall = 0;
        this.endingPointCall = 0;
    }
    
    protected ClientRequestInfoImpl(final ORB orb) {
        super(orb);
        this.entryCount = 0;
        this.startingPointCall = 0;
        this.endingPointCall = 0;
    }
    
    @Override
    public Object target() {
        if (this.cachedTargetObject == null) {
            this.cachedTargetObject = this.iorToObject(((CorbaContactInfo)this.messageMediator.getContactInfo()).getTargetIOR());
        }
        return this.cachedTargetObject;
    }
    
    @Override
    public Object effective_target() {
        if (this.cachedEffectiveTargetObject == null) {
            this.cachedEffectiveTargetObject = this.iorToObject(((CorbaContactInfo)this.messageMediator.getContactInfo()).getEffectiveTargetIOR());
        }
        return this.cachedEffectiveTargetObject;
    }
    
    @Override
    public TaggedProfile effective_profile() {
        if (this.cachedEffectiveProfile == null) {
            this.cachedEffectiveProfile = ((CorbaContactInfo)this.messageMediator.getContactInfo()).getEffectiveProfile().getIOPProfile();
        }
        return this.cachedEffectiveProfile;
    }
    
    @Override
    public Any received_exception() {
        this.checkAccess(17);
        if (this.cachedReceivedException == null) {
            this.cachedReceivedException = this.exceptionToAny(this.exception);
        }
        return this.cachedReceivedException;
    }
    
    @Override
    public String received_exception_id() {
        this.checkAccess(18);
        if (this.cachedReceivedExceptionId == null) {
            String cachedReceivedExceptionId = null;
            if (this.exception == null) {
                throw this.wrapper.exceptionWasNull();
            }
            if (this.exception instanceof SystemException) {
                cachedReceivedExceptionId = ORBUtility.repositoryIdOf(this.exception.getClass().getName());
            }
            else if (this.exception instanceof ApplicationException) {
                cachedReceivedExceptionId = ((ApplicationException)this.exception).getId();
            }
            this.cachedReceivedExceptionId = cachedReceivedExceptionId;
        }
        return this.cachedReceivedExceptionId;
    }
    
    @Override
    public TaggedComponent get_effective_component(final int n) {
        this.checkAccess(19);
        return this.get_effective_components(n)[0];
    }
    
    @Override
    public TaggedComponent[] get_effective_components(final int n) {
        this.checkAccess(20);
        final Integer n2 = new Integer(n);
        TaggedComponent[] iopComponents = null;
        boolean b = false;
        if (this.cachedEffectiveComponents == null) {
            this.cachedEffectiveComponents = new HashMap();
            b = true;
        }
        else {
            iopComponents = this.cachedEffectiveComponents.get(n2);
        }
        if (iopComponents == null && (b || !this.cachedEffectiveComponents.containsKey(n2))) {
            iopComponents = ((CorbaContactInfo)this.messageMediator.getContactInfo()).getEffectiveProfile().getTaggedProfileTemplate().getIOPComponents(this.myORB, n);
            this.cachedEffectiveComponents.put(n2, iopComponents);
        }
        if (iopComponents == null || iopComponents.length == 0) {
            throw this.stdWrapper.invalidComponentId(n2);
        }
        return iopComponents;
    }
    
    @Override
    public Policy get_request_policy(final int n) {
        this.checkAccess(21);
        throw this.wrapper.piOrbNotPolicyBased();
    }
    
    @Override
    public void add_request_service_context(final ServiceContext serviceContext, final boolean b) {
        this.checkAccess(22);
        if (this.cachedRequestServiceContexts == null) {
            this.cachedRequestServiceContexts = new HashMap();
        }
        this.addServiceContext(this.cachedRequestServiceContexts, this.messageMediator.getRequestServiceContexts(), serviceContext, b);
    }
    
    @Override
    public int request_id() {
        return this.messageMediator.getRequestId();
    }
    
    @Override
    public String operation() {
        return this.messageMediator.getOperationName();
    }
    
    @Override
    public Parameter[] arguments() {
        this.checkAccess(2);
        if (this.cachedArguments == null) {
            if (this.request == null) {
                throw this.stdWrapper.piOperationNotSupported1();
            }
            this.cachedArguments = this.nvListToParameterArray(this.request.arguments());
        }
        return this.cachedArguments;
    }
    
    @Override
    public TypeCode[] exceptions() {
        this.checkAccess(3);
        if (this.cachedExceptions == null) {
            if (this.request == null) {
                throw this.stdWrapper.piOperationNotSupported2();
            }
            final ExceptionList exceptions = this.request.exceptions();
            final int count = exceptions.count();
            final TypeCode[] cachedExceptions = new TypeCode[count];
            try {
                for (int i = 0; i < count; ++i) {
                    cachedExceptions[i] = exceptions.item(i);
                }
            }
            catch (final Exception ex) {
                throw this.wrapper.exceptionInExceptions(ex);
            }
            this.cachedExceptions = cachedExceptions;
        }
        return this.cachedExceptions;
    }
    
    @Override
    public String[] contexts() {
        this.checkAccess(4);
        if (this.cachedContexts == null) {
            if (this.request == null) {
                throw this.stdWrapper.piOperationNotSupported3();
            }
            final ContextList contexts = this.request.contexts();
            final int count = contexts.count();
            final String[] cachedContexts = new String[count];
            try {
                for (int i = 0; i < count; ++i) {
                    cachedContexts[i] = contexts.item(i);
                }
            }
            catch (final Exception ex) {
                throw this.wrapper.exceptionInContexts(ex);
            }
            this.cachedContexts = cachedContexts;
        }
        return this.cachedContexts;
    }
    
    @Override
    public String[] operation_context() {
        this.checkAccess(5);
        if (this.cachedOperationContext == null) {
            if (this.request == null) {
                throw this.stdWrapper.piOperationNotSupported4();
            }
            final NVList get_values = this.request.ctx().get_values("", 15, "*");
            final String[] cachedOperationContext = new String[get_values.count() * 2];
            if (get_values != null && get_values.count() != 0) {
                int n = 0;
                for (int i = 0; i < get_values.count(); ++i) {
                    NamedValue item;
                    try {
                        item = get_values.item(i);
                    }
                    catch (final Exception ex) {
                        return null;
                    }
                    cachedOperationContext[n] = item.name();
                    ++n;
                    cachedOperationContext[n] = item.value().extract_string();
                    ++n;
                }
            }
            this.cachedOperationContext = cachedOperationContext;
        }
        return this.cachedOperationContext;
    }
    
    @Override
    public Any result() {
        this.checkAccess(6);
        if (this.cachedResult == null) {
            if (this.request == null) {
                throw this.stdWrapper.piOperationNotSupported5();
            }
            final NamedValue result = this.request.result();
            if (result == null) {
                throw this.wrapper.piDiiResultIsNull();
            }
            this.cachedResult = result.value();
        }
        return this.cachedResult;
    }
    
    @Override
    public boolean response_expected() {
        return !this.messageMediator.isOneWay();
    }
    
    @Override
    public Object forward_reference() {
        this.checkAccess(10);
        if (this.replyStatus != 3) {
            throw this.stdWrapper.invalidPiCall1();
        }
        return this.iorToObject(this.getLocatedIOR());
    }
    
    private IOR getLocatedIOR() {
        return ((CorbaContactInfoList)this.messageMediator.getContactInfo().getContactInfoList()).getEffectiveTargetIOR();
    }
    
    protected void setLocatedIOR(final IOR ior) {
        ((CorbaContactInfoListIterator)((CorbaInvocationInfo)this.messageMediator.getBroker().getInvocationInfo()).getContactInfoListIterator()).reportRedirect((CorbaContactInfo)this.messageMediator.getContactInfo(), ior);
    }
    
    @Override
    public ServiceContext get_request_service_context(final int n) {
        this.checkAccess(12);
        if (this.cachedRequestServiceContexts == null) {
            this.cachedRequestServiceContexts = new HashMap();
        }
        return this.getServiceContext(this.cachedRequestServiceContexts, this.messageMediator.getRequestServiceContexts(), n);
    }
    
    @Override
    public ServiceContext get_reply_service_context(final int n) {
        this.checkAccess(13);
        if (this.cachedReplyServiceContexts == null) {
            this.cachedReplyServiceContexts = new HashMap();
        }
        try {
            final ServiceContexts replyServiceContexts = this.messageMediator.getReplyServiceContexts();
            if (replyServiceContexts == null) {
                throw new NullPointerException();
            }
            return this.getServiceContext(this.cachedReplyServiceContexts, replyServiceContexts, n);
        }
        catch (final NullPointerException ex) {
            throw this.stdWrapper.invalidServiceContextId(ex);
        }
    }
    
    @Override
    public Connection connection() {
        return (Connection)this.messageMediator.getConnection();
    }
    
    protected void setInfo(final MessageMediator messageMediator) {
        (this.messageMediator = (CorbaMessageMediator)messageMediator).setDIIInfo(this.request);
    }
    
    void setRetryRequest(final RetryType retryRequest) {
        this.retryRequest = retryRequest;
    }
    
    RetryType getRetryRequest() {
        return this.retryRequest;
    }
    
    void incrementEntryCount() {
        ++this.entryCount;
    }
    
    void decrementEntryCount() {
        --this.entryCount;
    }
    
    int getEntryCount() {
        return this.entryCount;
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
    
    protected void setDIIRequest(final Request request) {
        this.request = request;
    }
    
    protected void setDIIInitiate(final boolean diiInitiate) {
        this.diiInitiate = diiInitiate;
    }
    
    protected boolean isDIIInitiate() {
        return this.diiInitiate;
    }
    
    protected void setPICurrentPushed(final boolean piCurrentPushed) {
        this.piCurrentPushed = piCurrentPushed;
    }
    
    protected boolean isPICurrentPushed() {
        return this.piCurrentPushed;
    }
    
    @Override
    protected void setException(final Exception exception) {
        super.setException(exception);
        this.cachedReceivedException = null;
        this.cachedReceivedExceptionId = null;
    }
    
    protected boolean getIsOneWay() {
        return !this.response_expected();
    }
    
    @Override
    protected void checkAccess(final int n) throws BAD_INV_ORDER {
        int n2 = 0;
        Label_0116: {
            switch (this.currentExecutionPoint) {
                case 0: {
                    switch (this.startingPointCall) {
                        case 0: {
                            n2 = 0;
                            break;
                        }
                        case 1: {
                            n2 = 1;
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (this.endingPointCall) {
                        case 0: {
                            n2 = 2;
                            break Label_0116;
                        }
                        case 1: {
                            n2 = 3;
                            break Label_0116;
                        }
                        case 2: {
                            n2 = 4;
                            break Label_0116;
                        }
                    }
                    break;
                }
            }
        }
        if (!ClientRequestInfoImpl.validCall[n][n2]) {
            throw this.stdWrapper.invalidPiCall2();
        }
    }
    
    static {
        validCall = new boolean[][] { { true, true, true, true, true }, { true, true, true, true, true }, { true, false, true, false, false }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, true, true, true }, { false, false, true, false, false }, { true, true, true, true, true }, { true, false, true, true, true }, { false, false, true, true, true }, { false, false, false, false, true }, { true, true, true, true, true }, { true, false, true, true, true }, { false, false, true, true, true }, { true, true, true, true, true }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, false, true, false }, { false, false, false, true, false }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, false, false, false } };
    }
}
