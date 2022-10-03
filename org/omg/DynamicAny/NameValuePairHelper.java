package org.omg.DynamicAny;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameValuePairHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NameValuePair nameValuePair) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, nameValuePair);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameValuePair extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameValuePairHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NameValuePairHelper.__typeCode == null) {
                    if (NameValuePairHelper.__active) {
                        return ORB.init().create_recursive_tc(NameValuePairHelper._id);
                    }
                    NameValuePairHelper.__active = true;
                    NameValuePairHelper.__typeCode = ORB.init().create_struct_tc(id(), "NameValuePair", new StructMember[] { new StructMember("id", ORB.init().create_alias_tc(FieldNameHelper.id(), "FieldName", ORB.init().create_string_tc(0)), null), new StructMember("value", ORB.init().get_primitive_tc(TCKind.tk_any), null) });
                    NameValuePairHelper.__active = false;
                }
            }
        }
        return NameValuePairHelper.__typeCode;
    }
    
    public static String id() {
        return NameValuePairHelper._id;
    }
    
    public static NameValuePair read(final InputStream inputStream) {
        final NameValuePair nameValuePair = new NameValuePair();
        nameValuePair.id = inputStream.read_string();
        nameValuePair.value = inputStream.read_any();
        return nameValuePair;
    }
    
    public static void write(final OutputStream outputStream, final NameValuePair nameValuePair) {
        outputStream.write_string(nameValuePair.id);
        outputStream.write_any(nameValuePair.value);
    }
    
    static {
        NameValuePairHelper._id = "IDL:omg.org/DynamicAny/NameValuePair:1.0";
        NameValuePairHelper.__typeCode = null;
        NameValuePairHelper.__active = false;
    }
}
