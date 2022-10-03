package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.impl.encoding.CodeSetConversion;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import java.io.IOException;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import sun.corba.EncapsInputStreamFactory;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA.portable.UnknownException;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import org.omg.CORBA.portable.RemarshalException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.pept.encoding.OutputObject;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.corba.se.pept.transport.ContactInfo;
import java.util.concurrent.ConcurrentMap;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;

public class CorbaClientRequestDispatcherImpl implements ClientRequestDispatcher
{
    private ConcurrentMap<ContactInfo, Object> locks;
    
    public CorbaClientRequestDispatcherImpl() {
        this.locks = new ConcurrentHashMap<ContactInfo, Object>();
    }
    
    @Override
    public OutputObject beginRequest(final Object o, final String s, final boolean b, ContactInfo contactInfo) {
        ORB orb = null;
        try {
            final CorbaContactInfo corbaContactInfo = (CorbaContactInfo)contactInfo;
            orb = (ORB)contactInfo.getBroker();
            if (orb.subcontractDebugFlag) {
                this.dprint(".beginRequest->: op/" + s);
            }
            orb.getPIHandler().initiateClientPIRequest(false);
            CorbaConnection corbaConnection = null;
            Object o2 = this.locks.get(contactInfo);
            if (o2 == null) {
                final Object o3 = new Object();
                o2 = this.locks.putIfAbsent(contactInfo, o3);
                if (o2 == null) {
                    o2 = o3;
                }
            }
            synchronized (o2) {
                if (contactInfo.isConnectionBased()) {
                    if (contactInfo.shouldCacheConnection()) {
                        corbaConnection = (CorbaConnection)orb.getTransportManager().getOutboundConnectionCache(contactInfo).get(contactInfo);
                    }
                    if (corbaConnection != null) {
                        if (orb.subcontractDebugFlag) {
                            this.dprint(".beginRequest: op/" + s + ": Using cached connection: " + corbaConnection);
                        }
                    }
                    else {
                        try {
                            corbaConnection = (CorbaConnection)contactInfo.createConnection();
                            if (orb.subcontractDebugFlag) {
                                this.dprint(".beginRequest: op/" + s + ": Using created connection: " + corbaConnection);
                            }
                        }
                        catch (final RuntimeException ex) {
                            if (orb.subcontractDebugFlag) {
                                this.dprint(".beginRequest: op/" + s + ": failed to create connection: " + ex);
                            }
                            if (!this.getContactInfoListIterator(orb).reportException(contactInfo, ex)) {
                                throw ex;
                            }
                            if (this.getContactInfoListIterator(orb).hasNext()) {
                                contactInfo = (ContactInfo)this.getContactInfoListIterator(orb).next();
                                this.unregisterWaiter(orb);
                                return this.beginRequest(o, s, b, contactInfo);
                            }
                            throw ex;
                        }
                        if (corbaConnection.shouldRegisterReadEvent()) {
                            orb.getTransportManager().getSelector(0).registerForEvent(corbaConnection.getEventHandler());
                            corbaConnection.setState("ESTABLISHED");
                        }
                        if (contactInfo.shouldCacheConnection()) {
                            final OutboundConnectionCache outboundConnectionCache = orb.getTransportManager().getOutboundConnectionCache(contactInfo);
                            outboundConnectionCache.stampTime(corbaConnection);
                            outboundConnectionCache.put(contactInfo, corbaConnection);
                        }
                    }
                }
            }
            final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)contactInfo.createMessageMediator(orb, contactInfo, corbaConnection, s, b);
            if (orb.subcontractDebugFlag) {
                this.dprint(".beginRequest: " + this.opAndId(corbaMessageMediator) + ": created message mediator: " + corbaMessageMediator);
            }
            orb.getInvocationInfo().setMessageMediator(corbaMessageMediator);
            if (corbaConnection != null && corbaConnection.getCodeSetContext() == null) {
                this.performCodeSetNegotiation(corbaMessageMediator);
            }
            this.addServiceContexts(corbaMessageMediator);
            final OutputObject outputObject = contactInfo.createOutputObject(corbaMessageMediator);
            if (orb.subcontractDebugFlag) {
                this.dprint(".beginRequest: " + this.opAndId(corbaMessageMediator) + ": created output object: " + outputObject);
            }
            this.registerWaiter(corbaMessageMediator);
            synchronized (o2) {
                if (contactInfo.isConnectionBased() && contactInfo.shouldCacheConnection()) {
                    orb.getTransportManager().getOutboundConnectionCache(contactInfo).reclaim();
                }
            }
            orb.getPIHandler().setClientPIInfo(corbaMessageMediator);
            try {
                orb.getPIHandler().invokeClientPIStartingPoint();
            }
            catch (final RemarshalException ex2) {
                if (orb.subcontractDebugFlag) {
                    this.dprint(".beginRequest: " + this.opAndId(corbaMessageMediator) + ": Remarshal");
                }
                if (this.getContactInfoListIterator(orb).hasNext()) {
                    contactInfo = (ContactInfo)this.getContactInfoListIterator(orb).next();
                    if (orb.subcontractDebugFlag) {
                        this.dprint("RemarshalException: hasNext true\ncontact info " + contactInfo);
                    }
                    orb.getPIHandler().makeCompletedClientRequest(3, null);
                    this.unregisterWaiter(orb);
                    orb.getPIHandler().cleanupClientPIRequest();
                    return this.beginRequest(o, s, b, contactInfo);
                }
                if (orb.subcontractDebugFlag) {
                    this.dprint("RemarshalException: hasNext false");
                }
                throw ORBUtilSystemException.get(orb, "rpc.protocol").remarshalWithNowhereToGo();
            }
            corbaMessageMediator.initializeMessage();
            if (orb.subcontractDebugFlag) {
                this.dprint(".beginRequest: " + this.opAndId(corbaMessageMediator) + ": initialized message");
            }
            return outputObject;
        }
        finally {
            if (orb.subcontractDebugFlag) {
                this.dprint(".beginRequest<-: op/" + s);
            }
        }
    }
    
    @Override
    public InputObject marshalingComplete(final Object o, final OutputObject outputObject) throws ApplicationException, RemarshalException {
        ORB orb = null;
        MessageMediator messageMediator = null;
        try {
            messageMediator = outputObject.getMessageMediator();
            orb = (ORB)messageMediator.getBroker();
            if (orb.subcontractDebugFlag) {
                this.dprint(".marshalingComplete->: " + this.opAndId((CorbaMessageMediator)messageMediator));
            }
            return this.processResponse(orb, (CorbaMessageMediator)messageMediator, this.marshalingComplete1(orb, (CorbaMessageMediator)messageMediator));
        }
        finally {
            if (orb.subcontractDebugFlag) {
                this.dprint(".marshalingComplete<-: " + this.opAndId((CorbaMessageMediator)messageMediator));
            }
        }
    }
    
    public InputObject marshalingComplete1(final ORB orb, final CorbaMessageMediator corbaMessageMediator) throws ApplicationException, RemarshalException {
        try {
            corbaMessageMediator.finishSendingRequest();
            if (orb.subcontractDebugFlag) {
                this.dprint(".marshalingComplete: " + this.opAndId(corbaMessageMediator) + ": finished sending request");
            }
            return corbaMessageMediator.waitForResponse();
        }
        catch (final RuntimeException ex) {
            if (orb.subcontractDebugFlag) {
                this.dprint(".marshalingComplete: " + this.opAndId(corbaMessageMediator) + ": exception: " + ex.toString());
            }
            final boolean reportException = this.getContactInfoListIterator(orb).reportException(corbaMessageMediator.getContactInfo(), ex);
            final Exception invokeClientPIEndingPoint = orb.getPIHandler().invokeClientPIEndingPoint(2, ex);
            if (reportException) {
                if (invokeClientPIEndingPoint == ex) {
                    this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, new RemarshalException());
                }
                else {
                    this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, invokeClientPIEndingPoint);
                }
                return null;
            }
            if (invokeClientPIEndingPoint instanceof RuntimeException) {
                throw (RuntimeException)invokeClientPIEndingPoint;
            }
            if (invokeClientPIEndingPoint instanceof RemarshalException) {
                throw (RemarshalException)invokeClientPIEndingPoint;
            }
            throw ex;
        }
    }
    
    protected InputObject processResponse(final ORB orb, final CorbaMessageMediator corbaMessageMediator, final InputObject inputObject) throws ApplicationException, RemarshalException {
        final ORBUtilSystemException value = ORBUtilSystemException.get(orb, "rpc.protocol");
        if (orb.subcontractDebugFlag) {
            this.dprint(".processResponse: " + this.opAndId(corbaMessageMediator) + ": response received");
        }
        if (corbaMessageMediator.getConnection() != null) {
            ((CorbaConnection)corbaMessageMediator.getConnection()).setPostInitialContexts();
        }
        Exception ex = null;
        if (corbaMessageMediator.isOneWay()) {
            this.getContactInfoListIterator(orb).reportSuccess(corbaMessageMediator.getContactInfo());
            this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, orb.getPIHandler().invokeClientPIEndingPoint(0, ex));
            return null;
        }
        this.consumeServiceContexts(orb, corbaMessageMediator);
        ((CDRInputObject)inputObject).performORBVersionSpecificInit();
        if (corbaMessageMediator.isSystemExceptionReply()) {
            final SystemException systemExceptionReply = corbaMessageMediator.getSystemExceptionReply();
            if (orb.subcontractDebugFlag) {
                this.dprint(".processResponse: " + this.opAndId(corbaMessageMediator) + ": received system exception: " + systemExceptionReply);
            }
            if (!this.getContactInfoListIterator(orb).reportException(corbaMessageMediator.getContactInfo(), systemExceptionReply)) {
                final ServiceContexts replyServiceContexts = corbaMessageMediator.getReplyServiceContexts();
                if (replyServiceContexts != null) {
                    final UEInfoServiceContext ueInfoServiceContext = (UEInfoServiceContext)replyServiceContexts.get(9);
                    if (ueInfoServiceContext != null) {
                        this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, orb.getPIHandler().invokeClientPIEndingPoint(2, new UnknownException(ueInfoServiceContext.getUE())));
                        throw value.statementNotReachable3();
                    }
                }
                this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, orb.getPIHandler().invokeClientPIEndingPoint(2, systemExceptionReply));
                throw value.statementNotReachable4();
            }
            final Exception invokeClientPIEndingPoint = orb.getPIHandler().invokeClientPIEndingPoint(2, systemExceptionReply);
            if (systemExceptionReply == invokeClientPIEndingPoint) {
                this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, new RemarshalException());
                throw value.statementNotReachable1();
            }
            this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, invokeClientPIEndingPoint);
            throw value.statementNotReachable2();
        }
        else if (corbaMessageMediator.isUserExceptionReply()) {
            if (orb.subcontractDebugFlag) {
                this.dprint(".processResponse: " + this.opAndId(corbaMessageMediator) + ": received user exception");
            }
            this.getContactInfoListIterator(orb).reportSuccess(corbaMessageMediator.getContactInfo());
            final String peekUserExceptionId = this.peekUserExceptionId(inputObject);
            Exception unmarshalDIIUserException;
            Exception diiException;
            if (corbaMessageMediator.isDIIRequest()) {
                unmarshalDIIUserException = corbaMessageMediator.unmarshalDIIUserException(peekUserExceptionId, (InputStream)inputObject);
                diiException = orb.getPIHandler().invokeClientPIEndingPoint(1, unmarshalDIIUserException);
                corbaMessageMediator.setDIIException(diiException);
            }
            else {
                diiException = orb.getPIHandler().invokeClientPIEndingPoint(1, unmarshalDIIUserException = new ApplicationException(peekUserExceptionId, (org.omg.CORBA.portable.InputStream)inputObject));
            }
            if (diiException != unmarshalDIIUserException) {
                this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, diiException);
            }
            if (diiException instanceof ApplicationException) {
                throw (ApplicationException)diiException;
            }
            return inputObject;
        }
        else {
            if (corbaMessageMediator.isLocationForwardReply()) {
                if (orb.subcontractDebugFlag) {
                    this.dprint(".processResponse: " + this.opAndId(corbaMessageMediator) + ": received location forward");
                }
                this.getContactInfoListIterator(orb).reportRedirect((CorbaContactInfo)corbaMessageMediator.getContactInfo(), corbaMessageMediator.getForwardedIOR());
                final Exception invokeClientPIEndingPoint2 = orb.getPIHandler().invokeClientPIEndingPoint(3, null);
                if (!(invokeClientPIEndingPoint2 instanceof RemarshalException)) {
                    ex = invokeClientPIEndingPoint2;
                }
                if (ex != null) {
                    this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, ex);
                }
                this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, new RemarshalException());
                throw value.statementNotReachable5();
            }
            if (corbaMessageMediator.isDifferentAddrDispositionRequestedReply()) {
                if (orb.subcontractDebugFlag) {
                    this.dprint(".processResponse: " + this.opAndId(corbaMessageMediator) + ": received different addressing dispostion request");
                }
                this.getContactInfoListIterator(orb).reportAddrDispositionRetry((CorbaContactInfo)corbaMessageMediator.getContactInfo(), corbaMessageMediator.getAddrDispositionReply());
                final Exception invokeClientPIEndingPoint3 = orb.getPIHandler().invokeClientPIEndingPoint(5, null);
                if (!(invokeClientPIEndingPoint3 instanceof RemarshalException)) {
                    ex = invokeClientPIEndingPoint3;
                }
                if (ex != null) {
                    this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, ex);
                }
                this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, new RemarshalException());
                throw value.statementNotReachable6();
            }
            if (orb.subcontractDebugFlag) {
                this.dprint(".processResponse: " + this.opAndId(corbaMessageMediator) + ": received normal response");
            }
            this.getContactInfoListIterator(orb).reportSuccess(corbaMessageMediator.getContactInfo());
            corbaMessageMediator.handleDIIReply((InputStream)inputObject);
            this.continueOrThrowSystemOrRemarshal(corbaMessageMediator, orb.getPIHandler().invokeClientPIEndingPoint(0, null));
            return inputObject;
        }
    }
    
    protected void continueOrThrowSystemOrRemarshal(final CorbaMessageMediator corbaMessageMediator, final Exception ex) throws SystemException, RemarshalException {
        final ORB orb = (ORB)corbaMessageMediator.getBroker();
        if (ex == null) {
            return;
        }
        if (ex instanceof RemarshalException) {
            orb.getInvocationInfo().setIsRetryInvocation(true);
            this.unregisterWaiter(orb);
            if (orb.subcontractDebugFlag) {
                this.dprint(".continueOrThrowSystemOrRemarshal: " + this.opAndId(corbaMessageMediator) + ": throwing Remarshal");
            }
            throw (RemarshalException)ex;
        }
        if (orb.subcontractDebugFlag) {
            this.dprint(".continueOrThrowSystemOrRemarshal: " + this.opAndId(corbaMessageMediator) + ": throwing sex:" + ex);
        }
        throw (SystemException)ex;
    }
    
    protected CorbaContactInfoListIterator getContactInfoListIterator(final ORB orb) {
        return (CorbaContactInfoListIterator)((CorbaInvocationInfo)orb.getInvocationInfo()).getContactInfoListIterator();
    }
    
    protected void registerWaiter(final CorbaMessageMediator corbaMessageMediator) {
        if (corbaMessageMediator.getConnection() != null) {
            corbaMessageMediator.getConnection().registerWaiter(corbaMessageMediator);
        }
    }
    
    protected void unregisterWaiter(final ORB orb) {
        final MessageMediator messageMediator = orb.getInvocationInfo().getMessageMediator();
        if (messageMediator != null && messageMediator.getConnection() != null) {
            messageMediator.getConnection().unregisterWaiter(messageMediator);
        }
    }
    
    protected void addServiceContexts(final CorbaMessageMediator corbaMessageMediator) {
        final ORB orb = (ORB)corbaMessageMediator.getBroker();
        final CorbaConnection corbaConnection = (CorbaConnection)corbaMessageMediator.getConnection();
        final GIOPVersion giopVersion = corbaMessageMediator.getGIOPVersion();
        final ServiceContexts requestServiceContexts = corbaMessageMediator.getRequestServiceContexts();
        this.addCodeSetServiceContext(corbaConnection, requestServiceContexts, giopVersion);
        requestServiceContexts.put(MaxStreamFormatVersionServiceContext.singleton);
        requestServiceContexts.put(new ORBVersionServiceContext(ORBVersionFactory.getORBVersion()));
        if (corbaConnection != null && !corbaConnection.isPostInitialContexts()) {
            requestServiceContexts.put(new SendingContextServiceContext(orb.getFVDCodeBaseIOR()));
        }
    }
    
    protected void consumeServiceContexts(final ORB orb, final CorbaMessageMediator corbaMessageMediator) {
        final ServiceContexts replyServiceContexts = corbaMessageMediator.getReplyServiceContexts();
        final ORBUtilSystemException value = ORBUtilSystemException.get(orb, "rpc.protocol");
        if (replyServiceContexts == null) {
            return;
        }
        final ServiceContext value2 = replyServiceContexts.get(6);
        if (value2 != null) {
            final IOR ior = ((SendingContextServiceContext)value2).getIOR();
            try {
                if (corbaMessageMediator.getConnection() != null) {
                    ((CorbaConnection)corbaMessageMediator.getConnection()).setCodeBaseIOR(ior);
                }
            }
            catch (final ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (final Throwable t) {
                throw value.badStringifiedIor(t);
            }
        }
        final ServiceContext value3 = replyServiceContexts.get(1313165056);
        if (value3 != null) {
            orb.setORBVersion(((ORBVersionServiceContext)value3).getVersion());
        }
        this.getExceptionDetailMessage(corbaMessageMediator, value);
    }
    
    protected void getExceptionDetailMessage(final CorbaMessageMediator corbaMessageMediator, final ORBUtilSystemException ex) {
        final ServiceContext value = corbaMessageMediator.getReplyServiceContexts().get(14);
        if (value == null) {
            return;
        }
        if (!(value instanceof UnknownServiceContext)) {
            throw ex.badExceptionDetailMessageServiceContextType();
        }
        final byte[] data = ((UnknownServiceContext)value).getData();
        final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream((org.omg.CORBA.ORB)corbaMessageMediator.getBroker(), data, data.length);
        encapsInputStream.consumeEndian();
        corbaMessageMediator.setReplyExceptionDetailMessage("----------BEGIN server-side stack trace----------\n" + encapsInputStream.read_wstring() + "\n----------END server-side stack trace----------");
    }
    
    @Override
    public void endRequest(final Broker broker, final Object o, final InputObject inputObject) {
        final ORB orb = (ORB)broker;
        try {
            if (orb.subcontractDebugFlag) {
                this.dprint(".endRequest->");
            }
            final MessageMediator messageMediator = orb.getInvocationInfo().getMessageMediator();
            if (messageMediator != null) {
                if (messageMediator.getConnection() != null) {
                    ((CorbaMessageMediator)messageMediator).sendCancelRequestIfFinalFragmentNotSent();
                }
                try (final InputObject inputObject2 = messageMediator.getInputObject()) {}
                try (final OutputObject outputObject = messageMediator.getOutputObject()) {}
            }
            this.unregisterWaiter(orb);
            orb.getPIHandler().cleanupClientPIRequest();
        }
        catch (final IOException ex) {
            if (orb.subcontractDebugFlag) {
                this.dprint(".endRequest: ignoring IOException - " + ex.toString());
            }
        }
        finally {
            if (orb.subcontractDebugFlag) {
                this.dprint(".endRequest<-");
            }
        }
    }
    
    protected void performCodeSetNegotiation(final CorbaMessageMediator corbaMessageMediator) {
        final CorbaConnection corbaConnection = (CorbaConnection)corbaMessageMediator.getConnection();
        final IOR effectiveTargetIOR = ((CorbaContactInfo)corbaMessageMediator.getContactInfo()).getEffectiveTargetIOR();
        final GIOPVersion giopVersion = corbaMessageMediator.getGIOPVersion();
        if (corbaConnection != null && corbaConnection.getCodeSetContext() == null && !giopVersion.equals(GIOPVersion.V1_0)) {
            synchronized (corbaConnection) {
                if (corbaConnection.getCodeSetContext() != null) {
                    return;
                }
                final Iterator iteratorById = effectiveTargetIOR.getProfile().getTaggedProfileTemplate().iteratorById(1);
                if (!iteratorById.hasNext()) {
                    return;
                }
                corbaConnection.setCodeSetContext(CodeSetConversion.impl().negotiate(corbaConnection.getBroker().getORBData().getCodeSetComponentInfo(), ((CodeSetsComponent)iteratorById.next()).getCodeSetComponentInfo()));
            }
        }
    }
    
    protected void addCodeSetServiceContext(final CorbaConnection corbaConnection, final ServiceContexts serviceContexts, final GIOPVersion giopVersion) {
        if (giopVersion.equals(GIOPVersion.V1_0) || corbaConnection == null) {
            return;
        }
        CodeSetComponentInfo.CodeSetContext codeSetContext = null;
        if (corbaConnection.getBroker().getORBData().alwaysSendCodeSetServiceContext() || !corbaConnection.isPostInitialContexts()) {
            codeSetContext = corbaConnection.getCodeSetContext();
        }
        if (codeSetContext == null) {
            return;
        }
        serviceContexts.put(new CodeSetServiceContext(codeSetContext));
    }
    
    protected String peekUserExceptionId(final InputObject inputObject) {
        final CDRInputObject cdrInputObject = (CDRInputObject)inputObject;
        cdrInputObject.mark(Integer.MAX_VALUE);
        final String read_string = cdrInputObject.read_string();
        cdrInputObject.reset();
        return read_string;
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaClientRequestDispatcherImpl", s);
    }
    
    protected String opAndId(final CorbaMessageMediator corbaMessageMediator) {
        return ORBUtility.operationNameAndRequestId(corbaMessageMediator);
    }
}
