package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.portable.InputStream;
import java.io.IOException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;

public class CDROutputObject extends CorbaOutputObject
{
    private Message header;
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private OMGSystemException omgWrapper;
    private CorbaConnection connection;
    
    private CDROutputObject(final ORB orb, final GIOPVersion giopVersion, final Message header, final BufferManagerWrite bufferManagerWrite, final byte b, final CorbaMessageMediator corbaMessageMediator) {
        super(orb, giopVersion, header.getEncodingVersion(), false, bufferManagerWrite, b, corbaMessageMediator != null && corbaMessageMediator.getConnection() != null && ((CorbaConnection)corbaMessageMediator.getConnection()).shouldUseDirectByteBuffers());
        this.header = header;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
        this.omgWrapper = OMGSystemException.get(orb, "rpc.encoding");
        this.getBufferManager().setOutputObject(this);
        this.corbaMessageMediator = corbaMessageMediator;
    }
    
    public CDROutputObject(final ORB orb, final MessageMediator messageMediator, final Message message, final byte b) {
        this(orb, ((CorbaMessageMediator)messageMediator).getGIOPVersion(), message, BufferManagerFactory.newBufferManagerWrite(((CorbaMessageMediator)messageMediator).getGIOPVersion(), message.getEncodingVersion(), orb), b, (CorbaMessageMediator)messageMediator);
    }
    
    public CDROutputObject(final ORB orb, final MessageMediator messageMediator, final Message message, final byte b, final int n) {
        this(orb, ((CorbaMessageMediator)messageMediator).getGIOPVersion(), message, BufferManagerFactory.newBufferManagerWrite(n, message.getEncodingVersion(), orb), b, (CorbaMessageMediator)messageMediator);
    }
    
    public CDROutputObject(final ORB orb, final CorbaMessageMediator corbaMessageMediator, final GIOPVersion giopVersion, final CorbaConnection connection, final Message message, final byte b) {
        this(orb, giopVersion, message, BufferManagerFactory.newBufferManagerWrite(giopVersion, message.getEncodingVersion(), orb), b, corbaMessageMediator);
        this.connection = connection;
    }
    
    public Message getMessageHeader() {
        return this.header;
    }
    
    public final void finishSendingMessage() {
        this.getBufferManager().sendMessage();
    }
    
    @Override
    public void writeTo(final CorbaConnection corbaConnection) throws IOException {
        final ByteBufferWithInfo byteBufferWithInfo = this.getByteBufferWithInfo();
        this.getMessageHeader().setSize(byteBufferWithInfo.byteBuffer, byteBufferWithInfo.getSize());
        if (this.orb() != null) {
            if (((ORB)this.orb()).transportDebugFlag) {
                this.dprint(".writeTo: " + corbaConnection);
            }
            if (((ORB)this.orb()).giopDebugFlag) {
                CDROutputStream_1_0.printBuffer(byteBufferWithInfo);
            }
        }
        byteBufferWithInfo.byteBuffer.position(0).limit(byteBufferWithInfo.getSize());
        corbaConnection.write(byteBufferWithInfo.byteBuffer);
    }
    
    @Override
    public InputStream create_input_stream() {
        return null;
    }
    
    public CorbaConnection getConnection() {
        if (this.connection != null) {
            return this.connection;
        }
        return (CorbaConnection)this.corbaMessageMediator.getConnection();
    }
    
    @Override
    public final ByteBufferWithInfo getByteBufferWithInfo() {
        return super.getByteBufferWithInfo();
    }
    
    public final void setByteBufferWithInfo(final ByteBufferWithInfo byteBufferWithInfo) {
        super.setByteBufferWithInfo(byteBufferWithInfo);
    }
    
    @Override
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        final CodeSetComponentInfo.CodeSetContext codeSets = this.getCodeSets();
        if (codeSets == null) {
            return super.createCharCTBConverter();
        }
        final OSFCodeSetRegistry.Entry lookupEntry = OSFCodeSetRegistry.lookupEntry(codeSets.getCharCodeSet());
        if (lookupEntry == null) {
            throw this.wrapper.unknownCodeset(lookupEntry);
        }
        return CodeSetConversion.impl().getCTBConverter(lookupEntry, this.isLittleEndian(), false);
    }
    
    @Override
    protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
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
            final boolean useByteOrderMarkers = ((ORB)this.orb()).getORBData().useByteOrderMarkers();
            if (lookupEntry == OSFCodeSetRegistry.UTF_16) {
                if (this.getGIOPVersion().equals(GIOPVersion.V1_2)) {
                    return CodeSetConversion.impl().getCTBConverter(lookupEntry, false, useByteOrderMarkers);
                }
                if (this.getGIOPVersion().equals(GIOPVersion.V1_1)) {
                    return CodeSetConversion.impl().getCTBConverter(lookupEntry, this.isLittleEndian(), false);
                }
            }
            return CodeSetConversion.impl().getCTBConverter(lookupEntry, this.isLittleEndian(), useByteOrderMarkers);
        }
    }
    
    private CodeSetComponentInfo.CodeSetContext getCodeSets() {
        if (this.getConnection() == null) {
            return CodeSetComponentInfo.LOCAL_CODE_SETS;
        }
        return this.getConnection().getCodeSetContext();
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CDROutputObject", s);
    }
}
