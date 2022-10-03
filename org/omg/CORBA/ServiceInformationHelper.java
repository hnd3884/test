package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceInformationHelper
{
    private static TypeCode _tc;
    
    public static void write(final OutputStream outputStream, final ServiceInformation serviceInformation) {
        outputStream.write_long(serviceInformation.service_options.length);
        outputStream.write_ulong_array(serviceInformation.service_options, 0, serviceInformation.service_options.length);
        outputStream.write_long(serviceInformation.service_details.length);
        for (int i = 0; i < serviceInformation.service_details.length; ++i) {
            ServiceDetailHelper.write(outputStream, serviceInformation.service_details[i]);
        }
    }
    
    public static ServiceInformation read(final InputStream inputStream) {
        final ServiceInformation serviceInformation = new ServiceInformation();
        inputStream.read_ulong_array(serviceInformation.service_options = new int[inputStream.read_long()], 0, serviceInformation.service_options.length);
        serviceInformation.service_details = new ServiceDetail[inputStream.read_long()];
        for (int i = 0; i < serviceInformation.service_details.length; ++i) {
            serviceInformation.service_details[i] = ServiceDetailHelper.read(inputStream);
        }
        return serviceInformation;
    }
    
    public static ServiceInformation extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static void insert(final Any any, final ServiceInformation serviceInformation) {
        final OutputStream create_output_stream = any.create_output_stream();
        write(create_output_stream, serviceInformation);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static synchronized TypeCode type() {
        if (ServiceInformationHelper._tc == null) {
            ServiceInformationHelper._tc = ORB.init().create_struct_tc(id(), "ServiceInformation", new StructMember[] { new StructMember("service_options", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_ulong)), null), new StructMember("service_details", ORB.init().create_sequence_tc(0, ServiceDetailHelper.type()), null) });
        }
        return ServiceInformationHelper._tc;
    }
    
    public static String id() {
        return "IDL:omg.org/CORBA/ServiceInformation:1.0";
    }
}
