package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyListHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Policy[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Policy[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (PolicyListHelper.__typeCode == null) {
            PolicyListHelper.__typeCode = PolicyHelper.type();
            PolicyListHelper.__typeCode = ORB.init().create_sequence_tc(0, PolicyListHelper.__typeCode);
            PolicyListHelper.__typeCode = ORB.init().create_alias_tc(id(), "PolicyList", PolicyListHelper.__typeCode);
        }
        return PolicyListHelper.__typeCode;
    }
    
    public static String id() {
        return PolicyListHelper._id;
    }
    
    public static Policy[] read(final InputStream inputStream) {
        final Policy[] array = new Policy[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = PolicyHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final Policy[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            PolicyHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        PolicyListHelper._id = "IDL:omg.org/CORBA/PolicyList:1.0";
        PolicyListHelper.__typeCode = null;
    }
}
