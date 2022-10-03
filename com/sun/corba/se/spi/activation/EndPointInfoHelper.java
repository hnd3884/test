package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class EndPointInfoHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final EndPointInfo endPointInfo) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, endPointInfo);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static EndPointInfo extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (EndPointInfoHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (EndPointInfoHelper.__typeCode == null) {
                    if (EndPointInfoHelper.__active) {
                        return ORB.init().create_recursive_tc(EndPointInfoHelper._id);
                    }
                    EndPointInfoHelper.__active = true;
                    EndPointInfoHelper.__typeCode = ORB.init().create_struct_tc(id(), "EndPointInfo", new StructMember[] { new StructMember("endpointType", ORB.init().create_string_tc(0), null), new StructMember("port", ORB.init().create_alias_tc(TCPPortHelper.id(), "TCPPort", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    EndPointInfoHelper.__active = false;
                }
            }
        }
        return EndPointInfoHelper.__typeCode;
    }
    
    public static String id() {
        return EndPointInfoHelper._id;
    }
    
    public static EndPointInfo read(final InputStream inputStream) {
        final EndPointInfo endPointInfo = new EndPointInfo();
        endPointInfo.endpointType = inputStream.read_string();
        endPointInfo.port = inputStream.read_long();
        return endPointInfo;
    }
    
    public static void write(final OutputStream outputStream, final EndPointInfo endPointInfo) {
        outputStream.write_string(endPointInfo.endpointType);
        outputStream.write_long(endPointInfo.port);
    }
    
    static {
        EndPointInfoHelper._id = "IDL:activation/EndPointInfo:1.0";
        EndPointInfoHelper.__typeCode = null;
        EndPointInfoHelper.__active = false;
    }
}
