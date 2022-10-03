package com.sun.corba.se.impl.orb;

import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.CORBA.LocalObject;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.pept.transport.ContactInfo;
import java.util.List;
import java.net.InetSocketAddress;
import java.net.Socket;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.ORB;
import java.net.ServerSocket;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import sun.corba.SharedSecrets;
import java.util.HashMap;
import java.util.Map;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.impl.transport.DefaultIORToSocketInfoImpl;
import com.sun.corba.se.impl.transport.DefaultSocketFactoryImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ParserDataFactory;
import com.sun.corba.se.spi.orb.OperationFactory;
import java.net.URL;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.StringPair;
import org.omg.PortableInterceptor.ORBInitializer;
import com.sun.corba.se.spi.transport.TransportDefault;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.spi.orb.ParserData;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class ParserTable
{
    private static String MY_CLASS_NAME;
    private static ParserTable myInstance;
    private ORBUtilSystemException wrapper;
    private ParserData[] parserData;
    
    public static ParserTable get() {
        return ParserTable.myInstance;
    }
    
    public ParserData[] getParserData() {
        final ParserData[] array = new ParserData[this.parserData.length];
        System.arraycopy(this.parserData, 0, array, 0, this.parserData.length);
        return array;
    }
    
    private ParserTable() {
        this.wrapper = ORBUtilSystemException.get("orb.lifecycle");
        final String s = "65537,65801,65568";
        final String[] array = { "subcontract", "poa", "transport" };
        final USLPort[] array2 = { new USLPort("FOO", 2701), new USLPort("BAR", 3333) };
        final ReadTimeouts create = TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20);
        final ORBInitializer[] array3 = { null, new TestORBInitializer1(), new TestORBInitializer2() };
        final StringPair[] array4 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(ParserTable.MY_CLASS_NAME + "$TestORBInitializer1", "dummy"), new StringPair(ParserTable.MY_CLASS_NAME + "$TestORBInitializer2", "dummy") };
        final Acceptor[] array5 = { new TestAcceptor2(), new TestAcceptor1(), null };
        final StringPair[] array6 = { new StringPair("foo.bar.blech.NonExistent", "dummy"), new StringPair(ParserTable.MY_CLASS_NAME + "$TestAcceptor1", "dummy"), new StringPair(ParserTable.MY_CLASS_NAME + "$TestAcceptor2", "dummy") };
        final StringPair[] array7 = { new StringPair("Foo", "ior:930492049394"), new StringPair("Bar", "ior:3453465785633576") };
        final String s2 = "corbaloc::camelot/NameService";
        try {
            final URL url = new URL(s2);
        }
        catch (final Exception ex) {}
        this.parserData = new ParserData[] { ParserDataFactory.make("com.sun.CORBA.ORBDebug", OperationFactory.listAction(",", OperationFactory.stringAction()), "debugFlags", new String[0], array, "subcontract,poa,transport"), ParserDataFactory.make("org.omg.CORBA.ORBInitialHost", OperationFactory.stringAction(), "ORBInitialHost", "", "Foo", "Foo"), ParserDataFactory.make("org.omg.CORBA.ORBInitialPort", OperationFactory.integerAction(), "ORBInitialPort", new Integer(900), new Integer(27314), "27314"), ParserDataFactory.make("com.sun.CORBA.ORBServerHost", OperationFactory.stringAction(), "ORBServerHost", "", "camelot", "camelot"), ParserDataFactory.make("com.sun.CORBA.ORBServerPort", OperationFactory.integerAction(), "ORBServerPort", new Integer(0), new Integer(38143), "38143"), ParserDataFactory.make("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", OperationFactory.stringAction(), "listenOnAllInterfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBId", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.ORBid", OperationFactory.stringAction(), "orbId", "", "foo", "foo"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(-1), new Integer(1234), "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("org.omg.CORBA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "1234"), ParserDataFactory.make("com.sun.CORBA.connection.ORBHighWaterMark", OperationFactory.integerAction(), "highWaterMark", new Integer(240), new Integer(3745), "3745"), ParserDataFactory.make("com.sun.CORBA.connection.ORBLowWaterMark", OperationFactory.integerAction(), "lowWaterMark", new Integer(100), new Integer(12), "12"), ParserDataFactory.make("com.sun.CORBA.connection.ORBNumberToReclaim", OperationFactory.integerAction(), "numberToReclaim", new Integer(5), new Integer(231), "231"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOPVersion", this.makeGVOperation(), "giopVersion", GIOPVersion.DEFAULT_VERSION, new GIOPVersion(2, 3), "2.3"), ParserDataFactory.make("com.sun.CORBA.giop.ORBFragmentSize", this.makeFSOperation(), "giopFragmentSize", new Integer(1024), new Integer(65536), "65536"), ParserDataFactory.make("com.sun.CORBA.giop.ORBBufferSize", OperationFactory.integerAction(), "giopBufferSize", new Integer(1024), new Integer(234000), "234000"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP11BuffMgr", this.makeBMGROperation(), "giop11BuffMgr", new Integer(0), new Integer(1), "CLCT"), ParserDataFactory.make("com.sun.CORBA.giop.ORBGIOP12BuffMgr", this.makeBMGROperation(), "giop12BuffMgr", new Integer(2), new Integer(0), "GROW"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", OperationFactory.compose(OperationFactory.integerRangeAction(0, 3), OperationFactory.convertIntegerToShort()), "giopTargetAddressPreference", new Short((short)3), new Short((short)2), "2"), ParserDataFactory.make("com.sun.CORBA.giop.ORBTargetAddressing", this.makeADOperation(), "giopAddressDisposition", new Short((short)0), new Short((short)2), "2"), ParserDataFactory.make("com.sun.CORBA.codeset.AlwaysSendCodeSetCtx", OperationFactory.booleanAction(), "alwaysSendCodeSetCtx", Boolean.TRUE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkers", OperationFactory.booleanAction(), "useByteOrderMarkers", true, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.UseByteOrderMarkersInEncaps", OperationFactory.booleanAction(), "useByteOrderMarkersInEncaps", false, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.codeset.charsets", this.makeCSOperation(), "charData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getCharComponent(), CodeSetComponentInfo.createFromString(s), s), ParserDataFactory.make("com.sun.CORBA.codeset.wcharsets", this.makeCSOperation(), "wcharData", CodeSetComponentInfo.JAVASOFT_DEFAULT_CODESETS.getWCharComponent(), CodeSetComponentInfo.createFromString(s), s), ParserDataFactory.make("com.sun.CORBA.ORBAllowLocalOptimization", OperationFactory.booleanAction(), "allowLocalOptimization", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.legacy.connection.ORBSocketFactoryClass", this.makeLegacySocketFactoryOperation(), "legacySocketFactory", null, new TestLegacyORBSocketFactory(), ParserTable.MY_CLASS_NAME + "$TestLegacyORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBSocketFactoryClass", this.makeSocketFactoryOperation(), "socketFactory", new DefaultSocketFactoryImpl(), new TestORBSocketFactory(), ParserTable.MY_CLASS_NAME + "$TestORBSocketFactory"), ParserDataFactory.make("com.sun.CORBA.transport.ORBListenSocket", this.makeUSLOperation(), "userSpecifiedListenPorts", new USLPort[0], array2, "FOO:2701,BAR:3333"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIORToSocketInfoClass", this.makeIORToSocketInfoOperation(), "iorToSocketInfo", new DefaultIORToSocketInfoImpl(), new TestIORToSocketInfo(), ParserTable.MY_CLASS_NAME + "$TestIORToSocketInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBIIOPPrimaryToContactInfoClass", this.makeIIOPPrimaryToContactInfoOperation(), "iiopPrimaryToContactInfo", null, new TestIIOPPrimaryToContactInfo(), ParserTable.MY_CLASS_NAME + "$TestIIOPPrimaryToContactInfo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBContactInfoList", this.makeContactInfoListFactoryOperation(), "corbaContactInfoListFactory", null, new TestContactInfoListFactory(), ParserTable.MY_CLASS_NAME + "$TestContactInfoListFactory"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.integerAction(), "persistentServerPort", new Integer(0), new Integer(2743), "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBPersistentServerPort", OperationFactory.setFlagAction(), "persistentPortInitialized", Boolean.FALSE, Boolean.TRUE, "2743"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.integerAction(), "persistentServerId", new Integer(0), new Integer(294), "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "persistentServerIdInitialized", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBServerId", OperationFactory.setFlagAction(), "orbServerIdPropertySpecified", Boolean.FALSE, Boolean.TRUE, "294"), ParserDataFactory.make("com.sun.CORBA.POA.ORBActivated", OperationFactory.booleanAction(), "serverIsORBActivated", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.POA.ORBBadServerIdHandlerClass", OperationFactory.classAction(), "badServerIdHandlerClass", null, TestBadServerIdHandler.class, ParserTable.MY_CLASS_NAME + "$TestBadServerIdHandler"), ParserDataFactory.make("org.omg.PortableInterceptor.ORBInitializerClass.", this.makeROIOperation(), "orbInitializers", new ORBInitializer[0], array3, array4, ORBInitializer.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptor", this.makeAcceptorInstantiationOperation(), "acceptors", new Acceptor[0], array5, array6, Acceptor.class), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketType", OperationFactory.stringAction(), "acceptorSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "acceptorSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBAcceptorSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "acceptorSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketType", OperationFactory.stringAction(), "connectionSocketType", "SocketChannel", "foo", "foo"), ParserDataFactory.make("com.sun.CORBA.transport.ORBUseNIOSelectToWait", OperationFactory.booleanAction(), "connectionSocketUseSelectThreadToWait", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBConnectionSocketUseWorkerThreadForEvent", OperationFactory.booleanAction(), "connectionSocketUseWorkerThreadForEvent", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBDisableDirectByteBufferUse", OperationFactory.booleanAction(), "disableDirectByteBufferUse", Boolean.FALSE, Boolean.TRUE, "true"), ParserDataFactory.make("com.sun.CORBA.transport.ORBTCPReadTimeouts", this.makeTTCPRTOperation(), "readTimeouts", TransportDefault.makeReadTimeoutsFactory().create(100, 3000, 300, 20), create, "100:3000:300:20"), ParserDataFactory.make("com.sun.CORBA.encoding.ORBEnableJavaSerialization", OperationFactory.booleanAction(), "enableJavaSerialization", Boolean.FALSE, Boolean.FALSE, "false"), ParserDataFactory.make("com.sun.CORBA.ORBUseRepId", OperationFactory.booleanAction(), "useRepId", Boolean.TRUE, Boolean.TRUE, "true"), ParserDataFactory.make("org.omg.CORBA.ORBInitRef", OperationFactory.identityAction(), "orbInitialReferences", new StringPair[0], array7, array7, StringPair.class) };
    }
    
    private Operation makeTTCPRTOperation() {
        return OperationFactory.compose(OperationFactory.sequenceAction(":", new Operation[] { OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction(), OperationFactory.integerAction() }), new Operation() {
            @Override
            public Object operate(final Object o) {
                final Object[] array = (Object[])o;
                return TransportDefault.makeReadTimeoutsFactory().create((int)array[0], (int)array[1], (int)array[2], (int)array[3]);
            }
        });
    }
    
    private Operation makeUSLOperation() {
        return OperationFactory.listAction(",", OperationFactory.compose(OperationFactory.sequenceAction(":", new Operation[] { OperationFactory.stringAction(), OperationFactory.integerAction() }), new Operation() {
            @Override
            public Object operate(final Object o) {
                final Object[] array = (Object[])o;
                return new USLPort((String)array[0], (int)array[1]);
            }
        }));
    }
    
    private Operation makeMapOperation(final Map map) {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                return map.get(o);
            }
        };
    }
    
    private Operation makeBMGROperation() {
        final HashMap hashMap = new HashMap();
        hashMap.put("GROW", new Integer(0));
        hashMap.put("CLCT", new Integer(1));
        hashMap.put("STRM", new Integer(2));
        return this.makeMapOperation(hashMap);
    }
    
    private Operation makeLegacySocketFactoryOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                final String s = (String)o;
                try {
                    final Class<?> loadClass = SharedSecrets.getJavaCorbaAccess().loadClass(s);
                    if (ORBSocketFactory.class.isAssignableFrom(loadClass)) {
                        return loadClass.newInstance();
                    }
                    throw ParserTable.this.wrapper.illegalSocketFactoryType(loadClass.toString());
                }
                catch (final Exception ex) {
                    throw ParserTable.this.wrapper.badCustomSocketFactory(ex, s);
                }
            }
        };
    }
    
    private Operation makeSocketFactoryOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                final String s = (String)o;
                try {
                    final Class<?> loadClass = SharedSecrets.getJavaCorbaAccess().loadClass(s);
                    if (com.sun.corba.se.spi.transport.ORBSocketFactory.class.isAssignableFrom(loadClass)) {
                        return loadClass.newInstance();
                    }
                    throw ParserTable.this.wrapper.illegalSocketFactoryType(loadClass.toString());
                }
                catch (final Exception ex) {
                    throw ParserTable.this.wrapper.badCustomSocketFactory(ex, s);
                }
            }
        };
    }
    
    private Operation makeIORToSocketInfoOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                final String s = (String)o;
                try {
                    final Class<?> loadClass = SharedSecrets.getJavaCorbaAccess().loadClass(s);
                    if (IORToSocketInfo.class.isAssignableFrom(loadClass)) {
                        return loadClass.newInstance();
                    }
                    throw ParserTable.this.wrapper.illegalIorToSocketInfoType(loadClass.toString());
                }
                catch (final Exception ex) {
                    throw ParserTable.this.wrapper.badCustomIorToSocketInfo(ex, s);
                }
            }
        };
    }
    
    private Operation makeIIOPPrimaryToContactInfoOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                final String s = (String)o;
                try {
                    final Class<?> loadClass = SharedSecrets.getJavaCorbaAccess().loadClass(s);
                    if (IIOPPrimaryToContactInfo.class.isAssignableFrom(loadClass)) {
                        return loadClass.newInstance();
                    }
                    throw ParserTable.this.wrapper.illegalIiopPrimaryToContactInfoType(loadClass.toString());
                }
                catch (final Exception ex) {
                    throw ParserTable.this.wrapper.badCustomIiopPrimaryToContactInfo(ex, s);
                }
            }
        };
    }
    
    private Operation makeContactInfoListFactoryOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                final String s = (String)o;
                try {
                    final Class<?> loadClass = SharedSecrets.getJavaCorbaAccess().loadClass(s);
                    if (CorbaContactInfoListFactory.class.isAssignableFrom(loadClass)) {
                        return loadClass.newInstance();
                    }
                    throw ParserTable.this.wrapper.illegalContactInfoListFactoryType(loadClass.toString());
                }
                catch (final Exception ex) {
                    throw ParserTable.this.wrapper.badContactInfoListFactory(ex, s);
                }
            }
        };
    }
    
    private Operation makeCSOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                return CodeSetComponentInfo.createFromString((String)o);
            }
        };
    }
    
    private Operation makeADOperation() {
        return OperationFactory.compose(OperationFactory.compose(OperationFactory.integerRangeAction(0, 3), new Operation() {
            private Integer[] map = { new Integer(0), new Integer(1), new Integer(2), new Integer(0) };
            
            @Override
            public Object operate(final Object o) {
                return this.map[(int)o];
            }
        }), OperationFactory.convertIntegerToShort());
    }
    
    private Operation makeFSOperation() {
        return OperationFactory.compose(OperationFactory.integerAction(), new Operation() {
            @Override
            public Object operate(final Object o) {
                final int intValue = (int)o;
                if (intValue < 32) {
                    throw ParserTable.this.wrapper.fragmentSizeMinimum(new Integer(intValue), new Integer(32));
                }
                if (intValue % 8 != 0) {
                    throw ParserTable.this.wrapper.fragmentSizeDiv(new Integer(intValue), new Integer(8));
                }
                return o;
            }
        });
    }
    
    private Operation makeGVOperation() {
        return OperationFactory.compose(OperationFactory.listAction(".", OperationFactory.integerAction()), new Operation() {
            @Override
            public Object operate(final Object o) {
                final Object[] array = (Object[])o;
                return new GIOPVersion((int)array[0], (int)array[1]);
            }
        });
    }
    
    private Operation makeROIOperation() {
        return OperationFactory.compose(OperationFactory.maskErrorAction(OperationFactory.compose(OperationFactory.suffixAction(), OperationFactory.classAction())), new Operation() {
            @Override
            public Object operate(final Object o) {
                final Class clazz = (Class)o;
                if (clazz == null) {
                    return null;
                }
                if (ORBInitializer.class.isAssignableFrom(clazz)) {
                    ORBInitializer orbInitializer;
                    try {
                        orbInitializer = AccessController.doPrivileged((PrivilegedExceptionAction<ORBInitializer>)new PrivilegedExceptionAction() {
                            @Override
                            public Object run() throws InstantiationException, IllegalAccessException {
                                return clazz.newInstance();
                            }
                        });
                    }
                    catch (final PrivilegedActionException ex) {
                        throw ParserTable.this.wrapper.orbInitializerFailure(ex.getException(), clazz.getName());
                    }
                    catch (final Exception ex2) {
                        throw ParserTable.this.wrapper.orbInitializerFailure(ex2, clazz.getName());
                    }
                    return orbInitializer;
                }
                throw ParserTable.this.wrapper.orbInitializerType(clazz.getName());
            }
        });
    }
    
    private Operation makeAcceptorInstantiationOperation() {
        return OperationFactory.compose(OperationFactory.maskErrorAction(OperationFactory.compose(OperationFactory.suffixAction(), OperationFactory.classAction())), new Operation() {
            @Override
            public Object operate(final Object o) {
                final Class clazz = (Class)o;
                if (clazz == null) {
                    return null;
                }
                if (Acceptor.class.isAssignableFrom(clazz)) {
                    Acceptor acceptor;
                    try {
                        acceptor = AccessController.doPrivileged((PrivilegedExceptionAction<Acceptor>)new PrivilegedExceptionAction() {
                            @Override
                            public Object run() throws InstantiationException, IllegalAccessException {
                                return clazz.newInstance();
                            }
                        });
                    }
                    catch (final PrivilegedActionException ex) {
                        throw ParserTable.this.wrapper.acceptorInstantiationFailure(ex.getException(), clazz.getName());
                    }
                    catch (final Exception ex2) {
                        throw ParserTable.this.wrapper.acceptorInstantiationFailure(ex2, clazz.getName());
                    }
                    return acceptor;
                }
                throw ParserTable.this.wrapper.acceptorInstantiationTypeFailure(clazz.getName());
            }
        });
    }
    
    private Operation makeInitRefOperation() {
        return new Operation() {
            @Override
            public Object operate(final Object o) {
                final String[] array = (String[])o;
                if (array.length != 2) {
                    throw ParserTable.this.wrapper.orbInitialreferenceSyntax();
                }
                return array[0] + "=" + array[1];
            }
        };
    }
    
    static {
        ParserTable.MY_CLASS_NAME = ParserTable.class.getName();
        ParserTable.myInstance = new ParserTable();
    }
    
    public final class TestBadServerIdHandler implements BadServerIdHandler
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestBadServerIdHandler;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public void handle(final ObjectKey objectKey) {
        }
    }
    
    public static final class TestLegacyORBSocketFactory implements ORBSocketFactory
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestLegacyORBSocketFactory;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public ServerSocket createServerSocket(final String s, final int n) {
            return null;
        }
        
        @Override
        public SocketInfo getEndPointInfo(final ORB orb, final IOR ior, final SocketInfo socketInfo) {
            return null;
        }
        
        @Override
        public Socket createSocket(final SocketInfo socketInfo) {
            return null;
        }
    }
    
    public static final class TestORBSocketFactory implements ORBSocketFactory
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestORBSocketFactory;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public void setORB(final com.sun.corba.se.spi.orb.ORB orb) {
        }
        
        @Override
        public ServerSocket createServerSocket(final String s, final InetSocketAddress inetSocketAddress) {
            return null;
        }
        
        @Override
        public Socket createSocket(final String s, final InetSocketAddress inetSocketAddress) {
            return null;
        }
        
        @Override
        public void setAcceptedSocketOptions(final Acceptor acceptor, final ServerSocket serverSocket, final Socket socket) {
        }
    }
    
    public static final class TestIORToSocketInfo implements IORToSocketInfo
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestIORToSocketInfo;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public List getSocketInfo(final IOR ior) {
            return null;
        }
    }
    
    public static final class TestIIOPPrimaryToContactInfo implements IIOPPrimaryToContactInfo
    {
        @Override
        public void reset(final ContactInfo contactInfo) {
        }
        
        @Override
        public boolean hasNext(final ContactInfo contactInfo, final ContactInfo contactInfo2, final List list) {
            return true;
        }
        
        @Override
        public ContactInfo next(final ContactInfo contactInfo, final ContactInfo contactInfo2, final List list) {
            return null;
        }
    }
    
    public static final class TestContactInfoListFactory implements CorbaContactInfoListFactory
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestContactInfoListFactory;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public void setORB(final com.sun.corba.se.spi.orb.ORB orb) {
        }
        
        @Override
        public CorbaContactInfoList create(final IOR ior) {
            return null;
        }
    }
    
    public static final class TestORBInitializer1 extends LocalObject implements ORBInitializer
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestORBInitializer1;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public void pre_init(final ORBInitInfo orbInitInfo) {
        }
        
        @Override
        public void post_init(final ORBInitInfo orbInitInfo) {
        }
    }
    
    public static final class TestORBInitializer2 extends LocalObject implements ORBInitializer
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestORBInitializer2;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public void pre_init(final ORBInitInfo orbInitInfo) {
        }
        
        @Override
        public void post_init(final ORBInitInfo orbInitInfo) {
        }
    }
    
    public static final class TestAcceptor1 implements Acceptor
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestAcceptor1;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public boolean initialize() {
            return true;
        }
        
        @Override
        public boolean initialized() {
            return true;
        }
        
        @Override
        public String getConnectionCacheType() {
            return "FOO";
        }
        
        @Override
        public void setConnectionCache(final InboundConnectionCache inboundConnectionCache) {
        }
        
        @Override
        public InboundConnectionCache getConnectionCache() {
            return null;
        }
        
        @Override
        public boolean shouldRegisterAcceptEvent() {
            return true;
        }
        
        public void setUseSelectThreadForConnections(final boolean b) {
        }
        
        public boolean shouldUseSelectThreadForConnections() {
            return true;
        }
        
        public void setUseWorkerThreadForConnections(final boolean b) {
        }
        
        public boolean shouldUseWorkerThreadForConnections() {
            return true;
        }
        
        @Override
        public void accept() {
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public EventHandler getEventHandler() {
            return null;
        }
        
        @Override
        public MessageMediator createMessageMediator(final Broker broker, final Connection connection) {
            return null;
        }
        
        @Override
        public MessageMediator finishCreatingMessageMediator(final Broker broker, final Connection connection, final MessageMediator messageMediator) {
            return null;
        }
        
        @Override
        public InputObject createInputObject(final Broker broker, final MessageMediator messageMediator) {
            return null;
        }
        
        @Override
        public OutputObject createOutputObject(final Broker broker, final MessageMediator messageMediator) {
            return null;
        }
    }
    
    public static final class TestAcceptor2 implements Acceptor
    {
        @Override
        public boolean equals(final Object o) {
            return o instanceof TestAcceptor2;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public boolean initialize() {
            return true;
        }
        
        @Override
        public boolean initialized() {
            return true;
        }
        
        @Override
        public String getConnectionCacheType() {
            return "FOO";
        }
        
        @Override
        public void setConnectionCache(final InboundConnectionCache inboundConnectionCache) {
        }
        
        @Override
        public InboundConnectionCache getConnectionCache() {
            return null;
        }
        
        @Override
        public boolean shouldRegisterAcceptEvent() {
            return true;
        }
        
        public void setUseSelectThreadForConnections(final boolean b) {
        }
        
        public boolean shouldUseSelectThreadForConnections() {
            return true;
        }
        
        public void setUseWorkerThreadForConnections(final boolean b) {
        }
        
        public boolean shouldUseWorkerThreadForConnections() {
            return true;
        }
        
        @Override
        public void accept() {
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public EventHandler getEventHandler() {
            return null;
        }
        
        @Override
        public MessageMediator createMessageMediator(final Broker broker, final Connection connection) {
            return null;
        }
        
        @Override
        public MessageMediator finishCreatingMessageMediator(final Broker broker, final Connection connection, final MessageMediator messageMediator) {
            return null;
        }
        
        @Override
        public InputObject createInputObject(final Broker broker, final MessageMediator messageMediator) {
            return null;
        }
        
        @Override
        public OutputObject createOutputObject(final Broker broker, final MessageMediator messageMediator) {
            return null;
        }
    }
}
