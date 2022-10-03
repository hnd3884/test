package com.sun.corba.se.impl.interceptors;

import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.Encoding;
import org.omg.IOP.Codec;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.ORB;
import org.omg.IOP.CodecFactory;
import org.omg.CORBA.LocalObject;

public final class CodecFactoryImpl extends LocalObject implements CodecFactory
{
    private ORB orb;
    private ORBUtilSystemException wrapper;
    private static final int MAX_MINOR_VERSION_SUPPORTED = 2;
    private Codec[] codecs;
    
    public CodecFactoryImpl(final ORB orb) {
        this.codecs = new Codec[3];
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)orb, "rpc.protocol");
        for (int i = 0; i <= 2; ++i) {
            this.codecs[i] = new CDREncapsCodec(orb, 1, i);
        }
    }
    
    @Override
    public Codec create_codec(final Encoding encoding) throws UnknownEncoding {
        if (encoding == null) {
            this.nullParam();
        }
        Codec codec = null;
        if (encoding.format == 0 && encoding.major_version == 1 && encoding.minor_version >= 0 && encoding.minor_version <= 2) {
            codec = this.codecs[encoding.minor_version];
        }
        if (codec == null) {
            throw new UnknownEncoding();
        }
        return codec;
    }
    
    private void nullParam() {
        throw this.wrapper.nullParam();
    }
}
