package org.omg.IOP;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServiceContextHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServiceContext serviceContext) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serviceContext);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServiceContext extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServiceContextHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServiceContextHelper.__typeCode == null) {
                    if (ServiceContextHelper.__active) {
                        return ORB.init().create_recursive_tc(ServiceContextHelper._id);
                    }
                    ServiceContextHelper.__active = true;
                    ServiceContextHelper.__typeCode = ORB.init().create_struct_tc(id(), "ServiceContext", new StructMember[] { new StructMember("context_id", ORB.init().create_alias_tc(ServiceIdHelper.id(), "ServiceId", ORB.init().get_primitive_tc(TCKind.tk_ulong)), null), new StructMember("context_data", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null) });
                    ServiceContextHelper.__active = false;
                }
            }
        }
        return ServiceContextHelper.__typeCode;
    }
    
    public static String id() {
        return ServiceContextHelper._id;
    }
    
    public static ServiceContext read(final InputStream inputStream) {
        final ServiceContext serviceContext = new ServiceContext();
        serviceContext.context_id = inputStream.read_ulong();
        final int read_long = inputStream.read_long();
        inputStream.read_octet_array(serviceContext.context_data = new byte[read_long], 0, read_long);
        return serviceContext;
    }
    
    public static void write(final OutputStream outputStream, final ServiceContext serviceContext) {
        outputStream.write_ulong(serviceContext.context_id);
        outputStream.write_long(serviceContext.context_data.length);
        outputStream.write_octet_array(serviceContext.context_data, 0, serviceContext.context_data.length);
    }
    
    static {
        ServiceContextHelper._id = "IDL:omg.org/IOP/ServiceContext:1.0";
        ServiceContextHelper.__typeCode = null;
        ServiceContextHelper.__active = false;
    }
}
