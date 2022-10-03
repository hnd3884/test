package org.omg.IOP;

import org.omg.IOP.CodecPackage.TypeMismatch;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.CORBA.Any;

public interface CodecOperations
{
    byte[] encode(final Any p0) throws InvalidTypeForEncoding;
    
    Any decode(final byte[] p0) throws FormatMismatch;
    
    byte[] encode_value(final Any p0) throws InvalidTypeForEncoding;
    
    Any decode_value(final byte[] p0, final TypeCode p1) throws FormatMismatch, TypeMismatch;
}
