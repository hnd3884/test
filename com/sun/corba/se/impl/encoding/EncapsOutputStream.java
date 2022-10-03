package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.CompletionStatus;
import sun.corba.EncapsInputStreamFactory;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;

public class EncapsOutputStream extends CDROutputStream
{
    static final boolean usePooledByteBuffers = false;
    
    public EncapsOutputStream(final ORB orb) {
        this(orb, GIOPVersion.V1_2);
    }
    
    public EncapsOutputStream(final ORB orb, final GIOPVersion giopVersion) {
        this(orb, giopVersion, false);
    }
    
    public EncapsOutputStream(final ORB orb, final boolean b) {
        this(orb, GIOPVersion.V1_2, b);
    }
    
    public EncapsOutputStream(final ORB orb, final GIOPVersion giopVersion, final boolean b) {
        super(orb, giopVersion, (byte)0, b, BufferManagerFactory.newBufferManagerWrite(0, (byte)0, orb), (byte)1, false);
    }
    
    @Override
    public InputStream create_input_stream() {
        this.freeInternalCaches();
        return EncapsInputStreamFactory.newEncapsInputStream(this.orb(), this.getByteBuffer(), this.getSize(), this.isLittleEndian(), this.getGIOPVersion());
    }
    
    @Override
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
    }
    
    @Override
    protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
        if (this.getGIOPVersion().equals(GIOPVersion.V1_0)) {
            throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
        }
        if (this.getGIOPVersion().equals(GIOPVersion.V1_1)) {
            return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, this.isLittleEndian(), false);
        }
        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, false, ((ORB)this.orb()).getORBData().useByteOrderMarkersInEncapsulations());
    }
}
