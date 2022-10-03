package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.BoxedValueHelper;

public class StringValueHelper implements BoxedValueHelper
{
    private static String _id;
    private static StringValueHelper _instance;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final String s) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, s);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static String extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (StringValueHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (StringValueHelper.__typeCode == null) {
                    if (StringValueHelper.__active) {
                        return ORB.init().create_recursive_tc(StringValueHelper._id);
                    }
                    StringValueHelper.__active = true;
                    StringValueHelper.__typeCode = ORB.init().create_string_tc(0);
                    StringValueHelper.__typeCode = ORB.init().create_value_box_tc(StringValueHelper._id, "StringValue", StringValueHelper.__typeCode);
                    StringValueHelper.__active = false;
                }
            }
        }
        return StringValueHelper.__typeCode;
    }
    
    public static String id() {
        return StringValueHelper._id;
    }
    
    public static String read(final InputStream inputStream) {
        if (!(inputStream instanceof org.omg.CORBA_2_3.portable.InputStream)) {
            throw new BAD_PARAM();
        }
        return (String)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value(StringValueHelper._instance);
    }
    
    @Override
    public Serializable read_value(final InputStream inputStream) {
        return inputStream.read_string();
    }
    
    public static void write(final OutputStream outputStream, final String s) {
        if (!(outputStream instanceof org.omg.CORBA_2_3.portable.OutputStream)) {
            throw new BAD_PARAM();
        }
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value(s, StringValueHelper._instance);
    }
    
    @Override
    public void write_value(final OutputStream outputStream, final Serializable s) {
        if (!(s instanceof String)) {
            throw new MARSHAL();
        }
        outputStream.write_string((String)s);
    }
    
    @Override
    public String get_id() {
        return StringValueHelper._id;
    }
    
    static {
        StringValueHelper._id = "IDL:omg.org/CORBA/StringValue:1.0";
        StringValueHelper._instance = new StringValueHelper();
        StringValueHelper.__typeCode = null;
        StringValueHelper.__active = false;
    }
}
