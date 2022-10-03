package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;

public class InterceptorInvoker
{
    private ORB orb;
    private InterceptorList interceptorList;
    private boolean enabled;
    private PICurrent current;
    
    InterceptorInvoker(final ORB orb, final InterceptorList interceptorList, final PICurrent current) {
        this.enabled = false;
        this.orb = orb;
        this.interceptorList = interceptorList;
        this.enabled = false;
        this.current = current;
    }
    
    void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    void objectAdapterCreated(final ObjectAdapter objectAdapter) {
        if (this.enabled) {
            final IORInfoImpl iorInfoImpl = new IORInfoImpl(objectAdapter);
            final IORInterceptor[] array = (IORInterceptor[])this.interceptorList.getInterceptors(2);
            final int length = array.length;
            for (int i = length - 1; i >= 0; --i) {
                final IORInterceptor iorInterceptor = array[i];
                try {
                    iorInterceptor.establish_components(iorInfoImpl);
                }
                catch (final Exception ex) {}
            }
            iorInfoImpl.makeStateEstablished();
            for (int j = length - 1; j >= 0; --j) {
                final IORInterceptor iorInterceptor2 = array[j];
                if (iorInterceptor2 instanceof IORInterceptor_3_0) {
                    ((IORInterceptor_3_0)iorInterceptor2).components_established(iorInfoImpl);
                }
            }
            iorInfoImpl.makeStateDone();
        }
    }
    
