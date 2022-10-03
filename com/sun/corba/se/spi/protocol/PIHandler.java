package com.sun.corba.se.spi.protocol;

import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.corba.RequestImpl;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import java.io.Closeable;

public interface PIHandler extends Closeable
{
    void initialize();
    
    void destroyInterceptors();
    
    void objectAdapterCreated(final ObjectAdapter p0);
    
    void adapterManagerStateChanged(final int p0, final short p1);
    
    void adapterStateChanged(final ObjectReferenceTemplate[] p0, final short p1);
    
    void disableInterceptorsThisThread();
    
    void enableInterceptorsThisThread();
    
    void invokeClientPIStartingPoint() throws RemarshalException;
    
    Exception invokeClientPIEndingPoint(final int p0, final Exception p1);
    
    Exception makeCompletedClientRequest(final int p0, final Exception p1);
    
    void initiateClientPIRequest(final boolean p0);
    
    void cleanupClientPIRequest();
    
    void setClientPIInfo(final RequestImpl p0);
    
    void setClientPIInfo(final CorbaMessageMediator p0);
    
    void invokeServerPIStartingPoint();
    
    void invokeServerPIIntermediatePoint();
    
    void invokeServerPIEndingPoint(final ReplyMessage p0);
    
    void initializeServerPIInfo(final CorbaMessageMediator p0, final ObjectAdapter p1, final byte[] p2, final ObjectKeyTemplate p3);
    
    void setServerPIInfo(final Object p0, final String p1);
    
    void setServerPIInfo(final Exception p0);
    
    void setServerPIInfo(final NVList p0);
    
    void setServerPIExceptionInfo(final Any p0);
    
    void setServerPIInfo(final Any p0);
    
    void cleanupServerPIRequest();
    
    Policy create_policy(final int p0, final Any p1) throws PolicyError;
    
    void register_interceptor(final Interceptor p0, final int p1) throws DuplicateName;
    
    Current getPICurrent();
    
    void registerPolicyFactory(final int p0, final PolicyFactory p1);
    
    int allocateServerRequestId();
}
