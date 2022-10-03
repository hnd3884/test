package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceDetailHelper
{
    private static TypeCode _tc;
    
    public static void write(final OutputStream outputStream, final ServiceDetail serviceDetail) {
        outputStream.write_ulong(serviceDetail.service_detail_type);
        outputStream.write_long(serviceDetail.service_detail.length);
        outputStream.write_octet_array(serviceDetail.service_detail, 0, serviceDetail.service_detail.length);
    }
    
    public static ServiceDetail read(final InputStream inputStream) {
        final ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.service_detail_type = inputStream.read_ulong();
        inputStream.read_octet_array(serviceDetail.service_detail = new byte[inputStream.read_long()], 0, serviceDetail.service_detail.length);
        return serviceDetail;
    }
    
    public static ServiceDetail extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static void insert(final Any any, final ServiceDetail serviceDetail) {
        final OutputStream create_output_stream = any.create_output_stream();
        write(create_output_stream, serviceDetail);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static synchronized TypeCode type() {
        if (ServiceDetailHelper._tc == null) {
            ServiceDetailHelper._tc = ORB.init().create_struct_tc(id(), "ServiceDetail", new StructMember[] { new StructMember("service_detail_type", ORB.init().get_primitive_tc(TCKind.tk_ulong), null), new StructMember("service_detail", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null) });
        }
        return ServiceDetailHelper._tc;
    }
    
    public static String id() {
        return "IDL:omg.org/CORBA/ServiceDetail:1.0";
    }
}
