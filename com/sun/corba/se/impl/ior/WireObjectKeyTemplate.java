package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import org.omg.CORBA.OctetSeqHolder;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;

public class WireObjectKeyTemplate implements ObjectKeyTemplate
{
    private ORB orb;
    private IORSystemException wrapper;
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof WireObjectKeyTemplate;
    }
    
    @Override
    public int hashCode() {
        return 53;
    }
    
    private byte[] getId(final InputStream inputStream) {
        final CDRInputStream cdrInputStream = (CDRInputStream)inputStream;
        final int bufferLength = cdrInputStream.getBufferLength();
        final byte[] array = new byte[bufferLength];
        cdrInputStream.read_octet_array(array, 0, bufferLength);
        return array;
    }
    
    public WireObjectKeyTemplate(final ORB orb) {
        this.initORB(orb);
    }
    
    public WireObjectKeyTemplate(final InputStream inputStream, final OctetSeqHolder octetSeqHolder) {
        octetSeqHolder.value = this.getId(inputStream);
        this.initORB((ORB)inputStream.orb());
    }
    
    private void initORB(final ORB orb) {
        this.orb = orb;
        this.wrapper = IORSystemException.get(orb, "oa.ior");
    }
    
    @Override
    public void write(final ObjectId objectId, final OutputStream outputStream) {
        final byte[] id = objectId.getId();
        outputStream.write_octet_array(id, 0, id.length);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
    }
    
    @Override
    public int getSubcontractId() {
        return 2;
    }
    
    @Override
    public int getServerId() {
        return -1;
    }
    
    @Override
    public String getORBId() {
        throw this.wrapper.orbIdNotAvailable();
    }
    
    @Override
    public ObjectAdapterId getObjectAdapterId() {
        throw this.wrapper.objectAdapterIdNotAvailable();
    }
    
    @Override
    public byte[] getAdapterId() {
        throw this.wrapper.adapterIdNotAvailable();
    }
    
    @Override
    public ORBVersion getORBVersion() {
        return ORBVersionFactory.getFOREIGN();
    }
    
    @Override
    public CorbaServerRequestDispatcher getServerRequestDispatcher(final ORB orb, final ObjectId objectId) {
        return orb.getRequestDispatcherRegistry().getServerRequestDispatcher(new String(objectId.getId()));
    }
}
