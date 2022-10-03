package com.sun.corba.se.impl.interceptors;

import org.omg.CORBA.BAD_INV_ORDER;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.IOP.ServiceContextHelper;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import sun.corba.OutputStreamFactory;
import java.util.HashMap;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.NVList;
import org.omg.CORBA.UNKNOWN;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.CompletionStatus;
import java.io.IOException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.impl.util.RepositoryId;
import sun.corba.SharedSecrets;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.PortableInterceptor.ForwardRequest;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.legacy.interceptor.RequestInfoExt;
import org.omg.PortableInterceptor.RequestInfo;
import org.omg.CORBA.LocalObject;

public abstract class RequestInfoImpl extends LocalObject implements RequestInfo, RequestInfoExt
{
    protected ORB myORB;
    protected InterceptorsSystemException wrapper;
    protected OMGSystemException stdWrapper;
    protected int flowStackIndex;
    protected int startingPointCall;
    protected int intermediatePointCall;
    protected int endingPointCall;
    protected short replyStatus;
    protected static final short UNINITIALIZED = -1;
    protected int currentExecutionPoint;
    protected static final int EXECUTION_POINT_STARTING = 0;
    protected static final int EXECUTION_POINT_INTERMEDIATE = 1;
    protected static final int EXECUTION_POINT_ENDING = 2;
    protected boolean alreadyExecuted;
    protected Connection connection;
    protected ServiceContexts serviceContexts;
    protected ForwardRequest forwardRequest;
    protected IOR forwardRequestIOR;
    protected SlotTable slotTable;
    protected Exception exception;
    protected static final int MID_REQUEST_ID = 0;
    protected static final int MID_OPERATION = 1;
    protected static final int MID_ARGUMENTS = 2;
    protected static final int MID_EXCEPTIONS = 3;
    protected static final int MID_CONTEXTS = 4;
    protected static final int MID_OPERATION_CONTEXT = 5;
    protected static final int MID_RESULT = 6;
    protected static final int MID_RESPONSE_EXPECTED = 7;
    protected static final int MID_SYNC_SCOPE = 8;
    protected static final int MID_REPLY_STATUS = 9;
    protected static final int MID_FORWARD_REFERENCE = 10;
    protected static final int MID_GET_SLOT = 11;
    protected static final int MID_GET_REQUEST_SERVICE_CONTEXT = 12;
    protected static final int MID_GET_REPLY_SERVICE_CONTEXT = 13;
    protected static final int MID_RI_LAST = 13;
    
    void reset() {
        this.flowStackIndex = 0;
        this.startingPointCall = 0;
        this.intermediatePointCall = 0;
        this.endingPointCall = 0;
        this.setReplyStatus((short)(-1));
        this.currentExecutionPoint = 0;
        this.alreadyExecuted = false;
        this.connection = null;
        this.serviceContexts = null;
        this.forwardRequest = null;
        this.forwardRequestIOR = null;
        this.exception = null;
    }
    
    public RequestInfoImpl(final ORB myORB) {
        this.flowStackIndex = 0;
        this.replyStatus = -1;
        this.myORB = myORB;
        this.wrapper = InterceptorsSystemException.get(myORB, "rpc.protocol");
        this.stdWrapper = OMGSystemException.get(myORB, "rpc.protocol");
        this.slotTable = ((PICurrent)myORB.getPIHandler().getPICurrent()).getSlotTable();
    }
    
    @Override
    public abstract int request_id();
    
    @Override
    public abstract String operation();
    
    @Override
    public abstract Parameter[] arguments();
    
    @Override
    public abstract TypeCode[] exceptions();
    
    @Override
    public abstract String[] contexts();
    
    @Override
    public abstract String[] operation_context();
    
    @Override
    public abstract Any result();
    
    @Override
    public abstract boolean response_expected();
    
    @Override
    public short sync_scope() {
        this.checkAccess(8);
        return 1;
    }
    
    @Override
    public short reply_status() {
        this.checkAccess(9);
        return this.replyStatus;
    }
    
