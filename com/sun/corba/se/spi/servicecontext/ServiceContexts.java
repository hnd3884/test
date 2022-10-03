package com.sun.corba.se.spi.servicecontext;

import java.util.Iterator;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import org.omg.CORBA.CompletionStatus;
import sun.corba.EncapsInputStreamFactory;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import java.util.HashMap;
import org.omg.CORBA.OctetSeqHelper;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.util.Map;
import com.sun.corba.se.spi.orb.ORB;

public class ServiceContexts
{
    private static final int JAVAIDL_ALIGN_SERVICE_ID = -1106033203;
    private ORB orb;
    private Map scMap;
    private boolean addAlignmentOnWrite;
    private CodeBase codeBase;
    private GIOPVersion giopVersion;
    private ORBUtilSystemException wrapper;
    
    private static boolean isDebugging(final OutputStream outputStream) {
        final ORB orb = (ORB)outputStream.orb();
        return orb != null && orb.serviceContextDebugFlag;
    }
    
    private static boolean isDebugging(final InputStream inputStream) {
        final ORB orb = (ORB)inputStream.orb();
        return orb != null && orb.serviceContextDebugFlag;
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint(this, s);
    }
    
    public static void writeNullServiceContext(final OutputStream outputStream) {
        if (isDebugging(outputStream)) {
            ORBUtility.dprint("ServiceContexts", "Writing null service context");
        }
        outputStream.write_long(0);
    }
    
    private void createMapFromInputStream(final InputStream inputStream) {
        this.orb = (ORB)inputStream.orb();
        if (this.orb.serviceContextDebugFlag) {
            this.dprint("Constructing ServiceContexts from input stream");
        }
        final int read_long = inputStream.read_long();
        if (this.orb.serviceContextDebugFlag) {
            this.dprint("Number of service contexts = " + read_long);
        }
        for (int i = 0; i < read_long; ++i) {
            final int read_long2 = inputStream.read_long();
            if (this.orb.serviceContextDebugFlag) {
                this.dprint("Reading service context id " + read_long2);
            }
            final byte[] read = OctetSeqHelper.read(inputStream);
            if (this.orb.serviceContextDebugFlag) {
                this.dprint("Service context" + read_long2 + " length: " + read.length);
            }
            this.scMap.put(new Integer(read_long2), read);
        }
    }
    
    public ServiceContexts(final ORB orb) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.addAlignmentOnWrite = false;
        this.scMap = new HashMap();
        this.giopVersion = orb.getORBData().getGIOPVersion();
        this.codeBase = null;
    }
    
    public ServiceContexts(final InputStream inputStream) {
        this((ORB)inputStream.orb());
        this.codeBase = ((CDRInputStream)inputStream).getCodeBase();
        this.createMapFromInputStream(inputStream);
        this.giopVersion = ((CDRInputStream)inputStream).getGIOPVersion();
    }
    
    private ServiceContext unmarshal(final Integer n, final byte[] array) {
        final ServiceContextData serviceContextData = this.orb.getServiceContextRegistry().findServiceContextData(n);
        ServiceContext serviceContext;
        if (serviceContextData == null) {
            if (this.orb.serviceContextDebugFlag) {
                this.dprint("Could not find ServiceContextData for " + n + " using UnknownServiceContext");
            }
            serviceContext = new UnknownServiceContext(n, array);
        }
        else {
            if (this.orb.serviceContextDebugFlag) {
                this.dprint("Found " + serviceContextData);
            }
            final EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, array, array.length, this.giopVersion, this.codeBase);
            encapsInputStream.consumeEndian();
            serviceContext = serviceContextData.makeServiceContext(encapsInputStream, this.giopVersion);
            if (serviceContext == null) {
                throw this.wrapper.svcctxUnmarshalError(CompletionStatus.COMPLETED_MAYBE);
            }
        }
        return serviceContext;
    }
    
    public void addAlignmentPadding() {
        this.addAlignmentOnWrite = true;
    }
    
    public void write(final OutputStream outputStream, final GIOPVersion giopVersion) {
        if (isDebugging(outputStream)) {
            this.dprint("Writing service contexts to output stream");
            Utility.printStackTrace();
        }
        int size = this.scMap.size();
        if (this.addAlignmentOnWrite) {
            if (isDebugging(outputStream)) {
                this.dprint("Adding alignment padding");
            }
            ++size;
        }
        if (isDebugging(outputStream)) {
            this.dprint("Service context has " + size + " components");
        }
        outputStream.write_long(size);
        this.writeServiceContextsInOrder(outputStream, giopVersion);
        if (this.addAlignmentOnWrite) {
            if (isDebugging(outputStream)) {
                this.dprint("Writing alignment padding");
            }
            outputStream.write_long(-1106033203);
            outputStream.write_long(4);
            outputStream.write_octet((byte)0);
            outputStream.write_octet((byte)0);
            outputStream.write_octet((byte)0);
            outputStream.write_octet((byte)0);
        }
        if (isDebugging(outputStream)) {
            this.dprint("Service context writing complete");
        }
    }
    
    private void writeServiceContextsInOrder(final OutputStream outputStream, final GIOPVersion giopVersion) {
        final Integer n = new Integer(9);
        final Object remove = this.scMap.remove(n);
        for (final Integer n2 : this.scMap.keySet()) {
            this.writeMapEntry(outputStream, n2, this.scMap.get(n2), giopVersion);
        }
        if (remove != null) {
            this.writeMapEntry(outputStream, n, remove, giopVersion);
            this.scMap.put(n, remove);
        }
    }
    
    private void writeMapEntry(final OutputStream outputStream, final Integer n, final Object o, final GIOPVersion giopVersion) {
        if (o instanceof byte[]) {
            if (isDebugging(outputStream)) {
                this.dprint("Writing service context bytes for id " + n);
            }
            OctetSeqHelper.write(outputStream, (byte[])o);
        }
        else {
            final ServiceContext serviceContext = (ServiceContext)o;
            if (isDebugging(outputStream)) {
                this.dprint("Writing service context " + serviceContext);
            }
            serviceContext.write(outputStream, giopVersion);
        }
    }
    
    public void put(final ServiceContext serviceContext) {
        this.scMap.put(new Integer(serviceContext.getId()), serviceContext);
    }
    
    public void delete(final int n) {
        this.delete(new Integer(n));
    }
    
    public void delete(final Integer n) {
        this.scMap.remove(n);
    }
    
    public ServiceContext get(final int n) {
        return this.get(new Integer(n));
    }
    
    public ServiceContext get(final Integer n) {
        final ServiceContext value = this.scMap.get(n);
        if (value == null) {
            return null;
        }
        if (value instanceof byte[]) {
            final ServiceContext unmarshal = this.unmarshal(n, (byte[])(Object)value);
            this.scMap.put(n, unmarshal);
            return unmarshal;
        }
        return value;
    }
}
