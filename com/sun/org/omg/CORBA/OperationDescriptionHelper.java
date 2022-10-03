package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class OperationDescriptionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final OperationDescription operationDescription) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, operationDescription);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static OperationDescription extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (OperationDescriptionHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (OperationDescriptionHelper.__typeCode == null) {
                    if (OperationDescriptionHelper.__active) {
                        return ORB.init().create_recursive_tc(OperationDescriptionHelper._id);
                    }
                    OperationDescriptionHelper.__active = true;
                    OperationDescriptionHelper.__typeCode = ORB.init().create_struct_tc(id(), "OperationDescription", new StructMember[] { new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null), new StructMember("id", ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", ORB.init().create_string_tc(0)), null), new StructMember("defined_in", ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", ORB.init().create_string_tc(0)), null), new StructMember("version", ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", ORB.init().create_string_tc(0)), null), new StructMember("result", ORB.init().get_primitive_tc(TCKind.tk_TypeCode), null), new StructMember("mode", OperationModeHelper.type(), null), new StructMember("contexts", ORB.init().create_alias_tc(ContextIdSeqHelper.id(), "ContextIdSeq", ORB.init().create_sequence_tc(0, ORB.init().create_alias_tc(ContextIdentifierHelper.id(), "ContextIdentifier", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0))))), null), new StructMember("parameters", ORB.init().create_alias_tc(ParDescriptionSeqHelper.id(), "ParDescriptionSeq", ORB.init().create_sequence_tc(0, ParameterDescriptionHelper.type())), null), new StructMember("exceptions", ORB.init().create_alias_tc(ExcDescriptionSeqHelper.id(), "ExcDescriptionSeq", ORB.init().create_sequence_tc(0, ExceptionDescriptionHelper.type())), null) });
                    OperationDescriptionHelper.__active = false;
                }
            }
        }
        return OperationDescriptionHelper.__typeCode;
    }
    
    public static String id() {
        return OperationDescriptionHelper._id;
    }
    
    public static OperationDescription read(final InputStream inputStream) {
        final OperationDescription operationDescription = new OperationDescription();
        operationDescription.name = inputStream.read_string();
        operationDescription.id = inputStream.read_string();
        operationDescription.defined_in = inputStream.read_string();
        operationDescription.version = inputStream.read_string();
        operationDescription.result = inputStream.read_TypeCode();
        operationDescription.mode = OperationModeHelper.read(inputStream);
        operationDescription.contexts = ContextIdSeqHelper.read(inputStream);
        operationDescription.parameters = ParDescriptionSeqHelper.read(inputStream);
        operationDescription.exceptions = ExcDescriptionSeqHelper.read(inputStream);
        return operationDescription;
    }
    
    public static void write(final OutputStream outputStream, final OperationDescription operationDescription) {
        outputStream.write_string(operationDescription.name);
        outputStream.write_string(operationDescription.id);
        outputStream.write_string(operationDescription.defined_in);
        outputStream.write_string(operationDescription.version);
        outputStream.write_TypeCode(operationDescription.result);
        OperationModeHelper.write(outputStream, operationDescription.mode);
        ContextIdSeqHelper.write(outputStream, operationDescription.contexts);
        ParDescriptionSeqHelper.write(outputStream, operationDescription.parameters);
        ExcDescriptionSeqHelper.write(outputStream, operationDescription.exceptions);
    }
    
    static {
        OperationDescriptionHelper._id = "IDL:omg.org/CORBA/OperationDescription:1.0";
        OperationDescriptionHelper.__typeCode = null;
        OperationDescriptionHelper.__active = false;
    }
}
