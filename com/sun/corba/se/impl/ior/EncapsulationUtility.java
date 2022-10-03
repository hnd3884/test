package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.WriteContents;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import sun.corba.EncapsInputStreamFactory;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import java.util.List;

public class EncapsulationUtility
{
    private EncapsulationUtility() {
    }
    
    public static void readIdentifiableSequence(final List list, final IdentifiableFactoryFinder identifiableFactoryFinder, final InputStream inputStream) {
        for (int read_long = inputStream.read_long(), i = 0; i < read_long; ++i) {
            list.add(identifiableFactoryFinder.create(inputStream.read_long(), inputStream));
        }
    }
    
    public static void writeIdentifiableSequence(final List list, final OutputStream outputStream) {
        outputStream.write_long(list.size());
        for (final Identifiable identifiable : list) {
            outputStream.write_long(identifiable.getId());
            identifiable.write(outputStream);
        }
    }
    
    public static void writeOutputStream(final OutputStream outputStream, final OutputStream outputStream2) {
        final byte[] byteArray = ((CDROutputStream)outputStream).toByteArray();
        outputStream2.write_long(byteArray.length);
        outputStream2.write_octet_array(byteArray, 0, byteArray.length);
    }
    
    public static InputStream getEncapsulationStream(final InputStream inputStream) {
        final byte[] octets = readOctets(inputStream);
        final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(inputStream.orb(), octets, octets.length);
        encapsInputStream.consumeEndian();
        return encapsInputStream;
    }
    
    public static byte[] readOctets(final InputStream inputStream) {
        final int read_ulong = inputStream.read_ulong();
        final byte[] array = new byte[read_ulong];
        inputStream.read_octet_array(array, 0, read_ulong);
        return array;
    }
    
    public static void writeEncapsulation(final WriteContents writeContents, final OutputStream outputStream) {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)outputStream.orb());
        encapsOutputStream.putEndian();
        writeContents.writeContents(encapsOutputStream);
        writeOutputStream(encapsOutputStream, outputStream);
    }
}
