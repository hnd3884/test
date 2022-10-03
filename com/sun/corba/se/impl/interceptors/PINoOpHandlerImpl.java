package com.sun.corba.se.impl.interceptors;

import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.Interceptor;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.Any;
import org.omg.CORBA.NVList;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.PIHandler;

public class PINoOpHandlerImpl implements PIHandler
{
    @Override
    public void close() {
    }
    
    @Override
    public void initialize() {
    }
    
    @Override
    public void destroyInterceptors() {
    }
    
    @Override
    public void objectAdapterCreated(final ObjectAdapter objectAdapter) {
    }
    
    @Override
    public void adapterManagerStateChanged(final int n, final short n2) {
    }
    
    @Override
    public void adapterStateChanged(final ObjectReferenceTemplate[] array, final short n) {
    }
    
    @Override
    public void disableInterceptorsThisThread() {
    }
    
    @Override
    public void enableInterceptorsThisThread() {
    }
    
    @Override
    public void invokeClientPIStartingPoint() throws RemarshalException {
    }
    
    @Override
    public Exception invokeClientPIEndingPoint(final int n, final Exception ex) {
        return null;
    }
    
    @Override
    public Exception makeCompletedClientRequest(final int n, final Exception ex) {
        return null;
    }
    
    @Override
    public void initiateClientPIRequest(final boolean b) {
    }
    
    @Override
    public void cleanupClientPIRequest() {
    }
    
    @Override
    public void setClientPIInfo(final CorbaMessageMediator corbaMessageMediator) {
    }
    
    @Override
    public void setClientPIInfo(final RequestImpl requestImpl) {
    }
    
    public final void sendCancelRequestIfFinalFragmentNotSent() {
    }
    
    @Override
    public void invokeServerPIStartingPoint() {
    }
    
    @Override
    public void invokeServerPIIntermediatePoint() {
    }
    
    @Override
    public void invokeServerPIEndingPoint(final ReplyMessage replyMessage) {
    }
    
    @Override
    public void setServerPIInfo(final Exception ex) {
    }
    
    @Override
    public void setServerPIInfo(final NVList list) {
    }
    
    @Override
    public void setServerPIExceptionInfo(final Any any) {
    }
    
    @Override
    public void setServerPIInfo(final Any any) {
    }
    
    @Override
    public void initializeServerPIInfo(final CorbaMessageMediator corbaMessageMediator, final ObjectAdapter objectAdapter, final byte[] array, final ObjectKeyTemplate objectKeyTemplate) {
    }
    
    @Override
    public void setServerPIInfo(final Object o, final String s) {
    }
    
    @Override
    public void cleanupServerPIRequest() {
    }
    
    @Override
    public void register_interceptor(final Interceptor interceptor, final int n) throws DuplicateName {
    }
    
    @Override
    public Current getPICurrent() {
        return null;
    }
    
    @Override
    public Policy create_policy(final int n, final Any any) throws PolicyError {
        return null;
    }
    
    @Override
    public void registerPolicyFactory(final int n, final PolicyFactory policyFactory) {
    }
    
    @Override
    public int allocateServerRequestId() {
        return 0;
    }
}
