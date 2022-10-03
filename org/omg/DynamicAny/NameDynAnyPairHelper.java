package org.omg.DynamicAny;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameDynAnyPairHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NameDynAnyPair nameDynAnyPair) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, nameDynAnyPair);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameDynAnyPair extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameDynAnyPairHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NameDynAnyPairHelper.__typeCode == null) {
                    if (NameDynAnyPairHelper.__active) {
                        return ORB.init().create_recursive_tc(NameDynAnyPairHelper._id);
                    }
                    NameDynAnyPairHelper.__active = true;
                    NameDynAnyPairHelper.__typeCode = ORB.init().create_struct_tc(id(), "NameDynAnyPair", new StructMember[] { new StructMember("id", ORB.init().create_alias_tc(FieldNameHelper.id(), "FieldName", ORB.init().create_string_tc(0)), null), new StructMember("value", DynAnyHelper.type(), null) });
                    NameDynAnyPairHelper.__active = false;
                }
            }
        }
        return NameDynAnyPairHelper.__typeCode;
    }
    
    public static String id() {
        return NameDynAnyPairHelper._id;
    }
    
    public static NameDynAnyPair read(final InputStream inputStream) {
        final NameDynAnyPair nameDynAnyPair = new NameDynAnyPair();
        nameDynAnyPair.id = inputStream.read_string();
        nameDynAnyPair.value = DynAnyHelper.read(inputStream);
        return nameDynAnyPair;
    }
    
    public static void write(final OutputStream outputStream, final NameDynAnyPair nameDynAnyPair) {
        outputStream.write_string(nameDynAnyPair.id);
        DynAnyHelper.write(outputStream, nameDynAnyPair.value);
    }
    
    static {
        NameDynAnyPairHelper._id = "IDL:omg.org/DynamicAny/NameDynAnyPair:1.0";
        NameDynAnyPairHelper.__typeCode = null;
        NameDynAnyPairHelper.__active = false;
    }
}