    @Override
    public abstract Object forward_reference();
    
    @Override
    public Any get_slot(final int n) throws InvalidSlot {
        return this.slotTable.get_slot(n);
    }
    
    @Override
    public abstract ServiceContext get_request_service_context(final int p0);
    
    @Override
    public abstract ServiceContext get_reply_service_context(final int p0);
    
    @Override
    public Connection connection() {
        return this.connection;
    }
    
    private void insertApplicationException(final ApplicationException ex, final Any any) throws UNKNOWN {
        try {
            final Method method = SharedSecrets.getJavaCorbaAccess().loadClass(RepositoryId.cache.getId(ex.getId()).getClassName() + "Helper").getMethod("read", InputStream.class);
            final InputStream inputStream = ex.getInputStream();
            inputStream.mark(0);
            UserException ex2 = null;
            try {
                ex2 = (UserException)method.invoke(null, inputStream);
            }
            finally {
                try {
                    inputStream.reset();
                }
                catch (final IOException ex3) {
                    throw this.wrapper.markAndResetFailed(ex3);
                }
            }
            this.insertUserException(ex2, any);
        }
        catch (final ClassNotFoundException ex4) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex4);
        }
        catch (final NoSuchMethodException ex5) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex5);
        }
        catch (final SecurityException ex6) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex6);
        }
        catch (final IllegalAccessException ex7) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex7);
        }
        catch (final IllegalArgumentException ex8) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex8);
        }
        catch (final InvocationTargetException ex9) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex9);
        }
    }
    
    private void insertUserException(final UserException ex, final Any any) throws UNKNOWN {
        try {
            if (ex != null) {
                final Class<? extends UserException> class1 = ex.getClass();
                SharedSecrets.getJavaCorbaAccess().loadClass(class1.getName() + "Helper").getMethod("insert", Any.class, class1).invoke(null, any, ex);
            }
        }
        catch (final ClassNotFoundException ex2) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
        catch (final NoSuchMethodException ex3) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex3);
        }
        catch (final SecurityException ex4) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex4);
        }
        catch (final IllegalAccessException ex5) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex5);
        }
        catch (final IllegalArgumentException ex6) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex6);
        }
        catch (final InvocationTargetException ex7) {
            throw this.stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, ex7);
        }
    }
    
    protected Parameter[] nvListToParameterArray(final NVList list) {
        final int count = list.count();
        final Parameter[] array = new Parameter[count];
        try {
            for (int i = 0; i < count; ++i) {
                array[i] = new Parameter();
                final NamedValue item = list.item(i);
                array[i].argument = item.value();
                array[i].mode = ParameterMode.from_int(item.flags() - 1);
            }
        }
        catch (final Exception ex) {
            throw this.wrapper.exceptionInArguments(ex);
        }
        return array;
    }
    
    protected Any exceptionToAny(final Exception ex) {
        final Any create_any = this.myORB.create_any();
        if (ex == null) {
            throw this.wrapper.exceptionWasNull2();
        }
        if (ex instanceof SystemException) {
            ORBUtility.insertSystemException((SystemException)ex, create_any);
        }
        else if (ex instanceof ApplicationException) {
            try {
                this.insertApplicationException((ApplicationException)ex, create_any);
            }
            catch (final UNKNOWN unknown) {
                ORBUtility.insertSystemException(unknown, create_any);
            }
        }
        else if (ex instanceof UserException) {
            try {
                this.insertUserException((UserException)ex, create_any);
            }
            catch (final UNKNOWN unknown2) {
                ORBUtility.insertSystemException(unknown2, create_any);
            }
        }
        return create_any;
    }
    
    protected ServiceContext getServiceContext(final HashMap hashMap, final ServiceContexts serviceContexts, final int n) {
        final Integer n2 = new Integer(n);
        ServiceContext read = hashMap.get(n2);
        if (read == null) {
            final com.sun.corba.se.spi.servicecontext.ServiceContext value = serviceContexts.get(n);
            if (value == null) {
                throw this.stdWrapper.invalidServiceContextId();
            }
            final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.myORB);
            value.write(encapsOutputStream, GIOPVersion.V1_2);
            read = ServiceContextHelper.read(encapsOutputStream.create_input_stream());
            hashMap.put(n2, read);
        }
        return read;
    }
    
    protected void addServiceContext(final HashMap hashMap, final ServiceContexts serviceContexts, final ServiceContext serviceContext, final boolean b) {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.myORB);
        ServiceContextHelper.write(encapsOutputStream, serviceContext);
        final InputStream create_input_stream = encapsOutputStream.create_input_stream();
        final UnknownServiceContext unknownServiceContext = new UnknownServiceContext(create_input_stream.read_long(), (org.omg.CORBA_2_3.portable.InputStream)create_input_stream);
        final int id = unknownServiceContext.getId();
        if (serviceContexts.get(id) != null) {
            if (!b) {
                throw this.stdWrapper.serviceContextAddFailed(new Integer(id));
            }
            serviceContexts.delete(id);
        }
        serviceContexts.put(unknownServiceContext);
        hashMap.put(new Integer(id), serviceContext);
    }
    
    protected void setFlowStackIndex(final int flowStackIndex) {
        this.flowStackIndex = flowStackIndex;
    }
    
    protected int getFlowStackIndex() {
        return this.flowStackIndex;
    }
    
    protected void setEndingPointCall(final int endingPointCall) {
        this.endingPointCall = endingPointCall;
    }
    
    protected int getEndingPointCall() {
        return this.endingPointCall;
    }
    
    protected void setIntermediatePointCall(final int intermediatePointCall) {
        this.intermediatePointCall = intermediatePointCall;
    }
    
    protected int getIntermediatePointCall() {
        return this.intermediatePointCall;
    }
    
    protected void setStartingPointCall(final int startingPointCall) {
        this.startingPointCall = startingPointCall;
    }
    
    protected int getStartingPointCall() {
        return this.startingPointCall;
    }
    
    protected boolean getAlreadyExecuted() {
        return this.alreadyExecuted;
    }
    
    protected void setAlreadyExecuted(final boolean alreadyExecuted) {
        this.alreadyExecuted = alreadyExecuted;
    }
    
    protected void setReplyStatus(final short replyStatus) {
        this.replyStatus = replyStatus;
    }
    
    protected short getReplyStatus() {
        return this.replyStatus;
    }
    
    protected void setForwardRequest(final ForwardRequest forwardRequest) {
        this.forwardRequest = forwardRequest;
        this.forwardRequestIOR = null;
    }
    
    protected void setForwardRequest(final IOR forwardRequestIOR) {
        this.forwardRequestIOR = forwardRequestIOR;
        this.forwardRequest = null;
    }
    
    protected ForwardRequest getForwardRequestException() {
        if (this.forwardRequest == null && this.forwardRequestIOR != null) {
            this.forwardRequest = new ForwardRequest(this.iorToObject(this.forwardRequestIOR));
        }
        return this.forwardRequest;
    }
    
    protected IOR getForwardRequestIOR() {
        if (this.forwardRequestIOR == null && this.forwardRequest != null) {
            this.forwardRequestIOR = ORBUtility.getIOR(this.forwardRequest.forward);
        }
        return this.forwardRequestIOR;
    }
    
    protected void setException(final Exception exception) {
        this.exception = exception;
    }
    
    Exception getException() {
        return this.exception;
    }
    
    protected void setCurrentExecutionPoint(final int currentExecutionPoint) {
        this.currentExecutionPoint = currentExecutionPoint;
    }
    
    protected abstract void checkAccess(final int p0) throws BAD_INV_ORDER;
    
    void setSlotTable(final SlotTable slotTable) {
        this.slotTable = slotTable;
    }
    
    protected Object iorToObject(final IOR ior) {
        return ORBUtility.makeObjectReference(ior);
    }
}
