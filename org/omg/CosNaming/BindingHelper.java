package org.omg.CosNaming;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class BindingHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final Binding binding) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, binding);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Binding extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (BindingHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (BindingHelper.__typeCode == null) {
                    if (BindingHelper.__active) {
                        return ORB.init().create_recursive_tc(BindingHelper._id);
                    }
                    BindingHelper.__active = true;
                    BindingHelper.__typeCode = ORB.init().create_struct_tc(id(), "Binding", new StructMember[] { new StructMember("binding_name", ORB.init().create_alias_tc(NameHelper.id(), "Name", ORB.init().create_sequence_tc(0, NameComponentHelper.type())), null), new StructMember("binding_type", BindingTypeHelper.type(), null) });
                    BindingHelper.__active = false;
                }
            }
        }
        return BindingHelper.__typeCode;
    }
    
    public static String id() {
        return BindingHelper._id;
    }
    
    public static Binding read(final InputStream inputStream) {
        final Binding binding = new Binding();
        binding.binding_name = NameHelper.read(inputStream);
        binding.binding_type = BindingTypeHelper.read(inputStream);
        return binding;
    }
    
    public static void write(final OutputStream outputStream, final Binding binding) {
        NameHelper.write(outputStream, binding.binding_name);
        BindingTypeHelper.write(outputStream, binding.binding_type);
    }
    
    static {
        BindingHelper._id = "IDL:omg.org/CosNaming/Binding:1.0";
        BindingHelper.__typeCode = null;
        BindingHelper.__active = false;
    }
}
