package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.CompletionStatus;
import sun.corba.EncapsInputStreamFactory;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.ORB;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class EncapsInputStream extends CDRInputStream
{
    private ORBUtilSystemException wrapper;
    private CodeBase codeBase;
    
    public EncapsInputStream(final ORB orb, final byte[] array, final int n, final boolean b, final GIOPVersion giopVersion) {
        super(orb, ByteBuffer.wrap(array), n, b, giopVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)orb));
        this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)orb, "rpc.encoding");
        this.performORBVersionSpecificInit();
    }
    
    public EncapsInputStream(final ORB orb, final ByteBuffer byteBuffer, final int n, final boolean b, final GIOPVersion giopVersion) {
        super(orb, byteBuffer, n, b, giopVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)orb));
        this.performORBVersionSpecificInit();
    }
    
    public EncapsInputStream(final ORB orb, final byte[] array, final int n) {
        this(orb, array, n, GIOPVersion.V1_2);
    }
    
    public EncapsInputStream(final EncapsInputStream encapsInputStream) {
        super(encapsInputStream);
        this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)encapsInputStream.orb(), "rpc.encoding");
        this.performORBVersionSpecificInit();
    }
    
    public EncapsInputStream(final ORB orb, final byte[] array, final int n, final GIOPVersion giopVersion) {
        this(orb, array, n, false, giopVersion);
    }
    
    public EncapsInputStream(final ORB orb, final byte[] array, final int n, final GIOPVersion giopVersion, final CodeBase codeBase) {
        super(orb, ByteBuffer.wrap(array), n, false, giopVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)orb));
        this.codeBase = codeBase;
        this.performORBVersionSpecificInit();
    }
    
    @Override
    public CDRInputStream dup() {
        return EncapsInputStreamFactory.newEncapsInputStream(this);
    }
    
    @Override
    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1);
    }
    
    @Override
    protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
        if (this.getGIOPVersion().equals(GIOPVersion.V1_0)) {
            throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
        }
        if (this.getGIOPVersion().equals(GIOPVersion.V1_1)) {
            return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, this.isLittleEndian());
        }
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, false);
    }
    
    @Override
    public CodeBase getCodeBase() {
        return this.codeBase;
    }
}
