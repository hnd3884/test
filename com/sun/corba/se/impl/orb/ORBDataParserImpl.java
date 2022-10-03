package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.DataCollector;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.StringPair;
import org.omg.PortableInterceptor.ORBInitializer;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.net.URL;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ParserImplTableBase;

public class ORBDataParserImpl extends ParserImplTableBase implements ORBData
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private String ORBInitialHost;
    private int ORBInitialPort;
    private String ORBServerHost;
    private int ORBServerPort;
    private String listenOnAllInterfaces;
    private ORBSocketFactory legacySocketFactory;
    private com.sun.corba.se.spi.transport.ORBSocketFactory socketFactory;
    private USLPort[] userSpecifiedListenPorts;
    private IORToSocketInfo iorToSocketInfo;
    private IIOPPrimaryToContactInfo iiopPrimaryToContactInfo;
    private String orbId;
    private boolean orbServerIdPropertySpecified;
    private URL servicesURL;
    private String propertyInitRef;
    private boolean allowLocalOptimization;
    private GIOPVersion giopVersion;
    private int highWaterMark;
    private int lowWaterMark;
    private int numberToReclaim;
    private int giopFragmentSize;
    private int giopBufferSize;
    private int giop11BuffMgr;
    private int giop12BuffMgr;
    private short giopTargetAddressPreference;
    private short giopAddressDisposition;
    private boolean useByteOrderMarkers;
    private boolean useByteOrderMarkersInEncaps;
    private boolean alwaysSendCodeSetCtx;
    private boolean persistentPortInitialized;
    private int persistentServerPort;
    private boolean persistentServerIdInitialized;
    private int persistentServerId;
    private boolean serverIsORBActivated;
    private Class badServerIdHandlerClass;
    private CodeSetComponentInfo.CodeSetComponent charData;
    private CodeSetComponentInfo.CodeSetComponent wcharData;
    private ORBInitializer[] orbInitializers;
    private StringPair[] orbInitialReferences;
    private String defaultInitRef;
    private String[] debugFlags;
    private Acceptor[] acceptors;
    private CorbaContactInfoListFactory corbaContactInfoListFactory;
    private String acceptorSocketType;
    private boolean acceptorSocketUseSelectThreadToWait;
    private boolean acceptorSocketUseWorkerThreadForEvent;
    private String connectionSocketType;
    private boolean connectionSocketUseSelectThreadToWait;
    private boolean connectionSocketUseWorkerThreadForEvent;
    private ReadTimeouts readTimeouts;
    private boolean disableDirectByteBufferUse;
    private boolean enableJavaSerialization;
    private boolean useRepId;
    private CodeSetComponentInfo codesets;
    
    @Override
    public String getORBInitialHost() {
        return this.ORBInitialHost;
    }
    
    @Override
    public int getORBInitialPort() {
        return this.ORBInitialPort;
    }
    
    @Override
    public String getORBServerHost() {
        return this.ORBServerHost;
    }
    
    @Override
    public String getListenOnAllInterfaces() {
        return this.listenOnAllInterfaces;
    }
    
    @Override
    public int getORBServerPort() {
        return this.ORBServerPort;
    }
    
    @Override
    public ORBSocketFactory getLegacySocketFactory() {
        return this.legacySocketFactory;
    }
    
    @Override
    public com.sun.corba.se.spi.transport.ORBSocketFactory getSocketFactory() {
        return this.socketFactory;
    }
    
    @Override
    public USLPort[] getUserSpecifiedListenPorts() {
        return this.userSpecifiedListenPorts;
    }
    
    @Override
    public IORToSocketInfo getIORToSocketInfo() {
        return this.iorToSocketInfo;
    }
    
    @Override
    public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo() {
        return this.iiopPrimaryToContactInfo;
    }
    
    @Override
    public String getORBId() {
        return this.orbId;
    }
    
    @Override
    public boolean getORBServerIdPropertySpecified() {
        return this.orbServerIdPropertySpecified;
    }
    
    @Override
    public boolean isLocalOptimizationAllowed() {
        return this.allowLocalOptimization;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return this.giopVersion;
    }
    
    @Override
    public int getHighWaterMark() {
        return this.highWaterMark;
    }
    
    @Override
    public int getLowWaterMark() {
        return this.lowWaterMark;
    }
    
    @Override
    public int getNumberToReclaim() {
        return this.numberToReclaim;
    }
    
    @Override
    public int getGIOPFragmentSize() {
        return this.giopFragmentSize;
    }
    
    @Override
    public int getGIOPBufferSize() {
        return this.giopBufferSize;
    }
    
    @Override
    public int getGIOPBuffMgrStrategy(final GIOPVersion giopVersion) {
        if (giopVersion != null) {
            if (giopVersion.equals(GIOPVersion.V1_0)) {
                return 0;
            }
            if (giopVersion.equals(GIOPVersion.V1_1)) {
                return this.giop11BuffMgr;
            }
            if (giopVersion.equals(GIOPVersion.V1_2)) {
                return this.giop12BuffMgr;
            }
        }
        return 0;
    }
    
    @Override
    public short getGIOPTargetAddressPreference() {
        return this.giopTargetAddressPreference;
    }
    
    @Override
    public short getGIOPAddressDisposition() {
        return this.giopAddressDisposition;
    }
    
    @Override
    public boolean useByteOrderMarkers() {
        return this.useByteOrderMarkers;
    }
    
    @Override
    public boolean useByteOrderMarkersInEncapsulations() {
        return this.useByteOrderMarkersInEncaps;
    }
    
    @Override
    public boolean alwaysSendCodeSetServiceContext() {
        return this.alwaysSendCodeSetCtx;
    }
    
    @Override
    public boolean getPersistentPortInitialized() {
        return this.persistentPortInitialized;
    }
    
    @Override
    public int getPersistentServerPort() {
        if (this.persistentPortInitialized) {
            return this.persistentServerPort;
        }
        throw this.wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public boolean getPersistentServerIdInitialized() {
        return this.persistentServerIdInitialized;
    }
    
    @Override
    public int getPersistentServerId() {
        if (this.persistentServerIdInitialized) {
            return this.persistentServerId;
        }
        throw this.wrapper.persistentServeridNotSet(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public boolean getServerIsORBActivated() {
        return this.serverIsORBActivated;
    }
    
    @Override
    public Class getBadServerIdHandler() {
        return this.badServerIdHandlerClass;
    }
    
    @Override
    public CodeSetComponentInfo getCodeSetComponentInfo() {
        return this.codesets;
    }
    
    @Override
    public ORBInitializer[] getORBInitializers() {
        return this.orbInitializers;
    }
    
    @Override
    public StringPair[] getORBInitialReferences() {
        return this.orbInitialReferences;
    }
    
    @Override
    public String getORBDefaultInitialReference() {
        return this.defaultInitRef;
    }
    
    @Override
    public String[] getORBDebugFlags() {
        return this.debugFlags;
    }
    
    @Override
    public Acceptor[] getAcceptors() {
        return this.acceptors;
    }
    
    @Override
    public CorbaContactInfoListFactory getCorbaContactInfoListFactory() {
        return this.corbaContactInfoListFactory;
    }
    
    @Override
    public String acceptorSocketType() {
        return this.acceptorSocketType;
    }
    
    @Override
    public boolean acceptorSocketUseSelectThreadToWait() {
        return this.acceptorSocketUseSelectThreadToWait;
    }
    
    @Override
    public boolean acceptorSocketUseWorkerThreadForEvent() {
        return this.acceptorSocketUseWorkerThreadForEvent;
    }
    
    @Override
    public String connectionSocketType() {
        return this.connectionSocketType;
    }
    
    @Override
    public boolean connectionSocketUseSelectThreadToWait() {
        return this.connectionSocketUseSelectThreadToWait;
    }
    
    @Override
    public boolean connectionSocketUseWorkerThreadForEvent() {
        return this.connectionSocketUseWorkerThreadForEvent;
    }
    
    @Override
    public boolean isJavaSerializationEnabled() {
        return this.enableJavaSerialization;
    }
    
    @Override
    public ReadTimeouts getTransportTCPReadTimeouts() {
        return this.readTimeouts;
    }
    
    @Override
    public boolean disableDirectByteBufferUse() {
        return this.disableDirectByteBufferUse;
    }
    
    @Override
    public boolean useRepId() {
        return this.useRepId;
    }
    
    public ORBDataParserImpl(final ORB orb, final DataCollector dataCollector) {
        super(ParserTable.get().getParserData());
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "orb.lifecycle");
        this.init(dataCollector);
        this.complete();
    }
    
    public void complete() {
        this.codesets = new CodeSetComponentInfo(this.charData, this.wcharData);
    }
}
