package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.portable.InputStream;
import java.nio.ByteBuffer;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.pept.encoding.InputObject;

public class CDRInputObject extends CDRInputStream implements InputObject
{
    private CorbaConnection corbaConnection;
    private Message header;
    private boolean unmarshaledHeader;
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private OMGSystemException omgWrapper;
    
    public CDRInputObject(final ORB orb, final CorbaConnection corbaConnection, final ByteBuffer byteBuffer, final Message header) {
        super(orb, byteBuffer, header.getSize(), header.isLittleEndian(), header.getGIOPVersion(), header.getEncodingVersion(), BufferManagerFactory.newBufferManagerRead(header.getGIOPVersion(), header.getEncodingVersion(), orb));
        this.corbaConnection = corbaConnection;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
        this.omgWrapper = OMGSystemException.get(orb, "rpc.encoding");
        if (orb.transportDebugFlag) {
            this.dprint(".CDRInputObject constructor:");
        }
        this.getBufferManager().init(header);
        this.header = header;
        this.unmarshaledHeader = false;
        this.setIndex(12);
        this.setBufferLength(header.getSize());
    }
    
    public final CorbaConnection getConnection() {
        return this.corbaConnection;
    }
    
    public Message getMessageHeader() {
        return this.header;
    }
    
    public void unmarshalHeader() {
        if (!this.unmarshaledHeader) {
            try {
                if (((ORB)this.orb()).transportDebugFlag) {
                    this.dprint(".unmarshalHeader->: " + this.getMessageHeader());
                }
                this.getMessageHeader().read(this);
                this.unmarshaledHeader = true;
            }
            catch (final RuntimeException ex) {
                if (((ORB)this.orb()).transportDebugFlag) {
                    this.dprint(".unmarshalHeader: !!ERROR!!: " + this.getMessageHeader() + ": " + ex);
                }
                throw ex;
            }
            finally {
                if (((ORB)this.orb()).transportDebugFlag) {
                    this.dprint(".unmarshalHeader<-: " + this.getMessageHeader());
                }
            }
        }
    }
    
    public final boolean unmarshaledHeader() {
        return this.unmarshaledHeader;
    }
    
    @Override
    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        final CodeSetComponentInfo.CodeSetContext codeSets = this.getCodeSets();
        if (codeSets == null) {
            return super.createCharBTCConverter();
        }
        final OSFCodeSetRegistry.Entry lookupEntry = OSFCodeSetRegistry.lookupEntry(codeSets.getCharCodeSet());
        if (lookupEntry == null) {
            throw this.wrapper.unknownCodeset(lookupEntry);
        }
        return CodeSetConversion.impl().getBTCConverter(lookupEntry, this.isLittleEndian());
    }
    
    @Override
    protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
        final CodeSetComponentInfo.CodeSetContext codeSets = this.getCodeSets();
        if (codeSets == null) {
            if (this.getConnection().isServer()) {
                throw this.omgWrapper.noClientWcharCodesetCtx();
            }
            throw this.omgWrapper.noServerWcharCodesetCmp();
        }
        else {
            final OSFCodeSetRegistry.Entry lookupEntry = OSFCodeSetRegistry.lookupEntry(codeSets.getWCharCodeSet());
            if (lookupEntry == null) {
                throw this.wrapper.unknownCodeset(lookupEntry);
            }
            if (lookupEntry == OSFCodeSetRegistry.UTF_16 && this.getGIOPVersion().equals(GIOPVersion.V1_2)) {
                return CodeSetConversion.impl().getBTCConverter(lookupEntry, false);
            }
            return CodeSetConversion.impl().getBTCConverter(lookupEntry, this.isLittleEndian());
        }
    }
    
    private CodeSetComponentInfo.CodeSetContext getCodeSets() {
        if (this.getConnection() == null) {
            return CodeSetComponentInfo.LOCAL_CODE_SETS;
        }
        return this.getConnection().getCodeSetContext();
    }
    
    @Override
    public final CodeBase getCodeBase() {
        if (this.getConnection() == null) {
            return null;
        }
        return this.getConnection().getCodeBase();
    }
    
    @Override
    public CDRInputStream dup() {
        return null;
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CDRInputObject", s);
    }
}
