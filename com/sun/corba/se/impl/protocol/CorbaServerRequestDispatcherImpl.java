package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import com.sun.corba.se.pept.encoding.OutputObject;
import org.omg.CORBA.ServerRequest;
import com.sun.corba.se.impl.corba.ServerRequestImpl;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import org.omg.CORBA.DynamicImplementation;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;

public class CorbaServerRequestDispatcherImpl implements CorbaServerRequestDispatcher
{
    protected ORB orb;
    private ORBUtilSystemException wrapper;
    private POASystemException poaWrapper;
    
    public CorbaServerRequestDispatcherImpl(final ORB orb) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.poaWrapper = POASystemException.get(orb, "rpc.protocol");
    }
    
    @Override
    public IOR locate(final ObjectKey objectKey) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".locate->");
            }
            final ObjectKeyTemplate template = objectKey.getTemplate();
            try {
                this.checkServerId(objectKey);
            }
            catch (final ForwardException ex) {
                return ex.getIOR();
            }
            this.findObjectAdapter(template);
            return null;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".locate<-");
            }
        }
    }
    
    @Override
    public void dispatch(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".dispatch->: " + this.opAndId(corbaMessageMediator));
            }
            this.consumeServiceContexts(corbaMessageMediator);
            ((MarshalInputStream)corbaMessageMediator.getInputObject()).performORBVersionSpecificInit();
            final ObjectKey objectKey = corbaMessageMediator.getObjectKey();
            try {
                this.checkServerId(objectKey);
            }
            catch (final ForwardException ex) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatch: " + this.opAndId(corbaMessageMediator) + ": bad server id");
                }
                corbaMessageMediator.getProtocolHandler().createLocationForward(corbaMessageMediator, ex.getIOR(), null);
                return;
            }
            final String operationName = corbaMessageMediator.getOperationName();
            try {
                final byte[] id = objectKey.getId().getId();
                final ObjectKeyTemplate template = objectKey.getTemplate();
                final ObjectAdapter objectAdapter = this.findObjectAdapter(template);
                this.dispatchToServant(this.getServantWithPI(corbaMessageMediator, objectAdapter, id, template, operationName), corbaMessageMediator, id, objectAdapter);
            }
            catch (final ForwardException ex2) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatch: " + this.opAndId(corbaMessageMediator) + ": ForwardException caught");
                }
                corbaMessageMediator.getProtocolHandler().createLocationForward(corbaMessageMediator, ex2.getIOR(), null);
            }
            catch (final OADestroyed oaDestroyed) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatch: " + this.opAndId(corbaMessageMediator) + ": OADestroyed exception caught");
                }
                this.dispatch(corbaMessageMediator);
            }
            catch (final RequestCanceledException ex3) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatch: " + this.opAndId(corbaMessageMediator) + ": RequestCanceledException caught");
                }
                throw ex3;
            }
            catch (final UnknownException ex4) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatch: " + this.opAndId(corbaMessageMediator) + ": UnknownException caught " + ex4);
                }
                if (ex4.originalEx instanceof RequestCanceledException) {
                    throw (RequestCanceledException)ex4.originalEx;
                }
                final ServiceContexts serviceContexts = new ServiceContexts(this.orb);
                serviceContexts.put(new UEInfoServiceContext(ex4.originalEx));
                corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, this.wrapper.unknownExceptionInDispatch(CompletionStatus.COMPLETED_MAYBE, ex4), serviceContexts);
            }
            catch (final Throwable t) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatch: " + this.opAndId(corbaMessageMediator) + ": other exception " + t);
                }
                corbaMessageMediator.getProtocolHandler().handleThrowableDuringServerDispatch(corbaMessageMediator, t, CompletionStatus.COMPLETED_MAYBE);
            }
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".dispatch<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    private void releaseServant(final ObjectAdapter objectAdapter) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".releaseServant->");
            }
            if (objectAdapter == null) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".releaseServant: null object adapter");
                }
                return;
            }
            try {
                objectAdapter.returnServant();
            }
            finally {
                objectAdapter.exit();
                this.orb.popInvocationInfo();
            }
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".releaseServant<-");
            }
        }
    }
    
    private Object getServant(final ObjectAdapter objectAdapter, final byte[] array, final String operation) throws OADestroyed {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".getServant->");
            }
            final OAInvocationInfo invocationInfo = objectAdapter.makeInvocationInfo(array);
            invocationInfo.setOperation(operation);
            this.orb.pushInvocationInfo(invocationInfo);
            objectAdapter.getInvocationServant(invocationInfo);
            return invocationInfo.getServantContainer();
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".getServant<-");
            }
        }
    }
    
    protected Object getServantWithPI(final CorbaMessageMediator corbaMessageMediator, final ObjectAdapter objectAdapter, final byte[] array, final ObjectKeyTemplate objectKeyTemplate, final String s) throws OADestroyed {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".getServantWithPI->");
            }
            this.orb.getPIHandler().initializeServerPIInfo(corbaMessageMediator, objectAdapter, array, objectKeyTemplate);
            this.orb.getPIHandler().invokeServerPIStartingPoint();
            objectAdapter.enter();
            if (corbaMessageMediator != null) {
                corbaMessageMediator.setExecuteReturnServantInResponseConstructor(true);
            }
            final Object servant = this.getServant(objectAdapter, array, s);
            String s2 = "unknown";
            if (servant instanceof NullServant) {
                this.handleNullServant(s, (NullServant)servant);
            }
            else {
                s2 = objectAdapter.getInterfaces(servant, array)[0];
            }
            this.orb.getPIHandler().setServerPIInfo(servant, s2);
            if ((servant != null && !(servant instanceof DynamicImplementation) && !(servant instanceof org.omg.PortableServer.DynamicImplementation)) || SpecialMethod.getSpecialMethod(s) != null) {
                this.orb.getPIHandler().invokeServerPIIntermediatePoint();
            }
            return servant;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".getServantWithPI<-");
            }
        }
    }
    
    protected void checkServerId(final ObjectKey objectKey) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".checkServerId->");
            }
            final ObjectKeyTemplate template = objectKey.getTemplate();
            if (!this.orb.isLocalServerId(template.getSubcontractId(), template.getServerId())) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".checkServerId: bad server id");
                }
                this.orb.handleBadServerId(objectKey);
            }
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".checkServerId<-");
            }
        }
    }
    
    private ObjectAdapter findObjectAdapter(final ObjectKeyTemplate objectKeyTemplate) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".findObjectAdapter->");
            }
            final ObjectAdapterFactory objectAdapterFactory = this.orb.getRequestDispatcherRegistry().getObjectAdapterFactory(objectKeyTemplate.getSubcontractId());
            if (objectAdapterFactory == null) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".findObjectAdapter: failed to find ObjectAdapterFactory");
                }
                throw this.wrapper.noObjectAdapterFactory();
            }
            final ObjectAdapter find = objectAdapterFactory.find(objectKeyTemplate.getObjectAdapterId());
            if (find == null) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".findObjectAdapter: failed to find ObjectAdaptor");
                }
                throw this.wrapper.badAdapterId();
            }
            return find;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".findObjectAdapter<-");
            }
        }
    }
    
    protected void handleNullServant(final String s, final NullServant nullServant) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".handleNullServant->: " + s);
            }
            final SpecialMethod specialMethod = SpecialMethod.getSpecialMethod(s);
            if (specialMethod == null || !specialMethod.isNonExistentMethod()) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".handleNullServant: " + s + ": throwing OBJECT_NOT_EXIST");
                }
                throw nullServant.getException();
            }
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".handleNullServant<-: " + s);
            }
        }
    }
    
    protected void consumeServiceContexts(final CorbaMessageMediator corbaMessageMediator) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".consumeServiceContexts->: " + this.opAndId(corbaMessageMediator));
            }
            final ServiceContexts requestServiceContexts = corbaMessageMediator.getRequestServiceContexts();
            final GIOPVersion giopVersion = corbaMessageMediator.getGIOPVersion();
            final boolean processCodeSetContext = this.processCodeSetContext(corbaMessageMediator, requestServiceContexts);
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".consumeServiceContexts: " + this.opAndId(corbaMessageMediator) + ": GIOP version: " + giopVersion);
                this.dprint(".consumeServiceContexts: " + this.opAndId(corbaMessageMediator) + ": as code set context? " + processCodeSetContext);
            }
            final ServiceContext value = requestServiceContexts.get(6);
            if (value != null) {
                final IOR ior = ((SendingContextServiceContext)value).getIOR();
                try {
                    ((CorbaConnection)corbaMessageMediator.getConnection()).setCodeBaseIOR(ior);
                }
                catch (final ThreadDeath threadDeath) {
                    throw threadDeath;
                }
                catch (final Throwable t) {
                    throw this.wrapper.badStringifiedIor(t);
                }
            }
            boolean b = false;
            if (giopVersion.equals(GIOPVersion.V1_0) && processCodeSetContext) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".consumeServiceCOntexts: " + this.opAndId(corbaMessageMediator) + ": Determined to be an old Sun ORB");
                }
                this.orb.setORBVersion(ORBVersionFactory.getOLD());
            }
            else {
                b = true;
            }
            final ServiceContext value2 = requestServiceContexts.get(1313165056);
            if (value2 != null) {
                this.orb.setORBVersion(((ORBVersionServiceContext)value2).getVersion());
                b = false;
            }
            if (b) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".consumeServiceContexts: " + this.opAndId(corbaMessageMediator) + ": Determined to be a foreign ORB");
                }
                this.orb.setORBVersion(ORBVersionFactory.getFOREIGN());
            }
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".consumeServiceContexts<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    protected CorbaMessageMediator dispatchToServant(final Object o, final CorbaMessageMediator corbaMessageMediator, final byte[] array, final ObjectAdapter objectAdapter) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".dispatchToServant->: " + this.opAndId(corbaMessageMediator));
            }
            final String operationName = corbaMessageMediator.getOperationName();
            final SpecialMethod specialMethod = SpecialMethod.getSpecialMethod(operationName);
            if (specialMethod != null) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatchToServant: " + this.opAndId(corbaMessageMediator) + ": Handling special method");
                }
                return specialMethod.invoke(o, corbaMessageMediator, array, objectAdapter);
            }
            CorbaMessageMediator corbaMessageMediator2;
            if (o instanceof DynamicImplementation) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatchToServant: " + this.opAndId(corbaMessageMediator) + ": Handling old style DSI type servant");
                }
                final DynamicImplementation dynamicImplementation = (DynamicImplementation)o;
                final ServerRequestImpl serverRequestImpl = new ServerRequestImpl(corbaMessageMediator, this.orb);
                dynamicImplementation.invoke(serverRequestImpl);
                corbaMessageMediator2 = this.handleDynamicResult(serverRequestImpl, corbaMessageMediator);
            }
            else if (o instanceof org.omg.PortableServer.DynamicImplementation) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatchToServant: " + this.opAndId(corbaMessageMediator) + ": Handling POA DSI type servant");
                }
                final org.omg.PortableServer.DynamicImplementation dynamicImplementation2 = (org.omg.PortableServer.DynamicImplementation)o;
                final ServerRequestImpl serverRequestImpl2 = new ServerRequestImpl(corbaMessageMediator, this.orb);
                dynamicImplementation2.invoke(serverRequestImpl2);
                corbaMessageMediator2 = this.handleDynamicResult(serverRequestImpl2, corbaMessageMediator);
            }
            else {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".dispatchToServant: " + this.opAndId(corbaMessageMediator) + ": Handling invoke handler type servant");
                }
                corbaMessageMediator2 = (CorbaMessageMediator)((OutputObject)((InvokeHandler)o)._invoke(operationName, (InputStream)corbaMessageMediator.getInputObject(), corbaMessageMediator)).getMessageMediator();
            }
            return corbaMessageMediator2;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".dispatchToServant<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    protected CorbaMessageMediator handleDynamicResult(final ServerRequestImpl serverRequestImpl, final CorbaMessageMediator corbaMessageMediator) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".handleDynamicResult->: " + this.opAndId(corbaMessageMediator));
            }
            final Any checkResultCalled = serverRequestImpl.checkResultCalled();
            CorbaMessageMediator corbaMessageMediator2;
            if (checkResultCalled == null) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".handleDynamicResult: " + this.opAndId(corbaMessageMediator) + ": handling normal result");
                }
                corbaMessageMediator2 = this.sendingReply(corbaMessageMediator);
                serverRequestImpl.marshalReplyParams((OutputStream)corbaMessageMediator2.getOutputObject());
            }
            else {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".handleDynamicResult: " + this.opAndId(corbaMessageMediator) + ": handling error");
                }
                corbaMessageMediator2 = this.sendingReply(corbaMessageMediator, checkResultCalled);
            }
            return corbaMessageMediator2;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".handleDynamicResult<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    protected CorbaMessageMediator sendingReply(final CorbaMessageMediator corbaMessageMediator) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".sendingReply->: " + this.opAndId(corbaMessageMediator));
            }
            return corbaMessageMediator.getProtocolHandler().createResponse(corbaMessageMediator, new ServiceContexts(this.orb));
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".sendingReply<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    protected CorbaMessageMediator sendingReply(final CorbaMessageMediator corbaMessageMediator, final Any any) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".sendingReply/Any->: " + this.opAndId(corbaMessageMediator));
            }
            final ServiceContexts serviceContexts = new ServiceContexts(this.orb);
            String id;
            try {
                id = any.type().id();
            }
            catch (final BadKind badKind) {
                throw this.wrapper.problemWithExceptionTypecode(badKind);
            }
            CorbaMessageMediator corbaMessageMediator2;
            if (ORBUtility.isSystemException(id)) {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".sendingReply/Any: " + this.opAndId(corbaMessageMediator) + ": handling system exception");
                }
                corbaMessageMediator2 = corbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(corbaMessageMediator, ORBUtility.readSystemException(any.create_input_stream()), serviceContexts);
            }
            else {
                if (this.orb.subcontractDebugFlag) {
                    this.dprint(".sendingReply/Any: " + this.opAndId(corbaMessageMediator) + ": handling user exception");
                }
                corbaMessageMediator2 = corbaMessageMediator.getProtocolHandler().createUserExceptionResponse(corbaMessageMediator, serviceContexts);
                any.write_value((OutputStream)corbaMessageMediator2.getOutputObject());
            }
            return corbaMessageMediator2;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".sendingReply/Any<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    protected boolean processCodeSetContext(final CorbaMessageMediator corbaMessageMediator, final ServiceContexts serviceContexts) {
        try {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".processCodeSetContext->: " + this.opAndId(corbaMessageMediator));
            }
            final ServiceContext value = serviceContexts.get(1);
            if (value != null) {
                if (corbaMessageMediator.getConnection() == null) {
                    return true;
                }
                if (corbaMessageMediator.getGIOPVersion().equals(GIOPVersion.V1_0)) {
                    return true;
                }
                final CodeSetComponentInfo.CodeSetContext codeSetContext = ((CodeSetServiceContext)value).getCodeSetContext();
                if (((CorbaConnection)corbaMessageMediator.getConnection()).getCodeSetContext() == null) {
                    if (this.orb.subcontractDebugFlag) {
                        this.dprint(".processCodeSetContext: " + this.opAndId(corbaMessageMediator) + ": Setting code sets to: " + codeSetContext);
                    }
                    ((CorbaConnection)corbaMessageMediator.getConnection()).setCodeSetContext(codeSetContext);
                    if (codeSetContext.getCharCodeSet() != OSFCodeSetRegistry.ISO_8859_1.getNumber()) {
                        ((MarshalInputStream)corbaMessageMediator.getInputObject()).resetCodeSetConverters();
                    }
                }
            }
            return value != null;
        }
        finally {
            if (this.orb.subcontractDebugFlag) {
                this.dprint(".processCodeSetContext<-: " + this.opAndId(corbaMessageMediator));
            }
        }
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaServerRequestDispatcherImpl", s);
    }
    
    protected String opAndId(final CorbaMessageMediator corbaMessageMediator) {
        return ORBUtility.operationNameAndRequestId(corbaMessageMediator);
    }
}
