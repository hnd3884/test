package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.impl.corba.AnyImpl;
import sun.corba.EncapsInputStreamFactory;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.CORBA.portable.OutputStream;
import sun.corba.OutputStreamFactory;
import org.omg.IOP.CodecPackage.TypeMismatch;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.CORBA.Any;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.ORB;
import org.omg.IOP.Codec;
import org.omg.CORBA.LocalObject;

public final class CDREncapsCodec extends LocalObject implements Codec
{
    private ORB orb;
    ORBUtilSystemException wrapper;
    private GIOPVersion giopVersion;
    
    public CDREncapsCodec(final ORB orb, final int n, final int n2) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)orb, "rpc.protocol");
        this.giopVersion = GIOPVersion.getInstance((byte)n, (byte)n2);
    }
    
    @Override
    public byte[] encode(final Any any) throws InvalidTypeForEncoding {
        if (any == null) {
            throw this.wrapper.nullParam();
        }
        return this.encodeImpl(any, true);
    }
    
    @Override
    public Any decode(final byte[] array) throws FormatMismatch {
        if (array == null) {
            throw this.wrapper.nullParam();
        }
        return this.decodeImpl(array, null);
    }
    
    @Override
    public byte[] encode_value(final Any any) throws InvalidTypeForEncoding {
        if (any == null) {
            throw this.wrapper.nullParam();
        }
        return this.encodeImpl(any, false);
    }
    
    @Override
    public Any decode_value(final byte[] array, final TypeCode typeCode) throws FormatMismatch, TypeMismatch {
        if (array == null) {
            throw this.wrapper.nullParam();
        }
        if (typeCode == null) {
            throw this.wrapper.nullParam();
        }
        return this.decodeImpl(array, typeCode);
    }
    
    private byte[] encodeImpl(final Any any, final boolean b) throws InvalidTypeForEncoding {
        if (any == null) {
            throw this.wrapper.nullParam();
        }
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)this.orb, this.giopVersion);
        encapsOutputStream.putEndian();
        if (b) {
            encapsOutputStream.write_TypeCode(any.type());
        }
        any.write_value(encapsOutputStream);
        return encapsOutputStream.toByteArray();
    }
    
    private Any decodeImpl(final byte[] array, TypeCode read_TypeCode) throws FormatMismatch {
        if (array == null) {
            throw this.wrapper.nullParam();
        }
        AnyImpl anyImpl;
        try {
            final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, array, array.length, this.giopVersion);
            encapsInputStream.consumeEndian();
            if (read_TypeCode == null) {
                read_TypeCode = encapsInputStream.read_TypeCode();
            }
            anyImpl = new AnyImpl((com.sun.corba.se.spi.orb.ORB)this.orb);
            anyImpl.read_value(encapsInputStream, read_TypeCode);
        }
        catch (final RuntimeException ex) {
            throw new FormatMismatch();
        }
        return anyImpl;
    }
}