    void adapterManagerStateChanged(final int n, final short n2) {
        if (this.enabled) {
            final IORInterceptor[] array = (IORInterceptor[])this.interceptorList.getInterceptors(2);
            for (int i = array.length - 1; i >= 0; --i) {
                try {
                    final IORInterceptor iorInterceptor = array[i];
                    if (iorInterceptor instanceof IORInterceptor_3_0) {
                        ((IORInterceptor_3_0)iorInterceptor).adapter_manager_state_changed(n, n2);
                    }
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    void adapterStateChanged(final ObjectReferenceTemplate[] array, final short n) {
        if (this.enabled) {
            final IORInterceptor[] array2 = (IORInterceptor[])this.interceptorList.getInterceptors(2);
            for (int i = array2.length - 1; i >= 0; --i) {
                try {
                    final IORInterceptor iorInterceptor = array2[i];
                    if (iorInterceptor instanceof IORInterceptor_3_0) {
                        ((IORInterceptor_3_0)iorInterceptor).adapter_state_changed(array, n);
                    }
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    void invokeClientInterceptorStartingPoint(final ClientRequestInfoImpl clientRequestInfoImpl) {
        if (this.enabled) {
            try {
                this.current.pushSlotTable();
                clientRequestInfoImpl.setPICurrentPushed(true);
                clientRequestInfoImpl.setCurrentExecutionPoint(0);
                final ClientRequestInterceptor[] array = (ClientRequestInterceptor[])this.interceptorList.getInterceptors(0);
                int length = array.length;
                for (int n = 1, n2 = 0; n != 0 && n2 < length; ++n2) {
                    try {
                        array[n2].send_request(clientRequestInfoImpl);
                    }
                    catch (final ForwardRequest forwardRequest) {
                        length = n2;
                        clientRequestInfoImpl.setForwardRequest(forwardRequest);
                        clientRequestInfoImpl.setEndingPointCall(2);
                        clientRequestInfoImpl.setReplyStatus((short)3);
                        this.updateClientRequestDispatcherForward(clientRequestInfoImpl);
                        n = 0;
                    }
                    catch (final SystemException exception) {
                        length = n2;
                        clientRequestInfoImpl.setEndingPointCall(1);
                        clientRequestInfoImpl.setReplyStatus((short)1);
                        clientRequestInfoImpl.setException(exception);
                        n = 0;
                    }
                }
                clientRequestInfoImpl.setFlowStackIndex(length);
            }
            finally {
                this.current.resetSlotTable();
            }
        }
    }
    
    void invokeClientInterceptorEndingPoint(final ClientRequestInfoImpl clientRequestInfoImpl) {
        if (this.enabled) {
            try {
                clientRequestInfoImpl.setCurrentExecutionPoint(2);
                final ClientRequestInterceptor[] array = (ClientRequestInterceptor[])this.interceptorList.getInterceptors(0);
                final int flowStackIndex = clientRequestInfoImpl.getFlowStackIndex();
                int endingPointCall = clientRequestInfoImpl.getEndingPointCall();
                if (endingPointCall == 0 && clientRequestInfoImpl.getIsOneWay()) {
                    endingPointCall = 2;
                    clientRequestInfoImpl.setEndingPointCall(endingPointCall);
                }
                for (int i = flowStackIndex - 1; i >= 0; --i) {
                    try {
                        switch (endingPointCall) {
                            case 0: {
                                array[i].receive_reply(clientRequestInfoImpl);
                                break;
                            }
                            case 1: {
                                array[i].receive_exception(clientRequestInfoImpl);
                                break;
                            }
                            case 2: {
                                array[i].receive_other(clientRequestInfoImpl);
                                break;
                            }
                        }
                    }
                    catch (final ForwardRequest forwardRequest) {
                        endingPointCall = 2;
                        clientRequestInfoImpl.setEndingPointCall(endingPointCall);
                        clientRequestInfoImpl.setReplyStatus((short)3);
                        clientRequestInfoImpl.setForwardRequest(forwardRequest);
                        this.updateClientRequestDispatcherForward(clientRequestInfoImpl);
                    }
                    catch (final SystemException exception) {
                        endingPointCall = 1;
                        clientRequestInfoImpl.setEndingPointCall(endingPointCall);
                        clientRequestInfoImpl.setReplyStatus((short)1);
                        clientRequestInfoImpl.setException(exception);
                    }
                }
            }
            finally {
                if (clientRequestInfoImpl != null && clientRequestInfoImpl.isPICurrentPushed()) {
                    this.current.popSlotTable();
                }
            }
        }
    }
    
    void invokeServerInterceptorStartingPoint(final ServerRequestInfoImpl serverRequestInfoImpl) {
        if (this.enabled) {
            try {
                this.current.pushSlotTable();
                serverRequestInfoImpl.setSlotTable(this.current.getSlotTable());
                this.current.pushSlotTable();
                serverRequestInfoImpl.setCurrentExecutionPoint(0);
                final ServerRequestInterceptor[] array = (ServerRequestInterceptor[])this.interceptorList.getInterceptors(1);
                int length = array.length;
                for (int n = 1, n2 = 0; n != 0 && n2 < length; ++n2) {
                    try {
                        array[n2].receive_request_service_contexts(serverRequestInfoImpl);
                    }
                    catch (final ForwardRequest forwardRequest) {
                        length = n2;
                        serverRequestInfoImpl.setForwardRequest(forwardRequest);
                        serverRequestInfoImpl.setIntermediatePointCall(1);
                        serverRequestInfoImpl.setEndingPointCall(2);
                        serverRequestInfoImpl.setReplyStatus((short)3);
                        n = 0;
                    }
                    catch (final SystemException exception) {
                        length = n2;
                        serverRequestInfoImpl.setException(exception);
                        serverRequestInfoImpl.setIntermediatePointCall(1);
                        serverRequestInfoImpl.setEndingPointCall(1);
                        serverRequestInfoImpl.setReplyStatus((short)1);
                        n = 0;
                    }
                }
                serverRequestInfoImpl.setFlowStackIndex(length);
            }
            finally {
                this.current.popSlotTable();
            }
        }
    }
    
    void invokeServerInterceptorIntermediatePoint(final ServerRequestInfoImpl serverRequestInfoImpl) {
        final int intermediatePointCall = serverRequestInfoImpl.getIntermediatePointCall();
        if (this.enabled && intermediatePointCall != 1) {
            serverRequestInfoImpl.setCurrentExecutionPoint(1);
            final ServerRequestInterceptor[] array = (ServerRequestInterceptor[])this.interceptorList.getInterceptors(1);
            for (int length = array.length, i = 0; i < length; ++i) {
                try {
                    array[i].receive_request(serverRequestInfoImpl);
                }
                catch (final ForwardRequest forwardRequest) {
                    serverRequestInfoImpl.setForwardRequest(forwardRequest);
                    serverRequestInfoImpl.setEndingPointCall(2);
                    serverRequestInfoImpl.setReplyStatus((short)3);
                    break;
                }
                catch (final SystemException exception) {
                    serverRequestInfoImpl.setException(exception);
                    serverRequestInfoImpl.setEndingPointCall(1);
                    serverRequestInfoImpl.setReplyStatus((short)1);
                    break;
                }
            }
        }
    }
    
    void invokeServerInterceptorEndingPoint(final ServerRequestInfoImpl serverRequestInfoImpl) {
        if (this.enabled) {
            try {
                final ServerRequestInterceptor[] array = (ServerRequestInterceptor[])this.interceptorList.getInterceptors(1);
                final int flowStackIndex = serverRequestInfoImpl.getFlowStackIndex();
                int endingPointCall = serverRequestInfoImpl.getEndingPointCall();
                for (int i = flowStackIndex - 1; i >= 0; --i) {
                    try {
                        switch (endingPointCall) {
                            case 0: {
                                array[i].send_reply(serverRequestInfoImpl);
                                break;
                            }
                            case 1: {
                                array[i].send_exception(serverRequestInfoImpl);
                                break;
                            }
                            case 2: {
                                array[i].send_other(serverRequestInfoImpl);
                                break;
                            }
                        }
                    }
                    catch (final ForwardRequest forwardRequest) {
                        endingPointCall = 2;
                        serverRequestInfoImpl.setEndingPointCall(endingPointCall);
                        serverRequestInfoImpl.setForwardRequest(forwardRequest);
                        serverRequestInfoImpl.setReplyStatus((short)3);
                        serverRequestInfoImpl.setForwardRequestRaisedInEnding();
                    }
                    catch (final SystemException exception) {
                        endingPointCall = 1;
                        serverRequestInfoImpl.setEndingPointCall(endingPointCall);
                        serverRequestInfoImpl.setException(exception);
                        serverRequestInfoImpl.setReplyStatus((short)1);
                    }
                }
                serverRequestInfoImpl.setAlreadyExecuted(true);
            }
            finally {
                this.current.popSlotTable();
            }
        }
    }
    
    private void updateClientRequestDispatcherForward(final ClientRequestInfoImpl clientRequestInfoImpl) {
        final ForwardRequest forwardRequestException = clientRequestInfoImpl.getForwardRequestException();
        if (forwardRequestException != null) {
            clientRequestInfoImpl.setLocatedIOR(ORBUtility.getIOR(forwardRequestException.forward));
        }
    }
}
