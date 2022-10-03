package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.activation.EndPointInfoHelper;
import com.sun.corba.se.spi.activation.EndpointInfoListHelper;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerLocationPerORBHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerLocationPerORB serverLocationPerORB) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverLocationPerORB);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerLocationPerORB extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerLocationPerORBHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerLocationPerORBHelper.__typeCode == null) {
                    if (ServerLocationPerORBHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerLocationPerORBHelper._id);
                    }
                    ServerLocationPerORBHelper.__active = true;
                    ServerLocationPerORBHelper.__typeCode = ORB.init().create_struct_tc(id(), "ServerLocationPerORB", new StructMember[] { new StructMember("hostname", ORB.init().create_string_tc(0), null), new StructMember("ports", ORB.init().create_alias_tc(EndpointInfoListHelper.id(), "EndpointInfoList", ORB.init().create_sequence_tc(0, EndPointInfoHelper.type())), null) });
                    ServerLocationPerORBHelper.__active = false;
                }
            }
        }
        return ServerLocationPerORBHelper.__typeCode;
    }
    
    public static String id() {
        return ServerLocationPerORBHelper._id;
    }
    
    public static ServerLocationPerORB read(final InputStream inputStream) {
        final ServerLocationPerORB serverLocationPerORB = new ServerLocationPerORB();
        serverLocationPerORB.hostname = inputStream.read_string();
        serverLocationPerORB.ports = EndpointInfoListHelper.read(inputStream);
        return serverLocationPerORB;
    }
    
    public static void write(final OutputStream outputStream, final ServerLocationPerORB serverLocationPerORB) {
        outputStream.write_string(serverLocationPerORB.hostname);
        EndpointInfoListHelper.write(outputStream, serverLocationPerORB.ports);
    }
    
    static {
        ServerLocationPerORBHelper._id = "IDL:activation/Locator/ServerLocationPerORB:1.0";
        ServerLocationPerORBHelper.__typeCode = null;
        ServerLocationPerORBHelper.__active = false;
    }
}
