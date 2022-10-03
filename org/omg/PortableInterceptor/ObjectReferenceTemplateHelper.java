package org.omg.PortableInterceptor;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ObjectReferenceTemplateHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ObjectReferenceTemplate objectReferenceTemplate) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, objectReferenceTemplate);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ObjectReferenceTemplate extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectReferenceTemplateHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ObjectReferenceTemplateHelper.__typeCode == null) {
                    if (ObjectReferenceTemplateHelper.__active) {
                        return ORB.init().create_recursive_tc(ObjectReferenceTemplateHelper._id);
                    }
                    ObjectReferenceTemplateHelper.__active = true;
                    ObjectReferenceTemplateHelper.__typeCode = ORB.init().create_value_tc(ObjectReferenceTemplateHelper._id, "ObjectReferenceTemplate", (short)2, null, new ValueMember[0]);
                    ObjectReferenceTemplateHelper.__active = false;
                }
            }
        }
        return ObjectReferenceTemplateHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectReferenceTemplateHelper._id;
    }
    
    public static ObjectReferenceTemplate read(final InputStream inputStream) {
        return (ObjectReferenceTemplate)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value(id());
    }
    
    public static void write(final OutputStream outputStream, final ObjectReferenceTemplate objectReferenceTemplate) {
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value(objectReferenceTemplate, id());
    }
    
    static {
        ObjectReferenceTemplateHelper._id = "IDL:omg.org/PortableInterceptor/ObjectReferenceTemplate:1.0";
        ObjectReferenceTemplateHelper.__typeCode = null;
        ObjectReferenceTemplateHelper.__active = false;
    }
}
