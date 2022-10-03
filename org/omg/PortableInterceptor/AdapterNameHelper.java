package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StringSeqHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AdapterNameHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final String[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static String[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AdapterNameHelper.__typeCode == null) {
            AdapterNameHelper.__typeCode = ORB.init().create_string_tc(0);
            AdapterNameHelper.__typeCode = ORB.init().create_sequence_tc(0, AdapterNameHelper.__typeCode);
            AdapterNameHelper.__typeCode = ORB.init().create_alias_tc(StringSeqHelper.id(), "StringSeq", AdapterNameHelper.__typeCode);
            AdapterNameHelper.__typeCode = ORB.init().create_alias_tc(id(), "AdapterName", AdapterNameHelper.__typeCode);
        }
        return AdapterNameHelper.__typeCode;
    }
    
    public static String id() {
        return AdapterNameHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        return StringSeqHelper.read(inputStream);
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        StringSeqHelper.write(outputStream, array);
    }
    
    static {
        AdapterNameHelper._id = "IDL:omg.org/PortableInterceptor/AdapterName:1.0";
        AdapterNameHelper.__typeCode = null;
    }
}
