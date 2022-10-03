package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.oa.OADefault;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.spi.protocol.RequestDispatcherDefault;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.copyobject.CopyobjectDefaults;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.impl.dynamicany.DynAnyFactoryImpl;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedExceptionAction;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryContactInfoListImpl;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import java.util.Iterator;
import java.util.Collection;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.orb.ORBData;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.transport.TransportDefault;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORBConfigurator;

public class ORBConfiguratorImpl implements ORBConfigurator
{
    private ORBUtilSystemException wrapper;
    private static final int ORB_STREAM = 0;
    
    @Override
    public void configure(final DataCollector dataCollector, final ORB orb) {
        this.wrapper = ORBUtilSystemException.get(orb, "orb.lifecycle");
        this.initObjectCopiers(orb);
        this.initIORFinders(orb);
        orb.setClientDelegateFactory(TransportDefault.makeClientDelegateFactory(orb));
        this.initializeTransport(orb);
        this.initializeNaming(orb);
        this.initServiceContextRegistry(orb);
        this.initRequestDispatcherRegistry(orb);
        this.registerInitialReferences(orb);
        this.persistentServerInitialization(orb);
        this.runUserConfigurators(dataCollector, orb);
    }
    
    private void runUserConfigurators(final DataCollector dataCollector, final ORB orb) {
        final ConfigParser configParser = new ConfigParser();
        configParser.init(dataCollector);
        if (configParser.userConfigurators != null) {
            for (int i = 0; i < configParser.userConfigurators.length; ++i) {
                final Class clazz = configParser.userConfigurators[i];
                try {
                    ((ORBConfigurator)clazz.newInstance()).configure(dataCollector, orb);
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private void persistentServerInitialization(final ORB orb) {
        final ORBData orbData = orb.getORBData();
        if (orbData.getServerIsORBActivated()) {
            try {
                final Locator narrow = LocatorHelper.narrow(orb.resolve_initial_references("ServerLocator"));
                final Activator narrow2 = ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator"));
                final Collection acceptors = orb.getCorbaTransportManager().getAcceptors(null, null);
                final EndPointInfo[] array = new EndPointInfo[acceptors.size()];
                final Iterator iterator = acceptors.iterator();
                int n = 0;
                while (iterator.hasNext()) {
                    final Object next = iterator.next();
                    if (!(next instanceof LegacyServerSocketEndPointInfo)) {
                        continue;
                    }
                    final LegacyServerSocketEndPointInfo legacyServerSocketEndPointInfo = (LegacyServerSocketEndPointInfo)next;
                    int locatorPort = narrow.getEndpoint(legacyServerSocketEndPointInfo.getType());
                    if (locatorPort == -1) {
                        locatorPort = narrow.getEndpoint("IIOP_CLEAR_TEXT");
                        if (locatorPort == -1) {
                            throw new Exception("ORBD must support IIOP_CLEAR_TEXT");
                        }
                    }
                    legacyServerSocketEndPointInfo.setLocatorPort(locatorPort);
                    array[n++] = new EndPointInfo(legacyServerSocketEndPointInfo.getType(), legacyServerSocketEndPointInfo.getPort());
                }
                narrow2.registerEndpoints(orbData.getPersistentServerId(), orbData.getORBId(), array);
            }
            catch (final Exception ex) {
                throw this.wrapper.persistentServerInitError(CompletionStatus.COMPLETED_MAYBE, ex);
            }
        }
    }
    
    private void initializeTransport(final ORB orb) {
        final ORBData orbData = orb.getORBData();
        CorbaContactInfoListFactory corbaContactInfoListFactory = orbData.getCorbaContactInfoListFactory();
        final Acceptor[] acceptors = orbData.getAcceptors();
        final ORBSocketFactory legacySocketFactory = orbData.getLegacySocketFactory();
        orbData.getUserSpecifiedListenPorts();
        this.setLegacySocketFactoryORB(orb, legacySocketFactory);
        if (legacySocketFactory != null && corbaContactInfoListFactory != null) {
            throw this.wrapper.socketFactoryAndContactInfoListAtSameTime();
        }
        if (acceptors.length != 0 && legacySocketFactory != null) {
            throw this.wrapper.acceptorsAndLegacySocketFactoryAtSameTime();
        }
        orbData.getSocketFactory().setORB(orb);
        if (legacySocketFactory != null) {
            corbaContactInfoListFactory = new CorbaContactInfoListFactory() {
                @Override
                public void setORB(final ORB orb) {
                }
                
                @Override
                public CorbaContactInfoList create(final IOR ior) {
                    return new SocketFactoryContactInfoListImpl(orb, ior);
                }
            };
        }
        else if (corbaContactInfoListFactory != null) {
            corbaContactInfoListFactory.setORB(orb);
        }
        else {
            corbaContactInfoListFactory = TransportDefault.makeCorbaContactInfoListFactory(orb);
        }
        orb.setCorbaContactInfoListFactory(corbaContactInfoListFactory);
        int n = -1;
        if (orbData.getORBServerPort() != 0) {
            n = orbData.getORBServerPort();
        }
        else if (orbData.getPersistentPortInitialized()) {
            n = orbData.getPersistentServerPort();
        }
        else if (acceptors.length == 0) {
            n = 0;
        }
        if (n != -1) {
            this.createAndRegisterAcceptor(orb, legacySocketFactory, n, "DEFAULT_ENDPOINT", "IIOP_CLEAR_TEXT");
        }
        for (int i = 0; i < acceptors.length; ++i) {
            orb.getCorbaTransportManager().registerAcceptor(acceptors[i]);
        }
        final USLPort[] userSpecifiedListenPorts = orbData.getUserSpecifiedListenPorts();
        if (userSpecifiedListenPorts != null) {
            for (int j = 0; j < userSpecifiedListenPorts.length; ++j) {
                this.createAndRegisterAcceptor(orb, legacySocketFactory, userSpecifiedListenPorts[j].getPort(), "NO_NAME", userSpecifiedListenPorts[j].getType());
            }
        }
    }
    
    private void createAndRegisterAcceptor(final ORB orb, final ORBSocketFactory orbSocketFactory, final int n, final String s, final String s2) {
        SocketOrChannelAcceptorImpl socketOrChannelAcceptorImpl;
        if (orbSocketFactory == null) {
            socketOrChannelAcceptorImpl = new SocketOrChannelAcceptorImpl(orb, n, s, s2);
        }
        else {
            socketOrChannelAcceptorImpl = new SocketFactoryAcceptorImpl(orb, n, s, s2);
        }
        orb.getTransportManager().registerAcceptor(socketOrChannelAcceptorImpl);
    }
    
    private void setLegacySocketFactoryORB(final ORB orb, final ORBSocketFactory orbSocketFactory) {
        if (orbSocketFactory == null) {
            return;
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                @Override
                public Object run() throws InstantiationException, IllegalAccessException {
                    try {
                        orbSocketFactory.getClass().getMethod("setORB", ORB.class).invoke(orbSocketFactory, orb);
                    }
                    catch (final NoSuchMethodException ex) {}
                    catch (final IllegalAccessException ex2) {
                        final RuntimeException ex3 = new RuntimeException();
                        ex3.initCause(ex2);
                        throw ex3;
                    }
                    catch (final InvocationTargetException ex4) {
                        final RuntimeException ex5 = new RuntimeException();
                        ex5.initCause(ex4);
                        throw ex5;
                    }
                    return null;
                }
            });
        }
        catch (final Throwable t) {
            throw this.wrapper.unableToSetSocketFactoryOrb(t);
        }
    }
    
    private void initializeNaming(final ORB orb) {
        final LocalResolver localResolver = ResolverDefault.makeLocalResolver();
        orb.setLocalResolver(localResolver);
        final Resolver bootstrapResolver = ResolverDefault.makeBootstrapResolver(orb, orb.getORBData().getORBInitialHost(), orb.getORBData().getORBInitialPort());
        final Operation insurlOperation = ResolverDefault.makeINSURLOperation(orb, bootstrapResolver);
        orb.setURLOperation(insurlOperation);
        orb.setResolver(ResolverDefault.makeCompositeResolver(localResolver, ResolverDefault.makeCompositeResolver(ResolverDefault.makeORBInitRefResolver(insurlOperation, orb.getORBData().getORBInitialReferences()), ResolverDefault.makeCompositeResolver(ResolverDefault.makeORBDefaultInitRefResolver(insurlOperation, orb.getORBData().getORBDefaultInitialReference()), bootstrapResolver))));
    }
    
    private void initServiceContextRegistry(final ORB orb) {
        final ServiceContextRegistry serviceContextRegistry = orb.getServiceContextRegistry();
        serviceContextRegistry.register(UEInfoServiceContext.class);
        serviceContextRegistry.register(CodeSetServiceContext.class);
        serviceContextRegistry.register(SendingContextServiceContext.class);
        serviceContextRegistry.register(ORBVersionServiceContext.class);
        serviceContextRegistry.register(MaxStreamFormatVersionServiceContext.class);
    }
    
    private void registerInitialReferences(final ORB orb) {
        orb.getLocalResolver().register("DynAnyFactory", ClosureFactory.makeFuture(new Closure() {
            @Override
            public Object evaluate() {
                return new DynAnyFactoryImpl(orb);
            }
        }));
    }
    
    private void initObjectCopiers(final ORB orb) {
        final ObjectCopierFactory orbStreamObjectCopierFactory = CopyobjectDefaults.makeORBStreamObjectCopierFactory(orb);
        final CopierManager copierManager = orb.getCopierManager();
        copierManager.setDefaultId(0);
        copierManager.registerObjectCopierFactory(orbStreamObjectCopierFactory, 0);
    }
    
    private void initIORFinders(final ORB orb) {
        orb.getTaggedProfileFactoryFinder().registerFactory(IIOPFactories.makeIIOPProfileFactory());
        orb.getTaggedProfileTemplateFactoryFinder().registerFactory(IIOPFactories.makeIIOPProfileTemplateFactory());
        final TaggedComponentFactoryFinder taggedComponentFactoryFinder = orb.getTaggedComponentFactoryFinder();
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeCodeSetsComponentFactory());
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeJavaCodebaseComponentFactory());
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeORBTypeComponentFactory());
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeMaxStreamFormatVersionComponentFactory());
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeAlternateIIOPAddressComponentFactory());
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeRequestPartitioningComponentFactory());
        taggedComponentFactoryFinder.registerFactory(IIOPFactories.makeJavaSerializationComponentFactory());
        IORFactories.registerValueFactories(orb);
        orb.setObjectKeyFactory(IORFactories.makeObjectKeyFactory(orb));
    }
    
    private void initRequestDispatcherRegistry(final ORB orb) {
        final RequestDispatcherRegistry requestDispatcherRegistry = orb.getRequestDispatcherRegistry();
        final ClientRequestDispatcher clientRequestDispatcher = RequestDispatcherDefault.makeClientRequestDispatcher();
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 2);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 32);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.PERSISTENT_SCID);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 36);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.SC_PERSISTENT_SCID);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 40);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.IISC_PERSISTENT_SCID);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, 44);
        requestDispatcherRegistry.registerClientRequestDispatcher(clientRequestDispatcher, ORBConstants.MINSC_PERSISTENT_SCID);
        final CorbaServerRequestDispatcher serverRequestDispatcher = RequestDispatcherDefault.makeServerRequestDispatcher(orb);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, 2);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, 32);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, ORBConstants.PERSISTENT_SCID);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, 36);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, ORBConstants.SC_PERSISTENT_SCID);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, 40);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, ORBConstants.IISC_PERSISTENT_SCID);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, 44);
        requestDispatcherRegistry.registerServerRequestDispatcher(serverRequestDispatcher, ORBConstants.MINSC_PERSISTENT_SCID);
        orb.setINSDelegate(RequestDispatcherDefault.makeINSServerRequestDispatcher(orb));
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(RequestDispatcherDefault.makeJIDLLocalClientRequestDispatcherFactory(orb), 2);
        final LocalClientRequestDispatcherFactory poaLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makePOALocalClientRequestDispatcherFactory(orb);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(poaLocalClientRequestDispatcherFactory, 32);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(poaLocalClientRequestDispatcherFactory, ORBConstants.PERSISTENT_SCID);
        final LocalClientRequestDispatcherFactory fullServantCacheLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeFullServantCacheLocalClientRequestDispatcherFactory(orb);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(fullServantCacheLocalClientRequestDispatcherFactory, 36);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(fullServantCacheLocalClientRequestDispatcherFactory, ORBConstants.SC_PERSISTENT_SCID);
        final LocalClientRequestDispatcherFactory infoOnlyServantCacheLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(orb);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(infoOnlyServantCacheLocalClientRequestDispatcherFactory, 40);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(infoOnlyServantCacheLocalClientRequestDispatcherFactory, ORBConstants.IISC_PERSISTENT_SCID);
        final LocalClientRequestDispatcherFactory minimalServantCacheLocalClientRequestDispatcherFactory = RequestDispatcherDefault.makeMinimalServantCacheLocalClientRequestDispatcherFactory(orb);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(minimalServantCacheLocalClientRequestDispatcherFactory, 44);
        requestDispatcherRegistry.registerLocalClientRequestDispatcherFactory(minimalServantCacheLocalClientRequestDispatcherFactory, ORBConstants.MINSC_PERSISTENT_SCID);
        final CorbaServerRequestDispatcher bootstrapServerRequestDispatcher = RequestDispatcherDefault.makeBootstrapServerRequestDispatcher(orb);
        requestDispatcherRegistry.registerServerRequestDispatcher(bootstrapServerRequestDispatcher, "INIT");
        requestDispatcherRegistry.registerServerRequestDispatcher(bootstrapServerRequestDispatcher, "TINI");
        requestDispatcherRegistry.registerObjectAdapterFactory(OADefault.makeTOAFactory(orb), 2);
        final ObjectAdapterFactory poaFactory = OADefault.makePOAFactory(orb);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, 32);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, ORBConstants.PERSISTENT_SCID);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, 36);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, ORBConstants.SC_PERSISTENT_SCID);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, 40);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, ORBConstants.IISC_PERSISTENT_SCID);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, 44);
        requestDispatcherRegistry.registerObjectAdapterFactory(poaFactory, ORBConstants.MINSC_PERSISTENT_SCID);
    }
    
    public static class ConfigParser extends ParserImplBase
    {
        public Class[] userConfigurators;
        
        public ConfigParser() {
            this.userConfigurators = null;
        }
        
        public PropertyParser makeParser() {
            final PropertyParser propertyParser = new PropertyParser();
            propertyParser.addPrefix("com.sun.CORBA.ORBUserConfigurators", OperationFactory.compose(OperationFactory.suffixAction(), OperationFactory.classAction()), "userConfigurators", Class.class);
            return propertyParser;
        }
    }
}
