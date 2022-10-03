package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class AttributeModeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final AttributeMode attributeMode) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, attributeMode);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AttributeMode extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AttributeModeHelper.__typeCode == null) {
            AttributeModeHelper.__typeCode = ORB.init().create_enum_tc(id(), "AttributeMode", new String[] { "ATTR_NORMAL", "ATTR_READONLY" });
        }
        return AttributeModeHelper.__typeCode;
    }
    
    public static String id() {
        return AttributeModeHelper._id;
    }
    
    public static AttributeMode read(final InputStream inputStream) {
        return AttributeMode.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final AttributeMode attributeMode) {
        outputStream.write_long(attributeMode.value());
    }
    
    static {
        AttributeModeHelper._id = "IDL:omg.org/CORBA/AttributeMode:1.0";
        AttributeModeHelper.__typeCode = null;
    }
}
