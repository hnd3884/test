package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.TypeCodePackage.BadKind;
import java.math.BigDecimal;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import java.io.Serializable;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;

public final class TCUtility
{
    static void marshalIn(final OutputStream outputStream, final TypeCode typeCode, final long n, final Object o) {
        switch (typeCode.kind().value()) {
            case 0:
            case 1:
            case 31: {
                break;
            }
            case 2: {
                outputStream.write_short((short)(n & 0xFFFFL));
                break;
            }
            case 4: {
                outputStream.write_ushort((short)(n & 0xFFFFL));
                break;
            }
            case 3:
            case 17: {
                outputStream.write_long((int)(n & 0xFFFFFFFFL));
                break;
            }
            case 5: {
                outputStream.write_ulong((int)(n & 0xFFFFFFFFL));
                break;
            }
            case 6: {
                outputStream.write_float(Float.intBitsToFloat((int)(n & 0xFFFFFFFFL)));
                break;
            }
            case 7: {
                outputStream.write_double(Double.longBitsToDouble(n));
                break;
            }
            case 8: {
                if (n == 0L) {
                    outputStream.write_boolean(false);
                    break;
                }
                outputStream.write_boolean(true);
                break;
            }
            case 9: {
                outputStream.write_char((char)(n & 0xFFFFL));
                break;
            }
            case 10: {
                outputStream.write_octet((byte)(n & 0xFFL));
                break;
            }
            case 11: {
                outputStream.write_any((Any)o);
                break;
            }
            case 12: {
                outputStream.write_TypeCode((TypeCode)o);
                break;
            }
            case 13: {
                outputStream.write_Principal((Principal)o);
                break;
            }
            case 14: {
                outputStream.write_Object((org.omg.CORBA.Object)o);
                break;
            }
            case 23: {
                outputStream.write_longlong(n);
                break;
            }
            case 24: {
                outputStream.write_ulonglong(n);
                break;
            }
            case 26: {
                outputStream.write_wchar((char)(n & 0xFFFFL));
                break;
            }
            case 18: {
                outputStream.write_string((String)o);
                break;
            }
            case 27: {
                outputStream.write_wstring((String)o);
                break;
            }
            case 29:
            case 30: {
                ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value((Serializable)o);
                break;
            }
            case 28: {
                if (outputStream instanceof CDROutputStream) {
                    try {
                        ((CDROutputStream)outputStream).write_fixed((BigDecimal)o, typeCode.fixed_digits(), typeCode.fixed_scale());
                    }
                    catch (final BadKind badKind) {}
                    break;
                }
                outputStream.write_fixed((BigDecimal)o);
                break;
            }
            case 15:
            case 16:
            case 19:
            case 20:
            case 21:
            case 22: {
                ((Streamable)o)._write(outputStream);
                break;
            }
            case 32: {
                ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_abstract_interface(o);
                break;
            }
            default: {
                throw ORBUtilSystemException.get((ORB)outputStream.orb(), "rpc.presentation").typecodeNotSupported();
            }
        }
    }
    
    static void unmarshalIn(final InputStream inputStream, final TypeCode typeCode, final long[] array, final Object[] array2) {
        final int value = typeCode.kind().value();
        long n = 0L;
        Object o = array2[0];
        switch (value) {
            case 0:
            case 1:
            case 31: {
                break;
            }
            case 2: {
                n = ((long)inputStream.read_short() & 0xFFFFL);
                break;
            }
            case 4: {
                n = ((long)inputStream.read_ushort() & 0xFFFFL);
                break;
            }
            case 3:
            case 17: {
                n = ((long)inputStream.read_long() & 0xFFFFFFFFL);
                break;
            }
            case 5: {
                n = ((long)inputStream.read_ulong() & 0xFFFFFFFFL);
                break;
            }
            case 6: {
                n = ((long)Float.floatToIntBits(inputStream.read_float()) & 0xFFFFFFFFL);
                break;
            }
            case 7: {
                n = Double.doubleToLongBits(inputStream.read_double());
                break;
            }
            case 9: {
                n = ((long)inputStream.read_char() & 0xFFFFL);
                break;
            }
            case 10: {
                n = ((long)inputStream.read_octet() & 0xFFL);
                break;
            }
            case 8: {
                if (inputStream.read_boolean()) {
                    n = 1L;
                    break;
                }
                n = 0L;
                break;
            }
            case 11: {
                o = inputStream.read_any();
                break;
            }
            case 12: {
                o = inputStream.read_TypeCode();
                break;
            }
            case 13: {
                o = inputStream.read_Principal();
                break;
            }
            case 14: {
                if (o instanceof Streamable) {
                    ((Streamable)o)._read(inputStream);
                    break;
                }
                o = inputStream.read_Object();
                break;
            }
            case 23: {
                n = inputStream.read_longlong();
                break;
            }
            case 24: {
                n = inputStream.read_ulonglong();
                break;
            }
            case 26: {
                n = ((long)inputStream.read_wchar() & 0xFFFFL);
                break;
            }
            case 18: {
                o = inputStream.read_string();
                break;
            }
            case 27: {
                o = inputStream.read_wstring();
                break;
            }
            case 29:
            case 30: {
                o = ((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value();
                break;
            }
            case 28: {
                try {
                    if (inputStream instanceof CDRInputStream) {
                        o = ((CDRInputStream)inputStream).read_fixed(typeCode.fixed_digits(), typeCode.fixed_scale());
                    }
                    else {
                        o = inputStream.read_fixed().movePointLeft(typeCode.fixed_scale());
                    }
                }
                catch (final BadKind badKind) {}
                break;
            }
            case 15:
            case 16:
            case 19:
            case 20:
            case 21:
            case 22: {
                ((Streamable)o)._read(inputStream);
                break;
            }
            case 32: {
                o = ((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_abstract_interface();
                break;
            }
            default: {
                throw ORBUtilSystemException.get((ORB)inputStream.orb(), "rpc.presentation").typecodeNotSupported();
            }
        }
        array2[0] = o;
        array[0] = n;
    }
}
