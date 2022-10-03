package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ObjectReferenceTemplateSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ObjectReferenceTemplate[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ObjectReferenceTemplate[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectReferenceTemplateSeqHelper.__typeCode == null) {
            ObjectReferenceTemplateSeqHelper.__typeCode = ObjectReferenceTemplateHelper.type();
            ObjectReferenceTemplateSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ObjectReferenceTemplateSeqHelper.__typeCode);
            ObjectReferenceTemplateSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ObjectReferenceTemplateSeq", ObjectReferenceTemplateSeqHelper.__typeCode);
        }
        return ObjectReferenceTemplateSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectReferenceTemplateSeqHelper._id;
    }
    
    public static ObjectReferenceTemplate[] read(final InputStream inputStream) {
        final ObjectReferenceTemplate[] array = new ObjectReferenceTemplate[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ObjectReferenceTemplateHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final ObjectReferenceTemplate[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ObjectReferenceTemplateHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ObjectReferenceTemplateSeqHelper._id = "IDL:omg.org/PortableInterceptor/ObjectReferenceTemplateSeq:1.0";
        ObjectReferenceTemplateSeqHelper.__typeCode = null;
    }
}
