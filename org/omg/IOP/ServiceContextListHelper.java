package org.omg.IOP;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServiceContextListHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ServiceContext[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServiceContext[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServiceContextListHelper.__typeCode == null) {
            ServiceContextListHelper.__typeCode = ServiceContextHelper.type();
            ServiceContextListHelper.__typeCode = ORB.init().create_sequence_tc(0, ServiceContextListHelper.__typeCode);
            ServiceContextListHelper.__typeCode = ORB.init().create_alias_tc(id(), "ServiceContextList", ServiceContextListHelper.__typeCode);
        }
        return ServiceContextListHelper.__typeCode;
    }
    
    public static String id() {
        return ServiceContextListHelper._id;
    }
    
    public static ServiceContext[] read(final InputStream inputStream) {
        final ServiceContext[] array = new ServiceContext[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ServiceContextHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final ServiceContext[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ServiceContextHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ServiceContextListHelper._id = "IDL:omg.org/IOP/ServiceContextList:1.0";
        ServiceContextListHelper.__typeCode = null;
    }
}
